/* @(#)RubiksDiamondIdx3D.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import idx3d.*;
import java.awt.*;
import java.util.Arrays;
/**
 * RubiksDiamondIdx3D.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * its faces.
 * <br>2.0 2008-01-03 Adapted to changes in AbstractCube.
 * <br>1.0 December 26, 2003 Created.
 */
public class RubiksDiamondIdx3D extends AbstractRubiksCubeIdx3D {
    private final static int[] stickerToPartMap = {
        17, 22, 19, // front
        0, 9, 1,  // front right
        8, 20, 10, // right
        2, 12, 3,  // back right
        11, 25, 13, // back
        4, 15, 5, // back left
        14, 23, 16, // left
        6, 18, 7, // front left

        24, // down
        21  // up
    };
    private final static int[] stickerToFaceMap = {
        0, 0, 0, // front
        0, 0, 0,  // front right
        0, 0, 0, // right
        0, 0, 0,  // back right
        0, 0, 0, // back
        0, 0, 0, // back left
        0, 0, 0, // left
        0, 0, 0, // front left
        
        0, // down
        0,  // up
    };
    
    /** Creates a new instance. */
    public RubiksDiamondIdx3D() {
    }
    @Override
    protected float getUnitScaleFactor() {
        return super.getUnitScaleFactor() * 1.5f;
    }
    
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
  @Override
    protected void initCorners() {
        float[] verts = {
            // Vertices of the main cubicle 
            // ----------------------------
            //0: Front Face: center, center-right, bottom-right, bottom-center
            3,-3,-3,  8,-.5f,-.5f,   8,-8,7,   .5f,-8,-.5f,

            //4: Right Face: center, top-back, center-back, bottom-center, bottom-front
            9,0,0,  9,6,-8, 9,-5,-8,  9,-8,-5,  9,-8,6,
            
            //9: Bottom Face: center, front-right, center-right, back-center, back-left
            0,-9,0,  8,-9,6,  8,-9,-5,  5,-9,-8,  -6,-9,-8,

            //14: Back Face: up-right, center, down-left, down-center, center-right
            8,6,-9,  0,0,-9,  -6,-8,-9, 5,-8,-9,  8,-5,-9,

            //19: Left Face: center-back, top-front, bottom-front, bottom-left
            .5f,-.5f,-8,  3,-3,-3, .5f,-8,-.5f, -7,-8,-8,

            //23: Top Face: back-center, back-right, center-right, center
            .5f,-.5f,-8,  8,7,-8,   8,-.5f,-.5f,  3,-3,-3,

            // Vertices of the additional cubicle at the bottom right
            //27
             9,-4,-14, 14,-4,-14, 9,-4,-9,   14,-4,-9,

             //31
             4,-9,-14, 4,-9,-9,   4,-14,-14, 4,-14,-9,

             //35
             9,-14,-4, 14,-14,-4, 9,-9,-4, 14,-9,-4, 14,-14,-14
        };
        int[][] faces = {
            // Faces with stickers and with outlines
            //--------------------------------------
            //{24,2,22}, // Top, Front, Left Triangle
            
            // Sensor faces (no outlines and no fill color)
            // ------------
            
            {23, 24, 25, 26}, //Top face      The order of these faces
            {21, 22, 19, 20}, //Left face     updateStickersFillColor()
            {1, 2,3,0},     //Front face    is relevant, for method
            
            // Faces with outlines
            // (all faces which have outlines must be
            // at the beginning, this is relevant
            // for method updatePartsOutlineColor()
            //--------------------------------------
            // Faces of the main cubicle
            {5, 6, 7, 8},     //Right Face
            {10, 11, 12, 13}, //Bottom Face
            {14, 16, 17, 18},//Back Face
            
            
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
            {24, 14, 5}, //Top Back Right ruub rubb rrub
            
            // Edges of the main cubicle
            {2, 24, 5, 8},   //Top Right to Front Right
            {24, 22, 16, 14}, //Top Back to Back Left
            {10, 13, 22, 2},    //Bottom Front to Bottom Left

            {11, 10, 8, 7},   //Bottom Right
            {13, 12, 17, 16}, //Bottom Back
            {14, 18, 6, 5},   //Back Right

        };

        int i, j, k;
        for (k = 0; k < 8; k++) {
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
            object3D.triangle(1).setTriangleMaterial(sticker);
            object3D.triangle(2).setTriangleMaterial(sticker);
            object3D.triangle(3).setTriangleMaterial(sticker);
            object3D.triangle(4).setTriangleMaterial(sticker);
            object3D.triangle(5).setTriangleMaterial(sticker);
            parts[cornerOffset+k] = object3D;
        }
    }
  @Override
    protected void initEdges() {
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
        
        for (k = 0; k < 12; k++) {
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
            parts[edgeOffset+k] = object3D;
        }
    }
    /**
     * Initializes the side parts.
     */
  @Override
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
    
