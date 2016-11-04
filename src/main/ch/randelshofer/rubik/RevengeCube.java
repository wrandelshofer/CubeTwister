/*
 * @(#)RevengeCube.java  3.0.1  2009-01-25
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

/**
 * Represents the state of a 4-times sliced cube (Revenge Cube) by the location 
 * and orientation of its parts.
 * <p>
 * A Revenge Cube has 8 corner parts, 24 edge parts, 24 side parts and one
 * center part. The parts divide each face of the cube into 4 x 4 layers.
 * <p>
 * <b>Corner parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the corner parts:
 * <pre>
 *                 +---+---+---+---+
 *                 |4.0|       |2.0|
 *                 +---+       +---+
 *                 |               |
 *                 +       u       +
 *                 |               |
 *                 +---+       +---+
 *                 |6.0|       |0.0|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |4.1|       |6.2|6.1|       |0.2|0.1|       |2.2|2.1|       |4.2|
 * +---+       +---+---+       +---+---+       +---+---+       +---+
 * |               |               |               |               |
 * +       l       +       f       +       r       +       b       +
 * |               |               |               |               |
 * +---+       +---+---+       +---+---+       +---+---+       +---+
 * |5.2|       |7.1|7.2|       |1.1|1.2|       |3.1|3.2|       |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                 |7.0|       |1.0|
 *                 +---+       +---+
 *                 |               |
 *                 +       d       +
 *                 |               |
 *                 +---+       +---+
 *                 |5.0|       |3.0|
 *                 +---+---+---+---+
 * </pre>
 * <p>
 * <b>Edge parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the edge parts. The first 12 edges are located near the origins of the
 * x-, y- and z-axis. The second 12 edges are located far from the origin
 * of the x-, y- and z-axis.
 * <pre>
 *                         X---&gt;
 *                   +---+---+---+---+
 *                   |   |3.1|15 |   |
 *                 Z +---+---+---+---+ Z
 *                 | |6.0|       |0.0| |
 *                 v +---+   u   +---+ v
 *                   |18 |       |12 |
 *                   +---+---+---+---+
 *         Z---&gt;     |   |9.1|21 |   |      &lt;---Z           &lt;---X
 *   +---+---+---+---+---+---*---+---+---+---+---+---+---+---+---+---+
 *   |   |6.1|18 |   |   |9.0|21 |   |   |12 |0.1|   |   |15 |3.0|   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ^ |19 |       |22 |22 |       |13 |13 |       |16 |16 |       |19 | ^
 * | +---+   l   +---+---+   f   +---+---+   r   +---+---+   b   +---+ |
 * Y |7.0|       10.0|10.1       |1.1|1.0|       |4.0|4.1|       |7.1| Y
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |8.1|20 |   |   11.0|23 |   |   |14 |2.1|   |   |17 |5.0|   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---*---+---+
 *         Z---&gt;     |   11.1|23 |   |      &lt;---Z            &lt;---X
 *                   +---+---+---+---+
 *                   |20 |       |14 |
 *                   +---+   d   +---+ ^
 *                   |8.0|       |2.0| |
 *                   +---+---+---+---+ Z
 *                   |   |5.1|17 |   |
 *                   +---+---+---+---+
 *                       X---&gt;
 * </pre>
 * <p>
 * <b>Side parts</b>
 * <p>
 * The following diagram shows the initial orientation and location of 
 * the face parts:
 * <pre>
 *                 +---+---+---+---+
 *                 |      .1       |
 *                 +   +---+---+   +
 *                 |   | 1 | 7 |   |
 *                 + .0+---+---+.2 +
 *                 |   |19 |13 |   |
 *                 +   +---+---+   +
 *                 |      .3       |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |      .0       |      .2       |      .3       |      .1       |
 * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
 * |   |21 | 3 |   |   | 8 |14 |   |   |12 |18 |   |   | 5 |11 |   |
 * + .3+---+---+.1 + .1+---+---+.3 + .2+---+---+.0 + .0+---+---+.2 +
 * |   |15 | 9 |   |   | 2 |20 |   |   | 6 | 0 |   |   |23 |17 |   |
 * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
 * |      .2       |      .0       |      .1       |      .3       |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                 |      .0       |
 *                 +   +---+---+   +
 *                 |   |22 | 4 |   |
 *                 + .3+---+---+.1 +
 *                 |   |16 |10 |   |
 *                 +   +---+---+   +
 *                 |      .2       |
 *                 +---+---+---+---+
 * </pre>
 * <p>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>3.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>2.0 2007-12-31 Adapted to changes in AbstractCube.
 * <br>1.0  14 February 2005  Created.
 */
