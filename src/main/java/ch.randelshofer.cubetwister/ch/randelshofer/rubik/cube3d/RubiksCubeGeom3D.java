/* @(#)RubiksCubeFlat3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.geom3d.Shape3D;
import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube.AbstractRubiksCubeGeom3D;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;

/**
 * Geometrical representation of a Rubik's Cube in
 * three dimensions.
 *
 * @author Werner Randelshofer
 */
public class RubiksCubeGeom3D extends AbstractRubiksCubeGeom3D {
    /* Maps stickers to cube parts.
     *                +----+----+----+
     *                | 4.0|11  | 2.0|
     *                +----      ----+
     *                |14.0 21    8.0|
     *                +----      ----+
     *                | 6.0|17  | 0.0|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * | 4.1|14  | 6.2| 6.1|17.0| 0.2| 0.1| 8  | 2.2| 2.1|11.0| 4.2|
     * +----      ----+----      ----+----      ----+----      ----+
     * |15.0 23   18.0|18   22    9  | 9.0 20   12.0|12   25   15  |
     * +----      ----+----      ----+----      ----+----      ----+
     * | 5.2|16  | 7.1| 7.2|19.0| 1.1| 1.2|10  | 3.1| 3.2|13.0| 5.1|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                | 7.0|19  | 1.0|
     *                +----      ----+
     *                |16.0 24   10.0|
     *                +----      ----+
     *                |5.0 |13  | 3.0|
     *                +----+----+----+
     */

    private final static int[] stickerToPartMap = {
        0, 8, 2, 9, 20, 12, 1, 10, 3, // right
        4, 11, 2, 14, 21, 8, 6, 17, 0, // up
        6, 17, 0, 18, 22, 9, 7, 19, 1, // front
        4, 14, 6, 15, 23, 18, 5, 16, 7, // left
        7, 19, 1, 16, 24, 10, 5, 13, 3, // down
        2, 11, 4, 12, 25, 15, 3, 13, 5 // back
    };

