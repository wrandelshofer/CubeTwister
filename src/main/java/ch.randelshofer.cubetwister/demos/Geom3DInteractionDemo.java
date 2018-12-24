/* @(#)Geom3DInteractionDemo.java
 * Copyright (c) 2009 Werner Randelshofer, Switzerland. MIT License.
 */
package demos;

import ch.randelshofer.geom3d.*;
import ch.randelshofer.gui.event.SwipeEvent;
import ch.randelshofer.gui.event.SwipeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.Vector;
import javax.swing.*;

/**
 * Geom3DInteractionDemo.
 * @author Werner Randelshofer
 */
public class Geom3DInteractionDemo extends javax.swing.JPanel {
    private final static long serialVersionUID = 1L;

    /** Creates new form Geom3DInteractionDemo */
    public Geom3DInteractionDemo() {
        initComponents();

        JCanvas3D canvas = new JCanvas3D() {
    private final static long serialVersionUID = 1L;

            /** projected mouse coordinates */
            private int pmx,  pmy;
            /** world coordinates. */
            private Point3D[] w = new Point3D[3];
            /** projected coordinates. */
            private Point3D[] p = new Point3D[3];
            /** triangle p2 coordinates. */
            private Point3D[] p2 = new Point3D[3];
            /** orthogonal versions of the projected coordinates. */
            private Point3D[] o = new Point3D[3];

            @Override
            public void paintComponent(Graphics gr) {
                Graphics2D g = (Graphics2D) gr;
                super.paintComponent(gr);

                FontMetrics fm = g.getFontMetrics();
                int lineHeight = fm.getHeight();
                int line = 0;

                if (swipedFace != null) {
                    //
                    // Blue: Projected triangle and projected mouse coordinates.
                    // ---------------------------------------------------------
                    g.setColor(Color.BLUE.brighter());
                    /*if (drawString) g.drawString("projected face:" + p[0].x + "," + p[0].y +","+p[0].z+"  "+
                    p[1].x + "," + p[1].y +","+p[1].z+"  "+
                    p[2].x + "," + p[2].y +","+p[2].z, fm.getMaxAdvance(), ++line * lineHeight);
                     */
                    //g.draw(debugToPath(swipedFace));
                    g.drawLine(pmx - 10, pmy - 10, pmx + 10, pmy + 10);
                    g.drawLine(pmx + 10, pmy - 10, pmx - 10, pmy + 10);


                    GeneralPath path = new GeneralPath();
                    path.moveTo((float) p[0].x, (float) p[0].y);
                    path.lineTo((float) p[1].x, (float) p[1].y);
                    path.lineTo((float) p[2].x, (float) p[2].y);
                    path.closePath();
                    g.draw(path);
                }
            }

            @Override
            protected JCanvas3D.EventHandler createEventHandler() {
                return new EventHandler2();
            }

            class EventHandler2 extends JCanvas3D.EventHandler {
            @Override
            public void mouseDragged(MouseEvent evt) {
                super.mouseDragged(evt);
                update(evt, swipedFace);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                super.mousePressed(evt);
                update(evt, swipedFace);
            }
            }

            protected void update(MouseEvent evt, Face3D face) {
                int x = evt.getX();
                int y = evt.getY();

                if (face == null) {
                    w[0] = null;
                    p[0] = null;
                    pmx = -1;
                } else {
                    // We only need a triangle from the first three points of a face to
                    // perform the computations.
                    Point3D[] triangle = new Point3D[3];
                    {
                        Transform3D transform = transformModel.getTransform();

                        Vector<Face3D> visibleFaces = new Vector<Face3D>();
                        scene.addVisibleFacesTo(visibleFaces, transform, observer);

                        float[] coords = face.getCoords();
                        int[] vertices = face.getVertices();
                        for (int i = 0; i < 3; i++) {
                            triangle[i] = new Point3D(coords[vertices[i] * 3], coords[vertices[i] * 3 + 1], coords[vertices[i] * 3 + 2]);
                        }
                    }

                    // Get the coordinates of the projected triangle (the coordinates that
                    // have been rendered on the canvas using a camera transform).
                    Point3D[] pt = new Point3D[3];
                    {
                        Insets insets = getInsets();
                        Dimension size = getSize();
                        float cx, cy;
                        float sx, sy;

                        cx = insets.left + (size.width - insets.left - insets.right) / 2;
                        cy = insets.top + (size.height - insets.top - insets.bottom) / 2;
                        sx = (float) (Math.min((size.width - insets.left - insets.right) / 2, (size.height - insets.top - insets.bottom) / 2) * scaleFactor);
                        sy = -sx;

                        float ox = (float) observer.x;
                        float oy = (float) observer.y;
                        float oz = (float) observer.z;

                        for (int i = 0; i < 3; i++) {
                            double div = triangle[i].z - oz;
                            if (div != 0) {
                                pt[i] = new Point3D(
                                        (cx + ((ox - (oz * triangle[i].x - ox) / div) * sx)),
                                        (cy + ((oy - (oz * triangle[i].y - oy) / div) * sy)),
                                        triangle[i].z);
                            } else {
                                pt[i] = new Point3D(
                                        cx + (ox * sx),
                                        cy + (oy * sy),
                                        triangle[i].z);
                            }
                        }
                    }
        // Compute the z-coordinate of the projected mouse coordinates using
        // the plane equation derived from the projected triangle.
        Point3D pm;
        {
            double[] peq = Point3D.planeEquation(pt[0], pt[1], pt[2]);
            // ax+by+cz+d=0
            // ax+by+d=-cz
            // (ax+by+d)/-c=z
            pm = new Point3D(x, y, (peq[0] * x + peq[1] * y + peq[3]) / -peq[2]);

        // Verify, if pm is on plane:
        double verify = peq[0] * pm.x + peq[1] * pm.y + peq[2] * pm.z + peq[3];
       // System.out.println("Verify pm:" + verify);

        }

                    w = triangle;
                    p = pt;
                    pmx = evt.getX();
                    pmy = evt.getY();
                }
            }
        };

        Scene3D scene = new Scene3D();
        Shape3D shape = new Shape3D(
                new float[]{
                    -50, 50, 50,
                    -50, -50, 50,
                    50, 50, 50,
                    50, -50, 50,
                    50, 50, -50,
                    50, -50, -50,
                    -50, 50, -50,
                    -50, -50, -50,},
                new int[][]{
                    {0, 2, 3, 1},
                    {2, 4, 5, 3},
                    {4, 6, 7, 5},
                    {6, 0, 1, 7},
                    {6, 4, 2, 0},
                    {1, 3, 5, 7},},
                new Color[][]{
                    {Color.GRAY, Color.BLACK},
                    {Color.GRAY, Color.BLACK},
                    {Color.GRAY, Color.BLACK},
                    {Color.GRAY, Color.BLACK},
                    {Color.GRAY, Color.BLACK},
                    {Color.GRAY, Color.BLACK},});
        SwipeListener sw = new SwipeListener() {

            public void faceSwiped(SwipeEvent evt) {
                System.out.println("Face Swiped");
            }
        };
        Face3D[] faces = shape.getFaces();
        for (int i = 0; i < faces.length; i++) {
            faces[i].addSwipeListener(sw);
        }
        scene.addChild(shape);
        canvas.setScene(scene);
        canvas.setScaleFactor(0.007);
        canvas.setInteractionMode(JCanvas3D.Interaction.ROTATE_AND_SWIPE);
        add(canvas);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("Geom3D Interaction Test");
                f.add(new Geom3DInteractionDemo());
                f.setSize(400, 400);
                f.setVisible(true);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
