/*
 * @(#)Point3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;

import java.util.Objects;

import static java.lang.Math.abs;

/**
 * A point representing a location in (x, y, z) coordinate space.
 *
 * @author Werner Randelshofer
 */
public class Point3D implements Cloneable {

    public static final Point3D UNIT_X = new Point3D(1, 0, 0);
    public static final Point3D UNIT_Y = new Point3D(0, 1, 0);
    public static final Point3D UNIT_Z = new Point3D(0, 0, 1);
    public static final Point3D ZERO = new Point3D(0, 0, 0);
    /**
     * The x coordinate of the point.
     */
    public double x;
    /**
     * The y coordinate of the point.
     */
    public double y;
    /**
     * The z coordinate of the point.
     */
    public double z;

    /**
     * Constructs and initializes a Point with 0, 0, 0.
     */
    public Point3D() {
    }

    /**
     * Constructs and initializes a Point with the specified coordinates.
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D divide(double a) {
        return new Point3D(x / a, y / a, z / a);
    }

    public double squaredLength() {
        return squaredNorm();
    }

    /**
     * Returns a String that represents the value of this Object.
     */
    @Nonnull
    public String toString() {
        return "Point3D[" + x + ", " + y + ", " + z + "]";
    }

    /**
     * Returns the X coordinate of the point in double precision.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y coordinate of the point in double precision.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the Z coordinate of the point in double precision.
     */
    public double getZ() {
        return z;
    }

    public double get(int i) {
        switch (i) {
        case 0:
            return x;
        case 1:
            return y;
        case 2:
            return z;
        default:
            throw new IllegalArgumentException("i=" + i);
        }
    }


    /**
     * Computes the plane equation a*x + b*y + c*z + d = 0 from three given
     * points in space.
     *
     * @param v1
     * @param v2
     * @param v3
     */
    @Nonnull
    public static double[] planeEquation(@Nonnull Point3D v1, @Nonnull Point3D v2, @Nonnull Point3D v3) {
        double x1 = v1.x;
        double x2 = v2.x;
        double x3 = v3.x;
        double y1 = v1.y;
        double y2 = v2.y;
        double y3 = v3.y;
        double z1 = v1.z;
        double z2 = v2.z;
        double z3 = v3.z;

        double a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
        double b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
        double c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        double d = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1));
        return new double[]{a, b, c, d};
    }

    /**
     * Cross product of the vectors {@code a} and {@code b}.
     */
    @Nonnull
    public static Point3D cross(@Nonnull Point3D a, @Nonnull Point3D b) {
        return new Point3D(
                a.y * b.z - b.y * a.z,
                a.z * b.x - b.z * a.x,
                a.x * b.y - b.x * a.y);
    }

    /**
     * Cross product this x b of 2 vectors.
     */
    @Nonnull
    public Point3D cross(@Nonnull Point3D b) {
        return cross(this, b);
    }

    /**
     * Dot product of 2 vectors.
     */
    public static double dot(@Nonnull Point3D a, @Nonnull Point3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public double dot(Point3D b) {
        return dot(this, b);
    }

    /**
     * Subtracts 2 vectors.
     */
    @Nonnull
    public static Point3D sub(@Nonnull Point3D a, @Nonnull Point3D b) {
        return new Point3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public Point3D subtract(Point3D b) {
        return new Point3D(this.x - b.x, this.y - b.y, this.z - b.z);
    }

    public Point3D add(Point3D b) {
        return new Point3D(this.x + b.x, this.y + b.y, this.z + b.z);
    }

    public Point3D multiply(double b) {
        return new Point3D(this.x * b, this.y * b, this.z * b);
    }

    /**
     * Returns a normalized representation of this vector.
     */
    @Nonnull
    public Point3D normalized() {
        double dist = norm();
        if (dist == 0 || dist == 1) {
            return this;
        }
        return new Point3D(x / dist, y / dist, z / dist);
    }

    /**
     * Length of this vector.
     */
    public double length() {
        return norm();
    }

    /**
     * Norm of this vector.
     */
    public double norm() {
        return Math.sqrt(squaredNorm());
    }

    /**
     * Squared norm of this vector.
     */
    public double squaredNorm() {
        return x * x + y * y + z * z;
    }


    @Nonnull
    @Override
    public Point3D clone() {
        try {
            return (Point3D) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError err = new InternalError();
            err.initCause(ex);
            throw err;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Point3D)) {
            return false;
        }
        Point3D that = (Point3D) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public static boolean areCollinear(Point3D v0, Point3D v1, Point3D v2, double eps) {
        Point3D left = Point3D.sub(v1, v0);
        Point3D right = Point3D.sub(v2, v0);
        return v0.equals(v1) || v0.equals(v2) || abs(Point3D.cross(left, right).squaredLength()) <= eps;
    }
}
