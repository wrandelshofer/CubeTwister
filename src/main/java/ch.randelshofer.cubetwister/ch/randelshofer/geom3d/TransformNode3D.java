/* @(#)TransformNode3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import java.util.*;
/**
 * This node applies its Transform3D to all its children.
 *
 * @author Werner Randelshofer
 */
public class TransformNode3D implements Node3D {
    private ArrayList<Node3D> children = new ArrayList<Node3D>();
    private Transform3D transform = new Transform3D();
    private boolean isVisible = true;

    public void addChild(Node3D child) {
        children.add(child);
    }
    public Node3D getChild(int index) {
        return children.get(index);
    }
    public void setTransform(Transform3D transform) {
        this.transform = transform;
    }

    public Transform3D getTransform() {
        return transform;
    }
    public void setVisible(boolean b) {
        isVisible = b;
    }
    /**
     * Adds all faces to the vector that are visible when
     * this Shape3D is transformed by the given Transform3D.
     * The transform is applied to the faces before they are
     * added to the list.
     *
     * @param   v       The list to which the faces are added.
     * @param   t       This transform is applied to the faces
     *                  before they are tested for visibility
     *                  and added to the vector.
     * @param   observer Coords of the observer.
     */
    public void addVisibleFacesTo(List<Face3D> v, Transform3D t, Point3D observer) {
        if (isVisible) {
            Transform3D t2 = (Transform3D) transform.clone();
            t2.concatenate(t);
            for (Node3D child : children) {
                child.addVisibleFacesTo(v, t2, observer);
            }
        }
    }

}
