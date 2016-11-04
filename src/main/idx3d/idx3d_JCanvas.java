/*
 * @(#)idx3d_JCanvas.java  5.9  2014-04-27
 *
 * Copyright (c) 2005-2014 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package idx3d;

import ch.randelshofer.gui.event.SwipeEvent;
import ch.randelshofer.gui.event.SwipeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import javax.swing.*;
import javax.swing.event.*;

/**
 * idx3d_JCanvas.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>5.8 2011-06-29 Adds methods setSwipeTimeout and getSwipeTimeout.
 * <br>5.7 2010-08-18 Adds support for shared render pipelines.
 * <br>5.6 2010-08-08 Adds scroll wheel support.
 * <br>5.5 2010-06-01 Adds method setRasterizer.
 * <br>5.4 2009-11-28 Adds method flush.
 * <br>5.3 2009-04-11 Only process mouse events when enabled.
 * <br>5.2.1 2009-01-19 Update cursor on mouse release.
 * <br>5.2 2009-01-09 Cursor changes to hand cursor when over a triangle
 * which has event listeners. Added workaround for
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6634290
 * <br>5.1 2009-01-05 Added methods setUpdateCursor and isUpdateCursor.
 * <br>5.0.1 2009-01-03 Don't start swiping when not in ROTATE_AND_SWIPE
 * mode. Added method setMinFPS/getMinFPS.
 * <br>5.0 2009-01-01 Added ROTATE_AND_SWIPE interaction mode.
 * <br>4.2 2008-12-24 Added variable "quiet".
 * <br>4.1 2008-09-12 Added support for border painting and insets.
 * <br>4.0 2007-11-15 Upgraded to Java 1.4. 
 * <br>3.0.1 2007-09-10 Fixed some synchronization issues. 
 * <br>3.0 2007-09-09 Rewritten to match functionality of idx3d_CanvasAWT.
 * <br>2.0 2006-02-20 Rewritten for asynchronous rendering of the scene.
 * <br>1.0 January 5, 2006 Created.
 */
public class idx3d_JCanvas extends JComponent implements MouseListener, MouseMotionListener, ChangeListener, MouseWheelListener {

    private final static long serialVersionUID = 1L;
    //protected EventListenerList listenerList = new EventListenerList();

    /**
     * The 3d Scene.
     */
    protected idx3d_Scene scene;
    /**
     * The 3d Render Pipeline.
     */
    protected idx3d_RenderPipeline renderPipeline;
    private Object lock;
    /**
     * This flag is set to true, when the canvas is animating the scene.
     * Such as when the user drags the mouse over the canvas.
     */
    private boolean isAnimating;
    /**
     * oldx and oldy store the previous mouse location.
     */
    private int oldx, oldy;
    protected String cameraName;
    private Panel imageUpdateDiscarder;
    /**
     * Holds the triangle over which the mouse was pressed.
     * This variable is null, if the mouse was pressed over an inactive
     * area of the canvas, or outside of the canvas.
     */
    private idx3d_Triangle pressedTriangle;
    /**
     * Holds the mouse location over which the mouse was pressed.
     */
    private int pressedX, pressedY;
    /**
     * Holds the square of the minimal distance which is needed to fire a
     * SwipeEvent.
     */
    private final static int MIN_SWIPE_DIST_SQUARED = 5 * 5;
    /**
     * Holds the triangle over which the mouse is being scraped.
     * This variable is set to null, if a scrape event is fired.
     */
    private idx3d_Triangle swipedTriangle;
    /**
     * Holds the location where the scraping started in the coordinate
     * system of the scraped triangle.
     */
    private Point2D.Float swipeStartPos;
    /**
     * Holds the triangle which is currently under the mouse pointer.
     * This variable is null, if the mouse is over an inactive
     * area of the canvas, or outside of the canvas.
     */
    private idx3d_Triangle armedTriangle;
    /**
     * The current mouse position over the canvas.
     * This variable is null, if the mouse is not over the canvas.
     */
    private Point mousePosition;
    /**
     * The current pressedModifiersEx of the mouse.
     */
    private int pressedModifiersEx;
    /**
     * Don't fire events when quiet is true.
     */
    private boolean quiet = false;
    private Float debugSwipeEndPos;
    /** the value -1 is used when pressedWhen should be ignored. */
    private long pressedWhen = -1;
    private boolean isSwiping = false;
    private boolean isPressed = false;
    /** Defines the delay between mouse pressed and mouse dragged where swipe
     * events are accepted.
     */
    private int swipeTimeout = 500;
    private GeneralPath debugOverlayPathVerifyOrthogonal;
    private Shape debugOverlayPathComputedOrthogonal;
    /**
     * Desired maximal Frame Per Seconds Rate.
     */
    private int maxFPS = 30;
    /**
     * Minimal Frame Per Seconds Rate. If the renderer drops
     * below this rate, antialiasing is turned off.
     */
    private int minFPS = 20;
    /**
     * Measured time needed to render an antialiased screen.
     * We initialize this to 1000 milliseconds devided by the desired
     * animation frame rate - 1.
     * The - 1 is needed, to take first time initialization into
     * account.
     */
    private long antialiasRenderTime = 1000 / (maxFPS - 1);  
    private idx3d_Rasterizer rasterizer;

