/* @(#)Cube.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube;

/**
 * The interface for objects which represent the state of a Rubik's Cube-like
 * puzzle by describing the orientation and location of its parts.
 * <p>
 * A cube consists of corner parts, edge parts, side parts and a center part.
 * Each part has an individual location and orientation. Parts can permute their
 * location with other parts of the same type. A corner part can assume three
 * different orientations, an edge part can assume two, a side part four, and
 * the center part can assume 24 orientations.
 * <p>
 * The parts are arranged in layers. The layers are oriented on the X-, Y- and
 * Z-axis. The number of layers is the same for all axes. The axes have at least
 * two layers and can have up to 32 layers.
 *
 * @author Werner Randelshofer.
 */
public interface Cube extends Cloneable {
    /**
     * Resets the cube to its solved state.
     * <p>
     * If the cube was not in its solved state, this method fires a cubeChanged
     * event.
     *
     * @see #isSolved
     */
    public void reset();
    /**
     * Returns true if the cube is in its solved state.
     * <p>
     * In the solved state of the cube, each part <i>i</i> is at location <i>i</i>
     * and has orientation 0.
     */
    public boolean isSolved();
    
    /**
     * Sets the orientations and locations of the parts of this Cube to the 
     * same values as in the specified Cube.
     * <p>
     * If this cube was different from the specified cube, this method fires a 
     * cubeChanged event.
     *
     * @param tx The cube to be set to.
     * @throws IllegalArgumentException if the specified cube has not the same
     * layer count like this cube.
     */
    public void setTo(Cube tx);
    
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
    public abstract void transform(int axis, int layerMask, int angle);
    /**
     * Applies the permutation of the specified cube to this cube and fires a
     * cubeChanged event.
     *
     * @param that The cube to be applied to this cube object.
     * @throws IllegalArgumentException if the specified cube has not the same
     * layer count like this cube.
     *
     * @see #getLayerCount()
     */
    public void transform(Cube that);

    /**
     * Compares the Cube with another Cube for equality.<p>
     * Two Cubes are equal, if they have the same number of layers, and if
     * all their parts have the same orientations and locations.
     *
     * @return Returns true if the Cube is equal.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object o);
    /**
     * Returns the hash code for the cube.
     * Cubes which are equal must return the same hash code.
     *
     * @see #equals
     */
    @Override
    public int hashCode();
    
    
    /**
     * Adds a listener for CubeEvent's.
     */
    public void addCubeListener(CubeListener l);
    /**
     * Removes a listener for CubeEvent's.
     */
    public void removeCubeListener(CubeListener l);     
    
    /**
     * Turns listener notification off.
     * Setting this to false fires a cubeChanged event.
     */
    public void setQuiet(boolean b);
    
    /**
     * Returns the number of layers on the x, y and z axis.
     * @return A value from 2 through 32.
     */
    public int getLayerCount();
    /**
     * Returns the number of corner parts.
     * @return Always returns 8.
     */
    public int getCornerCount();
    /**
     * Returns the locations of the corner parts. This array must be treated
     * as read only. 
     * @return An array with a permutation of the numbers 0 through
     * getCornerCount() - 1.
     */
    public int[] getCornerLocations();
    /**
     * Returns the orientations of the corner parts. This array must be treated
     * as read only. 
     * @return An array of orientations. The array elements can have the values
     * 0, 1, and 2.
     */
    public int[] getCornerOrientations();
    /**
     * Sets the locations and orientations of the corner parts.
     * @param locations An array with a permutaton of the numbers 0 through
     * getCornerCount() - 1.
     * @param orientations An array with only the values 0, 1 and 2. The length
     * of the array must be getCornerCount().
     */
    public void setCorners(int[] locations, int[] orientations);
    /**
     * Gets the corner part at the specified location.
     * @return A value in the range from 0 through getCornerCount() - 1.
     */
    public int getCornerAt(int location);
    /**
     * Gets the location of the specified corner part.
     * @return A value in the range from 0 through getCornerCount() - 1.
     */
    public int getCornerLocation(int corner);
    /**
     * Gets the orientation of the specified corner part.
     * @return 0, 1 or 2.
     */
    public int getCornerOrientation(int corner);
    
    /**
     * Returns the number of edge parts.
     * @return 0 for a cube with 2 layers, 12 for a cube with 3 layers,
     * 24 for a cube with 4 layers, ... .
     */
    public int getEdgeCount();
    /**
     * Returns the locations of the edge parts. This array must be treated
     * as read only. 
     * @return An array with a permutation of the numbers 0 through
     * getEdgeCount() - 1.
     */
    public int[] getEdgeLocations();
    /**
     * Returns the orientations of the edge parts. This array must be treated
     * as read only. 
     * @return An array of orientations. The array elements can have the values
     * 0, and 1.
     */
    public int[] getEdgeOrientations();
    /**
     * Sets the locations and orientations of the edge parts.
     * @param locations An array with a permutaton of the numbers 0 through
     * getEdgeCount() - 1.
     * @param orientations An array with only the values 0, and 1. The length
     * of the array must be getEdgeCount().
     */
    public void setEdges(int[] locations, int[] orientations);
    /**
     * Gets the edge part at the specified location.
     * @return A value in the range from 0 through getEdgeCount() - 1.
     */
    public int getEdgeAt(int location);
    /**
     * Gets the location of the specified edge part.
     * @return A value in the range from 0 through getEdgeCount() - 1.
     */
    public int getEdgeLocation(int edge);
    /**
     * Gets the orientation of the specified edge part.
     * @return 0 or 1.
     */
    public int getEdgeOrientation(int edge);

