/* @(#)RubiksCubeApplet.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.virtual;

import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube3d.AbstractCubeIdx3D;
import ch.randelshofer.rubik.cube3d.Cube3DEvent;
import ch.randelshofer.rubik.cube3d.RubiksDiamondIdx3D;
import org.jhotdraw.annotation.Nonnull;

import java.awt.event.InputEvent;

/**
 * RubiksCubeApplet.
 * 
 * 
 * @author Werner Randelshofer
 */
public class VirtualRubiksDiamondApplet extends AbstractVirtualCubeApplet {
    private final static long serialVersionUID = 1L;

    @Nonnull
    protected AbstractCubeIdx3D createCube3D() {
        return new RubiksDiamondIdx3D();
    }

    @Override
    protected AbstractVirtualCubeApplet.EventHandler getPartsHandler() {
        if (partsHandler == null) {
            partsHandler = new AbstractVirtualCubeApplet.EventHandler() {

                @Override
                public void actionPerformed(@Nonnull Cube3DEvent evt) {
                    DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
                    int modifiersEx = evt.getModifiersEx();
                    int partIndex = evt.getPartIndex();
                    int stickerIndex = evt.getStickerIndex();
                    int sideIndex = evt.getSideIndex();
                    int offset, length;

                    Cube cube = cube3d.getCube();

                    boolean endState = !attr.isPartVisible(partIndex);
                    boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                    int relevantModifiersEx = (isMac) ? //
                            modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                            modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
                    switch (relevantModifiersEx) {
                        case InputEvent.SHIFT_DOWN_MASK:
                            // Shift affects all parts of the same color
                            offset = 0;
                            int face = Cubes.getFaceOfSticker(attr, stickerIndex);
                            offset = attr.getStickerOffset(face);
                            length = attr.getStickerCount(face);
                            for (int i = 0; i < length; i++) {
                                attr.setPartVisible(
                                        cube3d.getPartIndexForStickerIndex(i + offset),
                                        endState);
                            }
                            break;

                        case InputEvent.META_DOWN_MASK:
                        case InputEvent.CTRL_DOWN_MASK:
                            /*
                            // Meta affects all parts on the same face
                            offset = attr.getStickerOffset(sideIndex);
                            for (int i=0; i < attr.getStickerCount(sideIndex); i++) {
                            attr.setPartVisible(
                            cube.getPartAt(cube3d.getPartIndexForStickerIndex(i+offset)),
                            endState
                            );
                            }*/
                            break;

                        case InputEvent.ALT_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK:
                            // Alt affects all parts of the same type
                            int indices[];
                            if (partIndex < cube.getCornerCount()) {
                                indices = new int[cube.getCornerCount()];
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = i;
                                }
                            } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount()) {
                                indices = new int[cube.getEdgeCount()];
                                offset = cube.getCornerCount();
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount()) {
                                indices = new int[cube.getSideCount()];
                                offset = cube.getCornerCount() + cube.getEdgeCount();
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            } else {
                                offset = cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount();
                                indices = new int[attr.getPartCount() - offset];
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            }
                            for (int i = 0; i < indices.length; i++) {
                                attr.setPartVisible(indices[i], endState);
                            }
                            break;

                        case InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                        case InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                        case InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                            // Shift+Alt affects all parts except the center part
                            for (int i = 0, n = cube3d.getPartCount() - 1; i < n; i++) {
                                attr.setPartVisible(i, endState);
                            }
                            break;

                        case 0:
                            // No modifiersEx affects a single part only
                            attr.setPartVisible(partIndex, endState);
                            break;

                    }
                }
            };
        }
        return partsHandler;
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
