// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------
package idx3d;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Defines a triangle vertex.
 *
 * @version $Id$
 * hashCode() method.
 * <br>3.3 2006-02-22 Werner Randelshofer: Avoid unnecessary object creation.
 * 3.2 2003-12-21 Werner Randelshofer: Moved responsibility for texture
 * location from this class to idx3d_Triangle. Dependency note: This change also 
 * requires changes in idx3d_Triangle, idx3d_Rasterizer, idx3d_Object, 
 * idx3d_TextureProjector.
 */
public class idx3d_Vertex {
    // F I E L D S

    public idx3d_Object parent;
    /** (x,y,z) Coordinate of vertex. */
    public idx3d_Vector pos = new idx3d_Vector();
    /** Transformed vertex coordinate. */
    public idx3d_Vector pos2 = new idx3d_Vector();
    /** Normal Vector at vertex. */
    public idx3d_Vector n = new idx3d_Vector();
    /** Transformed normal vector (camera space). */
    public idx3d_Vector n2 = new idx3d_Vector();
    /** Projected x coordinate. */
    public int x;
    /** Projected y coordinate. */
    public int y;
    /** Projected z coordinate for z-Buffer. */
    public int z;
    /** Normal x-coordinate for envmapping. */
    public int nx = 0;
    /** Normal y-coordinate for envmapping. */
    public int ny = 0;
    /** Visibility tag for clipping. */
    public boolean visible = true;
    int clipcode = 0;
    /** Vertex index. */
    public int id;
    private float fact;
    /** Neighbor triangles of vertex. */
    private ArrayList<idx3d_Triangle> neighbor = new ArrayList<idx3d_Triangle>();

    // C O N S T R U C T O R S
    public idx3d_Vertex() {
        pos = new idx3d_Vector(0f, 0f, 0f);
    }

    public idx3d_Vertex(float xpos, float ypos, float zpos) {
        pos = new idx3d_Vector(xpos, ypos, zpos);
    }

    public idx3d_Vertex(idx3d_Vector ppos) {
        pos = ppos.getClone();
    }

    // P U B L I C   M E T H O D S
    /** Projects this vertex into camera space. */
    void project(idx3d_Matrix vertexProjection, idx3d_Matrix normalProjection, idx3d_Camera camera) {

        pos.transformInto(vertexProjection, pos2);
        n.transformInto(normalProjection, n2);

        fact = camera.screenscale / camera.fovfact / ((pos2.z > 0.1) ? pos2.z : 0.1f);
        x = (int) (pos2.x * fact + (camera.screenwidth >> 1));
        y = (int) (-pos2.y * fact + (camera.screenheight >> 1));
        z = (int) (65536f * pos2.z);
        nx = (int) (n2.x * 127 + 127);
        ny = (int) (n2.y * 127 + 127);
    }

    void clipFrustrum(int w, int h) {
        // View plane clipping
        clipcode = 0;
        if (x < 0) {
            clipcode |= 1;
        }
        if (x >= w) {
            clipcode |= 2;
        }
        if (y < 0) {
            clipcode |= 4;
        }
        if (y >= h) {
            clipcode |= 8;
        }
        if (pos2.z < 0) {
            clipcode |= 16;
        }
        visible = (clipcode == 0);
    }

    /** Registers a neighbor triangle. */
    void registerNeighbor(idx3d_Triangle triangle) {
        if (!neighbor.contains(triangle)) {
            neighbor.add(triangle);
        }
    }

    /** Resets the neighbors. */
    void resetNeighbors() {
        neighbor.clear();
    }

    /** Recalculates the vertex normal. */
    public void regenerateNormal() {
        float nx = 0;
        float ny = 0;
        float nz = 0;
        for (idx3d_Triangle tri: neighbor) {
            n = tri.getWeightedNormalInto(n);
            nx += n.x;
            ny += n.y;
            nz += n.z;
        }
        n.setTo(nx, ny, nz);
        n.normalize();
    }

    public idx3d_Vertex getClone() {
        idx3d_Vertex newVertex = new idx3d_Vertex();
        newVertex.pos = pos.getClone();
        newVertex.n = n.getClone();
        return newVertex;
    }

    @Override
    public String toString() {
        return new String("<vertex  x=" + pos.x + " y=" + pos.y + " z=" + pos.z + ">\r\n");
    }

    // BEGIN PATCH
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return equals((idx3d_Vertex) obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        return hash;
    }
    // END PATCH

    public boolean equals(idx3d_Vertex v) {
        return ((pos.x == v.pos.x) && (pos.y == v.pos.y) && (pos.z == v.pos.z));
    }

    public boolean equals(idx3d_Vertex v, float tolerance) {
        return Math.abs(idx3d_Vector.sub(pos, v.pos).length()) < tolerance;
    }
    /*
    static int count;
    private Throwable construct = new Throwable();
    public void finalize() {
    System.out.println("finalize idx3d_Vertex "+(count++));
    if (count == 1000) {
    construct.printStackTrace();
    }
    }*/
}
