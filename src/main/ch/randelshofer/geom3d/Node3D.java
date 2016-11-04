/*
 * @(#)Node.java  2.0  2009-01-04
 *
 * Copyright (c) 2000-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.geom3d;

import java.util.*;
/**
 * Represents a node of a three dimensional universe.
 *
 * @author Werner Randelshofer
 * @version 2.0 2009-01-04 Migrated to Java 1.5.
 * <br>1.0 2008-09-16 Renamed from SceneNode to Node3D.
 * <br>0.1  2000-02-28      Created.
 */
public interface Node3D {
    /**
     * Adds all faces to the vector that are visible when
     * this Node3D is transformed by the given Transform3D.
     * The transform is applied to the faces before they are
     * added to the list.
     *
     * @param   v       The list to which the faces are added.
     * @param   t       This transform is applied to the faces
     *                  before they are tested for visibility
     *                  and added to the vector.
     * @param   observer Coords of the observer.
     */
    public void addVisibleFacesTo(List<Face3D> v, Transform3D t, Point3D observer);
}

