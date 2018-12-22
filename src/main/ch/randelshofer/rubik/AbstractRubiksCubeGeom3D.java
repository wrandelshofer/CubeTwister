/* @(#)AbstractRubiksCubeFlat3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import org.monte.media.*;
import java.awt.*;
import javax.swing.SwingUtilities;

/**
 * Abstract base class for the geometrical representation of a {@link RubiksCube}
 * using the Geom3D engine.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>7.0 2008-01-06 Adapted to changes in AbstractCube.
 * <br>6.1 2007-09-09 Use SplineInterpolator to animat twists. 
 * <br>6.0 2005-03-06 Reworked.
 * <br>1.3.1 2003-08-04 CubeEvent's always reported a stickerIndex of 0.
 * <br>1.3 2003-07-17 Method computeTransformation() must take orientation
 * changes of the center part into account.
 * <br>1.2.1 2002-12-27 Animations must not stop in the middle.
 * <br>1.2 2002-06-29 RubiksCube replaced by RubiksCubeCore.
 * <br>1.1.3 2002-05-18 Class EventDispatcher replaced by RunnableDispatcher.
 * Class EventWorker replaced by RunnableWorker.
 * <br>1.1.2 2002-02-07 Method FaceAction.actionPerformed mustn't call
 * getPartFace() when the orientation attribute is -1.
 * <br>1.1.1 2001-11-11 ColorModel3D was not properly initialized.
 * <br> 1.1 2001-09-26 Uses now the Arxon Color scheme. Edge parts
 * are now correctly flipped.
 * <br>1.0 2001-08-04 Objects are now cloneable.
 * <br>0.5.1 2001-06-25 Adapted for JDK 1.3.
 * <br>0.5 2000-03-04
 */
public abstract class AbstractRubiksCubeGeom3D extends AbstractCubeGeom3D {

    public final static double[][] CORNER_EXPLODE_TRANSLATION = {
        {-1, 1, 1}, // left up front
        {-1, -1, 1}, // left down front
        {1, 1, 1}, // right up front
        {1, -1, 1}, // right down front

        {1, 1, -1}, // right up back
        {1, -1, -1}, // right down back
        {-1, 1, -1}, // left up back
        {-1, -1, -1} // left down back
    };
    public final static double[][] EDGE_EXPLODE_TRANSLATION = {
        {0, 1, 1}, // up front
        {-1, 0, 1}, // left front
        {0, -1, 1}, // down front

        {1, 1, 0}, // right up
        {1, 0, 1}, // right front
        {1, -1, 0}, // right down

        {0, 1, -1}, // up back
        {1, 0, -1}, // right back
        {0, -1, -1}, // down back

        {-1, 1, 0}, // left up
        {-1, 0, -1}, // left back
        {-1, -1, 0} // left down
    };
    public final static double[][] SIDE_EXPLODE_TRANSLATION = {
        {0, 0, 1}, // front
        {1, 0, 0}, // right
        {0, -1, 0}, // down
        {0, 0, -1}, // back
        {-1, 0, 0}, // left
        {0, 1, 0} // top
    };

    public AbstractRubiksCubeGeom3D() {
        super(3, 8, 12, 6, 1);
        init();
    }

