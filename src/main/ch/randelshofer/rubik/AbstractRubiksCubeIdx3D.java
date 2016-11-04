/*
 * @(#)AbstractCubeIdx3D.java  3.0  2007-12-30
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import org.monte.media.Interpolator;
import org.monte.media.SplineInterpolator;
import idx3d.idx3d_Camera;
import idx3d.idx3d_Group;
import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Light;
import idx3d.idx3d_Matrix;
import idx3d.idx3d_Object;
import idx3d.idx3d_ObjectFactory;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Vector;
import javax.swing.SwingUtilities;

/**
 * Abstract base class for the geometrical representation of a {@link RubiksCube}
 * using the Idx3D engine.
 *
 * Each part is represented by a idx3d_Object. The idx3d_Object is a child of
 * an idx3d_Group which represents the explosion factor. The idx3d_Group is
 * a child of the idx3d_Scene.
 *
 * @author  Werner Randelshofer
 * @version 3.0 2007-12-30 Adapted to changes in AbstractCube. 
 * <br>2.2 2007-09-09 Use SplineInterpolator for cube twists.
 * <br>2.1 2006-02-21 Avoid unecessary object creation.
 * <br>2.0 2004-08-29 Reflectivity of part fill color increased from 160
 * to 300. We use now a hierarchical scene graph. Support for explosion addded.
 * <br>1.0 December 26, 2003 Created.
 */
public abstract class AbstractRubiksCubeIdx3D extends AbstractCubeIdx3D {

    /**
     * A cube part has a side length of 18 mm.
     */
    protected final static float PART_LENGTH = 18f;

    /** Creates a new instance. */
    public AbstractRubiksCubeIdx3D() {
        super(3, 8, 12, 6, 1);
        init();
    }

    protected void init() {
        explosionShift = 18f;
        initEdges();
        initCorners();
        initSides();
        initCenter();
        initTransforms();
        initScene();
        initActions(scene);
        setCube(new RubiksCube());
        setAttributes(createAttributes());
    }

    @Override
    protected float getUnitScaleFactor() {
        return 0.019f * 54f / (PART_LENGTH * layerCount);
    }

    private void initScene() {
        // FIXME move scene creation to super class
        scene = new idx3d_Scene() {

            @Override
            public boolean isAdjusting() {
                return super.isAdjusting() || isAnimating() || isInStartedPlayer() || AbstractRubiksCubeIdx3D.this.isAdjusting();
            }

            @Override
            public void prepareForRendering() {
                validateAlphaBeta();
                validateScaleFactor();
                validateCube();
                validateStickersImage();
                validateAttributes();     // must be done after validateStickersImage!
                super.prepareForRendering();
            }

            @Override
            public /*final*/ void rotate(float dx, float dy, float dz) {
                super.rotate(dx, dy, dz);
                fireStateChanged();
            }
        };
        scene.setBackgroundColor(0xffffff);


        scaleTransform = new idx3d_Group();
        scaleTransform.scale(0.018f);

        for (int i = 0; i < locationTransforms.length; i++) {
            scaleTransform.addChild(locationTransforms[i]);
        }

        alphaBetaTransform = new idx3d_Group();
        alphaBetaTransform.addChild(scaleTransform);
        scene.addChild(alphaBetaTransform);
        scene.addCamera("Front", idx3d_Camera.FRONT());
        scene.camera("Front").setPos(0, 0, -4.9f);
        scene.camera("Front").setFov(40f);

        scene.addCamera("Rear", idx3d_Camera.FRONT());
        scene.camera("Rear").setPos(0, 0, 4.9f);
        scene.camera("Rear").setFov(40f);
        scene.camera("Rear").roll((float) Math.PI);

        //scene.environment.ambient = 0x0;
        //scene.environment.ambient = 0xcccccc;
        scene.environment.ambient = 0x777777;
        //scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,-0.5f,1f),0x888888,144,120));
        //scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(1f,-1f,1f),0x888888,144,120));
        // scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(1f,1f,1f),0x222222,100,40));
        //scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,1f,1f),0x222222,100,40));
        // scene.addLight("Light3",new idx3d_Light(new idx3d_Vector(-1f,2f,1f),0x444444,200,120));
        scene.addLight("KeyLight", new idx3d_Light(new idx3d_Vector(1f, -1f, 1f), 0xffffff, 0xffffff, 100, 100));
        scene.addLight("FillLightRight", new idx3d_Light(new idx3d_Vector(-1f, 0f, 1f), 0x888888, 50, 50));
        scene.addLight("FillLightDown", new idx3d_Light(new idx3d_Vector(0f, 1f, 1f), 0x666666, 50, 50));

        if (sharedLightmap == null) {
            sharedLightmap = scene.getLightmap();
        } else {
            scene.setLightmap(sharedLightmap);
        }

    }

