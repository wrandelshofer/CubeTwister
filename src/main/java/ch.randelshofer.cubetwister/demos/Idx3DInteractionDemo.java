/* @(#)Idx3DInteractionDemo.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */

/*
 * Idx3DInteractionDemo.java
 *
 * Created on Dec 28, 2008, 11:44:13 AM
 */
package demos;

import idx3d.*;
import org.jhotdraw.geom.Geom;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/**
 *
 * @author Werner Randelshofer
 */
public class Idx3DInteractionDemo extends javax.swing.JPanel {
    private final static long serialVersionUID = 1L;

    private idx3d_JCanvas canvas;

    /** Creates new form Idx3DInteractionDemo */
    public Idx3DInteractionDemo() {
        initComponents();

        canvas = new idx3d_JCanvas() {
    private final static long serialVersionUID = 1L;

            private int screenWidth,  screenHeight;
            private boolean isAntialiased;
            private float screenScale, fovFact;
            /** world coordinates. */
            private idx3d_Vector[] w = new idx3d_Vector[3];
            /** projected coordinates. */
            private idx3d_Vector[] p = new idx3d_Vector[3];
            /** triangle p2 coordinates. */
            private idx3d_Vector[] p2 = new idx3d_Vector[3];
            /** orthogonal versions of the projected coordinates. */
            private idx3d_Vector[] o = new idx3d_Vector[3];
            /** projected mouse coordinates */
            private int pmx,  pmy;
            /** orthogonal mouse coordinates */
            private idx3d_Vector om;

            private boolean drawString = false;

            @Override
            public void paintComponent(Graphics gr) {
                Graphics2D g = (Graphics2D) gr;
                super.paintComponent(gr);

                FontMetrics fm = g.getFontMetrics();
                int lineHeight = fm.getHeight();
                int line = 0;

                if (p[0] != null) {
                    //
                    // Blue: Projected triangle and projected mouse coordinates.
                    // ---------------------------------------------------------
                    g.setColor(Color.BLUE.brighter());
                    if (drawString) g.drawString("projected triangle:" + p[0].x + "," + p[0].y +","+p[0].z+"  "+
                             p[1].x + "," + p[1].y +","+p[1].z+"  "+
                             p[2].x + "," + p[2].y +","+p[2].z, fm.getMaxAdvance(), ++line * lineHeight);

                    g.drawPolyline(
                            new int[]{
                                (int) p[0].x,
                                (int) p[1].x,
                                (int) p[2].x,
                                (int) p[0].x,},
                            new int[]{
                                (int) p[0].y,
                                (int) p[1].y,
                                (int) p[2].y,
                                (int) p[0].y,},
                            4);

                    g.drawLine(pmx - 10, pmy - 10, pmx + 10, pmy + 10);
                    g.drawLine(pmx + 10, pmy - 10, pmx - 10, pmy + 10);
                    float[] peq = idx3d_Vector.planeEquation(p[0], p[1], p[2]);
                    if (drawString) g.drawString("projected peq:" + peq[0] + "x" + (peq[1] >= 0 ? "+" : "") + peq[1] + "y" + (peq[2] >= 0 ? "+" : "") + peq[2] + "z" + (peq[3] >= 0 ? "+" : "") + peq[3] + "=0", fm.getMaxAdvance(), ++line * lineHeight);

                    // Compute the z-coordinate of the projected mouse coordinates using
                    // the plane equation.
                    // ax+by+cz+d=0
                    // ax+by+d=-cz
                    // (ax+by+d)/-c=z
                    float pmz = (peq[0]*pmx+peq[1]*pmy+peq[3])/-peq[2];
                    if (drawString) g.drawString("projected mouse:" + pmx + "x " + pmy+"y "+pmz+"z", fm.getMaxAdvance(), ++line * lineHeight);


                    //
                    // Orange: Orthogonal triangle and orthogonal mouse coordinates.
                    // ---------------------------------------------------------
                    g.setColor(Color.ORANGE);
                    for (int i = 0; i < p.length; i++) {
                    float fact = screenScale / fovFact / ((p[i].z > 0.1) ? p[i].z : 0.1f);
                        o[i] = new idx3d_Vector(
                                (p[i].x - screenWidth / 2) * 2 / fact,
                                (p[i].y - screenHeight / 2) * 2 / fact,
                                p[i].z);
                    }
                    float fact = screenScale / fovFact / ((pmz > 0.1) ? pmz : 0.1f);
                    om = new idx3d_Vector(
                                (pmx - screenWidth / 2) * 2 / fact,
                                (pmy - screenHeight / 2) * 2 / fact,
                                pmz);
                    if (drawString) g.drawString("orthogonal triangle:" + o[0].x + "," + o[0].y +","+o[0].z+"  "+
                             o[1].x + "," + o[1].y +","+o[1].z+"  "+
                             o[2].x + "," + o[2].y +","+o[2].z, fm.getMaxAdvance(), ++line * lineHeight);
                    if (drawString) g.drawString("triangle p2:" + p2[0].x + "," + p2[0].y +","+p2[0].z+"  "+
                             p2[1].x + "," + p2[1].y +","+p2[1].z+"  "+
                             p2[2].x + "," + p2[2].y +","+p2[2].z, fm.getMaxAdvance(), ++line * lineHeight);
                    g.draw(new Line2D.Float(om.x - 10 + screenWidth / 2, om.y - 10+ screenHeight / 2, om.x + 10 + screenWidth / 2, om.y + 10+ screenHeight / 2));
                    g.draw(new Line2D.Float(om.x + 10 + screenWidth / 2, om.y - 10+ screenHeight / 2, om.x - 10 + screenWidth / 2, om.y + 10+ screenHeight / 2));

                    g.drawPolyline(
                            new int[]{
                                (int) o[0].x + screenWidth / 2,
                                (int) o[1].x + screenWidth / 2,
                                (int) o[2].x + screenWidth / 2,
                                (int) o[0].x + screenWidth / 2,},
                            new int[]{
                                (int) o[0].y + screenHeight / 2,
                                (int) o[1].y + screenHeight / 2,
                                (int) o[2].y + screenHeight / 2,
                                (int) o[0].y + screenHeight / 2,},
                            4);





                    //
                    // CYAN: Normalized Triangle and normalized mouse coordinates
                    // ---------------------------------------------------------
                    g.setColor(Color.cyan);
                    idx3d_Vector normal = idx3d_Vector.vectorProduct(
                            idx3d_Vector.sub(o[0], o[1]),
                            idx3d_Vector.sub(o[2], o[1]));
                    normal.normalize();

                    float k = idx3d_Vector.dotProduct(normal, o[0]);

                    if (drawString) g.drawString("normal:" + normal.toString(), fm.getMaxAdvance(), ++line * lineHeight);
                    if (drawString) g.drawString("k:" + k, fm.getMaxAdvance(), ++line * lineHeight);
//                    float normal = (p1-p2) x (p3-p2);
                    idx3d_Matrix m = new idx3d_Matrix();
                    //                m.rotate(0,0, (float) Math.acos(normal.z));
                    //                  m.rotate(0, (float) Math.acos(normal.y), 0);
//                    m.rotate((float) Math.acos(normal.x), 0,0);
                   /* m.rotate((float) Math.acos(normal.x), (float) Math.acos(normal.y), (float) Math.acos(normal.z));
                    m = m.inverse();
                     */

                    m = fromToRotation(normal, new idx3d_Vector(0, 0, 1));

                    //m.rotate(0,0,(float)Math.cos(Math.PI));
                    if (drawString) g.drawString("m:" + m, fm.getMaxAdvance(), ++line * lineHeight);

                    idx3d_Vector[] np = new idx3d_Vector[3];
                    for (int i = 0; i < p.length; i++) {
                        np[i] = o[i].transform(m);
                    }
                    idx3d_Vector nm = om.transform(m);

                    float minx = Float.MAX_VALUE;
                    float miny = Float.MAX_VALUE;
                    float maxx = Float.MIN_VALUE;
                    float maxy = Float.MIN_VALUE;
                    for (int i = 0; i < p.length; i++) {
                        minx = Math.min(minx, np[i].x);
                        miny = Math.min(miny, np[i].y);
                        maxx = Math.max(maxx, np[i].x);
                        maxy = Math.max(maxx, np[i].y);
                    }
                    float shiftx = 0, shifty = 0;
                    if (minx < 0) {
                        shiftx = -minx;
                    } else if (maxx > getWidth()) {
                        shiftx = getWidth() - maxx;
                    }
                    if (miny < 0) {
                        shifty = -miny;
                    } else if (maxy > getHeight()) {
                        shifty = getHeight() - maxy;
                    }

                    g.drawPolyline(
                            new int[]{
                                (int) (np[0].x+shiftx),
                                (int) (np[1].x+shiftx),
                                (int) (np[2].x+shiftx),
                                (int) (np[0].x+shiftx),},
                            new int[]{
                                (int) (np[0].y+shifty),
                                (int) (np[1].y+shifty),
                                (int) (np[2].y+shifty),
                                (int) (np[0].y+shifty),},
                            4);
                    g.draw(new Line2D.Float(nm.x - 10 +shiftx , nm.y - 10+shifty, nm.x + 10+shiftx , nm.y + 10+shifty));
                    g.draw(new Line2D.Float(nm.x + 10 +shiftx, nm.y - 10+shifty, nm.x - 10+shiftx , nm.y + 10+shifty));
                    for (int i = 0; i < np.length; i++) {
                        if (drawString) g.drawString(np[i].z + "", (int) np[i].x, np[i].y);
                    }
                        if (drawString) g.drawString(nm.z + "", (int) nm.x, nm.y);

                    //
                    // Dark CYAN: Normalized Triangle and normalized mouse coordinates
                    // ---------------------------------------------------------
                    g.setColor(Color.cyan.darker());

                   float angle = (float) Geom.angle(np[0].x,np[0].y,np[1].x,np[1].y);
                   m.reset();
                   m.rotate(0, 0, angle);
                    for (int i = 0; i < p.length; i++) {
                        np[i] = np[i].transform(m);
                    }
                    nm = nm.transform(m);
                     minx = Float.MAX_VALUE;
                     miny = Float.MAX_VALUE;
                     maxx = Float.MIN_VALUE;
                     maxy = Float.MIN_VALUE;
                    for (int i = 0; i < p.length; i++) {
                        minx = Math.min(minx, np[i].x);
                        miny = Math.min(miny, np[i].y);
                        maxx = Math.max(maxx, np[i].x);
                        maxy = Math.max(maxx, np[i].y);
                    }
                     shiftx = 0; shifty = 0;
                        shiftx = -minx;
                        shifty = -miny;

                    g.drawPolyline(
                            new int[]{
                                (int) (np[0].x+shiftx),
                                (int) (np[1].x+shiftx),
                                (int) (np[2].x+shiftx),
                                (int) (np[0].x+shiftx),},
                            new int[]{
                                (int) (np[0].y+shifty),
                                (int) (np[1].y+shifty),
                                (int) (np[2].y+shifty),
                                (int) (np[0].y+shifty),},
                            4);
                    g.draw(new Line2D.Float(nm.x - 10 +shiftx , nm.y - 10+shifty, nm.x + 10+shiftx , nm.y + 10+shifty));
                    g.draw(new Line2D.Float(nm.x + 10 +shiftx, nm.y - 10+shifty, nm.x - 10+shiftx , nm.y + 10+shifty));

                }
                if (w[0] != null) {
                    float f = 1;
                    float tx = getWidth() / 2;
                    float ty = getHeight() / 2;
                    g.setColor(Color.red);
                    g.drawPolyline(
                            new int[]{
                                (int) (w[0].x * f + tx),
                                (int) (w[1].x * f + tx),
                                (int) (w[2].x * f + tx),
                                (int) (w[0].x * f + tx),},
                            new int[]{
                                (int) (w[0].y * f + ty),
                                (int) (w[1].y * f + ty),
                                (int) (w[2].y * f + ty),
                                (int) (w[0].y * f + ty),},
                            4);

                    idx3d_Vector normal = idx3d_Vector.vectorProduct(
                            idx3d_Vector.sub(w[0], w[1]),
                            idx3d_Vector.sub(w[2], w[1]));
                    normal.normalize();
                    float k = idx3d_Vector.dotProduct(normal, p[0]);
                    if (drawString) g.drawString(normal.toString(), fm.getMaxAdvance(), ++line * lineHeight);
                    if (drawString) g.drawString("k:" + k, fm.getMaxAdvance(), ++line * lineHeight);

                    g.setColor(Color.MAGENTA);
                    idx3d_Matrix m = new idx3d_Matrix();
                    m.rotate(0, 0, (float) Math.acos(normal.z));
                    m.rotate(0, (float) Math.acos(1 - normal.y), 0);
                    m.rotate((float) Math.acos(1 - normal.x), 0, 0);
//                    m.rotate((float) Math.acos(normal.x), (float) Math.acos(normal.y), (float) Math.acos(normal.z));
                    m = m.inverse();
                    idx3d_Vector[] pp = new idx3d_Vector[3];
                    for (int i = 0; i < p.length; i++) {
                        pp[i] = w[i].transform(m);
                    }
                    float minx = Float.MAX_VALUE;
                    float miny = Float.MAX_VALUE;
                    float maxx = Float.MIN_VALUE;
                    float maxy = Float.MIN_VALUE;
                    for (int i = 0; i < p.length; i++) {
                        minx = Math.min(minx, pp[i].x);
                        miny = Math.min(miny, pp[i].y);
                        maxx = Math.max(maxx, pp[i].x);
                        maxy = Math.max(maxx, pp[i].y);
                    }
                    float shiftx = 0, shifty = 0;
                    if (minx < 0) {
                        shiftx = -minx;
                    } else if (maxx > getWidth()) {
                        shiftx = getWidth() - maxx;
                    }
                    if (miny < 0) {
                        shifty = -miny;
                    } else if (maxy > getHeight()) {
                        shifty = getHeight() - maxy;
                    }
                    for (int i = 0; i < p.length; i++) {
                        pp[i].x += shiftx;
                        pp[i].y += shifty;
                    }
                    if (false) g.drawPolyline(
                            new int[]{
                                (int) pp[0].x,
                                (int) pp[1].x,
                                (int) pp[2].x,
                                (int) pp[0].x,},
                            new int[]{
                                (int) pp[0].y,
                                (int) pp[1].y,
                                (int) pp[2].y,
                                (int) pp[0].y,},
                            4);
                    for (int i = 0; i < pp.length; i++) {
                        if (drawString) g.drawString(pp[i].z + "", (int) pp[i].x, pp[i].y);
                    }
                }

            }

            @Override
            protected void fireMouseDragged(MouseEvent evt, idx3d_Triangle triangle) {
                super.fireMouseDragged(evt, triangle);
                update(evt, triangle);
            }

            @Override
            protected void fireMousePressed(MouseEvent evt, idx3d_Triangle triangle) {
                super.fireMousePressed(evt, triangle);
                update(evt, triangle);
            }

            protected void update(MouseEvent evt, idx3d_Triangle triangle) {
                if (triangle == null) {
                    w[0] = null;
                    p[0] = null;
                    pmx = -1;
                } else {
                    w[0] = triangle.p1.pos.clone();
                    w[1] = triangle.p2.pos.clone();
                    w[2] = triangle.p3.pos.clone();
                    p[0] = new idx3d_Vector(triangle.p1.x, triangle.p1.y, triangle.p1.z);
                    p[1] = new idx3d_Vector(triangle.p2.x, triangle.p2.y, triangle.p2.z);
                    p[2] = new idx3d_Vector(triangle.p3.x, triangle.p3.y, triangle.p3.z);
                    isAntialiased = renderPipeline.getScreen().antialias();
                    if (isAntialiased) {
                        p[0] = new idx3d_Vector(triangle.p1.x / 2, triangle.p1.y / 2, triangle.p1.z / 65536f);
                        p[1] = new idx3d_Vector(triangle.p2.x / 2, triangle.p2.y / 2, triangle.p2.z / 65536f);
                        p[2] = new idx3d_Vector(triangle.p3.x / 2, triangle.p3.y / 2, triangle.p3.z / 65536f);
                    }
                    idx3d_Camera camera = scene.camera(cameraName);
                    screenScale = camera.getScreenScale();
                    fovFact = camera.getFOVFact();
                    p2[0] = triangle.p1.pos2.clone();
                    p2[1] = triangle.p2.pos2.clone();
                    p2[2] = triangle.p3.pos2.clone();
                    screenWidth = camera.getScreenWidth() / 2;
                    screenHeight = camera.getScreenHeight() / 2;
                    pmx = evt.getX();
                    pmy = evt.getY();
                }
            }
        };

        add(canvas);


        idx3d_Scene scene = new idx3d_Scene();
        idx3d_Object obj = idx3d_ObjectFactory.BOX(new idx3d_Vector(100, 100, 100));
        obj.setMaterial(new idx3d_Material(0x666666));
        /*
        obj.triangle(0).setTriangleMaterial(new idx3d_Material(0xff6666));
        obj.triangle(1).setTriangleMaterial(new idx3d_Material(0xff6666));
        obj.triangle(2).setTriangleMaterial(new idx3d_Material(0x66ff66));
        obj.triangle(3).setTriangleMaterial(new idx3d_Material(0x66ff66));
        */
        scene.addChild(obj);
        idx3d_Camera cam = new idx3d_Camera();
        cam.setPos(new idx3d_Vector(0, 0, -400));
        scene.addCamera("front", cam);

        cam = new idx3d_Camera();
        cam.setPos(new idx3d_Vector(400, 0, 0));
        scene.addCamera("right", cam);
        scene.setBackgroundColor(0xffffff);
        scene.addLight("light1", new idx3d_Light(new idx3d_Vector(300, 300, 300), 0xffffff, 0xffffff, 100, 50));
        scene.addLight("light2", new idx3d_Light(new idx3d_Vector(300, -300, -300), 0xffffff, 0xffffff, 100, 50));
        scene.addLight("light3", new idx3d_Light(new idx3d_Vector(-300, -300, -300), 0xffffff, 0xffffff, 100, 50));
        canvas.setScene(scene);
        canvas.setCamera("front");
        canvas.setInteractionMode(idx3d_JCanvas.Interaction.ROTATE);

    }