    @Override
    protected void computeTransformation() {
        Cube cube = getCube();

        synchronized (getLock()) {
            Transform3D t, t2;
            int loc;

            // Transform the scene
            t = scene.getTransform();
            t.setToIdentity();
            CubeAttributes a = getAttributes();
            double scale = a.getScaleFactor();
            t.scale(scale, scale, scale);
            t.rotateY(a.getBeta());
            t.rotateX(a.getAlpha());

            double explosion = 18d * a.getExplosionFactor();

            // Transform the part at the center of the cube
            t = transforms[centerOffset].getTransform();
            t.setToIdentity();

            switch (cube.getCubeOrientation()) {
                case -1: // Unknow or illegal orientation
                case 0: // Front at front, Right at right
                    // nothing to do
                    break;
                case 1: // Bottom, Right, CR
                    t.rotateX(Math.PI / 2);
                    break;
                case 2: // Back, Right, CR2
                    t.rotateX(Math.PI);
                    break;
                case 3: // Top, Right, CR'
                    t.rotateX(Math.PI / -2);
                    break;
                case 4: // Right, Back, CU
                    t.rotateY(Math.PI / 2);
                    break;
                case 5: // Back, Left, CU2
                    t.rotateY(Math.PI);
                    break;
                case 6: // Left, Front, CU'
                    t.rotateY(Math.PI / -2);
                    break;
                case 7: // Front, Top, CF
                    t.rotateZ(Math.PI / 2);
                    break;
                case 8: // Front, Left, CF2
                    t.rotateZ(Math.PI);
                    break;
                case 9: // Front, Bottom, CF'
                    t.rotateZ(Math.PI / -2);
                    break;
                case 10: // Right, Top, CR CU
                    t.rotateX(Math.PI / 2);
                    t.rotateY(Math.PI / 2);
                    break;
                case 11: // Top, Left, CR CU2
                    t.rotateX(Math.PI / 2);
                    t.rotateY(Math.PI);
                    break;
                case 12: // Left, Down, CR CU'
                    t.rotateX(Math.PI / 2);
                    t.rotateY(Math.PI / -2);
                    break;
                case 13: // Right, Front, CR2 CU
                    t.rotateX(Math.PI);
                    t.rotateY(Math.PI / 2);
                    break;
                case 14: // Left, Back, CR2 CU'
                    t.rotateX(Math.PI);
                    t.rotateY(Math.PI / -2);
                    break;
                case 15: // Right, Down, CR' CU
                    t.rotateX(Math.PI / -2);
                    t.rotateY(Math.PI / 2);
                    break;
                case 16: // Down, Left, CR' CU2
                    t.rotateX(Math.PI / -2);
                    t.rotateY(Math.PI);
                    break;
                case 17: // Left, Up, CR' CU'
                    t.rotateX(Math.PI / -2);
                    t.rotateY(Math.PI / -2);
                    break;
                case 18: // Down, Up, CR CF
                    t.rotateX(Math.PI / 2);
                    t.rotateZ(Math.PI / 2);
                    break;
                case 19: // Down, Back, CR CF'
                    t.rotateX(Math.PI / 2);
                    t.rotateZ(Math.PI / -2);
                    break;
                case 20: // Back, Down, CR2 CF
                    t.rotateX(Math.PI);
                    t.rotateZ(Math.PI / 2);
                    break;
                case 21: // Back, Up, CR2 CF'
                    t.rotateX(Math.PI);
                    t.rotateZ(Math.PI / -2);
                    break;
                case 22: // Up, Back, CR' CF
                    t.rotateX(Math.PI / -2);
                    t.rotateZ(Math.PI / 2);
                    break;
                case 23: // Up, Front, CR' CF'
                    t.rotateX(Math.PI / -2);
                    t.rotateZ(Math.PI / -2);
                    break;
                default:
                    break;
            }


            // Transform the six parts at each side of the cube
            for (int i = 0; i < sideCount; i++) {
                loc = cube.getSideLocation(i);
                int ori = cube.getSideOrientation(i);
                t = new Transform3D();
                t.rotateZ(ori * Math.PI / -2d);
                t.concatenate(identityTransforms[sideOffset + loc]);
//                t = (Transform3D) identityTransforms[sideOffset+loc].clone();
                t.translate(
                        SIDE_EXPLODE_TRANSLATION[loc][0] * explosion,
                        SIDE_EXPLODE_TRANSLATION[loc][1] * explosion,
                        SIDE_EXPLODE_TRANSLATION[loc][2] * explosion);
                transforms[sideOffset + i].setTransform(t);
            }


            // Transform the twelve edge parts
            for (int i = 0; i < edgeCount; i++) {
                loc = cube.getEdgeLocation(i);
                t = transforms[edgeOffset + i].getTransform();
                t.setToIdentity();
                if (getCube().getEdgeOrientation(i) == 1) {
                    t.rotateZ(Math.PI);
                    t.rotateX(Math.PI / 2);
                }
                t2 = (Transform3D) identityTransforms[edgeOffset + loc].clone();
                t2.translate(
                        EDGE_EXPLODE_TRANSLATION[loc][0] * explosion,
                        EDGE_EXPLODE_TRANSLATION[loc][1] * explosion,
                        EDGE_EXPLODE_TRANSLATION[loc][2] * explosion);
                t.concatenate(t2);
            }

            // Transform the eight corner parts
            for (int i = 0; i < cornerCount; i++) {
                loc = cube.getCornerLocation(i);
                t = transforms[cornerOffset + i].getTransform();
                t.setToIdentity();
                switch (cube.getCornerOrientation(i)) {
                    case 0:
                        break;
                    case 1:
                        t.rotateZ(Math.PI / -2);
                        t.rotateX(Math.PI / 2);
                        break;
                    case 2:
                        t.rotate(Math.PI / -2, 0, Math.PI / 2);
                        break;
                }
                t2 = (Transform3D) identityTransforms[cornerOffset + loc].clone();
                t2.translate(
                        CORNER_EXPLODE_TRANSLATION[loc][0] * explosion,
                        CORNER_EXPLODE_TRANSLATION[loc][1] * explosion,
                        CORNER_EXPLODE_TRANSLATION[loc][2] * explosion);

                t.concatenate(t2);
            }
        }
    }
    private static float[] SIDE_VERTS;
    private static int[][] SIDE_FACES;

