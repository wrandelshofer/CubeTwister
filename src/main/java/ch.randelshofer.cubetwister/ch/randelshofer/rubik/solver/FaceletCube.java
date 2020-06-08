/*
 * @(#)FaceletCube.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.solver;

import org.jhotdraw.annotation.Nonnull;

/**
 * The FaceletCube represents the cube by the markings
 * of the 54 individual facelets.  The FaceletCube can
 * then be asked to validate the cube to determine if
 * it is in a legal, and thus solvable, configuration.
 * <p>
 * This class has been derived from facecube.cpp and facecube.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 */
public class FaceletCube extends Object {

    // Face names
    public final static int U = 0;
    public final static int D = 1;
    public final static int L = 2;
    public final static int R = 3;
    public final static int F = 4;
    public final static int B = 5;

    /** Used to convert from face names to offsets. */
    private final static String FACE_NAMES = "UDLRFB";

    // Validation return codes
    public final static int VALID = 0;
    public final static int INVALID_MARKER = 1;
    public final static int INVALID_FACELETCOUNT = 2;
    public final static int DUPLICATE_CENTER_MARKING = 3;
    public final static int INVALID_CORNER_MARKINGS = 4;
    public final static int INVALID_CORNER_PARITY = 5;
    public final static int INVALID_EDGE_MARKINGS = 6;
    public final static int INVALID_EDGE_PARITY = 7;
    public final static int INVALID_TOTAL_PARITY = 8;
    public final static int NUMBER_OF_ERRORS = 9;

    // Map each corner facelet to a unique corner number
    private final static int URF = facesToCorner(U,R,F);
    private final static int RFU = facesToCorner(R,F,U);
    private final static int FUR = facesToCorner(F,U,R);

    private final static int UFL = facesToCorner(U,F,L);
    private final static int FLU = facesToCorner(F,L,U);
    private final static int LUF = facesToCorner(L,U,F);

    private final static int ULB = facesToCorner(U,L,B);
    private final static int LBU = facesToCorner(L,B,U);
    private final static int BUL = facesToCorner(B,U,L);

    private final static int UBR = facesToCorner(U,B,R);
    private final static int BRU = facesToCorner(B,R,U);
    private final static int RUB = facesToCorner(R,U,B);

    private final static int DFR = facesToCorner(D,F,R);
    private final static int FRD = facesToCorner(F,R,D);
    private final static int RDF = facesToCorner(R,D,F);

    private final static int DLF = facesToCorner(D,L,F);
    private final static int LFD = facesToCorner(L,F,D);
    private final static int FDL = facesToCorner(F,D,L);

    private final static int DBL = facesToCorner(D,B,L);
    private final static int BLD = facesToCorner(B,L,D);
    private final static int LDB = facesToCorner(L,D,B);

    private final static int DRB = facesToCorner(D,R,B);
    private final static int RBD = facesToCorner(R,B,D);
    private final static int BDR = facesToCorner(B,D,R);

    // Map each edge facelet to a unique edge number
    private final static int UF = facesToEdge(U,F);
    private final static int FU = facesToEdge(F,U);

    private final static int UL = facesToEdge(U,L);
    private final static int LU = facesToEdge(L,U);

    private final static int UB = facesToEdge(U,B);
    private final static int BU = facesToEdge(B,U);

    private final static int UR = facesToEdge(U,R);
    private final static int RU = facesToEdge(R,U);

    private final static int DF = facesToEdge(D,F);
    private final static int FD = facesToEdge(F,D);

    private final static int DL = facesToEdge(D,L);
    private final static int LD = facesToEdge(L,D);

    private final static int DB = facesToEdge(D,B);
    private final static int BD = facesToEdge(B,D);

    private final static int DR = facesToEdge(D,R);
    private final static int RD = facesToEdge(R,D);

    private final static int RF = facesToEdge(R,F);
    private final static int FR = facesToEdge(F,R);

    private final static int LF = facesToEdge(L,F);
    private final static int FL = facesToEdge(F,L);

    private final static int LB = facesToEdge(L,B);
    private final static int BL = facesToEdge(B,L);

    private final static int RB = facesToEdge(R,B);
    private final static int BR = facesToEdge(B,R);

