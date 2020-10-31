/*
 * @(#)SolverModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.rubik.Cube3DAdapter;
import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.cube.StickerCubes;
import ch.randelshofer.rubik.cube3d.Cube3D;
import ch.randelshofer.rubik.cube3d.Cube3DEvent;
import ch.randelshofer.rubik.cube3d.Cube3DListener;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.parser.ScriptPlayer;
import ch.randelshofer.undo.UndoableIntEdit;
import ch.randelshofer.undo.UndoableObjectEdit;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * SolverModel.
 *
 * @author Werner Randelshofer
 */
public class SolverModel extends AbstractBean implements CubeAttributes, PropertyChangeListener {
    private final static long serialVersionUID = 1L;

    @Nonnull
    protected EventListenerList listenerList = new EventListenerList();
    private CubeAttributes target;
    /**
     * Index of the current paint that we are painting.
     * If this is -1, we are twisting the cube.
     * If this is &gt;= 0, we are painting on the cube.
     */
    private int paintFace = -1;
    /**
     * This array contains a value for each of the stickerFaces of the cube.
     * If the value -1, the sticker has an undefined color.
     * The values 0 through 6 represent the six faces of the cube: front, right,
     * bottom, back, left, up.
     */
    private int[] stickerFaces;
    private Cube mappedCube;
    private boolean[] isMappable;
    private Cube3D cube3D;
    private ScriptPlayer player;
    private Cube3DListener cube3DHandler;

