/* @(#)Face3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import ch.randelshofer.gui.event.SwipeEvent;
import ch.randelshofer.gui.event.SwipeListener;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.event.EventListenerList;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Represents a planar and convex polygon in 3 dimensional space.
 *
 * @author Werner Randelshofer
 */
public class Face3D implements Comparable<Face3D> {

    /**
     * Coordinates used by this shape. Each group of three
     * entries in this array describe a vector x, y, z in
     * three dimensional space.
     * May contain coordinates not used by this face. This allows
     * to share coordinates by multiple faces.
     */
    private float[] coords;
    /**
     * List of the vertices describing the borderline of the face.
     * Each entry in this array is the index of the corresponding
     * vector in the coords array. The vertices of the face
     * must be indicated in clockwise direction. The faces must be
     * planar and their boundaries must be convex.
     */
    private int[] vertices;
    private double /*zMax, zMin,*/ zAvg;
    /*
    private double[] planeEquation;*/
    private Color[] colors;
    private ActionListener action;
    private EventListenerList listenerList;
    /**
     * This vector points from the plane (where the vertices of
     * the face are located) away.
     */
    private Point3D normal;

    /**
     * Creates a new Face3D object.
     *
     * @param coords    Coordinate data for the face. Each group of
     *                  three entries describe a vector x, y, z
     *                  in three dimensional space.
     * @param vertices  Each entry represents a vertex at the
     *                  borderline of the face. Each vertex is the index of a
     *                  vector in the coords array. The vertices of the face
     *                  must be indicated in clockwise direction. All vertices
     *                  must be on the same plane and the boundary of the face
     *                  must be convex.
     */
    public Face3D(float[] coords, int[] vertices, Color[] colors) {
        this.coords = coords;
        this.vertices = vertices;
        this.colors = colors;
        updateValues();
    }

    private void updateValues() {
        double z;

        /* zMax = Double.MIN_VALUE;
        zMin = Double.MAX_VALUE; */
        zAvg = 0;
        for (int i = 0; i < vertices.length; i++) {
            /*z = coords[vertices[i]*3+2];
            if (zMax < z) {
            zMax = z;
            }
            if (zMin > z) {
            zMin = z;
            }
            zAvg += z;
             */
            zAvg += coords[vertices[i] * 3 + 2];

        }
        zAvg /= (double) vertices.length;

        // compute the normal
        int p1, p2, p3;
        double vx, vy, vz;
        double wx, wy, wz;
        double px, py, pz;
        double sx, sy, sz;
        double q;

        p1 = vertices[0] * 3;
        p2 = vertices[1] * 3;
        p3 = vertices[2] * 3;
        vx = coords[p2] - coords[p1];
        vy = coords[p2 + 1] - coords[p1 + 1];
        vz = coords[p2 + 2] - coords[p1 + 2];
        wx = coords[p3] - coords[p1];
        wy = coords[p3 + 1] - coords[p1 + 1];
        wz = coords[p3 + 2] - coords[p1 + 2];

        normal = new Point3D(
                vy * wz - vz * wy,
                vz * wx - vx * wz,
                vx * wy - vy * wx);

        /*
        // compute the plane equation
        int i = vertices[0] * 3;
        planeEquation = new double[] {normal.x, normal.y, normal.z, -(normal.x*coords[i]+normal.y*coords[i+1]+normal.z*coords[i+2]) };
         */
    }

    public Color getBorderColor() {
        return colors[1];
    }

    public Color getFillColor() {
        return colors[0];
    }

    public void setFillColor(Color newValue) {
        colors[0] = newValue;
    }

    public void setAction(ActionListener action) {
        this.action = action;
    }

    public boolean handleEvent(@Nonnull MouseEvent evt) {
        if (action != null) {
//System.out.println("Face3D evt.modifiers:"+evt.getModifiers()+" ex:"+evt.getModifiersEx());
// FIXME - Find another way to pass modifiersEx to actionListener.
            ActionEvent aEvt = new ActionEvent(evt, ActionEvent.ACTION_PERFORMED, null, evt.getWhen(), evt.getModifiersEx());
            action.actionPerformed(aEvt);
        }
        return true;
    }

