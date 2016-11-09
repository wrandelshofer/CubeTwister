/* @(#)StateEvent.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

import java.util.EventObject;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StateEvent
extends EventObject {
    private final static long serialVersionUID = 1L;
  /**
   * State.
   */
  private int state;

  public StateEvent(Object source, int state) {
    super(source);
    this.state = state;
  }
  
  public int getNewState() {
    return state;
  }
}
