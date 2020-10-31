/*
 * @(#)SwipeListener.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.event;

import java.util.EventListener;

/**
 * SwipeListener.
 *
 * @author Werner Randelshofer
 */
public interface SwipeListener extends EventListener {
    public void faceSwiped(SwipeEvent evt);

}
