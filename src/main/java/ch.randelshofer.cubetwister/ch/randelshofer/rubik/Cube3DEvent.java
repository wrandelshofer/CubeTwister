/* @(#)Cube3DEvent.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.gui.event.SwipeEvent;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * Cube3DEvent is used to notify interested parties that an event has occured
 * in a Cube3D object.
 * 
 * @author Werner Randelshofer
 */
public class Cube3DEvent extends java.util.EventObject {
    private final static long serialVersionUID = 1L;

    /**
     * The index of the cube part. Values 0..7 are
     * corners, 8..19 edges, 20..25 sides, 26 center.
     */
    private int partIndex;
    /**
     * The orientation of the face on the part.
     * -1 if not applicable.
     */
    private int orientation;
    /**
     * The side of the cube. Values 0..5 = Front,
     * Right, Bottom, Back, Left, Top. Or -1 if the
     * side can not be determined.
     */
    private int sideIndex;
    /**
     * The sticker index or -1 if there is no sticker
     * on the face of the part where the mouse event
     * occured.
     */
    private int stickerIndex;
    /**
     * Modifiers of the mouse event. Use them only, if mouseEvent is null!
     */
    private int modifiersEx;
    @Nullable
    private MouseEvent mouseEvent;
    /** A value between 0 and 3 if the part was scraped. Value -1 if the part
     * was clicked.
     */
    private int swipeDirection;

    /**
     * Creates new Cube3DEvent
     * @param source The object on which the event initially occured.
     * @param partIndex The index of the cube part. Values 0..7 are
     *   corners, 8..19 edges, 20..25 sides, 26 center.
     * @param orientation The orientation of the face on the part.
     *   -1 if not applicable.
     * @param sideIndex The side of the cube. Values 0..5 = Front,
     *    Right, Bottom, Back, Left, Top. Or -1 if the side can not
     *    be determined.
     * @param stickerIndex The sticker index or -1 if there is no
     *    sticker on the face of the part where the mouse event
     *    occured.
     * @param mouseEvent The mouse event.
     */
    /**
     * Creates a new instance.
     */
    public Cube3DEvent(@Nonnull Cube3D source,
                       int partIndex, int orientation, int sideIndex, int stickerIndex,
                       @Nonnull MouseEvent mouseEvent) {
        this(source, partIndex, orientation, sideIndex, stickerIndex, -1, mouseEvent);
    }

    public Cube3DEvent(@Nonnull Cube3D source,
                       int partIndex, int orientation, int sideIndex, int stickerIndex,
                       int scraping,
                       @Nonnull MouseEvent mouseEvent) {
        super(source);
        this.partIndex = partIndex;
        this.orientation = orientation;
        this.sideIndex = sideIndex;
        this.stickerIndex = stickerIndex;
        this.mouseEvent = mouseEvent;
        this.swipeDirection = scraping;
        this.modifiersEx = mouseEvent.getModifiersEx();
    }

    /**
     * Creates new Cube3DEvent
     * @param source The object on which the event initially occured.
     * @param partIndex The index of the cube part. Values 0..7 are
     *   corners, 8..19 edges, 20..25 sides, 26 center.
     * @param orientation The orientation of the face on the part.
     *   -1 if not applicable.
     * @param sideIndex The side of the cube. Values 0..5 = Front,
     *    Right, Bottom, Back, Left, Top. Or -1 if the side can not
     *    be determined.
     * @param stickerIndex The sticker index or -1 if there is no
     *    sticker on the face of the part where the mouse event
     *    occured.
     * @param mouseEvent The mouse event.
     */
    /**
     * Creates a new instance.
     */
    public Cube3DEvent(@Nonnull Cube3D source,
                       int partIndex, int orientation, int sideIndex, int stickerIndex,
                       int modifiersEx) {
        this(source, partIndex, orientation, sideIndex, stickerIndex, -1, modifiersEx);

    }

