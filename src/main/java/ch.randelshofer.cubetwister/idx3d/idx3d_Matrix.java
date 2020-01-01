// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------
package idx3d;

import org.jhotdraw.annotation.Nonnull;

public class idx3d_Matrix // defines a 3d matrix
{
    // M A T R I X   D A T A

    public float m00 = 1, m01 = 0, m02 = 0, m03 = 0;
    public float m10 = 0, m11 = 1, m12 = 0, m13 = 0;
    public float m20 = 0, m21 = 0, m22 = 1, m23 = 0;
    public float m30 = 0, m31 = 0, m32 = 0, m33 = 1;


    // C O N S T R U C T O R S
    public idx3d_Matrix() {
    }

    public idx3d_Matrix(@Nonnull idx3d_Vector right, @Nonnull idx3d_Vector up, @Nonnull idx3d_Vector forward) {
        m00 = right.x;
        m10 = right.y;
        m20 = right.z;
        m01 = up.x;
        m11 = up.y;
        m21 = up.z;
        m02 = forward.x;
        m12 = forward.y;
        m22 = forward.z;
    }

    public void importFromArray(@Nonnull float[][] data) {
        if (data.length < 4) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (data[i].length < 4) {
                return;
            }
        }

        m00 = data[0][0];
        m01 = data[0][1];
        m02 = data[0][2];
        m03 = data[0][3];
        m10 = data[1][0];
        m11 = data[1][1];
        m12 = data[1][2];
        m13 = data[1][3];
        m20 = data[2][0];
        m21 = data[2][1];
        m22 = data[2][2];
        m23 = data[2][3];
        m30 = data[3][0];
        m31 = data[3][1];
        m32 = data[3][2];
        m33 = data[3][3];
    }

    @Nonnull
    public float[][] exportToArray() {
        float data[][] = new float[4][4];
        data[0][0] = m00;
        data[0][1] = m01;
        data[0][2] = m02;
        data[0][3] = m03;
        data[1][0] = m10;
        data[1][1] = m11;
        data[1][2] = m12;
        data[1][3] = m13;
        data[2][0] = m20;
        data[2][1] = m21;
        data[2][2] = m22;
        data[2][3] = m23;
        data[3][0] = m30;
        data[3][1] = m31;
        data[3][2] = m32;
        data[3][3] = m33;
        return data;
    }

    // F A C T O R Y  M E T H O D S
    @Nonnull
    public static idx3d_Matrix shiftMatrix(float dx, float dy, float dz) // matrix for shifting
    {
        idx3d_Matrix m = new idx3d_Matrix();
        m.m03 = dx;
        m.m13 = dy;
        m.m23 = dz;
        return m;
    }

    /** matrix for scaling. */
    @Nonnull
    public static idx3d_Matrix scaleMatrix(float dx, float dy, float dz) {
        idx3d_Matrix m = new idx3d_Matrix();
        m.m00 = dx;
        m.m11 = dy;
        m.m22 = dz;
        return m;
    }

    /** matrix for scaling. */
    @Nonnull
    public static idx3d_Matrix scaleMatrix(float d) {
        return idx3d_Matrix.scaleMatrix(d, d, d);
    }

    /** matrix for rotation. */
    @Nonnull
    public static idx3d_Matrix rotateMatrix_SLOW(float dx, float dy, float dz) {
        idx3d_Matrix out = new idx3d_Matrix();
        float SIN;
        float COS;

        if (dx != 0) {
            idx3d_Matrix m = new idx3d_Matrix();
            SIN = idx3d_Math.sin(dx);
            COS = idx3d_Math.cos(dx);
            m.m11 = COS;
            m.m12 = SIN;
            m.m21 = -SIN;
            m.m22 = COS;
            out.transform(m);
        }
        if (dy != 0) {
            idx3d_Matrix m = new idx3d_Matrix();
            SIN = idx3d_Math.sin(dy);
            COS = idx3d_Math.cos(dy);
            m.m00 = COS;
            m.m02 = SIN;
            m.m20 = -SIN;
            m.m22 = COS;
            out.transform(m);
        }
        if (dz != 0) {
            idx3d_Matrix m = new idx3d_Matrix();
            SIN = idx3d_Math.sin(dz);
            COS = idx3d_Math.cos(dz);
            m.m00 = COS;
            m.m01 = SIN;
            m.m10 = -SIN;
            m.m11 = COS;
            out.transform(m);
        }
        return out;
    }

    /** matrix for rotation. */
    @Nonnull
    public static idx3d_Matrix rotateMatrix(float dx, float dy, float dz) {
        idx3d_Matrix out = new idx3d_Matrix();
        float SIN;
        float COS;

        if (dx != 0) {
            SIN = idx3d_Math.sin(dx);
            COS = idx3d_Math.cos(dx);
            out.m11 = COS;
            out.m12 = SIN;
            out.m21 = -SIN;
            out.m22 = COS;
        }
        if (dy != 0) {
            SIN = idx3d_Math.sin(dy);
            COS = idx3d_Math.cos(dy);

            float a00 = out.m00, a01 = out.m01, a02 = out.m02, a03 = out.m03;
            float a20 = out.m20, a21 = out.m21, a22 = out.m22, a23 = out.m23;
            out.m00 = COS * a00 + SIN * a20;
            out.m01 = COS * a01 + SIN * a21;
            out.m02 = COS * a02 + SIN * a22;
            out.m03 = COS * a03 + SIN * a23;
            out.m20 = -SIN * a00 + COS * a20;
            out.m21 = -SIN * a01 + COS * a21;
            out.m22 = -SIN * a02 + COS * a22;
            out.m23 = -SIN * a03 + COS * a23;

        }
        if (dz != 0) {
            SIN = idx3d_Math.sin(dz);
            COS = idx3d_Math.cos(dz);
            float a00 = out.m00, a01 = out.m01, a02 = out.m02, a03 = out.m03;
            float a10 = out.m10, a11 = out.m11, a12 = out.m12, a13 = out.m13;

            out.m00 = COS * a00 + SIN * a10;
            out.m01 = COS * a01 + SIN * a11;
            out.m02 = COS * a02 + SIN * a12;
            out.m03 = COS * a03 + SIN * a13;
            out.m10 = -SIN * a00 + COS * a10;
            out.m11 = -SIN * a01 + COS * a11;
            out.m12 = -SIN * a02 + COS * a12;
            out.m13 = -SIN * a03 + COS * a13;
        }
        return out;
    }


    // P U B L I C   M E T H O D S

    /**
     * Sets this matrix to the values specified by the specified
     * matrix.
     */
    public void set(@Nonnull idx3d_Matrix that) {
        m00 = that.m00;
        m01 = that.m01;
        m02 = that.m02;
        m03 = that.m03;
        m10 = that.m10;
        m11 = that.m11;
        m12 = that.m12;
        m13 = that.m13;
        m20 = that.m20;
        m21 = that.m21;
        m22 = that.m22;
        m23 = that.m23;
        m30 = that.m30;
        m31 = that.m31;
        m32 = that.m32;
        m33 = that.m33;
    }

    public void shift(float dx, float dy, float dz) {
        transform(shiftMatrix(dx, dy, dz));
    }

    public void scale(float dx, float dy, float dz) {
        transform(scaleMatrix(dx, dy, dz));
    }

    public void scale(float d) {
        transform(scaleMatrix(d));
    }

    public void rotate(float dx, float dy, float dz) {
        float SIN;
        float COS;

        if (dx != 0) {
            SIN = idx3d_Math.sin(dx);
            COS = idx3d_Math.cos(dx);

            float a10 = m10, a11 = m11, a12 = m12, a13 = m13;
            float a20 = m20, a21 = m21, a22 = m22, a23 = m23;
            m10 = COS * a10 + SIN * a20;
            m11 = COS * a11 + SIN * a21;
            m12 = COS * a12 + SIN * a22;
            m13 = COS * a13 + SIN * a23;
            m20 = -SIN * a10 + COS * a20;
            m21 = -SIN * a11 + COS * a21;
            m22 = -SIN * a12 + COS * a22;
            m23 = -SIN * a13 + COS * a23;
        }
        if (dy != 0) {
            SIN = idx3d_Math.sin(dy);
            COS = idx3d_Math.cos(dy);

            float a00 = m00, a01 = m01, a02 = m02, a03 = m03;
            float a20 = m20, a21 = m21, a22 = m22, a23 = m23;
            m00 = COS * a00 + SIN * a20;
            m01 = COS * a01 + SIN * a21;
            m02 = COS * a02 + SIN * a22;
            m03 = COS * a03 + SIN * a23;
            m20 = -SIN * a00 + COS * a20;
            m21 = -SIN * a01 + COS * a21;
            m22 = -SIN * a02 + COS * a22;
            m23 = -SIN * a03 + COS * a23;

        }
        if (dz != 0) {
            SIN = idx3d_Math.sin(dz);
            COS = idx3d_Math.cos(dz);
            float a00 = m00, a01 = m01, a02 = m02, a03 = m03;
            float a10 = m10, a11 = m11, a12 = m12, a13 = m13;

            m00 = COS * a00 + SIN * a10;
            m01 = COS * a01 + SIN * a11;
            m02 = COS * a02 + SIN * a12;
            m03 = COS * a03 + SIN * a13;
            m10 = -SIN * a00 + COS * a10;
            m11 = -SIN * a01 + COS * a11;
            m12 = -SIN * a02 + COS * a12;
            m13 = -SIN * a03 + COS * a13;
        }
    }

    public void rotate_SLOW(float dx, float dy, float dz) {
        transform(rotateMatrix(dx, dy, dz));
    }

    public void scaleSelf(float dx, float dy, float dz) {
        preTransform(scaleMatrix(dx, dy, dz));
    }

    public void scaleSelf(float d) {
        preTransform(scaleMatrix(d));
    }

    public void rotateSelf(float dx, float dy, float dz) {
        preTransform(rotateMatrix(dx, dy, dz));
    }

    /**
     * Transforms this matrix by matrix n from left (this=n x this).
     */
    public void transform_SLOW(@Nonnull idx3d_Matrix n) {
        idx3d_Matrix m = this.getClone();

        m00 = n.m00 * m.m00 + n.m01 * m.m10 + n.m02 * m.m20;
        m01 = n.m00 * m.m01 + n.m01 * m.m11 + n.m02 * m.m21;
        m02 = n.m00 * m.m02 + n.m01 * m.m12 + n.m02 * m.m22;
        m03 = n.m00 * m.m03 + n.m01 * m.m13 + n.m02 * m.m23 + n.m03;
        m10 = n.m10 * m.m00 + n.m11 * m.m10 + n.m12 * m.m20;
        m11 = n.m10 * m.m01 + n.m11 * m.m11 + n.m12 * m.m21;
        m12 = n.m10 * m.m02 + n.m11 * m.m12 + n.m12 * m.m22;
        m13 = n.m10 * m.m03 + n.m11 * m.m13 + n.m12 * m.m23 + n.m13;
        m20 = n.m20 * m.m00 + n.m21 * m.m10 + n.m22 * m.m20;
        m21 = n.m20 * m.m01 + n.m21 * m.m11 + n.m22 * m.m21;
        m22 = n.m20 * m.m02 + n.m21 * m.m12 + n.m22 * m.m22;
        m23 = n.m20 * m.m03 + n.m21 * m.m13 + n.m22 * m.m23 + n.m23;
    }

    public void transform(@Nonnull idx3d_Matrix n) {
        float a00 = m00, a01 = m01, a02 = m02, a03 = m03;
        float a10 = m10, a11 = m11, a12 = m12, a13 = m13;
        float a20 = m20, a21 = m21, a22 = m22, a23 = m23;
        float a30 = m30, a31 = m31, a32 = m32, a33 = m33;

        m00 = n.m00 * a00 + n.m01 * a10 + n.m02 * a20;
        m01 = n.m00 * a01 + n.m01 * a11 + n.m02 * a21;
        m02 = n.m00 * a02 + n.m01 * a12 + n.m02 * a22;
        m03 = n.m00 * a03 + n.m01 * a13 + n.m02 * a23 + n.m03;
        m10 = n.m10 * a00 + n.m11 * a10 + n.m12 * a20;
        m11 = n.m10 * a01 + n.m11 * a11 + n.m12 * a21;
        m12 = n.m10 * a02 + n.m11 * a12 + n.m12 * a22;
        m13 = n.m10 * a03 + n.m11 * a13 + n.m12 * a23 + n.m13;
        m20 = n.m20 * a00 + n.m21 * a10 + n.m22 * a20;
        m21 = n.m20 * a01 + n.m21 * a11 + n.m22 * a21;
        m22 = n.m20 * a02 + n.m21 * a12 + n.m22 * a22;
        m23 = n.m20 * a03 + n.m21 * a13 + n.m22 * a23 + n.m23;
    }

    public void preTransform(@Nonnull idx3d_Matrix n) // transforms this matrix by matrix n from right (this=this x n)
    {
        idx3d_Matrix m = this.getClone();

        m00 = m.m00 * n.m00 + m.m01 * n.m10 + m.m02 * n.m20;
        m01 = m.m00 * n.m01 + m.m01 * n.m11 + m.m02 * n.m21;
        m02 = m.m00 * n.m02 + m.m01 * n.m12 + m.m02 * n.m22;
        m03 = m.m00 * n.m03 + m.m01 * n.m13 + m.m02 * n.m23 + m.m03;
        m10 = m.m10 * n.m00 + m.m11 * n.m10 + m.m12 * n.m20;
        m11 = m.m10 * n.m01 + m.m11 * n.m11 + m.m12 * n.m21;
        m12 = m.m10 * n.m02 + m.m11 * n.m12 + m.m12 * n.m22;
        m13 = m.m10 * n.m03 + m.m11 * n.m13 + m.m12 * n.m23 + m.m13;
        m20 = m.m20 * n.m00 + m.m21 * n.m10 + m.m22 * n.m20;
        m21 = m.m20 * n.m01 + m.m21 * n.m11 + m.m22 * n.m21;
        m22 = m.m20 * n.m02 + m.m21 * n.m12 + m.m22 * n.m22;
        m23 = m.m20 * n.m03 + m.m21 * n.m13 + m.m22 * n.m23 + m.m23;
    }

    @Nonnull
    public static idx3d_Matrix multiply(@Nonnull idx3d_Matrix m1, @Nonnull idx3d_Matrix m2) // returns m1 x m2
    {
        idx3d_Matrix m = new idx3d_Matrix();

        m.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
        m.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
        m.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
        m.m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03;
        m.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
        m.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
        m.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
        m.m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13;
        m.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
        m.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
        m.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        m.m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23;
        return m;
    }

    // BEGIN PATCH
    /*
     * A function for creating a rotation matrix that rotates a vector called
     * "from" into another vector called "to".
     * Input : from[3], to[3] which both must be *normalized* non-zero vectors
     * Output: mtx[3][3] -- a 3x3 matrix in colum-major form
     * Author: Tomas Moller, 1999
     * As seen at http://lists.apple.com/archives/mac-opengl/2001/Jan/msg00059.html
     */
    public static idx3d_Matrix fromToRotation(@Nonnull idx3d_Vector from, @Nonnull idx3d_Vector to) {
//#define M(row,col) mtx9[col*4+row]
        final float EPSILON = 0.00001f;
        idx3d_Vector v;
        float e, h;
        v = idx3d_Vector.vectorProduct(from, to);
        e = idx3d_Vector.dotProduct(from, to);
        idx3d_Matrix m;
        if (e > 1.0 - EPSILON) /* "from" almost or equal to "to"-vector? */ {
            /* return identity */
            m = new idx3d_Matrix();
            return m;
        } else if (e < -1.0 + EPSILON) /* "from" almost or equal to negated "to"? */ {
            idx3d_Vector up, left;
            float invlen;
            float fxx, fyy, fzz, fxy, fxz, fyz;
            float uxx, uyy, uzz, uxy, uxz, uyz;
            float lxx, lyy, lzz, lxy, lxz, lyz;
            /* left=CROSS(from, (1,0,0)) */
            left = new idx3d_Vector(0.0f, from.z, -from.y);
            if (idx3d_Vector.dotProduct(left, left) < EPSILON) /* was left=CROSS(from,(1,0,0)) a good
            choice? */ {
                /* here we know that left = CROSS(from, (1,0,0)) will be a good
                choice */
                left = new idx3d_Vector(-from.z, 0.0f, from.x);
            }
            /* normalize "left" */
            invlen = (float) (1.0 / Math.sqrt(idx3d_Vector.dotProduct(left, left)));
            left.x *= invlen;
            left.y *= invlen;
            left.z *= invlen;
            up = idx3d_Vector.vectorProduct(left, from);
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
            m = new idx3d_Matrix();
            m.m00 = fxx + uxx + lxx;
            m.m01 = fxy + uxy + lxy;
            m.m02 = fxz + uxz + lxz;
            m.m10 = m.m01;
            m.m11 = fyy + uyy + lyy;
            m.m12 = fyz + uyz + lyz;
            m.m20 = m.m02;
            m.m21 = m.m12;
            m.m22 = fzz + uzz + lzz;
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
            float hvx, hvz, hvxy, hvxz, hvyz;
            h = (float) ((1.0 - e) / idx3d_Vector.dotProduct(v, v));
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            m = new idx3d_Matrix();
            m.m00 = e + hvx * v.x;
            m.m01 = hvxy - v.z;
            m.m02 = hvxz + v.y;
            m.m10 = hvxy + v.z;
            m.m11 = e + h * v.y * v.y;
            m.m12 = hvyz - v.x;
            m.m20 = hvxz - v.y;
            m.m21 = hvyz + v.x;
            m.m22 = e + hvz * v.z;
//#endif
        }
//#undef M
        return m;
    }
