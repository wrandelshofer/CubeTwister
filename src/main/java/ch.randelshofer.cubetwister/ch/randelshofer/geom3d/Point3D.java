/* @(#)Point3D.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;

/**
 * A point representing a location in (x, y, z) coordinate space.
 *
 * @author Werner Randelshofer
 */
public class Point3D implements Cloneable {

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
     * returns a x b.
     */
    @Nonnull
    public static Point3D vectorProduct(@Nonnull Point3D a, @Nonnull Point3D b) {
        return new Point3D(a.y * b.z - b.y * a.z, a.z * b.x - b.z * a.x, a.x * b.y - b.x * a.y);
    }

    /**
     * dotProduct product of 2 vectors.
     */
    public static double dotProduct(@Nonnull Point3D a, @Nonnull Point3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * subtracts 2 vectors.
     */
    @Nonnull
    public static Point3D sub(@Nonnull Point3D a, @Nonnull Point3D b) {
        return new Point3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Normalizes the vector.
     */
    @Nonnull
    public Point3D normalize() {
        double dist = length();
        if (dist == 0) {
            return this;
        }
        double invdist = 1 / dist;
        x *= invdist;
        y *= invdist;
        z *= invdist;
        return this;
    }
    /** Length of this vector. */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
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
}
