/*
 * @(#)Transform3DModel.java  2.0  2007-11-15
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.geom3d;

import javax.swing.event.ChangeListener;

/**
 * The Transform3DModel wraps a Transform3D object and makes
 * it observable.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-11-15 Upgraded to Java 1.4. 
 * <br>1.0 2002-05-09 Created.
 */
public interface Transform3DModel {
    /**
     * Resets this transform to the Identity transform.
     */
    public void setToIdentity();

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate0(R), where R is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [ cos(rz)*cos(ry)     cos(rz)*sin(ry)*sin(rx)     cos(rz)*-sin(ry)*cos(rx)    0 ]
     *                            +sin(rz)*cos(rx)            +sin(rz)*-sin(ry)*cos(rx)
     *
     *    [ -sin(rz)*cos(ry)    -sin(rz)*sin(ry)*sin(rx)    -sin(rz)*-sin(ry)*cos(rx)   0 ]
     *                            +cos(rz)*cos(rx)            +cos(rz)*sin(rx)
     *
     *    [ sin(ry)             cos(ry)*-sin(rx)            cos(ry)*cos(rx)            0 ]
     *
     *      [ 0                   0                           0                        1 ]
     * </pre>
     * Positive values rotate counterclockwise.
     * @param rx Rotation along the x axis in radians.
     * @param ry Rotation along the y axis in radians.
     * @param rz Rotation along the z axis in radians.
     */
    public void rotate(double rx, double ry, double rz);
    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate0(R), where R is an
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
    public void rotateX(double rx);

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate0(R), where R is an
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
    public void rotateY(double ry);
    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate0(R), where R is an
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
    public void rotateZ(double rz);
    /**
     * Concatenates this transform with a scaling transformation.
     * This is equivalent to calling concatenate0(S), where S is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   sx   0    0    0   ]
     *    [   0    sy   0    0   ]
     *    [   0    0    sz   0   ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void scale(double sx, double sy, double sz);
    /**
     * Concatenates this transform with a translation transformation.
     * This is equivalent to calling concatenate0(T), where T is an
     * Transform3D represented by the following matrix:
     * <pre>
     *    [   1    0    0    tx  ]
     *    [   0    1    0    ty  ]
     *    [   0    0    1    tz  ]
     *    [   0    0    0    1   ]
     * </pre>
     */
    public void translate(double tx, double ty, double tz);
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
    public void concatenate(Transform3D t);
    /**
     * Returns the current state of the model.
     */
    public Transform3D getTransform();

    /**
     * Copies the current state of the model
     * into the given Transform3D object and
     * returns it.
     */
    public Transform3D getTransform(Transform3D t);
    
    /**
     * Sets this transform to a copy of the transform in the specified
     * <code>Transform3D</code> object.
     * @param Tx the <code>Transform3D</code> object from which to
     * copy the transform
     */
    public void setTransform(Transform3D Tx);

    /**
     * Adds a ChangeListener to the listener list.
     */
    public void addChangeListener(ChangeListener l);
    
    /**
     * Removes a ChangeListener from the listener list.
     */
    public void removeChangeListener(ChangeListener l);
}
