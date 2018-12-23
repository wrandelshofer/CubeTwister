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

import java.util.logging.Level;
import java.util.logging.Logger;

/** defines a 3d vector. */
public class idx3d_Vector implements Cloneable {
    // F I E L D S

    public float x = 0;      //Cartesian (default)
    public float y = 0;      //Cartesian (default)
    public float z = 0;      //Cartesian (default),Cylindric
    public float r = 0;      //Cylindric
    public float theta = 0;  //Cylindric


    // C O N S T R U C T O R S
    public idx3d_Vector() {
    }

    public idx3d_Vector(float xpos, float ypos, float zpos) {
        x = xpos;
        y = ypos;
        z = zpos;
    }

    // P U B L I C   M E T H O D S
    public void setTo(float xpos, float ypos, float zpos) {
        x = xpos;
        y = ypos;
        z = zpos;
        r = theta = 0;
    }

    /** Normalizes the vector. */
    public idx3d_Vector normalize() {
        float dist = length();
        if (dist == 0) {
            return this;
        }
        float invdist = 1 / dist;
        x *= invdist;
        y *= invdist;
        z *= invdist;
        return this;
    }

    /** Reverses the vector. */
    public idx3d_Vector reverse() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    /** Length of this vector. */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /** Modifies the vector by matrix m. */
    public idx3d_Vector transform(idx3d_Matrix m) {
        float newx = x * m.m00 + y * m.m01 + z * m.m02 + m.m03;
        float newy = x * m.m10 + y * m.m11 + z * m.m12 + m.m13;
        float newz = x * m.m20 + y * m.m21 + z * m.m22 + m.m23;
        return new idx3d_Vector(newx, newy, newz);
    }

    /** Modifies the vector by matrix m. */
    public idx3d_Vector transformInto(idx3d_Matrix m, idx3d_Vector resultVector) {
        float newx = x * m.m00 + y * m.m01 + z * m.m02 + m.m03;
        float newy = x * m.m10 + y * m.m11 + z * m.m12 + m.m13;
        float newz = x * m.m20 + y * m.m21 + z * m.m22 + m.m23;
        resultVector.setTo(newx, newy, newz);
        return resultVector;
    }

    /** Modifies the vector by matrix m. */
    public float transformIntoW(idx3d_Matrix m, idx3d_Vector resultVector) {
        float newx = x * m.m00 + y * m.m01 + z * m.m02 + m.m03;
        float newy = x * m.m10 + y * m.m11 + z * m.m12 + m.m13;
        float newz = x * m.m20 + y * m.m21 + z * m.m22 + m.m23;
        float neww = x * m.m30 + y * m.m31 + z * m.m32 + m.m33;
        resultVector.setTo(newx, newy, newz);
        return neww;
    }

    /** Builds the cylindric coordinates out of the given cartesian coordinates. */
    public void buildCylindric() {
        r = (float) Math.sqrt(x * x + y * y);
        theta = (float) Math.atan2(x, y);
    }

    /** Builds the cartesian coordinates out of the given cylindric coordinates. */
    public void buildCartesian() {
        x = r * idx3d_Math.cos(theta);
        y = r * idx3d_Math.sin(theta);
    }

    /** returns the normal vector of the plane defined by the two vectors. */
    public static idx3d_Vector getNormal(idx3d_Vector a, idx3d_Vector b) {
        return vectorProduct(a, b).normalize();
    }

    /** returns the normal vector of the plane defined by the two vectors. */
    public static idx3d_Vector getNormal(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c) {
        return vectorProduct(a, b, c).normalize();
    }

    /** returns the normal vector of the plane defined by the two vectors. */
    public static idx3d_Vector getNormalInto(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c, idx3d_Vector resultVector) {
        return vectorProductInto(a, b, c, resultVector).normalize();
    }

    /** returns a x b. */
    public static idx3d_Vector vectorProduct(idx3d_Vector a, idx3d_Vector b) {
        return new idx3d_Vector(a.y * b.z - b.y * a.z, a.z * b.x - b.z * a.x, a.x * b.y - b.x * a.y);
    }

