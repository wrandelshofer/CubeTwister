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
 * Lightmap for faster rendering, assuming static light sources.
 *
 */
public final class idx3d_Lightmap {
    int[] diffuse;
    int[] specular;
    private float[] sphere;
    private idx3d_Light[] light;
    private int lights;
    private int ambient;
    private int temp,overflow,color,pos,r,g,b;
    
    /**
     * Width and height of the diffuse and specular maps expressed as a
     * power of two.
     */
    private int powerX, powerY;
    private int shiftX, shiftY;
    private int maskX, maskY;
    
    public idx3d_Lightmap(idx3d_Scene scene) {
        //this(scene, 32, 32);
       // this(scene, 512, 512);
        this(scene, 256, 256);
    }
    
    /**
     * Creates a new light map of the specified horizontal and vertical 
     * resolution.
     * The resolution must be a power of two.
     */
    public idx3d_Lightmap(idx3d_Scene scene, int xResolution, int yResolution) {
        powerX = Math.max(4, (int) (Math.log(xResolution)/Math.log(2)));
        powerY = Math.max(4, (int) (Math.log(yResolution)/Math.log(2)));
        shiftX = 24 - powerX;
        shiftY = 24 - powerY;
        maskX = 0xffffffff >>> (32 - powerX);
        maskY = 0xffffffff >>> (32 - powerY);
        /*
        diffuse=new int[(1<<powerX) << powerY];
        specular=new int[(1<<powerX) << powerY];
        sphere=new float[(1<<powerX) << powerY];
        */
        scene.rebuild();
        light=scene.light;
        lights=scene.lights;
        ambient=scene.environment.ambient;
        // Do not automatically rebuild lightmap upon creation
        /*
        buildSphereMap();
        rebuildLightmap();
        */
    }
    
    private void init() {
        diffuse=new int[(1<<powerX) << powerY];
        specular=new int[(1<<powerX) << powerY];
        sphere=new float[(1<<powerX) << powerY];
        buildSphereMap();
    }
    
    /**
     * Q: What's the sphere map used for? Is it different for each scene,
     *    or can all scenes share a simple sphere map?
     *    Does it have to scale with the diffuse and specular maps or
     *    can it have an independent size?
     */
    private void buildSphereMap() {
        int width = 1 << powerX;
        int height = 1 << powerY;
        int halfW = width >> 1;
        int halfH = height >> 1;

        float fnx,fny,fnz;
        int pos;
        for (int ny=-halfH;ny<halfH;ny++) {
            fny=(float)ny/halfH;
            for (int nx=-halfW;nx<halfW;nx++) {
                pos=nx+halfW+((ny+halfH)<<powerX);
                fnx=(float)nx/halfW;
                fnz=(float)(1-Math.sqrt(fnx*fnx+fny*fny));
                sphere[pos]=(fnz>0)?fnz:0;
            }
        }
    }
    
    /**
     * Returns an index in the diffuse or specular map for the specified
     * coordinates.
     * The coordinates are always given for a virtual map of size 2^24*2^24.
     *
     * @param nx An value between -2^23 and +2^23.
     * @param ny A value between -2^23 and +2^23.
     */
    public int getIndex(int nx, int ny) {
        //((nx>>16)&255)+(((ny>>16)&255)<<8)
        return ((nx>>shiftX)&maskX)+(((ny>>shiftY)&maskY)<<powerX);
    }
    
    
    public void rebuildLightmap() {
        if (diffuse == null) init();
        
        int width = 1 << powerX;
        int height = 1 << powerY;
        int halfW = width >> 1;
        int halfH = height >> 1;
        
        if (idx3d_Scene.VERBOSE) {
        System.out.println(">> Rebuilding Light Map  ...  ["+lights+" light sources]");
        }
        idx3d_Vector l;
        float fnx,fny,angle,phongfact,sheen, spread;
        int diffuse,specular;
        float cos;
        int dr,dg,db,sr,sg,sb;
        idx3d_Vector angleVector = new idx3d_Vector();
        for (int ny=-halfH;ny<halfH;ny++) {
            fny=(float)ny/halfH;
            for (int nx=-halfW;nx<halfW;nx++) {
                pos=nx+halfW+((ny+halfH)<<powerX);
                fnx=(float)nx/halfW;
                sr=sg=sb=0;
                dr=idx3d_Color.getRed(ambient);
                dg=idx3d_Color.getGreen(ambient);
                db=idx3d_Color.getBlue(ambient);
                for (int i=0;i<lights;i++) {
                    l=light[i].v;
                    diffuse=light[i].diffuse;
                    specular=light[i].specular;
                    sheen=(float)light[i].highlightSheen/255f;
                    spread=(float)light[i].highlightSpread/4096;
                    spread=(spread<0.01f)?0.01f:spread;
                    angleVector.setTo(fnx,fny,sphere[pos]);
                    cos= (255*idx3d_Vector.angle(light[i].v, angleVector));
                    cos=(cos>0)?cos:0;
                    dr+=(int) (idx3d_Color.getRed(diffuse)*cos)>>8;
                    dg+=(int) (idx3d_Color.getGreen(diffuse)*cos)>>8;
                    db+=(int) (idx3d_Color.getBlue(diffuse)*cos)>>8;
                    phongfact=sheen*(float)Math.pow(cos/255f,1/spread);
                    sr+=(int)((float)idx3d_Color.getRed(specular)*phongfact);
                    sg+=(int)((float)idx3d_Color.getGreen(specular)*phongfact);
                    sb+=(int)((float)idx3d_Color.getBlue(specular)*phongfact);
                }
                this.diffuse[pos]=idx3d_Color.getCropColor(dr,dg,db);
                this.specular[pos]=idx3d_Color.getCropColor(sr,sg,sb);
            }
        }
    }
}
