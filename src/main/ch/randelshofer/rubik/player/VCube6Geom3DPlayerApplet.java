/* @(#)VCube6Geom3DPlayerApplet.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik.player;

import ch.randelshofer.geom3d.*;
import ch.randelshofer.rubik.parser.*;
import ch.randelshofer.rubik.*;
import java.io.InputStream;

/**
 * VCube6Geom3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */

public class VCube6Geom3DPlayerApplet extends AbstractCubeGeom3DPlayerApplet {
        private final static long serialVersionUID = 1L;

    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer p = new ScriptPlayer();
        p.setResetCube(new Cube6());
        JCanvas3D canvas3D = new JCanvas3D();
        VCube6Geom3D cube3D = new VCube6Geom3D();
        cube3D.setAnimated(true);
        p.setCanvas(new Cube3DCanvasGeom3D(canvas3D, cube3D));
        p.setCube3D(cube3D);
        p.setCube(cube3D.getCube());
        return p;
    }
    protected int getLayerCount() {
        return 6;
    }

    
    /**
     * Returns the Default XML Resource Data of this Applet as an Input Stream. 
     */
    @Override
    protected InputStream getPlayerResources() {
        return getClass().getResourceAsStream("/ch/randelshofer/rubik/player/VCube6PlayerResources.xml");
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * /
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        getContentPane().setLayout(new java.awt.FlowLayout());
    }// </editor-fold>//GEN-END:initComponents
    */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