    private boolean debugRenderTime;

    public void setDebugRenderTime(boolean b) {
        debugRenderTime = b;
    }

    public boolean isDebugRenderTime() {
        return debugRenderTime;
    }

    public idx3d_Rasterizer getRasterizer() {
        return rasterizer;
    }

    public enum Interaction {
        /* The canvas does not perform interactions on its own. */

        NOTHING,
        /* The canvas rotates the scene when the user drags the mouse. */
        ROTATE,
        /* The canvas rotates the scene if the drag started over a non-swipable area. */
        ROTATE_AND_SWIPE,
        /* The canvas rotates the scene if the drag started over the background. */
        ROTATE_OVER_BACKGROUND_AND_SWIPE
    }
    private Interaction interactionMode = Interaction.ROTATE_AND_SWIPE;
    /** Whether the idx3d_JCanvas should update the mouse cursor or not. */
    private boolean isUpdateCursor = true;
    private idx3d_RenderPipeline sharedRenderPipeline;
    private int[] idBuffer;
    private Rectangle idBufferBounds = new Rectangle();

    /** Creates a new instance. */
    public idx3d_JCanvas() {
        initComponents();
        imageUpdateDiscarder = new Panel();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setOpaque(true);
    }

    public void setInteractionMode(Interaction newValue) {
        Interaction oldValue = this.interactionMode;
        this.interactionMode = newValue;
        firePropertyChange("interactionMode", oldValue, newValue);
    }

    public Interaction getInteractionMode() {
        return interactionMode;
    }

    public void setScene(idx3d_Scene scene) {
        this.scene = scene;
        this.lock = scene;
        //scene.render();
    }

    public idx3d_Scene getScene() {
        return scene;
    }

    /** Sets a different rasterizer. */
    public void setRasterizer(idx3d_Rasterizer r) {
        rasterizer = r;
        // drop render pipeline
        renderPipeline = null;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public Object getLock() {
        return lock;
    }

    public void setSharedRenderPipeline(idx3d_RenderPipeline newValue) {
        this.sharedRenderPipeline = newValue;
        this.renderPipeline = null;
        this.idBuffer = null;
    }

    public void setCamera(String cameraName) {
        this.cameraName = cameraName;
    }

    /**
     * Frees resources used by the canvas.
     */
    public void dispose() {
        if (renderPipeline != null) {
            renderPipeline.dispose();
            renderPipeline = null;
        }
    }

    @Override
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        Dimension size = getSize();
        Insets insets = getInsets();
        // XXX - We only need to fill the background, if we are opaque AND 
        //    the rendered image is translucent
        if (false && isOpaque()) {
            gr.setColor(getBackground());
            gr.fillRect(0, 0, size.width, size.height);
        }
        if (scene != null) {
            try {
                paintScene(gr, insets.left, insets.top,
                        size.width - insets.left - insets.right,
                        size.height - insets.top - insets.bottom);
            } catch (OutOfMemoryError e) {
                // remove render pipeline. It might have been only partially allocated.
                renderPipeline = null;
                int lineHeight = (int) (gr.getFont().getSize() * 1.5f);
                gr.setColor(getBackground());
                gr.fillRect(0, 0, size.width, size.height);
                gr.clearRect(0, 0, getBounds().width, lineHeight * 3);
                gr.setColor(getForeground());
                gr.drawString("Sorry.", 12, lineHeight);
                gr.drawString("Out of memory.", 12, lineHeight * 2);
                try {
                    Runtime r = Runtime.getRuntime();
                    gr.drawString("Memory total:" + (r.totalMemory() / 1024 / 1024) + " MB", 12, lineHeight * 3);
                    gr.drawString("Memory used:" + ((r.totalMemory() - r.freeMemory()) / 1024 / 1024) + " MB", 12, lineHeight * 4);
                } catch (SecurityException se) {
                }
                e.printStackTrace();
            }
        }
        
        if (debugRenderTime) {
            gr.setColor(Color.black);
            gr.drawString("render time:"+antialiasRenderTime,gr.getFont().getSize(),gr.getFont().getSize()*2);
        }
        
        /*
         try {
         gr.drawString(" used=" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024), 12, 12);
         gr.drawString(" total=" + ((Runtime.getRuntime().totalMemory()) / 1024 / 1024), 12, 24);
         } catch (SecurityException e) {
         }
         /*
         if (debugOverlayPath != null) {
         g.setColor(Color.GREEN);
         g.draw(debugOverlayPath2);
         g.translate(getWidth() / 2, getHeight() / 2);
         g.setColor(new Color(0xa0ffff00, true));
         g.fill(debugOverlayPathVerifyOrthogonal);
         g.setColor(Color.ORANGE);
         g.draw(debugOverlayPathComputedOrthogonal);
         g.setColor(Color.BLUE);
         g.draw(debugOverlayPath);
         if (swipeStartPos != null && debugSwipeEndPos != null) {
         float f = 100f;
         g.draw(new Line2D.Float(swipeStartPos.x * f, swipeStartPos.y * f, debugSwipeEndPos.x * f, debugSwipeEndPos.y * f));
         }
         }*/
    }

