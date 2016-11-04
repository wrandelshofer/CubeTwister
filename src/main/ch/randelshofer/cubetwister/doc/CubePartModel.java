/*
 * @(#)CubePartModel.java  1.0.1  2002-07-29
 *
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.*;
import ch.randelshofer.undo.*;
import javax.swing.undo.*;
/**
 * Model for the properties of a cube part.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2002-07-29 Removing an outline color which
 * is also the fill color of the part did not fire a change event.
 * <br>1.0 2001-10-12
 */
public class CubePartModel extends EntityModel {
    private final static long serialVersionUID = 1L;
    public static final String PROP_NAME = "Name";
    public static final String PROP_VISIBLE = CubeAttributes.PART_VISIBLE_PROPERTY;
    public static final String PROP_FILL_COLOR = CubeAttributes.PART_FILL_COLOR_PROPERTY;
    public static final String PROP_OUTLINE_COLOR = CubeAttributes.PART_OUTLINE_COLOR_PROPERTY;

    /**
     * The visible property.
     */
    private boolean isVisible = true;
    /** 
     * The fill color property.
     */
    private CubeColorModel fillColor;
    /**
     * The outline color property.
     */
    private CubeColorModel outlineColor;
    
    /**
     * The name property.
     */
    private String name;
    
    /**
     * Creates new CubePartModel
     */
    public CubePartModel() {
    }

    /**
     * Gets the name property.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name property.
     */
    public void setName(String value) {
        if (value != name) {
            String oldValue = name;
            name = value;

            firePropertyChange(PROP_NAME, oldValue, value);

            fireUndoableEditHappened(
                new UndoableObjectEdit(this, "Name", oldValue, value) {
    private final static long serialVersionUID = 1L;
                    public void revert(Object a, Object b) {
                        name = (String) b;
                        firePropertyChange(PROP_NAME, a, b);
                    }
                }
            );
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
    
   public void basicSetVisible(boolean value) {
            isVisible = value;
    }
    public void setVisible(boolean value) {
        if (value != isVisible) {
            boolean oldValue = isVisible;
            basicSetVisible(value);

            firePropertyChange(PROP_VISIBLE, oldValue, value);

            fireUndoableEditHappened(
                new UndoableBooleanEdit(this, "Visible", oldValue, value) {
    private final static long serialVersionUID = 1L;
                    public void revert(boolean a, boolean b) {
                        isVisible = b;
                        firePropertyChange(PROP_VISIBLE, a, b);
                    }
                }
            );
        }
    }

    public CubeColorModel getFillColorModel() {
        return fillColor;
    }
    
   public void basicSetFillColorModel(CubeColorModel value) {
            fillColor = value;
    }
    public void setFillColorModel(CubeColorModel value) {
        if (value != fillColor) {
            CubeColorModel oldValue = fillColor;
            basicSetFillColorModel(value);

            firePropertyChange(PROP_FILL_COLOR, oldValue, value);

            UndoableEdit edit = new UndoableObjectEdit(this, "Fill Color", oldValue, value) {
    private final static long serialVersionUID = 1L;
                public void revert(Object a, Object b) {
                    fillColor = (CubeColorModel) b;
                    firePropertyChange(PROP_FILL_COLOR, a, b);
                }
            };
            fireUndoableEditHappened(edit);
        }
    }

    public CubeColorModel getOutlineColorModel() {
        return outlineColor;
    }
    
   public void basicSetOutlineColorModel(CubeColorModel value) {
            outlineColor = value;
    }
    public void setOutlineColorModel(CubeColorModel value) {
        if (value != outlineColor) {
            CubeColorModel oldValue = outlineColor;
            basicSetOutlineColorModel(value);

            firePropertyChange(PROP_OUTLINE_COLOR, oldValue, value);

            UndoableEdit edit = new UndoableObjectEdit(this, "Outline Color", oldValue, value) {
    private final static long serialVersionUID = 1L;
                public void revert(Object a, Object b) {
                    outlineColor = (CubeColorModel) b;
                    firePropertyChange(PROP_OUTLINE_COLOR, a, b);
                }
            };
            fireUndoableEditHappened(edit);
        }
    }
    public void removeNotify(EntityModel m) {
        if (m == fillColor) {
            EntityModel colors = m.getParent();
            if (colors.getChildCount() < 2) {
                fillColor = null;
            } else {
                int index = colors.getIndex(m);
                fillColor = (CubeColorModel) colors.getChildAt(index + ((index == 0) ? 1 : -1));
            }
            firePropertyChange(PROP_FILL_COLOR, m, fillColor);

            fireUndoableEditHappened(
                new UndoableObjectEdit(this, "Fill Color", m, fillColor) {
    private final static long serialVersionUID = 1L;
                    public void revert(Object a, Object b) {
                        fillColor = (CubeColorModel) b;
                        firePropertyChange(PROP_FILL_COLOR, a, b);
                    }
                }
            );

        }
        if (m == outlineColor) {
            EntityModel colors = m.getParent();
            if (colors.getChildCount() < 2) {
                outlineColor = null;
            } else {
                int index = colors.getIndex(m);
                outlineColor = (CubeColorModel) colors.getChildAt(index + ((index == 0) ? 1 : -1));
            }
            firePropertyChange(PROP_OUTLINE_COLOR, m, outlineColor);

            fireUndoableEditHappened(
                new UndoableObjectEdit(this, "Outline Color", m, outlineColor) {
    private final static long serialVersionUID = 1L;
                    public void revert(Object a, Object b) {
                        outlineColor = (CubeColorModel) b;
                        firePropertyChange(PROP_OUTLINE_COLOR, a, b);
                    }
                }
            );
        }
    }
}
