/*
 * @(#)RubiksCube.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube;

/**
 * Represents the state of a 3-times sliced cube (Rubik's Cube) by the location
 * and orientation of its parts.
 * <p>
 * A Rubik's Cube has 8 corner parts, 12 edge parts, 6 side parts and one
 * center part. The parts divide each face of the cube into 3 x 3 layers.
 * <p>
 * <b>Corner parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of
 * the corner parts:
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
 * |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|3.2|   |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 *             |7.0|   |1.0|
 *             +---     ---+
 *             |     4     |
 *             +---     ---+
 *             |5.0|   |3.0|
 *             +---+---+---+
 * </pre>
 * <p>
 * <b>Edge parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of
 * the edge parts:
 * <pre>
 *             +---+---+---+
 *             |   |3.1|   |
 *             +--- --- ---+
 *             |6.0| 1 |0.0|
 *             +--- --- ---+
 *             |   |9.1|   |
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 * |   |6.1|   |   |9.0|   |   |0.1|   |   |3.0|   |
 * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
 * |7.0| 3 10.0|10.1 2 |1.1|1.0| 0 |4.0|4.1| 5 |7.1|
 * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
 * |   |8.1|   |   |11.0   |   |2.1|   |   |5.0|   |
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 *             |   |11.1   |
 *             +--- --- ---+
 *             |8.0| 4 |2.0|
 *             +--- --- ---+
 *             |   |5.1|   |
 *             +---+---+---+
 * </pre>
 * <p>
 * <b>Side parts</b>
 * <p>
 * The following diagram shows the initial orientation and location of
 * the face parts:
 * <pre>
 *             +------------+
 *             |     .1     |
 *             |    ---     |
 *             | .0| 1 |.2  |
 *             |    ---     |
 *             |     .3     |
 * +-----------+------------+-----------+-----------+
 * |     .0    |     .2     |     .3    |    .1     |
 * |    ---    |    ---     |    ---    |    ---    |
 * | .3| 3 |.1 | .1| 2 |.3  | .2| 0 |.0 | .0| 5 |.2 |
 * |    ---    |    ---     |    ---    |    ---    |
 * |     .2    |    .0      |     .1    |     .3    |
 * +-----------+------------+-----------+-----------+
 *             |     .0     |
 *             |    ---     |
 *             | .3| 4 |.1  |
 *             |    ---     |
 *             |     .2     |
 *             +------------+
 * </pre>
 * <p>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 * @author Werner Randelshofer
 */
public class RubiksCube extends AbstractCube {

    /**
     * Holds the number of face parts, which is 6.
     */
    public final static int NUMBER_OF_SIDE_PARTS = 6;
    /**
     * Holds the number of edge parts, which is 12.
     */
    public final static int NUMBER_OF_EDGE_PARTS = 12;

