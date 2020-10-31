/*
 * @(#)RubiksCubeIdx3D.java
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
import idx3d.idx3d_ObjectFactory;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Triangle;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;

/**
 * A 3D model of a Rubik's Cube for the Idx3D rendering engine.
 *
 * @author Werner Randelshofer
 */
public class RubiksCubeIdx3D extends AbstractRubiksCubeIdx3D {

    /**
     * Image width is 504 pixels out of 512 pixels.
     */
    private final static float imageWidth = 504f / 512f;
    /**
     * Sticker size is one ninth of the imag width.
     */
    private final static float ss = imageWidth / 9f;
    /**
     * Bevelling is cut of from each sticker.
     */
    private float bevel;

    @Override
    protected void init() {
        bevel = 3f / 512f;
        super.init();
    }

    /**
     * Specifies how many pixels are cut off from the stickers image
     * for each sticker.
     */
    @Override
    public void setStickerBeveling(float newValue) {
        new Throwable().printStackTrace();
        bevel = newValue;
        initEdgeUVMap();
        initCornerUVMap();
        initSideUVMap();
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
            -8, 9, -8, 8, 9, -8, 8, 9, 8, -8, 9, 8,

            // Vertices of the additional cubicle at the bottom right
            //27
            9, -4, -14, 14, -4, -14, 9, -4, -9, 14, -4, -9,
            //31
            4, -9, -14, 4, -9, -9, 4, -14, -14, 4, -14, -9,
            //35
            9, -14, -4, 14, -14, -4, 9, -9, -4, 14, -9, -4, 14, -14, -14,

            // Copy 1 of vertices of the additional cubicle at the bottom right
            //40
            9, -4, -14, 14, -4, -14, 9, -4, -9, 14, -4, -9,
            //44
            4, -9, -14, 4, -9, -9, 4, -14, -14, 4, -14, -9,
            //48
            9, -14, -4, 14, -14, -4, 9, -9, -4, 14, -9, -4, 14, -14, -14,

            // Copy 2 of vertices of the additional cubicle at the bottom right
            //53
            9, -4, -14, 14, -4, -14, 9, -4, -9, 14, -4, -9,
            //57
            4, -9, -14, 4, -9, -9, 4, -14, -14, 4, -14, -9,
            //61
            9, -14, -4, 14, -14, -4, 9, -9, -4, 14, -9, -4, 14, -14, -14,

            // Copy 3 of vertices of the additional cubicle at the bottom right
            //66
            9, -4, -14, 14, -4, -14, 9, -4, -9, 14, -4, -9,
            //70
            4, -9, -14, 4, -9, -9, 4, -14, -14, 4, -14, -9,
            //74
            9, -14, -4, 14, -14, -4, 9, -9, -4, 14, -9, -4, 14, -14, -14

    };
    private final static int[][] CORNER_FACES = {
            // Faces with stickers and with outlines
            //--------------------------------------
            {0, 1, 2, 3}, //Up face    The sequence of these faces
            {23, 24, 25, 26}, //Front face      is relevant, for method
            {22, 19, 20, 21}, //Left face     updateStickersFillColor()

            // Inner edges of the main cubicle. We assign swipe actions to these.
            {3, 2, 10, 9}, //Up Back
            {1, 4, 8, 2}, //Up Right
            {24, 5, 4, 25}, //Front Right
            {15, 14, 24, 23}, //Front Bottom
            {13, 22, 21, 9}, //Left Back
            {16, 15, 19, 22}, //Left Bottom

            // Outer edges of the main cubicle. We assign no actions to these.
            {26, 25, 1, 0}, //Up Front
            {23, 26, 20, 19}, //Up Left
            {11, 10, 8, 7}, //Down Right
            {13, 12, 17, 16}, //Down Back
            {0, 3, 21, 20}, //Front Left
            {14, 18, 6, 5}, //Back Right

            // Faces of the main cubicle
            {4, 5, 6, 7, 8}, //Right Face
            {9, 10, 11, 12, 13}, //Down Face
            {14, 15, 16, 17, 18},//Back Face

            // Triangles at the cornerParts of the main cubicle
            {9, 21, 3}, //Bottom Left Front
            {10, 2, 8}, //Bottom Front Right
            {13, 16, 22},//Bottom Back Left

            {26, 0, 20}, //Top Front Left
            {25, 4, 1}, //Top Right Front ruuf rruf ruff
            {23, 19, 15},//Top Left Back luub llub lubb
            {24, 14, 5}, //Top Back Right ruub rubb rrub

            // Faces of the additional cubicle at the bottom right
            {27 + 10, 27 + 11, 27 + 9, 27 + 8}, // small up ⃞
            //{27 + 11, 27 + 3, 27 + 1, 27 + 12, 27 + 9},// left wall |_\
            //{27 + 1, 27 + 0, 27 + 4, 27 + 6, 27 + 12},// bottom wall |_\
            {27 + 5, 27 + 7, 27 + 6, 27 + 4},// small front  ⃞
            {27 + 5, 27 + 10, 27 + 8, 27 + 7},// rect front up
            {27 + 2, 27 + 3, 27 + 11, 27 + 10},//rect right up
            {27 + 0, 27 + 2, 27 + 5, 27 + 4},//rect right front
            {27 + 2, 27 + 10, 27 + 5},// diagonal triangle
            {27 + 0, 27 + 1, 27 + 3, 27 + 2},//small right
            //{27 + 12, 27 + 6, 27 + 7, 27 + 8, 27 + 9},// down wall |_\

            // Copy of faces of the additional cubicle at the bottom right
            {40 + 11, 40 + 3, 40 + 1, 40 + 12, 40 + 9},// left wall |_\
            {53 + 1, 53 + 0, 53 + 4, 53 + 6, 53 + 12},// bottom wall |_\
            {66 + 12, 66 + 6, 66 + 7, 66 + 8, 66 + 9},// down wall |_\

    };

