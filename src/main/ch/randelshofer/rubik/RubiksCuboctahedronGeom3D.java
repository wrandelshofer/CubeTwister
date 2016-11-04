/*
 * @(#)RubiksCuboctahedronFlat3D.java  2.1  2010-01-30
 *
 * Copyright (c) 2000-2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import java.awt.Color;
import java.util.*;

/**
 * Geometrical representation of a Rubik's Cuboctahedron in
 * three dimensions using the Geom3D engine.
 *
 * @author Werner Randelshofer
 * @version 2.1 2010-01-30 Completed color scheme.
 * <br>2.0 2009-01-04 Added support for twisting the cube by swiping over
 * its faces.
 * <br>1.1.4 2002-07-14 Cube draws more border lines.
 * <br>1.1.3 2002-04-07 Actions were not properly assigned to the
 * hexagonal stickers of the cube.
 * <br>1.1.2 2001-08-16 Inner faces which are never visible removed.
 * <br>1.1.1 2001-07-30 Colors adapted to fit original Rubik's Cube.
 * <br>1.1 2001-07-23 Inner faces have no borders to speed up the painting process.
 * <br>1.0 2000-03-11
 */
public class RubiksCuboctahedronGeom3D extends AbstractRubiksCubeGeom3D {
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
    protected float getUnitScaleFactor() {
        return 1.07f;
    }
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
        for (int i=0; i < 27; i++) {
            a.setPartFillColor(i, partsFillColor);
            a.setPartOutlineColor(i, partsOutlineColor);
        }
        
        return a;
    }
    private final static int[] stickerToFaceMap = {
        1, 0, 2, 1, 0, 1, 2, 0, 1, // front
        1, 1, 2, 0, 0, 0, 2, 1, 1, // right
        0, 0, 0, 1, 0, 1, 0, 0, 0, // down
        1, 0, 2, 1, 0, 1, 2, 0, 1, // back
        1, 1, 2, 0, 0, 0, 2, 1, 1, // left
        0, 0, 0, 1, 0, 1, 0, 0, 0,  // up

        2, 3, 2, 2, // Top Front Right
        3, 3, 3, 3, // Top Back Right
        2, 3, 2, 2, // Top Back Left
        3, 3, 3, 3, // Top Front Left

        3, 3, 3, 3, // Down Front Right
        2, 2, 3, 2, // Down Back Right
        3, 3, 3, 3, // Down Back Left
        2, 2, 3, 2, // Down Front Left
    };
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
        return 86;
    }
    /** Updates the outline color of the parts.
     */
    protected void updatePartsOutlineColor() {
        for (int partIndex = 0; partIndex < partCount; partIndex++) {
            Color color = attributes.getPartOutlineColor(partIndex);
            Shape3D shape = shapes[partIndex];
            int limit, limit2;
            if (partIndex < 8) {
                limit2 = 4;
                limit = 17;
            } else if (partIndex < 20) {
                limit2 = 4;
                limit = 18;
            } else if (partIndex < 26) {
                limit2 = 1;
                limit = 15;
            } else {
                limit = shape.getFaceCount();
                limit2 = 6;
            }
            if (attributes.getPartFillColor(partIndex) == null) {
                limit = limit2;
            }
            for (int i=shape.getFaceCount() - 1; i >= 0; i--) {
                shape.setBorderColor(i, (i < limit) ? color : null);
            }
        }
    }
    /** Updates the fill color of the parts.
     */
    final protected void updatePartsFillColor() {
        for (int partIndex = 0, n = getAttributes().getPartCount(); partIndex < n; partIndex++) {
            Color color = getAttributes().getPartFillColor(partIndex);
            Shape3D shape = shapes[partIndex];
            int offset;
            
            if (partIndex < edgeOffset) {
                // corners
                offset = 3;
            } else if (partIndex < sideOffset) {
                // edges
                offset = 2;
            } else if (partIndex < centerOffset) {
                // sides
                offset = 1;
            } else {
                offset = 0;
            }
            //System.out.println("partIndex"+partIndex+" colr="+color+" faces="+shape.getFaceCount());
            
            for (int i=shape.getFaceCount() - 1; i >= offset; i--) {
                shape.setFillColor(i, color);
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
            
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][] {
                // Faces with stickers and with outlines
                //--------------------------------------
                {24, 25, 26}, //Top face      The order of these faces
                {2, 0, 1},     //Front face    is relevant, for method
                {22, 23, 21}, //Left face     updateStickersFillColor()
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
}
        if (EDGE_FACES == null) {
            EDGE_FACES = new int[][] {
        // Faces with stickers and with outlines
        //--------------------------------------
        {3, 4, 0, 1, 2},     //Front  The order of these faces is relevant
        {25, 26, 27, 28, 29}, //Top    for method updateStickersFillColor
        {30, 31, 32}, // Triangle top right
        {33, 34, 35}, // Triangle top left
        
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
    }
        for (int i = 0; i < edgeCount; i++) {
            shapes[edgeOffset+i] = new Shape3D(EDGE_VERTS, EDGE_FACES, new Color[EDGE_FACES.length][4]);
        }
    }
    
    protected void initActions() {
        int i, j;
        for (i=0; i < cornerCount; i++) {
            int index=cornerOffset+i;
            for (j=0; j < 3; j++) {
                shapes[index].setAction(j, new AbstractRubiksCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: // u
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f)));
                        break;
                    case 1: // r
                        // Rotated by 180° with respect to RubiksCubeGeom3D
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) ( Math.PI / 4f)));
                        break;
                    case 2: // f
                        // Rotated by 180° with respect to RubiksCubeGeom3D
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / -4f)));
                        break;
                }
            }
            shapes[index].setAction(3, new AbstractRubiksCubeGeom3D.PartAction(index, -1, getStickerIndexForPart(index, 3)));
        }
        for (i=0; i < edgeCount; i++) {
            int index=edgeOffset+i;
            for (j=0; j < 2; j++) {
                shapes[index].setAction(j, new AbstractRubiksCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0:
                        // Rotated by 180° with respect to RubiksCubeGeom3D
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI+Math.PI / 2 + Math.PI / 4f)));
                        break;
                    case 1:
                        shapes[index].getFaces()[j].addSwipeListener(new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f)));
                        break;
                }
            }
            shapes[index].setAction(2, new AbstractRubiksCubeGeom3D.PartAction(index, -1, getStickerIndexForPart(index, 2)));
            shapes[index].setAction(3, new AbstractRubiksCubeGeom3D.PartAction(index, -1, getStickerIndexForPart(index, 3)));
        }
        for (i=0; i < sideCount; i++) {
            int index=sideOffset+i;
            shapes[index].setAction(0, new AbstractRubiksCubeGeom3D.PartAction(index, 0, getStickerIndexForPart(index, 0)));
            shapes[index].getFaces()[0].addSwipeListener(new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (-Math.PI / 4)));
            shapes[centerOffset].setAction(i, new AbstractRubiksCubeGeom3D.PartAction(centerOffset, i, -1));
        }
    }
    
    public String getName() {
        return "Rubik's Cuboctahedron";
    }

    public CubeKind getKind() {
        return CubeKind.CUBOCTAHEDRON;
    }
}
