/* @(#)ProfessorCube.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import org.jhotdraw.annotation.Nonnull;

/**
 * Represents the state of a 5-times sliced cube (Professor Cube) by the 
 * location and orientation of its parts.
 * <p>
 * A Professor Cube has 8 corner parts, 36 edge parts, 54 side parts and one
 * center part. The parts divide each face of the cube into 5 x 5 layers.
 * <p>
 * <b>Corner parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the corner parts:
 * <pre>
 *                     +---+---+---+---+---+
 *                     |4.0|           |2.0|
 *                     +---+           +---+
 *                     |                   |
 *                     +                   +
 *                     |         u         |
 *                     +                   +
 *                     |                   |
 *                     +---+           +---+
 *                     |6.0|           |0.0|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |4.1|           |6.2|6.1|           |0.2|0.1|           |2.2|2.1|           |4.2|
 * +---+           +---+---+           +---+---+           +---+---+           +---+
 * |                   |                   |                   |                   |
 * +                   +                   +                   +                   +
 * |         l         |         f         |         r         |     b             |
 * +                   +                   +                   +                   +
 * |                   |                   |                   |                   |
 * +---+           +---+---+           +---+---+           +---+---+           +---+
 * |5.2|           |7.1|7.2|           |1.1|1.2|           |3.1|3.2|           |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                     |7.0|           |1.0|
 *                     +---+           +---+
 *                     |                   |
 *                     +                   +
 *                     |         d         |
 *                     +                   +
 *                     |                   |
 *                     +---+           +---+
 *                     |5.0|           |3.0|
 *                     +---+---+---+---+---+
 * </pre>
 * <p>
 * <b>Edge parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the edge parts. The first 12 edges are located at the center of the x-, y-,
 * and z-axis. The second 12 edges are located near the origins of the
 * x-, y- and z-axis. The last 12 edges are located far from the origin
 * of the x-, y- and z-axis.
 * <pre>
 *                             X---&gt;
 *                       +---+---+---+---+---+
 *                       |   |15 |3.1| 27|   |
 *                       +---+---+---+---+---+
 *                       |18 |           |12 |
 *                     Z +---+           +---+ Z
 *                     | |6.0|     u     |0.0| |
 *                     V +---+           +---+ V
 *                       |30 |           |24 |
 *                       +---+---+---+---+---+
 *           Z---&gt;       |   |21 |9.1|33 |   |       &lt;---Z               &lt;---X
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |18 |6.1|30 |   |   |21 |9.0|33 |   |   |24 |0.1|12 |   |   |27 |3.0|15 |   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |31 |           |34 |34 |           |25 |25 |           |28 |28 |           |31 |
 * ^ +---+           +---+---+           +---+---+           +---+---+           +---+ ^
 * | |7.0|    l      10.0|10.1     f     |1.1|1.0|     r     |4.0|4.1|     b     |7.1| |
 * Y +---+           +---+---+           +---+---+           +---+---+           +---+ Y
 *   |19 |           |22 |22 |           |13 |13 |           |16 |16 |           |19 |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |20 |8.1|32 |   |   |23 11.0|35 |   |   |26 |2.1|14 |   |   |29 |5.0| 17|   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                       |   |23 11.1|35 |   |       &lt;---Z               &lt;---X
 *                       +---+---+---+---+---+
 *                       |32 |           |26 |
 *                       +---+           +---+ ^
 *                       |8.0|     d     |2.0| |
 *                       +---+           +---+ Z
 *                       |20 |           |14 |
 *                       +---+---+---+---+---+
 *                       |   |17 |5.1|29 |   |
 *                       +---+---+---+---+---+
 *                                X--&gt;
 * </pre>
 * <p>
 * <b>Side parts</b>
 * <p>
 * The following diagram shows the initial orientation and location of 
 * the side parts:
 * <pre>
 *                     +---+---+---+---+---+
 *                     |        .1         |
 *                     +   +---+---+---+   +
 *                     |   | 7 |37 |13 |   |
 *                     +   +---+---+---+   +
 *                     | .0|31 | 1 |43 |.2 |
 *                     +   +---+---+---+   +
 *                     |   |25 |49 |19 |   |
 *                     +   +---+---+---+   +
 *                     |        .3         |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |        .0         |        .2         |        .3         |        .1         |
 * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
 * |   |27 |33 | 9 |   |   |14 |44 |20 |   |   |18 |48 |24 |   |   |11 |41 |17 |   |
 * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
 * | .3|51 | 3 |39 |.1 | .1|38 | 2 |50 |.3 | .2|42 | 0 |30 |.0 | .0|35 | 5 |47 |.2 |
 * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
 * |   |21 |45 |15 |   |   | 8 |32 |26 |   |   |12 |36 | 6 |   |   |29 |53 |23 |   |
 * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
 * |        .2         |        .0         |        .1         |        .3         |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                     |        .0         |
 *                     +   +---+---+---+   +
 *                     |   |28 |34 |10 |   |
 *                     +   +---+---+---+   +
 *                     | .3|52 | 4 |40 |.1 |
 *                     +   +---+---+---+   +
 *                     |   |22 |46 |16 |   |
 *                     +   +---+---+---+   +
 *                     |        .2         |
 *                     +---+---+---+---+---+
 * </pre>
 * <p>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 * @author  Werner Randelshofer
 */
