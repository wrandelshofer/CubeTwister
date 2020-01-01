/* @(#)Barrel3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.Shape3D;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;

/**
 * Geometrical representation of a Rubik's Barrel in
 * three dimensions.
 *
 * @author Werner Randelshofer
 */
public class RubiksBarrelGeom3D extends AbstractRubiksCubeGeom3D {
    private final static int STICKER_COUNT = 42;
    
    
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
        1, 0, 1,  // front right
        1, 0, 1, // right
        1, 0, 1,  // back right
        0, 0, 0, // back
        1, 0, 1, // back left
        1, 0, 1, // left
        1, 0, 1, // front left
        
        0, 1, 0, 0, 0, 0, 0, 1, 0, // down
        0, 1, 0, 0, 0, 0, 0, 1, 0  // up
    };
    
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
            CORNER_FACES = new int[][] {
            // Faces with stickers and with outlines
            //--------------------------------------
            {23, 24, 25}, //Top face        The order of these faces
            {19, 1, 2, 22},   //Oblique face    is relevant, for method
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
        int i;
        Color[][][] colors = new Color[8][CORNER_FACES.length][0];
        for (i=0; i < 8; i++) {
            for (int j=0; j < CORNER_FACES.length; j++) {
                colors[i][j] = new Color[2];
            }
        }
        
        for (i = 0; i < cornerCount; i++) {
            shapes[cornerOffset+i] = new Shape3D(CORNER_VERTS, CORNER_FACES, colors[i]);
        }
        
    }
    protected void initEdges() {
        initSquareEdges();
        initObliqueEdges();
    }
    private static float[] SQUARE_EDGE_VERTS;
    private static int[][] SQUARE_EDGE_FACES;
    protected void initSquareEdges() {
        int i;
        if (SQUARE_EDGE_VERTS == null) {
            SQUARE_EDGE_VERTS = new float[]         {
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

        }
        if (SQUARE_EDGE_FACES == null) {
            SQUARE_EDGE_FACES = new int[][]          {
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
        }
        
        shapes[edgeOffset+0] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+2] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+3] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+5] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+6] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+8] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+9] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+11] = new Shape3D(SQUARE_EDGE_VERTS, SQUARE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
    }
    private static float[] OBLIQUE_EDGE_VERTS;
    private static int[][] OBLIQUE_EDGE_FACES;
    protected void initObliqueEdges() {
        int i;
        
        if (OBLIQUE_EDGE_VERTS == null) {
            OBLIQUE_EDGE_VERTS = new float[]         {
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
        if (OBLIQUE_EDGE_FACES == null) {
            OBLIQUE_EDGE_FACES = new int[][] {
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

        }
        
        shapes[edgeOffset+1] = new Shape3D(OBLIQUE_EDGE_VERTS, OBLIQUE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+4] = new Shape3D(OBLIQUE_EDGE_VERTS, OBLIQUE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+7] = new Shape3D(OBLIQUE_EDGE_VERTS, OBLIQUE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
        shapes[edgeOffset+10] = new Shape3D(OBLIQUE_EDGE_VERTS, OBLIQUE_EDGE_FACES, new Color[SQUARE_EDGE_FACES.length][2]);
    }
    @Override
    protected void initActions() {
        int i, j;
        for (i=0; i < 8; i++) {
            int index=cornerOffset+i;
            shapes[index].setAction(0, new AbstractCubeGeom3D.PartAction(index, 0, getStickerIndexForPart(index, 0)));
            shapes[index].setAction(2, new AbstractCubeGeom3D.PartAction(index, 1, getStickerIndexForPart(index, 1)));
            shapes[index].setAction(3, new AbstractCubeGeom3D.PartAction(index, 2, getStickerIndexForPart(index, 2)));
                        shapes[index].getFaces()[0].addSwipeListener(new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (Math.PI / -4f)));
                        shapes[index].getFaces()[2].addSwipeListener(new SwipeAction(index, 1, getStickerIndexForPart(index, 1), (float) (Math.PI + Math.PI / 4f)));
                        shapes[index].getFaces()[3].addSwipeListener(new SwipeAction(index, 2, getStickerIndexForPart(index, 2), (float) (Math.PI / -4f)));
        }
        for (i=0; i < 12; i++) {
            int index=edgeOffset+i;
            switch (i) {
                case 1 :
                case 4 :
                case 7 :
                case 10 :
                    for (j=0; j < 2; j++) {
                        //                    shapes[edgeOffset+i].setAction(j+1, new AbstractRubiksCubeFlat3D.EdgeAction(i, j));
                        shapes[index].setAction(j+1, new AbstractCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0:
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI/2+Math.PI / 4f)));
                        break;
                    case 1:
                        shapes[index].getFaces()[j+1].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f)));
                        break;
                }
                    }
                    break;
                default :
                    for (j=0; j < 2; j++) {
                        //                    shapes[edgeOffset+i].setAction(j, new AbstractRubiksCubeFlat3D.EdgeAction(i, j));
                        shapes[index].setAction(j, new AbstractCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0:
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI/2+Math.PI / 4f)));
                        break;
                    case 1:
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f)));
                        break;
                }
                    }
                    break;
            }
        }
        for (i=0; i < 6; i++) {
            int index=sideOffset+i;
            shapes[index].setAction(0, new AbstractCubeGeom3D.PartAction(index, 0, getStickerIndexForPart(index, 0)));
            shapes[centerOffset].setAction(i, new AbstractCubeGeom3D.PartAction(26, i, -1));
           shapes[index].getFaces()[0].addSwipeListener(new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (-Math.PI/4)));
        }
    }
    protected float getUnitScaleFactor() {
        return 1.1f;
    }

    @Nonnull
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, STICKER_COUNT, new int[] {3,3,3,3,3,3,3,3,9,9});

        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[STICKER_COUNT];

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

    @Nonnull
    public String getName() {
        return "Rubik's Barrel";
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
    public int getStickerCount() {
        return STICKER_COUNT;
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
                switch (partIndex - 8) {
                    case 1: case 4: case 7: case 10:
                        // Oblique edges
                offset = 3;
                        break;
                    default:
                        // Square edges
                offset = 2;
                        break;
                }
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
                limit2 = 2;
                limit = 17;
                offset = 4;
            } else if (partIndex < 20) {
                switch (partIndex - 8) {
                    case 1: case 4: case 7: case 10:
                        // Oblique edges
                        limit2 = 1;
                        limit = 17;
                        offset = 3;
                        break;
                    default:
                        // Square edges
                        limit2 = 2;
                        limit = 16;
                        offset = 0;
                        break;
                }
            } else if (partIndex < 26) {
                limit2 = 1;
                limit = 15;
                offset = 0;
            } else {
                limit2 = 6;
                limit = shape.getFaceCount();
                offset = 0;
            }
            if (attributes.getPartFillColor(partIndex) == null) {
                limit = limit2;
            }
            for (int i=shape.getFaceCount() - 1; i >= 0; i--) {
                shape.setBorderColor(i, (i < limit && (i < limit2 || i >= offset)) ? color : null);
            }
        }
    }

    @Nonnull
    public CubeKind getKind() {
        return CubeKind.BARREL;
    }
}
