/*
 * @(#)LayeredBorderLayout.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.JLayeredPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Iterator;

/**
 * LayoutManager which supports overlapping border layouts for use with
 * JLayeredPane.
 * <p>
 * The border layout is computed individually for each layer of the
 * JLayeredPane.
 *
 * @author Werner Randelshofer
 *  @version $Id$
 * <br>1.0 Jan 5, 2008 Created.
 */
public class LayeredBorderLayout implements LayoutManager2 {

    /**
     * The north layout constraint (top of container).
     */
    public static final String NORTH = "North";
    /**
     * The south layout constraint (bottom of container).
     */
    public static final String SOUTH = "South";
    /**
     * The east layout constraint (right side of container).
     */
    public static final String EAST = "East";
    /**
     * The west layout constraint (left side of container).
     */
    public static final String WEST = "West";
    /**
     * The center layout constraint (middle of container).
     */
    public static final String CENTER = "Center";
    /**
     * The south-center layout constraint (bottom middle of container).
     */
    public static final String SOUTH_CENTER = "SouthCenter";
    /**
     * The south-east layout constraint (bottom right of container).
     */
    public static final String SOUTH_EAST = "SouthEast";
    /**
     * The PIP south-east layout constraint (bottom right eighth of container
     * with insets).
     */
    public static final String PIP_SOUTH_EAST = "PIPSouthEast";
    /**
     * The absolute layout constraint. The bounds of components with this
     * constraint are not touched by the layout manager.
     */
    public static final String ABSOLUTE = "Absolute";

    /*
     * This hashtable maintains the association between
     * a component and its constraints.
     * The Keys in <code>comptable</code> are the components and the
     * values are the instances of <code>String</code>.
     *
     * @serial
     */
    @Nonnull
    protected HashMap<Component, Object> comptable = new HashMap<Component, Object>();
    /**
     * The horizontal gap between components.
     */
    private int hgap;
    /**
     * The vertical gap between components.
     */
    private int vgap;
    private double pipScaleFactor = 0.125;

    public void setPipScaleFactor(double sf) {
        pipScaleFactor = sf;
    }

    @Override
    public void addLayoutComponent(Component comp, @Nullable Object constraints) {
        comptable.put(comp, (constraints == null) ? CENTER : constraints);
    }

