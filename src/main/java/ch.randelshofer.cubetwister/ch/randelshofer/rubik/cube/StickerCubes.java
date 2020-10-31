/*
 * @(#)StickerCubes.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.io.StreamPosTokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;


public class StickerCubes {
    private StickerCubes() {
        // prevent instantiation
    }

    /**
     * This is used for mapping side part locations
     * to/from sticker positions on the cube.
     *
     * @see #rubiksCubeToStickers
     */
    protected final static int[][] SIDE_TRANSLATION = {
            {0, 4},
            {1, 4},
            {2, 4},
            {3, 4},
            {4, 4},
            {5, 4}
    };
    /**
     * This is used for mapping edge part locations and orientations
     * to/from sticker positions on the cube.
     * <p>
     * Description:<br>
     * edge orientation 0: face index, sticker index.
     * edge orientation 1: face index, sticker index.
     *
     * @see #rubiksCubeToStickers
     */
    protected final static int[][] EDGE_TRANSLATION = {
            {1, 5, 0, 1}, // edge 0 ur
            {0, 3, 2, 5}, //      1 rf
            {4, 5, 0, 7}, //      2 dr
            {5, 1, 1, 1}, //      3 bu
            {0, 5, 5, 3}, //      4 rb
            {5, 7, 4, 7}, //      5 bd
            {1, 3, 3, 1}, //      6 ul
            {3, 3, 5, 5}, //      7 lb
            {4, 3, 3, 7}, //      8 dl
            {2, 1, 1, 7}, //      9 fu
            {3, 5, 2, 3}, //     10 lf
            {2, 7, 4, 1} //     11 fd
    };
    /**
     * This is used for mapping corner part locations and orientations
     * to/from sticker positions on the cube.
     * <p>
     * Description:<br>
     * corner orientation 0, face index, corner orientation 1, face index, corner orientation 2, face index
     * <p>
     * XXX - Move this into RubiksCube class.
     *
     * @see #rubiksCubeToStickers
     */
    protected final static int[][] CORNER_TRANSLATION = {
            {1, 8, 0, 0, 2, 2}, // 0 urf
            {4, 2, 2, 8, 0, 6}, // 1 dfr
            {1, 2, 5, 0, 0, 2}, // 2 ubr
            {4, 8, 0, 8, 5, 6}, // 3 drb
            {1, 0, 3, 0, 5, 2}, // 4 ulb
            {4, 6, 5, 8, 3, 6}, // 5 dbl
            {1, 6, 2, 0, 3, 2}, // 6 ufl
            {4, 0, 3, 8, 2, 6} // 7 dlf
    };

    /**
     * Returns an array of stickers which reflect the current state of the cube.
     * <p>
     * The following diagram shows the indices of the array. The number before
     * the comma is the first dimension (faces), the number after the comma
     * is the second dimension (stickers).
     * <p>
     * The values of the array elements is the face index: 0..5.
     * <pre>
     *             +---+---+---+
     *             |1,0|1,1|1,2|
     *             +--- --- ---+
     *             |1,3|1,4|1,5|
     *             +--- --- ---+
     *             |1,6|1,7|1,8|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |3,0|3,1|3,2|2,0|2,1|2,2|0,0|0,1|0,2|5,0|5,1|5,2|
     * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
     * |3,3|3,4|3,5|2,3|2,4|2,5|0,3|0,4|0,5|5,3|5,4|5,5|
     * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
     * |3,6|3,7|3,8|2,6|2,7|2,8|0,0|0,1|0,2|5,0|5,1|5,2|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |4,0|4,1|4,2|
     *             +--- --- ---+
     *             |4,3|4,4|4,5|
     *             +--- --- ---+
     *             |4,6|4,7|4,8|
     *             +---+---+---+
     * </pre>
     *
     * @param cube
     * @return A two dimensional array. First dimension: faces.
     * Second dimension: sticker index on the faces.
     */
    @Nonnull
    public static int[][] rubiksCubeToStickers(RubiksCube cube) {
        int[][] stickers = new int[6][9];

        // Map side parts onto stickers.
        for (int i = 0; i < 6; i++) {
            int loc = cube.sideLoc[i];
            stickers[SIDE_TRANSLATION[i][0]][SIDE_TRANSLATION[i][1]] = SIDE_TRANSLATION[loc][0];
        }

        // Map edge parts onto stickers
        for (int i = 0; i < 12; i++) {
            int loc = cube.edgeLoc[i];
            int orient = cube.edgeOrient[i];
            stickers[EDGE_TRANSLATION[i][0]][EDGE_TRANSLATION[i][1]]
                    = (orient == 0) ? EDGE_TRANSLATION[loc][0] : EDGE_TRANSLATION[loc][2];
            stickers[EDGE_TRANSLATION[i][2]][EDGE_TRANSLATION[i][3]]
                    = (orient == 0) ? EDGE_TRANSLATION[loc][2] : EDGE_TRANSLATION[loc][0];
        }

        // Map corner parts onto stickers
        for (int i = 0; i < 8; i++) {
            int loc = cube.cornerLoc[i];
            int orient = cube.cornerOrient[i];
            stickers[CORNER_TRANSLATION[i][0]][CORNER_TRANSLATION[i][1]]
                    = (orient == 0)
                    ? CORNER_TRANSLATION[loc][0]
                    : ((orient == 1)
                    ? CORNER_TRANSLATION[loc][2]
                    : CORNER_TRANSLATION[loc][4]);
            stickers[CORNER_TRANSLATION[i][2]][CORNER_TRANSLATION[i][3]]
                    = (orient == 0)
                    ? CORNER_TRANSLATION[loc][2]
                    : ((orient == 1)
                    ? CORNER_TRANSLATION[loc][4]
                    : CORNER_TRANSLATION[loc][0]);
            stickers[CORNER_TRANSLATION[i][4]][CORNER_TRANSLATION[i][5]]
                    = (orient == 0)
                    ? CORNER_TRANSLATION[loc][4]
                    : ((orient == 1)
                    ? CORNER_TRANSLATION[loc][0]
                    : CORNER_TRANSLATION[loc][2]);
        }

        return stickers;
    }

    /**
     * Sets the cube to a state where the faces of the parts map to the provided
     * stickers array.
     *
     * @param cube     a cube
     * @param stickers An array of dimensions [6][9] containing sticker values
     *                 in the range [0,5] for the six faces right, up, front,
     *                 left, down, back.
     * @see #rubiksCubeToStickers
     */
    public static void setRubksCubeToStickers(RubiksCube cube, int[][] stickers) {
        int i = 0, j = 0, loc;

        int[] tempSideLoc = new int[6];
        int[] tempSideOrient = new int[6];
        int[] tempEdgeLoc = new int[12];
        int[] tempEdgeOrient = new int[12];
        int[] tempCornerLoc = new int[8];
        int[] tempCornerOrient = new int[8];

        // Translate face cubes to match stickers.
        try {
            for (i = 0; i < 6; i++) {
                for (j = 0; j < 6; j++) {
                    if (SIDE_TRANSLATION[j][0] == stickers[i][SIDE_TRANSLATION[j][1]]) {
                        tempSideLoc[i] = SIDE_TRANSLATION[j][0];
                        break;
                    }
                }
                //sideOrient[i] = 0; // already done by reset
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid side cube " + i);
        }

        for (i = 0; i < 5; i++) {
            for (j = i + 1; j < 6; j++) {
                if (tempSideLoc[i] == tempSideLoc[j]) {
                    throw new IllegalArgumentException("Duplicate side cubes " + i + "+" + j);
                }
            }
        }
        // Translate edge cubes to match stickers.
        for (i = 0; i < 12; i++) {
            int f0 = stickers[EDGE_TRANSLATION[i][0]][EDGE_TRANSLATION[i][1]];
            int f1 = stickers[EDGE_TRANSLATION[i][2]][EDGE_TRANSLATION[i][3]];
            for (loc = 0; loc
                    < 12; loc++) {
                if (EDGE_TRANSLATION[loc][0] == f0
                        && EDGE_TRANSLATION[loc][2] == f1) {
                    tempEdgeOrient[i] = 0; //??
                    break;

                } else if (EDGE_TRANSLATION[loc][0] == f1
                        && EDGE_TRANSLATION[loc][2] == f0) {
                    tempEdgeOrient[i] = 1;
                    break;
                }
            }
            if (loc == 12) {
                throw new IllegalArgumentException("Invalid edge cube " + i);
            }

            tempEdgeLoc[i] = loc;
        }

        for (i = 0; i < 11; i++) {
            for (j = i + 1; j < 12; j++) {
                if (tempEdgeLoc[i] == tempEdgeLoc[j]) {
                    throw new IllegalArgumentException(
                            "Duplicate edge cubes tempEdgeLoc[" + i + "]=" + tempEdgeLoc[i] + " tempEdgeLoc[" + j + "]=" + tempEdgeLoc[j]);
                }
            }
        }

        // Translate corner cubes to match stickers.
        for (i = 0; i < 8; i++) {
            int f0 = stickers[CORNER_TRANSLATION[i][0]][CORNER_TRANSLATION[i][1]];
            int f1 = stickers[CORNER_TRANSLATION[i][2]][CORNER_TRANSLATION[i][3]];
            int f2 = stickers[CORNER_TRANSLATION[i][4]][CORNER_TRANSLATION[i][5]];
            for (loc = 0; loc < 8; loc++) {
                if (CORNER_TRANSLATION[loc][0] == f0
                        && CORNER_TRANSLATION[loc][2] == f1
                        && CORNER_TRANSLATION[loc][4] == f2) {
                    tempCornerOrient[i] = 0;
                    break;

                } else if (CORNER_TRANSLATION[loc][0] == f2
                        && CORNER_TRANSLATION[loc][2] == f0
                        && CORNER_TRANSLATION[loc][4] == f1) {
                    tempCornerOrient[i] = 1;
                    break;

                } else if (CORNER_TRANSLATION[loc][0] == f1
                        && CORNER_TRANSLATION[loc][2] == f2
                        && CORNER_TRANSLATION[loc][4] == f0) {
                    tempCornerOrient[i] = 2;
                    break;
                }
            }
            if (loc == 8) {
                throw new IllegalArgumentException("Invalid corner cube " + i);
            }
            tempCornerLoc[i] = loc;
        }

        for (i = 0; i < 7; i++) {
            for (j = i + 1; j < 8; j++) {
                if (tempCornerLoc[i] == tempCornerLoc[j]) {
                    throw new IllegalArgumentException(
                            "Duplicate corner cubes tempCornerLoc[" + i + "]=" + tempCornerLoc[i] + " tempCornerLoc[" + j + "]=" + tempCornerLoc[j]);
                }
            }
        }

        cube.sideLoc = tempSideLoc;
        cube.sideOrient = tempSideOrient;
        cube.edgeLoc = tempEdgeLoc;
        cube.edgeOrient = tempEdgeOrient;
        cube.cornerLoc = tempCornerLoc;
        cube.cornerOrient = tempCornerOrient;

        if (!cube.isQuiet()) {
            cube.fireCubeChanged(new CubeEvent(cube, 0, 0, 0));
        }
    }

    /**
     * Sets the cube to the specified stickers.
     */
    public static void setToStickers(@Nonnull Cube cube, int[] stickers) {
        int[][] perFaceStickers = new int[6][cube.getLayerCount() * cube.getLayerCount()];
        int index = 0;
        for (int face = 0; face < perFaceStickers.length; face++) {
            for (int sticker = 0; sticker < perFaceStickers[face].length; sticker++) {
                perFaceStickers[face][sticker] = stickers[index++];
            }
        }
        StickerCubes.setRubksCubeToStickers(((RubiksCube) cube), perFaceStickers);
    }

    /**
     * Sets the cube to the specified stickers String.
     *
     * <pre>
     * stickersString = faceString{6}
     * faceString     = face, ':', face{9}, '\n'
     * face     = 'U'|'R'|'F'|'L'|'D'|'B'
     * </pre>
     *
     * @param cube           The cube to be set to the stickers string.
     * @param stickersString A string with 6*12 characters.
     * @param faces          A string with 6 characters identifying each face of the cube , e.g. "RUFLDB".
     * @throws java.io.IOException If stickersString has bad syntax
     */
    public static void setToStickersString(@Nonnull Cube cube, @Nonnull String stickersString, @Nonnull String faces) throws IOException {
        int[][] perFaceStickers = new int[6][9];
        StreamPosTokenizer t = new StreamPosTokenizer(new StringReader(stickersString));
        t.resetSyntax();
        t.eolIsSignificant(true);
        while (t.nextToken() != StreamTokenizer.TT_EOF) {
            if (t.ttype < 0) {
                throw new IOException("illegal token " + t.ttype + " at line=" + t.lineno() + ", pos=" + t.getStartPosition());
            }
            int face = faces.indexOf(t.ttype);
            if (face == -1) {
                throw new IOException("illegal face:" + (char) t.ttype + " at line=" + t.lineno() + ", pos=" + t.getStartPosition());
            }
            System.out.print(face + ":");
            if (t.nextToken() != ':') {
                throw new IOException("colon expected:" + (char) t.ttype + " at line=" + t.lineno() + ", pos=" + t.getStartPosition());
            }
            for (int i = 0; i < 9; i++) {
                int sticker = faces.indexOf(t.nextToken());
                if (sticker == -1) {
                    throw new IOException("illegal sticker:" + (char) t.ttype + " at line=" + t.lineno() + ", pos=" + t.getStartPosition());
                }
                perFaceStickers[face][i] = sticker;
                System.out.print(sticker);
            }
            while (t.nextToken() == '\n') {
            }
            System.out.println();
            t.pushBack();
        }
        StickerCubes.setRubksCubeToStickers(((RubiksCube) cube), perFaceStickers);
    }
}