    /*
     * A function for creating a rotation matrix that rotates a vector called
     * "from" into another vector called "to".
     * Input : from[3], to[3] which both must be *normalized* non-zero vectors
     * Output: mtx[3][3] -- a 3x3 matrix in colum-major form
     * Author: Tomas Moller, 1999
     * As seen at http://lists.apple.com/archives/mac-opengl/2001/Jan/msg00059.html
     */
    public static idx3d_Matrix fromToRotation(idx3d_Vector from, idx3d_Vector to) {
//#define M(row,col) mtx9[col*4+row]
        final float EPSILON = 0.00001f;
        idx3d_Vector v;
        float e, h;
        v = idx3d_Vector.vectorProduct(from, to);
        e = idx3d_Vector.dotProduct(from, to);
        idx3d_Matrix m;
        if (e > 1.0 - EPSILON) /* "from" almost or equal to "to"-vector? */ {
            /* return identity */
            m = new idx3d_Matrix();
            return m;
        } else if (e < -1.0 + EPSILON) /* "from" almost or equal to negated "to"? */ {
            idx3d_Vector up, left;
            float invlen;
            float fxx, fyy, fzz, fxy, fxz, fyz;
            float uxx, uyy, uzz, uxy, uxz, uyz;
            float lxx, lyy, lzz, lxy, lxz, lyz;
            /* left=CROSS(from, (1,0,0)) */
            left = new idx3d_Vector(0.0f, from.z, -from.y);
            if (idx3d_Vector.dotProduct(left, left) < EPSILON) /* was left=CROSS(from,(1,0,0)) a good
            choice? */ {
                /* here we now that left = CROSS(from, (1,0,0)) will be a good
                choice */
                left = new idx3d_Vector(-from.z, 0.0f, from.x);
            }
            /* normalize "left" */
            invlen = (float) (1.0 / Math.sqrt(idx3d_Vector.dotProduct(left, left)));
            left.x *= invlen;
            left.y *= invlen;
            left.z *= invlen;
            up = idx3d_Vector.vectorProduct(left, from);
            /* now we have a coordinate system, i.e., a basis; */
            /* M=(from, up, left), and we want to rotate to: */
            /* N=(-from, up, -left). This is done with the matrix:*/
            /* N*M^T where M^T is the transpose of M */
            fxx = -from.x * from.x;
            fyy = -from.y * from.y;
            fzz = -from.z * from.z;
            fxy = -from.x * from.y;
            fxz = -from.x * from.z;
            fyz = -from.y * from.z;

            uxx = up.x * up.x;
            uyy = up.y * up.y;
            uzz = up.z * up.z;
            uxy = up.x * up.y;
            uxz = up.x * up.z;
            uyz = up.y * up.z;

            lxx = -left.x * left.x;
            lyy = -left.y * left.y;
            lzz = -left.z * left.z;
            lxy = -left.x * left.y;
            lxz = -left.x * left.z;
            lyz = -left.y * left.z;
            /* symmetric matrix */
            m = new idx3d_Matrix();
            m.m00 = fxx + uxx + lxx;
            m.m01 = fxy + uxy + lxy;
            m.m02 = fxz + uxz + lxz;
            m.m10 = m.m01;
            m.m11 = fyy + uyy + lyy;
            m.m12 = fyz + uyz + lyz;
            m.m20 = m.m02;
            m.m21 = m.m12;
            m.m22 = fzz + uzz + lzz;
        } else /* the most common case, unless "from"="to", or "from"=-"to" */ {
            /*
            #if 0
            // unoptimized version - a good compiler will optimize this.
            h=(1.0-e)/DOT(v,v);
            M(0, 0)=e+h*v[0]*v[0]; M(0, 1)=h*v[0]*v[1]-v[2]; M(0,
            2)=h*v[0]*v[2]+v[1];
            M(1, 0)=h*v[0]*v[1]+v[2]; M(1, 1)=e+h*v[1]*v[1]; M(1,
            2)h*v[1]*v[2]-v[0];
            M(2, 0)=h*v[0]*v[2]-v[1]; M(2, 1)=h*v[1]*v[2]+v[0]; M(2,
            2)=e+h*v[2]*v[2];
            #else*/
// ...otherwise use this hand optimized version (9 mults less)
            float hvx, hvz, hvxy, hvxz, hvyz;
            h = (float) ((1.0 - e) / idx3d_Vector.dotProduct(v, v));
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            m = new idx3d_Matrix();
            m.m00 = e + hvx * v.x;
            m.m01 = hvxy - v.z;
            m.m02 = hvxz + v.y;
            m.m10 = hvxy + v.z;
            m.m11 = e + h * v.y * v.y;
            m.m12 = hvyz - v.x;
            m.m20 = hvxz - v.y;
            m.m21 = hvyz + v.x;
            m.m22 = e + hvz * v.z;
//#endif
        }
//#undef M
        return m;
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
                JFrame f = new JFrame("Idx3D Interaction Test");
                f.add(new Idx3DInteractionDemo());
                f.setSize(400, 400);
                f.setVisible(true);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
