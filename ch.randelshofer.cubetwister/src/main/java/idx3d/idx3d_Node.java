/* @(#)idx3d_Node.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package idx3d;

import org.jhotdraw.annotation.Nullable;

import java.util.Collections;
/**
 * idx3d_Node objects divide into group node objects and leaf node objects.
 * idx3d_Group node objects serve to group their child node objects together
 * according to the group node's semantics. Leaf nodes specify the geometric
 * elements that idx3d uses in rendering; specifically, geometric objects,
 * and lights.
 *
 * @author  Werner Randelshofer
 */
public class idx3d_Node extends idx3d_CoreObject {
    /**
     * Parent object. 
     * FIXME: This should be package protected, and should not be accessed from
     * outside this package!
     */
    @Nullable
    public idx3d_Group parent = null;
    
    /**
     * This flag returns true if the data fields of this node are valid for
     * the render pipeline.
     */
    private boolean isValid = false;
    
    /** Creates a new instance. */
    public idx3d_Node() {
    }

    @Nullable
    public final idx3d_Group getParent() {
        return parent;
    }
    /**
     * Invalidate this node and all its parent nodes.
     */
    public void invalidate() {
        isValid = false;
        if (parent != null) {
            parent.invalidate();
        }
    }
    
    /**
     * Returns true if this node is valid for rendering with the render pipeline.
     */
    public boolean isValid() {
        return isValid;
    }
    /**
     * Validates this node and all its children.
     */
    public void validate() {
            isValid = true;
    }

    /**
     * Enumerates the children of this node.
     */
    public Iterable<idx3d_Node> children() {
        return Collections.emptyList();
    }
}
