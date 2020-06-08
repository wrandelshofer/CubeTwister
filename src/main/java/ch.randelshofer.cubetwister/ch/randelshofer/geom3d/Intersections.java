/*
 * @(#)Intersections.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

public class Intersections {
    /**
     * Computes the intersection of two rays given their origins and their normals.
     * <p>
     * The two rays must lie on the same plane.
     * <pre>
     *     o1 + a * d1 = o2 + b * d2;
     *     with a ≥ 0 and b ≥ 0
     *
     *     a * d1 = o2 - o1 + b * d2;
     *
     *     a * d1 = o2 - o1 + b * d2  // Take cross product with d2 on both sides,
     *                                // this will make the term with 'b' drop out.
     *     a * d1.cross(d2) = (o2 - o1).cross(d2)
     *     a = (o2 - o1).cross(d2).norm() / d1.cross(d2).norm()
     * </pre>
     *
     * @param o1 start point of ray 1
     * @param d1 direction of ray 1
     * @param o2 start point of ray 2
     * @param d2 direction of ray 2
     */
    public static Point3D intersectRays(Point3D o1, Point3D d1, Point3D o2, Point3D d2) {
        double denom = d1.cross(d2).squaredNorm();
        if (denom == 0) {
            return null;
        }
        double numa = det(o2.subtract(o1), d2, d1.cross(d2));
        double numb = det(o2.subtract(o1), d1, d1.cross(d2));
        double a = numa / denom;
        double b = numb / denom;
        return a >= 0 && b >= 0 ? o1.add(d1.multiply(a)) : null;
    }

    /**
     * Computes the intersection of two lines given their origins and their normals.
     * <p>
     * The two normals must lie on the same plane.
     * <pre>
     *     o1 + a * d1 = o2 + b * d2;
     *
     *          a * d1 = o2 - o1 + b * d2;
     *
     *     a * d1 = o2 - o1 + b * d2  // Take cross product with d2 on both sides,
     *                                // this will make the term with 'b' drop out
     *                                // because the cross-product of a vector
     *                                // with itself is a zero-vector.
     *     a * d1.cross(d2) = (o2 - o1).cross(d2)
     *     a = det(o2 - o1,d2,d1.cross(d2)) / d1.cross(d2).squaredNorm()
     * </pre>
     *
     * @param o1 start point of ray 1
     * @param d1 direction of ray 1
     * @param o2 start point of ray 2
     * @param d2 direction of ray 2
     */
    public static Point3D intersectNormals(Point3D o1, Point3D d1, Point3D o2, Point3D d2) {
        double denom = d1.cross(d2).squaredNorm();
        if (denom == 0) {
            return null;
        }
        double numa = det(o2.subtract(o1), d2, d1.cross(d2));
        double a = numa / denom;
        return o1.add(d1.multiply(a));
    }

    /**
     * Computes the determinant of the 3x3 matrix given by the 3 vectors
     * a, b and c.
     *
     * @param a vector a
     * @param b vector b
     * @param c vector c
     * @return the determinant
     */
    private static double det(Point3D a, Point3D b, Point3D c) {
        double a11 = a.x, a12 = b.x, a13 = c.x,
                a21 = a.y, a22 = b.y, a23 = c.y,
                a31 = a.z, a32 = b.z, a33 = c.z;

        return a11 * a22 * a33
                + a12 * a23 * a31
                + a13 * a21 * a32
                - a13 * a22 * a31
                - a12 * a21 * a33
                - a11 * a23 * a32;
    }
}
