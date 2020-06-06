package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clips convex parts out of a reflex face until only convex faces are left.
 */
public class ConvexClipping {

    public List<List<Integer>> clipConvexIndices(List<Point2D.Double> poly) {
        int n = poly.size();
        List<Integer> ilist = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ilist.add(i);
        }
        return clipConvexIndices(poly, ilist);
    }

    private List<List<Integer>> clipConvexIndices(List<Point2D.Double> vlist, List<Integer> ilist) {
        List<List<Integer>> convexPolys = new ArrayList<>();
        boolean clockwise = EarClipping.isClockwise(vlist, ilist);
        clipConvexIndices(vlist, ilist, convexPolys, clockwise);
        return convexPolys;
    }

    private static class Diagonal {
        private final int i0;
        private final int i1;

        public Diagonal(int i0, int i1) {
            this.i0 = i0;
            this.i1 = i1;
        }
    }

    private void clipConvexIndices(List<Point2D.Double> vlist, List<Integer> ilist, List<List<Integer>> convexPolys, boolean clockwise) {
        List<Integer> convexHullList = ConvexHull.getConvexHull2D(vlist, ilist);
        int n = ilist.size();

        // Recursion base: Face is convex.
        // -------
        if (convexHullList.size() == n) {
            convexPolys.add(convexHullList);
            return;
        }

        // Recursion step: Face is reflex.
        // -------

        // Find point i0 that is not on convex hull
        Diagonal diagonal = findShortestDiagonal(vlist, ilist, clockwise, convexHullList);
        int i0 = diagonal.i0;
        int i1 = diagonal.i1;

        // Split face at diagonal from i0 to i1
        ArrayList<Integer> a;
        ArrayList<Integer> b;
        if (i0 < i1) {
            //    0-------1=i0
            //    | A   / |
            //    |   /   |
            //    | /  B  |
            // i1=3-------2
            a = new ArrayList<>(i0 + 1 + n - i1 + 1);
            a.addAll(ilist.subList(0, i0 + 1));
            a.addAll(ilist.subList(i1, n));
            b = new ArrayList<>(ilist.subList(i0, i1 + 1));
        } else {
            //    0-------1=i1
            //    | A   / |
            //    |   /   |
            //    | /  B  |
            // i0=3-------2
            a = new ArrayList<>(i0 + 1 + n - i1 + 1);
            a.addAll(ilist.subList(0, i1 + 1));
            a.addAll(ilist.subList(i0, n));
            b = new ArrayList<>(ilist.subList(i1, i0 + 1));
        }

        clipConvexIndices(vlist, a, convexPolys, clockwise);
        clipConvexIndices(vlist, b, convexPolys, clockwise);
    }

    @Nonnull
    private ConvexClipping.Diagonal findAnyDiagonal(List<Point2D.Double> vlist, List<Integer> ilist, boolean clockwise, List<Integer> convexHullList) {
        int n = ilist.size();
        Set<Integer> convexHull = new HashSet<>(convexHullList);
        int i0 = -1;
        for (int i = 0; i < n; i++) {
            if (!convexHull.contains(ilist.get(i))) {
                i0 = i;
                break;
            }
        }
        assert i0 != -1;

        // Find diagonal from i0 to another point i1
        int iM = (i0 + n - 1) % n;
        int iP = (i0 + 1) % n;
        int i1 = -1;
        for (int i = 0; i < n; i++) {
            if (i != i0 && i != iM && i != iP
                    && EarClipping.isDiagonal(vlist, ilist, i0, i, clockwise)) {
                i1 = i;
                break;
            }
        }
        return new Diagonal(i0, i1);
    }

    @Nonnull
    private ConvexClipping.Diagonal findShortestDiagonal(List<Point2D.Double> vlist, List<Integer> ilist, boolean clockwise, List<Integer> convexHullList) {
        int n = ilist.size();
        Set<Integer> convexHull = new HashSet<>(convexHullList);
        int shortest0 = -1;
        int shortest1 = -1;
        double shortestDist = Double.POSITIVE_INFINITY;
        for (int i0 = 0; i0 < n; i0++) {
            if (!convexHull.contains(ilist.get(i0))) {

                // Find diagonal from i0 to another point i1
                int iM = (i0 + n - 1) % n;
                int iP = (i0 + 1) % n;
                for (int i1 = 0; i1 < n; i1++) {
                    if (i1 != i0 && i1 != iM && i1 != iP
                            && EarClipping.isDiagonal(vlist, ilist, i0, i1, clockwise)) {
                        double dist = vlist.get(i0).distanceSq(vlist.get(i1));
                        if (dist < shortestDist) {
                            shortest0 = i0;
                            shortest1 = i1;
                            shortestDist = dist;
                        }
                    }
                }
            }
        }
        return new Diagonal(shortest0, shortest1);
    }

}
