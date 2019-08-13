/* @(#)AbstractCubeIdx3D.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import idx3d.idx3d_Group;
import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Lightmap;
import idx3d.idx3d_Matrix;
import idx3d.idx3d_Object;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Texture;

import java.awt.Color;
import java.awt.Image;

/**
 * Abstract base class for the geometrical representation of a {@link Cube}
 * using the Idx3D engine.
 *
 * @author  Werner Randelshofer
 */
public abstract class AbstractCubeIdx3D extends AbstractCube3D {

    protected int cornerCount;
    protected int edgeCount;
    protected int sideCount;
    protected int centerCount;
    protected int partCount;
    protected int cornerOffset;
    protected int edgeOffset = cornerCount;
    protected int sideOffset = cornerCount + edgeCount;
    protected int centerOffset = cornerCount + edgeCount + sideCount;
    protected static idx3d_Lightmap sharedLightmap;
    protected idx3d_Scene scene;
    protected idx3d_Group alphaBetaTransform;
    protected idx3d_Group scaleTransform;
    protected Image cachedStickersImage;
    protected idx3d_Texture stickersTexture;
    protected final static float HALF_PI = (float) (Math.PI / 2d);
    protected final float PI = (float) Math.PI;
    protected float explosionShift;
    private boolean isAttributesValid = false;
    private boolean isAlphaBetaValid = false;
    private boolean isStickersImageValid = false;
    private boolean isCubeValid = false;
    //private boolean isScaleFactorValid = false;
    /**
     * Holds the parts in the sequence: corners, edges, sides and center.
     */
    protected idx3d_Object[] parts;
    /**
     * Holds the identity locationTransforms of the parts.
     * The identity locationTransforms represent the cube in solved state.
     */
    protected idx3d_Matrix[] identityVertexMatrix;
    protected idx3d_Matrix[] identityNormalMatrix;
    /**
     * Holds the locationTransforms of the parts in the sequence: corners, edges, sides and center.
     */
    protected idx3d_Group[] locationTransforms;
    /**
     * Holds the explosionTransforms of the parts in the sequence: corners, edges, sides and center.
     */
    protected idx3d_Group[] explosionTransforms;
    protected float explosion = 15f;
    protected boolean isInStartedPlayer;

    /**
     * Creates a new instance.
     */
    public AbstractCubeIdx3D(int layerCount, int cornerCount, int edgeCount, int sideCount, int centerCount) {
        super(layerCount);
        this.cornerCount = cornerCount;
        this.edgeCount = edgeCount;
        this.sideCount = sideCount;
        this.centerCount = centerCount;
        partCount = cornerCount + edgeCount + sideCount + centerCount;
        cornerOffset = 0;
        edgeOffset = cornerCount;
        sideOffset = cornerCount + edgeCount;
        centerOffset = cornerCount + edgeCount + sideCount;

        parts = new idx3d_Object[partCount];

        identityVertexMatrix = new idx3d_Matrix[partCount];
        identityNormalMatrix = new idx3d_Matrix[partCount];
        locationTransforms = new idx3d_Group[partCount];
        explosionTransforms = new idx3d_Group[partCount];
    }

    @Override
    public Object getScene() {
        return scene;
    }

    @Override
    public Object getLock() {
        return scene;
    }

    @Override
    public int getPartCount() {
        return partCount;
    }

    protected abstract void initActions(idx3d_Scene scene);

    @Override
    protected void updateAttributes() {
        isAttributesValid = false;
        updateScaleFactor();
        updateExplosionFactor();
        updateAlphaBeta();
        updatePartsVisibility();
        updatePartsFillColor();
        //updatePartsOutlineColor();
        updateStickersFillColor();
        updateBackgroundColor();
        fireStateChanged();
    }

    protected void validateAttributes() {
        if (!isAttributesValid) {
            isAttributesValid = true;
            idx3d_InternalMaterial material;
            int offset;

            CubeAttributes attr = getAttributes();

            validateStickersImage();
            for (int i = 0, n = getPartCount(); i < n; i++) {
                //updatePartVisibility(i, (attr.isPartVisible(i)) ? 1f : 0f);
                updatePartFillColor(i, attr.getPartFillColor(i));
                updatePartOutlineColor(i, attr.getPartOutlineColor(i));
            }
            for (int i = 0, n = getStickerCount(); i < n; i++) {
                updateStickerFillColor(i, attr.getStickerFillColor(i));
                //updateStickerVisibility(i, (attr.isStickerVisible(i)) ? 1f : 0f);
            }
        }
    }

    /**
     * Updates the fill color of a part.
     * The part Index is interpreted according to the scheme
     * used by method getPart(int);
     */
    protected void updatePartFillColor(int index, Color c) {
        idx3d_Object part = getPart(index);
        idx3d_InternalMaterial material = part.material;
        material.setColor(c.getRGB());
        material.setReflectivity(280);
        part.setMaterial(material);
    }
    protected void updatePartOutlineColor(int index, Color c) {
    }

