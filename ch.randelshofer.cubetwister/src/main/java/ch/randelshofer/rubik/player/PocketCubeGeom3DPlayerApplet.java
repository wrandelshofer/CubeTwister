/*
 * @(#)PocketCubeGeom3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.player;

import ch.randelshofer.geom3d.JCanvas3D;
import ch.randelshofer.rubik.Cube3DCanvasGeom3D;
import ch.randelshofer.rubik.cube.PocketCube;
import ch.randelshofer.rubik.cube3d.PocketCubeGeom3D;
import org.jhotdraw.annotation.Nonnull;
import org.monte.media.swing.player.JPlayerControlAqua;

import java.io.InputStream;

/**
 * PocketCubeGeom3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 */
public class PocketCubeGeom3DPlayerApplet extends AbstractCubeGeom3DPlayerApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer player = new ScriptPlayer();
        player.setPlayerControl(new JPlayerControlAqua());
        JCanvas3D canvas3D = new JCanvas3D();
        PocketCubeGeom3D cube3D = new PocketCubeGeom3D();
        cube3D.setAnimated(true);
        player.setCanvas(new Cube3DCanvasGeom3D(canvas3D, cube3D));
        player.setCube3D(cube3D);
        player.setCube(cube3D.getCube());
        player.setResetCube(new PocketCube());
        return player;
    }
    protected int getLayerCount() {
        return 2;
    }

    /**
     * Returns the Default XML Resource Data of this Applet as an Input Stream.
     */
    protected InputStream getPlayerResources() {
        return getClass().getResourceAsStream("/ch/randelshofer/rubik/player/PocketPlayerResources.xml");
    }
     /*
     * /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * /
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
