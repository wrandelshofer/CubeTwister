/* @(#)JCanvas3D.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import ch.randelshofer.gui.event.SwipeEvent;
import ch.randelshofer.gui.event.SwipeListener;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A canvas for rendering three dimensional geometry.
 *
 * @author Werner Randelshofer
 */
public class JCanvas3D extends JComponent implements ChangeListener {
    private final static long serialVersionUID = 1L;

    private Dimension preferredSize = new Dimension(200, 200);
    protected Scene3D scene;
    @Nullable
    protected Graphics backGfx;
    @Nullable
    protected Image backImg;
    protected Dimension backSize = new Dimension(0, 0);
    protected Transform3DModel transformModel;
    //    private double xrot, yrot;
    protected Object lock = new Object();
    @Nonnull
    protected Point3D observer = new Point3D(0, 0, 260);
    protected Point3D lightSource = new Point3D(-500, 500, 1000);
    protected double ambientLightIntensity = 0.6;
    protected double lightSourceIntensity = 1.0;
    @Nullable
    private Image backgroundImage;
    /** This flag is true during mouse drag events. */
    protected boolean isAdjusting;
    protected boolean isDirty = true;
    protected double scaleFactor = 1.0;
    private EventHandler eventHandler;
    /**
     * Desired maximal Frame Per Seconds Rate.
     */
    private int maxFPS = 30;    // BEGIN PATCH Antialias
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
    private boolean isSwiping;
    @Nullable
    private Point2D.Double swipeStartPos;
    private Point2D.Double debugSwipeEndPos;
    private GeneralPath debugOverlayPathVerifyOrthogonal;
    private GeneralPath debugOverlayPathComputedOrthogonal;
    private GeneralPath debugOverlayPath;
    private GeneralPath debugOverlayPath2;
        private boolean isArmed = true;

    public void flush() {
        backGfx = null;
        backImg = null;
    }

    private static class ActiveEntry {

        private Shape path;
        private Face3D face;

        public ActiveEntry(Shape path, Face3D face) {
            this.path = path;
            this.face = face;
        }
    }
    /**
     * Contains all faces that are visible and have actions.
     * A pair of elements contains the 2d polygon of the face
     * and the action of the face.
     */
    @Nonnull
    private ArrayList<ActiveEntry> activeFaces = new ArrayList<ActiveEntry>();
    /**
     * This flag is true when a mouse down or a mouse up evt was a popup evt.
     * A mouse clicked evt is ignored when this flag is set to true.
     */
    protected boolean isPopupTrigger;
    private boolean isRotateOnMouseDrag = false;
    private java.beans.PropertyChangeSupport changeSupport;
    private boolean isPressed;
    @Nullable
    protected Face3D swipedFace;
    @Nullable
    private Face3D armedFace;
    /** Defines the delay between mouse pressed and mouse dragged where swipe
     * events are accepted. Set this to Integer.MAX_VALUE for infinite delay.
     */
    private int swipeDelay = 500;
    /**
     * Holds the square of the minimal distance which is needed to fire a
     * SwipeEvent.
     */
    private final static int MIN_SWIPE_DIST_SQUARED = 5 * 5;

    public enum Interaction {
        /* The canvas does not perform interactions on its own. */

        NOTHING,
        /* The canvas rotates the scene when the user drags the mouse. */
        ROTATE,
        /* The canvas rotates the scene if the drag started over the background. */
        ROTATE_AND_SWIPE
    }
    private Interaction interactionMode = Interaction.ROTATE_AND_SWIPE;

    public JCanvas3D() {
        eventHandler=createEventHandler();
        addMouseListener(eventHandler);
        addMouseWheelListener(eventHandler);
        setBackground(Color.white);
        setRotateOnMouseDrag(true);
        setTransformModel(new DefaultTransform3DModel());
    }

    @Nonnull
    protected EventHandler createEventHandler() {
        return new EventHandler();
    }

    public void setInteractionMode(Interaction newValue) {
        Interaction oldValue = this.interactionMode;
        this.interactionMode = newValue;
        firePropertyChange("interactionMode", oldValue, newValue);
    }

    public Interaction getInteractionMode() {
        return interactionMode;
    }

