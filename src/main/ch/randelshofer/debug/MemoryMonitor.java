/* @(#)MemoryMonitor.java
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tracks Memory allocated and used, displayed in graph form.
 * @version $Id$
 * <br>2.0 2006-01-09 Complete rewrite.
 */
public class MemoryMonitor extends JPanel {
    private final static long serialVersionUID = 1L;

    static JCheckBox dateStampCB = new JCheckBox("Output Date Stamp");
    public Surface surf;
    JPanel controls;
    boolean doControls;
    JTextField tf;

    public MemoryMonitor() {
        initComponents();

        add(surf = new Surface(), BorderLayout.CENTER);
        surf.setOpaque(true);
        surf.setBackground(Color.black);
memoryLabel.setFont(new Font("Dialog",Font.PLAIN,11));
        long maxMem = Runtime.getRuntime().maxMemory();
        if (maxMem == Long.MAX_VALUE) {
            memoryLabel.setText("Max Memory: unlimited");
        } else {
            String unit = "bytes";
            float scaled = maxMem;
            if (scaled > 1024) {
                scaled = scaled / 1024f;
                unit = "KB";
            }
            if (scaled > 1024) {
                scaled = scaled / 1024f;
                unit = "MB";
            }
            if (scaled > 1024) {
                scaled = scaled / 1024f;
                unit = "GB";
            }
            if (unit == "bytes") {
                memoryLabel.setText("Max Memory: " + maxMem + " bytes");
            } else {
                memoryLabel.setText("Max Memory: "+scaled+" "+unit);
            }
        }
    }

    public void start() {
        surf.start();
    }

    public void stop() {
        surf.stop();
    }

    public class Surface extends JPanel implements Runnable {
    private final static long serialVersionUID = 1L;

        public Thread thread;
        public long sleepAmount = 1000;
        private Runtime r = Runtime.getRuntime();
        /*
        private int columnInc;
        private int pts[];
        private int tpts[];
        private int ptNum;
        private float freeMemory, totalMemory;
        private Rectangle graphOutlineRect = new Rectangle();
        private Rectangle2D mfRect = new Rectangle2D.Float();
        private Rectangle2D muRect = new Rectangle2D.Float();
        private Line2D graphLine = new Line2D.Float();
        private Color graphColor = new Color(46, 139, 87);
        private Color mfColor = new Color(0, 100, 0);
        private String usedStr;

        private float oldTotalMemory = -1;
        private float oldFreeMemory = -1;

        private String totalMemoryString;
         */
        // Data points of used memory
        private long[] used;
        private long[] total;
        private int index;
        private int count;
        private long[] tmp;
        private int w, h;
        private long totalMemory;
        private long freeMemory;
        private String totalMemoryStr;
        private String usedMemoryStr;
        private Color graphColor = new Color(46, 139, 87);
        private Color mfColor = new Color(0, 100, 0);
        private int ascent, descent;
        private int blockHeight;
        private int blockWidth;
        private int tick;
        private int i, n;
        private int y, oldy;
        private float scale;
        private int tooFar;

