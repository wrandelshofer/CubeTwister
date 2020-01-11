/* @(#)AbstractCube.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube;

/** We use our own implementation of EventListenerListAWT, because this class must
 * be able to run on a Java 1.1 VM.
 */

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.event.EventListenerList;
import java.util.Arrays;

/**
 * Abstract base class for classes which implement the {@link Cube} interface.
 * <p>
 * This class provides support for event listeners, and it defines the variables 
 * which hold the location and orientation of the cube parts.
 * <p>
 * <b>Faces and Axes</b>
 * <p>
 * This class defines the location of the six faces of the cube, as shown below:
 * <pre>
 *             +---+---+---+
 *             |           |
 *             |           |
 *             |    1 u    |
 *             |           |
 *             |           |
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 * |           |           |           |           |
 * |           |           |           |           |
 * |    3 l    |    2 f    |    0 r    |    5 b    |
 * |           |           |           |           |
 * |           |           |           |           |
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 *             |           |
 *             |           |
 *             |    4 d    |
 *             |           |
 *             |           |
 *             +---+---+---+
 * </pre>
 * The numbers represent the ID's of the faces: 0=right, 1=up, 2=front, 3=left, 
 * 4=down, 5=back.
 * <p>
 * The face ID's are symmetric along the axis from the right-up-front corner 
 * through the left-down-back corner of the cube.
 * <p>
 * <ul>
 * <li>The x-axis passes from the center of face 3 through the center of face 0.
 * </li>
 * <li>The y-axis passes from the center of face 4 through the center of face 1.
 * </li>
 * <li>The z-axis passes from the center of face 5 through the center of face 2.
 * </li>
 * </ul>
 * <p>
 * <b>Corner parts</b>
 * <p>
 * This class defines the initial locations and orientations of the corner parts
 * as shown below:
 * <pre>
 *             +---+---+---+
 *          ulb|4.0|   |2.0|ubr
 *             +---     ---+
 *             |     u     |
 *             +---     ---+
 *          ufl|6.0|   |0.0|urf
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 * |4.1|   |6.2|6.1|   |0.2|0.1|   |2.2|2.1|   |4.2|
 * +---     ---+---     ---+---     ---+---     ---+
 * |     l     |     f     |     r     |     b     |
 * +---     ---+---     ---+---     ---+---     ---+
 * |5.2|   |7.1|7.2|   |1.1|1.2|   |3.1|3.2|   |5.1|
 * +---+---+---+---+---+---+---+---+---+---+---+---+
 *          dlf|7.0|   |1.0|dfr
 *             +---     ---+
 *             |     d     |
 *             +---     ---+
 *          dbl|5.0|   |3.0|drb
 *             +---+---+---+
 * </pre>
 * <p>
 * The numbers before the dots represents the ID's of the corner parts. There are
 * 12 corner parts with ID's ranging from 0 through 11.  Since a corner part is
 * visible on three faces of the cube, the ID of each part is shown 3 times.
 * <p>
 * The numbers after the dots indicate the orientations of the corner parts.
 * Each corner part can have three different orientations: 0=initial, 
 * 1=tilted counterclockwise and 2=titled clockwise.
 * <p>
 * The orientations of the corner parts are symmetric along the axis from the 
 * right-up-front corner through the left-down-back corner of the cube.
 * <pre>
 *       +-----------+              +-----------+
 *      /4.0/   /2.0/|             /1.0/   /3.0/|
 *     +---     ---+.2            +---     ---+.2
 *    /     u     /|/|           /     d     /|/| 
 *   +---     ---+   +          +---     ---+   +
 *  /6.0/   /0.0/|  /|         /7.0/   /5.0/|  /|
 * +---+---+---*.1  .1        +---+---+---*.1  .1 
 * | .1|   | .2|/ r|/         | .1|   | .2|/ b|/
 * +---     ---+   +          +---     ---+   +
 * |     f     |/|/           |     l     |/|/
 * +---     ---+.2            +---     ---+.2
 * | .2|   | .1|/             |.2 |   | .1|/ 
 * +---+---+---+              +---+---+---+
 * </pre>
 * <p>
 * Here is an alternative representation of the initial locations and 
 * orientations of the corner parts as a list:
 * <ul>
 * <li>0: urf</li><li>1: dfr</li><li>2: ubr</li><li>3: drb</li>
 * <li>4: ulb</li><li>5: dbl</li><li>6: ufl</li><li>7: dlf</li>
 * </ul>
 * <p>
 * <b>Edge parts</b>
 * <p>
 * This class defines the orientations of the edge parts and the location
 * of the first 12 edges.
 * (The locations of additional edge parts are defined by subclasses):
 * <pre>
 *               +----+---+----+
 *               |    |3.1|    |
 *               |    +---+    |
 *               +---+     +---+
 *             ul|6.0|  u  |0.0|ur
 *               +---+     +---+
 *               |    +---+    |
 *               |    |9.1|    |
 * +----+---+----+----+---+----+----+---+----+----+---+----+
 * |    |6.1|    |    |9.0|fu  |    |0.1|    |    |3.0|bu  |
 * |    +---+    |    +---+    |    +---+    |    +---+    |
 * +---+     +---+---+     +---+---+     +---+---+     +---+
 * |7.0|  l  10.0|10.1  f  |1.1|1.0|  r  |4.0|4.1|  b  |7.1|
 * +---+     +---+---+     +---+---+     +---+---+     +---+
 * |lb  +---+  lf|    +---+    |rf  +---+  rb|    +---+    |
 * |    |8.1|    |    11.0|fd  |    |2.1|    |    |5.0|bd  |
 * +----+---+----+----+---+----+----+---+----+----+---+----+
 *               |    11.1|    |
 *               |    +---+    |
 *               +---+     +---+
 *             dl|8.0|  d  |2.0|dr
 *               +---+     +---+
 *               |    +---+    |
 *               |    |5.1|    |
 *               +----+---+----+
 * </pre>
 * The numbers after the dots indicate the orientations of the edge parts.
 * Each edge part can have two different orientations: 0=initial, 1=flipped.
 * <pre>
 *               +----+---+----+
 *               |    |3.1|    |
 *               |    +---+    |
 *               +---+     +---+
 *             ul|6.0|  u  |0.0|ur
 *               +---+     +---+
 *               |    +---+    |
 *               |    |9.1|    |
 * +----+---+----+----+---+----+----+---+----+----+---+----+
 * |    |6.1|    |    |9.0|fu  |    |0.1|    |    |3.0|bu  |
 * |    +---+    |    +---+    |    +---+    |    +---+    |
 * +---+     +---+---+     +---+---+     +---+---+     +---+
 * |7.0|  l  10.0|10.1  f  |1.1|1.0|  r  |4.0|4.1|  b  |7.1|
 * +---+     +---+---+     +---+---+     +---+---+     +---+
 * |lb  +---+  lf|    +---+    |rf  +---+  rb|    +---+    |
 * |    |8.1|    |    11.0|fd  |    |2.1|    |    |5.0|bd  |
 * +----+---+----+----+---+----+----+---+----+----+---+----+
 *               |    11.1|    |
 *               |    +---+    |
 *               +---+     +---+
 *             dl|8.0|  d  |2.0|dr
 *               +---+     +---+
 *               |    +---+    |
 *               |    |5.1|    |
 *               +----+---+----+
 * </pre>
 * <p>
 * The orientations of the edge parts are symmetric along the axis from the 
 * front-up edge through the back-down edge of the cube.
 * <pre>
 *       +-----------+      +-----------+
 *      /   / 3 /   /|      |\   \11 \   \
 *     +--- --- ---+ +      + +--- --- ---+
 *    /6.0/ u /0.0/|/|      |\|\8.0\ d \2.0\
 *   +--- --- ---+  4.0   10.0  +--- --- ---+
 *  /   / 9 /   /| |/|      |\ \|\   \ 5 \   \
 * +---+-*-+---+  r  +      +  l  +---+-*-+---+
 * |   |9.0|   |/| |/        \|\ \|   |5.0|   |
 * +--- --- ---+  2.1        6.1  +--- --- ---+
 * |10 | f | 1 |/|/            \|\| 7 | b | 4 |
 * +--- --- ---+ +              + +--- --- ---+
 * |   11.0|   |/                \|   |3.0|   |
 * +---+---+---+                  +---+---+---+
 * </pre>
 * <p>
 * Here is an alternative representation of the initial locations and 
 * orientations of the edge parts as a list:
 * <ul>
 * <li> 0: ur</li><li> 1: rf</li><li> 2: dr</li> 
 * <li> 3: bu</li><li> 4: rb</li><li> 5: bd</li> 
 * <li> 6: ul</li><li> 7: lb</li><li> 8: dl</li> 
 * <li> 9: fu</li><li>10: lf</li><li>11: fd</li>
 * </ul>
 * <p>
 * <b>Side parts</b>
 * <p>
 * This class defines the orientations of the side parts as shown below
 * (The locations of the side parts are defined by subclasses):
 * <pre>
 *             +-----------+
 *             |     .1    |
 *             |   +---+ u |
 *             | .0| 1 |.2 |
 *             |   +---+   |
 *             |     .3    |
 * +-----------+-----------+-----------+-----------+
 * |     .0    |     .2    |     .3    |    .1     |
 * |   +---+ l |   +---+ f |   +---+ r |   +---+ b |
 * | .3| 3 |.1 | .1| 2 |.3 | .2| 0 |.0 | .0| 5 |.2 |
 * |   +---+   |   +---+   |   +---+   |   +---+   |
 * |     .2    |    .0     |     .1    |     .3    |
 * +-----------+-----------+-----------+-----------+
 *             |     .0    |
 *             |   +---+ d |
 *             | .3| 4 |.1 |
 *             |   +---+   |
 *             |     .2    |
 *             +-----------+
 * </pre>
 * The numbers after the dots indicate the orientations of the side parts.
 * Each side part can have four different orientations: 0=initial, 
 * 1=tilted clockwise, 2=flipped, 3=tilted counterclockwise.
 * <p>
 * The orientations of the side parts are symmetric along the axis from the 
 * right-up-front corner through the left-down-back corner of the cube.
 * <pre>
 *       +-----------+              +-----------+
 *      /     .1    /|             /     .1    /|
 *     +    ---    +r+            +    ---    +b+
 *    / .0/ 1 /.2 /  |           / .0/ 4 /.2 /  | 
 *   +    ---    +.3 +          +    ---    +.3 +
 *  / u   .3    / /|.0         / d   .3    / /|.0
 * +---+---+---*  0  +        +---+---+---*  5  + 
 * | f   .2    .2|/ /         | l   .2    .2|/ /
 * +    ---    + .1+          +    ---    + .1+
 * | .1| 2 |.3 |  /           | .1| 3 |.3 |  /
 * +    ---    + +            +    ---    + +
 * |     .0    |/             |     .0    |/ 
 * +---+---+---+              +---+---+---+
 * </pre>
 * <p>
 * Here is an alternative representation of the initial locations and 
 * orientations of the side parts as a list:
 * <ul>
 * <li>0: r</li> <li>1: u</li> <li>2: f</li> 
 * <li>3: l</li> <li>4: d</li> <li>5: b</li> 
 * </ul>
 * 
 * @author Werner Randelshofer
 */
