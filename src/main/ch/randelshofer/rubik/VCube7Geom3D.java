/*
 * @(#)VCube7Geom3D.java  1.2  2010-04-05
 *
 * Copyright (c) 2008-2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import java.awt.Color;
import java.util.*;
import static java.lang.Math.*;

/**
 * Geometrical representation of {@link Cube7} as a V-Cube 7 in three dimensions.
 * <p>
 * The faces of a V-Cube 7 are curved. This class constructs the faces
 * from six intersecting spheres.
 *
 * @author Werner Randelshofer
 * @version 1.2 2010-04-05 Added swipe actions to edges adjacent to stickers.
 * <br>1.1 2009-01-04 Added support for twisting the cube by swiping over
 * its faces.
 * <br>1.0 2008-09-16 Created.
 */
public class VCube7Geom3D extends AbstractCube7Geom3D {
    /* The variables SCX, SCY, SCZ define the center of the sphere used for
     * the front face.
     */

    private final static float SCX = 0;
    private final static float SCY = 0;
    private final static float SCZ = PART_LENGTH * 3.5f * -4.5f;
    /* The radius is chosen, so that the sphere touches the four corners of the
     * cube minus the bevel length. Here we use the distance from the left-up-front (luf) corner to center
     * of the sphere to compute the radius.
     */
    private final static float SR = (float) sqrt(
            (SCX - PART_LENGTH * -3.5f + BEVEL_LENGTH) * (SCX - PART_LENGTH * -3.5f + BEVEL_LENGTH) +
            (SCY - PART_LENGTH * 3.5f + BEVEL_LENGTH) * (SCY - PART_LENGTH * 3.5f + BEVEL_LENGTH) +
            (SCZ - PART_LENGTH * 3.5f + BEVEL_LENGTH) * (SCZ - PART_LENGTH * 3.5f + BEVEL_LENGTH));
    /**
     * Corner parts are bigger than regular parts, and edge parts are rectangular.
     */
    private final static float EXT_LENGTH = PART_LENGTH + 2f;
    private final static float CUBE_LENGTH = PART_LENGTH * 3.5f;
    /** Corner vertices */
    private final static float C_LUF = PART_LENGTH + 0f - BEVEL_LENGTH; // touches the sphere
    private final static float C_RUF_LUB_LDF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f); // edge
    private final static float C_RDF_RUB_LDB = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f); // outer ring
    /** Innermost edge vertices */
    private final static float E1_LUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E1_RUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E1_LUB_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E1_RUB_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /** Near inner edge vertices */
    private final static float E2_LUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E2_RUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E2_LUB_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E2_RUB_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /** Near inner edge vertices */
    private final static float E3_LUF = E2_RUF;
    private final static float E3_RUF = E2_LUF;
    private final static float E3_LUB_LDF = E2_RUB_RDF;
    private final static float E3_RUB_RDF = E2_LUB_LDF;
    /** Outermost edge vertices */
    private final static float E4_LUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E4_RUF = PART_LENGTH + 1.41f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 3.5f)) * (SCY - (PART_LENGTH * 3.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E4_LUB_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float E4_RUB_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 2.5f)) * (SCY - (PART_LENGTH * 2.5f)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /** Outermost edge vertices */
    private final static float E5_LUF = E4_RUF;
    private final static float E5_RUF = E4_LUF;
    private final static float E5_LUB_LDF = E4_RUB_RDF;
    private final static float E5_RUB_RDF = E4_LUB_LDF;
    /** Central side part */
    private final static float S1_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S1_RUF = S1_LUF;
    private final static float S1_LDF = S1_LUF;
    private final static float S1_RDF = S1_LUF;
    /** Inner ring corners ldf */
    private final static float S2_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S2_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S2_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S2_RUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /** Inner ring corners luf */
    private final static float S3_LUF = S2_LDF;
    private final static float S3_RUF = S2_RDF;
    private final static float S3_LDF = S2_LUF;
    private final static float S3_RDF = S2_RUF;
    /** Inner ring corners ruf */
    private final static float S4_LUF = S2_RDF;
    private final static float S4_RUF = S2_LDF;
    private final static float S4_LDF = S2_RUF;
    private final static float S4_RDF = S2_LUF;
    /** Inner ring corners rdf */
    private final static float S5_LUF = S2_RUF;
    private final static float S5_RUF = S2_LUF;
    private final static float S5_LDF = S2_RDF;
    private final static float S5_RDF = S2_LDF;
    /** Inner ring edges */
    /* side edge d */
    private final static float S6_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S6_RUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S6_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S6_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /* side edge l */
    private final static float S7_LUF = S6_LDF;
    private final static float S7_RUF = S6_LUF;
    private final static float S7_LDF = S6_RDF;
    private final static float S7_RDF = S6_RUF;
    /* side edge u */
    private final static float S8_LUF = S6_LDF;
    private final static float S8_RUF = S6_RDF;
    private final static float S8_LDF = S6_LUF;
    private final static float S8_RDF = S6_RUF;
    /* side edge r */
    private final static float S9_LUF = S6_RUF;
    private final static float S9_RUF = S6_RDF;
    private final static float S9_LDF = S6_LUF;
    private final static float S9_RDF = S6_LDF;
    /** Outer ring corners ldf */
    private final static float S10_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S10_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S10_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S10_RUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /** Outer ring corners luf */
    private final static float S11_LUF = S10_LDF;
    private final static float S11_RUF = S10_RDF;
    private final static float S11_LDF = S10_LUF;
    private final static float S11_RDF = S10_RUF;
    /** Inner ring corners ruf */
    private final static float S12_LUF = S10_RDF;
    private final static float S12_RUF = S10_LDF;
    private final static float S12_LDF = S10_RUF;
    private final static float S12_RDF = S10_LUF;
    /** Inner ring corners rdf */
    private final static float S13_LUF = S10_RUF;
    private final static float S13_RUF = S10_LUF;
    private final static float S13_LDF = S10_RDF;
    private final static float S13_RDF = S10_LDF;
    /** Outer ring central edges */
    private final static float S14_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S14_RUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S14_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S14_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * 0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /* side edge l */
    private final static float S15_LUF = S14_LDF;
    private final static float S15_RUF = S14_LUF;
    private final static float S15_LDF = S14_RDF;
    private final static float S15_RDF = S14_RUF;
    /* side edge u */
    private final static float S16_LUF = S14_LDF;
    private final static float S16_RUF = S14_RDF;
    private final static float S16_LDF = S14_LUF;
    private final static float S16_RDF = S14_RUF;
    /* side edge r */
    private final static float S17_LUF = S14_RUF;
    private final static float S17_RUF = S14_RDF;
    private final static float S17_LDF = S14_LUF;
    private final static float S17_RDF = S14_LDF;
    /** Outer ring lateral edges */
    /* side edge d */
    private final static float S18_LUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S18_RUF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -1.5f - BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S18_LDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -1.5f + BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    private final static float S18_RDF = PART_LENGTH + 2f * (float) (sqrt(abs(
            (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) * (SCX - (PART_LENGTH * -0.5f - BEVEL_LENGTH)) +
            (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) * (SCY - (PART_LENGTH * -2.5f + BEVEL_LENGTH)) -
            SR * SR)) + SCZ - PART_LENGTH * 3.5f);
    /* side edge l */
    private final static float S19_LUF = S18_LDF;
    private final static float S19_RUF = S18_LUF;
    private final static float S19_LDF = S18_RDF;
    private final static float S19_RDF = S18_RUF;
    /* side edge u */
    private final static float S20_LUF = S18_RDF;
    private final static float S20_RUF = S18_LDF;
    private final static float S20_LDF = S18_RUF;
    private final static float S20_RDF = S18_LUF;
    /* side edge r */
    private final static float S21_LUF = S18_RUF;
    private final static float S21_RUF = S18_RDF;
    private final static float S21_LDF = S18_LUF;
    private final static float S21_RDF = S18_LDF;
    /* side edge d */
    private final static float S22_LUF = S18_RUF;
    private final static float S22_RUF = S18_LUF;
    private final static float S22_LDF = S18_RDF;
    private final static float S22_RDF = S18_LDF;
    /* side edge l */
    private final static float S23_LUF = S22_LDF;
    private final static float S23_RUF = S22_LUF;
    private final static float S23_LDF = S22_RDF;
    private final static float S23_RDF = S22_RUF;
    /* side edge u */
    private final static float S24_LUF = S22_RDF;
    private final static float S24_RUF = S22_LDF;
    private final static float S24_LDF = S22_RUF;
    private final static float S24_RDF = S22_LUF;
    /* side edge r */
    private final static float S25_LUF = S22_RUF;
    private final static float S25_RUF = S22_RDF;
    private final static float S25_LDF = S22_LUF;
    private final static float S25_RDF = S22_LDF;
    private final static int STICKER_COUNT = 6 * 7 * 7;
    private static float[] CORNER_VERTS;
    private static int[][] CORNER_FACES;

    @Override
    protected void initCorners() {
        if (CORNER_VERTS == null) {
            // The corner parts are smaller than the side parts and curved 
            CORNER_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(C_LUF * 0.5f - BEVEL_LENGTH), (C_LUF * 0.5f - BEVEL_LENGTH), C_LUF * 0.5f,
                        -(C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), C_RUF_LUB_LDF * 0.5f,
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), C_RUF_LUB_LDF * 0.5f,
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), C_RDF_RUB_LDB * 0.5f,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        -(C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        -(C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f,
                        //8:lluf      lldf       rruf      rrdf
                        -C_LUF * 0.5f, (C_LUF * 0.5f - BEVEL_LENGTH), (C_LUF * 0.5f - BEVEL_LENGTH),
                        -C_RUF_LUB_LDF * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        PART_LENGTH * 0.5f, (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        PART_LENGTH * 0.5f, (C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -C_RUF_LUB_LDF * 0.5f, (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -C_RDF_RUB_LDB * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(C_LUF * 0.5f - BEVEL_LENGTH), C_LUF * 0.5f, (C_LUF * 0.5f - BEVEL_LENGTH),
                        -(C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), C_RUF_LUB_LDF * 0.5f, (C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, (C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), C_RDF_RUB_LDB * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(C_RUF_LUB_LDF * 0.5f - BEVEL_LENGTH), C_RUF_LUB_LDF * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(C_RDF_RUB_LDB * 0.5f - BEVEL_LENGTH), -PART_LENGTH * 0.5f, -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
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
    private static float[] EDGE1_VERTS;
    private static float[] EDGE2_VERTS;
    private static float[] EDGE3_VERTS;
    private static float[] EDGE4_VERTS;
    private static float[] EDGE5_VERTS;
    private static int[][] EDGE_FACES;

    @Override
    protected void initEdges() {
        if (EDGE1_VERTS == null) {
            // The edge parts are curved depending on their distance to the corners of the cube
            EDGE1_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUF * 0.5f - BEVEL_LENGTH), (E1_LUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUB_LDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUF * 0.5f - BEVEL_LENGTH), (E1_RUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUB_RDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (E1_LUF * 0.5f - BEVEL_LENGTH), (E1_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (E1_RUF * 0.5f - BEVEL_LENGTH), (E1_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (E1_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (E1_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUF * 0.5f), (E1_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E1_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUF * 0.5f), (E1_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E1_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_RUB_RDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E1_LUB_LDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
            EDGE2_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUF * 0.5f - BEVEL_LENGTH), (E2_LUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUB_LDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUF * 0.5f - BEVEL_LENGTH), (E2_RUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUB_RDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (E2_LUF * 0.5f - BEVEL_LENGTH), (E2_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (E2_RUF * 0.5f - BEVEL_LENGTH), (E2_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (E2_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (E2_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUF * 0.5f), (E2_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E2_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUF * 0.5f), (E2_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E2_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_RUB_RDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E2_LUB_LDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
            EDGE3_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUF * 0.5f - BEVEL_LENGTH), (E3_LUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUB_LDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUF * 0.5f - BEVEL_LENGTH), (E3_RUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUB_RDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (E3_LUF * 0.5f - BEVEL_LENGTH), (E3_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (E3_RUF * 0.5f - BEVEL_LENGTH), (E3_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (E3_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (E3_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUF * 0.5f), (E3_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E3_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUF * 0.5f), (E3_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E3_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_RUB_RDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E3_LUB_LDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
            EDGE4_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUF * 0.5f - BEVEL_LENGTH), (E4_LUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUB_LDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUF * 0.5f - BEVEL_LENGTH), (E4_RUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUB_RDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (E4_LUF * 0.5f - BEVEL_LENGTH), (E4_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (E4_RUF * 0.5f - BEVEL_LENGTH), (E4_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (E4_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (E4_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUF * 0.5f), (E4_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E4_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUF * 0.5f), (E4_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E4_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_RUB_RDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E4_LUB_LDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH)
                    };
            EDGE5_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUF * 0.5f - BEVEL_LENGTH), (E5_LUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUB_LDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUF * 0.5f - BEVEL_LENGTH), (E5_RUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUB_RDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f),
                        //8:lluf      lldf       rruf      rrdf
                        -(PART_LENGTH * 0.5f), (E5_LUF * 0.5f - BEVEL_LENGTH), (E5_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (E5_RUF * 0.5f - BEVEL_LENGTH), (E5_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (E5_RUB_RDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (E5_LUB_LDF * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        //16:luuf     lddf       ruuf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUF * 0.5f), (E5_LUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E5_LUB_LDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUF * 0.5f), (E5_RUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (E5_RUB_RDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_RUB_RDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (E5_LUB_LDF * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH),
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

        float[] verts;
        for (int part = 0; part < edgeCount; part++) {
            switch (part / 12) {
                case 0:
                    verts = EDGE1_VERTS;
                    break;
                case 1:
                    switch (part % 12) {
                        case 0:
                        case 1:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                            verts = EDGE2_VERTS;
                            break;
                        default:
                            verts = EDGE3_VERTS;
                            break;
                    }
                    break;
                case 2:
                    switch (part % 12) {
                        case 0:
                        case 1:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                            verts = EDGE3_VERTS;
                            break;
                        default:
                            verts = EDGE2_VERTS;
                            break;
                    }
                    break;
                case 3:
                    switch (part % 12) {
                        case 0:
                        case 1:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                            verts = EDGE4_VERTS;
                            break;
                        default:
                            verts = EDGE5_VERTS;
                            break;
                    }
                    break;
                case 4:
                default:
                    switch (part % 12) {
                        case 0:
                        case 1:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                            verts = EDGE5_VERTS;
                            break;
                        default:
                            verts = EDGE4_VERTS;
                            break;
                    }
                    break;
            }
            shapes[edgeOffset + part] = new Shape3D(verts, EDGE_FACES, new Color[EDGE_FACES.length][2], EDGE_FACES.length - 4);
            shapes[edgeOffset + part].setReduced(true);
        }
    }
    private static float[] SIDE1_VERTS;
    private static float[] SIDE2_VERTS;
    private static float[] SIDE3_VERTS;
    private static float[] SIDE4_VERTS;
    private static float[] SIDE5_VERTS;
    private static float[] SIDE6_VERTS;
    private static float[] SIDE7_VERTS;
    private static float[] SIDE8_VERTS;
    private static float[] SIDE9_VERTS;
    private static float[] SIDE10_VERTS;
    private static float[] SIDE11_VERTS;
    private static float[] SIDE12_VERTS;
    private static float[] SIDE13_VERTS;
    private static float[] SIDE14_VERTS;
    private static float[] SIDE15_VERTS;
    private static float[] SIDE16_VERTS;
    private static float[] SIDE17_VERTS;
    private static float[] SIDE18_VERTS;
    private static float[] SIDE19_VERTS;
    private static float[] SIDE20_VERTS;
    private static float[] SIDE21_VERTS;
    private static float[] SIDE22_VERTS;
    private static float[] SIDE23_VERTS;
    private static float[] SIDE24_VERTS;
    private static float[] SIDE25_VERTS;
    private static int[][] SIDE_FACES;

    @Override
    protected void initSides() {
        if (SIDE1_VERTS == null) {
            // The side parts are curved depending on their distance to the corners of the cube.
            SIDE1_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S1_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S1_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S1_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S1_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S1_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE2_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S2_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S2_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S2_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S2_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S2_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE3_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S3_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S3_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S3_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S3_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S3_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE4_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S4_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S4_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S4_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S4_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S4_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE5_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S5_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S5_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S5_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S5_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S5_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE6_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S6_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S6_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S6_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S6_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S6_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE7_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S7_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S7_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S7_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S7_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S7_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE8_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S8_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S8_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S8_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S8_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S8_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE9_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S9_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S9_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S9_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S9_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S9_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE10_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S10_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S10_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S10_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S10_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S10_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE11_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S11_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S11_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S11_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S11_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S11_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE12_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S12_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S12_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S12_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S12_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S12_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE13_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S13_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S13_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S13_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S13_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S13_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE14_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S14_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S14_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S14_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S14_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S14_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE15_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S15_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S15_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S15_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S15_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S15_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE16_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S16_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S16_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S16_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S16_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S16_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE17_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S17_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S17_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S17_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S17_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S17_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE18_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S18_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S18_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S18_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S18_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S18_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE19_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S19_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S19_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S19_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S19_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S19_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE20_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S20_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S20_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S20_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S20_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S20_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE21_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S21_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S21_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S21_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S21_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S21_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE22_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S22_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S22_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S22_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S22_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S22_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE23_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S23_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S23_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S23_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S23_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S23_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE24_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S24_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S24_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S24_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S24_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S24_LDF * 0.5f - BEVEL_LENGTH),
                        //20:ruub,    rddb,       luub,       lddb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f + BEVEL_LENGTH)
                    };
            SIDE25_VERTS = new float[]{
                        //0:ruff      luff       ldff       rdff
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_RUF * 0.5f),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_RDF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_LUF * 0.5f),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_LDF * 0.5f),
                        //4:rubb,    rdbb,       lubb,       ldbb
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //8:rruf      lluf       lldf      rrdf
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (S25_LDF * 0.5f - BEVEL_LENGTH),
                        //12:rrub,    rrdb,      llub,      lldb
                        (PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f), -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f + BEVEL_LENGTH),
                        //16:ruuf     luuf       lddf       rddf
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S25_RUF * 0.5f - BEVEL_LENGTH),
                        -(PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S25_RDF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), (PART_LENGTH * 0.5f), (S25_LUF * 0.5f - BEVEL_LENGTH),
                        (PART_LENGTH * 0.5f - BEVEL_LENGTH), -(PART_LENGTH * 0.5f), (S25_LDF * 0.5f - BEVEL_LENGTH),
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

        float[] verts;
        for (int part = 0; part < sideCount; part++) {
            switch (part / 6) {
                case 0: // middle
                    verts = SIDE1_VERTS;
                    break;
                    
                case 1: // inner ldf
                    verts = SIDE5_VERTS;
                    break;
                case 2: // inner luf
                    verts = SIDE4_VERTS;
                    break;
                case 3: // inner ruf
                    verts = SIDE3_VERTS;
                    break;
                case 4: // inner rdf
                    verts = SIDE2_VERTS;
                    break;
                    
                case 5: // inner d
                    verts = SIDE6_VERTS;
                    break;
                case 6: // inner l
                    verts = SIDE9_VERTS;
                    break;
                case 7: // inner u
                    verts = SIDE8_VERTS;
                    break;
                case 8: // inner r
                    verts = SIDE7_VERTS;
                    break;
                    
                case 9: // outer ldf
                    verts = SIDE13_VERTS;
                    break;
                case 10: // outer luf
                    verts = SIDE12_VERTS;
                    break;
                case 11: // outer ruf
                    verts = SIDE11_VERTS;
                    break;
                case 12: // outer rdf
                    verts = SIDE10_VERTS;
                    break;
                    
                case 13: // outer d
                    verts = SIDE14_VERTS;
                    break;
                case 14: // outer l
                    verts = SIDE17_VERTS;
                    break;
                case 15: // outer u
                    verts = SIDE16_VERTS;
                    break;
                case 16: // outer r
                    verts = SIDE15_VERTS;
                    break;
                    
                case 21: // outer d
                    verts = SIDE18_VERTS;
                    break;
                case 22: // outer l
                    verts = SIDE21_VERTS;
                    break;
                case 23: // outer u
                    verts = SIDE20_VERTS;
                    break;
                case 24: // outer r
                    verts = SIDE19_VERTS;
                    break;
                    
                case 17: // outer d
                    verts = SIDE22_VERTS;
                    break;
                case 18: // outer l
                    verts = SIDE25_VERTS;
                    break;
                case 19: // outer u
                    verts = SIDE24_VERTS;
                    break;
                case 20: // outer r
                default:
                    verts = SIDE23_VERTS;
                    break;
            }
            shapes[sideOffset + part] = new Shape3D(verts, SIDE_FACES, new Color[SIDE_FACES.length][2], SIDE_FACES.length - 4);
            shapes[sideOffset + part].setReduced(true);
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
        return "V-Cube 7";
    }

    @Override
    protected CubeAttributes createAttributes() {
        DefaultCubeAttributes a = new DefaultCubeAttributes(partCount, getStickerCount(),
                new int[]{layerCount * layerCount, layerCount * layerCount, layerCount * layerCount, layerCount * layerCount, layerCount * layerCount, layerCount * layerCount});
        Color[] partsFillColor = new Color[partCount];
        Color[] partsOutlineColor = new Color[partCount];
        Color[] stickersFillColor = new Color[getStickerCount()];

        Arrays.fill(partsFillColor, 0, partCount - 1, new Color(24, 24, 24));
        Arrays.fill(partsOutlineColor, 0, partCount - 1, new Color(16, 16, 16));
        Arrays.fill(partsFillColor, centerOffset, partCount, new Color(240, 240, 240));
        Arrays.fill(partsOutlineColor, centerOffset, partCount, new Color(240, 240, 240));

        Arrays.fill(stickersFillColor, 0 * layerCount * layerCount, 1 * layerCount * layerCount, new Color(255, 210, 0)); // Right: Yellow
        Arrays.fill(stickersFillColor, 1 * layerCount * layerCount, 2 * layerCount * layerCount, new Color(0, 51, 115)); // Up: Blue
        Arrays.fill(stickersFillColor, 2 * layerCount * layerCount, 3 * layerCount * layerCount, new Color(140, 0, 15)); // Front: Red
        Arrays.fill(stickersFillColor, 3 * layerCount * layerCount, 4 * layerCount * layerCount, new Color(248, 248, 248)); // Left: White
        Arrays.fill(stickersFillColor, 4 * layerCount * layerCount, 5 * layerCount * layerCount, new Color(0, 115, 47)); // Down: Green
        Arrays.fill(stickersFillColor, 5 * layerCount * layerCount, 6 * layerCount * layerCount, new Color(255, 70, 0)); // Back: Orange

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
            if (stickerToPartMap[sticker] == part && stickerToFaceMap[sticker] == orientation) {
                break;
            }
        }
        return sticker;
    }

    @Override
    public int getStickerCount() {
        return 7 * 7 * 6;
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
       return CubeKind.CUBE_7;
    }
}
