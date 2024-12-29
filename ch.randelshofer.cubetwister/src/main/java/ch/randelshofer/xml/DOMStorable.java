/*
 * @(#)DOMStorable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.xml;

/**
 * DOMStorable.
 *
 * @author  Werner Randelshofer
 */
public interface DOMStorable {
    public void write(DOMOutput out);
    public void read(DOMInput in);
}
