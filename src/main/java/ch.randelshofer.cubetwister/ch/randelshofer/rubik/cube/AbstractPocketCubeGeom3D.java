/* @(#)AbstractPocketCubeGeom3D.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube;

import ch.randelshofer.geom3d.Scene3D;
import ch.randelshofer.geom3d.Shape3D;
import ch.randelshofer.geom3d.Transform3D;
import ch.randelshofer.geom3d.TransformNode3D;
import org.jhotdraw.annotation.Nonnull;
import org.monte.media.av.Interpolator;
import org.monte.media.interpolator.SplineInterpolator;

import javax.swing.SwingUtilities;
import java.awt.Color;

/**
 * Abstract base class for the geometrical representation of a {@link PocketCube}
 * using the Geom3D engine.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractPocketCubeGeom3D extends AbstractCubeGeom3D {

    protected final static double[][] CORNER_EXPLODE_TRANSLATION = {
        {-1, 1, 1}, // left up front
        {-1, -1, 1}, // left down front
        {1, 1, 1}, // right up front
        {1, -1, 1}, // right down front

        {1, 1, -1}, // right up back
        {1, -1, -1}, // right down back
        {-1, 1, -1}, // left up back
        {-1, -1, -1}  // left down back
    };

    public AbstractPocketCubeGeom3D() {
        super(2, 8, 0, 0, 1);
        init();
    }

    protected float getUnitScaleFactor() {
        return 1.5f;
    }

    protected void computeTransformation() {
        Cube cube = getCube();

        synchronized (getLock()) {
            Transform3D t, t2;
            int loc;

            t = scene.getTransform();
            t.setToIdentity();
            double scale = getAttributes().getScaleFactor() * getUnitScaleFactor();
            t.scale(scale, scale, scale);
            t.rotateY(getAttributes().getBeta());
            t.rotateX(getAttributes().getAlpha());

            double explosion = 18d * getAttributes().getExplosionFactor();

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

    protected void initEdges() {
    }

    protected void initSides() {
    }
    private static float[] CENTER_VERTS;
    private static int[][] CENTER_FACES;

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
                        2, -4.5f, 4.5f, 4.5f, -4.5f, 2, 4.5f, -4.5f, -2, 2, -4.5f, -4.5f, -2, -4.5f, -4.5f, -4.5f, -4.5f, -2, -4.5f, -4.5f, 2, -2, -4.5f, 4.5f,
                    };
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
                        {9, 26, 27, 19},
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
                        {87, 95, 88, 80},
                    };
        }

        shapes[centerOffset] = new Shape3D(CENTER_VERTS, CENTER_FACES, new Color[CENTER_FACES.length][2]);
    }

    protected void initTransforms() {
        scene = new Scene3D();

        // Create identity transforms
        for (int i = 0; i < partCount; i++) {
            identityTransforms[i] = new Transform3D();
        }

        // Move all corner parts to up front left and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
            identityTransforms[cornerOffset + i].translate(-9, 9, 9);
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

        // Add transforms to scene
        for (int i = 0; i < partCount; i++) {
            transforms[i] = new TransformNode3D();
            transforms[i].addChild(shapes[i]);
            scene.addChild(transforms[i]);
        }
    }

    @Nonnull
    protected Cube createCube() {
        return new PocketCube();
    }

    @Override
    public void cubeTwisted(@Nonnull CubeEvent evt) {
        int loc;

        int layerMask = evt.getLayerMask();
        final int axis = evt.getAxis();
        final int angle = evt.getAngle();
        Cube model = getCube();

        final int[] partIndices = new int[27];
        //final int[] locations = new int[27];
        final int[] orientations = new int[27];
        final int[] locations = evt.getAffectedLocations();
        int count = locations.length;

        for (int i = 0; i < count; i++) {
            partIndices[i] = model.getPartAt(locations[i]);
            orientations[i] = model.getPartOrientation(partIndices[i]);
        }
        scene.setAdjusting(true);

        // Create interpolator
        final int finalCount = count;
        Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f) {

            protected void update(float value) {
                //validateTwist(finalIndices, locationTransforms, normaltransforms, axis, angle, value);
                validateTwist(partIndices, locations, orientations, finalCount, axis, angle, value);
                fireStateChanged();
            }

            @Override
            public boolean isSequential(@Nonnull Interpolator that) {
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
