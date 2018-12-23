/* @(#)PocketCubeIdx3D.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Object;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Triangle;

import java.awt.Color;
import java.util.Arrays;

/**
 * Represents the geometry of a PocketCube for the Idx3D rendering engine.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>3.1 2010-04-01 Added swipe actions to edges adjacent to stickers.
 * <br>3.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>2.0 2008-01-01 Adapted to changes in AbstractCube.
 * <br>1.0 2005-12-29 Created.
 */
public class PocketCubeIdx3D extends AbstractPocketCubeIdx3D {

    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one sixth of the imag width.
     */
    private final static float ss = imageWidth / 6f;
    /**
     * Bevelling is cut of from each sticker.
     */
    private float bevelling;

    @Override
    protected void init() {
        bevelling = 3f / 512f;
        super.init();
    }
    private final static float[] CORNER_VERTS = {
        // Vertices of the main cubicle
        // ----------------------------
        //0: Front Face: luff, ruff, rdff, ldff
        -8, 8, 9, 8, 8, 9, 8, -8, 9, -8, -8, 9,
        //4: Right Face: top-front, top-back, centerPart-back, bottom-centerPart, bottom-front
        9, 8, 8, 9, 8, -8, 9, -5, -8, 9, -8, -5, 9, -8, 8,
        //9: Bottom Face: front-left, front-right, centerPart-right, back-centerPart, back-left
        -8, -9, 8, 8, -9, 8, 8, -9, -5, 5, -9, -8, -8, -9, -8,
        //14: Back Face: up-right, up-left, down-left, down-centerPart, centerPart-right
        8, 8, -9, -8, 8, -9, -8, -8, -9, 5, -8, -9, 8, -5, -9,
        //19: Left Face: top-back, top-front, bottom-front, bottom-back
        -9, 8, -8, -9, 8, 8, -9, -8, 8, -9, -8, -8,
        //23: Top Face: back-left, back-right, front-right, front-left
        -8, 9, -8, 8, 9, -8, 8, 9, 8, -8, 9, 8, // Vertices of the additional cubicle at the bottom right
    /*
    //27
    9,-4,-14, 14,-4,-14, 9,-4,-9,   14,-4,-9,
    //31
    4,-9,-14, 4,-9,-9,   4,-14,-14, 4,-14,-9,
    //35
    9,-14,-4, 14,-14,-4, 9,-9,-4, 14,-9,-4, 14,-14,-14*/
     };
    private final static int[][] CORNER_FACES = {
        // Faces with stickers and with outlines
        //--------------------------------------
        //    we assign twist and swipe actions to these
        {0, 1, 2, 3}, //Up face    The sequence of these faces
        {23, 24, 25, 26}, //Front face      is relevant, for method
        {22, 19, 20, 21}, //Left face     updateStickersFillColor()

        // Faces with outlines
        // (all faces which have outlines must be
        // at the beginning, this is relevant
        // for method updatePartsOutlineColor()
        //--------------------------------------

        // Faces of the main cubicle, we assign no actions to these
        {4, 5, 6, 7, 8}, //Right Face
        {9, 10, 11, 12, 13}, //Bottom Face
        {14, 15, 16, 17, 18},//Back Face

        // Faces without outlines
        //--------------------------------------

        // Inner edges of the main cubicle. We assign swipe actions to these.
        {3, 2, 10, 9}, //Bottom Front
        {1, 4, 8, 2}, //Front Right
        {24, 5, 4, 25}, //Top Right
        {15, 14, 24, 23}, //Top Back
        {13, 22, 21, 9}, //Bottom Left
        {16, 15, 19, 22}, //Back Left

        // Outer edges of the main cubicle. We assign no actions to these.
        {26, 25, 1, 0}, //Top Front
        {23, 26, 20, 19}, //Top Left
        {11, 10, 8, 7}, //Bottom Right
        {13, 12, 17, 16}, //Bottom Back
        {0, 3, 21, 20}, //Front Left
        {14, 18, 6, 5}, //Back Right

        // Triangles at the cornes of the main cubicle.
        //    We assign no actions to these.
        {9, 21, 3}, //Bottom Left Front
        {10, 2, 8}, //Bottom Front Right
        {13, 16, 22},//Bottom Back Left

        {26, 0, 20}, //Top Front Left
        {25, 4, 1}, //Top Right Front ruuf rruf ruff
        {23, 19, 15},//Top Left Back luub llub lubb
        {24, 14, 5}, //Top Back Right ruub rubb rrub

        // Faces of the additional cubicle at the bottom right.
        // These faces can never be seen unless we would take the cube apart.
        /*{27+10, 27+11, 27+9, 27+8},
        {27+11, 27+3, 27+1, 27+12, 27+9},
        {27+1, 27+0, 27+4, 27+6, 27+12},
        {27+5, 27+7, 27+6, 27+4},
        {27+5, 27+10, 27+8, 27+7},
        {27+2, 27+3, 27+11, 27+10},
        {27+0, 27+2, 27+5, 27+4},
        {27+2, 27+10, 27+5},
        {27+0, 27+1, 27+3, 27+2},
        {27+12, 27+6, 27+7, 27+8,27+9},*/
    };

