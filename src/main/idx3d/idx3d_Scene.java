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

import ch.randelshofer.gui.event.SwipeListener;
import java.awt.event.*;
import java.util.*;

/**
 * Represents a scene in three dimensional space.
 *
 * @version $Id$
 * <br>6.1 2008-04-29 Wener Randelshofer made prepareForRendering public.
 * <br>6.0 2007-08-28 Werner Randelshofer: Added support for mouse listeners
 * on triangles.
 * <br>5.0 2006-01-06 Werner Randelshofer: Removed all references to
 * render pipeline from scene.
 * Method setAntialiasHint added.
 * <br>4.0 2004-08-29 Werner Randelshofer: Support for scene graphs added.
 * Superclass changed from idx3d_CoreObject to idx3d_Group. 
 * Method setAntialiasHint added.
 * <br>3.2 2003-12-18 Werner Randelshofer: 
 * Changed the idBuffer array from int[] to short[] to save memory. The idBuffer
 * only stores triangle id's now (instead of object id's in its upper 16 bit
 * and the triangle id in the lower 16 bits). We do not need to store the object
 * id in the idBuffer, because all triangles know their parent object.
 * Changed the size of the idBuffer to the size of the display area (instead of
 * to the size of the antialias screen, which is twice as big.
 * Dependency note: These changes also require changes in class 
 * idx3d_Math, idx3d_RenderPipeline and idx3d_Rasterizer.
 * Methods addActionListener/removeActionListener added.
 *
 *
 * Added a VERBOSE attribute. This is used to activate or suppress diagnostic
 * messages of the rendering engine.
 */
public class idx3d_Scene extends idx3d_Group {
    //Release Information
    public final static String version = "3.1.001";
    public final static String release = "29.05.2000";
    public final static boolean VERBOSE = false;    // F I E L D S		
                /*
    public idx3d_RenderPipeline renderPipeline;
    public int width,height;
     */
    public idx3d_Environment environment = new idx3d_Environment();
    public idx3d_Camera defaultCamera = idx3d_Camera.FRONT();
    public idx3d_Object object[];
    public idx3d_Light light[];
    public int objects = 0;
    public int lights = 0;
    private boolean lightsNeedRebuild = true;
    protected boolean preparedForRendering = false;
    public idx3d_Vector normalizedOffset = new idx3d_Vector(0f, 0f, 0f);
    public float normalizedScale = 1f;
    private static boolean instancesRunning = false;    // D A T A   S T R U C T U R E S
    public HashMap<String,idx3d_Object> objectData = new HashMap<String,idx3d_Object>();
    public HashMap<String,idx3d_Light> lightData = new HashMap<String,idx3d_Light>();
    public HashMap<String,idx3d_InternalMaterial> materialData = new HashMap<String,idx3d_InternalMaterial>();
    public HashMap<String,idx3d_Camera> cameraData = new HashMap<String,idx3d_Camera>();
    /**
     * Maps a triangle or an object to an ActionListener.
     */
    private HashMap<Object,ActionListener[]> actionMap = new HashMap<Object,ActionListener[]>();
    /**
     * Maps a triangle or an object to a MouseListener.
     */
    private HashMap<Object,MouseListener[]> mouseMap = new HashMap<Object,MouseListener[]>();
    /**
     * Maps a triangle or an object to a SwipeListener.
     */
    private HashMap<Object,SwipeListener[]> scrapeMap = new HashMap<Object,SwipeListener[]>();
    /* Werner Randelshofer. Added. */
    private boolean isAdjusting;

    // C O N S T R U C T O R S
    public idx3d_Scene() {
    }
    /*
    public idx3d_Scene(int w, int h)
    {
    showInfo(); width=w; height=h;
    renderPipeline= new idx3d_RenderPipeline(this,w,h);
    }
     */

