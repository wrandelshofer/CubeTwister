/*
 * @(#)SimpleRubikApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.test;

import ch.randelshofer.rubik.Cube3DAdapter;
import ch.randelshofer.rubik.Cube3DCanvasIdx3D;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.cube3d.Cube3DEvent;
import ch.randelshofer.rubik.cube3d.RubiksCubeIdx3D;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.JApplet;

/**
 * SimpleRubikApplet.
 * @author Werner Randelshofer
 */
public class SimpleRubikApplet extends JApplet {
    private final static long serialVersionUID = 1L;

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    @Override
    public void init() {
        // Create a cube and show it in an interactive 3D canvas:
        // --------------------
        RubiksCube cube = new RubiksCube();

        RubiksCubeIdx3D cube3d = new RubiksCubeIdx3D();
        cube3d.setAnimated(true);
        cube3d.setCube(cube);

        Cube3DCanvasIdx3D canvas = new Cube3DCanvasIdx3D();
        canvas.setCube3D(cube3d);
        add(canvas.getVisualComponent());

        cube3d.addCube3DListener(new Cube3DAdapter() {

            @Override
            public void actionPerformed(@Nonnull Cube3DEvent evt) {
                evt.applyTo(evt.getCube3D().getCube());
            }
        });
    }

    // TODO overwrite start(), stop() and destroy() methods
}
