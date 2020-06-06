package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ConvexClippingTest {


    @TestFactory
    public List<DynamicTest> testFactoryConvexClip() {
        return Arrays.asList(
                dynamicTest("[(-shape", () -> testConvexClip(points(
                        0, 40,
                        30, 40,
                        15, 30,
                        10, 20,
                        15, 10,
                        30, 0,
                        0, 0
                ), 5)),
                dynamicTest(")]-shape", () -> testConvexClip(points(
                        0, 40,
                        -30, 40,
                        -15, 30,
                        -10, 20,
                        -15, 10,
                        -30, 0,
                        0, 0
                ), 5)),
                dynamicTest("rough circle segment", () -> testConvexClip(points(
                        0, -2.9, 0.0, 0.0, 0.0, 0.1, 0.9, 3.4, 1.5, 4.7, 2.6, 6.6, 3.6, 7.9, 4.2, 8.5, 5.4, 6.9, 5.0, 6.5, 4.3, 5.6, 3.2, 3.7, 2.8, 2.8, 2.1, 0.3, 2.0, -0.1, 1.9, -0.4, 1.9, -2.8, 2.2, -5.1, 2.3, -5.7, 2.6, -6.5, 3.4, -8.4, 3.9, -9.3, 2.4, -10.6, 1.6, -9.3, 0.5, -6.7, 0.4, -6.2, 0.3, -5.9), 17)),
                dynamicTest("circle segment", () -> testConvexClip(points(
                        -2.220446049250313E-16, -2.9846621175575287, 0.0, 0.0, 0.024690245782453615, 0.18503420782622826, 0.9679706904715062, 3.4574659139232162, 1.56472218727641, 4.745725812177891, 2.647507966876407, 6.637931559210909, 3.6211109027901647, 7.904849150045861, 4.210593832761793, 8.52794220841832, 5.49910973865815, 6.979845806154759, 5.062573357927392, 6.518419778387071, 4.358270443424841, 5.60193299504199, 3.2754842369839574, 3.709728133528591, 2.864494488612161, 2.8224877777578325, 2.1591664100714625, 0.3755613579599295, 2.0247796423162434, -0.1571351807758562, 1.9938868571611172, -0.47427578581231034, 1.993887959925635, -2.8636361995447186, 2.2591647259997765, -5.110384990961289, 2.3808917641349443, -5.741392401253066, 2.6489082159722432, -6.539492998542276, 3.4714667279372393, -8.476823720615497, 3.9928699027784673, -9.316462988436697, 2.445301258403071, -10.60453962941882, 1.6970601560720544, -9.399612586170054, 0.5515839643773632, -6.701728858394809, 0.41490728532632826, -6.294731928580962, 0.35283128294694643, -5.972945904327868), 16)),
                dynamicTest("concave clockwise", () -> testConvexClip(points(0, 0, 0, 20, 20, 20, 8, 10), 2)),
                dynamicTest("concave counter-clockwise", () -> testConvexClip(points(8, 10, 20, 20, 0, 20, 0, 0), 2)),
                dynamicTest("flag off diagonal", () -> testConvexClip(points(8, 10, 20, 20, 0, 20, 0, 0, 20, 0), 3)),
                dynamicTest("flag on diagonal 3", () -> testConvexClip(points(10, 10, 20, 20, 0, 20, 0, 0, 20, 0), 3)),
                dynamicTest("flag on diagonal 1", () -> testConvexClip(points(0, 0, 20, 0, 10, 10, 20, 20, 0, 20), 3)),
                dynamicTest("flag on diagonal 2", () -> testConvexClip(points(20, 0, 10, 10, 20, 20, 0, 20, 0, 0), 3)),
                dynamicTest("square clockwise", () -> testConvexClip(points(0, 0, 0, 10, 10, 10, 10, 0), 1)),
                dynamicTest("square counter-clockwise", () -> testConvexClip(points(0, 0, 10, 0, 10, 10, 0, 10), 1))

        );
    }


    private static class Edge {
        private final Point2D.Double a;
        private final Point2D.Double b;

        private Edge(Point2D.Double a, Point2D.Double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Edge that = (Edge) o;
            return a.equals(that.a) &&
                    b.equals(that.b) ||
                    a.equals(that.b) &&
                            b.equals(that.a);
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }
    }

    void testConvexClip(List<Point2D.Double> p, int expected) throws Exception {
        System.err.println("test:" + p);

        List<List<Integer>> convexPolys = new ConvexClipping().clipConvexIndices(Collections.unmodifiableList(p));

        Path2D.Double path = new Path2D.Double();
        Set<Edge> edges = new LinkedHashSet<>();
        for (int i = 0, n = p.size(); i < n; i++) {
            Point2D.Double a = p.get(i);
            Point2D.Double b = p.get((i + 1) % n);
            edges.add(new Edge(a, b));
            if (path.getCurrentPoint() == null) {
                path.moveTo(a.getX(), a.getY());
            } else {
                path.lineTo(a.getX(), a.getY());
            }
        }
        path.closePath();


        String buf = toSvgPoly(p, convexPolys);
        System.out.println(convexPolys);
        System.out.println(buf);
        assertEquals(expected, convexPolys.size());


        // All diagonals must be contained in the path (we just check the midpoints)
        for (List<Integer> poly : convexPolys) {
            for (int i = 0, n = poly.size(); i < n; i++) {
                int j = (i + 1) % n;
                Point2D.Double a = p.get(poly.get(i));
                Point2D.Double b = p.get(poly.get(j));
                if (!edges.contains(new Edge(a, b))) {
                    Point2D.Double mid = new Point2D.Double(0.5 * (a.getX() + b.getX()), 0.5 * (a.getY() + b.getY()));
                    assertTrue(path.contains(mid), mid + " not contained in path, diagonal:" + a + " " + b + "\ntriangle:" + poly);
                }
            }
        }


    }

    @Nonnull
    public static String toSvgPoly(List<Point2D.Double> vlist, List<List<Integer>> ilist) {
        StringBuilder buf = new StringBuilder();
        for (List<Integer> poly : ilist) {
            buf.append("M");
            for (int index : poly) {
                buf.append(vlist.get(index).getX());
                buf.append(",");
                buf.append(vlist.get(index).getY());
                buf.append(" ");
            }
            buf.append('Z');
        }
        return buf.toString();
    }

    private List<Point2D.Double> points(double... c) {
        List<Point2D.Double> p = new ArrayList<>();
        for (int i = 0; i < c.length; i += 2) {
            p.add(new Point2D.Double(c[i], c[i + 1]));
        }
        return p;
    }
}