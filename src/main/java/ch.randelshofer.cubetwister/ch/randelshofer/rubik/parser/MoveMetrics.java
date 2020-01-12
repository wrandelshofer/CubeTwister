/* @(#)MoveMetrics.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

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
     * Current move node.
     */
    @Nullable
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
     * Counts the number of no-op moves.
     * Includes the current node.
     */
    private int moveCount = 0;
    /**
     * True if we coalesce subsequent moves over the same axis and angle
     * while counting.
     */
    private boolean coalesce = true;

    public MoveMetrics() {
    }

    public MoveMetrics(boolean coalesce) {
        this.coalesce = coalesce;
    }

    /**
     * Gets the layer turn count of the subtree starting
     * at this node.
     */
    public static int getLayerTurnCount(@Nonnull Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getLayerTurnCount();
    }

    /**
     * Gets the block turn count of the subtree starting
     * at this node.
     */
    public static int getBlockTurnCount(@Nonnull Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getBlockTurnCount();
    }

    /**
     * Gets the face turn count of the subtree starting
     * at this node.
     */
    public static int getFaceTurnCount(@Nonnull Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getFaceTurnCount();
    }

    /**
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    public static int getQuarterTurnCount(@Nonnull Node node) {
        MoveMetrics metrics = new MoveMetrics();
        metrics.accept(node);
        return metrics.getQuarterTurnCount();
    }

    @Override
    public void accept(@Nonnull Node node) {
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
            if (layerMask == 0 || angle == 0) {
                // skip nop, don't count
            } else if (current == null) {
                // cannot coalesce
                current = moveNode;
                moveCount++;
            } else if (coalesce && current.getAxis() == axis && layerMask == allLayers) {
                // skip cube rotation over same axis
                moveCount++;
            } else if (coalesce && current.getAxis() == axis && current.getLayerMask() == layerMask) {
                // coalesce subsequent move on same axis and same layer
                current = new MoveNode(layerCount, axis, layerMask, angle + current.getAngle(),
                        current.getStartPosition(), moveNode.getEndPosition());
                moveCount++;
            } else if (coalesce && current.getAxis() == axis && current.getAngle() == angle && (current.getLayerMask() & layerMask) == 0) {
                // coalesce subsequent move on same axis and angle and different layers
                current = new MoveNode(layerCount, axis, current.getLayerMask() | layerMask, angle,
                        current.getStartPosition(), moveNode.getEndPosition());
                moveCount++;
            } else {
                // cannot coalesce
                if (isTwistMove(current)) {
                    addToTurnMetrics(current);
                }
                current = moveNode;
                moveCount++;
            }
        }
    }

    private void addToTurnMetrics(@Nonnull MoveNode move) {
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
    @Nonnull
    public MoveMetrics combine(@Nonnull MoveMetrics that) {
        this.ltm += that.ltm;
        this.btm += that.btm;
        this.qtm += that.qtm;
        this.ftm += that.ftm;
        int tmpCount = this.moveCount;
        if (that.current != null) {
            accept(that.current);
        }
        this.moveCount = tmpCount + that.moveCount;
        return this;
    }

    /**
     * Gets the block turn count of the specified move node.
     */
    private int countBlockTurns(@Nonnull MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int turns = abs(move.getAngle()) % 4;
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
     * <p>
     * If a move has changed at least one layer but not all layers:
     * <ul>
     *     <li>counts 1: if the inner-most layer has been turned
     *     together with the outer-most layer</li>
     *     <li>counts 2: otherwise</li>
     * </ul>
     */
    private int countFaceTurns(@Nonnull MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int angle = abs(move.getAngle()) % 4;

        int allLayers = (1 << layerCount) - 1;
        if (angle == 0 || layerMask == 0 || layerMask == allLayers) {
            return 0;
        }

        boolean innerTurned = (layerMask & 1) != 0;
        boolean outerTurned = (layerMask & (1 << (layerCount - 1))) != 0;

        return innerTurned == outerTurned ? 2 : 1;
    }

    /**
     * Gets the layer turn count of the specified move node.
     */
    private int countLayerTurns(@Nonnull MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int turns = abs(move.getAngle()) % 4;
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
     * Gets the face turn count of the specified node.
     */
    private int countQuarterTurns(@Nonnull MoveNode move) {
        int qturns = abs(move.getAngle() % 4);
        if (qturns == 3) {
            qturns = 1;
        }
        return countFaceTurns(move) * qturns;
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
     * Gets the current layer turn count.
     */
    public int getLayerTurnCount() {
        return current == null ? ltm : ltm + countLayerTurns(current);
    }

    /**
     * Gets the number of no-op moves.
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Gets the current quarter turn count.
     */
    public int getQuarterTurnCount() {
        return current == null ? qtm : qtm + countQuarterTurns(current);
    }

    public boolean isCoalesce() {
        return this.coalesce;
    }

    public void setCoalesce(boolean coalesce) {
        this.coalesce = coalesce;
    }

    /**
     * Returns true if the specified move twists layers.
     *
     * @param move a move
     * @return true if move twists layrser
     */
    private boolean isTwistMove(@Nonnull MoveNode move) {
        int layerCount = move.getLayerCount();
        int turns = abs(move.getAngle()) % 4;
        int allLayers = (1 << (layerCount)) - 1;
        int layerMask = move.getLayerMask();

        return turns != 0 && layerMask != 0 && layerMask != allLayers;

    }

    @Nonnull
    @Override
    public String toString() {
        return "MoveMetrics{" +
                "ftm=" + ftm +
                ", qtm=" + qtm +
                ", btm=" + btm +
                ", ltm=" + ltm +
                ", moves=" + moveCount +
                '}';
    }
}