public class RevengeCube extends AbstractCube {

    /**
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
     * <pre>
     *                         X---&gt;
     *                   +---+---+---+---+
     *                   |   |3.1|15 |   |
     *                 Z +---+---+---+---+ Z
     *                 | |6.0|       |0.0| |
     *                 v +---+   u   +---+ v
     *                   |18 |       |12 |
     *                   +---+---+---+---+
     *         Z---&gt;     |   |9.1|21 |   |      &lt;---Z           &lt;---X
     *   +---+---+---+---+---+---*---+---+---+---+---+---+---+---+---+---+
     *   |   |6.1|18 |   |   |9.0|21 |   |   |12 |0.1|   |   |15 |3.0|   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * ^ |19 |       |22 |22 |       |13 |13 |       |16 |16 |       |19 | ^
     * | +---+   l   +---+---+   f   +---+---+   r   +---+---+   b   +---+ |
     * Y |7.0|       10.0|10.1       |1.1|1.0|       |4.0|4.1|       |7.1| Y
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |8.1|20 |   |   11.0|23 |   |   |14 |2.1|   |   |17 |5.0|   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---*---+---+
     *         Z---&gt;     |   11.1|23 |   |      &lt;---Z            &lt;---X
     *                   +---+---+---+---+
     *                   |20 |       |14 |
     *                   +---+   d   +---+ ^
     *                   |8.0|       |2.0| |
     *                   +---+---+---+---+ Z
     *                   |   |5.1|17 |   |
     *                   +---+---+---+---+
     *                       X---&gt;
     * </pre>
     */
    private final static int[][][][] EDGE_SWIPE_TABLE = {
        { // edge 0 ur
            {//u
                {2, 2, 1}, // axis, layerMask, angle
                {0, 8, -1},
                {2, 2, -1},
                {0, 8, 1}
            },
            {//r
                {2, 2, -1}, // axis, layerMask, angle
                {1, 8, -1},
                {2, 2, 1},
                {1, 8, 1}
            },},
        { //      1 rf
            {//r
                {1, 2, 1}, // axis, layerMask, angle
                {2, 8, -1},
                {1, 2, -1},
                {2, 8, 1}
            },
            {//f
                {1, 2, -1}, // axis, layerMask, angle
                {0, 8, -1},
                {1, 2, 1},
                {0, 8, 1}
            },},
        { //      2 dr
            {//d
                {2, 2, -1}, // axis, layerMask, angle
                {0, 8, -1},
                {2, 2, 1},
                {0, 8, 1}
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
                {1, 8, -1},
                {0, 2, 1},
                {1, 8, 1}
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
                {0, 8, -1},
                {1, 2, -1},
                {0, 8, 1}
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
                {1, 8, -1},
                {2, 2, -1},
                {1, 8, 1}
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
                {1, 8, -1},
                {0, 2, -1},
                {1, 8, 1}
            },
            {//u
                {0, 2, -1}, // axis, layerMask, angle
                {2, 8, -1},
                {0, 2, 1},
                {2, 8, 1}
            },},
        { //     10 lf
            {//l
                {1, 2, -1}, // axis, layerMask, angle
                {2, 8, -1},
                {1, 2, 1},
                {2, 8, 1}
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
                {2, 8, -1},
                {0, 2, -1},
                {2, 8, 1}
            },}
    };
    /* Swipe table.
     * First dimension: side part index.
     * Second dimension: swipe direction
     * Third dimension: axis,layermask,angle
     *
     * <pre>
     *                 +---+---+---+---+
     *                 |      .1       |
     *                 +   +---+---+   +
     *                 |   | 1 | 7 |   |
     *                 + .0+---+---+.2 +
     *                 |   |19 |13 |   |
     *                 +   +---+---+   +
     *                 |      .3       |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |      .0       |      .2       |      .3       |      .1       |
     * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
     * |   |21 | 3 |   |   | 8 |14 |   |   |12 |18 |   |   | 5 |11 |   |
     * + .3+---+---+.1 + .1+---+---+.3 + .2+---+---+.0 + .0+---+---+.2 +
     * |   |15 | 9 |   |   | 2 |20 |   |   | 6 | 0 |   |   |23 |17 |   |
     * +   +---+---+   +   +---+---+   +   +---+---+   +   +---+---+   +
     * |      .2       |      .0       |      .1       |      .3       |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |      .0       |
     *                 +   +---+---+   +
     *                 |   |22 | 4 |   |
     *                 + .3+---+---+.1 +
     *                 |   |16 |10 |   |
     *                 +   +---+---+   +
     *                 |      .2       |
     *                 +---+---+---+---+
     * </pre>
     */
    private final static int[][][] SIDE_SWIPE_TABLE = {
        {// 0 r (+6)
            {1, 2, -1}, // axis, layerMask, angle
            {2, 4, 1},
            {1, 2, 1},
            {2, 4, -1}
        },
        {// 1 u (+6)

            {2, 2, -1},
            {0, 4, 1},
            {2, 2, 1},
            {0, 4, -1}
        },
        {// 2 f (+6)
            {0, 2, -1},
            {1, 4, 1},
            {0, 2, 1},
            {1, 4, -1}
        },
        {// 3 l (+6)
            {2, 4, 1},
            {1, 2, -1},
            {2, 4, -1},
            {1, 2, 1}
        },
        {// 4 d (+6)
            {0, 4, 1},
            {2, 2, -1},
            {0, 4, -1},
            {2, 2, 1}
        },
        { // 5 b (+6)
            {1, 4, 1},
            {0, 2, -1},
            {1, 4, -1},
            {0, 2, 1}
        }
    };

