/*
 * @(#)RubiksCuboctahedronIdx3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Object;
import idx3d.idx3d_ObjectFactory;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Triangle;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;
/**
 * Geometrical representation of a Rubik's Cuboctahedron in
 * three dimensions using the Idx3D engine.
 *
 * @author  Werner Randelshofer
 */
public class RubiksCuboctahedronIdx3D extends AbstractRubiksCubeIdx3D {
    private final static int[] stickerToPartMap = {
        6, 17, 0, 18, 22,  9, 7, 19, 1, // front
        0,  8, 2,  9, 20, 12, 1, 10, 3, // right
        1, 10, 3, 19, 24, 13, 7, 16, 5, // bottom
        2, 11, 4, 12, 25, 15, 3, 13, 5, // back
        4, 14, 6, 15, 23, 18, 5, 16, 7, // left
        6, 14, 4, 17, 21, 11, 0,  8, 2, // top

        17, 0, 8, 9, // Top Front Right
        8, 2, 11, 12, // Top Back Right
        11, 4, 14, 15, // Top Back Left
        14, 6, 17, 18, // Top Front Left

        9, 19, 1, 10, // Down Front Right
        12, 10, 3, 13, // Down Back Right
        15, 13, 5, 16, // Down Back Left
        18, 16, 7, 19, // Down Front Left
    };
    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one ninth of the imag width.
     */
    private final static float ss = imageWidth / 9f;
    /**
     * Reserve is one pixel of the image.
     */
    private final static float rr = 1f / 512f;

    @Override
    protected float getUnitScaleFactor() {
        return super.getUnitScaleFactor() * 1.2f;
    }