public abstract class AbstractCube implements Cube, Cloneable {

    /** Identifier for the corner part type. */
    public final static int CORNER_PART = 0;
    /** Identifier for the edge part type. */
    public final static int EDGE_PART = 1;
    /** Identifier for the side part type. */
    public final static int SIDE_PART = 2;
    /** Identifier for the center part type. */
    public final static int CENTER_PART = 3;
    /**
     * Holds the number of corner parts, which is 8.
     */
    public final static int NUMBER_OF_CORNER_PARTS = 8;
    /**
     * Listener support.
     */
    @Nonnull EventListenerList listenerList = new EventListenerList();
    /**
     * Set this to true if listeners shall not be notified
     * about state changes.
     */
    private boolean isQuiet;
    /**
     * Number of layers on the x, y and z axis.
     */
    protected int layerCount;
    /**
     * This array holds the locations of the corner parts.
     * <p>
     * The value of an array element represents the ID of a corner part. The 
     * value must be element of {0..7}. 
     * <p>
     * Each array element has a unique value.
     * <p>
     * Initially each corner part with ID i is located at cornerLoc[i].
     */
    protected int[] cornerLoc;
    /**
     * This array holds the orientations of the corner parts.
     * <p>
     * The value of an array element represents the orientation of a corner part.
     * The value must be element of {0, 1, 2}.
     * <ul>
     * <li>0 = initial orientation</li>
     * <li>1 = tilted counterclockwise</li>
     * <li>2 = tilted clockwise</li>
     * </ul>
     * <p>
     * Multiple array elements can have the same value.
     * <p>
     * Initially each corner part is oriented at orientation 0.
     */
    protected int[] cornerOrient;
    /**
     * This array holds the locations of the edge parts.
     * <p>
     * The value of an array element represents the ID of an edge part. The 
     * value must be element of {0..(n-1)}. Whereas n is the number of edge
     * parts.
     * <p>
     * Each array element has a unique value.
     * <p>
     * Initially each edge part with ID i is located at edgeLoc[i].
     */
    protected int[] edgeLoc;
    /**
     * This array holds the orientations of the edge parts.
     * <p>
     * The value of an array element represents the orientation of an edge part.
     * The value must be element of {0, 1}.
     * <ul>
     * <li>0 = initial orientation</li>
     * <li>1 = flipped</li>
     * </ul>
     * <p>
     * Multiple array elements can have the same value.
     * <p>
     * Initially each edge part is oriented at orientation 0.
     */
    protected int[] edgeOrient;
    /**
     * This array holds the locations of the side parts.
     * <p>
     * The value of an array element represents the ID of a side part. The 
     * value must be element of {0..(n-1)}. Whereas n is the number of side
     * parts.
     * <p>
     * Each array element has a unique value.
     * <p>
     * Initially each side part with ID i is located at sideLoc[i].
     */
    protected int[] sideLoc;
    /**
     * This array holds the orientations of the side parts.
     * <p>
     * The value of an array element represents the orientation of a side part.
     * The value must be element of {0, 1, 2, 4}.
     * <ul>
     * <li>0 = initial orientation</li>
     * <li>1 = tilted counterclockwise</li>
     * <li>2 = flipped</li>
     * <li>3 = tilted clockwise</li>
     * </ul>
     * <p>
     * Multiple array elements can have the same value.
     * <p>
     * Initially each side part is oriented at orientation 0.
     */
    protected int[] sideOrient;

