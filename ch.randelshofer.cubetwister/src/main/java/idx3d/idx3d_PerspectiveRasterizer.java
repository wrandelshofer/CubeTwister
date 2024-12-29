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

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Linear rasterizer stage of the render pipeline.
 * <p>
 * This rasterizer performs linear interpolation of textures. Hence the
 * algorithm is very fast but yields distortions when faces are not parallel
 * to the camera plane.
 * <p>
 * For surfaces with large textured triangles use
 * {@link idx3d_PerspectiveRasterizer}.
 *
 */
@SuppressWarnings("cast")
public final class idx3d_PerspectiveRasterizer extends idx3d_Rasterizer {

    private boolean materialLoaded = false;
    private boolean lightmapLoaded = false;
    public boolean ready = false;

    // Current material settings
    private int color = 0;
    private int currentColor = 0;
    private int transparency = 0;
    // BEGIN PATCH texture transparency
    private int textureTransparency = 0;
    // END PATCH texture transparency
    private int reflectivity = 0;
    private int refraction = 0;
    @Nullable
    private idx3d_Texture texture = null;
    @Nullable
    private int[] envmap = null;
    @Nullable
    private int[] diffuse = null;
    @Nullable
    private int[] specular = null;
    @Nullable
    private short[] refractionMap = null;
    private int tw = 0;
    private int th = 0;
    private int tbitW = 0;
    private int tbitH = 0;

    // Rasterizer hints
    private int mode = 0;
    private int F = 0;   	// FLAT
    private int W = 1;	// WIREFRAME
    private int P = 2;  	// PHONG
    private int E = 4;  	// ENVMAP
    private int T = 8; 	// TEXTURED
    private int SHADED = P | E | T;

    //  R E G I S T E R S
    idx3d_Vertex p1, p2, p3, tempVertex;
    private int bkgrd, c, s, lutID, envID, r, //lutID is position in LUT (diffuse,specular), envID is  postion in LUT (envmap)

            x1, x2, x3, x4, y1, y2, y3, z1, z2, z3, z4,
            x, y, z, k, dx, dy, dz, offset, pos, temp,
            xL, xR, xBase, zBase, xMax, yMax, dxL, dxR, dzBase,
            nx1, nx2, nx3, nx4, ny1, ny2, ny3, ny4,
            nxBase, nyBase,
                    dnx4, dny4,
                    dnx, dny, nx, ny,
                    dnxBase, dnyBase,
                    tx1, tx2, tx3, tx4, ty1, ty2, ty3, ty4,
                    txBase, tyBase,
                    dtx4, dty4,
                    dtx, dty, tx, ty,
                    dtxBase, dtyBase,
                    idyBase;

    @Nullable idx3d_Screen screen;
    @Nullable int[] zBuffer;
    @Nullable int[] idBuffer;
    int width, height;
    boolean useIdBuffer;
    boolean antialias;
    final int zFar = 0xFFFFFFF;
    int currentId = 0;

    @Nullable
    private idx3d_Lightmap lightmap;

    // Constructor
    public idx3d_PerspectiveRasterizer(@Nullable idx3d_RenderPipeline pipeline) {
        // BEGIN PATCH Perspective correct rasterizer
        if (pipeline != null) {
            setPipeline(pipeline);
        }
        /*
         rebuildReferences(pipeline);
         loadLightmap(pipeline.lightmap);
         */
        // END PATCH Perspective correct rasterizer
    }

    // BEGIN PATCH Perspective correct rasterizer
    public void setPipeline(@Nonnull idx3d_RenderPipeline pipeline) {
        rebuildReferences(pipeline);
        loadLightmap(pipeline.lightmap);
    }
        // END PATCH Perspective correct rasterizer

    // References    
    void rebuildReferences(@Nonnull idx3d_RenderPipeline pipeline) {
        screen = pipeline.screen;
        zBuffer = pipeline.zBuffer;
        idBuffer = pipeline.idBuffer;
        width = screen.w;
        height = screen.h;
        useIdBuffer = pipeline.useIdBuffer;
        antialias = screen.antialias();
    }