public class ProfessorCube extends AbstractCube {

    /**
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
     * <p>
     * The layermask value applies to the first part on an edge of a 
     * RevengeCube. To get to the layermask value for the first edge part of a
     * ProfessorCube, the value 2 has be replaced by 4 and the value 8 by 16.
     * <pre>
     *                             X---&gt;
     *                       +---+---+---+---+---+
     *                       |   |15 |3.1| 27|   |
     *                       +---+---+---+---+---+
     *                       |18 |           |12 |
     *                     Z +---+           +---+ Z
     *                     | |6.0|     u     |0.0| |
     *                     V +---+           +---+ V
     *                       |30 |           |24 |
     *                       +---+---+---+---+---+
     *           Z---&gt;       |   |21 |9.1|33 |   |       &lt;---Z               &lt;---X
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |18 |6.1|30 |   |   |21 |9.0|33 |   |   |24 |0.1|12 |   |   |27 |3.0|15 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |31 |           |34 |34 |           |25 |25 |           |28 |28 |           |31 |
     * ^ +---+           +---+---+           +---+---+           +---+---+           +---+ ^
     * | |7.0|    l      10.0|10.1     f     |1.1|1.0|     r     |4.0|4.1|     b     |7.1| |
     * Y +---+           +---+---+           +---+---+           +---+---+           +---+ Y
     *   |19 |           |22 |22 |           |13 |13 |           |16 |16 |           |19 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |20 |8.1|32 |   |   |23 11.0|35 |   |   |26 |2.1|14 |   |   |29 |5.0| 17|   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                       |   |23 11.1|35 |   |       &lt;---Z               &lt;---X
     *                       +---+---+---+---+---+
     *                       |32 |           |26 |
     *                       +---+           +---+ ^
     *                       |8.0|     d     |2.0| |
     *                       +---+           +---+ Z
     *                       |20 |           |14 |
     *                       +---+---+---+---+---+
     *                       |   |17 |5.1|29 |   |
     *                       +---+---+---+---+---+
     *                                X--&gt;
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
    /* Side swipe table.
     * First dimension: side part index.
     * Second dimension: swipe direction
     * Third dimension: axis,layermask,angle
     * <p>
     * The layermask value applies to the third part on a side of the cube.
     *
     * <pre>
     *                     +---+---+---+---+---+
     *                     |        .1         |
     *                     +   +---+---+---+   +
     *                     |   | 7 |37 |13 |   |
     *                     +   +---+---+---+   +
     *                     | .0|31 | 1 |43 |.2 |
     *                     +   +---+---+---+   +
     *                     |   |25 |49 |19 |   |
     *                     +   +---+---+---+   +
     *                     |        .3         |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |        .0         |        .2         |        .3         |        .1         |
     * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
     * |   |27 |33 | 9 |   |   |14 |44 |20 |   |   |18 |48 |24 |   |   |11 |41 |17 |   |
     * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
     * | .3|51 | 3 |39 |.1 | .1|38 | 2 |50 |.3 | .2|42 | 0 |30 |.0 | .0|35 | 5 |47 |.2 |
     * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
     * |   |21 |45 |15 |   |   | 8 |32 |26 |   |   |12 |36 | 6 |   |   |29 |53 |23 |   |
     * +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +   +---+---+---+   +
     * |        .2         |        .0         |        .1         |        .3         |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |        .0         |
     *                     +   +---+---+---+   +
     *                     |   |28 |34 |10 |   |
     *                     +   +---+---+---+   +
     *                     | .3|52 | 4 |40 |.1 |
     *                     +   +---+---+---+   +
     *                     |   |22 |46 |16 |   |
     *                     +   +---+---+---+   +
     *                     |        .2         |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private final static int[][][] SIDE_SWIPE_TABLE = {
        {// 0 r (+12)
            {1, 2, -1}, // axis, layerMask, angle
            {2, 8, 1},
            {1, 2, 1},
            {2, 8, -1}
        },
        {// 1 u (+12)

            {2, 2, -1},
            {0, 8, 1},
            {2, 2, 1},
            {0, 8, -1}
        },
        {// 2 f (+12)
            {0, 2, -1},
            {1, 8, 1},
            {0, 2, 1},
            {1, 8, -1}
        },
        {// 3 l (+12)
            {2, 8, 1},
            {1, 2, -1},
            {2, 8, -1},
            {1, 2, 1}
        },
        {// 4 d (+12)
            {0, 8, 1},
            {2, 2, -1},
            {0, 8, -1},
            {2, 2, 1}
        },
        { // 5 b (+12)
            {1, 8, 1},
            {0, 2, -1},
            {1, 8, -1},
            {0, 2, 1}
        }
    };

    /** Creates a new instance. */
    public ProfessorCube() {
        super(5);
    }