    /** Transformation types of the cube. */
    protected enum TransformType {

        IDENTITY,
        SINGLE_AXIS_TRANSFORM,
        GENERAL_TRANSFORM,
        UNKNOWN;
    }
    /**
     * This field caches the current transformation type of the cube.
     */
    protected TransformType transformType = TransformType.IDENTITY;
    /** If transformType is SINGLE_AXIS_TRANSFORM, this field holds the
     * transformation axis. Otherwise, the value of this field is undefined.
     */
    protected int transformAxis;
    /** If transformType is SINGLE_AXIS_TRANSFORM, this field holds the
     * transformation angle. Otherwise, the value of this field is undefined.
     */
    protected int transformAngle;
    /** If transformType is SINGLE_AXIS_TRANSFORM, this field holds the
     * transformation mask. Otherwise, the value of this field is undefined.
     */
    protected int transformMask;
    /**
     * This array maps corner parts to the six faces of the cube.
     * <p>
     * The first dimension of the array represents the locations, the
     * second dimension the orientations. The values represent the 6 faces:
     * 0=right, 1=up, 2=front, 3=left, 4=down, 5=back.
     */
    protected final static int[][] CORNER_TO_FACE_MAP = {
        {1, 0, 2}, // urf
        {4, 2, 0}, // dfr
        {1, 5, 0}, // ubr
        {4, 0, 5}, // drb
        {1, 3, 5}, // ulb
        {4, 5, 3}, // dbl
        {1, 2, 3}, // ufl
        {4, 3, 2}, // dlf
    };
    /**
     * This array maps edge parts to the three axes of the cube.
     * <p>
     * The index represents the ID of an edge.
     * The values represent the 3 axes 0=x, 1=y and 2=z.
     */
    protected final static int[] EDGE_TO_AXIS_MAP = {
        2, // edge 0
        1, //      1
        2, //      2
        0, //      3
        1,
        0,
        2,
        1,
        2,
        0,
        1,
        0
    };
    /**
     * This array maps edge parts to rotation angles over the three axes of the
     * cube.
     * <p>
     * The index for the first dimension represents the location,
     * the index for the second dimension the orientation.
     * The value 1 represents clockwise angle, -1 represents 
     * counterclockwise angle.
     */
    protected final static int[][] EDGE_TO_ANGLE_MAP = {
        {1, -1}, // edge 0 ur
        {1, -1}, //      1 rf
        {-1, 1}, //      2 dr
        {-1, 1}, //      3 bu
        {-1, 1}, //      4 rb
        {1, -1}, //      5 bd
        {-1, 1}, //      6 ul
        {1, -1}, //      7 lb
        {1, -1}, //      8 dl
        {1, -1}, //      9 fu
        {-1, 1}, //     10 lf
        {-1, 1} //     11 fd
    };
    /**
     * This array maps edge parts to the 6 faces of the cube.
     * <p>
     * The index for the first dimension represents the location,
     * the index for the second dimension the orientation.
     * The values represent the 6 faces:
     * 0=right, 1=up, 2=front, 3=left, 4=down, 5=back.
     */
    protected final static int[][] EDGE_TO_FACE_MAP = {
        {1, 0}, // edge 0 ur
        {0, 2}, //      1 rf
        {4, 0}, //      2 dr
        {5, 1}, //      3 bu
        {0, 5}, //      4 rb
        {5, 0}, //      5 bd
        {1, 3}, //      6 ul
        {3, 5}, //      7 lb
        {4, 3}, //      8 dl
        {2, 1}, //      9 fu
        {3, 2}, //     10 lf
        {2, 4} //     11 fd
    };
    /**
     * This is used for mapping center part orientations
     * to the 6 sides of the cube.
     * <p>
     * The index for the first dimension represents the location,
     * the index for the second dimension the orientation.
     * The values represent the 6 sides.
     */
    @Nonnull
    protected int[][] CENTER_TO_SIDE_MAP = {
        //{f, r, d, b, l, u }
        {0, 1, 2, 3, 4, 5} // 0: Front at front, Right at right
        , {5, 1, 0, 2, 4, 3}// 1: Bottom, Right, CR
        , {3, 1, 5, 0, 4, 2}// 2: Back, Right, CR2
        , {2, 1, 3, 5, 4, 0}// 3: Top, Right, CR'
        , {4, 0, 2, 1, 3, 5}// 4: Right, Back, CU
        , {3, 4, 2, 0, 1, 5}// 5: Back, Left, CU2
        , {1, 3, 2, 4, 0, 5} // 6: // Left, Front, CU'
        , {0, 2, 4, 3, 5, 1} // 7: // Front, Top, CF
        , {0, 4, 5, 3, 1, 2} // 8: // Front, Left, CF2
        , {0, 5, 1, 3, 2, 4} // 9: // Front, Bottom, CF'
        , {5, 0, 4, 2, 3, 1} // 10: // Right, Top, CR CU
        , {5, 4, 3, 2, 1, 0} // 11: // Top, Left, CR CU2
        , {5, 3, 1, 2, 0, 4} // 12: // Left, Down, CR CU'
        , {1, 0, 5, 4, 3, 2} // 13: // Right, Front, CR2 CU
        , {4, 3, 5, 1, 0, 2} // 14: // Left, Back, CR2 CU'
        , {2, 0, 1, 5, 3, 4} // 15: // Right, Down, CR' CU
        , {2, 4, 0, 5, 1, 3} // 16: // Down, Left, CR' CU2
        , {2, 3, 4, 5, 0, 1} // 17: // Left, Up, CR' CU'
        , {1, 2, 0, 4, 5, 3} // 18: // Down, Up, CR CF
        , {4, 5, 0, 1, 2, 3} // 19: // Down, Back, CR CF'
        , {3, 2, 1, 0, 5, 4} // 20: // Back, Down, CR2 CF
        , {3, 5, 4, 0, 2, 1} // 21: // Back, Up, CR2 CF'
        , {4, 2, 3, 1, 5, 0} // 22: // Up, Back, CR' CF
        , {1, 5, 3, 4, 2, 0} // 23: // Up, Front, CR' CF'
    //{f, r, d, b, l, u }
    };
    /* Corner swipe table.
     * First dimension: side part index.
     * Second dimension: orientation.
     * Third dimension: swipe direction
     * Fourth dimension: axis,layermask,angle
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
     * </pre>*/
    protected final static int[][][][] CORNER_SWIPE_TABLE = {
        {// 0 urf
            {//u
                {2, 4, 1}, // axis, layerMask, angle
                {0, 4, -1},
                {2, 4, -1},
                {0, 4, 1}
            },
            {//r
                {1, 4, 1},
                {2, 4, -1},
                {1, 4, -1},
                {2, 4, 1}
            },
            {//f
                {0, 4, -1},
                {1, 4, 1},
                {0, 4, 1},
                {1, 4, -1}
            }
        }, {// 1 dfr
            {//d
                {0, 4, 1}, // axis, layerMask, angle
                {2, 4, -1},
                {0, 4, -1},
                {2, 4, 1}
            },
            {//f
                {1, 1, -1}, // axis, layerMask, angle
                {0, 4, -1},
                {1, 1, 1},
                {0, 4, 1}
            },
            {//r
                {2, 4, -1}, // axis, layerMask, angle
                {1, 1, -1},
                {2, 4, 1},
                {1, 1, 1}
            }
        }, {// 2 ubr
            {//u
                {0, 4, 1}, // axis, layerMask, angle
                {2, 1, 1},
                {0, 4, -1},
                {2, 1, -1}
            },
            {//b
                {1, 4, 1}, // axis, layerMask, angle
                {0, 4, -1},
                {1, 4, -1},
                {0, 4, 1}
            },
            {//r
                {2, 1, 1}, // axis, layerMask, angle
                {1, 4, 1},
                {2, 1, -1},
                {1, 4, -1}
            }
        }, {// 3 drb
            {//d
                {2, 1, -1}, // axis, layerMask, angle
                {0, 4, -1},
                {2, 1, 1},
                {0, 4, 1}
            },
            {//r
                {1, 1, -1}, // axis, layerMask, angle
                {2, 1, 1},
                {1, 1, 1},
                {2, 1, -1}
            },
            {//b
                {0, 4, -1}, // axis, layerMask, angle
                {1, 1, -1},
                {0, 4, 1},
                {1, 1, 1}
            }
        }, {// 4 ulb
            {//u
                {2, 1, -1}, // axis, layerMask, angle
                {0, 1, 1},
                {2, 1, 1},
                {0, 1, -1}
            },
            {//l
                {1, 4, 1}, // axis, layerMask, angle
                {2, 1, 1},
                {1, 4, -1},
                {2, 1, -1}
            },
            {//b
                {0, 1, 1}, // axis, layerMask, angle
                {1, 4, 1},
                {0, 1, -1},
                {1, 4, -1}
            }
        }, {// 5 dbl
            {//d
                {0, 1, -1}, // axis, layerMask, angle
                {2, 1, 1},
                {0, 1, 1},
                {2, 1, -1}
            },
            {//b
                {1, 1, -1}, // axis, layerMask, angle
                {0, 1, 1},
                {1, 1, 1},
                {0, 1, -1}
            },
            {//l
                {2, 1, 1}, // axis, layerMask, angle
                {1, 1, -1},
                {2, 1, -1},
                {1, 1, 1}
            }
        }, {// 6 ufl
            {//u
                {0, 1, -1}, // axis, layerMask, angle
                {2, 4, -1},
                {0, 1, 1},
                {2, 4, 1}
            },
            {//f
                {1, 4, 1}, // axis, layerMask, angle
                {0, 1, 1},
                {1, 4, -1},
                {0, 1, -1}
            },
            {//l
                {2, 4, -1}, // axis, layerMask, angle
                {1, 4, 1},
                {2, 4, 1},
                {1, 4, -1}
            }
        }, {// 7 dlf
            {//d
                {2, 4, 1}, // axis, layerMask, angle
                {0, 1, 1},
                {2, 4, -1},
                {0, 1, -1}
            },
            {//l
                {1, 1, -1}, // axis, layerMask, angle
                {2, 4, -1},
                {1, 1, 1},
                {2, 4, 1}
            },
            {//f
                {0, 1, 1}, // axis, layerMask, angle
                {1, 1, -1},
                {0, 1, -1},
                {1, 1, 1}
            }
        }
    };

