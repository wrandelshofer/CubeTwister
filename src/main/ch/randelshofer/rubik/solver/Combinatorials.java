/*
 * @(#)Cube.java  0.0  2000-07-01
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

/**
 * Combinatorial algorithms.
 *
 * This class has been derived from Combinat.cpp and Combinat.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Combinatorials {
    /**
     * N Choose M - Compute the number of ways a subset of N items can
     * be selected from a set of M items.  N must be greater than M.
     * The formula for N choose M is:
     *   N! / (M! * (N-M)!)
     *
     * The N! / (N-M)! portion can be computed iteratively as:
     *   N * (N-1) * (N-2) * (N-3) * ... * (N-M+1)
     *
     * The M! portion can be divided out iteratively as:
     *   1, 2, 3 ... M
     * (i.e. first divide partial result by 1, then by 2, then by 3,...)
     *
     * Note that both require M iterations allowing the entire
     * calculation to be performed within a single loop.  Note also
     * that we want to keep the numerator as large as possible during
     * this calculation to avoid truncation error during the division.
     * This is accomplished by performing the multiplication in
     * descending order and the division in ascending order.
     *
     * Since N choose M is equivalent to N choose (N-M) we can take
     * advantage of this property to optimize the calculation in
     * cases where M &gt; N/2 since using the equivalent form results
     * in a smaller M recalling that M is the number of loop iterations.
     *
     * Note, the performance of this function could be improved by
     * using a table of precomputed results (i.e. NChooseM[N][M]).
     */
     public static int NChooseM(int N, int M) {
        int NoverMfact = N;     // Iterates from N down to M+1 to
                                //   compute N! / (N-M)!
        int Mfact = 1;          // Iterates from 1 to M to divide
                                //   out the M! term
        int result = 1;         // Holds the result of N choose M
        if (N < M) return 0;    // M must be a subset of M
        if (M > N/2) M = N-M;   // Optimization
        while (NoverMfact > M)
        {
                result *= NoverMfact--; // Work on the N! / (N-M)! part
                result /= Mfact++;      // Divide out the M! part
        }
        return result;
    }

    /**
     * The following pair of permutation algorithms are based on a
     * description in Knuth's "Fundamental Algorithms Volume 2:
     * Seminumerical Algorithms" p64.
     *
     * PermutationToOrdinal - Given a permutation contained in a
     *  vector of length n, compute a unique ordinal in the range
     * (0,...n!-1).
     *
     * This algorithm is based on the notion of a "factorial number
     * system".  An ordinal can be thought of as a number with a
     * variable base, the sum of factorial terms, where each term
     * is the product of a factorial and an associated coefficent.
     * It generates the coefficients by finding the position of the
     * largest, second largest, third largest, etc. elements in
     * the permutation vector.  Each time it finds the ith largest
     * element, it exchanges that with the element at location i.
     * Thus there are i possibilities for the position of the ith
     * largest element. This process yields the i coefficients.
     *
     */
    public static int permutationToOrdinal(int[] vector, int off, int len) {
        int ordinal = 0;
        int[] v;
        int limit;
        int i;
        int coeffI = 0;
        int temp;

        // Make a copy of the permutation vector
        v = new int[len];
        System.arraycopy(vector, off, v, 0, len);

        for (limit = len - 1; limit > 0; limit--) {
            // Find the maximum up to the current limit
            temp = -1;
            for (i = 0; i <= limit; i++) {
                if (v[i] > temp) {
                    temp = v[i];
                    coeffI = i;
                }
            }
            // Accumulate result
            ordinal = ordinal*(limit+1)+coeffI;

            // Exchange elements
            temp       = v[limit];
            v[limit]   = v[coeffI];
            v[coeffI] = temp;
        }
        return ordinal;
    }

    /**
     * OrdinalToPermutation - Given an ordinal in the range
     * (0,...n!-1) compute a unique permutation of n items.
     *
     * This algorithm is essentially the above algorithm run
     * backwards.  It uses modulo arithmetic and division to
     * produce the coefficients that drive the exchanges.
     */
    public static void ordinalToPermutation(int ordinal, int[] vector, int off, int len, int offset) {
        int i;
        int coeffI;
        int temp;

        // Construct an inital permutation
        for (i = 0; i < len; i++) {
                vector[i+off] = i+offset;
        }

        for (i = 1; i < len; i++) {
            // Compute the coefficent
            coeffI   = ordinal % (i+1);
            // Divide out current "factorial number base"
            ordinal   /= (i+1);

            // Exchange elements
            temp           = vector[i+off];
            vector[i+off]      = vector[coeffI+off];
            vector[coeffI+off] = temp;
        }
    }
}


