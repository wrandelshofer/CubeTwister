/* @(#)AbstractProfessorCubeGeom3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 *
 * Parts of the code are copyright (c) Markus Pirzer, Germany
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.geom3d.Scene3D;
import ch.randelshofer.geom3d.Transform3D;
import ch.randelshofer.geom3d.TransformNode3D;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeEvent;
import ch.randelshofer.rubik.cube.ProfessorCube;
import org.jhotdraw.annotation.Nonnull;
import org.monte.media.av.Interpolator;
import org.monte.media.interpolator.SplineInterpolator;

import javax.swing.SwingUtilities;


/**
 * Abstract base class for the geometrical representation of a {@link ProfessorCube}
 * using the Geom3D engine.
 *
 * @author Werner Randelshofer, Markus Pirzer
 */
public abstract class AbstractProfessorCubeGeom3D extends AbstractCubeGeom3D {
    /**
     * A cube part has a side length of 13 mm.
     */
    protected final static float PART_LENGTH = 13f;
    /**
     * The beveled edge of a cube part has a length of 1 mm.
     */
    protected final static float BEVEL_LENGTH = 1f;
    
    public final static double[][] EXPLODE_TRANSLATIONS = {
        // Corners
        {-1,  1,  1}, // left up front
        {-1, -1,  1}, // left down front
        { 1,  1,  1}, // right up front
        { 1, -1,  1}, // right down front
        
        { 1,  1, -1}, // right up back
        { 1, -1, -1}, // right down back
        {-1,  1, -1}, // left up back
        {-1, -1, -1}, // left down back
        
        // Edges
        { 0,  1,  1}, // up front
        {-1,  0,  1}, // left front
        { 0, -1,  1}, // down front
        
        { 1,  1,  0}, // right up
        { 1,  0,  1}, // right front
        { 1, -1,  0}, // right down
        
        { 0,  1, -1}, // up back
        { 1,  0, -1}, // right back
        { 0, -1, -1}, // down back
        
        {-1,  1,  0}, // left up
        {-1,  0, -1}, // left back
        {-1, -1,  0},  // left down
        
        { 0,  1,  1}, // up front
        {-1,  0,  1}, // left front
        { 0, -1,  1}, // down front
        
        { 1,  1,  0}, // right up
        { 1,  0,  1}, // right front
        { 1, -1,  0}, // right down
        
        { 0,  1, -1}, // up back
        { 1,  0, -1}, // right back
        { 0, -1, -1}, // down back
        
        {-1,  1,  0}, // left up
        {-1,  0, -1}, // left back
        {-1, -1,  0},  // left down
        
        { 0,  1,  1}, // up front
        {-1,  0,  1}, // left front
        { 0, -1,  1}, // down front
        
        { 1,  1,  0}, // right up
        { 1,  0,  1}, // right front
        { 1, -1,  0}, // right down
        
        { 0,  1, -1}, // up back
        { 1,  0, -1}, // right back
        { 0, -1, -1}, // down back
        
        {-1,  1,  0}, // left up
        {-1,  0, -1}, // left back
        {-1, -1,  0}, // left down
        
        // Sides
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}, // top
        { 0,  0,  1}, // front
        { 1,  0,  0}, // right
        { 0, -1,  0}, // down
        { 0,  0, -1}, // back
        {-1,  0,  0}, // left
        { 0,  1,  0}  // top
    };
    
    public AbstractProfessorCubeGeom3D() {
        super(5, 8, 12*3, 6*9, 1);
        init();
    }
    
    protected void initTransforms() {
        scene = new Scene3D();
        
        // Create identity transforms
        for (int i = 0; i < partCount; i++) {
            identityTransforms[i] = new Transform3D();
        }
        
        /*
         * Corners
         *              +---+----+---+
         *              |6.0|    |4.0|
         *              +---+    +---+
         *              |     5      |
         *              +---+    +---+
         *              |0.0|    |2.0|
         * +---+----+---+---+----+---+---+----+---+---+----+---+
         * |6.1|    |0.2|0.1|    |2.2|2.1|    |4.2|4.1|    |6.2|
         * +---+    +---+---+    +---+---+    +---+---+    +---+
         * |     4      |     0      |     1      |     3      |
         * +---+    +---+---+    +---+---+    +---+---+    +---+
         * |7.2|    |1.1|1.2|    |3.1|3.2|    |5.1|5.2|    |7.1|
         * +---+----+---+---+----+---+---+----+---+---+----+---+
         *              |1.0|    |3.0|
         *              +---+    +---+
         *              |     2      |
         *              +---+    +---+
         *              |7.0|    |5.0|
         *              +---+----+---+
         */
        // Move all corner parts to up front left (ufl) and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
            identityTransforms[cornerOffset+i].translate(PART_LENGTH * -2f, PART_LENGTH * 2f, PART_LENGTH * 2f);
        }
        // urf
        identityTransforms[cornerOffset+0].rotate(0, -HALF_PI, 0);
        // dfr
        identityTransforms[cornerOffset+1].rotate(0, 0, PI);
        // ubr
        identityTransforms[cornerOffset+2].rotate(0, PI, 0);
        // drb
        identityTransforms[cornerOffset+3].rotate(0, 0, PI);
        identityTransforms[cornerOffset+3].rotate(0, -HALF_PI, 0);
        // ulb
        identityTransforms[cornerOffset+4].rotate(0, HALF_PI, 0);
        // dbl
        identityTransforms[cornerOffset+5].rotate(PI, 0, 0);
        // ufl
        //--no transformation---
        // dlf
        identityTransforms[cornerOffset+7].rotate(0, HALF_PI, 0);
        identityTransforms[cornerOffset+7].rotate(PI, 0, 0);
        
        
        /**
         * Edges
         *                     +---+---+---+---+---+
         *                     |   |18 |6.0| 30|   |
         *                     +---+---+---+---+---+
         *                     |21 |           |27 |
         *                     +---+           +---+
         *                     |9.1|     5     |3.1|
         *                     +---+           +---+
         *                     |33 |           |15 |
         *                     +---+---+---+---+---+
         *                     |   |24 |0.0| 12|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |   |21 |9.0|33 |   |   |24 |0.1| 12|   |   |15 |3.0|27 |   |   |30 |6.1| 18|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |22 |           |25 |25 |           |16 |16 |           |31 |31 |           |22 |
         * +---+           +---+---+           +---+---+           +---+---+           +---+
         * |10.1     4     |1.1|1.0|     0     |4.0|4.1|     1     |7.1|7.0|     3     |10.0
         * +---+           +---+---+           +---+---+           +---+---+           +---+
         * |34 |           |13 |13 |           |28 |28 |           |19 |19 |           |34 |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         * |   |35 11.0|23 |   |   |14 |2.1| 26|   |   |29 |5.0|17 |   |   |20 |8.1| 32|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         *                     |   |14 |2.0| 26|   |
         *                     +---+---+---+---+---+
         *                     |23 |           |29 |
         *                     +---+           +---+
         *                     |11.1     2     |5.1|
         *                     +---+           +---+
         *                     |35 |           |17 |
         *                     +---+---+---+---+---+
         *                     |   |32 |8.0| 20|   |
         *                     +---+---+---+---+---+
         */
        // Move all edge parts to front up (fu) and then rotate them in place
        for (int i = 0; i < edgeCount; i++) {
            Transform3D t = identityTransforms[edgeOffset + i];
                    t.translate(0, PART_LENGTH * 2f, PART_LENGTH * 2f);
            if (i >= 12) {
                switch (i - 12) {
                    case 12:
                    case 13:
                    case 2:
                    case 3:
                    case 4:
                    case 17:
                    case 6:
                    case 19:
                    case 20:
                    case 21:
                    case 10:
                    case 11:
                    t.translate(PART_LENGTH * 1f, 0, 0);
                        break;
                    default:
                    t.translate(PART_LENGTH * -1f, 0, 0);
                        break;
                }
            }
            switch (i % 12) {
                case 0 : // ur
                    t.rotate(0, HALF_PI, HALF_PI);
                    break;
                case 1 : // rf
                    t.rotateY(-HALF_PI);
                    t.rotateX(-HALF_PI);
                    break;
                case 2 : // dr
                    t.rotate(0, -HALF_PI, HALF_PI);
                    break;
                case 3 : // bu
                    t.rotateY(PI);
                    break;
                case 4 : // rb
                    t.rotateZ(HALF_PI);
                    t.rotateY(-HALF_PI);
                    break;
                case 5 : // bd
                    t.rotateX(PI);
                    break;
                case 6 : // ul
                    t.rotate(0, -HALF_PI, -HALF_PI);
                    break;
                case 7 : // lb
                    t.rotateZ(-HALF_PI);
                    t.rotateY(HALF_PI);
                    break;
                case 8 : // dl
                    t.rotate(-HALF_PI, HALF_PI, 0);
                    break;
                 case 9 : // fu
                //--no transformation--
                break;
                case 10 : // lf
                    t.rotateZ(HALF_PI);
                    t.rotateY(HALF_PI);
                    break;
                case 11 : // fd
                    t.rotateZ(PI);
                    break;
            }
        }
        
        // Move all side parts to the front side and rotate them into place
        // Move all side parts to the front side and then rotate them in place
        for (int i=0; i < sideCount; i++) {
            Transform3D t = identityTransforms[sideOffset + i];
            switch (i / 6) {
                case 0 :
                    t.translate(0, 0, PART_LENGTH * 2f);
                    break;
                case 1 :
                    t.translate(PART_LENGTH * -1f, PART_LENGTH * -1f, PART_LENGTH * 2f);
                    break;
                case 2 :
                    t.translate(PART_LENGTH * -1f, PART_LENGTH * 1f, PART_LENGTH * 2f);
                    break;
                case 3 :
                    t.translate(PART_LENGTH * 1f, PART_LENGTH * 1f, PART_LENGTH * 2f);
                    break;
                case 4 :
                    t.translate(PART_LENGTH * 1f, PART_LENGTH * -1f, PART_LENGTH * 2f);
                    break;
                case 5 :
                    t.translate(0, PART_LENGTH * -1f, PART_LENGTH * 2f);
                    break;
                case 6 :
                    t.translate(PART_LENGTH * -1f, 0, PART_LENGTH * 2f);
                    break;
                case 7 :
                    t.translate(0, PART_LENGTH * 1f, PART_LENGTH * 2f);
                    break;
                case 8 :
                    t.translate(PART_LENGTH * 1f, 0, PART_LENGTH * 2f);
                    break;
            }
            switch (i % 6) {
                case 0 : // r
                    t.rotateZ(Math.PI/-2);
                    t.rotateY(Math.PI/-2);
                    break;
                case 1 : // u
                    t.rotateZ(Math.PI/2);
                    t.rotateX(Math.PI/2);
                    break;
                case 2 : // f
                    break;
                case 3 : // l
                    t.rotateZ(Math.PI);
                    t.rotateY(Math.PI/2);
                    break;
                case 4 : // d
                    t.rotateZ(Math.PI);
                    t.rotateX(Math.PI/-2);
                    break; 
                case 5 : // b
                    t.rotateZ(Math.PI/2);
                    t.rotateY(Math.PI);
                    break;
            }
        }
        
        for (int i = 0; i < partCount; i++) {
            transforms[i] = new TransformNode3D();
            transforms[i].addChild(shapes[i]);
            scene.addChild(transforms[i]);
        }
    }
    
    protected float getUnitScaleFactor() {
        return 0.8f;
    }
    
    protected void computeTransformation() {
        Cube cube = getCube();
        
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
            
            // Reduce number of faces to be drawn to optimize speed
            /*
            for (int i=0; i < partCount; i++) {
                shapes[i].setReduced(true);
            }*/
            
            double explosion = PART_LENGTH * 1.5d * getAttributes().getExplosionFactor();
            
            // Transform the part at the center of the cube
            t = transforms[centerOffset].getTransform();
            t.setToIdentity();
            
            switch (cube.getCubeOrientation()) {
                case -1: // Unknow or illegal orientation
                case  0: // Front at front, Right at right
                    // nothing to do
                    break;
                case  1: // Bottom, Right, CR
                    t.rotateX(Math.PI/2);
                    break;
                case  2: // Back, Right, CR2
                    t.rotateX(Math.PI);
                    break;
                case  3: // Top, Right, CR'
                    t.rotateX(Math.PI/-2);
                    break;
                case  4: // Right, Back, CU
                    t.rotateY(Math.PI/2);
                    break;
                case  5: // Back, Left, CU2
                    t.rotateY(Math.PI);
                    break;
                case  6: // Left, Front, CU'
                    t.rotateY(Math.PI/-2);
                    break;
                case  7: // Front, Top, CF
                    t.rotateZ(Math.PI/2);
                    break;
                case  8: // Front, Left, CF2
                    t.rotateZ(Math.PI);
                    break;
                case  9: // Front, Bottom, CF'
                    t.rotateZ(Math.PI/-2);
                    break;
                case 10: // Right, Top, CR CU
                    t.rotateX(Math.PI/2);
                    t.rotateY(Math.PI/2);
                    break;
                case 11: // Top, Left, CR CU2
                    t.rotateX(Math.PI/2);
                    t.rotateY(Math.PI);
                    break;
                case 12: // Left, Down, CR CU'
                    t.rotateX(Math.PI/2);
                    t.rotateY(Math.PI/-2);
                    break;
                case 13: // Right, Front, CR2 CU
                    t.rotateX(Math.PI);
                    t.rotateY(Math.PI/2);
                    break;
                case 14: // Left, Back, CR2 CU'
                    t.rotateX(Math.PI);
                    t.rotateY(Math.PI/-2);
                    break;
                case 15: // Right, Down, CR' CU
                    t.rotateX(Math.PI/-2);
                    t.rotateY(Math.PI/2);
                    break;
                case 16: // Down, Left, CR' CU2
                    t.rotateX(Math.PI/-2);
                    t.rotateY(Math.PI);
                    break;
                case 17: // Left, Up, CR' CU'
                    t.rotateX(Math.PI/-2);
                    t.rotateY(Math.PI/-2);
                    break;
                case 18: // Down, Up, CR CF
                    t.rotateX(Math.PI/2);
                    t.rotateZ(Math.PI/2);
                    break;
                case 19: // Down, Back, CR CF'
                    t.rotateX(Math.PI/2);
                    t.rotateZ(Math.PI/-2);
                    break;
                case 20: // Back, Down, CR2 CF
                    t.rotateX(Math.PI);
                    t.rotateZ(Math.PI/2);
                    break;
                case 21: // Back, Up, CR2 CF'
                    t.rotateX(Math.PI);
                    t.rotateZ(Math.PI/-2);
                    break;
                case 22: // Up, Back, CR' CF
                    t.rotateX(Math.PI/-2);
                    t.rotateZ(Math.PI/2);
                    break;
                case 23: // Up, Front, CR' CF'
                    t.rotateX(Math.PI/-2);
                    t.rotateZ(Math.PI/-2);
                    break;
                default:
                    break;
            }
            
            
            // Transform the six parts at each side of the cube
            for (int i=0; i < sideCount; i++) {
                loc = cube.getSideLocation(i);
                int ori = cube.getSideOrientation(i);
                t = new Transform3D();
                t.rotateZ(ori*Math.PI/-2d);
                t.concatenate(identityTransforms[sideOffset+loc]);
                //t = (Transform3D) identityTransforms[sideOffset + loc].clone();
                t.translate(
                        EXPLODE_TRANSLATIONS[sideOffset+loc][0] * explosion,
                        EXPLODE_TRANSLATIONS[sideOffset+loc][1] * explosion,
                        EXPLODE_TRANSLATIONS[sideOffset+loc][2] * explosion
                        );
                transforms[sideOffset+i].setTransform(t);
            }
            
            
            for (int i=0; i < edgeCount; i++) {
                loc = cube.getEdgeLocation(i);
                t = transforms[edgeOffset+i].getTransform();
                t.setToIdentity();
                if (cube.getEdgeOrientation(i) == 1) {
                    t.rotateZ(Math.PI);
                    t.rotateX(Math.PI/2);
                }
                t2 = (Transform3D) identityTransforms[edgeOffset+loc].clone();
                t2.translate(
                        EXPLODE_TRANSLATIONS[edgeOffset+loc][0] * explosion,
                        EXPLODE_TRANSLATIONS[edgeOffset+loc][1] * explosion,
                        EXPLODE_TRANSLATIONS[edgeOffset+loc][2] * explosion
                        );
                t.concatenate(t2);
            }
            
            for (int i=0; i < cornerCount; i++) {
                loc = cube.getCornerLocation(i);
                t = transforms[cornerOffset+i].getTransform();
                t.setToIdentity();
                switch (cube.getCornerOrientation(i)) {
                    case 0 :
                        break;
                    case 1 :
                        t.rotateZ(Math.PI/-2);
                        t.rotateX(Math.PI/2);
                        break;
                    case 2 :
                        t.rotate(Math.PI/-2,0,Math.PI/2);
                        break;
                }
                t2 = (Transform3D) identityTransforms[cornerOffset+loc].clone();
                t2.translate(
                        EXPLODE_TRANSLATIONS[cornerOffset+loc][0] * explosion,
                        EXPLODE_TRANSLATIONS[cornerOffset+loc][1] * explosion,
                        EXPLODE_TRANSLATIONS[cornerOffset+loc][2] * explosion
                        );
                
                t.concatenate(t2);
            }
        }
    }

    @Nonnull
    protected Cube createCube() {
        return new ProfessorCube();
    }

    @Override
    public void cubeTwisted(@Nonnull CubeEvent evt) {
        int loc;

        int layerMask = evt.getLayerMask();
        final int axis = evt.getAxis();
        final int angle = evt.getAngle();
        Cube model = getCube();

        final int[] partIndices = new int[partCount];
        //final int[] locations = new int[partCount];
        final int[] orientations = new int[partCount];
        //int count=0;
        final int[] locations = evt.getAffectedLocations();
        int count = locations.length;
        
        
        // Reduce number of faces to be drawn to optimize speed
        boolean reduce = (layerMask & 1) == (layerMask & 2) && (layerMask & 8) == (layerMask & 16);
        for (int i=0; i < cornerCount; i++) {
            shapes[cornerOffset+i].setReduced(reduce);
        }
        reduce = (layerMask & 2) == (layerMask & 4) && (layerMask & 4) == (layerMask & 8);
        for (int i=0; i < 12; i++) {
            shapes[edgeOffset+i].setReduced(reduce);
        }
        reduce = (layerMask == 0) || (layerMask == 32);
        for (int i=12; i < edgeCount; i++) {
            shapes[edgeOffset+i].setReduced(reduce);
        }
        reduce = (layerMask & 2) == (layerMask & 4) && (layerMask & 4) == (layerMask & 8);
        for (int i=0; i < 6; i++) {
            shapes[sideOffset+i].setReduced(reduce);
        }
        reduce = (layerMask == 0) || (layerMask == 32);
        for (int i=6; i < sideCount; i++) {
            shapes[sideOffset+i].setReduced(reduce);
        }
        Cube cube = getCube();
        
        // Construct interpolator
        for (int i=0; i < count; i++) {
            partIndices[i] = model.getPartAt(locations[i]);
            orientations[i] = model.getPartOrientation(partIndices[i]);
        }
        
        scene.setAdjusting(true);
        final int finalCount = count;
        Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f) {
            protected void update(float value) {
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
                scene.setAdjusting(false || isInStartedPlayer());
                super.finish(now);
            }
        };
        interpolator.setTimespan(Math.abs(angle) * attributes.getTwistDuration());
        dispatch(interpolator);
        
        // Wait until interpolator has finished
        if (!getAnimator().isSynchronous() && !SwingUtilities.isEventDispatchThread()) {
            try {
                synchronized (interpolator) {
                    while (! interpolator.isFinished()) {
                        interpolator.wait();
                    }
                }
            } catch (InterruptedException e) {
                // empty (we exit the while loop)
            }
        }
        
    }
}