    /** returns a x b. */
    public static idx3d_Vector vectorProductInto(idx3d_Vector a, idx3d_Vector b, idx3d_Vector resultVector) {
        resultVector.setTo(
                a.y * b.z - b.y * a.z,
                a.z * b.x - b.z * a.x,
                a.x * b.y - b.x * a.y);
        return resultVector;
    }

    /** returns (b-a) x (c-a). */
    public static idx3d_Vector vectorProduct(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c) {
        //	return vectorProduct(sub(b,a),sub(c,a));
        return vectorProductInto(a, b, c, new idx3d_Vector());
    }

    /** returns (b-a) x (c-a). */
    public static idx3d_Vector vectorProductInto(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c, idx3d_Vector resultVector) {
        float bax = b.x - a.x;
        float bay = b.y - a.y;
        float baz = b.z - a.z;
        float cax = c.x - a.x;
        float cay = c.y - a.y;
        float caz = c.z - a.z;

        resultVector.x = bay * caz - cay * baz;
        resultVector.y = baz * cax - caz * bax;
        resultVector.z = bax * cay - cax * bay;

        return resultVector;
    //return vectorProductInto(sub(b,a),sub(c,a), resultVector);
    }

    /** returns the angle between 2 vectors. */
    public static float angle(idx3d_Vector a, idx3d_Vector b) {
        a.normalize();
        b.normalize();
        return (a.x * b.x + a.y * b.y + a.z * b.z);
    }

    /** adds 2 vectors. */
    public static idx3d_Vector add(idx3d_Vector a, idx3d_Vector b) {
        return new idx3d_Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /** subtracts 2 vectors. */
    public static idx3d_Vector sub(idx3d_Vector a, idx3d_Vector b) {
        return new idx3d_Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /** dotProduct product of 2 vectors. */
    public static float dotProduct(idx3d_Vector a, idx3d_Vector b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /** substracts 2 vectors. */
    public static idx3d_Vector scale(float f, idx3d_Vector a) {
        return new idx3d_Vector(f * a.x, f * a.y, f * a.z);
    }

    /** length of vector. */
    public static float len(idx3d_Vector a) {
        return (float) Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
    }

    /** returns a random vector. */
    public static idx3d_Vector random(float fact) {
        return new idx3d_Vector(fact * idx3d_Math.random(), fact * idx3d_Math.random(), fact * idx3d_Math.random());
    }
    /**
     * Computes the plane equation a*x + b*y + c*z + d = 0 from three given
     * points in space.
     *
     * @param v1
     * @param v2
     * @param v3
     */
    public static float[] planeEquation(idx3d_Vector v1, idx3d_Vector v2, idx3d_Vector v3) {
        float x1 = v1.x;
        float x2 = v2.x;
        float x3 = v3.x;
        float y1 = v1.y;
        float y2 = v2.y;
        float y3 = v3.y;
        float z1 = v1.z;
        float z2 = v2.z;
        float z3 = v3.z;

        float a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
        float b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
        float c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        float d = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1));
        return new float[]{a, b, c, d};
    }

    public String toString() {
        return new String("<vector x=" + x + " y=" + y + " z=" + z + ">\r\n");
    }

    public idx3d_Vector getClone() {
        return new idx3d_Vector(x, y, z);
    }

    public boolean equals(Object o) {
        if (o instanceof idx3d_Vector) {
            return equals((idx3d_Vector) o);
        } else {
            return false;
        }
    }

    public boolean equals(idx3d_Vector that) {
        return this.x - that.x < 0.001 &&
                this.y - that.y < 0.001 &&
                this.z - that.z < 0.001;
    }

    public int hashCode() {
        return Float.floatToIntBits(x * y * z);
    }
    /*
    static int count;
    private Throwable construct = new Throwable();
    public void finalize() {
    System.out.println("finalize idx3d_Vector "+(count++));
    if (count == 2000) {
    construct.printStackTrace();
    }
    }*/

    @Override
    public idx3d_Vector clone() {
        try {
            return (idx3d_Vector) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError err = new InternalError();
            err.initCause(ex);
            throw err;
        }
    }
}