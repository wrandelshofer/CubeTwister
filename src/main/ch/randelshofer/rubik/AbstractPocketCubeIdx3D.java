/*
 * @(#)AbstractPocketCubeIdx3D.java  2.0  2008-01-06
 *
 * Copyright (c) 2005-2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.gui.*;
import org.monte.media.*;
import idx3d.*;
import javax.swing.SwingUtilities;

/**
 * Abstract base class for the geometrical representation of a {@link PocketCube}
 * using the Idx3D engine.
 *
 * Each part is represented by a idx3d_Object. The idx3d_Object is a child of
 * an idx3d_Group which represents the explosion factor. The idx3d_Group is
 * a child of the idx3d_Scene.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2008-01-06 Adapted to changes in AbstractCube.
 * <br>1.1 2007-09-09 Use SplineInterpolator for cube twists. 
 * <br>1.0 2005-12-29 Created.
 */
public abstract class AbstractPocketCubeIdx3D extends AbstractCubeIdx3D {
    /**
     * A cube part has a side length of 18 mm.
     */
    protected final static float PART_LENGTH = 18f;

    /** Creates a new instance. */
    public AbstractPocketCubeIdx3D() {
        super(2, 8, 0, 0, 1);
        init();
    }

    protected void init() {
        explosionShift = 9f;
        initCorners();
        initCenter();
        initTransforms();
        initScene();
        initActions(scene);
        setCube(new PocketCube());
        setAttributes(createAttributes());
    }

    private void initScene() {
        // FIXME move scene creation to super class
        scene = new idx3d_Scene() {

            @Override
            public boolean isAdjusting() {
                return super.isAdjusting() || isAnimating() || isInStartedPlayer() || AbstractPocketCubeIdx3D.this.isAdjusting();
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
        //scene.useIdBuffer(true);
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

    protected abstract void initCorners();

    protected void initCenter() {
        /*
        // FIXME - Should be segmented into 8 parts
        idx3d_Object cylinder1, cylinder2, cylinder3;

        cylinder1 = idx3d_ObjectFactory.CYLINDER(4.5f, 13.5f, 24);
        cylinder2 = idx3d_ObjectFactory.CYLINDER(4.5f, 13.5f, 24);
        cylinder3 = idx3d_ObjectFactory.CYLINDER(4.5f, 13.5f, 24);

        cylinder2.rotate((float) (Math.PI / 2), 0f, 0f);
        cylinder3.rotate(0f, 0f, (float) (Math.PI / 2));

        cylinder2.matrixMeltdown();
        cylinder3.matrixMeltdown();
        cylinder1.incorporateGeometry(cylinder2);
        cylinder1.incorporateGeometry(cylinder3);

        idx3d_Object object3D = cylinder1;
        object3D.material = new idx3d_InternalMaterial();
        parts[centerOffset] = object3D;
         */
        idx3d_Object object3D = idx3d_ObjectFactory.SPHERE(9, 18);
        object3D.material = new idx3d_InternalMaterial();
        parts[centerOffset] = object3D;
    }

    protected void initTransforms() {
        //sceneTransform = new TransformNode();

        /*
         * Corners
         *             +---+---+---+
         *             |4.0|   |2.0|
         *             +---+   +---+
         *             |     1     |
         *             +---+   +---+
         *             |6.0|   |0.0|
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         * |4.1|   |6.2|6.1|   |0.2|0.1|   |2.2|2.1|   |4.2|
         * +---+   +---+---+   +---+---+   +---+---+   +---+
         * |     3     |     2     |     0     |     5     |
         * +---+   +---+---+   +---+---+   +---+---+   +---+
         * |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|3.2|   |5.1|
         * +---+---+---+---+---+---+---+---+---+---+---+---+
         *             |7.0|   |1.0|
         *             +---+   +---+
         *             |     4     |
         *             +---+   +---+
         *             |5.0|   |3.0|
         *             +---+---+---+
         */
        // Move all corner parts to up front left (ufl).
        for (int i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            explosionTransforms[index] = new idx3d_Group();
            explosionTransforms[index].rotate(HALF_PI, 0, 0);
            explosionTransforms[index].shift(-9f, 9f, -9f);
            explosionTransforms[index].addChild(parts[index]);
            identityVertexMatrix[index] = new idx3d_Matrix();
        }

        // urf
        identityVertexMatrix[cornerOffset + 0].rotate(0, -HALF_PI, 0);
        // dfr
        identityVertexMatrix[cornerOffset + 1].rotate(0, 0, PI);
        // ubr
        identityVertexMatrix[cornerOffset + 2].rotate(0, PI, 0);
        // drb
        identityVertexMatrix[cornerOffset + 3].rotate(0, 0, PI);
        identityVertexMatrix[cornerOffset + 3].rotate(0, -HALF_PI, 0);
        // ulb
        identityVertexMatrix[cornerOffset + 4].rotate(0, HALF_PI, 0);
        // dbl
        identityVertexMatrix[cornerOffset + 5].rotate(PI, 0, 0);
        // ufl
        //--no transformation---
        // dlf
        identityVertexMatrix[cornerOffset + 7].rotate(0, HALF_PI, 0);
        identityVertexMatrix[cornerOffset + 7].rotate(PI, 0, 0);
        //

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

    protected abstract void initActions(idx3d_Scene scene);

    @Override
    protected float getUnitScaleFactor() {
        return 0.019f * 54f / (PART_LENGTH * layerCount);
    }

    @Override
    public void cubeTwisted(CubeEvent evt) {
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

        // Create interpolator
        final int finalCount = count;
        Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f) {

            protected void update(float value) {
                //validateTwist(finalIndices, locationTransforms, normaltransforms, axis, angle, value);
                validateTwist(partIndices, locations, orientations, finalCount, axis, angle, value);
                fireStateChanged();
            }

            @Override
            public boolean isSequential(Interpolator that) {
                return (that.getClass() == this.getClass());
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

    public void validateTwist(int[] partIndices, int[] locations, int[] orientations, int length, int axis, int angle, float alpha) {
        synchronized (getCube()) {
            idx3d_Matrix rotation = new idx3d_Matrix();
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

            idx3d_Matrix orientationMatrix = new idx3d_Matrix();
            for (int i = 0; i < length; i++) {
                orientationMatrix.reset();
                if (partIndices[i] < centerOffset) {
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
                }
                parts[partIndices[i]].setTransform(orientationMatrix);
                idx3d_Group transform = locationTransforms[partIndices[i]];
                transform.setTransform(identityVertexMatrix[locations[i]]);
                transform.transform(rotation);
            }
        }
        fireStateChanged();
    }
    /*
    protected void updateExplosionFactor(float factor) {
    float shift = explosionShift * factor;
    int index = centerOffset;
    explosionTransforms[index].resetTransform();
    explosionTransforms[index].rotate(HALF_PI, 0, 0);
    explosionTransforms[index].shift(-shift, shift, -shift);
    super.updateExplosionFactor(factor);
    }*/
}
