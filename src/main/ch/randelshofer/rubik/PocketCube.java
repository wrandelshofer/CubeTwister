/*
 * @(#)PocketCube.java  3.0  2009-01-01
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

/**
 * Represents the state of a 2-times sliced cube (Pocket Cube) by the location 
 * and orientation of its parts.
 * <p>
 * A Pocket Cube has 8 corner parts. The parts divide each face of the cube into
 * 2 x 2 layers.
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the corner parts:
 * <pre>
 *         +---+---+
 *         |4.0|2.0|
 *         +--- ---+ 
 *  ulb ufl|6.0|0.0|urf ubr  
 * +---+---+---+---+---+---+---+---+
 * |4.1|6.2|6.1|0.2|0.1|2.2|2.1|4.2|
 * +--- ---+--- ---+--- ---+--- ---+
 * |5.2|7.1|7.2|1.1|1.2|3.1|3.2|5.1|
 * +---+---+---+---+---+---+---+---+
 *  dbl dlf|7.0|1.0|dfr drb
 *         +--- ---+
 *         |5.0|3.0|
 *         +---+---+
 * </pre>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 * 
 * @author  Werner Randelshofer
 * @version 3.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>2.0 2007-12-29 Adapted to changes in AbstractCube.
 * <br>1.0  14 February 2005  Created.
 */
public class PocketCube extends AbstractCube {

