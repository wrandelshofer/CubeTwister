/*
 * @(#)CubeStickerModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.undo.UndoableBooleanEdit;
import ch.randelshofer.undo.UndoableObjectEdit;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Model for the properties of a cube part.
 *
 * @author  Werner Randelshofer
 */
public class CubeStickerModel extends EntityModel {
    private final static long serialVersionUID = 1L;
    public static final String PROP_NAME = "Name";
    public static final String PROP_VISIBLE = CubeAttributes.STICKER_VISIBLE_PROPERTY;
    public static final String PROP_FILL_COLOR = CubeAttributes.STICKER_FILL_COLOR_PROPERTY;

    /**
     * The visible property.
     */
    private boolean isVisible = true;
    /**
     * The fill color property.
     */
    @Nullable
    private CubeColorModel fillColor;
    /**
     * The name property.
     */
    private String name;

    @Nonnull
    private PropertyChangeListener fillColorHandler = new PropertyChangeListener() {

        @Override
        public void propertyChange(@Nonnull PropertyChangeEvent evt) {
            if (evt.getPropertyName() == CubeColorModel.PROP_COLOR) {
                firePropertyChange(PROP_FILL_COLOR, evt.getOldValue(), evt.getNewValue());
            }
        }

    };


    /**
     * Creates new CubeStickerModel
     */
    public CubeStickerModel() {
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

    @Nullable
    public CubeColorModel getFillColorModel() {
        return fillColor;
    }

    public void basicSetFillColorModel(CubeColorModel newValue) {
       if (fillColor!=null) {
           fillColor.removePropertyChangeListener(fillColorHandler);
       }
            fillColor = newValue;
       if (fillColor!=null) {
           fillColor.addPropertyChangeListener(fillColorHandler);
       }
    }
    public void setFillColorModel(CubeColorModel value) {
        if (value != fillColor) {
            CubeColorModel oldValue = fillColor;
            basicSetFillColorModel(value);

            firePropertyChange(PROP_FILL_COLOR, oldValue, value);

            fireUndoableEditHappened(
                    new UndoableObjectEdit(this, "Fill Color", oldValue, value) {
                        private final static long serialVersionUID = 1L;

                        public void revert(Object a, Object b) {
                            fillColor = (CubeColorModel) b;
                            firePropertyChange(PROP_FILL_COLOR, a, b);
                        }
                    }
            );
        }
    }

    public void removeNotify(@Nonnull EntityModel m) {
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
    }
}