    public Cube3DEvent(@Nonnull Cube3D source,
                       int partIndex, int orientation, int sideIndex, int stickerIndex,
                       int scraping,
                       int modifiersEx) {
        super(source);
        this.partIndex = partIndex;
        this.orientation = orientation;
        this.sideIndex = sideIndex;
        this.stickerIndex = stickerIndex;
        this.mouseEvent = null;
        this.swipeDirection = scraping;
        this.modifiersEx = modifiersEx;
    }

    @Nonnull
    public Cube3D getCube3D() {
        return (Cube3D) getSource();
    }

    public Cube getCube() {
        return getCube3D().getCube();
    }

    public boolean isInverse() {
        if ((modifiersEx & InputEvent.BUTTON1_DOWN_MASK) == 0) {
            return false;
        }
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int relevantModifiersEx = (isMac) ? //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        return (mouseEvent instanceof SwipeEvent)//
                ? false //
                : (relevantModifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK)) != 0;
    }

    public boolean isDoubleAngle() {
        if ((modifiersEx & InputEvent.BUTTON1_DOWN_MASK) == 0) {
            return false;
        }
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int relevantModifiersEx = (isMac) ? //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        return (mouseEvent instanceof SwipeEvent)//
                ? (relevantModifiersEx & (InputEvent.SHIFT_DOWN_MASK)) != 0 //
                : (relevantModifiersEx & (InputEvent.SHIFT_DOWN_MASK)) != 0;
    }

    public boolean isDoubleLayer() {
        if ((modifiersEx & InputEvent.BUTTON1_DOWN_MASK) == 0) {
            return false;
        }
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int relevantModifiersEx = (isMac) ? //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        return (mouseEvent instanceof SwipeEvent)//
                ? (relevantModifiersEx & (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0 //
                : (relevantModifiersEx & (InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK)) != 0;
    }

    public int getAxis() {
        int axis = 0;
        Cube cube = getCube();
        if (swipeDirection == -1 && orientation == -1) {
            axis = 0; // should be no axis at all
        } else if (swipeDirection == -1) {
            axis = cube.getPartAxis(partIndex, orientation);
        } else {
            axis = cube.getPartSwipeAxis(partIndex, orientation, swipeDirection);
        }
        return axis;
    }

    public int getLayerMask() {
        int layerMask = 0;
        boolean doubleLayer = isDoubleLayer();
        Cube cube = getCube();
        if (swipeDirection == -1 && orientation == -1) {
            layerMask = 0; // nothing happens
        } else if (swipeDirection == -1) {
            layerMask = cube.getPartLayerMask(partIndex, orientation);
            if (doubleLayer) {
                if (layerMask == 1 || layerMask == 1 << (cube.getLayerCount() - 1)) {
                    layerMask |= (layerMask << 1) | (layerMask >>> 1);
                    layerMask &= (1 << cube.getLayerCount()) - 1;
                } else {
                    layerMask = ((1 << (cube.getLayerCount() - 2)) - 1) << 1;
                }
            }
        } else {
            layerMask = cube.getPartSwipeLayerMask(partIndex, orientation, swipeDirection);
            if (doubleLayer) {
                if (cube.getLayerCount() % 2 == 1
                        && layerMask == 1 << (cube.getLayerCount() / 2)) {
                    layerMask = ((1 << (cube.getLayerCount() - 2)) - 1) << 1;
                } else if (layerMask < cube.getLayerCount()) {
                    layerMask |= layerMask << 1;
                } else {
                    layerMask |= layerMask >>> 1;
                }
                layerMask &= (1 << (cube.getLayerCount())) - 1;
            }
        }
        return layerMask;
    }
    public int getAngle() {
        int angle = 0;
        Cube cube = getCube();
        boolean doubleAngle=isDoubleAngle();
        if (swipeDirection == -1 && orientation == -1) {
            angle = 0; // no angle
        } else if (swipeDirection == -1) {
            angle = cube.getPartAngle(partIndex, orientation);
            if (doubleAngle) {
                angle *= 2;
            }
        } else {
            angle = cube.getPartSwipeAngle(partIndex, orientation, swipeDirection);
            if (doubleAngle) {
                angle *= 2;
            }
        }
        return angle;
    }

    /**
     * Applies this event to the specified cube.
     * If the event does not represent a cube transformation, nothing happens.
     */
    public void applyTo(@Nonnull Cube cube) {
        if ((modifiersEx & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            applyTo(cube,
                    isInverse(),
                    isDoubleAngle(),
                    isDoubleLayer());
        }

    }

    /**
     * Applies this event to the provided cube.
     * If the event does not represent a cube transformation, nothing happens.
     */
    private void applyTo(@Nonnull Cube cube, boolean inverse, boolean doubleAngle, boolean doubleLayer) {
        int axis, layerMask, angle;
        if (swipeDirection == -1 && orientation == -1) {
            return;
        } else if (swipeDirection == -1) {
            axis = cube.getPartAxis(partIndex, orientation);
            layerMask = cube.getPartLayerMask(partIndex, orientation);
            if (doubleLayer) {
                if (layerMask == 1 || layerMask == 1 << (cube.getLayerCount() - 1)) {
                    layerMask |= (layerMask << 1) | (layerMask >>> 1);
                    layerMask &= (1 << cube.getLayerCount()) - 1;
                } else {
                    layerMask = ((1 << (cube.getLayerCount() - 2)) - 1) << 1;
                }
            }
            angle = cube.getPartAngle(partIndex, orientation);
            if (doubleAngle) {
                angle *= 2;
            }
        } else {
            axis = cube.getPartSwipeAxis(partIndex, orientation, swipeDirection);
            layerMask = cube.getPartSwipeLayerMask(partIndex, orientation, swipeDirection);
            angle = cube.getPartSwipeAngle(partIndex, orientation, swipeDirection);
            if (doubleLayer) {
                if (cube.getLayerCount() % 2 == 1
                        && layerMask == 1 << (cube.getLayerCount() / 2)) {
                    layerMask = ((1 << (cube.getLayerCount() - 2)) - 1) << 1;
                } else if (layerMask < cube.getLayerCount()) {
                    layerMask |= layerMask << 1;
                } else {
                    layerMask |= layerMask >>> 1;
                }
                layerMask &= (1 << (cube.getLayerCount())) - 1;
            }
            if (doubleAngle) {
                angle *= 2;
            }
        }
        //System.out.println("transform " + axis + " " + layerMask + "," + angle);
        cube.transform(
                axis,
                layerMask,
                (inverse) ? -angle : angle);
    }

    /**
     * The index of the cube part. Values 0..7 are
     * corners, 8..19 edges, 20..25 sides, 26 center.
     */
    public int getPartIndex() {
        return partIndex;
    }

    /**
     * The orientation of the face on the part.
     * -1 if not applicable.
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * The side of the cube. Values 0..5 = Front,
     * Right, Bottom, Back, Left, Top. Or -1 if the
     * side can not be determined.
     */
    public int getSideIndex() {
        return sideIndex;
    }

    /**
     * The sticker index or -1 if there is no sticker
     * on the face of the part where the mouse event
     * occured.
     */
    public int getStickerIndex() {
        return stickerIndex;
    }

    /**
     * The extended modifiers. 
     */
    public int getModifiersEx() {
        return modifiersEx;
    }

    /**
     * The mouse event.
     * This method returns null, when the event has been fired due to
     * an action event.
     */
    @Nullable
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    @Nonnull
    @Override
    public String toString() {
        return getClass().getName() + "@" + System.identityHashCode(this) + " [partIndex=" + partIndex + ",orientation=" + orientation + ",sideIndex=" + sideIndex + ",stickerIndex=" + stickerIndex + ",modifiers=" + mouseEvent + "]";
    }
}