    /** Creates a new instance. */
    public AbstractCube() {
        this(3);
    }

    /**
     * Creates a new instance.
     * @param layerCount number of layers on the x, y and z axis.
     *
     * @throws IllegalArgumentException if the layour count is smaller than 2.
     */
    public AbstractCube(int layerCount) {
        if (layerCount < 2) {
            throw new IllegalArgumentException("layerCount: " + layerCount + " < 2");
        }
        this.layerCount = layerCount;

        cornerLoc = new int[8];
        cornerOrient = new int[8];

        if (layerCount > 2) {
            edgeLoc = new int[(layerCount - 2) * 12];
            edgeOrient = new int[edgeLoc.length];
            sideLoc = new int[(layerCount - 2) * (layerCount - 2) * 6];
            sideOrient = new int[sideLoc.length];
        } else {
            edgeLoc = edgeOrient = sideLoc = sideOrient = new int[0];
        }

        reset();
    }

    /**
     * Compares two cubes for equality.
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || !(o instanceof Cube)) {
            return false;
        } else {
            Cube that = (Cube) o;

            return that.getLayerCount() == this.layerCount && Arrays.equals(that.getCornerLocations(), this.cornerLoc) && Arrays.equals(that.getCornerOrientations(), this.cornerOrient) && Arrays.equals(that.getEdgeLocations(), this.edgeLoc) && Arrays.equals(that.getEdgeOrientations(), this.edgeOrient) && Arrays.equals(that.getSideLocations(), this.sideLoc) && Arrays.equals(that.getSideOrientations(), this.sideOrient);
        }
    }

    /**
     * Returns the hash code for the cube.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        int sub = 0;
        for (int i = 0; i < cornerLoc.length; i++) {
            sub = sub << 1 + cornerLoc[i];
        }
        hash |= sub;
        sub = 0;
        for (int i = 0; i < edgeLoc.length; i++) {
            sub = sub << 1 + edgeLoc[i];
        }
        hash |= sub;
        sub = 0;
        for (int i = 0; i < sideLoc.length; i++) {
            sub = sub << 1 + sideLoc[i];
        }
        return hash;
    }

    /**
     * Resets the cube to its initial (ordered) state.
     */
    @Override
    public void reset() {
        synchronized (this) {
            transformType = TransformType.IDENTITY;

            int i;
            for (i = 0; i < cornerLoc.length; i++) {
                cornerLoc[i] = i;
                cornerOrient[i] = 0;
            }

            for (i = 0; i < edgeLoc.length; i++) {
                edgeLoc[i] = i;
                edgeOrient[i] = 0;
            }

            for (i = 0; i < sideLoc.length; i++) {
                sideLoc[i] = i;
                sideOrient[i] = 0;
            }
        }
        fireCubeChanged(new CubeEvent(this, 0, 0, 0));
    }