    private final static float[] CORNER_VERTICES = {
        // Vertices of the main cubicle
        // ----------------------------
        //0: Front Face: bottom-left, top-right, bottom-right
        2,-8,9,  8,-2,9,   8,-8,9,

            //3: Right Face: top-center, top-back, center-back, bottom-center, bottom-front, center-front
        9,8,-1,  9,8,-8,  9,-5,-8,  9,-8,-5,  9,-8,8,  9,-1,8,

            //9: Bottom Face: front-center, front-right, center-right, back-center, back-left, center-left
        1,-9,8,  8,-9,8,  8,-9,-5,  5,-9,-8,  -8,-9,-8,  -8,-9,-1,

            //15: Back Face: up-right, up-center, center-left, down-left, down-center, center-right
        8,8,-9,  1,8,-9,  -8,-1,-9, -8,-8,-9,  5,-8,-9,  8,-5,-9,

            //21: Left Face: top-back, bottom-front, bottom-back
        -9,-2,-8,  -9,-8,-2, -9,-8,-8,

            //24: Top Face: back-left, back-right, front-right
        2,9,-8,  8,9,-8,   8,9,-2,

            //27: Oblique face: center-top-back, right-top-center, right-center-front,
        // center-bottom-front, left-bottom-center, left-center-back
        0,8,-8, 8,8,0, 8,0,8, 0,-8,8, -8,-8,0, -8,0,-8,

            // Vertices of the additional cubicle at the bottom right
        //33
        9,-4,-14, 14,-4,-14, 9,-4,-9,   14,-4,-9,

            //37
        4,-9,-14, 4,-9,-9,   4,-14,-14, 4,-14,-9,

            //41
        9,-14,-4, 14,-14,-4, 9,-9,-4, 14,-9,-4, 14,-14,-14
    };
    private final static int[][] CORNER_FACES = {
        // Faces with stickers and with outlines
        //--------------------------------------
        {0, 1, 2},     //Front face    is relevant, for method
        {24, 25, 26}, //Top face      The order of these faces
        {21, 22, 23}, //Left face     updateStickersFillColor()
        {27, 28, 29, 30, 31, 32}, //Hexagon

            // Faces with outlines
        // (all faces which have outlines must be
        // at the beginning, this is relevant
        // for method updatePartsOutlineColor()
        //--------------------------------------

            // Faces of the additional cubicle at the bottom right
        {33+10, 33+11, 33+9, 33+8},
        {33+11, 33+3, 33+1, 33+12, 33+9},
        {33+1, 33+0, 33+4, 33+6, 33+12},
        {33+5, 33+7, 33+6, 33+4},
        {33+5, 33+10, 33+8, 33+7},
        {33+2, 33+3, 33+11, 33+10},
        {33+0, 33+2, 33+5, 33+4},
        {33+2, 33+10, 33+5},
        {33+0, 33+1, 33+3, 33+2},
        {33+12, 33+6, 33+7, 33+8, 33+9},

            // Faces of the main cubicle
        {3, 4, 5, 6, 7, 8},     //Right Face
        {9, 10, 11, 12, 13, 14}, //Bottom Face
        {15, 16, 17, 18, 19, 20},//Back Face

            // Faces without outlines
        //--------------------------------------
        // Triangles at the corners of the main cubicle
        {7, 10, 2}, //Bottom Front Right
        {13, 18, 23},//Bottom Back Left
        {25, 15, 4}, //Top Back Right

            {27, 16, 24}, // Hexagon Back Top
        {28, 26, 3}, // Hexagon Top Right
        {29, 8, 1}, //Hexagon Rigth Front
        {30, 0, 9}, //Hexagon Front Bottom
        {31, 14, 22}, // Hexagon Bottom Left
        {32, 21, 17}, // Hexagon Left Back


            // Edges of the main cubicle
        {26, 25, 4, 3},   //Top Right
        {25, 24, 16, 15}, //Top Back

            {10, 9, 0, 2},    //Bottom Front
        {11, 10, 7, 6},   //Bottom Right
        {13, 12, 19, 18}, //Bottom Back
        {14, 13, 23, 22},  //Bottom Left

            {2, 1, 8, 7},     //Front Right
        {15, 20, 5, 4},   //Back Right
        {18, 17, 21, 23}, //Back Left

            {27, 24, 26, 28}, // Hexagon Top
        {28, 3, 8, 29}, // Hexagon Top Right
        {29, 1, 0, 30}, // Hexagon Bottom Right
        {30, 9, 14, 31}, // Hexagon Bottom
        {31, 22, 21, 32}, // Hexagon Bottom Left
        {32, 17, 16, 27}, // Hexagon Top Left
    };
    /*
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
     */
    protected void initCorners() {
        int i, j, part;
        float[] verts = CORNER_VERTICES;
        int[][] faces = CORNER_FACES;

        for (part = 0; part < 8; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < verts.length / 3; i++) {
                object3D.addVertex(
                verts[i*3], verts[i*3+1], verts[i*3+2]
                );
            }
            for (i=0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                    object3D.vertex(faces[i][0]),
                    object3D.vertex(faces[i][j-1]),
                    object3D.vertex(faces[i][j])
                    );

                    object3D.addTriangle(triangle);
                }
            }
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(2).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(3).setTriangleMaterial(sticker);
            object3D.triangle(4).setTriangleMaterial(sticker);
            object3D.triangle(5).setTriangleMaterial(sticker);
            object3D.triangle(6).setTriangleMaterial(sticker);
            object3D.triangle(7).setTriangleMaterial(sticker);
            parts[cornerOffset+part] = object3D;
        /*
         * Corners
         *              +---+----+---+
         *              |6.0|    |4.0|
         *              +---+    +---+
         *              |     5      |
         *              +---+    +---+
         *              |0.0|    |2.0|
         * +---+----+---+---+----+---+---+----+---+---+----+---+
         * |6.1|    |0.2|0.1|    |2.2|2.1|    |4.2|4.1|    |6.2|
         * +---+    +---+---+    +---+---+    +---+---+    +---+
         * |     4      |     0      |     1      |     3      |
         * +---+    +---+---+    +---+---+    +---+---+    +---+
         * |7.2|    |1.1|1.2|    |3.1|3.2|    |5.1|5.2|    |7.1|
         * +---+----+---+---+----+---+---+----+---+---+----+---+
         *              |1.0|    |3.0|
         *              +---+    +---+
         *              |     2      |
         *              +---+    +---+
         *              |7.0|    |5.0|
         *              +---+----+---+
         * /
            switch (part) {
                case 0: // up front left
                    object3D.triangle(0).setUV(ss * 3 + rr,ss * 3 - rr, ss * 4 - rr,ss * 3 - rr, ss * 4 - rr, ss * 2 + rr);
                    object3D.triangle(1).setUV(ss * 3 + rr,ss * 3 - rr, ss * 4 - rr,ss * 2 + rr, ss * 3 + rr, ss * 2 + rr);
                    object3D.triangle(2).setUV(ss * 3 + rr,ss * 4 - rr, ss * 4 - rr,ss * 4 - rr, ss * 4 - rr, ss * 3 + rr);
                    object3D.triangle(3).setUV(ss * 3 + rr,ss * 4 - rr, ss * 4 - rr,ss * 3 + rr, ss * 3 + rr, ss * 3 + rr);
                    object3D.triangle(4).setUV(ss * 2 + rr,ss * 4 - rr, ss * 3 - rr,ss * 4 - rr, ss * 3 - rr, ss * 3 + rr);
                    object3D.triangle(5).setUV(ss * 2 + rr,ss * 4 - rr, ss * 3 - rr,ss * 3 + rr, ss * 2 + rr, ss * 3 + rr);
                    break;
                case 1: // down left front
                    object3D.triangle(0).setUV(ss * 3 + rr,ss * 6 + rr, ss * 3 + rr,ss * 7 - rr, ss * 4 - rr, ss * 7 - rr);
                    object3D.triangle(1).setUV(ss * 3 + rr,ss * 6 + rr, ss * 4 - rr,ss * 7 - rr, ss * 4 - rr, ss * 6 + rr);
                    object3D.triangle(2).setUV(ss * 3 - rr,ss * 5 + rr, ss * 2 + rr,ss * 5 + rr, ss * 2 + rr, ss * 6 - rr);
                    object3D.triangle(3).setUV(ss * 3 - rr,ss * 5 + rr, ss * 2 + rr,ss * 6 - rr, ss * 3 - rr, ss * 6 - rr);
                    object3D.triangle(4).setUV(ss * 4 - rr,ss * 5 + rr, ss * 3 + rr,ss * 5 + rr, ss * 3 + rr, ss * 6 - rr);
                    object3D.triangle(5).setUV(ss * 4 - rr,ss * 5 + rr, ss * 3 + rr,ss * 6 - rr, ss * 4 - rr, ss * 6 - rr);
                    break;
                case 2: // up right front
                    object3D.triangle(0).setUV(ss * 6 - rr,ss * 3 - rr, ss * 6 - rr,ss * 2 + rr, ss * 5 + rr, ss * 2 + rr);
                    object3D.triangle(1).setUV(ss * 6 - rr,ss * 3 - rr, ss * 5 + rr,ss * 2 + rr, ss * 5 + rr, ss * 3 - rr);
                    object3D.triangle(2).setUV(ss * 6 + rr,ss * 4 - rr, ss * 7 - rr,ss * 4 - rr, ss * 7 - rr, ss * 3 + rr);
                    object3D.triangle(3).setUV(ss * 6 + rr,ss * 4 - rr, ss * 7 - rr,ss * 3 + rr, ss * 6 + rr, ss * 3 + rr);
                    object3D.triangle(4).setUV(ss * 5 + rr,ss * 4 - rr, ss * 6 - rr,ss * 4 - rr, ss * 6 - rr, ss * 3 + rr);
                    object3D.triangle(5).setUV(ss * 5 + rr,ss * 4 - rr, ss * 6 - rr,ss * 3 + rr, ss * 5 + rr, ss * 3 + rr);
                    break;
                case 3: // down front right
                    object3D.triangle(0).setUV(ss * 6 - rr,ss * 6 + rr, ss * 5 + rr,ss * 6 + rr, ss * 5 + rr, ss * 7 - rr);
                    object3D.triangle(1).setUV(ss * 6 - rr,ss * 6 + rr, ss * 5 + rr,ss * 7 - rr, ss * 6 - rr, ss * 7 - rr);
                    object3D.triangle(2).setUV(ss * 6 - rr,ss * 5 + rr, ss * 5 + rr,ss * 5 + rr, ss * 5 + rr, ss * 6 - rr);
                    object3D.triangle(3).setUV(ss * 6 - rr,ss * 5 + rr, ss * 5 + rr,ss * 6 - rr, ss * 6 - rr, ss * 6 - rr);
                    object3D.triangle(4).setUV(ss * 7 - rr,ss * 5 + rr, ss * 6 + rr,ss * 5 + rr, ss * 6 + rr, ss * 6 - rr);
                    object3D.triangle(5).setUV(ss * 7 - rr,ss * 5 + rr, ss * 6 + rr,ss * 6 - rr, ss * 7 - rr, ss * 6 - rr);
                    break;
                case 4: // up back right
                    object3D.triangle(0).setUV(ss * 6 - rr,ss * 0 + rr, ss * 5 + rr,ss * 0 + rr, ss * 5 + rr, ss * 1 - rr);
                    object3D.triangle(1).setUV(ss * 6 - rr,ss * 0 + rr, ss * 5 + rr,ss * 1 - rr, ss * 6 - rr, ss * 1 - rr);
                    object3D.triangle(2).setUV(ss * 6 + rr,ss * 7 - rr, ss * 7 - rr,ss * 7 - rr, ss * 7 - rr, ss * 6 + rr);
                    object3D.triangle(3).setUV(ss * 6 + rr,ss * 7 - rr, ss * 7 - rr,ss * 6 + rr, ss * 6 + rr, ss * 6 + rr);
                    object3D.triangle(4).setUV(ss * 8 + rr,ss * 4 - rr, ss * 9 - rr,ss * 4 - rr, ss * 9 - rr, ss * 3 + rr);
                    object3D.triangle(5).setUV(ss * 8 + rr,ss * 4 - rr, ss * 9 - rr,ss * 3 + rr, ss * 8 + rr, ss * 3 + rr);
                    break;
                case 5: // down right back
                    object3D.triangle(0).setUV(ss * 6 - rr,ss * 9 - rr, ss * 6 - rr,ss * 8 + rr, ss * 5 + rr, ss * 8 + rr);
                    object3D.triangle(1).setUV(ss * 6 - rr,ss * 9 - rr, ss * 5 + rr,ss * 8 + rr, ss * 5 + rr, ss * 9 - rr);
                    object3D.triangle(2).setUV(ss * 9 - rr,ss * 5 + rr, ss * 8 + rr,ss * 5 + rr, ss * 8 + rr, ss * 6 - rr);
                    object3D.triangle(3).setUV(ss * 9 - rr,ss * 5 + rr, ss * 8 + rr,ss * 6 - rr, ss * 9 - rr, ss * 6 - rr);
                    object3D.triangle(4).setUV(ss * 7 - rr,ss * 8 + rr, ss * 6 + rr,ss * 8 + rr, ss * 6 + rr, ss * 9 - rr);
                    object3D.triangle(5).setUV(ss * 7 - rr,ss * 8 + rr, ss * 6 + rr,ss * 9 - rr, ss * 7 - rr, ss * 9 - rr);
                    break;
                case 6: // up left back
                    object3D.triangle(0).setUV(ss * 3 + rr,ss * 0 + rr, ss * 3 + rr,ss * 1 - rr, ss * 4 - rr, ss * 1 - rr);
                    object3D.triangle(1).setUV(ss * 3 + rr,ss * 0 + rr, ss * 4 - rr,ss * 1 - rr, ss * 4 - rr, ss * 0 + rr);
                    object3D.triangle(2).setUV(ss * 0 + rr,ss * 4 - rr, ss * 1 - rr,ss * 4 - rr, ss * 1 - rr, ss * 3 + rr);
                    object3D.triangle(3).setUV(ss * 0 + rr,ss * 4 - rr, ss * 1 - rr,ss * 3 + rr, ss * 0 + rr, ss * 3 + rr);
                    object3D.triangle(4).setUV(ss * 8 + rr,ss * 7 - rr, ss * 9 - rr,ss * 7 - rr, ss * 9 - rr, ss * 6 + rr);
                    object3D.triangle(5).setUV(ss * 8 + rr,ss * 7 - rr, ss * 9 - rr,ss * 6 + rr, ss * 8 + rr, ss * 6 + rr);
                    break;
                case 7: // down back left
                    object3D.triangle(0).setUV(ss * 3 + rr,ss * 9 - rr, ss * 4 - rr,ss * 9 - rr, ss * 4 - rr, ss * 8 + rr);
                    object3D.triangle(1).setUV(ss * 3 + rr,ss * 9 - rr, ss * 4 - rr,ss * 8 + rr, ss * 3 + rr, ss * 8 + rr);
                    object3D.triangle(2).setUV(ss * 9 - rr,ss * 8 + rr, ss * 8 + rr,ss * 8 + rr, ss * 8 + rr, ss * 9 - rr);
                    object3D.triangle(3).setUV(ss * 9 - rr,ss * 8 + rr, ss * 8 + rr,ss * 9 - rr, ss * 9 - rr, ss * 9 - rr);
                    object3D.triangle(4).setUV(ss * 1 - rr,ss * 5 + rr, ss * 0 + rr,ss * 5 + rr, ss * 0 + rr, ss * 6 - rr);
                    object3D.triangle(5).setUV(ss * 1 - rr,ss * 5 + rr, ss * 0 + rr,ss * 6 - rr, ss * 1 - rr, ss * 6 - rr);
                    break;
            }*/
        }
    }
    private final static float[] EDGE_VERTICES = {
        // Vertices of the main cubicle
        //-----------------------------
        //0: Front Face: center-left, top-center, center-right, bottom-right, bottom-left
        -8,0,9,  0,8,9,  8,0,9,   8,-8,9,   -8,-8,9,

            //5: Right Face: center-front, top-center, top-back, center-back, bottom-center, bottom-front
        9,0,8,  9,8,0,  9,8,-8,  9,-4,-8, 9,-8,-4, 9,-8,8,

            //11: Bottom Face: front-left, front-right, back-right, back-left
        -8,-9,8,  8,-9,8, 8,-9,-3, -8,-9,-3,

            //15: Back Face: up-right, up-left, down-left, down-right
        8,8,-9,  -8,8,-9,  -8,-3,-9, 8,-3,-9,

            //19: Left Face: top-back, top-center, center-front, bottom-front, bottom-center, center-back
        -9,8,-8,  -9,8,0, -9,0,8,  -9,-8,8, -9,-8,-4, -9,-4,-8,

            //25: Top Face: back-left, back-right, center-right, front-center, center-left
        -8,9,-8,  8,9,-8,   8,9,0,  0,9,8,  -8,9,0,

            //30: Top Right Triangle: top-front, right-back, right-front;
        1,8,8,  8,8,1,  8,1,8,

            //33: Top Left Triangle: back-left, right-front, left-front;
        -8,8,1,  -1,8,8,  -8,1,8,

            // Vertices of the additional cubicle at the back bottom.
        //-------------------------------------------------------
        //36
        4,-3,-9,  4,-1,-9,  4,-1,-14,  4,-14,-14,  4,-14,-1,  4,-9,-1, 4,-9,-3,

            //43
        -4,-3,-9,  -4,-1,-9, -4,-1,-14, -4,-14,-14, -4,-14,-1, -4,-9,-1, -4,-9,-3
    };
    private final static int[][] EDGE_FACES = {
        // Faces with stickers and with outlines
        //--------------------------------------
        {0, 1, 2, 3, 4},     //Front  The order of these faces is relevant
        {27, 28, 29, 25, 26}, //Top    for method updateStickersFillColor
        {33, 34, 35}, // Triangle top left
        {30, 31, 32}, // Triangle top right

            // Faces with outlines
        // (all faces which have outlines must be
        // at the beginning, this is relevant
        // for method updatePartsOutlineColor()
        //--------------------------------------
        // Faces of the additional cubicle at the back and bottom
        {36+0,36+1,36+2},
        {36+0,36+2,36+3,36+4,36+6},
        {36+4,36+5,36+6},

            {36+9,36+8,36+7},
        {36+13,36+11,36+10,36+9,36+7},
        {36+13,36+12,36+11},

            {36+1,36+8,36+9,36+2},
        {36+2,36+9,36+10,36+3},
        {36+3,36+10,36+11,36+4},
        {36+5,36+4,36+11,36+12},

            // Faces without outlines
        //--------------------------------------
        // Faces of the main cubicle
        {5, 6, 7, 8, 9, 10}, //Right
        {11, 12, 13, 14},     //Bottom
        {15, 16, 17, 18},         //Back
        {19, 20, 21, 22, 23, 24},   //Left


            // Triangular faces at the corners of the main cubicle
        {29, 33, 20}, //Top Front Left
        {27, 6, 31}, //Top Right Front
        {25,19,16}, //Top Left Back
        {26,15,7}, //Top Back Right
        {11,22,4}, //Bottom Left Front
        {12,3,10}, //Bottom Front Right

            {5,2,32}, //Right, Front, Triangle right
        {35,0,21}, //Triangle left, Front, Left

            // Edges of the main cubicle
        {27,26,7,6}, //Top Right
        {26,25,16,15},   //Top Back
        {25,29,20,19},  //Top Left
        {12,11,4,3},   //Bottom Front
        {13,12,10,9}, //Bottom Right
        {11,14,23,22},    //Bottom Left
        {3,2,5,10},   //Front Right
        {0,4,22,21},     //Front Left
        {15,18,8,7},   //Back Right
        {17,16,19,24},   //Back Left

            {8,18,17,24,23,14,13,9}, // Back Down

            {1, 30, 32, 2}, // Front, Triangle right
        {35, 34, 1, 0}, // Triangle left, Front
        {29, 28, 34, 33}, // Top, Triangle left
        {28, 27, 31, 30}, // Top, Triangle Right

            {32, 31, 6, 5}, // Triangle right, Right
        {33, 35, 21, 20}, // Triangle left, Left

            {34, 28, 1}, // Triangle left, Top Front Center
        {1, 28, 30}, // Top Front Center, Triangle Right
    };

    protected void initEdges() {
        int i, j, part;

        float[] verts = EDGE_VERTICES;
        int[][] faces = EDGE_FACES;

        for (part = 0; part < 12; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < verts.length / 3; i++) {
                object3D.addVertex(
                verts[i*3], verts[i*3+1], verts[i*3+2]
                );
            }
            for (i=0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                    object3D.vertex(faces[i][0]),
                    object3D.vertex(faces[i][j-1]),
                    object3D.vertex(faces[i][j])
                    );
                    object3D.addTriangle(triangle);
                }
            }
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            object3D.triangle(1).setTriangleMaterial(sticker);
            object3D.triangle(2).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(3).setTriangleMaterial(sticker);
            object3D.triangle(4).setTriangleMaterial(sticker);
            object3D.triangle(5).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(6).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(7).setTriangleMaterial(sticker);
            parts[edgeOffset+part] = object3D;

        }
    }
    private final static float[] SIDE_VERTS = {
        //0:luff      ldff       ruff       rdff
        -8, 8, 9,  -8,-8, 9,   8, 8, 9,   8,-8, 9,

            //4:rubb,    rdbb,       lubb,       ldbb
        8,8,-1,   8,-8,-1,   -8,8,-1,  -8,-8,-1,

            //8:lluf      lldf     - rruf    - rrdf
        -9, 8, 8,  -9,-8, 8,   9, 8, 8,   9,-8, 8,

            //12:rrub,  - rrdb,      llub,      lldb
        9,8,0,   9,-8,0,   -9,8,0,  -9,-8,0,

            //16:luuf     lddf       ruuf       rddf
        -8, 9, 8,  -8,-9, 8,   8, 9, 8,   8,-9, 8,

            //20:ruub,    rddb,       luub,       lddb
        8,9,0,        8,-9,0,     -8,9,0,     -8,-9,0,
            /*
            //24
            2,4.5f,-1,  4.5f,2,-1,  4.5f,-2,-1,  2,-4.5f,-1,  -2,-4.5f,-1, -4.5f,-2,-1,  -4.5f,2,-1,  -2,4.5f,-1,

            //32
            2,4.5f,-9,  4.5f,2,-9,  4.5f,-2,-9,  2,-4.5f,-9,  -2,-4.5f,-9, -4.5f,-2,-9,  -4.5f,2,-9,  -2,4.5f,-9,
             */
    };
    private final static int[][] SIDE_FACES = {
        // Faces with stickers and with outlines
        //--------------------------------------
        {0, 2, 3, 1},     //Front

            // Faces with outlines
        // (all faces which have outlines must be
        // at the beginning, this is relevant
        // for method updatePartsOutlineColor()
        //--------------------------------------
        {16, 22, 20, 18}, //Top
        {14, 8, 9, 15}, //Left
        {12, 13, 11, 10}, //Right
        {17, 19, 21, 23}, //Bottom
        {4, 6, 7, 5},     //Back
            /*
            // Back face of the axis
            {39, 38, 37, 36, 35, 34, 33, 32},

            // Faces of the axis
            {24, 32, 33, 25},
            {25, 33, 34, 26},
            {26, 34, 35, 27},
            {27, 35, 36, 28},
            {28, 36, 37, 29},
            {29, 37, 38, 30},
            {30, 38, 39, 31},
            {31, 39, 32, 24},
             */
        // Faces without outlines
        //--------------------------------------
        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
        {19, 3,11}, //Bottom Front Right  rddf rdff rrdf
        {23, 7,15}, //Bottom Back Left lddb ldbb lldb
        {21,13, 5}, //Bottom Right Back rddb rrdb rdbb

            {16, 0, 8}, //Top Front Left luuf luff lluf
        {18,10, 2}, //Top Right Front ruuf rruf ruff
        {22,14, 6}, //Top Left Back luub llub lubb
        {20, 4,12}, //Top Back Right ruub rubb rrub

            {16, 18, 2, 0},   //Top Front
        {18, 20, 12, 10}, //Top Right
        {20, 22, 6, 4},   //Top Back
        {22, 16, 8, 14},  //Top Left

            {19, 17, 1, 3},   //Bottom Front
        {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
        {23, 21, 5, 7},   //Bottom Back lddb rddb rdbb ldbb
        {17,23,15, 9},  //Bottom Left lddf lddb lldb lldf

            {3, 2, 10, 11},   //Front Right rdff ruff rruf rrdf
        {0, 1, 9, 8},     //Front Left
        {4, 5, 13, 12},   //Back Right
        {7, 6, 14, 15},  //Back Left

    };

    protected void initSides() {
        int i, j, part;
        idx3d_Object cylinder;

        float[] verts = SIDE_VERTS;
        int[][] faces = SIDE_FACES;

        for (part = 0; part < 6; part++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < verts.length / 3; i++) {
                object3D.addVertex(
                verts[i*3], verts[i*3+1], verts[i*3+2]
                );
            }
            for (i=0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                    object3D.vertex(faces[i][0]),
                    object3D.vertex(faces[i][j-1]),
                    object3D.vertex(faces[i][j])
                    );
                    object3D.addTriangle(triangle);
                    //if (i == 0) triangle.setMaterial(STICKER_MATERIALS[part]);
                }
            }

            cylinder = idx3d_ObjectFactory.CYLINDER(8f, 4.5f, 12, true, false);
            cylinder.rotate((float) (Math.PI / 2), 0f, 0f);
            cylinder.shift(0f, 0f, -5f);
            cylinder.matrixMeltdown();

            object3D.incorporateGeometry(cylinder);
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker1 = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker1);
            object3D.triangle(1).setTriangleMaterial(sticker1);

            /**
             * UV coordinates for stickers on side parts.
             * First dimension = parts,
             * Second dimension = sticker coordinates
             * Third dimension = x and y coordinate values
             *
             *               +-------------+
             *               |      .1     |
             *               |    +---+    |
             *               |  .0| 5 |.2  |
             *               |    +---+    |
             *               |      .3     |
             * +-------------+-------------+-------------+.............+
             * |      .1     |      .2     |      .3     |             '
             * |    +---+    |    +---+    |    +---+    |             '
             * |  .0| 4 |.2  |  .1| 0 |.3  |  .2| 1 |.0  |      3      '
             * |    +---+    |    +---+    |    +---+    |             '
             * |      .3     |     .0      |      .1     |             '
             * +-------------+-------------+-------------+.............+
             *               |      .3     |     .0      |      |
             *               |    +---+    |    +---+    |      |
             *               |  .2| 2 |.0  |  .3| 3 |.1  | <----+
             *               |    +---+    |    +---+    |
             *               |      .1     |      .2     |
             *               +-------------+-------------+
             */
            switch (part) {
                case 0: // front
                    object3D.triangle(0).setUV(ss * 5 - rr,ss * 4 + rr, ss * 4 + rr,ss * 4 + rr, ss * 4 + rr,ss * 5 - rr);
                    object3D.triangle(1).setUV(ss * 5 - rr,ss * 4 + rr, ss * 4 + rr,ss * 5 - rr, ss * 5 - rr,ss * 5 - rr);
                    break;
                case 1: // right
                    object3D.triangle(0).setUV(ss * 7 + rr,ss * 4 + rr, ss * 7 + rr,ss * 5 - rr, ss * 8 - rr,ss * 5 - rr);
                    object3D.triangle(1).setUV(ss * 7 + rr,ss * 4 + rr, ss * 8 - rr,ss * 5 - rr, ss * 8 - rr,ss * 4 + rr);
                    break;
                case 2: // down
                    object3D.triangle(0).setUV(ss * 4 + rr,ss * 7 + rr, ss * 4 + rr,ss * 8 - rr, ss * 5 - rr,ss * 8 - rr);
                    object3D.triangle(1).setUV(ss * 4 + rr,ss * 7 + rr, ss * 5 - rr,ss * 8 - rr, ss * 5 - rr,ss * 7 + rr);
                    break;
                case 3: // back
                    object3D.triangle(0).setUV(ss * 7 + rr,ss * 8 - rr, ss * 8 - rr,ss * 8 - rr, ss * 8 - rr,ss * 7 + rr);
                    object3D.triangle(1).setUV(ss * 7 + rr,ss * 8 - rr, ss * 8 - rr,ss * 7 + rr, ss * 7 + rr,ss * 7 + rr);
                    break;
                case 4: // left
                    object3D.triangle(0).setUV(ss * 2 - rr,ss * 5 - rr, ss * 2 - rr,ss * 4 + rr, ss * 1 + rr,ss * 4 + rr);
                    object3D.triangle(1).setUV(ss * 2 - rr,ss * 5 - rr, ss * 1 + rr,ss * 4 + rr, ss * 1 + rr,ss * 5 - rr);
                    break;
                case 5: // up
                    object3D.triangle(0).setUV(ss * 5 - rr,ss * 2 - rr, ss * 5 - rr,ss * 1 + rr, ss * 4 + rr,ss * 1 + rr);
                    object3D.triangle(1).setUV(ss * 5 - rr,ss * 2 - rr, ss * 4 + rr,ss * 1 + rr, ss * 4 + rr,ss * 2 - rr);
                    break;
            }

            parts[sideOffset+part] = object3D;
        }
    }


    /**
     * Updates the fill color of the stickers.
     * The sticker Index is interpreted according to this
     * scheme:
     * <pre>
     *                     --+--+--
     *                    /45|46|47\
     *                   +---+--+---+
     *                   | 48|49| 50|
     *                   +---+--+---+
     *                    \51|52|53/
     * +----------++----------++----------++----------+
     *  \62/63\64/  \66/67\68/  \54/55\56/  \58/59\60/
     *    +----+      +----+      +----+      +----+
     *     \65/        \69/        \57/        \61/
     *      \/          \/          \/          \/
     *        --+---+--   --+---+--   --+---+--   --+---+--
     *       /36| 37|38\ /0 | 1 | 2\ /9 | 10|11\ /27| 28|29\
     *      +---+---+---+---+---+---+---+---+---+---+---+---+
     *      | 39| 40| 41| 3 | 4 | 5 | 12| 13| 14| 30| 31| 32|
     *      +---+---+---+---+---+---+---+---+---+---+---+---+
     *       \42| 43|44/ \6 | 7 | 8/ \15| 16|17/ \33| 34|35/
     *        --+---+--   --+---+--   --+---+--   --+---+--
     *      /\          /\          /\          /\
     *     /78\        /82\        /70\        /74\
     *    +----+      +----+      +----+      +----+
     *  /79/80\81\  /83/84\85\  /71/72\73\  /75/76\77\
     * +----------++----------++----------++----------+
     *                    /18|19|20\
     *                   +---+--+---+
     *                   | 21|22| 23|
     *                   +---+--+---+
     *                    \24|25|26/
     *                     --+--+--
     * </pre>
     */
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToTriangleMap = {
        1, 0, 2, 3, 0, 3, 2, 0, 1, // front
        1, 3, 2, 0, 0, 0, 2, 3, 1, // right
        0, 0, 0, 3, 0, 3, 0, 0, 0, // down
        1, 0, 2, 3, 0, 3, 2, 0, 1, // back
        1, 3, 2, 0, 0, 0, 2, 3, 1, // left
        0, 0, 0, 3, 0, 3, 0, 0, 0,  // up

            6, 3, 6, 6, // Top Front Right
        7, 3, 7, 7, // Top Back Right
        6, 3, 6, 6, // Top Back Left
        7, 3, 7, 7, // Top Front Left

            7, 7, 3, 7, // Down Front Right
        6, 6, 3, 6, // Down Back Right
        7, 7, 3, 7, // Down Back Left
        6, 6, 3, 6, // Down Front Left
    };
    /*
    private final static int[] stickerToTriangleMap = {
        1, 0, 2, 3, 0, 3, 2, 0, 1, // front
        1, 3, 2, 0, 0, 0, 2, 3, 1, // right
        0, 3, 0, 0, 0, 0, 0, 3, 0, // down
        1, 0, 2, 3, 0, 3, 2, 0, 1, // back
        1, 3, 2, 0, 0, 0, 2, 3, 1, // left
        0, 3, 0, 0, 0, 0, 0, 3, 0,  // up

        6, 3, 6, 6, // Top Front Right
        7, 3, 7, 7, // Top Back Right
        6, 3, 6, 6, // Top Back Left
        7, 3, 7, 7, // Top Front Left

        7, 7, 3, 7, // Down Front Right
        6, 6, 3, 6, // Down Back Right
        7, 7, 3, 7, // Down Back Left
        6, 6, 3, 6, // Down Front Left
    };*/
    private final static int[] stickerToOrientationMap = {
        1, 0, 2, 1, 0, 1, 2, 0, 1, // front
        1, 1, 2, 0, 0, 0, 2, 1, 1, // right
        0, 1, 0, 0, 0, 0, 0, 3, 0, // down
        1, 0, 2, 1, 0, 1, 2, 0, 1, // back
        1, 1, 2, 0, 0, 0, 2, 1, 1, // left
        0, 1, 0, 0, 0, 0, 0, 1, 0,  // up

            6, 3, 6, 6, // Top Front Right
        7, 3, 7, 7, // Top Back Right
        6, 3, 6, 6, // Top Back Left
        7, 3, 7, 7, // Top Front Left

            7, 7, 3, 7, // Down Front Right
        6, 6, 3, 6, // Down Back Right
        7, 7, 3, 7, // Down Back Left
        6, 6, 3, 6, // Down Front Left
    };

    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToTriangleMap[stickerIndex];
    }
    protected int getStickerIndexForPart(int part, int orientation) {
        int sticker;
        for (sticker = stickerToPartMap.length - 1; sticker >= 0; sticker--) {
            if (stickerToPartMap[sticker] == part
            && stickerToOrientationMap[sticker] == orientation) break;
        }
        return sticker;
    }
    protected int getTriangleCountForStickerIndex(int stickerIndex) {
        int partIndex = getPartIndexForStickerIndex(stickerIndex);
        if (partIndex < 8) {
            // corners
            return 1;
        } else if (partIndex < 20) {
            // edges
            return 3;
        } else if (partIndex < 26) {
            // sides
            return 2;
        } else {
            // center
            return -1;
        }
    }


    public int getStickerCount() {
        return 86;
    }

    @Nonnull
    public CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(27, getStickerCount(), new int[] {9,9,9,9,9,9,4,4,4,4,4,4,4,4});
        Color partsFillColor = new Color(16, 16, 16);
        Color partsOutlineColor = Color.black;
        Color[] stickers = new Color[getStickerCount()];

        Arrays.fill(stickers,  0,  9, new Color(140,0,15)); // Front: red
        Arrays.fill(stickers,  9, 18, new Color(255,210,0)); // Right: yellow
        Arrays.fill(stickers, 18, 27, new Color(0,115,47)); // Down: green
        Arrays.fill(stickers, 27, 36, new Color(255,70,0)); // Back: orange
        Arrays.fill(stickers, 36, 45, new Color(248,248,248)); // Left: white
        Arrays.fill(stickers, 45, 54, new Color(0,51,115)); // Up: blue

        Arrays.fill(stickers, 54, 58, new Color(241,185,207)); // Up-Front-Right: light rose
        Arrays.fill(stickers, 58, 62, new Color(164,109,193)); // Up-Back-Right: light violet
        Arrays.fill(stickers, 62, 66, new Color(255,46,156)); // Up-Back-Left: deep pink
        Arrays.fill(stickers, 66, 70, new Color(174,173,160)); // Up-Front-Left: gray
        Arrays.fill(stickers, 70, 74, new Color(176,24,119)); // Down-Front-Right: red violet
        Arrays.fill(stickers, 74, 78, new Color(31,203,69)); // Down-Back-Right: light green
        Arrays.fill(stickers, 78, 82, new Color(102,102,0)); // Down-Back-Left: gold
        Arrays.fill(stickers, 82, 86, new Color(0,220,220)); // Down-Front-Left: cyan

        a.setStickerFillColor(stickers);

        for (int i=0; i < 6; i++) {
            for (int j=0; j < 9; j++) {
                int index = i*9+j;
                a.setStickerOutlineColor(index, partsOutlineColor);
            }
        }
        for (int i=0; i < 26; i++) {
            a.setPartFillColor(i, partsFillColor);
            a.setPartOutlineColor(i, partsOutlineColor);
        }
        a.setPartFillColor(26, new Color(240, 240, 240));
        a.setPartOutlineColor(26, new Color(240, 240, 240));

        return a;
    }

    protected void initActions(@Nonnull idx3d_Scene scene) {
        int i, j;
        PartAction action;

        // Corners
        for (i = 0; i < 8; i++) {
            int index = cornerOffset + i;
            for (j = 0; j < 3; j++) {
                action = new PartAction(
                        index, j, getStickerIndexForPart(index, j)
                );

                scene.addMouseListener(parts[index].triangle(j), action);
                switch (j) {
                    case 0:
                         scene.addSwipeListener(parts[index].triangle(j), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (0)));
                        break;
                    case 1:
                        scene.addSwipeListener(parts[index].triangle(j), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI+Math.PI/4)));
                       break;
                    case 2:
                        scene.addSwipeListener(parts[index].triangle(j), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI)));
                        break;
                }
            }
        }

        // Edges
        for (i=0; i < 12; i++) {
            int index = edgeOffset + i;
            for (j=0; j < 2; j++) {
                action = new PartAction(
                        index, j, getStickerIndexForPart(index, j)
                );
                scene.addMouseListener(parts[index].triangle(j*3), action);
                scene.addMouseListener(parts[index].triangle(j*3+1), action);
                scene.addMouseListener(parts[index].triangle(j*3+2), action);
                switch (j) {
                    case 0://yellow
                        scene.addSwipeListener(parts[index].triangle(j * 3), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (0)));
                        scene.addSwipeListener(parts[index].triangle(j * 3 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI+Math.PI/2 + Math.PI / 4f)));
                        scene.addSwipeListener(parts[index].triangle(j * 3 + 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI/8f)));
                        break;
                    case 1:
                        scene.addSwipeListener(parts[index].triangle(j * 3), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (0)));
                     scene.addSwipeListener(parts[index].triangle(j * 3 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI+Math.PI / 2f+Math.PI/4f)));
                      scene.addSwipeListener(parts[index].triangle(j * 3 + 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI+Math.PI / 2f+Math.PI/8f)));
                        break;
                }
            }
        }

        // Sides
        for (i=0; i < 6; i++) {
            int index = sideOffset + i;
            action = new PartAction(
                    index, 0, getStickerIndexForPart(index, 0)
            );

            scene.addMouseListener(parts[sideOffset+i].triangle(0), action);
            scene.addMouseListener(parts[sideOffset+i].triangle(1), action);
            scene.addSwipeListener(parts[index].triangle(0), new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI / 2f + Math.PI / 4f)));
            scene.addSwipeListener(parts[index].triangle(1), new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) Math.PI / 2f));
        }

        for (i=0; i < 27; i++) {
            action = new PartAction(
                    i, -1, -1
            );

            scene.addMouseListener(parts[i], action);

        }
    }

    public void setStickerBeveling(float newValue) {
    }

    @Nonnull
    public CubeKind getKind() {
        return CubeKind.CUBOCTAHEDRON;
    }
}
