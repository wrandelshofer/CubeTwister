/*
 * @(#)Cube7.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

/**
 * Represents the state of a 7-times sliced cube (such as a V-Cube 7) by the
 * location and orientation of its parts.
 * <p>
 * A V-Cube 7 has 8 corner parts, 60 edge parts, 150 side parts and one
 * center part. The parts divide each face of the cube into 7 x 7 layers.
 * <p>
 * <b>Corner parts</b>
 * <p>
 * The following diagram shows the initial orientations and locations of 
 * the corner parts:
 * <pre>
 *                             +---+---+---+---+---+---+---+
 *                             |4.0|                   |2.0|
 *                             +---+                   +---+
 *                             |                           |
 *                             +                           +
 *                             |                           |
 *                             +                           +
 *                             |             u             |
 *                             +                           +
 *                             |                           |
 *                             +                           +
 *                             |                           |
 *                             +---+                   +---+
 *                             |6.0|                   |0.0|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |4.1|                   |6.2|6.1|                   |0.2|0.1|                   |2.2|2.1|                   |4.2|
 * +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
 * |                           |                           |                           |                           |
 * +                           +                           +                           +                           +
 * |                           |                           |                           |                           |
 * +                           +                           +                           +                           +
 * |             l             |             f             |             r             |             b             |
 * +                           +                           +                           +                           +
 * |                           |                           |                           |                           |
 * +                           +                           +                           +                           +
 * |                           |                           |                           |                           |
 * +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
 * |5.2|                   |7.1|7.2|                   |1.1|1.2|                   |3.1|3.2|                   |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                             |7.0|                   |1.0|
 *                             +---+                   +---+
 *                             |                           |
 *                             +                           +
 *                             |                           |
 *                             +                           +
 *                             |             d             |
 *                             +                           +
 *                             |                           |
 *                             +                           +
 *                             |                           |
 *                             +---+                   +---+
 *                             |5.0|                   |3.0|
 *                             +---+---+---+---+---+---+---+
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
 *                                           X---&gt;
 *                               +---+---+---+---+---+---+---+
 *                               |   |39 |15 |3.1| 27|51 |   |
 *                               +---+---+---+---+---+---+---+
 *                               |42 |                   |36 |
 *                               +---+                   +---+ 
 *                               |18 |                   |12 |
 *                             Z +---+                   +---+ Z
 *                             | |6.0|         u         |0.0| |
 *                             V +---+                   +---+ V
 *                               |30 |                   |24 |
 *                               +---+                   +---+ 
 *                               |54 |                   |48 |
 *                               +---+---+---+---+---+---+---+
 *               Z---&gt;           |   |45 |21 |9.1|33 |57 |   |           &lt;---Z                       &lt;---X    
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |42 |18 |6.1|30 |54 |   |   |45 |21 |9.0|33 |57 |   |   |48 |24 |0.1|12 |36 |   |   |51 |27 |3.0|15 |39 |   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |55 |                   |58 |58 |                   |49 |49 |                   |52 |52 |                   |55 |
 *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+  
 *   |31 |                   |34 |34 |                   |25 |25 |                   |28 |28 |                   |31 |
 * ^ +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ ^
 * | |7.0|        l          10.0|10.1         f         |1.1|1.0|         r         |4.0|4.1|         b         |7.1| |
 * Y +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ Y
 *   |19 |                   |22 |22 |                   |13 |13 |                   |16 |16 |                   |19 |
 *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+  
 *   |43 |                   |46 |46 |                   |37 |37 |                   |40 |40 |                   |43 |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *   |   |44 |20 |8.1|32 |56 |   |   |47 |23 11.0|35 |59 |   |   |50 |26 |2.1|14 |38 |   |   |53 |29 |5.0| 17|41 |   |
 *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                               |   |47 |23 11.1|35 |59 |   |           &lt;---Z                       &lt;---X
 *                               +---+---+---+---+---+---+---+
 *                               |56 |                   |50 |
 *                               +---+                   +---+ 
 *                               |32 |                   |26 |
 *                               +---+                   +---+ ^
 *                               |8.0|         d         |2.0| |
 *                               +---+                   +---+ Z
 *                               |20 |                   |14 |
 *                               +---+                   +---+ 
 *                               |44 |                   |38 |
 *                               +---+---+---+---+---+---+---+
 *                               |   |41 |17 |5.1|29 |53 |   |
 *                               +---+---+---+---+---+---+---+
 *                                           X--&gt;
 * </pre>
 * <p>
 * <b>Side parts</b>
 * <p>
 * The following diagram shows the initial orientation and location of 
 * the side parts:
 * <pre>
 *                             +---+---+---+---+---+---+---+
 *                             |            .1             |
 *                             +   +---+---+---+---+---+   +
 *                             |   |55 |133|85 |109|61 |   |
 *                             +   +---+---+---+---+---+   +
 *                             |   |103| 7 |37 |13 |139|   |
 *                             +   +---+---+---+---+---+   +
 *                             | .0|79 |31 | 1 |43 |91 |.2 |
 *                             +   +---+---+---+---+---+   +
 *                             |   |127|25 |49 |19 |115|   |
 *                             +   +---+---+---+---+---+   +
 *                             |   |73 |121|97 |145|67 |   |
 *                             +   +---+---+---+---+---+   +
 *                             |            .3             |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * |            .0             |            .2             |            .3             |            .1             |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * |   |75 |129|81 |105|57 |   |   |62 |140|92 |116|68 |   |   |66 |144|96 |120|72 |   |   |59 |137|89 |113|65 |   |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * |   |123|27 |33 | 9 |135|   |   |110|14 |44 |20 |146|   |   |114|18 |48 |24 |126|   |   |107|11 |41 |17 |143|   |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * | .3|99 |51 | 3 |39 |87 |.1 | .1|86 |38 | 2 |50 |98 |.3 | .2|90 |42 | 0 |30 |78 |.0 | .0|83 |35 | 5 |47 |95 |.2 |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * |   |147|21 |45 |15 |111|   |   |134| 8 |32 |26 |122|   |   |138|12 |36 | 6 |102|   |   |131|29 |53 |23 |119|   |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * |   |69 |117|93 |141|63 |   |   |56 |104|80 |128|74 |   |   |60 |108|84 |132|54 |   |   |77 |125|101|149|71 |   |
 * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
 * |            .2             |            .0             |            .1             |            .3             |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 *                             |            .0             |
 *                             +   +---+---+---+---+---+   +
 *                             |   |76 |130|82 |106|58 |   |
 *                             +   +---+---+---+---+---+   +
 *                             |   |124|28 |34 |10 |136|   |
 *                             +   +---+---+---+---+---+   +
 *                             | .3|100|52 | 4 |40 |88 |.1 |
 *                             +   +---+---+---+---+---+   +
 *                             |   |148|22 |46 |16 |112|   |
 *                             +   +---+---+---+---+---+   +
 *                             |   |70 |118|94 |142|64 |   |
 *                             +   +---+---+---+---+---+   +
 *                             |            .2             |
 *                             +---+---+---+---+---+---+---+
 * </pre>
 * <p>
 * For more information about the location and orientation of the parts see
 * {@link AbstractCube}.
 *
 * @author Werner Randelshofer
 * @version 3.0.1 2009-01-25 Fixed EDGE_SWIPE_TABLE.
 * <br>3.0 2009-01-01 Added support for twisting the cube by swiping over
 * its faces.
 * <br>1.0 2008-08-13 Created.
 */