    /** Creates a new instance. */
    public RevengeCube() {
        super(4);
    }

    /**
     * Transforms the cube and fires a cubeTwisted event.
     *
     * @param  axis  0=x, 1=y, 2=z axis.
     * @param  layerMask A bitmask specifying the layers to be transformed.
     *           The size of the layer mask depends on the value returned by
     *           <code>getLayerCount(axis)</code>. For a 3x3x3 cube, the layer mask has the
     *           following meaning:
     *           15=rotate the whole cube;<br>
     *           1=twist slice near the axis (left, down, back)<br>
     *           2=twist slice in the near middle of the axis<br>
     *           4=twist slice in the far middle of the axis<br>
     *           8=twist slice far away from the axis (right, up, front)
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
                // twist at left middle, bottom middle, back middle
                switch (axis) {
                    case 0: // x
                        switch (an) {
                            case -1:
                                twistML();
                                break;
                            case 1:
                                twistML();
                                twistML();
                                twistML();
                                break;
                            case 2:
                                twistML();
                                twistML();
                                break;
                        }
                        break;
                    case 1: // y
                        switch (an) {
                            case -1:
                                twistMD();
                                break;
                            case 1:
                                twistMD();
                                twistMD();
                                twistMD();
                                break;
                            case 2:
                                twistMD();
                                twistMD();
                                break;
                        }
                        break;
                    case 2: // z
                        switch (an) {
                            case -1:
                                twistMB();
                                break;
                            case 1:
                                twistMB();
                                twistMB();
                                twistMB();
                                break;
                            case 2:
                                twistMB();
                                twistMB();
                                break;
                        }
                }
            }
            if ((layerMask & 4) != 0) {
                // twist at left middle, bottom middle, back middle
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
            if ((layerMask & 8) != 0) {
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
     *                 +---+---+---+---+
     *                 |   |   |   |2.0|
     *                 +--- --- --- ---+
     *                 |   |       |0.0|
     *                 +---         ---+
     *                 |   |       |12 |
     *                 +--- --- --- ---+
     *                 |   |   |   |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |0.2|0.1|12 |0.1|2.2|2.1|   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   |       |13 |13 |12  18 |16 |16 |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |       |1.1|1.0| 6   0 |4.0|4.1|       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   |   |   |1.1|1.2|14 |2.1|3.1|3.2|   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |1.0|
     *                 +--- --- --- ---+
     *                 |   |       |14 |
     *                 +---         ---+
     *                 |   |       |2.0|
     *                 +--- --- --- ---+
     *                 |   |   |   |3.0|
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 12, 1, 2, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 0, 13, 14, 4, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 18, 12, 6, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * U.
     * <pre>
     *                 +---+---+---+---+
     *                 |4.0|3.1|15 |2.0|
     *                 +--- --- --- ---+
     *                 |6.0| 1   7 |0.0|
     *                 +---         ---+
     *                 |18 |19  13 |12 |
     *                 +--- --- --- ---+
     *                 |6.0|9.1|21 |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|6.1|18 |6.2|6.1|9.0|21 |0.2|0.1|12 |0.1|2.2|2.2|15 |3.0|4.2|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 0, 3, 18, 21, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 12, 15, 6, 9, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 1, 19, 13, 7, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * F.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |6.0|9.1|21 |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |6.2|6.1|9.0|21 |0.2|0.1|   |   |   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |22 |22 | 8  14 |13 |13 |       |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       10.0|10.1 2  20 |1.1|1.0|       |   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |7.1| 7 11.0|23 |1.1|1.2|   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |7.0|11 |23 |1.0|
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 21, 22, 11, 1, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 9, 10, 23, 13, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 20, 14, 8, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * L.
     * <pre>
     *                 +---+---+---+---+
     *                 |4.0|   |   |   |
     *                 +--- --- --- ---+
     *                 |6.0|       |   |
     *                 +---         ---+
     *                 |18 |       |   |
     *                 +--- --- --- ---+
     *                 |6.0|   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|6.1|18 |6.2|6.1|   |   |   |   |   |   |   |   |   |   |4.2|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |19 |21   3 |22 |22 |       |   |   |       |   |   |       |19 |
     * +---         ---+---         ---+---         ---+---         ---+
     * |7.0|15   9 10.0|10.1       |   |   |       |   |   |       |7.1|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |5.2|8.1|20 |7.1|7.2|   |   |   |   |   |   |   |   |   |   |5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |7.0|   |   |   |
     *                 +--- --- --- ---+
     *                 |20 |       |   |
     *                 +---         ---+
     *                 |8.0|       |   |
     *                 +--- --- --- ---+
     *                 |5.0|   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
        fourCycle(edgeLoc, 6, 7, 20, 22, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 18, 19, 8, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 21, 15, 9, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * D.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |5.2|8.1|20 |7.1| 7 11.0|23 |1.1|1.2|14 |2.1|3.1|3.2|17 |5.0|5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |7.0|11 |23 |1.0|
     *                 +--- --- --- ---+
     *                 |20 |22   4 |14 |
     *                 +---         ---+
     *                 |8.0|16  10 |2.0|
     *                 +--- --- --- ---+
     *                 |5.0|5.1|17 |3.0|
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 2, 23, 20, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 14, 11, 8, 17, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 4, 22, 16, 10, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * B.
     * <pre>
     *                 +---+---+---+---+
     *                 |4.0|3.1|15 |2.0|
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |4.1|   |   |   |   |   |   |   |   |   |   |2.2|2.2|15 |3.0|4.2|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |19 |       |   |   |       |   |   |       |16 |16 | 5  11 |19 |
     * +---         ---+---         ---+---         ---+---         ---+
     * |7.0|       |   |   |       |   |   |       |4.0|4.1|23  17 |7.1|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |5.2|   |   |   |   |   |   |   |   |   |   |3.1|3.2|17 |5.0|5.1|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |5.0|5.1| 17|3.0|
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 3, 16, 17, 7, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 15, 4, 5, 19, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 5, 23, 17, 11, sideOrient, 3, 3, 3, 3, 4);
    }

    /**
     * MR.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |15 |   |
     *                 +--- --- --- ---+
     *                 |   |    7.2|   |
     *                 +---         ---+
     *                 |   |    13 |   |
     *                 +--- --- --- ---+
     *                 |   |   |21 |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |21 |   |   |   |   |   |   |15 |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   |   14.3|   |   |       |   |   |5.2    |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |    20 |   |   |       |   |   |23     |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   |   |23 |   |   |   |   |   |   |17 |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |23 |   |
     *                 +--- --- --- ---+
     *                 |   |    4.1|   |
     *                 +---         ---+
     *                 |   |    10 |   |
     *                 +--- --- --- ---+
     *                 |   |   |17 |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistMR() {
        fourCycle(edgeLoc, 15, 21, 23, 17, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 7, 14, 4, 23, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 13, 20, 10, 5, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * MU.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |19 |21  3.1|22 |22 |8.3 14 |13 |13 12.0 18 |16 |16 |5.2 11 |19 |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistMU() {
        fourCycle(edgeLoc, 19, 22, 13, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 14, 18, 11, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 21, 8, 12, 5, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * MF.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |18 |19  13 |12 |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |18 |   |   |   |   |   |   |12 |   |   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |     3 |   |   |       |   |   |12     |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |     9 |   |   |       |   |   | 6     |   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |20 |   |   |   |   |   |   |14 |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |20 |22   4 |14 |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistMF() {
        fourCycle(edgeLoc, 12, 18, 20, 14, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 6, 13, 3, 22, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 12, 19, 9, 4, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * ML.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |3.1|   |   |
     *                 +--- --- --- ---+
     *                 |   |1.2    |   |
     *                 +---         ---+
     *                 |   |19     |   |
     *                 +--- --- --- ---+
     *                 |   |9.1|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |9.0|   |   |   |   |   |   |   |   |3.0|   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   | 8     |   |   |       |   |   |   11.2|   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |       |   |   |2.3    |   |   |       |   |   |    17 |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   11.0|   |   |   |   |   |   |   |   |5.0|   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |11 |   |   |
     *                 +--- --- --- ---+
     *                 |   |22     |   |
     *                 +---         ---+
     *                 |   |16.1   |   |
     *                 +--- --- --- ---+
     *                 |   |5.1|   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistML() {
        fourCycle(edgeLoc, 5, 11, 9, 3, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 19, 11, 16, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 8, 1, 17, 22, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * MD.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |       |   |   |       |   |   |       |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |7.0|15   9 10.0|10.1 2  20 |1.1|1.0| 6   0 |4.0|4.1|23  17 |7.1|
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistMD() {
        fourCycle(edgeLoc, 1, 10, 7, 4, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 20, 9, 17, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 6, 2, 15, 23, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * MB.
     * <pre>
     *                 +---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |6.0|1.2  7 |0.0|
     *                 +---         ---+
     *                 |   |       |   |
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |6.1|   |   |   |   |   |   |   |   |0.1|   |   |   |   |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |21     |   |   |       |   |   |    18 |   |   |       |   |
     * +---         ---+---         ---+---         ---+---         ---+
     * |   |15.1   |   |   |       |   |   |    0.0|   |   |       |   |
     * +--- --- --- ---+--- --- --- ---+--- --- --- ---+--- --- --- ---+
     * |   |8.1|   |   |   |   |   |   |   |   |2.1|   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                 |   |   |   |   |
     *                 +--- --- --- ---+
     *                 |   |       |   |
     *                 +---         ---+
     *                 |8.0|16 10.1|2.0|
     *                 +--- --- --- ---+
     *                 |   |   |   |   |
     *                 +---+---+---+---+
     * </pre>
     */
    private void twistMB() {
        fourCycle(edgeLoc, 0, 2, 8, 6, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 16, 21, 7, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 18, 10, 15, 1, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return (face < 3) ? 8 : 1;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int loc = getEdgeLocation(part - cornerLoc.length);
            return 2 << (loc / 12);
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return (face < 3) ? 8 : 1;
        } else {
            return 0;
        }
    }

