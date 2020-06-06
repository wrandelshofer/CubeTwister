/*
 * @(#)ConvexHull.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package ch.randelshofer.geom3d;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.randelshofer.geom3d.Geom2D.dot;
import static ch.randelshofer.geom3d.Geom2D.normalize;
import static ch.randelshofer.geom3d.Geom2D.perp;
import static ch.randelshofer.geom3d.Geom2D.subtract;

/**
 * Provides utility methods for computing the convex hull from a set of points.
 *
 * @author Werner Randelshofer
 */
public class ConvexHull {
    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    public static List<Point2D.Double> getConvexHull2D(List<Point2D.Double> points) {
        return Arrays.asList(getConvexHull2D(points.toArray(new Point2D.Double[0])));
    }

    public static List<Integer> getConvexHull2D(List<Point2D.Double> vlist, List<Integer> ilist) {
        // Quickly return if no work is needed
        if (ilist.size() < 3) {
            return new ArrayList<>(ilist);
        }

        // Sort points from left to right O(n log n)
        List<Integer> sorted = new ArrayList<>(ilist);
        sorted.sort((i1, i2) -> {
            Point2D.Double o1 = vlist.get((int) i1);
            Point2D.Double o2 = vlist.get((int) i2);
            int cmp = Double.compare(o1.x, o2.x);
            return (cmp == 0) ? Double.compare(o1.y, o2.y) : cmp;
        });

        int n = sorted.size();
        int[] hull = new int[n + 2];

        // Process upper part of convex hull O(n)
        int upper = 0; // Number of points in upper part of convex hull
        hull[upper++] = sorted.get(0);
        hull[upper++] = sorted.get(1);
        for (int i = 2; i < n; i++) {
            hull[upper++] = sorted.get(i);
            while (upper > 2 && !isRightTurn2D(vlist, hull[upper - 3], hull[upper - 2], hull[upper - 1])) {
                hull[upper - 2] = hull[upper - 1];
                upper--;
            }
        }

        // Process lower part of convex hull O(n)
        int lower = upper; // (lower - number + 1) = number of points in the lower part of the convex hull
        hull[lower++] = sorted.get(n - 2);
        for (int i = n - 3; i >= 0; i--) {
            hull[lower++] = sorted.get(i);
            while (lower - upper > 1 && !isRightTurn2D(vlist, hull[lower - 3], hull[lower - 2], hull[lower - 1])) {
                hull[lower - 2] = hull[lower - 1];
                lower--;
            }
        }
        lower -= 1;

        // Reduce array
        ArrayList<Integer> convexHull = new ArrayList<>(lower);
        for (int i = 0; i < lower; i++) {
            convexHull.add(hull[i]);
        }
        return convexHull;
    }

    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    public static Point2D.Double[] getConvexHull2D(Point2D.Double[] points) {
        // Quickly return if no work is needed
        if (points.length < 3) {
            return points.clone();
        }

        // Sort points from left to right O(n log n)
        Point2D.Double[] sorted = points.clone();
        Arrays.sort(sorted, (o1, o2) -> {
            int cmp = Double.compare(o1.x, o2.x);
            return (cmp == 0) ? Double.compare(o1.y, o2.y) : cmp;
        });

        int n = sorted.length;
        Point2D.Double[] hull = new Point2D.Double[n + 2];

        // Process upper part of convex hull O(n)
        int upper = 0; // Number of points in upper part of convex hull
        hull[upper++] = sorted[0];
        hull[upper++] = sorted[1];
        for (int i = 2; i < n; i++) {
            hull[upper++] = sorted[i];
            while (upper > 2 && !isRightTurn2D(hull[upper - 3], hull[upper - 2], hull[upper - 1])) {
                hull[upper - 2] = hull[upper - 1];
                upper--;
            }
        }

        // Process lower part of convex hull O(n)
        int lower = upper; // (lower - number + 1) = number of points in the lower part of the convex hull
        hull[lower++] = sorted[n - 2];
        for (int i = n - 3; i >= 0; i--) {
            hull[lower++] = sorted[i];
            while (lower - upper > 1 && !isRightTurn2D(hull[lower - 3], hull[lower - 2], hull[lower - 1])) {
                hull[lower - 2] = hull[lower - 1];
                lower--;
            }
        }
        lower -= 1;

        // Reduce array
        Point2D.Double[] convexHull = new Point2D.Double[lower];
        System.arraycopy(hull, 0, convexHull, 0, lower);
        return convexHull;
    }

    /**
     * Returns true, if the three given points make a right turn.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if right turn.
     */
    public static boolean isRightTurn2D(List<Point.Double> vlist, int p1, int p2, int p3) {
        return isRightTurn2D(vlist.get(p1), vlist.get(p2), vlist.get(p3));
    }

    /**
     * Returns true, if the three given points make a right turn.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if right turn.
     */
    public static boolean isRightTurn2D(Point.Double p1, Point.Double p2, Point.Double p3) {
        if (p1.equals(p2) || p2.equals(p3)) {
            // no right turn if points are at same location
            return false;
        }

        double val = (p2.x * p3.y + p1.x * p2.y + p3.x * p1.y) - (p2.x * p1.y + p3.x * p2.y + p1.x * p3.y);
        return val > 0;
    }

    public static List<Point2D.Double> getMinimumAreaRectangle(List<Point2D.Double> poly) {
        int n = poly.size();
        List<Integer> ilist = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ilist.add(i);
        }
        return getMinimumAreaRectangle(poly, ilist);
    }

    /**
     * Gets the minimum-area-rectangle (MAR) fitted on the given points.
     * <p>
     * <ol>
     *     <li>Compute the convex hull of the polygpon.</li>
     *     <li>The MAR has the same orientation as one of the edges of the convex hull.</li>
     * </ol>
     * <p>
     * References: <a href="https://gis.stackexchange.com/questions/22895/finding-minimum-area-rectangle-for-given-points">stackoverflow</a>
     */
    public static List<Point2D.Double> getMinimumAreaRectangle(List<Point2D.Double> vlist, List<Integer> ilist) {
        int n = ilist.size();
        List<Integer> a = getConvexHull2D(vlist, ilist); // indices of extremal points
        double minArea = Double.POSITIVE_INFINITY;
        Point2D.Double r0 = null;
        Point2D.Double r1 = null;
        Point2D.Double r2 = null;
        Point2D.Double r3 = null;
        for (int i = 0; i < n; i++) {
            Point2D.Double dir = // direction of edge
                    normalize(subtract(vlist.get(ilist.get((i + 1) % n)), vlist.get(ilist.get((i)))));
            Point2D.Double perp = perp(dir);// unit normal direction of edge

            double minx = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            double maxy = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                Point2D.Double p = vlist.get(j);
                double x = dot(dir, p);
                double y = dot(perp, p);
                minx = Math.min(x, minx);
                miny = Math.min(y, miny);
                maxx = Math.max(x, maxx);
                maxy = Math.max(y, maxy);
            }
            double w = maxx - minx;
            double h = maxy - miny;
            double area = w * h;
            if (area < minArea) {
                minArea = area;
                r0 = new Point2D.Double(minx * dir.x + miny * perp.x, minx * dir.y + miny * perp.y);
                r1 = new Point2D.Double(maxx * dir.x + miny * perp.x, maxx * dir.y + miny * perp.y);
                r2 = new Point2D.Double(maxx * dir.x + maxy * perp.x, maxx * dir.y + maxy * perp.y);
                r3 = new Point2D.Double(minx * dir.x + maxy * perp.x, minx * dir.y + maxy * perp.y);
            }
        }
        return List.of(r0, r1, r2, r3);
    }
}