    public void showInfo() {
        if (instancesRunning) {
            return;
        }
        if (VERBOSE) {
            System.out.println();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(" idx3d Kernel " + version + " [Build " + release + "]");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(" (c)1999 by Peter Walser, all rights reserved.");
            System.out.println(" http://www2.active.ch/~proxima/idx3d");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        instancesRunning = true;
    }
    // D A T A   M A N A G E M E N T
    public void rebuild() {
        validate();
    }

    @Override
    public void validate() {
        if (!isValid()) {
            /*
            objects=objectData.size();
            object=new idx3d_Object[objects];
            Enumeration enumer=objectData.elements();
            for (int i=objects-1;i>=0;i--)
            {
            object[i]=(idx3d_Object)enumer.nextElement();
            object[i].id=i;
            object[i].rebuild();
            }*/
            super.validate();
            ArrayList<idx3d_Object> temp = new ArrayList<idx3d_Object>();
            for (idx3d_Node node : preorderIterator()) {
                if (node instanceof idx3d_Object) {
                    ((idx3d_Object) node).id = temp.size();
                    temp.add((idx3d_Object)node);
                }
            }
            objects = temp.size();
            object = temp.toArray(new idx3d_Object[objects]);
        }

        if (lightsNeedRebuild) {
            lightsNeedRebuild = false;
            lights = lightData.size();
            light = new idx3d_Light[lights];
            Iterator<idx3d_Light> enumer = lightData.values().iterator();
            for (int i = lights - 1; i >= 0; i--) {
                light[i] =  enumer.next();
            }
        }
    }

    // A C C E S S O R S
    public idx3d_Object object(String key) {
        return objectData.get(key);
    }

    public idx3d_Light light(String key) {
        return lightData.get(key);
    }

    public idx3d_InternalMaterial material(String key) {
        return materialData.get(key);
    }

    public idx3d_Camera camera(String key) {
        return cameraData.get(key);
    }
    
    // O B J E C T   M A N A G E M E N T
    public void addObject(String key, idx3d_Object obj) {
        obj.name = key;
        objectData.put(key, obj);
        addChild(obj);
    }

    public void removeObject(String key) {
        removeChild(objectData.remove(key));
        preparedForRendering = false;
    }

    public void addLight(String key, idx3d_Light l) {
        lightData.put(key, l);
        lightsNeedRebuild = true;
    }

    public void removeLight(String key) {
        lightData.remove(key);
        lightsNeedRebuild = true;
        preparedForRendering = false;
    }

    public void addMaterial(String key, idx3d_InternalMaterial m) {
        materialData.put(key, m);
    }

    public void removeMaterial(String key) {
        materialData.remove(key);
    }

    public void addCamera(String key, idx3d_Camera c) {
        cameraData.put(key, c);
    }

    public void removeCamera(String key) {
        cameraData.remove(key);
    }

    public void addActionListener(idx3d_Object object, ActionListener listener) {
        basicAddActionListener(object, listener);
    }

    public void removeActionListener(idx3d_Object object, ActionListener listener) {
        basicRemoveActionListener(object, listener);
    }

    public ActionListener[] getActionListeners(idx3d_Object object) {
        ActionListener[] listeners = actionMap.get(object);
        return (listeners == null) ? new ActionListener[0] : listeners;
    }

    public void addActionListener(idx3d_Triangle triangle, ActionListener listener) {
        basicAddActionListener(triangle, listener);
    }

    public void removeActionListener(idx3d_Triangle triangle, ActionListener listener) {
        basicRemoveActionListener(object, listener);
    }

    public ActionListener[] getActionListeners(idx3d_Triangle triangle) {
        ActionListener[] listeners = actionMap.get(triangle);
        return (listeners == null) ? new ActionListener[0] : listeners;
    }

    private void basicAddActionListener(Object object, ActionListener listener) {
        ActionListener[] listeners = actionMap.get(object);
        if (listeners == null) {
            listeners = new ActionListener[]{listener};
        } else {
            ActionListener[] temp = listeners;
            listeners = new ActionListener[temp.length + 1];
            System.arraycopy(temp, 0, listeners, 0, temp.length);
            listeners[temp.length] = listener;
        }
        actionMap.put(object, listeners);
    }

    private void basicRemoveActionListener(Object object, ActionListener listener) {
        ActionListener[] listeners = actionMap.get(object);
        if (listeners != null) {
            ActionListener[] temp = listeners;
            listeners = new ActionListener[temp.length - 1];
            int j = 0;
            for (int i = 0; i < temp.length; i++) {
                listeners[i - j] = temp[i];
                if (j == 0 && temp[j] == listener) {
                    j = 1;
                }
            }
            if (listeners.length == 0) {
                actionMap.remove(object);
            } else {
                actionMap.put(object, listeners);
            }
        }
    }

    public void addMouseListener(idx3d_Object object, MouseListener listener) {
        basicAddMouseListener(object, listener);
    }

    public void removeMouseListener(idx3d_Object object, MouseListener listener) {
        basicRemoveMouseListener(object, listener);
    }
    public void addScrapeListener(idx3d_Object object, SwipeListener listener) {
        basicAddSwipeListener(object, listener);
    }
    public void addSwipeListener(idx3d_Triangle triangle, SwipeListener listener) {
        basicAddSwipeListener(triangle, listener);
    }

    public void removeSwipeListener(idx3d_Object object, SwipeListener listener) {
        basicRemoveSwipeListener(object, listener);
    }
    public SwipeListener[] getSwipeListeners(idx3d_Object object) {
        SwipeListener[] listeners = scrapeMap.get(object);
        return (listeners == null) ? new SwipeListener[0] : listeners;
    }

    public SwipeListener[] getSwipeListeners(idx3d_Triangle triangle) {
        SwipeListener[] listeners = scrapeMap.get(triangle);
        return (listeners == null) ? new SwipeListener[0] : listeners;
    }



    public MouseListener[] getMouseListeners(idx3d_Object object) {
        MouseListener[] listeners = mouseMap.get(object);
        return (listeners == null) ? new MouseListener[0] : listeners;
    }

    public void addMouseListener(idx3d_Triangle triangle, MouseListener listener) {
        basicAddMouseListener(triangle, listener);
    }

    public void removeMouseListener(idx3d_Triangle triangle, MouseListener listener) {
        basicRemoveMouseListener(object, listener);
    }

    public MouseListener[] getMouseListeners(idx3d_Triangle triangle) {
        MouseListener[] listeners = mouseMap.get(triangle);
        return (listeners == null) ? new MouseListener[0] : listeners;
    }

    private void basicAddMouseListener(Object object, MouseListener listener) {
        MouseListener[] listeners = mouseMap.get(object);
        if (listeners == null) {
            listeners = new MouseListener[]{listener};
        } else {
            MouseListener[] temp = listeners;
            listeners = new MouseListener[temp.length + 1];
            System.arraycopy(temp, 0, listeners, 0, temp.length);
            listeners[temp.length] = listener;
        }
        mouseMap.put(object, listeners);
    }

    private void basicRemoveMouseListener(Object object, MouseListener listener) {
        MouseListener[] listeners = mouseMap.get(object);
        if (listeners != null) {
            MouseListener[] temp = listeners;
            listeners = new MouseListener[temp.length - 1];
            int j = 0;
            for (int i = 0; i < temp.length; i++) {
                listeners[i - j] = temp[i];
                if (j == 0 && temp[j] == listener) {
                    j = 1;
                }
            }
            if (listeners.length == 0) {
                mouseMap.remove(object);
            } else {
                mouseMap.put(object, listeners);
            }
        }
    }
    private void basicAddSwipeListener(Object object, SwipeListener listener) {
        SwipeListener[] listeners = scrapeMap.get(object);
        if (listeners == null) {
            listeners = new SwipeListener[]{listener};
        } else {
            SwipeListener[] temp = listeners;
            listeners = new SwipeListener[temp.length + 1];
            System.arraycopy(temp, 0, listeners, 0, temp.length);
            listeners[temp.length] = listener;
        }
        scrapeMap.put(object, listeners);
    }

    private void basicRemoveSwipeListener(Object object, SwipeListener listener) {
        SwipeListener[] listeners = scrapeMap.get(object);
        if (listeners != null) {
            SwipeListener[] temp = listeners;
            listeners = new SwipeListener[temp.length - 1];
            int j = 0;
            for (int i = 0; i < temp.length; i++) {
                listeners[i - j] = temp[i];
                if (j == 0 && temp[j] == listener) {
                    j = 1;
                }
            }
            if (listeners.length == 0) {
                scrapeMap.remove(object);
            } else {
                scrapeMap.put(object, listeners);
            }
        }
    }

    // R E N D E R I N G
    /** Werner Randelshofer. Made public. */
    public boolean isPreparedForRendering() {
        return preparedForRendering;
    }

    /** Werner Randelshofer. Made public. */
    public void prepareForRendering() {
        if (preparedForRendering) {
            return;
        }
        preparedForRendering = true;

        if (VERBOSE) {
            System.out.println(">> Preparing structures for realtime rendering ...   ");
        }
        validate();
        buildLightmap();
        printSceneInfo();
    }

    public void printSceneInfo() {
        if (VERBOSE) {
            System.out.println(">> | Objects   : " + objects);
            System.out.println(">> | Vertices  : " + countVertices());
            System.out.println(">> | Triangles : " + countTriangles());
        }
    }

    /* Werner Randelshofer. Added. */
    public idx3d_Camera getDefaultCamera() {
        return this.defaultCamera;
    }

    /* Werner Randelshofer. Removed. */
    /*
    public final void render(idx3d_Camera cam)
    {
    renderPipeline.render(cam);
    }
    
    public final void render()
    {
    renderPipeline.render(this.defaultCamera);
    }
    
    public final Image getImage()
    {
    return renderPipeline.screen.getImage();
    }
    
    public void setAntialias(boolean antialias)
    {
    renderPipeline.setAntialias(antialias);
    }
    public void setAntialiasHint(boolean antialias)
    {
    renderPipeline.setAntialiasHint(antialias);
    }
    
    public final boolean antialias()
    {
    return renderPipeline.screen.antialias;
    }
    
    public float getFPS()
    {
    return renderPipeline.getFPS();
    }
    
    public void useIdBuffer(boolean useIdBuffer)
    // Enables / Disables idBuffering
    {
    renderPipeline.useIdBuffer(useIdBuffer);
    }
     */
    public idx3d_Triangle identifyTriangleAt(int[] idBuffer, int width, int height, int xpos, int ypos) {
//System.out.println("scene.identifyTriangleAt idBuffer="+renderPipeline.useIdBuffer+" wh="+renderPipeline.getWidth()+" "+renderPipeline.getHeight());
        if (idBuffer == null || idBuffer.length != width * height) {
            return null;
        }
        if (xpos < 0 || xpos >= width) {
            return null;
        }
        if (ypos < 0 || ypos >= height) {
            return null;
        }
        int pos = xpos + width * ypos;
        //if(renderPipeline.screen.antialias) pos*=2;
        int idCode = idBuffer[pos];
        if (idCode < 0) {
            return null;
        }
        return object[idCode >> 16].triangle[idCode & 0xFFFF];
    }
    public idx3d_Triangle identifyTriangleAt(idx3d_RenderPipeline renderPipeline, int xpos, int ypos) {
//System.out.println("scene.identifyTriangleAt idBuffer="+renderPipeline.useIdBuffer+" wh="+renderPipeline.getWidth()+" "+renderPipeline.getHeight());
        if (renderPipeline == null || !renderPipeline.useIdBuffer) {
            return null;
        }
        if (xpos < 0 || xpos >= renderPipeline.getWidth()) {
            return null;
        }
        if (ypos < 0 || ypos >= renderPipeline.getHeight()) {
            return null;
        }
        int pos = xpos + renderPipeline.screen.width * ypos;
        //if(renderPipeline.screen.antialias) pos*=2;
        int idCode = renderPipeline.idBuffer[pos];
        if (idCode < 0) {
            return null;
        }
        return object[idCode >> 16].triangle[idCode & 0xFFFF];
    }

    public idx3d_Object identifyObjectAt(idx3d_RenderPipeline renderPipeline, int xpos, int ypos) {
        idx3d_Triangle tri = identifyTriangleAt(renderPipeline, xpos, ypos);
        if (tri == null) {
            return null;
        }
        return tri.parent;
    }
    /** Werner Randelshofer. Moved here from RenderPipeline, and added support
     * for sharing. */
    private idx3d_Lightmap lightmap;

    public void setLightmap(idx3d_Lightmap newValue) {
        lightmap = newValue;
    }

    private void buildLightmap() {
        if (lightmap == null) {
            lightmap = new idx3d_Lightmap(this);
            lightmap.rebuildLightmap();
        }
    }

    public idx3d_Lightmap getLightmap() {
        if (lightmap == null) {
            buildLightmap();
        }
        return lightmap;
    }
    // P U B L I C   M E T H O D S
            /* Werner Randelshofer. Removed.
    public java.awt.Dimension size()
    {
    return new java.awt.Dimension(width,height);
    }
    
    public void resize(int w, int h)
    {
    if ((width==w)&&(height==h)) return;
    width=w;
    height=h;
    renderPipeline.resize(w,h);
    }
     */
    /* Werner Randelshofer. Added. */
    public void setIsAdjusting(boolean newValue) {
        isAdjusting = newValue;
    }

    public boolean isAdjusting() {
        return isAdjusting;
    }

    public void setBackgroundColor(int bgcolor) {
        environment.bgcolor = 0xffffff & bgcolor;
    }

    public void setBackground(idx3d_Texture t) {
        environment.setBackground(t);
    }

    public void setAmbient(int ambientcolor) {
        environment.ambient = ambientcolor;
    }

    public int countVertices() {
        int counter = 0;
        for (int i = 0; i < objects; i++) {
            counter += object[i].vertices;
        }
        return counter;
    }

    public int countTriangles() {
        int counter = 0;
        for (int i = 0; i < objects; i++) {
            counter += object[i].triangles;
        }
        return counter;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<scene>\r\n");
        for (int i = 0; i < objects; i++) {
            buffer.append(object[i].toString());
        }
        return buffer.toString();
    }

    public void normalize() // useful if you can't find your objects on the screen ;)
    {
        invalidate();
        validate();

        idx3d_Vector min, max, tempmax, tempmin;
        if (objects == 0) {
            return;
        }
        matrix = new idx3d_Matrix();
        normalmatrix = new idx3d_Matrix();

        max = object[0].max();
        min = object[0].min();

        for (int i = 0; i < objects; i++) {
            tempmax = object[i].max();
            tempmin = object[i].min();
            if (tempmax.x > max.x) {
                max.x = tempmax.x;
            }
            if (tempmax.y > max.y) {
                max.y = tempmax.y;
            }
            if (tempmax.z > max.z) {
                max.z = tempmax.z;
            }
            if (tempmin.x < min.x) {
                min.x = tempmin.x;
            }
            if (tempmin.y < min.y) {
                min.y = tempmin.y;
            }
            if (tempmin.z < min.z) {
                min.z = tempmin.z;
            }
        }
        float xdist = max.x - min.x;
        float ydist = max.y - min.y;
        float zdist = max.z - min.z;
        float xmed = (max.x + min.x) / 2;
        float ymed = (max.y + min.y) / 2;
        float zmed = (max.z + min.z) / 2;

        float diameter = (xdist > ydist) ? xdist : ydist;
        diameter = (zdist > diameter) ? zdist : diameter;

        normalizedOffset = new idx3d_Vector(xmed, ymed, zmed);
        normalizedScale = 2 / diameter;

        shift(normalizedOffset.reverse());
        scale(normalizedScale);

    }    // P R I V A T E   M E T H O D S
}