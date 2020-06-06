package ch.randelshofer.geom3d;

public class FaceNode {
    public final Point3D vertex;
    public final Point3D normal;
    public final Point3D texture;

    public FaceNode(Point3D vertex, Point3D texture, Point3D normal) {
        this.vertex = vertex;
        this.normal = normal;
        this.texture = texture;
    }

    public Point3D getVertex() {
        return vertex;
    }

    public Point3D getNormal() {
        return normal;
    }

    /**
     * Creates a copy of this face node with the given normal.
     *
     * @param withNormal the new normal
     * @return a copy of this face node with the given normal
     */
    public FaceNode withNormal(Point3D withNormal) {
        return new FaceNode(vertex, texture, withNormal);
    }

    /**
     * Creates a copy of this face node with the given vertex.
     *
     * @param withVertex the new normal
     * @return a copy of this face node with the given normal
     */
    public FaceNode withVertex(Point3D withVertex) {
        return new FaceNode(withVertex, texture, normal);
    }

    @Override
    public String toString() {
        return "" +
                "" + vertex.x + " " + vertex.y + " " + vertex.z +
                "/" + (texture == null ? "" : texture.x + " " + texture.y + " " + texture.z) +
                "/" + (normal == null ? "" : normal.x + " " + normal.y + " " + normal.z) +
                "";
    }
}
