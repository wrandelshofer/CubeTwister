/* @(#)PocketCubeFlat3D.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import java.awt.Color;
import java.util.*;

/**
 * Geometrical representation of a Rubik's Cube in
 * three dimensions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>3.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>2.0 2008-01-06 Adapted to changes in AbstractCube.
 * <br>1.0 2005-12-26 Created
 */
public class PocketCubeGeom3D extends AbstractPocketCubeGeom3D {
    /** Updates the outline color of the parts.
     */
    @Override
    protected void updatePartsOutlineColor() {
        for (int partIndex = 0; partIndex < 9; partIndex++) {
            Color color = attributes.getPartOutlineColor(partIndex);
            Shape3D shape = getPart(partIndex);
            int limit, limit2;
            if (partIndex < 8) {
                limit2 = 3;
                limit = 16;
            } else {
                limit2 = 6;
                limit = shape.getFaceCount();
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
    @Override
    final protected void updatePartsFillColor() {
        for (int partIndex = 0, n = getAttributes().getPartCount(); partIndex < n; partIndex++) {
            Color color = getAttributes().getPartFillColor(partIndex);
            Shape3D shape = shapes[partIndex];
            int offset;
            
            if (partIndex < edgeOffset) {
                offset = 3;
            } else if (partIndex < sideOffset) {
                offset = 2;
            } else if (partIndex < centerOffset) {
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
    @Override
    public CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(), 
                new int[]{4, 4, 4, 4, 4, 4});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0*2*2, 1*2*2, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1*2*2, 2*2*2, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2*2*2, 3*2*2, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3*2*2, 4*2*2, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4*2*2, 5*2*2, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5*2*2, 6*2*2, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    @Override
    protected void initActions() {
        int i, j;
        for (i=0; i < cornerCount; i++) {
            int index = cornerOffset+i;
            for (j=0; j < 3; j++) {
                shapes[index].setAction(
                j,
                new AbstractPocketCubeGeom3D.PartAction(
                index, j, getStickerIndexForPart(index, j))
                );
                switch (j) {
                    case 0: {// u
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[6].addSwipeListener(sa);
                        shapes[index].getFaces()[7].addSwipeListener(sa);
                        break;
                        }
                    case 1: {// r
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[8].addSwipeListener(sa);
                        shapes[index].getFaces()[9].addSwipeListener(sa);
                        break;
                        }
                    case 2: {// f
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[10].addSwipeListener(sa);
                        shapes[index].getFaces()[11].addSwipeListener(sa);
                        break;
                        }
                }
            }
        }
        for (i=0; i < 6; i++) {
            shapes[centerOffset].setAction(i, new AbstractPocketCubeGeom3D.PartAction(9, i, -1));
        }
    }
    public String getName() {
        return "Pocket Cube";
    }
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
    
    @Override
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[] {
                // Vertices of the main cubicle
                // ----------------------------
                //0: Front Face: top-left, top-right, bottom-right, bottom-left
                -8,8,9,  8,8,9,   8,-8,9,   -8,-8,9,
                
                //4: Right Face: top-front, top-back, center-back, bottom-center, bottom-front
                9,8,8,  9,8,-8, 9,-5,-8,  9,-8,-5,  9,-8,8,
                
                //9: Bottom Face: front-left, front-right, center-right, back-center, back-left
                -8,-9,8,  8,-9,8,  8,-9,-5,  5,-9,-8,  -8,-9,-8,
                
                //14: Back Face: up-right, up-left, down-left, down-center, center-right
                8,8,-9,  -8,8,-9,  -8,-8,-9, 5,-8,-9,  8,-5,-9,
                
                //19: Left Face: top-back, top-front, bottom-front, bottom-back
                -9,8,-8,  -9,8,8, -9,-8,8, -9,-8,-8,
                
                //23: Top Face: back-left, back-right, front-right, front-left
                -8,9,-8,  8,9,-8,   8,9,8,   -8,9,8,
                
                // Vertices of the additional cubicle at the bottom right
                /*
                //27
                9,-4,-14, 14,-4,-14, 9,-4,-9,   14,-4,-9,
                
                //31
                4,-9,-14, 4,-9,-9,   4,-14,-14, 4,-14,-9,
                
                //35
                9,-14,-4, 14,-14,-4, 9,-9,-4, 14,-9,-4, 14,-14,-14
                 */
            };
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][] {
                // Faces with stickers and with outlines
                //--------------------------------------
                {23, 24, 25, 26}, //Top face      The order of these faces
                {0, 1, 2, 3},     //Front face    is relevant, for method
                {19, 20, 21, 22}, //Left face     updateStickersFillColor()
                
                // Faces with outlines
                // (all faces which have outlines must be
                // at the beginning, this is relevant
                // for method updatePartsOutlineColor()
                //--------------------------------------
                
                // Faces of the main cubicle
                {4, 5, 6, 7, 8},     //Right Face
                {9, 10, 11, 12, 13}, //Bottom Face
                {14, 15, 16, 17, 18},//Back Face
                
                // Faces without outlines
                //--------------------------------------
                
                // Inner edges of the main cubicle. We assign swipe actions to these.
                {24, 5, 4, 25},   //Top Right
                {15, 14, 24, 23}, //Top Back
                {3, 2, 10, 9},    //Bottom Front
                {1, 4, 8, 2},     //Front Right
                {22, 21, 9, 13},  //Bottom Left
                {15, 19, 22, 16}, //Back Left

                // Outer edges of the main cubicle. We assign no actions to these.
                {26, 25, 1, 0},   //Top Front
                {23, 26, 20, 19}, //Top Left
                {11, 10, 8, 7},   //Bottom Right
                {13, 12, 17, 16}, //Bottom Back
                {0, 3, 21, 20},   //Front Left
                {14, 18, 6, 5},   //Back Right
                
                // Triangles at the corners of the main cubicle
                {9, 21, 3}, //Bottom Left Front
                {10, 2, 8}, //Bottom Front Right
                {13, 16, 22},//Bottom Back Left
                
                {26, 0, 20}, //Top Front Left
                {25, 4, 1},  //Top Right Front ruuf rruf ruff
                {23, 19, 15},//Top Left Back luub llub lubb
                {24, 14, 5}, //Top Back Right ruub rubb rrub
                
                
                // Faces of the additional cubicle at the bottom right
                // These faces can never be seen unless we would take the cube apart.
                /*
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
                */
            };
        }
        
        for (int i = 0; i < cornerCount; i++) {
            shapes[cornerOffset+i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2]);
        }
        
    }
    /**
     * Sticker to part map.<br>
     * (the number before the dot indicates the part,
     * the number after the dot indicates the orientation of the part.)
     * <pre>
     *         +---+---+
     *         |4.0|2.0|
     *         +--- ---+ 
     *         |6.0|0.0|   
     * +---+---+---+---+---+---+---+---+
     * |4.1|6.2|6.1|0.2|0.1|2.2|2.1|4.2|
     * +--- ---+--- ---+--- ---+--- ---+
     * |5.2|7.1|7.2|1.1|1.2|3.1|3.2|5.1|
     * +---+---+---+---+---+---+---+---+
     *         |7.0|1.0|
     *         +--- ---+
     *         |5.0|3.0|
     *         +---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 2, 1, 3, // right
        4, 2, 6, 0, // up
        6, 0, 7, 1, // front
        4, 6, 5, 7, // left
        7, 1, 5, 3, // down
        2, 4, 3, 5 // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    /**
     * Maps stickers to the faces of the parts.
     * <p>
     * The number before the comma is the first dimension (faces), the number
     * after the comma is the second dimension (stickers).
     *
     * <pre>
     *         +---+---+
     *         |1,0|1,1|
     *         +--- ---+ 
     *         |1,2|1,3|   
     * +---+---+---+---+---+---+---+---+
     * |3,0|3,1|2,0|2,1|0,0|0,1|5,0|5,1|
     * +--- ---+--- ---+--- ---+--- ---+
     * |3,2|3,3|2,2|2,3|0,2|0,3|5,2|5,3|
     * +---+---+---+---+---+---+---+---+
     *         |4,0|4,1|
     *         +--- ---+
     *         |4,2|4,3|
     *         +---+---+
     * </pre>
     */
    private final static int[] stickerToFaceMap = {
        1, 2, 2, 1, // right
        0, 0, 0, 0, // up
        1, 2, 2, 1, // front
        1, 2, 2, 1, // left
        0, 0, 0, 0, // down
        1, 2, 2, 1 // back
    };
    @Override
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
    
    @Override
    public int getStickerCount() {
        return 6*4;
    }
    @Override
    public CubeKind getKind() {
       return CubeKind.POCKET;
    }
}
