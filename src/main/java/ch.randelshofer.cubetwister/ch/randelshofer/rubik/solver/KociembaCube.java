/* @(#)KociembaCube.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

/**
 * Rubik's Cube class definition
 * with extensions for Kociemba's algorithm
 *
 * This class has been derived from KociCube.cpp and KociCube.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 */
public class KociembaCube extends Cube {
    // Number of possiblities for each axis of the two triples

    /** 3^7 = 2187. */
    public final static int TWISTS = (3*3*3*3*3*3*3);

    /** 2^11 = 2048. */
    public final static int FLIPS = (2*2*2*2*2*2*2*2*2*2*2);

    /** 12 choose 4 = 495. */
    public final static int CHOICES = 495;

    /** 8! = 40320. */
    public final static int CORNER_PERMUTATIONS = (8*7*6*5*4*3*2*1);

    /** 8! = 40320. */
    public final static int NON_MIDDLE_SLICE_EDGE_PERMUTATIONS = (8*7*6*5*4*3*2*1);

    /** 4! = 24 . */
    public final static int MIDDLE_SLICE_EDGE_PERMUTATIONS = (4*3*2*1);

    /** A 12 bit number, 1 bit for each edge. */
    private final static int NUMBER_OF_ENCODED_CHOICE_PERMUTATIONS = 4096;

    /** 12 choose 4 edges. */
    private final static int NUMBER_OF_CHOICE_ORDINALS = 495;

    public KociembaCube() {}

    // Phase 1 triple

    /**
     * Corner orientations (3^7 = 2187).
     * Twist is represented as a trinary (base 3) number
     */
    public int getTwist() {
        int corner;     // The current corner
        int twist = 0;
        for (corner = FIRST_CORNER_CUBIE; corner < LAST_CORNER_CUBIE; corner++) {
            twist = twist*3 + cornerCubieOrientations[corner];
        }
        return twist;
    }
    public void setTwist(int twist) {
        int corner;     // The current corner
        int paritySum = 0;      // For calculating corner parity
        for (corner =  LAST_CORNER_CUBIE-1; corner >= FIRST_CORNER_CUBIE; corner--) {
            paritySum += (cornerCubieOrientations[corner] = twist%3);
            twist /= 3;
        }
        // Derive the flip of the last edge cubie
        //   from the current total corner parity.
        //   (total corner parity must be a multiple of 3)
        // 3-ParitySum%3 is the amount to add to round up
        //   to the next multiple of 3.  Note that in the case
        //   where ParitySum%3 equals zero we want the amount
        //   to be zero, not three.  Therefore we compute
        //   (3-ParitySum%3)%3.
        cornerCubieOrientations[LAST_CORNER_CUBIE] = (3 - paritySum % 3) % 3;
    }

    /**
     * Edge orientations (2^11 = 2048).
     * Flip is represented as a binary number
     */
    public int getFlip() {
        int edge;       // The current edge
        int flip = 0;
        for (edge = FIRST_EDGE_CUBIE; edge < LAST_EDGE_CUBIE; edge++) {
            flip = flip * 2 + edgeCubieOrientations[edge];
        }

        return flip;
    }

    public void setFlip(int flip) {
        int edge;       // The current edge
        int paritySum = 0;      // For calculating edge parity
        for (edge =  LAST_EDGE_CUBIE - 1; edge >= FIRST_EDGE_CUBIE; edge--) {
            paritySum += (edgeCubieOrientations[edge] = flip%2);
            flip /= 2;
        }
        // Derive the flip of the last edge cubie
        //   from the current total edge parity
        //   (total edge parity must be even)
        edgeCubieOrientations[LAST_EDGE_CUBIE] = paritySum % 2;
    }

    /**
     * Four middle slice edge positions (12 choose 4 = 495).
     * Choice of the four middle slice edge positions
     * Note that "choice" is for the permutation of those edge
     * cubicles occupied by middle slice edge cubies, as opposed to
     * a choice of all edge cubies within edge cubicles.
     */
    public int getChoice() {
        int[] choicePermutation = new int[4]; // Permutation of the four middle slice edges
        int edge;               // The current edge
        int i = 0;
        // Scan for middle slice edges to construct the choice permutation vector
        for (edge =  FIRST_EDGE_CUBIE; edge <= LAST_EDGE_CUBIE; edge++) {
            if (isMiddleSliceEdgeCubie(edgeCubiePermutations[edge])) {
                choicePermutation[i++] = edge;
            }
        }
        return choiceOrdinal(choicePermutation);
    }

