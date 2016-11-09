/* @(#)SwipeEvent.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui.event;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * SwipeEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 2008-12-31 Created.
 */
public class SwipeEvent extends MouseEvent {
    private final static long serialVersionUID = 1L;

    private float angle;

    public SwipeEvent(Component source, int id, long when, int modifiersEx,
                      int x, int y, int clickCount, boolean popupTrigger,
                      int button, float angle) {
        super(source,  id,  when,  modifiersEx,
                       x,  y,  clickCount,  popupTrigger,
                       button);
    }
    public SwipeEvent(MouseEvent evt, float angle) {
        super(evt.getComponent(),  evt.getID(), evt.getWhen(),  evt.getModifiersEx(),
                       evt.getX(),  evt.getY(), evt.getClickCount(),  evt.isPopupTrigger(),
                       evt.getButton());
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }
}
