/*
 * @(#)AbstractRevengeCubeGeom3D.java  7.1.2  2010-04-04
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.*;
import org.monte.media.*;
import javax.swing.SwingUtilities;

/** 
 * Abstract base class for the geometrical representation of a {@link RevengeCube}
 * using the Geom3D engine.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>7.1.1 2009-01-04 Method computeTransformation did not properly
 * orient side parts.
 * <br>7.1 2008-08-17 Streamlined with code of class AbstractVCube6Geom3D.
 * <br>7.0 2008-01-06 Adapted to changes in AbstractCube.
 * <br>6.1 2007-09-09 Use SplineInterpolator for animation.
 * <br>1.0 2005-12-26 W. Randelshofer created.
 */
public abstract class AbstractRevengeCubeGeom3D extends AbstractCubeGeom3D {
    /**
     * A cube part has a side length of 14 mm.
     */
    protected final static float SIDE_LENGTH = 14f;
    /**
     * The beveled edge of a cube part has a length of 0.75 mm.
     */
    protected final static float BEVEL_LENGTH = 1f;

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
        {-1, -1, 0}, // left down

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
        {-1, -1, 0}, // left down

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
        {0, 1, 0}, // top

        {0, 0, 1}, // front
        {1, 0, 0}, // right
        {0, -1, 0}, // down
        {0, 0, -1}, // back
        {-1, 0, 0}, // left
        {0, 1, 0}, // top

        {0, 0, 1}, // front
        {1, 0, 0}, // right
        {0, -1, 0}, // down
        {0, 0, -1}, // back
        {-1, 0, 0}, // left
        {0, 1, 0}, // top

