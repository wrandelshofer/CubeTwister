/*
 * @(#)Debugger.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
/*
 * Debugger.java
 *
 * Created on October 18, 2001, 7:29 PM
 */

package ch.randelshofer.debug;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  Werner Randelshofer
 */
public class Debugger extends javax.swing.JFrame {
    private final static long serialVersionUID = 1L;
    private static Debugger instance;
    
    public static Debugger getInstance() {
        if (instance == null) {
            instance = new Debugger();
        }
        return instance;
    }

    /** Creates new form DebugFrame */
    public Debugger() {
        initComponents();
    }
    
    @SuppressWarnings({"deprecation"})
    public void show() {
        super.show();
        memoryMonitor.start();
    }
    
    public void dispose() {
        memoryMonitor.stop();
        super.dispose();
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(300, 200);
    }
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTabbedPane1 = new javax.swing.JTabbedPane();
        memoryMonitor = new ch.randelshofer.debug.MemoryMonitor();
        clipboardInspector = new ch.randelshofer.debug.ClipboardInspector();
        systemPropertiesInspector1 = new ch.randelshofer.debug.SystemPropertiesInspector();
        desktopPropertiesInspector1 = new ch.randelshofer.debug.DesktopPropertiesInspector();
        systemColorsInspector1 = new ch.randelshofer.debug.SystemColorsInspector();
        uIDefaultsInspector1 = new ch.randelshofer.debug.UIDefaultsInspector();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Debugger");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 11));
        jTabbedPane1.addTab("Memory", null, memoryMonitor, "");

        jTabbedPane1.addTab("Clipboard", null, clipboardInspector, "");

        jTabbedPane1.addTab("System Properties", systemPropertiesInspector1);

        jTabbedPane1.addTab("Desktop Properties", desktopPropertiesInspector1);

        jTabbedPane1.addTab("System Colors", systemColorsInspector1);

        jTabbedPane1.addTab("UI Defaults", uIDefaultsInspector1);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        dispose();
    }//GEN-LAST:event_exitForm

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        JFrame f = new Debugger();
        f.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent evt) { System.exit(0); } } );
        f.setVisible(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ch.randelshofer.debug.ClipboardInspector clipboardInspector;
    private ch.randelshofer.debug.DesktopPropertiesInspector desktopPropertiesInspector1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private ch.randelshofer.debug.MemoryMonitor memoryMonitor;
    private ch.randelshofer.debug.SystemColorsInspector systemColorsInspector1;
    private ch.randelshofer.debug.SystemPropertiesInspector systemPropertiesInspector1;
    private ch.randelshofer.debug.UIDefaultsInspector uIDefaultsInspector1;
    // End of variables declaration//GEN-END:variables

}