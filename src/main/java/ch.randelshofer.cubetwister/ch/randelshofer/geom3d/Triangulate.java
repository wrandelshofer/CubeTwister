package ch.randelshofer.geom3d;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Encapsulates an algorithm than can triangulate a polygon.
 */
public interface Triangulate {
    /**
     * Triangulates the given polygon.
     *
     * @param polygon a polygon
     * @return a list of triangles
     */
    default List<List<Point3D>> triangulate3d(List<Point3D> polygon) {
        List<Point2D.Double> points = projectIntoPlane(polygon);
        triangulate2D(points);
        return null;
    }

    /**
     * Triangulates the given polygon.
     *
     * @param polygon a polygon
     * @return a list of triangles
     */
    List<List<Point2D.Double>> triangulate2D(List<Point2D.Double> polygon);

    /**
     * The polygon is given in 3d-coordinates!
     * Project polygon in two dimensions by projecting it on the xy, xz or yz
     * plane where the area of the polygon is maximized.
     * (T. Mˆller, E. Haines, 2002, Chapter 13.8 Ray Polygon Intersection, Page 582).
     *
     * @param points points in 3d plane
     * @return points in 2d plane
     */
    static List<Point2D.Double> projectIntoPlane(List<Point3D> points) {
        int i = getProjectionIntoPlaneAxis(points);

        List<Point2D.Double> list = new ArrayList<>();
        switch (i) {
        case 0:
            for (Point3D point : points) {
                list.add(new Point2D.Double(point.getY(), point.getZ()));
            }
            break;
        case 1:
            for (Point3D point : points) {
                list.add(new Point2D.Double(point.getX(), point.getZ()));
            }
            break;
        case 2:
            for (Point3D point : points) {
                list.add(new Point2D.Double(point.getX(), point.getY()));
            }
            break;
        }
        return list;
    }

    static int getProjectionIntoPlaneAxis(List<Point3D> points) {
        Point3D p0, p1, p2;
        p0 = points.get(0);
        p1 = points.get(1);
        p2 = points.get(2);

        Point3D normal = Point3D.cross(
                Point3D.sub(p1, p0), Point3D.sub(p2, p0));
        int i = 0;// x
        if (abs(normal.get(1)) > abs(normal.get(i))) {
            i = 1;
        }
        if (abs(normal.get(2)) > abs(normal.get(i))) {
            i = 2;
        }
        return i;
    }

    /**
     * Determine the ordering of the vertices by computing its area.
     * If the area is negative, the vertices are ordered clockwise.
     * (P. Bourke, 1998)
     */
    default boolean isClockwise(List<Point2D.Double> polygon) {
        double area = 0;
        for (int i = 0, n = polygon.size(); i < n - 1; i++) {
            Point2D.Double p0 = polygon.get(i);
            Point2D.Double p1 = polygon.get(i + 1);
            area += p0.x * p1.y - p1.x * p0.y;
        }
        return area < 0;
    }

    /**
     * Computes a cross-product of two 2d-vertices and returns the z-coordinate.
     * <pre>
     *  Kross(u, v) = Cross((u,0), (v,0)).z
     *  </pre>
     * Reference:<br>
     * (P. Schneider, D. Eberly, 2002, Chapter 13 Computational Geometry Topics, Page 771).
     *
     * @param u Vertice u, vector [x,y].
     * @param v Vertice v, vector [x,y.
     */
    static double kross(Point2D.Double u, Point2D.Double v) {
        return u.x * v.y - u.y * v.x;
    }
}