    protected void updateStickerFillColor(int index, Color c) {
        int partIndex = getPartIndexForStickerIndex(index);
        int triangleIndex = getPartFaceIndexForStickerIndex(index);
        idx3d_Object part = getPart(partIndex);
        idx3d_InternalMaterial material = part.triangle(triangleIndex).getMaterial();
        if (getAttributes().isStickerVisible(index)) {
            material.setColor(c.getRGB());
            material.setReflectivity(80);

            // Set texture only, if it has pixel data
            if (stickersTexture != null && stickersTexture.pixel != null) {
                material.setTexture(stickersTexture);
            } else {
                material.setTexture(null);
            }
        } else {
            material.setColor(getAttributes().getPartFillColor(partIndex).getRGB());
            material.setReflectivity(280);
            material.setTexture(null);
        }
        //material.setFlat(true);
        //material.setWireframe(true);
    }

    /**
     * Updates the visibility of a sticker.
     */
    @Override
    protected void updateStickerVisibility(int index, float alpha) {
        int partIndex = getPartIndexForStickerIndex(index);
        int faceIndex = getPartFaceIndexForStickerIndex(index);
        idx3d_Object part = getPart(partIndex);
        Color stickerColor = getAttributes().getStickerFillColor(index);
        Color partColor = getAttributes().getPartFillColor(partIndex);

        idx3d_InternalMaterial material = part.triangle(faceIndex).getMaterial();
        if (stickersTexture == null) {
            Color mixedColor = new Color(
                    (int) (stickerColor.getRed() * alpha + partColor.getRed() * (1f - alpha)),
                    (int) (stickerColor.getGreen() * alpha + partColor.getGreen() * (1f - alpha)),
                    (int) (stickerColor.getBlue() * alpha + partColor.getBlue() * (1f - alpha)));

            material.setColor(mixedColor.getRGB());
        } else {
            material.setTextureTransparency(255 - (int) (255 * alpha));
            material.setColor(partColor.getRGB());
            material.setTexture(stickersTexture);
        }
        material.setReflectivity((int) (80 * alpha + 280 * (1f - alpha)));
    }
    /*
    protected void updateScaleFactor() {
    isScaleFactorValid = false;
    fireStateChanged();
    }
    protected void validateScaleFactor() {
    if (! isScaleFactorValid) {
    isScaleFactorValid = true;
    scaleTransform.resetTransform();
    scaleTransform.scale(0.018f * attributes.getScaleFactor());
    }
    }*/

    protected void validateScaleFactor() {
        //  updateScaleFactor();
    }

    protected void updateScaleFactor() {
        updateScaleFactor(attributes.getScaleFactor());
    }

    protected float getUnitScaleFactor() {
        return 0.018f;
    }

    @Override
    protected void updateScaleFactor(float factor) {
        scaleTransform.resetTransform();
        scaleTransform.scale(getUnitScaleFactor() * factor);
    }

    protected void updateExplosionFactor() {
        updateExplosionFactor(attributes.getExplosionFactor());
    }

    @Override
    protected void updateExplosionFactor(float factor) {
        float baseShift = explosionShift + explosionShift * factor;
        float shift;
        CubeAttributes a = getAttributes();
        for (int i = 0; i < cornerCount; i++) {
            int index = cornerOffset + i;
            shift = baseShift + a.getPartExplosion(index);
            explosionTransforms[index].resetTransform();
            explosionTransforms[index].rotate(HALF_PI, 0, 0);
            explosionTransforms[index].shift(-shift, shift, -shift);
        }
        for (int i = 0; i < edgeCount; i++) {
            int index = edgeOffset + i;
            shift = baseShift + a.getPartExplosion(index);
            explosionTransforms[index].resetTransform();
            explosionTransforms[index].rotate(0, PI, 0);
            explosionTransforms[index].shift(0f, shift, -shift);
        }
        for (int i = 0; i < sideCount; i++) {
            int index = sideOffset + i;
            shift = baseShift + a.getPartExplosion(index);
            explosionTransforms[index].resetTransform();
            explosionTransforms[index].rotate(0, PI, 0);
            explosionTransforms[index].shift(0f, 0f, -shift);
        }
        fireStateChanged();
    }

    @Override
    protected void updateAlphaBeta() {
        isAlphaBetaValid = false;
        fireStateChanged();
    }

    protected void validateAlphaBeta() {
        if (!isAlphaBetaValid) {
            isAlphaBetaValid = true;
            float alpha = getAttributes().getAlpha();
            float beta = getAttributes().getBeta();
            alphaBetaTransform.resetTransform();
            //alphaBetaTransform.rotate(alpha / (float) Math.PI / 2f, beta / (float) Math.PI / 2f, 0);
            alphaBetaTransform.rotate(0, beta, 0);
            alphaBetaTransform.rotate(-alpha, 0, 0);
        }
    }

    protected idx3d_Object getPart(int partIndex) {
        return parts[partIndex];
    }

    @Override
    protected void updateStickersImage() {
        isStickersImageValid = false;
        updateAttributes();
        fireStateChanged();
    }

