/* @(#)BarrelIdx3D.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import idx3d.*;
import java.awt.*;
import java.util.Arrays;
/**
 * BarrelIdx3D.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * its faces.
 * <br>2.0 2008-01-03 Adapted to changes in AbstractCube.
 * <br>1.0 December 26, 2003 Created.
 */
public class RubiksBarrelIdx3D extends AbstractRubiksCubeIdx3D {
    private final static int[] stickerToPartMap = {
        17, 22, 19, // front
        0, 9, 1,  // front right
        8, 20, 10, // right
        2, 12, 3,  // back right
        11, 25, 13, // back
        4, 15, 5, // back left
        14, 23, 16, // left
        6, 18, 7, // front left
        
        7, 19, 1, 16, 24, 10, 5, 13, 3, // down
        4, 11, 2, 14, 21, 8, 6, 17, 0  // up
    };
    private final static int[] stickerToFaceMap = {
        0, 0, 0, // front
        2, 0, 2,  // front right
        2, 0, 2, // right
        2, 0, 2,  // back right
        0, 0, 0, // back
        2, 0, 2, // back left
        2, 0, 2, // left
        2, 0, 2, // front left
        
        0, 2, 0, 0, 0, 0, 0, 2, 0, // down
        0, 2, 0, 0, 0, 0, 0, 2, 0  // up
    };
    
