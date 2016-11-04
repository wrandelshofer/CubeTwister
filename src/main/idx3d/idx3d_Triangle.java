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

/**
 * Defines a 3d triangle.
 *
 * @version $Id$
 * <br>3.2 2003-12-18 Werner Randelshofer: Methods setMaterial and
 * getMaterial added. These methods allows for individual materials
 * per triangle. Dependency note: This change also requires changes in
 * class idx3d_RenderPipeline.
 * Moved responsibility for texture location from class idx3d_Vertex to
 * this class. Dependency note: This change also requires changes in
 * idx3d_Vertex, idx3d_Rasterizer.
 */
public class idx3d_Triangle {
    // F I E L D S
    
    /** The object which contains this triangle. */
    public idx3d_Object parent;
    /** Visibility tag for clipping. */
    public boolean visible=true;
    /** visibility tag for frustrum clipping. */
    public boolean outOfFrustrum=false;
    
    /** First  vertex. */
    public idx3d_Vertex p1;
    /** Second vertex. */
    public idx3d_Vertex p2;
    /** Third  vertex. */
    public idx3d_Vertex p3;
    
    /** Normal vector of flat triangle. */
    public idx3d_Vector n = new idx3d_Vector();
    /** Projected Normal vector. */
    public idx3d_Vector n2 = new idx3d_Vector();
    
    /** for clipping. */
    private int minx,maxx,miny,maxy;
    private idx3d_Vector triangleCenter=new idx3d_Vector();
    float dist=0;
    
    public int id=0;
    
    /** Texture x-coordinates (relative). */
    public float[] u = new float[3];
    /** Texture y-coordinates (relative). */
    public float[] v = new float[3];
    /** Texture x-coordinates (absolute). */
    public float[] tx = new float[3];
    /** Texture y-coordinates (absolute). */
    public float[] ty = new float[3];
    
    
    /**
     * The material of this triangle used to override the material
     * setting of the parent idx3d_Object. If this variable is null,
     * the material of the parent is used.
     */
    private idx3d_InternalMaterial material;
    
    
    // C O N S T R U C T O R S
    
    public idx3d_Triangle(idx3d_Vertex a, idx3d_Vertex b, idx3d_Vertex c) {
        p1=a;
        p2=b;
        p3=c;
    }
    
    
    // P U B L I C   M E T H O D S
    /**
     * Sets an individual material for this triangle. This allows for
     * multi-material idx3d_Objects at the triangle level.
     * Set this to null, to use the material specified by the parent idx3d_Object.
     *
     * @param material A material or null.
     */
    public void setTriangleMaterial(idx3d_InternalMaterial material) {
        this.material = material;
    }
    /**
     * Returns the local material setting.
     *
     * @return Returns the material used to render this triangle.
     */
    public idx3d_InternalMaterial getTriangleMaterial() {
        return material;
    }
    /**
     * Returns the material which shall be used to render this triangle.
     * If this triangle does not have an individual material, the material of
     * the parent idx3d_Object is returned.
     *
     * @return Returns the material used to render this triangle.
     */
    public idx3d_InternalMaterial getMaterial() {
        return (material != null) ? material : parent.material;
    }
    
    
    /** Backface culling and frustrum clipping. */
    public void clipFrustrum(int w, int h) {
        if (parent.material==null) {visible=false; return; }
        outOfFrustrum=(p1.clipcode&p2.clipcode&p3.clipcode)!=0;
        if (outOfFrustrum) {visible=false; return; }
        if (n2.z>0.5) {visible=true; return; }
        
        triangleCenter.x=(p1.pos2.x+p2.pos2.x+p3.pos2.x);
        triangleCenter.y=(p1.pos2.y+p2.pos2.y+p3.pos2.y);
        triangleCenter.z=(p1.pos2.z+p2.pos2.z+p3.pos2.z);
        visible=idx3d_Vector.angle(triangleCenter,n2)>0;
        
    }
    
    public void project(idx3d_Matrix normalProjection) {
        n.transformInto(normalProjection, n2);
        dist=getDist();
        
        idx3d_InternalMaterial material;
        idx3d_Texture texture;
        if ((material = getMaterial()) != null
        && (texture = material.getTexture()) != null) {
            for (int i=0; i < 3; i++) {
                tx[i]=(texture.width*u[i]);
                ty[i]=(texture.height*v[i]);
            }
        }
    }
    
    public void setUV(float u1, float v1, float u2, float v2, float u3, float v3) {
        this.u[0]=u1;
        this.v[0]=v1;
        this.u[1]=u2;
        this.v[1]=v2;
        this.u[2]=u3;
        this.v[2]=v3;
    }
    public void setUV(int vertex, float u, float v) {
        this.u[vertex]=u;
        this.v[vertex]=v;
    }
    
    
    public void scaleTextureCoordinates(float fx, float fy) {
        for (int i=0; i < 3; i++) {
            u[i]*=fx;
            v[i]*=fy;
        }
    }
    
    public void regenerateNormal() {
        //n=idx3d_Vector.getNormal(p1.pos,p2.pos,p3.pos);
        idx3d_Vector.getNormalInto(p1.pos,p2.pos,p3.pos, n);
    }
    
    public idx3d_Vector getWeightedNormal() {
        return idx3d_Vector.vectorProduct(p1.pos,p2.pos,p3.pos);
    }
    public idx3d_Vector getWeightedNormalInto(idx3d_Vector resultVector) {
        return idx3d_Vector.vectorProductInto(p1.pos,p2.pos,p3.pos, resultVector);
    }
    
    public idx3d_Vertex getMedium() {
        float cx=(p1.pos.x+p2.pos.x+p3.pos.x)/3;
        float cy=(p1.pos.y+p2.pos.y+p3.pos.y)/3;
        float cz=(p1.pos.z+p2.pos.z+p3.pos.z)/3;
        //float cu=(p1.u+p2.u+p3.u)/3;
        //float cv=(p1.v+p2.v+p3.v)/3;
        return new idx3d_Vertex(cx,cy,cz/*,cu,cv*/);
    }
    
    public idx3d_Vector getCenter() {
        float cx=(p1.pos.x+p2.pos.x+p3.pos.x)/3;
        float cy=(p1.pos.y+p2.pos.y+p3.pos.y)/3;
        float cz=(p1.pos.z+p2.pos.z+p3.pos.z)/3;
        return new idx3d_Vector(cx,cy,cz);
    }
    
    public float getDist() {
        return p1.z+p2.z+p3.z;
    }
    
    
    public boolean degenerated() {
        return p1.equals(p2)||p2.equals(p3)||p3.equals(p1);
    }
    
    public idx3d_Triangle getClone() {
        return new idx3d_Triangle(p1,p2,p3);
    }
    
    
    
    // P R I V A T E   M E T H O D S
    /*
    private static int count;
  public void finalize() {
    System.out.println("finalize "+this+" "+(count++));
}*/
  
    
}