    @Override
    protected void initSides() {
        if (SIDE_VERTS == null) {
            SIDE_VERTS = new float[]{
                        //0:luff      ldff       ruff       rdff
                        -8, 8, 9, -8, -8, 9, 8, 8, 9, 8, -8, 9,
                        //4:rubb,    rdbb,       lubb,       ldbb
                        8, 8, -1, 8, -8, -1, -8, 8, -1, -8, -8, -1,
                        //8:lluf      lldf       rruf      rrdf
                        -9, 8, 8, -9, -8, 8, 9, 8, 8, 9, -8, 8,
                        //12:rrub,    rrdb,      llub,      lldb
                        9, 8, 0, 9, -8, 0, -9, 8, 0, -9, -8, 0,
                        //16:luuf     lddf       ruuf       rddf
                        -8, 9, 8, -8, -9, 8, 8, 9, 8, 8, -9, 8,
                        //20:ruub,    rddb,       luub,       lddb
                        8, 9, 0, 8, -9, 0, -8, 9, 0, -8, -9, 0,
                        //24
                        2, 4.5f, -1, 4.5f, 2, -1, 4.5f, -2, -1, 2, -4.5f, -1, -2, -4.5f, -1, -4.5f, -2, -1, -4.5f, 2, -1, -2, 4.5f, -1,
                        //32
                        2, 4.5f, -9, 4.5f, 2, -9, 4.5f, -2, -9, 2, -4.5f, -9, -2, -4.5f, -9, -4.5f, -2, -9, -4.5f, 2, -9, -2, 4.5f, -9,};
        }
        if (SIDE_FACES == null) {
            SIDE_FACES = new int[][]{
                        // Faces with stickers and with outlines
                        //--------------------------------------
                        {0, 2, 3, 1}, //Front

                        // Faces with outlines
                        // (all faces which have outlines must be
                        // at the beginning, this is relevant
                        // for method updatePartsOutlineColor()
                        //--------------------------------------
                        {16, 22, 20, 18}, //Top
                        {14, 8, 9, 15}, //Left
                        {12, 13, 11, 10}, //Right
                        {17, 19, 21, 23}, //Bottom
                        {4, 6, 7, 5}, //Back

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
                        // Faces without outlines
                        //--------------------------------------
                        // Inner edges of the main cubicle. We assign swipe actions to these.
                        {16, 18, 2, 0}, //Top Front
                        {1, 3, 19, 17}, //Bottom Front
                        {2, 10, 11, 3}, //Front Right rdff ruff rruf rrdf
                        {8, 0, 1, 9}, //Front Left

                        // Outer edges of the main cubicle. We assign no actions to these.
                        {18, 20, 12, 10}, //Top Right
                        {20, 22, 6, 4}, //Top Back
                        {22, 16, 8, 14}, //Top Left

                        {21, 19, 11, 13}, //Bottom Right rddb rddf rrdf rrdb
                        {23, 21, 5, 7}, //Bottom Back lddb rddb rdbb ldbb
                        {17, 23, 15, 9}, //Bottom Left lddf lddb lldb lldf

                        {4, 5, 13, 12}, //Back Right
                        {7, 6, 14, 15}, //Back Left

                        {17, 9, 1}, //Bottom Left Front lddf lldf ldff
                        {19, 3, 11}, //Bottom Front Right  rddf rdff rrdf
                        {23, 7, 15}, //Bottom Back Left lddb ldbb lldb
                        {21, 13, 5}, //Bottom Right Back rddb rrdb rdbb

                        {16, 0, 8}, //Top Front Left luuf luff lluf
                        {18, 10, 2}, //Top Right Front ruuf rruf ruff
                        {22, 14, 6}, //Top Left Back luub llub lubb
                        {20, 4, 12}, //Top Back Right ruub rubb rrub
                    };
        }
        for (int i = 0; i < sideCount; i++) {
            shapes[sideOffset + i] = new Shape3D(SIDE_VERTS, SIDE_FACES, new Color[SIDE_FACES.length][2]);
        }
    }
    private static float[] CENTER_VERTS;
    private static int[][] CENTER_FACES;