    @Nonnull
    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
        // nothing to do
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, name);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        comptable.remove(comp);
    }

    @Nonnull
    @Override
    public Dimension preferredLayoutSize(Container container) {
        JLayeredPane target = (JLayeredPane) container;
        synchronized (target.getTreeLock()) {
            HashMap<Integer,Dimension> layerDims = new HashMap<Integer,Dimension>();

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Dimension dim;
                if (!layerDims.containsKey(layer)) {
                    dim = new Dimension(0, 0);
                    layerDims.put(layer, dim);
                } else {
                    dim = layerDims.get(layer);
                }

                if (constraint.equals(CENTER)
                        || constraint.equals(SOUTH_CENTER)
                        || constraint.equals(SOUTH_EAST)) {
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    dim.height = Math.max(dim.height, d.height);
                }
            }
            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Dimension dim;
                dim = layerDims.get(layer);
                if (constraint.equals(EAST)) {
                    Dimension d = c.getPreferredSize();
                    dim.width += d.width + hgap;
                    dim.height = Math.max(d.height, dim.height);
                } else if (constraint.equals(WEST)) {
                    Dimension d = c.getPreferredSize();
                    dim.width += d.width + hgap;
                    dim.height = Math.max(d.height, dim.height);
                } else if (constraint.equals(NORTH)) {
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + vgap;
                } else if (constraint.equals(SOUTH)) {
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + vgap;
                }
            }

            Dimension dim = new Dimension(0, 0);
            for (Iterator i = layerDims.values().iterator(); i.hasNext();) {
                Dimension d = (Dimension) i.next();
                dim.width = Math.max(dim.width, d.width);
                dim.height = Math.max(dim.height, d.height);
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    @Nonnull
    public Dimension minimumLayoutSize(Container container) {
        JLayeredPane target = (JLayeredPane) container;
        synchronized (target.getTreeLock()) {
            HashMap<Integer,Dimension> layerDims = new HashMap<Integer,Dimension>();

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Dimension dim;
                if (!layerDims.containsKey(layer)) {
                    dim = new Dimension(0, 0);
                    layerDims.put(layer, dim);
                } else {
                    dim = layerDims.get(layer);
                }

                if (constraint.equals(CENTER)
                        || constraint.equals(SOUTH_CENTER)
                        || constraint.equals(SOUTH_EAST)) {
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(dim.width, d.width);
                    dim.height = Math.max(dim.height, d.height);
                }
            }
            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Dimension dim;
                dim = layerDims.get(layer);

                if (constraint.equals(EAST)) {
                    Dimension d = c.getMinimumSize();
                    dim.width += d.width + hgap;
                    dim.height = Math.max(d.height, dim.height);
                } else if (constraint.equals(WEST)) {
                    Dimension d = c.getMinimumSize();
                    dim.width += d.width + hgap;
                    dim.height = Math.max(d.height, dim.height);
                } else if (constraint.equals(NORTH)) {
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + vgap;
                } else if (constraint.equals(SOUTH)) {
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + vgap;
                }
            }

            Dimension dim = new Dimension(0, 0);
            for (Iterator i = layerDims.values().iterator(); i.hasNext();) {
                Dimension d = (Dimension) i.next();
                dim.width = Math.max(dim.width, d.width);
                dim.height = Math.max(dim.height, d.height);
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    @Override
    public void layoutContainer(Container container) {
        JLayeredPane target = (JLayeredPane) container;
        synchronized (target.getTreeLock()) {
            int height = target.getHeight();
            int width = target.getWidth();

            Insets insets = target.getInsets();

            HashMap<Integer,Insets> layerIns = new HashMap<Integer,Insets>();
            boolean ltr = target.getComponentOrientation().isLeftToRight();

            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Insets ins;
                if (!layerIns.containsKey(layer)) {
                    ins = new Insets(insets.top, insets.left, insets.bottom, insets.right);
                    layerIns.put(layer, ins);
                } else {
                    ins = layerIns.get(layer);
                }
                int top = ins.top;
                int bottom = height - ins.bottom;
                int left = ins.left;
                int right = width - ins.right;
                if (constraint.equals(NORTH)) {
                    c.setSize(right - left, c.getHeight());
                    Dimension d = c.getPreferredSize();
                    c.setBounds(left, top, right - left, d.height);
                    ins.top += d.height + vgap;
                } else if (constraint.equals(SOUTH)) {
                    c.setSize(right - left, c.getHeight());
                    Dimension d = c.getPreferredSize();
                    c.setBounds(left, bottom - d.height, right - left, d.height);
                    ins.bottom -= d.height + vgap;
                }
            }
            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Insets ins;
                ins = layerIns.get(layer);
                int top = ins.top;
                int bottom = height - ins.bottom;
                int left = ins.left;
                int right = width - ins.right;
                if (constraint.equals(EAST)) {
                    c.setSize(c.getWidth(), bottom - top);
                    Dimension d = c.getPreferredSize();
                    c.setBounds(right - d.width, top, d.width, bottom - top);
                    ins.right += d.width + hgap;
                } else if (constraint.equals(WEST)) {
                    c.setSize(c.getWidth(), bottom - top);
                    Dimension d = c.getPreferredSize();
                    c.setBounds(left, top, d.width, bottom - top);
                    ins.left += d.width + hgap;
                }
            }
            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Insets ins;
                ins = layerIns.get(layer);
                int top = ins.top;
                int bottom = height - ins.bottom;
                int left = ins.left;
                int right = width - ins.right;
                if (constraint.equals(SOUTH_CENTER)) {
                    Dimension d = c.getPreferredSize();
                    c.setBounds(
                            insets.left + (width - d.width) / 2,
                            height - insets.bottom - d.height,
                            d.width,
                            d.height);
                } else if (constraint.equals(SOUTH_EAST)) {
                    Dimension d = c.getPreferredSize();
                    c.setBounds(
                            insets.left + (width - d.width),
                            height - insets.bottom - d.height,
                            d.width,
                            d.height);
                } else if (constraint.equals(PIP_SOUTH_EAST)) {
                    Dimension d = new Dimension((width - insets.right - insets.left), (height - insets.top - insets.bottom));
                    if (pipScaleFactor <= 0.25) {
                        Insets pipInsets = new Insets(10, 0, 10, 0);
                        d.width = d.height = (int) (Math.sqrt(width*height)*pipScaleFactor);
                        c.setBounds(
                                insets.left + (width - d.width) - pipInsets.right,
                                height - insets.bottom - d.height - pipInsets.bottom,
                                d.width,
                                d.height);
                    } else {
                        Insets ins0 = layerIns.get(0);
                        if (ins0 == null) {
                            ins0 = new Insets(0, 0, 0, 0);
                        }
                        Insets pipInsets = new Insets(10, 0, 10, 10);
                        d.width = d.height = (int) (Math.sqrt(width*height)*pipScaleFactor);
                        Dimension d0 = new Dimension(width - ins0.right - ins0.left - d.width, height - ins0.top - ins0.bottom);
                        if (d.width > d0.width * pipScaleFactor) {
                            d.height = d.width = (int) ((d.width + d0.width) / (1 + pipScaleFactor) * pipScaleFactor);
                        }
                        c.setBounds(
                                insets.left + (width - d.width) - pipInsets.right,
                                insets.top + pipInsets.top + (height - d.height - pipInsets.top - pipInsets.bottom) / 2,
                                d.width,
                                d.height);
                        ins0.right += d.width;
                    }
                }
            }
            for (int i = 0, n = target.getComponentCount(); i < n; i++) {
                Component c = target.getComponent(i);
                String constraint = (String) comptable.get(c);
                Integer layer = new Integer(target.getLayer(c));
                Insets ins;
                ins = layerIns.get(layer);
                int top = ins.top;
                int bottom = height - ins.bottom;
                int left = ins.left;
                int right = width - ins.right;
                if (constraint.equals(CENTER)) {
                    c.setBounds(left, top, right - left, bottom - top);
                }
            }
        }
    }
}