    @Override
    public abstract int getPartIndexForStickerIndex(int stickerIndex);

    @Override
    protected abstract int getPartFaceIndexForStickerIndex(int stickerIndex);

    protected abstract int getStickerIndexForPart(int part, int orientation);

    @Override
    protected abstract int getStickerCount();

    protected abstract void initEdges();

    protected abstract void initCorners();

    protected abstract void initSides();

    protected void initCenter() {
        idx3d_Object cylinder1, cylinder2, cylinder3;

        cylinder1 = idx3d_ObjectFactory.CYLINDER(18, 4.5f, 12);
        cylinder2 = idx3d_ObjectFactory.CYLINDER(18, 4.5f, 12);
        cylinder3 = idx3d_ObjectFactory.CYLINDER(18, 4.5f, 12);

        cylinder2.rotate((float) (Math.PI / 2), 0f, 0f);
        cylinder3.rotate(0f, 0f, (float) (Math.PI / 2));

        cylinder2.matrixMeltdown();
        cylinder3.matrixMeltdown();
        cylinder1.incorporateGeometry(cylinder2);
        cylinder1.incorporateGeometry(cylinder3);

        idx3d_Object object3D = cylinder1;
        object3D.material = new idx3d_InternalMaterial();
        parts[centerOffset] = object3D;
    }

    protected void initTransforms() {
        //sceneTransform = new TransformNode();

        /*
         * Corners
         *             +---+---+---+
         *          ulb|4.0|   |2.0|ubr
         *             +---+   +---+
         *             |     1     |
         *             +---+   +---+
         *          ufl|6.0|   |0.0|urf
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         * |4.1|   |6.2|6.1|   |0.2|0.1|   |2.2|2.1|   |4.2|
         * +---+   +---+---+   +---+---+   +---+---+   +---+
         * |     3     |     2     |     0     |     5     |
         * +---+   +---+---+   +---+---+   +---+---+   +---+
         * |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|3.2|   |5.1|
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         *          dlf|7.0|   |1.0|dfr
         *             +---+   +---+
         *             |     4     |
         *             +---+   +---+
         *          dbl|5.0|   |3.0|drb
         *             +---+---+---+
         */
        // Move all corner parts to up front left (ufl) and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            explosionTransforms[index] = new idx3d_Group();
            explosionTransforms[index].rotate(HALF_PI, 0, 0);
            explosionTransforms[index].shift(-18f, 18f, -18f);
            explosionTransforms[index].addChild(parts[index]);
            identityVertexMatrix[index] = new idx3d_Matrix();
        }

        // 0:urf
        identityVertexMatrix[cornerOffset + 0].rotate(0, -HALF_PI, 0);
        // 1:dfr
        identityVertexMatrix[cornerOffset + 1].rotate(0, 0, PI);
        // 2:ubr
        identityVertexMatrix[cornerOffset + 2].rotate(0, PI, 0);
        // 3:drb
        identityVertexMatrix[cornerOffset + 3].rotate(0, 0, PI);
        identityVertexMatrix[cornerOffset + 3].rotate(0, -HALF_PI, 0);
        // 4:ulb
        identityVertexMatrix[cornerOffset + 4].rotate(0, HALF_PI, 0);
        // 5:dbl
        identityVertexMatrix[cornerOffset + 5].rotate(PI, 0, 0);
        // 6:ufl
        //--no transformation---
        // 7:dlf
        identityVertexMatrix[cornerOffset + 7].rotate(0, HALF_PI, 0);
        identityVertexMatrix[cornerOffset + 7].rotate(PI, 0, 0);
        //


