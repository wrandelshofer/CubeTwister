package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ConvexHullTest {

    @TestFactory
    public List<DynamicTest> testFactoryMinimumAreaRectangle() {
        return Arrays.asList(
                dynamicTest("axis-aligned rectangle", () -> testMinimumAreaRectangle(points(
                        0, 0,
                        10, 0,
                        10, 20,
                        0, 20
                ))),
                dynamicTest("diamond square", () -> testMinimumAreaRectangle(points(
                        10, 0,
                        20, 10,
                        10, 20,
                        0, 10
                ))),
                dynamicTest("rough circle segment", () -> testMinimumAreaRectangle(points(
                        0, -2.9, 0.0, 0.0, 0.0, 0.1, 0.9, 3.4, 1.5, 4.7, 2.6, 6.6, 3.6, 7.9, 4.2, 8.5, 5.4, 6.9, 5.0, 6.5, 4.3, 5.6, 3.2, 3.7, 2.8, 2.8, 2.1, 0.3, 2.0, -0.1, 1.9, -0.4, 1.9, -2.8, 2.2, -5.1, 2.3, -5.7, 2.6, -6.5, 3.4, -8.4, 3.9, -9.3, 2.4, -10.6, 1.6, -9.3, 0.5, -6.7, 0.4, -6.2, 0.3, -5.9))),
                dynamicTest("irregular quadrangle", () -> testMinimumAreaRectangle(points(
                        190, 110, 150, 150, 220, 190, 250, 150, 250, 150)))
        );
    }


    void testMinimumAreaRectangle(List<Point2D.Double> p) throws Exception {
        System.out.println("test:" + toSvgPoly(p));
        List<Point2D.Double> mar = ConvexHull.getMinimumAreaRectangle(p);

        System.out.println("mar:" + toSvgPoly(mar));
    }

    private List<Point2D.Double> points(double... c) {
        List<Point2D.Double> p = new ArrayList<>();
        for (int i = 0; i < c.length; i += 2) {
            p.add(new Point2D.Double(c[i], c[i + 1]));
        }
        return p;
    }

    @Nonnull
    public static String toSvgPoly(List<Point2D.Double> poly) {
        StringBuilder buf = new StringBuilder();
        buf.append("M");
        for (Point2D.Double p : poly) {
            buf.append(p.getX());
            buf.append(",");
            buf.append(p.getY());
            buf.append(" ");
        }
        buf.append('Z');

        return buf.toString();
    }

}
