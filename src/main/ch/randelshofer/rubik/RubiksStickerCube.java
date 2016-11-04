/*
 * @(#)RubiksStickerCube.java  1.0  January 14, 2007
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 *
 * This class has been derived from facecube.cpp and facecube.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 */

package ch.randelshofer.rubik;

/**
 * The RubiksStickerCube represents a Rubik's Cube by the markings
 * of its 54 individual stickers.  The RubiksStickerCube can
 * then be asked to validate the cube to determine if
 * it is in a legal, and thus solvable, configuration.
 * <p>
 * The stickers are expressed by the values 0 through 6 representing the
 * six faces of the cube: front, right, down, back, left, up.
 * The value -1 is used to express an unknown sticker value. The 
 * RubiksStickerCube can fill in values for unknown stickers.
 * <p>
 * This class has been derived from facecube.cpp and facecube.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 14, 2007 Created.
 */
public class RubiksStickerCube {

    // Face names
    public final static int F = 0;
    public final static int R = 1;
    public final static int D = 2;
    public final static int B = 3;
    public final static int L = 4;
    public final static int U = 5;
    
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
    private int[] stickers = new int[6*9];
    /** Markings mapped to each face. */
    private int[] faces = new int[6];

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
     * Sticker indices:
     * <pre>
     *                 +---+---+---+
     *                 | 45| 46| 47|
     *                 +---+---+---+
     *                 | 48| 49| 50|
     *                 +---+---+---+
     *                 | 51| 52| 53|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 36| 37| 38| 0 | 1 | 2 | 9 | 10| 11| 27| 28| 29|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 39| 40| 41| 3 | 4 | 5 | 12| 13| 14| 30| 31| 32|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 42| 43| 44| 6 | 7 | 8 | 15| 16| 17| 33| 34| 35|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *                 | 18| 19| 20|
     *                 +---+---+---+
     *                 | 21| 22| 23|
     *                 +---+---+---+
     *                 | 24| 25| 26|
     *                 +---+---+---+
     * </pre>
     */
    private final static int[][] CORNER_FACELETS = {
        {53,  9,  2 },    // URF
        {51,  0, 38 },    // UFL
        {45, 36, 29 },    // ULB
        {47, 27, 11 },    // UBR
        {20,  8, 15 },    // DFR
        {18, 44,  6 },    // DLF
        {24, 35, 42 },    // DBL
        {26, 17, 33 }     // DRB
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


    /** Edge sticker locations. 
     * <pre>
     *                 +---+---+---+
     *                 | 45| 46| 47|
     *                 +---+---+---+
     *                 | 48| 49| 50|
     *                 +---+---+---+
     *                 | 51| 52| 53|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 36| 37| 38| 0 | 1 | 2 | 9 | 10| 11| 27| 28| 29|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 39| 40| 41| 3 | 4 | 5 | 12| 13| 14| 30| 31| 32|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 42| 43| 44| 6 | 7 | 8 | 15| 16| 17| 33| 34| 35|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *                 | 18| 19| 20|
     *                 +---+---+---+
     *                 | 21| 22| 23|
     *                 +---+---+---+
     *                 | 24| 25| 26|
     *                 +---+---+---+
     * </pre>
     */
    private final static int[][] EDGE_FACELETS = {
        { 52,  1 },    // UF
        { 48, 37 },    // UL
        { 46, 28 },    // UB
        { 50, 10 },    // UR
        { 19,  7 },    // DF
        { 21, 43 },    // DL
        { 25, 34 },    // DB
        { 23, 16 },    // DR
        { 12,  5 },    // RF
        { 41,  3 },    // LF
        { 39, 32 },    // LB
        { 14, 30 }     // RB
    };

    /**
     * Table of valid edge sticker orientations.
     * Marked stickers appears first in the left column below
     *   (e.g. U is the the marked sticker of the UF edge, etc.)
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
    private int[] cornerCubiePermutations = new int[RubiksCube.NUMBER_OF_CORNER_PARTS];
    private int[] cornerCubieOrientations = new int[RubiksCube.NUMBER_OF_CORNER_PARTS];

    private int[] edgeCubiePermutations = new int[RubiksCube.NUMBER_OF_EDGE_PARTS];
    private int[] edgeCubieOrientations = new int[RubiksCube.NUMBER_OF_EDGE_PARTS];

    /** Error messages. */
    private static String[] errorText = {
        "",
        "Sticker marking does not match any center marking",
        "There must be 9 stickers for each marking",
        "Duplicate center marking",
        "Invalid corner markings",
        "Invalid corner orientation parity",
        "Invalid edge markings",
        "Invalid edge orientation parity",
        "Invalid total permutation parity"
    };

    /** Default constructor. */
    public RubiksStickerCube() {
    }

    public void setStickers(int[] stickers) {
        System.arraycopy(stickers, 0, this.stickers, 0, this.stickers.length);
    }
    

    /** Validate markings, permutation, and parity. */
    public int validate(Cube cube) {
        int status;

        // Must validate centers first!
        if ((status = validateCenters()) != VALID) {
            return status;
        }

        if ((status = validateStickers()) != VALID) {
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
        cube.setCorners(
            cornerCubiePermutations, cornerCubieOrientations
        );
        cube.setEdges(
            edgeCubiePermutations, edgeCubieOrientations
        );
        
        int[] sidePartsLocations = new int[6];
        for (int i=0; i < sidePartsLocations.length; i++) {
            sidePartsLocations[i] = i;
        }
        int[] sidePartsOrientations = new int[6];
        cube.setSides(
            sidePartsLocations, edgeCubieOrientations
        );

        return status;
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
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("FaceletCube(\n  cornerPerm:{");
        for (int i=0; i < RubiksCube.NUMBER_OF_CORNER_PARTS; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubiePermutations[i]);
        }
        b.append("}\n  cornerOrient:{");
        for (int i=0; i < RubiksCube.NUMBER_OF_CORNER_PARTS; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubieOrientations[i]);
        }
        b.append("}\n  edgePerm:{");
        for (int i=0; i < RubiksCube.NUMBER_OF_EDGE_PARTS; i++) {
            if (i > 0) { b.append(','); }
            b.append(edgeCubiePermutations[i]);
        }
        b.append("}\n  edgeOrient:{");
        for (int i=0; i < RubiksCube.NUMBER_OF_EDGE_PARTS; i++) {
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
            faces[face] = -1;
        }

        // Establish face markings and make sure each face marking
        //   is unique
        for (face = 0; face < 6; face++) {
            for (faceName = 0; faceName < 6; faceName++) {
                if (faces[faceName] == stickers[face*9+4]) {
                    return DUPLICATE_CENTER_MARKING;  // Duplicate!
                }
            }
            faces[face] = stickers[face*9+4];  // Set another center face
        }
        return VALID;
    }