        /**
         * Edges
         *             +---+---+---+
         *             |   |3.1|   |
         *             +--- --- ---+
         *             |6.0| u |0.0|
         *             +--- --- ---+
         *             |   |9.1|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         * |   |6.1|   |   |9.0|   |   |0.1|   |   |3.0|   |
         * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
         * |7.0| l 10.0|10.1 f |1.1|1.0| r |4.0|4.1| b |7.1|
         * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
         * |   |8.1|   |   |11.0   |   |2.1|   |   |5.0|   |
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         *             |   |11.1   |
         *             +--- --- ---+
         *             |8.0| d |2.0|
         *             +--- --- ---+
         *             |   |5.1|   |
         *             +---+---+---+
         */
        // Move all edge parts to front up (fu) and then rotate them in place
        for (int i = 0; i < 12; i++) {
            int index = edgeOffset + i;
            explosionTransforms[index] = new idx3d_Group();
            explosionTransforms[index].rotate(0, PI, 0);
            explosionTransforms[index].shift(0f, 18f, -18f);
            explosionTransforms[index].addChild(parts[index]);
            identityVertexMatrix[index] = new idx3d_Matrix();
        }
        // ur
        identityVertexMatrix[edgeOffset + 0].rotate(0, HALF_PI, 0f);
        identityVertexMatrix[edgeOffset + 0].rotate(0, 0, HALF_PI);
        // rf
        identityVertexMatrix[edgeOffset + 1].rotate(0, -HALF_PI, 0);
        identityVertexMatrix[edgeOffset + 1].rotate(HALF_PI, 0, 0);
        // dr
        identityVertexMatrix[edgeOffset + 2].rotate(0, -HALF_PI, HALF_PI);
        // bu
        identityVertexMatrix[edgeOffset + 3].rotate(0, PI, 0);
        // rb
        identityVertexMatrix[edgeOffset + 4].rotate(0, 0, HALF_PI);
        identityVertexMatrix[edgeOffset + 4].rotate(0, -HALF_PI, 0);
        // bd
        identityVertexMatrix[edgeOffset + 5].rotate(PI, 0, 0);
        // ul
        identityVertexMatrix[edgeOffset + 6].rotate(-HALF_PI, -HALF_PI, 0);
        // lb
        identityVertexMatrix[edgeOffset + 7].rotate(0, 0, -HALF_PI);
        identityVertexMatrix[edgeOffset + 7].rotate(0, HALF_PI, 0);
        // dl
        identityVertexMatrix[edgeOffset + 8].rotate(HALF_PI, HALF_PI, 0);
        // fu
        //--no transformation--
        // lf
        identityVertexMatrix[edgeOffset + 10].rotate(0, 0, HALF_PI);
        identityVertexMatrix[edgeOffset + 10].rotate(0, HALF_PI, 0);
        // fd
        identityVertexMatrix[edgeOffset + 11].rotate(0, 0, PI);

        /* Sides
         *             +------------+
         *             |     .1     |
         *             |    ---     |
         *             | .0| 1 |.2  |
         *             |    ---     |
         *             |     .3     |
         * +-----------+------------+-----------+-----------+
         * |     .0    |     .2     |     .3    |    .1     |
         * |    ---    |    ---     |    ---    |    ---    |
         * | .3| 3 |.1 | .1| 2 |.3  | .2| 0 |.0 | .0| 5 |.2 |
         * |    ---    |    ---     |    ---    |    ---    |
         * |     .2    |    .0      |     .1    |     .3    |
         * +-----------+------------+-----------+-----------+
         *             |     .0     |
         *             |    ---     |
         *             | .3| 4 |.1  |
         *             |    ---     |
         *             |     .2     |
         *             +------------+
         */
        // Move all side parts to front
        for (int i = 0; i < 6; i++) {
            int index = sideOffset + i;
            explosionTransforms[index] = new idx3d_Group();
            explosionTransforms[index].rotate(0, PI, 0);
            explosionTransforms[index].shift(0f, 0f, -18f);
            explosionTransforms[index].addChild(parts[index]);
            identityVertexMatrix[index] = new idx3d_Matrix();
        }

