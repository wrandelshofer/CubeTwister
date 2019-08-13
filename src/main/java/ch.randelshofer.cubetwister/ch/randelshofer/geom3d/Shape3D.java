/* @(#)Shape3D.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.geom3d;

import java.awt.event.*;
import java.awt.Color;
import java.util.*;

/**
 * Represents a 3 dimensional shape consisting of an
 * arbitrary number of planar and convex vertices.
 *
 * @author Werner Randelshofer
 */
public class Shape3D
        implements Node3D {

    /**
     * Coordinates used by this shape. Each group of three
     * entries in this array describe a vector x, y, z in
     * three dimensional space.
     */
    private float[] coords;
    /**
     * Transformed coordinates. Each group of three
     * entries in this array describe a vector x, y, z in
     * three dimensional space.
     */
    private float[] tc;
    /**
     * Each entry in the first dimension of this array describes
     * a face of the shape. Each entry in the second dimension of
     * this array represents a vertex at the borderline of the face.
     * Each vertex is the index of a vector in the coords array.
     * The vertices of the face must be indicated in clockwise
     * direction. All vertices must be on the same plane and
     * the boundary of the face must be convex.
     */
    private int[][] vertices;
    private Color[][] colors;
    private boolean isVisible = true;
    private boolean isWireframe = false;
    private boolean isReduced = false;
    private int reducedFaceCount;
    private Face3D[] faces3D;
    private Transform3D transform = new Transform3D();

    /**
     * Creates a new Shape3D object.
     *
     * @param coords    Coordinate data for the shape. Each group of
     *                  three entries describe a vector x, y, z
     *                  in three dimensional space.
     * @param faces     Faces of the shape. Each entry in the first dimension
     *                  of this array describes a face. Each entry in the second
     *                  dimension of this array represents a vertex at the
     *                  borderline of the face. Each vertex is the index of a
     *                  vector in the coords array. The vertices of the face
     *                  must be indicated in clockwise direction. All vertices
     *                  must be on the same plane and the boundary of the face
     *                  must be convex.
     * @param colors    The colors for each face. For each face, two colors
     *                  can be specified: a fill color (index 0 of the inner array)
     *                  and a wireframe color (index 1 of the inner array).
     */
    public Shape3D(float[] coords, int[][] faces, Color[][] colors) {
        this(coords, faces, colors, faces.length);
    }

    /**
     * Creates a new Shape3D object.
     *
     * @param coords    Coordinate data for the shape. Each group of
     *                  three entries describe a vector x, y, z
     *                  in three dimensional space.
     * @param faces     Faces of the shape. Each entry in the first dimension
     *                  of this array describes a face. Each entry in the second
     *                  dimension of this array represents a vertex at the
     *                  borderline of the face. Each vertex is the index of a
     *                  vector in the coords array. The vertices of the face
     *                  must be indicated in clockwise direction. All vertices
     *                  must be on the same plane and the boundary of the face
     *                  must be convex.
     * @param colors    The colors for each face. For each face, two colors
     *                  can be specified: a fill color (index 0 of the inner array)
     *                  and a wireframe color (index 1 of the inner array).
     * @param reducedFaceCount The shape will only draw reducedFaceCount colors,
     *                  if the attribute isReduced is set to true.
     */
    public Shape3D(float[] coords, int[][] faces, Color[][] colors, int reducedFaceCount) {
        this.coords = coords;
        this.vertices = faces;
        this.colors = colors;
        this.reducedFaceCount = reducedFaceCount;
    }

    public float[] getCoords() {
        return coords;
    }

    public int[][] getVertices() {
        return vertices;
    }

    public Face3D[] getFaces() {
        createFaces();
        return faces3D;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean b) {
        isVisible = b;
    }

    public boolean isRecuced() {
        return isReduced;
    }

    public void setReduced(boolean b) {
        isReduced = b;
    }

    public void setTransform(Transform3D t) {
        transform = t;
    }

    private void createFaces() {
        if (faces3D == null) {
            faces3D = new Face3D[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                faces3D[i] = new Face3D(
                        coords,
                        vertices[i],
                        (isWireframe) ? new Color[]{null, colors[i][1]} : colors[i]);
            }
        }
    }

    /**
     * Adds all vertices to the vector that are visible when
     * this Shape3D is transformed by the given Transform3D.
     * The transform is applied to the vertices before they are
     * added to the list.
     *
     * @param   v       The vector to which the vertices are added.
     * @param   t       This transform is applied to the vertices
     *                  before they are tested for visibility
     *                  and added to the list.
     * @param   observer Coords of the observer.
     */
    public void addVisibleFacesTo(List<Face3D> v, Transform3D t, Point3D observer) {
        Transform3D t2;
        if (isVisible) {
            t2 = (Transform3D) transform.clone();
            t2.concatenate(t);
            if (tc == null || tc.length != coords.length) {
                tc = new float[coords.length];
            }
            t2.transform(coords, 0, tc, 0, coords.length / 3);

            createFaces();
            for (int i = 0, n = (isReduced) ? reducedFaceCount : vertices.length; i < n; i++) {
                //for (int i=0; i < vertices.length; i++) {
                faces3D[i].setCoords(tc);
                if (faces3D[i].isVisible(observer)) {
                    v.add(faces3D[i]);
                }
            }
        }
    }

    public void setAction(int face, ActionListener action) {
        createFaces();
        faces3D[face].setAction(action);
    }

    public Color getFillColor(int face) {
       return colors[face][0];
    }
    public void setFillColor(int face, Color color) {
        colors[face][0] = color;
    }

    public void setBorderColor(int face, Color color) {
        colors[face][1] = color;
    }

    public int getFaceCount() {
        return vertices.length;
    }

    public boolean isWireframe() {
        return isWireframe;
    }

    public void setWireframe(boolean b) {
        isWireframe = b;
        faces3D = null;
    }
}