    /**
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
     * <pre>
     *             +---+---+---+
     *             |   |3.1|   |
     *             +--- --- ---+
     *             |6.0| 1 |0.0|
     *             +--- --- ---+
     *             |   |9.1|   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |6.1|   |   |9.0|   |   |0.1|   |   |3.0|   |
     * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
     * |7.0| 3 10.0|10.1 2 |1.1|1.0| 0 |4.0|4.1| 5 |7.1|
     * +--- --- ---+--- --- ---+--- --- ---+--- --- ---+
     * |   |8.1|   |   |11.0   |   |2.1|   |   |5.0|   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+
     *             |   |11.1   |
     *             +--- --- ---+
     *             |8.0| 4 |2.0|
     *             +--- --- ---+
     *             |   |5.1|   |
     *             +---+---+---+
     * </pre>
     */
    private final static int[][][][] EDGE_SWIPE_TABLE = {
            { // edge 0 ur
                    {//u
                            {2, 2, 1}, // axis, layerMask, angle
                            {0, 4, -1},
                            {2, 2, -1},
                            {0, 4, 1}
                    },
                    {//r
                            {2, 2, -1}, // axis, layerMask, angle
                            {1, 4, -1},
                            {2, 2, 1},
                            {1, 4, 1}
                    },},
            { //      1 rf
                    {//r
                            {1, 2, 1}, // axis, layerMask, angle
                            {2, 4, -1},
                            {1, 2, -1},
                            {2, 4, 1}
                    },
                    {//f
                            {1, 2, -1}, // axis, layerMask, angle
                            {0, 4, -1},
                            {1, 2, 1},
                            {0, 4, 1}
                    },},
            { //      2 dr
                    {//d
                            {2, 2, -1}, // axis, layerMask, angle
                            {0, 4, -1},
                            {2, 2, 1},
                            {0, 4, 1}
                    },
                    {//r
                            {2, 2, 1}, // axis, layerMask, angle
                            {1, 1, 1},
                            {2, 2, -1},
                            {1, 1, -1}
                    },},
            { //      3 bu
                    {//b
                            {0, 2, -1}, // axis, layerMask, angle
                            {1, 4, -1},
                            {0, 2, 1},
                            {1, 4, 1}
                    },
                    {//u
                            {0, 2, 1}, // axis, layerMask, angle
                            {2, 1, 1},
                            {0, 2, -1},
                            {2, 1, -1}
                    },},
            { //      4 rb
                    {//r
                            {1, 2, -1}, // axis, layerMask, angle
                            {2, 1, 1},
                            {1, 2, 1},
                            {2, 1, -1}
                    },
                    {//b
                            {1, 2, 1}, // axis, layerMask, angle
                            {0, 4, -1},
                            {1, 2, -1},
                            {0, 4, 1}
                    },},
            { //      5 bd
                    {//b
                            {0, 2, 1}, // axis, layerMask, angle
                            {1, 1, 1},
                            {0, 2, -1},
                            {1, 1, -1}
                    },
                    {//d
                            {0, 2, -1}, // axis, layerMask, angle
                            {2, 1, 1},
                            {0, 2, 1},
                            {2, 1, -1}
                    },},
            { //      6 ul
                    {//u
                            {2, 2, -1}, // axis, layerMask, angle
                            {0, 1, 1},
                            {2, 2, 1},
                            {0, 1, -1}
                    },
                    {//l
                            {2, 2, 1}, // axis, layerMask, angle
                            {1, 4, -1},
                            {2, 2, -1},
                            {1, 4, 1}
                    },},
            { //      7 lb
                    {//l
                            {1, 2, 1}, // axis, layerMask, angle
                            {2, 1, 1},
                            {1, 2, -1},
                            {2, 1, -1}
                    },
                    {//b
                            {1, 2, -1}, // axis, layerMask, angle
                            {0, 1, 1},
                            {1, 2, 1},
                            {0, 1, -1}
                    },},
            { //      8 dl
                    {//d
                            {2, 2, 1}, // axis, layerMask, angle
                            {0, 1, 1},
                            {2, 2, -1},
                            {0, 1, -1}
                    },
                    {//l
                            {2, 2, -1}, // axis, layerMask, angle
                            {1, 1, 1},
                            {2, 2, 1},
                            {1, 1, -1}
                    },},
            { //      9 fu
                    {//f
                            {0, 2, 1}, // axis, layerMask, angle
                            {1, 4, -1},
                            {0, 2, -1},
                            {1, 4, 1}
                    },
                    {//u
                            {0, 2, -1}, // axis, layerMask, angle
                            {2, 4, -1},
                            {0, 2, 1},
                            {2, 4, 1}
                    },},
            { //     10 lf
                    {//l
                            {1, 2, -1}, // axis, layerMask, angle
                            {2, 4, -1},
                            {1, 2, 1},
                            {2, 4, 1}
                    },
                    {//f
                            {1, 2, 1}, // axis, layerMask, angle
                            {0, 1, 1},
                            {1, 2, -1},
                            {0, 1, -1}
                    },},
            { //     11 fd
                    {//f
                            {0, 2, -1}, // axis, layerMask, angle
                            {1, 1, 1},
                            {0, 2, 1},
                            {1, 1, -1}
                    },
                    {//d
                            {0, 2, 1}, // axis, layerMask, angle
                            {2, 4, -1},
                            {0, 2, -1},
                            {2, 4, 1}
                    },}
    };
    /* Swipe table.
     * First dimension: side part index.
     * Second dimension: swipe direction
     * Third dimension: axis,layermask,angle
     *
     * <pre>
     *             +------------+
     *             |     .1     |
     *             |    ---     |
     *             | .0| 1 |.2  |
     *             |    ---     |
     *             |     .3     |
     * +-----------+------------+-----------+-----------+
     * |     .0    |     .2     |     .3    |    .1     |
     * |    ---    |    ---     |    ---    |    ---    |
     * | .3| 3 |.1 | .1| 2 |.3  | .2| 0 |.0 | .0| 5 |.2 |
     * |    ---    |    ---     |    ---    |    ---    |
     * |     .2    |    .0      |     .1    |     .3    |
     * +-----------+------------+-----------+-----------+
     *             |     .0     |
     *             |    ---     |
     *             | .3| 4 |.1  |
     *             |    ---     |
     *             |     .2     |
     *             +------------+
     * </pre>
     */
    private final static int[][][] SIDE_SWIPE_TABLE = {
            {// 0 r
                    {1, 2, -1}, // axis, layerMask, angle
                    {2, 2, 1},
                    {1, 2, 1},
                    {2, 2, -1}
            },
            {// 1 u
                    {2, 2, -1},
                    {0, 2, 1},
                    {2, 2, 1},
                    {0, 2, -1}
            },
            {// 2 f
                    {0, 2, -1},
                    {1, 2, 1},
                    {0, 2, 1},
                    {1, 2, -1}
            },
            {// 3 l
                    {2, 2, 1},
                    {1, 2, -1},
                    {2, 2, -1},
                    {1, 2, 1}
            },
            {// 4 d
                    {0, 2, 1},
                    {2, 2, -1},
                    {0, 2, -1},
                    {2, 2, 1}
            },
            { // 5 b
                    {1, 2, 1},
                    {0, 2, -1},
                    {1, 2, -1},
                    {0, 2, 1}
            }
    };

