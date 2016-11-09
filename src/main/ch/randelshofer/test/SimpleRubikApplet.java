/* @(#)SimpleRubikApplet.java
 * Copyright (c) 2009 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.test;

import ch.randelshofer.rubik.*;
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
            public void actionPerformed(Cube3DEvent evt) {
                evt.applyTo(evt.getCube3D().getCube());
            }
        });
    }

    // TODO overwrite start(), stop() and destroy() methods
}
