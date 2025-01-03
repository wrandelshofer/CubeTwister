/*
 * @(#)Vector_Inspector.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */

package idx3d.debug;

import org.jhotdraw.annotation.Nonnull;

public class Vector_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public Vector_Inspector(@Nonnull java.util.Vector vec, String id) {
        super(vec, id);
        addEntry(new InspectorFrameEntry(this, "int", "size", vec.size() + ""));

        java.util.Enumeration enumer = vec.elements();
        int index = 0;
        while (enumer.hasMoreElements()) {
            addEntry(new InspectorFrameEntry(this, enumer.nextElement(), "elementAt(" + (index++) + ")"));
        }

    }
	
}