    /** Creates a new instance. */
    public RubiksBarrelIdx3D() {
    }
    @Override
    protected float getUnitScaleFactor() {
        return super.getUnitScaleFactor() * 1.2f;
    }
    
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[] {
                // Vertices of the main cubicle
                // ----------------------------
                //0: Oblique Front Face: top-center, top-right, bottom-right, bottom-center
                -0,8,0,  8,8,8,   8,-8,8,  0,-8,0,
                
                //4: Right Face: top-front, top-back, center-back, bottom-center, bottom-front
                9,8,8,  9,8,-8, 9,-5,-8,  9,-8,-5,  9,-8,8,
                
                //9: Bottom Face: center, front-right, center-right, back-center, back-left
                .5f,-9,-.5f,  8,-9,7,  8,-9,-5,  5,-9,-8,  -7,-9,-8,
                
                //14: Back Face: up-right, up-left, down-left, down-center, center-right
                8,8,-9,  -8,8,-9,  -8,-8,-9, 5,-8,-9,  8,-5,-9,
                
                //19: Oblique Left Face: top-back, left-front, bottom-front, bottom-back
                -8,8,-8,  -0,8,0, -0,-8,0, -8,-8,-8,
                
                //23: Top Face: back-left, back-right, front-right, center
                -7,9,-8,  8,9,-8,   8,9,7,   .5f,9,-.5f,
                
                // Vertices of the additional cubicle at the bottom right
                //27
                9,-4,-14, 14,-4,-14, 9,-4,-9,   14,-4,-9,
                
                //31
                4,-9,-14, 4,-9,-9,   4,-14,-14, 4,-14,-9,
                
                //35
                9,-14,-4, 14,-14,-4, 9,-9,-4, 14,-9,-4, 14,-14,-14
            };
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][]          {
                // Faces with stickers and with outlines
                //--------------------------------------
                {23, 24, 25}, //Top face        The order of these faces
                //{19, 1, 2, 22},   //Oblique face    is relevant, for method
                //                updateStickersFillColor()
                
                // Sensor faces (no outlines and no fill color)
                // ------------
                {0, 1, 2, 3},     //Front Sensor face
                {19, 20, 21, 22}, //Left Sensor face
                
                // Faces with outlines
                // (all faces which have outlines must be
                // at the beginning, this is relevant
                // for method updatePartsOutlineColor()
                //--------------------------------------
                // Faces of the main cubicle
                {4, 5, 6, 7, 8},     //Right Face
                {9, 10, 11, 12, 13}, //Bottom Face
                {14, 15, 16, 17, 18},//Back Face
                
                
                // Faces of the additional cubicle at the bottom right
                {27+10, 27+11, 27+9, 27+8},
                {27+11, 27+3, 27+1, 27+12, 27+9},
                {27+1, 27+0, 27+4, 27+6, 27+12},
                {27+5, 27+7, 27+6, 27+4},
                {27+5, 27+10, 27+8, 27+7},
                {27+2, 27+3, 27+11, 27+10},
                {27+0, 27+2, 27+5, 27+4},
                {27+2, 27+10, 27+5},
                {27+0, 27+1, 27+3, 27+2},
                {27+12, 27+6, 27+7, 27+8,27+9},
                
                // Faces without outlines
                //--------------------------------------
                
                // Triangles at the corners of the main cubicle
                {10, 2, 8}, //Bottom Front Right
                {13, 16, 22},//Bottom Back Left
                
                {25, 4, 1},  //Top Right Front ruuf rruf ruff
                {23, 19, 15},//Top Left Back luub llub lubb
                {24, 14, 5}, //Top Back Right ruub rubb rrub
                
                // Edges of the main cubicle
                {23, 25, 1, 19},   //Top Front to Top Left
                {25, 24, 5, 4},   //Top Right
                {24, 23, 15, 14}, //Top Back
                
                {10, 13, 22, 2},    //Bottom Front to Bottom Left
                {11, 10, 8, 7},   //Bottom Right
                {13, 12, 17, 16}, //Bottom Back
                
                {2, 1, 4, 8},     //Front Right
                {0, 3, 21, 20},   //Front Left
                {14, 18, 6, 5},   //Back Right
                {16, 15, 19, 22}, //Back Left
                
            };
        }
        int i, j, k;
        for (k = 0; k < 8; k++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < CORNER_VERTS.length / 3; i++) {
                object3D.addVertex(
                        CORNER_VERTS[i*3], CORNER_VERTS[i*3+1], CORNER_VERTS[i*3+2]
                        );
            }
            for (i=0; i < CORNER_FACES.length; i++) {
                for (j = 2; j < CORNER_FACES[i].length; j++) {
                    object3D.addTriangle(
                            new idx3d_Triangle(
                            object3D.vertex(CORNER_FACES[i][0]),
                            object3D.vertex(CORNER_FACES[i][j-1]),
                            object3D.vertex(CORNER_FACES[i][j])
                            )
                            );
                }
            }
            
            float pi = (float) Math.PI;
            float halfpi = (float) (Math.PI / 2d);
            object3D.matrix.rotate(0, 0, halfpi);
            object3D.normalmatrix.rotate(0, 0, halfpi);
            object3D.matrix.rotate(0, -halfpi, 0);
            object3D.normalmatrix.rotate(0, -halfpi, 0);
            object3D.matrixMeltdown();
            
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            //object3D.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(1).setTriangleMaterial(sticker);
            object3D.triangle(2).setTriangleMaterial(sticker);
            //sticker = new idx3d_InternalMaterial();
            object3D.triangle(3).setTriangleMaterial(sticker);
            object3D.triangle(4).setTriangleMaterial(sticker);
            parts[cornerOffset+k] = object3D;
        }
    }
    protected void initEdges() {
        initSquareEdges();
        initObliqueEdges();
    }
    protected void initSquareEdges() {
        int i, j, k;
        
        float[] verts = {
            // Vertices of the main cubicle
            
            //0: Front Face: top-left, top-right, bottom-right, bottom-left
            -8,8,9,  8,8,9,   8,-8,9,   -8,-8,9,
            
            //4: Right Face: top-front, top-back, center-back, bottom-center, bottom-front
            9,8,8,  9,8,-8, 9,-4,-8, 9,-8,-4, 9,-8,8,
            
            //9: Bottom Face: front-left, front-right, back-right, back-left
            -8,-9,8,  8,-9,8, 8,-9,-3, -8,-9,-3,
            
            //13: Back Face: up-right, up-left, down-left, down-right
            8,8,-9,  -8,8,-9,  -8,-3,-9, 8,-3,-9,
            
            //17: Left Face: top-back, top-front, bottom-front, bottom-center, center-back
            -9,8,-8,  -9,8,8, -9,-8,8, -9,-8,-4, -9,-4,-8,
            
            //22: Top Face: back-left, back-right, front-right, front-left
            -8,9,-8,  8,9,-8,   8,9,8,   -8,9,8,
            
            // Vertices of the additional cubicle at the back bottom.
            //26
            4,-3,-9,  4,-1,-9,  4,-1,-14,  4,-14,-14,  4,-14,-1,  4,-9,-1, 4,-9,-3,
            
            //33
            -4,-3,-9,  -4,-1,-9, -4,-1,-14, -4,-14,-14, -4,-14,-1, -4,-9,-1, -4,-9,-3
        };
        int[][] faces = {
            // Faces with stickers and with outlines
            //--------------------------------------
            {0, 1, 2, 3},     //Front  The order of these faces is relevant
            {22, 23, 24, 25}, //Top    for method updateStickersFillColor
            
            
            // Faces with outlines
            // (all faces which have outlines must be
            // at the beginning, this is relevant
            // for method updatePartsOutlineColor()
            //--------------------------------------
            // Faces of the additional cubicle at the back and bottom
            {26+0,26+1,26+2},
            {26+0,26+2,26+3,26+4,26+6},
            {26+4,26+5,26+6},
            
            {26+9,26+8,26+7},
            {26+13,26+11,26+10,26+9,26+7},
            {26+13,26+12,26+11},
            
            {26+1,26+8,26+9,26+2},
            {26+2,26+9,26+10,26+3},
            {26+3,26+10,26+11,26+4},
            {26+5,26+4,26+11,26+12},
            
            // Faces without outlines
            //--------------------------------------
            // Faces of the main cubicle
            {4, 5, 6, 7, 8}, //Right
            {9, 10, 11, 12},     //Bottom
            {13, 14, 15, 16},         //Back
            {17, 18, 19, 20, 21},   //Left
            
            // Triangular faces at the corners of the main cubicle
            {25, 0,18}, //Top Front Left
            {24,4,1}, //Top Right Front
            {22,17,14}, //Top Left Back
            {23,13,5}, //Top Back Right
            {9,19,3}, //Bottom Left Front
            {10,2,8}, //Bottom Front Right
            
            // Edges of the main cubicle
            {25,24,1,0},   //Top Front
            {24,23,5,4}, //Top Right
            {23,22,14,13},   //Top Back
            {22,25,18,17},  //Top Left
            {3,2,10,9},   //Bottom Front
            {8,7,11,10}, //Bottom Right
            {20,19,9,12},    //Bottom Left
            {1,4,8,2},   //Front Right
            {0,3,19,18},     //Front Left
            {5,13,16,6},   //Back Right
            {14,17,21,15},   //Back Left
            {16,15,21,20,12,11,7,6}, // Back Down
            
        };
        
        int[] indices = {0, 2, 3, 5, 6, 8, 9, 11};
        for (k = 0; k < indices.length; k++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i*3], verts[i*3+1], verts[i*3+2]
                        );
            }
            for (i=0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    object3D.addTriangle(
                            new idx3d_Triangle(
                            object3D.vertex(faces[i][0]),
                            object3D.vertex(faces[i][j-1]),
                            object3D.vertex(faces[i][j])
                            )
                            );
                }
            }
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            object3D.triangle(1).setTriangleMaterial(sticker);
            sticker = new idx3d_InternalMaterial();
            object3D.triangle(2).setTriangleMaterial(sticker);
            object3D.triangle(3).setTriangleMaterial(sticker);
            parts[edgeOffset+indices[k]] = object3D;
        }
    }
    protected void initObliqueEdges() {
        int i, j, k;
        
        float[] verts = {
            // Vertices of the main cubicle
            
            //0: Oblique Front Face: top-left, top-right, bottom-right, bottom-left
            -8,0,0,  8,0,0,   8,-8,8,   -8,-8,8,
            
            //4: Right Face: center, top-back, center-back, bottom-center, bottom-front
            9,-.5f,-.5f,  9,7,-8, 9,-4,-8, 9,-8,-4, 9,-8,7,
            
            //9: Bottom Face: front-left, front-right, back-right, back-left
            -8,-9,8,  8,-9,8, 8,-9,-3, -8,-9,-3,
            
            //13: Back Face: up-right, up-left, down-left, down-right
            8,8,-9,  -8,8,-9,  -8,-3,-9, 8,-3,-9,
            
            //17: Left Face: top-back, top-front, bottom-front, bottom-center, center-back
            -9,7,-8,  -9,-.5f,-.5f, -9,-8,7, -9,-8,-4, -9,-4,-8,
            
            //22: Oblique Top Face: back-left, back-right, front-right, front-left
            -8,8,-8,  8,8,-8,   8,0,0,   -8,0,0,
            
            // Vertices of the additional cubicle at the back bottom.
            //26
            4,-3,-9,  4,-1,-9,  4,-1,-14,  4,-14,-14,  4,-14,-1,  4,-9,-1, 4,-9,-3,
            
            //33
            -4,-3,-9,  -4,-1,-9, -4,-1,-14, -4,-14,-14, -4,-14,-1, -4,-9,-1, -4,-9,-3
        };
        int[][] faces = {
            // Faces with stickers and with outlines
            //--------------------------------------
            //{22,23, 2, 3},     //Oblique     The order of these faces is relevant
            //Front+Top   for method updateStickersFillColor
            
            // Sensor faces (no outlines and no fill color)
            // ------------
            {0, 1, 2, 3},     //Front Sensor
            {22, 23, 24, 25}, //Top Sensor
            
            
            // Faces with outlines
            // (all faces which have outlines must be
            // at the beginning, this is relevant
            // for method updatePartsOutlineColor()
            //--------------------------------------
            // Faces of the additional cubicle at the back and bottom
            {26+0,26+1,26+2},
            {26+0,26+2,26+3,26+4,26+6},
            {26+4,26+5,26+6},
            
            {26+9,26+8,26+7},
            {26+13,26+11,26+10,26+9,26+7},
            {26+13,26+12,26+11},
            
            {26+1,26+8,26+9,26+2},
            {26+2,26+9,26+10,26+3},
            {26+3,26+10,26+11,26+4},
            {26+5,26+4,26+11,26+12},
            
            // Faces without outlines
            //--------------------------------------
            // Faces of the main cubicle
            {5, 6, 7, 8}, //Right
            {9, 10, 11, 12},     //Bottom
            {13, 14, 15, 16},         //Back
            {17, 19, 20, 21},   //Left
            
            // Triangular faces at the corners of the main cubicle
            {22,17,14}, //Top Left Back
            {23,13,5}, //Top Back Right
            {9,19,3}, //Bottom Left Front
            {10,2,8}, //Bottom Front Right
            
            // Edges of the main cubicle
            {2,23,5,8}, //Top Right to Front Right
            {23,22,14,13},   //Top Back
            {22,3,19,17},  //Top Left to Front Left
            {3,2,10,9},   //Bottom Front
            {8,7,11,10}, //Bottom Right
            {20,19,9,12},    //Bottom Left
            {5,13,16,6},   //Back Right
            {14,17,21,15},   //Back Left
            {16,15,21,20,12,11,7,6}, // Back Down
            
        };
        
        int[] indices = {1, 4, 7, 10};
        for (k = 0; k < indices.length; k++) {
            idx3d_Object object3D = new idx3d_Object();
            for (i=0; i < verts.length / 3; i++) {
                object3D.addVertex(
                        verts[i*3], verts[i*3+1], verts[i*3+2]
                        );
            }
            for (i=0; i < faces.length; i++) {
                for (j = 2; j < faces[i].length; j++) {
                    object3D.addTriangle(
                            new idx3d_Triangle(
                            object3D.vertex(faces[i][0]),
                            object3D.vertex(faces[i][j-1]),
                            object3D.vertex(faces[i][j])
                            )
                            );
                }
            }
            object3D.material = new idx3d_InternalMaterial();
            idx3d_InternalMaterial sticker = new idx3d_InternalMaterial();
            object3D.triangle(0).setTriangleMaterial(sticker);
            object3D.triangle(1).setTriangleMaterial(sticker);
            //sticker = new idx3d_InternalMaterial();
            object3D.triangle(2).setTriangleMaterial(sticker);
            object3D.triangle(3).setTriangleMaterial(sticker);
            parts[edgeOffset+indices[k]] = object3D;
        }
    }
    /**
     * Initializes the side parts.
     */
    protected void initSides() {
        int i, j, k;
        idx3d_Object cylinder;
        
        float[] verts = {
            //0:luff      ldff       ruff       rdff
            -8, 8, 9,  -8,-8, 9,   8, 8, 9,   8,-8, 9,
            
            //4:rubb,    rdbb,       lubb,       ldbb
            8,8,-1,   8,-8,-1,   -8,8,-1,  -8,-8,-1,
            
            //8:lluf      lldf       rruf      rrdf
            -9, 8, 8,  -9,-8, 8,   9, 8, 8,   9,-8, 8,
            
            //12:rrub,    rrdb,      llub,      lldb
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
        int[][] faces = {
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
        
        for (k = 0; k < 6; k++) {
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
                    //if (i == 0) triangle.setMaterial(STICKER_MATERIALS[k]);
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
            
            parts[sideOffset+k] = object3D;
        }
    }
    
    
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(27, 42, new int[] {3,3,3,3,3,3,3,3,9,9});
        
        Color[] partsFillColor = new Color[27];
        Color[] partsOutlineColor = new Color[27];
        Color[] stickersFillColor = new Color[42];
        
        Arrays.fill(partsFillColor, 0, 26, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, 26, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, 26, 27, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, 26, 27, new Color(240, 240, 240));
        
        Arrays.fill(stickersFillColor, 0, 3, new Color(140,0,15)); //Front: red
        Arrays.fill(stickersFillColor, 3, 6, new Color(155,0,97)); //Front Right: dark purple
        Arrays.fill(stickersFillColor, 6, 9, new Color(70,188,218)); //Right: cyan
        Arrays.fill(stickersFillColor, 9, 12, new Color(225,60,124)); //Back Right: bright purple
        Arrays.fill(stickersFillColor, 12, 15, new Color(0,115,47)); //Back; green
        Arrays.fill(stickersFillColor, 15, 18, new Color(255,70,0)); //Back Left: orange
        Arrays.fill(stickersFillColor, 18, 21, new Color(124,83,0)); //Left: gold
        Arrays.fill(stickersFillColor, 21, 24, new Color(255,210,0)); //Front-Left: yellow
        
        Arrays.fill(stickersFillColor, 24, 33, new Color(248,248,248)); //Down: white
        Arrays.fill(stickersFillColor, 33, 42, new Color(0,41,103)); //Up: blue
        
        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        
        return a;
    }
    
    public int getStickerCount() {
        return 42;
    }
    /**
     * Gets the part which holds the indicated sticker.
     * The sticker index is interpreted according to this
     * scheme:
     * <pre>
     *                    /+---+\
     *                  /33| 34|35\
     *                 +---+---+---+
     *                 | 36| 37| 38|
     *                 +---+---+---+
     *                  \39| 40|41/
     *       +----+---+----+---+----+---+----+---+
     *       | 15 | 18| 21 | 0 |  3 | 6 |  9 | 12|
     *       +----+---+----+---+----+---+----+---+
     *       | 16 | 19| 22 | 1 |  4 | 7 | 10 | 13|
     *       +----+---+----+---+----+---+----+---+
     *       | 17 | 20| 23 | 2 |  5 | 8 | 11 | 14|
     *       +----+---+----+---+----+---+----+---+
     *                  /24| 25|26\
     *                 +---+---+---+
     *                 | 27| 28| 29|
     *                 +---+---+---+
     *                  \30| 31|32/
     *                    \+---+/
     * </pre>
     */
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex];
    }
    protected int getStickerIndexForPart(int part, int orientation) {
        int sticker;
        for (sticker = stickerToPartMap.length - 1; sticker >= 0; sticker--) {
            if (stickerToPartMap[sticker] == part
                    && stickerToFaceMap[sticker] == orientation) break;
        }
        return sticker;
    }
    
    protected void initActions(idx3d_Scene scene) {
        int i, j;
        PartAction action;
        
        // Corners
        for (i=0; i < 8; i++) {
            int index = cornerOffset + i;
                action = new PartAction(
                        index, 0, getStickerIndexForPart(index, 0)
                        );
                
                scene.addMouseListener(parts[index].triangle(0), action);
                        scene.addSwipeListener(parts[index].triangle(0), new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI+Math.PI/4)));
                
                action = new PartAction(
                        index, 1, getStickerIndexForPart(index, 2)
                        );
                scene.addMouseListener(parts[index].triangle(1), action);
                scene.addMouseListener(parts[index].triangle(2), action);
                        scene.addSwipeListener(parts[index].triangle(1), new SwipeAction(index, 2, getStickerIndexForPart(index, 1), (float) (Math.PI+Math.PI/-2+Math.PI/4)));
                        scene.addSwipeListener(parts[index].triangle(2), new SwipeAction(index, 2, getStickerIndexForPart(index, 1), (float) (Math.PI/2-Math.PI/16)));

                action = new PartAction(
                        index, 2, getStickerIndexForPart(index, 2)
                        );
                scene.addMouseListener(parts[index].triangle(3), action);
                scene.addMouseListener(parts[index].triangle(4), action);
                        scene.addSwipeListener(parts[index].triangle(3), new SwipeAction(index, 1, getStickerIndexForPart(index, 2), (float) (Math.PI/4)));
                        scene.addSwipeListener(parts[index].triangle(4), new SwipeAction(index, 1, getStickerIndexForPart(index, 2), (float) (Math.PI/-16)));
        }

        // Edges
        for (i=0; i < 12; i++) {
            int index = edgeOffset + i;
            for (j=0; j < 2; j++) {
                int k = (i==1 || i==4 || i==7 || i==10) ? 0 : j*2;
                action = new PartAction(
                        index, j, getStickerIndexForPart(index, k)
                        );
                scene.addMouseListener(parts[index].triangle(j*2), action);
                scene.addMouseListener(parts[index].triangle(j*2+1), action);
                switch (j) {
                    case 0:
                        scene.addSwipeListener(parts[index].triangle(j * 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f)));
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f)));
                        break;
                    case 1:
                        scene.addSwipeListener(parts[index].triangle(j * 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f + Math.PI / 4f)));
                        scene.addSwipeListener(parts[index].triangle(j * 2 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2f)));
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
    
    public CubeKind getKind() {
       return CubeKind.BARREL;
    }
}