public class Cube7 extends AbstractCube {

    /**
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
     * <p>
     * The layermask value applies to the first part on an edge of a
     * RevengeCube. To get to the layermask value for the first edge part of a
     * Cube7, the value 2 has be replaced by 8 and the value 8 by 64.
     * <pre>
     *                                           X---&gt;
     *                               +---+---+---+---+---+---+---+
     *                               |   |39 |15 |3.1| 27|51 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |42 |                   |36 |
     *                               +---+                   +---+
     *                               |18 |                   |12 |
     *                             Z +---+                   +---+ Z
     *                             | |6.0|         u         |0.0| |
     *                             V +---+                   +---+ V
     *                               |30 |                   |24 |
     *                               +---+                   +---+
     *                               |54 |                   |48 |
     *                               +---+---+---+---+---+---+---+
     *               Z---&gt;           |   |45 |21 |9.1|33 |57 |   |           &lt;---Z                       &lt;---X
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |42 |18 |6.1|30 |54 |   |   |45 |21 |9.0|33 |57 |   |   |48 |24 |0.1|12 |36 |   |   |51 |27 |3.0|15 |39 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |                   |58 |58 |                   |49 |49 |                   |52 |52 |                   |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |31 |                   |34 |34 |                   |25 |25 |                   |28 |28 |                   |31 |
     * ^ +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ ^
     * | |7.0|        l          10.0|10.1         f         |1.1|1.0|         r         |4.0|4.1|         b         |7.1| |
     * Y +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ Y
     *   |19 |                   |22 |22 |                   |13 |13 |                   |16 |16 |                   |19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+
     *   |43 |                   |46 |46 |                   |37 |37 |                   |40 |40 |                   |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |44 |20 |8.1|32 |56 |   |   |47 |23 11.0|35 |59 |   |   |50 |26 |2.1|14 |38 |   |   |53 |29 |5.0| 17|41 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |47 |23 11.1|35 |59 |   |           &lt;---Z                       &lt;---X
     *                               +---+---+---+---+---+---+---+
     *                               |56 |                   |50 |
     *                               +---+                   +---+
     *                               |32 |                   |26 |
     *                               +---+                   +---+ ^
     *                               |8.0|         d         |2.0| |
     *                               +---+                   +---+ Z
     *                               |20 |                   |14 |
     *                               +---+                   +---+
     *                               |44 |                   |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |   |41 |17 |5.1|29 |53 |   |
     *                               +---+---+---+---+---+---+---+
     *                                           X--&gt;
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
     * The layermask value applies to the third side part on a face of a
     * ProfessorCube. To get to the layermask value of a Cube7, the value has to
     * be multiplied by 2.
     *
     * <pre>
     *                             +---+---+---+---+---+---+---+
     *                             |            .1             |
     *                             +   +---+---+---+---+---+   +
     *                             |   |55 |133|85 |109|61 |   |
     *                             +   +---+---+---+---+---+   +
     *                             |   |103| 7 |37 |13 |139|   |
     *                             +   +---+---+---+---+---+   +
     *                             | .0|79 |31 | 1 |43 |91 |.2 |
     *                             +   +---+---+---+---+---+   +
     *                             |   |127|25 |49 |19 |115|   |
     *                             +   +---+---+---+---+---+   +
     *                             |   |73 |121|97 |145|67 |   |
     *                             +   +---+---+---+---+---+   +
     *                             |            .3             |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * |            .0             |            .2             |            .3             |            .1             |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * |   |75 |129|81 |105|57 |   |   |62 |140|92 |116|68 |   |   |66 |144|96 |120|72 |   |   |59 |137|89 |113|65 |   |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * |   |123|27 |33 | 9 |135|   |   |110|14 |44 |20 |146|   |   |114|18 |48 |24 |126|   |   |107|11 |41 |17 |143|   |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * | .3|99 |51 | 3 |39 |87 |.1 | .1|86 |38 | 2 |50 |98 |.3 | .2|90 |42 | 0 |30 |78 |.0 | .0|83 |35 | 5 |47 |95 |.2 |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * |   |147|21 |45 |15 |111|   |   |134| 8 |32 |26 |122|   |   |138|12 |36 | 6 |102|   |   |131|29 |53 |23 |119|   |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * |   |69 |117|93 |141|63 |   |   |56 |104|80 |128|74 |   |   |60 |108|84 |132|54 |   |   |77 |125|101|149|71 |   |
     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
     * |            .2             |            .0             |            .1             |            .3             |
     * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                             |            .0             |
     *                             +   +---+---+---+---+---+   +
     *                             |   |76 |130|82 |106|58 |   |
     *                             +   +---+---+---+---+---+   +
     *                             |   |124|28 |34 |10 |136|   |
     *                             +   +---+---+---+---+---+   +
     *                             | .3|100|52 | 4 |40 |88 |.1 |
     *                             +   +---+---+---+---+---+   +
     *                             |   |148|22 |46 |16 |112|   |
     *                             +   +---+---+---+---+---+   +
     *                             |   |70 |118|94 |142|64 |   |
     *                             +   +---+---+---+---+---+   +
     *                             |            .2             |
     *                             +---+---+---+---+---+---+---+
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
    public Cube7() {
        super(7);
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
                                twistN3L();
                                break;
                            case 1:
                                twistN3L();
                                twistN3L();
                                twistN3L();
                                break;
                            case 2:
                                twistN3L();
                                twistN3L();
                                break;
                        }
                        break;
                    case 1: // y
                        switch (an) {
                            case -1:
                                twistN3D();
                                break;
                            case 1:
                                twistN3D();
                                twistN3D();
                                twistN3D();
                                break;
                            case 2:
                                twistN3D();
                                twistN3D();
                                break;
                        }
                        break;
                    case 2: // z
                        switch (an) {
                            case -1:
                                twistN3B();
                                break;
                            case 1:
                                twistN3B();
                                twistN3B();
                                twistN3B();
                                break;
                            case 2:
                                twistN3B();
                                twistN3B();
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
                                twistN3R();
                                break;
                            case -1:
                                twistN3R();
                                twistN3R();
                                twistN3R();
                                break;
                            case 2:
                                twistN3R();
                                twistN3R();
                                break;
                        }
                        break;
                    case 1: // y
                        switch (an) {
                            case 1:
                                twistN3U();
                                break;
                            case -1:
                                twistN3U();
                                twistN3U();
                                twistN3U();
                                break;
                            case 2:
                                twistN3U();
                                twistN3U();
                                break;
                        }
                        break;
                    case 2: // z
                        switch (an) {
                            case 1:
                                twistN3F();
                                break;
                            case -1:
                                twistN3F();
                                twistN3F();
                                twistN3F();
                                break;
                            case 2:
                                twistN3F();
                                twistN3F();
                                break;
                        }
                }
            }
            if ((layerMask & 32) != 0) {
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
            if ((layerMask & 64) != 0) {
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
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |2.0|
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |36 |
     *                               +---+                   +---+  
     *                               |   |                   |12 |
     *                               +---+                   +---+  
     *                               |   |         u         |0.0|
     *                               +---+                   +---+  
     *                               |   |                   |24 |
     *                               +---+                   +---+  
     *                               |   |                   |48 |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   | 0 | 0 |48 |24 |0.1|12 |36 | 2 | 2 |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |49 |49 |66  144 96  120 72 |52 |52 |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |25 |25 |114 18  48  24  126|28 |28 |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |1.1|1.0|90  42  0.0 30  78 |4.0|4.1|         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |13 |13 |138 12  36   6  102|16 |16 |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |37 |37 |60  108 84  132 54 |40 |40 |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   | 1 | 1 |50 |26 |2.1|14 |38 | 3 | 3 |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |1.0|
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |50 |
     *                               +---+                   +---+  
     *                               |   |                   |28 |
     *                               +---+                   +---+  
     *                               |   |         d         |2.0|
     *                               +---+                   +---+  
     *                               |   |                   |14 |
     *                               +---+                   +---+  
     *                               |   |                   |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |3.0|
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistR() {
        fourCycle(cornerLoc, 0, 1, 3, 2, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 0, 1, 2, 4, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 12 + i, 25 + i, 26 + i, 16 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 24 + i, 13 + i, 14 + i, 28 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[0] = (sideOrient[0] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 6 + i, 24 + i, 18 + i, 12 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * U.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |4.0|39 |15 |3.1|27 |51 |2.0|
     *                               +---+---+---+---+---+---+---+
     *                               |42 |55  133 85  109 61 |36 |
     *                               +---+                   +---+  
     *                               |18 |103  7  37  13  139|12 |
     *                               +---+                   +---+  
     *                               |6.0|79  31  1.2 43  91 |0.0|
     *                               +---+                   +---+  
     *                               |30 |127 25  49  19  115|24 |
     *                               +---+                   +---+  
     *                               |54 |73  121 97  145 67 |48 |
     *                               +---+---+---+---+---+---+---+
     *                               |6.0|45 |21 |9.1|33 |57 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 4 |42 |18 |6.1|30 |54 | 6 | 6 |45 |21 |9.0|33 |57 | 0 | 0 |48 |24 |0.1|12 |36 | 2 | 2 |51 |27 |3.0|15 |39 | 4 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistU() {
        fourCycle(cornerLoc, 0, 2, 4, 6, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 0, 3, 6, 9, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 12 + i, 15 + i, 30 + i, 33 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 24 + i, 27 + i, 18 + i, 21 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[1] = (sideOrient[1] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 7 + i, 25 + i, 19 + i, 13 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * F.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |6.0|45 |21 |9.1|33 |57 |0.0|
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   | 6 | 6 |45 |21 |9.0|33 |57 | 0 | 0 |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |58 |58 |62  140 92  116 68 |49 |49 |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |34 |34 |110 14  44  20  146|25 |25 |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         10.0|10.1 86 38  2.3 50  98 |1.1|1.0|         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |22 |22 |134  8  32  26  122|13 |13 |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |46 |46 |56  104 80  128 74 |37 |37 |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   | 7 | 7 |47 |23 11.0|35 |59 | 1 | 1 |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |7.0|47 |23 11.1|35 |59 |1.0|
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistF() {
        fourCycle(cornerLoc, 6, 7, 1, 0, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 9, 10, 11, 1, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 21 + i, 22 + i, 35 + i, 25 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 33 + i, 34 + i, 23 + i, 13 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[2] = (sideOrient[2] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 8 + i, 26 + i, 20 + i, 14 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * L.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |4.0|   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |18 |                   |   |
     *                               +---+                   +---+  
     *                               |6.0|         u         |   |
     *                               +---+                   +---+  
     *                               |30 |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |6.0|   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 4 |42 |18 |6.1|30 |54 | 6 | 6 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   | 4 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |75  129 81  105 57 |58 |58 |                   |   |   |                   |   |   |                   |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |31 |123 27  33   9  135|34 |34 |                   |   |   |                   |   |   |                   |31 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |7.0|99  51   l  39  87 10.0|10.1         f         |   |   |         r         |   |   |         b         |7.1|
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |19 |147 21  45  15  111|22 |22 |                   |   |   |                   |   |   |                   |19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |43 |69  117 93  141 63 |46 |46 |                   |   |   |                   |   |   |                   |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 5 |44 |20 |8.1|32 |56 | 7 | 7 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |7.0|   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |56 |                   |   |
     *                               +---+                   +---+  
     *                               |32 |                   |   |
     *                               +---+                   +---+  
     *                               |8.0|         d         |   |
     *                               +---+                   +---+  
     *                               |20 |                   |   |
     *                               +---+                   +---+  
     *                               |44 |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |5.0|   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistL() {
        fourCycle(cornerLoc, 6, 4, 5, 7, cornerOrient, 2, 1, 2, 1, 3);
        fourCycle(edgeLoc, 6, 7, 8, 10, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 18 + i, 19 + i, 32 + i, 34 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 30 + i, 31 + i, 20 + i, 22 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[3] = (sideOrient[3] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 9 + i, 27 + i, 21 + i, 15 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * D.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 5 |44 |20 |8.1|32 |56 | 7 | 7 |47 |23 11.0|35 |59 | 1 | 1 |50 |26 |2.1|14 |38 | 3 | 3 |53 |29 |5.0| 17|41 | 5 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |7.0|47 |23 11.1|35 |59 |1.0|
     *                               +---+---+---+---+---+---+---+
     *                               |56 |76  130 82  106 58 |50 |
     *                               +---+                   +---+  
     *                               |32 |124 28  34  10  136|26 |
     *                               +---+                   +---+  
     *                               |8.0|100 52  4.1 40  88 |2.0|
     *                               +---+                   +---+  
     *                               |20 |148 22  46  16  112|14 |
     *                               +---+                   +---+  
     *                               |44 |70  118 94  142 64 |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |5.0|41 |17 |5.1|29 |53 |3.0|
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistD() {
        fourCycle(cornerLoc, 7, 5, 3, 1, cornerOrient, 0, 0, 0, 0, 3);
        fourCycle(edgeLoc, 2, 11, 8, 5, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 26 + i, 23 + i, 20 + i, 29 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 14 + i, 35 + i, 32 + i, 17 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[4] = (sideOrient[4] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 10 + i, 28 + i, 22 + i, 16 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * B.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |4.0|39 |15 |3.1|27 |51 |2.0|
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 4 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   | 2 | 2 |51 |27 |3.0|15 |39 | 4 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |                   |   |   |                   |   |   |                   |52 |52 |59  137 89  113 65 |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |31 |                   |   |   |                   |   |   |                   |28 |28 |107 11  41  17  143|31 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |7.0|         l         |   |   |         f         |   |   |         r         |4.0|4.1|83  35  5.2 47  95 |7.1|
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |19 |                   |   |   |                   |   |   |                   |16 |16 |131 29  53  23  119|19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |43 |                   |   |   |                   |   |   |                   |40 |40 |77  125 101 149 71 |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   | 5 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   | 3 | 3 |53 |29 |5.0| 17|41 | 5 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |5.0|41 |17 |5.1|29 |53 |3.0|
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistB() {
        fourCycle(cornerLoc, 2, 3, 5, 4, cornerOrient, 1, 2, 1, 2, 3);
        fourCycle(edgeLoc, 3, 4, 5, 7, edgeOrient, 1, 1, 1, 1, 2);
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 27 + i, 16 + i, 17 + i, 31 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        for (int i = 0; i < 48; i += 24) {
            fourCycle(edgeLoc, 15 + i, 28 + i, 29 + i, 19 + i, edgeOrient, 1, 1, 1, 1, 2);
        }
        sideOrient[5] = (sideOrient[5] + 3) % 4;
        for (int i = 0; i < 144; i += 24) {
            fourCycle(sideLoc, 11 + i, 29 + i, 23 + i, 17 + i, sideOrient, 3, 3, 3, 3, 4);
        }
    }

    /**
     * MR.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |3.1|   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |        85         |   |
     *                               +---+                   +---+  
     *                               |   |        37         |   |
     *                               +---+                   +---+  
     *                               |   |        1.2        |   |
     *                               +---+                   +---+  
     *                               |   |        49         |   |
     *                               +---+                   +---+  
     *                               |   |        97         |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |9.1|   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |9.0|   |   |   |   |   |   |   |   |   |   |   |   |   |3.0|   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |        92         |   |   |                   |   |   |        89         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |        44         |   |   |                   |   |   |        41         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |        2.3        |   |   |         r         |   |   |        5.2        |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |        32         |   |   |                   |   |   |        53         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |        80         |   |   |                   |   |   |        101        |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   11.0|   |   |   |   |   |   |   |   |   |   |   |   |   |5.0|   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   11.1|   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |        82         |   |
     *                               +---+                   +---+  
     *                               |   |        34         |   |
     *                               +---+                   +---+  
     *                               |   |        4.1        |   |
     *                               +---+                   +---+  
     *                               |   |        46         |   |
     *                               +---+                   +---+  
     *                               |   |        94         |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |5.1|   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistMR() {
        fourCycle(edgeLoc, 3, 9, 11, 5, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 2, 4, 5, 1, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 44, 34, 53, 37, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 32, 46, 41, 49, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 92, 82, 101, 85, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 80, 94, 89, 97, sideOrient, 2, 3, 2, 1, 4);
    }

    /**
     * MU.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 0
     *   |7.0|99  51  3.1 39  87 10.0|10.1 86  38 2.3 50  98 |1.1|1.0|90  42  0.0 30  78 |4.0|4.1|83  35  5.2 47  95 |7.1|
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistMU() {
        fourCycle(edgeLoc, 1, 4, 7, 10, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 3, 2, 0, 5, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 51, 38, 42, 35, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 39, 50, 30, 47, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 99, 86, 90, 83, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 87, 98, 78, 95, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * MF.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |6.0|79  31  1.2 43  91 |0.0|
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |6.1|   |   |   |   |   |   |   |   |   |   |   |   |   |0.1|   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |        81         |   |   |                   |   |   |        96         |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |        33         |   |   |                   |   |   |        48         |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         3         |   |   |         f         |   |   |         0         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |        45         |   |   |                   |   |   |        36         |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |        93         |   |   |                   |   |   |        84         |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |8.1|   |   |   |   |   |   |   |   |   |   |   |   |   |2.1|   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |8.0|100 52  4.1 40  88 |2.0|
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistMF() {
        fourCycle(edgeLoc, 0, 6, 8, 2, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 0, 1, 3, 4, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 48, 31, 45, 40, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 36, 43, 33, 52, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 96, 79, 93, 88, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 84, 91, 81, 100, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * N3R.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |27 |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |            109    |   |
     *                               +---+                   +---+  
     *                               |   |            13     |   |
     *                               +---+                   +---+  
     *                               |   |         u  43     |   |
     *                               +---+                   +---+  
     *                               |   |            19     |   |
     *                               +---+                   +---+  
     *                               |   |            145    |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |33 |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |33 |   |   |   |   |   |   |   |   |   |   |   |27 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |            116    |   |   |                   |   |   |    137            |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |            20     |   |   |                   |   |   |    11             |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f  50     |   |   |         r         |   |   |    35   b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |            26     |   |   |                   |   |   |    29             |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |            128    |   |   |                   |   |   |    125            |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |35 |   |   |   |   |   |   |   |   |   |   |   |29 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |35 |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |            106    |   |
     *                               +---+                   +---+  
     *                               |   |            10     |   |
     *                               +---+                   +---+  
     *                               |   |         d  40     |   |
     *                               +---+                   +---+  
     *                               |   |            16     |   |
     *                               +---+                   +---+  
     *                               |   |            142    |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |29 |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3R() {
        fourCycle(edgeLoc, 27, 33, 35, 29, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 43, 50, 40, 35, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 13, 20, 10, 29, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 19, 26, 16, 11, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 109, 116, 106, 125, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 145, 128, 142, 137, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * N3U.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |31 |123  27  33   9 135|34 |34 |110 14  44  20  146|25 |25 |114 18  48  24  126|28 |28 |107 11  41  17  143|31 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3U() {
        fourCycle(edgeLoc, 25, 28, 31, 34, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 33, 44, 48, 41, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 27, 14, 18, 11, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 9, 20, 24, 17, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 123, 110, 114, 107, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 135, 146, 126, 143, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * N3F.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |30 |127 25  49  19  115|24 |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |30 |   |   |   |   |   |   |   |   |   |   |   |24 |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |            105    |   |   |                   |   |   |    144            |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |             9     |   |   |                   |   |   |    18             |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l  39     |   |   |         f         |   |   |    42   r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |            15     |   |   |                   |   |   |    12             |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |            141    |   |   |                   |   |   |    108            |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |32 |   |   |   |   |   |   |   |   |   |   |   |26 |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |32 |124 28  34  10  136|26 |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3F() {
        fourCycle(edgeLoc, 24, 30, 32, 26, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 42, 49, 39, 34, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 18, 25, 15, 10, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 12, 19, 9, 28, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 144, 127, 141, 136, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 108, 115, 105, 124, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * N3L.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |15 |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |    133            |   |
     *                               +---+                   +---+  
     *                               |   |     7             |   |
     *                               +---+                   +---+  
     *                               |   |    31   u         |   |
     *                               +---+                   +---+  
     *                               |   |    25             |   |
     *                               +---+                   +---+  
     *                               |   |    121            |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |21 |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |21 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |15 |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |    140            |   |   |                   |   |   |            113    |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |    14             |   |   |                   |   |   |            17     |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |    38   f         |   |   |         r         |   |   |         b  47     |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |     8             |   |   |                   |   |   |            23     |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |    104            |   |   |                   |   |   |            149    |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |23 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |17 |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |23 |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |    130            |   |
     *                               +---+                   +---+  
     *                               |   |    28             |   |
     *                               +---+                   +---+  
     *                               |   |    52   d         |   |
     *                               +---+                   +---+  
     *                               |   |    22             |   |
     *                               +---+                   +---+  
     *                               |   |    118            |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |17 |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3L() {
        fourCycle(edgeLoc, 17, 23, 21, 15, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 38, 31, 47, 52, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 8, 25, 17, 22, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 14, 7, 23, 28, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 104, 121, 113, 118, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 140, 133, 149, 130, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * N3D.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |19 |147 21  45  15  111|22 |22 |134  8  32  26  122|13 |13 |138 12  36   6  102|16 |16 |131 29  53  23  119|19 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3D() {
        fourCycle(edgeLoc, 13, 22, 19, 16, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 36, 32, 45, 53, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 12, 8, 21, 29, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 6, 26, 15, 23, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 138, 134, 147, 131, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 102, 122, 111, 119, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * N3B.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |18 |103  7  37  13  139|12 |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |18 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |12 |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |    129            |   |   |                   |   |   |            120    |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |    27             |   |   |                   |   |   |            24     |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |    51   l         |   |   |         f         |   |   |         r  30     |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |    21             |   |   |                   |   |   |             6     |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |    117            |   |   |                   |   |   |            132    |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |20 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |14 |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |20 |148 22  46  16  112|14 |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistN3B() {
        fourCycle(edgeLoc, 18, 12, 14, 20, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 30, 46, 51, 37, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 6, 22, 27, 13, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 24, 16, 21, 7, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 132, 148, 129, 139, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 120, 112, 117, 103, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * NR.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |51 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                61 |   |
     *                               +---+                   +---+  
     *                               |   |                139|   |
     *                               +---+                   +---+  
     *                               |   |         u      91 |   |
     *                               +---+                   +---+  
     *                               |   |                115|   |
     *                               +---+                   +---+  
     *                               |   |                67 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |57 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |57 |   |   |   |   |   |   |   |   |   |51 |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                68 |   |   |                   |   |   |59                 |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                146|   |   |                   |   |   |107                |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f      98 |   |   |         r         |   |   |83       b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                122|   |   |                   |   |   |131                |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                74 |   |   |                   |   |   |77                 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |59 |   |   |   |   |   |   |   |   |   |53 |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |59 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                58 |   |
     *                               +---+                   +---+  
     *                               |   |                136|   |
     *                               +---+                   +---+  
     *                               |   |         d      88 |   |
     *                               +---+                   +---+  
     *                               |   |                112|   |
     *                               +---+                   +---+  
     *                               |   |                64 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |53 |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistNR() {
        fourCycle(edgeLoc, 51, 57, 59, 53, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 61, 68, 58, 77, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 139, 146, 136, 131, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 91, 98, 88, 83, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 115, 122, 112, 107, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 67, 74, 64, 59, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NU.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |55 |75  129 81  105 57 |58 |58 |62  140 92  116 68 |49 |49 |66  144 96  120 72 |52 |52 |59  137 89  113 65 |55 |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistNU() {
        fourCycle(edgeLoc, 55, 58, 49, 52, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 75, 62, 66, 59, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 129, 140, 144, 137, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 81, 92, 96, 89, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 105, 116, 120, 113, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 57, 68, 72, 65, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * NF.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |54 |73  121 97  145 67 |48 |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |54 |   |   |   |   |   |   |   |   |   |48 |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                57 |   |   |                   |   |   |66                 |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                135|   |   |                   |   |   |114                |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l      87 |   |   |         f         |   |   |90       r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                111|   |   |                   |   |   |138                |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                63 |   |   |                   |   |   |60                 |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |56 |   |   |   |   |   |   |   |   |   |50 |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |56 |76  130 82  106 58 |50 |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistNF() {
        fourCycle(edgeLoc, 48, 54, 56, 50, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 66, 73, 63, 58, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 114, 121, 111, 106, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 90, 97, 87, 82, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 138, 145, 135, 130, sideOrient, 1, 2, 3, 2, 4);
        fourCycle(sideLoc, 60, 67, 57, 76, sideOrient, 1, 2, 3, 2, 4);
    }

    /**
     * NL.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |39 |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |55                 |   |
     *                               +---+                   +---+  
     *                               |   |103                |   |
     *                               +---+                   +---+  
     *                               |   |79       u         |   |
     *                               +---+                   +---+  
     *                               |   |127                |   |
     *                               +---+         .3        +---+  
     *                               |   |73                 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |45 |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |45 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |39 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |62                 |   |   |                   |   |   |                65 |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |110                |   |   |                   |   |   |                143|   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |86       f         |   |   |         r         |   |   |         b      95 |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |134                |   |   |                   |   |   |                119|   |
     *   +---+                   +---+---+         .0        +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |56                 |   |   |                   |   |   |                71 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |47 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |41 |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |47 |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |76                 |   |
     *                               +---+                   +---+  
     *                               |   |124                |   |
     *                               +---+                   +---+  
     *                               |   |100      d         |   |
     *                               +---+                   +---+  
     *                               |   |148                |   |
     *                               +---+         .2        +---+  
     *                               |   |70                 |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |41 |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistNL() {
        fourCycle(edgeLoc, 41, 47, 45, 39, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 70, 56, 73, 65, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 148, 134, 127, 143, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 100, 86, 79, 95, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 124, 110, 103, 119, sideOrient, 2, 3, 2, 1, 4);
        fourCycle(sideLoc, 76, 62, 55, 71, sideOrient, 2, 3, 2, 1, 4);
    }

    /**
     * ND.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |         l         |   |   |         f         |   |   |         r         |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |                   |   |   |                   |   |   |                   |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |43 |69  117 93  141 63 |46 |46 |56  104 80  128 74 |37 |37 |60  108 84  132 54 |40 |40 |77  125 101 149 71 |43 |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistND() {
        fourCycle(edgeLoc, 37, 46, 43, 40, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 60, 56, 69, 77, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 108, 104, 117, 125, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 84, 80, 93, 101, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 132, 128, 141, 149, sideOrient, 3, 2, 1, 2, 4);
        fourCycle(sideLoc, 54, 74, 63, 71, sideOrient, 3, 2, 1, 2, 4);
    }

    /**
     * NB.
     * <pre>
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |42 |55  133 85  109 61 |36 |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         u         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |42 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |36 |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |75                 |   |   |                   |   |   |                72 |   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |123                |   |   |                   |   |   |                126|   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |99       l         |   |   |         f         |   |   |         r      78 |   |   |         b         |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |147                |   |   |                   |   |   |                102|   |   |                   |   |
     *   +---+                   +---+---+                   +---+---+                   +---+---+                   +---+ 
     *   |   |69                 |   |   |                   |   |   |                54 |   |   |                   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *   |   |44 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |38 |   |   |   |   |   |   |   |   |
     *   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |   |         d         |   |
     *                               +---+                   +---+  
     *                               |   |                   |   |
     *                               +---+                   +---+  
     *                               |44 |70  118 94  142 64 |38 |
     *                               +---+---+---+---+---+---+---+
     *                               |   |   |   |   |   |   |   |
     *                               +---+---+---+---+---+---+---+
     * </pre>
     */
    private void twistNB() {
        fourCycle(edgeLoc, 42, 36, 38, 44, edgeOrient, 1, 1, 1, 1, 2);
        fourCycle(sideLoc, 72, 64, 69, 55, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 126, 142, 147, 133, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 78, 94, 99, 85, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 102, 118, 123, 109, sideOrient, 2, 1, 2, 3, 4);
        fourCycle(sideLoc, 54, 70, 75, 61, sideOrient, 2, 1, 2, 3, 4);
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    public int getPartLayerMask(int part, int orientation) {
        int face = getPartFace(part, orientation);
        if (part < cornerLoc.length) {
            return (face < 3) ? 64 : 1;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int loc = getEdgeLocation(part - cornerLoc.length) / 12;
            /*
            switch (loc) {
            case 0 :
            return 4;
            case 1 :
            return 2;
            case 2 :
            return 8;
            case 3 :
            return 1;
            case 4 :
            default :
            return 16;
            }*/
            switch (loc) {
                case 0:
                    return 8;
                case 1:
                    return 4;
                case 2:
                    return 16;
                case 3:
                    return 2;
                case 4:
                default:
                    return 32;
            }
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return (face < 3) ? 64 : 1;
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
            return mask == 4 ? 64 : mask;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            int edgeIndex = part - cornerLoc.length;
            int loc = getEdgeLocation(edgeIndex);
            int ori = (2 - getPartOrientation(part) + orientation) % 2;
            int mask = EDGE_SWIPE_TABLE[loc % 12][ori][swipeDirection][1];

            // Transform value from RevengeCube to Cube7:
            if (mask == 2) {
                mask = 8;
            } else if (mask == 8) {
                mask = 64;
            }

            // Adapt value to each of the five different edge parts on an edge
            switch (loc / 12) {
                case 0:
                    break;
                case 1:
                    if (mask == 8) {
                        mask = 4;
                    }
                    break;
                case 2:
                    if (mask == 8) {
                        mask = 16;
                    }
                    break;
                case 3:
                    if (mask == 8) {
                        mask = 2;
                    }
                    break;
                case 4:
                    if (mask == 8) {
                        mask = 32;
                    }
                    break;
            }
//System.out.println("Cube7 part:"+part+" orientation:"+orientation+" swipeDirection:"+swipeDirection+" Mask "+mask);
            return mask;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            int loc = getSideLocation(part - cornerLoc.length - edgeLoc.length);
            int ori = (4 - getPartOrientation(part) + swipeDirection) % 4;
            int mask = SIDE_SWIPE_TABLE[loc % 6][ori][1];


            // Adapt value to each of the 16 different edge parts on an edge
            switch (loc / 6) {
                // Center:
                // -------
                case 0:
                    mask = 4;
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                // Inner ring:
                // -------
                case 1:
                    mask = (loc % 6) < 3 ? 2 : 8;
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 2:
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 3:
                    mask = (loc % 6) < 3 ? 8 : 2;
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 4:
                    if (mask == 8) {
                        mask = 2;
                    } else {
                        mask = 8;
                    }
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 5:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 6:
                    if (mask == 8) {
                        mask = (loc % 6) < 3 ? 4 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 7:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 8 : 4;
                    }
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                case 8:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 8 : 4;
                    } else {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    }
                    mask <<= 1; // Transform from ProfessorCube to Cube7.
                    break;
                // Outer ring corners:
                // -------
                case 9:
                    mask = (loc % 6) < 3 ? 2 : 32;
                    break;
                case 10:
                    if (mask == 2) {
                        mask = 2;
                    } else {
                        mask = 32;
                    }
                    break;
                case 11:
                        mask = (loc % 6) < 3 ? 32 : 2;
                    break;
                case 12:
                    if (mask == 8) {
                        mask = 2;
                    } else {
                        mask = 32;
                    }
                    break;
                // Outer ring centers of edges:
                // -------
                case 13:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 8 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 8;
                    }
                    break;
                case 14:
                    if (mask == 8) {
                        mask = (loc % 6) < 3 ? 8 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 8;
                    }
                    break;
                case 15:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 8 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 32 : 8;
                    }
                    break;
                case 16:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 32 : 8;
                    } else {
                        mask = (loc % 6) < 3 ? 8 : 2;
                    }
                    break;
                // Outer ring clockwise from centers of edges:
                // -------
                case 17:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 16;
                    }
                    break;
                case 18:
                    if (mask == 8) {
                        mask = (loc % 6) < 3 ? 16 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    break;
                case 19:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 16 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 32 : 4;
                    }
                    break;
                case 20:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 32 : 16;
                    } else {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    }
                    break;
                // Outer ring counter-clockwise from centers of edges:
                // -------
                    /* +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
                     * |   |75 |129|81 |105|57 |   |   |62 |140|92 |116|68 |   |   |66 |144|96 |120|72 |   |   |59 |137|89 |113|65 |   |
                     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
                     * |   |123|27 |33 | 9 |135|   |   |110|14 |44 |20 |146|   |   |114|18 |48 |24 |126|   |   |107|11 |41 |17 |143|   |
                     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
                     * | .3|99 |51 | 3 |39 |87 |.1 | .1|86 |38 | 2 |50 |98 |.3 | .2|90 |42 | 0 |30 |78 |.0 | .0|83 |35 | 5 |47 |95 |.2 |
                     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
                     * |   |147|21 |45 |15 |111|   |   |134| 8 |32 |26 |122|   |   |138|12 |36 | 6 |102|   |   |131|29 |53 |23 |119|   |
                     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +
                     * |   |69 |117|93 |141|63 |   |   |56 |104|80 |128|74 |   |   |60 |108|84 |132|54 |   |   |77 |125|101|149|71 |   |
                     * +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +   +---+---+---+---+---+   +*/
                case 21:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 16 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 4;
                    }
                    break;
                case 22:
                    if (mask == 8) {
                        mask = (loc % 6) < 3 ? 4 : 32;
                    } else {
                        mask = (loc % 6) < 3 ? 2 : 16;
                    }
                    break;
                case 23:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 4 : 2;
                    } else {
                        mask = (loc % 6) < 3 ? 32 : 16;
                    }
                    break;
                case 24:
                    if (mask == 2) {
                        mask = (loc % 6) < 3 ? 32 : 4;
                    } else {
                        mask = (loc % 6) < 3 ? 16 : 2;
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
