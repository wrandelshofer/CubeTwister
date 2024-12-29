/*
 * @(#)AbstractProfessorCubeIdx3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeEvent;
import ch.randelshofer.rubik.cube.ProfessorCube;
import idx3d.idx3d_Camera;
import idx3d.idx3d_Group;
import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Light;
import idx3d.idx3d_Matrix;
import idx3d.idx3d_Object;
import idx3d.idx3d_ObjectFactory;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Vector;
import org.jhotdraw.annotation.Nonnull;
import org.monte.media.av.Interpolator;
import org.monte.media.interpolator.SplineInterpolator;

import javax.swing.SwingUtilities;

/**
 * Abstract base class for the geometrical representation of a {@link ProfessorCube}
 * using the Idx3D engine.
 *
 * @author  Werner Randelshofer
 */
public abstract class AbstractProfessorCubeIdx3D extends AbstractCubeIdx3D {
    /**
     * A cube part has a side length of 13 mm.
     */
    protected final static float PART_LENGTH = 13f;
    /**
     * The beveled edge of a cube part has a length of 1 mm.
     */
    protected final static float BEVEL_LENGTH = 1f;

    /** Creates a new instance. */
    public AbstractProfessorCubeIdx3D() {
        super(5, 8, 3 * 12, 3 * 3 * 6, 1);
        init();
    }

    public void init() {
        explosionShift = 2f * PART_LENGTH;
        setUnitScaleFactor(0.019f * 54f / (PART_LENGTH * layerCount));
        initEdges();
        initCorners();
        initSides();
        initCenter();
        initTransforms();
        initScene();
        initActions(scene);
        setCube(new ProfessorCube());
        setAttributes(createAttributes());
    }

    protected abstract void initEdges();

    protected abstract void initCorners();

    protected abstract void initSides();

    protected void initCenter() {
        idx3d_Object sphere;

        sphere = idx3d_ObjectFactory.SPHERE(PART_LENGTH * 1.5f, 18);

        idx3d_Object object3D = sphere;
        object3D.material = new idx3d_InternalMaterial();
        idx3d_Group group = new idx3d_Group();
        group.addChild(object3D);
        parts[centerOffset] = group;
    }

    /*
    @Override
    public float getUnitScaleFactor() {
        return 0.019f * 54f / (PART_LENGTH * layerCount);
    }*/

