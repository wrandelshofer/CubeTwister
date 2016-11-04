/*
 * @(#)CubeColorComboBoxModel.java  1.0  2001-08-03
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import java.beans.*;
import javax.swing.*;

/**
 * CubeColorComboBoxModel is used to present a choice of colors in the 
 * "Stickers" and "Parts" page of the CubeView.
 *
 * @author  werni
 * @version $Id$
 */
public class CubeColorComboBoxModel
extends AbstractListModel
implements ComboBoxModel, PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private Object selectedItem;
    private CubeModel cubeModel;
    private int cachedSize;
    
    public void setModel(CubeModel m) {
        if (cubeModel != null) {
            cubeModel.removePropertyChangeListener(this);
            
            fireIntervalRemoved(this, 0, cachedSize);
        }
        cubeModel = m;
        if (cubeModel != null) {
            //cubeModel.addPropertyChangeListener(this);
            cachedSize = cachedSize = cubeModel.getColors().getChildCount();
            cubeModel.getColors().addPropertyChangeListener(this);
            fireIntervalAdded(this, 0, cachedSize);
        }
    }
    
    /** Return the selected item  */
    public Object getSelectedItem() {
        return selectedItem;
    }
    
    /** Set the selected item  */
    public void setSelectedItem(Object anItem) {
        selectedItem = anItem;
        fireContentsChanged(this, -1, -1);
    }
    
    /**
     * Returns the length of the list.
     */
    public int getSize() {
        return cachedSize;
    }
    
    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return (cubeModel == null || index >= cubeModel.getColors().getChildCount()) ? null : cubeModel.getColors().getChildAt(index);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name.equals(EntityModel.PROP_CHILD_COUNT)) {
            fireIntervalRemoved(this, 0, cachedSize);
            fireIntervalAdded(this, 0, cachedSize = cubeModel.getColors().getChildCount());
        }
    }    
}

