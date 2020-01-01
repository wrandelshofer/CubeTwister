/* @(#)CubeColorComboBoxModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * CubeColorComboBoxModel is used to present a choice of colors in the 
 * "Stickers" and "Parts" page of the CubeView.
 *
 * @author Werner Randelshofer
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
    @Nullable
    public Object getElementAt(int index) {
        return (cubeModel == null || index >= cubeModel.getColors().getChildCount()) ? null : cubeModel.getColors().getChildAt(index);
    }

    public void propertyChange(@Nonnull PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name.equals(EntityModel.PROP_CHILD_COUNT)) {
            fireIntervalRemoved(this, 0, cachedSize);
            fireIntervalAdded(this, 0, cachedSize = cubeModel.getColors().getChildCount());
        }
    }    
}

