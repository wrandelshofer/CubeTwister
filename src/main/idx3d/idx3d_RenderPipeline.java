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

/*
 * @version 4.2 2006-02-21 Werner Randelshofer: Avoid unecessary object creation. 
 */
package idx3d;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Renders a scene in a sequence of stages.
 *
 * @version 5.1.1 2010-11-06 Werner Randelshofer Fixes possible null pointer
 * dereference in method performResizing.
 * <br>5.1 2010-08-18 Werner Randelshofer Added support for screen sharing.
 * <br>5.0 2009-01-03 Werner Randelshofer Moved dynamic determination of
 * antialiasing mode out of RenderPipeline into idx3d_JCanvas.
 * <br>4.2 2006-02-21 Werner Randelshofer: Avoid unecessary object creation.
 * <br>4.0 2004-08-29 Werner Randelshofer: Method setAntialiasHint added. 
 * <br>3.2 2003-12-18 Werner Randelshofer: 
 * Retrieve the material from the current triangle instead from its parent 
 * object. This allows for individual materials at the triangle level. 
 * Dependency note: This change also requires changes in class idx3d_Triangle.
 *
 * Changed the size of the idBuffer to the size of the display area (instead of
 * to the size of the antialias screen, which is twice as big.
 * Dependency note: These changes also require changes in class 
 * idx3d_Rasterizer and idx3d_Scene.
 *
 * Method render calls method clearReferences on the rasteriezer to allow for 
 * better memory management by the garbage collector.
 * Dependency note: This change also requires changes in class 
 * idx3d_Rasterizer.
 *
 * Added a visible attribute to idx3d_InternalMaterial. This change also affects idx3d_InternalMaterial.
 * Added dispose method.
 */
public class idx3d_RenderPipeline {
    // F I E L D S

    public idx3d_Screen screen;
    idx3d_Scene scene;
    public idx3d_Lightmap lightmap;
    private boolean resizingRequested = false;
    private boolean antialiasChangeRequested = false;
    private int requestedWidth;
    private int requestedHeight;
    private boolean requestedAntialias;
    boolean useIdBuffer = false;
    idx3d_Rasterizer rasterizer;
    ArrayList<idx3d_Triangle> opaqueQueue = new ArrayList<idx3d_Triangle>();
    ArrayList<idx3d_Triangle> transparentQueue = new ArrayList<idx3d_Triangle>();
    idx3d_Matrix vertexProjection = new idx3d_Matrix();
    idx3d_Matrix normalProjection = new idx3d_Matrix();
    final int zFar = 0xFFFFFFF;    // B U F F E R S
    public int zBuffer[];
    public int idBuffer[];
    // C O N S T R U C T O R S

    public idx3d_RenderPipeline(idx3d_Scene scene, int w, int h) {
        screen = new idx3d_Screen(w, h);
        zBuffer = new int[screen.w * screen.h];
        rasterizer = new idx3d_LinearRasterizer(this);
        setScene(scene);
    }
    // P U B L I C   M E T H O D S

    public void setAntialias(boolean antialias) {
        if (screen.isAntialias() != antialias) {
            antialiasChangeRequested = true;
            requestedAntialias = antialias;
        }
    }

    public float getFPS() {
        return (float) ((int) (screen.FPS * 100)) / 100;
    }

    public void resize(int w, int h) {
        resizingRequested = true;
        requestedWidth = w;
        requestedHeight = h;
    }

    /* Werner Randelshofer. Moved lightmap building into scene. */
    public void buildLightMap() {
        lightmap = scene.getLightmap();
        rasterizer.loadLightmap(lightmap);
    }

    public final void render(idx3d_Camera cam) {
        long start = System.currentTimeMillis();

        // Resize if requested
        if (resizingRequested) {
            performResizing();
        }
        if (antialiasChangeRequested) {
            performAntialiasChange();
        }
        rasterizer.rebuildReferences(this);

        // Clear buffers	
        idx3d_Math.clearBuffer(zBuffer, zFar);
        if (useIdBuffer) {
            idx3d_Math.clearBuffer(idBuffer, (short) -1);
        }
        if (scene.environment.background != null) {
            screen.drawBackground(scene.environment.background, 0, 0, screen.w, screen.h);
        } else {
            screen.clear(scene.environment.bgcolor);        // Prepare
        }
        cam.setScreensize(screen.w, screen.h);
        scene.prepareForRendering();
        emptyQueues();
        rasterizer.rebuildReferences(this);

        // Project

        idx3d_Matrix m = cam.getMatrix();
        idx3d_Matrix nm = cam.getNormalMatrix();
        idx3d_Object obj;
        idx3d_Triangle t;
        idx3d_Vertex v;
        int w = screen.w;
        int h = screen.h;

        for (int id = scene.objects - 1; id >= 0; id--) {
            obj = scene.object[id];
            if (obj.visible) {
                //vertexProjection=obj.getVertexProjection();
                //normalProjection=obj.getNormalProjection();
                obj.getVertexProjectionInto(vertexProjection);
                obj.getNormalProjectionInto(normalProjection);
                vertexProjection.transform(m);
                normalProjection.transform(nm);

                for (int i = obj.vertices - 1; i >= 0; i--) {
                    v = obj.vertex[i];
                    v.project(vertexProjection, normalProjection, cam);
                    v.clipFrustrum(w, h);
                }
                for (int i = obj.triangles - 1; i >= 0; i--) {
                    t = obj.triangle[i];
                    t.project(normalProjection);
                    t.clipFrustrum(w, h);
                    enqueueTriangle(t);
                }
            }
        }

        idx3d_Triangle[] tri;
        tri = getOpaqueQueue();
        if (tri != null) {
            for (int i = tri.length - 1; i >= 0; i--) {
                //rasterizer.loadMaterial(tri[i].parent.material);
                rasterizer.loadMaterial(tri[i].getMaterial());
                rasterizer.render(tri[i]);
            }
        }
        tri = getTransparentQueue();
        if (tri != null) {
            for (int i = 0; i < tri.length; i++) {
                //rasterizer.loadMaterial(tri[i].parent.material);
                rasterizer.loadMaterial(tri[i].getMaterial());
                rasterizer.render(tri[i]);
            }
        }
        screen.render();

        // Clean up
        rasterizer.clearReferences();

    }

    public void useIdBuffer(boolean useIdBuffer) {
        if (this.useIdBuffer != useIdBuffer) {
            this.useIdBuffer = useIdBuffer;
            if (useIdBuffer) {
                idBuffer = new int[screen.width * screen.height];
            } else {
                idBuffer = null;
            }
        }
    }
    // P R I V A T E   M E T H O D S

    private void performResizing() {
        try {
            resizingRequested = false;

            // Maybe there is nothing we need to do?
            // BEGIN PATCH wr
            if (screen == null
                    || screen != null && screen.w == requestedWidth && screen.h == requestedHeight//
                    && zBuffer != null && zBuffer.length == screen.width * screen.height //
                    && (!useIdBuffer || idBuffer != null && idBuffer.length == screen.width * screen.height)) {
                // END PATCH wr
                return;
            }
            // Clear references to buffers before allocating new ones
            zBuffer = null;
            idBuffer = null;

            screen.resize(requestedWidth, requestedHeight);
            zBuffer = new int[screen.w * screen.h];
            if (useIdBuffer) {
                idBuffer = new int[screen.width * screen.height];
            }
        } catch (OutOfMemoryError e) {
            if (screen.isAntialias()) {
                // Antialiasing needs huge amounts of memory
                // We switch it off and try again.
                screen.setAntialias(false);
                performResizing();
            } else if (requestedWidth > 10 && requestedHeight > 10) {
                // Big screens needs lots of memory too.
                // We reduce the screen size and try again.
                requestedWidth = Math.max(1, requestedWidth / 2);
                requestedHeight = Math.max(1, requestedHeight / 2);
                performResizing();
            } else {
                // If we can't handle the out of memory situation,
                // we propagate the error up the call stack.
                throw e;
            }
        }
    }

    private void performAntialiasChange() {
        antialiasChangeRequested = false;
        try {
            screen.setAntialias(requestedAntialias);
            // Don't allocate a new zBuffer if we can avoid it.
            // We will only create a new zBuffer, if it is too
            // small, or if it is more than 4 times bigger than
            // what we need.
            int len = screen.w * screen.h;
            if (zBuffer == null || zBuffer.length < len || zBuffer.length > len * 4) {
                zBuffer = null;
                zBuffer = new int[len];
            }
        } catch (OutOfMemoryError e) {
            if (!requestedAntialias) {
                // Rethrow if we can't even render non-antialiased screen.
                throw e;
            }

            zBuffer = null;
            screen.setAntialias(false);
            zBuffer = new int[screen.w * screen.h];
        }
    }
    // Triangle sorting

    private void emptyQueues() {
        opaqueQueue.clear();
        transparentQueue.clear();
    }

    private void enqueueTriangle(idx3d_Triangle tri) {
        //if (tri.parent.material==null) return;
        if (tri.getMaterial() == null) {
            return;
        }
        if (tri.getMaterial().visible == false) {
            return;
        }
        if (tri.visible == false) {
            return;
            //if ((tri.parent.material.transparency==255)&&(tri.parent.material.reflectivity==0)) return;
        }
        if ((tri.getMaterial().transparency == 255) && (tri.getMaterial().reflectivity == 0)) {
            return;        //if (tri.parent.material.transparency>0) transparentQueue.addElement(tri);
        }
        if (tri.getMaterial().transparency > 0) {
            transparentQueue.add(tri);
        } else {
            opaqueQueue.add(tri);
        }
    }

    private idx3d_Triangle[] getOpaqueQueue() {
        if (opaqueQueue.size() == 0) {
            return null;
        }
        idx3d_Triangle[] tri = opaqueQueue.toArray(new idx3d_Triangle[opaqueQueue.size()]);
        return sortTriangles(tri, 0, tri.length - 1);
    }

    public idx3d_Rasterizer getRasterizer() {
        return rasterizer;
    }
    // BEGIN PATCH Perspective correct rasterizer

    public void setRasterizer(idx3d_Rasterizer r) {
        if (rasterizer == r) {
            return;
        }
        if (rasterizer != null) {
            rasterizer.clearReferences();
        }
        rasterizer = r;
        if (rasterizer != null) {
            rasterizer.setPipeline(this);
        }

    }
    // END PATCH Perspective correct rasterizer

    public idx3d_Screen getScreen() {
        return screen;
    }

    private idx3d_Triangle[] getTransparentQueue() {
        if (transparentQueue.size() == 0) {
            return null;
        }
        idx3d_Triangle[] tri = transparentQueue.toArray(new idx3d_Triangle[transparentQueue.size()]);
        return sortTriangles(tri, 0, tri.length - 1);
    }

    private idx3d_Triangle[] sortTriangles(idx3d_Triangle[] tri, int L, int R) {
        float m = (tri[L].dist + tri[R].dist) / 2;
        int i = L;
        int j = R;
        idx3d_Triangle temp;

        do {
            while (tri[i].dist > m) {
                i++;
            }
            while (tri[j].dist < m) {
                j--;
            }
            if (i <= j) {
                temp = tri[i];
                tri[i] = tri[j];
                tri[j] = temp;
                i++;
                j--;
            }
        } while (j >= i);

        if (L < j) {
            sortTriangles(tri, L, j);
        }
        if (R > i) {
            sortTriangles(tri, i, R);
        }
        return tri;
    }

    /* Werner Randelshofer. Moved from Scene. */
    public java.awt.Dimension size() {
        if (screen != null) {
            return new java.awt.Dimension(screen.width, screen.height);
        } else {
            return new java.awt.Dimension(0, 0);
        }

    }

    public int getWidth() {
        return (screen != null) ? screen.width : 0;
    }

    public int getHeight() {
        return (screen != null) ? screen.height : 0;
    }

    public idx3d_Scene getScene() {
        return scene;
    }

    public void setScene(idx3d_Scene newValue) {
        if (this.scene != newValue) {
            this.scene = newValue;
            buildLightMap();
        }
    }

    public final Image getImage() {
        return screen.getImage();
    }

    public void dispose() {
        if (rasterizer != null) {
            rasterizer.clearReferences();
            rasterizer = null;
        }
        if (screen != null) {
            screen.dispose();
            screen = null;
        }
        scene = null;
        lightmap = null;
    }
}
