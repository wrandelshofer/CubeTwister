/*
 * @(#)ProfessorCubeIdx3D.java  2.2  2010-04-04
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import idx3d.*;
import java.awt.*;
import java.util.Arrays;

/**
 * ProfessorCubeIdx3D.
 *
 * @author  Werner Randelshofer
 * @version 2.2 2010-04-04 Added swipe actions to edges adjacent to stickers.
 * <br>2.1 2008-08-17 Streamlined with code of class VCube7Idx3D.
 * <br>2008-03-19 Fixed UV values for "rd" edges.
 * <br>2008-01-01 Adapted to changes in AbstractCube. 
 * <br>1.1 2007-12-27 Increased image width from 504 to 512.
 * <br>1.0 2005-12-31 Created.
 */
public class ProfessorCubeIdx3D extends AbstractProfessorCubeIdx3D {

    private final static int STICKER_COUNT = 6 * 5 * 5;
    /**
     * Image width is 510 pixels out of 512 pixels.
     */
    private final static float imageWidth = 510f / 512f;
    /**
     * Sticker size is one 15-th of the image width.
     */
    private final static float ss = imageWidth / 15f;
    /**
     * Bevelling is cut off from the texture of each sticker.
     */
    private float bev;

    @Override
    public void init() {
        bev = 3f / 512f;
        super.init();
    }
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;