    public void setChoice(int choice) {
        choicePermutation(choice, edgeCubiePermutations);
//      PrintVector(EdgeCubiePermutations, 12);
        return;
    }

    // Phase 2 triple

    /** Permutation of the 8 corners (8! = 40320). */
    public int getCornerPermutation() {
        return Combinatorials.permutationToOrdinal(cornerCubiePermutations, 0, NUMBER_OF_CORNER_CUBIES);
    }
    public void setCornerPermutation(int ordinal) {
        Combinatorials.ordinalToPermutation(ordinal, cornerCubiePermutations, 0, NUMBER_OF_CORNER_CUBIES, FIRST_CORNER_CUBIE);
    }

    /** Permutation of the 8 non-middle slice edges (8! = 40320). */
    public int getNonMiddleSliceEdgePermutation() {
        return Combinatorials.permutationToOrdinal(edgeCubiePermutations, 0, 8);
    }
    /**
     * Note: None of the non middle slice edge cubies are
     * allowed to be in the middle slice prior to calling
     * this function.  If that is not the case, then you
     * must first call backToHome().
     */
    public void setNonMiddleSliceEdgePermutation(int ordinal) {
        Combinatorials.ordinalToPermutation(ordinal, edgeCubiePermutations, 0, 8, FIRST_EDGE_CUBIE);
    }

    /** Permutation of the 4 middle slice edges (4! = 2. */
    public int getMiddleSliceEdgePermutation() {
        return Combinatorials.permutationToOrdinal(edgeCubiePermutations, FIRST_MIDDLE_SLICE_EDGE_CUBIE, 4);
    }
    /**
     * Note: All of the middle slice edge cubies must be in
     * the middle slice prior to calling prior to calling
     * If that is not the case, then you must first call
     * backToHome().
     */
    public void setMiddleSliceEdgePermutation(int ordinal) {
        Combinatorials.ordinalToPermutation(ordinal, edgeCubiePermutations, FIRST_MIDDLE_SLICE_EDGE_CUBIE, 4, FIRST_MIDDLE_SLICE_EDGE_CUBIE);
    }

    /** Predicate to determine if a cubie is a middle slice edge cubie. */
    private static boolean isMiddleSliceEdgeCubie(int cubie) {
        return cubie >= FIRST_MIDDLE_SLICE_EDGE_CUBIE && cubie <= LAST_MIDDLE_SLICE_EDGE_CUBIE;
    }

