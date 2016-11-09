/* @(#)HUDBorder.java
 * Copyright (c) 2009 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik.player;

import java.awt.*;
import javax.swing.border.Border;

/**
 * HUDBorder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HUDBorder implements Border {
 public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
                        Graphics2D g = (Graphics2D) gr;
                        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                        g.setColor(new Color(0x77666666, true));
                        g.drawRoundRect(x, y, width - 2, height - 2, 18, 18);
                        g.setColor(new Color(0x77cccccc, true));
                        g.fillRoundRect(x + 1, y + 1, width - 3, height - 3, 16, 16);
                        
                        g.setColor(new Color(0xcc000000, true));
                        g.fillRoundRect(x + 4, y + 4, width - 8, height - 8, 10, 10);
                        g.setColor(new Color(0xdd000000, true));
                        g.drawRoundRect(x + 3, y + 3, width - 8, height - 8, 12, 12);
                    }

                    public Insets getBorderInsets(Component c) {
                        return new Insets(9, 9, 9, 9);
                    }

                    public boolean isBorderOpaque() {
                        return false;
                    }
}
