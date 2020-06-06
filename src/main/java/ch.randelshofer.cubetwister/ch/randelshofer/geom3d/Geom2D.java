package ch.randelshofer.geom3d;

import java.awt.geom.Point2D;

public class Geom2D {
    static public Point2D.Double subtract(Point2D.Double v1, Point2D.Double v0) {
        return new Point2D.Double(v1.getX() - v0.getX(), v1.getY() - v0.getY());
    }

    static public Point2D.Double add(Point2D.Double v1, Point2D.Double v0) {
        return new Point2D.Double(v1.getX() + v0.getX(), v1.getY() + v0.getY());
    }

    static public double dot(Point2D.Double a, Point2D.Double b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    static public Point2D.Double multiply(Point2D.Double v, double a) {
        return new Point2D.Double(v.getX() * a, v.getY() * a);
    }

    static public Point2D.Double divide(Point2D.Double v, double a) {
        return new Point2D.Double(v.getX() / a, v.getY() / a);
    }

    static public Point2D.Double normalize(Point2D.Double v) {
        double norm = v.distance(0, 0);
        return new Point2D.Double(v.getX() / norm, v.getY() / norm);
    }

    static public Point2D.Double perp(Point2D.Double v) {
        return new Point2D.Double(-v.getY(), v.getX());
    }
}