    /**
     * Returns true if the cube is in its ordered (solved) state.
     */
    @Override
    public boolean isSolved() {
        int i;
        for (i = 0; i < cornerLoc.length; i++) {
            if (cornerLoc[i] != i) {
                return false;
            }
            if (cornerOrient[i] != 0) {
                return false;
            }
        }

        for (i = 0; i < edgeLoc.length; i++) {
            if (edgeLoc[i] != i) {
                return false;
            }
            if (edgeOrient[i] != 0) {
                return false;
            }
        }

        for (i = 0; i < sideLoc.length; i++) {
            if (sideLoc[i] != i) {
                return false;
            }
            if (sideOrient[i] != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Adds a listener for CubeEvent's.
     */
    @Override
    public void addCubeListener(CubeListener l) {
        listenerList.add(CubeListener.class, l);
    }

    /**
     * Removes a listener for CubeEvent's.
     */
    @Override
    public void removeCubeListener(CubeListener l) {
        listenerList.remove(CubeListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireCubeTwisted(CubeEvent event) {
        if (!isQuiet) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CubeListener.class) {
                    ((CubeListener) listeners[i + 1]).cubeTwisted(event);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireCubeChanged(CubeEvent event) {
        if (!isQuiet) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CubeListener.class) {
                    ((CubeListener) listeners[i + 1]).cubeChanged(event);
                }
            }
        }
    }

    /**
     * Set this to false to prevent notification of listeners.
     * Setting this to true will fire a cubeChanged event.
     */
    @Override
    public void setQuiet(boolean b) {
        if (b != isQuiet) {
            isQuiet = b;
            if (!isQuiet) {
                fireCubeChanged(new CubeEvent(this, 0, 0, 0));
            }
        }
    }

    public boolean isQuiet() {
        return isQuiet;
    }

    /**
     * Returns the locations of all corner parts.
     */
    @Override
    public int[] getCornerLocations() {
        return cornerLoc.clone();
    }

    /**
     * Returns the orientations of all corner parts.
     */
    @Override
    public int[] getCornerOrientations() {
        return cornerOrient.clone();
    }

    /**
     * Sets the locations and orientations of all corner parts.
     */
    @Override
    public void setCorners(@Nonnull int[] locations, @Nonnull int[] orientations) {
        synchronized (this) {
            transformType = TransformType.UNKNOWN;

            System.arraycopy(locations, 0, cornerLoc, 0, cornerLoc.length);
            System.arraycopy(orientations, 0, cornerOrient, 0, cornerOrient.length);
        }
        fireCubeChanged(new CubeEvent(this, 0, 0, 0));
    }

    /**
     * Gets the corner part at the specified location.
     */
    @Override
    public int getCornerAt(int location) {
        return cornerLoc[location];
    }

    /**
     * Gets the location of the specified corner part.
     */
    @Override
    public int getCornerLocation(int corner) {
        int i;
        if (cornerLoc[corner] == corner) {
            return corner;
        }
        for (i = cornerLoc.length - 1; i >= 0; i--) {
            if (cornerLoc[i] == corner) {
                break;
            }
        }
        return i;
    }

    /**
     * Returns the number of corner parts.
     */
    @Override
    public int getCornerCount() {
        return cornerLoc.length;
    }

    /**
     * Returns the number of edge parts.
     */
    @Override
    public int getEdgeCount() {
        return edgeLoc.length;
    }

    /**
     * Returns the number of side parts.
     */
    @Override
    public int getSideCount() {
        return sideLoc.length;
    }

    /**
     * Gets the orientation of the specified corner part.
     */
    @Override
    public int getCornerOrientation(int corner) {
        return cornerOrient[getCornerLocation(corner)];
    }

    /**
     * Returns the locations of all edge parts.
     */
    @Override
    public int[] getEdgeLocations() {
        return edgeLoc.clone();
    }

    /**
     * Returns the orientations of all edge parts.
     */
    @Override
    public int[] getEdgeOrientations() {
        return edgeOrient.clone();
    }

    /**
     * Sets the locations and orientations of all edge parts.
     */
    @Override
    public void setEdges(@Nonnull int[] locations, @Nonnull int[] orientations) {
        synchronized (this) {
            transformType = TransformType.UNKNOWN;
            System.arraycopy(locations, 0, edgeLoc, 0, edgeLoc.length);
            System.arraycopy(orientations, 0, edgeOrient, 0, edgeOrient.length);
        }
        fireCubeChanged(new CubeEvent(this, 0, 0, 0));
    }

    /**
     * Gets the edge part at the specified location.
     */
    @Override
    public int getEdgeAt(int location) {
        return edgeLoc[location];
    }

    /**
     * Gets the location of the specified edge part.
     */
    @Override
    public int getEdgeLocation(int edge) {
        int i;
        if (edgeLoc[edge] == edge) {
            return edge;
        }
        for (i = edgeLoc.length - 1; i >= 0; i--) {
            if (edgeLoc[i] == edge) {
                break;
            }
        }
        return i;
    }

    /**
     * Gets the orientation of the specified edge part.
     */
    @Override
    public int getEdgeOrientation(int edge) {
        return edgeOrient[getEdgeLocation(edge)];
    }

    /**
     * Returns the locations of all side parts.
     */
    @Override
    public int[] getSideLocations() {
        return sideLoc.clone();
    }

    /**
     * Returns the orientations of all side parts.
     */
    @Override
    public int[] getSideOrientations() {
        return sideOrient.clone();
    }

    /**
     * Sets the locations and orientations of all side parts.
     */
    @Override
    public void setSides(@Nonnull int[] locations, @Nonnull int[] orientations) {
        synchronized (this) {
            transformType = TransformType.UNKNOWN;
            System.arraycopy(locations, 0, sideLoc, 0, sideLoc.length);
            System.arraycopy(orientations, 0, sideOrient, 0, sideOrient.length);
        }
        fireCubeChanged(new CubeEvent(this, 0, 0, 0));
    }

    /**
     * Gets the side part at the specified location.
     */
    @Override
    public int getSideAt(int location) {
        return sideLoc[location];
    }

    /**
     * Gets the face on which the sticker of the specified side part can
     * be seen.
     */
    private int getSideFace(int sidePart) {
        return getSideLocation(sidePart) % 6;
    }

    /**
     * Gets the location of the specified side part.
     */
    @Override
    public int getSideLocation(int side) {
        int i;
        if (sideLoc[side] == side) {
            return side;
        }
        for (i = sideLoc.length - 1; i >= 0; i--) {
            if (sideLoc[i] == side) {
                break;
            }
        }
        return i;
    }

    /**
     * Gets the orientation of the specified side part.
     */
    @Override
    public int getSideOrientation(int side) {
        return sideOrient[getSideLocation(side)];
    }

    /**
     * Copies the permutation of the specified cube to this cube.
     *
     * @param tx The cube to be applied to this cube object.
     */
    @Override
    public void setTo(Cube tx) {
        Cube that = tx;
        if (that.getLayerCount() != this.getLayerCount()) {
            throw new IllegalArgumentException("that.layers=" + that.getLayerCount() + " must match this.layers=" + this.getLayerCount());
        }

        // Prevent deadlocks by always locking the object with the smaller
        // identity hashcode first.
        Object a, b;
        if (System.identityHashCode(this) < System.identityHashCode(that)) {
            a = this;
            b = that;
        } else {
            a = that;
            b = this;
        }

        synchronized (a) {
            synchronized (b) {
                if (that instanceof AbstractCube) {
                    this.transformType = ((AbstractCube) that).transformType;
                    this.transformAxis = ((AbstractCube) that).transformAxis;
                    this.transformAngle = ((AbstractCube) that).transformAngle;
                    this.transformMask = ((AbstractCube) that).transformMask;
                } else {
                    this.transformType = TransformType.UNKNOWN;
                }

                System.arraycopy(that.getSideLocations(), 0, sideLoc, 0, sideLoc.length);
                System.arraycopy(that.getSideOrientations(), 0, sideOrient, 0, sideOrient.length);
                System.arraycopy(that.getEdgeLocations(), 0, edgeLoc, 0, edgeLoc.length);
                System.arraycopy(that.getEdgeOrientations(), 0, edgeOrient, 0, edgeOrient.length);
                System.arraycopy(that.getCornerLocations(), 0, cornerLoc, 0, cornerLoc.length);
                System.arraycopy(that.getCornerOrientations(), 0, cornerOrient, 0, cornerOrient.length);
            }
        }
        fireCubeChanged(new CubeEvent(this, 0, 0, 0));
    }

    /**
     * Returns the number of layers on the x, y and z axis.
     */
    @Override
    public int getLayerCount() {
        return layerCount;
    }

    /**
     * Transforms the cube and fires a cubeTwisted event. The actual work
     * is done in method transform0.
     *
     * @param  axis  0=x, 1=y, 2=z axis.
     * @param  layerMask A bitmask specifying the layers to be transformed.
     *           The size of the layer mask depends on the value returned by
     *           <code>getLayerCount(axis)</code>. For a 3x3x3 cube, the layer mask has the
     *           following meaning:
     *           7=rotate the whole cube;<br>
     *           1=twist slice near the axis (left, bottom, behind)<br>
     *           2=twist slice in the middle of the axis<br>
     *           4=twist slice far away from the axis (right, top, front)
     * @param  angle  positive values=clockwise rotation<br>
     *                negative values=counterclockwise rotation<br>
     *               1=90 degrees<br>
     *               2=180 degrees
     *
     * @see #getLayerCount()
     */
    @Override
    public final void transform(int axis, int layerMask, int angle) {
        // Update transform type
        synchronized (this) {
            switch (transformType) {
                case IDENTITY:
                    transformAxis = axis;
                    transformMask = layerMask;
                    transformAngle = angle;
                    transformType = TransformType.SINGLE_AXIS_TRANSFORM;
                    break;
                case SINGLE_AXIS_TRANSFORM:
                    if (transformAxis == axis) {
                        if (transformAngle == angle) {
                            if (transformMask == layerMask) {
                                transformAngle = (transformAngle + angle) % 3;
                            } else if ((transformMask & layerMask) == 0) {
                                transformMask |= layerMask;
                            } else {
                                transformType = TransformType.GENERAL_TRANSFORM;
                            }
                        } else {
                            if (transformMask == layerMask) {
                                transformAngle = (transformAngle + angle) % 3;
                            } else {
                                transformType = TransformType.GENERAL_TRANSFORM;
                            }
                        }
                    } else {
                        transformType = TransformType.GENERAL_TRANSFORM;
                    }
                    break;
            }

            // Perform the transform
            transform0(axis, layerMask, angle);
        }

        // Inform listeners.
        if (!isQuiet()) {
            fireCubeTwisted(new CubeEvent(this, axis, layerMask, angle));
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
     *           7=rotate the whole cube;<br>
     *           1=twist slice near the axis (left, bottom, behind)<br>
     *           2=twist slice in the middle of the axis<br>
     *           4=twist slice far away from the axis (right, top, front)
     * @param  angle  positive values=clockwise rotation<br>
     *                negative values=counterclockwise rotation<br>
     *               1=90 degrees<br>
     *               2=180 degrees
     *
     * @see #getLayerCount()
     */
    protected abstract void transform0(int axis, int layerMask, int angle);

    /**
     * Applies the permutation of the specified cube to this cube and fires a
     * cubeChanged event.
     *
     * @param tx The cube to be used to transform this cube object.
     * @throws IllegalArgumentException if one or more of the values returned
     * by <code>tx.getLayourCount(axis)</code> are different from this cube.
     *
     * @see #getLayerCount()
     */
    @Override
    public void transform(@Nonnull Cube tx) {
        if (tx.getLayerCount() != this.getLayerCount()) {
            throw new IllegalArgumentException("tx.layers=" + tx.getLayerCount() + " must match this.layers=" + this.getLayerCount());
        }

        // Prevent deadlocks by always locking the object with the smaller
        // identity hashcode first.
        Object a, b;
        if (System.identityHashCode(this) < System.identityHashCode(tx)) {
            a = this;
            b = tx;
        } else {
            a = tx;
            b = this;
        }

        int taxis = 0, tangle = 0, tmask = 0;
        synchronized (a) {
            synchronized (b) {
                if (tx instanceof AbstractCube) {
                    AbstractCube atx = (AbstractCube) tx;
                    switch (atx.transformType) {
                        case IDENTITY:
                            return; // nothing to do
                        case SINGLE_AXIS_TRANSFORM:
                            taxis = atx.transformAxis;
                            tangle = atx.transformAngle;
                            tmask = atx.transformMask;
                            break;
                    }
                }

                if (tmask == 0) {
                    transformType = TransformType.UNKNOWN;
                    int[] tempLoc;
                    int[] tempOrient;

                    tempLoc = cornerLoc.clone();
                    tempOrient = cornerOrient.clone();
                    int[] txLoc = tx.getCornerLocations();
                    int[] txOrient = tx.getCornerOrientations();
                    for (int i = 0; i < txLoc.length; i++) {
                        cornerLoc[i] = tempLoc[txLoc[i]];
                        cornerOrient[i] = (tempOrient[txLoc[i]] + txOrient[i]) % 3;
                    }

                    tempLoc = edgeLoc.clone();
                    tempOrient = edgeOrient.clone();
                    txLoc = tx.getEdgeLocations();
                    txOrient = tx.getEdgeOrientations();
                    for (int i = 0; i < txLoc.length; i++) {
                        edgeLoc[i] = tempLoc[txLoc[i]];
                        edgeOrient[i] = (tempOrient[txLoc[i]] + txOrient[i]) % 2;
                    }

                    tempLoc = sideLoc.clone();
                    tempOrient = sideOrient.clone();
                    txLoc = tx.getSideLocations();
                    txOrient = tx.getSideOrientations();
                    for (int i = 0; i < txLoc.length; i++) {
                        sideLoc[i] = tempLoc[txLoc[i]];
                        sideOrient[i] = (tempOrient[txLoc[i]] + txOrient[i]) % 4;
                    }
                }
            }
        }
        if (tmask == 0) {
            fireCubeChanged(new CubeEvent(this, 0, 0, 0));
        } else {
            transform(taxis, tmask, tangle);
        }
    }

    /**
     * Performs a two cycle permutation and orientation change.
     */
    protected void twoCycle(
            int[] loc, int l1, int l2,
            int[] orient, int o1, int o2,
            int modulo) {
        int swap;

        swap = loc[l1];
        loc[l1] = loc[l2];
        loc[l2] = swap;

        swap = orient[l1];
        orient[l1] = (orient[l2] + o1) % modulo;
        orient[l2] = (swap + o2) % modulo;
    }

    /**
     * Performs a four cycle permutation and orientation change.
     */
    protected void fourCycle(
            int[] loc, int l1, int l2, int l3, int l4,
            int[] orient, int o1, int o2, int o3, int o4,
            int modulo) {
        int swap;

        swap = loc[l1];
        loc[l1] = loc[l2];
        loc[l2] = loc[l3];
        loc[l3] = loc[l4];
        loc[l4] = swap;

        swap = orient[l1];
        orient[l1] = (orient[l2] + o1) % modulo;
        orient[l2] = (orient[l3] + o2) % modulo;
        orient[l3] = (orient[l4] + o3) % modulo;
        orient[l4] = (swap + o4) % modulo;
    }

    /**
     * Returns the face at which the indicated orientation
     * of the part is located.
     */
    @Override
    public int getPartFace(int part, int orient) {
        synchronized (this) {
            if (part < cornerLoc.length) {
                return getCornerFace(part, orient);
            } else if (part < cornerLoc.length + edgeLoc.length) {
                return getEdgeFace(part - cornerLoc.length, orient);
            } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
                return getSideFace(part - cornerLoc.length - edgeLoc.length);
            } else {
                return getCenterSide(orient);
            }
        }
    }

    /**
     * Returns the orientation of the specified part.
     */
    @Override
    public int getPartOrientation(int part) {
        if (part < cornerLoc.length) {
            return getCornerOrientation(part);
        } else if (part < cornerLoc.length + edgeLoc.length) {
            return getEdgeOrientation(part - cornerLoc.length);
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return getSideOrientation(part - cornerLoc.length - edgeLoc.length);
        } else {
            return getCubeOrientation();
        }
    }

    /**
     * Returns the location of the specified part.
     */
    @Override
    public int getPartLocation(int part) {
        if (part < cornerLoc.length) {
            return getCornerLocation(part);
        } else if (part < cornerLoc.length + edgeLoc.length) {
            return cornerLoc.length + getEdgeLocation(part - cornerLoc.length);
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return cornerLoc.length + edgeLoc.length + getSideLocation(part - cornerLoc.length - edgeLoc.length);
        } else {
            return 0;
        }
    }

    /**
     * Returns the current axis on which the orientation of the part lies.
     * Returns -1 if the part lies on none or multiple axis (the center part).
     */
    @Override
    public int getPartAxis(int part, int orientation) {
        if (part < cornerLoc.length) {
            // Corner parts
            int face = getPartFace(part, orientation);
            return (face) % 3;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            // Edge parts
            return EDGE_TO_AXIS_MAP[getEdgeLocation(part - cornerLoc.length) % 12];
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            // Side parts
            int face = getPartFace(part, orientation);
            return (face) % 3;
        } else {
            return -1;
        }
    }

    /**
     * Returns the angle which is clockwise for the specified part orientation.
     * Returns 1 or -1.
     * Returns 0 if the direction can not be determined (the center part).
     */
    @Override
    public int getPartAngle(int part, int orientation) {
        if (part >= cornerLoc.length && part < cornerLoc.length + edgeLoc.length) {
            // Edge parts
            return EDGE_TO_ANGLE_MAP[getEdgeLocation(part - cornerLoc.length) % 12][(getEdgeOrientation(part - cornerLoc.length) + orientation) % 2];
        } else {
            // Corner parts and Side parts
            int side = getPartFace(part, orientation);
            switch (side) {
                case 0:
                case 1:
                case 2:
                    return 1;
                case 3:
                case 4:
                case 5:
                default:
                    return -1;
            }
        }
    }

    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     */
    @Override
    public abstract int getPartLayerMask(int part, int orientation);

    /**
     * Returns the type of the specified part.
     */
    @Override
    public int getPartType(int part) {
        if (part < cornerLoc.length) {
            return CORNER_PART;
        } else if (part < cornerLoc.length + edgeLoc.length) {
            return EDGE_PART;
        } else if (part < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return SIDE_PART;
        } else {
            return CENTER_PART;
        }
    }

    /**
     * Returns the location of the specified part.
     */
    @Override
    public int getPartAt(int location) {
        if (location < cornerLoc.length) {
            return getCornerAt(location);
        } else if (location < cornerLoc.length + edgeLoc.length) {
            return cornerLoc.length + getEdgeAt(location - cornerLoc.length);
        } else if (location < cornerLoc.length + edgeLoc.length + sideLoc.length) {
            return cornerLoc.length + edgeLoc.length + getSideAt(location - cornerLoc.length - edgeLoc.length);
        } else {
            return cornerLoc.length + edgeLoc.length + sideLoc.length;
        }
    }

    /**
     * Returns the side at which the indicated orientation
     * of the center part is located.
     *
     * @return The side. A value ranging from 0 to 5.
     * <code><pre>
     *     +---+
     *     | 5 |
     * +---+---+---+---+
     * | 4 | 0 | 1 | 3 |
     * +---+---+---+---+
     *     | 2 |
     *     +---+
     * </pre></code>
     */
    private int getCenterSide(int orient) {
        return CENTER_TO_SIDE_MAP[getCubeOrientation()][orient];
    }

    /**
     * Returns the face an which the sticker at the specified orientation
     * of the edge can be seen.
     */
    private int getEdgeFace(int edge, int orient) {
        int loc = getEdgeLocation(edge) % 12;
        int ori = (edgeOrient[loc] + orient) % 2;

        return EDGE_TO_FACE_MAP[loc][ori];
    }

    /**
     * Returns the face on which the sticker at the specified orientation
     * of the corner can be seen.
     */
    private int getCornerFace(int corner, int orient) {
        int loc = getCornerLocation(corner);
        int ori = (3 + orient - cornerOrient[loc]) % 3;
        return CORNER_TO_FACE_MAP[loc][ori];
    }

    /**
     * Returns the orientation of the whole cube.
     * @return The orientation of the cube, or -1 if
     * the orientation can not be determined.
     */
    @Override
    public int getCubeOrientation() {
        // The cube has no orientation, if it has no side parts.
        if (sideLoc.length == 0) {
            return -1;
        }

        // The location of the front side and the right
        // side are used to determine the orientation
        // of the cube.
        switch (sideLoc[2] * 6 + sideLoc[0]) {
            case 2 * 6 + 0:
                return 0; // Front at front, Right at right
            case 4 * 6 + 0:
                return 1; // Front at Bottom, Right at right, CR
            case 5 * 6 + 0:
                return 2; // Back, Right, CR2
            case 1 * 6 + 0:
                return 3; // Top, Right, CR'
            case 0 * 6 + 5:
                return 4; // Right, Back, CU
            case 5 * 6 + 3:
                return 5; // Back, Left, CU2
            case 3 * 6 + 2:
                return 6; // Left, Front, CU'
            case 2 * 6 + 1:
                return 7; // Front, Top, CF
            case 2 * 6 + 3:
                return 8; // Front, Left, CF2
            case 2 * 6 + 4:
                return 9; // Front, Bottom, CF'
            case 0 * 6 + 1:
                return 10; // Right, Top, CR CU
            case 1 * 6 + 3:
                return 11; // Top, Left, CR CU2
            case 3 * 6 + 4:
                return 12; // Left, Down, CR CU'
            case 0 * 6 + 2:
                return 13; // Right, Front, CR2 CU
            case 3 * 6 + 5:
                return 14; // Left, Back, CR2 CU'
            case 0 * 6 + 4:
                return 15; // Right, Down, CR' CU
            case 4 * 6 + 3:
                return 16; // Down, Left, CR' CU2
            case 3 * 6 + 1:
                return 17; // Left, Up, CR' CU'
            case 4 * 6 + 1:
                return 18; // Down, Up, CR CF
            case 4 * 6 + 5:
                return 19; // Down, Back, CR CF'
            case 5 * 6 + 4:
                return 20; // Back, Down, CR2 CF
            case 5 * 6 + 1:
                return 21; // Back, Up, CR2 CF'
            case 1 * 6 + 5:
                return 22; // Up, Back, CR' CF
            case 1 * 6 + 2:
                return 23; // Up, Front, CR' CF'
            default:
                return -1;
        }
    }

    @Nonnull
    @Override
    public Object clone() {
        try {
            AbstractCube that = (AbstractCube) super.clone();
            that.listenerList = new EventListenerList();
            if (this.cornerLoc != null) {
                that.cornerLoc = this.cornerLoc.clone();
            }
            if (this.cornerOrient != null) {
                that.cornerOrient = this.cornerOrient.clone();
            }
            if (this.edgeLoc != null) {
                that.edgeLoc = this.edgeLoc.clone();
            }
            if (this.edgeOrient != null) {
                that.edgeOrient = this.edgeOrient.clone();
            }
            if (this.sideLoc != null) {
                that.sideLoc = this.sideLoc.clone();
            }
            if (this.sideOrient != null) {
                that.sideOrient = this.sideOrient.clone();
            }
            return that;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public int getPartCount() {
        return getCornerCount() + getEdgeCount() + getSideCount() + 1;
    }

    /**
     * Returns an array of part ID's, for each part in this cube,
     * which is not at its initial location or has not its initial
     * orientation.
     */
    @Nonnull
    @Override
    public int[] getUnsolvedParts() {
        int[] a = new int[cornerLoc.length + edgeLoc.length + sideLoc.length];
        int count = 0;
        for (int i = 0; i < cornerLoc.length; i++) {
            if (cornerLoc[i] != i || cornerOrient[i] != 0) {
                a[count++] = i;
            }
        }
        for (int i = 0; i < edgeLoc.length; i++) {
            if (edgeLoc[i] != i || edgeOrient[i] != 0) {
                a[count++] = i + cornerLoc.length;
            }
        }
        for (int i = 0; i < sideLoc.length; i++) {
            if (sideLoc[i] != i || sideOrient[i] != 0) {
                a[count++] = i + cornerLoc.length + edgeLoc.length;
            }
        }
        int[] result = new int[count];
        System.arraycopy(a, 0, result, 0, count);
        return result;
    }
}
