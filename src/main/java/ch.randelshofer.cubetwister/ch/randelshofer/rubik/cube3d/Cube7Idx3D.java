/*
 * @(#)Cube7Idx3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube.Cube7;
import idx3d.idx3d_Group;
import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Node;
import idx3d.idx3d_Object;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Triangle;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;

/**
 * Simplified geometrical representation of {@link Cube7} in three dimensions.
 * <p>
 * The representation is simplified in the sense that all stickers of the
 * cube are square. In a real physical representation, such as a V-Cube 7,
 * the surfaces of the cube are slightly rounded, resulting in stickers of
 * different sizes and different aspect ratios.
 *
 * @author Werner Randelshofer
 */
public class Cube7Idx3D extends AbstractCube7Idx3D {

    private final static int STICKER_COUNT = 6 * 7 * 7;
    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one 15-th of the image width.
     */
    private final static float ss = imageWidth / 21f;
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
            idx3d_Group group = new idx3d_Group();
            group.addChild(object3D);
            parts[cornerOffset + part] = group;
        }
        initCornerUVMap();
    }

    /**
     * Initalizes the Corner UV Map.
     * <pre>
     *    0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21
     *  0                             +---+---+---+---+---+---+---+
     *                                |4.0|                   |2.0|
     *  1                             +---+                   +---+
     *                                |                           |
     *  2                             +                           +
     *                                |                           |
     *  3                             +                           +
     *                                |             u             |
     *  4                             +                           +
     *                                |                           |
     *  5                             +                           +
     *                                |                           |
     *  6                             +---+                   +---+
     *                                |6.0|                   |0.0|
     *  7 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...........................+
     *    |4.1|                   |6.2|6.1|                   |0.2|0.1|                   |2.2|                           '
     *  8 +---+                   +---+---+                   +---+---+                   +---+                           '
     *    |                           |                           |                           |                           '
     *  9 +                           +                           +                           +                           '
     *    |                           |                           |                           |                           '
     * 10 +                           +                           +                           +                           '
     *    |             l             |             f             |             r             |             b             '
     * 11 +                           +                           +                           +                           '
     *    |                           |                           |                           |                           '
     * 12 +                           +                           +                           +                           '
     *    |                           |                           |                           |                           '
     * 13 +---+                   +---+---+                   +---+---+                   +---+                           '
     *    |5.2|                   |7.1|7.2|                   |1.1|1.2|                   |3.1|                           '
     * 14 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...........................+
     *                                |7.0|                   |1.0|2.1|                   |4.2|     |
     * 15                             +---+                   +---+---+                   +---+     |
     *                                |                           |                           |     |
     * 16                             +                           +                           +     |
     *                                |                           |                           |     |
     * 17                             +                           +                           +     |
     *                                |             d             |             b             |  &lt;--+
     * 18                             +                           +                           +
     *                                |                           |                           |
     * 19                             +                           +                           +
     *                                |                           |                           |
     * 20                             +---+                   +---+---+                   +---+
     *                                |5.0|                   |3.0|3.2|                   |5.1|
     * 21                             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Group group = parts[cornerOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            switch (part) {
            case 0: // up right front
                object3D.triangle(0).setUV(ss * 14 - bev, ss * 7 - bev, ss * 14 - bev, ss * 6 + bev, ss * 13 + bev, ss * 6 + bev);
                object3D.triangle(1).setUV(ss * 14 - bev, ss * 7 - bev, ss * 13 + bev, ss * 6 + bev, ss * 13 + bev, ss * 7 - bev);
                object3D.triangle(2).setUV(ss * 14 + bev, ss * 8 - bev, ss * 15 - bev, ss * 8 - bev, ss * 15 - bev, ss * 7 + bev);
                object3D.triangle(3).setUV(ss * 14 + bev, ss * 8 - bev, ss * 15 - bev, ss * 7 + bev, ss * 14 + bev, ss * 7 + bev);
                object3D.triangle(4).setUV(ss * 13 + bev, ss * 8 - bev, ss * 14 - bev, ss * 8 - bev, ss * 14 - bev, ss * 7 + bev);
                object3D.triangle(5).setUV(ss * 13 + bev, ss * 8 - bev, ss * 14 - bev, ss * 7 + bev, ss * 13 + bev, ss * 7 + bev);
                break;
            case 1: // down front right
                    object3D.triangle(0).setUV(ss * 14 - bev, ss * 14 + bev, ss * 13 + bev, ss * 14 + bev, ss * 13 + bev, ss * 15 - bev);
                    object3D.triangle(1).setUV(ss * 14 - bev, ss * 14 + bev, ss * 13 + bev, ss * 15 - bev, ss * 14 - bev, ss * 15 - bev);
                    object3D.triangle(2).setUV(ss * 14 - bev, ss * 13 + bev, ss * 13 + bev, ss * 13 + bev, ss * 13 + bev, ss * 14 - bev);
                    object3D.triangle(3).setUV(ss * 14 - bev, ss * 13 + bev, ss * 13 + bev, ss * 14 - bev, ss * 14 - bev, ss * 14 - bev);
                    object3D.triangle(4).setUV(ss * 15 - bev, ss * 13 + bev, ss * 14 + bev, ss * 13 + bev, ss * 14 + bev, ss * 14 - bev);
                    object3D.triangle(5).setUV(ss * 15 - bev, ss * 13 + bev, ss * 14 + bev, ss * 14 - bev, ss * 15 - bev, ss * 14 - bev);
                    break;
                case 2: // up back right
                    object3D.triangle(0).setUV(ss * 14 - bev, ss * 0 + bev, ss * 13 + bev, ss * 0 + bev, ss * 13 + bev, ss * 1 - bev);
                    object3D.triangle(1).setUV(ss * 14 - bev, ss * 0 + bev, ss * 13 + bev, ss * 1 - bev, ss * 14 - bev, ss * 1 - bev);
                    object3D.triangle(2).setUV(ss * 14 + bev, ss * 15 - bev, ss * 15 - bev, ss * 15 - bev, ss * 15 - bev, ss * 14 + bev);
                    object3D.triangle(3).setUV(ss * 14 + bev, ss * 15 - bev, ss * 15 - bev, ss * 14 + bev, ss * 14 + bev, ss * 14 + bev);
                    object3D.triangle(4).setUV(ss * 20 + bev, ss * 8 - bev, ss * 21 - bev, ss * 8 - bev, ss * 21 - bev, ss * 7 + bev);
                    object3D.triangle(5).setUV(ss * 20 + bev, ss * 8 - bev, ss * 21 - bev, ss * 7 + bev, ss * 20 + bev, ss * 7 + bev);
                    break;
                case 3: // down right back
                    object3D.triangle(0).setUV(ss * 14 - bev, ss * 21 - bev, ss * 14 - bev, ss * 20 + bev, ss * 13 + bev, ss * 20 + bev);
                    object3D.triangle(1).setUV(ss * 14 - bev, ss * 21 - bev, ss * 13 + bev, ss * 20 + bev, ss * 13 + bev, ss * 21 - bev);
                    object3D.triangle(2).setUV(ss * 21 - bev, ss * 13 + bev, ss * 20 + bev, ss * 13 + bev, ss * 20 + bev, ss * 14 - bev);
                    object3D.triangle(3).setUV(ss * 21 - bev, ss * 13 + bev, ss * 20 + bev, ss * 14 - bev, ss * 21 - bev, ss * 14 - bev);
                    object3D.triangle(4).setUV(ss * 15 - bev, ss * 20 + bev, ss * 14 + bev, ss * 20 + bev, ss * 14 + bev, ss * 21 - bev);
                    object3D.triangle(5).setUV(ss * 15 - bev, ss * 20 + bev, ss * 14 + bev, ss * 21 - bev, ss * 15 - bev, ss * 21 - bev);
                    break;
                case 4: // up left back
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * 0 + bev, ss * 7 + bev, ss * 1 - bev, ss * 8 - bev, ss * 1 - bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * 0 + bev, ss * 8 - bev, ss * 1 - bev, ss * 8 - bev, ss * 0 + bev);
                    object3D.triangle(2).setUV(ss * 0 + bev, ss * 8 - bev, ss * 1 - bev, ss * 8 - bev, ss * 1 - bev, ss * 7 + bev);
                    object3D.triangle(3).setUV(ss * 0 + bev, ss * 8 - bev, ss * 1 - bev, ss * 7 + bev, ss * 0 + bev, ss * 7 + bev);
                    object3D.triangle(4).setUV(ss * 20 + bev, ss * 15 - bev, ss * 21 - bev, ss * 15 - bev, ss * 21 - bev, ss * 14 + bev);
                    object3D.triangle(5).setUV(ss * 20 + bev, ss * 15 - bev, ss * 21 - bev, ss * 14 + bev, ss * 20 + bev, ss * 14 + bev);
                    break;
                case 5: // down back left
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * 21 - bev, ss * 8 - bev, ss * 21 - bev, ss * 8 - bev, ss * 20 + bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * 21 - bev, ss * 8 - bev, ss * 20 + bev, ss * 7 + bev, ss * 20 + bev);
                    object3D.triangle(2).setUV(ss * 21 - bev, ss * 20 + bev, ss * 20 + bev, ss * 20 + bev, ss * 20 + bev, ss * 21 - bev);
                    object3D.triangle(3).setUV(ss * 21 - bev, ss * 20 + bev, ss * 20 + bev, ss * 21 - bev, ss * 21 - bev, ss * 21 - bev);
                    object3D.triangle(4).setUV(ss * 1 - bev, ss * 13 + bev, ss * 0 + bev, ss * 13 + bev, ss * 0 + bev, ss * 14 - bev);
                    object3D.triangle(5).setUV(ss * 1 - bev, ss * 13 + bev, ss * 0 + bev, ss * 14 - bev, ss * 1 - bev, ss * 14 - bev);
                    break;
                case 6: // up front left
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * 7 - bev, ss * 8 - bev, ss * 7 - bev, ss * 8 - bev, ss * 6 + bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * 7 - bev, ss * 8 - bev, ss * 6 + bev, ss * 7 + bev, ss * 6 + bev);
                    object3D.triangle(2).setUV(ss * 7 + bev, ss * 8 - bev, ss * 8 - bev, ss * 8 - bev, ss * 8 - bev, ss * 7 + bev);
                    object3D.triangle(3).setUV(ss * 7 + bev, ss * 8 - bev, ss * 8 - bev, ss * 7 + bev, ss * 7 + bev, ss * 7 + bev);
                    object3D.triangle(4).setUV(ss * 6 + bev, ss * 8 - bev, ss * 7 - bev, ss * 8 - bev, ss * 7 - bev, ss * 7 + bev);
                    object3D.triangle(5).setUV(ss * 6 + bev, ss * 8 - bev, ss * 7 - bev, ss * 7 + bev, ss * 6 + bev, ss * 7 + bev);
                    break;
                case 7: // down left front
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * 14 + bev, ss * 7 + bev, ss * 15 - bev, ss * 8 - bev, ss * 15 - bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * 14 + bev, ss * 8 - bev, ss * 15 - bev, ss * 8 - bev, ss * 14 + bev);
                    object3D.triangle(2).setUV(ss * 7 - bev, ss * 13 + bev, ss * 6 + bev, ss * 13 + bev, ss * 6 + bev, ss * 14 - bev);
                    object3D.triangle(3).setUV(ss * 7 - bev, ss * 13 + bev, ss * 6 + bev, ss * 14 - bev, ss * 7 - bev, ss * 14 - bev);
                    object3D.triangle(4).setUV(ss * 8 - bev, ss * 13 + bev, ss * 7 + bev, ss * 13 + bev, ss * 7 + bev, ss * 14 - bev);
                    object3D.triangle(5).setUV(ss * 8 - bev, ss * 13 + bev, ss * 7 + bev, ss * 14 - bev, ss * 8 - bev, ss * 14 - bev);
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
            idx3d_Group group = new idx3d_Group();
            group.addChild(object3D);
            parts[edgeOffset + part] = group;
        }
        initEdgeUVMap();
    }

    /**
     * Initializes the UV Map for the edge parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21
     * 0                             +---+---+---+---+---+---+---+
     *                               |   |   |   |3.1|   |   |   |
     * 1                             +--- ---+---+---+---+--- ---+
     *                               |   |                   |   |
     * 2                             +---+                   +---+
     *                               |   |                   |   |
     * 2                             +---+                   +---+
     *                               |6.0|         u         |0.0|
     * 2                             +---+                   +---+
     *                               |   |                   |   |
     * 2                             +---+                   +---+
     *                               |   |                   |   |
     * 1                             +--- ---+---+---+---+--- ---+
     *                               |   |   |   |9.1|   |   |   |
     * 3 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...........................+
     *   |   |   |   |6.1|   |   |   |   |   |   |9.0|   |   |   |   |48 |24 |0.1|12 |36 |   |                           '
     * 4 +--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+                           '
     *   |   |                   |   |   |                   |   |   |                   |   |                           '
     *   +---+                   +---+---+                   +---+---+                   +---+                           '
     *   |   |                   |   |   |                   |   |   |                   |   |                           '
     *   +---+                   +---+---+                   +---+---+                   +---+                           '
     *   |7.0|         l         10.0|10.1         f         |1.1|1.0|         r         |4.0|             b             '
     *   +---+                   +---+---+                   +---+---+                   +---+                           '
     *   |   |                   |   |   |                   |   |   |                   |   |                           '
     *   +---+                   +---+---+                   +---+---+                   +---+                           '
     *   |   |                   |   |   |                   |   |   |                   |   |                           '
     * 4 +--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+                           '
     *   |   |   |   |8.1|   |   |   |   |   |   |11.0   |   |   |   |   |   |2.1|   |   |   |                           '
     * 3 +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+...........................+
     *                               |   |   |   |11.1   |   |   |   |   |   |3.0|   |   |   |     |
     * 7                             +--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+     |
     *                               |   |                   |   |   |                   |   |     |
     *                               +---+                   +---+---+                   +---+     |
     *                               |   |                   |   |   |                   |   |     |
     *                               +---+                   +---+---+                   +---+     |
     *                               |8.0|         d         |2.0|4.1|         b         |7.1|  &lt;--+
     *                               +---+                   +---+---+                   +---+
     *                               |   |                   |   |   |                   |   |
     *                               +---+                   +---+---+                   +---+
     *                               |   |                   |   |   |                   |   |
     * 8                             +--- ---+---+---+---+--- ---+--- ---+---+---+---+--- ---+
     *                               |   |   |   |5.1|   |   |   |   |   |   |5.0|   |   |   |
     * 9                             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initEdgeUVMap() {
        for (int part = 0; part < edgeCount; part++) {
            idx3d_Group group = parts[edgeOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            int m = 0;
            if (part >= 12) {
                switch ((part - 12) % 24) {
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
                        m = 1 + (part - 12) / 24;
                        break;
                    default:
                        m = -1 - (part - 12) / 24;
                        break;
                }
            }
            switch (part % 12) {
                //a0   1       2   3       4   5   6       7   8       9  10  11      12  13      14  15
                //n0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21
                case 0: // up right
                    object3D.triangle(0).setUV(ss * 14 - bev, ss * (4 + m) - bev, ss * 14 - bev, ss * (3 + m) + bev, ss * 13 + bev, ss * (3 + m) + bev);
                    object3D.triangle(1).setUV(ss * 14 - bev, ss * (4 + m) - bev, ss * 13 + bev, ss * (3 + m) + bev, ss * 13 + bev, ss * (4 + m) - bev);
                    object3D.triangle(2).setUV(ss * (17 - m) + bev, ss * 8 - bev, ss * (18 - m) - bev, ss * 8 - bev, ss * (18 - m) - bev, ss * 7 + bev);
                    object3D.triangle(3).setUV(ss * (17 - m) + bev, ss * 8 - bev, ss * (18 - m) - bev, ss * 7 + bev, ss * (17 - m) + bev, ss * 7 + bev);
                    break;
                case 1: // right front
                    object3D.triangle(0).setUV(ss * 14 + bev, ss * (10 - m) + bev, ss * 14 + bev, ss * (11 - m) - bev, ss * 15 - bev, ss * (11 - m) - bev);
                    object3D.triangle(1).setUV(ss * 14 + bev, ss * (10 - m) + bev, ss * 15 - bev, ss * (11 - m) - bev, ss * 15 - bev, ss * (10 - m) + bev);
                    object3D.triangle(2).setUV(ss * 13 + bev, ss * (10 - m) + bev, ss * 13 + bev, ss * (11 - m) - bev, ss * 14 - bev, ss * (11 - m) - bev);
                    object3D.triangle(3).setUV(ss * 13 + bev, ss * (10 - m) + bev, ss * 14 - bev, ss * (11 - m) - bev, ss * 14 - bev, ss * (10 - m) + bev);
                    break;
                case 2: // down right
                    object3D.triangle(0).setUV(ss * 14 - bev, ss * (18 + m) - bev, ss * 14 - bev, ss * (17 + m) + bev, ss * 13 + bev, ss * (17 + m) + bev);
                    object3D.triangle(1).setUV(ss * 14 - bev, ss * (18 + m) - bev, ss * 13 + bev, ss * (17 + m) + bev, ss * 13 + bev, ss * (18 + m) - bev);
                    object3D.triangle(2).setUV(ss * (18 + m) - bev, ss * 13 + bev, ss * (17 + m) + bev, ss * 13 + bev, ss * (17 + m) + bev, ss * 14 - bev);
                    object3D.triangle(3).setUV(ss * (18 + m) - bev, ss * 13 + bev, ss * (17 + m) + bev, ss * 14 - bev, ss * (18 + m) - bev, ss * 14 - bev);
                    break;
                case 3: // back up
                    object3D.triangle(0).setUV(ss * (18 + m) - bev, ss * 14 + bev, ss * (17 + m) + bev, ss * 14 + bev, ss * (17 + m) + bev, ss * 15 - bev);
                    object3D.triangle(1).setUV(ss * (18 + m) - bev, ss * 14 + bev, ss * (17 + m) + bev, ss * 15 - bev, ss * (18 + m) - bev, ss * 15 - bev);
                    object3D.triangle(2).setUV(ss * (10 - m) + bev, ss * 1 - bev, ss * (11 - m) - bev, ss * 1 - bev, ss * (11 - m) - bev, ss * 0 + bev);
                    object3D.triangle(3).setUV(ss * (10 - m) + bev, ss * 1 - bev, ss * (11 - m) - bev, ss * 0 + bev, ss * (10 - m) + bev, ss * 0 + bev);
                    break;
                case 4: // right back
                    object3D.triangle(0).setUV(ss * 21 - bev, ss * (11 + m) - bev, ss * 21 - bev, ss * (10 + m) + bev, ss * 20 + bev, ss * (10 + m) + bev);
                    object3D.triangle(1).setUV(ss * 21 - bev, ss * (11 + m) - bev, ss * 20 + bev, ss * (10 + m) + bev, ss * 20 + bev, ss * (11 + m) - bev);
                    object3D.triangle(2).setUV(ss * 15 - bev, ss * (18 + m) - bev, ss * 15 - bev, ss * (17 + m) + bev, ss * 14 + bev, ss * (17 + m) + bev);
                    object3D.triangle(3).setUV(ss * 15 - bev, ss * (18 + m) - bev, ss * 14 + bev, ss * (17 + m) + bev, ss * 14 + bev, ss * (18 + m) - bev);
                    break;
                case 5: // back down
                    object3D.triangle(0).setUV(ss * (17 - m) + bev, ss * 21 - bev, ss * (18 - m) - bev, ss * 21 - bev, ss * (18 - m) - bev, ss * 20 + bev);
                    object3D.triangle(1).setUV(ss * (17 - m) + bev, ss * 21 - bev, ss * (18 - m) - bev, ss * 20 + bev, ss * (17 - m) + bev, ss * 20 + bev);
                    object3D.triangle(2).setUV(ss * (11 + m) - bev, ss * 20 + bev, ss * (10 + m) + bev, ss * 20 + bev, ss * (10 + m) + bev, ss * 21 - bev);
                    object3D.triangle(3).setUV(ss * (11 + m) - bev, ss * 20 + bev, ss * (10 + m) + bev, ss * 21 - bev, ss * (11 + m) - bev, ss * 21 - bev);
                    break;
                case 6: // up left
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * (3 - m) + bev, ss * 7 + bev, ss * (4 - m) - bev, ss * 8 - bev, ss * (4 - m) - bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * (3 - m) + bev, ss * 8 - bev, ss * (4 - m) - bev, ss * 8 - bev, ss * (3 - m) + bev);
                    object3D.triangle(2).setUV(ss * (3 - m) + bev, ss * 8 - bev, ss * (4 - m) - bev, ss * 8 - bev, ss * (4 - m) - bev, ss * 7 + bev);
                    object3D.triangle(3).setUV(ss * (3 - m) + bev, ss * 8 - bev, ss * (4 - m) - bev, ss * 7 + bev, ss * (3 - m) + bev, ss * 7 + bev);
                    break;
                case 7: // left back
                    object3D.triangle(0).setUV(ss * 0 + bev, ss * (10 - m) + bev, ss * 0 + bev, ss * (11 - m) - bev, ss * 1 - bev, ss * (11 - m) - bev);
                    object3D.triangle(1).setUV(ss * 0 + bev, ss * (10 - m) + bev, ss * 1 - bev, ss * (11 - m) - bev, ss * 1 - bev, ss * (10 - m) + bev);
                    object3D.triangle(2).setUV(ss * 20 + bev, ss * (17 - m) + bev, ss * 20 + bev, ss * (18 - m) - bev, ss * 21 - bev, ss * (18 - m) - bev);
                    object3D.triangle(3).setUV(ss * 20 + bev, ss * (17 - m) + bev, ss * 21 - bev, ss * (18 - m) - bev, ss * 21 - bev, ss * (17 - m) + bev);
                    break;
                case 8: // down left
                    object3D.triangle(0).setUV(ss * 7 + bev, ss * (17 - m) + bev, ss * 7 + bev, ss * (18 - m) - bev, ss * 8 - bev, ss * (18 - m) - bev);
                    object3D.triangle(1).setUV(ss * 7 + bev, ss * (17 - m) + bev, ss * 8 - bev, ss * (18 - m) - bev, ss * 8 - bev, ss * (17 - m) + bev);
                    object3D.triangle(2).setUV(ss * (4 + m) - bev, ss * 13 + bev, ss * (3 + m) + bev, ss * 13 + bev, ss * (3 + m) + bev, ss * 14 - bev);
                    object3D.triangle(3).setUV(ss * (4 + m) - bev, ss * 13 + bev, ss * (3 + m) + bev, ss * 14 - bev, ss * (4 + m) - bev, ss * 14 - bev);
                    break;
                case 9: // front up
                    object3D.triangle(0).setUV(ss * (11 + m) - bev, ss * 7 + bev, ss * (10 + m) + bev, ss * 7 + bev, ss * (10 + m) + bev, ss * 8 - bev);
                    object3D.triangle(1).setUV(ss * (11 + m) - bev, ss * 7 + bev, ss * (10 + m) + bev, ss * 8 - bev, ss * (11 + m) - bev, ss * 8 - bev);
                    object3D.triangle(2).setUV(ss * (11 + m) - bev, ss * 6 + bev, ss * (10 + m) + bev, ss * 6 + bev, ss * (10 + m) + bev, ss * 7 - bev);
                    object3D.triangle(3).setUV(ss * (11 + m) - bev, ss * 6 + bev, ss * (10 + m) + bev, ss * 7 - bev, ss * (11 + m) - bev, ss * 7 - bev);
                    break;
                case 10: // left front
                    object3D.triangle(0).setUV(ss * 7 - bev, ss * (11 + m) - bev, ss * 7 - bev, ss * (10 + m) + bev, ss * 6 + bev, ss * (10 + m) + bev);
                    object3D.triangle(1).setUV(ss * 7 - bev, ss * (11 + m) - bev, ss * 6 + bev, ss * (10 + m) + bev, ss * 6 + bev, ss * (11 + m) - bev);
                    object3D.triangle(2).setUV(ss * 8 - bev, ss * (11 + m) - bev, ss * 8 - bev, ss * (10 + m) + bev, ss * 7 + bev, ss * (10 + m) + bev);
                    object3D.triangle(3).setUV(ss * 8 - bev, ss * (11 + m) - bev, ss * 7 + bev, ss * (10 + m) + bev, ss * 7 + bev, ss * (11 + m) - bev);
                    break;
                case 11: // front down
                    object3D.triangle(0).setUV(ss * (10 - m) + bev, ss * 14 - bev, ss * (11 - m) - bev, ss * 14 - bev, ss * (11 - m) - bev, ss * 13 + bev);
                    object3D.triangle(1).setUV(ss * (10 - m) + bev, ss * 14 - bev, ss * (11 - m) - bev, ss * 13 + bev, ss * (10 - m) + bev, ss * 13 + bev);
                    object3D.triangle(2).setUV(ss * (10 - m) + bev, ss * 15 - bev, ss * (11 - m) - bev, ss * 15 - bev, ss * (11 - m) - bev, ss * 14 + bev);
                    object3D.triangle(3).setUV(ss * (10 - m) + bev, ss * 15 - bev, ss * (11 - m) - bev, ss * 14 + bev, ss * (10 - m) + bev, ss * 14 + bev);
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

        int[] mxMap = {0, 0, -1, -1, -1, 0, 1, 1, 1};
        int[] myMap = {0, 1, 1, 0, -1, -1, -1, 0, 1};
        for (int part = 0; part <
                sideCount; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (int i = 0; i <
                    SIDE_VERTS.length / 3; i++) {
                object3D.addVertex(
                        SIDE_VERTS[i * 3], SIDE_VERTS[i * 3 + 1], SIDE_VERTS[i * 3 + 2]);
            }

            for (int i = 0; i <
                    SIDE_FACES.length; i++) {
                for (int j = 2; j <
                        SIDE_FACES[i].length; j++) {
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

            idx3d_Group group = new idx3d_Group();
            group.addChild(object3D);
            parts[sideOffset + part] = group;
        }

        initSideUVMap();
    }

    /**
     * Initializes the UV coordinates for the side parts.
     */
    protected void initSideUVMap() {

        // Mapping from 5x5 cube to 7x7 cube
        //a 0   1       2   3       4   5   6       7   8       9  10  11      12  13      14  15
        //n 0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21
        int[] n = {0, 1, 3, 4, 6, 7, 8, 10, 11, 13, 14, 15, 17, 18, 20, 21};

        // Location of the stickers relative to the side part in the middle
        int[] mxMap = {
            0, // middle
            -1, -1, 1, 1, // corners of inner circle
            0, -1, 0, 1, // edges of inner circle
            -2, -2, 2, 2, // corners of outer circle
            0, -2, 0, 2, // edges 1 of outer circle
            -1, -2, 1, 2, // edges 2 of outer circle
            1, -2, -1, 2 // edges 3 of outer circle
        };
        int[] myMap = {
            0, // middle
            1, -1, -1, 1, // corners of inner circle
            1, 0, -1, 0, // edges of inner circle
            2, -2, -2, 2, // corners of outer circle
            2, 0, -2, 0, // edges 1 of outer circle
            2, -1, -2, 1, // edges 2 of outer circle
            2, 1, -2, -1 // edges 3 of outer circle
        };

        for (int part = 0; part < sideCount; part++) {
            idx3d_Group group = parts[sideOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            int mx = mxMap[part / 6];
            int my = myMap[part / 6];
            switch (part % 6) {
            case 0: // right
                object3D.triangle(0).setUV(ss * (n[12] + my) + bev, ss * (n[7] - mx) + bev, ss * (n[12] + my) + bev, ss * (n[8] - mx) - bev, ss * (n[13] + my) - bev, ss * (n[8] - mx) - bev);
                object3D.triangle(1).setUV(ss * (n[12] + my) + bev, ss * (n[7] - mx) + bev, ss * (n[13] + my) - bev, ss * (n[8] - mx) - bev, ss * (n[13] + my) - bev, ss * (n[7] - mx) + bev);
                break;
            case 1: // up
                object3D.triangle(0).setUV(ss * (n[8] - my) - bev, ss * (n[3] + mx) - bev, ss * (n[8] - my) - bev, ss * (n[2] + mx) + bev, ss * (n[7] - my) + bev, ss * (n[2] + mx) + bev);
                object3D.triangle(1).setUV(ss * (n[8] - my) - bev, ss * (n[3] + mx) - bev, ss * (n[7] - my) + bev, ss * (n[2] + mx) + bev, ss * (n[7] - my) + bev, ss * (n[3] + mx) - bev);
                    break;
                case 2: // front
                    object3D.triangle(0).setUV(ss * (n[8] + mx) - bev, ss * (n[7] + my) + bev, ss * (n[7] + mx) + bev, ss * (n[7] + my) + bev, ss * (n[7] + mx) + bev, ss * (n[8] + my) - bev);
                    object3D.triangle(1).setUV(ss * (n[8] + mx) - bev, ss * (n[7] + my) + bev, ss * (n[7] + mx) + bev, ss * (n[8] + my) - bev, ss * (n[8] + mx) - bev, ss * (n[8] + my) - bev);
                    break;
                case 3: // left
                    object3D.triangle(0).setUV(ss * (n[2] - mx) + bev, ss * (n[8] - my) - bev, ss * (n[3] - mx) - bev, ss * (n[8] - my) - bev, ss * (n[3] - mx) - bev, ss * (n[7] - my) + bev);
                    object3D.triangle(1).setUV(ss * (n[2] - mx) + bev, ss * (n[8] - my) - bev, ss * (n[3] - mx) - bev, ss * (n[7] - my) + bev, ss * (n[2] - mx) + bev, ss * (n[7] - my) + bev);
                    break;
                case 4: // down
                    object3D.triangle(0).setUV(ss * (n[7] - mx) + bev, ss * (n[13] - my) - bev, ss * (n[8] - mx) - bev, ss * (n[13] - my) - bev, ss * (n[8] - mx) - bev, ss * (n[12] - my) + bev);
                    object3D.triangle(1).setUV(ss * (n[7] - mx) + bev, ss * (n[13] - my) - bev, ss * (n[8] - mx) - bev, ss * (n[12] - my) + bev, ss * (n[7] - mx) + bev, ss * (n[12] - my) + bev);
                    break;
                case 5: // back
                    object3D.triangle(0).setUV(ss * (n[13] - my) - bev, ss * (n[13] + mx) - bev, ss * (n[13] - my) - bev, ss * (n[12] + mx) + bev, ss * (n[12] - my) + bev, ss * (n[12] + mx) + bev);
                    object3D.triangle(1).setUV(ss * (n[13] - my) - bev, ss * (n[13] + mx) - bev, ss * (n[12] - my) + bev, ss * (n[12] + mx) + bev, ss * (n[12] - my) + bev, ss * (n[13] + mx) - bev);
                    break;
            }

        }
    }
    /**
     * The numbers show the part indices. The stickers are numbered from top
     * left to bottom right on each face. The sequence of the faces is right,
     * up, front, left, down, back.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |4.0|39 |15 |3.1|27 |51 |2.0|
     *                               +---+---+---+---+---+---+---+
     *                               |42 |55  133 85  109 61 |36 |
     *                               +---+                   +---+
     *                               |18 |103  7  37  13  139|12 |
     *                               +---+                   +---+
     *                               |6.0|79  31  1.2 43  91 |0.0|
     *                               +---+                   +---+
     *                               |30 |127 25  49  19  115|24 |
     *                               +---+                   +---+
     *                               |54 |73  121 97  145 67 |48 |
     *                               +---+---+---+---+---+---+---+
     *                               |6.0|45 |21 |9.1|33 |57 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 4 |42 |18 |6.1|30 |54 | 6 | 6 |45 |21 |9.0|33 |57 | 0 | 0 |48 |24 |0.1|12 |36 | 2 | 2 |51 |27 |3.0|15 |39 | 4 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |75  129 81  105 57 |58 |58 |62  140 92  116 68 |49 |49 |66  144 96  120 72 |52 |52 |59  137 89  113 65 |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |31 |123  27  33   9 135|34 |34 |110 14  44  20  146|25 |25 |114 18  48  24  126|28 |28 |107 11  41  17  143|31 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |7.0|99  51  3.1 39  87 10.0|10.1 86  38 2.3 50  98 |1.1|1.0|90  42  0.0 30  78 |4.0|4.1|83  35  5.2 47  95 |7.1|
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |19 |147 21  45  15  111|22 |22 |134  8  32  26  122|13 |13 |138 12  36   6  102|16 |16 |131 29  53  23  119|19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |43 |69  117 93  141 63 |46 |46 |56  104 80  128 74 |37 |37 |60  108 84  132 54 |40 |40 |77  125 101 149 71 |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 5 |44 |20 |8.1|32 |56 | 7 | 7 |47 |23 11.0|35 |59 | 1 | 1 |50 |26 |2.1|14 |38 | 3 | 3 |53 |29 |5.0| 17|41 | 5 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |7.0|47 |23 11.1|35 |59 |1.0|
     *                               +---+---+---+---+---+---+---+
     *                               |56 |76  130 82  106 58 |50 |
     *                               +---+                   +---+
     *                               |32 |124 28  34  10  136|26 |
     *                               +---+                   +---+
     *                               |8.0|100 52  4.1 40  88 |2.0|
     *                               +---+                   +---+
     *                               |20 |148 22  46  16  112|14 |
     *                               +---+                   +---+
     *                               |44 |70  118 94  142 64 |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |5.0|41 |17 |5.1|29 |53 |3.0|
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 48 + 8, 24 + 8, 0 + 8, 12 + 8, 36 + 8, 2, //
        49 + 8, 66 + 68, 144 + 68, 96 + 68, 120 + 68, 72 + 68, 52 + 8, //
        25 + 8, 114 + 68, 18 + 68, 48 + 68, 24 + 68, 126 + 68, 28 + 8,//
        1 + 8, 90 + 68, 42 + 68, 0 + 68, 30 + 68, 78 + 68, 4 + 8, //
        13 + 8, 138 + 68, 12 + 68, 36 + 68, 6 + 68, 102 + 68, 16 + 8,//
        37 + 8, 60 + 68, 108 + 68, 84 + 68, 132 + 68, 54 + 68, 40 + 8,//
        1, 50 + 8, 26 + 8, 2 + 8, 14 + 8, 38 + 8, 3, // right
        //
        4, 39 + 8, 15 + 8, 3 + 8, 27 + 8, 51 + 8, 2, //
        42 + 8, 55 + 68, 133 + 68, 85 + 68, 109 + 68, 61 + 68, 36 + 8, //
        18 + 8, 103 + 68, 7 + 68, 37 + 68, 13 + 68, 139 + 68, 12 + 8, //
        6 + 8, 79 + 68, 31 + 68, 1 + 68, 43 + 68, 91 + 68, 0 + 8,//
        30 + 8, 127 + 68, 25 + 68, 49 + 68, 19 + 68, 115 + 68, 24 + 8,//
        54 + 8, 73 + 68, 121 + 68, 97 + 68, 145 + 68, 67 + 68, 48 + 8,
        6, 45 + 8, 21 + 8, 9 + 8, 33 + 8, 57 + 8, 0, // up
        //
        6, 45 + 8, 21 + 8, 9 + 8, 33 + 8, 57 + 8, 0,//
        58 + 8, 62 + 68, 140 + 68, 92 + 68, 116 + 68, 68 + 68, 49 + 8, //
        34 + 8, 110 + 68, 14 + 68, 44 + 68, 20 + 68, 146 + 68, 25 + 8,//
        10 + 8, 86 + 68, 38 + 68, 2 + 68, 50 + 68, 98 + 68, 1 + 8,//
        22 + 8, 134 + 68, 8 + 68, 32 + 68, 26 + 68, 122 + 68, 13 + 8,//
        46 + 8, 56 + 68, 104 + 68, 80 + 68, 128 + 68, 74 + 68, 37 + 8,//
        7, 47 + 8, 23 + 8, 11 + 8, 35 + 8, 59 + 8, 1, // front
        //
        4, 42 + 8, 18 + 8, 6 + 8, 30 + 8, 54 + 8, 6, //
        55 + 8, 75 + 68, 129 + 68, 81 + 68, 105 + 68, 57 + 68, 58 + 8,//
        31 + 8, 123 + 68, 27 + 68, 33 + 68, 9 + 68, 135 + 68, 34 + 8,//
        7 + 8, 99 + 68, 51 + 68, 3 + 68, 39 + 68, 87 + 68, 10 + 8,//
        19 + 8, 147 + 68, 21 + 68, 45 + 68, 15 + 68, 111 + 68, 22 + 8,//
        43 + 8, 69 + 68, 117 + 68, 93 + 68, 141 + 68, 63 + 68, 46 + 8,
        5, 44 + 8, 20 + 8, 8 + 8, 32 + 8, 56 + 8, 7, // left
        //
        7, 47 + 8, 23 + 8, 11 + 8, 35 + 8, 59 + 8, 1,//
        56 + 8, 76 + 68, 130 + 68, 82 + 68, 106 + 68, 58 + 68, 50 + 8,
        32 + 8, 124 + 68, 28 + 68, 34 + 68, 10 + 68, 136 + 68, 26 + 8,//
        8 + 8, 100 + 68, 52 + 68, 4 + 68, 40 + 68, 88 + 68, 2 + 8,//
        20 + 8, 148 + 68, 22 + 68, 46 + 68, 16 + 68, 112 + 68, 14 + 8,//
        44 + 8, 70 + 68, 118 + 68, 94 + 68, 142 + 68, 64 + 68, 38 + 8,
        5, 41 + 8, 17 + 8, 5 + 8, 29 + 8, 53 + 8, 3, // down
        //
        2, 51 + 8, 27 + 8, 3 + 8, 15 + 8, 39 + 8, 4,//
        52 + 8, 59 + 68, 137 + 68, 89 + 68, 113 + 68, 65 + 68, 55 + 8,
        28 + 8, 107 + 68, 11 + 68, 41 + 68, 17 + 68, 143 + 68, 31 + 8,//
        4 + 8, 83 + 68, 35 + 68, 5 + 68, 47 + 68, 95 + 68, 7 + 8,//
        16 + 8, 131 + 68, 29 + 68, 53 + 68, 23 + 68, 119 + 68, 19 + 8,//
        40 + 8, 77 + 68, 125 + 68, 101 + 68, 149 + 68, 71 + 68, 43 + 8,
        3, 53 + 8, 29 + 8, 5 + 8, 17 + 8, 41 + 8, 5, // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, 1, // right
        //
        0, 1, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 1, 0, // up
        //
        1, 0, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 0, 1, // front
        //
        1, 1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, 1, // left
        //
        0, 1, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 1, 0, // down
        //
        1, 0, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 0, 1, // back
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
    public int getStickerCount() {
        return 6 * 7 * 7;
    }

    @Nonnull
    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{7*7, 7*7, 7*7, 7*7, 7*7, 7*7});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 7 * 7, 1 * 7 * 7, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 7 * 7, 2 * 7 * 7, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 7 * 7, 3 * 7 * 7, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 7 * 7, 4 * 7 * 7, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 7 * 7, 5 * 7 * 7, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 7 * 7, 6 * 7 * 7, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    @Override
    protected void initActions(@Nonnull idx3d_Scene scene) {
        int i, j;
        PartAction action;

        // Corners
        for (i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            for (j = 0; j < 3; j++) {
                action = new PartAction(
                        i, j, getStickerIndexForPart(i, j));

                idx3d_Object obj = (idx3d_Object) parts[index].getChild(0);
                scene.addMouseListener(obj.triangle(j * 2), action);
                scene.addMouseListener(obj.triangle(j * 2 + 1), action);
                switch (j) {
                case 0: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                    scene.addSwipeListener(obj.triangle(j * 2), a0);
                    scene.addSwipeListener(obj.triangle(j * 2 + 1), a1);
                    scene.addSwipeListener(obj.triangle(6), a0);
                    scene.addSwipeListener(obj.triangle(7), a1);
                    scene.addSwipeListener(obj.triangle(8), a0);
                    scene.addSwipeListener(obj.triangle(9), a1);
                    break;
                }
                    case 1: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f));
                        scene.addSwipeListener(obj.triangle(j * 2), a0);
                        scene.addSwipeListener(obj.triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(obj.triangle(10), a0);
                        scene.addSwipeListener(obj.triangle(11), a1);
                        scene.addSwipeListener(obj.triangle(12), a0);
                        scene.addSwipeListener(obj.triangle(13), a1);
                        break;
                    }
                    case 2: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                        scene.addSwipeListener(obj.triangle(j * 2), a0);
                        scene.addSwipeListener(obj.triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(obj.triangle(14), a0);
                        scene.addSwipeListener(obj.triangle(15), a1);
                        scene.addSwipeListener(obj.triangle(16), a0);
                        scene.addSwipeListener(obj.triangle(17), a1);
                    }
                    break;
                }
            }

        }

        // Edges
        for (i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            for (j = 0; j < 2; j++) {
                action = new PartAction(
                        index, j, getStickerIndexForPart(index, j));

                idx3d_Object obj = (idx3d_Object) parts[index].getChild(0);
                scene.addMouseListener(obj.triangle(j * 2), action);
                scene.addMouseListener(obj.triangle(j * 2 + 1), action);
                switch (j) {
                case 0: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                    scene.addSwipeListener(obj.triangle(j * 2), a0);
                    scene.addSwipeListener(obj.triangle(j * 2 + 1), a1);
                    scene.addSwipeListener(obj.triangle(4), a0);
                    scene.addSwipeListener(obj.triangle(5), a1);
                    scene.addSwipeListener(obj.triangle(6), a0);
                    scene.addSwipeListener(obj.triangle(7), a1);
                    scene.addSwipeListener(obj.triangle(8), a0);
                    scene.addSwipeListener(obj.triangle(9), a1);
                    break;
                }
                    case 1: {
                        SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f + Math.PI / 4f));
                        SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f));
                        scene.addSwipeListener(obj.triangle(j * 2), a0);
                        scene.addSwipeListener(obj.triangle(j * 2 + 1), a1);
                        scene.addSwipeListener(obj.triangle(10), a0);
                        scene.addSwipeListener(obj.triangle(11), a1);
                        scene.addSwipeListener(obj.triangle(12), a0);
                        scene.addSwipeListener(obj.triangle(13), a1);
                        scene.addSwipeListener(obj.triangle(14), a0);
                        scene.addSwipeListener(obj.triangle(15), a1);
                        break;
                    }
                }
            }

        }

        // Sides
        for (i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            action = new PartAction(
                    i + sideOffset, 0, getStickerIndexForPart(index, 0));

            idx3d_Object obj = (idx3d_Object) parts[index].getChild(0);
            scene.addMouseListener(obj.triangle(0), action);
            scene.addMouseListener(obj.triangle(1), action);
            SwipeAction a0 = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI / 2f + Math.PI / 4f));
            SwipeAction a1 = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) Math.PI / 2f);
            scene.addSwipeListener(obj.triangle(0), a0);
            scene.addSwipeListener(obj.triangle(1), a1);
            scene.addSwipeListener(obj.triangle(2), a0);
            scene.addSwipeListener(obj.triangle(3), a1);
            scene.addSwipeListener(obj.triangle(4), a0);
            scene.addSwipeListener(obj.triangle(5), a1);
            scene.addSwipeListener(obj.triangle(6), a0);
            scene.addSwipeListener(obj.triangle(7), a1);
            scene.addSwipeListener(obj.triangle(8), a0);
            scene.addSwipeListener(obj.triangle(9), a1);
        }

        for (i = 0; i < partCount; i++) {
            action = new PartAction(
                    i, -1, -1);
            for (idx3d_Node child : parts[i].children()) {
                scene.addMouseListener((idx3d_Object) child, action);
            }
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

    @Nonnull
    @Override
    public CubeKind getKind() {
        return CubeKind.CUBE_7;
    }
}