    public ActionListener getAction() {
        return action;
    }

    public boolean isVisible(@Nonnull Point3D observer) {
        double sx, sy, sz;
        double q;


        sx = coords[vertices[0] * 3] - observer.x;
        sy = coords[vertices[0] * 3 + 1] - observer.y;
        sz = coords[vertices[0] * 3 + 2] - observer.z;
        q = sx * normal.x + sy * normal.y + sz * normal.z;
        return q > 0;
    }

    public int[] getVertices() {
        return vertices;
    }

    public float[] getCoords() {
        return coords;
    }

    public void setCoords(float[] coords) {
        this.coords = coords;
        updateValues();
    }

    /**
     * Returns ambient light intensity + the intensity of this
     * face's diffuse reflection to the light source.
     * If the light source is behind the face, the ambient
     * light intensity is returned.
     * FIXME: The brightness must be determined before a
     * perspective distortion is applied to the face.
     */
    public double getBrightness(@Nonnull Point3D light, double lightSourceIntensity, double ambientLightIntensity) {
        getNormal();

        // Vector from face to lightsource
        double lx = light.x - normal.x;
        double ly = light.y - normal.y;
        double lz = light.z - normal.z;

        // Intensity of the light
        double cosa =
                (normal.x * lx + normal.y * ly + normal.z * lz)
                / Math.sqrt(
                (normal.x * normal.x + normal.y * normal.y + normal.z * normal.z)
                * (lx * lx + ly * ly + lz * lz));

        if (cosa < 0) {
            // lightsource shines on face
            return ambientLightIntensity - cosa * lightSourceIntensity;
        } else {
            // face does not face the lightsource
            return ambientLightIntensity;
        }

    }
    /*
    public double getZmin() {
    return zMin;
    }
    public double getZmax() {
    return zMax;
    }
     */

    public int compareTo(@Nonnull Face3D that) {
        double c;
        /*
        // Quick comparison for faces that do not
        // overlap on z-axis.
        if (this.zMin >= that.zMax) {
        return 1;
        }
        if (this.zMax <= that.zMin) {
        return -1;
        }

        // Semi-Quick comparison for faces that do
        // not overlap on x- and y- axis.
        RectangleFloat thatbounds = that.getBoundsXY();
        RectangleFloat thisbounds = this.getBoundsXY();
        if (! thisbounds.intersects(thatbounds)) {
        c = this.zAvg - that.zAvg;
        return (c > 0) ? 1 : ((c < 0) ? -1 : 0);
        }

        /* XXX not working.
        If all vertices of that face are behind
        the plane on which all the vertices of this
        face lie, then this face is closer to
        the observer.
         * /
        that.getNormal();
        float fx, fy,fz;
        float px = that.coords[that.vertices[0]*3];
        float py = that.coords[that.vertices[0]*3+1];
        float pz = that.coords[that.vertices[0]*3+2];
        double q;
        int i;
        for (i=0; i < vertices.length; i++) {
        fx = coords[vertices[i]*3] - px;
        fy = coords[vertices[i]*3+1] - py;
        fz = coords[vertices[i]*3+2] - pz;
        q = fx * that.normal.x + fy * that.normal.y + fz * that.normal.z;
        if (q < 0) {
        return 1;
        }
        }
         */

        c = this.zAvg - that.zAvg;
        return (c > 0) ? 1 : ((c < 0) ? -1 : 0);
        /*
        c = this.zMin - that.zMin;
        return (c > 0) ? 1 : ((c < 0) ? -1 : 0);
         */
    }

