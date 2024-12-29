/*
 * @(#)RevengeCubeIdx3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.DefaultCubeAttributes;
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
 * RevengeCubeIdx3D.
 *
 * @author Werner Randelshofer
 */
public class RevengeCubeIdx3D extends AbstractRevengeCubeIdx3D {

    private final static int STICKER_COUNT = 6 * 4 * 4;
    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one 12-th of the image width.
     */
    private final static float ss = imageWidth / 12f;
    /**
     * Bevelling is one pixel of a sticker image.
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

                    {16, 0, 8}, //Top Front Left luuf luff lluf
                    {18, 10, 2}, //Top Right Front ruuf rruf ruff
                    {22, 14, 6}, //Top Left Back luub llub lubb
                    {20, 4, 12}, //Top Back Right ruub rubb rrub

                    // Cut Off Faces: The following faces need only be drawn,
                    //                when a face layer of the cube is being twisted.
                    {12, 13, 11, 10}, //Right
                    {17, 19, 21, 23}, //Bottom
                    {4, 6, 7, 5}, //Back
                    // Cut Off Faces:
                    // These faces can never be seen unless we would take the cube apart.
                    {21, 13, 5}, //Bottom Right Back rddb rrdb rdbb
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
     *    0   1   2   3   4   5   6   7   8   9  10  11  12
     *  0                 +---+---+---+---+
     *                    |4.0|       |2.0|
     *  1                 +---+       +---+
     *                    |               |
     *  2                 +       u       +
     *                    |               |
     *  3                 +---+       +---+
     *                 ufl|6.0|       |0.0|urf
     *  4 +---+---+---+---+---+---+---+---+---+---+---+---+...............+
     *    |4.1|       |6.2|6.1|       |0.2|0.1|       |2.2|               .
     *  5 +---+       +---+---+       +---+---+       +---+               .
     *    |               |               |               |               .
     *  6 +       l       +       f       +       r       +       b       .
     *    |               |               |               |               .
     *  7 +---+       +---+---+       +---+---+       +---+               .
     *    |5.2|       |7.1|7.2|       |1.1|1.2|       |3.1|               .
     *  8 +---+---+---+---+---+---+---+---+---+---+---+---+...............+
     *                 dlf|7.0|       |1.0|2.1|       |4.2|       |
     *  9                 +---+       +---+---+       +---+       |
     *                    |               |               |       |
     * 10                 +       d       +       b       +    &lt;--+
     *                    |               |               |
     * 11                 +---+       +---+---+       +---+
     *                    |5.0|       |3.0|3.2|       |5.1|
     * 12                 +---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Group group = parts[cornerOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            switch (part) {
            case 0: // up right front
                object3D.triangle(0).setUV(ss * 8 - bev, ss * 4 - bev, ss * 8 - bev, ss * 3 + bev, ss * 7 + bev, ss * 3 + bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * 4 - bev, ss * 7 + bev, ss * 3 + bev, ss * 7 + bev, ss * 4 - bev);
                object3D.triangle(2).setUV(ss * 8 + bev, ss * 5 - bev, ss * 9 - bev, ss * 5 - bev, ss * 9 - bev, ss * 4 + bev);
                object3D.triangle(3).setUV(ss * 8 + bev, ss * 5 - bev, ss * 9 - bev, ss * 4 + bev, ss * 8 + bev, ss * 4 + bev);
                object3D.triangle(4).setUV(ss * 7 + bev, ss * 5 - bev, ss * 8 - bev, ss * 5 - bev, ss * 8 - bev, ss * 4 + bev);
                object3D.triangle(5).setUV(ss * 7 + bev, ss * 5 - bev, ss * 8 - bev, ss * 4 + bev, ss * 7 + bev, ss * 4 + bev);
                break;
            case 1: // down front right
                object3D.triangle(0).setUV(ss * 8 - bev, ss * 8 + bev, ss * 7 + bev, ss * 8 + bev, ss * 7 + bev, ss * 9 - bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * 8 + bev, ss * 7 + bev, ss * 9 - bev, ss * 8 - bev, ss * 9 - bev);
                object3D.triangle(2).setUV(ss * 8 - bev, ss * 7 + bev, ss * 7 + bev, ss * 7 + bev, ss * 7 + bev, ss * 8 - bev);
                object3D.triangle(3).setUV(ss * 8 - bev, ss * 7 + bev, ss * 7 + bev, ss * 8 - bev, ss * 8 - bev, ss * 8 - bev);
                object3D.triangle(4).setUV(ss * 9 - bev, ss * 7 + bev, ss * 8 + bev, ss * 7 + bev, ss * 8 + bev, ss * 8 - bev);
                object3D.triangle(5).setUV(ss * 9 - bev, ss * 7 + bev, ss * 8 + bev, ss * 8 - bev, ss * 9 - bev, ss * 8 - bev);
                break;
            case 2: // up back right
                object3D.triangle(0).setUV(ss * 8 - bev, ss * 0 + bev, ss * 7 + bev, ss * 0 + bev, ss * 7 + bev, ss * 1 - bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * 0 + bev, ss * 7 + bev, ss * 1 - bev, ss * 8 - bev, ss * 1 - bev);
                object3D.triangle(2).setUV(ss * 8 + bev, ss * 9 - bev, ss * 9 - bev, ss * 9 - bev, ss * 9 - bev, ss * 8 + bev);
                object3D.triangle(3).setUV(ss * 8 + bev, ss * 9 - bev, ss * 9 - bev, ss * 8 + bev, ss * 8 + bev, ss * 8 + bev);
                object3D.triangle(4).setUV(ss * 11 + bev, ss * 5 - bev, ss * 12 - bev, ss * 5 - bev, ss * 12 - bev, ss * 4 + bev);
                object3D.triangle(5).setUV(ss * 11 + bev, ss * 5 - bev, ss * 12 - bev, ss * 4 + bev, ss * 11 + bev, ss * 4 + bev);
                break;
            case 3: // down right back
                object3D.triangle(0).setUV(ss * 8 - bev, ss * 12 - bev, ss * 8 - bev, ss * 11 + bev, ss * 7 + bev, ss * 11 + bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * 12 - bev, ss * 7 + bev, ss * 11 + bev, ss * 7 + bev, ss * 12 - bev);
                object3D.triangle(2).setUV(ss * 12 - bev, ss * 7 + bev, ss * 11 + bev, ss * 7 + bev, ss * 11 + bev, ss * 8 - bev);
                object3D.triangle(3).setUV(ss * 12 - bev, ss * 7 + bev, ss * 11 + bev, ss * 8 - bev, ss * 12 - bev, ss * 8 - bev);
                object3D.triangle(4).setUV(ss * 9 - bev, ss * 11 + bev, ss * 8 + bev, ss * 11 + bev, ss * 8 + bev, ss * 12 - bev);
                object3D.triangle(5).setUV(ss * 9 - bev, ss * 11 + bev, ss * 8 + bev, ss * 12 - bev, ss * 9 - bev, ss * 12 - bev);
                break;
            case 4: // up left back
                object3D.triangle(0).setUV(ss * 4 + bev, ss * 0 + bev, ss * 4 + bev, ss * 1 - bev, ss * 5 - bev, ss * 1 - bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * 0 + bev, ss * 5 - bev, ss * 1 - bev, ss * 5 - bev, ss * 0 + bev);
                object3D.triangle(2).setUV(ss * 0 + bev, ss * 5 - bev, ss * 1 - bev, ss * 5 - bev, ss * 1 - bev, ss * 4 + bev);
                object3D.triangle(3).setUV(ss * 0 + bev, ss * 5 - bev, ss * 1 - bev, ss * 4 + bev, ss * 0 + bev, ss * 4 + bev);
                object3D.triangle(4).setUV(ss * 11 + bev, ss * 9 - bev, ss * 12 - bev, ss * 9 - bev, ss * 12 - bev, ss * 8 + bev);
                object3D.triangle(5).setUV(ss * 11 + bev, ss * 9 - bev, ss * 12 - bev, ss * 8 + bev, ss * 11 + bev, ss * 8 + bev);
                break;
            case 5: // down back left
                object3D.triangle(0).setUV(ss * 4 + bev, ss * 12 - bev, ss * 5 - bev, ss * 12 - bev, ss * 5 - bev, ss * 11 + bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * 12 - bev, ss * 5 - bev, ss * 11 + bev, ss * 4 + bev, ss * 11 + bev);
                object3D.triangle(2).setUV(ss * 12 - bev, ss * 11 + bev, ss * 11 + bev, ss * 11 + bev, ss * 11 + bev, ss * 12 - bev);
                object3D.triangle(3).setUV(ss * 12 - bev, ss * 11 + bev, ss * 11 + bev, ss * 12 - bev, ss * 12 - bev, ss * 12 - bev);
                object3D.triangle(4).setUV(ss * 1 - bev, ss * 7 + bev, ss * 0 + bev, ss * 7 + bev, ss * 0 + bev, ss * 8 - bev);
                object3D.triangle(5).setUV(ss * 1 - bev, ss * 7 + bev, ss * 0 + bev, ss * 8 - bev, ss * 1 - bev, ss * 8 - bev);
                break;
            case 6: // up front left
                object3D.triangle(0).setUV(ss * 4 + bev, ss * 4 - bev, ss * 5 - bev, ss * 4 - bev, ss * 5 - bev, ss * 3 + bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * 4 - bev, ss * 5 - bev, ss * 3 + bev, ss * 4 + bev, ss * 3 + bev);
                object3D.triangle(2).setUV(ss * 4 + bev, ss * 5 - bev, ss * 5 - bev, ss * 5 - bev, ss * 5 - bev, ss * 4 + bev);
                object3D.triangle(3).setUV(ss * 4 + bev, ss * 5 - bev, ss * 5 - bev, ss * 4 + bev, ss * 4 + bev, ss * 4 + bev);
                object3D.triangle(4).setUV(ss * 3 + bev, ss * 5 - bev, ss * 4 - bev, ss * 5 - bev, ss * 4 - bev, ss * 4 + bev);
                object3D.triangle(5).setUV(ss * 3 + bev, ss * 5 - bev, ss * 4 - bev, ss * 4 + bev, ss * 3 + bev, ss * 4 + bev);
                break;
            case 7: // down left front
                object3D.triangle(0).setUV(ss * 4 + bev, ss * 8 + bev, ss * 4 + bev, ss * 9 - bev, ss * 5 - bev, ss * 9 - bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * 8 + bev, ss * 5 - bev, ss * 9 - bev, ss * 5 - bev, ss * 8 + bev);
                object3D.triangle(2).setUV(ss * 4 - bev, ss * 7 + bev, ss * 3 + bev, ss * 7 + bev, ss * 3 + bev, ss * 8 - bev);
                object3D.triangle(3).setUV(ss * 4 - bev, ss * 7 + bev, ss * 3 + bev, ss * 8 - bev, ss * 4 - bev, ss * 8 - bev);
                object3D.triangle(4).setUV(ss * 5 - bev, ss * 7 + bev, ss * 4 + bev, ss * 7 + bev, ss * 4 + bev, ss * 8 - bev);
                object3D.triangle(5).setUV(ss * 5 - bev, ss * 7 + bev, ss * 4 + bev, ss * 8 - bev, ss * 5 - bev, ss * 8 - bev);
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

                    //
                    {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                    {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf

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
                    {4, 6, 7, 5}, //Back
                    // Cut Off Faces:
                    // These faces can never be seen unless we would take the cube apart.
                    {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb
                    {23, 7, 15}, //Bottom Back Left lddb ldbb lldb
                    {21, 13, 5}, //Bottom Right Back rddb rrdb rdbb
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
     * Initializes the Edge UV Map.
     * <pre>
     *    0   1   2   3   4   5   6   7   8   9  10  11  12
     *  0                 +---+---+---+---+
     *                    |   |3.1|15 |   |
     *  1                 +---+---+---+---+
     *                    |6.0|       |0.0|
     *  2                 +---+   u   +---+
     *                    |18 |       |12 |
     *  3                 +---+---+---+---+
     *                    |   |9.1|21 |   |
     *  4 +---+---+---+---+---+---*---+---+---+---+---+---+...............+
     *    |   |6.1|18 |   |   |9.0|21 |   |   |12 |0.1|   |               '
     *  5 +---+---+---+---+---+---+---+---+---+---+---+---+               '
     *    |19 |       |22 |22 |       |13 |13 |       |16 |               '
     *  6 +---+   l   +---+---+   f   +---+---+   r   +---+       b       '
     *    |7.0|       10.0|10.1       |1.1|1.0|       |4.0|               '
     *  7 +---+---+---+---+---+---+---+---+---+---+---+---+               '
     *    |   |8.1|20 |   |   11.0|23 |   |   |14 |2.1|   |               '
     *  8 +---+---+---+---+---+---+---+---+---+---+---+---+...............+
     *                    |   11.1|23 |   |   |15 |3.0|   |       |
     *  9                 +---+---+---+---+---+---+---+---+       |
     *                    |20 |       |14 |16 |       |19 |       |
     * 10                 +---+   d   +---+---+   b   +---+    &lt;--+
     *                    |8.0|       |2.0|4.1|       |7.1|
     * 11                 +---+---+---+---+---+---+---+---+
     *                    |   |5.1|17 |   |   |17 |5.0|   |
     * 12                 +---+---+---+---+---+---+---+---+
     * </pre>
     */
    protected void initEdgeUVMap() {
        for (int part = 0; part < edgeCount; part++) {
            idx3d_Group group = parts[edgeOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            //int m = part / 12;
            int m;
            switch (part) {
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
                m = 0;
                break;
            }
            switch (part % 12) {
            case 0: // up right
                object3D.triangle(0).setUV(ss * 8 - bev, ss * (2 + m) - bev, ss * 8 - bev, ss * (1 + m) + bev, ss * 7 + bev, ss * (1 + m) + bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * (2 + m) - bev, ss * 7 + bev, ss * (1 + m) + bev, ss * 7 + bev, ss * (2 + m) - bev);
                object3D.triangle(2).setUV(ss * (10 - m) + bev, ss * 5 - bev, ss * (11 - m) - bev, ss * 5 - bev, ss * (11 - m) - bev, ss * 4 + bev);
                object3D.triangle(3).setUV(ss * (10 - m) + bev, ss * 5 - bev, ss * (11 - m) - bev, ss * 4 + bev, ss * (10 - m) + bev, ss * 4 + bev);
                break;
            case 1: // right front
                object3D.triangle(0).setUV(ss * 8 + bev, ss * (6 - m) + bev, ss * 8 + bev, ss * (7 - m) - bev, ss * 9 - bev, ss * (7 - m) - bev);
                object3D.triangle(1).setUV(ss * 8 + bev, ss * (6 - m) + bev, ss * 9 - bev, ss * (7 - m) - bev, ss * 9 - bev, ss * (6 - m) + bev);
                object3D.triangle(2).setUV(ss * 7 + bev, ss * (6 - m) + bev, ss * 7 + bev, ss * (7 - m) - bev, ss * 8 - bev, ss * (7 - m) - bev);
                object3D.triangle(3).setUV(ss * 7 + bev, ss * (6 - m) + bev, ss * 8 - bev, ss * (7 - m) - bev, ss * 8 - bev, ss * (6 - m) + bev);
                break;
            case 2: // down right
                object3D.triangle(0).setUV(ss * 8 - bev, ss * (10 + m) - bev, ss * 8 - bev, ss * (9 + m) + bev, ss * 7 + bev, ss * (9 + m) + bev);
                object3D.triangle(1).setUV(ss * 8 - bev, ss * (10 + m) - bev, ss * 7 + bev, ss * (9 + m) + bev, ss * 7 + bev, ss * (10 + m) - bev);
                object3D.triangle(2).setUV(ss * (10 + m) - bev, ss * 7 + bev, ss * (9 + m) + bev, ss * 7 + bev, ss * (9 + m) + bev, ss * 8 - bev);
                object3D.triangle(3).setUV(ss * (10 + m) - bev, ss * 7 + bev, ss * (9 + m) + bev, ss * 8 - bev, ss * (10 + m) - bev, ss * 8 - bev);
                break;
            case 3: // back up
                object3D.triangle(0).setUV(ss * (10 + m) - bev, ss * 8 + bev, ss * (9 + m) + bev, ss * 8 + bev, ss * (9 + m) + bev, ss * 9 - bev);
                object3D.triangle(1).setUV(ss * (10 + m) - bev, ss * 8 + bev, ss * (9 + m) + bev, ss * 9 - bev, ss * (10 + m) - bev, ss * 9 - bev);
                object3D.triangle(2).setUV(ss * (6 - m) + bev, ss * 1 - bev, ss * (7 - m) - bev, ss * 1 - bev, ss * (7 - m) - bev, ss * 0 + bev);
                object3D.triangle(3).setUV(ss * (6 - m) + bev, ss * 1 - bev, ss * (7 - m) - bev, ss * 0 + bev, ss * (6 - m) + bev, ss * 0 + bev);
                break;
            case 4: // right back
                object3D.triangle(0).setUV(ss * 12 - bev, ss * (6 + m) - bev, ss * 12 - bev, ss * (5 + m) + bev, ss * 11 + bev, ss * (5 + m) + bev);
                object3D.triangle(1).setUV(ss * 12 - bev, ss * (6 + m) - bev, ss * 11 + bev, ss * (5 + m) + bev, ss * 11 + bev, ss * (6 + m) - bev);
                object3D.triangle(2).setUV(ss * 9 - bev, ss * (10 + m) - bev, ss * 9 - bev, ss * (9 + m) + bev, ss * 8 + bev, ss * (9 + m) + bev);
                object3D.triangle(3).setUV(ss * 9 - bev, ss * (10 + m) - bev, ss * 8 + bev, ss * (9 + m) + bev, ss * 8 + bev, ss * (10 + m) - bev);
                break;
            case 5: // back down
                object3D.triangle(0).setUV(ss * (10 - m) + bev, ss * 12 - bev, ss * (11 - m) - bev, ss * 12 - bev, ss * (11 - m) - bev, ss * 11 + bev);
                object3D.triangle(1).setUV(ss * (10 - m) + bev, ss * 12 - bev, ss * (11 - m) - bev, ss * 11 + bev, ss * (10 - m) + bev, ss * 11 + bev);
                object3D.triangle(2).setUV(ss * (6 + m) - bev, ss * 11 + bev, ss * (5 + m) + bev, ss * 11 + bev, ss * (5 + m) + bev, ss * 12 - bev);
                object3D.triangle(3).setUV(ss * (6 + m) - bev, ss * 11 + bev, ss * (5 + m) + bev, ss * 12 - bev, ss * (6 + m) - bev, ss * 12 - bev);
                break;
            case 6: // up left
                object3D.triangle(0).setUV(ss * 4 + bev, ss * (2 - m) + bev, ss * 4 + bev, ss * (3 - m) - bev, ss * 5 - bev, ss * (3 - m) - bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * (2 - m) + bev, ss * 5 - bev, ss * (3 - m) - bev, ss * 5 - bev, ss * (2 - m) + bev);
                object3D.triangle(2).setUV(ss * (2 - m) + bev, ss * 5 - bev, ss * (3 - m) - bev, ss * 5 - bev, ss * (3 - m) - bev, ss * 4 + bev);
                object3D.triangle(3).setUV(ss * (2 - m) + bev, ss * 5 - bev, ss * (3 - m) - bev, ss * 4 + bev, ss * (2 - m) + bev, ss * 4 + bev);
                break;
            case 7: // left back
                object3D.triangle(0).setUV(ss * 0 + bev, ss * (6 - m) + bev, ss * 0 + bev, ss * (7 - m) - bev, ss * 1 - bev, ss * (7 - m) - bev);
                object3D.triangle(1).setUV(ss * 0 + bev, ss * (6 - m) + bev, ss * 1 - bev, ss * (7 - m) - bev, ss * 1 - bev, ss * (6 - m) + bev);
                object3D.triangle(2).setUV(ss * 11 + bev, ss * (10 - m) + bev, ss * 11 + bev, ss * (11 - m) - bev, ss * 12 - bev, ss * (11 - m) - bev);
                object3D.triangle(3).setUV(ss * 11 + bev, ss * (10 - m) + bev, ss * 12 - bev, ss * (11 - m) - bev, ss * 12 - bev, ss * (10 - m) + bev);
                break;
            case 8: // down left
                object3D.triangle(0).setUV(ss * 4 + bev, ss * (10 - m) + bev, ss * 4 + bev, ss * (11 - m) - bev, ss * 5 - bev, ss * (11 - m) - bev);
                object3D.triangle(1).setUV(ss * 4 + bev, ss * (10 - m) + bev, ss * 5 - bev, ss * (11 - m) - bev, ss * 5 - bev, ss * (10 - m) + bev);
                object3D.triangle(2).setUV(ss * (2 + m) - bev, ss * 7 + bev, ss * (1 + m) + bev, ss * 7 + bev, ss * (1 + m) + bev, ss * 8 - bev);
                object3D.triangle(3).setUV(ss * (2 + m) - bev, ss * 7 + bev, ss * (1 + m) + bev, ss * 8 - bev, ss * (2 + m) - bev, ss * 8 - bev);
                break;
            case 9: // front up
                object3D.triangle(0).setUV(ss * (6 + m) - bev, ss * 4 + bev, ss * (5 + m) + bev, ss * 4 + bev, ss * (5 + m) + bev, ss * 5 - bev);
                object3D.triangle(1).setUV(ss * (6 + m) - bev, ss * 4 + bev, ss * (5 + m) + bev, ss * 5 - bev, ss * (6 + m) - bev, ss * 5 - bev);
                object3D.triangle(2).setUV(ss * (6 + m) - bev, ss * 3 + bev, ss * (5 + m) + bev, ss * 3 + bev, ss * (5 + m) + bev, ss * 4 - bev);
                object3D.triangle(3).setUV(ss * (6 + m) - bev, ss * 3 + bev, ss * (5 + m) + bev, ss * 4 - bev, ss * (6 + m) - bev, ss * 4 - bev);
                break;
            case 10: // left front
                object3D.triangle(0).setUV(ss * 4 - bev, ss * (6 + m) - bev, ss * 4 - bev, ss * (5 + m) + bev, ss * 3 + bev, ss * (5 + m) + bev);
                object3D.triangle(1).setUV(ss * 4 - bev, ss * (6 + m) - bev, ss * 3 + bev, ss * (5 + m) + bev, ss * 3 + bev, ss * (6 + m) - bev);
                object3D.triangle(2).setUV(ss * 5 - bev, ss * (6 + m) - bev, ss * 5 - bev, ss * (5 + m) + bev, ss * 4 + bev, ss * (5 + m) + bev);
                object3D.triangle(3).setUV(ss * 5 - bev, ss * (6 + m) - bev, ss * 4 + bev, ss * (5 + m) + bev, ss * 4 + bev, ss * (6 + m) - bev);
                break;
            case 11: // front down
                object3D.triangle(0).setUV(ss * (6 - m) + bev, ss * 8 - bev, ss * (7 - m) - bev, ss * 8 - bev, ss * (7 - m) - bev, ss * 7 + bev);
                object3D.triangle(1).setUV(ss * (6 - m) + bev, ss * 8 - bev, ss * (7 - m) - bev, ss * 7 + bev, ss * (6 - m) + bev, ss * 7 + bev);
                object3D.triangle(2).setUV(ss * (6 - m) + bev, ss * 9 - bev, ss * (7 - m) - bev, ss * 9 - bev, ss * (7 - m) - bev, ss * 8 + bev);
                object3D.triangle(3).setUV(ss * (6 - m) + bev, ss * 9 - bev, ss * (7 - m) - bev, ss * 8 + bev, ss * (6 - m) + bev, ss * 8 + bev);
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

                    {4, 6, 7, 5}, //Back
                    {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                    {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf

                    {16, 0, 8}, //Top Front Left luuf luff lluf
                    {18, 10, 2}, //Top Right Front ruuf rruf ruff

                    // Cut Off Faces: The following faces need only be drawn,
                    //                when a layer of the cube is being twisted.
                    {16, 22, 20, 18}, //Top
                    {14, 8, 9, 15}, //Left
                    {12, 13, 11, 10}, //Right
                    {17, 19, 21, 23}, //Bottom

                    // Cut Off Faces:
                    // These faces can never be seen unless we would take the cube apart.
                    {22, 14, 6}, //Top Left Back luub llub lubb
                    {20, 4, 12}, //Top Back Right ruub rubb rrub
                    {20, 22, 6, 4}, //Top Back

                    {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb

                    {4, 5, 13, 12}, //Back Right
                    {7, 6, 14, 15}, //Back Left
                    {23, 7, 15}, //Bottom Back Left lddb ldbb lldb
                    {21, 13, 5}, //Bottom Right Back rddb rrdb rdbb
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
            idx3d_Group group = new idx3d_Group();
            group.addChild(object3D);
            parts[sideOffset + part] = group;
        }
        initSideUVMap();
    }

    /**
     * Initializes the UV coordinates for the side parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9  10  11  12
     * 0                 +---+-----------+
     *                   |               |
     * 1                 |   +---+   +   |
     *                   |   | 1 |   u   |
     * 2                 |   +---+       |
     *                   |               |
     * 3                 |   +       +   |
     *                   |               |
     * 4 +---------------+---------------+---------------+...............+
     *   |               |               |               |               '
     * 5 |   +   +---+   |   +       +   |   +       +   |               '
     *   |       | 3 |l  |           f   |           r   |               '
     * 6 |       +---+   |   +---+       |       +---+   |       b       '
     *   |               |   | 2 |       |       | 0 |   |               '
     * 7 |   +       +   |   +---+   +   |   +   +---+   |               '
     *   |               |               |               |               '
     * 8 +---------------+---+-----------+---------------+...............+
     *                   |               |               |     |
     * 9                 |   +   +---+   |   +---+   +   |     |
     *                   |       | 4 |d  |   | 5 |   b   |     |
     * 10                |       +---+   |   +---+       |  &lt;--+
     *                   |               |               |
     * 11                |   +       +   |   +       +   |
     *                   |               |               |
     * 12                +---+-----------+---------------+
     *
     * Whereas each area marked by the + symbols contains side parts with the
     * following placements:
     *
     * +---+---+
     * | 1   2 |
     * +       +
     * | 0   3 |
     * +---+---+
     * </pre>
     */
    protected void initSideUVMap() {

        /**
         * UV coordinates for stickers on side parts.
         * First dimension = parts,
         * Second dimension = sticker coordinates
         * Third dimension = x and y coordinate values
         */
        for (int part = 0; part < sideCount; part++) {
            idx3d_Group group = parts[sideOffset + part];
            idx3d_Object object3D = (idx3d_Object) group.getChild(0);
            int mx = (part / 6) / 2; // 0,0,1,1
            int my = (part / 6 == 1 || part / 6 == 2) ? 1 : 0; // 0,1,1,0
            switch (part % 6) {
            case 0: // right
                object3D.triangle(0).setUV(ss * (10 - my) + bev, ss * (6 - mx) + bev, ss * (10 - my) + bev, ss * (7 - mx) - bev, ss * (11 - my) - bev, ss * (7 - mx) - bev);
                object3D.triangle(1).setUV(ss * (10 - my) + bev, ss * (6 - mx) + bev, ss * (11 - my) - bev, ss * (7 - mx) - bev, ss * (11 - my) - bev, ss * (6 - mx) + bev);
                break;
            case 1: // up
                object3D.triangle(0).setUV(ss * (6 + my) - bev, ss * (2 + mx) - bev, ss * (6 + my) - bev, ss * (1 + mx) + bev, ss * (5 + my) + bev, ss * (1 + mx) + bev);
                object3D.triangle(1).setUV(ss * (6 + my) - bev, ss * (2 + mx) - bev, ss * (5 + my) + bev, ss * (1 + mx) + bev, ss * (5 + my) + bev, ss * (2 + mx) - bev);
                break;
            case 2: // front
                object3D.triangle(0).setUV(ss * (6 + mx) - bev, ss * (6 - my) + bev, ss * (5 + mx) + bev, ss * (6 - my) + bev, ss * (5 + mx) + bev, ss * (7 - my) - bev);
                object3D.triangle(1).setUV(ss * (6 + mx) - bev, ss * (6 - my) + bev, ss * (5 + mx) + bev, ss * (7 - my) - bev, ss * (6 + mx) - bev, ss * (7 - my) - bev);
                break;
            case 3: // left
                object3D.triangle(0).setUV(ss * (2 - mx) + bev, ss * (6 + my) - bev, ss * (3 - mx) - bev, ss * (6 + my) - bev, ss * (3 - mx) - bev, ss * (5 + my) + bev);
                object3D.triangle(1).setUV(ss * (2 - mx) + bev, ss * (6 + my) - bev, ss * (3 - mx) - bev, ss * (5 + my) + bev, ss * (2 - mx) + bev, ss * (5 + my) + bev);
                break;
            case 4: // down
                object3D.triangle(0).setUV(ss * (6 - mx) + bev, ss * (10 + my) - bev, ss * (7 - mx) - bev, ss * (10 + my) - bev, ss * (7 - mx) - bev, ss * (9 + my) + bev);
                object3D.triangle(1).setUV(ss * (6 - mx) + bev, ss * (10 + my) - bev, ss * (7 - mx) - bev, ss * (9 + my) + bev, ss * (6 - mx) + bev, ss * (9 + my) + bev);
                break;
            case 5: // back
                object3D.triangle(0).setUV(ss * (10 + my) - bev, ss * (10 + mx) - bev, ss * (10 + my) - bev, ss * (9 + mx) + bev, ss * (9 + my) + bev, ss * (9 + mx) + bev);
                object3D.triangle(1).setUV(ss * (10 + my) - bev, ss * (10 + mx) - bev, ss * (9 + my) + bev, ss * (9 + mx) + bev, ss * (9 + my) + bev, ss * (10 + mx) - bev);
                break;
            }

        }
    }

    /**
     * Sticker to part map.<br>
     * (the number before the dot indicates the part,
     * the number after the dot indicates the sticker.)
     * <pre>
     *                 +---+---+---+---+
     *                 4.16|11 |23 |2.0|
     *                 +--- --- --- ---+
     *                 |14 |33  39 | 8 |
     *                 +---         ---+
     *                 |26 |51  45 |20 |
     *                 +--- --- --- ---+
     *                 | 6 |17 |29 |0.31
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * 4.46|14 |26 | 6 6.32|17 |29 | 0 |0.0|20 | 8 2.80| 2 |23 |11 | 4 |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |27 |53  35 |30 |30 |40  46 |21 |21 |44  50 |24 |24 |37  43 |27 |
     * +---         ---+---         ---+---         ---+---         ---+
     * |15 |47  41 |18 |18 |34  52 | 9 | 9 |38  32 |12 |12 |55  49 |15 |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * | 5 |16 |28 |7.63 7 |19 |31 |1.45 1 |22 |10 |3.15 3 |25 |13 |5.95
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 7.64|19 |31 | 1 |
     *                 +--- --- --- ---+
     *                 |28 |54  36 |22 |
     *                 +---         ---+
     *                 |16 |48  42 |10 |
     *                 +--- --- --- ---+
     *                 | 5 |13 |25 |3.79
     *                 +---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
            0, 20, 8, 2,/**/ 21, 44, 50, 24,/**/ 9, 38, 32, 12,/**/ 1, 22, 10, 3, // right
            4, 11, 23, 2,/**/ 14, 33, 39, 8,/**/ 26, 51, 45, 20,/**/ 6, 17, 29, 0, // up
            6, 17, 29, 0,/**/ 30, 40, 46, 21,/**/ 18, 34, 52, 9,/**/ 7, 19, 31, 1, // front
            4, 14, 26, 6,/**/ 27, 53, 35, 30,/**/ 15, 47, 41, 18,/**/ 5, 16, 28, 7, // left
            7, 19, 31, 1,/**/ 28, 54, 36, 22,/**/ 16, 48, 42, 10,/**/ 5, 13, 25, 3, // down
            2, 23, 11, 4,/**/ 24, 37, 43, 27,/**/ 12, 55, 49, 15,/**/ 3, 25, 13, 5 // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }

    private final static int[] stickerToFaceMap = {
            1, 1, 1, 2, /**/ 0, 0, 0, 0,/**/ 0, 0, 0, 0,/**/ 2, 1, 1, 1, // right
            0, 1, 1, 0, /**/ 0, 0, 0, 0,/**/ 0, 0, 0, 0,/**/0, 1, 1, 0, // up
            1, 0, 0, 2, /**/ 1, 0, 0, 1,/**/ 1, 0, 0, 1,/**/ 2, 0, 0, 1, // front
            1, 1, 1, 2, /**/ 0, 0, 0, 0,/**/ 0, 0, 0, 0,/**/ 2, 1, 1, 1, // left
            0, 1, 1, 0, /**/ 0, 0, 0, 0,/**/ 0, 0, 0, 0,/**/ 0, 1, 1, 0, // down
            1, 0, 0, 2, /**/ 1, 0, 0, 1,/**/ 1, 0, 0, 1,/**/ 2, 0, 0, 1, // back
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
        return 6 * 4 * 4;
    }

    @Nonnull
    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(), new int[]{16, 16, 16, 16, 16, 16});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 4 * 4, 1 * 4 * 4, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 4 * 4, 2 * 4 * 4, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 4 * 4, 3 * 4 * 4, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 4 * 4, 4 * 4 * 4, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 4 * 4, 5 * 4 * 4, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 4 * 4, 6 * 4 * 4, new Color(255, 70, 0)); // Back: Orange

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

                idx3d_Group group = parts[index];
                idx3d_Object obj = (idx3d_Object) group.getChild(0);
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

                idx3d_Group group = parts[index];
                idx3d_Object obj = (idx3d_Object) group.getChild(0);
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

            idx3d_Group group = parts[index];
            idx3d_Object obj = (idx3d_Object) group.getChild(0);
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
        return CubeKind.REVENGE;
    }
}