    /**
     * Creates a new instance of SolverModel
     */
    public SolverModel(@Nonnull Cube3D cube3D, ScriptPlayer player, CubeAttributes target) {
        //   this.target = target;
        this.cube3D = cube3D;
        this.player = player;

        cube3DHandler = new Cube3DAdapter() {

            @Override
            public void actionPerformed(@Nonnull Cube3DEvent evt) {
                if (SolverModel.this.player != null) {
                    SolverModel.this.player.stop();
                }
                if (isTwisting()) {
                    if (evt.getSideIndex() != -1) {
                        Cube cube = SolverModel.this.cube3D.getCube();
                        evt.applyTo(cube);
                    }
                    // FIXME - Implement undo/redo here
                } else {
                    int index = evt.getStickerIndex();
                    if (index != -1) {
                        if (isMappable[index]) {
                            setStickerMapping(index, paintFace);
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                }
            }
        };

        cube3D.addCube3DListener(cube3DHandler);

        //  stickerFaces = new int[target.getStickerCount()];
        //  isMappable = new boolean[target.getStickerCount()];
        //  reset();
        setCubeAttributes(target);
    }

    public void setPlayer(ScriptPlayer player) {
        this.player = player;
    }

    public void setCubeAttributes(CubeAttributes m) {
        if (target != null) {
            target.removePropertyChangeListener(this);
        }
        target = m;
        if (target != null) {
            stickerFaces = new int[target.getStickerCount()];
            isMappable = new boolean[target.getStickerCount()];
            reset();
            target.addPropertyChangeListener(this);
         }
        propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);
    }

    /**
     * Sets the face we are painting.
     * If this is -1, we are twisting the cube.
     * If this is &gt;= 0, we are painting on the cube.
     */
    public void setPaintFace(int newValue) {
        int oldValue = paintFace;
        paintFace = newValue;
        propertySupport.firePropertyChange("paintFace", oldValue, newValue);
        if (newValue == -1) {
            propertySupport.firePropertyChange(CubeAttributes.STICKERS_IMAGE_PROPERTY, null, target.getStickersImage());
        } else {
            propertySupport.firePropertyChange(CubeAttributes.STICKERS_IMAGE_PROPERTY, target.getStickersImage(), null);
        }
    }

    public boolean isPainting() {
        return paintFace >= 0;
    }

    public boolean isTwisting() {
        return paintFace == -1;
    }

    @Override
    public void reset() {
        int[] oldValue = stickerFaces.clone();
        int stickersPerFace = target.getStickerCount(0);
        int faceIndex = 0;
        int offset = 0;
        for (int i = 0; i < stickerFaces.length; i++) {
            if (i - offset >= target.getStickerCount(faceIndex)) {
                offset += target.getStickerCount(faceIndex);
                faceIndex++;
            }
            stickerFaces[i] = faceIndex;
            isMappable[i] = true;
        }
        // Lock unmappable stickerFaces for Pocket Cube
        if (target.getFaceCount() == 6 && target.getStickerCount() == 6 * 4) {
            stickerFaces[1] = 0;
            isMappable[1] = false;
            stickerFaces[4] = 1;
            isMappable[4] = false;
            stickerFaces[23] = 5;
            isMappable[23] = false;

            // Lock unmappable stickerFaces for Rubik's Cube
        } else if (target.getFaceCount() == 6 && target.getStickerCount() == 6 * 9) {
            for (int i = 0; i < 6; i++) {
                stickerFaces[i * 9 + 4] = i;
                isMappable[i * 9 + 4] = false;
            }
            // Lock unmappable stickerFaces for Revenge Cube
        } else if (target.getFaceCount() == 6 && target.getStickerCount() == 6 * 16) {
            stickerFaces[3] = 0;
            isMappable[3] = false;
            stickerFaces[16] = 1;
            isMappable[16] = false;
            stickerFaces[6 * 16 - 1] = 5;
            isMappable[6 * 16 - 1] = false;
            // Lock unmappable stickerFaces values for Professor Cube
        } else if (target.getFaceCount() == 6 && target.getStickerCount() == 6 * 25) {
            for (int i = 0; i < 6; i++) {
                stickerFaces[i * 25 + 12] = i;
                isMappable[i * 25 + 12] = false;
            }
        }
        setPaintFace(-1);

        int[] newValue = stickerFaces.clone();
        propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);

        fireUndoableEditHappened(new UndoableObjectEdit(this, "Reset Stickers", oldValue, newValue) {
    private final static long serialVersionUID = 1L;

            @Override
            public void revert(Object oldValue, Object newValue) {
                int[] nv = (int[]) newValue;
                System.arraycopy(nv, 0, stickerFaces, 0, stickerFaces.length);
                propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);
            }
        });
    }

    public void clear() {
        int[] oldValue = new int[stickerFaces.length];
        int[] newValue = new int[stickerFaces.length];

        System.arraycopy(stickerFaces, 0, oldValue, 0, stickerFaces.length);
        for (int i = 0; i < stickerFaces.length; i++) {
            if (isMappable[i]) {
                stickerFaces[i] = -1;
            }
        }

        if (!isPainting()) {
            setPaintFace(0);
        }

        System.arraycopy(stickerFaces, 0, newValue, 0, stickerFaces.length);
        propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);

        fireUndoableEditHappened(new UndoableObjectEdit(this, "Clear Stickers", oldValue, newValue) {
    private final static long serialVersionUID = 1L;

            public void revert(Object oldValue, Object newValue) {
                int[] nv = (int[]) newValue;

                System.arraycopy(nv, 0, stickerFaces, 0, nv.length);

                propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);
            }
        });
    }

    public void fill() {
        int[] oldValue = stickerFaces.clone();
        int faceIndex = 0;
        int offset = 0;
        for (int i = 0; i < stickerFaces.length; i++) {
            if (i - offset >= target.getStickerCount(faceIndex)) {
                offset += target.getStickerCount(faceIndex);
                faceIndex++;
            }
            if (stickerFaces[i] == -1) {
                stickerFaces[i] = faceIndex;
            }
        }
        int[] newValue = stickerFaces.clone();
        propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);

        fireUndoableEditHappened(new UndoableObjectEdit(this, "Fill Stickers", oldValue, newValue) {
    private final static long serialVersionUID = 1L;

            public void revert(Object oldValue, Object newValue) {
                int[][] nv = (int[][]) newValue;
                for (int i = 0; i < 6; i++) {
                    System.arraycopy(nv[i], 0, stickerFaces[i], 0, 9);
                }
                propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, Color.black, Color.white);
            }
        });
    }

    /**
     * The side of the cube. Values 0..5 = Front,
     * Right, Bottom, Back, Left, Top. Or -1 if the
     * side can not be determined.
     */
    public void setStickerMapping(final int stickerIndex, int mapValue) {
        System.out.println("SolverModel.setStickerMapping:" + stickerIndex + "," + mapValue);


        int oldMapValue = stickerFaces[stickerIndex];

        Color oldValue = getStickerFillColor(stickerIndex);
        stickerFaces[stickerIndex] = mapValue;
        Color newValue = getStickerFillColor(stickerIndex);
        propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, oldValue, newValue);

        fireUndoableEditHappened(new UndoableIntEdit(this, "Set Sticker", oldMapValue, mapValue) {
    private final static long serialVersionUID = 1L;

            public void revert(int oldValue, int newValue) {
                Color o = getStickerFillColor(stickerIndex);
                stickerFaces[stickerIndex] = newValue;
                Color n = getStickerFillColor(stickerIndex);
                propertySupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, o, n);
            }
        });
    }
    /*
    public int getSticker(int stickerIndex) {
    return stickerFaces[stickerIndex];
    }*/

    @Nullable
    public Color getStickerFillColor(int stickerIndex) {
        if (isPainting()) {
            int faceIndex = stickerFaces[stickerIndex];

            if (faceIndex == -1) {
                return null;
            } else {
                int offset = 0;
                for (int i = 0; i < faceIndex; i++) {
                    offset += target.getStickerCount(i);
                }
                return target.getStickerFillColor(offset);
            }
        } else {
            return target.getStickerFillColor(stickerIndex);
        }
    }

    public boolean isStickerVisible(int stickerIndex) {
        if (isPainting()) {
            int sideIndex = stickerFaces[stickerIndex];
            return sideIndex != -1 && target.isStickerVisible(stickerIndex);
        } else {
            return target.isStickerVisible(stickerIndex);
        }
    }

    public Color getPartFillColor(int partIndex) {
        return target.getPartFillColor(partIndex);
    }

    public Color getPartOutlineColor(int partIndex) {
        return target.getPartOutlineColor(partIndex);
    }

    public float getAlpha() {
        return target.getAlpha();
    }

    public float getBeta() {
        return target.getBeta();
    }

    public float getExplosionFactor() {
        return target.getExplosionFactor();
    }

    public float getScaleFactor() {
        return target.getScaleFactor();
    }

    public boolean isPartVisible(int partIndex) {
        return target.isPartVisible(partIndex);
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(@Nonnull PropertyChangeEvent evt) {
        propertySupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    /**
     * Returns true, if the solver model affects
     * the stickerFaces of the cube.
     */
    public boolean isStickersChanged() {
        if (!isPainting()) {
            for (int i = 0; i < stickerFaces.length; i++) {
                if (stickerFaces[i] != i) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSolverSupported() {
        return target.getFaceCount() == 6 && target.getStickerCount() == 54;
    }

    public boolean isSolveable() {
        if (!isSolverSupported()) {
            return false;
        }
        int[] stickerCounts = new int[6];
        for (int i = 0; i < stickerFaces.length; i++) {
            if (stickerFaces[i] != -1) {
                stickerCounts[stickerFaces[i]]++;
            }
        }
        for (int i = 0; i < stickerCounts.length; i++) {
            if (stickerCounts[i] != target.getStickerCount(i)) {
                return false;
            }
        }
        try {
            mappedCube = new RubiksCube();
            StickerCubes.setToStickers(mappedCube, stickerFaces);
            // Method getMappedCube and getMappedPermutationString
            // depend on, that mappedCube is set to the current
            // mapping by this method.
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Nonnull
    public String getMappedStickersString(@Nonnull Cube cube) {
        if (isSolverSupported() && isPainting()) {
            return Cubes.toMappedStickersString(cube, stickerFaces);
        } else {
            return Cubes.toNormalizedStickersString(cube);
        }
    }

    @Nonnull
    public String getMappedPermutationString(@Nonnull Cube cube, @Nonnull Notation notation) {
        if (isPainting()) {
            if (isSolveable()) {
                if (mappedCube == null) {
                    mappedCube = (Cube) cube.clone();
                }
                StickerCubes.setToStickers(mappedCube, stickerFaces);
                mappedCube.transform(cube);
                return "mapped:\n" + Cubes.toPermutationString(mappedCube, notation);
            } else {
                return "Can not determine permutation from stickers.";
            }
        } else {
            return Cubes.toPermutationString(cube, notation);
        }
    }

    @Nonnull
    public Cube getMappedCube(@Nonnull Cube cube) {
        if (isSolveable()) {
            if (mappedCube == null || mappedCube.getLayerCount() != cube.getLayerCount()) {
                mappedCube = (Cube) cube.clone();
            } else {
                mappedCube.transform(cube);
            }
            return (Cube) mappedCube.clone();
        } else {
            throw new IllegalStateException("Solver model is not solveable");
        }
    }

    /**
     * Adds an UndoableEditListener.
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }

    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEditHappened(UndoableEdit edit) {
        UndoableEditEvent evt = null;

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UndoableEditListener.class) {
                // Lazily create the event
                if (evt == null) {
                    evt = new UndoableEditEvent(this, edit);
                }
                ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(evt);
            }
        }
    }

    public int getFaceCount() {
        return target.getFaceCount();
    }

    public int getPartCount() {
        return target.getPartCount();
    }

    public int getStickerCount() {
        return target.getStickerCount();
    }

    public int getStickerCount(int face) {
        return target.getStickerCount(face);
    }

    public int getStickerOffset(int face) {
        return target.getStickerOffset(face);
    }

    public Color getStickerOutlineColor(int index) {
        return target.getStickerOutlineColor(index);
    }

    @Nullable
    public Image getStickersImage() {
        return (isPainting()) ? null : target.getStickersImage();
    }

    public Color getFrontBgColor() {
        return target.getFrontBgColor();
    }

    public Image getFrontBgImage() {
        return target.getFrontBgImage();
    }

    public Color getRearBgColor() {
        return target.getRearBgColor();
    }

    public Image getRearBgImage() {
        return target.getRearBgImage();
    }

    public boolean isFrontBgImageVisible() {
        return target.isFrontBgImageVisible();
    }

    public boolean isRearBgImageVisible() {
        return target.isRearBgImageVisible();
    }

    public boolean isStickersImageVisible() {
        return target.isStickersImageVisible();
    }

    public void setValueIsAdjusting(boolean newValue) {
        target.setValueIsAdjusting(newValue);

    }

    public boolean getValueIsAdjusting() {
        return target.getValueIsAdjusting();
    }

    public float getPartExplosion(int index) {
        return target.getPartExplosion(index);
    }

    public float getStickerExplosion(int index) {
        return target.getStickerExplosion(index);
    }

    public void dispose() {
        target.dispose();
    }

    public int getTwistDuration() {
        return target.getTwistDuration();
    }

    public void setTo(CubeAttributes that) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    public Object clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
