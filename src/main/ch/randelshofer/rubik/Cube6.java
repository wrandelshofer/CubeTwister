/* @(#)Cube6.java
 * 
 * Copyright (c) 2008-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik;

/**
 * Represents the state of a 6-times sliced cube (such as a V-Cube 6) by the 
 * location and orientation of its parts.
 * <p>
 * A V-Cube 6 has 8 corner parts, 48 edge parts, 96 side parts and one
 * center part. The parts divide each face of the cube into 6 x 6 layers.
 * <p>
 * <b>Corner parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the corner parts:
 * <pre>
 *                         +---+---+---+---+---+---+
 *                         |4.0|               |2.0|
 *                         +---+               +---+
 *                         |                       |
 *                         +                       +
 *                         |                       |
 *                         +           u           +
 *                         |                       |
 *                         +                       +
 *                         |                       |
 *                         +---+               +---+
 *                         |6.0|               |0.0|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |4.1|               |6.2|6.1|               |0.2|0.1|               |2.2|2.1|               |4.2|
 * +---+               +---+---+               +---+---+               +---+---+               +---+
 * |                       |                       |                       |                       |
 * +                       +                       +                       +                       +
 * |                       |                       |                       |                       |
 * +           l           +           f           +           r           +           b           +
 * |                       |                       |                       |                       |
 * +                       +                       +                       +                       +
 * |                       |                       |                       |                       |
 * +---+               +---+---+               +---+---+               +---+---+               +---+
 * |5.2|               |7.1|7.2|               |1.1|1.2|               |3.1|3.2|               |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                         |7.0|               |1.0|
 *                         +---+               +---+
 *                         |                       |
 *                         +                       +
 *                         |           d           |
 *                         +                       +
 *                         |                       |
 *                         +                       +
 *                         |                       |
 *                         +---+               +---+
 *                         |5.0|               |3.0|
 *                         +---+---+---+---+---+---+
 * </pre>
 * <p>
 * <b>Edge parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the edge parts. The first 12 edges are located near the origins of the
 * x-, y- and z-axis. The second 12 edges are located far from the origin
 * of the x-, y- and z-axis. The third 12 edges are located near the origins of
 * the axes. The fourth 12 edges are again located near the origins of the axes. 
 * <pre>
 *                                     X---&gt;
 *                           +---+---+---+---+---+---+
 *                           |   |27 |3.1|15 |39 |   |
 *                           +---+---+---+---+---+---+
 *                           |30 |               |24 |
 *                           +---+               +---+  
 *                           |6.0|               |0.0|
 *                         Z +---+       u       +---+ Z
 *                         | |18 |               |12 | |
 *                         V +---+               +---+ V
 *                           |42 |               |36 |
 *                           +---+---+---+---+---+---+
 *           Z---&gt;           |   |33 |9.1|21 |45 |   |       &lt;---Z                   &lt;---X
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |30 |18 |6.1|42 |   |   |33 |9.0|21 |45 |   |   |36 |12 |0.1|24 |   |   |39 |15 |3.0|27 |   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |43 |               |46 |46 |               |37 |37 |               |40 |40 |               |43 |
 * ^ +---+               +---+---+               +---+---+               +---+---+               +---+ ^
 * | |19 |               |22 |22 |               |13 |13 |               |16 |16 |               |19 | |
 * Y +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ Y
 *   |7.0|               10.0|10.1               |1.1|1.0|               |4.0|4.1|               |7.1| 
 *   +---+               +---+---+               +---+---+               +---+---+               +---+  
 *   |31 |               |34 |34 |               |25 |25 |               |28 |28 |               |31 |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |32 |8.1|20 |44 |   |   |35 11.0|23 |47 |   |   |38 |14 |2.1|26 |   |   |41 |17 |5.0|29 |   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                           |   |35 11.1|23 |47 |   |       &lt;---Z                   &lt;---X
 *                           +---+---+---+---+---+---+
 *                           |44 |               |38 |
 *                           +---+               +---+ ^
 *                           |20 |               |14 | |
 *                           +---+       d       +---+ Z
 *                           |8.0|               |2.0| 
 *                           +---+               +---+  
 *                           |32 |               |26 |
 *                           +---+---+---+---+---+---+
 *                           |   |29 |5.1|17 |41 |   |
 *                           +---+---+---+---+---+---+
 *                                    X--&gt;
 * </pre>
 * <p>
 * <b>Side parts</b>
 * <p>
 * The following diagram shows the initial orientation and location of 
 * the side parts:
 * <pre>
 *                         +---+---+---+---+---+---+
 *                         |          .1           |
 *                         +   +---+---+---+---+   +
 *                         |   |25 |79 |55 |31 |   |
 *                         +   +---+---+---+---+   +
 *                         |   |49 | 1 | 7 |85 |   |
 *                         + .0+---+---+---+---+.2 +
 *                         |   |73 |19 |13 |61 |   |
 *                         +   +---+---+---+---+   +
 *                         |   |43 |67 |91 |37 |   |
 *                         +   +---+---+---+---+   +
 *                         |          .3           |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |          .0           |          .2           |          .3           |          .1           |
 * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
 * |   |45 |75 |51 |27 |   |   |32 |86 |62 |38 |   |   |36 |90 |66 |42 |   |   |29 |83 |59 |35 |   |
 * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
 * |   |69 |21 | 3 |81 |   |   |56 | 8 |14 |92 |   |   |60 |12 |18 |72 |   |   |53 | 5 |11 |89 |   |
 * + .3+---+---+---+---+.1 + .1+---+---+---+---+.3 + .2+---+---+---+---+.0 + .0+---+---+---+---+.2 +
 * |   |93 |15 | 9 |57 |   |   |80 | 2 |20 |68 |   |   |84 | 6 | 0 |48 |   |   |77 |23 |17 |65 |   |
 * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
 * |   |39 |63 |87 |33 |   |   |26 |50 |74 |44 |   |   |30 |54 |78 |24 |   |   |47 |71 |95 |41 |   |
 * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
 * |          .2           |          .0           |          .1           |          .3           |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                         |          .0           |
 *                         +   +---+---+---+---+   +
 *                         |   |46 |76 |52 |28 |   |
 *                         +   +---+---+---+---+   +
 *                         |   |70 |22 | 4 |82 |   |
 *                         + .3+---+---+---+---+.1 +
 *                         |   |94 |16 |10 |58 |   |
 *                         +   +---+---+---+---+   +
 *                         |   |40 |64 |88 |34 |   |
 *                         +   +---+---+---+---+   +
 *                         |          .2           |
 *                         +---+---+---+---+---+---+
 * </pre>
 * <p>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>2.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>1.0 2008-08-13 Created.
 */
