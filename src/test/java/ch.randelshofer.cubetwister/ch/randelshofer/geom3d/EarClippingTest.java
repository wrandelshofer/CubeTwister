/*
 * @(#)EarClippingTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

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

class EarClippingTest {
    @TestFactory
    public List<DynamicTest> testFactoryClockwise() {
        return Arrays.asList(
                dynamicTest("counter-clockwise V 1", () -> testClockwise(points(1, 0, 2, 1, 0, 1), false)),
                dynamicTest("counter-clockwise V 2", () -> testClockwise(points(2, 1, 0, 1, 1, 0), false)),
                dynamicTest("counter-clockwise V 3", () -> testClockwise(points(0, 1, 1, 0, 2, 1), false)),
                dynamicTest("clockwise V 1", () -> testClockwise(points(1, 0, 0, 1, 2, 1), true)),
                dynamicTest("clockwise V 2", () -> testClockwise(points(0, 1, 2, 1, 1, 0), true)),
                dynamicTest("clockwise V 3", () -> testClockwise(points(2, 1, 1, 0, 0, 1), true))
        );
    }

    public void testClockwise(List<Point2D.Double> poly, boolean expected) {
        boolean actual = new EarClipping().isClockwise(poly);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> testFactoryCone() {
        return Arrays.asList(
                dynamicTest("top-right inside counter-clockwise convex <<90° L", () -> testCone(0, 0, /**/10, 10, /**/5, 10, /**/10, 5, false, true)),
                dynamicTest("top-right inside counter-clockwise convex <90° L", () -> testCone(0, 0, /**/10, 10, /**/1, 10, /**/10, 1, false, true)),
                dynamicTest("top-right inside counter-clockwise convex 90° L", () -> testCone(0, 0, /**/10, 10, /**/0, 10, /**/10, 0, false, true)),
                dynamicTest("top inside clockwise reflex W", () -> testCone(10, 5, /**/10, 10, /**/20, 0, /**/0, 0, true, true)),
                dynamicTest("top inside counter-clockwise reflex W", () -> testCone(10, 5, /**/10, 10, /**/0, 0, /**/20, 0, false, true)),
                dynamicTest("bottom outside clockwise reflex W", () -> testCone(10, 5, /**/10, -10, /**/20, 0, /**/0, 0, true, false)),
                dynamicTest("bottom outside counter-clockwise reflex W", () -> testCone(10, 5, /**/10, -10, /**/0, 0, /**/20, 0, false, false)),
                dynamicTest("left inside clockwise reflex W", () -> testCone(10, 5,/**/ 0.1, 10,/**/ 20, 0,/**/ 0, 0, true, true)),
                dynamicTest("left inside counter-clockwise reflex W", () -> testCone(10, 5,/**/ 0.1, 10,/**/ 0, 0,/**/20, 0, false, true)),
                dynamicTest("left outside clockwise reflex W", () -> testCone(10, 5,/**/ 0.1, 0,/**/ 20, 0,/**/ 0, 0, true, false)),
                dynamicTest("left outside counter-clockwise reflex W", () -> testCone(10, 5,/**/ 0.1, 0,/**/ 0, 0,/**/20, 0, false, false)),
                dynamicTest("left inside clockwise convex V", () -> testCone(10, 0,/**/0.1, 10,/**/20, 10,/**/0, 10, true, true)),
                dynamicTest("left inside counter-clockwise convex V", () -> testCone(10, 0, /**/0.1, 10, /**/0, 10, /**/20, 10, false, true)),
                dynamicTest("left outside clockwise convex V", () -> testCone(10, 0,/**/-0.1, 10,/**/20, 10,/**/0, 10, true, false)),
                dynamicTest("left outside counter-clockwise convex V", () -> testCone(10, 0, /**/-0.1, 10, /**/0, 10, /**/20, 10, false, false)),
                dynamicTest("top inside clockwise convex V", () -> testCone(10, 0,/**/10, 10,/**/20, 10,/**/0, 10, true, true)),
                dynamicTest("top inside counter-clockwise convex V", () -> testCone(10, 0/**/, 10, 10/**/, 0, 10/**/, 20, 10, false, true)),
                dynamicTest("bottom outside clockwise convex V", () -> testCone(10, 0,/**/10, -10,/**/20, 10,/**/0, 10, true, false)),
                dynamicTest("bottom outside counter-clockwise convex V", () -> testCone(10, 0/**/, 10, -10/**/, 0, 10/**/, 20, 10, false, false))
        );
    }

    public void testCone(double v0x, double v0y, double v1x, double v1y, double vMx, double vMy, double vPx, double vPy,
                         boolean clockwise, boolean expected) {
        testCone(new Point2D.Double(v0x, v0y),
                new Point2D.Double(v1x, v1y),
                new Point2D.Double(vMx, vMy),
                new Point2D.Double(vPx, vPy),
                clockwise,
                expected
        );
    }

    public void testCone(Point2D.Double v0, Point2D.Double v1, Point2D.Double vM, Point2D.Double vP, boolean clockwise, boolean expected) {
        EarClipping earClipping = new EarClipping();
        boolean actualClockwise = earClipping.isClockwise(Arrays.asList(vM, v0, vP));
        System.out.println("actualClockwise " + actualClockwise + " vsExpected " + clockwise);
        boolean actual = EarClipping.segmentInCone(v0, v1, vM, vP, clockwise);
        assertEquals(expected, actual);
    }


    @TestFactory
    public List<DynamicTest> testFactoryTriangulate() {
        return Arrays.asList(
                dynamicTest("[(-shape", () -> testTriangulate(points(
                        0, 40,
                        30, 40,
                        15, 30,
                        10, 20,
                        15, 10,
                        30, 0,
                        0, 0
                ))),
                dynamicTest(")]-shape", () -> testTriangulate(points(
                        0, 40,
                        -30, 40,
                        -15, 30,
                        -10, 20,
                        -15, 10,
                        -30, 0,
                        0, 0
                ))),
                dynamicTest("rough circle segment", () -> testTriangulate(points(
                        0, -2.9, 0.0, 0.0, 0.0, 0.1, 0.9, 3.4, 1.5, 4.7, 2.6, 6.6, 3.6, 7.9, 4.2, 8.5, 5.4, 6.9, 5.0, 6.5, 4.3, 5.6, 3.2, 3.7, 2.8, 2.8, 2.1, 0.3, 2.0, -0.1, 1.9, -0.4, 1.9, -2.8, 2.2, -5.1, 2.3, -5.7, 2.6, -6.5, 3.4, -8.4, 3.9, -9.3, 2.4, -10.6, 1.6, -9.3, 0.5, -6.7, 0.4, -6.2, 0.3, -5.9))),
                dynamicTest("circle segment", () -> testTriangulate(points(
                        -2.220446049250313E-16, -2.9846621175575287, 0.0, 0.0, 0.024690245782453615, 0.18503420782622826, 0.9679706904715062, 3.4574659139232162, 1.56472218727641, 4.745725812177891, 2.647507966876407, 6.637931559210909, 3.6211109027901647, 7.904849150045861, 4.210593832761793, 8.52794220841832, 5.49910973865815, 6.979845806154759, 5.062573357927392, 6.518419778387071, 4.358270443424841, 5.60193299504199, 3.2754842369839574, 3.709728133528591, 2.864494488612161, 2.8224877777578325, 2.1591664100714625, 0.3755613579599295, 2.0247796423162434, -0.1571351807758562, 1.9938868571611172, -0.47427578581231034, 1.993887959925635, -2.8636361995447186, 2.2591647259997765, -5.110384990961289, 2.3808917641349443, -5.741392401253066, 2.6489082159722432, -6.539492998542276, 3.4714667279372393, -8.476823720615497, 3.9928699027784673, -9.316462988436697, 2.445301258403071, -10.60453962941882, 1.6970601560720544, -9.399612586170054, 0.5515839643773632, -6.701728858394809, 0.41490728532632826, -6.294731928580962, 0.35283128294694643, -5.972945904327868))),
                dynamicTest("concave clockwise", () -> testTriangulate(points(0, 0, 0, 20, 20, 20, 8, 10))),
                dynamicTest("concave counter-clockwise", () -> testTriangulate(points(8, 10, 20, 20, 0, 20, 0, 0))),
                dynamicTest("flag off diagonal", () -> testTriangulate(points(8, 10, 20, 20, 0, 20, 0, 0, 20, 0))),
                dynamicTest("flag on diagonal 3", () -> testTriangulate(points(10, 10, 20, 20, 0, 20, 0, 0, 20, 0))),
                dynamicTest("flag on diagonal 1", () -> testTriangulate(points(0, 0, 20, 0, 10, 10, 20, 20, 0, 20))),
                dynamicTest("flag on diagonal 2", () -> testTriangulate(points(20, 0, 10, 10, 20, 20, 0, 20, 0, 0))),
                dynamicTest("square clockwise", () -> testTriangulate(points(0, 0, 0, 10, 10, 10, 10, 0))),
                dynamicTest("square counter-clockwise", () -> testTriangulate(points(0, 0, 10, 0, 10, 10, 0, 10)))

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

    void testTriangulate(List<Point2D.Double> p) throws Exception {
        System.err.println("test:" + p);

        List<List<Integer>> triangles = new EarClipping().triangulateIndices(Collections.unmodifiableList(p));

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


        StringBuilder buf = new StringBuilder();
        for (List<Integer> triangle : triangles) {
            buf.append("M");
            for (int index : triangle) {
                buf.append(p.get(index).getX());
                buf.append(",");
                buf.append(p.get(index).getY());
                buf.append(" ");
            }
            buf.append('Z');
        }
        System.out.println(triangles);
        System.out.println(buf);
        assertEquals(p.size() - 2, triangles.size());


        // All diagonals must be contained in the path (we just check the midpoints)
        for (List<Integer> triangle : triangles) {
            for (int i = 0, n = triangle.size(); i < n; i++) {
                int j = (i + 1) % n;
                Point2D.Double a = p.get(triangle.get(i));
                Point2D.Double b = p.get(triangle.get(j));
                if (!edges.contains(new Edge(a, b))) {
                    Point2D.Double mid = new Point2D.Double(0.5 * (a.getX() + b.getX()), 0.5 * (a.getY() + b.getY()));
                    assertTrue(path.contains(mid), mid + " not contained in path, diagonal:" + a + " " + b + "\ntriangle:" + triangle);
                }
            }
        }


    }

    private List<Point2D.Double> points(double... c) {
        List<Point2D.Double> p = new ArrayList<>();
        for (int i = 0; i < c.length; i += 2) {
            p.add(new Point2D.Double(c[i], c[i + 1]));
        }
        return p;
    }
}