    @Override
    protected void initCorners() {
        int i, j, part;
        if (CORNER_VERTS == null) {
            /*
            CORNER_VERTS = new float[]{
            //0:luff      ldff       ruff       rdff
            -6, 6, 7, -6, -6, 7, 6, 6, 7, 6, -6, 7,
            //4:rubb,    rdbb,       lubb,       ldbb
            6, 6, -7, 6, -6, -7, -6, 6, -7, -6, -6, -7,
            //8:lluf      lldf       rruf      rrdf
            -7, 6, 6, -7, -6, 6, 7, 6, 6, 7, -6, 6,
            //12:rrub,    rrdb,      llub,      lldb
            7, 6, -6, 7, -6, -6, -7, 6, -6, -7, -6, -6,
            //16:luuf     lddf       ruuf       rddf
            -6, 7, 6, -6, -7, 6, 6, 7, 6, 6, -7, 6,
            //20:ruub,    rddb,       luub,       lddb
            6, 7, -6, 6, -7, -6, -6, 7, -6, -6, -7, -6
            };*/
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
     *    0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
     *  0                     +---+---+---+---+---+
     *                        |4.0|           |2.0|  
     *  1                     +---+           +---+
     *                        |                   |
     *  2                     +                   +
     *                        |         u         |
     *  3                     +                   +
     *                        |                   |
     *  4                     +---+           +---+
     *                        |6.0|           |0.0|  
     *  5 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...................+
     *    |4.1|           |6.2|6.1|           |0.2|0.1|           |2.2|                   '
     *  6 +---+           +---+---+           +---+---+           +---+                   '
     *    |                   |                   |                   |                   '
     *  7 +                   +                   +                   +                   '
     *    |         l         |         f         |         r         |         b         '
     *  8 +                   +                   +                   +                   '
     *    |                   |                   |                   |                   '
     *  9 +---+           +---+---+           +---+---+           +---+                   '
     *    |5.2|           |7.1|7.2|           |1.1|1.2|           |3.1|                   '
     * 10 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...................+
     *                        |7.0|           |1.0|2.1|           |4.2|     |
     * 11                     +---+           +---+---+           +---+     |
     *                        |                   |                   |     |
     * 12                     +                   +                   +     |
     *                        |         d         |         b         |  &lt;--+
     * 13                     +                   +                   +     
     *                        |                   |                   |     
     * 14                     +---+           +---+---+           +---+
     *                        |5.0|           |3.0|3.2|           |5.1|
     * 15                     +---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Object object3D = parts[cornerOffset + part];
            switch (part) {
                case 0: // up right front
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * 5 - bev, ss * 10 - bev, ss * 4 + bev, ss * 9 + bev, ss * 4 + bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * 5 - bev, ss * 9 + bev, ss * 4 + bev, ss * 9 + bev, ss * 5 - bev);
                    object3D.triangle(2).setUV(ss * 10 + bev, ss * 6 - bev, ss * 11 - bev, ss * 6 - bev, ss * 11 - bev, ss * 5 + bev);
                    object3D.triangle(3).setUV(ss * 10 + bev, ss * 6 - bev, ss * 11 - bev, ss * 5 + bev, ss * 10 + bev, ss * 5 + bev);
                    object3D.triangle(4).setUV(ss * 9 + bev, ss * 6 - bev, ss * 10 - bev, ss * 6 - bev, ss * 10 - bev, ss * 5 + bev);
                    object3D.triangle(5).setUV(ss * 9 + bev, ss * 6 - bev, ss * 10 - bev, ss * 5 + bev, ss * 9 + bev, ss * 5 + bev);
                    break;
                case 1: // down front right
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * 10 + bev, ss * 9 + bev, ss * 10 + bev, ss * 9 + bev, ss * 11 - bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * 10 + bev, ss * 9 + bev, ss * 11 - bev, ss * 10 - bev, ss * 11 - bev);
                    object3D.triangle(2).setUV(ss * 10 - bev, ss * 9 + bev, ss * 9 + bev, ss * 9 + bev, ss * 9 + bev, ss * 10 - bev);
                    object3D.triangle(3).setUV(ss * 10 - bev, ss * 9 + bev, ss * 9 + bev, ss * 10 - bev, ss * 10 - bev, ss * 10 - bev);
                    object3D.triangle(4).setUV(ss * 11 - bev, ss * 9 + bev, ss * 10 + bev, ss * 9 + bev, ss * 10 + bev, ss * 10 - bev);
                    object3D.triangle(5).setUV(ss * 11 - bev, ss * 9 + bev, ss * 10 + bev, ss * 10 - bev, ss * 11 - bev, ss * 10 - bev);
                    break;
                case 2: // up back right
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * 0 + bev, ss * 9 + bev, ss * 0 + bev, ss * 9 + bev, ss * 1 - bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * 0 + bev, ss * 9 + bev, ss * 1 - bev, ss * 10 - bev, ss * 1 - bev);
                    object3D.triangle(2).setUV(ss * 10 + bev, ss * 11 - bev, ss * 11 - bev, ss * 11 - bev, ss * 11 - bev, ss * 10 + bev);
                    object3D.triangle(3).setUV(ss * 10 + bev, ss * 11 - bev, ss * 11 - bev, ss * 10 + bev, ss * 10 + bev, ss * 10 + bev);
                    object3D.triangle(4).setUV(ss * 14 + bev, ss * 6 - bev, ss * 15 - bev, ss * 6 - bev, ss * 15 - bev, ss * 5 + bev);
                    object3D.triangle(5).setUV(ss * 14 + bev, ss * 6 - bev, ss * 15 - bev, ss * 5 + bev, ss * 14 + bev, ss * 5 + bev);
                    break;
                case 3: // down right back
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * 15 - bev, ss * 10 - bev, ss * 14 + bev, ss * 9 + bev, ss * 14 + bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * 15 - bev, ss * 9 + bev, ss * 14 + bev, ss * 9 + bev, ss * 15 - bev);
                    object3D.triangle(2).setUV(ss * 15 - bev, ss * 9 + bev, ss * 14 + bev, ss * 9 + bev, ss * 14 + bev, ss * 10 - bev);
                    object3D.triangle(3).setUV(ss * 15 - bev, ss * 9 + bev, ss * 14 + bev, ss * 10 - bev, ss * 15 - bev, ss * 10 - bev);
                    object3D.triangle(4).setUV(ss * 11 - bev, ss * 14 + bev, ss * 10 + bev, ss * 14 + bev, ss * 10 + bev, ss * 15 - bev);
                    object3D.triangle(5).setUV(ss * 11 - bev, ss * 14 + bev, ss * 10 + bev, ss * 15 - bev, ss * 11 - bev, ss * 15 - bev);
                    break;
                case 4: // up left back
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * 0 + bev, ss * 5 + bev, ss * 1 - bev, ss * 6 - bev, ss * 1 - bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * 0 + bev, ss * 6 - bev, ss * 1 - bev, ss * 6 - bev, ss * 0 + bev);
                    object3D.triangle(2).setUV(ss * 0 + bev, ss * 6 - bev, ss * 1 - bev, ss * 6 - bev, ss * 1 - bev, ss * 5 + bev);
                    object3D.triangle(3).setUV(ss * 0 + bev, ss * 6 - bev, ss * 1 - bev, ss * 5 + bev, ss * 0 + bev, ss * 5 + bev);
                    object3D.triangle(4).setUV(ss * 14 + bev, ss * 11 - bev, ss * 15 - bev, ss * 11 - bev, ss * 15 - bev, ss * 10 + bev);
                    object3D.triangle(5).setUV(ss * 14 + bev, ss * 11 - bev, ss * 15 - bev, ss * 10 + bev, ss * 14 + bev, ss * 10 + bev);
                    break;
                case 5: // down back left
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * 15 - bev, ss * 6 - bev, ss * 15 - bev, ss * 6 - bev, ss * 14 + bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * 15 - bev, ss * 6 - bev, ss * 14 + bev, ss * 5 + bev, ss * 14 + bev);
                    object3D.triangle(2).setUV(ss * 15 - bev, ss * 14 + bev, ss * 14 + bev, ss * 14 + bev, ss * 14 + bev, ss * 15 - bev);
                    object3D.triangle(3).setUV(ss * 15 - bev, ss * 14 + bev, ss * 14 + bev, ss * 15 - bev, ss * 15 - bev, ss * 15 - bev);
                    object3D.triangle(4).setUV(ss * 1 - bev, ss * 9 + bev, ss * 0 + bev, ss * 9 + bev, ss * 0 + bev, ss * 10 - bev);
                    object3D.triangle(5).setUV(ss * 1 - bev, ss * 9 + bev, ss * 0 + bev, ss * 10 - bev, ss * 1 - bev, ss * 10 - bev);
                    break;
                case 6: // up front left
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * 5 - bev, ss * 6 - bev, ss * 5 - bev, ss * 6 - bev, ss * 4 + bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * 5 - bev, ss * 6 - bev, ss * 4 + bev, ss * 5 + bev, ss * 4 + bev);
                    object3D.triangle(2).setUV(ss * 5 + bev, ss * 6 - bev, ss * 6 - bev, ss * 6 - bev, ss * 6 - bev, ss * 5 + bev);
                    object3D.triangle(3).setUV(ss * 5 + bev, ss * 6 - bev, ss * 6 - bev, ss * 5 + bev, ss * 5 + bev, ss * 5 + bev);
                    object3D.triangle(4).setUV(ss * 4 + bev, ss * 6 - bev, ss * 5 - bev, ss * 6 - bev, ss * 5 - bev, ss * 5 + bev);
                    object3D.triangle(5).setUV(ss * 4 + bev, ss * 6 - bev, ss * 5 - bev, ss * 5 + bev, ss * 4 + bev, ss * 5 + bev);
                    break;
                case 7: // down left front
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * 10 + bev, ss * 5 + bev, ss * 11 - bev, ss * 6 - bev, ss * 11 - bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * 10 + bev, ss * 6 - bev, ss * 11 - bev, ss * 6 - bev, ss * 10 + bev);
                    object3D.triangle(2).setUV(ss * 5 - bev, ss * 9 + bev, ss * 4 + bev, ss * 9 + bev, ss * 4 + bev, ss * 10 - bev);
                    object3D.triangle(3).setUV(ss * 5 - bev, ss * 9 + bev, ss * 4 + bev, ss * 10 - bev, ss * 5 - bev, ss * 10 - bev);
                    object3D.triangle(4).setUV(ss * 6 - bev, ss * 9 + bev, ss * 5 + bev, ss * 9 + bev, ss * 5 + bev, ss * 10 - bev);
                    object3D.triangle(5).setUV(ss * 6 - bev, ss * 9 + bev, ss * 5 + bev, ss * 10 - bev, ss * 6 - bev, ss * 10 - bev);
                    break;
            }
        }
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;

    @Override
    protected void initEdges() {
        if (EDGE_VERTS == null) {
            /*
            EDGE_VERTS = new float[]{
            //0:luff      ldff       ruff       rdff
            -6, 6, 7, -6, -6, 7, 6, 6, 7, 6, -6, 7,
            //4:rubb,    rdbb,       lubb,       ldbb
            6, 6, -7, 6, -6, -7, -6, 6, -7, -6, -6, -7,
            //8:lluf      lldf       rruf      rrdf
            -7, 6, 6, -7, -6, 6, 7, 6, 6, 7, -6, 6,
            //12:rrub,    rrdb,      llub,      lldb
            7, 6, -6, 7, -6, -6, -7, 6, -6, -7, -6, -6,
            //16:luuf     lddf       ruuf       rddf
            -6, 7, 6, -6, -7, 6, 6, 7, 6, 6, -7, 6,
            //20:ruub,    rddb,       luub,       lddb
            6, 7, -6, 6, -7, -6, -6, 7, -6, -6, -7, -6
            };*/
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
     * Initializes the UV Map for the edge parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
     * 0                     +---+---+---+---+---+
     *                       |   |   |3.1|   |   |
     * 1                     +--- ---+---+--- ---+
     *                       |   |           |   |
     * 2                     +---+           +---+
     *                       |6.0|     u     |0.0|
     * 2                     +---+           +---+
     *                       |   |           |   |
     * 1                     +--- ---+---+--- ---+
     *                       |   |   |9.1|   |   |
     * 3 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...................+
     *   |   |   |6.1|   |   |   |   |9.0|   |   |   |24 |0.1|12 |   |                   '
     * 4 +--- ---+---+--- ---+--- ---+---+--- ---+--- ---+---+--- ---+                   '
     *   |   |           |   |   |           |   |   |           |   |                   '
     *   +---+           +---+---+           +---+---+           +---+                   '       
     *   |7.0|     l     10.0|10.1     f     |1.1|1.0|     r     |4.0|         b         '
     *   +---+           +---+---+           +---+---+           +---+                   '       
     *   |   |           |   |   |           |   |   |           |   |                   '
     * 4 +--- ---+---+--- ---+--- ---+---+--- ---+--- ---+---+--- ---+                   '
     *   |   |   |8.1|   |   |   |   |11.0   |   |   |   |2.1|   |   |                   '
     * 3 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...................+
     *                       |   |   |11.1   |   |   |   |3.0|   |   |     |
     * 7                     +--- ---+---+--- ---+--- ---+---+--- ---+     |
     *                       |   |           |   |   |           |   |     |
     *                       +---+           +---+---+           +---+     |
     *                       |8.0|     d     |2.0|4.1|     b     |7.1|  &lt;--+
     *                       +---+           +---+---+           +---+     
     *                       |   |           |   |   |           |   |     
     * 8                     +--- ---+---+--- ---+--- ---+---+--- ---+
     *                       |   |   |5.1|   |   |   |   |5.0|   |   |
     * 9                     +---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initEdgeUVMap() {
        for (int part = 0; part < 12 * 3; part++) {
            idx3d_Object object3D = parts[edgeOffset + part];
            int m = 0;
            if (part >= 12) {
                switch (part - 12) {
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
                        m = 1;
                        break;
                    default:
                        m = -1;
                        break;
                }
            }
            switch (part % 12) {
                case 0: // up right
                    //a0   1     2     3   4   5     6     7   8   9    10    11  12
                    //n0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * (3 + m) - bev, ss * 10 - bev, ss * (2 + m) + bev, ss * 9 + bev, ss * (2 + m) + bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * (3 + m) - bev, ss * 9 + bev, ss * (2 + m) + bev, ss * 9 + bev, ss * (3 + m) - bev);
                    object3D.triangle(2).setUV(ss * (12 - m) + bev, ss * 6 - bev, ss * (13 - m) - bev, ss * 6 - bev, ss * (13 - m) - bev, ss * 5 + bev);
                    object3D.triangle(3).setUV(ss * (12 - m) + bev, ss * 6 - bev, ss * (13 - m) - bev, ss * 5 + bev, ss * (12 - m) + bev, ss * 5 + bev);
                    break;
                case 1: // right front
                    object3D.triangle(0).setUV(ss * 10 + bev, ss * (7 - m) + bev, ss * 10 + bev, ss * (8 - m) - bev, ss * 11 - bev, ss * (8 - m) - bev);
                    object3D.triangle(1).setUV(ss * 10 + bev, ss * (7 - m) + bev, ss * 11 - bev, ss * (8 - m) - bev, ss * 11 - bev, ss * (7 - m) + bev);
                    object3D.triangle(2).setUV(ss * 9 + bev, ss * (7 - m) + bev, ss * 9 + bev, ss * (8 - m) - bev, ss * 10 - bev, ss * (8 - m) - bev);
                    object3D.triangle(3).setUV(ss * 9 + bev, ss * (7 - m) + bev, ss * 10 - bev, ss * (8 - m) - bev, ss * 10 - bev, ss * (7 - m) + bev);
                    break;
                case 2: // down right
                    object3D.triangle(0).setUV(ss * 10 - bev, ss * (13 + m) - bev, ss * 10 - bev, ss * (12 + m) + bev, ss * 9 + bev, ss * (12 + m) + bev);
                    object3D.triangle(1).setUV(ss * 10 - bev, ss * (13 + m) - bev, ss * 9 + bev, ss * (12 + m) + bev, ss * 9 + bev, ss * (13 + m) - bev);
                    object3D.triangle(2).setUV(ss * (13 + m) - bev, ss * 9 + bev, ss * (12 + m) + bev, ss * 9 + bev, ss * (12 + m) + bev, ss * 10 - bev);
                    object3D.triangle(3).setUV(ss * (13 + m) - bev, ss * 9 + bev, ss * (12 + m) + bev, ss * 10 - bev, ss * (13 + m) - bev, ss * 10 - bev);
                    break;
                case 3: // back up
                    object3D.triangle(0).setUV(ss * (13 + m) - bev, ss * 10 + bev, ss * (12 + m) + bev, ss * 10 + bev, ss * (12 + m) + bev, ss * 11 - bev);
                    object3D.triangle(1).setUV(ss * (13 + m) - bev, ss * 10 + bev, ss * (12 + m) + bev, ss * 11 - bev, ss * (13 + m) - bev, ss * 11 - bev);
                    object3D.triangle(2).setUV(ss * (7 - m) + bev, ss * 1 - bev, ss * (8 - m) - bev, ss * 1 - bev, ss * (8 - m) - bev, ss * 0 + bev);
                    object3D.triangle(3).setUV(ss * (7 - m) + bev, ss * 1 - bev, ss * (8 - m) - bev, ss * 0 + bev, ss * (7 - m) + bev, ss * 0 + bev);
                    break;
                case 4: // right back
                    object3D.triangle(0).setUV(ss * 15 - bev, ss * (8 + m) - bev, ss * 15 - bev, ss * (7 + m) + bev, ss * 14 + bev, ss * (7 + m) + bev);
                    object3D.triangle(1).setUV(ss * 15 - bev, ss * (8 + m) - bev, ss * 14 + bev, ss * (7 + m) + bev, ss * 14 + bev, ss * (8 + m) - bev);
                    object3D.triangle(2).setUV(ss * 11 - bev, ss * (13 + m) - bev, ss * 11 - bev, ss * (12 + m) + bev, ss * 10 + bev, ss * (12 + m) + bev);
                    object3D.triangle(3).setUV(ss * 11 - bev, ss * (13 + m) - bev, ss * 10 + bev, ss * (12 + m) + bev, ss * 10 + bev, ss * (13 + m) - bev);
                    break;
                case 5: // back down
                    object3D.triangle(0).setUV(ss * (12 - m) + bev, ss * 15 - bev, ss * (13 - m) - bev, ss * 15 - bev, ss * (13 - m) - bev, ss * 14 + bev);
                    object3D.triangle(1).setUV(ss * (12 - m) + bev, ss * 15 - bev, ss * (13 - m) - bev, ss * 14 + bev, ss * (12 - m) + bev, ss * 14 + bev);
                    object3D.triangle(2).setUV(ss * (8 + m) - bev, ss * 14 + bev, ss * (7 + m) + bev, ss * 14 + bev, ss * (7 + m) + bev, ss * 15 - bev);
                    object3D.triangle(3).setUV(ss * (8 + m) - bev, ss * 14 + bev, ss * (7 + m) + bev, ss * 15 - bev, ss * (8 + m) - bev, ss * 15 - bev);
                    break;
                case 6: // up left
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * (2 - m) + bev, ss * 5 + bev, ss * (3 - m) - bev, ss * 6 - bev, ss * (3 - m) - bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * (2 - m) + bev, ss * 6 - bev, ss * (3 - m) - bev, ss * 6 - bev, ss * (2 - m) + bev);
                    object3D.triangle(2).setUV(ss * (2 - m) + bev, ss * 6 - bev, ss * (3 - m) - bev, ss * 6 - bev, ss * (3 - m) - bev, ss * 5 + bev);
                    object3D.triangle(3).setUV(ss * (2 - m) + bev, ss * 6 - bev, ss * (3 - m) - bev, ss * 5 + bev, ss * (2 - m) + bev, ss * 5 + bev);
                    break;
                case 7: // left back
                    object3D.triangle(0).setUV(ss * 0 + bev, ss * (7 - m) + bev, ss * 0 + bev, ss * (8 - m) - bev, ss * 1 - bev, ss * (8 - m) - bev);
                    object3D.triangle(1).setUV(ss * 0 + bev, ss * (7 - m) + bev, ss * 1 - bev, ss * (8 - m) - bev, ss * 1 - bev, ss * (7 - m) + bev);
                    object3D.triangle(2).setUV(ss * 14 + bev, ss * (12 - m) + bev, ss * 14 + bev, ss * (13 - m) - bev, ss * 15 - bev, ss * (13 - m) - bev);
                    object3D.triangle(3).setUV(ss * 14 + bev, ss * (12 - m) + bev, ss * 15 - bev, ss * (13 - m) - bev, ss * 15 - bev, ss * (12 - m) + bev);
                    break;
                case 8: // down left
                    object3D.triangle(0).setUV(ss * 5 + bev, ss * (12 - m) + bev, ss * 5 + bev, ss * (13 - m) - bev, ss * 6 - bev, ss * (13 - m) - bev);
                    object3D.triangle(1).setUV(ss * 5 + bev, ss * (12 - m) + bev, ss * 6 - bev, ss * (13 - m) - bev, ss * 6 - bev, ss * (12 - m) + bev);
                    object3D.triangle(2).setUV(ss * (3 + m) - bev, ss * 9 + bev, ss * (2 + m) + bev, ss * 9 + bev, ss * (2 + m) + bev, ss * 10 - bev);
                    object3D.triangle(3).setUV(ss * (3 + m) - bev, ss * 9 + bev, ss * (2 + m) + bev, ss * 10 - bev, ss * (3 + m) - bev, ss * 10 - bev);
                    break;
                case 9: // front up
                    object3D.triangle(0).setUV(ss * (8 + m) - bev, ss * 5 + bev, ss * (7 + m) + bev, ss * 5 + bev, ss * (7 + m) + bev, ss * 6 - bev);
                    object3D.triangle(1).setUV(ss * (8 + m) - bev, ss * 5 + bev, ss * (7 + m) + bev, ss * 6 - bev, ss * (8 + m) - bev, ss * 6 - bev);
                    object3D.triangle(2).setUV(ss * (8 + m) - bev, ss * 4 + bev, ss * (7 + m) + bev, ss * 4 + bev, ss * (7 + m) + bev, ss * 5 - bev);
                    object3D.triangle(3).setUV(ss * (8 + m) - bev, ss * 4 + bev, ss * (7 + m) + bev, ss * 5 - bev, ss * (8 + m) - bev, ss * 5 - bev);
                    break;
                case 10: // left front
                    object3D.triangle(0).setUV(ss * 5 - bev, ss * (8 + m) - bev, ss * 5 - bev, ss * (7 + m) + bev, ss * 4 + bev, ss * (7 + m) + bev);
                    object3D.triangle(1).setUV(ss * 5 - bev, ss * (8 + m) - bev, ss * 4 + bev, ss * (7 + m) + bev, ss * 4 + bev, ss * (8 + m) - bev);
                    object3D.triangle(2).setUV(ss * 6 - bev, ss * (8 + m) - bev, ss * 6 - bev, ss * (7 + m) + bev, ss * 5 + bev, ss * (7 + m) + bev);
                    object3D.triangle(3).setUV(ss * 6 - bev, ss * (8 + m) - bev, ss * 5 + bev, ss * (7 + m) + bev, ss * 5 + bev, ss * (8 + m) - bev);
                    break;
                case 11: // front down
                    object3D.triangle(0).setUV(ss * (7 - m) + bev, ss * 10 - bev, ss * (8 - m) - bev, ss * 10 - bev, ss * (8 - m) - bev, ss * 9 + bev);
                    object3D.triangle(1).setUV(ss * (7 - m) + bev, ss * 10 - bev, ss * (8 - m) - bev, ss * 9 + bev, ss * (7 - m) + bev, ss * 9 + bev);
                    object3D.triangle(2).setUV(ss * (7 - m) + bev, ss * 11 - bev, ss * (8 - m) - bev, ss * 11 - bev, ss * (8 - m) - bev, ss * 10 + bev);
                    object3D.triangle(3).setUV(ss * (7 - m) + bev, ss * 11 - bev, ss * (8 - m) - bev, ss * 10 + bev, ss * (7 - m) + bev, ss * 10 + bev);
                    break;
            }
        }
    }
    private static float[] SIDE_VERTS;
    private static int[][] SIDE_FACES;

    @Override
    protected void initSides() {
        if (SIDE_VERTS == null) {
            /*
            SIDE_VERTS = new float[]{
            //0:luff      ldff       ruff       rdff
            -6, 6, 7, -6, -6, 7, 6, 6, 7, 6, -6, 7,
            //4:rubb,    rdbb,       lubb,       ldbb
            6, 6, -7, 6, -6, -7, -6, 6, -7, -6, -6, -7,
            //8:lluf      lldf       rruf      rrdf
            -7, 6, 6, -7, -6, 6, 7, 6, 6, 7, -6, 6,
            //12:rrub,    rrdb,      llub,      lldb
            7, 6, -6, 7, -6, -6, -7, 6, -6, -7, -6, -6,
            //16:luuf     lddf       ruuf       rddf
            -6, 7, 6, -6, -7, 6, 6, 7, 6, 6, -7, 6,
            //20:ruub,    rddb,       luub,       lddb
            6, 7, -6, 6, -7, -6, -6, 7, -6, -6, -7, -6
            };*/
            SIDE_VERTS = new float[]{
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

        int[] mxMap = {0, 0, -1, -1, -1, 0, 1, 1, 1};
        int[] myMap = {0, 1, 1, 0, -1, -1, -1, 0, 1};
        for (int part = 0; part
                < sideCount; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (int i = 0; i
                    < SIDE_VERTS.length / 3; i++) {
                object3D.addVertex(
                        SIDE_VERTS[i * 3], SIDE_VERTS[i * 3 + 1], SIDE_VERTS[i * 3 + 2]);
            }

            for (int i = 0; i
                    < SIDE_FACES.length; i++) {
                for (int j = 2; j
                        < SIDE_FACES[i].length; j++) {
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
     */
    protected void initSideUVMap() {
        int[] mxMap = {0, -1, -1, 1, 1, 0, -1, 0, 1};
        int[] myMap = {0, 1, -1, -1, 1, 1, 0, -1, 0};
        for (int part = 0; part
                < 6 * 9; part++) {
            idx3d_Object object3D = parts[sideOffset + part];
            int mx = mxMap[part / 6];
            int my = myMap[part / 6];
            switch (part % 6) {
                case 0: // right
                    object3D.triangle(0).setUV(ss * (12 + my) + bev, ss * (7 - mx) + bev, ss * (12 + my) + bev, ss * (8 - mx) - bev, ss * (13 + my) - bev, ss * (8 - mx) - bev);
                    object3D.triangle(1).setUV(ss * (12 + my) + bev, ss * (7 - mx) + bev, ss * (13 + my) - bev, ss * (8 - mx) - bev, ss * (13 + my) - bev, ss * (7 - mx) + bev);
                    break;
                case 1: // up
                    object3D.triangle(0).setUV(ss * (8 - my) - bev, ss * (3 + mx) - bev, ss * (8 - my) - bev, ss * (2 + mx) + bev, ss * (7 - my) + bev, ss * (2 + mx) + bev);
                    object3D.triangle(1).setUV(ss * (8 - my) - bev, ss * (3 + mx) - bev, ss * (7 - my) + bev, ss * (2 + mx) + bev, ss * (7 - my) + bev, ss * (3 + mx) - bev);
                    break;
                case 2: // front
                    object3D.triangle(0).setUV(ss * (8 + mx) - bev, ss * (7 + my) + bev, ss * (7 + mx) + bev, ss * (7 + my) + bev, ss * (7 + mx) + bev, ss * (8 + my) - bev);
                    object3D.triangle(1).setUV(ss * (8 + mx) - bev, ss * (7 + my) + bev, ss * (7 + mx) + bev, ss * (8 + my) - bev, ss * (8 + mx) - bev, ss * (8 + my) - bev);
                    break;
                case 3: // left
                    object3D.triangle(0).setUV(ss * (2 - mx) + bev, ss * (8 - my) - bev, ss * (3 - mx) - bev, ss * (8 - my) - bev, ss * (3 - mx) - bev, ss * (7 - my) + bev);
                    object3D.triangle(1).setUV(ss * (2 - mx) + bev, ss * (8 - my) - bev, ss * (3 - mx) - bev, ss * (7 - my) + bev, ss * (2 - mx) + bev, ss * (7 - my) + bev);
                    break;
                case 4: // down
//                    object3D.triangle(0).setUV(ss * (7 + mx) + bev, ss * (13 - my) - bev, ss * (8 + mx) - bev, ss * (13 - my) - bev, ss * (8 + mx) - bev, ss * (12 - my) + bev);
//                   object3D.triangle(1).setUV(ss * (7 + mx) + bev, ss * (13 - my) - bev, ss * (8 + mx) - bev, ss * (12 - my) + bev, ss * (7 + mx) + bev, ss * (12 - my) + bev);
                    object3D.triangle(0).setUV(ss * (7 - mx) + bev, ss * (13 - my) - bev, ss * (8 - mx) - bev, ss * (13 - my) - bev, ss * (8 - mx) - bev, ss * (12 - my) + bev);
                    object3D.triangle(1).setUV(ss * (7 - mx) + bev, ss * (13 - my) - bev, ss * (8 - mx) - bev, ss * (12 - my) + bev, ss * (7 - mx) + bev, ss * (12 - my) + bev);
                    break;
                case 5: // back
                    object3D.triangle(0).setUV(ss * (13 - my) - bev, ss * (13 + mx) - bev, ss * (13 - my) - bev, ss * (12 + mx) + bev, ss * (12 - my) + bev, ss * (12 + mx) + bev);
                    object3D.triangle(1).setUV(ss * (13 - my) - bev, ss * (13 + mx) - bev, ss * (12 - my) + bev, ss * (12 + mx) + bev, ss * (12 - my) + bev, ss * (13 + mx) - bev);
                    break;
            }

        }
    }
    /**
     * The numbers show the part indices. The stickers are numbered from top
     * left to bottom right on each face. The sequence of the faces is right,
     * up, front, left, down, back. 
     * <pre>
     *                     +---+---+---+---+---+
     *                     | 4 |23 |11 |35 | 2 |
     *                     +---+---+---+---+---+
     *                     |26 |51  81  57 |20 |
     *                     +---+           +---+
     *                     |14 |75  45  87 | 8 |
     *                     +---+           +---+
     *                     |38 |69  93  63 |32 |
     *                     +---+---+---+---+---+
     *                     | 6 |29 |17 |41 | 0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 4 |26 |14 |38 | 6 | 6 |29 |17 |41 | 0 | 0 |32 | 8 |20 | 2 | 2 |35 |11 |23 | 4 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |39 |71  77  53 |42 |42 |58  88  64 |33 |33 |62  92  68 |36 |36 |55  85  61 |39 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |15 |95  47  83 |18 |18 |82  46  94 | 9 | 9 |86  44  74 |12 |12 |79  49  91 |15 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |27 |65  89  59 |30 |30 |52  76  70 |21 |21 |56  80  50 |24 |24 |73  97  67 |27 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 5 |28 |16 |40 | 7 | 7 |31 |19 |43 | 1 | 1 |34 |10 |22 | 3 | 3 |37 |13 |25 | 5 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     | 7 |31 |19 |43 | 1 |
     *                     +---+---+---+---+---+
     *                     |40 |72  78  54 |34 |
     *                     +---+           +---+
     *                     |16 |96  48  84 |10 |
     *                     +---+           +---+
     *                     |28 |66  90  60 |22 |
     *                     +---+---+---+---+---+
     *                     | 5 |25 |13 |37 | 3 |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 32, 8, 20, 2, //
        33, 62, 92, 68, 36,//
        9, 86, 44, 74, 12, //
        21, 56, 80, 50, 24,//
        1, 34, 10, 22, 3, // right
        //
        4, 23, 11, 35, 2, //
        26, 51, 81, 57, 20, //
        14, 75, 45, 87, 8,//
        38, 69, 93, 63, 32,//
        6, 29, 17, 41, 0, // up
        //
        6, 29, 17, 41, 0,//
        42, 58, 88, 64, 33,//
        18, 82, 46, 94, 9,//
        30, 52, 76, 70, 21,//
        7, 31, 19, 43, 1, // front
        //
        4, 26, 14, 38, 6, //
        39, 71, 77, 53, 42,//
        15, 95, 47, 83, 18,//
        27, 65, 89, 59, 30,//
        5, 28, 16, 40, 7, // left
        //
        7, 31, 19, 43, 1,//
        40, 72, 78, 54, 34,//
        16, 96, 48, 84, 10,//
        28, 66, 90, 60, 22,//
        5, 25, 13, 37, 3, // down
        //
        2, 35, 11, 23, 4,//
        36, 55, 85, 61, 39,//
        12, 79, 49, 91, 15,//
        24, 73, 97, 67, 27,//
        3, 37, 13, 25, 5, // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, // right
        //
        0, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 1, 1, 1, 0, // up
        //
        1, 0, 0, 0, 2, //
        1, 0, 0, 0, 1, //
        1, 0, 0, 0, 1, //
        1, 0, 0, 0, 1, //
        2, 0, 0, 0, 1, // front
        //
        1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, // left
        //
        0, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, //
        0, 1, 1, 1, 0, // down
        //
        1, 0, 0, 0, 2, //
        1, 0, 0, 0, 1, //
        1, 0, 0, 0, 1, //
        1, 0, 0, 0, 1, //
        2, 0, 0, 0, 1, // back
    };

    @Override
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex] * 2;
    }

    protected int getStickerIndexForPart(int part, int orientation) {
        int sticker;
        for (sticker = stickerToPartMap.length - 1; sticker
                >= 0; sticker--) {
            if (stickerToPartMap[sticker] == part && stickerToFaceMap[sticker] == orientation) {
                break;
            }
        }

        return sticker;
    }

    @Override
    protected int getStickerCount() {
        return 6 * 5 * 5;
    }

    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{25, 25, 25, 25, 25, 25});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 5 * 5, 1 * 5 * 5, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 5 * 5, 2 * 5 * 5, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 5 * 5, 3 * 5 * 5, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 5 * 5, 4 * 5 * 5, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 5 * 5, 5 * 5 * 5, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 5 * 5, 6 * 5 * 5, new Color(255, 70, 0)); // Back: Orange

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
            for (j = 0; j
                    < 3; j++) {
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
        for (i = 0; i
                < edgeCount; i++) {
            int index = edgeOffset + i;
            for (j = 0; j
                    < 2; j++) {
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
        for (i = 0; i
                < sideCount; i++) {
            int index = sideOffset + i;
            action =
                    new AbstractCube3D.PartAction(
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

        for (i = 0; i
                < partCount; i++) {
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
        bev = newValue;
        initEdgeUVMap();
        initCornerUVMap();
        initSideUVMap();
    }

    @Override
    public CubeKind getKind() {
        return CubeKind.PROFESSOR;
    }

}