    /**
     * Creates a new instance.
     */
    public RubiksCube() {
        super(3);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    @Override
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return (face < 3) ? 4 : 1;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            return 2;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return (face < 3) ? 4 : 1;
        } else {
            return 0;
        }

    }

    @Override
    public int getPartSwipeAxis(int part, int orientation, int swipeDirection) {
        if (part < cornerLoc.length) {
            int loc = getCornerLocation(part);
            int ori = (3 - getPartOrientation(part) + orientation) % 3;
            return CORNER_SWIPE_TABLE[loc][ori][swipeDirection][0];
        } else if (part < cornerLoc.length + edgeLoc.length) {
            // Edge parts
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            return EDGE_SWIPE_TABLE[loc][ori][swipeDirection][0];
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc][ori][0];
        } else {
            return -1;
        }

    }

    @Override
    public int getPartSwipeLayerMask(int part, int orientation, int swipeDirection) {
        if (part < cornerLoc.length) {
            int loc = getCornerLocation(part);
            int ori = (3 - getPartOrientation(part) + orientation) % 3;
            return CORNER_SWIPE_TABLE[loc][ori][swipeDirection][1];
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            return EDGE_SWIPE_TABLE[loc][ori][swipeDirection][1];
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc][ori][1];
        } else {
            return 0;
        }

    }

    @Override
    public int getPartSwipeAngle(int part, int orientation, int swipeDirection) {
        if (part < cornerLoc.length) {
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
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = getEdgeOrientation(edgeIndex);
            int sori = (2 - ori + orientation) % 2;
            int dir = swipeDirection;
            int angle = EDGE_SWIPE_TABLE[loc][sori][dir][2];
            return angle;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc][ori][2];
        } else {
            return 0;
        }

    }

    /**
     * Transforms the cube and fires a cubeTwisted event.
     *
     * @param axis      0=x, 1=y, 2=z axis.
     * @param layerMask A bitmask specifying the layers to be transformed.
     *                  The size of the layer mask depends on the value returned by
     *                  <code>getLayerCount(axis)</code>. The layer mask has the
     *                  following meaning:
     *                  7=rotate the whole cube;<br>
     *                  1=twist slice near the axis (left, down, back)<br>
     *                  2=twist slice in the middle of the axis<br>
     *                  4=twist slice far away from the axis (right, top, up)
     * @param angle     positive values=clockwise rotation<br>
     *                  negative values=counterclockwise rotation<br>
     *                  1=90 degrees<br>
     *                  2=180 degrees
     */
    @Override
    protected void transform0(int axis, int layerMask, int angle) {
        if (angle == 0) {
            return; // NOP
        }

        synchronized (this) {

            // Convert angle -2 to 2 to simplify the switch statements
            int an = (angle == -2) ? 2 : angle;

            if ((layerMask & 1) != 0) {
                // twist at left, down, back
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
                // twist at left middle, down middle, back middle
                switch (axis) {
                case 0: // x
                    switch (an) {
                    case 1:
                        twistMR();
                        break;
                    case -1:
                        twistMR();
                        twistMR();
                        twistMR();
                        break;
                    case 2:
                        twistMR();
                        twistMR();
                        break;
                    }
                    break;
                case 1: // y
                    switch (an) {
                    case 1:
                        twistMU();
                        break;
                    case -1:
                        twistMU();
                        twistMU();
                        twistMU();
                        break;
                    case 2:
                        twistMU();
                        twistMU();
                        break;
                    }
                    break;
                case 2: // z
                    switch (an) {
                    case 1:
                        twistMF();
                        break;
                    case -1:
                        twistMF();
                        twistMF();
                        twistMF();
                        break;
                    case 2:
                        twistMF();
                        twistMF();
                        break;
                    }
                }
            }

            if ((layerMask & 4) != 0) {
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
    }

    /**
     * R.
     * <pre>
     *                +----+----+----+
     *                |    |    | 2.0|
     *                +---- ---- ----+
     *                |    |    | 0.0|
     *                +---- ---- ----+
     *                |    |    | 0.0|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    |    |    |    |    | 0.2| 0.1| 0.1| 2.2| 2.1|    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    | 1.1| 1.0| 0.0| 4.0| 4.1|    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    | 1.1| 1.2| 2.1| 3.1| 3.2|    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |    | 1.0|
     *                +---- ---- ----+
     *                |    |    | 2.0|
     *                +---- ---- ----+
     *                |    |    | 3.0|
     *                +----+----+----+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 0, 1, 2, 4, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[0] = (sideOrient[0] + 3) % 4;
    }

    /**
     * U.
     * <pre>
     *                +----+----+----+
     *                | 4.0| 3.1| 2.0|
     *                +---- ---- ----+
     *                | 6.0| 1.2| 0.0|
     *                +---- ---- ----+
     *                | 6.0| 9.1| 0.0|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * | 4.1| 6.1| 6.2| 6.1| 9.0| 0.2| 0.1| 0.1| 2.2| 2.1| 3.0| 4.2|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +----+----+----+
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 0, 3, 6, 9, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[1] = (sideOrient[1] + 3) % 4;
    }

    /**
     * F.
     * <pre>
     *                +----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                | 6.0| 9.1| 0.0|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    |    | 6.2| 6.1| 9.0| 0.2| 0.1|    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |10.0|10.1| 2.3| 1.1| 1.0|    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    | 7.1| 7.2|11.0| 1.1| 1.2|    |    |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                | 7.0|11.1| 1.0|
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +----+----+----+
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 9, 10, 11, 1, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[2] = (sideOrient[2] + 3) % 4;
    }

    /**
     * L.
     * <pre>
     *                +----+----+----+
     *                | 4.0|    |    |
     *                +---- ---- ----+
     *                | 6.0|    |    |
     *                +---- ---- ----+
     *                | 6.0|    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * | 4.1| 6.1| 6.2| 6.1|    |    |    |    |    |    |    | 4.2|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 7.0| 3.1|10.0|10.1|    |    |    |    |    |    |    | 7.1|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 5.2| 8.1| 7.1| 7.2|    |    |    |    |    |    |    | 5.1|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                | 7.0|    |    |
     *                +---- ---- ----+
     *                | 8.0|    |    |
     *                +---- ---- ----+
     *                | 5.0|    |    |
     *                +----+----+----+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
        fourCycle(edgeLoc, 6, 7, 8, 10, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[3] = (sideOrient[3] + 3) % 4;
    }

    /**
     * D.
     * <pre>
     *                +----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 5.2| 8.1| 7.1| 7.2|11.0| 1.1| 1.2| 2.1| 3.1| 3.2| 5.0| 5.1|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                | 7.0|11.1| 1.0|
     *                +---- ---- ----+
     *                | 8.0| 4.1| 2.0|
     *                +---- ---- ----+
     *                | 5.0| 5.1| 3.0|
     *                +----+----+----+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 2, 11, 8, 5, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[4] = (sideOrient[4] + 3) % 4;
    }

    /**
     * B.
     * <pre>
     *                +----+----+----+
     *                | 4.0| 3.1| 2.0|
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * | 4.1|    |    |    |    |    |    |    | 2.2| 2.1| 3.0| 4.2|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 7.0|    |    |    |    |    |    |    | 4.0| 4.1| 5.2| 7.1|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 5.2|    |    |    |    |    |    |    | 3.1| 3.2| 5.0| 5.1|
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                | 5.0| 5.1| 3.0|
     *                +----+----+----+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 3, 4, 5, 7, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[5] = (sideOrient[5] + 3) % 4;
    }

    /**
     * MR.
     * <pre>
     *                +----+----+----+
     *                |    | 3.1|    |
     *                +---- ---- ----+
     *                |    | 1.2|    |
     *                +---- ---- ----+
     *                |    | 9.1|    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    |    |    |    | 9.0|    |    |    |    |    | 3.0|    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    | 2.3|    |    |    |    |    | 5.2|    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |11.0|    |    |    |    |    | 5.0|    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |11.1|    |
     *                +---- ---- ----+
     *                |    | 4.1|    |
     *                +---- ---- ----+
     *                |    | 5.1|    |
     *                +----+----+----+
     * </pre>
     */
    private void twistMR() {
        fourCycle(edgeLoc, 3, 9, 11, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 4, 5, 1, sideOrient, 2, 3, 2, 1, 4);
    }

    /**
     * MU.
     * <pre>
     *                +----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * | 7.0| 3.1|10.0|10.1| 2.3| 1.1| 1.0| 0.0| 5.0| 4.1| 5.2| 7.1|
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    |    |    |    |    |    |    |    |    |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                |    |    |    |
     *                +----+----+----+
     * </pre>
     */
    private void twistMU() {
        fourCycle(edgeLoc, 1, 4, 7, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 2, 0, 5, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * MF.
     * <pre>
     *                +----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                | 6.0| 1.2| 0.0|
     *                +---- ---- ----+
     *                |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     * |    | 6.1|    |    |    |    |    | 0.1|    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    | 3.1|    |    |    |    |    | 0.0|    |    |    |    |
     * +---- ---- ----+---- ---- ----+---- ---- ----+---- ---- ----+
     * |    | 8.1|    |    |    |    |    | 2.1|    |    |    |    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+
     *                |    |    |    |
     *                +---- ---- ----+
     *                | 8.0| 4.1| 2.0|
     *                +---- ---- ----+
     *                |    |    |    |
     *                +----+----+----+
     * </pre>
     */
    private void twistMF() {
        fourCycle(edgeLoc, 0, 6, 8, 2, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 1, 3, 4, sideOrient, 1, 2, 3, 2, 4);
    }

}
