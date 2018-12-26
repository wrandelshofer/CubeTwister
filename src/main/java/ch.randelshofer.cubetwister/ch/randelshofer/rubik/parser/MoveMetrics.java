/* @(#)MoveMetrics.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Provides metrics for move sequences.
 */
public class MoveMetrics {
    /**
     * Gets the layer turn count of the subtree starting
     * at this node.
     */
    public static int getLayerTurnCount(Node node) {
        int count = 0;
        for (MoveNode move : coalesceMovesForCounting(node)) {
            count += countLayerTurns(move);
        }
        return count;
    }

    /**
     * Gets the layer turn count of the specified move node.
     */
    private static int countLayerTurns(MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int angle = move.getAngle();
        if (angle == 0) {
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
     * Gets the block turn count of the subtree starting
     * at this node.
     */
    public static int getBlockTurnCount(Node node) {
        int count = 0;
        for (MoveNode move : coalesceMovesForCounting(node)) {
            count += countBlockTurns(move);
        }
        return count;
    }

    /**
     * Gets the block turn count of the specified move node.
     */
    private static int countBlockTurns(MoveNode move) {
        int layerCount = move.getLayerCount();
        int layerMask = move.getLayerMask();
        int angle = move.getAngle();
        if (angle == 0) {
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
     * Gets the face turn count of the subtree starting
     * at this node.
     */
    public static int getFaceTurnCount(Node node) {
        int count = 0;
        for (MoveNode move : coalesceMovesForCounting(node)) {
            count += countFaceTurns(move);
        }
        return count;
    }

    /**
     * Gets the face turn count of the specified node.
     */
    private static int countFaceTurns(MoveNode move) {
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
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    public static int getQuarterTurnCount(Node node) {
        int count = 0;
        for (MoveNode move : coalesceMovesForCounting(node)) {
            count += countQuarterTurns(move);
        }
        return count;
    }

    /**
     * Gets the face turn count of the specified node.
     */
    private static int countQuarterTurns(MoveNode move) {
        int qturns = abs(move.getAngle() % 4);
        if (qturns == 3) {
            qturns = 1;
        }
        return countFaceTurns(move) * qturns;
    }

    /**
     * Coalesces sequences of moves for counting.
     * <p>
     * Removes all non-twisting moves.
     * <p>
     * Coalesces sequences of moves on the same axis and layers into
     * a single move.
     * <p>
     * Coalesces sequences of moves on the same axis and angle and different layers into
     * a single move.
     *
     * @param node a (sub)tree of the AST
     * @return a list of coalesced moves
     */
    private static List<MoveNode> coalesceMovesForCounting(Node node) {
        List<MoveNode> result = new ArrayList<>();
        MoveNode prev = null;
        for (Node n : node.resolvedIterable(false)) {
            if (!(n instanceof MoveNode)) {
                continue;// filter for move nodes
            }
            MoveNode current = (MoveNode) n;
            int layerMask = current.getLayerMask();
            int layerCount = current.getLayerCount();
            int angle = current.getAngle();
            int allLayers = (1 << layerCount) - 1;
            int axis = current.getAxis();
            if (prev == null) {
                prev = current;
            } else {
                if (layerMask == 0 || angle == 0) {
                    // skip nop
                } else if (prev.getAxis() == axis && layerMask == allLayers) {
                    // skip cube rotation over same axis

                } else if (prev.getAxis() == axis && prev.getLayerMask() == layerMask) {
                    // coalesce subsequent move on same axis and same layer
                    prev = new MoveNode(layerCount, axis, layerMask, angle + prev.getAngle(),
                            prev.getStartPosition(), current.getEndPosition());
                } else if (prev.getAxis() == axis && prev.getAngle() == angle && (prev.getLayerMask() ^ layerMask) == 0) {
                    // coalesce subsequent move on same axis and angle and different layers
                    prev = new MoveNode(layerCount, axis, layerMask, angle + prev.getAngle(),
                            prev.getStartPosition(), current.getEndPosition());
                } else {
                    // cannot coalesce
                    if (isTwistMove(prev)) {
                        result.add(prev);
                    }
                    prev = current;
                }
            }
        }
        if (prev != null && isTwistMove(prev)) {
            result.add(prev);
        }
        return result;
    }

    /**
     * Returns true if the specified move twists layers.
     *
     * @param move a move
     * @return true if move twists layrser
     */
    private static boolean isTwistMove(MoveNode move) {
        int layerCount = move.getLayerCount();
        int turns = abs(move.getAngle()) % 4;
        int allLayers = (1 << (layerCount)) - 1;
        int layerMask = move.getLayerMask();

        return turns != 0 && layerMask != 0 && layerMask != allLayers;

    }
}