    /**
     * Transforms the cube and fires a cubeTwisted event.
     *
     * @param  axis  0=x, 1=y, 2=z axis.
     * @param  layerMask A bitmask specifying the layers to be transformed.
     *           The size of the layer mask depends on the value returned by
     *           <code>getLayerCount(axis)</code>. For a 3x3x3 cube, the layer mask has the
     *           following meaning:
     *           31=rotate the whole cube;<br>
     *           1=twist slice near the axis (left, down, back)<br>
     *           2=twist slice in the near middle of the axis<br>
     *           4=twist slice in the middle of the axis<br>
     *           8=twist slice in the far middle of the axis<br>
     *           16=twist slice far away from the axis (right, up, front)
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
                                twistNL();
                                break;
                            case 1:
                                twistNL();
                                twistNL();
                                twistNL();
                                break;
                            case 2:
                                twistNL();
                                twistNL();
                                break;
                        }
                        break;
                    case 1: // y
                        switch (an) {
                            case -1:
                                twistND();
                                break;
                            case 1:
                                twistND();
                                twistND();
                                twistND();
                                break;
                            case 2:
                                twistND();
                                twistND();
                                break;
                        }
                        break;
                    case 2: // z
                        switch (an) {
                            case -1:
                                twistNB();
                                break;
                            case 1:
                                twistNB();
                                twistNB();
                                twistNB();
                                break;
                            case 2:
                                twistNB();
                                twistNB();
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
                // twist at left middle, bottom middle, back middle
                switch (axis) {
                    case 0: // x
                        switch (an) {
                            case 1:
                                twistNR();
                                break;
                            case -1:
                                twistNR();
                                twistNR();
                                twistNR();
                                break;
                            case 2:
                                twistNR();
                                twistNR();
                                break;
                        }
                        break;
                    case 1: // y
                        switch (an) {
                            case 1:
                                twistNU();
                                break;
                            case -1:
                                twistNU();
                                twistNU();
                                twistNU();
                                break;
                            case 2:
                                twistNU();
                                twistNU();
                                break;
                        }
                        break;
                    case 2: // z
                        switch (an) {
                            case 1:
                                twistNF();
                                break;
                            case -1:
                                twistNF();
                                twistNF();
                                twistNF();
                                break;
                            case 2:
                                twistNF();
                                twistNF();
                                break;
                        }
                }
            }
            if ((layerMask & 16) != 0) {
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
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |2.0|
     *                     +---+---+---+---+---+
     *                     |   |           |12 |
     *                     +---+           +---+
     *                     |   |           |0.0|
     *                     +---+           +---+
     *                     |   |           |24 |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   | 0 | 0 |24 |0.1|12 | 2 | 2 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |           |25 |25 |18  48  24 |28 |28 |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |1.1|1.0|42  0.0 30 |4.0|4.1|           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |13 |13 |12  36   6 |16 |16 |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   | 1 | 1 |26 |2.1|14 | 3 | 3 |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |1.0|
     *                     +---+---+---+---+---+
     *                     |   |           |26 |
     *                     +---+           +---+
     *                     |   |           |2.0|
     *                     +---+           +---+
     *                     |   |           |14 |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |3.0|
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 0, 1, 2, 4, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 12, 25, 26, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 24, 13, 14, 28, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[0] = (sideOrient[0] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 6 + i, 24 + i, 18 + i, 12 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * U.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |4.0|15 |3.1| 27|2.0|
     *                     +---+---+---+---+---+
     *                     |18 | 7  37  13 |12 |
     *                     +---+           +---+
     *                     |6.0|31  1.2 43 |0.0|
     *                     +---+           +---+
     *                     |30 |25  49  19 |24 |
     *                     +---+---+---+---+---+
     *                     |6.0|21 |9.1|33 |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 4 |18 |6.1|30 | 6 | 6 |21 |9.0|33 | 0 | 0 |24 |0.1|12 | 2 | 2 |27 |3.0|15 | 4 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 0, 3, 6, 9, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 12, 15, 30, 33, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 24, 27, 18, 21, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[1] = (sideOrient[1] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 7 + i, 25 + i, 19 + i, 13 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * F.
     * <pre>
     *                     |   |           |   |
     *                     +---+---+---+---+---+
     *                     |6.0|21 |9.1|33 |0.0|
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   | 6 | 6 |21 |9.0|33 | 0 | 0 |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |34 |34 |14  44  20 |25 |25 |           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           10.0|10.1 38  2.3 50|1.1|1.0|           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |22 |22 | 8  32  26 |13 |13 |           |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   | 7 | 7 |23 11.0|35 | 1 | 1 |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |7.0|23 11.1|35 |1.0|
     *                     +---+---+---+---+---+
     *                     |   |           |   |
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 9, 10, 11, 1, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 21, 22, 35, 25, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 33, 34, 23, 13, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[2] = (sideOrient[2] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 8 + i, 26 + i, 20 + i, 14 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * L.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |4.0|   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |18 |           |   |
     *                     +---+           +---+
     *                     |6.0|           |   |
     *                     +---+           +---+
     *                     |30 |           |   |
     *                     +---+---+---+---+---+
     *                     |6.0|   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 4 |18 |6.1|30 | 6 | 6 |   |   |   |   |   |   |   |   |   |   |   |   |   | 4 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |31 |27  33   9 |34 |34 |           |   |   |           |   |   |           |31 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |7.0|51  3.1 39 10.0|10.1           |   |   |           |   |   |           |7.1|
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |19 |21  45  15 |22 |22 |           |   |   |           |   |   |           |19 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 5 |20 |8.1|32 | 7 | 7 |   |   |   |   |   |   |   |   |   |   |   |   |   | 5 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |7.0|   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |32 |           |   |
     *                     +---+           +---+
     *                     |8.0|           |   |
     *                     +---+           +---+
     *                     |20 |           |   |
     *                     +---+---+---+---+---+
     *                     |5.0|   |   |   |   |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
        fourCycle(edgeLoc, 6, 7, 8, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 18, 19, 32, 34, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 30, 31, 20, 22, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[3] = (sideOrient[3] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 9 + i, 27 + i, 21 + i, 15 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * D.
     * <pre>
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 5 |20 |8.1|32 | 7 | 7 |23 11.0|35 | 1 | 1 |26 |2.1|14 | 3 | 3 |29 |5.0| 17| 5 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |7.0|23 11.1|35 |1.0|
     *                     +---+---+---+---+---+
     *                     |32 |28  34  10 |26 |
     *                     +---+           +---+
     *                     |8.0|52  4.1 40 |2.0|
     *                     +---+           +---+
     *                     |20 |22  46  16 |14 |
     *                     +---+---+---+---+---+
     *                     |5.0|17 |5.1|29 |3.0|
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 2, 11, 8, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 26, 23, 20, 29, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 14, 35, 32, 17, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[4] = (sideOrient[4] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 10 + i, 28 + i, 22 + i, 16 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * B.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |4.0|15 |3.1| 27|2.0|
     *                     +---+---+---+---+---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 4 |   |   |   |   |   |   |   |   |   |   |   |   |   | 2 | 2 |27 |3.0|15 | 4 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |31 |           |   |   |           |   |   |           |28 |28 |11  41  17 |31 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |7.0|           |   |   |           |   |   |           |4.0|4.1|35  5.2 47 |7.1|
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |19 |           |   |   |           |   |   |           |16 |16 |29  53  23 |19 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * | 5 |   |   |   |   |   |   |   |   |   |   |   |   |   | 3 | 3 |29 |5.0| 17| 5 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+---+---+---+---+
     *                     |5.0|17 |5.1|29 |3.0|
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 3, 4, 5, 7, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 27, 16, 17, 31, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 15, 28, 29, 19, edgeOrient, 1, 1, 1, 1, 2);
        sideOrient[5] = (sideOrient[5] + 3) % 4;
        for (int i = 0; i < 48; i += 24) {
            fourCycle(sideLoc, 11 + i, 29 + i, 23 + i, 17 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * MR.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |   |   |3.1|   |   |
     *                     +---+---+---+---+---+
     *                     |   |    37     |   |
     *                     +---+           +---+
     *                     |   |    1.2    |   |
     *                     +---+           +---+
     *                     |   |    49     |   |
     *                     +---+---+---+---+---+
     *                     |   |   |9.1|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |9.0|   |   |   |   |   |   |   |   |   |3.0|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |    44     |   |   |           |   |   |    41     |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |    2.3    |   |   |           |   |   |    5.2    |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |    32     |   |   |           |   |   |    53     |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   11.0|   |   |   |   |   |   |   |   |   |5.0|   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   11.1|   |   |
     *                     +---+---+---+---+---+
     *                     |   |    34     |   |
     *                     +---+           +---+
     *                     |   |    4.1    |   |
     *                     +---+           +---+
     *                     |   |    46     |   |
     *                     +---+---+---+---+---+
     *                     |   |   |5.1|   |   |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistMR() {
        fourCycle(edgeLoc, 3, 9, 11, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 4, 5, 1, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 44, 34, 53, 37, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 32, 46, 41, 49, sideOrient, 2, 3, 2, 1, 4);
    }

    /**
     * MU.
     * <pre>
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |7.0|51  3.1 39 10.0|10.1 38 2.3 50 |1.1|1.0|42  0.0 30 |4.0|4.1|35  5.2 47 |7.1|
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     * </pre>
     */
    private void twistMU() {
        fourCycle(edgeLoc, 1, 4, 7, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 2, 0, 5, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 51, 38, 42, 35, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 39, 50, 30, 47, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * MF.
     * <pre>
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |6.0|31  1.2 43 |0.0|
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |6.1|   |   |   |   |   |   |   |   |   |0.1|   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |    33     |   |   |           |   |   |    48     |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |    3.1    |   |   |           |   |   |    0.0    |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |    45     |   |   |           |   |   |    36     |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |8.1|   |   |   |   |   |   |   |   |   |2.1|   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |8.0|52  4.1 40 |2.0|
     *                     +---+           +---+
     *                     |   |           |   |
     * </pre>
     */
    private void twistMF() {
        fourCycle(edgeLoc, 0, 6, 8, 2, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 1, 3, 4, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 48, 31, 45, 40, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 36, 43, 33, 52, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NR.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |   |   |   |27 |   |
     *                     +---+---+---+---+---+
     *                     |   |        13 |   |
     *                     +---+           +---+
     *                     |   |        43 |   |
     *                     +---+           +---+
     *                     |   |        19 |   |
     *                     +---+---+---+---+---+
     *                     |   |   |   |33 |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |33 |   |   |   |   |   |   |   |27 |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |        20 |   |   |           |   |   |11         |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |        50 |   |   |           |   |   |35         |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |        26 |   |   |           |   |   |29         |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |35 |   |   |   |   |   |   |   |29 |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |35 |   |
     *                     +---+---+---+---+---+
     *                     |   |        10 |   |
     *                     +---+           +---+
     *                     |   |        40 |   |
     *                     +---+           +---+
     *                     |   |        16 |   |
     *                     +---+---+---+---+---+
     *                     |   |   |   |29 |   |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistNR() {
        fourCycle(edgeLoc, 27, 33, 35, 29, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 13, 20, 10, 29, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 43, 50, 40, 35, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 19, 26, 16, 11, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NU.
     * <pre>
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |31 |27  33   9 |34 |34 |14  44  20 |25 |25 |18  48  24 |28 |28 |11  41  17 |31 |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     * </pre>
     */
    private void twistNU() {
        fourCycle(edgeLoc, 25, 28, 31, 34, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 33, 44, 48, 41, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 27, 14, 18, 11, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 9, 20, 24, 17, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * NF.
     * <pre>
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |30 |25  49  19 |24 |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |30 |   |   |   |   |   |   |   |24 |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |         9 |   |   |           |   |   |18         |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |        39 |   |   |           |   |   |42         |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |        15 |   |   |           |   |   |12         |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |32 |   |   |   |   |   |   |   |26 |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |32 |28  34  10 |26 |
     *                     +---+           +---+
     *                     |   |           |   |
     * </pre>
     */
    private void twistNF() {
        fourCycle(edgeLoc, 24, 30, 32, 26, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 42, 49, 39, 34, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 18, 25, 15, 10, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 12, 19, 9, 28, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NL.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |   |15 |   |   |   |
     *                     +---+---+---+---+---+
     *                     |   | 7         |   |
     *                     +---+           +---+
     *                     |   |31         |   |
     *                     +---+           +---+
     *                     |   |25         |   |
     *                     +---+---+---+---+---+
     *                     |   |21 |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |21 |   |   |   |   |   |   |   |   |   |   |   |15 |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |14         |   |   |           |   |   |        17 |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |38         |   |   |           |   |   |        47 |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   | 8         |   |   |           |   |   |        23 |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |23 |   |   |   |   |   |   |   |   |   |   |   |17 |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |23 |   |   |   |
     *                     +---+---+---+---+---+
     *                     |   |28         |   |
     *                     +---+           +---+
     *                     |   |52         |   |
     *                     +---+           +---+
     *                     |   |22         |   |
     *                     +---+---+---+---+---+
     *                     |   |17 |   |   |   |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistNL() {
        fourCycle(edgeLoc, 17, 23, 21, 15, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 8, 25, 17, 22, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 38, 31, 47, 52, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 14, 7, 23, 28, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * ND.
     * <pre>
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |           |   |   |           |   |   |           |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |19 |21  45  15 |22 |22 | 8  32  26 |13 |13 |12  36   6 |16 |16 |29  53  23 |19 |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     * </pre>
     */
    private void twistND() {
        fourCycle(edgeLoc, 13, 22, 19, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 36, 32, 45, 53, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 12, 8, 21, 29, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 6, 26, 15, 23, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * NB.
     * <pre>
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |18 | 7  37  13 |12 |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |18 |   |   |   |   |   |   |   |   |   |   |   |12 |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |27         |   |   |           |   |   |        24 |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |51         |   |   |           |   |   |        30 |   |   |           |   |
     * +---+           +---+---+           +---+---+           +---+---+           +---+
     * |   |21         |   |   |           |   |   |         6 |   |   |           |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |   |20 |   |   |   |   |   |   |   |   |   |   |   |14 |   |   |   |   |   |   |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |   |           |   |
     *                     +---+           +---+
     *                     |20 |22  46  16 |14 |
     *                     +---+---+---+---+---+
     *                     |   |   |   |   |   |
     *                     +---+---+---+---+---+
     * </pre>
     */
    private void twistNB() {
        fourCycle(edgeLoc, 18, 12, 14, 20, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 6, 22, 27, 13, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 30, 46, 51, 37, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 24, 16, 21, 7, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return (face < 3) ? 16 : 1;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int loc = getEdgeLocation(part - cornerLoc.length) / 12;
            return (loc == 0) ? 4 : ((loc == 1) ? 2 : 8);
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return (face < 3) ? 16 : 1;
        } else {
            return 0;
        }
    }

    @Nonnull
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
            return mask == 4 ? 16 : mask;

        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            int mask = EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][1];

            // Transform value from RevengeCube to ProfessorCube:
            if (mask == 2) {
                mask = 4;
            } else if (mask == 8) {
                mask = 16;
            }

            // Adapt value to each of the three different edge parts on an edge
            switch (loc / 12) {
                case 0:
                    break;
                case 1:
                    if (mask == 4) {
                        mask = 2;
                    }
                    break;
                case 2:
                    if (mask == 4) {
                        mask = 8;
                    }
                    break;
            }
            return mask;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            int mask = SIDE_SWIPE_TABLE[loc % 6][ori][1];
            switch (loc / 6) {
                case 0:
                    mask = 4;
                    break;
                case 1:
                    mask = (loc % 6) < 3 ? 2 : 8;
                    break;
                case 2:
                    break;
                case 3:
                    mask = (loc % 6) < 3 ? 8 : 2;
                    break;
                case 4:
                    if (mask == 8) {
                        mask = 2;
                    } else {
                        mask = 8;
                    }
                    break;
                case 5:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    break;
                case 6:
                    if (mask == 8) {
                        mask = (loc % 6) < 3 ? 4 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    break;
                case 7:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 8 : 4;
                    }
                    break;
                case 8:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 8 : 4;
                    } else {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    }
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