    /**
     * Compute the choice ordinal from the choice permutation.
     * <p>
     * The following algorithm implements the approach taken by Herbert Kociemba
     * to map the permutation of the middle slice edges to a unique ordinal within
     * the range (0..494).  This approach does not use much memory as no lookup
     * tables are employed and it is reasonably efficient.
     * <p>
     * ChoiceOrdinal - Compute a unique ordinal for each of the 495
     *   (i.e. 12 Choose 4) possible middle slice edge permutations.
     *   This is simply referred to as "choice".  The ordinal is in
     *   the range (0...494) where 0 corresponds to the [0,1,2,3] edge
     *   permutation and 494 corresponds to the [8,9,10,11] permutation.
     *   The algorithm below is best understood by a simple example.
     * <p>
     *   Consider 6 edges taken 3 at a time.  In lexicographic order,
     *   the permutations are:
     * <pre>
     *    0) 012    5 Choose 2 = 10 possibilites beginning with 0
     *    1) 013            4 Choose 1 = 4 possibilities beginning with 01
     *    2) 014
     *    3) 015
     *    4) 023            3 Choose 1 = 3 possibilities beginning with 02
     *    5) 024
     *    6) 025
     *    7) 034            2 Choose 1 = 2 possibilities beginning with 03
     *    8) 035
     *    9) 045            1 Choose 1 = 1 possibility beginning with 04
     *
     *   10) 123    4 Choose 2 = 6 possibities beginning with 1
     *   11) 124            3 Choose 1 = 3 possibilities beginning with 12
     *   12) 125
     *   13) 134            2 Choose 1 = 2 possibilities beginning with 13
     *   14) 135
     *   15) 145            1 Choose 1 = 1 possibility beginning with 14
     *
     *   16) 234    3 Choose 2 = 3 possibilites beginning with 2
     *   17) 235            2 Choose 1 = 2 possibilities beginning with 23
     *   18) 245            1 Choose 1 = 1 possibility beginning with 24
     *
     *   19) 345    2 Choose 2 = 1 possibility beginning with 3
     * </pre>
     *  Since each permutation is monotonically increasing, it's easy to see
     *  how to determine the number of possibilities for any given permutation
     *  prefix.  All edge permutations to the right of a given edge permutations
     *  must be greater than the given permutation number, so the number of
     *  remaining choices can be reduced accordingly at that point (e.g. if
     *  edge 1 is present then we are limited to choices 2,3,4,5 in the remaining
     *  two positions thus there are 4 choose 2 = 6 permutations beginning with
     *  1.
     * <br>
     *  The edges are first sorted, using a radix sort (see the mark vector
     *  below).  The edges are then scanned in ascending (lexicographic)
     *  order.  If an edge is not present, then the ordinal is increased by
     *  the number of possible permutations for the current edge choices.
     */
    private static int choiceOrdinal(int[] choicePermutation) {
        boolean[] edgeMarkVector = new boolean[NUMBER_OF_EDGE_CUBIES]; // For radix sort of the edges
        int edgesRemaining = 4;         // Counts remaining edges
        int ordinal = 0;                // The choice permutation ordinal
        int edge;                               // The current edge

        // Radix sort the edges
        for (edge = 0; edge < NUMBER_OF_EDGE_CUBIES; edge++) {
            edgeMarkVector[edge] = false;
        }
        for (edge = 0; edge < 4; edge++) {
            edgeMarkVector[choicePermutation[edge]] = true;
        }

        // Scan the edges and compute the ordinal for this permutation
        edge = 0;
        while (edgesRemaining > 0) {
            if (edgeMarkVector[edge++]) {
                edgesRemaining--;       // One edge less to go
            } else {
                // Skip this many permutations
                ordinal += Combinatorials.NChooseM(12-edge, edgesRemaining-1);
            }
        }
        return ordinal;
    }

    /**
     * Compute the choice permutation from the choice ordinal.
     * <p>
     * ChoicePermutation - Given a choice ordinal, compute the associated
     * choice permutation.  Cubicles that are not part of the supplied
     * choice are assigned an invalid cubie.
     * The algorithm is essentially the inverse of the permutation to
     * choice algorithm.
     */
    private static void choicePermutation(int choiceOrdinal, int[] choicePermutation) {
        int edge;               // The current edge
        int digit = 0;          // The currend edge permutation "digit"
        int combinations;       // Number of combinations prefixed with this "digit"

        // All other edges are unknown, so begin by initializing them to "invalid"
        for (edge = 0; edge < NUMBER_OF_EDGE_CUBIES; edge++) {
            choicePermutation[edge] = INVALID_CUBIE;
        }

        // Advance four "digits"
        for (edge = 0; edge < 4; edge++) {
            // This is something like division where we divide by subtracting
            // off the number of combinations possible for the current "digit".
            for (;;) {
                // Initially starting at 0###, so this begins at 11 Choose 3
                //   (0 is eliminated leaving 11 possibilites, and there are
                //    3 unassigned "digits")
                // N decreases each time we advance the "digit"
                // M decreases each time we move one "digit" to the right
                combinations = Combinatorials.NChooseM(12-1-digit++, 4-1-edge);
                if (choiceOrdinal >= combinations) {
                    choiceOrdinal -= combinations;
                } else {
                    break;
                }
            }
            // Since digit is always bumped, must back up by one
            // Assign middle slice edges in ascending order
            choicePermutation[digit-1] = FIRST_MIDDLE_SLICE_EDGE_CUBIE+edge;
        }
    }
}
