/* @(#)ScrollPaneLayout2.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import java.awt.*;
import javax.swing.*;
/**
 * Objects of this class behave essentially like
 * javax.swing.ScrollPaneLayout but treats the top
 * right corner specially.
 * <p>
 * If there is a component in the top right corner, it
 * is granted to be visible when the vertical scroll bar
 * is visible.
 *
 * @author  werni
 */
public class ScrollPaneLayout2 extends ScrollPaneLayout implements javax.swing.plaf.UIResource  {
        private final static long serialVersionUID = 1L;

    /** Creates a new instance of CornerLayout2 */
    public ScrollPaneLayout2() {
    }
    
    /**
     * Same as ScrollPaneLayout.layoutContainer but the top right
     * corner is treated specially.
     *
     * @param parent the <code>Container</code> to lay out
     */
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        
        if (upperRight != null && rowHead == null) {
            if (vsb != null && vsb.isVisible()) {
                Rectangle vsbR = vsb.getBounds();
                
                upperRight.setBounds(vsbR.x, vsbR.y,
                vsbR.width, vsbR.width);
                
                vsb.setBounds(vsbR.x, vsbR.y + vsbR.width,
                vsbR.width, vsbR.height - vsbR.width);
            }
        }
         
    }
}
