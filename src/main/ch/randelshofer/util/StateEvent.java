/*
 * @(#)StateEvent.java  1.0  1999-10-19
 *
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.util.EventObject;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
 * @version    1.0  1999-10-19
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