    /** The 9 markings for each of the 6 faces. */
    @Nonnull
    private char[] faceletMarkings = new char[6*9];
    /** Markings mapped to each face. */
    @Nonnull
    private char[] faceMarkings = new char[6+1];

    /**
     * Refer to the diagrams below to determine the array index
     * for any given facelet.
     * <p>
     * Direction key:
     * <pre>
     *      Up
     * Left Front Right Back
     *      Down
     * <pre>
     * Facelet indices:
     * <pre>
     *            0  1  2
     *            3  4  5
     *            6  7  8
     *
     * 18 19 20  36 37 38  27 28 29  45 46 47
     * 21 22 23  39 40 41  30 31 32  48 49 50
     * 24 25 26  42 43 44  33 34 35  51 52 53
     *
     *            9 10 11
     *           12 13 14
     *           15 16 17
     * </pre>
     */
    private final static int[][] CORNER_FACELETS = {
        { 8,  27, 38 },    // URF
        { 6,  36, 20 },    // UFL
        { 0,  18, 47 },    // ULB
        { 2,  45, 29 },    // UBR
        { 11, 44, 33 },    // DFR
        {  9, 26, 42 },    // DLF
        { 15, 53, 24 },    // DBL
        { 17, 35, 51 }     // DRB
    };

    /** Table of valid corner facelet orientations. */
    private final static int[] CORNER_MAP = {
    //   0    1    2  (Twist)
        URF, RFU, FUR,
        UFL, FLU, LUF,
        ULB, LBU, BUL,
        UBR, BRU, RUB,
        DFR, FRD, RDF,
        DLF, LFD, FDL,
        DBL, BLD, LDB,
        DRB, RBD, BDR
    };


    /** Edge facelet locations. */
    private final static int[][] EDGE_FACELETS = {
        {  7, 37 },    // UF
        {  3, 19 },    // UL
        {  1, 46 },    // UB
        {  5, 28 },    // UR
        { 10, 43 },    // DF
        { 12, 25 },    // DL
        { 16, 52 },    // DB
        { 14, 34 },    // DR
        { 30, 41 },    // RF
        { 23, 39 },    // LF
        { 21, 50 },    // LB
        { 32, 48 }     // RB
    };

    /**
     * Table of valid edge facelet orientations.
     * Marked facelet appears first in the left column below
     *   (e.g. U is the the marked facelet of the UF edge, etc.)
     */
    private final static int[] EDGE_MAP = {
    //   0   1  (Flip)
        UF, FU,
        UL, LU,
        UB, BU,
        UR, RU,
        DF, FD,
        DL, LD,
        DB, BD,
        DR, RD,
        RF, FR,
        LF, FL,
        LB, BL,
        RB, BR
    };

    /** The resulting cubie permutation and orientations */
    @Nonnull
    private int[] cornerCubiePermutations = new int[Cube.NUMBER_OF_CORNER_CUBIES];
    @Nonnull
    private int[] cornerCubieOrientations = new int[Cube.NUMBER_OF_CORNER_CUBIES];

    @Nonnull
    private int[] edgeCubiePermutations = new int[Cube.NUMBER_OF_EDGE_CUBIES];
    @Nonnull
    private int[] edgeCubieOrientations = new int[Cube.NUMBER_OF_EDGE_CUBIES];

    /** Error messages. */
    @Nonnull
    private static String[] errorText = {
        "",
        "Facelet marking does not match any center marking",
        "There must be 9 facelets for each marking",
        "Duplicate center marking",
        "Invalid corner markings",
        "Invalid corner orientation parity",
        "Invalid edge markings",
        "Invalid edge orientation parity",
        "Invalid total permutation parity"
    };

    /** Default constructor. */
    public FaceletCube() {
        faceMarkings[6] = '\0';  // Nul terminate
    }

    /** Set the cube markings for a given face. */
    public void setFaceMarkings(int face, char[] markings) {
        int facelet;
        for (facelet = 0; facelet < 9; facelet++) {
            faceletMarkings[face*9+facelet] = markings[facelet];
        }
    }

