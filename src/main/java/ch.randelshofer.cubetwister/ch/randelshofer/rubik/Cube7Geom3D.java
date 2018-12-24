/* @(#)Cube7Geom3D.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.Shape3D;

import java.awt.Color;
import java.util.Arrays;

/**
 * Simplified geometrical representation of {@link Cube7} in three dimensions.
 * <p>
 * The representation is simplified in the sense that all stickers of the
 * cube are square. In a real physical representation, such as a V-Cube 7, 
 * the surfaces of the cube are slightly rounded, resulting in stickers of
 * different sizes and different aspect ratios.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.1 2009-01-04 Added support for twisting the cube by swiping over
 * its faces.
 * <br>1.0 2008-08-19 Created.
 */
public class Cube7Geom3D extends AbstractCube7Geom3D {
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;
    @Override
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            CORNER_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        //8:lluf      lldf       rruf      rrdf
                        -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //1(PART_LENGTH * 0.5f - BEVEL_LENGTH):luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
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
        
        for (int i = 0; i < 8; i++) {
            shapes[cornerOffset+i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2], CORNER_FACES.length - 3);
            shapes[cornerOffset+i].setReduced(true);
        }
    }
    private static float[] EDGE_VERTS;
    private static int[][] EDGE_FACES;
    @Override
    protected void initEdges() {
        if (EDGE_VERTS == null) {
            EDGE_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        //8:lluf      lldf       rruf      rrdf
                        -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //1(PART_LENGTH * 0.5f - BEVEL_LENGTH):luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
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
        
        for (int i = 0; i < edgeCount; i++) {
            shapes[edgeOffset+i] = new Shape3D(EDGE_VERTS, EDGE_FACES, new Color[EDGE_FACES.length][2], EDGE_FACES.length - 4);
            shapes[edgeOffset+i].setReduced(true);
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
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), 
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH), 
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH),
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
            shapes[sideOffset+i] = new Shape3D(SIDE_VERTS, SIDE_FACES, new Color[SIDE_FACES.length][2], SIDE_FACES.length - 4);
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
                        new AbstractCubeGeom3D.PartAction(
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
                shapes[index].setAction(j, new AbstractCubeGeom3D.PartAction(index, j, getStickerIndexForPart(index, j)));
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
            shapes[index].setAction(0, new AbstractCubeGeom3D.PartAction(index, 0, getStickerIndexForPart(index, 0)));
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
    
    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(), 
                new int[]{layerCount*layerCount, layerCount*layerCount, layerCount*layerCount, layerCount*layerCount, layerCount*layerCount, layerCount*layerCount});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0*layerCount*layerCount, 1*layerCount*layerCount, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1*layerCount*layerCount, 2*layerCount*layerCount, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2*layerCount*layerCount, 3*layerCount*layerCount, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3*layerCount*layerCount, 4*layerCount*layerCount, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4*layerCount*layerCount, 5*layerCount*layerCount, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5*layerCount*layerCount, 6*layerCount*layerCount, new Color(255, 70, 0)); // Back: Orange

        a.setPartFillColor(partsFillColor);
        a.setPartOutlineColor(partsOutlineColor);
        a.setStickerFillColor(stickersFillColor);
        return a;
    }

    /**
     * The numbers show the part indices. The stickers are numbered from top
     * left to bottom right on each face. The sequence of the faces is right,
     * up, front, left, down, back. 
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |4.0|39 |15 |3.1|27 |51 |2.0|
     *                               +---+---+---+---+---+---+---+
     *                               |42 |55  133 85  109 61 |36 |
     *                               +---+                   +---+  
     *                               |18 |103  7  37  13  139|12 |
     *                               +---+                   +---+  
     *                               |6.0|79  31  1.2 43  91 |0.0|
     *                               +---+                   +---+  
     *                               |30 |127 25  49  19  115|24 |
     *                               +---+                   +---+  
     *                               |54 |73  121 97  145 67 |48 |
     *                               +---+---+---+---+---+---+---+
     *                               |6.0|45 |21 |9.1|33 |57 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 4 |42 |18 |6.1|30 |54 | 6 | 6 |45 |21 |9.0|33 |57 | 0 | 0 |48 |24 |0.1|12 |36 | 2 | 2 |51 |27 |3.0|15 |39 | 4 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |75  129 81  105 57 |58 |58 |62  140 92  116 68 |49 |49 |66  144 96  120 72 |52 |52 |59  137 89  113 65 |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |31 |123  27  33   9 135|34 |34 |110 14  44  20  146|25 |25 |114 18  48  24  126|28 |28 |107 11  41  17  143|31 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |7.0|99  51  3.1 39  87 10.0|10.1 86  38 2.3 50  98 |1.1|1.0|90  42  0.0 30  78 |4.0|4.1|83  35  5.2 47  95 |7.1|
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |19 |147 21  45  15  111|22 |22 |134  8  32  26  122|13 |13 |138 12  36   6  102|16 |16 |131 29  53  23  119|19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |43 |69  117 93  141 63 |46 |46 |56  104 80  128 74 |37 |37 |60  108 84  132 54 |40 |40 |77  125 101 149 71 |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 5 |44 |20 |8.1|32 |56 | 7 | 7 |47 |23 11.0|35 |59 | 1 | 1 |50 |26 |2.1|14 |38 | 3 | 3 |53 |29 |5.0| 17|41 | 5 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |7.0|47 |23 11.1|35 |59 |1.0|
     *                               +---+---+---+---+---+---+---+
     *                               |56 |76  130 82  106 58 |50 |
     *                               +---+                   +---+  
     *                               |32 |124 28  34  10  136|26 |
     *                               +---+                   +---+  
     *                               |8.0|100 52  4.1 40  88 |2.0|
     *                               +---+                   +---+  
     *                               |20 |148 22  46  16  112|14 |
     *                               +---+                   +---+  
     *                               |44 |70  118 94  142 64 |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |5.0|41 |17 |5.1|29 |53 |3.0|
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 48 + 8, 24 + 8, 0 + 8, 12 + 8, 36 + 8, 2, //
        49 + 8, 66 + 68, 144 + 68, 96 + 68, 120 + 68, 72 + 68, 52 + 8, //
        25 + 8, 114 + 68, 18 + 68, 48 + 68, 24 + 68, 126 + 68, 28 + 8,//
        1 + 8, 90 + 68, 42 + 68, 0 + 68, 30 + 68, 78 + 68, 4 + 8, //
        13 + 8, 138 + 68, 12 + 68, 36 + 68, 6 + 68, 102 + 68, 16 + 8,//
        37 + 8, 60 + 68, 108 + 68, 84 + 68, 132 + 68, 54 + 68, 40 + 8,//
        1, 50 + 8, 26 + 8, 2 + 8, 14 + 8, 38 + 8, 3, // right
        //
        4, 39 + 8, 15 + 8, 3 + 8, 27 + 8, 51 + 8, 2, //
        42 + 8, 55 + 68, 133 + 68, 85 + 68, 109 + 68, 61 + 68, 36 + 8, //
        18 + 8, 103 + 68, 7 + 68, 37 + 68, 13 + 68, 139 + 68, 12 + 8, //
        6 + 8, 79 + 68, 31 + 68, 1 + 68, 43 + 68, 91 + 68, 0 + 8,//
        30 + 8, 127 + 68, 25 + 68, 49 + 68, 19 + 68, 115 + 68, 24 + 8,//
        54 + 8, 73 + 68, 121 + 68, 97 + 68, 145 + 68, 67 + 68, 48 + 8,
        6, 45 + 8, 21 + 8, 9 + 8, 33 + 8, 57 + 8, 0, // up
        //
        6, 45 + 8, 21 + 8, 9 + 8, 33 + 8, 57 + 8, 0,//
        58 + 8, 62 + 68, 140 + 68, 92 + 68, 116 + 68, 68 + 68, 49 + 8, //
        34 + 8, 110 + 68, 14 + 68, 44 + 68, 20 + 68, 146 + 68, 25 + 8,//
        10 + 8, 86 + 68, 38 + 68, 2 + 68, 50 + 68, 98 + 68, 1 + 8,//
        22 + 8, 134 + 68, 8 + 68, 32 + 68, 26 + 68, 122 + 68, 13 + 8,//
        46 + 8, 56 + 68, 104 + 68, 80 + 68, 128 + 68, 74 + 68, 37 + 8,//
        7, 47 + 8, 23 + 8, 11 + 8, 35 + 8, 59 + 8, 1, // front
        //
        4, 42 + 8, 18 + 8, 6 + 8, 30 + 8, 54 + 8, 6, //
        55 + 8, 75 + 68, 129 + 68, 81 + 68, 105 + 68, 57 + 68, 58 + 8,//
        31 + 8, 123 + 68, 27 + 68, 33 + 68, 9 + 68, 135 + 68, 34 + 8,//
        7 + 8, 99 + 68, 51 + 68, 3 + 68, 39 + 68, 87 + 68, 10 + 8,//
        19 + 8, 147 + 68, 21 + 68, 45 + 68, 15 + 68, 111 + 68, 22 + 8,//
        43 + 8, 69 + 68, 117 + 68, 93 + 68, 141 + 68, 63 + 68, 46 + 8,
        5, 44 + 8, 20 + 8, 8 + 8, 32 + 8, 56 + 8, 7, // left
        //
        7, 47 + 8, 23 + 8, 11 + 8, 35 + 8, 59 + 8, 1,//
        56 + 8, 76 + 68, 130 + 68, 82 + 68, 106 + 68, 58 + 68, 50 + 8,
        32 + 8, 124 + 68, 28 + 68, 34 + 68, 10 + 68, 136 + 68, 26 + 8,//
        8 + 8, 100 + 68, 52 + 68, 4 + 68, 40 + 68, 88 + 68, 2 + 8,//
        20 + 8, 148 + 68, 22 + 68, 46 + 68, 16 + 68, 112 + 68, 14 + 8,//
        44 + 8, 70 + 68, 118 + 68, 94 + 68, 142 + 68, 64 + 68, 38 + 8,
        5, 41 + 8, 17 + 8, 5 + 8, 29 + 8, 53 + 8, 3, // down
        //
        2, 51 + 8, 27 + 8, 3 + 8, 15 + 8, 39 + 8, 4,//
        52 + 8, 59 + 68, 137 + 68, 89 + 68, 113 + 68, 65 + 68, 55 + 8,
        28 + 8, 107 + 68, 11 + 68, 41 + 68, 17 + 68, 143 + 68, 31 + 8,//
        4 + 8, 83 + 68, 35 + 68, 5 + 68, 47 + 68, 95 + 68, 7 + 8,//
        16 + 8, 131 + 68, 29 + 68, 53 + 68, 23 + 68, 119 + 68, 19 + 8,//
        40 + 8, 77 + 68, 125 + 68, 101 + 68, 149 + 68, 71 + 68, 43 + 8,
        3, 53 + 8, 29 + 8, 5 + 8, 17 + 8, 41 + 8, 5, // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, 1, // right
        //
        0, 1, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 1, 0, // up
        //
        1, 0, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 0, 1, // front
        //
        1, 1, 1, 1, 1, 1, 2, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        2, 1, 1, 1, 1, 1, 1, // left
        //
        0, 1, 1, 1, 1, 1, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, //
        0, 1, 1, 1, 1, 1, 0, // down
        //
        1, 0, 0, 0, 0, 0, 2, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        1, 0, 0, 0, 0, 0, 1, //
        2, 0, 0, 0, 0, 0, 1, // back
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
        return 7*7*6;
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
        for (int partIndex = 0; partIndex < partCount; partIndex++) {
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
    public CubeKind getKind() {
       return CubeKind.CUBE_7;
    }
}
