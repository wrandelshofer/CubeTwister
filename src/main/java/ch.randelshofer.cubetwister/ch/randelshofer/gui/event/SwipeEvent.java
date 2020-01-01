/* @(#)SwipeEvent.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.event;

import org.jhotdraw.annotation.Nonnull;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * SwipeEvent.
 *
 * @author Werner Randelshofer
 */
public class SwipeEvent extends MouseEvent {
    private final static long serialVersionUID = 1L;

    private float angle;

    public SwipeEvent(@Nonnull Component source, int id, long when, int modifiersEx,
                      int x, int y, int clickCount, boolean popupTrigger,
                      int button, float angle) {
        super(source, id, when, modifiersEx,
                x, y, clickCount, popupTrigger,
                button);
    }

    public SwipeEvent(@Nonnull MouseEvent evt, float angle) {
        super(evt.getComponent(), evt.getID(), evt.getWhen(), evt.getModifiersEx(),
                evt.getX(), evt.getY(), evt.getClickCount(), evt.isPopupTrigger(),
                evt.getButton());
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }
}