    protected void initTransforms() {
        /*
         * Corners
         */
        // Move all corner parts to up front left (ufl) and then rotate them in place
        for (int i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
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
        // We can clone the normalmatrix here form the vertex matrix, because
        // the vertex matrix consists of rotations only.
        for (int i = 0; i < cornerCount; i++) {
            identityNormalMatrix[i] = identityVertexMatrix[i].getClone();
        }

        /**
         * Edges
         */
        // Move all edge parts to front up (fu) and then rotate them in place
        for (int i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            idx3d_Matrix vt = new idx3d_Matrix();
            idx3d_Matrix nt = new idx3d_Matrix();
            identityVertexMatrix[index] = vt;
            identityNormalMatrix[index] = nt;
            // The vertex matrix is the same as the normal matrix, but with
            // an additional shift, which is made before the rotation.
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
                        vt.shift(PART_LENGTH, 0f, 0f);
                        break;
                    default:
                        vt.shift(-PART_LENGTH, 0f, 0f);
                        break;
                }
            }
            // Now we do the rotation with the normal matrix only
            switch (i % 12) {
                case 0: // ur
                    nt.rotate(0, HALF_PI, 0f);
                    nt.rotate(0, 0, HALF_PI);
                    break;
                case 1: // rf
                    nt.rotate(0, -HALF_PI, 0);
                    nt.rotate(HALF_PI, 0, 0);
                    break;
                case 2: // dr
                    nt.rotate(0, -HALF_PI, HALF_PI);
                    break;
                case 3: // bu
                    nt.rotate(0, PI, 0);
                    break;
                case 4: // rb
                    nt.rotate(0, 0, HALF_PI);
                    nt.rotate(0, -HALF_PI, 0);
                    break;
                case 5: // bd
                    nt.rotate(PI, 0, 0);
                    break;
                case 6: // ul
                    nt.rotate(-HALF_PI, -HALF_PI, 0);
                    break;
                case 7: // lb
                    nt.rotate(0, 0, -HALF_PI);
                    nt.rotate(0, HALF_PI, 0);
                    break;
                case 8: // dl
                    nt.rotate(HALF_PI, HALF_PI, 0);
                    break;
                case 9: // fu
                    //--no transformation--
                    break;
                case 10: // lf
                    nt.rotate(0, 0, HALF_PI);
                    nt.rotate(0, HALF_PI, 0);
                    break;
                case 11: // fd
                    nt.rotate(0, 0, PI);
                    break;
            }
            // Finally, we concatenate the rotation to the vertex matrix
            vt.transform(nt);
        }
        /* Sides
         */
        // Move all side parts to the front side and rotate them into place
        for (int i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            idx3d_Matrix vt = new idx3d_Matrix();
            idx3d_Matrix nt = new idx3d_Matrix();
            identityVertexMatrix[index] = vt;
            identityNormalMatrix[index] = nt;
            // The vertex matrix is the same as the normal matrix, but with
            // an additional shift, which is made before the rotation.
            switch (i / 6) {
                case 0 :
                //    vt.shift(0, 0, 0);
                    break;
                case 1:
                    vt.shift(-PART_LENGTH, -PART_LENGTH, 0);
                    break;
                case 2:
                    vt.shift(-PART_LENGTH, PART_LENGTH, 0);
                    break;
                case 3:
                    vt.shift(PART_LENGTH, PART_LENGTH, 0);
                    break;
                case 4:
                    vt.shift(PART_LENGTH, -PART_LENGTH, 0);
                    break;
                case 5:
                    vt.shift(0, -PART_LENGTH, 0);
                    break;
                case 6:
                    vt.shift(-PART_LENGTH, 0, 0);
                    break;
                case 7:
                    vt.shift(0, PART_LENGTH, 0);
                    break;
                case 8:
                    vt.shift(PART_LENGTH, 0, 0);
                    break;
            }

            switch (i % 6) {
                case 0 : // r
                    nt.rotate(0f, 0f, -HALF_PI);
                    nt.rotate(0f, -HALF_PI, 0f);
                    break;
                case 1 : // u
                    nt.rotate(0f, 0f, HALF_PI);
                    nt.rotate(-HALF_PI, 0f, 0f);
                    break;
                 case 2 : // f
                //--no transformation--
                    break;
                case 3 : // l
                    nt.rotate(0f, 0f, PI);
                    nt.rotate(0f, HALF_PI, 0f);
                    break;
                case 4 : // d
                    nt.rotate(0f, 0f, PI);
                    nt.rotate(HALF_PI, 0f, 0f);
                    break;
                case 5 : // b
                    nt.rotate(0f, 0f, HALF_PI);
                    nt.rotate(0f, PI, 0f);
                    break;
            }
            // Finally, we concatenate the rotation to the vertex matrix
            vt.transform(nt);
        }


        // Center part
        identityVertexMatrix[centerOffset] = new idx3d_Matrix();
        identityNormalMatrix[centerOffset] = new idx3d_Matrix();

        // copy all vertex locationTransforms into the normal locationTransforms.
        // create the locationTransforms
        for (int i = 0; i < partCount; i++) {

            locationTransforms[i] = new idx3d_Group();
            locationTransforms[i].matrix.set(identityVertexMatrix[i]);
            locationTransforms[i].normalmatrix.set(identityNormalMatrix[i]);

            explosionTransforms[i] = new idx3d_Group();
            explosionTransforms[i].addChild(parts[i]);
            locationTransforms[i].addChild(explosionTransforms[i]);
        }
    }

    protected abstract void initActions(idx3d_Scene scene);

    private void initScene() {
        // FIXME move scene creation to super class
        scene = new idx3d_Scene() {

            @Override
            public boolean isAdjusting() {
                return super.isAdjusting() || isAnimating() || isInStartedPlayer() || AbstractProfessorCubeIdx3D.this.isAdjusting();
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
        scene.setBackgroundColor(0xffffffff);

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

        scene.validate();
    }

    public void validateTwist(int[] partIndices, int[] locations, int[] orientations, int length, int axis, int angle, float alpha) {
        synchronized (getCube()) {
            idx3d_Matrix rotation = new idx3d_Matrix();
            float rad = (HALF_PI * angle * (1f - alpha));
            //float rad = (float) (HALF_PI*angle*alpha);
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
                //transform.setTransform(identityVertexMatrix[locations[i]]);
                //transform.setTransform(identityVertexMatrix[locations[i]]);
                transform.matrix.set(identityVertexMatrix[locations[i]]);
                transform.normalmatrix.set(identityNormalMatrix[locations[i]]);
                transform.transform(rotation);
            }
        }
        fireStateChanged();
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
        //int count = 0;
        final int[] locations = evt.getAffectedLocations();
        int count = locations.length;

        for (int i = 0; i < count; i++) {
            partIndices[i] = model.getPartAt(locations[i]);
            orientations[i] = model.getPartOrientation(partIndices[i]);
        }

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
