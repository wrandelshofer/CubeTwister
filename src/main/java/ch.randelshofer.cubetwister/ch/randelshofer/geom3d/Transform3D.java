/* @(#)Transform3D.java
 * Copyright (c) 1998 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * This class represents a 3D transform which performs a
 * mapping from 3D coordinates to other 3D coordinates.
 * <p>
 * Such a coordinate transformation can be represented by a 4 row by
 * 4 column matrix with an implied last row of [ 0 0 0 1 ] which
 * transforms source coordinates <code>(x,&nbsp;y)</code> into
 * destination coordinates <code>(x',&nbsp;y')</code> by considering
 * them to be a column vector and multiplying the coordinate vector
 * by the matrix according to the following process.
 * <pre>
 * [ x'] = [  m00  m10  m20  m30 ] [ x ] = [ m00x + m10y + m20z + m30 ]
 * [ y'] = [  m01  m11  m21  m31 ] [ y ] = [ m01x + m11y + m21z + m31 ]
 * [ z'] = [  m02  m12  m22  m32 ] [ z ] = [ m02x + m12y + m22z + m32 ]
 * [ 1 ] = [   0    0    0    1  ] [ 1 ] = [             1            ]
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class Transform3D
        implements Cloneable {
    public double m00, m10, m20, m30;
    public double m01, m11, m21, m31;
    public double m02, m12, m22, m32; // Matrix

    /**
     * Constructs a new Transform3D representing the Identity
     * transformation.
     */
    public Transform3D() {
        this(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0);
    }

    /**
     * Constructs a Transform3D representing the specified rotation transformation.
     *
     * @param rx Rotation along the x axis in radians.
     * @param ry Rotation along the y axis in radians.
     * @param rz Rotation along the z axis in radians.
     */
    public Transform3D(double rx, double ry, double rz) {
        rotate(rx, ry, rz);
    }


    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ 1   0         0        0 ]
     *    [ 0   cos(rx)    sin(rx)   0 ]
     *    [ 0   -sin(rx)   cos(rx)   0 ]
     *      [ 0   0         0        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param rx Rotation along the x axis in radians.
     */
    public void rotateX(double rx) {
        double sinX, cosX;

        sinX = sin(rx);
        cosX = cos(rx);

        Transform3D r = new Transform3D(
                1d, 0d, 0d, 0d,
                0d, cosX, sinX, 0d,
                0d, -sinX, cosX, 0d
        );
        concatenate(r);
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(ry)   0   -sin(ry)   0 ]
     *    [ 0        1   0         0 ]
     *    [ sin(ry)   0   cos(ry)    0 ]
     *      [ 0        0   0         1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param ry Rotation along the y axis in radians.
     */
    public void rotateY(double ry) {
        double sinY, cosY;

        sinY = sin(ry);
        cosY = cos(ry);

        Transform3D r = new Transform3D(
                cosY, 0d, -sinY, 0d,
                0d, 1d, 0d, 0d,
                sinY, 0d, cosY, 0d
        );
        concatenate(r);
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(rz)    sin(rz)   0   0 ]
     *    [ -sin(rz)   cos(rz)   0   0 ]
     *    [ 0         0        1   0 ]
     *      [ 0         0        0   1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param rz Rotation along the z axis in radians.
     */
    public void rotateZ(double rz) {
        double sinZ, cosZ;

        sinZ = sin(rz);
        cosZ = cos(rz);
        Transform3D r = new Transform3D(
                cosZ, sinZ, 0d, 0d,
                -sinZ, cosZ, 0d, 0d,
                0d, 0d, 1d, 0d
        );
        concatenate(r);
    }

    /**
     * Concatenates this transform with a scaling transformation.
     * This is equivalent to calling concatenate(S), where S is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   sx   0    0    0   ]
     *    [   0    sy   0    0   ]
     *    [   0    0    sz   0   ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void scaled(double sx, double sy, double sz) {
        Transform3D s = new Transform3D(
                sx, 0d, 0d, 0d,
                0d, sy, 0d, 0d,
                0d, 0d, sz, 0d
        );
        concatenate(s);
    }

    /**
     * Concatenates this transform with a translation transformation.
     * This is equivalent to calling concatenate(T), where T is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   1    0    0    tx  ]
     *    [   0    1    0    ty  ]
     *    [   0    0    1    tz  ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void translated(double tx, double ty, double tz) {
        Transform3D s = new Transform3D(
                1d, 0d, 0d, tx,
                0d, 1d, 0d, ty,
                0d, 0d, 1d, tz
        );
        concatenate(s);
    }

    /**
     * Concatenates a transform Tx to this transform Cx.
     * Cx is updated to perform the combined transformation.
     * Transforming a point p by the updated transform Cx' is
     * equivalent to first transforming p by Tx and then transforming
     * the result by the original transform Cx. In other words,
     * Cx'(p) = Cx(Tx(p)).  In matrix notation, if this transform Cx
     * is represented by the matrix [this] and Tx is represented by
     * the matrix [Tx], then this method does the following:
     * <pre>
     *    [this] = [this] x [Tx]
     * </pre>
     *
     * @param t The transform object to be concatenated with
     *          this transform object.
     */
    public void concatenate(@Nonnull Transform3D t) {
        double a00 = m00;
        double a10 = m10;
        double a20 = m20;
        double a30 = m30;
        double a01 = m01;
        double a11 = m11;
        double a21 = m21;
        double a31 = m31;
        double a02 = m02;
        double a12 = m12;
        double a22 = m22;
        double a32 = m32;

        m00 = a00 * t.m00 + a01 * t.m10 + a02 * t.m20;
        m01 = a00 * t.m01 + a01 * t.m11 + a02 * t.m21;
        m02 = a00 * t.m02 + a01 * t.m12 + a02 * t.m22;

        m10 = a10 * t.m00 + a11 * t.m10 + a12 * t.m20;
        m11 = a10 * t.m01 + a11 * t.m11 + a12 * t.m21;
        m12 = a10 * t.m02 + a11 * t.m12 + a12 * t.m22;

        m20 = a20 * t.m00 + a21 * t.m10 + a22 * t.m20;
        m21 = a20 * t.m01 + a21 * t.m11 + a22 * t.m21;
        m22 = a20 * t.m02 + a21 * t.m12 + a22 * t.m22;

        m30 = a30 * t.m00 + a31 * t.m10 + a32 * t.m20 + t.m30;
        m31 = a30 * t.m01 + a31 * t.m11 + a32 * t.m21 + t.m31;
        m32 = a30 * t.m02 + a31 * t.m12 + a32 * t.m22 + t.m32;
    }

    /**
     * Constructs a new transform from 12 double precision values
     * representing the 12 specifiable entries of the 4x4 transformation matrix.
     */
    public Transform3D(
            double m00, double m10, double m20, double m30,
            double m01, double m11, double m21, double m31,
            double m02, double m12, double m22, double m32) {
        this.m00 = m00;
        this.m10 = m10;
        this.m20 = m20;
        this.m30 = m30;
        this.m01 = m01;
        this.m11 = m11;
        this.m21 = m21;
        this.m31 = m31;
        this.m02 = m02;
        this.m12 = m12;
        this.m22 = m22;
        this.m32 = m32;
    }

    /**
     * Constructs a new transform from 12 double precision values
     * representing the 12 specifiable entries of the 4x4 transformation matrix.
     */
    public Transform3D(double[][] matrix) {
        this.m00 = matrix[0][0];
        this.m10 = matrix[1][0];
        this.m20 = matrix[2][0];
        this.m30 = matrix[3][0];
        this.m01 = matrix[0][1];
        this.m11 = matrix[1][1];
        this.m21 = matrix[2][1];
        this.m31 = matrix[3][1];
        this.m02 = matrix[0][2];
        this.m12 = matrix[1][2];
        this.m22 = matrix[2][2];
        this.m32 = matrix[3][2];
    }

    /**
     * Resets this transform to the Identity transform.
     */
    public void setToIdentity() {
        this.m00 = 1d;
        this.m10 = 0d;
        this.m20 = 0d;
        this.m30 = 0d;
        this.m01 = 0d;
        this.m11 = 1d;
        this.m21 = 0d;
        this.m31 = 0d;
        this.m02 = 0d;
        this.m12 = 0d;
        this.m22 = 1d;
        this.m32 = 0d;
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(rz)*cos(ry)     cos(rz)*sin(ry)*sin(rx)     cos(rz)*-sin(ry)*cos(rx)    0 ]
     *                            +sin(rz)*cos(rx)            +sin(rz)*-sin(ry)*cos(rx)
     *
     *    [ -sin(rz)*cos(ry)    -sin(rz)*sin(ry)*sin(rx)    -sin(rz)*-sin(ry)*cos(rx)   0 ]
     *                            +cos(rz)*cos(rx)            +cos(rz)*sin(rx)
     *
     *    [ sin(ry)             cos(ry)*-sin(rx)            cos(ry)*cos(rx)            0 ]
     *
     *      [ 0                   0                           0                        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param rx Rotation along the x axis in radians.
     * @param ry Rotation along the y axis in radians.
     * @param rz Rotation along the z axis in radians.
     */
    public void rotate(double rx, double ry, double rz) {
        rotateX(rx);
        rotateY(ry);
        rotateZ(rz);
    }

    public Transform3D rotated(double rx, double ry, double rz) {
        return rotatedX(rx).
                rotatedY(ry).
                rotatedZ(rz);
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ 1   0         0        0 ]
     *    [ 0   cos(rx)    sin(rx)   0 ]
     *    [ 0   -sin(rx)   cos(rx)   0 ]
     *      [ 0   0         0        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param rx Rotation along the x axis in radians.
     */
    public Transform3D rotatedX(double rx) {
        double sinX, cosX;

        sinX = sin(rx);
        cosX = cos(rx);

        Transform3D r = new Transform3D(
                1d, 0d, 0d, 0d,
                0d, cosX, sinX, 0d,
                0d, -sinX, cosX, 0d
        );
        return concatenated(r);
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(ry)   0   -sin(ry)   0 ]
     *    [ 0        1   0         0 ]
     *    [ sin(ry)   0   cos(ry)    0 ]
     *      [ 0        0   0         1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param ry Rotation along the y axis in radians.
     */
    public Transform3D rotatedY(double ry) {
        double sinY, cosY;

        sinY = sin(ry);
        cosY = cos(ry);

        Transform3D r = new Transform3D(
                cosY, 0d, -sinY, 0d,
                0d, 1d, 0d, 0d,
                sinY, 0d, cosY, 0d
        );
        return concatenated(r);
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(rz)    sin(rz)   0   0 ]
     *    [ -sin(rz)   cos(rz)   0   0 ]
     *    [ 0         0        1   0 ]
     *      [ 0         0        0   1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     *
     * @param rz Rotation along the z axis in radians.
     */
    public Transform3D rotatedZ(double rz) {
        double sinZ, cosZ;

        sinZ = sin(rz);
        cosZ = cos(rz);
        Transform3D r = new Transform3D(
                cosZ, sinZ, 0d, 0d,
                -sinZ, cosZ, 0d, 0d,
                0d, 0d, 1d, 0d
        );
        return concatenated(r);
    }

    /**
     * Concatenates this transform with a scaling transformation.
     * This is equivalent to calling concatenate(S), where S is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   sx   0    0    0   ]
     *    [   0    sy   0    0   ]
     *    [   0    0    sz   0   ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void scale(double sx, double sy, double sz) {
        Transform3D s = new Transform3D(
                sx, 0d, 0d, 0d,
                0d, sy, 0d, 0d,
                0d, 0d, sz, 0d
        );
        concatenate(s);
    }

    /**
     * Concatenates this transform with a translation transformation.
     * This is equivalent to calling concatenate(T), where T is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   1    0    0    tx  ]
     *    [   0    1    0    ty  ]
     *    [   0    0    1    tz  ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void translate(double tx, double ty, double tz) {
        Transform3D s = new Transform3D(
                1d, 0d, 0d, tx,
                0d, 1d, 0d, ty,
                0d, 0d, 1d, tz
        );
        concatenate(s);
    }

    /**
     * Concatenates a transform Tx to this transform Cx.
     * Cx is updated to perform the combined transformation.
     * Transforming a point p by the updated transform Cx' is
     * equivalent to first transforming p by Tx and then transforming
     * the result by the original transform Cx. In other words,
     * Cx'(p) = Cx(Tx(p)).  In matrix notation, if this transform Cx
     * is represented by the matrix [this] and Tx is represented by
     * the matrix [Tx], then this method does the following:
     * <pre>
     *    [this] = [this] x [Tx]
     * </pre>
     *
     * @param t The transform object to be concatenated with
     *          this transform object.
     */
    public Transform3D concatenated(@Nonnull Transform3D t) {
        double a00 = m00;
        double a10 = m10;
        double a20 = m20;
        double a30 = m30;
        double a01 = m01;
        double a11 = m11;
        double a21 = m21;
        double a31 = m31;
        double a02 = m02;
        double a12 = m12;
        double a22 = m22;
        double a32 = m32;

        double m00, m01, m02, m10, m11, m12, m20, m21, m22, m30, m31, m32;
        m00 = a00 * t.m00 + a01 * t.m10 + a02 * t.m20;
        m01 = a00 * t.m01 + a01 * t.m11 + a02 * t.m21;
        m02 = a00 * t.m02 + a01 * t.m12 + a02 * t.m22;

        m10 = a10 * t.m00 + a11 * t.m10 + a12 * t.m20;
        m11 = a10 * t.m01 + a11 * t.m11 + a12 * t.m21;
        m12 = a10 * t.m02 + a11 * t.m12 + a12 * t.m22;

        m20 = a20 * t.m00 + a21 * t.m10 + a22 * t.m20;
        m21 = a20 * t.m01 + a21 * t.m11 + a22 * t.m21;
        m22 = a20 * t.m02 + a21 * t.m12 + a22 * t.m22;

        m30 = a30 * t.m00 + a31 * t.m10 + a32 * t.m20 + t.m30;
        m31 = a30 * t.m01 + a31 * t.m11 + a32 * t.m21 + t.m31;
        m32 = a30 * t.m02 + a31 * t.m12 + a32 * t.m22 + t.m32;

        return new Transform3D(m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32);
    }

    /**
     * Returns the complete 4x4 matrix representing this transform.
     */
    @Nonnull
    public double[][] getMatrix() {
        double[][] m = {
                {m00, m10, m20, m30},
                {m01, m11, m21, m31},
                {m02, m12, m22, m32},
                {0, 0, 0, 1}
        };
        return m;
    }

    /**
     * Compares two Objects for equality.
     * Returns a boolean that indicates whether this Object is equivalent
     * to the specified Object. This method is used when an Object is stored
     * in a hashtable.
     *
     * @param obj the Object to compare with
     * @return true if these Objects are equal; false otherwise.
     * @see java.util.Hashtable
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Transform3D)) {
            return false;
        }

        Transform3D t = (Transform3D) obj;
        return m00 == t.m00
                && m10 == t.m10
                && m20 == t.m20
                && m30 == t.m30
                && m01 == t.m01
                && m11 == t.m11
                && m21 == t.m21
                && m31 == t.m31
                && m02 == t.m02
                && m12 == t.m12
                && m22 == t.m22
                && m32 == t.m32;
    }

    /**
     * Transforms the specified point.
     */
    public void transform(@Nonnull Point3D pt) {
        transform(pt, pt);
    }

    /**
     * Transforms the specified ptSrc and stores the result in ptDst.
     * If ptDst is null, a new Point3D object will be allocated before
     * storing. In either case, ptDst containing the transformed point
     * is returned for convenience.
     * Note that ptSrc and ptDst can be the same. In this case, the input
     * point will be overwritten with the transformed point.
     */
    @Nullable
    public Point3D transform(@Nonnull Point3D ptSrc, @Nullable Point3D ptDst) {
        double xx = ptSrc.x * this.m00 + ptSrc.y * this.m10 + ptSrc.z * this.m20 + this.m30;
        double yy = ptSrc.x * this.m01 + ptSrc.y * this.m11 + ptSrc.z * this.m21 + this.m31;
        double zz = ptSrc.x * this.m02 + ptSrc.y * this.m12 + ptSrc.z * this.m22 + this.m32;

        if (ptDst == null) {
            return new Point3D(xx, yy, zz);
        } else {
            ptDst.x = xx;
            ptDst.y = yy;
            ptDst.z = zz;
            return ptDst;
        }
    }

    /**
     * Transforms the specified polySrc and stores the result in polyDst.
     * If polyDst is null, a new Polygon3D object will be allocated before
     * storing. In either case, polyDst containing the transformed polygon
     * is returned for convenience.
     * Note that polySrc and polyDst can be the same. In this case, the input
     * point will be overwritten with the transformed point.
     * When the polyDst does not have enough capacity to store the data, its
     * capacity will be increased.
     */
    @Nullable
    public Polygon3D transform(@Nonnull Polygon3D polySrc, @Nullable Polygon3D polyDst) {
        // Create destination polygon if necessary
        if (polyDst == null) {
            polyDst = new Polygon3D(polySrc.npoints);
        }
        // Ensure enough capacity in destination polygon
        if (polyDst.xpoints.length < polySrc.npoints) {
            polyDst.setCapacity(polySrc.npoints);
        }
        // Transform the points
        for (int i = polySrc.npoints - 1; --i >= 0; ) {
            polyDst.xpoints[i] =
                    polySrc.xpoints[i] * this.m00 +
                            polySrc.ypoints[i] * this.m10 +
                            polySrc.zpoints[i] * this.m20 + this.m30;
            polyDst.ypoints[i] =
                    polySrc.xpoints[i] * this.m01 +
                            polySrc.ypoints[i] * this.m11 +
                            polySrc.zpoints[i] * this.m21 + this.m31;
            polyDst.ypoints[i] =
                    polySrc.xpoints[i] * this.m02 +
                            polySrc.ypoints[i] * this.m12 +
                            polySrc.zpoints[i] * this.m22 + this.m32;
        }
        return polyDst;
    }

    /**
     * Transforms the vertices of source array and stores the result in the dest
     * array.
     *
     * @param src        Each group of three entries in this array describe a vector x, y, z.
     * @param srcOffset  Index of the first vector.
     * @param dest       Each group of three entries in this array describe a vector x, y, z.
     * @param destOffset Index of the first vector.
     * @param count      Number of vectors to be transformed.
     */
    public void transform(float[] src, int srcOffset, float[] dest, int destOffset, int count) {
        int i, j, end;
        // Transform the points
        end = srcOffset + count * 3;
        j = destOffset * 3;
        for (i = srcOffset; i < end; i += 3, j += 3) {
            dest[j] = (float) (
                    src[i] * this.m00 +
                            src[i + 1] * this.m10 +
                            src[i + 2] * this.m20 + this.m30
            );
            dest[j + 1] = (float) (
                    src[i] * this.m01 +
                            src[i + 1] * this.m11 +
                            src[i + 2] * this.m21 + this.m31
            );
            dest[j + 2] = (float) (
                    src[i] * this.m02 +
                            src[i + 1] * this.m12 +
                            src[i + 2] * this.m22 + this.m32
            );
        }
    }

    /**
     * Creates a clone of the object. A new instance is allocated and a
     * bitwise clone of the current object is place in the new object.
     *
     * @return a clone of this Object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    @Nonnull
    public String toString() {
        return "{" + m00 + "," + m10 + "," + m20 + "," + m30 + "\n" + m01 + "," + m11 + "," + m21 + "," + m31 + "\n" + m02 + "," + m12 + "," + m22 + "," + m32 + "}";
    }


    /**
     * Returns true, when at least one of the elements
     * of the transform is NaN.
     */
    public boolean isNaN() {
        return Double.isNaN(m00)
                || Double.isNaN(m10)
                || Double.isNaN(m20)
                || Double.isNaN(m30)
                || Double.isNaN(m01)
                || Double.isNaN(m11)
                || Double.isNaN(m21)
                || Double.isNaN(m31)
                || Double.isNaN(m02)
                || Double.isNaN(m12)
                || Double.isNaN(m22)
                || Double.isNaN(m32);
    }

    /**
     * Sets this transform to a copy of the transform in the specified
     * <code>Transform3D</code> object.
     *
     * @param Tx the <code>Transform3D</code> object from which to
     *           copy the transform
     */
    public void setTransform(@Nonnull Transform3D Tx) {
        this.m00 = Tx.m00;
        this.m10 = Tx.m10;
        this.m20 = Tx.m20;
        this.m30 = Tx.m30;
        this.m01 = Tx.m01;
        this.m11 = Tx.m11;
        this.m21 = Tx.m21;
        this.m31 = Tx.m31;
        this.m02 = Tx.m02;
        this.m12 = Tx.m12;
        this.m22 = Tx.m22;
        this.m32 = Tx.m32;
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(m00);
        bits = bits * 31 + Double.doubleToLongBits(m00);
        bits = bits * 31 + Double.doubleToLongBits(m01);
        bits = bits * 31 + Double.doubleToLongBits(m02);
        bits = bits * 31 + Double.doubleToLongBits(m10);
        bits = bits * 31 + Double.doubleToLongBits(m11);
        bits = bits * 31 + Double.doubleToLongBits(m12);
        bits = bits * 31 + Double.doubleToLongBits(m20);
        bits = bits * 31 + Double.doubleToLongBits(m21);
        bits = bits * 31 + Double.doubleToLongBits(m22);
        bits = bits * 31 + Double.doubleToLongBits(m10);
        bits = bits * 31 + Double.doubleToLongBits(m11);
        bits = bits * 31 + Double.doubleToLongBits(m32);
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    /*
     * A function for creating a rotation matrix that rotates a vector called
     * "from" into another vector called "to".
     * Input : from[3], to[3] which both must be *normalized* non-zero vectors
     * Output: mtx[3][3] -- a 3x3 matrix in colum-major form
     * Author: Tomas Moller, 1999
     * As seen at http://lists.apple.com/archives/mac-opengl/2001/Jan/msg00059.html
     */
    public static Transform3D fromToRotation(@Nonnull Point3D from, @Nonnull Point3D to) {

        final double EPSILON = 0.00001f;
        Point3D v;
        double e, h;
        v = Point3D.cross(from, to);
        e = Point3D.dot(from, to);
        double m00, m01, m02, m10, m11, m12, m20, m21, m22, m30, m31, m32;
        if (e > 1.0 - EPSILON) /* "from" almost or equal to "to"-vector? */ {
            /* return identity */
            return new Transform3D();
        } else if (e < -1.0 + EPSILON) /* "from" almost or equal to negated "to"? */ {
            Point3D up, left;
            double invlen;
            double fxx, fyy, fzz, fxy, fxz, fyz;
            double uxx, uyy, uzz, uxy, uxz, uyz;
            double lxx, lyy, lzz, lxy, lxz, lyz;
            /* left=CROSS(from, (1,0,0)) */
            left = new Point3D(0.0f, from.z, -from.y);
            if (Point3D.dot(left, left) < EPSILON) /* was left=CROSS(from,(1,0,0)) a good
            choice? */ {
                /* here we know that left = CROSS(from, (1,0,0)) will be a good
                choice */
                left = new Point3D(-from.z, 0.0f, from.x);
            }
            /* normalize "left" */
            invlen = (1.0 / Math.sqrt(Point3D.dot(left, left)));
            left.x *= invlen;
            left.y *= invlen;
            left.z *= invlen;
            up = Point3D.cross(left, from);
            /* now we have a coordinate system, i.e., a basis; */
            /* M=(from, up, left), and we want to rotate to: */
            /* N=(-from, up, -left). This is done with the matrix:*/
            /* N*M^T where M^T is the transpose of M */
            fxx = -from.x * from.x;
            fyy = -from.y * from.y;
            fzz = -from.z * from.z;
            fxy = -from.x * from.y;
            fxz = -from.x * from.z;
            fyz = -from.y * from.z;

            uxx = up.x * up.x;
            uyy = up.y * up.y;
            uzz = up.z * up.z;
            uxy = up.x * up.y;
            uxz = up.x * up.z;
            uyz = up.y * up.z;

            lxx = -left.x * left.x;
            lyy = -left.y * left.y;
            lzz = -left.z * left.z;
            lxy = -left.x * left.y;
            lxz = -left.x * left.z;
            lyz = -left.y * left.z;
            /* symmetric matrix */
            m00 = fxx + uxx + lxx;
            m01 = fxy + uxy + lxy;
            m02 = fxz + uxz + lxz;
            m10 = m01;
            m11 = fyy + uyy + lyy;
            m12 = fyz + uyz + lyz;
            m20 = m02;
            m21 = m12;
            m22 = fzz + uzz + lzz;
            m30 = m31 = m32 = 0;
        } else /* the most common case, unless "from"="to", or "from"=-"to" */ {
            /*
            #if 0
            // unoptimized version - a good compiler will optimize this.
            h=(1.0-e)/DOT(v,v);
            M(0, 0)=e+h*v[0]*v[0]; M(0, 1)=h*v[0]*v[1]-v[2]; M(0,
            2)=h*v[0]*v[2]+v[1];
            M(1, 0)=h*v[0]*v[1]+v[2]; M(1, 1)=e+h*v[1]*v[1]; M(1,
            2)h*v[1]*v[2]-v[0];
            M(2, 0)=h*v[0]*v[2]-v[1]; M(2, 1)=h*v[1]*v[2]+v[0]; M(2,
            2)=e+h*v[2]*v[2];
            #else*/
            // ...otherwise use this hand optimized version (9 mults less)
            double hvx, hvz, hvxy, hvxz, hvyz;
            h = ((1.0 - e) / Point3D.dot(v, v));
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            m00 = e + hvx * v.x;
            m01 = hvxy - v.z;
            m02 = hvxz + v.y;
            m10 = hvxy + v.z;
            m11 = e + h * v.y * v.y;
            m12 = hvyz - v.x;
            m20 = hvxz - v.y;
            m21 = hvyz + v.x;
            m22 = e + hvz * v.z;
            m30 = m31 = m32 = 0;
            //#endif
        }
        //#undef M
        return new Transform3D(m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32);
    }

    @Nonnull
    public Transform3D getInverse() // Inverts this matrix
    // Code generated with MapleV and handoptimized
    {
        double m03 = 0;
        double m13 = 0;
        double m23 = 0;
        double m33 = 1;


        double q1 = m12;
        double q6 = m10 * m01;
        double q7 = m10 * m21;
        double q8 = m02;
        double q13 = m20 * m01;
        double q14 = m20 * m11;
        double q21 = m02 * m21;
        double q22 = m03 * m21;
        double q25 = m01 * m12;
        double q26 = m01 * m13;
        double q27 = m02 * m11;
        double q28 = m03 * m11;
        double q29 = m10 * m22;
        double q30 = m10 * m23;
        double q31 = m20 * m12;
        double q32 = m20 * m13;
        double q35 = m00 * m22;
        double q36 = m00 * m23;
        double q37 = m20 * m02;
        double q38 = m20 * m03;
        double q41 = m00 * m12;
        double q42 = m00 * m13;
        double q43 = m10 * m02;
        double q44 = m10 * m03;
        double q45 = m00 * m11;
        double q48 = m00 * m21;
        double q49 = q45 * m22 - q48 * q1 - q6 * m22 + q7 * q8;
        double q50 = q13 * q1 - q14 * q8;
        double q51 = 1 / (q49 + q50);


        double tm00, tm01, tm02, tm10, tm11, tm12, tm20, tm21, tm22, tm30, tm31, tm32;
        tm00 = (m11 * m22 * m33 - m11 * m23 * m32 - m21 * m12 * m33 + m21 * m13 * m32 + m31 * m12 * m23 - m31 * m13 * m22) * q51;
        tm01 = -(m01 * m22 * m33 - m01 * m23 * m32 - q21 * m33 + q22 * m32) * q51;
        tm02 = (q25 * m33 - q26 * m32 - q27 * m33 + q28 * m32) * q51;
        tm30 = -(q25 * m23 - q26 * m22 - q27 * m23 + q28 * m22 + q21 * m13 - q22 * m12) * q51;
        tm10 = -(q29 * m33 - q30 * m32 - q31 * m33 + q32 * m32) * q51;
        tm11 = (q35 * m33 - q36 * m32 - q37 * m33 + q38 * m32) * q51;
        tm12 = -(q41 * m33 - q42 * m32 - q43 * m33 + q44 * m32) * q51;
        tm31 = (q41 * m23 - q42 * m22 - q43 * m23 + q44 * m22 + q37 * m13 - q38 * m12) * q51;
        tm20 = (q7 * m33 - q30 * m31 - q14 * m33 + q32 * m31) * q51;
        tm21 = -(q48 * m33 - q36 * m31 - q13 * m33 + q38 * m31) * q51;
        tm22 = (q45 * m33 - q42 * m31 - q6 * m33 + q44 * m31) * q51;
        tm32 = -(q45 * m23 - q42 * m21 - q6 * m23 + q44 * m21 + q13 * m13 - q38 * m11) * q51;

        return new Transform3D(tm00, tm10, tm20, tm30,
                tm01, tm11, tm21, tm31,
                tm02, tm12, tm22, tm32);
    }

    public double coeff(int y, int x) {
        if (y < 0 || y > 3 || x < 0 || x > 2) {
            throw new IllegalArgumentException("x=" + x + " y=" + y);
        }

        switch (y) {
        case 0:
            switch (x) {
            case 0:
                return m00;
            case 1:
                return m01;
            case 2:
                return m02;
            }
        case 1:
            switch (x) {
            case 0:
                return m10;
            case 1:
                return m11;
            case 2:
                return m12;
            }
        case 2:
            switch (x) {
            case 0:
                return m20;
            case 1:
                return m21;
            case 2:
                return m22;
            }
        case 3:
            switch (x) {
            case 0:
                return m30;
            case 1:
                return m31;
            case 2:
                return m32;
            }
        }
        throw new IllegalArgumentException("x=" + x + " y=" + y);
    }

    public double trace() {
        return m00 + m11 + m22 + 1;
    }

    /**
     * Performs an exponential map interpolation from R1 to R2.
     * <p>
     * R1 -> R1 exphat( lambda * loghat(inv(R1)*R2) )
     *
     * @param R1     3x3 rotation matrix 1
     * @param R2     3x3 rotation matrix 2
     * @param lambda amount of interpolation, lambda in [0,1]
     * @return interpolated rotation
     * /
    public static Transform3D ELERP(Transform3D R1, Transform3D R2, double lambda) {
    return R1 * exphat(lambda * loghat(R1.transposed() * R2));
    }

    /**
     * Computes the exponential map operation.
     * <p>
     * R = exphat(r) = I + sin(theta)/theta*hat(r)+(1-cos(theta))/theta^2*hat(r)^2
     * where
     * theta = norm(r)
     *
     * @param r Exponential map in so(3)
     * @return R   Rotation matrix of r in SO(3)
     * /
    public static Transform3D exphat(Point3D r) {
    double theta = r.norm();
    Transform3D R = new Transform3D();
    if (abs(theta) < 1e-14f) {
    R.setToIdentity();
    } else {
    R.setToIdentity();
    R = R + hat(r).multiply(sin(theta) / theta ) + (1 - cos(theta)) / (theta * theta) * hat(r) * hat(r);
    }
    return R;
    }*/

    /**
     * Performs the hat operation on a 3-vector.
     */
    public static Transform3D hat(Point3D r) {
        return new Transform3D(
                0, -r.z, r.y, 0,
                r.z, 0, -r.x, 0,
                -r.y, r.x, 0, 0);
    }
}
