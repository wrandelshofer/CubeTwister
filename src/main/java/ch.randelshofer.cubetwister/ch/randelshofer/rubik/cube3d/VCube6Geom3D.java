/* @(#)VCube6Geom3D.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.geom3d.Shape3D;
import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube.Cube6;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.util.Arrays;

/**
 * Geometrical representation of {@link Cube6} as a V-Cube 6 in three dimensions.
 * <p>
 * In a V-Cube 6, the edge parts are rectangular, and the corner parts are
 * bigger than the side parts.
 *
 * @author Werner Randelshofer
 */
public class VCube6Geom3D extends AbstractCube6Geom3D {
    /**
     * The length of side parts is extended towards the center of the cube
     * to prevent 'holes' when the cube is twisting.
     */
    private final static float EXT_LENGTH = PART_LENGTH + 1f;
    
    private final static int STICKER_COUNT = 6 * 6 * 6;

    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
    @Override
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            // The corner parts are bigger than the side parts
            CORNER_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, 
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        //8:lluf      lldf       rruf      rrdf
                        -EXT_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        -EXT_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        PART_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), 
                        PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        PART_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -EXT_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -EXT_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), 
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), EXT_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(EXT_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
        }
        if (CORNER_FACES == null) {
            CORNER_FACES = new int[][]{
                        // Faces with stickers and actions
                        {22, 20, 18, 16}, //Up
                        {0, 2, 3, 1}, //Front
                        {14, 8, 9, 15}, //Left

                        // Edges with swipe actions
                        {20, 12, 10, 18}, //Up Right
                        {6, 4, 20, 22}, //Up Back

                        {1, 3, 19, 17}, //Down Front
                        {2, 10, 11, 3}, //Front Right rdff ruff rruf rrdf

                        {6, 14, 15, 7}, //Back Left
                        {15, 9, 17, 23}, //Down Left lddf lddb lldb lldf

                        // Edges without actions
                        {21, 19, 11, 13}, //Down Right rddb rddf rrdf rrdb
                        {0, 1, 9, 8}, //Front Left
                        {16, 18, 2, 0}, //Up Front
                        {22, 16, 8, 14}, //Up Left
                        {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb
                        {4, 5, 13, 12}, //Back Right

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
        
        for (int i = 0; i < cornerCount; i++) {
            shapes[cornerOffset+i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2], CORNER_FACES.length - 3);
            shapes[cornerOffset+i].setReduced(true);
        }
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;

    @Override
    protected void initEdges() {
        int i;

        if (EDGE_VERTS == null) {
           // The edge parts are rectangular
            EDGE_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
        }

        if (EDGE_FACES == null) {
            EDGE_FACES = new int[][]{
                        {0, 2, 3, 1}, //Front
                        {22, 20, 18, 16}, //Up

                        // Edges with swipe actions
                        {1, 3, 19, 17}, //Down Front
                        {2, 10, 11, 3}, //Front Right rdff ruff rruf rrdf
                        {8, 0, 1, 9}, //Front Left

                        {20, 12, 10, 18}, //Up Right
                        {6, 4, 20, 22}, //Up Back
                        {14, 22, 16, 8}, //Up Left

                        // Edges without actions
                        {16, 18, 2, 0}, //Up Front

                        //
                        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                        {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
                        //{23, 7,15}, //Bottom Back Left lddb ldbb lldb
                        //{21,13, 5}, //Bottom Right Back rddb rrdb rdbb

                        {16, 0, 8}, //Top Front Left luuf luff lluf
                        {18, 10, 2}, //Top Right Front ruuf rruf ruff
                        {22, 14, 6}, //Top Left Back luub llub lubb
                        {20, 4, 12}, //Top Back Right ruub rubb rrub


                        //{23, 21, 5, 7},   //Bottom Back lddb rddb rdbb ldbb


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

        Color[][][] colors = new Color[edgeCount][EDGE_FACES.length][0];
        //Color[] faceColor = new Color[] {PART_FILL_COLOR, null};
        for (i = 0; i < edgeCount; i++) {
            for (int j = 0; j < EDGE_FACES.length; j++) {
                colors[i][j] = new Color[2];
            }
        }


        for (i = 0; i < edgeCount; i++) {
            shapes[edgeOffset + i] = new Shape3D(EDGE_VERTS, EDGE_FACES, colors[i], EDGE_FACES.length - 2);
            shapes[edgeOffset + i].setReduced(true);
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
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (EXT_LENGTH * 0.5f - BEVEL_LENGTH),
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

                        // Edges with actions
                        {16, 18, 2, 0}, //Top Front
                        {1, 3, 19, 17}, //Bottom Front
                        {2, 10, 11, 3}, //Front Right rdff ruff rruf rrdf
                        {8, 0, 1, 9}, //Front Left


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

                        {18, 20, 12, 10}, //Top Right
                        {22, 16, 8, 14}, //Top Left
                        {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
                        {17, 23, 15, 9}, //Bottom Left lddf lddb lldb lldf

                        // Cut Off Faces: The following faces need only be drawn,
                        //                when a layer of the cube is being twisted.
                        {16, 22, 20, 18}, //Top
                        {14, 8, 9, 15}, //Left
                        {12, 13, 11, 10}, //Right
                        {17, 19, 21, 23}, //Bottom
                    };
        }
        
        for (int i = 0; i < sideCount; i++) {
            shapes[sideOffset+i] = new Shape3D(SIDE_VERTS, SIDE_FACES, new Color[SIDE_FACES.length][2], SIDE_FACES.length - 2);
            shapes[sideOffset+i].setReduced(true);
        }
    }
    
    /** The simple cube has no visible center because
     * it can not be taken apart.
     */
    @Override
    protected void initCenter() {
        /*
        float[] verts = {
            //0:luff      ldff       ruff       rdff
            -15, 15, 15,  -15,-15, 15,   15, 15, 15,   15,-15, 15,
         
            //4:rubb,    rdbb,       lubb,       ldbb
            15,15,-15,   15,-15,-15,   -15,15,-15,  -15,-15,-15,
        };
        int[][] faces = {
            {0, 2, 3, 1},     //Front
            {4, 6, 7, 5},     //Back
            {6, 4, 2, 0}, //Top
            {1, 7, 6, 0}, //Left
            {2, 4, 5, 3}, //Right
            {3, 5, 7, 1}, //Bottom
        };
         
        Color[][] colors = new Color[faces.length][2];
        Color[] faceColor = {PART_FILL_COLOR, null};
            for (int j=0; j < faces.length; j++) {
                colors[j] = faceColor;
            }
         
        shapes[centerOffset] = new Shape3D(verts, faces, colors);
         */
        float[] verts = {};
        int[][] faces = {};
        
        Color[][] colors = {};
        
        shapes[centerOffset] = new Shape3D(verts, faces, colors);
        shapes[centerOffset].setVisible(false);
        
    }
    @Override
    protected void initActions() {
        for (int i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            for (int j = 0; j < 3; j++) {
                shapes[index].setAction(
                        j,
                        new PartAction(
                                index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: {// u
                        SwipeAction sa = new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[3].addSwipeListener(sa);
                        shapes[index].getFaces()[4].addSwipeListener(sa);
                        break;
                        }
                    case 1: {// r
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI + Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[5].addSwipeListener(sa);
                        shapes[index].getFaces()[6].addSwipeListener(sa);
                        break;
                        }
                    case 2: {// f
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI / -4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[7].addSwipeListener(sa);
                        shapes[index].getFaces()[8].addSwipeListener(sa);
                        break;
                        }
                }
            }
        }
        for (int i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            for (int j = 0; j < 2; j++) {
                shapes[index].setAction(j, new PartAction(index, j, getStickerIndexForPart(index, j)));
                switch (j) {
                    case 0: {
                        SwipeAction sa =new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (Math.PI/2+Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[2].addSwipeListener(sa);
                        shapes[index].getFaces()[3].addSwipeListener(sa);
                        shapes[index].getFaces()[4].addSwipeListener(sa);
                        break;
                        }
                    case 1: {
                        SwipeAction sa=new SwipeAction(index, j, getStickerIndexForPart(index, j), (float) (-Math.PI / 4f));
                        shapes[index].getFaces()[j].addSwipeListener(sa);
                        shapes[index].getFaces()[5].addSwipeListener(sa);
                        shapes[index].getFaces()[6].addSwipeListener(sa);
                        shapes[index].getFaces()[7].addSwipeListener(sa);
                        break;
                        }
                }
            }
        }
        for (int i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            shapes[index].setAction(0, new PartAction(index, 0, getStickerIndexForPart(index, 0)));
            SwipeAction sa = new SwipeAction(index, 0, getStickerIndexForPart(index, 0), (float) (-Math.PI / 4));
            shapes[index].getFaces()[0].addSwipeListener(sa);
            shapes[index].getFaces()[1].addSwipeListener(sa);
            shapes[index].getFaces()[2].addSwipeListener(sa);
            shapes[index].getFaces()[3].addSwipeListener(sa);
            shapes[index].getFaces()[4].addSwipeListener(sa);
        }
    //for (i = 0; i < 6; i++) {
    //            shapes[centerOffset].setAction(i, new AbstractRubiksCubeFlat3D.PartAction(8+12*3+6*9, i, -1));
    //}
    }

    @Nonnull
    public String getName() {
        return "V-Cube 6";
    }
    
    /**
     * Sticker to part map.<br>
     * (the number before the dot indicates the part,
     * the number after the dot indicates the sticker.)
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |4.0|27 |3.1|15 |39 |2.0|
     *                           +---+---+---+---+---+---+
     *                           |30 |25  79  55  31 |24 |
     *                           +---+               +---+  
     *                           |6.0|49   1   7  85 |0.0|
     *                           +---+       u       +---+ 
     *                           |18 |73  19  13  61 |12 |
     *                           +---+               +---+ 
     *                           |42 |43  67  91  37 |36 |
     *                           +---+---+---+---+---+---+
     *                           |6.0|33 |9.1|21 |45 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |4.1|30 |6.1|18 |42 |6.2|6.1|33 |9.0|21 |45 |0.2|0.1|36 |12 |0.1|24 |2.2|2.1|39 |15 |3.0|27 |4.2|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |45  75  51  27 |46 |46 |32  86  62  38 |37 |37 |36  90  66  42 |40 |40 |29  83  59  35 |43 |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |19 |69  21  3.1 81 |22 |22 |56  8.3 14  92 |13 |13 |60 12.0 18  72 |16 |16 |53  5.2 11  89 |19 |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |7.0|93  15   9  57 10.0|10.1 80  2  20  68 |1.1|1.0|84   6   0  48 |4.0|4.1|77  23  17  65 |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |31 |39  63  87  33 |34 |34 |26  50  74  44 |25 |25 |30  54  78  24 |28 |28 |47  71  95  41 |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |5.2|32 |8.1|20 |44 |7.1|7.2|35 11.1|23 |47 |1.1|1.2|38 |14 |2.1|26 |3.1|3.2|41 |17 |5.0|29 |5.1|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |7.0|35 11.1|23 |47 |1.0|
     *                           +---+---+---+---+---+---+
     *                           |44 |46  76  52  28 |38 |
     *                           +---+               +---+ 
     *                           |20 |70  22   4  82 |14 |
     *                           +---+       d       +---+ 
     *                           |8.0|94  16  10  58 |2.0|
     *                           +---+               +---+  
     *                           |32 |40  64  88  34 |26 |
     *                           +---+---+---+---+---+---+
     *                           |5.0|29 |5.1|17 |41 |3.0|
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 36 + 8, 12 + 8, 0 + 8, 24 + 8, 2, //
        37 + 8, 36 + 56, 90 + 56, 66 + 56, 42 + 56, 40 + 8,//
        13 + 8, 60 + 56, 12 + 56, 18 + 56, 72 + 56, 16 + 8,//
        1 + 8, 84 + 56, 6 + 56, 0 + 56, 48 + 56, 4 + 8,//
        25 + 8, 30 + 56, 54 + 56, 78 + 56, 24 + 56, 28 + 8,//
        1, 38 + 8, 14 + 8, 2 + 8, 26 + 8, 3, // right
        //
        4, 27 + 8, 3 + 8, 15 + 8, 39 + 8, 2,//
        30 + 8, 25 + 56, 79 + 56, 55 + 56, 31 + 56, 24 + 8, //
        6 + 8, 49 + 56, 1 + 56, 7 + 56, 85 + 56, 0 + 8, //
        18 + 8, 73 + 56, 19 + 56, 13 + 56, 61 + 56, 12 + 8, //
        42 + 8, 43 + 56, 67 + 56, 91 + 56, 37 + 56, 36 + 8, //
        6, 33 + 8, 9 + 8, 21 + 8, 45 + 8, 0, // up
        //
        6, 33 + 8, 9 + 8, 21 + 8, 45 + 8, 0, //
        46 + 8, 32 + 56, 86 + 56, 62 + 56, 38 + 56, 37 + 8,//
        22 + 8, 56 + 56, 8 + 56, 14 + 56, 92 + 56, 13 + 8, //
        10 + 8, 80 + 56, 2 + 56, 20 + 56, 68 + 56, 1 + 8,//
        34 + 8, 26 + 56, 50 + 56, 74 + 56, 44 + 56, 25 + 8, //
        7, 35 + 8, 11 + 8, 23 + 8, 47 + 8, 1, // front
        //
        4, 30 + 8, 6 + 8, 18 + 8, 42 + 8, 6,//
        43 + 8, 45 + 56, 75 + 56, 51 + 56, 27 + 56, 46 + 8,//
        19 + 8, 69 + 56, 21 + 56, 3 + 56, 81 + 56, 22 + 8,//
        7 + 8, 93 + 56, 15 + 56, 9 + 56, 57 + 56, 10 + 8, //
        31 + 8, 39 + 56, 63 + 56, 87 + 56, 33 + 56, 34 + 8,//
        5, 32 + 8, 8 + 8, 20 + 8, 44 + 8, 7, // left
        //
        7, 35 + 8, 11 + 8, 23 + 8, 47 + 8, 1, //
        44 + 8, 46 + 56, 76 + 56, 52 + 56, 28 + 56, 38 + 8,//
        20 + 8, 70 + 56, 22 + 56, 4 + 56, 82 + 56, 14 + 8, //
        8 + 8, 94 + 56, 16 + 56, 10 + 56, 58 + 56, 2 + 8, //
        32 + 8, 40 + 56, 64 + 56, 88 + 56, 34 + 56, 26 + 8, //
        5, 29 + 8, 5 + 8, 17 + 8, 41 + 8, 3, // down
        //
        2, 39 + 8, 15 + 8, 3 + 8, 27 + 8, 4, //
        40 + 8, 29 + 56, 83 + 56, 59 + 56, 35 + 56, 43 + 8,//
        16 + 8, 53 + 56, 5 + 56, 11 + 56, 89 + 56, 19 + 8,//
        4 + 8, 77 + 56, 23 + 56, 17 + 56, 65 + 56, 7 + 8, //
        28 + 8, 47 + 56, 71 + 56, 95 + 56, 41 + 56, 31 + 8,//
        3, 41 + 8, 17 + 8, 5 + 8, 29 + 8, 5 // back
    };
    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, // right
        //
        0, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 0, // up
        //
        1, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 1, // front
        //
        1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, // left
        //
        0, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 0, // down
        //
        1, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 1, // back
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
        return STICKER_COUNT;
    }

    @Nonnull
    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(), new int[]{6*6, 6*6, 6*6, 6*6, 6*6, 6*6});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0*6*6, 1*6*6, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1*6*6, 2*6*6, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2*6*6, 3*6*6, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3*6*6, 4*6*6, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4*6*6, 5*6*6, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5*6*6, 6*6*6, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    /** Updates the outline color of the parts.
     */
    @Override
    protected void updatePartsOutlineColor() {
        for (int partIndex = 0; partIndex < partCount; partIndex++) {
            Color color = attributes.getPartOutlineColor(partIndex);
            Shape3D shape = getPart(partIndex);
            int limit, limit2;
            if (partIndex < edgeOffset) {
                limit2 = 3;
                limit = 16;
            } else if (partIndex < sideOffset) {
                limit2 = 2;
                limit = 16;
            } else if (partIndex < centerOffset) {
                limit2 = 1;
                limit = 15;
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

    @Nonnull
    @Override
    public CubeKind getKind() {
        return CubeKind.CUBE_6;
    }
}