    void clearReferences() {
        screen = null;
        zBuffer = null;
        idBuffer = null;
        width = -1;
        height = -1;
        useIdBuffer = false;
        antialias = false;
    }

    public boolean isAntialiased() {
        return antialias;
    }

    // Lightmap loader
    public void loadLightmap(@Nullable idx3d_Lightmap lm) {
        this.lightmap = lm;
        if (lm == null) {
            return;
        }
        diffuse = lm.diffuse;
        specular = lm.specular;
        lightmapLoaded = true;
        ready = lightmapLoaded && materialLoaded;
    }

    // Material loader
    public void loadMaterial(@Nonnull idx3d_InternalMaterial material) {
        color = material.color;
        transparency = material.transparency;
        // BEGIN PATCH texture transparency
        textureTransparency = material.textureTransparency;
        // END PATCH texture transparency
        reflectivity = material.reflectivity;
        texture = material.texture;
        if (material.envmap != null) {
            envmap = material.envmap.pixel;
        } else {
            envmap = null;
        }

        if (texture != null) {
            tw = texture.width - 1;
            th = texture.height - 1;
            tbitW = texture.bitWidth;
            tbitH = texture.bitHeight;
        }

        mode = 0;
        if (!material.flat) {
            mode |= P;
        }
        if (envmap != null) {
            mode |= E;
        }
        if (texture != null) {
            mode |= T;
        }
        if (material.wireframe) {
            mode |= W;
        }
        materialLoaded = true;
        ready = lightmapLoaded && materialLoaded;
    }
    private int yStart, yEnd, zStart, zEnd, txStart, txEnd, tyStart, tyEnd;

    private final static int ix = 0, iy = 1, iz = 2, iw = 3, iu = 4, iv = 5;
    private final static int FPbits = 32;
    private final static long FP = 1L << FPbits;

    private final static class Vertex {

        public long x, y, z, w, u, v;

        public Vertex(long x, long y, long z, long w, long u, long v) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.u = u;
            this.v = v;
        }