    public int[][] toStickers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setToStickers(int[][] stickers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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
            return EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][0];
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc % 6][ori][0];
        } else {
            return -1;
        }

    }

    public int getPartSwipeLayerMask(int part, int orientation, int swipeDirection) {
        if (part < cornerLoc.length) {
            int loc = getCornerLocation(part);
            int ori = (3 - getPartOrientation(part) + orientation) % 3;
            int mask = CORNER_SWIPE_TABLE[loc][ori][swipeDirection][1];
            return mask == 4 ? 8 : mask;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            int mask= EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][1];
            switch (loc / 12) {
                case 0:
                    break;
                case 1:
                    if (mask == 2) { mask = 4; }
                    break;
            }
            return mask;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            int mask = SIDE_SWIPE_TABLE[loc % 6][ori][1];
            switch (loc / 6) {
                case 0:
                    mask = (loc % 6) < 3 ? 2 : 4;
                    break;
                case 1:
                    break;
                case 2:
                    mask = (loc % 6) < 3 ? 4 : 2;
                    break;
                case 3:
                    mask = mask == 2 ? 4 : 2;
                    break;
            }
            return mask;
        } else {
            return 0;
        }

    }

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
            int angle = EDGE_SWIPE_TABLE[loc % 12][sori][dir][2];
            return angle;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc % 6][ori][2];
        } else {
            return 0;
        }

    }
}