    /** Creates a new instance. */
    public PocketCube() {
        super(2);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return 2 >>> (face / 3);
        } else {
            return 0;
        }
    }

    /**
     * Transforms the cube and fires a cubeTwisted event.
     *
     * @param  axis  0=x, 1=y, 2=z axis.
     * @param  layerMask A bitmask specifying the layers to be transformed.
     *           The size of the layer mask depends on the value returned by
     *           <code>getLayerCount(axis)</code>. For a 3x3x3 cube, the layer mask has the
     *           following meaning:
     *           3=rotate the whole cube;<br>
     *           1=twist slice near the axis (left, down, back)<br>
     *           2=twist slice far away from the axis (right, up, front)
     * @param  angle  positive values=clockwise rotation<br>
     *                negative values=counterclockwise rotation<br>
     *               1=90 degrees<br>
     *               2=180 degrees
     *
     * @see #getLayerCount()
     */
    @Override
    protected void transform0(int axis, int layerMask, int angle) {
        if (axis < 0 || axis > 2) {
            throw new IllegalArgumentException("axis: " + axis);
        }

        if (layerMask < 0 || layerMask >= 1 << layerCount) {
            throw new IllegalArgumentException("layerMask: " + layerMask);
        }

        if (angle < -2 || angle > 2) {
            throw new IllegalArgumentException("angle: " + angle);
        }
        if (angle == 0) {
            return; // NOP
        }

        // Convert angle -2 to 2 to simplify the switch statements
        int an = (angle == -2) ? 2 : angle;

        if ((layerMask & 1) != 0) {
            // twist at left, bottom, back
            switch (axis) {
                case 0: // x
                    switch (an) {
                        case -1:
                            twistL();
                            break;
                        case 1:
                            twistL();
                            twistL();
                            twistL();
                            break;
                        case 2:
                            twistL();
                            twistL();
                            break;
                    }
                    break;
                case 1: // y
                    switch (an) {
                        case -1:
                            twistD();
                            break;
                        case 1:
                            twistD();
                            twistD();
                            twistD();
                            break;
                        case 2:
                            twistD();
                            twistD();
                            break;
                    }
                    break;
                case 2: // z
                    switch (an) {
                        case -1:
                            twistB();
                            break;
                        case 1:
                            twistB();
                            twistB();
                            twistB();
                            break;
                        case 2:
                            twistB();
                            twistB();
                            break;
                    }
            }
        }
        if ((layerMask & 2) != 0) {

            // twist at right, top, front
            switch (axis) {
                case 0: // x
                    switch (an) {
                        case 1:
                            twistR();
                            break;
                        case -1:
                            twistR();
                            twistR();
                            twistR();
                            break;
                        case 2:
                            twistR();
                            twistR();
                            break;
                    }
                    break;
                case 1: // y
                    switch (an) {
                        case 1:
                            twistU();
                            break;
                        case -1:
                            twistU();
                            twistU();
                            twistU();
                            break;
                        case 2:
                            twistU();
                            twistU();
                            break;
                    }
                    break;
                case 2: // z
                    switch (an) {
                        case 1:
                            twistF();
                            break;
                        case -1:
                            twistF();
                            twistF();
                            twistF();
                            break;
                        case 2:
                            twistF();
                            twistF();
                            break;
                    }
            }
        }
    }

    /**
     * R.
     * <pre>
     *             +---+---+---+
     *             |   |   |2.0|
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |   |   |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |0.2|0.1|   |2.2|2.1|   |   |
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |   |   |   |   |   |1.1|1.2|   |3.1|3.2|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |   |   |1.0|
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |   |   |3.0|
     *             +---+---+---+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
    }

    /**
     * U.
     * <pre>
     *             +---+---+---+
     *             |4.0|   |2.0|
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |6.0|   |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|   |6.2|6.1|   |0.2|0.1|   |2.2|2.1|   |4.2|
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |   |   |   |
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |   |   |   |
     *             +---+---+---+
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
    }

    /**
     * F.
     * <pre>
     *             +---+---+---+
     *             |   |   |   |
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |6.0|   |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |6.2|6.1|   |0.2|0.1|   |   |   |   |   |
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |   |   |7.1|7.2|   |1.1|1.2|   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |7.0|   |1.0|
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |   |   |   |
     *             +---+---+---+
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
    }

    /**
     * L.
     * <pre>
     *             +---+---+---+
     *             |4.0|   |   |
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |6.0|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|   |6.2|6.1|   |   |   |   |   |   |   |4.2|
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |5.2|   |7.1|7.2|   |   |   |   |   |   |   |5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |7.0|   |   |
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |5.0|   |   |
     *             +---+---+---+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
    }

    /**
     * D.
     * <pre>
     *             +---+---+---+
     *             |   |   |   |
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|3.2|   |5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |7.0|   |1.0|
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |5.0|   |3.0|
     *             +---+---+---+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
    }

    /**
     * B.
     * <pre>
     *             +---+---+---+
     *             |4.0|   |2.0|
     *             +---     ---+
     *             |     1     |
     *             +---     ---+
     *             |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|   |   |   |   |   |   |   |2.2|2.1|   |4.2|
     * +---     ---+---     ---+---    +---+---     ---+
     * |     3     |     2     |     0     |     5     |
     * +---     ---+---     ---+---    +---+---     ---+
     * |5.2|   |   |   |   |   |   |   |3.1|3.2|   |5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |   |   |   |
     *             +---     ---+
     *             |     4     |
     *             +---     ---+
     *             |5.0|   |3.0|
     *             +---+---+---+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
    }

    /**
     * Returns an array of stickers which reflect the current state of the cube.
     * <p>
     * The following diagram shows the indices of the array. The number before
     * the comma is the first dimension (faces), the number after the comma
     * is the second dimension (stickers).
     * <p>
     * The values of the array elements is the face index: 0..5.
     * <pre>
     *         +---+---+
     *      ulb|1,0|1,1|ubr
     *         +--- ---+ 
     *      ufl|1,2|1,3|urf
     * +---+---+---+---+---+---+---+---+
     * |3,0|3,1|2,0|2,1|0,0|0,1|5,0|5,1|
     * +--- ---+--- ---+--- ---+--- ---+
     * |3,2|3,3|2,2|2,3|0,2|0,3|5,2|5,3|
     * +---+---+---+---+---+---+---+---+
     *      dlf|4,0|4,1|dfr
     *         +--- ---+
     *      dbl|4,2|4,3|drb
     *         +---+---+
     * </pre>
     * @return A two dimensional array. First dimension: faces.
     * Second dimension: sticker index on the faces.
     */
    public int[][] toStickers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setToStickers(int[][] stickers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPartSwipeAxis(int part, int orientation, int swipeDirection) {
        int loc = getCornerLocation(part);
        int ori = (3 - getPartOrientation(part) + orientation) % 3;
        return CORNER_SWIPE_TABLE[loc][ori][swipeDirection][0];
    }

    public int getPartSwipeLayerMask(int part, int orientation, int swipeDirection) {
        int loc = getCornerLocation(part);
        int ori = (3 - getPartOrientation(part) + orientation) % 3;
        int mask = CORNER_SWIPE_TABLE[loc][ori][swipeDirection][1];
        return mask == 4 ? 2 : mask;
    }

    public int getPartSwipeAngle(int part, int orientation, int swipeDirection) {
        int loc = getCornerLocation(part);
        int ori = getPartOrientation(part);
        int sori = (3 - ori + orientation) % 3;
        int dir = swipeDirection;
        int angle = CORNER_SWIPE_TABLE[loc][sori][dir][2];
        if (ori == 2 && (sori == 0 || sori == 2)) {
            angle = -angle;
        } else if (ori == 1 && (sori == 1 || sori == 2)) {
            angle = -angle;
        }

        return angle;
    }
}
