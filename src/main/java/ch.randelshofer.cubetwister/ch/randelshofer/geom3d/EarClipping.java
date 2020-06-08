/*
 * @(#)EarClipping.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.randelshofer.geom3d.Geom2D.subtract;
import static java.lang.Math.abs;

/**
 * Triangulation
 * <p>
 * References:<br>
 * P. Schneider, D. Eberly. (2002). Geometric Tools for Computer Graphics. Morgan Kaufmann.<br>
 * T. Mˆller, E. Haines (2002) Real Time Rendering, 2nd Edition. Natick: AK Peters.<br>
 * P. Bourke (1998). Determining Whether Or Not A Polygon (2D) Has Its Vertices Ordered Clockwise Or Counterclockwise. http://local.wasp.uwa.edu.au/~pbourke/geometry/clockwise/index.html<br>
 * W. D. Scott, C. C. Joyce, E. R. Magnus (1982). Optimization of a triangle net. Perth: Murdoch University.<br>
 */
public class EarClipping {
    /**
     * This is a simple O(n^3) implementation.
     *
     * @param vlist list of vertices
     * @return list of polygons
     */
    public List<List<Integer>> triangulateIndices(List<Point2D.Double> vlist) {
        int n = vlist.size();
        List<Integer> ilist = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ilist.add(i);
        }
        return triangulate(vlist, ilist);
    }


    /**
     * This is a simple O(n^3) implementation.
     *
     * @param poly list of vertices
     * @return list of polygons
     */
    public List<List<Point2D.Double>> triangulate(List<Point2D.Double> poly) {
        List<List<Integer>> lists = triangulateIndices(poly);
        List<List<Point2D.Double>> triangles = new ArrayList<>();
        for (List<Integer> list : lists) {
            List<Point2D.Double> face = new ArrayList<>();
            for (int index : list) {
                face.add(poly.get(index));
            }
            triangles.add(face);
        }
        return triangles;
    }

    /**
     * This is a simple O(n^3) implementation of ear clipping.
     *
     * @param vs list of vertices
     * @param fs face indices into vertex list
     * @return list of triangles
     */
    public List<List<Integer>> triangulate(List<Point2D.Double> vs, List<Integer> fs) {
        boolean clockwise = isClockwise(vs, fs);

        List<List<Integer>> triangles = new ArrayList<>();
        List<Integer> gs = new ArrayList<>(fs);// Mutable face indices gs
        int n = gs.size();
        int i0 = 0;
        while (n > 3) { // cut off ears until we only have one triangle left
            int i1 = (i0 + 1) % n, i2 = (i0 + 2) % n;
            if (isDiagonal(vs, gs, i0, i2, clockwise)) {
                triangles.add(Arrays.asList(gs.get(i0), gs.get(i1), gs.get(i2)));
                gs.remove(i1);
                n--;
            }
            i0 = (i0 + 1) % n;
        }
        if (n == 3) {
            triangles.add(Arrays.asList(gs.get(0), gs.get(1), gs.get(2)));
        }

        return triangles;
    }

    private boolean isCollinear(List<Point2D.Double> vlist, List<Integer> flist, int i0, int i1, int i2) {
        return isCollinear(vlist.get(flist.get(i0)), vlist.get(flist.get(i1)), vlist.get(flist.get(i2)));
    }


    /**
     * Checks  if vs.get(fs.get(a)) to vs.get(fs.get(b)) is a diagonal.
     *
     * @param vs        vertex list
     * @param fs        index list
     * @param a         index of diagonal in fs
     * @param b         index of diagonal in fs
     * @param clockwise whether the vertex indices are given in clockwise
     *                  or counter-clockwise sequence
     * @return true if vs.get(fs.get(a)) to vs.get(fs.get(b)) is a diagonal.
     */
    public static boolean isDiagonal(List<Point2D.Double> vs, List<Integer> fs, int a, int b, boolean clockwise) {
        int n = fs.size();
        // Segment may be a diagonal or may be external to the polygon. Need
        // to distinguish between the two. The first two arguments of
        // segmentInCone are the line segment. The first and last two arguments
        // from the cone.
        int aM = (a - 1 + n) % n;
        int aP = (a + 1) % n;
        Point2D.Double va = vs.get(fs.get(a));
        Point2D.Double vb = vs.get(fs.get(b));

        if (isCollinear(vs.get(fs.get(aM)), va, vs.get(fs.get(aP)))) {
            // Flip the diagonal if aM,a,aP is collinear - in the hope that
            //                      bM,b,bP is non-collinear
            Point2D.Double vswap = va;
            va = vb;
            vb = vswap;
            int aswap = a;
            a = b;
            b = aswap;
            aM = (a - 1 + n) % n;
            aP = (a + 1) % n;
        }

        if (!segmentInCone(va, vb,
                vs.get(fs.get(aM)), vs.get(fs.get(aP)), clockwise)) {
            return false;
        }

        // test segment <vs[is[a]], vs[is[b]]> to see if it is a diagonal
        for (int c = 0; c < n; c++) {
            int d = (c + 1) % n;
            if (c != a && c != b && d != a && d != b) {
                // The first two arguments of SegmentsIntersect form a line
                // segment. The last two arguments form an edge to be tested
                // for intersection with the segment.
                if (segmentsIntersect(va, vb,
                        vs.get(fs.get(c)), vs.get(fs.get(d)))) {
                    return false;
                }
            }
        }
        return true;
    }

    static double kross(Point2D.Double u, Point2D.Double v) {
        // Kross(U, V) = Cross((U,O), (V,O)).z
        return u.getX() * v.getY() - u.getY() * v.getX();
    }

    static boolean segmentInCone(Point2D.Double v0, Point2D.Double v1, Point2D.Double vM, Point2D.Double vP, boolean clockwise) {
        // assert" VM, VO, VP are not col linear
        if (isCollinear(vM, v0, vP)) {
            return false;
        }

        Point2D.Double diff = subtract(v1, v0),
                edgeL = subtract(clockwise ? vP : vM, v0),
                edgeR = subtract(clockwise ? vM : vP, v0);
        double kross = kross(edgeR, edgeL);
        if (kross > 0) {
            // vertex is convex
            return kross(diff, edgeR) < 0 && kross(diff, edgeL) > 0;
        } else {
            // vertex is reflex
            return kross(diff, edgeR) < 0 || kross(diff, edgeL) > 0;
        }
    }

    /**
     * Returns true if two line segments intersect.
     *
     * @param a Vertice 1 of the first line.
     * @param b Vertice 2 of the first line.
     * @param c Vertice 1 of the second line.
     * @param d Vertice 2 of the second line.
     */
    static private boolean segmentsIntersect(Point2D.Double a, Point2D.Double b, Point2D.Double c, Point2D.Double d) {
        double ax = a.getX(), ay = a.getY(),
                bx = b.getX(), by = b.getY(),
                cx = c.getX(), cy = c.getY(),
                dx = d.getX(), dy = d.getY();

        double cdnum = (dx - cx) * (ay - cy) - (dy - cy) * (ax - cx);//=kross(subtract(d,c),subtract(a,c))
        double abnum = (bx - ax) * (ay - cy) - (by - ay) * (ax - cx);//=kross(subtract(b,a),subtract(a,c));
        double denom = (dy - cy) * (bx - ax) - (dx - cx) * (by - ay);//=kross(subtract(b,a),subtract(d,c))

        if (denom != 0) {
            double t = abnum / denom;
            double u = cdnum / denom;
            return 0 <= u && u <= 1 && 0 <= t && t <= 1;
        }
        return false;
    }

    /**
     * Determine the ordering of the vertices by computing its area.
     * If the area is negative, the vertices are ordered clockwise.
     * (P. Bourke, 1998)
     */
    public boolean isClockwise(List<Point2D.Double> polygon) {
        double area = 0;
        for (int i = 0, n = polygon.size(); i < n; i++) {
            Point2D.Double p0 = polygon.get(i);
            Point2D.Double p1 = polygon.get((i + 1) % n);
            area += p0.x * p1.y - p1.x * p0.y;
        }
        return area < 0;
    }

    /**
     * Determine the ordering of the vertices by computing its area.
     * If the area is negative, the vertices are ordered clockwise.
     * (P. Bourke, 1998)
     */
    public static boolean isClockwise(List<Point2D.Double> vertices, List<Integer> indices) {
        double area = 0;
        for (int i = 0, n = indices.size(); i < n; i++) {
            Point2D.Double p0 = vertices.get(indices.get(i));
            Point2D.Double p1 = vertices.get(indices.get((i + 1) % n));
            area += p0.x * p1.y - p1.x * p0.y;
        }
        return area < 0;
    }

    /**
     * Returns true if the three points are collinear.
     *
     * @param p0 a point
     * @param p1 a point
     * @param p2 a point
     * @return true if the area is 0
     */
    public static boolean isCollinear(Point2D p0, Point2D p1, Point2D p2) {
        return isCollinear(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Returns true if the three points are collinear.
     *
     * @param a x-coordinate of point 0
     * @param b y-coordinate of point 0
     * @param m x-coordinate of point 1
     * @param n y-coordinate of point 1
     * @param x x-coordinate of point 2
     * @param y y-coordinate of point 2
     * @return true if collinear
     */
    public static boolean isCollinear(double a, double b, double m, double n, double x, double y) {
        return abs(a * (n - y) + m * (y - b) + x * (b - n)) < 1e-6;
    }
}