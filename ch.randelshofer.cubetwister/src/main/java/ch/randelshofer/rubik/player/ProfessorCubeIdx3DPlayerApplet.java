/*
 * @(#)ProfessorCubeIdx3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.player;

import ch.randelshofer.rubik.Cube3DCanvas;
import ch.randelshofer.rubik.Cube3DCanvasIdx3D;
import ch.randelshofer.rubik.cube.ProfessorCube;
import ch.randelshofer.rubik.cube3d.ProfessorCubeIdx3D;
import org.jhotdraw.annotation.Nonnull;

import java.io.InputStream;

/**
 * ProfessorCubeIdx3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 */
public class ProfessorCubeIdx3DPlayerApplet extends AbstractCubeIdx3DPlayerApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer p = new ScriptPlayer();
        p.setResetCube(new ProfessorCube());
        ProfessorCubeIdx3D cube3D = new ProfessorCubeIdx3D();
        cube3D.setAnimated(true);
        p.setCube3D(cube3D);
        p.setCube(cube3D.getCube());
        Cube3DCanvas c3d = new Cube3DCanvasIdx3D();
        c3d.setCube3D(cube3D);
        c3d.setLock(cube3D.getCube());
        c3d.setCamera("Front");
        p.setCanvas(c3d);
        return p;
    }
    protected int getLayerCount() {
        return 5;
    }


    /**
     * Returns the Default XML Resource Data of this Applet as an Input Stream.
     */
    @Override
    protected InputStream getPlayerResources() {
        return getClass().getResourceAsStream("/ch/randelshofer/rubik/player/ProfessorPlayerResources.xml");
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
