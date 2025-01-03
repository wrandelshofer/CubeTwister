/*
 * @(#)VCube7Geom3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.player;

import ch.randelshofer.geom3d.JCanvas3D;
import ch.randelshofer.rubik.Cube3DCanvasGeom3D;
import ch.randelshofer.rubik.cube.Cube7;
import ch.randelshofer.rubik.cube3d.VCube7Geom3D;
import org.jhotdraw.annotation.Nonnull;

import java.io.InputStream;

/**
 * VCube7Geom3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 */

public class VCube7Geom3DPlayerApplet extends AbstractCubeGeom3DPlayerApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer p = new ScriptPlayer();
        p.setResetCube(new Cube7());
        JCanvas3D canvas3D = new JCanvas3D();
        VCube7Geom3D cube3D = new VCube7Geom3D();
        cube3D.setAnimated(true);
        p.setCanvas(new Cube3DCanvasGeom3D(canvas3D, cube3D));
        p.setCube3D(cube3D);
        p.setCube(cube3D.getCube());
        return p;
    }
    protected int getLayerCount() {
        return 7;
    }


    /**
     * Returns the Default XML Resource Data of this Applet as an Input Stream.
     */
    protected InputStream getPlayerResources() {
        return getClass().getResourceAsStream("/ch/randelshofer/rubik/player/VCube7PlayerResources.xml");
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