        public Surface() {
            setFont(new Font("Dialog", Font.PLAIN, 11));
            setBackground(Color.black);
            setOpaque(true);

            addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (thread == null) {
                        start();
                    } else {
                        stop();
                    }
                }
            });

            blockWidth = 60; // 1 minute
        }

        public void paintComponent(Graphics g) {
            if (ascent == 0) {
                ascent = g.getFontMetrics().getAscent();
                descent = g.getFontMetrics().getDescent();
            }

            w = getWidth();
            h = getHeight();

            // Clear
            g.setColor(getBackground());
            g.fillRect(0, 0, w, h);

            freeMemory = r.freeMemory();
            totalMemory = r.totalMemory();

            // .. Draw allocated and used strings ..
            g.setColor(Color.green);
            totalMemoryStr = (totalMemory / 1024) + "K allocated";
            usedMemoryStr = ((totalMemory - freeMemory) / 1024) + "K used";

            g.drawString(totalMemoryStr, 4, ascent + 1);
            g.drawString(usedMemoryStr, 4, h - descent - 1);

            // Calculate remaining bounds
            h -= (descent + ascent) * 2 + 8;
            g.translate(0, descent + ascent + 4);

            // .. Memory Free ..
            g.setColor(mfColor);
            blockHeight = h / 10;
            h = blockHeight * 10;
            for (i = 0, n = (int) (freeMemory / (float) totalMemory * 10); i < n; i++) {
                g.fillRect(5, i * blockHeight, 15, blockHeight - 1);
            }
            // .. Memory Used ..
            g.setColor(Color.green);
            for (; i < 10; i++) {
                g.fillRect(5, i * blockHeight, 15, blockHeight - 1);
            }

            // Calculate remaining bounds
            w -= 30;
            g.translate(25, 0);

            g.setColor(graphColor);
            g.drawRect(0, 0, w, 10 * blockHeight);

            // Draw rows
            for (i = 0; i < 10; i++) {
                g.fillRect(0, i * blockHeight, w, 1);
            }

            // Draw animated column movement
            tick = (tick + 1) % blockWidth;
            for (i = w - tick; i > 0; i -= blockWidth) {
                g.fillRect(i, 0, 1, 10 * blockHeight);
            }

            // Rescale data points if necessary
            if (used == null || used.length != w) {
                if (used == null) {
                    used = new long[w];
                    total = new long[w];
                } else {
                    index = Math.min(index, w);
                    count = Math.min(count, index + 1);

                    tmp = used;
                    used = new long[w];
                    System.arraycopy(tmp, 0, used, 0, index);
                    tmp = total;
                    total = new long[w];
                    System.arraycopy(tmp, 0, total, 0, index);
                }
            }

            // Add data point
            used[index] = totalMemory - freeMemory;
            total[index] = totalMemory;
            index = (index + 1) % used.length;
            if (count < w) {
                count++;
            }

            scale = h / (float) totalMemory;

            // Draw total graph
            g.setColor(Color.green);
            n = index - 1;
            if (n < 0) {
                n += w;
            }
            oldy = h - (int) (total[n] * scale);
            for (i = 1; i < count; i++) {
                n = index - i;
                if (n < 0) {
                    n += w;
                }

                y = h - (int) (total[n] * scale);
                if (y >= 0 || oldy >= 0) {
                    tooFar = Math.min(0, Math.min(oldy, y));
                    g.fillRect(w - i, Math.min(oldy, y) - tooFar, 1, Math.abs(oldy - y) + tooFar + 1);
                }
                oldy = y;
            }

            // Draw used graph
            g.setColor(Color.yellow);
            n = index - 1;
            if (n < 0) {
                n += w;
            }
            oldy = h - (int) (used[n] * scale);
            for (i = 1; i < count; i++) {
                n = index - i;
                if (n < 0) {
                    n += w;
                }

                y = h - (int) (used[n] * scale);
                if (y >= 0 || oldy >= 0) {
                    tooFar = Math.min(0, Math.min(oldy, y));
                    g.fillRect(w - i, Math.min(oldy, y) - tooFar, 1, Math.abs(oldy - y) + tooFar + 1);
                }
                oldy = y;
            }
        }

        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName("MemoryMonitor");
            thread.start();
        }

        public synchronized void stop() {
            thread = null;
            notify();
        }

        public void run() {
            Thread me = Thread.currentThread();

            while (thread == me && !isShowing() || getWidth() == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
            }


            while (thread == me && isShowing()) {
                repaint();
                try {
                    Thread.sleep(sleepAmount);
                } catch (InterruptedException e) {
                    break;
                }
            }
            thread = null;
        }
    }

    public static void main(String s[]) {
        final MemoryMonitor demo = new MemoryMonitor();
        WindowListener l = new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowDeiconified(WindowEvent e) {
                demo.surf.start();
            }

            public void windowIconified(WindowEvent e) {
                demo.surf.stop();
            }
        };
        JFrame f = new JFrame("Java2D Demo - MemoryMonitor");
        f.addWindowListener(l);
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(200, 200));
        f.setVisible(true);
        demo.surf.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        gcButton = new javax.swing.JButton();
        memoryLabel = new javax.swing.JLabel();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        gcButton.setText("GC");
        gcButton.addActionListener(formListener);
        jPanel1.add(gcButton);
        jPanel1.add(memoryLabel);

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == gcButton) {
                MemoryMonitor.this.gc(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void gc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gc
        Runtime.getRuntime().gc();
    }//GEN-LAST:event_gc
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton gcButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel memoryLabel;
    // End of variables declaration//GEN-END:variables
}