  @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(27, 26, new int[] {3,3,3,3,3,3,3,3,1,1});
        
        Color[] partsFillColor = new Color[27];
        Color[] partsOutlineColor = new Color[27];
        Color[] stickersFillColor = new Color[42];
        
        Arrays.fill(partsFillColor, 0, 26, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, 26, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, 26, 27, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, 26, 27, new Color(240, 240, 240));
        
        Arrays.fill(stickersFillColor, 0, 3, new Color(0,51,115)); //Front: blue
        Arrays.fill(stickersFillColor, 3, 6, new Color(100,0,100)); //Front Right: dark purple
        Arrays.fill(stickersFillColor, 6, 9, new Color(255,70,0)); //Right: orange
        Arrays.fill(stickersFillColor, 9, 12, new Color(200,100,200)); //Back Right: bright purple
        Arrays.fill(stickersFillColor, 12, 15, new Color(40,200,200)); //Back; cyan
        Arrays.fill(stickersFillColor, 15, 18, new Color(102,102,0)); //Back Left: gold
        Arrays.fill(stickersFillColor, 18, 21, new Color(255,210,0)); //Left: yellow
        Arrays.fill(stickersFillColor, 21, 24, new Color(0,115,47)); //Front-Left: green
        
        Arrays.fill(stickersFillColor, 24, 25, new Color(248,248,248)); //Down: white
        Arrays.fill(stickersFillColor, 25, 26, new Color(140,0,15)); //Up: red

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

  @Override
    public int getStickerCount() {
        return 26;
    }
    /**
     * Gets the part which holds the indicated sticker.
     * The sticker index is interpreted according to this
     * scheme:
     * <pre>
     *                     +---+
     *                     | 25|
     *         /\ +---+ /\ +---+ /\ +---+ /\ +---+
     *        /15\| 18|/21\| 0 |/ 3\| 6 |/ 9\| 12| 
     *       +----+---+----+---+----+---+----+---+
     *       | 16 | 19| 22 | 1 |  4 | 7 | 10 | 13| 
     *       +----+---+----+---+----+---+----+---+
     *        \17/| 20|\23/| 2 |\ 5/| 8 |\11/| 14| 
     *         \/ +---+ \/ +---+ \/ +---+ \/ +---+
     *                     | 24|
     *                     +---+
     * </pre>
     */
  @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    
  @Override
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex];
    }
  @Override
    protected int getStickerIndexForPart(int part, int orientation) {
        int sticker;
        for (sticker = stickerToPartMap.length - 1; sticker >= 0; sticker--) {
            if (stickerToPartMap[sticker] == part
            && stickerToFaceMap[sticker] == orientation) break;
        }
        return sticker;
    }
    
  @Override
    protected void initActions(idx3d_Scene scene) {
        int i, j;
        PartAction action;
        
        // Corners
        for (i=0; i < 8; i++) {
            int index = cornerOffset + i;
            for (j=0; j < 3; j++) {
                action = new PartAction(
                index, j, getStickerIndexForPart(index, 0)
                );
                
                scene.addMouseListener(parts[index].triangle(j*2), action);
                scene.addMouseListener(parts[index].triangle(j*2+1), action);
                switch (j) {
                    case 0:
                        //scene.addSwipeListener(parts[index].triangle(j * 2), new SwipeAction(index, 1, getStickerIndexForPart(index, j), (float) (Math.PI / 2f + Math.PI / 4f)));
                        //scene.addSwipeListener(parts[index].triangle(j * 2 + 1), new SwipeAction(index, 1, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f)));
                        break;
                    case 1:
                     //   scene.addSwipeListener(parts[index].triangle(j * 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f + Math.PI / 4f)));
                    //    scene.addSwipeListener(parts[index].triangle(j * 2 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 2f)));
                        break;
                    case 2:
                   //     scene.addSwipeListener(parts[index].triangle(j * 2), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f + Math.PI / 4f)));
                    //    scene.addSwipeListener(parts[index].triangle(j * 2 + 1), new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 2f)));
                        break;
                }
            }
        }
        
        // Edges
        for (i=0; i < 12; i++) {
            int index = edgeOffset + i;
            for (j=0; j < 2; j++) {
                action = new PartAction(
                index, j, getStickerIndexForPart(index, 0)
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
       return CubeKind.DIAMOND;
    }
}
