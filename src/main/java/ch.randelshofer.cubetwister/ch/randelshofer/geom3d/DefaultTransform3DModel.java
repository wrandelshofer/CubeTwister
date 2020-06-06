/* @(#)TDefaultransform3DModel.java
 * Copyright (c) 1998 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import ch.randelshofer.beans.AbstractStateModel;
import org.jhotdraw.annotation.Nonnull;
//import javax.swing.event.*;
/**
 * The default implementation of the Transform3DModel interface.
 * This is a Transform3D object that fires change events, when its matrix
 * changes.
 *
 * @author Werner Randelshofer
 */
public class DefaultTransform3DModel
        extends AbstractStateModel
        implements Transform3DModel {
    private Transform3D transform;

    /**
     * Creates a new instance.
     */
    public DefaultTransform3DModel() {
        transform = new Transform3D();
    }

    /**
     * Constructs a new transform from 12 double precision values
     * representing the 12 specifiable entries of the 4x4 transformation matrix.
     */
    public DefaultTransform3DModel(double m00, double m10, double m20, double m30, double m01, double m11, double m21, double m31, double m02, double m12, double m22, double m32) {
        transform = new Transform3D(
                m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32
        );
    }

    /**
     * Constructs a new transform from 12 double precision values
     * representing the 12 specifiable entries of the 4x4 transformation matrix.
     */
    public DefaultTransform3DModel(@Nonnull double[][] matrix) {
        transform = new Transform3D(matrix);
    }

    /**
     * Resets this transform to the Identity transform.
     */
    public void setToIdentity() {
        transform.setToIdentity();
        fireStateChanged();
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ 1   0         0        0 ]
     *    [ 0   cos(rx)    sin(rx)   0 ]
     *    [ 0   -sin(rx)   cos(rx)   0 ]
     *      [ 0   0         0        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     * @param rx Rotation along the x axis in radians.
     */
    public void rotateX(double rx) {
        transform.rotateX(rx);
        fireStateChanged();
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(ry)   0   -sin(ry)   0 ]
     *    [ 0        1   0         0 ]
     *    [ sin(ry)   0   cos(ry)    0 ]
     *      [ 0        0   0         1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     * @param ry Rotation along the y axis in radians.
     */
    public void rotateY(double ry) {
        transform.rotateY(ry);
        fireStateChanged();
      }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(rz)    sin(rz)   0   0 ]
     *    [ -sin(rz)   cos(rz)   0   0 ]
     *    [ 0         0        1   0 ]
     *      [ 0         0        0   1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     * @param rz Rotation along the z axis in radians.
     */
    public void rotateZ(double rz) {
        transform.rotateZ(rz);
        fireStateChanged();
    }

    /**
     * Concatenates this transform with a scaling transformation.
     * This is equivalent to calling concatenate(S), where S is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   sx   0    0    0   ]
     *    [   0    sy   0    0   ]
     *    [   0    0    sz   0   ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void scale(double sx, double sy, double sz) {
        transform.scale(sx, sy, sz);
        fireStateChanged();
    }

    /**
     * Concatenates this transform with a translation transformation.
     * This is equivalent to calling concatenate(T), where T is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   1    0    0    tx  ]
     *    [   0    1    0    ty  ]
     *    [   0    0    1    tz  ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void translate(double tx, double ty, double tz) {
        transform.translate(tx, ty, tz);
        fireStateChanged();
    }

    /**
     * Concatenates a transform Tx to this transform Cx.
     * Cx is updated to perform the combined transformation.
     * Transforming a point p by the updated transform Cx' is
     * equivalent to first transforming p by Tx and then transforming
     * the result by the original transform Cx. In other words,
     * Cx'(p) = Cx(Tx(p)).  In matrix notation, if this transform Cx
     * is represented by the matrix [this] and Tx is represented by
     * the matrix [Tx], then this method does the following:
     * <pre>
     *    [this] = [this] x [Tx]
     * </pre>
     * @param t The transform object to be concatenated with
     * this transform object.
     */
    public void concatenate(@Nonnull Transform3D t) {
        transform.concatenated(t);
        fireStateChanged();
    }

    /**
     * Sets this transform to a copy of the transform in the specified
     * <code>Transform3D</code> object.
     *
     * @param t the <code>Transform3D</code> object from which to
     *          copy the transform
     */
    public void setTransform(@Nonnull Transform3D t) {
        transform.setTransform(t);
        fireStateChanged();
    }

    /**
     * Returns the current state of the model.
     */
    @Nonnull
    public Transform3D getTransform() {
        return (Transform3D) transform.clone();
    }

    /**
     * Copies the current state of the model
     * into the given Transform3D object and
     * returns it.
     */
    @Nonnull
    public Transform3D getTransform(@Nonnull Transform3D t) {
        t.setTransform(transform);
        return t;
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate0(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *   [ cos(rz)*cos(ry)     cos(rz)*sin(ry)*sin(rx)     cos(rz)*-sin(ry)*cos(rx)    0 ]
     *                           +sin(rz)*cos(rx)            +sin(rz)*-sin(ry)*cos(rx)
     *
     *   [ -sin(rz)*cos(ry)    -sin(rz)*sin(ry)*sin(rx)    -sin(rz)*-sin(ry)*cos(rx)   0 ]
     *                           +cos(rz)*cos(rx)            +cos(rz)*sin(rx)
     *
     *   [ sin(ry)             cos(ry)*-sin(rx)            cos(ry)*cos(rx)            0 ]
     *
     *     [ 0                   0                           0                        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     * @param rx Rotation along the x axis in radians.
     * @param ry Rotation along the y axis in radians.
     * @param rz Rotation along the z axis in radians.
     */
    public void rotate(double rx, double ry, double rz) {
        transform.rotate(rx, ry, rz);
        fireStateChanged();
    }
}
