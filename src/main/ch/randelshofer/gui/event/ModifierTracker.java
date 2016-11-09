/* @(#)ModifierTracker.java
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui.event;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import javax.swing.event.ChangeListener;

/**
 * ModifierTracker.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ModifierTracker {

    public final static String MODIFIERS_EX_PROPERTY = "modifiersEx";
    private static PropertyChangeSupport listeners;
    private static Handler handler;
    private static int modifiersEx;

    private static class Handler implements AWTEventListener {

        @Override
        public void eventDispatched(AWTEvent e) {
            if (e instanceof KeyEvent) {
                KeyEvent evt = (KeyEvent) e;

                int oldValue = modifiersEx;
                modifiersEx = evt.getModifiersEx();
                listeners.firePropertyChange(MODIFIERS_EX_PROPERTY, oldValue, modifiersEx);
            }
        }
    }

    public static void addModifierListener(PropertyChangeListener l) {
        if (listeners == null) {
            handler = new Handler();
            try {
                Toolkit.getDefaultToolkit().addAWTEventListener(handler, KeyEvent.KEY_EVENT_MASK);
            } catch (SecurityException e) {
                // Bail - we can't listen to modifier keys
                // e.printStackTrace();
            }
            listeners = new PropertyChangeSupport(ModifierTracker.class);
            modifiersEx = 0;
        }
        listeners.addPropertyChangeListener(l);
    }

    public static void removeModifierListener(PropertyChangeListener l) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(l);
            if (listeners.hasListeners(null)) {
                try {
                    Toolkit.getDefaultToolkit().removeAWTEventListener(handler);
                } catch (SecurityException e) {
                }
                handler = null;
                listeners = null;
            }
        }
    }
}