public class Cube6 extends AbstractCube {

    /**
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
     * <p>
     * The layermask value applies to the first part on an edge of a
     * RevengeCube. To get to the layermask value for a Cube6, the value
     * 2 has be replaced by 4 and the value 8 by 32.
     * <pre>
     *                                     X---&gt;
     *                           +---+---+---+---+---+---+
     *                           |   |27 |3.1|15 |39 |   |
     *                           +---+---+---+---+---+---+
     *                           |30 |               |24 |
     *                           +---+               +---+
     *                           |6.0|               |0.0|
     *                         Z +---+       u       +---+ Z
     *                         | |18 |               |12 | |
     *                         V +---+               +---+ V
     *                           |42 |               |36 |
     *                           +---+---+---+---+---+---+
     *           Z---&gt;           |   |33 |9.1|21 |45 |   |       &lt;---Z                   &lt;---X
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |30 |18 |6.1|42 |   |   |33 |9.0|21 |45 |   |   |36 |12 |0.1|24 |   |   |39 |15 |3.0|27 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |               |46 |46 |               |37 |37 |               |40 |40 |               |43 |
     * ^ +---+               +---+---+               +---+---+               +---+---+               +---+ ^
     * | |19 |               |22 |22 |               |13 |13 |               |16 |16 |               |19 | |
     * Y +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ Y
     *   |7.0|               10.0|10.1               |1.1|1.0|               |4.0|4.1|               |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+
     *   |31 |               |34 |34 |               |25 |25 |               |28 |28 |               |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |32 |8.1|20 |44 |   |   |35 11.0|23 |47 |   |   |38 |14 |2.1|26 |   |   |41 |17 |5.0|29 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |35 11.1|23 |47 |   |       &lt;---Z                   &lt;---X
     *                           +---+---+---+---+---+---+
     *                           |44 |               |38 |
     *                           +---+               +---+ ^
     *                           |20 |               |14 | |
     *                           +---+       d       +---+ Z
     *                           |8.0|               |2.0|
     *                           +---+               +---+
     *                           |32 |               |26 |
     *                           +---+---+---+---+---+---+
     *                           |   |29 |5.1|17 |41 |   |
     *                           +---+---+---+---+---+---+
     *                                    X--&gt;
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
     * <p>
     * The layermask value applies to the second part on a side of a
     * RevengeCube. To get to the layermask value for a Cube6, the value has to
     * be multiplied by 2.
     *
     * <pre>
     *                         +---+---+---+---+---+---+
     *                         |          .1           |
     *                         +   +---+---+---+---+   +
     *                         |   |25 |79 |55 |31 |   |
     *                         +   +---+---+---+---+   +
     *                         |   |49 | 1 | 7 |85 |   |
     *                         + .0+---+---+---+---+.2 +
     *                         |   |73 |19 |13 |61 |   |
     *                         +   +---+---+---+---+   +
     *                         |   |43 |67 |91 |37 |   |
     *                         +   +---+---+---+---+   +
     *                         |          .3           |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |          .0           |          .2           |          .3           |          .1           |
     * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
     * |   |45 |75 |51 |27 |   |   |32 |86 |62 |38 |   |   |36 |90 |66 |42 |   |   |29 |83 |59 |35 |   |
     * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
     * |   |69 |21 | 3 |81 |   |   |56 | 8 |14 |92 |   |   |60 |12 |18 |72 |   |   |53 | 5 |11 |89 |   |
     * + .3+---+---+---+---+.1 + .1+---+---+---+---+.3 + .2+---+---+---+---+.0 + .0+---+---+---+---+.2 +
     * |   |93 |15 | 9 |57 |   |   |80 | 2 |20 |68 |   |   |84 | 6 | 0 |48 |   |   |77 |23 |17 |65 |   |
     * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
     * |   |39 |63 |87 |33 |   |   |26 |50 |74 |44 |   |   |30 |54 |78 |24 |   |   |47 |71 |95 |41 |   |
     * +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +   +---+---+---+---+   +
     * |          .2           |          .0           |          .1           |          .3           |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                         |          .0           |
     *                         +   +---+---+---+---+   +
     *                         |   |46 |76 |52 |28 |   |
     *                         +   +---+---+---+---+   +
     *                         |   |70 |22 | 4 |82 |   |
     *                         + .3+---+---+---+---+.1 +
     *                         |   |94 |16 |10 |58 |   |
     *                         +   +---+---+---+---+   +
     *                         |   |40 |64 |88 |34 |   |
     *                         +   +---+---+---+---+   +
     *                         |          .2           |
     *                         +---+---+---+---+---+---+
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
    public Cube6() {
        super(6);
    }

    @Override
    public void transform0(int axis, int layerMask, int angle) {
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
        if ((layerMask & 8) != 0) {
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
        if ((layerMask & 16) != 0) {
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
        if ((layerMask & 32) != 0) {
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
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |2.0|
     *                           +---+---+---+---+---+---+
     *                           |   |               |24 |
     *                           +---+               +---+  
     *                           |   |               |0.0|
     *                           +---+       u       +---+ 
     *                           |   |               |12 |
     *                           +---+               +---+ 
     *                           |   |               |36 |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |0.2|0.1|36 |12 |0.1|24 |2.2|2.1|   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |37 |37 |36  90  66  42 |40 |40 |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |13 |13 |60  12  18  72 |16 |16 |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |1.1|1.0|84   6   0  48 |4.0|4.1|               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |25 |25 |30  54  78  24 |28 |28 |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |1.1|1.2|38 |14 |2.1|26 |3.1|3.2|   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |1.0|
     *                           +---+---+---+---+---+---+
     *                           |   |               |38 |
     *                           +---+               +---+ 
     *                           |   |               |14 |
     *                           +---+       d       +---+ 
     *                           |   |               |2.0|
     *                           +---+               +---+  
     *                           |   |               |26 |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |3.0|
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 12, 1, 2, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 0, 13, 14, 4, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 24, 37, 38, 28, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 36, 25, 26, 40, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 0 + i, 18 + i, 12 + i, 6 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * U.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |4.0|27 |3.1|15 |39 |2.0|
     *                           +---+---+---+---+---+---+
     *                           |30 |25  79  55  31 |24 |
     *                           +---+               +---+  
     *                           |6.0|49   1   7  85 |0.0|
     *                           +---+       u       +---+ 
     *                           |18 |73  19  13  61 |12 |
     *                           +---+               +---+ 
     *                           |42 |43  67  91  37 |36 |
     *                           +---+---+---+---+---+---+
     *                           |6.0|33 |9.1|21 |45 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |4.1|30 |6.1|18 |42 |6.2|6.1|33 |9.0|21 |45 |0.2|0.1|36 |12 |0.1|24 |2.2|2.1|39 |15 |3.0|27 |4.2|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 0, 3, 18, 21, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 12, 15, 6, 9, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 24, 27, 42, 45, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 39, 30, 33, 36, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 1 + i, 19 + i, 13 + i, 7 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * F.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |6.0|33 |9.1|21 |45 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |6.2|6.1|33 |9.0|21 |45 |0.2|0.1|   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |46 |46 |32  86  62  38 |37 |37 |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |22 |22 |56   8  14  92 |13 |13 |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               10.0|10.1 80  2  20  68 |1.1|1.0|               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |34 |34 |26  50  74  44 |25 |25 |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |7.1|7.2|35 11.0|23 |47 |1.1|1.2|   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |7.0|35 11.1|23 |47 |1.0|
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 21, 22, 11, 1, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 9, 10, 23, 13, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 33, 34, 47, 37, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 45, 46, 35, 25, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 2 + i, 20 + i, 14 + i, 8 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * L.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |4.0|   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |30 |               |   |
     *                           +---+               +---+  
     *                           |6.0|               |   |
     *                           +---+       u       +---+ 
     *                           |18 |               |   |
     *                           +---+               +---+ 
     *                           |42 |               |   |
     *                           +---+---+---+---+---+---+
     *                           |6.0|   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |4.1|30 |6.1|18 |42 |6.2|6.1|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |4.2|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |45  75  51  27 |46 |46 |               |   |   |               |   |   |               |43 |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |19 |69  21   3  81 |22 |22 |               |   |   |               |   |   |               |19 |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |7.0|93  15   9  57 10.0|10.1               |   |   |               |   |   |               |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |31 |39  63  87  33 |34 |34 |               |   |   |               |   |   |               |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |5.2|32 |8.1|20 |44 |7.1|7.2|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |5.1|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |7.0|   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |44 |               |   |
     *                           +---+               +---+ 
     *                           |20 |               |   |
     *                           +---+       d       +---+ 
     *                           |8.0|               |   |
     *                           +---+               +---+  
     *                           |32 |               |   |
     *                           +---+---+---+---+---+---+
     *                           |5.0|   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
        fourCycle(edgeLoc, 6, 7, 20, 22, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 18, 19, 8, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 30, 31, 44, 46, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 42, 43, 32, 34, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 3 + i, 21 + i, 15 + i, 9 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * D.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |5.2|32 |8.1|20 |44 |7.1|7.2|35 11.1|23 |47 |1.1|1.2|38 |14 |2.1|26 |3.1|3.2|41 |17 |5.0|29 |5.1|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |7.0|35 11.1|23 |47 |1.0|
     *                           +---+---+---+---+---+---+
     *                           |44 |46  76  52  28 |38 |
     *                           +---+               +---+ 
     *                           |20 |70  22   4  82 |14 |
     *                           +---+       d       +---+ 
     *                           |8.0|94  16  10  58 |2.0|
     *                           +---+               +---+  
     *                           |32 |40  64  88  34 |26 |
     *                           +---+---+---+---+---+---+
     *                           |5.0|29 |5.1|17 |41 |3.0|
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 2, 23, 20, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 14, 11, 8, 17, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 26, 47, 44, 29, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 38, 35, 32, 41, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 4 + i, 22 + i, 16 + i, 10 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * B.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |4.0|27 |3.1|15 |39 |2.0|
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |4.1|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |2.2|2.1|39 |15 |3.0|27 |4.2|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |               |   |   |               |   |   |               |40 |40 |29  83  59  35 |43 |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |19 |               |   |   |               |   |   |               |16 |16 |53   5  11  89 |19 |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |7.0|               |   |   |               |   |   |               |4.0|4.1|77  23  17  65 |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |31 |               |   |   |               |   |   |               |28 |28 |47  71  95  41 |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |5.2|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |3.1|3.2|41 |17 |5.0|29 |5.1|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |5.0|29 |5.1|17 |41 |3.0|
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 3, 16, 17, 7, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 15, 4, 5, 19, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 27, 40, 41, 31, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(edgeLoc, 39, 28, 29, 43, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 96; i += 24) {
            fourCycle(sideLoc, 5 + i, 23 + i, 17 + i, 11 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * MR.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |15 |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |        55     |   |
     *                           +---+               +---+  
     *                           |   |        7.2    |   |
     *                           +---+       u       +---+ 
     *                           |   |        13     |   |
     *                           +---+               +---+ 
     *                           |   |        91     |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |21 |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |21 |   |   |   |   |   |   |   |   |   |   |15 |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |        62     |   |   |               |   |   |    83         |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |        14.3   |   |   |               |   |   |    5.2        |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |        20     |   |   |               |   |   |    23         |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |        74     |   |   |               |   |   |    71         |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |23 |   |   |   |   |   |   |   |   |   |   |17 |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |23 |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |        52     |   |
     *                           +---+               +---+ 
     *                           |   |        4.1    |   |
     *                           +---+       d       +---+ 
     *                           |   |        10     |   |
     *                           +---+               +---+  
     *                           |   |        88     |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |17 |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistMR() {
        fourCycle(edgeLoc, 15, 21, 23, 17, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 7, 14, 4, 23, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 13, 20, 10, 5, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 55, 62, 52, 71, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 91, 74, 88, 83, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * MU.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |19 |69  21  3.1 81 |22 |22 |56  8.3 14  92 |13 |13 |60 12.0 18  72 |16 |16 |53  5.2 11  89 |19 |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistMU() {
        fourCycle(edgeLoc, 19, 22, 13, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 14, 18, 11, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 21, 8, 12, 5, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 69, 56, 60, 53, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 81, 92, 72, 89, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * MF.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |18 |73  19  13  61 |12 |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |18 |   |   |   |   |   |   |   |   |   |   |12 |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |        51     |   |   |               |   |   |    90         |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |         3     |   |   |               |   |   |    12         |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |         9     |   |   |               |   |   |     6         |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |        87     |   |   |               |   |   |    54         |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |20 |   |   |   |   |   |   |   |   |   |   |14 |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |20 |70  22   4  82 |14 |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistMF() {
        fourCycle(edgeLoc, 12, 18, 20, 14, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 6, 13, 3, 22, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 12, 19, 9, 4, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 54, 61, 51, 70, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 90, 73, 87, 82, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * ML.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |3.1|   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |    79         |   |
     *                           +---+               +---+  
     *                           |   |    1.2        |   |
     *                           +---+       u       +---+ 
     *                           |   |    19         |   |
     *                           +---+               +---+ 
     *                           |   |    67         |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |9.1|   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |9.0|   |   |   |   |   |   |   |   |   |   |   |   |3.0|   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |    86         |   |   |               |   |   |        59     |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |     8         |   |   |               |   |   |        11.2   |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |    2.3        |   |   |               |   |   |        17     |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |    50         |   |   |               |   |   |        95     |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   11.0|   |   |   |   |   |   |   |   |   |   |   |   |5.0|   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |11 |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |    76         |   |
     *                           +---+               +---+ 
     *                           |   |    22         |   |
     *                           +---+       d       +---+ 
     *                           |   |    16.1       |   |
     *                           +---+               +---+  
     *                           |   |    64         |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |5.1|   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistML() {
        fourCycle(edgeLoc, 5, 11, 9, 3, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 19, 11, 16, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 8, 1, 17, 22, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 50, 67, 59, 64, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 86, 79, 95, 76, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * MD.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |7.0|93  15   9  57 10.0|10.1 80  2  20  68 |1.1|1.0|84   6   0  48 |4.0|4.1|77  23  17  65 |7.1|
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistMD() {
        fourCycle(edgeLoc, 1, 10, 7, 4, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 20, 9, 17, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 6, 2, 15, 23, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 48, 68, 57, 65, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 84, 80, 93, 77, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * MB.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |6.0|49  1.2  7  85 |0.0|
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |6.1|   |   |   |   |   |   |   |   |   |   |   |   |0.1|   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |    75         |   |   |               |   |   |        66     |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |    21         |   |   |               |   |   |        18     |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |    15.1       |   |   |               |   |   |        0.0    |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |    63         |   |   |               |   |   |        78     |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |8.1|   |   |   |   |   |   |   |   |   |   |   |   |2.1|   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |8.0|94  16  10.1 58|2.0|
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistMB() {
        fourCycle(edgeLoc, 0, 2, 8, 6, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 16, 21, 7, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 18, 10, 15, 1, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 66, 58, 63, 49, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 78, 94, 75, 85, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * NR.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |39 |   |
     *                           +---+---+---+---+---+---+
     *                           |   |            31 |   |
     *                           +---+               +---+  
     *                           |   |            85 |   |
     *                           +---+       u       +---+ 
     *                           |   |            61 |   |
     *                           +---+               +---+ 
     *                           |   |            37 |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |45 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |45 |   |   |   |   |   |   |   |   |39 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |            38 |   |   |               |   |   |29             |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |            92 |   |   |               |   |   |53             |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |            68 |   |   |               |   |   |77             |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |            44 |   |   |               |   |   |47             |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |47 |   |   |   |   |   |   |   |   |41 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |47 |   |
     *                           +---+---+---+---+---+---+
     *                           |   |            28 |   |
     *                           +---+               +---+ 
     *                           |   |            82 |   |
     *                           +---+       d       +---+ 
     *                           |   |            58 |   |
     *                           +---+               +---+  
     *                           |   |            34 |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |41 |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistNR() {
        fourCycle(edgeLoc, 39, 45, 47, 41, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 31, 38, 28, 47, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 85, 92, 82, 77, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 61, 68, 58, 53, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 37, 44, 34, 29, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NU.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |43 |45  75  51  27 |46 |46 |32  86  62  38 |37 |37 |36  90  66  42 |40 |40 |29  83  59  35 |43 |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistNU() {
        fourCycle(edgeLoc, 43, 46, 37, 40, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 45, 32, 36, 29, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 75, 86, 90, 83, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 51, 62, 66, 59, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 27, 38, 42, 35, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * NF.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |42 |43  67  91  37 |36 |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |42 |   |   |   |   |   |   |   |   |36 |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |            27 |   |   |               |   |   |36             |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |            81 |   |   |               |   |   |60             |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |            57 |   |   |               |   |   |84             |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |            33 |   |   |               |   |   |30             |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |44 |   |   |   |   |   |   |   |   |38 |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |44 |46  76  52  28 |38 |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistNF() {
        fourCycle(edgeLoc, 36, 42, 44, 38, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 36, 43, 33, 28, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 60, 67, 57, 52, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 84, 91, 81, 76, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 30, 37, 27, 46, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NL.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |27 |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |25             |   |
     *                           +---+               +---+  
     *                           |   |49             |   |
     *                           +---+       u       +---+ 
     *                           |   |73             |   |
     *                           +---+               +---+ 
     *                           |   |43             |   |
     *                           +---+---+---+---+---+---+
     *                           |   |33 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |33 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |27 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |32             |   |   |               |   |   |            35 |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |56             |   |   |               |   |   |            89 |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |80             |   |   |               |   |   |            65 |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |               |   |   |26             |   |   |               |   |   |            41 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |35 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |29 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |35 |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |46             |   |
     *                           +---+               +---+ 
     *                           |   |70             |   |
     *                           +---+       d       +---+ 
     *                           |   |94             |   |
     *                           +---+               +---+  
     *                           |   |40             |   |
     *                           +---+---+---+---+---+---+
     *                           |   |29 |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistNL() {
        fourCycle(edgeLoc, 29, 35, 33, 27, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 32, 25, 41, 46, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 56, 49, 65, 70, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 80, 73, 89, 94, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 26, 43, 35, 40, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * ND.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |               |   |   |               |   |   |               |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |31 |39  63  87  33 |34 |34 |26  50  74  44 |25 |25 |30  54  78  24 |28 |28 |47  71  95  41 |31 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistND() {
        fourCycle(edgeLoc, 25, 34, 31, 28, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 30, 26, 39, 47, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 54, 50, 63, 71, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 78, 74, 87, 95, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 24, 44, 33, 41, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * NB.
     * <pre>
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |30 |25  79  55  31 |24 |
     *                           +---+               +---+  
     *                           |   |               |   |
     *                           +---+       u       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |30 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |24 |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |45             |   |   |               |   |   |            42 |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+ 
     *   |   |69             |   |   |               |   |   |            72 |   |   |               |   |
     *   +---+       l       +---+---+       f       +---+---+       r       +---+---+       b       +---+ 
     *   |   |93             |   |   |               |   |   |            48 |   |   |               |   |
     *   +---+               +---+---+               +---+---+               +---+---+               +---+  
     *   |   |39             |   |   |               |   |   |            24 |   |   |               |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |32 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |26 |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     *                           |   |               |   |
     *                           +---+               +---+ 
     *                           |   |               |   |
     *                           +---+       d       +---+ 
     *                           |   |               |   |
     *                           +---+               +---+  
     *                           |32 |40  64  88  34 |26 |
     *                           +---+---+---+---+---+---+
     *                           |   |   |   |   |   |   |
     *                           +---+---+---+---+---+---+
     * </pre>
     */
    private void twistNB() {
        fourCycle(edgeLoc, 24, 26, 32, 30, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 42, 34, 39, 25, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 72, 88, 93, 79, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 48, 64, 69, 55, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 24, 40, 45, 31, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    @Override
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return (face < 3) ? 32 : 1;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int loc = getEdgeLocation(part - cornerLoc.length);
            switch (loc / 12) {
                case 0:
                    return 4;
                case 1:
                    return 8;
                case 2:
                    return 2;
                case 3:
                default:
                    return 16;
            }
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return (face < 3) ? 32 : 1;
        } else {
            return 0;
        }
    }

