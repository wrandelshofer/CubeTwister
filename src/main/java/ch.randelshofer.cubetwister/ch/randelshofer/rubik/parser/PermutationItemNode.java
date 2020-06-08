/*
 * @(#)PermutationItemNode.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.cube.Cube;
import org.jhotdraw.annotation.Nonnull;

public class PermutationItemNode extends Node {
    /**
     * The orientation of the part.
     * Values: 0, 1 for edge parts.
     * 0, 1, 2 for side parts.
     * 0, 1, 2, 3, 4, 5 for corner parts
     */
    private int orientation;
    /**
     * The location of the part.
     *
     * @see Cube
     */
    private int location;

    @Nonnull
    public PermutationItemNode clone() {
        return (PermutationItemNode) super.clone();

    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