    private int validateStickers() {
        int[] faceCount = new int[6];

        // Reset facelet count for all faces
        for (int i = 0; i < 6; i++) {
            faceCount[i] = 0;
        }

        // Validate all 54 stickers
        int face;
        for (int i = 0; i < 6*9; i++) {
            if ((face = faceletOffsetToMarking(i)) < 0) {
                return INVALID_MARKER;
            }
            faceCount[face]++;
        }

        // Each face must containe exactly 9 facelets
        for (int i = 0; i < 6; i++) {
            if (faceCount[i] != 9) {
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
        for (cubicle = 0; cubicle < RubiksCube.NUMBER_OF_CORNER_PARTS; cubicle++) {
            // Compute corner cubie number directly from its facelets
            cubie = facesToCorner(
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][0]),
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][1]),
                faceletOffsetToMarking(CORNER_FACELETS[cubicle][2])
            );

            cornerLocation = -1;  // Assume corner cubie "not found"

            // Search the table of valid corner facelet orientations
            for (corner = 0; corner < RubiksCube.NUMBER_OF_CORNER_PARTS*3; corner++) {
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
                System.out.println("RubiksStickerCube: Invalid corner marking for cubicle:"+cubicle+" cubie:"+cubie+" URF:"+URF);
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
        for (cubicle = 0; cubicle < RubiksCube.NUMBER_OF_EDGE_PARTS; cubicle++) {
            // Compute edge cubie number directly from its facelets
            cubie = facesToEdge(
                faceletOffsetToMarking(EDGE_FACELETS[cubicle][0]),
                faceletOffsetToMarking(EDGE_FACELETS[cubicle][1])
            );

            edgeLocation = -1;  // Assume edge cubie "not found"

            // Search the table of valid edge facelet orientations
            for (edge = 0; edge < RubiksCube.NUMBER_OF_EDGE_PARTS*2; edge++) {
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
        return getPermutationParity(edgeCubiePermutations, RubiksCube.NUMBER_OF_EDGE_PARTS);
    }
    /** Compute total corner permutation parity (CPP). */
    private int getCornerPermutationParity() {
        return getPermutationParity(cornerCubiePermutations, RubiksCube.NUMBER_OF_CORNER_PARTS);
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
        return stickers[offset];
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
