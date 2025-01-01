/*
 * @(#)Cube7Idx3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */


package ch.randelshofer.rubik.player;

import ch.randelshofer.rubik.Cube3DCanvasIdx3D;
import ch.randelshofer.rubik.cube.Cube7;
import ch.randelshofer.rubik.cube3d.Cube7Idx3D;
import idx3d.idx3d_JCanvas;
import org.jhotdraw.annotation.Nonnull;

import java.io.InputStream;

/**
 * Cube7Idx3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 */
public class Cube7Idx3DPlayerApplet extends AbstractCubeIdx3DPlayerApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer p = new ScriptPlayer();
        p.setResetCube(new Cube7());
        idx3d_JCanvas canvas3D = new idx3d_JCanvas();
        Cube7Idx3D cube3D = new Cube7Idx3D();
        cube3D.setAnimated(true);
        p.setCanvas(new Cube3DCanvasIdx3D(canvas3D, cube3D));
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
    @Override
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