// END PATCH
@Nonnull
public String toString() {
    StringBuilder out = new StringBuilder("<Matrix: \r\n");
    out.append(m00 + "," + m01 + "," + m02 + "," + m03 + ",\r\n");
    out.append(m10 + "," + m11 + "," + m12 + "," + m13 + ",\r\n");
    out.append(m20 + "," + m21 + "," + m22 + "," + m23 + ",\r\n");
    out.append(m30 + "," + m31 + "," + m32 + "," + m33 + ">\r\n");
    return out.toString();
}

    @Nonnull
    public idx3d_Matrix getClone() {
        idx3d_Matrix m = new idx3d_Matrix();
        m.m00 = m00;
        m.m01 = m01;
        m.m02 = m02;
        m.m03 = m03;
        m.m10 = m10;
        m.m11 = m11;
        m.m12 = m12;
        m.m13 = m13;
        m.m20 = m20;
        m.m21 = m21;
        m.m22 = m22;
        m.m23 = m23;
        m.m30 = m30;
        m.m31 = m31;
        m.m32 = m32;
        m.m33 = m33;
        return m;
    }

    @Nonnull
    public idx3d_Matrix inverse() // Returns the inverse of this matrix
    // Code generated with MapleV and handoptimized
    {
        idx3d_Matrix m = new idx3d_Matrix();

        float q1 = m12;
        float q6 = m10 * m01;
        float q7 = m10 * m21;
        float q8 = m02;
        float q13 = m20 * m01;
        float q14 = m20 * m11;
        float q21 = m02 * m21;
        float q22 = m03 * m21;
        float q25 = m01 * m12;
        float q26 = m01 * m13;
        float q27 = m02 * m11;
        float q28 = m03 * m11;
        float q29 = m10 * m22;
        float q30 = m10 * m23;
        float q31 = m20 * m12;
        float q32 = m20 * m13;
        float q35 = m00 * m22;
        float q36 = m00 * m23;
        float q37 = m20 * m02;
        float q38 = m20 * m03;
        float q41 = m00 * m12;
        float q42 = m00 * m13;
        float q43 = m10 * m02;
        float q44 = m10 * m03;
        float q45 = m00 * m11;
        float q48 = m00 * m21;
        float q49 = q45 * m22 - q48 * q1 - q6 * m22 + q7 * q8;
        float q50 = q13 * q1 - q14 * q8;
        float q51 = 1 / (q49 + q50);

        m.m00 = (m11 * m22 * m33 - m11 * m23 * m32 - m21 * m12 * m33 + m21 * m13 * m32 + m31 * m12 * m23 - m31 * m13 * m22) * q51;
        m.m01 = -(m01 * m22 * m33 - m01 * m23 * m32 - q21 * m33 + q22 * m32) * q51;
        m.m02 = (q25 * m33 - q26 * m32 - q27 * m33 + q28 * m32) * q51;
        m.m03 = -(q25 * m23 - q26 * m22 - q27 * m23 + q28 * m22 + q21 * m13 - q22 * m12) * q51;
        m.m10 = -(q29 * m33 - q30 * m32 - q31 * m33 + q32 * m32) * q51;
        m.m11 = (q35 * m33 - q36 * m32 - q37 * m33 + q38 * m32) * q51;
        m.m12 = -(q41 * m33 - q42 * m32 - q43 * m33 + q44 * m32) * q51;
        m.m13 = (q41 * m23 - q42 * m22 - q43 * m23 + q44 * m22 + q37 * m13 - q38 * m12) * q51;
        m.m20 = (q7 * m33 - q30 * m31 - q14 * m33 + q32 * m31) * q51;
        m.m21 = -(q48 * m33 - q36 * m31 - q13 * m33 + q38 * m31) * q51;
        m.m22 = (q45 * m33 - q42 * m31 - q6 * m33 + q44 * m31) * q51;
        m.m23 = -(q45 * m23 - q42 * m21 - q6 * m23 + q44 * m21 + q13 * m13 - q38 * m11) * q51;

        return m;
    }

    public void reset() // Resets the matrix
    {
        m00 = 1;
        m01 = 0;
        m02 = 0;
        m03 = 0;
        m10 = 0;
        m11 = 1;
        m12 = 0;
        m13 = 0;
        m20 = 0;
        m21 = 0;
        m22 = 1;
        m23 = 0;
        m30 = 0;
        m31 = 0;
        m32 = 0;
        m33 = 1;
    }
    // P R I V A T E   M E T H O D S
/*
    static int count;
    private Throwable construct = new Throwable();
    public void finalize() {
    System.out.println("finalize idx3d_Matrix "+(count++));
    if (count == 1000) {
    construct.printStackTrace();
    }
    }*/
}