    public void setTransformModel(@Nonnull Transform3DModel value) {
        Transform3DModel oldValue = this.transformModel;
        if (oldValue != null) {
            oldValue.removeChangeListener(this);
        }
        this.transformModel = value;
        value.addChangeListener(this);
        stateChanged(null);
        firePropertyChange("transformModel", oldValue, value);
    }

    public Transform3DModel getTransformModel() {
        return transformModel;
    }

    public void setRotateOnMouseDrag(boolean b) {
        if (b != isRotateOnMouseDrag) {
            isRotateOnMouseDrag = b;
            if (b) {
                addMouseMotionListener(eventHandler);
            } else {
                removeMouseMotionListener(eventHandler);
            }
        }
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }
    /*
    public void update(Graphics g) {
    paint(g);
    }*/

    @Override
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        Insets insets = getInsets();
        // Create an image for double buffering
        Dimension s = getSize();
        s.width -= insets.left - insets.right;
        s.height -= insets.top - insets.bottom;
        if (backGfx == null || backSize.width != s.width || backSize.height != s.height) {
            if (s.width <= 0 || s.height <= 0) {
                return;
            }
            createBackGraphics(s);
            backSize = s;
            isDirty = true;
        }

        if (isDirty) {
            isDirty = false;
            synchronized (lock) {
                boolean isAntialias = !isAdjusting && !scene.isAdjusting();
                isAntialias |= antialiasRenderTime < 1000 / (minFPS);

                long start = System.currentTimeMillis();
                setGraphicHints(backGfx, isAntialias);
                paintBackground(backGfx);
                paint3D(backGfx);
                if (isAntialias) {
                    // Take the average out of 4 measurements to even out
                    // Garbage collection times and other unexpected delays.
                    antialiasRenderTime = (antialiasRenderTime * 3 + System.currentTimeMillis() - start) / 4;
                }
            }
        }
//System.out.println("antialiasing render time: "+antialiasRenderTime+" minFPS render time:"+(1000/minFPS));

        g.drawImage(backImg, insets.left, insets.top, null);