    /**
     * Returns the number of side parts.
     * @return 0 for a cube with 2 layers, 6 for a cube with 3 layers,
     * 24 for a cube with 4 layers, ... .
     */
    public int getSideCount();
    /**
     * Returns the locations of the side parts.  This array must be treated
     * as read only. 
     * @return An array with a permutation of the numbers 0 through
     * getSideCount() - 1.
     */
    public int[] getSideLocations();
    /**
     * Returns the orientations of the side parts. This array must be treated
     * as read only. 
     * @return An array of orientations. The array elements can have the values
     * 0, 1, 2 and 3.
     */
    public int[] getSideOrientations();
    /**
     * Sets the locations and orientations of the side parts.
     * @param locations An array with a permutaton of the numbers 0 through
     * getSideCount() - 1.
     * @param orientations An array with only the values 0, 1, 2, and 3. The length
     * of the array must be getSideCount().
     */
    public void setSides(int[] locations, int[] orientations);
    /**
     * Gets the side part at the specified location.
     * @return A value in the range from 0 through getSideCount() - 1.
     */
    public int getSideAt(int location);
    /**
     * Gets the location of the specified side part.
     * @return A value in the range from 0 through getSideCount() - 1.
     */
    public int getSideLocation(int side);
    /**
     * Gets the orientation of the specified side part.
     * @return 0, 1, 2 or 3.
     */
    public int getSideOrientation(int side);
    
    
    /**
     * Returns the side at which the indicated orientation
     * of the part is located.
     * @return A value in the range from 0 through 5.
     */
    public int getPartFace(int part, int orient);
    /**
     * Returns the orientation of the specified part.
     * @return Returns 0..2 for a corner part, 0..1 for an edge part, 0..3 for
     * a side part, 0..24 for the center part.
     */
    public int getPartOrientation(int part);
    /**
     * Returns the location of the specified part.
     * @return Returns the location for a corner part, location + getCornerPartCount()
     * for an edge part, location + getCornerPartCount() + getEdgePartCount() for
     * a side part, getPartCount() - 1 for the center part.
     */
    public int getPartLocation(int part);
    
    /**
     * Returns the current axis on which the orientation of the part lies.
     * @return 0 for the x-axis, 1 for the y-axis, 2 for the z-axis.
     * -1 if the part lies on none or multiple axis (the center part).
     */
    public int getPartAxis(int part, int orientation);
    /**
     * Returns the current layer mask on which the orientation of the part lies.
     * Returns 0 if no mask can be determined (the center part).
     * @return 2^layer number
     */
    public int getPartLayerMask(int part, int orientation);
     /**
     * Returns the angle which is clockwise for the specified part orientation.
     * Returns 1 or -1.
     * Returns 0 if the direction can not be determined (the center part).
     */
    public int getPartAngle(int part, int orientation);
   /**
     * Returns the  axis on which the orientation of the part can be swiped
     * into the specified direction.
     *
     * @param part The part index.
     * @param orientation The orientation of the part where swiping is performed.
     * @param swipeDirection The direction of the swipe. 0=up,1=right,2=down,4=left.
     *
     * @return 0 for the x-axis, 1 for the y-axis, 2 for the z-axis.
     * -1 if the part lies on none or multiple axis (the center part).
     */
    public int getPartSwipeAxis(int part, int orientation, int swipeDirection);
    /**
     * Returns the layer mask on which the orientation of the part can be
     * swiped into the specified direction.
     *
     * Returns 0 if no mask can be determined (the center part).
     *
     * @param part The part index.
     * @param orientation The orientation of the part where swiping is performed.
     * @param swipeDirection The direction of the swipe. 0=up,1=right,2=down,4=left.
     *
     * @return 2^layer number
     */
    public int getPartSwipeLayerMask(int part, int orientation, int swipeDirection);
   /**
     * Returns the angle on which the orientation of the part can be swiped
     * into the specified direction.
     *
     * @param part The part index.
     * @param orientation The orientation of the part where swiping is performed.
     * @param swipeDirection The direction of the swipe. 0=up,1=right,2=down,4=left.
     *
     * @return Returns 1 or -1. Returns 0 if the direction can not be determined
    *  (the center part).
     */
    public int getPartSwipeAngle(int part, int orientation, int swipeDirection);
    
    /**
     * Returns the type of the specified part.
     * @return 0 for corner parts, 1 for edge parts, 2 for side parts, 3 for the
     * center part.
     */
    public int getPartType(int part);
    /**
     * Returns the location of the specified part.
     */
    public int getPartAt(int location);
    /**
     * Returns the orientation of the whole cube.
     * FIXME - Replace this by getCenterOrientation.
     * @return The orientation of the cube, or -1 if
     * the orientation can not be determined.
     */
    public int getCubeOrientation();
    
    /**
     * Clones the cube.
     */
    public Object clone();

    /**
     * Returns the number of cube parts.
     */
    public int getPartCount();

    /**
     * Returns an array of part ID's, for each part in this cube,
     * which is not at its initial location or has not its initial
     * orientation.
     */
    public int[] getUnsolvedParts();
    
    /**
     * Converts the cube into a stickers array reflecting the current 
     * permutation of the cube.
     *
     * @return  Array of stickers: int[6][getLayerCount()*getLayerCount()]. 
     * Same structure as in method setStickers().
     *
     * @see  #setToStickers
     */
    public int[][] toStickers();
    
    /**
     * Sets the cube to a state where the faces of the parts map to the
     * provided stickers array.
     *
     * @param  stickers Sticker array to be reused. The method creates a new
     *   array if this parameter is null.
     */
    public void setToStickers(int[][] stickers);
}
