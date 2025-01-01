/*
 * @(#)RubiksCuboctahedronGeom3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.player;

import ch.randelshofer.geom3d.JCanvas3D;
import ch.randelshofer.rubik.Cube3DCanvasGeom3D;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.cube3d.RubiksCuboctahedronGeom3D;
import org.jhotdraw.annotation.Nonnull;

/**
 * RubiksCuboctahedronGeom3DPlayerApplet.
 *
 * @author  Werner Randelshofer
 */
public class RubiksCuboctahedronGeom3DPlayerApplet extends AbstractCubeGeom3DPlayerApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected ScriptPlayer createPlayer() {
        ScriptPlayer p = new ScriptPlayer();
        p.setResetCube(new RubiksCube());
        JCanvas3D canvas3D = new JCanvas3D();
        //RubiksCubeSimple3D cube3D = new RubiksCubeSimple3D();
        RubiksCuboctahedronGeom3D cube3D = new RubiksCuboctahedronGeom3D();
        cube3D.setAnimated(true);
        p.setCanvas(new Cube3DCanvasGeom3D(canvas3D, cube3D));
        p.setCube3D(cube3D);
        p.setCube(cube3D.getCube());
        return p;
    }
    protected int getLayerCount() {
        return 3;
    }


    /** This method is called from within the init() method to
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