    /**
     * Validate markings, permutation, and parity.
     */
    public int validate(@Nonnull Cube cube) {
        int status;

        // Must validate centers first!
        if ((status = validateCenters()) != VALID) {
            return status;
        }

        if ((status = validateFacelets()) != VALID) {
            return status;
        }

        if ((status = validateCorners()) != VALID) {
            return status;
        }

        if ((status = validateEdges()) != VALID) {
            return status;
        }

        // Total corner permutation parity must equal
        //   total edge permutation parity
        if (getEdgePermutationParity() != getCornerPermutationParity()) {
            return INVALID_TOTAL_PARITY;
        }

        // Return a properly initialized cube model
        cube.setState(
            cornerCubiePermutations, cornerCubieOrientations,
            edgeCubiePermutations, edgeCubieOrientations
        );

        return status;
    }

    /**
     * Convert face name to enumeration offset.
     *
     * @return  offset or -1 if the face name is invalid.
     */
    public int faceNameToOffset(char faceName) {
        return FACE_NAMES.indexOf(faceName);
    }

    /** Return the text associated with an error return code. */
    public String getErrorText(int error) {
        if (error >= NUMBER_OF_ERRORS) {
            error = 0;
        }
        return errorText[error];
    }

    /** Dump cube state. */
    public void dump() {
        System.out.println(toString());
    }

