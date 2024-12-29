/*
 * @(#)AbstractCubeGeom3DPlayerApplet.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.player;

import ch.randelshofer.geom3d.JCanvas3D;
import ch.randelshofer.geom3d.Point3D;
import ch.randelshofer.geom3d.RotatedTransform3DModel;
import ch.randelshofer.rubik.Cube3DCanvas;
import ch.randelshofer.rubik.Cube3DCanvasGeom3D;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube3d.Cube3D;
import ch.randelshofer.util.AppletParameterException;
import ch.randelshofer.util.Applets;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * AbstractCubeGeom3DPlayerApplet.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractCubeGeom3DPlayerApplet extends AbstractPlayerApplet {

    @Nonnull
    @Override
    final protected Cube3DCanvas createRearCanvas() {
        Cube3D cube3D = frontCanvas.getCube3D();
        JCanvas3D frontCanvas3D = ((Cube3DCanvasGeom3D) frontCanvas).getCanvas3D();

        JCanvas3D canvas3D = new JCanvas3D();
        canvas3D.setTransformModel(new RotatedTransform3DModel(0,Math.PI,Math.PI,frontCanvas3D.getTransformModel()));
        Cube3DCanvas c3d = new Cube3DCanvasGeom3D(canvas3D, cube3D);
        canvas3D.setOpaque(false);
        c3d.setCube3D(cube3D);
        c3d.setLock(cube3D.getCube());
        return c3d;
    }
    @Override
    protected void readParameters()
            throws AppletParameterException {
        super.readParameters();

        int[] ints;
        String value;
        Color color;

        Cube3D cube3D = player.getCube3D();
        DefaultCubeAttributes attributes = (DefaultCubeAttributes) cube3D.getAttributes();

        // Get lighting properties
        Component canvas3D = player.getVisualComponent();
        if (canvas3D instanceof JCanvas3D) {
            JCanvas3D c3D = (JCanvas3D) canvas3D;
            c3D.setLightSourceIntensity(Applets.getParameter(this, "lightSourceIntensity", 0.3));
            c3D.setAmbientLightIntensity(Applets.getParameter(this, "ambientLightIntensity", 0.85));// 0.6));
            ints = Applets.getParameters(this, "lightSourcePosition", new int[] {-500, 500, 1000});
            if (ints.length != 3) {
                throw new IllegalArgumentException("Invalid parameter 'lightSourcePosition' provides "+ints.length+" instead of 3 entries.");
            }
            c3D.setLightSource(new Point3D(ints[0], ints[1], ints[2]));

            // Get background image
            value = getParameter("backgroundImage");
            if (value != null) {
                try {
                    Image img = getImage(new URL(getDocumentBase(), value));
                    c3D.setBackgroundImage(img);
                    attributes.setFrontBgImage(img);
                    attributes.setRearBgImage(img);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Invalid parameter 'backgroundImage' malformed URL: "+value);
                }
            }
            // Configure the rear canvas
            // -------------------------
            if ("true".equals(Applets.getParameter(this, "rearView","false"))) {
                // background color
                color = new Color(Applets.getParameter(this, "rearViewBackgroundColor", Applets.getParameter(this, "backgroundColor", 0xffffff)));
                rearCanvas.getVisualComponent().setBackground(color);

                // Get background image
                value = Applets.getParameter(this, "rearViewBackgroundImage", getParameter("backgroundImage"));
                if (value != null) {
                    try {
                        rearCanvas.setBackgroundImage(getImage(new URL(getDocumentBase(), value)));
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException("Invalid parameter 'backgroundImage' malformed URL: "+value);
                    }
                }

                // Get lighting properties
                if (rearCanvas instanceof JCanvas3D) {
                    JCanvas3D rc3D = (JCanvas3D) canvas3D;
                    rc3D.setLightSourceIntensity(Applets.getParameter(this, "lightSourceIntensity", 0.6));
                    rc3D.setAmbientLightIntensity(Applets.getParameter(this, "ambientLightIntensity", 0.6));
                    ints = Applets.getParameters(this, "lightSourcePosition", new int[] {-500, 500, 1000});
                    if (ints.length != 3) {
                        throw new IllegalArgumentException("Invalid parameter 'lightSourcePosition' provides "+ints.length+" instead of 3 entries.");
                    }
                    rc3D.setLightSource(new Point3D(ints[0], ints[1], ints[2]));
                }
            }
            }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
