/* @(#)RubiksDiamondFlat3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import java.awt.Color;
import java.util.*;

/**
 * Geometrical representation of a Rubik's Diamond in
 * three dimensions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * its faces.
 * <br>1.2 2002-07-29 Outlines to inner faces added to improve
 * visual appearance of a semi-disassembled cube.
 * <br>1.1.2 2001-08-16 Inner faces which are never visible removed.
 * <br>1.1.1 2001-07-30 Colors adapted to fit original Rubik's Cube.
 * <br>1.1 2001-07-23 Inner faces have no borders to speed up the painting process.
 * <br>1.0 2000-03-11
 */
public class RubiksDiamondGeom3D extends AbstractRubiksCubeGeom3D {
    private final static int[] stickerToPartMap_old = {
        8, 20, 10, // front
        2, 12, 3,  // front right
        11, 21, 13, // right
        4, 15, 5,  // back right
        14, 23, 16, // back
        6, 18, 7, // back left
        17, 24, 19, // left
        0, 9, 1, // front left
        
        22, // down
        25  // up
    };
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
    /**
     * Updates the fill color of the stickers.
     * The sticker Index is interpreted according to this
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
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    protected float getUnitScaleFactor() {
        return 1.25f;
    }
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
    
    private final static int[] stickerToFaceMap = {
        0, 0, 0, // front
        0, 0, 0,  // front right
        0, 0, 0, // right
        0, 0, 0,  // back right
        0, 0, 0, // back
        0, 0, 0, // back left
        0, 0, 0, // left
        0, 0, 0, // front left
        
        0,  // down
        0,  // up
    };
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
    public int getStickerCount() {
        return 26;
    }
    /** Updates the fill color of the parts.
     */
    protected void updatePartsFillColor() {
        for (int partIndex = 0; partIndex < 27; partIndex++) {
            Color color = attributes.getPartFillColor(partIndex);
            Shape3D shape = getPart(partIndex);
            int offset;
            if (partIndex < 8) {
                offset = 4;
            } else if (partIndex < 20) {
                offset = 3;
            } else if (partIndex < 26) {
                offset = 1;
            } else {
                offset = 0;
            }
            
            for (int i=shape.getFaceCount() - 1; i >= offset; i--) {
                shape.setFillColor(i, color);
            }
        }
    }
    /** Updates the outline color of the parts.
     */
    protected void updatePartsOutlineColor() {
        for (int partIndex = 0; partIndex < 27; partIndex++) {
            Color color = attributes.getPartOutlineColor(partIndex);
            Shape3D shape = getPart(partIndex);
            int limit, limit2;
            int offset;
            if (partIndex < 8) {
                limit = 15;
                limit2 = 1;
                offset = 4;
            } else if (partIndex < 20) {
                limit = 17;
                limit2 = 1;
                offset = 3;
            } else if (partIndex < 26) {
                limit = 15;
                limit2 = 1;
                offset = 1;
            } else {
                limit2 = 6;
                limit = shape.getFaceCount();
                offset = 0;
            }
            if (attributes.getPartFillColor(partIndex) == null) {
                limit = limit2;
            }
            for (int i=shape.getFaceCount() - 1; i >= 0; i--) {
                shape.setBorderColor(i, (i < limit && (i == 0 || i >= offset)) ? color : null);
            }
        }
    }
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[] {
                // Vertices of the main cubicle
                // ----------------------------
                //0: Front Face: center, center-right, bottom-right, bottom-center
                2,-2,-2,  8,0,0,   8,-8,7,   -0,-8,0,
                
                //4: Right Face: center, top-back, center-back, bottom-center, bottom-front
                9,0,0,  9,6,-8, 9,-5,-8,  9,-8,-5,  9,-8,6,
                
                //9: Bottom Face: center, front-right, center-right, back-center, back-left
                0,-9,0,  8,-9,6,  8,-9,-5,  5,-9,-8,  -6,-9,-8,
                
                //14: Back Face: up-right, center, down-left, down-center, center-right
                8,6,-9,  0,0,-9,  -6,-8,-9, 5,-8,-9,  8,-5,-9,
                
                //19: Left Face: center-back, top-front, bottom-front, bottom-left
                -0,0,-8,  2,-2,-2, -0,-8,0, -7,-8,-8,
                
                //23: Top Face: back-center, back-right, center-right, center
                -0,0,-8,  8,7,-8,   8,0,0,  2,-2,-2,
                
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
            CORNER_FACES = new int[][]         {
                // Faces with stickers and with outlines
                //--------------------------------------
                {24,2,22}, // Top, Front, Left Triangle
                
                // Sensor faces (no outlines and no fill color)
                // ------------
                {23, 24, 25, 26}, //Top face      The order of these faces
                {0, 1, 2, 3},     //Front face    is relevant, for method
                {19, 20, 21, 22}, //Left face     updateStickersFillColor()
                
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
            
        }
        
        for (int i = 0; i < cornerCount; i++) {
            shapes[cornerOffset+i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2]);
        }
        
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;
    protected void initEdges() {
        if (EDGE_VERTS == null) {
            EDGE_VERTS = new float[] {
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
        }
        if (EDGE_FACES == null) {
            EDGE_FACES = new int[][] {
                // Faces with stickers and with outlines
                //--------------------------------------
                {22,23, 2, 3},     //Oblique     The order of these faces is relevant
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
                // Faces of the main cubicle
                {5, 6, 7, 8}, //Right
                {9, 10, 11, 12},     //Bottom
                {13, 14, 15, 16},         //Back
                {17, 19, 20, 21},   //Left
                
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
        }
        
        for (int i=0; i < edgeCount; i++) {
            shapes[edgeOffset+i] = new Shape3D(EDGE_VERTS, EDGE_FACES, new Color[EDGE_FACES.length][2]);
        }
    }
    
    protected void initActions() {
        int i, j;
        for (i=0; i < cornerCount; i++) {
            int index=cornerOffset+i;
            for (j=0; j < 3; j++) {
            shapes[index].setAction(j+1, new AbstractCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: // u
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f)));
                        break;
                    case 1: // r
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 4f)));
                        break;
                    case 2: // f
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f)));
                        break;
                }
            }
        }
        for (i=0; i < edgeCount; i++) {
            int index=edgeOffset+i;
            for (j=0; j < 2; j++) {
                shapes[index].setAction(j+1, new AbstractCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0:
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / 2 + Math.PI / 4f)));
                        break;
                    case 1:
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f)));
                        break;
                }
            }
        }
        for (i=0; i < sideCount; i++) {
            int index=sideOffset+i;
            shapes[index].setAction(0, new AbstractCubeGeom3D.PartAction(index, 0, getStickerIndexForPart(index, 0)));
            shapes[index].getFaces()[0].addSwipeListener(new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (-Math.PI / 4)));
            shapes[centerOffset].setAction(i, new AbstractCubeGeom3D.PartAction(centerOffset, i, -1));
        }
    }
    public String getName() {
        return "Rubik's Diamond";
    }

    public CubeKind getKind() {
        return CubeKind.DIAMOND;
    }
}