    /**
     * Algorithm stolen from java.awt.Polygon.
     *
     * Tests if the specified coordinates are inside the boundary of the
     * <code>Shape</code>.
     * @param x,&nbsp;y the specified coordinates
     * @return <code>true</code> if the <code>Shape</code> contains the
     * specified coordinates; <code>false</code> otherwise.
     * /
    public boolean containsXY(double x, double y) {
    if (getBoundsXY().contains((float)x, (float)y)) {
    int hits = 0;
    double ySave = 0;

    // Find a vertex that is not on the halfline
    int i = 0;
    while (i < vertices.length && coords[vertices[i] * 3 + 1] == y) {
    i++;
    }

    // Walk the edges of the polygon
    for (int n = 0; n < vertices.length; n++) {
    int j = (i + 1) % vertices.length;

    double dx = coords[vertices[j] * 3] - coords[vertices[i] * 3];
    double dy = coords[vertices[j] * 3 + 1] - coords[vertices[i] * 3 + 1];

    // Ignore horizontal edges completely
    if (dy != 0) {
    // Check to see if the edge intersects
    // the horizontal halfline through (x, y)
    double rx = x - coords[vertices[i] * 3];
    double ry = y - coords[vertices[i] * 3 + 1];

    // Deal with edges starting or ending on the halfline
    if (coords[vertices[j] * 3 + 1] == y && coords[vertices[j] * 3] >= x) {
    ySave = coords[vertices[i] * 3 + 1];
    }
    if (coords[vertices[i] * 3 + 1] == y && coords[vertices[i] * 3] >= x) {
    if ((ySave > y) != (coords[vertices[j] * 3 + 1] > y)) {
    hits--;
    }
    }

    // Tally intersections with halfline
    double s = ry / dy;
    if (s >= 0.0 && s <= 1.0 && (s * dx) >= rx) {
    hits++;
    }
    }
    i = j;
    }

    // Inside if number of intersections odd
    return (hits % 2) != 0;
    }
    return false;
    }

    /**
     * Algorithm stolen from java.awt.Polygon.
     *
     * Gets the 2D bounding box of this <code>Face</code>.
     * The bounding box is the smallest {@link Rectangle} whose
     * sides are parallel to the x and y axes of the
     * coordinate space, and can completely contain the <code>Polygon</code>.
     * @return a <code>Rectangle</code> that defines the boundsXY of this
     * <code>Polygon</code>.
     * @since       JDK1.1
     * /
    public RectangleFloat getBoundsXY() {
    if (boundsXY == null) {
    //void calculateBounds(int xpoints[], int ypoints[], int npoints) {
    float boundsMinX = Float.MAX_VALUE;
    float boundsMinY = Float.MAX_VALUE;
    float boundsMaxX = Float.MIN_VALUE;
    float boundsMaxY = Float.MIN_VALUE;

    for (int i = 0; i < vertices.length; i++) {
    int x = (int) coords[vertices[i] * 3];
    boundsMinX = Math.min(boundsMinX, x);
    boundsMaxX = Math.max(boundsMaxX, x);
    int y = (int) coords[vertices[i] * 3 + 1];
    boundsMinY = Math.min(boundsMinY, y);
    boundsMaxY = Math.max(boundsMaxY, y);
    }
    boundsXY = new RectangleFloat(boundsMinX, boundsMinY,
    boundsMaxX - boundsMinX,
    boundsMaxY - boundsMinY);
    }
    return boundsXY;
    }
     */
    private Point3D getNormal() {
        return normal;
    }

  
    public void addSwipeListener(SwipeListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(SwipeListener.class, l);
    }

    public void removeSwipeListener(SwipeListener l) {
        if (listenerList != null) {
            listenerList.remove(SwipeListener.class, l);
        }
    }

    public SwipeListener[] getSwipeListeners() {
        if (listenerList != null) {
            return listenerList.getListeners(SwipeListener.class);
        }
        return new SwipeListener[0];
    }

    public int getSwipeListenerCount() {
        if (listenerList != null) {
            return listenerList.getListenerCount(SwipeListener.class);
        }
        return 0;
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    protected void fireSwipeEvent(@Nonnull MouseEvent evt, float angle) {
        if (listenerList != null) {
            SwipeEvent swipeEvent = null;
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == SwipeListener.class) {
                    // Lazily create the event:
                    if (swipeEvent == null) {
                        swipeEvent = new SwipeEvent(evt, angle);
                    }
                    ((SwipeListener) listeners[i + 1]).faceSwiped(swipeEvent);
                }
            }
        }
    }
}
