/* @(#)StateEvent.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.util.EventObject;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
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