    @Override
    protected void initCorners() {
        int i, j, part;
        float[] verts = CORNER_VERTS;
        int[][] faces = CORNER_FACES;

        for (part = 0; part < 8; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i = 0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
            }
            for (i = 0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            object3D.vertex(faces[i][0]),
                            object3D.vertex(faces[i][j - 1]),
                            object3D.vertex(faces[i][j]));

                    object3D.addTriangle(triangle);
                }
            }
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            object3D.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(2).setTriangleMaterial(sticker);
            object3D.triangle(3).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(4).setTriangleMaterial(sticker);
            object3D.triangle(5).setTriangleMaterial(sticker);
            parts[cornerOffset + part] = object3D;
        }
        initCornerUVMap();
    }

    /** 
     * Initializes the UV Map for the corner parts.
     * <pre>
     *   0   1   2   3   4   5   6
     * 0         +---+---+
     *           |4.0|2.0|
     * 1         +--- ---+ 
     *           |6.0|0.0|   
     * 2 +---+---+---+---+---+---+.......+
     *   |4.1|6.2|6.1|0.2|0.1|2.2|       '
     * 3 +--- ---+--- ---+--- ---+       '
     *   |5.2|7.1|7.2|1.1|1.2|3.1|       '
     * 4 +---+---+---+---+---+---+.......+
     *           |7.0|1.0|2.1|4.2|   |
     * 5         +--- ---+--- ---+ &lt;-+
     *           |5.0|3.0|3.2|5.1|
     * 6         +---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Object object3D = parts[cornerOffset + part];
            switch (part) {
                case 0: // up right front
                    object3D.triangle(0).setUV(ss * 4 - bevelling, ss * 2 - bevelling, ss * 4 - bevelling, ss * 1 + bevelling, ss * 3 + bevelling, ss * 1 + bevelling);
                    object3D.triangle(1).setUV(ss * 4 - bevelling, ss * 2 - bevelling, ss * 3 + bevelling, ss * 1 + bevelling, ss * 3 + bevelling, ss * 2 - bevelling);
                    object3D.triangle(2).setUV(ss * 4 + bevelling, ss * 3 - bevelling, ss * 5 - bevelling, ss * 3 - bevelling, ss * 5 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(3).setUV(ss * 4 + bevelling, ss * 3 - bevelling, ss * 5 - bevelling, ss * 2 + bevelling, ss * 4 + bevelling, ss * 2 + bevelling);
                    object3D.triangle(4).setUV(ss * 3 + bevelling, ss * 3 - bevelling, ss * 4 - bevelling, ss * 3 - bevelling, ss * 4 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(5).setUV(ss * 3 + bevelling, ss * 3 - bevelling, ss * 4 - bevelling, ss * 2 + bevelling, ss * 3 + bevelling, ss * 2 + bevelling);
                    break;
                case 1: // down front right
                    object3D.triangle(0).setUV(ss * 4 - bevelling, ss * 4 + bevelling, ss * 3 + bevelling, ss * 4 + bevelling, ss * 3 + bevelling, ss * 5 - bevelling);
                    object3D.triangle(1).setUV(ss * 4 - bevelling, ss * 4 + bevelling, ss * 3 + bevelling, ss * 5 - bevelling, ss * 4 - bevelling, ss * 5 - bevelling);
                    object3D.triangle(2).setUV(ss * 4 - bevelling, ss * 3 + bevelling, ss * 3 + bevelling, ss * 3 + bevelling, ss * 3 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(3).setUV(ss * 4 - bevelling, ss * 3 + bevelling, ss * 3 + bevelling, ss * 4 - bevelling, ss * 4 - bevelling, ss * 4 - bevelling);
                    object3D.triangle(4).setUV(ss * 5 - bevelling, ss * 3 + bevelling, ss * 4 + bevelling, ss * 3 + bevelling, ss * 4 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(5).setUV(ss * 5 - bevelling, ss * 3 + bevelling, ss * 4 + bevelling, ss * 4 - bevelling, ss * 5 - bevelling, ss * 4 - bevelling);
                    break;
                case 2: // up back right
                    object3D.triangle(0).setUV(ss * 4 - bevelling, ss * 0 + bevelling, ss * 3 + bevelling, ss * 0 + bevelling, ss * 3 + bevelling, ss * 1 - bevelling);
                    object3D.triangle(1).setUV(ss * 4 - bevelling, ss * 0 + bevelling, ss * 3 + bevelling, ss * 1 - bevelling, ss * 4 - bevelling, ss * 1 - bevelling);
                    object3D.triangle(2).setUV(ss * 4 + bevelling, ss * 5 - bevelling, ss * 5 - bevelling, ss * 5 - bevelling, ss * 5 - bevelling, ss * 4 + bevelling);
                    object3D.triangle(3).setUV(ss * 4 + bevelling, ss * 5 - bevelling, ss * 5 - bevelling, ss * 4 + bevelling, ss * 4 + bevelling, ss * 4 + bevelling);
                    object3D.triangle(4).setUV(ss * 5 + bevelling, ss * 3 - bevelling, ss * 6 - bevelling, ss * 3 - bevelling, ss * 6 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(5).setUV(ss * 5 + bevelling, ss * 3 - bevelling, ss * 6 - bevelling, ss * 2 + bevelling, ss * 5 + bevelling, ss * 2 + bevelling);
                    break;
                case 3: // down right back
                    object3D.triangle(0).setUV(ss * 4 - bevelling, ss * 6 - bevelling, ss * 4 - bevelling, ss * 5 + bevelling, ss * 3 + bevelling, ss * 5 + bevelling);
                    object3D.triangle(1).setUV(ss * 4 - bevelling, ss * 6 - bevelling, ss * 3 + bevelling, ss * 5 + bevelling, ss * 3 + bevelling, ss * 6 - bevelling);
                    object3D.triangle(2).setUV(ss * 6 - bevelling, ss * 3 + bevelling, ss * 5 + bevelling, ss * 3 + bevelling, ss * 5 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(3).setUV(ss * 6 - bevelling, ss * 3 + bevelling, ss * 5 + bevelling, ss * 4 - bevelling, ss * 6 - bevelling, ss * 4 - bevelling);
                    object3D.triangle(4).setUV(ss * 5 - bevelling, ss * 5 + bevelling, ss * 4 + bevelling, ss * 5 + bevelling, ss * 4 + bevelling, ss * 6 - bevelling);
                    object3D.triangle(5).setUV(ss * 5 - bevelling, ss * 5 + bevelling, ss * 4 + bevelling, ss * 6 - bevelling, ss * 5 - bevelling, ss * 6 - bevelling);
                    break;
                case 4: // up left back
                    object3D.triangle(0).setUV(ss * 2 + bevelling, ss * 0 + bevelling, ss * 2 + bevelling, ss * 1 - bevelling, ss * 3 - bevelling, ss * 1 - bevelling);
                    object3D.triangle(1).setUV(ss * 2 + bevelling, ss * 0 + bevelling, ss * 3 - bevelling, ss * 1 - bevelling, ss * 3 - bevelling, ss * 0 + bevelling);
                    object3D.triangle(2).setUV(ss * 0 + bevelling, ss * 3 - bevelling, ss * 1 - bevelling, ss * 3 - bevelling, ss * 1 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(3).setUV(ss * 0 + bevelling, ss * 3 - bevelling, ss * 1 - bevelling, ss * 2 + bevelling, ss * 0 + bevelling, ss * 2 + bevelling);
                    object3D.triangle(4).setUV(ss * 5 + bevelling, ss * 5 - bevelling, ss * 6 - bevelling, ss * 5 - bevelling, ss * 6 - bevelling, ss * 4 + bevelling);
                    object3D.triangle(5).setUV(ss * 5 + bevelling, ss * 5 - bevelling, ss * 6 - bevelling, ss * 4 + bevelling, ss * 5 + bevelling, ss * 4 + bevelling);
                    break;
                case 5: // down back left
                    object3D.triangle(0).setUV(ss * 2 + bevelling, ss * 6 - bevelling, ss * 3 - bevelling, ss * 6 - bevelling, ss * 3 - bevelling, ss * 5 + bevelling);
                    object3D.triangle(1).setUV(ss * 2 + bevelling, ss * 6 - bevelling, ss * 3 - bevelling, ss * 5 + bevelling, ss * 2 + bevelling, ss * 5 + bevelling);
                    object3D.triangle(2).setUV(ss * 6 - bevelling, ss * 5 + bevelling, ss * 5 + bevelling, ss * 5 + bevelling, ss * 5 + bevelling, ss * 6 - bevelling);
                    object3D.triangle(3).setUV(ss * 6 - bevelling, ss * 5 + bevelling, ss * 5 + bevelling, ss * 6 - bevelling, ss * 6 - bevelling, ss * 6 - bevelling);
                    object3D.triangle(4).setUV(ss * 1 - bevelling, ss * 3 + bevelling, ss * 0 + bevelling, ss * 3 + bevelling, ss * 0 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(5).setUV(ss * 1 - bevelling, ss * 3 + bevelling, ss * 0 + bevelling, ss * 4 - bevelling, ss * 1 - bevelling, ss * 4 - bevelling);
                    break;
                case 6: // up front left
                    object3D.triangle(0).setUV(ss * 2 + bevelling, ss * 2 - bevelling, ss * 3 - bevelling, ss * 2 - bevelling, ss * 3 - bevelling, ss * 1 + bevelling);
                    object3D.triangle(1).setUV(ss * 2 + bevelling, ss * 2 - bevelling, ss * 3 - bevelling, ss * 1 + bevelling, ss * 2 + bevelling, ss * 1 + bevelling);
                    object3D.triangle(2).setUV(ss * 2 + bevelling, ss * 3 - bevelling, ss * 3 - bevelling, ss * 3 - bevelling, ss * 3 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(3).setUV(ss * 2 + bevelling, ss * 3 - bevelling, ss * 3 - bevelling, ss * 2 + bevelling, ss * 2 + bevelling, ss * 2 + bevelling);
                    object3D.triangle(4).setUV(ss * 1 + bevelling, ss * 3 - bevelling, ss * 2 - bevelling, ss * 3 - bevelling, ss * 2 - bevelling, ss * 2 + bevelling);
                    object3D.triangle(5).setUV(ss * 1 + bevelling, ss * 3 - bevelling, ss * 2 - bevelling, ss * 2 + bevelling, ss * 1 + bevelling, ss * 2 + bevelling);
                    break;
                case 7: // down left front
                    //object3D.triangle(0).setUV(ss * 3 + bevelling,ss * 6 + bevelling, ss * 3 + bevelling,ss * 7 - bevelling, ss * 4 - bevelling, ss * 7 - bevelling);
                    object3D.triangle(0).setUV(ss * 2 + bevelling, ss * 4 + bevelling, ss * 2 + bevelling, ss * 5 - bevelling, ss * 3 - bevelling, ss * 5 - bevelling);
                    object3D.triangle(1).setUV(ss * 2 + bevelling, ss * 4 + bevelling, ss * 3 - bevelling, ss * 5 - bevelling, ss * 3 - bevelling, ss * 4 + bevelling);
                    object3D.triangle(2).setUV(ss * 2 - bevelling, ss * 3 + bevelling, ss * 1 + bevelling, ss * 3 + bevelling, ss * 1 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(3).setUV(ss * 2 - bevelling, ss * 3 + bevelling, ss * 1 + bevelling, ss * 4 - bevelling, ss * 2 - bevelling, ss * 4 - bevelling);
                    object3D.triangle(4).setUV(ss * 3 - bevelling, ss * 3 + bevelling, ss * 2 + bevelling, ss * 3 + bevelling, ss * 2 + bevelling, ss * 4 - bevelling);
                    object3D.triangle(5).setUV(ss * 3 - bevelling, ss * 3 + bevelling, ss * 2 + bevelling, ss * 4 - bevelling, ss * 3 - bevelling, ss * 4 - bevelling);
                    break;
            }
        }
    }
    /**
     * Sticker to part map.<br>
     * (the number before the dot indicates the part,
     * the number after the dot indicates the sticker.)
     * <pre>
     *         +---+---+
     *         |4.0|2.0|
     *         +--- ---+ 
     *         |6.0|0.0|   
     * +---+---+---+---+---+---+---+---+
     * |4.1|6.2|6.1|0.2|0.1|2.2|2.1|4.2|
     * +--- ---+--- ---+--- ---+--- ---+
     * |5.2|7.1|7.2|1.1|1.2|3.1|3.2|5.1|
     * +---+---+---+---+---+---+---+---+
     *         |7.0|1.0|
     *         +--- ---+
     *         |5.0|3.0|
     *         +---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 2, 1, 3, // right
        4, 2, 6, 0, // up
        6, 0, 7, 1, // front
        4, 6, 5, 7, // left
        7, 1, 5, 3, // down
        2, 4, 3, 5 // back
    };

    /**
     * Gets the part which holds the indicated sticker.
     * The sticker index is interpreted according to this
     * scheme:
     * <pre>
     *         +---+---+
     *         | 4 | 5 |
     *         +--- ---+ 
     *         | 6 | 7 |   
     * +---+---+---+---+---+---+---+---+
     * |12 |13 | 8 | 9 | 0 | 1 |20 |21 |
     * +--- ---+--- ---+--- ---+--- ---+
     * |14 |15 |10 |11 | 2 | 3 |22 |23 |
     * +---+---+---+---+---+---+---+---+
     *         |16 |17 |
     *         +--- ---+
     *         |18 |19 |
     *         +---+---+
     * </pre>
     */
    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    /**
     * Maps stickers to the faces of the parts.
     * <pre>
     *         +---+---+
     *         |4.0|2.0|
     *         +--- ---+ 
     *         |6.0|0.0|   
     * +---+---+---+---+---+---+---+---+
     * |4.1|6.2|6.1|0.2|0.1|2.2|2.1|4.2|
     * +--- ---+--- ---+--- ---+--- ---+
     * |5.2|7.1|7.2|1.1|1.2|3.1|3.2|5.1|
     * +---+---+---+---+---+---+---+---+
     *         |7.0|1.0|
     *         +--- ---+
     *         |5.0|3.0|
     *         +---+---+
     * </pre>
     */
    private final static int[] stickerToFaceMap = {
        1, 2, 2, 1, // right
        0, 0, 0, 0, // up
        1, 2, 2, 1, // front
        1, 2, 2, 1, // left
        0, 0, 0, 0, // down
        1, 2, 2, 1 // back
    };

    @Override
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex] * 2;
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
    protected int getStickerCount() {
        return 6 * 2 * 2;
    }

    @Override
    public CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{4, 4, 4, 4, 4, 4});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 2 * 2, 1 * 2 * 2, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 2 * 2, 2 * 2 * 2, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 2 * 2, 3 * 2 * 2, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 2 * 2, 4 * 2 * 2, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 2 * 2, 5 * 2 * 2, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 2 * 2, 6 * 2 * 2, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    @Override
    protected void initActions(idx3d_Scene scene) {
        int i, j;
        PartAction action;

        // Corners
        for (i = 0; i < 8; i++) {
            int index = i;
            for (j = 0; j < 3; j++) {
                action = new AbstractCube3D.PartAction(
                        index, j, getStickerIndexForPart(index, j));

                scene.addMouseListener(parts[cornerOffset + i].triangle(j * 2), action);
                scene.addMouseListener(parts[cornerOffset + i].triangle(j * 2 + 1), action);
                switch (j) {
                    case 0: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(15), a0);
                        scene.addSwipeListener(parts[index].triangle(16), a1);
                        scene.addSwipeListener(parts[index].triangle(17), a0);
                        scene.addSwipeListener(parts[index].triangle(18), a1);
                        break;
                    }
                    case 1: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(19), a0);
                        scene.addSwipeListener(parts[index].triangle(20), a1);
                        scene.addSwipeListener(parts[index].triangle(21), a0);
                        scene.addSwipeListener(parts[index].triangle(22), a1);
                        break;
                    }
                    case 2: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(23), a0);
                        scene.addSwipeListener(parts[index].triangle(24), a1);
                        scene.addSwipeListener(parts[index].triangle(25), a0);
                        scene.addSwipeListener(parts[index].triangle(26), a1);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Specifies how many pixels are cut off from the stickers image
     * for each sticker.
     */
    @Override
    public void setStickerBeveling(float newValue) {
        bevelling = newValue;
        initCornerUVMap();
    }

    @Override
    public CubeKind getKind() {
        return CubeKind.POCKET;
    }
}