    @Override
    protected void initCenter() {
        if (CENTER_VERTS == null) {
            CENTER_VERTS = new float[]{
                        //0
                        2, 4.5f, 9, 4.5f, 2, 9, 4.5f, -2, 9, 2, -4.5f, 9, -2, -4.5f, 9, -4.5f, -2, 9, -4.5f, 2, 9, -2, 4.5f, 9,
                        //8
                        2, 4.5f, 4.5f, 4.5f, 2, 4.5f, 4.5f, -2, 4.5f, 2, -4.5f, 4.5f, -2, -4.5f, 4.5f, -4.5f, -2, 4.5f, -4.5f, 2, 4.5f, -2, 4.5f, 4.5f,
                        //16
                        2, 4.5f, -4.5f, 4.5f, 2, -4.5f, 4.5f, -2, -4.5f, 2, -4.5f, -4.5f, -2, -4.5f, -4.5f, -4.5f, -2, -4.5f, -4.5f, 2, -4.5f, -2, 4.5f, -4.5f,
                        //24
                        2, 4.5f, -9, 4.5f, 2, -9, 4.5f, -2, -9, 2, -4.5f, -9, -2, -4.5f, -9, -4.5f, -2, -9, -4.5f, 2, -9, -2, 4.5f, -9,
                        //32
                        -4.5f, 2, 4.5f, -4.5f, 4.5f, 2, -4.5f, 4.5f, -2, -4.5f, 2, -4.5f, -4.5f, -2, -4.5f, -4.5f, -4.5f, -2, -4.5f, -4.5f, 2, -4.5f, -2, 4.5f,
                        //40
                        -9, 2, 4.5f, -9, 4.5f, 2, -9, 4.5f, -2, -9, 2, -4.5f, -9, -2, -4.5f, -9, -4.5f, -2, -9, -4.5f, 2, -9, -2, 4.5f,
                        //48
                        9, 2, 4.5f, 9, 4.5f, 2, 9, 4.5f, -2, 9, 2, -4.5f, 9, -2, -4.5f, 9, -4.5f, -2, 9, -4.5f, 2, 9, -2, 4.5f,
                        //56
                        4.5f, 2, 4.5f, 4.5f, 4.5f, 2, 4.5f, 4.5f, -2, 4.5f, 2, -4.5f, 4.5f, -2, -4.5f, 4.5f, -4.5f, -2, 4.5f, -4.5f, 2, 4.5f, -2, 4.5f,
                        //64
                        2, 4.5f, 4.5f, 4.5f, 4.5f, 2, 4.5f, 4.5f, -2, 2, 4.5f, -4.5f, -2, 4.5f, -4.5f, -4.5f, 4.5f, -2, -4.5f, 4.5f, 2, -2, 4.5f, 4.5f,
                        //72
                        2, 9, 4.5f, 4.5f, 9, 2, 4.5f, 9, -2, 2, 9, -4.5f, -2, 9, -4.5f, -4.5f, 9, -2, -4.5f, 9, 2, -2, 9, 4.5f,
                        //80
                        2, -9, 4.5f, 4.5f, -9, 2, 4.5f, -9, -2, 2, -9, -4.5f, -2, -9, -4.5f, -4.5f, -9, -2, -4.5f, -9, 2, -2, -9, 4.5f,
                        //88
                        2, -4.5f, 4.5f, 4.5f, -4.5f, 2, 4.5f, -4.5f, -2, 2, -4.5f, -4.5f, -2, -4.5f, -4.5f, -4.5f, -4.5f, -2, -4.5f, -4.5f, 2, -2, -4.5f, 4.5f,};
        }
        if (CENTER_FACES == null) {
            CENTER_FACES = new int[][]{
                        // front axis
                        {0, 1, 2, 3, 4, 5, 6, 7},
                        // right axis
                        {48, 49, 50, 51, 52, 53, 54, 55},
                        // bottom axis
                        {80, 81, 82, 83, 84, 85, 86, 87},
                        // back axis
                        {31, 30, 29, 28, 27, 26, 25, 24},
                        // left axis
                        {47, 46, 45, 44, 43, 42, 41, 40},
                        // top axis
                        {79, 78, 77, 76, 75, 74, 73, 72},
                        {15, 14, 33},
                        {9, 8, 57},
                        {11, 10, 62},
                        {13, 12, 38},
                        {34, 22, 23},
                        {58, 16, 17},
                        {61, 18, 19},
                        {37, 20, 21},
                        {0, 8, 9, 1},
                        {1, 9, 10, 2},
                        {2, 10, 11, 3},
                        {3, 11, 12, 4},
                        {4, 12, 13, 5},
                        {5, 13, 14, 6},
                        {6, 14, 15, 7},
                        {7, 15, 8, 0},
                        {16, 24, 25, 17},
                        {17, 25, 26, 18},
                        {18, 26, 27, 19},
                        {19, 27, 28, 20},
                        {20, 28, 29, 21},
                        {21, 29, 30, 22},
                        {22, 30, 31, 23},
                        {23, 31, 24, 16},
                        {32, 40, 41, 33},
                        {33, 41, 42, 34},
                        {34, 42, 43, 35},
                        {35, 43, 44, 36},
                        {36, 44, 45, 37},
                        {37, 45, 46, 38},
                        {38, 46, 47, 39},
                        {39, 47, 40, 32},
                        {48, 56, 57, 49},
                        {49, 57, 58, 50},
                        {50, 58, 59, 51},
                        {51, 59, 60, 52},
                        {52, 60, 61, 53},
                        {53, 61, 62, 54},
                        {54, 62, 63, 55},
                        {55, 63, 56, 48},
                        {64, 72, 73, 65},
                        {65, 73, 74, 66},
                        {66, 74, 75, 67},
                        {67, 75, 76, 68},
                        {68, 76, 77, 69},
                        {69, 77, 78, 70},
                        {70, 78, 79, 71},
                        {71, 79, 72, 64},
                        {80, 88, 89, 81},
                        {81, 89, 90, 82},
                        {82, 90, 91, 83},
                        {83, 91, 92, 84},
                        {84, 92, 93, 85},
                        {85, 93, 94, 86},
                        {86, 94, 95, 87},
                        {87, 95, 88, 80},};
        }

        shapes[centerOffset] = new Shape3D(CENTER_VERTS, CENTER_FACES, new Color[CENTER_FACES.length][2]);
    }