    /**
     * Gets the part which holds the indicated sticker.
     * The sticker index is interpreted according to this
     * scheme:
     * <pre>
     *                 +---+---+---+
     *                 | 9 | 10| 11|
     *                 +---+---+---+
     *                 | 12| 13| 14|
     *                 +---+---+---+
     *                 | 15| 16| 17|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 27| 28| 29| 18| 19| 20| 0 | 1 | 2 | 45| 46| 47|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 30| 31| 32| 21| 22| 23| 3 | 4 | 5 | 48| 49| 50|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *     | 33| 34| 35| 24| 25| 26| 6 | 7 | 8 | 51| 52| 53|
     *     +---+---+---+---+---+---+---+---+---+---+---+---+
     *                 | 36| 37| 38|
     *                 +---+---+---+
     *                 | 39| 40| 41|
     *                 +---+---+---+
     *                 | 42| 43| 44|
     *                 +---+---+---+
     * </pre>
     */
    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 2, 0, 0, 0, 2, 1, 1, // right
        0, 1, 0, 0, 0, 0, 0, 1, 0, // up
        1, 0, 2, 1, 0, 1, 2, 0, 1, // front
        1, 1, 2, 0, 0, 0, 2, 1, 1, // left
        0, 1, 0, 0, 0, 0, 0, 1, 0, // down
        1, 0, 2, 1, 0, 1, 2, 0, 1 // back
    };

    @Override
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex];
    }

    protected int getStickerIndexForPart(int part, int orientation) {
        int sticker;
        for (sticker = stickerToPartMap.length - 1; sticker >= 0; sticker--) {
            if (stickerToPartMap[sticker] == part && stickerToFaceMap[sticker] == orientation) {
                break;
            }
        }
        return sticker;
    }

    @Override
    public int getStickerCount() {
        return 54;
    }

    /** Updates the outline color of the parts.
     */
    @Override
    protected void updatePartsOutlineColor() {
        for (int partIndex = 0; partIndex < 27; partIndex++) {
            Color color = attributes.getPartOutlineColor(partIndex);
            Shape3D shape = getPart(partIndex);
            int limit, limit2;
            if (partIndex < 8) {
                limit2 = 3;
                limit = 16;
            } else if (partIndex < 20) {
                limit2 = 2;
                limit = 16;
            } else if (partIndex < 26) {
                limit2 = 1;
                limit = 15;
            } else {
                limit2 = 6;
                limit = shape.getFaceCount();
            }
            if (attributes.getPartFillColor(partIndex) == null) {
                limit = limit2;
            }
            for (int i = shape.getFaceCount() - 1; i >= 0; i--) {
                shape.setBorderColor(i, (i < limit) ? color : null);
            }
        }
    }

    /** Updates the fill color of the parts.
     */
    @Override
    final protected void updatePartsFillColor() {
        for (int partIndex = 0, n = getAttributes().getPartCount(); partIndex < n; partIndex++) {
            Color color = getAttributes().getPartFillColor(partIndex);
            Shape3D shape = shapes[partIndex];
            int offset;

            if (partIndex < edgeOffset) {
                offset = 3;
            } else if (partIndex < sideOffset) {
                offset = 2;
            } else if (partIndex < centerOffset) {
                offset = 1;
            } else {
                offset = 0;
            }
            //System.out.println("partIndex"+partIndex+" colr="+color+" faces="+shape.getFaceCount());

            for (int i = shape.getFaceCount() - 1; i >= offset; i--) {
                shape.setFillColor(i, color);
            }
        }
    }

    @Nonnull
    @Override
    public CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{9, 9, 9, 9, 9, 9});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 3 * 3, 1 * 3 * 3, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 3 * 3, 2 * 3 * 3, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 3 * 3, 3 * 3 * 3, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 3 * 3, 4 * 3 * 3, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 3 * 3, 5 * 3 * 3, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 3 * 3, 6 * 3 * 3, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    @Nonnull
    public String getName() {
        return "Rubik's Cube";
    }
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;

    @Override
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[]{
                        // Vertices of the main cubicle
                        // ----------------------------
                        //0: Front Face: top-left, top-right, bottom-right, bottom-left
                        -8, 8, 9, 8, 8, 9, 8, -8, 9, -8, -8, 9,
                        //4: Right Face: top-front, top-back, center-back, bottom-center, bottom-front
                        9, 8, 8, 9, 8, -8, 9, -5, -8, 9, -8, -5, 9, -8, 8,
                        //9: Bottom Face: front-left, front-right, center-right, back-center, back-left
                        -8, -9, 8, 8, -9, 8, 8, -9, -5, 5, -9, -8, -8, -9, -8,
                        //14: Back Face: up-right, up-left, down-left, down-center, center-right
                        8, 8, -9, -8, 8, -9, -8, -8, -9, 5, -8, -9, 8, -5, -9,
                        //19: Left Face: top-back, top-front, bottom-front, bottom-back
                        -9, 8, -8, -9, 8, 8, -9, -8, 8, -9, -8, -8,
                        //23: Top Face: back-left, back-right, front-right, front-left
                        -8, 9, -8, 8, 9, -8, 8, 9, 8, -8, 9, 8,
                        // Vertices of the additional cubicle at the bottom right
                        //27
                        9, -4, -14, 14, -4, -14, 9, -4, -9, 14, -4, -9,
                        //31
                        4, -9, -14, 4, -9, -9, 4, -14, -14, 4, -14, -9,
                        //35
                        9, -14, -4, 14, -14, -4, 9, -9, -4, 14, -9, -4, 14, -14, -14
                    };
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][]{
                        // Faces with stickers and with outlines
                        //--------------------------------------
                        {23, 24, 25, 26}, //Top face      The order of these faces
                        {0, 1, 2, 3}, //Front face    is relevant, for method
                        {19, 20, 21, 22}, //Left face     updateStickersFillColor()

                        // Faces with outlines
                        // (all faces which have outlines must be
                        // at the beginning, this is relevant
                        // for method updatePartsOutlineColor()
                        //--------------------------------------

                        // Faces of the main cubicle
                        {4, 5, 6, 7, 8}, //Right Face
                        {9, 10, 11, 12, 13}, //Bottom Face
                        {14, 15, 16, 17, 18},//Back Face

                        // Faces of the additional cubicle at the bottom right
                        {27 + 10, 27 + 11, 27 + 9, 27 + 8},
                        {27 + 11, 27 + 3, 27 + 1, 27 + 12, 27 + 9},
                        {27 + 1, 27 + 0, 27 + 4, 27 + 6, 27 + 12},
                        {27 + 5, 27 + 7, 27 + 6, 27 + 4},
                        {27 + 5, 27 + 10, 27 + 8, 27 + 7},
                        {27 + 2, 27 + 3, 27 + 11, 27 + 10},
                        {27 + 0, 27 + 2, 27 + 5, 27 + 4},
                        {27 + 2, 27 + 10, 27 + 5},
                        {27 + 0, 27 + 1, 27 + 3, 27 + 2},
                        {27 + 12, 27 + 6, 27 + 7, 27 + 8, 27 + 9},
                        // Faces without outlines
                        //--------------------------------------

                        // Inner edges of the main cubicle. We assign swipe actions to these.
                        {24, 5, 4, 25}, //Top Right
                        {15, 14, 24, 23}, //Top Back
                        {3, 2, 10, 9}, //Bottom Front
                        {1, 4, 8, 2}, //Front Right
                        {22, 21, 9, 13}, //Bottom Left
                        {15, 19, 22, 16}, //Back Left

                        // Outer edges of the main cubicle. We assign no actions to these.
                        {26, 25, 1, 0}, //Top Front
                        {23, 26, 20, 19}, //Top Left
                        {11, 10, 8, 7}, //Bottom Right
                        {13, 12, 17, 16}, //Bottom Back
                        {0, 3, 21, 20}, //Front Left
                        {14, 18, 6, 5}, //Back Right

                        // Triangles at the corners of the main cubicle
                        {9, 21, 3}, //Bottom Left Front
                        {10, 2, 8}, //Bottom Front Right
                        {13, 16, 22},//Bottom Back Left

                        {26, 0, 20}, //Top Front Left
                        {25, 4, 1}, //Top Right Front ruuf rruf ruff
                        {23, 19, 15},//Top Left Back luub llub lubb
                        {24, 14, 5}, //Top Back Right ruub rubb rrub
                    };
        }
        for (int i = 0; i < cornerCount; i++) {
            shapes[cornerOffset + i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2]);
        }
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;

    @Override
    protected void initEdges() {
        if (EDGE_VERTS == null) {
            EDGE_VERTS = new float[]{
                        // Vertices of the main cubicle
                        //-----------------------------
                        //0: Front Face: top-left, top-right, bottom-right, bottom-left
                        -8, 8, 9, 8, 8, 9, 8, -8, 9, -8, -8, 9,
                        //4: Right Face: top-front, top-back, center-back, bottom-center, bottom-front
                        9, 8, 8, 9, 8, -8, 9, -4, -8, 9, -8, -4, 9, -8, 8,
                        //9: Bottom Face: front-left, front-right, back-right, back-left
                        -8, -9, 8, 8, -9, 8, 8, -9, -3, -8, -9, -3,
                        //13: Back Face: up-right, up-left, down-left, down-right
                        8, 8, -9, -8, 8, -9, -8, -3, -9, 8, -3, -9,
                        //17: Left Face: top-back, top-front, bottom-front, bottom-center, center-back
                        -9, 8, -8, -9, 8, 8, -9, -8, 8, -9, -8, -4, -9, -4, -8,
                        //22: Top Face: back-left, back-right, front-right, front-left
                        -8, 9, -8, 8, 9, -8, 8, 9, 8, -8, 9, 8,
                        // Vertices of the additional cubicle at the back bottom.
                        //-------------------------------------------------------
                        //26
                        4, -3, -9, 4, -1, -9, 4, -1, -14, 4, -14, -14, 4, -14, -1, 4, -9, -1, 4, -9, -3,
                        //33
                        -4, -3, -9, -4, -1, -9, -4, -1, -14, -4, -14, -14, -4, -14, -1, -4, -9, -1, -4, -9, -3
                    };

        }
        if (EDGE_FACES == null) {
            EDGE_FACES = new int[][]{
                        // Faces with stickers and with outlines
                        //--------------------------------------
                        {0, 1, 2, 3}, //Front  The order of these faces is relevant
                        {22, 23, 24, 25}, //Top    for method updateStickersFillColor

                        // Faces with outlines
                        // (all faces which have outlines must be
                        // at the beginning, this is relevant
                        // for method updatePartsOutlineColor()
                        //--------------------------------------
                        // Faces of the main cubicle
                        {4, 5, 6, 7, 8}, //Right
                        {9, 10, 11, 12}, //Bottom
                        {13, 14, 15, 16}, //Back
                        {17, 18, 19, 20, 21}, //Left

                        // Faces of the additional cubicle at the back and bottom
                        {26 + 0, 26 + 1, 26 + 2},
                        {26 + 0, 26 + 2, 26 + 3, 26 + 4, 26 + 6},
                        {26 + 4, 26 + 5, 26 + 6},
                        {26 + 9, 26 + 8, 26 + 7},
                        {26 + 13, 26 + 11, 26 + 10, 26 + 9, 26 + 7},
                        {26 + 13, 26 + 12, 26 + 11},
                        {26 + 1, 26 + 8, 26 + 9, 26 + 2},
                        {26 + 2, 26 + 9, 26 + 10, 26 + 3},
                        {26 + 3, 26 + 10, 26 + 11, 26 + 4},
                        {26 + 5, 26 + 4, 26 + 11, 26 + 12},
                        // Faces without outlines
                        //--------------------------------------
                        // Inner edges of the main cubicle. We assign swipe actions to these.
                        {1, 4, 8, 2}, //Front Right
                        {18, 0, 3, 19}, //Front Left
                        {3, 2, 10, 9}, //Bottom Front

                        {23, 5, 4, 24}, //Top Right
                        {14, 13, 23, 22}, //Top Back
                        {17, 22, 25, 18}, //Top Left

                        // Outer edges of the main cubicle. We assign no actions to these.
                        {25, 24, 1, 0}, //Top Front
                        {8, 7, 11, 10}, //Bottom Right
                        {20, 19, 9, 12}, //Bottom Left
                        {5, 13, 16, 6}, //Back Right
                        {14, 17, 21, 15}, //Back Left

                        // Faces of the main cubicle
                        {16, 15, 21, 20, 12, 11, 7, 6}, // Back Down

                        // Triangular faces at the corners of the main cubicle
                        {25, 0, 18}, //Top Front Left
                        {24, 4, 1}, //Top Right Front
                        {22, 17, 14}, //Top Left Back
                        {23, 13, 5}, //Top Back Right
                        {9, 19, 3}, //Bottom Left Front
                        {10, 2, 8}, //Bottom Front Right
                    };
        }
        for (int i = 0; i < edgeCount; i++) {
            shapes[edgeOffset + i] = new Shape3D(EDGE_VERTS, EDGE_FACES, new Color[EDGE_FACES.length][2]);
        }
    }

    @Nonnull
    @Override
    public CubeKind getKind() {
        return CubeKind.RUBIK;
    }

    @Override
    protected void initActions() {
        int i, j;
        for (i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            for (j = 0; j < 3; j++) {
                shapes[index].setAction(
                        j,
                        new PartAction(
                                index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: {// u
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[16].addSwipeListener(sa);
                        shapes[index].getFaces()[17].addSwipeListener(sa);
                        break;
                    }
                    case 1: {// r
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[18].addSwipeListener(sa);
                        shapes[index].getFaces()[19].addSwipeListener(sa);
                        break;
                    }
                    case 2: {// f
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[20].addSwipeListener(sa);
                        shapes[index].getFaces()[21].addSwipeListener(sa);
                        break;
                    }
                }
            }
        }
        for (i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            for (j = 0; j < 2; j++) {
                shapes[index].setAction(j, new PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: {
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[16].addSwipeListener(sa);
                        shapes[index].getFaces()[17].addSwipeListener(sa);
                        shapes[index].getFaces()[18].addSwipeListener(sa);
                        break;
                    }
                    case 1: {
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[19].addSwipeListener(sa);
                        shapes[index].getFaces()[20].addSwipeListener(sa);
                        shapes[index].getFaces()[21].addSwipeListener(sa);
                        break;
                    }
                }
            }
        }
        for (i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            shapes[index].setAction(0, new PartAction(index, 0, getStickerIndexForPart(index, 0)));
            shapes[centerOffset].setAction(i, new PartAction(centerOffset, i, -1));
            SwipeAction sa = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (-Math.PI / 4));
            shapes[index].getFaces()[0].addSwipeListener(sa);
            shapes[index].getFaces()[15].addSwipeListener(sa);
            shapes[index].getFaces()[16].addSwipeListener(sa);
            shapes[index].getFaces()[17].addSwipeListener(sa);
            shapes[index].getFaces()[18].addSwipeListener(sa);
        }
    }

}