    @Nonnull
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("FaceletCube(\n  cornerPerm:{");
        for (int i=0; i < Cube.NUMBER_OF_CORNER_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubiePermutations[i]);
        }
        b.append("}\n  cornerOrient:{");
        for (int i=0; i < Cube.NUMBER_OF_CORNER_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubieOrientations[i]);
        }
        b.append("}\n  edgePerm:{");
        for (int i=0; i < Cube.NUMBER_OF_EDGE_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(edgeCubiePermutations[i]);
        }
        b.append("}\n  edgeOrient:{");
        for (int i=0; i < Cube.NUMBER_OF_EDGE_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(edgeCubieOrientations[i]);
        }
        b.append("\n)");
        return b.toString();
    }


    // Validation sub functions
    private int validateCenters() {
        int face;
        int faceName;

        // Initialize face marking
        for (face = 0; face < 6; face++) {
            faceMarkings[face] = '\0';
        }

        // Establish face markings and make sure each face marking
        //   is unique
        for (face = 0; face < 6; face++) {
            for (faceName = 0; faceName < 6; faceName++) {
                if (faceMarkings[faceName] == faceletMarkings[face*9+4]) {
                    return DUPLICATE_CENTER_MARKING;  // Duplicate!
                }
            }
            faceMarkings[face] = faceletMarkings[face*9+4];  // Set another center face
        }
        return VALID;
    }

    private int validateFacelets() {
        int face;
        int facelet;
        int[] facelets = new int[6];

        // Reset facelet count for all faces
        for (face = 0; face < 6; face++) {
            facelets[face] = 0;
        }

        // Validate all 54 facelets
        for (facelet = 0; facelet < 6*9; facelet++) {
            if ((face = faceletOffsetToMarking(facelet)) < 0) {
                return INVALID_MARKER;
            }
            facelets[face]++;
        }

        // Each face must containe exactly 9 facelets
        for (face = 0; face < 6; face++) {
            if (facelets[face] != 9) {
                return INVALID_FACELETCOUNT;
            }
        }

        return VALID;
    }

    private int validateCorners() {
        int cubicle;
        int corner, cornerLocation;
        int cornerParity = 0;
        int cubie;

        // For all corner cubies...
        for (cubicle = 0; cubicle < Cube.NUMBER_OF_CORNER_CUBIES; cubicle++) {
            // Compute corner cubie number directly from its facelets
            cubie = facesToCorner(
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][0]),
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][1]),
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][2])
            );

            cornerLocation = -1;  // Assume corner cubie "not found"

            // Search the table of valid corner facelet orientations
            for (corner = 0; corner < Cube.NUMBER_OF_CORNER_CUBIES*3; corner++) {
                if (cubie == CORNER_MAP[corner]) {
                    // Found a match

                    // Save location
                    cornerLocation = corner;
                    // Permutation corresponds to row offset
                    cornerCubiePermutations[cubicle] = corner/3;
                    // Orientation corresponds to column offset
                    cornerCubieOrientations[cubicle] = corner%3;
                    // Accumulate corner parity sum
                    cornerParity += cornerCubieOrientations[cubicle];
                    break;
                }
            }
            // Error if corner orientation not found
            if (cornerLocation == -1) {
                return INVALID_CORNER_MARKINGS;
            }
        }

        // Total corner orientation parity (COP) must be zero
        if (cornerParity%3 != 0) {
            return INVALID_CORNER_PARITY;
        }

        return VALID;
    }

    private int validateEdges() {
        int cubicle;
        int edge, edgeLocation;
        int edgeParity = 0;
        int cubie;

        // For all edge cubies...
        for (cubicle = 0; cubicle < Cube.NUMBER_OF_EDGE_CUBIES; cubicle++) {
            // Compute edge cubie number directly from its facelets
            cubie = facesToEdge(
                faceletOffsetToMarking(EDGE_FACELETS[cubicle][0]),
                faceletOffsetToMarking(EDGE_FACELETS[cubicle][1])
            );

            edgeLocation = -1;  // Assume edge cubie "not found"

            // Search the table of valid edge facelet orientations
            for (edge = 0; edge < Cube.NUMBER_OF_EDGE_CUBIES*2; edge++) {
                if (cubie == EDGE_MAP[edge]) {
                    // Found a match

                    // Save location
                    edgeLocation = edge;
                    // Permutation corresponds to row offset
                    edgeCubiePermutations[cubicle] = edge/2;
                    // Orientation corresponds to column offset
                    edgeCubieOrientations[cubicle] = edge%2;
                    // Accumulate edge parity sum
                    edgeParity += edgeCubieOrientations[cubicle];
                    break;
                }
            }
            // Error if edge orientation not found
            if (edgeLocation == -1) {
                return INVALID_EDGE_MARKINGS;
            }
        }

        // Total edge orientation parity (EOP) must be zero
        if (edgeParity%2 != 0) {
            return INVALID_EDGE_PARITY;
        }

        return VALID;
    }

    /** Compute total edge permutation parity (EPP). */
    private int getEdgePermutationParity() {
        return getPermutationParity(edgeCubiePermutations, Cube.NUMBER_OF_EDGE_CUBIES);
    }
    /** Compute total corner permutation parity (CPP). */
    private int getCornerPermutationParity() {
        return getPermutationParity(cornerCubiePermutations, Cube.NUMBER_OF_CORNER_CUBIES);
    }

    /**
     * Permutation parity can be computed by counting the number of
     * reversals in the permutation sequence, - i.e., the number
     * of pairs (p,q) such that p>q and p precedes q.  Then
     * determine if the result is even or odd.  Do this for both
     * edges (EPP) and corners (CPP). A configuration is reachable
     * if EPP=CPP.  (August/September cube.lovers - Vanderschel/Saxe)
     */
    private int getPermutationParity(int[] permutations, int numberOfCubies) {
        int p, q;
        int permutationParity = 0;

        for (p = 0; p < numberOfCubies-1; p++) {
            for (q = p+1; q < numberOfCubies; q++) {
                if (permutations[p] > permutations[q]) {
                    permutationParity++;
                }
            }
        }
        return permutationParity % 2;
    }

    private int faceletOffsetToMarking(int offset) {
        char c = faceletMarkings[offset];
        int i;
        for (i = faceMarkings.length - 2; i >= 0; i--) {
            if (faceMarkings[i] == c) {
                break;
            }
        }
        return i;
/*  char* name;
  if (!(name = strchr(faceMarkings, faceletMarkings[offset])))
    return -1;
  return name-faceMarkings;  // Compute face enumeration
  */
    }


    /**
     * Constructs a unique number
     * for each corner cubie from its facelets.
     */
    private static int facesToCorner(int face1, int face2, int face3) {
        return ((face1*6)+face2)*6+face3;
    }
    /**
     * Constructs a unique number
     * for each edge cubie from its facelets.
     */
    private static int facesToEdge(int face1, int face2) {
        return (face1*6+face2);
    }
}