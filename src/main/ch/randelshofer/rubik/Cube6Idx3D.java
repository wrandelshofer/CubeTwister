/*
 * @(#)Cube6Idx3D.java  1.1  2010-04-04
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import idx3d.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Simplified geometrical representation of {@link Cube6} in three dimensions.
 * <p>
 * The representation is simplified in the sense that all stickers of the
 * cube are square. In a real physical representation, such as a V-Cube 6, 
 * the stickers on the edge parts need to be rectangular, and the stickers on 
 * the corner parts need to be larger than those on the center parts.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0.1 2008-12-19 Fixed UV-Values for the right-down edges.
 * <br>1.0 2008-10-14 Created.
 */
public class Cube6Idx3D extends AbstractCube6Idx3D {

    private final static int STICKER_COUNT = 6 * 6 * 6;
    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one 18-th of the image width.
     */
    private final static float ss = imageWidth / 18f;
    /**
     * Bevelling is one pixel of a sticker image.
     */
    private float bevel;

    @Override
    public void init() {
        bevel = 3f / 512f;
        super.init();
    }
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;

    @Override
    protected void initCorners() {
        int i, j, part;
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        //8:lluf      lldf       rruf      rrdf
                        -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][]{
                        // Faces with stickers
                        {0, 2, 3, 1}, //Up face      The sequence of these faces
                        {22, 20, 18, 16}, //Front face   is relevant, for method
                        {15, 14, 8, 9}, //Left face    updateStickersFillColor().

                        // Edges with swipe actions.
                        {1, 3, 19, 17}, //Up Back
                        {2, 10, 11, 3}, //Up Right

                        {20, 12, 10, 18}, //Front Right
                        {6, 4, 20, 22}, //Front Down

                        {23, 15, 9, 17}, //Left Back
                        {7, 6, 14, 15}, //Left Down

                        // Edges without actions.
                        {0, 1, 9, 8}, //Up Left
                        {4, 5, 13, 12}, //Back Right
                        {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb
                        {16, 18, 2, 0}, //Up Front
                        {21, 19, 11, 13}, //Right Back
                        {22, 16, 8, 14}, //Front Left

                        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                        {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
                        {23, 7, 15}, //Bottom Back Left lddb ldbb lldb
                        //{21,13, 5}, //Bottom Right Back rddb rrdb rdbb

                        {16, 0, 8}, //Top Front Left luuf luff lluf
                        {18, 10, 2}, //Top Right Front ruuf rruf ruff
                        {22, 14, 6}, //Top Left Back luub llub lubb
                        {20, 4, 12}, //Top Back Right ruub rubb rrub



                        // Cut Off Faces: The following faces need only be drawn,
                        //                when a face layer of the cube is being twisted.
                        {12, 13, 11, 10}, //Right
                        {17, 19, 21, 23}, //Bottom
                        {4, 6, 7, 5},     //Back
                    };
        }
        for (part = 0; part < cornerCount; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i = 0; i < CORNER_VERTS.length / 3; i++) {
                object3D.addVertex(
                        CORNER_VERTS[i * 3], CORNER_VERTS[i * 3 + 1], CORNER_VERTS[i * 3 + 2]);
            }
            for (i = 0; i < CORNER_FACES.length; i++) {
                for (j = 2; j < CORNER_FACES[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            object3D.vertex(CORNER_FACES[i][0]),
                            object3D.vertex(CORNER_FACES[i][j - 1]),
                            object3D.vertex(CORNER_FACES[i][j]));

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
     * Initalizes the Corner UV Map.
     * <pre>
     *    0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18
     *  0                         +---+---+---+---+---+---+
     *                            |4.0|               |2.0|
     *  1                         +---+               +---+
     *                            |                       |
     *  2                         +                       +
     *                            |                       |
     *  3                         +           u           +
     *                            |                       |
     *  4                         +                       +
     *                            |                       |
     *  5                         +---+               +---+
     *                         ufl|6.0|               |0.0|urf
     *  6 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+.......................+
     *    |4.1|               |6.2|6.1|               |0.2|0.1|               |2.2|                       .
     *  7 +---+               +---+---+               +---+---+               +---+                       .
     *    |                       |                       |                       |                       .
     *  8 +                       +                       +                       +                       .
     *    |                       |                       |                       |                       .
     *  9 +           l           +           f           +           r           +           b           .
     *    |                       |                       |                       |                       .
     * 10 +                       +                       +                       +                       .
     *    |                       |                       |                       |                       .
     * 11 +---+               +---+---+               +---+---+               +---+                       .
     *    |5.2|               |7.1|7.2|               |1.1|1.2|               |3.1|                       .
     * 12 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+.......................+
     *                         dlf|7.0|               |1.0|2.1|               |4.2|       |
     * 13                         +---+               +---+---+               +---+       |
     *                            |                       |                       |       |
     * 14                         +                       +                       +       |
     *                            |                       |                       |       |
     * 15                         +           d           +           b           +    &lt;--+
     *                            |                       |                       |
     * 16                         +                       +                       +       
     *                            |                       |                       |       
     * 17                         +---+               +---+---+               +---+
     *                            |5.0|               |3.0|3.2|               |5.1|
     * 18                         +---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Object object3D = parts[cornerOffset + part];
            switch (part) {
                case 0: // up right front
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * 6 - bevel, ss * 12 - bevel, ss * 5 + bevel, ss * 11 + bevel, ss * 5 + bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * 6 - bevel, ss * 11 + bevel, ss * 5 + bevel, ss * 11 + bevel, ss * 6 - bevel);
                    object3D.triangle(2).setUV(ss * 12 + bevel, ss * 7 - bevel, ss * 13 - bevel, ss * 7 - bevel, ss * 13 - bevel, ss * 6 + bevel);
                    object3D.triangle(3).setUV(ss * 12 + bevel, ss * 7 - bevel, ss * 13 - bevel, ss * 6 + bevel, ss * 12 + bevel, ss * 6 + bevel);
                    object3D.triangle(4).setUV(ss * 11 + bevel, ss * 7 - bevel, ss * 12 - bevel, ss * 7 - bevel, ss * 12 - bevel, ss * 6 + bevel);
                    object3D.triangle(5).setUV(ss * 11 + bevel, ss * 7 - bevel, ss * 12 - bevel, ss * 6 + bevel, ss * 11 + bevel, ss * 6 + bevel);
                    break;
                case 1: // down front right
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * 12 + bevel, ss * 11 + bevel, ss * 12 + bevel, ss * 11 + bevel, ss * 13 - bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * 12 + bevel, ss * 11 + bevel, ss * 13 - bevel, ss * 12 - bevel, ss * 13 - bevel);
                    object3D.triangle(2).setUV(ss * 12 - bevel, ss * 11 + bevel, ss * 11 + bevel, ss * 11 + bevel, ss * 11 + bevel, ss * 12 - bevel);
                    object3D.triangle(3).setUV(ss * 12 - bevel, ss * 11 + bevel, ss * 11 + bevel, ss * 12 - bevel, ss * 12 - bevel, ss * 12 - bevel);
                    object3D.triangle(4).setUV(ss * 13 - bevel, ss * 11 + bevel, ss * 12 + bevel, ss * 11 + bevel, ss * 12 + bevel, ss * 12 - bevel);
                    object3D.triangle(5).setUV(ss * 13 - bevel, ss * 11 + bevel, ss * 12 + bevel, ss * 12 - bevel, ss * 13 - bevel, ss * 12 - bevel);
                    break;
                case 2: // up back right
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * 0 + bevel, ss * 11 + bevel, ss * 0 + bevel, ss * 11 + bevel, ss * 1 - bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * 0 + bevel, ss * 11 + bevel, ss * 1 - bevel, ss * 12 - bevel, ss * 1 - bevel);
                    object3D.triangle(2).setUV(ss * 12 + bevel, ss * 13 - bevel, ss * 13 - bevel, ss * 13 - bevel, ss * 13 - bevel, ss * 12 + bevel);
                    object3D.triangle(3).setUV(ss * 12 + bevel, ss * 13 - bevel, ss * 13 - bevel, ss * 12 + bevel, ss * 12 + bevel, ss * 12 + bevel);
                    object3D.triangle(4).setUV(ss * 17 + bevel, ss * 7 - bevel, ss * 18 - bevel, ss * 7 - bevel, ss * 18 - bevel, ss * 6 + bevel);
                    object3D.triangle(5).setUV(ss * 17 + bevel, ss * 7 - bevel, ss * 18 - bevel, ss * 6 + bevel, ss * 17 + bevel, ss * 6 + bevel);
                    break;
                case 3: // down right back
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * 18 - bevel, ss * 12 - bevel, ss * 17 + bevel, ss * 11 + bevel, ss * 17 + bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * 18 - bevel, ss * 11 + bevel, ss * 17 + bevel, ss * 11 + bevel, ss * 18 - bevel);
                    object3D.triangle(2).setUV(ss * 18 - bevel, ss * 11 + bevel, ss * 17 + bevel, ss * 11 + bevel, ss * 17 + bevel, ss * 12 - bevel);
                    object3D.triangle(3).setUV(ss * 18 - bevel, ss * 11 + bevel, ss * 17 + bevel, ss * 12 - bevel, ss * 18 - bevel, ss * 12 - bevel);
                    object3D.triangle(4).setUV(ss * 13 - bevel, ss * 17 + bevel, ss * 12 + bevel, ss * 17 + bevel, ss * 12 + bevel, ss * 18 - bevel);
                    object3D.triangle(5).setUV(ss * 13 - bevel, ss * 17 + bevel, ss * 12 + bevel, ss * 18 - bevel, ss * 13 - bevel, ss * 18 - bevel);
                    break;
                case 4: // up left back
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * 0 + bevel, ss * 6 + bevel, ss * 1 - bevel, ss * 7 - bevel, ss * 1 - bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * 0 + bevel, ss * 7 - bevel, ss * 1 - bevel, ss * 7 - bevel, ss * 0 + bevel);
                    object3D.triangle(2).setUV(ss * 0 + bevel, ss * 7 - bevel, ss * 1 - bevel, ss * 7 - bevel, ss * 1 - bevel, ss * 6 + bevel);
                    object3D.triangle(3).setUV(ss * 0 + bevel, ss * 7 - bevel, ss * 1 - bevel, ss * 6 + bevel, ss * 0 + bevel, ss * 6 + bevel);
                    object3D.triangle(4).setUV(ss * 17 + bevel, ss * 13 - bevel, ss * 18 - bevel, ss * 13 - bevel, ss * 18 - bevel, ss * 12 + bevel);
                    object3D.triangle(5).setUV(ss * 17 + bevel, ss * 13 - bevel, ss * 18 - bevel, ss * 12 + bevel, ss * 17 + bevel, ss * 12 + bevel);
                    break;
                case 5: // down back left
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * 18 - bevel, ss * 7 - bevel, ss * 18 - bevel, ss * 7 - bevel, ss * 17 + bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * 18 - bevel, ss * 7 - bevel, ss * 17 + bevel, ss * 6 + bevel, ss * 17 + bevel);
                    object3D.triangle(2).setUV(ss * 18 - bevel, ss * 17 + bevel, ss * 17 + bevel, ss * 17 + bevel, ss * 17 + bevel, ss * 18 - bevel);
                    object3D.triangle(3).setUV(ss * 18 - bevel, ss * 17 + bevel, ss * 17 + bevel, ss * 18 - bevel, ss * 18 - bevel, ss * 18 - bevel);
                    object3D.triangle(4).setUV(ss * 1 - bevel, ss * 11 + bevel, ss * 0 + bevel, ss * 11 + bevel, ss * 0 + bevel, ss * 12 - bevel);
                    object3D.triangle(5).setUV(ss * 1 - bevel, ss * 11 + bevel, ss * 0 + bevel, ss * 12 - bevel, ss * 1 - bevel, ss * 12 - bevel);
                    break;
                case 6: // up front left
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * 6 - bevel, ss * 7 - bevel, ss * 6 - bevel, ss * 7 - bevel, ss * 5 + bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * 6 - bevel, ss * 7 - bevel, ss * 5 + bevel, ss * 6 + bevel, ss * 5 + bevel);
                    object3D.triangle(2).setUV(ss * 6 + bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 6 + bevel);
                    object3D.triangle(3).setUV(ss * 6 + bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 6 + bevel, ss * 6 + bevel, ss * 6 + bevel);
                    object3D.triangle(4).setUV(ss * 5 + bevel, ss * 7 - bevel, ss * 6 - bevel, ss * 7 - bevel, ss * 6 - bevel, ss * 6 + bevel);
                    object3D.triangle(5).setUV(ss * 5 + bevel, ss * 7 - bevel, ss * 6 - bevel, ss * 6 + bevel, ss * 5 + bevel, ss * 6 + bevel);
                    break;
                case 7: // down left front
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * 12 + bevel, ss * 6 + bevel, ss * 13 - bevel, ss * 7 - bevel, ss * 13 - bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * 12 + bevel, ss * 7 - bevel, ss * 13 - bevel, ss * 7 - bevel, ss * 12 + bevel);
                    object3D.triangle(2).setUV(ss * 6 - bevel, ss * 11 + bevel, ss * 5 + bevel, ss * 11 + bevel, ss * 5 + bevel, ss * 12 - bevel);
                    object3D.triangle(3).setUV(ss * 6 - bevel, ss * 11 + bevel, ss * 5 + bevel, ss * 12 - bevel, ss * 6 - bevel, ss * 12 - bevel);
                    object3D.triangle(4).setUV(ss * 7 - bevel, ss * 11 + bevel, ss * 6 + bevel, ss * 11 + bevel, ss * 6 + bevel, ss * 12 - bevel);
                    object3D.triangle(5).setUV(ss * 7 - bevel, ss * 11 + bevel, ss * 6 + bevel, ss * 12 - bevel, ss * 7 - bevel, ss * 12 - bevel);
                    break;
            }
        }
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;

    @Override
    protected void initEdges() {
        if (EDGE_VERTS == null) {
            EDGE_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
        }
        if (EDGE_FACES == null) {
            EDGE_FACES = new int[][]{
                        {0, 2, 3, 1}, //Front
                        {22, 20, 18, 16}, //Up

                        // Inner edges of the main cubicle. We assign swipe actions to these.
                        {8, 0, 1, 9}, //Front Right
                        {2, 10, 11, 3}, //Front Left
                        {1, 3, 19, 17}, //Down Front

                        {6, 4, 20, 22}, //Top Back
                        {20, 12, 10, 18}, //Top Left
                        {14, 22, 16, 8}, //Top Right

                        // Outer edges of the main cubicle. We assign no actions to these.
                        {16, 18, 2, 0}, //Top Front
                        //{23, 21, 5, 7},   //Bottom Back lddb rddb rdbb ldbb

                        //
                        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                        {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
                        //{23, 7,15}, //Bottom Back Left lddb ldbb lldb
                        //{21,13, 5}, //Bottom Right Back rddb rrdb rdbb

                        {16, 0, 8}, //Top Front Left luuf luff lluf
                        {18, 10, 2}, //Top Right Front ruuf rruf ruff
                        {22, 14, 6}, //Top Left Back luub llub lubb
                        {20, 4, 12}, //Top Back Right ruub rubb rrub

                        {4, 5, 13, 12}, //Back Right
                        {7, 6, 14, 15}, //Back Left
                        {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
                        {17, 23, 15, 9}, //Bottom Left lddf lddb lldb lldf

                        // Cut Off Faces: The following faces need only be drawn,
                        //                when a layer of the cube is being twisted.
                        {14, 8, 9, 15}, //Left
                        {12, 13, 11, 10}, //Right
                        {17, 19, 21, 23}, //Bottom
                        {4, 6, 7, 5},     //Back
                    };
        }
        for (int part = 0; part < edgeCount; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (int i = 0; i < EDGE_VERTS.length / 3; i++) {
                object3D.addVertex(
                        EDGE_VERTS[i * 3], EDGE_VERTS[i * 3 + 1], EDGE_VERTS[i * 3 + 2]);
            }
            for (int i = 0; i < EDGE_FACES.length; i++) {
                for (int j = 2; j < EDGE_FACES[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            object3D.vertex(EDGE_FACES[i][0]),
                            object3D.vertex(EDGE_FACES[i][j - 1]),
                            object3D.vertex(EDGE_FACES[i][j]));
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
            parts[edgeOffset + part] = object3D;
        }
        initEdgeUVMap();
    }

    /**
     * Initializes the Edge UV Map.
     * <pre>
     *    0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18
     *  0                         +---+---+---+---+---+---+
     *                            |   |   |3.1|15 |   |   |
     *  1                         +---+---+---+---+---+---+
     *                            |   |               |   | 
     *  2                         +---+               +---+
     *                            |6.0|               |0.0|
     *  3                         +---+       u       +---+
     *                            |18 |               |12 |
     *  4                         +---+               +---+
     *                            |   |               |   | 
     *  5                         +---+---+---+---+---+---+
     *                            |   |   |9.1|21 |   |   |      
     *  6 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+.......................+
     *    |   |   |6.1|18 |   |   |   |   |9.0|21 |   |   |   |   |12 |0.1|   |   |               '
     *  7 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+               '
     *    |   |               |   |   |               |   |   |               |   |               '
     *  8 +---+               +---+---+               +---+---+               +---+               '
     *    |19 |               |22 |22 |               |13 |13 |               |16 |               '
     *  9 +---+       l       +---+---+       f       +---+---+       r       +---+       b       '
     *    |7.0|               10.0|10.1               |1.1|1.0|               |4.0|               '
     * 10 +---+               +---+---+               +---+---+               +---+               '
     *    |   |               |   |   |               |   |   |               |   |               '
     * 11 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+               '
     *    |   |   |8.1|20 |   |   |   |   11.0|23 |   |   |   |   |14 |2.1|   |   |               '
     * 12 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+.......................+
     *                            |   |   11.1|23 |   |   |   |   |15 |3.0|   |   |       |
     * 13                         +---+---+---+---+---+---+---+---+---+---+---+---+       |
     *                            |   |               |   |   |               |   |       |
     * 14                         +---+               +---+---+               +---+       |
     *                            |20 |               |14 |16 |               |19 |       |
     * 15                         +---+       d       +---+---+       b       +---+    &lt;--+
     *                            |8.0|               |2.0|4.1|               |7.1|
     * 16                         +---+               +---+---+               +---+       
     *                            |   |               |   |   |               |   |       
     * 17                         +---+---+---+---+---+---+---+---+---+---+---+---+
     *                            |   |   |5.1|17 |   |   |   |   |17 |5.0|   |   |
     * 18                         +---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initEdgeUVMap() {
        for (int part = 0; part < edgeCount; part++) {
            idx3d_Object object3D = parts[edgeOffset + part];
            //int m = part / 12;
            int m;
            switch (part % 24) {
                case 12:
                case 13:
                case 2:
                case 3:
                case 4:
                case 17:
                case 6:
                case 19:
                case 20:
                case 21:
                case 10:
                case 11:
                    m = 1 + part / 24;
                    break;
                default:
                    m = 0 - part / 24;
                    break;
            }
            switch (part % 12) {
                case 0: // up right
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * (3 + m) - bevel, ss * 12 - bevel, ss * (2 + m) + bevel, ss * 11 + bevel, ss * (2 + m) + bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * (3 + m) - bevel, ss * 11 + bevel, ss * (2 + m) + bevel, ss * 11 + bevel, ss * (3 + m) - bevel);
                    object3D.triangle(2).setUV(ss * (15 - m) + bevel, ss * 7 - bevel, ss * (16 - m) - bevel, ss * 7 - bevel, ss * (16 - m) - bevel, ss * 6 + bevel);
                    object3D.triangle(3).setUV(ss * (15 - m) + bevel, ss * 7 - bevel, ss * (16 - m) - bevel, ss * 6 + bevel, ss * (15 - m) + bevel, ss * 6 + bevel);
                    break;
                case 1: // right front
                    object3D.triangle(0).setUV(ss * 12 + bevel, ss * (9 - m) + bevel, ss * 12 + bevel, ss * (10 - m) - bevel, ss * 13 - bevel, ss * (10 - m) - bevel);
                    object3D.triangle(1).setUV(ss * 12 + bevel, ss * (9 - m) + bevel, ss * 13 - bevel, ss * (10 - m) - bevel, ss * 13 - bevel, ss * (9 - m) + bevel);
                    object3D.triangle(2).setUV(ss * 11 + bevel, ss * (9 - m) + bevel, ss * 11 + bevel, ss * (10 - m) - bevel, ss * 12 - bevel, ss * (10 - m) - bevel);
                    object3D.triangle(3).setUV(ss * 11 + bevel, ss * (9 - m) + bevel, ss * 12 - bevel, ss * (10 - m) - bevel, ss * 12 - bevel, ss * (9 - m) + bevel);
                    break;
                case 2: // down right
                    object3D.triangle(0).setUV(ss * 12 - bevel, ss * (15 + m) - bevel, ss * 12 - bevel, ss * (14 + m) + bevel, ss * 11 + bevel, ss * (14 + m) + bevel);
                    object3D.triangle(1).setUV(ss * 12 - bevel, ss * (15 + m) - bevel, ss * 11 + bevel, ss * (14 + m) + bevel, ss * 11 + bevel, ss * (15 + m) - bevel);
                    object3D.triangle(2).setUV(ss * (15 + m) - bevel, ss * 11 + bevel, ss * (14 + m) + bevel, ss * 11 + bevel, ss * (14 + m) + bevel, ss * 12 - bevel);
                    object3D.triangle(3).setUV(ss * (15 + m) - bevel, ss * 11 + bevel, ss * (14 + m) + bevel, ss * 12 - bevel, ss * (15 + m) - bevel, ss * 12 - bevel);
                    break;
                case 3: // back up
                    object3D.triangle(0).setUV(ss * (15 + m) - bevel, ss * 12 + bevel, ss * (14 + m) + bevel, ss * 12 + bevel, ss * (14 + m) + bevel, ss * 13 - bevel);
                    object3D.triangle(1).setUV(ss * (15 + m) - bevel, ss * 12 + bevel, ss * (14 + m) + bevel, ss * 13 - bevel, ss * (15 + m) - bevel, ss * 13 - bevel);
                    object3D.triangle(2).setUV(ss * (9 - m) + bevel, ss * 1 - bevel, ss * (10 - m) - bevel, ss * 1 - bevel, ss * (10 - m) - bevel, ss * 0 + bevel);
                    object3D.triangle(3).setUV(ss * (9 - m) + bevel, ss * 1 - bevel, ss * (10 - m) - bevel, ss * 0 + bevel, ss * (9 - m) + bevel, ss * 0 + bevel);
                    break;
                case 4: // right back
                    object3D.triangle(0).setUV(ss * 18 - bevel, ss * (9 + m) - bevel, ss * 18 - bevel, ss * (8 + m) + bevel, ss * 17 + bevel, ss * (8 + m) + bevel);
                    object3D.triangle(1).setUV(ss * 18 - bevel, ss * (9 + m) - bevel, ss * 17 + bevel, ss * (8 + m) + bevel, ss * 17 + bevel, ss * (9 + m) - bevel);
                    object3D.triangle(2).setUV(ss * 13 - bevel, ss * (15 + m) - bevel, ss * 13 - bevel, ss * (14 + m) + bevel, ss * 12 + bevel, ss * (14 + m) + bevel);
                    object3D.triangle(3).setUV(ss * 13 - bevel, ss * (15 + m) - bevel, ss * 12 + bevel, ss * (14 + m) + bevel, ss * 12 + bevel, ss * (15 + m) - bevel);
                    break;
                case 5: // back down
                    object3D.triangle(0).setUV(ss * (15 - m) + bevel, ss * 18 - bevel, ss * (16 - m) - bevel, ss * 18 - bevel, ss * (16 - m) - bevel, ss * 17 + bevel);
                    object3D.triangle(1).setUV(ss * (15 - m) + bevel, ss * 18 - bevel, ss * (16 - m) - bevel, ss * 17 + bevel, ss * (15 - m) + bevel, ss * 17 + bevel);
                    object3D.triangle(2).setUV(ss * (9 + m) - bevel, ss * 17 + bevel, ss * (8 + m) + bevel, ss * 17 + bevel, ss * (8 + m) + bevel, ss * 18 - bevel);
                    object3D.triangle(3).setUV(ss * (9 + m) - bevel, ss * 17 + bevel, ss * (8 + m) + bevel, ss * 18 - bevel, ss * (9 + m) - bevel, ss * 18 - bevel);
                    break;
                case 6: // up left
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * (3 - m) + bevel, ss * 6 + bevel, ss * (4 - m) - bevel, ss * 7 - bevel, ss * (4 - m) - bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * (3 - m) + bevel, ss * 7 - bevel, ss * (4 - m) - bevel, ss * 7 - bevel, ss * (3 - m) + bevel);
                    object3D.triangle(2).setUV(ss * (3 - m) + bevel, ss * 7 - bevel, ss * (4 - m) - bevel, ss * 7 - bevel, ss * (4 - m) - bevel, ss * 6 + bevel);
                    object3D.triangle(3).setUV(ss * (3 - m) + bevel, ss * 7 - bevel, ss * (4 - m) - bevel, ss * 6 + bevel, ss * (3 - m) + bevel, ss * 6 + bevel);
                    break;
                case 7: // left back
                    object3D.triangle(0).setUV(ss * 0 + bevel, ss * (9 - m) + bevel, ss * 0 + bevel, ss * (10 - m) - bevel, ss * 1 - bevel, ss * (10 - m) - bevel);
                    object3D.triangle(1).setUV(ss * 0 + bevel, ss * (9 - m) + bevel, ss * 1 - bevel, ss * (10 - m) - bevel, ss * 1 - bevel, ss * (9 - m) + bevel);
                    object3D.triangle(2).setUV(ss * 17 + bevel, ss * (15 - m) + bevel, ss * 17 + bevel, ss * (16 - m) - bevel, ss * 18 - bevel, ss * (16 - m) - bevel);
                    object3D.triangle(3).setUV(ss * 17 + bevel, ss * (15 - m) + bevel, ss * 18 - bevel, ss * (16 - m) - bevel, ss * 18 - bevel, ss * (15 - m) + bevel);
                    break;
                case 8: // down left
                    object3D.triangle(0).setUV(ss * 6 + bevel, ss * (15 - m) + bevel, ss * 6 + bevel, ss * (16 - m) - bevel, ss * 7 - bevel, ss * (16 - m) - bevel);
                    object3D.triangle(1).setUV(ss * 6 + bevel, ss * (15 - m) + bevel, ss * 7 - bevel, ss * (16 - m) - bevel, ss * 7 - bevel, ss * (15 - m) + bevel);
                    object3D.triangle(2).setUV(ss * (3 + m) - bevel, ss * 11 + bevel, ss * (2 + m) + bevel, ss * 11 + bevel, ss * (2 + m) + bevel, ss * 12 - bevel);
                    object3D.triangle(3).setUV(ss * (3 + m) - bevel, ss * 11 + bevel, ss * (2 + m) + bevel, ss * 12 - bevel, ss * (3 + m) - bevel, ss * 12 - bevel);
                    break;
                case 9: // front up
                    object3D.triangle(0).setUV(ss * (9 + m) - bevel, ss * 6 + bevel, ss * (8 + m) + bevel, ss * 6 + bevel, ss * (8 + m) + bevel, ss * 7 - bevel);
                    object3D.triangle(1).setUV(ss * (9 + m) - bevel, ss * 6 + bevel, ss * (8 + m) + bevel, ss * 7 - bevel, ss * (9 + m) - bevel, ss * 7 - bevel);
                    object3D.triangle(2).setUV(ss * (9 + m) - bevel, ss * 5 + bevel, ss * (8 + m) + bevel, ss * 5 + bevel, ss * (8 + m) + bevel, ss * 6 - bevel);
                    object3D.triangle(3).setUV(ss * (9 + m) - bevel, ss * 5 + bevel, ss * (8 + m) + bevel, ss * 6 - bevel, ss * (9 + m) - bevel, ss * 6 - bevel);
                    break;
                case 10: // left front
                    object3D.triangle(0).setUV(ss * 6 - bevel, ss * (9 + m) - bevel, ss * 6 - bevel, ss * (8 + m) + bevel, ss * 5 + bevel, ss * (8 + m) + bevel);
                    object3D.triangle(1).setUV(ss * 6 - bevel, ss * (9 + m) - bevel, ss * 5 + bevel, ss * (8 + m) + bevel, ss * 5 + bevel, ss * (9 + m) - bevel);
                    object3D.triangle(2).setUV(ss * 7 - bevel, ss * (9 + m) - bevel, ss * 7 - bevel, ss * (8 + m) + bevel, ss * 6 + bevel, ss * (8 + m) + bevel);
                    object3D.triangle(3).setUV(ss * 7 - bevel, ss * (9 + m) - bevel, ss * 6 + bevel, ss * (8 + m) + bevel, ss * 6 + bevel, ss * (9 + m) - bevel);
                    break;
                case 11: // front down
                    //  object3D.triangle(0).setUV(ss * (10 - m) + bevel, ss * 11 - bevel, ss * (11 - m) - bevel, ss * 11 - bevel, ss * (11 - m) - bevel, ss * 10 + bevel);
                    //  object3D.triangle(1).setUV(ss * (10 - m) + bevel, ss * 11 - bevel, ss * (11 - m) - bevel, ss * 10 + bevel, ss * (10 - m) + bevel, ss * 10 + bevel);
                    //  object3D.triangle(2).setUV(ss * (10 - m) + bevel, ss * 12 - bevel, ss * (11 - m) - bevel, ss * 12 - bevel, ss * (11 - m) - bevel, ss * 11 + bevel);
                    //  object3D.triangle(3).setUV(ss * (10 - m) + bevel, ss * 12 - bevel, ss * (11 - m) - bevel, ss * 11 + bevel, ss * (10 - m) + bevel, ss * 11 + bevel);

                    object3D.triangle(0).setUV(ss * (9 - m) + bevel, ss * 12 - bevel, ss * (10 - m) - bevel, ss * 12 - bevel, ss * (10 - m) - bevel, ss * 11 + bevel);
                    object3D.triangle(1).setUV(ss * (9 - m) + bevel, ss * 12 - bevel, ss * (10 - m) - bevel, ss * 11 + bevel, ss * (9 - m) + bevel, ss * 11 + bevel);
                    object3D.triangle(2).setUV(ss * (9 - m) + bevel, ss * 13 - bevel, ss * (10 - m) - bevel, ss * 13 - bevel, ss * (10 - m) - bevel, ss * 12 + bevel);
                    object3D.triangle(3).setUV(ss * (9 - m) + bevel, ss * 13 - bevel, ss * (10 - m) - bevel, ss * 12 + bevel, ss * (9 - m) + bevel, ss * 12 + bevel);

                    break;
            }
        }
    }
    private static float[] SIDE_VERTS;
    private static int[][] SIDE_FACES;

    @Override
    protected void initSides() {
        if (SIDE_VERTS == null) {
            // Note: The side verts are longer towards the center to avoid
            // holes in the cube while twisting.
            SIDE_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
        }
        if (SIDE_FACES == null) {
            SIDE_FACES = new int[][]{
                        {0, 2, 3, 1}, //Front

                        // Inner edges of the main cubicle. We assign swipe actions to these.
                        {16, 18, 2, 0}, //Top Front
                        {1, 3, 19, 17}, //Bottom Front
                        {2, 10, 11, 3}, //Front Left
                        {8, 0, 1, 9}, //Front Right

                        // Outer edges of the main cubicle. We assign no actions to these.
                        {18, 20, 12, 10}, //Top Right
                        {22, 16, 8, 14}, //Top Left
                        {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
                        {17, 23, 15, 9}, //Bottom Left lddf lddb lldb lldf

                        //    {4, 6, 7, 5},     //Back
                        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                        {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
                        //    {23, 7,15}, //Bottom Back Left lddb ldbb lldb
                        //    {21,13, 5}, //Bottom Right Back rddb rrdb rdbb

                        {16, 0, 8}, //Top Front Left luuf luff lluf
                        {18, 10, 2}, //Top Right Front ruuf rruf ruff
                        //    {22,14, 6}, //Top Left Back luub llub lubb
                        //    {20, 4,12}, //Top Back Right ruub rubb rrub

                        //    {20, 22, 6, 4},   //Top Back

                        //    {23, 21, 5, 7},   //Bottom Back lddb rddb rdbb ldbb

                        //    {4, 5, 13, 12},   //Back Right
                        //    {7, 6, 14, 15},  //Back Left

                        // Cut Off Faces: The following faces need only be drawn,
                        //                when a layer of the cube is being twisted.
                        {16, 22, 20, 18}, //Top
                        {14, 8, 9, 15}, //Left
                        {12, 13, 11, 10}, //Right
                        {17, 19, 21, 23}, //Bottom
                    };
        }

        for (int part = 0; part < sideCount; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (int i = 0; i < SIDE_VERTS.length / 3; i++) {
                object3D.addVertex(
                        SIDE_VERTS[i * 3], SIDE_VERTS[i * 3 + 1], SIDE_VERTS[i * 3 + 2]);
            }
            for (int i = 0; i < SIDE_FACES.length; i++) {
                for (int j = 2; j < SIDE_FACES[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            object3D.vertex(SIDE_FACES[i][0]),
                            object3D.vertex(SIDE_FACES[i][j - 1]),
                            object3D.vertex(SIDE_FACES[i][j]));
                    object3D.addTriangle(triangle);
                //if (i == 0) triangle.setMaterial(STICKER_MATERIALS[part]);
                }
            }

            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker1 = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker1);
            object3D.triangle(1).setTriangleMaterial(sticker1);
            parts[sideOffset + part] = object3D;
        }
        initSideUVMap();
    }

    /**
     * Initializes the UV coordinates for the side parts.
     * <pre>
     *    0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18
     *  0                         +-------+---------------+
     *                            |                       |
     *  1                         +   +               +   |
     *                            |                       |
     *  2                         +   +   +---+           |
     *                            |       | 1 |   u       |
     *  3                         |       +---+           |
     *                            |                       |
     *  4                         |                       |
     *                            |                       |
     *  5                         |   +               +   |
     *                            |                       |
     *  6 +-----------------------+-------+---------------+---+---+---+---+---+---+.......................+
     *    |                       |                       |                       |                       '
     *  7 |   +              +    |   +               +   |   +               +   |                       '
     *    |                       |                       |                       |                       '
     *  8 |           +---+       |                       |                       |                       '
     *    |           | 3 |l      |               f       |               r       |                       '
     *  9 |           +---+       |       +---+           |           +---+       |           b           '
     *    |                       |       | 2 |           |           | 0 |       |                       '
     * 10 |                       |       +---+           |           +---+       |                       '
     *    |                       |                       |                       |                       '
     * 11 |   +              +    |   +               +   |   +               +   |                       '
     *    |                       |                       |                       |                       '
     * 12 +-----------------------+-------+---------------+---+---+---+---+---+---+.......................+
     *                            |                       |                       |     |
     * 13                         |   +               +   |   +               +   |     |
     *                            |                       |                       |     |
     * 14                         +           +---+       +       +---+           |     |
     *                            |           | 4 |d      |       | 5 |   b       |     |
     * 15                         +           +---+       +       +---+           |  &lt;--+
     *                            |                       |                       |
     * 16                         |                       |                       |
     *                            |                       |                       |
     * 17                         |   +               +   |   +               +   |
     *                            |                       |                       |
     * 18                         +-------+---------------+-----------------------+
     * 
     * Whereas each area marked by the + symbols contains side parts with the
     * following placements:
     * 
     * +---+---+---+---+
     * | 5  14  10   6 |
     * +               +
     * | 9   1   2  15 |
     * +               +
     * |13   0   3  11 |
     * +               +
     * | 4   8  12   7 |
     * +---+---+---+---+
     * </pre>
     */
    protected void initSideUVMap() {

        /**
         * UV coordinates for stickers on side parts.
         * First dimension = parts,
         * Second dimension = sticker coordinates
         * Third dimension = x and y coordinate values
         */
        int[] xcoords = {0, 0, 1, 1, -1, -1, 2, 2, 0, -1, 1, 2, 1, -1, 0, 2};
        int[] ycoords = {0, 1, 1, 0, -1, 2, 2, -1, -1, 1, 2, 0, -1, 0, 2, 1};
        for (int part = 0; part < sideCount; part++) {
            idx3d_Object object3D = parts[sideOffset + part];
            int mx = xcoords[part / 6];
            int my = ycoords[part / 6];
            switch (part % 6) {
                case 0: // right
                    object3D.triangle(0).setUV(ss * (15 - my) + bevel, ss * (9 - mx) + bevel, ss * (15 - my) + bevel, ss * (10 - mx) - bevel, ss * (16 - my) - bevel, ss * (10 - mx) - bevel);
                    object3D.triangle(1).setUV(ss * (15 - my) + bevel, ss * (9 - mx) + bevel, ss * (16 - my) - bevel, ss * (10 - mx) - bevel, ss * (16 - my) - bevel, ss * (9 - mx) + bevel);
                    break;
                case 1: // up
                    object3D.triangle(0).setUV(ss * (9 + my) - bevel, ss * (3 + mx) - bevel, ss * (9 + my) - bevel, ss * (2 + mx) + bevel, ss * (8 + my) + bevel, ss * (2 + mx) + bevel);
                    object3D.triangle(1).setUV(ss * (9 + my) - bevel, ss * (3 + mx) - bevel, ss * (8 + my) + bevel, ss * (2 + mx) + bevel, ss * (8 + my) + bevel, ss * (3 + mx) - bevel);
                    break;
                case 2: // front
                    object3D.triangle(0).setUV(ss * (9 + mx) - bevel, ss * (9 - my) + bevel, ss * (8 + mx) + bevel, ss * (9 - my) + bevel, ss * (8 + mx) + bevel, ss * (10 - my) - bevel);
                    object3D.triangle(1).setUV(ss * (9 + mx) - bevel, ss * (9 - my) + bevel, ss * (8 + mx) + bevel, ss * (10 - my) - bevel, ss * (9 + mx) - bevel, ss * (10 - my) - bevel);
                    break;
                case 3: // left
                    object3D.triangle(0).setUV(ss * (3 - mx) + bevel, ss * (9 + my) - bevel, ss * (4 - mx) - bevel, ss * (9 + my) - bevel, ss * (4 - mx) - bevel, ss * (8 + my) + bevel);
                    object3D.triangle(1).setUV(ss * (3 - mx) + bevel, ss * (9 + my) - bevel, ss * (4 - mx) - bevel, ss * (8 + my) + bevel, ss * (3 - mx) + bevel, ss * (8 + my) + bevel);
                    break;
                case 4: // down
                    object3D.triangle(0).setUV(ss * (9 - mx) + bevel, ss * (15 + my) - bevel, ss * (10 - mx) - bevel, ss * (15 + my) - bevel, ss * (10 - mx) - bevel, ss * (14 + my) + bevel);
                    object3D.triangle(1).setUV(ss * (9 - mx) + bevel, ss * (15 + my) - bevel, ss * (10 - mx) - bevel, ss * (14 + my) + bevel, ss * (9 - mx) + bevel, ss * (14 + my) + bevel);
                    break;
                case 5: // back
                    object3D.triangle(0).setUV(ss * (15 + my) - bevel, ss * (15 + mx) - bevel, ss * (15 + my) - bevel, ss * (14 + mx) + bevel, ss * (14 + my) + bevel, ss * (14 + mx) + bevel);
                    object3D.triangle(1).setUV(ss * (15 + my) - bevel, ss * (15 + mx) - bevel, ss * (14 + my) + bevel, ss * (14 + mx) + bevel, ss * (14 + my) + bevel, ss * (15 + mx) - bevel);
                    break;
            }

        }
    }
    /**
     * Sticker to part map.<br>
     * (the number before the dot indicates the part,
     * the number after the dot indicates the sticker.)
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |4.0|27 |3.1|15 |39 |2.0|
     *                           +---+---+---+---+---+---+
     *                           |30 |25  79  55  31 |24 |
     *                           +---+               +---+  
     *                           |6.0|49   1   7  85 |0.0|
     *                           +---+       u       +---+ 
     *                           |18 |73  19  13  61 |12 |
     *                           +---+               +---+ 
     *                           |42 |43  67  91  37 |36 |
     *                           +---+---+---+---+---+---+
     *                           |6.0|33 |9.1|21 |45 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |4.1|30 |6.1|18 |42 |6.2|6.1|33 |9.0|21 |45 |0.2|0.1|36 |12 |0.1|24 |2.2|2.1|39 |15 |3.0|27 |4.2|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |45  75  51  27 |46 |46 |32  86  62  38 |37 |37 |36  90  66  42 |40 |40 |29  83  59  35 |43 |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |19 |69  21  3.1 81 |22 |22 |56  8.3 14  92 |13 |13 |60 12.0 18  72 |16 |16 |53  5.2 11  89 |19 |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |7.0|93  15   9  57 10.0|10.1 80  2  20  68 |1.1|1.0|84   6   0  48 |4.0|4.1|77  23  17  65 |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |31 |39  63  87  33 |34 |34 |26  50  74  44 |25 |25 |30  54  78  24 |28 |28 |47  71  95  41 |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |5.2|32 |8.1|20 |44 |7.1|7.2|35 11.1|23 |47 |1.1|1.2|38 |14 |2.1|26 |3.1|3.2|41 |17 |5.0|29 |5.1|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |7.0|35 11.1|23 |47 |1.0|
     *                           +---+---+---+---+---+---+
     *                           |44 |46  76  52  28 |38 |
     *                           +---+               +---+ 
     *                           |20 |70  22   4  82 |14 |
     *                           +---+       d       +---+ 
     *                           |8.0|94  16  10  58 |2.0|
     *                           +---+               +---+  
     *                           |32 |40  64  88  34 |26 |
     *                           +---+---+---+---+---+---+
     *                           |5.0|29 |5.1|17 |41 |3.0|
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 36 + 8, 12 + 8, 0 + 8, 24 + 8, 2, //
        37 + 8, 36 + 56, 90 + 56, 66 + 56, 42 + 56, 40 + 8,//
        13 + 8, 60 + 56, 12 + 56, 18 + 56, 72 + 56, 16 + 8,//
        1 + 8, 84 + 56, 6 + 56, 0 + 56, 48 + 56, 4 + 8,//
        25 + 8, 30 + 56, 54 + 56, 78 + 56, 24 + 56, 28 + 8,//
        1, 38 + 8, 14 + 8, 2 + 8, 26 + 8, 3, // right
        //
        4, 27 + 8, 3 + 8, 15 + 8, 39 + 8, 2,//
        30 + 8, 25 + 56, 79 + 56, 55 + 56, 31 + 56, 24 + 8, //
        6 + 8, 49 + 56, 1 + 56, 7 + 56, 85 + 56, 0 + 8, //
        18 + 8, 73 + 56, 19 + 56, 13 + 56, 61 + 56, 12 + 8, //
        42 + 8, 43 + 56, 67 + 56, 91 + 56, 37 + 56, 36 + 8, //
        6, 33 + 8, 9 + 8, 21 + 8, 45 + 8, 0, // up
        //
        6, 33 + 8, 9 + 8, 21 + 8, 45 + 8, 0, //
        46 + 8, 32 + 56, 86 + 56, 62 + 56, 38 + 56, 37 + 8,//
        22 + 8, 56 + 56, 8 + 56, 14 + 56, 92 + 56, 13 + 8, //
        10 + 8, 80 + 56, 2 + 56, 20 + 56, 68 + 56, 1 + 8,//
        34 + 8, 26 + 56, 50 + 56, 74 + 56, 44 + 56, 25 + 8, //
        7, 35 + 8, 11 + 8, 23 + 8, 47 + 8, 1, // front
        //
        4, 30 + 8, 6 + 8, 18 + 8, 42 + 8, 6,//
        43 + 8, 45 + 56, 75 + 56, 51 + 56, 27 + 56, 46 + 8,//
        19 + 8, 69 + 56, 21 + 56, 3 + 56, 81 + 56, 22 + 8,//
        7 + 8, 93 + 56, 15 + 56, 9 + 56, 57 + 56, 10 + 8, //
        31 + 8, 39 + 56, 63 + 56, 87 + 56, 33 + 56, 34 + 8,//
        5, 32 + 8, 8 + 8, 20 + 8, 44 + 8, 7, // left
        //
        7, 35 + 8, 11 + 8, 23 + 8, 47 + 8, 1, //
        44 + 8, 46 + 56, 76 + 56, 52 + 56, 28 + 56, 38 + 8,//
        20 + 8, 70 + 56, 22 + 56, 4 + 56, 82 + 56, 14 + 8, //
        8 + 8, 94 + 56, 16 + 56, 10 + 56, 58 + 56, 2 + 8, //
        32 + 8, 40 + 56, 64 + 56, 88 + 56, 34 + 56, 26 + 8, //
        5, 29 + 8, 5 + 8, 17 + 8, 41 + 8, 3, // down
        //
        2, 39 + 8, 15 + 8, 3 + 8, 27 + 8, 4, //
        40 + 8, 29 + 56, 83 + 56, 59 + 56, 35 + 56, 43 + 8,//
        16 + 8, 53 + 56, 5 + 56, 11 + 56, 89 + 56, 19 + 8,//
        4 + 8, 77 + 56, 23 + 56, 17 + 56, 65 + 56, 7 + 8, //
        28 + 8, 47 + 56, 71 + 56, 95 + 56, 41 + 56, 31 + 8,//
        3, 41 + 8, 17 + 8, 5 + 8, 29 + 8, 5 // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, // right
        //
        0, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 0, // up
        //
        1, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 1, // front
        //
        1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, // left
        //
        0, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 0, // down
        //
        1, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 1, // back
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
        return 6 * 6 * 6;
    }

    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(), new int[]{36, 36, 36, 36, 36, 36});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 6 * 6, 1 * 6 * 6, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 6 * 6, 2 * 6 * 6, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 6 * 6, 3 * 6 * 6, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 6 * 6, 4 * 6 * 6, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 6 * 6, 5 * 6 * 6, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 6 * 6, 6 * 6 * 6, new Color(255, 70, 0)); // Back: Orange

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
        for (i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            for (j = 0; j < 3; j++) {
                action = new AbstractCube3D.PartAction(
                        i, j, getStickerIndexForPart(i, j));

                scene.addMouseListener(parts[index].triangle(j * 2), action);
                scene.addMouseListener(parts[index].triangle(j * 2 + 1), action);
                switch (j) {
                    case 0: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(6), a0);
                        scene.addSwipeListener(parts[index].triangle(7), a1);
                        scene.addSwipeListener(parts[index].triangle(8), a0);
                        scene.addSwipeListener(parts[index].triangle(9), a1);
                        break;
                    }
                    case 1: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(10), a0);
                        scene.addSwipeListener(parts[index].triangle(11), a1);
                        scene.addSwipeListener(parts[index].triangle(12), a0);
                        scene.addSwipeListener(parts[index].triangle(13), a1);
                        break;
                    }
                    case 2: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(14), a0);
                        scene.addSwipeListener(parts[index].triangle(15), a1);
                        scene.addSwipeListener(parts[index].triangle(16), a0);
                        scene.addSwipeListener(parts[index].triangle(17), a1);
                    }
                    break;
                }
            }
        }

        // Edges
        for (i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            for (j = 0; j < 2; j++) {
                action = new AbstractCube3D.PartAction(
                        index, j, getStickerIndexForPart(index, j));

                scene.addMouseListener(parts[index].triangle(j * 2), action);
                scene.addMouseListener(parts[index].triangle(j * 2 + 1), action);
                switch (j) {
                    case 0:{
                        SwipeAction a0=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(4), a0);
                        scene.addSwipeListener(parts[index].triangle(5), a1);
                        scene.addSwipeListener(parts[index].triangle(6), a0);
                        scene.addSwipeListener(parts[index].triangle(7), a1);
                        scene.addSwipeListener(parts[index].triangle(8), a0);
                        scene.addSwipeListener(parts[index].triangle(9), a1);
                        break;
                        }
                    case 1: {
                        SwipeAction a0=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f));
                        scene.addSwipeListener(parts[index].triangle(j * 2), a0);
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(parts[index].triangle(10), a0);
                        scene.addSwipeListener(parts[index].triangle(11), a1);
                        scene.addSwipeListener(parts[index].triangle(12), a0);
                        scene.addSwipeListener(parts[index].triangle(13), a1);
                        scene.addSwipeListener(parts[index].triangle(14), a0);
                        scene.addSwipeListener(parts[index].triangle(15), a1);
                        break;
                        }
                }
            }
        }

        // Sides
        for (i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            action = new AbstractCube3D.PartAction(
                    i + sideOffset, 0, getStickerIndexForPart(index, 0));

            scene.addMouseListener(parts[index].triangle(0), action);
            scene.addMouseListener(parts[index].triangle(1), action);
            SwipeAction a0=new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI / 2f + Math.PI / 4f));
            SwipeAction a1=new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) Math.PI / 2f);
            scene.addSwipeListener(parts[index].triangle(0), a0);
            scene.addSwipeListener(parts[index].triangle(1), a1);
            scene.addSwipeListener(parts[index].triangle(2), a0);
            scene.addSwipeListener(parts[index].triangle(3), a1);
            scene.addSwipeListener(parts[index].triangle(4), a0);
            scene.addSwipeListener(parts[index].triangle(5), a1);
            scene.addSwipeListener(parts[index].triangle(6), a0);
            scene.addSwipeListener(parts[index].triangle(7), a1);
            scene.addSwipeListener(parts[index].triangle(8), a0);
            scene.addSwipeListener(parts[index].triangle(9), a1);
        }

        for (i = 0; i < partCount; i++) {
            action = new AbstractCube3D.PartAction(
                    i, -1, -1);

            scene.addMouseListener(parts[i], action);
        }
    }

    /**
     * Specifies how many pixels are cut off from the stickers image
     * for each sticker.
     */
    @Override
    public void setStickerBeveling(float newValue) {
        bevel = newValue;
        initEdgeUVMap();
        initCornerUVMap();
        initSideUVMap();
    }
    @Override
    public CubeKind getKind() {
       return CubeKind.CUBE_6;
    }
}
