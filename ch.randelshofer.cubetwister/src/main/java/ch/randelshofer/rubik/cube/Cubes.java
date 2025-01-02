/*
 * @(#)Cubes.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube;

import ch.randelshofer.math.IntMath;
import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides static utility methods for Cube objects.
 *
 * @author Werner Randelshofer
 */
public class Cubes {

    /**
     * Creates a cube with the specified layer count.
     */
    @Nonnull
    public static Cube create(int layerCount) {
        String n;
        switch (layerCount) {
        case 2:
            n = "ch.randelshofer.rubik.PocketCube";
            break;
        case 3:
            n = "ch.randelshofer.rubik.RubiksCube";
            break;
        case 4:
            n = "ch.randelshofer.rubik.RevengeCube";
            break;
        case 5:
            n = "ch.randelshofer.rubik.ProfessorCube";
            break;
        case 6:
            n = "ch.randelshofer.rubik.Cube6";
            break;
        case 7:
            n = "ch.randelshofer.rubik.Cube7";
            break;
        default:
            throw new IllegalArgumentException("Unsupported layer count " + layerCount);
        }
        try {
            return (Cube) Class.forName(n).getConstructor().newInstance();
        } catch (Exception ex) {
            InternalError e = new InternalError("Couldn't create cube " + n);
            e.initCause(ex);
            throw e;
        }
    }