    @Override
    protected void initTransforms() {
        scene = new Scene3D();

        // Create identity transforms
        for (int i = 0; i < partCount; i++) {
            identityTransforms[i] = new Transform3D();
        }

        // Move all corner parts to up front left and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
            identityTransforms[cornerOffset + i].translate(-18, 18, 18);
        }
        // urf
        identityTransforms[cornerOffset + 0].rotate(0, -HALF_PI, 0);
        // dfr
        identityTransforms[cornerOffset + 1].rotate(0, 0, PI);
        // ubr
        identityTransforms[cornerOffset + 2].rotate(0, PI, 0);
        // drb
        identityTransforms[cornerOffset + 3].rotate(0, 0, PI);
        identityTransforms[cornerOffset + 3].rotate(0, -HALF_PI, 0);
        // ulb
        identityTransforms[cornerOffset + 4].rotate(0, HALF_PI, 0);
        // dbl
        identityTransforms[cornerOffset + 5].rotate(PI, 0, 0);
        // ufl
        //--no transformation---
        // dlf
        identityTransforms[cornerOffset + 7].rotate(0, HALF_PI, 0);
        identityTransforms[cornerOffset + 7].rotate(PI, 0, 0);


        // Move all edge parts to front up (fu) and then rotate them in place
        for (int i = 0; i < edgeCount; i++) {
            identityTransforms[edgeOffset + i].translate(0, 18, 18);
        }
        // ur
        identityTransforms[edgeOffset + 0].rotate(0, HALF_PI, HALF_PI);
        // rf
        identityTransforms[edgeOffset + 1].rotateY(-HALF_PI);
        identityTransforms[edgeOffset + 1].rotateX(-HALF_PI);
        // dr
        identityTransforms[edgeOffset + 2].rotate(0, -HALF_PI, HALF_PI);
        // bu
        identityTransforms[edgeOffset + 3].rotateY(PI);
        // rb
        identityTransforms[edgeOffset + 4].rotateZ(HALF_PI);
        identityTransforms[edgeOffset + 4].rotateY(-HALF_PI);
        // bd
        identityTransforms[edgeOffset + 5].rotateX(PI);
        // ul
        identityTransforms[edgeOffset + 6].rotate(0, -HALF_PI, -HALF_PI);
        // lb
        identityTransforms[edgeOffset + 7].rotateZ(-HALF_PI);
        identityTransforms[edgeOffset + 7].rotateY(HALF_PI);
        // dl
        identityTransforms[edgeOffset + 8].rotate(-HALF_PI, HALF_PI, 0);
        // fu
        //--no transformation--
        // lf
        identityTransforms[edgeOffset + 10].rotateZ(HALF_PI);
        identityTransforms[edgeOffset + 10].rotateY(HALF_PI);
        // fd
        identityTransforms[edgeOffset + 11].rotateZ(PI);