    @Override
    protected void initCorners() {
        int i, j, part;
        float[] verts = CORNER_VERTS;
        int[][] faces = CORNER_FACES;

        for (part = 0; part < 8; part++) {
            idx3d_Object object3D = new idx3d_Object();
            idx3d_Object stickerR = new idx3d_Object();
            idx3d_Object stickerU = new idx3d_Object();
            idx3d_Object stickerF = new idx3d_Object();
            object3D.name = "corner" + part;
            stickerR.name = "cornerR" + part;
            stickerU.name = "cornerU" + part;
            stickerF.name = "cornerF" + part;

            for (i = 0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerR.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerU.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerF.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
            }
            for (i = 0; i < faces.length; i++) {
                idx3d_Object obj;
                switch (i) {
                case 0:
                    obj = stickerR;
                    break;
                case 1:
                    obj = stickerU;
                    break;
                case 2:
                    obj = stickerF;
                    break;
                default:
                    obj = object3D;
                    break;
                }
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            obj.vertex(faces[i][0]),
                            obj.vertex(faces[i][j - 1]),
                            obj.vertex(faces[i][j]));

                    obj.addTriangle(triangle);
                }
            }
            object3D.material = stickerR.material = stickerU.material = stickerF.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            stickerR.triangle(0).setTriangleMaterial(sticker);
            stickerR.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            stickerU.triangle(0).setTriangleMaterial(sticker);
            stickerU.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            stickerF.triangle(0).setTriangleMaterial(sticker);
            stickerF.triangle(1).setTriangleMaterial(sticker);
            idx3d_Group group3D = new idx3d_Group();
            group3D.addChild(stickerR);
            group3D.addChild(stickerU);
            group3D.addChild(stickerF);
            group3D.addChild(object3D);
            parts[cornerOffset + part] = group3D;
        }
        initCornerUVMap();
    }

    /**
     * Initializes the UV Map for the corner parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9
     * 0             +---+---+---+
     *               |4.0|   |2.0|
     * 1             +---+   +---+
     *               |     u     |
     * 2             +---+   +---+
     *               |6.0|   |0.0|
     * 3 +---+---+---+---+---+---+---+---+---+...........+
     *   |4.1|   |6.2|6.1|   |0.2|0.1|   |2.2|           '
     * 4 +---+   +---+---+   +---+---+   +---+           '
     *   |     l     |     f     |     r     |     b     '
     * 5 +---+   +---+---+   +---+---+   +---+           '
     *   |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|           '
     * 6 +---+---+---+---+---+---+---+---+---+...........+
     *               |7.0|   |1.0|2.1|   |4.2|     |
     * 7             +---+   +---+---+   +---+     |
     *               |     d     |     b     |  &lt;--+
     * 8             +---+   +---+---+   +---+
     *               |5.0|   |3.0|3.2|   |5.1|
     * 9             +---+---+---+---+---+---+
     * </pre>
     */
    protected void initCornerUVMap() {
        for (int part = 0; part < 8; part++) {
            idx3d_Group group = parts[cornerOffset + part];
            idx3d_Object stickerR = (idx3d_Object) group.getChild(0);
            idx3d_Object stickerU = (idx3d_Object) group.getChild(1);
            idx3d_Object stickerF = (idx3d_Object) group.getChild(2);
            switch (part) {
            case 0: // up right front
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 3 - bevel, ss * 6 - bevel, ss * 2 + bevel, ss * 5 + bevel, ss * 2 + bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 3 - bevel, ss * 5 + bevel, ss * 2 + bevel, ss * 5 + bevel, ss * 3 - bevel);
                stickerU.triangle(0).setUV(ss * 6 + bevel, ss * 4 - bevel, ss * 7 - bevel, ss * 4 - bevel, ss * 7 - bevel, ss * 3 + bevel);
                stickerU.triangle(1).setUV(ss * 6 + bevel, ss * 4 - bevel, ss * 7 - bevel, ss * 3 + bevel, ss * 6 + bevel, ss * 3 + bevel);
                stickerF.triangle(0).setUV(ss * 5 + bevel, ss * 4 - bevel, ss * 6 - bevel, ss * 4 - bevel, ss * 6 - bevel, ss * 3 + bevel);
                stickerF.triangle(1).setUV(ss * 5 + bevel, ss * 4 - bevel, ss * 6 - bevel, ss * 3 + bevel, ss * 5 + bevel, ss * 3 + bevel);
                break;
            case 1: // down front right
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 6 + bevel, ss * 5 + bevel, ss * 6 + bevel, ss * 5 + bevel, ss * 7 - bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 6 + bevel, ss * 5 + bevel, ss * 7 - bevel, ss * 6 - bevel, ss * 7 - bevel);
                stickerU.triangle(0).setUV(ss * 6 - bevel, ss * 5 + bevel, ss * 5 + bevel, ss * 5 + bevel, ss * 5 + bevel, ss * 6 - bevel);
                stickerU.triangle(1).setUV(ss * 6 - bevel, ss * 5 + bevel, ss * 5 + bevel, ss * 6 - bevel, ss * 6 - bevel, ss * 6 - bevel);
                stickerF.triangle(0).setUV(ss * 7 - bevel, ss * 5 + bevel, ss * 6 + bevel, ss * 5 + bevel, ss * 6 + bevel, ss * 6 - bevel);
                stickerF.triangle(1).setUV(ss * 7 - bevel, ss * 5 + bevel, ss * 6 + bevel, ss * 6 - bevel, ss * 7 - bevel, ss * 6 - bevel);
                break;
            case 2: // up back right
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 0 + bevel, ss * 5 + bevel, ss * 0 + bevel, ss * 5 + bevel, ss * 1 - bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 0 + bevel, ss * 5 + bevel, ss * 1 - bevel, ss * 6 - bevel, ss * 1 - bevel);
                stickerU.triangle(0).setUV(ss * 6 + bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 6 + bevel);
                stickerU.triangle(1).setUV(ss * 6 + bevel, ss * 7 - bevel, ss * 7 - bevel, ss * 6 + bevel, ss * 6 + bevel, ss * 6 + bevel);
                stickerF.triangle(0).setUV(ss * 8 + bevel, ss * 4 - bevel, ss * 9 - bevel, ss * 4 - bevel, ss * 9 - bevel, ss * 3 + bevel);
                stickerF.triangle(1).setUV(ss * 8 + bevel, ss * 4 - bevel, ss * 9 - bevel, ss * 3 + bevel, ss * 8 + bevel, ss * 3 + bevel);
                break;
            case 3: // down right back
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 9 - bevel, ss * 6 - bevel, ss * 8 + bevel, ss * 5 + bevel, ss * 8 + bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 9 - bevel, ss * 5 + bevel, ss * 8 + bevel, ss * 5 + bevel, ss * 9 - bevel);
                stickerU.triangle(0).setUV(ss * 9 - bevel, ss * 5 + bevel, ss * 8 + bevel, ss * 5 + bevel, ss * 8 + bevel, ss * 6 - bevel);
                stickerU.triangle(1).setUV(ss * 9 - bevel, ss * 5 + bevel, ss * 8 + bevel, ss * 6 - bevel, ss * 9 - bevel, ss * 6 - bevel);
                stickerF.triangle(0).setUV(ss * 7 - bevel, ss * 8 + bevel, ss * 6 + bevel, ss * 8 + bevel, ss * 6 + bevel, ss * 9 - bevel);
                stickerF.triangle(1).setUV(ss * 7 - bevel, ss * 8 + bevel, ss * 6 + bevel, ss * 9 - bevel, ss * 7 - bevel, ss * 9 - bevel);
                break;
            case 4: // up left back
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 0 + bevel, ss * 3 + bevel, ss * 1 - bevel, ss * 4 - bevel, ss * 1 - bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 0 + bevel, ss * 4 - bevel, ss * 1 - bevel, ss * 4 - bevel, ss * 0 + bevel);
                stickerU.triangle(0).setUV(ss * 0 + bevel, ss * 4 - bevel, ss * 1 - bevel, ss * 4 - bevel, ss * 1 - bevel, ss * 3 + bevel);
                stickerU.triangle(1).setUV(ss * 0 + bevel, ss * 4 - bevel, ss * 1 - bevel, ss * 3 + bevel, ss * 0 + bevel, ss * 3 + bevel);
                stickerF.triangle(0).setUV(ss * 8 + bevel, ss * 7 - bevel, ss * 9 - bevel, ss * 7 - bevel, ss * 9 - bevel, ss * 6 + bevel);
                stickerF.triangle(1).setUV(ss * 8 + bevel, ss * 7 - bevel, ss * 9 - bevel, ss * 6 + bevel, ss * 8 + bevel, ss * 6 + bevel);
                break;
            case 5: // down back left
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 9 - bevel, ss * 4 - bevel, ss * 9 - bevel, ss * 4 - bevel, ss * 8 + bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 9 - bevel, ss * 4 - bevel, ss * 8 + bevel, ss * 3 + bevel, ss * 8 + bevel);
                stickerU.triangle(0).setUV(ss * 9 - bevel, ss * 8 + bevel, ss * 8 + bevel, ss * 8 + bevel, ss * 8 + bevel, ss * 9 - bevel);
                stickerU.triangle(1).setUV(ss * 9 - bevel, ss * 8 + bevel, ss * 8 + bevel, ss * 9 - bevel, ss * 9 - bevel, ss * 9 - bevel);
                stickerF.triangle(0).setUV(ss * 1 - bevel, ss * 5 + bevel, ss * 0 + bevel, ss * 5 + bevel, ss * 0 + bevel, ss * 6 - bevel);
                stickerF.triangle(1).setUV(ss * 1 - bevel, ss * 5 + bevel, ss * 0 + bevel, ss * 6 - bevel, ss * 1 - bevel, ss * 6 - bevel);
                break;
            case 6: // up front left
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 3 - bevel, ss * 4 - bevel, ss * 3 - bevel, ss * 4 - bevel, ss * 2 + bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 3 - bevel, ss * 4 - bevel, ss * 2 + bevel, ss * 3 + bevel, ss * 2 + bevel);
                stickerU.triangle(0).setUV(ss * 3 + bevel, ss * 4 - bevel, ss * 4 - bevel, ss * 4 - bevel, ss * 4 - bevel, ss * 3 + bevel);
                stickerU.triangle(1).setUV(ss * 3 + bevel, ss * 4 - bevel, ss * 4 - bevel, ss * 3 + bevel, ss * 3 + bevel, ss * 3 + bevel);
                stickerF.triangle(0).setUV(ss * 2 + bevel, ss * 4 - bevel, ss * 3 - bevel, ss * 4 - bevel, ss * 3 - bevel, ss * 3 + bevel);
                stickerF.triangle(1).setUV(ss * 2 + bevel, ss * 4 - bevel, ss * 3 - bevel, ss * 3 + bevel, ss * 2 + bevel, ss * 3 + bevel);
                break;
            case 7: // down left front
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 6 + bevel, ss * 3 + bevel, ss * 7 - bevel, ss * 4 - bevel, ss * 7 - bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 6 + bevel, ss * 4 - bevel, ss * 7 - bevel, ss * 4 - bevel, ss * 6 + bevel);
                stickerU.triangle(0).setUV(ss * 3 - bevel, ss * 5 + bevel, ss * 2 + bevel, ss * 5 + bevel, ss * 2 + bevel, ss * 6 - bevel);
                stickerU.triangle(1).setUV(ss * 3 - bevel, ss * 5 + bevel, ss * 2 + bevel, ss * 6 - bevel, ss * 3 - bevel, ss * 6 - bevel);
                stickerF.triangle(0).setUV(ss * 4 - bevel, ss * 5 + bevel, ss * 3 + bevel, ss * 5 + bevel, ss * 3 + bevel, ss * 6 - bevel);
                stickerF.triangle(1).setUV(ss * 4 - bevel, ss * 5 + bevel, ss * 3 + bevel, ss * 6 - bevel, ss * 4 - bevel, ss * 6 - bevel);
                break;
            }
        }
    }

    private final static float[] EDGE_VERTS = {
            // Vertices of the main cubicle
            //-----------------------------
            //0: Front Face: top-left, top-right, bottom-right, bottom-left
            -8, 8, 9, 8, 8, 9, 8, -8, 9, -8, -8, 9,
            //4: Right Face: top-front, top-back, centerPart-back, bottom-centerPart, bottom-front
            9, 8, 8, 9, 8, -8, 9, -4, -8, 9, -8, -4, 9, -8, 8,
            //9: Bottom Face: front-left, front-right, back-right, back-left
            -8, -9, 8, 8, -9, 8, 8, -9, -3, -8, -9, -3,
            //13: Back Face: up-right, up-left, down-left, down-right
            8, 8, -9, -8, 8, -9, -8, -3, -9, 8, -3, -9,
            //17: Left Face: top-back, top-front, bottom-front, bottom-centerPart, centerPart-back
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
    private final static int[][] EDGE_FACES = {
            // Faces with stickers and with outlines
            //--------------------------------------
            {0, 1, 2, 3}, //Front  The order of these faces is relevant
            {22, 23, 24, 25}, //Top    for method updateStickersFillColor

            // Inner edges of the main cubicle. We assign swipe actions to these.
            {1, 4, 8, 2}, //Front Right
            {18, 0, 3, 19}, //Front Left 6+7
            {3, 2, 10, 9}, //Bottom Front 8+9
            {23, 5, 4, 24}, //Top Right 10+11
            {14, 13, 23, 22}, //Top Back 12+13
            {17, 22, 25, 18}, //Top Left
            // Outer edges of the main cubicle. We assign no actions to these.
            {25, 24, 1, 0}, //Top Front
            {8, 7, 11, 10}, //Bottom Right
            {20, 19, 9, 12}, //Bottom Left
            {5, 13, 16, 6}, //Back Right
            {14, 17, 21, 15}, //Back Left

            // Faces of the main cubicle
            {4, 5, 6, 7, 8}, //Right
            {9, 10, 11, 12}, //Bottom
            {13, 14, 15, 16}, //Back
            {17, 18, 19, 20, 21}, //Left
            {16, 15, 21, 20, 12, 11, 7, 6}, // Back Down

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
            // Triangular faces at the cornerParts of the main cubicle
            {25, 0, 18}, //Top Front Left
            {24, 4, 1}, //Top Right Front
            {22, 17, 14}, //Top Left Back
            {23, 13, 5}, //Top Back Right
            {9, 19, 3}, //Bottom Left Front
            {10, 2, 8}, //Bottom Front Right
    };

    @Override
    protected void initEdges() {
        int i, j, part;

        float[] verts = EDGE_VERTS;
        int[][] faces = EDGE_FACES;

        for (part = 0; part < 12; part++) {
            idx3d_Object object3D = new idx3d_Object();
            object3D.name = "edge" + part;
            idx3d_Object stickerR = new idx3d_Object();
            stickerR.name = "edgeR" + part;
            idx3d_Object stickerU = new idx3d_Object();
            stickerU.name = "edgeU" + part;
            for (i = 0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerR.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerU.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
            }
            for (i = 0; i < faces.length; i++) {
                idx3d_Object obj;
                switch (i) {
                case 0:
                    obj = stickerR;
                    break;
                case 1:
                    obj = stickerU;
                    break;
                default:
                    obj = object3D;
                    break;
                }
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            obj.vertex(faces[i][0]),
                            obj.vertex(faces[i][j - 1]),
                            obj.vertex(faces[i][j]));
                    obj.addTriangle(triangle);
                }
            }
            object3D.material = stickerR.material = stickerU.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            stickerR.triangle(0).setTriangleMaterial(sticker);
            stickerR.triangle(1).setTriangleMaterial(sticker);

            sticker = new idx3d_InternalMaterial();
            stickerU.triangle(0).setTriangleMaterial(sticker);
            stickerU.triangle(1).setTriangleMaterial(sticker);
            idx3d_Group group3D = new idx3d_Group();
            group3D.addChild(stickerR);
            group3D.addChild(stickerU);
            group3D.addChild(object3D);
            parts[edgeOffset + part] = group3D;
        }
        initEdgeUVMap();
    }

    /**
     * Initializes the UV Map for the edge parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9
     * 0             +---+---+---+
     *               |   |3.1|   |
     * 1             +--- --- ---+
     *               |6.0| u |0.0|
     * 2             +--- --- ---+
     *               |   |9.1|   |
     * 3 +---+---+---+---+---+---+---+---+---+...........+
     *   |   |6.1|   |   |9.0|   |   |0.1|   |           '
     * 4 +--- --- ---+--- --- ---+--- --- ---+           '
     *   |7.0| l 10.0|10.1 f |1.1|1.0| r |4.0|     b     '
     * 5 +--- --- ---+--- --- ---+--- --- ---+           '
     *   |   |8.1|   |   |11.0   |   |2.1|   |           '
     * 6 +---+---+---+---+---+---+---+---+---+...........+
     *               |   |11.1   |   |3.0|   |     |
     * 7             +--- --- ---+--- --- ---+     |
     *               |8.0| d |2.0|4.1| b |7.1|  &lt;--+
     * 8             +--- --- ---+--- --- ---+
     *               |   |5.1|   |   |5.0|   |
     * 9             +---+---+---+---+---+---+
     * </pre>
     */
    protected void initEdgeUVMap() {
        for (int part = 0; part < 12; part++) {
            idx3d_Group group = parts[edgeOffset + part];
            idx3d_Object stickerR = (idx3d_Object) group.getChild(0);
            idx3d_Object stickerU = (idx3d_Object) group.getChild(1);
            switch (part) {
            case 0: // up right
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 2 - bevel, ss * 6 - bevel, ss * 1 + bevel, ss * 5 + bevel, ss * 1 + bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 2 - bevel, ss * 5 + bevel, ss * 1 + bevel, ss * 5 + bevel, ss * 2 - bevel);
                stickerU.triangle(0).setUV(ss * 7 + bevel, ss * 4 - bevel, ss * 8 - bevel, ss * 4 - bevel, ss * 8 - bevel, ss * 3 + bevel);
                stickerU.triangle(1).setUV(ss * 7 + bevel, ss * 4 - bevel, ss * 8 - bevel, ss * 3 + bevel, ss * 7 + bevel, ss * 3 + bevel);
                break;
            case 1: // right front
                stickerR.triangle(0).setUV(ss * 6 + bevel, ss * 4 + bevel, ss * 6 + bevel, ss * 5 - bevel, ss * 7 - bevel, ss * 5 - bevel);
                stickerR.triangle(1).setUV(ss * 6 + bevel, ss * 4 + bevel, ss * 7 - bevel, ss * 5 - bevel, ss * 7 - bevel, ss * 4 + bevel);
                stickerU.triangle(0).setUV(ss * 5 + bevel, ss * 4 + bevel, ss * 5 + bevel, ss * 5 - bevel, ss * 6 - bevel, ss * 5 - bevel);
                stickerU.triangle(1).setUV(ss * 5 + bevel, ss * 4 + bevel, ss * 6 - bevel, ss * 5 - bevel, ss * 6 - bevel, ss * 4 + bevel);
                break;
            case 2: // down right
                stickerR.triangle(0).setUV(ss * 6 - bevel, ss * 8 - bevel, ss * 6 - bevel, ss * 7 + bevel, ss * 5 + bevel, ss * 7 + bevel);
                stickerR.triangle(1).setUV(ss * 6 - bevel, ss * 8 - bevel, ss * 5 + bevel, ss * 7 + bevel, ss * 5 + bevel, ss * 8 - bevel);
                stickerU.triangle(0).setUV(ss * 8 - bevel, ss * 5 + bevel, ss * 7 + bevel, ss * 5 + bevel, ss * 7 + bevel, ss * 6 - bevel);
                stickerU.triangle(1).setUV(ss * 8 - bevel, ss * 5 + bevel, ss * 7 + bevel, ss * 6 - bevel, ss * 8 - bevel, ss * 6 - bevel);
                break;
            case 3: // back up
                stickerR.triangle(0).setUV(ss * 8 - bevel, ss * 6 + bevel, ss * 7 + bevel, ss * 6 + bevel, ss * 7 + bevel, ss * 7 - bevel);
                stickerR.triangle(1).setUV(ss * 8 - bevel, ss * 6 + bevel, ss * 7 + bevel, ss * 7 - bevel, ss * 8 - bevel, ss * 7 - bevel);
                stickerU.triangle(0).setUV(ss * 4 + bevel, ss * 1 - bevel, ss * 5 - bevel, ss * 1 - bevel, ss * 5 - bevel, ss * 0 + bevel);
                stickerU.triangle(1).setUV(ss * 4 + bevel, ss * 1 - bevel, ss * 5 - bevel, ss * 0 + bevel, ss * 4 + bevel, ss * 0 + bevel);
                break;
            case 4: // right back
                stickerR.triangle(0).setUV(ss * 9 - bevel, ss * 5 - bevel, ss * 9 - bevel, ss * 4 + bevel, ss * 8 + bevel, ss * 4 + bevel);
                stickerR.triangle(1).setUV(ss * 9 - bevel, ss * 5 - bevel, ss * 8 + bevel, ss * 4 + bevel, ss * 8 + bevel, ss * 5 - bevel);
                stickerU.triangle(0).setUV(ss * 7 - bevel, ss * 8 - bevel, ss * 7 - bevel, ss * 7 + bevel, ss * 6 + bevel, ss * 7 + bevel);
                stickerU.triangle(1).setUV(ss * 7 - bevel, ss * 8 - bevel, ss * 6 + bevel, ss * 7 + bevel, ss * 6 + bevel, ss * 8 - bevel);
                break;
            case 5: // back down
                stickerR.triangle(0).setUV(ss * 7 + bevel, ss * 9 - bevel, ss * 8 - bevel, ss * 9 - bevel, ss * 8 - bevel, ss * 8 + bevel);
                stickerR.triangle(1).setUV(ss * 7 + bevel, ss * 9 - bevel, ss * 8 - bevel, ss * 8 + bevel, ss * 7 + bevel, ss * 8 + bevel);
                stickerU.triangle(0).setUV(ss * 5 - bevel, ss * 8 + bevel, ss * 4 + bevel, ss * 8 + bevel, ss * 4 + bevel, ss * 9 - bevel);
                stickerU.triangle(1).setUV(ss * 5 - bevel, ss * 8 + bevel, ss * 4 + bevel, ss * 9 - bevel, ss * 5 - bevel, ss * 9 - bevel);
                break;
            case 6: // up left
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 1 + bevel, ss * 3 + bevel, ss * 2 - bevel, ss * 4 - bevel, ss * 2 - bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 1 + bevel, ss * 4 - bevel, ss * 2 - bevel, ss * 4 - bevel, ss * 1 + bevel);
                stickerU.triangle(0).setUV(ss * 1 + bevel, ss * 4 - bevel, ss * 2 - bevel, ss * 4 - bevel, ss * 2 - bevel, ss * 3 + bevel);
                stickerU.triangle(1).setUV(ss * 1 + bevel, ss * 4 - bevel, ss * 2 - bevel, ss * 3 + bevel, ss * 1 + bevel, ss * 3 + bevel);
                break;
            case 7: // left back
                stickerR.triangle(0).setUV(ss * 0 + bevel, ss * 4 + bevel, ss * 0 + bevel, ss * 5 - bevel, ss * 1 - bevel, ss * 5 - bevel);
                stickerR.triangle(1).setUV(ss * 0 + bevel, ss * 4 + bevel, ss * 1 - bevel, ss * 5 - bevel, ss * 1 - bevel, ss * 4 + bevel);
                stickerU.triangle(0).setUV(ss * 8 + bevel, ss * 7 + bevel, ss * 8 + bevel, ss * 8 - bevel, ss * 9 - bevel, ss * 8 - bevel);
                stickerU.triangle(1).setUV(ss * 8 + bevel, ss * 7 + bevel, ss * 9 - bevel, ss * 8 - bevel, ss * 9 - bevel, ss * 7 + bevel);
                break;
            case 8: // down left
                stickerR.triangle(0).setUV(ss * 3 + bevel, ss * 7 + bevel, ss * 3 + bevel, ss * 8 - bevel, ss * 4 - bevel, ss * 8 - bevel);
                stickerR.triangle(1).setUV(ss * 3 + bevel, ss * 7 + bevel, ss * 4 - bevel, ss * 8 - bevel, ss * 4 - bevel, ss * 7 + bevel);
                stickerU.triangle(0).setUV(ss * 2 - bevel, ss * 5 + bevel, ss * 1 + bevel, ss * 5 + bevel, ss * 1 + bevel, ss * 6 - bevel);
                stickerU.triangle(1).setUV(ss * 2 - bevel, ss * 5 + bevel, ss * 1 + bevel, ss * 6 - bevel, ss * 2 - bevel, ss * 6 - bevel);
                break;
            case 9: // front up
                stickerR.triangle(0).setUV(ss * 5 - bevel, ss * 3 + bevel, ss * 4 + bevel, ss * 3 + bevel, ss * 4 + bevel, ss * 4 - bevel);
                stickerR.triangle(1).setUV(ss * 5 - bevel, ss * 3 + bevel, ss * 4 + bevel, ss * 4 - bevel, ss * 5 - bevel, ss * 4 - bevel);
                stickerU.triangle(0).setUV(ss * 5 - bevel, ss * 2 + bevel, ss * 4 + bevel, ss * 2 + bevel, ss * 4 + bevel, ss * 3 - bevel);
                stickerU.triangle(1).setUV(ss * 5 - bevel, ss * 2 + bevel, ss * 4 + bevel, ss * 3 - bevel, ss * 5 - bevel, ss * 3 - bevel);
                break;
            case 10: // left front
                stickerR.triangle(0).setUV(ss * 3 - bevel, ss * 5 - bevel, ss * 3 - bevel, ss * 4 + bevel, ss * 2 + bevel, ss * 4 + bevel);
                stickerR.triangle(1).setUV(ss * 3 - bevel, ss * 5 - bevel, ss * 2 + bevel, ss * 4 + bevel, ss * 2 + bevel, ss * 5 - bevel);
                stickerU.triangle(0).setUV(ss * 4 - bevel, ss * 5 - bevel, ss * 4 - bevel, ss * 4 + bevel, ss * 3 + bevel, ss * 4 + bevel);
                stickerU.triangle(1).setUV(ss * 4 - bevel, ss * 5 - bevel, ss * 3 + bevel, ss * 4 + bevel, ss * 3 + bevel, ss * 5 - bevel);
                break;
            case 11: // front down
                stickerR.triangle(0).setUV(ss * 4 + bevel, ss * 6 - bevel, ss * 5 - bevel, ss * 6 - bevel, ss * 5 - bevel, ss * 5 + bevel);
                stickerR.triangle(1).setUV(ss * 4 + bevel, ss * 6 - bevel, ss * 5 - bevel, ss * 5 + bevel, ss * 4 + bevel, ss * 5 + bevel);
                stickerU.triangle(0).setUV(ss * 4 + bevel, ss * 7 - bevel, ss * 5 - bevel, ss * 7 - bevel, ss * 5 - bevel, ss * 6 + bevel);
                stickerU.triangle(1).setUV(ss * 4 + bevel, ss * 7 - bevel, ss * 5 - bevel, ss * 6 + bevel, ss * 4 + bevel, ss * 6 + bevel);
                break;
            }
        }
    }

    private final static float[] SIDE_VERTS = {
            //0:luff      ldff       ruff       rdff
            -8, 8, 9, -8, -8, 9, 8, 8, 9, 8, -8, 9,
            //4:rubb,    rdbb,       lubb,       ldbb
            8, 8, -1, 8, -8, -1, -8, 8, -1, -8, -8, -1,
            //8:lluf      lldf     - rruf    - rrdf
            -9, 8, 8, -9, -8, 8, 9, 8, 8, 9, -8, 8,
            //12:rrub,  - rrdb,      llub,      lldb
            9, 8, 0, 9, -8, 0, -9, 8, 0, -9, -8, 0,
            //16:luuf     lddf       ruuf       rddf
            -8, 9, 8, -8, -9, 8, 8, 9, 8, 8, -9, 8,
            //20:ruub,    rddb,       luub,       lddb
            8, 9, 0, 8, -9, 0, -8, 9, 0, -8, -9, 0, /*
    //24
    2,4.5f,-1,  4.5f,2,-1,  4.5f,-2,-1,  2,-4.5f,-1,  -2,-4.5f,-1, -4.5f,-2,-1,  -4.5f,2,-1,  -2,4.5f,-1,
    //32
    2,4.5f,-9,  4.5f,2,-9,  4.5f,-2,-9,  2,-4.5f,-9,  -2,-4.5f,-9, -4.5f,-2,-9,  -4.5f,2,-9,  -2,4.5f,-9,
     */};
    private final static int[][] SIDE_FACES = {
            // Faces with stickers and with outlines
            //--------------------------------------
            {0, 2, 3, 1}, //Front

            // Inner edges of the main cubicle. We assign swipe actions to these.
            {16, 18, 2, 0}, //Top Front
            {1, 3, 19, 17}, //Bottom Front
            {2, 10, 11, 3}, //Front Right rdff ruff rruf rrdf
            {8, 0, 1, 9}, //Front Left

            // Outer edges of the main cubicle. We assign no actions to these.
            {18, 20, 12, 10}, //Top Right
            {20, 22, 6, 4}, //Top Back
            {22, 16, 8, 14}, //Top Left
            {4, 5, 13, 12}, //Back Right
            {7, 6, 14, 15}, //Back Left
            {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
            {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb
            {17, 23, 15, 9}, //Bottom Left lddf lddb lldb lldf

            // Side faces
            {16, 22, 20, 18}, //Top
            {14, 8, 9, 15}, //Left
            {12, 13, 11, 10}, //Right
            {17, 19, 21, 23}, //Bottom
            {4, 6, 7, 5}, //Back

            // Triangular faces at the cornerParts of the main cubicle
            {17, 9, 1}, //Bottom Left Front lddf lldf ldff
            {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
            {23, 7, 15}, //Bottom Back Left lddb ldbb lldb
            {21, 13, 5}, //Bottom Right Back rddb rrdb rdbb

            {16, 0, 8}, //Top Front Left luuf luff lluf
            {18, 10, 2}, //Top Right Front ruuf rruf ruff
            {22, 14, 6}, //Top Left Back luub llub lubb
            {20, 4, 12}, //Top Back Right ruub rubb rrub
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
             */};

    @Override
    protected void initSides() {
        int i, j, part;
        idx3d_Object cylinder;

        float[] verts = SIDE_VERTS;
        int[][] faces = SIDE_FACES;

        for (part = 0; part < 6; part++) {
            idx3d_Group group3D = new idx3d_Group();
            idx3d_Object object3D = new idx3d_Object();
            object3D.name = "side" + part;

            idx3d_Object stickerR = new idx3d_Object();
            stickerR.name = "sideR" + part;

            object3D.children();

            for (i = 0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
                stickerR.addVertex(
                        verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
            }
            for (i = 0; i < faces.length; i++) {
                idx3d_Object obj = i == 0 ? stickerR : object3D;
                for (j = 2; j < faces[i].length; j++) {
                    idx3d_Triangle triangle = new idx3d_Triangle(
                            obj.vertex(faces[i][0]),
                            obj.vertex(faces[i][j - 1]),
                            obj.vertex(faces[i][j]));
                    obj.addTriangle(triangle);
                }
            }

            cylinder = idx3d_ObjectFactory.CYLINDER(8f, 4.5f, 12, true, false);
            cylinder.rotate((float) (Math.PI / 2), 0f, 0f);
            cylinder.shift(0f, 0f, -5f);
            cylinder.matrixMeltdown();

            object3D.incorporateGeometry(cylinder);
            object3D.material = stickerR.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            stickerR.triangle(0).setTriangleMaterial(sticker);
            stickerR.triangle(1).setTriangleMaterial(sticker);

            group3D.addChild(stickerR);
            group3D.addChild(object3D);

            parts[sideOffset + part] = group3D;
        }
        initSideUVMap();
    }

    /**
     * Initializes the UV coordinates for the side parts.
     * <pre>
     *   0   1   2   3   4   5   6   7   8   9
     * 0             +-----------+
     *               |           |
     * 1             |   +---+ u |
     *               |   | 1 |   |
     * 2             |   +---+   |
     *               |           |
     * 3 +-----------+-----------+-----------+...........+
     *   |           |           |           |           '
     * 4 |   +---+ l |   +---+ f |   +---+ r |           '
     *   |   | 3 |   |   | 2 |   |   | 0 |   |     b     '
     * 5 |   +---+   |   +---+   |   +---+   |           '
     *   |           |           |           |           '
     * 6 +-----------+-----------+-----------+...........+
     *               |           |           |     |
     * 7             |   +---+ d |   +---+ b |     |
     *               |   | 4 |   |   | 5 |   |  &lt;--+
     * 8             |   +---+   |   +---+   |
     *               |           |           |
     * 9             +-----------+-----------+
     * </pre>
     */
    protected void initSideUVMap() {
        /* UV coordinates for stickers on side parts.
         * First dimension = parts,
         * Second dimension = sticker coordinates
         * Third dimension = x and y coordinate values
         */
        for (int part = 0; part < 6; part++) {
            idx3d_Group group3D = parts[sideOffset + part];
            idx3d_Object object3D = (idx3d_Object) group3D.getChild(0);
            switch (part) {
            case 0: // right
                object3D.triangle(0).setUV(ss * 7 + bevel, ss * 4 + bevel, ss * 7 + bevel, ss * 5 - bevel, ss * 8 - bevel, ss * 5 - bevel);
                object3D.triangle(1).setUV(ss * 7 + bevel, ss * 4 + bevel, ss * 8 - bevel, ss * 5 - bevel, ss * 8 - bevel, ss * 4 + bevel);
                break;
            case 1: // up
                object3D.triangle(0).setUV(ss * 5 - bevel, ss * 2 - bevel, ss * 5 - bevel, ss * 1 + bevel, ss * 4 + bevel, ss * 1 + bevel);
                object3D.triangle(1).setUV(ss * 5 - bevel, ss * 2 - bevel, ss * 4 + bevel, ss * 1 + bevel, ss * 4 + bevel, ss * 2 - bevel);
                break;
            case 2: // front
                object3D.triangle(0).setUV(ss * 5 - bevel, ss * 4 + bevel, ss * 4 + bevel, ss * 4 + bevel, ss * 4 + bevel, ss * 5 - bevel);
                object3D.triangle(1).setUV(ss * 5 - bevel, ss * 4 + bevel, ss * 4 + bevel, ss * 5 - bevel, ss * 5 - bevel, ss * 5 - bevel);
                break;
            case 4: // down
                object3D.triangle(0).setUV(ss * 4 + bevel, ss * 8 - bevel, ss * 5 - bevel, ss * 8 - bevel, ss * 5 - bevel, ss * 7 + bevel);
                object3D.triangle(1).setUV(ss * 4 + bevel, ss * 8 - bevel, ss * 5 - bevel, ss * 7 + bevel, ss * 4 + bevel, ss * 7 + bevel);
                break;
            case 3: // left
                object3D.triangle(0).setUV(ss * 1 + bevel, ss * 5 - bevel, ss * 2 - bevel, ss * 5 - bevel, ss * 2 - bevel, ss * 4 + bevel);
                object3D.triangle(1).setUV(ss * 1 + bevel, ss * 5 - bevel, ss * 2 - bevel, ss * 4 + bevel, ss * 1 + bevel, ss * 4 + bevel);
                break;
            case 5: // back
                object3D.triangle(0).setUV(ss * 8 - bevel, ss * 8 - bevel, ss * 8 - bevel, ss * 7 + bevel, ss * 7 + bevel, ss * 7 + bevel);
                object3D.triangle(1).setUV(ss * 8 - bevel, ss * 8 - bevel, ss * 7 + bevel, ss * 7 + bevel, ss * 7 + bevel, ss * 8 - bevel);
                break;
            }
        }
    }

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
        return stickerToFaceMap[stickerIndex] * 2;
    }

    @Override
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

    @Override
    protected void initActions(@Nonnull idx3d_Scene scene) {
        int i, j;
        PartAction action;

        // Corners
        for (i = 0; i < 8; i++) {
            int index = cornerOffset + i;
            for (j = 0; j < 3; j++) {
                action = new PartAction(
                        index, j, getStickerIndexForPart(index, j));

                idx3d_Object stickerR = (idx3d_Object) parts[index].getChild(0);
                idx3d_Object stickerU = (idx3d_Object) parts[index].getChild(1);
                idx3d_Object stickerF = (idx3d_Object) parts[index].getChild(2);
                idx3d_Object obj = (idx3d_Object) parts[index].getChild(3);
                switch (j) {
                case 0: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                    scene.addMouseListener(stickerR.triangle(0), action);
                    scene.addMouseListener(stickerR.triangle(1), action);
                    scene.addSwipeListener(stickerR.triangle(0), a0);
                    scene.addSwipeListener(stickerR.triangle(1), a1);
                    scene.addSwipeListener(obj.triangle(8 - 6), a0);
                    scene.addSwipeListener(obj.triangle(7 - 6), a1);
                    scene.addSwipeListener(obj.triangle(8 - 6), a0);
                    scene.addSwipeListener(obj.triangle(9 - 6), a1);
                    break;
                }
                case 1: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f));
                    scene.addMouseListener(stickerU.triangle(0), action);
                    scene.addMouseListener(stickerU.triangle(1), action);
                    scene.addSwipeListener(stickerU.triangle(0), a0);
                    scene.addSwipeListener(stickerU.triangle(1), a1);
                    scene.addSwipeListener(obj.triangle(10 - 6), a0);
                    scene.addSwipeListener(obj.triangle(11 - 6), a1);
                    scene.addSwipeListener(obj.triangle(12 - 6), a0);
                    scene.addSwipeListener(obj.triangle(13 - 6), a1);
                    break;
                }
                case 2: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                    scene.addMouseListener(stickerF.triangle(0), action);
                    scene.addMouseListener(stickerF.triangle(1), action);
                    scene.addSwipeListener(stickerF.triangle(0), a0);
                    scene.addSwipeListener(stickerF.triangle(1), a1);
                    scene.addSwipeListener(obj.triangle(14 - 6), a0);
                    scene.addSwipeListener(obj.triangle(15 - 6), a1);
                    scene.addSwipeListener(obj.triangle(16 - 6), a0);
                    scene.addSwipeListener(obj.triangle(17 - 6), a1);
                }
                break;
                }
            }
        }

        // Edges
        for (i = 0; i < 12; i++) {
            int index = edgeOffset + i;
            for (j = 0; j < 2; j++) {
                action = new PartAction(
                        i + 8, j, getStickerIndexForPart(index, j));

                idx3d_Object stickerR = (idx3d_Object) parts[index].getChild(0);
                idx3d_Object stickerU = (idx3d_Object) parts[index].getChild(1);
                idx3d_Object obj = (idx3d_Object) parts[index].getChild(2);

                switch (j) {
                case 0: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f));
                    scene.addMouseListener(stickerR.triangle(0), action);
                    scene.addMouseListener(stickerR.triangle(1), action);
                    scene.addSwipeListener(stickerR.triangle(0), a0);
                    scene.addSwipeListener(stickerR.triangle(1), a1);
                    scene.addSwipeListener(obj.triangle(4 - 4), a0);
                    scene.addSwipeListener(obj.triangle(5 - 4), a1);
                    scene.addSwipeListener(obj.triangle(6 - 4), a0);
                    scene.addSwipeListener(obj.triangle(7 - 4), a1);
                    scene.addSwipeListener(obj.triangle(8 - 4), a0);
                    scene.addSwipeListener(obj.triangle(9 - 4), a1);
                    break;
                }
                case 1: {
                    SwipeAction a0 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f + Math.PI / 4f));
                    SwipeAction a1 = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f));
                    scene.addMouseListener(stickerU.triangle(0), action);
                    scene.addMouseListener(stickerU.triangle(1), action);
                    scene.addSwipeListener(stickerU.triangle(0), a0);
                    scene.addSwipeListener(stickerU.triangle(1), a1);
                    scene.addSwipeListener(obj.triangle(10 - 4), a0);
                    scene.addSwipeListener(obj.triangle(11 - 4), a1);
                    scene.addSwipeListener(obj.triangle(12 - 4), a0);
                    scene.addSwipeListener(obj.triangle(13 - 4), a1);
                    scene.addSwipeListener(obj.triangle(14 - 4), a0);
                    scene.addSwipeListener(obj.triangle(15 - 4), a1);
                    break;
                }
                }
            }
        }

        // Sides
        for (i = 0; i < 6; i++) {
            int index = sideOffset + i;
            action = new PartAction(
                    index, 0, getStickerIndexForPart(index, 0));
            idx3d_Object stickerR = (idx3d_Object) parts[index].getChild(0);
            idx3d_Object obj = (idx3d_Object) parts[index].getChild(1);

            scene.addMouseListener(stickerR.triangle(0), action);
            scene.addMouseListener(stickerR.triangle(1), action);
            SwipeAction a0 = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI / 2f + Math.PI / 4f));
            SwipeAction a1 = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) Math.PI / 2f);
            scene.addSwipeListener(stickerR.triangle(0), a0);
            scene.addSwipeListener(stickerR.triangle(1), a1);
            scene.addSwipeListener(obj.triangle(2 - 2), a0);
            scene.addSwipeListener(obj.triangle(3 - 2), a1);
            scene.addSwipeListener(obj.triangle(4 - 2), a0);
            scene.addSwipeListener(obj.triangle(5 - 2), a1);
            scene.addSwipeListener(obj.triangle(6 - 2), a0);
            scene.addSwipeListener(obj.triangle(7 - 2), a1);
            scene.addSwipeListener(obj.triangle(8 - 2), a0);
            scene.addSwipeListener(obj.triangle(9 - 2), a1);
        }

        for (i = 0; i < 27; i++) {
            action = new PartAction(
                    i, -1, -1);
            for (idx3d_Node child : parts[i].children()) {
                scene.addMouseListener((idx3d_Object) child, action);
            }
        }
    }

    @Nonnull
    @Override
    public CubeKind getKind() {
        return CubeKind.RUBIK;
    }
}
