/*
 * @(#)ProfessorCubeGeom3D.java  6.3  2010-04-05
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import java.awt.Color;
import java.util.*;

/**
 * Geometrical representation of simplified Rubik's Professor Cube in
 * three dimensions.
 *
 * @author Werner Randelshofer, Markus Pirzer
 * @version $Id$
 * <br>6.2 2009-01-04 Added support for twisting the cube by swiping over
 * its faces.
 * <br>6.1 2008-08-19 W. Randelshofer Streamlined with code of VCube7Geom3D.
 * 6.0.1 2008-01-13 W. Randelshofer: Fixed color scheme of attributes
 * created by method createAttributes. 
 * <br>6.0 2008-01-08 W. Randelshofer: Updated to changes in AbstractCube. 
 * <br>5.1 2004-08-21 W. Randelshofer Changed dimensions of parts to match
 * the Rubik's Professor Cube by Oddzon: 7 x 7 x 7 cm.
 * <br>5.0 2004-07-10 M. Pirzer: Cube changed from a standard 3x3x3 Rubik's Cube to a
 * 5x5x5 Rubik's Professor Cube.
 * <br> 1.0 W. Randelshofer 2000-03-11 Created.
 */
public class ProfessorCubeGeom3D extends AbstractProfessorCubeGeom3D {

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
            shapes[cornerOffset + i] = new Shape3D(CORNER_VERTS, CORNER_FACES, new Color[CORNER_FACES.length][2], CORNER_FACES.length - 3);
            shapes[cornerOffset + i].setReduced(true);
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
            shapes[edgeOffset + i] = new Shape3D(EDGE_VERTS, EDGE_FACES, new Color[EDGE_FACES.length][2], EDGE_FACES.length - 4);
            shapes[edgeOffset + i].setReduced(true);
        }
    }
    private static float[] SIDE_VERTS;
    private static int[][] SIDE_FACES;

    @Override
    protected void initSides() {
        if (SIDE_VERTS == null) {
            SIDE_VERTS = new float[]{
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
            shapes[sideOffset + i] = new Shape3D(SIDE_VERTS, SIDE_FACES, new Color[SIDE_FACES.length][2], SIDE_FACES.length - 4);
            shapes[sideOffset + i].setReduced(true);
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

    public String getName() {
        return "Professor Cube";
    }

    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{25, 25, 25, 25, 25, 25});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * 5 * 5, 1 * 5 * 5, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * 5 * 5, 2 * 5 * 5, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * 5 * 5, 3 * 5 * 5, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * 5 * 5, 4 * 5 * 5, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * 5 * 5, 5 * 5 * 5, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * 5 * 5, 6 * 5 * 5, new Color(255, 70, 0)); // Back: Orange

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
     *                     +---+---+---+---+---+
     *                     | 4 |23 |11 |35 | 2 |
     *                     +---+---+---+---+---+
     *                     |26 |51  81  57 |20 |
     *                     +---+           +---+
     *                     |14 |75  45  87 | 8 |
     *                     +---+           +---+
     *                     |38 |69  93  63 |32 |
     *                     +---+---+---+---+---+
     *                     | 6 |29 |17 |41 | 0 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 4 |26 |14 |38 | 6 | 6 |29 |17 |41 | 0 | 0 |32 | 8 |20 | 2 | 2 |35 |11 |23 | 4 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |39 |71  77  53 |42 |42 |58  88  64 |33 |33 |62  92  68 |36 |36 |55  85  61 |39 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |15 |95  47  83 |18 |18 |82  46  94 | 9 | 9 |86  44  74 |12 |12 |79  49  91 |15 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |27 |65  89  59 |30 |30 |52  76  70 |21 |21 |56  80  50 |24 |24 |73  97  67 |27 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 5 |28 |16 |40 | 7 | 7 |31 |19 |43 | 1 | 1 |34 |10 |22 | 3 | 3 |37 |13 |25 | 5 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     | 7 |31 |19 |43 | 1 |
     *                     +---+---+---+---+---+
     *                     |40 |72  78  54 |34 |
     *                     +---+           +---+
     *                     |16 |96  48  84 |10 |
     *                     +---+           +---+
     *                     |28 |66  90  60 |22 |
     *                     +---+---+---+---+---+
     *                     | 5 |25 |13 |37 | 3 |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private final static int[] stickerToPartMap = {
        0, 32, 8, 20, 2, 33, 62, 92, 68, 36, 9, 86, 44, 74, 12, 21, 56, 80, 50, 24, 1, 34, 10, 22, 3, // right
        4, 23, 11, 35, 2, 26, 51, 81, 57, 20, 14, 75, 45, 87, 8, 38, 69, 93, 63, 32, 6, 29, 17, 41, 0, // up
        6, 29, 17, 41, 0, 42, 58, 88, 64, 33, 18, 82, 46, 94, 9, 30, 52, 76, 70, 21, 7, 31, 19, 43, 1, // front
        4, 26, 14, 38, 6, 39, 71, 77, 53, 42, 15, 95, 47, 83, 18, 27, 65, 89, 59, 30, 5, 28, 16, 40, 7, // left
        7, 31, 19, 43, 1, 40, 72, 78, 54, 34, 16, 96, 48, 84, 10, 28, 66, 90, 60, 22, 5, 25, 13, 37, 3, // down
        2, 35, 11, 23, 4, 36, 55, 85, 61, 39, 12, 79, 49, 91, 15, 24, 73, 97, 67, 27, 3, 37, 13, 25, 5, // back
    };

    @Override
    public int getPartIndexForStickerIndex(int stickerIndex) {
        return stickerToPartMap[stickerIndex];
    }
    private final static int[] stickerToFaceMap = {
        1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, // right
        0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, // up
        1, 0, 0, 0, 2, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 2, 0, 0, 0, 1, // front
        1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, // left
        0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, // down
        1, 0, 0, 0, 2, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 2, 0, 0, 0, 1, // back
    };

    @Override
    protected int getPartFaceIndexForStickerIndex(int stickerIndex) {
        return stickerToFaceMap[stickerIndex];
    }

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
        return 5 * 5 * 6;
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
            for (int i = shape.getFaceCount() - 1; i >= 0; i--) {
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

            for (int i = shape.getFaceCount() - 1; i >= offset; i--) {
                shape.setFillColor(i, color);
            }
        }
    }

    @Override
    public CubeKind getKind() {
        return CubeKind.PROFESSOR;
    }
}