        // Move side parts to front and then rotate them into place
        for (int i = 0; i < sideCount; i++) {
            identityTransforms[sideOffset + i].translate(0, 0, 18);
        }
        //rufldb
        identityTransforms[sideOffset + 0].rotateZ(Math.PI / -2);//r
        identityTransforms[sideOffset + 0].rotateY(Math.PI / -2);//r
        identityTransforms[sideOffset + 1].rotateZ(Math.PI / 2);//u
        identityTransforms[sideOffset + 1].rotateX(Math.PI / 2);//u
        // nothing to do for front side
        identityTransforms[sideOffset + 3].rotateZ(Math.PI);//l
        identityTransforms[sideOffset + 3].rotateY(Math.PI / 2);//l
        identityTransforms[sideOffset + 4].rotateZ(Math.PI);//d
        identityTransforms[sideOffset + 4].rotateX(Math.PI / -2);//d
        identityTransforms[sideOffset + 5].rotateZ(Math.PI / 2);//b
        identityTransforms[sideOffset + 5].rotateY(Math.PI);//b

        // Add transforms to scene
        for (int i = 0; i < partCount; i++) {
            transforms[i] = new TransformNode3D();
            transforms[i].addChild(shapes[i]);
            scene.addChild(transforms[i]);
        }
    }

    @Override
    protected Cube createCube() {
        return new RubiksCube();
    }

    @Override
    public void cubeTwisted(CubeEvent evt) {
        int loc;

        int layerMask = evt.getLayerMask();
        final int axis = evt.getAxis();
        final int angle = evt.getAngle();
        Cube model = getCube();

        final int[] partIndices = new int[27];
        final int[] locations = new int[27];
        final int[] orientations = new int[27];
        int count = 0;

        int[] affectedParts = evt.getAffectedLocations();
        if ((layerMask & 2) != 0) {
            count = affectedParts.length + 1;
            System.arraycopy(affectedParts, 0, locations, 0, count - 1);
            locations[count - 1] = centerOffset;
        } else {
            count = affectedParts.length;
            System.arraycopy(affectedParts, 0, locations, 0, count);
        }
        for (int i = 0; i < count; i++) {
            partIndices[i] = model.getPartAt(locations[i]);
            orientations[i] = model.getPartOrientation(partIndices[i]);
        }
        scene.setAdjusting(true);
        final int finalCount = count;
        Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f) {

            @Override
            protected void update(float value) {
                validateTwist(partIndices, locations, orientations, finalCount, axis, angle, value);
                fireStateChanged();
            }

            @Override
            public boolean isSequential(Interpolator that) {
                return (that.getClass() == this.getClass());
            }

            @Override
            public void finish(long now) {
                // reduce number of faces which need to be rendered
                for (int i = 0; i < partCount; i++) {
                    shapes[i].setReduced(true);
                }
                scene.setAdjusting(false||isInStartedPlayer());
                super.finish(now);
            }
        };
        interpolator.setTimespan(Math.abs(angle) * attributes.getTwistDuration());
        dispatch(interpolator);

        // Wait until interpolator has finished
        if (!getAnimator().isSynchronous() && !SwingUtilities.isEventDispatchThread()) {
            try {
                synchronized (interpolator) {
                    while (!interpolator.isFinished()) {
                        interpolator.wait();
                    }
                }
            } catch (InterruptedException e) {
                // empty (we exit the while loop)
            }
        }
    }
}