        public Vertex() {

        }

    }

    @Override
    public void render(@Nonnull idx3d_Triangle tri) {
        if (!ready) {
            return;
        }
        if (tri.parent == null) {
            return;
        }
        if ((mode & W) != 0) {
            drawWireframe(tri, color);
            if ((mode & W) == 0) {
                return;
            }

        }

        if (mode == T) {
            renderTriangleT(tri);
        } else {
            renderTriangle(tri);
        }
    }

    private void renderTriangle(@Nonnull idx3d_Triangle tri) {
        p1 = tri.p1;
        p2 = tri.p2;
        p3 = tri.p3;
        tx1 = (int) tri.tx[0] << 16;
        tx2 = (int) tri.tx[1] << 16;
        tx3 = (int) tri.tx[2] << 16;
        ty1 = (int) tri.ty[0] << 16;
        ty2 = (int) tri.ty[1] << 16;
        ty3 = (int) tri.ty[2] << 16;

        // Sort vertices by y-axis
        if (p1.y > p2.y) {
            tempVertex = p1;
            p1 = p2;
            p2 = tempVertex;
            temp = tx1;
            tx1 = tx2;
            tx2 = temp;
            temp = ty1;
            ty1 = ty2;
            ty2 = temp;
        }
        if (p2.y > p3.y) {
            tempVertex = p2;
            p2 = p3;
            p3 = tempVertex;
            temp = tx2;
            tx2 = tx3;
            tx3 = temp;
            temp = ty2;
            ty2 = ty3;
            ty3 = temp;
        }
        if (p1.y > p2.y) {
            tempVertex = p1;
            p1 = p2;
            p2 = tempVertex;
            temp = tx1;
            tx1 = tx2;
            tx2 = temp;
            temp = ty1;
            ty1 = ty2;
            ty2 = temp;
        }

        // abort if triangle is outside bounds
        if (p1.y >= height) {
            return;
        }
        if (p3.y < 0) {
            return;
        }
        if (p1.y == p3.y) {
            return;
        }

        if (mode == F) {
            // XXX - Compute index is broken
            //lutID=(int)(tri.n2.x*127+127)+((int)(tri.n2.y*127+127)<<8);
            lutID = lightmap.getIndex((int) (tri.n2.x * (1 << 23) + (1 << 23)), (int) (tri.n2.y * (1 << 23) + (1 << 23)));
            c = idx3d_Color.multiply(color, diffuse[lutID]);
            s = idx3d_Color.scale(specular[lutID], reflectivity);
            currentColor = idx3d_Color.add(c, s);
        }

        currentId = (tri.parent.id << 16) | tri.id;

        x1 = p1.x << 8;
        x2 = p2.x << 8;
        x3 = p3.x << 8;
        y1 = p1.y;
        y2 = p2.y;
        y3 = p3.y;

        x4 = x1 + (x3 - x1) * (y2 - y1) / (y3 - y1);
        x1 <<= 8;
        x2 <<= 8;
        x3 <<= 8;
        x4 <<= 8;

        z1 = p1.z;
        z2 = p2.z;
        z3 = p3.z;
        nx1 = p1.nx << 16;
        nx2 = p2.nx << 16;
        nx3 = p3.nx << 16;
        ny1 = p1.ny << 16;
        ny2 = p2.ny << 16;
        ny3 = p3.ny << 16;

        dx = (x4 - x2) >> 16;
        if (dx == 0) {
            return;
        }

        temp = 256 * (y2 - y1) / (y3 - y1);

        z4 = z1 + ((z3 - z1) >> 8) * temp;
        nx4 = nx1 + ((nx3 - nx1) >> 8) * temp;
        ny4 = ny1 + ((ny3 - ny1) >> 8) * temp;
        tx4 = tx1 + ((tx3 - tx1) >> 8) * temp;
        ty4 = ty1 + ((ty3 - ty1) >> 8) * temp;

        dz = (z4 - z2) / dx;
        dnx = (nx4 - nx2) / dx;
        dny = (ny4 - ny2) / dx;
        dtx = (tx4 - tx2) / dx;
        dty = (ty4 - ty2) / dx;

        if (dx < 0) {
            temp = x2;
            x2 = x4;
            x4 = temp;
            z2 = z4;
            tx2 = tx4;
            ty2 = ty4;
            nx2 = nx4;
            ny2 = ny4;
        }
        // Rasterize the upper part of the triangle from p1 to p2.
        //
        //       + p1          + p1      The "L" edge goes from p1 to p2 and
        //    L /=\ R   or  L /=\ R      the "R" edge goes from p1 to p4.
        //     /===\         /===\
        //  p2+=====+p4   p2+=====+p4
        //   /   . '         ' .   \
        //  + . '               ' . +
        // p3                      p3
        if (y2 >= 0) {
            dy = y2 - y1;
            if (dy != 0) {
                dxL = (x2 - x1) / dy;
                dxR = (x4 - x1) / dy;
                dzBase = (z2 - z1) / dy;
                dnxBase = (nx2 - nx1) / dy;
                dnyBase = (ny2 - ny1) / dy;
                dtxBase = (tx2 - tx1) / dy;
                dtyBase = (ty2 - ty1) / dy;
            }

            xBase = x1;
            xMax = x1;
            zBase = z1;
            nxBase = nx1;
            nyBase = ny1;
            txBase = tx1;
            tyBase = ty1;

            if (y1 < 0) {
                xBase -= y1 * dxL;
                xMax -= y1 * dxR;
                zBase -= y1 * dzBase;
                nxBase -= y1 * dnxBase;
                nyBase -= y1 * dnyBase;
                txBase -= y1 * dtxBase;
                tyBase -= y1 * dtyBase;
                y1 = 0;
            }

            y2 = (y2 < height) ? y2 : height;
            offset = y1 * width;
            for (y = y1; y < y2; y++) {
                renderLine();
            }
        }

        // Rasterize the lower part of the triangle from p2 to p3.
        //
        //       + p1           + p1    The "L" edge goes from p2 to p3 and
        //      / \      or    / \      the "R" edge goes from p1 to p3
        //     /   \          /   \     regardless of their actual position
        //    /=====+ p2  p2 +=====\    on the screen.
        //   /===, '          ' ,===\
        //  +=, '                ' .=+
        // p3                       p3
        if (y2 < height) {
            dy = y3 - y2;
            if (dy != 0) {
                dxL = (x3 - x2) / dy;
                dxR = (x3 - x4) / dy;
                dzBase = (z3 - z2) / dy;
                dnxBase = (nx3 - nx2) / dy;
                dnyBase = (ny3 - ny2) / dy;
                dtxBase = (tx3 - tx2) / dy;
                dtyBase = (ty3 - ty2) / dy;
            }

            xBase = x2;
            xMax = x4;
            zBase = z2;
            nxBase = nx2;
            nyBase = ny2;
            txBase = tx2;
            tyBase = ty2;

            if (y2 < 0) {
                xBase -= y2 * dxL;
                xMax -= y2 * dxR;
                zBase -= y2 * dzBase;
                nxBase -= y2 * dnxBase;
                nyBase -= y2 * dnyBase;
                txBase -= y2 * dtxBase;
                tyBase -= y2 * dtyBase;
                y2 = 0;
            }

            y3 = (y3 < height) ? y3 : height;
            offset = y2 * width;

            for (y = y2; y < y3; y++) {
                renderLine();
            }
        }
    }

    private void renderTriangleT(@Nonnull idx3d_Triangle tri) {
        if (!ready) {
            return;
        }
        if (tri.parent == null) {
            return;
        }
        if ((mode & W) != 0) {
            drawWireframe(tri, color);
            if ((mode & W) == 0) {
                return;
            }

        }

        Vertex p1 = new Vertex(tri.p1.x, tri.p1.y, (long) tri.p1.z << FPbits, (long) (FP / tri.p1.pos2.z), (long) (FP * tri.tx[0] / tri.p1.pos2.z), (long) (FP * tri.ty[0] / tri.p1.pos2.z));
        Vertex p2 = new Vertex(tri.p2.x, tri.p2.y, (long) tri.p2.z << FPbits, (long) (FP / tri.p2.pos2.z), (long) (FP * tri.tx[1] / tri.p2.pos2.z), (long) (FP * tri.ty[1] / tri.p2.pos2.z));
        Vertex p3 = new Vertex(tri.p3.x, tri.p3.y, (long) tri.p3.z << FPbits, (long) (FP / tri.p3.pos2.z), (long) (FP * tri.tx[2] / tri.p3.pos2.z), (long) (FP * tri.ty[2] / tri.p3.pos2.z));

        // Sort vertices by y-axis
        if (p1.y > p2.y) {
            Vertex swap = p1;
            p1 = p2;
            p2 = swap;
        }
        if (p2.y > p3.y) {
            Vertex swap = p2;
            p2 = p3;
            p3 = swap;
        }
        if (p1.y > p2.y) {
            Vertex swap = p1;
            p1 = p2;
            p2 = swap;
        }

        // abort if triangle is outside bounds
        if (p1.y >= height) {
            return;
        }
        if (p3.y < 0) {
            return;
        }
        if (p1.y == p3.y) {
            return;
        }

        renderTrapezoid(p1, p2, p1, p3, max(0, (int) p1.y), min(height, (int) p2.y));
        renderTrapezoid(p2, p3, p1, p3, max(0, (int) p2.y), min(height, (int) p3.y));
    }

    @Nonnull
    private Vertex pL = new Vertex();
    @Nonnull
    private Vertex pR = new Vertex();

    /** Current values for point interpolation along left edge. */
    private long pLx, pLz, pLu, pLv, pLw;
    /** Deltas for for point interpolation along left edge. */
    private long dpLx, dpLz, dpLu, dpLv, dpLw;
    /** Current values for point interpolation along right edge. */
    private long pRx, pRz, pRu, pRv, pRw;
    /** Deltas for for point interpolation along right edge. */
    private long dpRx, dpRz, dpRu, dpRv, dpRw;
    /** Current values for point interpolation along X-axis. */
    private long pz, pu, pv, pw;
    /** Deltas for for point interpolation along X-axis. */
    private long dpz, dpu, dpv, dpw;

    /** Renders a trapezoid from y=ymin to y<ymax.
     * <pre>
     *      p1          p3
     *      /            \
     *     /==============\   + ymin
     *    /================\  |
     *   /==================\ + ymax
     *  /                    \
     * p2                    p4
     * </pre>
     * @param p1 Top left vertex.
     * @param p2 Bottom left vertex.
     * @param p3 Top right vertex.
     * @param p4 Bottom right vertex.
     * @param ymin Y min.
     * @param ymax Y max.
     */
    private void renderTrapezoid(@Nonnull Vertex p1, @Nonnull Vertex p2, @Nonnull Vertex p3, @Nonnull Vertex p4, int ymin, int ymax) {
        if (ymin >= ymax) {
            return;
        }
        {
            // This bulky sequence moves the invariant out of the for loop
            interpolate(p1, p2, ((long) (ymin - p1.y) << FPbits) / (p2.y - p1.y), pL);
            interpolate(p3, p4, ((long) (ymin - p3.y) << FPbits) / (p4.y - p3.y), pR);
            if (pL.x == pR.x) {
                interpolate(p1, p2, ((long) (ymax - p1.y) << FPbits) / (p2.y - p1.y), pL);
                interpolate(p3, p4, ((long) (ymax - p3.y) << FPbits) / (p4.y - p3.y), pR);
            }
            if (pL.x > pR.x) {
                Vertex swap = p1;
                p1 = p3;
                p3 = swap;
                swap = p2;
                p2 = p4;
                p4 = swap;
            }
        }
        {
            long d =  p2.y - p1.y;
            dpLx = ((long) (p2.x - p1.x) << FPbits) / d;
            dpLz = ((p2.z - p1.z)) / d;
            dpLw = ((p2.w - p1.w)) / d;
            dpLu = ((p2.u - p1.u)) / d;
            dpLv = ((p2.v - p1.v)) / d;
            pLx = ((long) p1.x << FPbits) + (ymin - p1.y) * dpLx;
            pLz = p1.z + (ymin - p1.y) * dpLz;
            pLw = p1.w + (ymin - p1.y) * dpLw;
            pLu = p1.u + (ymin - p1.y) * dpLu;
            pLv = p1.v + (ymin - p1.y) * dpLv;
        }
        {
            long d = p4.y - p3.y;
            dpRx = ((long) (p4.x - p3.x) << FPbits) / d;
            dpRz = ((p4.z - p3.z)) / d;
            dpRw = ((p4.w - p3.w)) / d;
            dpRu = ((p4.u - p3.u)) / d;
            dpRv = ((p4.v - p3.v)) / d;
            pRx = ((long) p3.x << FPbits) + (ymin - p3.y) * dpRx;
            pRz = p3.z + (ymin - p3.y) * dpRz;
            pRw = p3.w + (ymin - p3.y) * dpRw;
            pRu = p3.u + (ymin - p3.y) * dpRu;
            pRv = p3.v + (ymin - p3.y) * dpRv;
        }
        for (y = ymin; y < ymax; y++) {
            int xStart = max(0, (int) (pLx >> FPbits));
            int xEnd = min(width, (int) (pRx >> FPbits));
            long d = xEnd - xStart;
            if (d > 0) {
                dpz = ((pRz - pLz)) / d;
                dpw = ((pRw - pLw)) / d;
                dpu = ((pRu - pLu)) / d;
                dpv = ((pRv - pLv)) / d;
                pz = pLz;
                pw = pLw;
                pu = pLu;
                pv = pLv;
                renderLineT(xStart, xEnd);
            }
            pLx += dpLx;
            pLz += dpLz;
            pLw += dpLw;
            pLu += dpLu;
            pLv += dpLv;
            pRx += dpRx;
            pRz += dpRz;
            pRw += dpRw;
            pRu += dpRu;
            pRv += dpRv;
        }
    }

    /** Renders a line using y, pL, pR. */
    private void renderLineT(int xStart, int xEnd) {
        for (x = xStart; x < xEnd; x++) {
            plot();

            pz += dpz;
            pw += dpw;
            pu += dpu;
            pv += dpv;
        }
    }

    private final void plot() {
        pos = (y * width + x);
        z = (int) (pz >> FPbits);
        tx = pw == 0 ? 0 : (int) (pu / pw);
        ty = pw == 0 ? 0 : (int) (pv / pw);
        if (z < zBuffer[pos]) {
            bkgrd = screen.p[pos];
            c = texture.pixel[((tx) & tw) + (((ty) & th) << tbitW)];
            c = idx3d_Color.transparency(bkgrd, c, transparency);

            //screen.p[pos]=0xFF000000|c;
            screen.p[pos] = c;
            zBuffer[pos] = z;
            if (useIdBuffer) {
                if (antialias) {
                    //idBuffer[offset / 4 + x / 2]=currentId;
                    idBuffer[idyBase + (x >>> 1)] = currentId;
                } else {
                    idBuffer[pos] = currentId;
                }
            }
        }
    }

    /**
     * @param p1
     * @param p2
     * @param a    Fixed decimal 16.16.
     * @param dest
     */
    private final void interpolate(@Nonnull Vertex p1, @Nonnull Vertex p2, long a, @Nonnull Vertex dest) {
        long invA = FP - a;
        dest.x = (invA * p1.x + a * p2.x) >> FPbits;
        dest.y = (invA * p1.y + a * p2.y) >> FPbits;
        dest.z = (invA * p1.z + a * p2.z) >> FPbits;
        dest.w = (invA * p1.w + a * p2.w) >> FPbits;
        dest.u = (invA * p1.u + a * p2.u) >> FPbits;
        dest.v = (invA * p1.v + a * p2.v) >> FPbits;
    }

    private void renderLine() {
        xL = xBase >> 16;
        xR = xMax >> 16;
        z = zBase;
        nx = nxBase;
        ny = nyBase;
        tx = txBase;
        ty = tyBase;
        idyBase = (y >>> 1) * (width >>> 1);
        if (xL < 0) {
            z -= xL * dz;
            nx -= xL * dnx;
            ny -= xL * dny;
            tx -= xL * dtx;
            ty -= xL * dty;
            xL = 0;
        }
        xR = (xR < width) ? xR : width;

        if (mode == F) {
            renderLineF();
        } else if ((mode & SHADED) == P) {
            renderLineP();
        } else if ((mode & SHADED) == E) {
            renderLineE();
        } else if ((mode & SHADED) == T) {
            renderLineT();
        } else if ((mode & SHADED) == (P | E)) {
            renderLinePE();
        } else if ((mode & SHADED) == (P | T)) {
            renderLinePT();
        } else if ((mode & SHADED) == (P | E | T)) {
            renderLinePET();
        }

        offset += width;
        xBase += dxL;
        xMax += dxR;
        zBase += dzBase;
        nxBase += dnxBase;
        nyBase += dnyBase;
//*   xr  := (x - x1) / (x1 - x2);
//*   tr  := xr * z1 / (xr * z1 + (1 - xr) * z2);
        double xr = (y - yStart) / (float) (yEnd - yStart);
        double tr = xr * zStart / (xr * zStart + (1 - xr) * zEnd);
        //txBase = (int) ((txStart * tr + (1 - tr) * txEnd));
        //tyBase = (int) ((tyStart * tr + (1 - tr) * tyEnd));
        txBase = (int) ((txStart * (1 - tr) + (tr) * txEnd));
        tyBase = (int) ((tyStart * (1 - tr) + (tr) * tyEnd));
    }

    // Fast scanline rendering
    private void renderLineF() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                bkgrd = screen.p[pos];
                c = idx3d_Color.transparency(bkgrd, currentColor, transparency);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
        }

    }

    private void renderLineP() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                //lutID=((nx>>16)&255)+(((ny>>16)&255)<<8);
                lutID = lightmap.getIndex(nx, ny);
                bkgrd = screen.p[pos];
                c = idx3d_Color.multiply(color, diffuse[lutID]);
                s = specular[lutID];
                s = idx3d_Color.scale(s, reflectivity);
                c = idx3d_Color.transparency(bkgrd, c, transparency);
                c = idx3d_Color.add(c, s);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            nx += dnx;
            ny += dny;
        }

    }

    private void renderLineE() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                //lutID=((nx>>16)&255)+(((ny>>16)&255)<<8);
                lutID = lightmap.getIndex(nx, ny);
                envID = ((nx >> 16) & 255) + (((ny >> 16) & 255) << 8);
                bkgrd = screen.p[pos];
                s = idx3d_Color.add(specular[lutID], envmap[envID]);
                s = idx3d_Color.scale(s, reflectivity);
                c = idx3d_Color.transparency(bkgrd, s, transparency);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            nx += dnx;
            ny += dny;
        }

    }

    private void renderLineT() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                bkgrd = screen.p[pos];
                c = texture.pixel[((tx >> 16) & tw) + (((ty >> 16) & th) << tbitW)];
                c = idx3d_Color.transparency(bkgrd, c, transparency);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        //idBuffer[offset / 4 + x / 2]=currentId;
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            tx += dtx;
            ty += dty;
        }

    }

    private void renderLinePE() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                //lutID=((nx>>16)&255)+(((ny>>16)&255)<<8);
                lutID = lightmap.getIndex(nx, ny);
                envID = ((nx >> 16) & 255) + (((ny >> 16) & 255) << 8);
                bkgrd = screen.p[pos];
                c = idx3d_Color.multiply(color, diffuse[lutID]);
                s = idx3d_Color.add(specular[lutID], envmap[envID]);
                s = idx3d_Color.scale(s, reflectivity);
                c = idx3d_Color.transparency(bkgrd, c, transparency);
                c = idx3d_Color.add(c, s);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            nx += dnx;
            ny += dny;
        }
    }

    private void renderLinePT() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                //lutID=((nx>>16)&255)+(((ny>>16)&255)<<8);
                lutID = lightmap.getIndex(nx, ny);
                bkgrd = screen.p[pos];
                c = texture.pixel[((tx >> 16) & tw) + (((ty >> 16) & th) << tbitW)];
                // BEGIN PATCH Texture transparency
                c = idx3d_Color.transparency(color, c, textureTransparency);
                // END PATCH Texture transparency
                c = idx3d_Color.multiply(c, diffuse[lutID]);
                s = specular[lutID];
                s = idx3d_Color.scale(s, reflectivity);
                c = idx3d_Color.transparency(bkgrd, c, transparency);
                c = idx3d_Color.add(c, s);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        //idBuffer[offset / 4 + x / 2]=currentId;
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            nx += dnx;
            ny += dny;
            tx += dtx;
            ty += dty;
        }
    }

    private void renderLinePET() {
        for (x = xL; x < xR; x++) {
            pos = x + offset;
            if (z < zBuffer[pos]) {
                //lutID=((nx>>16)&255)+(((ny>>16)&255)<<8);
                lutID = lightmap.getIndex(nx, ny);
                envID = ((nx >> 16) & 255) + (((ny >> 16) & 255) << 8);
                bkgrd = screen.p[pos];
                c = texture.pixel[((tx >> 16) & tw) + (((ty >> 16) & th) << tbitW)];
                c = idx3d_Color.multiply(c, diffuse[lutID]);
                s = idx3d_Color.add(specular[lutID], envmap[envID]);
                s = idx3d_Color.scale(s, reflectivity);
                c = idx3d_Color.transparency(bkgrd, c, transparency);
                c = idx3d_Color.add(c, s);

                //screen.p[pos]=0xFF000000|c;
                screen.p[pos] = c;
                zBuffer[pos] = z;
                if (useIdBuffer) {
                    if (antialias) {
                        idBuffer[idyBase + (x >>> 1)] = currentId;
                    } else {
                        idBuffer[pos] = currentId;
                    }
                }
            }
            z += dz;
            nx += dnx;
            ny += dny;
            tx += dtx;
            ty += dty;
        }
    }

    private void drawWireframe(@Nonnull idx3d_Triangle tri, int defaultcolor) {
        drawLine(tri.p1, tri.p2, defaultcolor);
        drawLine(tri.p2, tri.p3, defaultcolor);
        drawLine(tri.p3, tri.p1, defaultcolor);
    }

    private void drawLine(@Nonnull idx3d_Vertex a, @Nonnull idx3d_Vertex b, int color) {
        idx3d_Vertex temp;
        if ((a.clipcode & b.clipcode) != 0) {
            return;
        }

        dx = Math.abs(a.x - b.x);
        dy = (int) Math.abs(a.y - b.y);
        dz = 0;

        if (dx > dy) {
            if (a.x > b.x) {
                temp = a;
                a = b;
                b = temp;
            }
            if (dx > 0) {
                dz = (b.z - a.z) / dx;
                dy = ((b.y - a.y) << 16) / dx;
            }
            z = a.z;
            y = a.y << 16;
            for (x = a.x; x <= b.x; x++) {
                y2 = y >> 16;
                if (idx3d_Math.inrange(x, 0, width - 1) && idx3d_Math.inrange(y2, 0, height - 1)) {
                    offset = y2 * width;
                    if (z < zBuffer[x + offset]) {
                        if (!screen.antialias) {
                            screen.p[x + offset] = color;
                            zBuffer[x + offset] = z;
                        } else {
                            screen.p[x + offset] = color;
                            screen.p[x + offset + 1] = color;
                            screen.p[x + offset + width] = color;
                            screen.p[x + offset + width + 1] = color;
                            zBuffer[x + offset] = z;
                        }
                    }
                    if (useIdBuffer) {
                        if (antialias) {
                            idBuffer[idyBase + (x >>> 1)] = currentId;
                        } else {
                            idBuffer[x + offset] = currentId;
                        }
                    }
                }
                z += dz;
                y += dy;
            }
        } else {
            if (a.y > b.y) {
                temp = a;
                a = b;
                b = temp;
            }
            if (dy > 0) {
                dz = (b.z - a.z) / dy;
                dx = ((b.x - a.x) << 16) / dy;
            }
            z = a.z;
            x = a.x << 16;
            try {
                for (y = a.y; y <= b.y; y++) {
                    x2 = x >> 16;
                    if (idx3d_Math.inrange(x2, 0, width - 1) && idx3d_Math.inrange(y, 0, height - 1)) {
                        offset = y * width;
                        if (z < zBuffer[x2 + offset]) {
                            if (!screen.antialias) {
                                screen.p[x2 + offset] = color;
                                zBuffer[x2 + offset] = z;
                            } else {
                                screen.p[x2 + offset] = color;
                                screen.p[x2 + offset + 1] = color;
                                screen.p[x2 + offset + width] = color;
                                screen.p[x2 + offset + width + 1] = color;
                                zBuffer[x2 + offset] = z;
                            }
                        }
                        if (useIdBuffer) {
                            if (antialias) {
                                idBuffer[idyBase + (x >>> 1)] = currentId;
                            } else {
                                idBuffer[x2 + offset] = currentId;
                            }
                        }
                    }
                    z += dz;
                    x += dx;
                }
            } catch (IndexOutOfBoundsException e) {

            }
        }
    }

}
