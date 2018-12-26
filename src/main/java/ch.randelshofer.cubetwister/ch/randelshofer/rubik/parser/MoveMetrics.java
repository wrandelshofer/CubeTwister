/* @(#)MoveMetrics.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.abs;

/**
 * Provides metrics for move sequences.
 * <p>
 * This class is designed to work with (but does not require) streams.
 * For example, you can compute move metrics for a stream with:
 * <pre>{@code
 * Stream<Node> nodeStream = ...;
 * MoveMetrics metrics = nodeStream.collect(MoveMetrics::new,
 *                                          MoveMetrics::accept,
 *                                          MoveMetrics::combine);
 * }</pre>
 */
public class MoveMetrics implements Consumer<Node> {
    /**
     * Current node.
     */
    private MoveNode current = null;
    /**
     * Face Turn Metric without current node.
     */
    private int ftm = 0;
    /**
     * Quarter Turn Metric without current node.
     */
    private int qtm = 0;
    /**
     * Block Turn Metric without current node.
     */
    private int btm = 0;
    /**
     * Layer Turn Metric without current node.
     */
    private int ltm = 0;

    /**
     * Gets the layer turn count of the subtree starting
     * at this node.
     */
    public static int getLayerTurnCount(Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getLayerTurnCount();
    }

    /**
     * Gets the block turn count of the subtree starting
     * at this node.
     */
    public static int getBlockTurnCount(Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getBlockTurnCount();
    }

    /**
     * Gets the face turn count of the subtree starting
     * at this node.
     */
    public static int getFaceTurnCount(Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getFaceTurnCount();
    }

    /**
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    public static int getQuarterTurnCount(Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getQuarterTurnCount();
    }

    @Override
    public void accept(Node node) {
        // coalesce moves for counting
        for (Node resolvedNode : node.resolvedIterable(false)) {
            if (!(resolvedNode instanceof MoveNode)) {
                continue;// filter for move nodes
            }
            MoveNode moveNode = (MoveNode) resolvedNode;
            int layerMask = moveNode.getLayerMask();
            int layerCount = moveNode.getLayerCount();
            int angle = moveNode.getAngle();
            int allLayers = (1 << layerCount) - 1;
            int axis = moveNode.getAxis();
            if (current == null) {
                current = moveNode;
            } else {
                if (layerMask == 0 || angle == 0) {
                    // skip nop
                } else if (current.getAxis() == axis && layerMask == allLayers) {
                    // skip cube rotation over same axis

                } else if (current.getAxis() == axis && current.getLayerMask() == layerMask) {
                    // coalesce subsequent move on same axis and same layer
                    current = new MoveNode(layerCount, axis, layerMask, angle + current.getAngle(),
                            current.getStartPosition(), moveNode.getEndPosition());
                } else if (current.getAxis() == axis && current.getAngle() == angle && (current.getLayerMask() ^ layerMask) == 0) {
                    // coalesce subsequent move on same axis and angle and different layers
                    current = new MoveNode(layerCount, axis, layerMask, angle + current.getAngle(),
                            current.getStartPosition(), moveNode.getEndPosition());
                } else {
                    // cannot coalesce
                    if (isTwistMove(current)) {
                        addToMetrics(current);
                    }
                    current = moveNode;
                }
            }
        }
    }

    private void addToMetrics(MoveNode move) {
        ltm += countLayerTurns(move);
        qtm += countQuarterTurns(move);
        ftm += countFaceTurns(move);
        btm += countBlockTurns(move);
    }

    /**
     * Combines the state of another {@code MoveMetrics} into this
     * one.
     *
     * @param that another {@code MoveMetrics}
     * @throws NullPointerException if {@code other} is null
     */
    public MoveMetrics combine(MoveMetrics that) {
        this.ltm += that.ltm;
        this.btm += that.btm;
        this.qtm += that.qtm;
        this.ftm += that.ftm;
        if (that.current != null) {
            accept(that.current);
        }
        return this;
    }

    /**
     * Gets the current layer turn count.
     */
    public int getLayerTurnCount() {
        return current == null ? ltm : ltm + countLayerTurns(current);
    }

    /**
     * Gets the current block turn count.
     */
    public int getBlockTurnCount() {
        return current == null ? btm : btm + countBlockTurns(current);
    }

    /**
     * Gets the current face turn count.
     */
    public int getFaceTurnCount() {
        return current == null ? ftm : ftm + countFaceTurns(current);
    }

    /**
     * Gets the current quarter turn count.
     */
    public int getQuarterTurnCount() {
        return current == null ? qtm : qtm + countQuarterTurns(current);
    }

    /**
     * Gets the layer turn count of the specified move node.
     */
    private int countLayerTurns(MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int turns = abs(move.getAngle())%4;
        if (turns == 0) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < layerCount; i++) {
                if (((layerMask >>> i) & 1) == 1) {
                    count++;
                }
            }
            return Math.min(count, layerCount - count);
        }
    }

    /**
     * Gets the block turn count of the specified move node.
     */
    private int countBlockTurns(MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int turns = abs(move.getAngle())%4;
        if (turns == 0) {
            return 0;
        } else {
            int previousTurnedLayer = 0;
            int countTurned = 0;
            int previousImmobileLayer = 1;
            int countImmobile = 0;
            for (int i = 0; i < layerCount; i++) {
                int currentLayer = (layerMask >>> i) & 1;
                if (currentLayer == 1 && currentLayer != previousTurnedLayer) {
                    countTurned++;
                }
                if (currentLayer == 0 && currentLayer != previousImmobileLayer) {
                    countImmobile++;
                }
                previousTurnedLayer = previousImmobileLayer = currentLayer;
            }
            return Math.min(countTurned, countImmobile);
        }
    }

    /**
     * Gets the face turn count of the specified node.
     */
    private int countFaceTurns(MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int count = getBlockTurnCount(move);
        if (count != 0 && ((layerMask & (1 | (1 << (layerCount - 1)))) == 0
                || (layerMask & (1 | (1 << (layerCount - 1)))) == (1 | (1 << (layerCount - 1))))) {
            count++;
        }
        return count;
    }

    /**
     * Gets the face turn count of the specified node.
     */
    private int countQuarterTurns(MoveNode move) {
        int qturns = abs(move.getAngle() % 4);
        if (qturns == 3) {
            qturns = 1;
        }
        return countFaceTurns(move) * qturns;
    }

    /**
     * Returns true if the specified move twists layers.
     *
     * @param move a move
     * @return true if move twists layrser
     */
    private boolean isTwistMove(MoveNode move) {
        int layerCount = move.getLayerCount();
        int turns = abs(move.getAngle()) % 4;
        int allLayers = (1 << (layerCount)) - 1;
        int layerMask = move.getLayerMask();

        return turns != 0 && layerMask != 0 && layerMask != allLayers;

    }
}
