/* @(#)TestDnDJList.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.test;

import javax.swing.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.gui.tree.*;
import ch.randelshofer.quaqua.QuaquaManager;
import javax.swing.tree.*;

/**
 * A JFrame with two DnDJLists.
 *
 * @author  Werner Randelshofer
 */
public class TestJDnDTree extends javax.swing.JFrame {
        private final static long serialVersionUID = 1L;

    /** Creates new form TestDnDList */
    public TestJDnDTree() {
        initComponents();
    DefaultMutableTreeModel m = new DefaultMutableTreeModel();
        m.insertNodeInto(new DefaultMutableTreeNode("anna"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("berta"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("carla"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("daniel"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("egon"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("franz"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("gabriel"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("helene"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("isabelle"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("josef"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("lena"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        
        tree1.setModel(m);
        //list1.set

        m = new DefaultMutableTreeModel();
        m.insertNodeInto(new DefaultMutableTreeNode("1"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("2"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
        m.insertNodeInto(new DefaultMutableTreeNode("3"), (MutableTreeNode) m.getRoot(), m.getChildCount(m.getRoot()));
 
        tree2.setModel(m);
        
        setSize(600,400);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        tree1 = new ch.randelshofer.gui.JDnDTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        tree2 = new ch.randelshofer.gui.JDnDTree();
        jLabel1 = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tree1.setRootVisible(false);
        tree1.setShowsRootHandles(true);
        jScrollPane1.setViewportView(tree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 20, 20, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        tree2.setRootVisible(false);
        tree2.setShowsRootHandles(true);
        jScrollPane2.setViewportView(tree2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 10, 20, 20);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        jLabel1.setText("<html><b>DnDJList demo</b><br>\nDrag and drop elements from one list to the other.<br>\nSince DnDJList is a subclass of MutableJList, you can use the popup menu available on both lists to use the clipboard as an alternative method for transfering an element from one list to the other. The popup menus also allow the creation and deletion of elements.\n");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        getContentPane().add(jLabel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
        } catch (Throwable e) {
        }
        new TestJDnDTree().setVisible(true);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private ch.randelshofer.gui.JDnDTree tree1;
    private ch.randelshofer.gui.JDnDTree tree2;
    // End of variables declaration//GEN-END:variables
    
}
