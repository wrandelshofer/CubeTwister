package ch.randelshofer.geom3d;

import java.util.Objects;

import static java.lang.Double.max;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.nextUp;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Represents a quaternion.
 * <p>
 * This class represents a quaternion {@code w+xi+yj+zk} that is a convenient representation of
 * orientations and rotations of objects in three dimensions. Compared to other representations
 * like Euler angles or 3x3 matrices, quaternions offer the following advantages:
 * <ul>
 * <li>compact storage (4 scalars)</li>
 * <li>efficient to compose (28 flops)</li>
 * <li>stable spherical interpolation</li>
 * </ul>
 */
public class Quaternion {
    public final static Quaternion IDENTITY = new Quaternion(1, 0, 0, 0);

    private final double x, y, z, w;

    /**
     * Constructs and initializes the quaternion {@code w+xi+yj+zk} from
     * its four coefficients.
     * <p>
     * Note the order of the arguments: the real {@code w} coefficient comes first.
     */
    public Quaternion(double w, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quaternion)) {
            return false;
        }
        Quaternion that = (Quaternion) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Double.compare(that.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    /**
     * returns a vector expression of the imaginary part (x,y,z).
     */
    public Point3D vec() {
        return new Point3D(x, y, z);
    }

    /**
     * Rotation of a vector by a quaternion.
     * <p>
     * If the quaternion is used to rotate several points (>1)
     * then it is much more efficient to first convert it to a 3x3 Matrix.
     * <p>
     * Comparison of the operation cost for n transformations:
     * <dl>
     *     <dt>Quaternion</dt><dd>30 n</dd>
     *     <dt>Via a Matrix</dt><dd>24 + 15n</dd>
     * </dl>
     */
    public Point3D transform(Point3D v) {
        Point3D vec = this.vec();
        Point3D uv = vec.cross(v);
        uv = uv.add(uv);
        return v.add(uv.multiply(w)).add(vec.cross(uv));
    }

    /**
     * Returns the squared norm of the quaternion's coefficients
     */
    public double squaredNorm() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * Returns the norm of the quaternion's coefficients
     */
    public double norm() {
        return sqrt(squaredNorm());
    }

    /**
     * Returns a normalized quaternion.
     *
     * @return normalized
     */
    public Quaternion normalized() {
        double norm = norm();
        if (norm == 1) {
            return this;
        }
        return new Quaternion(w / norm, x / norm, y / norm, z / norm);
    }


    /**
     * Returns the dot product of two quaternions {@code a} and {@code b}.
     * <p>
     * Geometrically speaking, the dot product of two unit quaternions
     * corresponds to the cosine of half the angle between the two rotations.
     */
    public static double dot(Quaternion a, Quaternion b) {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    public double dot(Quaternion b) {
        return dot(this, b);
    }

    /**
     * Returns the angle (in radian) between two rotations.
     */
    public static double angularDistance(Quaternion a, Quaternion b) {
        double d = abs(dot(a, b));
        if (d >= 1.0) {
            return 0;
        }
        return 2 * acos(d);

    }

    /**
     * Returns the spherical linear interpolation between the two quaternions
     * {@code a} and {@code b}.
     * <p>
     * slerp -> sin( (1-λ)*θ ) / sin(θ) * a + sin(λ*θ) / sin(θ) * b
     * where
     * θ = acos(q1 * q2)
     * </p>
     *
     * @param a quaternion a
     * @param b quaternion b
     * @param t amount of interpolation, t in [0,1].
     * @return an interpolated quaternion between a and b
     */
    public static Quaternion slerp(Quaternion a, Quaternion b, double t) {
        double omega = dot(a, b);
        double absOmega = abs(omega);

        double sa;
        double sb;

        if (absOmega >= Math.nextDown(1.0)) {
            sa = 1.0 - t;
            sb = t;
        } else {
            // theta is the angle between the 2 quaternions
            double theta = acos(absOmega);
            double sinTheta = sin(theta);

            sa = sin((1.0 - t) * theta) / sinTheta;
            sb = sin((t * theta)) / sinTheta;
            if (omega < 0) {
                sb = -sb;
            }
        }

        return new Quaternion(sa * a.w + sb * b.w,
                sa * a.x + sb * b.x,
                sa * a.y + sb * b.y,
                sa * a.z + sb * b.z

        );
    }

    public Quaternion slerp(double t) {
        return Quaternion.slerp(Quaternion.IDENTITY, this, t);
    }


    /**
     * Performs a spherical linear interpolation from q1 to q2.
     * <p>
     * q1 -> sin((1-lambda)*omega)/sin(omega)*q1 + sin(lambda*omega)/sin(omega)*q2
     * where
     * omega = acos(q1 * q2)
     *
     * @param q1     unit quaternion 1
     * @param q2     unit quaternion 2
     * @param lambda amount of interpolation, lambda in [0,1]
     * @return interpolated rotation
     */
    public static Quaternion SLERP(Quaternion q1, Quaternion q2, double lambda) {
        double omega = acos(q1.dot(q2));
        if (abs(omega) < 1e-6) {
            return q1.multiply(1 - lambda).add(q2.multiply(lambda));
        } else {
            return q1.multiply(sin((1 - lambda) * omega) / sin(omega)).add(q2.multiply(sin(lambda * omega) / sin(omega)));
        }
    }

    public Quaternion multiply(double s) {
        return new Quaternion(w * s, x * s, y * s, z * s);
    }

    public Quaternion add(Quaternion b) {
        return add(this, b);
    }

    public static Quaternion add(Quaternion a, Quaternion b) {
        return new Quaternion(a.w + b.w, a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Computes the product of quaternions {@code a} and {@code }b.
     *
     * @param a quaternion
     * @param b quaternion
     * @return a * b
     */
    public static Quaternion product(Quaternion a, Quaternion b) {
        return new Quaternion(
                a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z,
                a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
                a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z,
                a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x
        );

    }

    /**
     * Returns the conjugate of this which is equal to the multiplicative inverse
     * if the quaternion is normalized.
     * <p>
     * The conjugate of a quaternion represents the opposite rotation.
     */
    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    /**
     * Convert this quaternion to a 3x3 rotation matrix. The quaternion is required to
     * be normalized, otherwise the result is undefined.
     */
    public Transform3D toRotationMatrix() {

        double tx = 2 * x;
        double ty = 2 * y;
        double tz = 2 * z;
        double twx = tx * w;
        double twy = ty * w;
        double twz = tz * w;
        double txx = tx * x;
        double txy = ty * x;
        double txz = tz * x;
        double tyy = ty * y;
        double tyz = tz * y;
        double tzz = tz * z;

        return new Transform3D(
                1 - (tyy + tzz), txy + twz, txz - twy, 0,
                txy - twz, 1 - (txx + tzz), tyz + twx, 0,
                txz + twy, tyz - twx, 1 - (txx + tyy), 0);
    }

    /**
     * Creates a quaternion representing a rotation between
     * the two arbitrary vectors {@code a} and {@code b}. In other words, the built
     * rotation represent a rotation sending the line of direction {@code a}
     * to the line of direction {@code b} both lines passing through the origin.
     * <p>
     * Note that the two input vectors do not have to be normalized, and
     * do not need to have the same norm.
     */
    public static Quaternion ofTwoVectors(Point3D a, Point3D b) {
        Point3D v0 = a.normalized();
        Point3D v1 = b.normalized();
        double c = v1.dot(v0);
        double w;
        Point3D vec;

        // If dot == 1, vectors are the same
        if (c >= 1.0f) {
            return IDENTITY;
        }
        // if dot == -1, vectors are nearly opposites
        if (c <= nextUp(-1)) {

            // Generate an axis
            Point3D axis = Point3D.UNIT_X.cross(a);
            if (axis.squaredLength() == 0) // pick another if colinear
            {
                axis = Point3D.UNIT_Y.cross(a);
            }
            axis = axis.normalized();
            return ofAngleAxis(Math.PI, axis);
        }
        Point3D axis = v0.cross(v1);
        double s = sqrt((1 + c) * 2);
        vec = axis.divide(s);
        w = s * 0.5;

        return new Quaternion(w, vec.x, vec.y, vec.z);
    }

    /**
     * Creates a quaternion from angle axis.
     */
    public static Quaternion ofAngleAxis(double angle, Point3D axis) {
        double ha = 0.5 * angle;
        double w = cos(ha);
        Point3D v = axis.multiply(sin(ha));
        return new Quaternion(w, v.x, v.y, v.z);
    }

    /**
     * Return angle axis of quaternion.
     */
    public Point3D toAxis() {
        double n2 = vec().squaredNorm();
        Point3D axis;
        if (n2 < Math.nextUp(0.0)) {
            axis = new Point3D(1, 0, 0);
        } else {
            axis = vec().divide(sqrt(n2));
        }
        return axis;
    }

    /**
     * Return angle axis of quaternion.
     */
    public double toAngle() {
        double n2 = vec().squaredNorm();
        double angle;
        if (n2 < Math.nextUp(0.0)) {
            angle = 0;
        } else {
            angle = 2 * acos(min(max(-1, w), 1));
        }
        return angle;
    }

    /**
     * Creates a quaternion from a rotation matrix.
     */
    public static Quaternion ofRotationMatrix(Transform3D mat) {
        // This algorithm comes from  "Quaternion Calculus and Fast Animation",
        // Ken Shoemake, 1987 SIGGRAPH course notes
        double t = mat.trace() - 1;
        double w, x, y, z;
        if (t > 0) {
            t = sqrt(t + 1.0);
            w = 0.5 * t;
            t = 0.5 / t;
            x = (mat.coeff(2, 1) - mat.coeff(1, 2)) * t;
            y = (mat.coeff(0, 2) - mat.coeff(2, 0)) * t;
            z = (mat.coeff(1, 0) - mat.coeff(0, 1)) * t;
            return new Quaternion(w, x, y, z);
        } else {
            int i = 0;
            if (mat.coeff(1, 1) > mat.coeff(0, 0)) {
                i = 1;
            }
            if (mat.coeff(2, 2) > mat.coeff(i, i)) {
                i = 2;
            }
            int j = (i + 1) % 3;
            int k = (j + 1) % 3;

            t = sqrt(mat.coeff(i, i) - mat.coeff(j, j) - mat.coeff(k, k) + 1.0);
            double[] coeff = new double[3];
            coeff[i] = 0.5 * t;
            t = 0.5 / t;
            w = (mat.coeff(k, j) - mat.coeff(j, k)) * t;
            coeff[j] = (mat.coeff(j, i) + mat.coeff(i, j)) * t;
            coeff[k] = (mat.coeff(k, i) + mat.coeff(i, k)) * t;
            return new Quaternion(w, coeff[0], coeff[1], coeff[2]);
        }
    }

    @Override
    public String toString() {
        return "Quaternion{" +
                "" + w +
                " " + x +
                "," + y +
                "," + z +
                '}';
    }
}