    @Override
    public int[][] toStickers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setToStickers(int[][] stickers) {
        throw new UnsupportedOperationException("Not supported yet.");
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
            return EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][0];
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            return SIDE_SWIPE_TABLE[loc % 6][ori][0];
        } else {
            return -1;
        }

    }

    @Override
    public int getPartSwipeLayerMask(int part, int orientation, int swipeDirection) {
        if (part < cornerLoc.length) {
            int loc = getCornerLocation(part);
            int ori = (3 - getPartOrientation(part) + orientation) % 3;
            int mask = CORNER_SWIPE_TABLE[loc][ori][swipeDirection][1];
            return mask == 4 ? 32 : mask;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            int mask = EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][1];

            // Transform value from RevengeCube to Cube6:
            if (mask == 2) {
                mask = 4;
            } else if (mask == 8) {
                mask = 32;
            }

            // Adapt value to each of the four different edge parts on an edge
            switch (loc / 12) {
                case 0:
                    break;
                case 1:
                    if (mask == 4) {
                        mask = 8;
                    }
                    break;
                case 2:
                    if (mask == 4) {
                        mask = 2;
                    }
                    break;
                case 3:
                    if (mask == 4) {
                        mask = 16;
                    }
                    break;
            }
            return mask;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            int mask = SIDE_SWIPE_TABLE[loc % 6][ori][1];

            // Transform value from RevengeCube to Cube6:
            mask <<= 1;

            // Adapt value to each of the 16 different edge parts on an edge
            switch (loc / 6) {
                case 0:
                    mask = (loc % 6) < 3 ? 4 : 8;
                    break;
                case 1:
                    break;
                case 2:
                    mask = (loc % 6) < 3 ? 8 : 4;
                    break;
                case 3:
                    mask = mask == 4 ? 8 : 4;
                    break;
                //
                case 4:
                    mask = (loc % 6) < 3 ? 2 : 16;
                    break;
                case 5:
                    mask = mask == 8 ? 16 : 2;
                    break;
                case 6:
                    mask = (loc % 6) < 3 ? 16 : 2;
                    break;
                case 7:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 16 : 16;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 2;
                    }
                    break;
                case 8:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 4 : 16;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 8;
                    }
                    break;
                case 9:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    } else {
                        mask = (loc % 6) < 3 ? 8 : 16;
                    }
                    break;
                case 10:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 8 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 16 : 4;
                    }
                    break;
                case 11:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 16 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    }
                    break;
                case 12:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 8 : 16;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    break;
                case 13:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 2 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 4 : 16;
                    }
                    break;
                case 14:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 16 : 8;
                    }
                    break;
                case 15:
                    if (mask == 4) {
                        mask = (loc % 6) < 3 ? 16 : 4;
                    } else {
                        mask = (loc % 6) < 3 ? 8 : 2;
                    }
                    break;
            }
            return mask;
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