        if (false && debugOverlayPath != null) {
            g.setColor(Color.GREEN);
            if (debugOverlayPath2 != null) {
                g.draw(debugOverlayPath2);
            }
            g.translate(getWidth() / 2, getHeight() / 2);
            g.setColor(new Color(0xa0ffff00, true));
            if (debugOverlayPathVerifyOrthogonal != null) {
                g.fill(debugOverlayPathVerifyOrthogonal);
            }
            g.setColor(Color.ORANGE);
            if (debugOverlayPathComputedOrthogonal != null) {
                g.draw(debugOverlayPathComputedOrthogonal);
            }
            g.setColor(Color.CYAN.darker());
            g.draw(debugOverlayPath);
            if (swipeStartPos != null && debugSwipeEndPos != null) {
                float f = 100f;
                g.draw(new Line2D.Double(swipeStartPos.x * f, swipeStartPos.y * f, debugSwipeEndPos.x * f, debugSwipeEndPos.y * f));
            }
            g.translate(getWidth() / -2, getHeight() / -2);
        }
    }

    public void setToIdentity() {
        transformModel.setToIdentity();
    }

    public void setObserver(float distance) {
        observer = new Point3D(0, 0, distance);
    }

    public void setAmbientLightIntensity(double d) {
        ambientLightIntensity = d;
    }

    public void setLightSourceIntensity(double d) {
        lightSourceIntensity = d;
    }

    public void setLightSource(Point3D p) {
        lightSource = p;
    }

    public void setBackgroundImage(Image img) {
        backgroundImage = img;
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img, 0);
        mt.checkID(0, true);

    }

    public void setTransform(Transform3D transform) {
        transformModel.setTransform(transform);
    }

    public Transform3D getTransform() {
        return transformModel.getTransform();
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        // We get rid of the background image if we can not
        // load it.
        if ((infoflags & ImageObserver.ERROR) != 0) {
            if (img == backgroundImage) {
                backgroundImage = null;
            }
        }
        if (img == backgroundImage) {
            isDirty = true;
        }
        return super.imageUpdate(img, infoflags, x, y, width, height);
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        stateChanged(null);
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScene(Scene3D u) {
        scene = u;
        stateChanged(null);
    }

    public Scene3D getScene() {
        return scene;
    }

    /**
     * Draws the background.
     */
    protected void paintBackground(@Nonnull Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, backSize.width, backSize.height);
        }
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, backSize.width, backSize.height, this);
        } else if (!isOpaque()) {
            Insets insets = getInsets();
            g.setColor(getBackground());
            g.fillRect(insets.left, insets.top,
                    backSize.width - insets.left - insets.right,
                    backSize.height - insets.top - insets.bottom);
        }
    }

    /** Updates the cursor to reflect the active tool of the canvas. */
    private void updateCursor() {
//        setCursor(Cursor.getPredefinedCursor(isPressed & !isSwiping ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        setCursor(Cursor.getPredefinedCursor(armedFace != null
                && (armedFace.getAction() != null
                || armedFace.getSwipeListenerCount() != 0) && !isPressed || isSwiping ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void setPreferredSize(Dimension s) {
        preferredSize = s;
    }

    @Override
    public Dimension getPreferredSize() {
        return (preferredSize != null) ? preferredSize : super.getPreferredSize();
    }

    @Override
    public void addPropertyChangeListener(
            @Nullable PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            changeSupport = new java.beans.PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(
            @Nullable PropertyChangeListener listener) {
        if (listener == null || changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    //--------

    private void fireFaceSwiped(@Nonnull MouseEvent evt, @Nonnull Face3D face, float angle) {
        SwipeListener[] listeners = face.getSwipeListeners();
        SwipeEvent sevt = null;
        if (listeners.length > 0) {
            sevt = new SwipeEvent(evt, angle);
        }
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].faceSwiped(sevt);
        }
    }

    /**
     * Sets the antialiasing hint for the provided graphics object on or off.
     *
     * Note: This is a very expensive operation. Don't call it whithin a thight
     * loop.
     *
     * @return Returns true if the hint could be set successfully. Returns
     * false, if either casting the Graphics object to Graphics2D failed, or
     * if the Java VM does not support Java2D.
     */
    private void setGraphicHints(Graphics gr, boolean isAntialias) {
        Graphics2D g = (Graphics2D) gr;
        if (isAntialias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
    }

    protected void createBackGraphics(@Nonnull Dimension s) {
        backImg = createImage(s.width, s.height);
        backGfx = backImg.getGraphics();
    }

    protected void paint3D(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        try {
            Insets insets = getInsets();

            int[] xpoints, ypoints;
            float[] coords;
            int[] vertices;
            float cx, cy;
            float sx, sy;
            int i, j;

            Dimension size = getSize();
            Transform3D transform = transformModel.getTransform();
            cx = insets.left + (size.width - insets.left - insets.right) / 2;
            cy = insets.top + (size.height - insets.top - insets.bottom) / 2;
            sx = (float) (Math.min((size.width - insets.left - insets.right) / 2, (size.height - insets.top - insets.bottom) / 2) * scaleFactor);
            sy = -sx;

            ArrayList<Face3D> visibleFaces = new ArrayList<Face3D>();
            activeFaces.clear();
            scene.addVisibleFacesTo(visibleFaces, transform, observer);
            Face3D[] faces = visibleFaces.toArray(new Face3D[visibleFaces.size()]);
            Arrays.sort(faces);

            xpoints = new int[5];
            ypoints = new int[5];

            float ox = (float) observer.x;
            float oy = (float) observer.y;
            float oz = (float) observer.z;

            for (i = 0; i < faces.length; i++) {
                Face3D face = faces[i];
                if (face == null) {
                    continue;
                }
                coords = face.getCoords();
                vertices = face.getVertices();
                GeneralPath path = new GeneralPath();

                int vertice = vertices[0] * 3;
                float div = coords[vertices[0] * 3 + 2] - oz;
                if (div != 0) {
                    path.moveTo(
                            (cx + ((ox - (oz * coords[vertice] - ox) / div) * sx)),
                            (cy + ((oy - (oz * coords[vertice + 1] - oy) / div) * sy)));
                } else {
                    path.moveTo((cx + (ox * sx)), (cy + (oy * sy)));
                }
                for (j = 1; j < vertices.length; j++) {
                    vertice = vertices[j] * 3;
                    div = coords[vertices[j] * 3 + 2] - oz;
                    if (div != 0) {
                        path.lineTo(
                                (cx + ((ox - (oz * coords[vertice] - ox) / div) * sx)),
                                (cy + ((oy - (oz * coords[vertice + 1] - oy) / div) * sy)));
                    } else {
                        path.lineTo(
                                (cx + (ox * sx)),
                                (cy + (oy * sy)));
                    }
                }
                path.closePath();

                Color color;
                if ((color = face.getFillColor()) != null) {
                    /*
                    float brightness = (float) face.getBrightness(lightSource, lightSourceIntensity, ambientLightIntensity);
                    //if (brightness > 1.0) brightness = Math.sqrt(brightness);
                    brightness = 1.0f + (float) shadingIntensity * (brightness - 1.0f);
                     */
                    double brightness;
                    if (lightSource == null) {
                        brightness = 1d;
                    } else {
                        brightness = face.getBrightness(lightSource, lightSourceIntensity, ambientLightIntensity);
                    }
                    g.setColor(
                            new Color(
                            Math.min(255, (int) (color.getRed() * brightness)),
                            Math.min(255, (int) (color.getGreen() * brightness)),
                            Math.min(255, (int) (color.getBlue() * brightness))));
                    g.fill(path);
                }

                if ((color = face.getBorderColor()) != null) {
                    g.setColor(color);
                    g.draw(path);
                }

                //if (!isAdjusting) {
                    if (face.getAction() != null || face.getSwipeListenerCount() != 0) {
                        activeFaces.add(new ActiveEntry(path, face));
                    }
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Face3D getFaceAt(int x, int y) {
        for (int i = activeFaces.size() - 1; i >= 0; i--) {
            Shape poly = activeFaces.get(i).path;
            Face3D face = activeFaces.get(i).face;
            if (poly.contains(x, y)) {
                return face;
            }
        }
        return null;
    }

    @Nonnull
    public Point2D.Double canvasToFace(int x, int y, @Nonnull Face3D face) {
        // We only need a triangle from the first three points of a face to
        // perform the computations.
//        Point3D[] triangle = new Point3D[3];
        Point3D[] triangle = new Point3D[face.getVertices().length];
        {
            Transform3D transform = transformModel.getTransform();

            ArrayList<Face3D> visibleFaces = new ArrayList<Face3D>();
            // XXX - This is overkill. We should only compute the face and not
            // the whole scene.
            scene.addVisibleFacesTo(visibleFaces, transform, observer);

            float[] coords = face.getCoords();
            int[] vertices = face.getVertices();
            for (int i = 0; i < triangle.length; i++) {
                triangle[i] = new Point3D(coords[vertices[i] * 3], coords[vertices[i] * 3 + 1], coords[vertices[i] * 3 + 2]);
            }
        }

        Insets insets = getInsets();
        Dimension size = getSize();
        float sx, sy;
        sx = (float) (Math.min((size.width - insets.left - insets.right) / 2, (size.height - insets.top - insets.bottom) / 2) * scaleFactor);
        sy = -sx;
        float cx, cy;
        cx = insets.left + (size.width - insets.left - insets.right) / 2;
        cy = insets.top + (size.height - insets.top - insets.bottom) / 2;

        float ox = (float) observer.x;
        float oy = (float) observer.y;
        float oz = (float) observer.z;

        // Get the coordinates of the projected triangle (the coordinates that
        // have been rendered on the canvas using a camera transform).
        Point3D[] pt = new Point3D[triangle.length];
        {
            for (int i = 0; i < triangle.length; i++) {
                double div = triangle[i].z - oz;
                if (div != 0) {
                    pt[i] = new Point3D(
                            (cx + ((ox - (oz * triangle[i].x - ox) / div) * sx)),
                            (cy + ((oy - (oz * triangle[i].y - oy) / div) * sy)),
                            triangle[i].z);
                } else {
                    pt[i] = new Point3D(
                            cx + (ox * sx),
                            cy + (oy * sy),
                            triangle[i].z);
                }
            }
        }
        // Compute the z-coordinate of the projected mouse coordinates using
        // the plane equation derived from the projected triangle.
        Point3D pm;
        {
            double[] peq = Point3D.planeEquation(pt[0], pt[1], pt[2]);
            // ax+by+cz+d=0
            // ax+by+d=-cz
            // (ax+by+d)/-c=z
            pm = new Point3D(x, y, (peq[0] * x + peq[1] * y + peq[3]) / -peq[2]);

            // Verify, if pm is on plane:
            //double verify = peq[0] * pm.x + peq[1] * pm.y + peq[2] * pm.z + peq[3];
            //System.out.println("Verify pm:" + verify);
        }

        // Compute the orthogonal projection of the triangle and of the mouse
        // coordinates by factoring out the camera transform.
        Point3D[] ot = new Point3D[triangle.length];
        Point3D[] oot = new Point3D[triangle.length];
        Point3D om;
        {
            /* Note in theory we don't need to compute this, because the values
             * are stored in triangle.p[1-3].pos2.
             * But we still have to do this, due to rounding errors in the
             * projected triangle.
             */
            GeneralPath path;
            for (int i = 0; i < triangle.length; i++) {
                ot[i] = triangle[i].clone();
            }
            path = new GeneralPath();
            float sf = 1;
            path.moveTo((float) ot[0].x * sf, (float) ot[0].y * sf);
            for (int i = 1; i < triangle.length; i++) {
                path.lineTo((float) ot[i].x * sf, (float) ot[i].y * sf);
            }
            path.closePath();
            debugOverlayPathVerifyOrthogonal = path;
            for (int i = 0; i < pt.length; i++) {
                // pfact is the scale factor of the perspective transformation
                double div = pt[i].z - oz;
                oot[i] = ot[i];
                ot[i] = new Point3D(
                        (((pt[i].x - cx) / sx) * -div + ox + ox) / oz,
                        (((pt[i].y - cy) / sy) * -div + oy + oy) / oz,
                        pt[i].z);
            }
            path = new GeneralPath();
            path.moveTo((float) ot[0].x * sf, (float) ot[0].y * sf);
            for (int i = 1; i < triangle.length; i++) {
                path.lineTo((float) ot[i].x * sf, (float) ot[i].y * sf);
            }
            path.closePath();
            debugOverlayPathComputedOrthogonal = path;

            double div = pm.z - oz;
            om = new Point3D(
                    (((pm.x - cx) / sx) * -div + ox + ox) / oz,
                    (((pm.y - cy) / sy) * -div + oy + oy) / oz,
                    pm.z);
            path.moveTo((float) om.x * sf - 10, (float) om.y * sf - 10);
            path.lineTo((float) om.x * sf + 10, (float) om.y * sf + 10);
            path.moveTo((float) om.x * sf - 10, (float) om.y * sf + 10);
            path.lineTo((float) om.x * sf + 10, (float) om.y * sf - 10);

            // Verify, if om is on plane:
            {
                double[] peq = Point3D.planeEquation(ot[0], ot[1], ot[2]);
                double verify = peq[0] * om.x + peq[1] * om.y + peq[2] * om.z + peq[3];
                //System.out.println("** Verify om:" + verify + " ** " + om);
                Point3D omShouldBe = new Point3D(om.x, om.y, (peq[0] * om.x + peq[1] * om.y + peq[3]) / -peq[2]);
                //System.out.println("** om should be:" + omShouldBe);
                om.z = omShouldBe.z; // XXX - This is a dirty ugly hack!! We should fix the computation


                for (int i = 0; i < 3; i++) {
                    verify = peq[0] * ot[i].x + peq[1] * ot[i].y + peq[2] * ot[i].z + peq[3];
                    //  System.out.println("Verify ot[" + i + "]:" + verify+" -- "+ot[i]);
                }
                /*
                peq = Point3D.planeEquation(oot[0], oot[1], oot[2]);
                verify = peq[0] * om.x + peq[1] * om.y + peq[2] * om.z + peq[3];
                //System.out.println("** Verify om on OOT:" + verify+" ** "+om);
                for (int i = 0; i < 3; i++) {
                verify = peq[0] * ot[i].x + peq[1] * ot[i].y + peq[2] * ot[i].z + peq[3];
                //System.out.println("Verify ot[" + i + "] on OOT:" + verify+" -- "+ot[i]);
                }*/
                /*
                for (int i = 0; i < 3; i++) {
                Point3D diff = Point3D.sub(ot[i], oot[i]);
                System.out.println("Verify ot->oot[" + i + "]  diff:" + diff);
                }*/
            }
        }
        // Transform the orthogonal triangle into the plane of the triangle
        Point3D[] nt = new Point3D[triangle.length];
        Point3D nm;
        {
            // Compute a rotation matrix which transforms from the plane
            // of the orthogonal triangle to a plane with the z-coordinate facing
            // to us.
            Point3D normal = Point3D.cross(
                    Point3D.sub(ot[0], ot[1]),
                    Point3D.sub(ot[2], ot[1]));
            normal = normal.normalized();
            Transform3D m = Transform3D.fromToRotation(normal, new Point3D(0, 0, 1));
            m = m.getInverse();
            for (int i = 0; i < ot.length; i++) {
                nt[i] = m.transform(ot[i], null);
                //nt[i] = ot[i].transform(m);
            }
            nm = m.transform(om, null);
            //nm = om.transform(m);
        }

        // Place the first vertex of the triangle at 0,0,0 of the plane
        {
            double shiftx = -nt[0].x;
            double shifty = -nt[0].y;
            double shiftz = -nt[0].z;
            for (int i = 0; i < nt.length; i++) {
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
            double angle = Math.atan2(nt[1].y - nt[0].y, nt[1].x - nt[0].x);

            Transform3D m = new Transform3D();
            m.rotate(0, 0, angle);
            for (int i = 0; i < nt.length; i++) {
                //nt[i] = nt[i].transform(m);
                nt[i] = m.transform(nt[i], null);
            }
            //nm = nm.transform(m);
            nm = m.transform(nm, null);
        }

        GeneralPath path = new GeneralPath();
        float f = 1f;
        path.moveTo((float) nt[0].x * f, (float) nt[0].y * f);
        for (int i = 1; i < nt.length; i++) {
            path.lineTo((float) nt[i].x * f, (float) nt[i].y * f);
        }
        path.closePath();
        path.moveTo((float) nm.x * f - 10, (float) nm.y * f - 10);
        path.lineTo((float) nm.x * f + 10, (float) nm.y * f + 10);
        path.moveTo((float) nm.x * f - 10, (float) nm.y * f + 10);
        path.lineTo((float) nm.x * f + 10, (float) nm.y * f - 10);
        debugOverlayPath = path;

        path = new GeneralPath();
        path.moveTo((float) pt[0].x * f, (float) pt[0].y * f);
        for (int i = 1; i < nt.length; i++) {
            path.lineTo((float) pt[i].x * f, (float) pt[i].y * f);
        }
        path.closePath();
        debugOverlayPath2 = path;

        return new Point2D.Double(nm.x, nm.y);
    }
        @Override
        public void stateChanged(ChangeEvent event) {
            isDirty = true;
            repaint();
        }

    protected class EventHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

        private int prevx, prevy;
        private int pressedX, pressedY;
        private boolean isMouseDrag = false;
        private long pressedWhen;
    private int pressedModifiersEx;

        @Override
        public void mouseClicked(@Nonnull MouseEvent evt) {
            try {
                if (isEnabled() && !isPopupTrigger
                        && pressedWhen != -1 && evt.getWhen() - pressedWhen < swipeDelay) {
                    int x = evt.getX();
                    int y = evt.getY();
                    prevx = x;
                    prevy = y;
                    Face3D face = getFaceAt(x, y);
                    if (face != null) {
                        //
                        //face.handleEvent(evt);
                        face.handleEvent(new MouseEvent(evt.getComponent(), evt.getID(), evt.getWhen(), pressedModifiersEx,
                                evt.getX(), evt.getY(), evt.getClickCount(), evt.isPopupTrigger(), evt.getButton()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void mouseDragged(@Nonnull MouseEvent evt) {
            isPopupTrigger = false;
            if (isAdjusting /*&& isArmed*/ && isEnabled()) {

                int x = evt.getX();
                int y = evt.getY();

                // Clear pressed and swiped triangles, if the user waited too long
                // before dragging the mouse
                if (pressedWhen != -1) {
                    if (evt.getWhen() - pressedWhen > swipeDelay) {
                        isSwiping = false;
                    }
                    pressedWhen = -1;
                }

                if (scene != null) {
                    if (interactionMode == Interaction.ROTATE
                            || interactionMode == Interaction.ROTATE_AND_SWIPE && !isSwiping) {
                        Dimension size = getSize();
                        double xtheta = (prevy - y) * (Math.PI * 2d / (double) size.width);
                        double ytheta = (prevx - x) * (Math.PI * 2d / (double) size.height);
                        transformModel.rotate(xtheta, ytheta, 0);
                    }
                    if (isSwiping && swipedFace != null) {
                        debugSwipeEndPos = canvasToFace(x, y, armedFace);

                    }
                    if (isSwiping && swipedFace != null
                            && (pressedX - x) * (pressedX - x)
                            + (pressedY - y) * (pressedY - y) > MIN_SWIPE_DIST_SQUARED) {

                        Point2D.Double scrapeEnd = canvasToFace(x, y, swipedFace);
                        float angle = (float) Math.atan2(scrapeEnd.y - swipeStartPos.y, scrapeEnd.x - swipeStartPos.x);
                        fireFaceSwiped(evt, swipedFace, -angle);

                        swipedFace = null;
                    }
                }
                prevx = x;
                prevy = y;
            }
        }

        @Override
        public void mouseWheelMoved(@Nonnull MouseWheelEvent evt) {
            if (isEnabled() && evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                pressedModifiersEx = evt.getModifiersEx();
                float dx;
                float dy;
                if ((pressedModifiersEx & InputEvent.SHIFT_DOWN_MASK) != 0) {
                    dx = (float) (evt.getUnitsToScroll()) / -50;
                    dy = 0;
                } else {
                    dy = (float) (evt.getUnitsToScroll()) / -50;
                    dx = 0;
            }

            if (scene != null) {
                if (interactionMode == Interaction.ROTATE
                        || interactionMode == Interaction.ROTATE_AND_SWIPE) {
                        double xtheta = (dy);
                        double ytheta = (dx);
                        transformModel.rotate(xtheta, ytheta, 0);
                }
            }
            repaint();
            updateCursor();
        }
    }

        @Override
        public void mouseMoved(@Nonnull MouseEvent event) {
            isPopupTrigger = false;
            int x = event.getX();
            int y = event.getY();
            armedFace = getFaceAt(x, y);
            updateCursor();
        }

        @Override
        public void mouseEntered(@Nonnull MouseEvent event) {
            isArmed = true;
            int x = event.getX();
            int y = event.getY();
            armedFace = getFaceAt(x, y);
            updateCursor();
        }

        @Override
        public void mouseExited(MouseEvent event) {
            isArmed = false;
            armedFace = null;
            updateCursor();
        }

        @Override
        public void mousePressed(@Nonnull MouseEvent evt) {
            // Workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6634290
            pressedModifiersEx = evt.getModifiersEx();

            int x = evt.getX();
            int y = evt.getY();
            isPressed = true;
            pressedX = x;
            pressedY = y;
            pressedWhen = evt.getWhen();

            isPopupTrigger = evt.isPopupTrigger();
            if (isEnabled() && !isPopupTrigger) {
                isAdjusting = true;
                prevx = pressedX;
                prevy = pressedY;
            }
            if (scene != null) {
                swipedFace = armedFace = getFaceAt(x, y);
                if (swipedFace != null) {
                    if (swipedFace.getSwipeListeners().length == 0) {
                        swipedFace = null;
                    } else {
                        swipeStartPos = canvasToFace(pressedX, pressedY, swipedFace);
                    }
                } else {
                    swipeStartPos = null;
                }
                isSwiping = swipedFace != null;
                if (isSwiping && swipeDelay != Integer.MAX_VALUE) {
                    Timer t = new Timer(swipeDelay, new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (swipedFace != null) {
                                isSwiping = false;
                                updateCursor();
                            }
                        }
                    });
                    t.setRepeats(false);
                    t.start();
                }
                updateCursor();
                //fireMousePressed(evt, armedFace);
            }
        }

        @Override
        public void mouseReleased(@Nonnull MouseEvent event) {
            int x = event.getX();
            int y = event.getY();
            isPressed = false;
            isSwiping = false;
            if (isAdjusting) {
                isAdjusting = false;
                stateChanged(null);
            }
            isPopupTrigger |= event.isPopupTrigger();
            armedFace = getFaceAt(x, y);
            updateCursor();
        }

    }

    /** Gets the swipeDelay. Integer.MAX_VALUE indicates infinite delay. */
    public int getSwipeDelay() {
        return swipeDelay;
    }

    /** Sets the swipeDelay. Integer.MAX_VALUE indicates infinite delay. */
    public void setSwipeDelay(int newValue) {
        this.swipeDelay = newValue;
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
