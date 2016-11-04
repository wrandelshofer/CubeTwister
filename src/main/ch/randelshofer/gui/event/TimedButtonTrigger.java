/*
 * @(#)TimedButtonTrigger.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.event;

import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;


/**
 * Invokes the doClick method of an javax.swing.AbstractButton
 * repeatedely when the user keeps the mouse pressed during a 
 * long period of time.
 * 
 * @author  werni
 * @version 1.0 2001-07-28
 */
public class TimedButtonTrigger 
extends MouseAdapter
implements ActionListener {
    private Timer timer;
    private AbstractButton button;
    private boolean isPressed;
    
    /** Creates new TimedButtonTrigger */
    public TimedButtonTrigger(AbstractButton button) {
        this.button = button;
        button.addMouseListener(this);
        timer = new Timer(60, this);
        timer.setInitialDelay(60);
    }

    public void actionPerformed(ActionEvent evt) {
        button.doClick();
    }
    
    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        timer.stop();
    }
    
    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        isPressed = false;
        timer.stop();
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        if (button.isEnabled()) {
            isPressed = true;
            timer.start();
        }
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
        if (isPressed && button.isEnabled()) {
            timer.start();
        }
    }
}
