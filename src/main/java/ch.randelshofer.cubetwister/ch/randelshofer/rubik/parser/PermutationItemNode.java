package ch.randelshofer.rubik.parser;

public class PermutationItemNode extends Node {
    enum PartType {
        SIDE,
        EDGE,
        CORNER
    }

    public PermutationItemNode() {
        super();
    }


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
     * @see ch.randelshofer.rubik.Cube
     */
    private int location;

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
