/* @(#)IntMath.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.math;

/**
 * Utility class for integer arithmetic.
 * 
 * @author Werner Randelshofer
 */
public class IntMath {
    
    /** Creates a new instance of IntMath */
    public IntMath() {
    }

    /**
     * Returns an int whose value is the greatest common divisor of
     * <tt>abs(a)</tt> and <tt>abs(b)</tt>.  Returns 0 if
     * <tt>a==0 &amp;&amp; b==0</tt>.
     *
     * @param  a value with with the GCD is to be computed.
     * @param  b value with with the GCD is to be computed.
     * @return <tt>GCD(a, b)</tt>
     */
    public static int gcd(int a, int b) {
        // Quelle:
        //   Herrmann, D. (1992). Algorithmen Arbeitsbuch. 
        //   Bonn, München Paris: Addison Wesley.
        //   ggt6, Seite 63
        
        a = Math.abs(a);
        b = Math.abs(b);
        
        while (a > 0 && b > 0) {
            a = a % b;
            if (a > 0) b = b % a;
        }
        return a + b;
    }
    /**
     * Returns a long whose value is the greatest common divisor of
     * <tt>abs(a)</tt> and <tt>abs(b)</tt>.  Returns 0 if
     * <tt>a==0 &amp;&amp; b==0</tt>.
     *
     * @param  a value with with the GCD is to be computed.
     * @param  b value with with the GCD is to be computed.
     * @return <tt>GCD(a, b)</tt>
     */
    public static long gcd(long a, long b) {
        // Quelle:
        //   Herrmann, D. (1992). Algorithmen Arbeitsbuch.
        //   Bonn, München Paris: Addison Wesley.
        //   ggt6, Seite 63

        a = Math.abs(a);
        b = Math.abs(b);

        while (a > 0 && b > 0) {
            a = a % b;
            if (a > 0) b = b % a;
        }
        return a + b;
    }
    
    /**
     * Returns an int whose value is the smallest common multiple of
     * <tt>abs(a)</tt> and <tt>abs(b)</tt>.  Returns 0 if
     * <tt>a==0 || b==0</tt>.
     *
     * @param  a value with with the SCM is to be computed.
     * @param  b value with with the SCM is to be computed.
     * @return <tt>SCM(a, b)</tt>
     */
    public static int scm(int a, int b) {
        // Quelle:
        //   Herrmann, D. (1992). Algorithmen Arbeitsbuch. 
        //   Bonn, M�nchen Paris: Addison Wesley.
        //   gill, Seite 141

        if (a == 0 || b == 0) return 0;
                
        a = Math.abs(a);
        b = Math.abs(b);

        int u = a;
        int v = b;
        
        while (a != b) {
            if (a < b) {
                b -= a;
                v += u;
            } else {
                a -= b;
                u += v;
            }
        }
        
        
        //return a; // gcd
        return (u + v) / 2; // scm
    }
    
    /**
     * Reverses all 32 bits of the provided integer value.
     */
    public static int reverseBits(int a) {
        return reverseBits(a, 32);
    }
    /**
     * Reverses specified number of bits of the provided integer value.
     * @param a The number.
     * @param numBits The number of bits (must be between 1 and 32).
     */
    public static int reverseBits(int a, int numBits) {
        int b = 0;
        for (int i=0; i < numBits; i++) {
            b <<= 1;
            b |= (a & 1);
            a >>>= 1;
        }
        return b;
        
    }
    
    public static void main(String[] args) {
        for (int i=0; i < 8; i++) {
            int a = 1<<i;
            int b = reverseBits(a, 3);
            System.out.println(a+" - "+b);
        }
    }
}