    protected void validateStickersImage() {
        if (!isStickersImageValid) {
            isStickersImageValid = true;

            // Defensively free the texture before creating a new one.
            stickersTexture = null;

            CubeAttributes a = getAttributes();
            Image stickersImage = (!a.isStickersImageVisible()) ? null : a.getStickersImage();
            if (stickersImage != cachedStickersImage) {
                cachedStickersImage = stickersImage;
                if (stickersImage != null) {
                    try {
                        stickersTexture = new idx3d_Texture(stickersImage);
                        if (stickersTexture.pixel == null) {
                            throw new OutOfMemoryError("Couldn't create a texture from the image - maybe because we are low on memory?");
                        }
                    } catch (OutOfMemoryError e) {

                        // If the image is very large, try again with a smaller image
                        if (stickersImage.getWidth(null) > 1024) {
                            try {
                                Image smallImage = stickersImage.getScaledInstance(1024, 1024, Image.SCALE_FAST);
                                stickersTexture = new idx3d_Texture(smallImage);
                                if (stickersTexture.pixel == null) {
                                    throw new OutOfMemoryError("Couldn't create a texture from the image - maybe because we are low on memory?");
                                }
                                System.err.println("Warning. AbstractCubeIdx3D used a smaller texture image to save memory.");
                            } catch (OutOfMemoryError e2) {
                                // give up
                                isStickersImageValid = false;
                                cachedStickersImage = null;
                                throw e;
                            }
                        } else {
                                System.err.println("Warning. AbstractCubeIdx3D couldn't create a texture from the stickers image - maybe because the stickers image could not be found or is corrupt.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Frees resources used by the 3D cube.
     */
    @Override
    public void dispose() {
        super.dispose();
        stickersTexture = null;
        scene = null;
        attributes = null;
    }

    @Override
    protected void updateCube() {
        stopAnimation();
        isCubeValid = false;
        fireStateChanged();
    }

    protected void validateCube() {
        if (!isCubeValid) {
            isCubeValid = true;
            Cube model = getCube();
            int[] partIndices = new int[partCount];
            int[] locations = new int[partCount];
            int[] orientations = new int[partCount];
            synchronized (model) {
                for (int i = 0; i < partCount; i++) {
                    locations[i] = i;
                    partIndices[i] = model.getPartAt(locations[i]);
                    orientations[i] = model.getPartOrientation(partIndices[i]);
                }
            }
            validateTwist(partIndices, locations, orientations, partCount, 0, 0, 1f);
        }
    }

    protected abstract void validateTwist(int[] partIndices, int[] locations, int[] orientations, int length, int axis, int angle, float alpha);

    /**
     * Updates the visibility of a part.
     * The part Index is interpreted according to the scheme
     * used by method getPart(int);
     */
    @Override
    protected void updatePartVisibility(int index, float alpha) {
        idx3d_Object part = getPart(index);
        idx3d_InternalMaterial material = part.material;
        int transparency = (int) ((1f - alpha) * 255);
        if (isShowGhostParts()) {
            transparency = Math.min(transparency, 225);
        }
        boolean visible = transparency != 255;
        material.setTransparency(transparency);
        material.setVisible(visible);
        for (int i = 0, n = part.getTriangleCount(); i < n; i++) {
            material = part.triangle(i).getTriangleMaterial();
            if (material != null) {
            material.setTransparency(transparency);
            material.setVisible(visible);
            }
        }
    }

    /**
     * Updates the visible state of the parts.
     */
    final protected void updatePartsVisibility() {
        CubeAttributes attr = getAttributes();
        for (int i = 0; i < partCount; i++) {
            updatePartVisibility(i, attr.isPartVisible(i) ? 1f : 0f);
        }
    }

    /**
     * Updates the fill color of the parts.
     */
    final protected void updatePartsFillColor() {
        CubeAttributes attr = getAttributes();
        for (int i = 0; i < partCount; i++) {
            updatePartFillColor(i, attr.getPartFillColor(i));
        }
    }

    /**
     * Updates the stickers color of the parts.
     */
    final protected void updateStickersFillColor() {
        CubeAttributes attr = getAttributes();
        for (int i = 0, n = getStickerCount(); i < n; i++) {
            updateStickerFillColor(i, attr.getStickerFillColor(i));
        }
    }

    private void updateBackgroundColor() {
        CubeAttributes attr = getAttributes();
        if (attr.getFrontBgColor() != null) {
            scene.setBackgroundColor(attr.getFrontBgColor().getRGB());
        }
        if (attr.getFrontBgImage() != null) {
            idx3d_Texture texture = new idx3d_Texture(attr.getFrontBgImage());
            scene.setBackground(texture);
        }
    }

    @Override
    public abstract void setStickerBeveling(float newValue);

    @Override
    public void setInStartedPlayer(boolean newValue) {
        isInStartedPlayer=newValue;
        if (!newValue) fireStateChanged();
    }

    @Override
    public boolean isInStartedPlayer() {
        return isInStartedPlayer;
    }
}
