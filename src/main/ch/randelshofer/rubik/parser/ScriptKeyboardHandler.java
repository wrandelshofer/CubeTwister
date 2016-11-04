/*
 * @(#)ScriptKeyboardHandler.java
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.Cube3DCanvas;
import ch.randelshofer.rubik.CubeEvent;
import ch.randelshofer.rubik.CubeListener;
import ch.randelshofer.rubik.Cubes;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A handler which parses keyboard input from a Cube3DCanvas.
 *
 * @author Werner Randelshofer
 * @version 1.1 2010-10-17 Actively clear buffer on timeout.
 * <br>1.0 2010-05-02 Created.
 */
public class ScriptKeyboardHandler extends AbstractBean {
    private final static long serialVersionUID = 1L;

    public static String KEY_BUFFER_PROPERTY = "keyBuffer";
    public static String INVERSE_CASE_PROPERTY = "inverseCase";
    /** The cube on which the parser acts. */
    private Cube cube;
    /** Intermediate cube. */
    private Cube tmpCube;
    /** The keyboard buffer holds the contents of the keyboard. */
    private StringBuilder keyBuffer = new StringBuilder();
    /** The sequence buffer holds the parsed sequence so far. */
    private Node currentSeq = null;
    /** This parser is used to decode the contents of the keyboard buffer. */
    private ScriptParser parser;
    /** The time when the buffer will cleared. */
    private long bestBefore;
    /** The time to live for contents in the buffer. */
    private static final long BUFFER_TTL = 2000;
    /** Whether the case of characters is inverted. */
    private boolean isInvertingCase = true;
    private Handler handler = new Handler();
    private Cube3DCanvas canvas;
    private int isChanging;
    private Timer timer;

    private class Handler implements KeyListener, FocusListener, MouseListener, CubeListener, ActionListener {

        @Override
        public void focusGained(FocusEvent e) {
            clearBuffer();
        }

        @Override
        public void focusLost(FocusEvent e) {
            clearBuffer();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (!e.getComponent().isEnabled()) {
                return;
            }
            switch (e.getKeyCode()) {
                case KeyEvent.CHAR_UNDEFINED:
                    break;
                case ' ':
                    clearBuffer();
                    break;
                default:
                    parse(e.getKeyChar());
                    break;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!e.getComponent().isEnabled()) {
                return;
            }
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    clearBuffer();
                    break;
                case KeyEvent.VK_HOME:
                    clearBuffer();
                    if (canvas != null) {
                        canvas.reset();
                    }
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    backspace();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ((JComponent) e.getSource()).requestFocus();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void cubeTwisted(CubeEvent evt) {
            if (isChanging == 0) {
                clearBuffer();
            }
        }

        @Override
        public void cubeChanged(CubeEvent evt) {
            if (isChanging == 0) {
                clearBuffer();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            long now = System.currentTimeMillis();
            if (bestBefore < now) {
                clearBuffer();
            }
        }
    }

    public ScriptKeyboardHandler() {
timer= new Timer((int) BUFFER_TTL, handler);
timer.setRepeats(false);
    }

    public ScriptParser getParser() {
        return parser;
    }

    public void setParser(ScriptParser parser) {
        this.parser = parser;
        tmpCube = Cubes.create(parser.getNotation().getLayerCount());
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    /** Sets the canvas on which this keyboard parser acts. */
    public void setCanvas(Cube3DCanvas c) {
        if (canvas != null) {
            removeFrom(canvas.getVisualComponent());
        }
        canvas = c;
        if (canvas != null) {
            addTo(c.getVisualComponent());
        }
    }

    public Cube3DCanvas getCanvas() {
        return canvas;
    }

    public void addTo(Component c) {
        c.addKeyListener(handler);
        c.addFocusListener(handler);
        c.addMouseListener(handler);
        c.setFocusable(true);
    }

    public void removeFrom(Component c) {
        c.removeKeyListener(handler);
        c.removeFocusListener(handler);
        c.removeMouseListener(handler);
    }

    /** Clears the key buffer. */
    public void clearBuffer() {
        String oldValue = keyBuffer.toString();
        keyBuffer.setLength(0);
        currentSeq = null;
        firePropertyChange(KEY_BUFFER_PROPERTY, oldValue, keyBuffer.toString());
    }

    private void backspace() {
        String oldValue = keyBuffer.toString();
        if (keyBuffer.length() > 0) {
            keyBuffer.setLength(keyBuffer.length() - 1);
        }
        firePropertyChange(KEY_BUFFER_PROPERTY, oldValue, keyBuffer.toString());
    }

    /** Parses the key buffer. */
    private void parse(char ch) {
        String oldValue = keyBuffer.toString();

        // Clear buffer if it has decayed
        long now = System.currentTimeMillis();
        if (bestBefore < now) {
            keyBuffer.setLength(0);
            currentSeq = null;
        }
        bestBefore = now + BUFFER_TTL;
timer.restart();
        if (isInvertingCase) {
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else {
                ch = Character.toUpperCase(ch);
            }
        }

        Node newSeq;
        try {
            keyBuffer.append(ch);
            newSeq = parser.parse(keyBuffer.toString());
        } catch (IOException ex) {
            newSeq = null;
        }

        if (newSeq != null) {
            // If the new sequence consists of more than one statements.
            // Get rid of all statements except the last one.
            if (newSeq.getChildCount() > 1) {
                currentSeq = null;
                Node lastChild = newSeq.getChildAt(newSeq.getChildCount() - 1);
                newSeq = lastChild;
                keyBuffer.delete(0, lastChild.getStartPosition());
            }

            if (currentSeq != null) {
                tmpCube.reset();
                currentSeq.applyTo(tmpCube, true);
                newSeq.applyTo(tmpCube, false);
                isChanging++;
                cube.transform(tmpCube);
                isChanging--;
            } else {
                isChanging++;
                newSeq.applyTo(cube, false);
                isChanging--;
            }
            currentSeq = newSeq;
        }
        firePropertyChange(KEY_BUFFER_PROPERTY, oldValue, keyBuffer.toString());
    }

    public String getKeyBuffer() {
        return keyBuffer.toString();
    }
}