    /**
     * Returns the minimal number of frame seconds before the renderer switches
     * antialising off.
     *
     * @return Number of Frames per second
     */
    public int getMinFPS() {
        return minFPS;
    }

    /**
     * Minimal Frame Per Seconds Rate. If the renderer drops
     * below this rate, antialiasing is turned off.
     *
     * @param newValue Number of Frames per second.
     */
    public void setMinFPS(int newValue) {
        minFPS = newValue;
    }

    private void paintScene(Graphics g, int x, int y, int width, int height) {
        // Reuse an existing render pipeline or create a new one
        if (renderPipeline == null) {
            if (sharedRenderPipeline != null) {
                renderPipeline = sharedRenderPipeline;
                renderPipeline.setScene(scene);
                Dimension renderSize = renderPipeline.size();
                if (renderSize.width != width || renderSize.height != height) {
                    renderPipeline.resize(width, height);
                }
            } else {
                renderPipeline = new idx3d_RenderPipeline(scene, width, height);
                if (rasterizer != null) {
                    renderPipeline.setRasterizer(rasterizer);
                }
            }
            renderPipeline.useIdBuffer(true);
        } else {
            renderPipeline.setScene(scene);
            Dimension renderSize = renderPipeline.size();
            if (renderSize.width != width || renderSize.height != height) {
                renderPipeline.resize(width, height);
            }
        }
        // We turn off antialiasing, if the scene is adjusting, or if we are
        // animating.
        boolean antialiased = !isAnimating && !scene.isAdjusting();
        if (!antialiased) {
            // We turn antialiasing back on, if rendering time is above minFPS
            antialiased = antialiasRenderTime <= 1000 / (minFPS);
        }
        renderPipeline.setAntialias(antialiased);
        long start = System.currentTimeMillis();
        synchronized ((lock == null) ? scene : lock) {
            renderPipeline.render((cameraName != null) ? scene.camera(cameraName) : scene.getDefaultCamera());
            g.drawImage(renderPipeline.getImage(), x, y, this);
        }
// Measure the time needed to render an antialiased screen
        // We use the last 3 measurements and the current one
        // to determine the render time. The idea is to even out
        // time differences caused by the JIT and by
        // other tasks running on the computer.
        long end = System.currentTimeMillis();
        if (antialiased) {
            antialiasRenderTime = (antialiasRenderTime * 3 + end - start) / 4;
        }
        if (sharedRenderPipeline != null) {
            if (idBuffer == null || idBuffer.length != sharedRenderPipeline.idBuffer.length) {
                idBuffer = sharedRenderPipeline.idBuffer.clone();
            } else {
                System.arraycopy(sharedRenderPipeline.idBuffer, 0, idBuffer, 0, idBuffer.length);
            }
        } else {
            idBuffer = renderPipeline.idBuffer;
        }
        idBufferBounds.x = y;
        idBufferBounds.y = y;
        idBufferBounds.width = width;
        idBufferBounds.height = height;
        if (mousePosition != null) {
            updateArmedTriangle(new MouseEvent(
                    this, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), pressedModifiersEx,
                    mousePosition.x, mousePosition.y,
                    0, false), identifyTriangleAt(mousePosition.x, mousePosition.y));
        }
    }

    private idx3d_Triangle identifyTriangleAt(int x, int y) {
        idx3d_Triangle triangle = scene.identifyTriangleAt(idBuffer,//
                idBufferBounds.width, idBufferBounds.height, x - idBufferBounds.x, y - idBufferBounds.y);
        return triangle;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (isEnabled()) {
            if (pressedWhen != -1 && evt.getWhen() - pressedWhen < swipeTimeout) {
                mousePosition = evt.getPoint();
                // Workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6634290
                // Don't get pressedModifiersEx from mouseClicked event - they are bogus.
                //pressedModifiersEx = evt.getModifiersEx();
                //System.out.println("idx3d_JCanvas mouseClicked modifiersEx:"+pressedModifiersEx);
                if (scene != null) {
                    idx3d_Triangle triangle = identifyTriangleAt(evt.getX(), evt.getY());
                    if (triangle != null) {
                        //fireMouseClicked(evt, triangle);
                        fireMouseClicked(new MouseEvent(evt.getComponent(), evt.getID(), evt.getWhen(), pressedModifiersEx,
                                evt.getX(), evt.getY(), evt.getClickCount(), evt.isPopupTrigger(), evt.getButton()), triangle);
                        ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, pressedModifiersEx);
                        ActionListener[] listeners = scene.getActionListeners(triangle);
                        if (listeners.length == 0) {
                            listeners = scene.getActionListeners(triangle.parent);
                        }
                        for (int i = 0; i
                                < listeners.length; i++) {
                            listeners[i].actionPerformed(actionEvent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if (isEnabled()) {
            int x = evt.getX();
            int y = evt.getY();
            mousePosition = evt.getPoint();
            pressedModifiersEx = evt.getModifiersEx();
            float dx = (float) (y - oldy) / 50;
            float dy = (float) (oldx - x) / 50;
            // Clear pressed and swiped triangles, if the user waited too long
            // before dragging the mouse
            if (pressedWhen != -1) {
                if (evt.getWhen() - pressedWhen > swipeTimeout) {
                    isSwiping = false;
                }
                pressedWhen = -1;
            }
            if (scene != null) {
                if (interactionMode == Interaction.ROTATE
                        || interactionMode == Interaction.ROTATE_AND_SWIPE && !isSwiping
                        || interactionMode == Interaction.ROTATE_OVER_BACKGROUND_AND_SWIPE && !isSwiping && pressedTriangle == null) {
                    idx3d_Camera camera = scene.camera(cameraName);
                    idx3d_Matrix camMx = camera.getMatrix();
                    camMx.inverse(); // !!! EXPENSIVE
                    idx3d_Vector dv = new idx3d_Vector(dx, dy, 0);
                    dv.transform(camMx);
                    if (camera.getMatrix().m11 < 0) {
                        scene.rotate(dv.x, -dv.y, 0);
                    } else {
                        scene.rotate(dv.x, dv.y, 0);
                    }
                }
                if (isSwiping && swipedTriangle != null) {
                    debugSwipeEndPos = canvasToTriangle(mousePosition.x, mousePosition.y, pressedTriangle);
                }
                if (isSwiping && swipedTriangle != null
                        && (pressedX - x) * (pressedX - x)
                        + (pressedY - y) * (pressedY - y) > MIN_SWIPE_DIST_SQUARED) {
                    Point2D.Float swipeEnd = canvasToTriangle(x, y, swipedTriangle);
                    float angle = (float) Math.atan2(swipeEnd.y - swipeStartPos.y, swipeEnd.x - swipeStartPos.x);
                    fireTriangleSwiped(
                            evt, swipedTriangle, angle);
                    swipedTriangle = null;
                }
            }
            oldx = x;
            oldy = y;
            isAnimating = true;
            repaint();
            updateCursor();
            if (scene != null) {
                idx3d_Triangle triangle = identifyTriangleAt(x, y);
                updateArmedTriangle(
                        evt, triangle);
                fireMouseDragged(
                        evt, triangle);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        if (isEnabled()) {
            mousePosition = evt.getPoint();
            pressedModifiersEx = evt.getModifiers();
            if (scene != null) {
                updateArmedTriangle(evt, identifyTriangleAt(evt.getX(), evt.getY()));
                updateCursor();
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        if (isEnabled()) {
            mousePosition = null;
            if (scene != null) {
                updateArmedTriangle(evt, null);
                updateCursor();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        if (isEnabled()) {
            mousePosition = evt.getPoint();
            pressedModifiersEx = evt.getModifiersEx();
            if (scene != null) {
                updateArmedTriangle(evt, identifyTriangleAt(evt.getX(), evt.getY()));
                updateCursor();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (isEnabled()) {
            mousePosition = evt.getPoint();
            pressedModifiersEx = evt.getModifiersEx();
            isPressed = true;
            pressedX = oldx = evt.getX();
            pressedY = oldy = evt.getY();
            pressedWhen = evt.getWhen();
            if (scene != null) {
                swipedTriangle = pressedTriangle = identifyTriangleAt(evt.getX(), evt.getY());
                if (interactionMode != Interaction.ROTATE_AND_SWIPE
                        && interactionMode != Interaction.ROTATE_OVER_BACKGROUND_AND_SWIPE) {
                    swipedTriangle = null;
                }
                if (swipedTriangle != null) {
                    if (scene.getSwipeListeners(swipedTriangle).length == 0) {
                        swipedTriangle = null;
                    } else {
                        swipeStartPos = canvasToTriangle(pressedX, pressedY, swipedTriangle);
                    }
                } else {
                    swipeStartPos = null;
                }
                isSwiping = swipedTriangle != null;
                if (isSwiping && swipeTimeout != Integer.MAX_VALUE) {
                    Timer t = new Timer(swipeTimeout, new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (swipedTriangle != null) {
                                isSwiping = false;
                                updateCursor();
                            }
                        }
                    });
                    t.setRepeats(false);
                    t.start();
                }
                updateCursor();
                fireMousePressed(
                        evt, pressedTriangle);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (isEnabled()) {
            mousePosition = evt.getPoint();
            // Workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6634290
            // Don't get pressedModifiersEx from mouseReleased event - they are bogus.
            // pressedModifiersEx = evt.getModifiersEx();
            isPressed = false;
            isAnimating = false;
            isSwiping = false;
            pressedTriangle = null;
            repaint();
            if (scene != null) {
                armedTriangle = identifyTriangleAt(evt.getX(), evt.getY());
                updateCursor();
                fireMouseReleased(
                        evt, armedTriangle);
            }
        }
    }

    public void mouseWheelMoved(MouseWheelEvent evt) {
        if (isEnabled() && evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            pressedModifiersEx = evt.getModifiersEx();
            float dx;
            float dy;
            if ((pressedModifiersEx & InputEvent.SHIFT_DOWN_MASK) == 0) {
                dx = (float) (evt.getUnitsToScroll()) / 50;
                dy = 0;
            } else {
                dy = (float) (evt.getUnitsToScroll()) / -50;
                dx = 0;
            }
            if (scene != null) {
                if (interactionMode == Interaction.ROTATE
                        || interactionMode == Interaction.ROTATE_AND_SWIPE
                        || interactionMode == Interaction.ROTATE_OVER_BACKGROUND_AND_SWIPE) {
                    idx3d_Camera camera = scene.camera(cameraName);
                    idx3d_Matrix camMx = camera.getMatrix();
                    camMx.inverse();
                    idx3d_Vector dv = new idx3d_Vector(dx, dy, 0);
                    dv.transform(camMx);
                    if (camera.getMatrix().m11 < 0) {
                        scene.rotate(dv.x, -dv.y, 0);
                    } else {
                        scene.rotate(dv.x, dv.y, 0);
                    }
                }
            }
            isAnimating = true;
            repaint();
            updateCursor();
        }
    }

    /** Updates the cursor to reflect the active tool of the canvas. */
    private void updateCursor() {
        if (isUpdateCursor) {
            //setCursor(Cursor.getPredefinedCursor(isPressed & !isSwiping ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            setCursor(Cursor.getPredefinedCursor(armedTriangle != null
                    && (scene.getActionListeners(armedTriangle).length != 0
                    || scene.getSwipeListeners(armedTriangle).length != 0) && !isPressed || isSwiping ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        }
    }

    /** Sets whether the mouse cursor should be updated by the canvas. */
    public void setUpdateCursor(boolean newValue) {
        boolean oldValue = isUpdateCursor;
        isUpdateCursor = newValue;
        if (newValue == false) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            updateCursor();
        }
        firePropertyChange("updateCursor", oldValue, newValue);
    }

    /** Returns true, if the mouse cursor is updated by the canvas. */
    public boolean isUpdateCursor() {
        return isUpdateCursor;
    }
    private GeneralPath debugOverlayPath, debugOverlayPath2;

    /**
     * Converts a location on the canvas to a location on the plane
     * of the specified triangle.
     *
     * @param x
     * @param y
     * @param triangle
     * @return Point on triangle.
     */
    protected Point2D.Float canvasToTriangle(int x, int y, idx3d_Triangle triangle) {
//System.out.println("canvasToTriangle @"+triangle.hashCode()+" x:"+triangle.p1.x);
        idx3d_Camera camera = scene.camera(cameraName);
        {
            // Project the triangle using the camera
            // We need to do this here, because the same scene can be rendered
            // by multiple cameras in multiple canvases simultaneously.
            idx3d_Matrix m = camera.getMatrix();
            idx3d_Matrix nm = camera.getNormalMatrix();
            idx3d_Object obj;
            idx3d_Triangle t;
            idx3d_Vertex v;
            //int w = renderPipeline.getWidth();
            //int h = renderPipeline.getHeight();
            idx3d_Matrix vertexProjection = new idx3d_Matrix();
            idx3d_Matrix normalProjection = new idx3d_Matrix();
            obj = triangle.parent;
            if (obj.visible) {
                //vertexProjection=obj.getVertexProjection();
                //normalProjection=obj.getNormalProjection();
                obj.getVertexProjectionInto(vertexProjection);
                obj.getNormalProjectionInto(normalProjection);
                vertexProjection.transform(m);
                normalProjection.transform(nm);
                for (int i = obj.vertices - 1; i
                        >= 0; i--) {
                    v = obj.vertex[i];
                    v.project(vertexProjection, normalProjection, camera);
                }
            }
        }
        // Get the coordinates of the projected triangle (the coordinates that
        // have been rendered on the canvas using a camera transform).
        idx3d_Vector[] pt = new idx3d_Vector[3];
        { // afact is the antialiasing factor
            float afact = renderPipeline.getScreen().antialias() ? 2f : 1f;
            float fact = camera.screenscale / camera.fovfact / ((triangle.p1.pos2.z > 0.1) ? triangle.p1.pos2.z : 0.1f);
            pt[0] = new idx3d_Vector(
                    (triangle.p1.pos2.x * fact + (camera.screenwidth >> 1)) / afact,
                    (-triangle.p1.pos2.y * fact + (camera.screenheight >> 1)) / afact,
                    (triangle.p1.pos2.z));
            fact = camera.screenscale / camera.fovfact / ((triangle.p2.pos2.z > 0.1) ? triangle.p2.pos2.z : 0.1f);
            pt[1] = new idx3d_Vector(
                    (triangle.p2.pos2.x * fact + (camera.screenwidth >> 1)) / afact,
                    (-triangle.p2.pos2.y * fact + (camera.screenheight >> 1)) / afact,
                    (triangle.p2.pos2.z));
            fact = camera.screenscale / camera.fovfact / ((triangle.p3.pos2.z > 0.1) ? triangle.p3.pos2.z : 0.1f);
            pt[2] = new idx3d_Vector(
                    (triangle.p3.pos2.x * fact + (camera.screenwidth >> 1)) / afact,
                    (-triangle.p3.pos2.y * fact + (camera.screenheight >> 1)) / afact,
                    (triangle.p3.pos2.z));
        } // Compute the z-coordinate of the projected mouse coordinates using
        // the plane equation derived from the projected triangle.
        idx3d_Vector pm;
        {
            float[] peq = idx3d_Vector.planeEquation(pt[0], pt[1], pt[2]);
            // ax+by+cz+d=0
            // ax+by+d=-cz
            // (ax+by+d)/-c=z
            pm = new idx3d_Vector(x, y, (peq[0] * x + peq[1] * y + peq[3]) / -peq[2]);
            // Verify, if pm is on plane:
            //float verify = peq[0] * pm.x + peq[1] * pm.y + peq[2] * pm.z + peq[3];
            //System.out.println("Verify pm:" + verify);
        }
        // Compute the orthogonal projection of the triangle and of the mouse
        // coordinates by factoring out the camera transform.
        idx3d_Vector[] ot = new idx3d_Vector[3];
        idx3d_Vector om;
        {
            float screenScale = camera.getScreenScale();
            // afact is the antialiasing factor
            float afact = renderPipeline.getScreen().antialias() ? 2f : 1f;
//            screenScale/=afact;
            float fovFact = camera.getFOVFact();
//            fovFact/=afact;
            float screenWidth = camera.getScreenWidth();
            float screenHeight = camera.getScreenHeight();
            /* Note in theory we don't need to compute this, because the values
             * are stored in triangle.p[1-3].pos2.
             * But we still have to do this, due to rounding errors in the
             * projected triangle.
             */
            GeneralPath path;
            ot[0] = triangle.p1.pos2.clone();
            ot[1] = triangle.p2.pos2.clone();
            ot[2] = triangle.p3.pos2.clone();
            path = new GeneralPath();
            path.moveTo(ot[0].x * 100, ot[0].y * 100);
            path.lineTo(ot[1].x * 100, ot[1].y * 100);
            path.lineTo(ot[2].x * 100, ot[2].y * 100);
            path.closePath();
            debugOverlayPathVerifyOrthogonal = path;
            for (int i = 0; i
                    < pt.length; i++) {
                // pfact is the scale factor of the perspective transformation
                float pfact = screenScale / fovFact / ((pt[i].z > 0.1) ? pt[i].z : 0.1f);
                ot[i] = new idx3d_Vector(
                        (pt[i].x - (screenWidth / 2 / afact)) * afact / pfact,
                        (pt[i].y - (screenHeight / 2 / afact)) * afact / -pfact,
                        pt[i].z);
            }
            path = new GeneralPath();
            path.moveTo(ot[0].x * 100, ot[0].y * 100);
            path.lineTo(ot[1].x * 100, ot[1].y * 100);
            path.lineTo(ot[2].x * 100, ot[2].y * 100);
            path.closePath();
            debugOverlayPathComputedOrthogonal = path;
            float pfact = screenScale / fovFact / ((pm.z > 0.1) ? pm.z : 0.1f);
            om = new idx3d_Vector(
                    (pm.x - screenWidth / 2 / afact) * afact / pfact,
                    (pm.y - screenHeight / 2 / afact) * afact / -pfact,
                    pm.z);
            /*
             // Verify, if om is on plane:
             float[] peq = idx3d_Vector.planeEquation(ot[0], ot[1], ot[2]);
             float verify = peq[0] * om.x + peq[1] * om.y + peq[2] * om.z + peq[3];
             System.out.println("Verify om:" + verify);
             for (int i = 0; i < 3; i++) {
             verify = peq[0] * ot[i].x + peq[1] * ot[i].y + peq[2] * ot[i].z + peq[3];
             System.out.println("Verify ot[" + i + "]:" + verify);
             }*/
        }
        // Transform the orthogonal triangle into the plane of the triangle
        idx3d_Vector[] nt = new idx3d_Vector[3];
        idx3d_Vector nm;
        {
            // Compute a rotation matrix which transforms from the plane
            // of the orthogonal triangle to a plane with the z-coordinate facing
            // to us.
            idx3d_Vector normal = idx3d_Vector.vectorProduct(
                    idx3d_Vector.sub(ot[0], ot[1]),
                    idx3d_Vector.sub(ot[2], ot[1]));
            normal.normalize();
            idx3d_Matrix m = idx3d_Matrix.fromToRotation(normal, new idx3d_Vector(0, 0, 1));
            for (int i = 0; i
                    < ot.length; i++) {
                nt[i] = ot[i].transform(m);
            }
            nm = om.transform(m);
        } // Place the first vertex of the triangle at 0,0,0 of the plane
        {
            float shiftx = -nt[0].x;
            float shifty = -nt[0].y;
            float shiftz = -nt[0].z;
            for (int i = 0; i
                    < nt.length; i++) {
                nt[i].x += shiftx;
                nt[i].y += shifty;
                nt[i].z += shiftz;
            }
            nm.x += shiftx;
            nm.y += shifty;
            nm.z += shiftz;
        }
        // Rotate the triangle
        {
            float angle = (float) Math.atan2(nt[1].y - nt[0].y, nt[1].x - nt[0].x);
            idx3d_Matrix m = new idx3d_Matrix();
            m.rotate(0, 0, angle);
            for (int i = 0; i
                    < nt.length; i++) {
                nt[i] = nt[i].transform(m);
            }
            nm = nm.transform(m);
        }
        GeneralPath path = new GeneralPath();
        float f = 100f;
        path.moveTo(nt[0].x * f, nt[0].y * f);
        path.lineTo(nt[1].x * f, nt[1].y * f);
        path.lineTo(nt[2].x * f, nt[2].y * f);
        path.closePath();
        path.moveTo(nm.x * f - 10, nm.y * f - 10);
        path.lineTo(nm.x * f + 10, nm.y * f + 10);
        path.moveTo(nm.x * f - 10, nm.y * f + 10);
        path.lineTo(nm.x * f + 10, nm.y * f - 10);
        debugOverlayPath = path;
        path = new GeneralPath();
        path.moveTo(pt[0].x, pt[0].y);
        path.lineTo(pt[1].x, pt[1].y);
        path.lineTo(pt[2].x, pt[2].y);
        path.closePath();
        debugOverlayPath2 = path;
        return new Point2D.Float(nm.x, nm.y);
    }

    public void stateChanged(ChangeEvent event) {
        repaint();
    }

    @Override
    public Point getMousePosition() {
        return mousePosition;
    }

    /**
     * Updates the currently armed triangle.
     *
     * @param evt The mouse event which caused the arming.
     * @param newTriangle The newly armed triangle. Pass null, to dearm the currently
     * armed triangle.
     */
    private void updateArmedTriangle(MouseEvent evt, idx3d_Triangle newTriangle) {
        idx3d_Object oldObject = (armedTriangle == null) ? null : armedTriangle.parent;
        idx3d_Object newObject = (newTriangle == null) ? null : newTriangle.parent;
        if (armedTriangle != newTriangle) {
            if (armedTriangle != null) {
                fireTriangleExited(evt, armedTriangle);
            }
            armedTriangle = newTriangle;
            if (armedTriangle != null) {
                fireTriangleEntered(evt, armedTriangle);
            }
        }
        if (oldObject != newObject) {
            fireObjectExited(evt, oldObject);
            fireObjectEntered(
                    evt, newObject);
        }
    }

    private void fireObjectExited(MouseEvent evt, idx3d_Object obj) {
        if (!quiet) {
            if (obj != null) {
                MouseListener[] listeners = scene.getMouseListeners(obj);
                for (int i = 0; i
                        < listeners.length; i++) {
                    listeners[i].mouseExited(evt);
                }
            }
        }
    }

    private void fireObjectEntered(MouseEvent evt, idx3d_Object obj) {
        if (!quiet) {
            if (obj != null) {
                MouseListener[] listeners = scene.getMouseListeners(obj);
                for (int i = 0; i
                        < listeners.length; i++) {
                    listeners[i].mouseEntered(evt);
                }
            }
        }
    }

    private void fireTriangleExited(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            MouseListener[] listeners = scene.getMouseListeners(triangle);
            for (int i = 0; i
                    < listeners.length; i++) {
                listeners[i].mouseExited(evt);
            }
        }
    }

    private void fireTriangleEntered(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            MouseListener[] listeners = scene.getMouseListeners(triangle);
            for (int i = 0; i
                    < listeners.length; i++) {
                listeners[i].mouseEntered(evt);
            }
        }
    }

    private void fireTriangleSwiped(MouseEvent evt, idx3d_Triangle triangle, float angle) {
        if (!quiet) {
//System.out.println("Triangle Swiped "+(int)(angle/Math.PI*180)+"°");
            SwipeListener[] listeners = scene.getSwipeListeners(triangle);
            SwipeEvent sevt = null;
            if (listeners.length > 0) {
                sevt = new SwipeEvent(evt, angle);
            }
            for (int i = 0; i
                    < listeners.length; i++) {
                listeners[i].faceSwiped(sevt);
            }
        }
    }

    private void fireMouseClicked(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            MouseListener[] listeners = scene.getMouseListeners(triangle);
            if (listeners.length == 0) {
                listeners = scene.getMouseListeners(triangle.parent);
            }
            for (int i = 0; i
                    < listeners.length; i++) {
                listeners[i].mouseClicked(evt);
            }
        }
    }

    protected void fireMousePressed(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            // Fire scene event
            if (triangle != null) {
                MouseListener[] listeners = scene.getMouseListeners(triangle);
                if (listeners.length == 0) {
                    listeners = scene.getMouseListeners(triangle.parent);
                }
                for (int i = 0; i
                        < listeners.length; i++) {
                    listeners[i].mousePressed(evt);
                }
            }
        }
    }

    private void fireMouseReleased(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            if (triangle != null) {
                MouseListener[] listeners = scene.getMouseListeners(triangle);
                if (listeners.length == 0) {
                    listeners = scene.getMouseListeners(triangle.parent);
                }
                for (int i = 0; i
                        < listeners.length; i++) {
                    listeners[i].mouseReleased(evt);
                }
            }
        }
    }

    protected void fireMouseDragged(MouseEvent evt, idx3d_Triangle triangle) {
        if (!quiet) {
            // Fire scene event
            if (triangle != null) {
                MouseListener[] listeners = scene.getMouseListeners(triangle);
                if (listeners.length == 0) {
                    listeners = scene.getMouseListeners(triangle.parent);
                }
                for (int i = 0; i
                        < listeners.length; i++) {
                    //  listeners[i].mouseDragged(evt);
                }
            }
        }
    }

    public void reset() {
        if (scene != null) {
            scene.resetTransform();
        }
    }

    public void flush() {
        renderPipeline = null;
    }

    /** Gets the timeout for swipe operations.
     * If the user presses the mouse key for a longer amount of milliseconds,
     * the canvas performs a scene rotation rather than a swipe operation.
     *
     * Integer.MAX_VALUE indicates infinite timeout.
     */
    public int getSwipeTimeout() {
        return swipeTimeout;
    }

    /** Sets the timeout for swipe operations.
     * Integer.MAX_VALUE indicates infinite delay. */
    public void setSwipeTimeout(int newValue) {
        this.swipeTimeout = newValue;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