        {0, 0, 1}, // front
        {1, 0, 0}, // right
        {0, -1, 0}, // down
        {0, 0, -1}, // back
        {-1, 0, 0}, // left
        {0, 1, 0}, // top

      
        };
    
    public AbstractRevengeCubeGeom3D() {
        super(4, 8, 12 * 2, 6 * 4, 1);
        init();
    }

    protected float getUnitScaleFactor() {
        return 0.95f;
    }

    @Override
    protected void computeTransformation() {Cube cube = getCube();

        synchronized (getLock()) {
            Transform3D t, t2;
            int loc;

            // Transform the scene
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


            // Transform the six parts at each side of the cube
            for (int i = 0; i < sideCount; i++) {
                loc = cube.getSideLocation(i);
                int ori = cube.getSideOrientation(i);
                t = new Transform3D();
                t.rotateZ(ori*Math.PI/-2d);
                t.concatenate(identityTransforms[sideOffset+loc]);
                //t = (Transform3D) identityTransforms[sideOffset + loc].clone();
                t.translate(
                        SIDE_EXPLODE_TRANSLATION[loc][0] * explosion,
                        SIDE_EXPLODE_TRANSLATION[loc][1] * explosion,
                        SIDE_EXPLODE_TRANSLATION[loc][2] * explosion);
                transforms[sideOffset + i].setTransform(t);
            }


            for (int i = 0; i < edgeCount; i++) {
                loc = cube.getEdgeLocation(i);
                t = transforms[edgeOffset + i].getTransform();
                t.setToIdentity();
                if (cube.getEdgeOrientation(i) == 1) {
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

    @Override
    protected void initTransforms() {
        scene = new Scene3D();

        // Create identity transforms
        for (int i = 0; i < partCount; i++) {
            identityTransforms[i] = new Transform3D();
        }

        // Move all corner parts to up front left (ufl) and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
//            identityTransforms[cornerOffset + i].translate(-21, 21, 21);
            identityTransforms[cornerOffset + i].translate(SIDE_LENGTH * -1.5f, SIDE_LENGTH * 1.5f, SIDE_LENGTH * 1.5f);
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

        /* Edges
         *                 +---+---+---+---+
         *                 |   |18 |6.0|   |
         *                 +---+---+---+---+
         *                 |21 |       |3.1|
         *                 +---+   5   +---+
         *                 |9.1|       |15 |
         *                 +---+---+---+---+
         *                 |   |0.0| 12|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |   |21 |9.0|   |   |0.1| 12|   |   |15 |3.0|   |   |6.1| 18|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |22 |       |1.1|1.0|       |16 |16 |       |7.1|7.0|       |22 |
         * +---+   4   +---+---+   0   +---+---+   1   +---+---+   3   +---+
         * |10.1       |13 |13 |       |4.0|4.1|       |19 |19 |       |10.0
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |   11.0|23 |   |   |14 |2.1|   |   |5.0|17 |   |   |20 |8.1|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         *                 |   |14 |2.0|   |
         *                 +---+---+---+---+
         *                 |23 |       |5.1|
         *                 +---+   2   +---+
         *                 |11.1       |17 |
         *                 +---+---+---+---+
         *                 |   |8.0| 20|   |
         *                 +---+---+---+---+    */
        // Move all edge parts to the front side and then rotate them in place
        for (int i = 0; i < edgeCount; i++) {
            switch (i) {
                case 12 : 
                case 13 : 
                case 2 :
                case 3 :
                case 4 :
                case 17 :
                case 6 :
                case 19 :
                case 20 :
                case 21 :
                case 10 :
                case 11 :
                    identityTransforms[edgeOffset + i].translate(SIDE_LENGTH * 0.5f, SIDE_LENGTH * 1.5f, SIDE_LENGTH * 1.5f);
                    break;
                default:
                    identityTransforms[edgeOffset + i].translate(SIDE_LENGTH * -0.5f, SIDE_LENGTH * 1.5f, SIDE_LENGTH * 1.5f);
                    break;
            }
            switch (i % 12) {
                case 0: // ur
                    identityTransforms[edgeOffset + i].rotate(0, HALF_PI, HALF_PI);
                    break;
                case 1: // rf
                    identityTransforms[edgeOffset + i].rotateY(-HALF_PI);
                    identityTransforms[edgeOffset + i].rotateX(-HALF_PI);
                    break;
                case 2: // dr
                    identityTransforms[edgeOffset + i].rotate(0, -HALF_PI, HALF_PI);
                    break;
                case 3: // bu
                    identityTransforms[edgeOffset + i].rotateY(PI);
                    break;
                case 4: // rb
                    identityTransforms[edgeOffset + i].rotateZ(HALF_PI);
                    identityTransforms[edgeOffset + i].rotateY(-HALF_PI);
                    break;
                case 5: // bd
                    identityTransforms[edgeOffset + i].rotateX(PI);
                    break;
                case 6: // ul
                    identityTransforms[edgeOffset + i].rotate(0, -HALF_PI, -HALF_PI);
                    break;
                case 7: // lb
                    identityTransforms[edgeOffset + i].rotateZ(-HALF_PI);
                    identityTransforms[edgeOffset + i].rotateY(HALF_PI);
                    break;
                case 8: // dl
                    identityTransforms[edgeOffset + i].rotate(-HALF_PI, HALF_PI, 0);
                    break;
                case 9: // fu
                    //--no transformation--
                    break;
                case 10: // lf
                    identityTransforms[edgeOffset + i].rotateZ(HALF_PI);
                    identityTransforms[edgeOffset + i].rotateY(HALF_PI);
                    break;
                case 11: // fd
                    identityTransforms[edgeOffset + i].rotateZ(PI);
                    break;
            }
        }
        /*
         *                 +---+---+---+---+
         *                 |    .1         |
         *                 +   +---+---+   +
         *                 |   | 5 |11 |.2 |
         *                 +   +---+---+   +
         *                 | .0|23 |17 |   |
         *                 +   +---+---+   +
         *                 |         .3    |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |      .1       |    .2         |      .3       |    .0         |
         * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
         * |   | 4 |10 |   |   | 6 |12 |.3 |   |13 |19 |.0 |   |21 | 3 |.1 |
         * +   +---+---+.2 +   +---+---+   +   +---+---+   +   +---+---+   +
         * | .0|22 |16 |   | .1| 0 |18 |   | .2| 7 | 1 |   | .3|15 | 9 |   |
         * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
         * |      .3       |        .0     |        .1     |      .2       |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         *                 |      .3       |
         *                 +   +---+---+   +
         *                 |   |14 |20 |.0 |
         *                 + .2+---+---+   +
         *                 |   | 8 | 2 |   |
         *                 +   +---+---+   +
         *                 |        .1     |
         *                 +---+---+---+---+
         */
        // Move all side parts to the front side and then rotate them in place
        for (int i = 0; i < sideCount; i++) {
            switch (i / 6) {
                case 0:
                    identityTransforms[sideOffset + i].translate(SIDE_LENGTH * -0.5f, SIDE_LENGTH * -0.5f, SIDE_LENGTH * 1.5f);
                    break;
                case 1:
                    identityTransforms[sideOffset + i].translate(SIDE_LENGTH * -0.5f, SIDE_LENGTH * 0.5f, SIDE_LENGTH * 1.5f);
                    break;
                case 2:
                    identityTransforms[sideOffset + i].translate(SIDE_LENGTH * 0.5f, SIDE_LENGTH * 0.5f, SIDE_LENGTH * 1.5f);
                    break;
                case 3:
                    identityTransforms[sideOffset + i].translate(SIDE_LENGTH * 0.5f, SIDE_LENGTH * -0.5f, SIDE_LENGTH * 1.5f);
                    break;
            }
            switch (i % 6) {
                case 0: // r
                    identityTransforms[sideOffset + i].rotateZ(Math.PI / -2);
                    identityTransforms[sideOffset + i].rotateY(Math.PI / -2);
                    break;
                case 1: // u
                    identityTransforms[sideOffset + i].rotateZ(Math.PI / 2);
                    identityTransforms[sideOffset + i].rotateX(Math.PI / 2);
                    break;
                 case 2 : // f
                //--no transformation--
                    break;
                case 3: // l
                    identityTransforms[sideOffset + i].rotateZ(Math.PI);
                    identityTransforms[sideOffset + i].rotateY(Math.PI / 2);
                    break;
                case 4: // d
                    identityTransforms[sideOffset + i].rotateZ(Math.PI);
                    identityTransforms[sideOffset + i].rotateX(Math.PI / -2);
                    break;
                case 5: // b
                    identityTransforms[sideOffset + i].rotateZ(Math.PI / 2);
                    identityTransforms[sideOffset + i].rotateY(Math.PI);
                    break;
            }
        }

        for (int i = 0; i < partCount; i++) {
            transforms[i] = new TransformNode3D();
            transforms[i].addChild(shapes[i]);
            scene.addChild(transforms[i]);
        }
    }

    @Override
    protected Cube createCube() {
        return new RevengeCube();
    }

    @Override
    public void cubeTwisted(CubeEvent evt) {
        int loc;

        int layerMask = evt.getLayerMask();
        final int axis = evt.getAxis();
        final int angle = evt.getAngle();
        Cube model = getCube();

        final int[] partIndices = new int[partCount];
        //final int[] locations = new int[partCount];
        final int[] orientations = new int[partCount];
        //int count = 0;
        final int[] locations = evt.getAffectedLocations();
        int count = locations.length;

        // Reduce number of faces to be drawn to optimize speed
        // FIXME - This has potential for optimization
        boolean reduce = (layerMask & 1) == (layerMask & 2) && (layerMask & 4) == (layerMask & 8);
        for (int i = 0; i < cornerCount; i++) {
            shapes[cornerOffset + i].setReduced(reduce);
        }
        reduce = (layerMask == 0) || (layerMask == 15);
        for (int i = 0; i < edgeCount; i++) {
            shapes[edgeOffset + i].setReduced(reduce);
        }
        reduce = (layerMask == 0) || (layerMask == 15);
        for (int i = 0; i < sideCount; i++) {
            shapes[sideOffset + i].setReduced(reduce);
        }

        // Construct interpolator
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
