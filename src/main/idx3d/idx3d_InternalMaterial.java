/* @(#)idx3d_InternalMaterial.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package idx3d;

/**
 * This is the superclass of idx3d_Material. This class helps us to taylor
 * idx3d, if we do not need to load textures from a file or an URL into our 
 * scene.
 *
* @author  Werner Randelshofer
 * @version $Id$
 * Dependency note: The creation of this class changes the class hierarchy of
 * idx3d. This change also affects the following classes: idx3d_Material, 
 * Inspector, idx3d_MaterialInspector, idx3d_RenderPipeline, idx3d_Triangle,
 * idx3d_Rasterizer, idx3d_Scene, idx3d_Object.
 */
public class idx3d_InternalMaterial 
{
	int color=0;
	int transparency=0;
        
        // BEGIN PATCH W. Randelshofer Texture transparency
        /* Texture transparency is used to mix the texture with the material 
         * color. 
         * If textureTransparency=255, only the color used,
         * If textureTransparency=0, only the texture is used.
         */ 
	int textureTransparency=0;
        // END PATCH W. Randelshofer Texture transparency
        
	int reflectivity=255;
	idx3d_Texture texture=null;
	idx3d_Texture envmap=null;
	boolean flat=false;
	boolean wireframe=false;
	boolean opaque=true;
	String texturePath=null;
	String envmapPath=null;
	public idx3d_TextureSettings textureSettings=null;
	public idx3d_TextureSettings envmapSettings=null;
	
        boolean visible=true;
        
	// Constructor
	
		public idx3d_InternalMaterial()
		{
		}
		
		public idx3d_InternalMaterial(int color)
		{
			setColor(color);
		}
		
		public idx3d_InternalMaterial(idx3d_Texture t)
		{
			setTexture(t);
			reflectivity=255;
		}
		
	// Setters
	
		public void setTexture(idx3d_Texture t)
		{
			texture=t;
			if (texture!=null) texture.resize();
		}
		
		public void setEnvmap(idx3d_Texture env)
		{
			envmap=env;
			env.resize(256,256);
		}
		
		public void setColor(int c)
		{
			color=c;
		}
		
		public void setTransparency(int factor)
		{
			transparency=idx3d_Math.crop(factor,0,255);
			opaque=(transparency==0);
		}
                // BEGIN PATCH W. Randelshofer Texture transparency
		public void setTextureTransparency(int factor)
		{
			textureTransparency=idx3d_Math.crop(factor,0,255);
            	}
                // END PATCH W. Randelshofer Texture transparency
		
		public void setReflectivity(int factor)
		{
			reflectivity=idx3d_Math.crop(factor,0,255);
		}
		
		public void setFlat(boolean flat)
		{
			this.flat=flat;
		}
		
		public void setWireframe(boolean wireframe)
		{
			this.wireframe=wireframe;
		}
		
                public void setVisible(boolean b) {
                    visible = b;
                }
	// Getters
		
		public idx3d_Texture getTexture()
		{
			return texture;
		}
		
		public idx3d_Texture getEnvmap()
		{
			return envmap;
		}
		
		public int getColor()
		{
			return color;
		}
		
		public int getTransparency()
		{
			return transparency;
		}
                // BEGIN PATCH W. Randelshofer Texture transparency
		public int getTextureTransparency()
		{
			return textureTransparency;
		}
                // END PATCH W. Randelshofer Texture transparency
		
		public int getReflectivity()
		{
			return reflectivity;
		}
		
		public boolean isFlat()
		{
			return flat;
		}
		
		public boolean isWireframe()
		{
			return wireframe;
		}
                public boolean isVisible() {
                    return visible;
                }
}