        // r
        identityVertexMatrix[sideOffset + 0].rotate(0f, 0f, -HALF_PI);
        identityVertexMatrix[sideOffset + 0].rotate(0f, -HALF_PI, 0f);
        // u
        identityVertexMatrix[sideOffset + 1].rotate(0f, 0f, HALF_PI);
        identityVertexMatrix[sideOffset + 1].rotate(-HALF_PI, 0f, 0f);
        // f
        //--no transformation--
        // l
        identityVertexMatrix[sideOffset + 3].rotate(0f, 0f, PI);
        identityVertexMatrix[sideOffset + 3].rotate(0f, HALF_PI, 0f);
        // d
        identityVertexMatrix[sideOffset + 4].rotate(0f, 0f, PI);
        identityVertexMatrix[sideOffset + 4].rotate(HALF_PI, 0f, 0f);
        // b
        identityVertexMatrix[sideOffset + 5].rotate(0f, 0f, HALF_PI);
        identityVertexMatrix[sideOffset + 5].rotate(0f, PI, 0f);


        // Center part
        identityVertexMatrix[centerOffset] = new idx3d_Matrix();
        explosionTransforms[centerOffset] = new idx3d_Group();
        explosionTransforms[centerOffset].addChild(parts[centerOffset]);

        // copy all vertex locationTransforms into the normal locationTransforms.
        // create the locationTransforms
        for (int i = 0; i < partCount; i++) {
            identityNormalMatrix[i] = identityVertexMatrix[i].getClone();
            locationTransforms[i] = new idx3d_Group();
            locationTransforms[i].matrix.set(identityVertexMatrix[i]);
            locationTransforms[i].normalmatrix.set(identityNormalMatrix[i]);
            locationTransforms[i].addChild(explosionTransforms[i]);
        }
    }
    /**
     * These matrices are reused on each invocation of validateTwist.
     */
    private idx3d_Matrix updateTwistRotation = new idx3d_Matrix();
    private idx3d_Matrix updateTwistOrientation = new idx3d_Matrix();

    @Override
    public void cubeTwisted(CubeEvent evt) {
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
        };
        interpolator.setTimespan(Math.abs(angle) * attributes.getTwistDuration());
        if (! isAnimated) {
            interpolator.finish(System.currentTimeMillis());
        } else {
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

    @Override
    public void validateTwist(int[] partIndices, int[] locations, int[] orientations, int length, int axis, int angle, float alpha) {
        synchronized (getCube()) {
            // idx3d_Matrix rotation = new idx3d_Matrix();
            idx3d_Matrix rotation = updateTwistRotation;
            rotation.reset();
            float rad = (HALF_PI * angle * (1f - alpha));
            switch (axis) {
                case 0:
                    rotation.rotate(rad, 0, 0);
                    break;
                case 1:
                    rotation.rotate(0, -rad, 0);
                    break;
                case 2:
                    rotation.rotate(0, 0, -rad);
                    break;
            }

            //idx3d_Matrix orientationMatrix = new idx3d_Matrix();
            idx3d_Matrix orientationMatrix = updateTwistOrientation;
            for (int i = 0; i < length; i++) {
                orientationMatrix.reset();
                if (partIndices[i] < edgeOffset) {
                    switch (orientations[i]) {
                        case 0:
                            break;
                        case 1:
                            orientationMatrix.rotate(-HALF_PI, 0, HALF_PI);
                            break;
                        case 2:
                            orientationMatrix.rotate(0, 0, -HALF_PI);
                            orientationMatrix.rotate(HALF_PI, 0, 0);
                            break;
                    }
                } else if (partIndices[i] < sideOffset) {
                    orientationMatrix.reset();
                    if (orientations[i] == 1) {
                        orientationMatrix.rotate(0, 0, PI);
                        orientationMatrix.rotate(HALF_PI, 0, 0);
                    }
                } else if (partIndices[i] < centerOffset) {
                    if (orientations[i] > 0) {
                        orientationMatrix.rotate(0, 0, HALF_PI * orientations[i]);
                    }
                }
                parts[partIndices[i]].setTransform(orientationMatrix);
                idx3d_Group transform = locationTransforms[partIndices[i]];
                transform.setTransform(identityVertexMatrix[locations[i]]);
                transform.transform(rotation);
            }
        }
        fireStateChanged();
    }
}