    @Nonnull
    public static String toVisualPermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {

        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toVisualPermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toVisualPermutationString(cube);
        }
    }

    @Nonnull
    private static String toVisualPermutationString(@Nonnull Cube cube,
                                                    Syntax syntax,
                                                    String tR, String tU, String tF,
                                                    String tL, String tD, String tB,
                                                    String tPlus, String tPlusPlus, String tMinus,
                                                    String tBegin, String tEnd, String tDelimiter) {

        StringBuilder buf = new StringBuilder();

        String corners = toCornerPermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);
        String edges = toEdgePermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);
        String sides = toVisualSidePermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);

        buf.append(corners);
        if (buf.length() > 0 && edges.length() > 0) {
            buf.append('\n');
        }
        buf.append(edges);
        if (buf.length() > 0 && sides.length() > 0) {
            buf.append('\n');
        }
        buf.append(sides);
        if (buf.length() == 0) {
            buf.append(tBegin);
            buf.append(tEnd);
        }
        return buf.toString();
    }

    /**
     * Prevent instance creation.
     */
    private Cubes() {
    }

    /**
     * Returns a number that describes the order
     * of the permutation of the supplied cube.
     * <p>
     * The order says how many times the permutation
     * has to be applied to the cube to get back to the
     * initial state.
     *
     * @param cube A cube
     * @return the order of the permutation of the cube
     */
    public static int getOrder(@Nonnull Cube cube) {
        int[] cornerLoc = cube.getCornerLocations();
        int[] cornerOrient = cube.getCornerOrientations();
        int[] edgeLoc = cube.getEdgeLocations();
        int[] edgeOrient = cube.getEdgeOrientations();
        int[] sideLoc = cube.getSideLocations();
        int[] sideOrient = cube.getSideOrientations();

        int order = 1;

        boolean[] visitedLocs;
        int i, j, k, n, p;
        int prevOrient;
        int length;

        // determine cycle lengths of the current corner permutation
        // and compute smallest common multiple
        visitedLocs = new boolean[cornerLoc.length];

        for (i = 0; i < 8; i++) {
            if (!visitedLocs[i]) {
                if (cornerLoc[i] == i && cornerOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                prevOrient = 0;

                for (j = 0; cornerLoc[j] != i; j++) {
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;
                    prevOrient = (prevOrient + cornerOrient[j]) % 3;

                    length++;

                    for (k = 0; cornerLoc[k] != j; k++) {
                    }
                    j = k;
                }

                prevOrient = (prevOrient + cornerOrient[i]) % 3;
                if (prevOrient != 0) {
                    length *= 3;
                }
                order = IntMath.scm(order, length);
            }
        }

        // determine cycle lengths of the current edge permutation
        // and compute the smallest common multiple
        visitedLocs = new boolean[edgeLoc.length];
        for (i = 0, n = edgeLoc.length; i < n; i++) {
            if (!visitedLocs[i]) {
                if (edgeLoc[i] == i && edgeOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                prevOrient = 0;

                for (j = 0; edgeLoc[j] != i; j++) {
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;
                    prevOrient ^= edgeOrient[j];

                    length++;

                    for (k = 0; edgeLoc[k] != j; k++) {
                    }
                    j = k;
                }

                if ((prevOrient ^ edgeOrient[i]) == 1) {
                    //order = IntMath.scm(order, 2);
                    length *= 2;
                }
                order = IntMath.scm(order, length);
            }
        }

        // determine cycle lengths of the current side permutation
        // and compute smallest common multiple
        visitedLocs = new boolean[sideLoc.length];
        for (i = 0, n = sideLoc.length; i < n; i++) {
            if (!visitedLocs[i]) {
                if (sideLoc[i] == i && sideOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                prevOrient = 0;

                for (j = 0; sideLoc[j] != i; j++) {
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;

                    length++;

                    prevOrient = (prevOrient + sideOrient[j]) % 4;

                    for (k = 0; sideLoc[k] != j; k++) {
                    }
                    j = k;
                }

                prevOrient = (prevOrient + sideOrient[i]) % 4;
                switch (prevOrient) {
                case 0: // no sign
                    break;
                case 1: // '-' sign
                    //order = IntMath.scm(order, 4);
                    length *= 4;
                    break;
                case 2: // '++' sign
                    //order = IntMath.scm(order, 2);
                    length *= 2;
                    break;
                case 3: // '+' sign
                    //order = IntMath.scm(order, 4);
                    length *= 4;
                    break;
                }
                order = IntMath.scm(order, length);
            }
        }

        return order;
    }

    /**
     * Returns a number that describes the order
     * of the permutation of the supplied cube,
     * assuming that all stickers only have a solid
     * color, and that all stickers on the same face
     * have the same color.
     * <p>
     * On a cube with such stickers, we can
     * not visually determine the orientation of its
     * side parts, and we can not visually determine
     * a permutation of side parts of which all side
     * parts are on the same face of the cube.
     * <p>
     * The order says how many times the permutation
     * has to be applied to the cube to get the
     * initial state.
     *
     * @param cube A cube
     * @return the order of the permutation of the cube
     */
    public static int getVisibleOrder(@Nonnull Cube cube) {
        int[] cornerLoc = cube.getCornerLocations();
        int[] cornerOrient = cube.getCornerOrientations();
        int[] edgeLoc = cube.getEdgeLocations();
        int[] edgeOrient = cube.getEdgeOrientations();
        int[] sideLoc = cube.getSideLocations();
        int[] sideOrient = cube.getSideOrientations();
        int order = 1;

        boolean[] visitedLocs;
        int i, j, k, n, p;
        int prevOrient;
        int length;

        // determine cycle lengths of the current corner permutation
        // and compute smallest common multiple
        visitedLocs = new boolean[cornerLoc.length];

        for (i = 0, n = cornerLoc.length; i < n; i++) {
            if (!visitedLocs[i]) {
                if (cornerLoc[i] == i && cornerOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                prevOrient = 0;

                for (j = 0; cornerLoc[j] != i; j++) {
                    // search first permuted part
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;
                    prevOrient = (prevOrient + cornerOrient[j]) % 3;

                    length++;

                    for (k = 0; cornerLoc[k] != j; k++) {
                    }
                    j = k;
                }

                prevOrient = (prevOrient + cornerOrient[i]) % 3;
                if (prevOrient != 0) {
                    length *= 3;
                }
                order = IntMath.scm(order, length);
            }
        }

        // determine cycle lengths of the current edge permutation
        // and compute smallest common multiple
        visitedLocs = new boolean[edgeLoc.length];
        for (i = 0, n = edgeLoc.length; i < n; i++) {
            if (!visitedLocs[i]) {
                if (edgeLoc[i] == i && edgeOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                prevOrient = 0;

                for (j = 0; edgeLoc[j] != i; j++) {
                    // search first permuted part
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;
                    prevOrient ^= edgeOrient[j];

                    length++;

                    for (k = 0; edgeLoc[k] != j; k++) {
                    }
                    j = k;
                }

                if ((prevOrient ^ edgeOrient[i]) == 1) {
                    length *= 2;
                }
                order = IntMath.scm(order, length);
            }
        }

        // Determine cycle lengths of the current side permutation
        // and compute smallest common multiple.
        // - Ignore changes of orientation.
        // - Ignore side permutations which are entirely on same face.
        visitedLocs = new boolean[sideLoc.length];
        List<Integer> facesInPermutation = new ArrayList<>();
        for (i = 0, n = sideLoc.length; i < n; i++) {
            if (!visitedLocs[i]) {
                if (sideLoc[i] == i && sideOrient[i] == 0) {
                    continue;
                }

                length = 1;

                visitedLocs[i] = true;
                facesInPermutation.clear();
                facesInPermutation.add(sideLoc[i] % 6);

                for (j = 0; sideLoc[j] != i; j++) {
                    // search first permuted part
                }

                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;

                    length++;
                    facesInPermutation.add(sideLoc[j] % 6);

                    for (k = 0; sideLoc[k] != j; k++) {
                        // search next permuted part
                    }
                    j = k;
                }
                if (length > 0) {
                    // If all parts at a distance of 3 are on the same face, the length can be divided by 3.
                    // If all parts at a distance of 2 are on the same face, the length can be divided by 2
                    // If all parts are in the same face, the length can be reduced to 1
                    int reducedLength = length;
                    SubcycleSearch:
                    for (int subcycleLength = 1; subcycleLength < length; subcycleLength++) {
                        if (subcycleLength > 0 && length % subcycleLength == 0) {
                            boolean canReduceLength = true;
                            for (j = subcycleLength; j < length; j += subcycleLength) {
                                for (k = 0; k < subcycleLength; k++) {
                                    if (facesInPermutation.get(j + k - subcycleLength) != facesInPermutation.get(j + k)) {
                                        canReduceLength = false;
                                        break;
                                    }
                                }
                            }
                            if (canReduceLength) {
                                reducedLength = subcycleLength;
                                break SubcycleSearch;
                            }
                        }
                    }
                    order = IntMath.scm(order, reducedLength);
                }
            }
        }

        return order;
    }

    /**
     * Returns a String that describes the current
     * location of the stickers. Ignores the rotation
     * of the cube.
     */
    @Nonnull
    public static String toNormalizedStickersString(@Nonnull Cube cube) {
        char[] faces = new char[]{'R', 'U', 'F', 'L', 'D', 'B'};
        int[][] stickers = StickerCubes.rubiksCubeToStickers(((RubiksCube) cube));

        // This map is used to normalize the
        // possibly rotated cube.
        int[] faceMap = new int[6];
        for (int i = 0; i < 6; i++) {
            faceMap[stickers[i][4]] = i;
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < stickers.length; i++) {
            if (i != 0) {
                buf.append('\n');
            }
            buf.append(faces[faceMap[stickers[i][4]]]);
            buf.append(':');
            for (int j = 0; j < stickers[i].length; j++) {
                buf.append(faces[faceMap[stickers[i][j]]]);
            }
        }
        return buf.toString();
    }

    /**
     * Returns a String that describes the current
     * location of the stickers. Ignores the rotation
     * of the cube.
     */
    @Nonnull
    public static String toMappedStickersString(@Nonnull Cube cube, int[] mappings) {
        //char[] faces = new char[]{'F', 'R', 'D', 'B', 'L', 'U'};
        char[] faces = new char[]{'R', 'U', 'F', 'L', 'D', 'B'};
        int[][] stickers = getMappedStickers(cube, mappings);

        // This map is used to normalize the
        // possibly rotated cube.
        int[] faceMap = new int[6];
        for (int i = 0; i < 6; i++) {
            faceMap[stickers[i][4]] = i;
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < stickers.length; i++) {
            if (i != 0) {
                buf.append('\n');
            }
            buf.append(faces[faceMap[stickers[i][4]]]);
            buf.append(':');
            for (int j = 0; j < stickers[i].length; j++) {
                if (stickers[i][j] == -1) {
                    buf.append('.');
                } else {
                    buf.append(faces[faceMap[stickers[i][j]]]);
                }
            }
        }
        return buf.toString();
    }

    /**
     * Returns the stickers reflecting the current permutation of the cube.
     *
     * @param mappings An array with sticker mappings. It must have the
     *                 same structure as described for method setStickers().
     * @return Array of stickers: int[6][9]. Same structure as in method setStickers().
     */
    public static int[][] getMappedStickers(@Nonnull Cube cube, int[] mappings) {
        int[][] perFaceMappings = new int[6][cube.getLayerCount() * cube.getLayerCount()];
        int index = 0;
        for (int face = 0; face < perFaceMappings.length; face++) {
            for (int sticker = 0; sticker < perFaceMappings[face].length; sticker++) {
                perFaceMappings[face][sticker] = mappings[index++];
            }
        }
        return getMappedStickers(cube, perFaceMappings);
    }

    /**
     * Returns the stickers reflecting the current permutation of the cube.
     *
     * @param mappings An array with sticker mappings. It must have the
     *                 same structure as described for method setStickers().
     * @return Array of stickers: int[6][9]. Same structure as in method setStickers().
     */
    public static int[][] getMappedStickers(@Nonnull Cube cube, int[][] mappings) {
        int[][] stickers = StickerCubes.rubiksCubeToStickers(((RubiksCube) cube));
        int[][] mappedStickers = stickers.clone();

        for (int face = 0; face < stickers.length; face++) {
            for (int sticker = 0; sticker < stickers[face].length; sticker++) {
                mappedStickers[face][sticker] = mappings[stickers[face][sticker]][sticker];
            }
        }
        return mappedStickers;
    }

    public static int getFaceOfSticker(@Nonnull CubeAttributes attr, int stickerIndex) {
        int face;
        for (face = 0; face < attr.getFaceCount(); face++) {
            if (attr.getStickerOffset(face) > stickerIndex) {
                break;
            }
        }
        return face - 1;
    }

    /**
     * Returns a String describing the state of the cube using
     * Bandelow's English permutation notation.
     *
     * @param cube a cube
     * @return a permutation String
     */
    @Nonnull
    public static String toPermutationString(@Nonnull Cube cube) {
        return toPermutationString(cube, Syntax.PRECIRCUMFIX,
                "r", "u", "f", "l", "d", "b",
                "+", "++", "-",
                "(", ")", ",");
    }

    @Nonnull
    public static String toVisualPermutationString(@Nonnull Cube cube) {
        return toVisualPermutationString(cube, Syntax.PRECIRCUMFIX,
                "r", "u", "f", "l", "d", "b",
                "+", "++", "-",
                "(", ")", ",");
    }

    @Nonnull
    public static String toPermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {
        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toPermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toPermutationString(cube);
        }
    }

    @Nonnull
    public static String toCornerPermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {
        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toCornerPermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toCornerPermutationString(cube);
        }
    }

    @Nonnull
    public static String toCornerPermutationString(@Nonnull Cube cube) {
        return toCornerPermutationString(cube, Syntax.PRECIRCUMFIX,
                "r", "u", "f", "l", "d", "b",
                "+", "++", "-", "(", ")", ",");
    }

    @Nonnull
    public static String toEdgePermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {
        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toEdgePermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toEdgePermutationString(cube);
        }
    }

    @Nonnull
    public static String toEdgePermutationString(@Nonnull Cube cube) {
        return toEdgePermutationString(cube, Syntax.PRECIRCUMFIX,
                "r", "u", "f", "l", "d", "b",
                "+", "++", "-", "(", ")", ",");
    }

    @Nonnull
    public static String toSidePermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {
        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toSidePermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toEdgePermutationString(cube);
        }
    }

    @Nonnull
    public static String toVisualSidePermutationString(@Nonnull Cube cube, @Nonnull ScriptNotation notation) {
        if (notation.isSupported(Symbol.PERMUTATION)) {
            return toVisualSidePermutationString(cube, notation.getSyntax(Symbol.PERMUTATION),
                    notation.getToken(Symbol.FACE_R),
                    notation.getToken(Symbol.FACE_U),
                    notation.getToken(Symbol.FACE_F),
                    notation.getToken(Symbol.FACE_L),
                    notation.getToken(Symbol.FACE_D),
                    notation.getToken(Symbol.FACE_B),
                    notation.getToken(Symbol.PERMUTATION_PLUS),
                    notation.getToken(Symbol.PERMUTATION_PLUSPLUS),
                    notation.getToken(Symbol.PERMUTATION_MINUS),
                    notation.getToken(Symbol.PERMUTATION_BEGIN),
                    notation.getToken(Symbol.PERMUTATION_END),
                    notation.getToken(Symbol.PERMUTATION_DELIMITER));
        } else {
            return toEdgePermutationString(cube);
        }
    }

    @Nonnull
    public static String toSidePermutationString(@Nonnull Cube cube) {
        return toSidePermutationString(cube, Syntax.PRECIRCUMFIX,
                "r", "u", "f", "l", "d", "b",
                "+", "++", "-", "(", ")", ",");
    }

    /**
     * Returns a String describing the permutation cycles of the parts in a cube.
     *
     * @param cube
     * @param syntax
     * @param tR
     * @param tU
     * @param tF
     * @param tL
     * @param tD
     * @param tB
     * @param tPlus
     * @param tPlusPlus
     * @param tMinus
     * @param tBegin
     * @param tEnd
     * @param tDelimiter
     * @return
     */
    @Nonnull
    private static String toPermutationString(@Nonnull Cube cube,
                                              Syntax syntax,
                                              String tR, String tU, String tF,
                                              String tL, String tD, String tB,
                                              String tPlus, String tPlusPlus, String tMinus,
                                              String tBegin, String tEnd, String tDelimiter) {

        StringBuilder buf = new StringBuilder();

        String corners = toCornerPermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);
        String edges = toEdgePermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);
        String sides = toSidePermutationString(cube, syntax,
                tR, tU, tF, tL, tD, tB,
                tPlus, tPlusPlus, tMinus,
                tBegin, tEnd, tDelimiter);

        buf.append(corners);
        if (buf.length() > 0 && edges.length() > 0) {
            buf.append('\n');
        }
        buf.append(edges);
        if (buf.length() > 0 && sides.length() > 0) {
            buf.append('\n');
        }
        buf.append(sides);
        if (buf.length() == 0) {
            buf.append(tBegin);
            buf.append(tEnd);
        }
        return buf.toString();
    }

    /**
     * Returns a String describing the permutation cycles of the corner
     * parts in a cube.
     *
     * @param cube
     * @param syntax
     * @param tR
     * @param tU
     * @param tF
     * @param tL
     * @param tD
     * @param tB
     * @param tPlus
     * @param tPlusPlus
     * @param tMinus
     * @param tBegin
     * @param tEnd
     * @param tDelimiter
     * @return
     */
    @Nonnull
    private static String toCornerPermutationString(@Nonnull Cube cube, Syntax syntax,
                                                    String tR, String tU, String tF,
                                                    String tL, String tD, String tB,
                                                    String tPlus, String tPlusPlus, String tMinus,
                                                    String tBegin, String tEnd, String tDelimiter) {

        int[] cornerLoc = cube.getCornerLocations();
        int[] cornerOrient = cube.getCornerOrientations();
        int[] cycle = new int[Math.max(Math.max(cube.getCornerCount(), cube.getEdgeCount()), cube.getSideCount())];

        StringBuilder buf = new StringBuilder();
        boolean[] visitedLocs;

        int i, j, k, p, n;

        int prevOrient;
        boolean isFirst;

        // describe the state changes of the corner parts
        String[][] corners = {
                {tU, tR, tF},// urf
                {tD, tF, tR},// dfr
                {tU, tB, tR},// ubr
                {tD, tR, tB},// drb
                {tU, tL, tB},// ulb
                {tD, tB, tL},// dbl
                {tU, tF, tL},// ufl
                {tD, tL, tF}// dlf
        };

        visitedLocs = new boolean[cube.getCornerCount()];
        isFirst = true;
        for (i = 0, n
                = cube.getCornerCount(); i < n; i++) {
            if (!visitedLocs[i]) {
                if (cornerLoc[i] == i && cornerOrient[i] == 0) {
                    continue;
                }

                // gather a permutation cycle
                int cycleLength = 0;
                int cycleStart = 0;
                j = i;
                while (!visitedLocs[j]) {
                    visitedLocs[j] = true;
                    cycle[cycleLength++] = j;
                    if (cornerLoc[j] < cornerLoc[cycle[cycleStart]]) {
                        cycleStart = cycleLength - 1;
                    }
                    for (k = 0; cornerLoc[k] != j; k++) {
                    }
                    j = k;
                }

                // print the permutation cycle
                if (isFirst) {
                    isFirst = false;
                } else {
                    buf.append(' ');
                }
                if (syntax == Syntax.PREFIX) {
                    // the sign of the cycle will be inserted before the opening bracket
                    p = buf.length();
                    buf.append(tBegin);
                } else if (syntax == Syntax.PRECIRCUMFIX) {
                    // the sign of the cycle will be inserted after the opening bracket
                    buf.append(tBegin);
                    p = buf.length();
                } else {
                    buf.append(tBegin);
                    p = -1;
                }

                prevOrient = 0;
                for (k = 0; k < cycleLength; k++) {
                    j = cycle[(cycleStart + k) % cycleLength];
                    if (k != 0) {
                        buf.append(tDelimiter);
                        prevOrient = (prevOrient + cornerOrient[j]) % 3;
                    }
                    switch (prevOrient) {
                    case 0:
                        buf.append(corners[j][0]);
                        buf.append(corners[j][1]);
                        buf.append(corners[j][2]);
                        break;
                    case 2:
                        buf.append(corners[j][1]);
                        buf.append(corners[j][2]);
                        buf.append(corners[j][0]);
                        break;
                    case 1:
                        buf.append(corners[j][2]);
                        buf.append(corners[j][0]);
                        buf.append(corners[j][1]);
                        break;
                    }
                }
                j = cycle[cycleStart];
                prevOrient = (prevOrient + cornerOrient[j]) % 3;
                if (syntax == Syntax.POSTCIRCUMFIX) {
                    // the sign of the cycle will be inserted before the closing bracket
                    p = buf.length();
                    buf.append(tEnd);
                } else if (syntax == Syntax.SUFFIX) {
                    // the sign of the cycle will be inserted after the closing bracket
                    buf.append(tEnd);
                    p = buf.length();
                } else {
                    buf.append(tEnd);
                }
                // insert cycle sign
                if (prevOrient != 0) {
                    buf.insert(p, (prevOrient == 1) ? tMinus : tPlus);
                }
            }
        }
        return buf.toString();
    }

    /**
     * Returns a String describing the permutation cycles of the edge parts
     * in a cube.
     */
    @Nonnull
    private static String toEdgePermutationString(@Nonnull Cube cube, Syntax syntax,
                                                  String tR, String tU, String tF,
                                                  String tL, String tD, String tB,
                                                  String tPlus, String tPlusPlus, String tMinus,
                                                  String tBegin, String tEnd, String tDelimiter) {

        int[] edgeLoc = cube.getEdgeLocations();
        int[] edgeOrient = cube.getEdgeOrientations();
        int[] cycle = new int[Math.max(Math.max(cube.getCornerCount(), cube.getEdgeCount()), cube.getSideCount())];
        int layerCount = cube.getLayerCount();
        boolean hasEvenLayerCount = layerCount % 2 == 0;

        StringBuilder buf = new StringBuilder();
        boolean[] visitedLocs;

        int i, j, k, l, p, n;
        int prevOrient;
        boolean isFirst;

        // describe the state changes of the edge parts
        if (edgeLoc.length > 0) {
            String[][] edges = {
                    {tU, tR}, //"ur"
                    {tR, tF}, //"rf"
                    {tD, tR}, //"dr"
                    {tB, tU}, //"bu"
                    {tR, tB}, //"rb"
                    {tB, tD}, //"bd"
                    {tU, tL}, //"ul"
                    {tL, tB}, //"lb"
                    {tD, tL}, //"dl"
                    {tF, tU}, //"fu"
                    {tL, tF}, //"lf"
                    {tF, tD} //"fd"
            };
            visitedLocs = new boolean[cube.getEdgeCount()];
            isFirst = true;
            int previousCycleStartEdge = -1;
            for (i = 0, n
                    = cube.getEdgeCount(); i < n; i++) {
                if (!visitedLocs[i]) {
                    if (edgeLoc[i] == i && edgeOrient[i] == 0) {
                        continue;
                    }

                    // gather a permutation cycle
                    int cycleLength = 0;
                    int cycleStart = 0;
                    j = i;
                    while (!visitedLocs[j]) {
                        visitedLocs[j] = true;
                        cycle[cycleLength++] = j;
                        if (previousCycleStartEdge == j % 12) {
                            cycleStart = cycleLength - 1;
                        }
                        for (k = 0; edgeLoc[k] != j; k++) {
                        }
                        j = k;
                    }
                    previousCycleStartEdge = cycle[cycleStart] % 12;

                    // print the permutation cycle
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        buf.append(' ');
                    }

                    if (syntax == Syntax.PREFIX) {
                        // the sign of the cycle will be inserted before the opening bracket
                        p = buf.length();
                        buf.append(tBegin);
                    } else if (syntax == Syntax.PRECIRCUMFIX) {
                        // the sign of the cycle will be inserted after the opening bracket
                        buf.append(tBegin);
                        p = buf.length();
                    } else {
                        buf.append(tBegin);
                        p = -1;
                    }

                    prevOrient = 0;
                    for (k = 0; k < cycleLength; k++) {
                        j = cycle[(cycleStart + k) % cycleLength];
                        if (k != 0) {
                            buf.append(tDelimiter);
                            prevOrient ^= edgeOrient[j];
                        }
                        if (prevOrient == 1) {
                            buf.append(edges[j % 12][1]);
                            buf.append(edges[j % 12][0]);
                        } else {
                            buf.append(edges[j % 12][0]);
                            buf.append(edges[j % 12][1]);
                        }
                        if (hasEvenLayerCount) {
                            buf.append(j / 12 + 1);
                        } else {
                            if (j >= 12) {
                                buf.append(j / 12);
                            }
                        }
                    }
                    j = cycle[cycleStart];
                    if (syntax == Syntax.POSTCIRCUMFIX) {
                        // the sign of the cycle will be inserted before the closing bracket
                        p = buf.length();
                        buf.append(tEnd);
                    } else if (syntax == Syntax.SUFFIX) {
                        // the sign of the cycle will be inserted after the closing bracket
                        buf.append(tEnd);
                        p = buf.length();
                    } else {
                        buf.append(tEnd);
                    }
                    // insert cycle sign
                    if ((prevOrient ^ edgeOrient[j]) == 1) {
                        buf.insert(p, tPlus);
                    }
                }
            }
        }

        return buf.toString();
    }

    /**
     * Returns a String describing the permutation cycles of the side parts
     * in the cube.
     */
    @Nonnull
    private static String toSidePermutationString(@Nonnull Cube cube, Syntax syntax,
                                                  String tR, String tU, String tF,
                                                  String tL, String tD, String tB,
                                                  String tPlus, String tPlusPlus, String tMinus,
                                                  String tBegin, String tEnd, String tDelimiter) {

        int[] sideLoc = cube.getSideLocations();
        int[] sideOrient = cube.getSideOrientations();
        int[] cycle = new int[Math.max(Math.max(cube.getCornerCount(), cube.getEdgeCount()), cube.getSideCount())];
        int layerCount = cube.getLayerCount();
        boolean hasEvenLayerCount = layerCount % 2 == 0;

        StringBuilder buf = new StringBuilder();
        boolean[] visitedLocs;

        int i, j, k, l, p, n;
        int prevOrient;
        boolean isFirst;

        if (sideLoc.length > 0) { // describe the state changes of the side parts
            String[] sides = new String[]{
                    tR, tU, tF, tL, tD, tB // r u f l d b
            };
            String[] sideOrients = new String[]{
                    "", tMinus, tPlusPlus, tPlus
            };
            visitedLocs = new boolean[cube.getSideCount()];
            isFirst = true;

            // First Pass: Only print permutation cycles which lie on a single
            // face of the cube.
            // Second pass: Only print permutation cycles which don't lie on
            // a singe fass of the cube.
            for (int twoPass = 0; twoPass < 2; twoPass++) {
                Arrays.fill(visitedLocs, false);
                for (int byFaces = 0, nf = 6; byFaces < nf; byFaces++) {
                    for (int byParts = 0, np = cube.getSideCount() / 6; byParts < np; byParts++) {
                        i = byParts + byFaces * np;
                        if (!visitedLocs[i]) {
                            if (sideLoc[i] == i && sideOrient[i] == 0) {
                                continue;
                            }

                            // gather a permutation cycle
                            int cycleLength = 0;
                            int cycleStart = 0;
                            boolean isOnSingleFace = true;
                            j = i;
                            while (!visitedLocs[j]) {
                                visitedLocs[j] = true;
                                cycle[cycleLength++] = j;
                                if (j % 6 != i % 6) {
                                    isOnSingleFace = false;
                                }
                                if (cycle[cycleStart] > j) {
                                    cycleStart = cycleLength - 1;
                                }
                                for (k = 0; sideLoc[k] != j; k++) {
                                }
                                j = k;
                            }

                            if (isOnSingleFace == (twoPass == 0)) {

                                // print the permutation cycle
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    buf.append(' ');
                                }
                                if (syntax == Syntax.PREFIX) {
                                    // the sign of the cycle will be inserted before the opening bracket
                                    p = buf.length();
                                    buf.append(tBegin);
                                } else if (syntax == Syntax.PRECIRCUMFIX) {
                                    // the sign of the cycle will be inserted after the opening bracket
                                    buf.append(tBegin);
                                    p = buf.length();
                                } else {
                                    buf.append(tBegin);
                                    p = -1;
                                }

                                prevOrient = 0;
                                for (k = 0; k < cycleLength; k++) {
                                    j = cycle[(cycleStart + k) % cycleLength];
                                    if (k != 0) {
                                        buf.append(tDelimiter);
                                        prevOrient = (prevOrient + sideOrient[j]) % 4;
                                    }
                                    if (syntax == Syntax.PREFIX
                                            || syntax == Syntax.PRECIRCUMFIX
                                            || syntax == Syntax.POSTCIRCUMFIX) {
                                        buf.append(sideOrients[prevOrient]);
                                    }
                                    buf.append(sides[j % 6]);
                                    if (syntax == Syntax.SUFFIX) {
                                        buf.append(sideOrients[prevOrient]);
                                    }
                                    if (hasEvenLayerCount) {
                                        buf.append(j / 6 + 1);
                                    } else {
                                        if (j >= 6) {
                                            buf.append(j / 6);
                                        }
                                    }
                                }
                                j = cycle[cycleStart];
                                prevOrient = (prevOrient + sideOrient[j]) % 4;
                                if (syntax == Syntax.POSTCIRCUMFIX) {
                                    // the sign of the cycle will be inserted before the closing bracket
                                    p = buf.length();
                                    buf.append(tEnd);
                                } else if (syntax == Syntax.SUFFIX) {
                                    // the sign of the cycle will be inserted after the closing bracket
                                    buf.append(tEnd);
                                    p = buf.length();
                                } else {
                                    buf.append(tEnd);
                                }
                                // insert cycle sign
                                if (prevOrient != 0) {
                                    buf.insert(p, sideOrients[prevOrient]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return buf.toString();
    }

    /**
     * Returns a String describing the permutation cycles of the side parts
     * in the cube.
     * <p>
     * XXX - This method is not properly implemented. It should ignore part
     * orientations.
     */
    @Nonnull
    private static String toVisualSidePermutationString(@Nonnull Cube cube, Syntax syntax,
                                                        String tR, String tU, String tF,
                                                        String tL, String tD, String tB,
                                                        String tPlus, String tPlusPlus, String tMinus,
                                                        String tBegin, String tEnd, String tDelimiter) {

        int[] sideLoc = cube.getSideLocations();
        int[] sideOrient = cube.getSideOrientations();
        int[] cycle = new int[Math.max(Math.max(cube.getCornerCount(), cube.getEdgeCount()), cube.getSideCount())];
        int layerCount = cube.getLayerCount();
        boolean hasEvenLayerCount = layerCount % 2 == 0;

        StringBuilder buf = new StringBuilder();
        boolean[] visitedLocs;

        int i, j, k;
        int prevOrient;
        boolean isFirst;

        if (sideLoc.length > 0) { // describe the state changes of the side parts
            String[] sides = new String[]{
                    tR, tU, tF, tL, tD, tB // r u f l d b
            };
            String[] sideOrients = new String[]{
                    "", tMinus, tPlusPlus, tPlus
            };
            visitedLocs = new boolean[cube.getSideCount()];
            isFirst = true;
            int previousCycleStartSide;

            // First Pass: Only print permutation cycles which lie on a single
            // face of the cube.
            // Second pass: Only print permutation cycles which don't lie on
            // a singe fass of the cube.
            for (int twoPass = 0; twoPass < 2; twoPass++) {
                Arrays.fill(visitedLocs, false);
                for (int byFaces = 0, nf = 6; byFaces < nf; byFaces++) {
                    previousCycleStartSide = -1;
                    for (int byParts = 0, np = cube.getSideCount() / 6; byParts < np; byParts++) {
                        i = byParts + byFaces * np;
                        if (!visitedLocs[i]) {
                            if (sideLoc[i] == i && sideOrient[i] == 0) {
                                continue;
                            }

                            // gather a permutation cycle
                            int cycleLength = 0;
                            int cycleStart = 0;
                            boolean isOnSingleFace = true;
                            j = i;
                            while (!visitedLocs[j]) {
                                visitedLocs[j] = true;
                                cycle[cycleLength++] = j;
                                if (j % 6 != i % 6) {
                                    isOnSingleFace = false;
                                }
                                if (cycle[cycleStart] > j) {
                                    cycleStart = cycleLength - 1;
                                }
                                for (k = 0; sideLoc[k] != j; k++) {
                                }
                                j = k;
                            }
                            previousCycleStartSide = cycle[cycleStart] % 6;

                            if (isOnSingleFace == (twoPass == 0)) {
                                // only print cycles which contain more than one part
                                if (cycleLength > 1) {

                                    // print the permutation cycle
                                    if (isFirst) {
                                        isFirst = false;
                                    } else {
                                        buf.append(' ');
                                    }
                                    if (syntax == Syntax.PREFIX) {
                                        // the sign of the cycle will be inserted before the opening bracket
                                        buf.append(tBegin);
                                    } else if (syntax == Syntax.PRECIRCUMFIX) {
                                        // the sign of the cycle will be inserted after the opening bracket
                                        buf.append(tBegin);
                                    } else {
                                        buf.append(tBegin);
                                    }

                                    prevOrient = 0;
                                    for (k = 0; k < cycleLength; k++) {
                                        j = cycle[(cycleStart + k) % cycleLength];
                                        if (k != 0) {
                                            buf.append(tDelimiter);
                                            prevOrient = (prevOrient + sideOrient[j]) % 4;
                                        }
                                        if (syntax == Syntax.PREFIX
                                                || syntax == Syntax.PRECIRCUMFIX
                                                || syntax == Syntax.POSTCIRCUMFIX) {
                                            // buf.append(sideOrients[prevOrient]);
                                        }
                                        buf.append(sides[j % 6]);
                                        if (syntax == Syntax.SUFFIX) {
                                            //buf.append(sideOrients[prevOrient]);
                                        }
                                        if (hasEvenLayerCount) {
                                            buf.append(j / 6 + 1);
                                        } else {
                                            if (j >= 6) {
                                                buf.append(j / 6);
                                            }
                                        }
                                    }
                                    j = cycle[cycleStart];
                                    prevOrient = (prevOrient + sideOrient[j]) % 4;
                                    if (syntax == Syntax.POSTCIRCUMFIX) {
                                        // the sign of the cycle will be inserted before the closing bracket
                                        buf.append(tEnd);
                                    } else if (syntax == Syntax.SUFFIX) {
                                        // the sign of the cycle will be inserted after the closing bracket
                                        buf.append(tEnd);
                                    } else {
                                        buf.append(tEnd);
                                    }
                                    // insert cycle sign
                                    if (prevOrient != 0) {
                                        //buf.insert(p, sideOrients[prevOrient]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return buf.toString();
    }


}
