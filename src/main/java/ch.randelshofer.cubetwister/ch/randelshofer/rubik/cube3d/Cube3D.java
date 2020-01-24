/* @(#)Cube3D.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube3d;

import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.util.Dispatcher;
import org.monte.media.player.Animator;

import javax.swing.event.ChangeListener;
/**
 * The interface for objects which represent the three dimensional geometry of
 * a Rubik's Cube-like puzzle.
 *
 * @author Werner Randelshofer.
 */
public interface Cube3D {

    int getPartCount();

    int getStickerCount();

    Object getScene();

    /**
     * Returns the lock object used for synchronizing model and view changes.
     */
    Object getLock();

    void setAnimator(Animator animator);

    Animator getAnimator();

    /**
     * Sets the lock object used for synchronizing model and view changes.
     */
    void setLock(Object o);

    /**
     * Sets the dispatcher used to process animations of the 3D geometry.
     */
    void setDispatcher(Dispatcher dispatcher);

    /**
     * Gets the dispatcher used to process animations of the 3D geometry.
     */
    Dispatcher getDispatcher();


    /**
     * Sets whether changes in the permutation model shall cause an animated
     * (multi-frame) change in the 3D geometry or whether they shall be
     * reflected immediately.
     */
    void setAnimated(boolean b);

    /**
     * Returns true when the 3D geometry animates permutation changes.
     */
    boolean isAnimated();

    /**
     * Stops all running animations.
     */
    void stopAnimation();
    
    /**
     * Sets the underlying permutation model.
     */
    void setCube(Cube cube);

    /**
     * Gets the underlying permutation model.
     */
    Cube getCube();

    /**
     * Gets the kind of the cube.
     */
    CubeKind getKind();

    /**
     * Sets cube attributees.
     */
    void setAttributes(CubeAttributes attributes);

    /**
     * Gets cube attributees.
     */
    CubeAttributes getAttributes();

    /**
     * Adds a change listener. The change listener is notified about geometry
     * changes. This is useful for a 3D canvas interested to know when to repaint.
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes a change listener.
     */
    void removeChangeListener(ChangeListener listener);

    void addCube3DListener(Cube3DListener listener);

    void removeCube3DListener(Cube3DListener listener);

    /** Returns true if the cube is currently performing an animation. */
    boolean isAnimating();

    /** Returns true if the cube is in a started script player. */
    boolean isInStartedPlayer();

    /**
     * This is set to true by the script player if it is started.
     */
    void setInStartedPlayer(boolean newValue);

    boolean isShowGhostParts();

    void setShowGhostParts(boolean b);

    void dispose();

    int getPartIndexForStickerIndex(int i);

    void setStickerBeveling(float newValue);
}
