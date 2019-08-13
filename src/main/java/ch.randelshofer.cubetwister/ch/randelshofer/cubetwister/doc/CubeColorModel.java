/* @(#)CubeColorModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.undo.*;
import ch.randelshofer.xml.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.beans.*;
import javax.swing.undo.*;

/**
 * Holds a single color value. CubeColorModel is a child of CubeColorsModel.
 *
 * @author Werner Randelshofer
 */
public class CubeColorModel extends InfoModel implements DOMStorable {
    private final static long serialVersionUID = 1L;
    public static final String PROP_COLOR = "Color";

    private Color color = Color.white;

    /**
     * Creates new CubeColorModel
     */
    public CubeColorModel() {
    }

    /**
     * Creates new CubeColorModel
     */
    public CubeColorModel(String name, Color color) {
        this.basicSetName(name);
        this.color = color;
    }
    public Color getColor() {
        return color;
    }

    public void setColor(Color value) {
        if (value != color) {
            Color oldValue = color;
            basicSetColor(value);

            firePropertyChange(PROP_COLOR, oldValue, value);

            fireUndoableEditHappened(
                new UndoableObjectEdit(this, "Color", oldValue, value) {
    private final static long serialVersionUID = 1L;
                    public void revert(Object a, Object b) {
                        color = (Color) b;
                        firePropertyChange(PROP_COLOR, a, b);
                    }
                }
            );
        }
    }
   public void basicSetColor(Color value) {
            color = value;
    }
    
    public void read(DOMInput in) {
        float[] components = new float[4];
        components[0] = in.getAttribute("red", 0f);
        components[1] = in.getAttribute("green", 0f);
        components[2] = in.getAttribute("blue", 0f);
        components[3] = in.getAttribute("alpha", 0f);
        color = new Color(components[0], components[1], components[2], components[3]);
        basicSetName(in.getText());
    }
    
    public void write(DOMOutput out) {
        float[] components = color.getComponents(new float[4]);
        out.setAttribute("red", Float.toString(components[0]));
        out.setAttribute("green", Float.toString(components[1]));
        out.setAttribute("blue", Float.toString(components[2]));
        out.setAttribute("alpha", Float.toString(components[3]));
        out.addText(getName());
    }

    
}
