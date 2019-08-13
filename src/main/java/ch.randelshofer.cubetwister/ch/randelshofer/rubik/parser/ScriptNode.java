/* @(#)SequenceNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Symbol;

/**
 * A SequenceNode holds a sequence of statements as its children A.
 * The side effect of a script node to a Cube is A.
 *
 * @author Werner Randelshofer
 */
public class ScriptNode extends Node {
        private final static long serialVersionUID = 1L;

    /**
     * Creates a script node with start position = 0
     * and end position = 0.
     */
    public ScriptNode() {
        this(-1, -1);
    }
    /**
     * Creates a script node which represents a symbol
     * at the indicated position in the source code.
     *  @param startpos The start position of the symbol.
     * @param endpos The end position of the symbol.
     */
    public ScriptNode(int startpos, int endpos) {
        super(Symbol.SEQUENCE, startpos, endpos);
        setAllowsChildren(true);
    }
    /**
     * Maps cube orientations to symbols.
     *
     * @see ch.randelshofer.rubik.RubiksCube#getCubeOrientation()
     */
    private final static Move[][] orientationToMoveMap = {
            // R U F L D B
            {}, // 0
            {Move.CR}, {Move.CR2}, {Move.CL}, // 1 x-axis
            {Move.CU}, {Move.CU2}, {Move.CD}, // 2 y-axis
            {Move.CF}, {Move.CF2}, {Move.CB}, // 3 z-axis
            {Move.CR, Move.CU}, {Move.CR, Move.CU2}, {Move.CR, Move.CD},
            {Move.CR2, Move.CU}, {Move.CR2, Move.CD}, // 4 corner-axis from ru to ld
            {Move.CL, Move.CU}, {Move.CL, Move.CU2}, {Move.CL, Move.CD},
            {Move.CR, Move.CF}, {Move.CR, Move.CB}, // 5 corner-axis from lu to rd
            {Move.CR2, Move.CF}, {Move.CR2, Move.CB}, // 8
            {Move.CL, Move.CF}, {Move.CL, Move.CB} // 9
            /*
             // F R D B L U
             {}, // 0
             {Move.CR}, {Move.CR2}, {Move.CL}, // 1
             {Move.CU}, {Move.CU2}, {Move.CD}, // 2
             {Move.CF}, {Move.CF2}, {Move.CB}, // 3
             {Move.CR, Move.CU}, {Move.CR, Move.CU2}, {Move.CR, Move.CD}, // 4
             {Move.CR2, Move.CU}, {Move.CR2, Move.CD}, // 5
             {Move.CL, Move.CU}, {Move.CL, Move.CU2}, {Move.CL, Move.CD}, // 6
             {Move.CR, Move.CF}, {Move.CR, Move.CB}, // 7
             {Move.CR2, Move.CF}, {Move.CR2, Move.CB}, // 8
             {Move.CL, Move.CF}, {Move.CL, Move.CB} // 9
             */};
    /**
     * Transformes the subtree starting at this node by the given cube
     * orientation. Does nothing if the orientation can not be done.
     */
    public void transformOrientation(int layerCount, int cubeOrientation, boolean inverse) {
        // XXX - This method currently only works for 3x3 cubes. We need to pass a cube as a parameter
        if (cubeOrientation >= 1) {
            if (inverse) {
                add(new NOPNode());
                if (orientationToMoveMap[cubeOrientation].length == 2) {
                    GroupingNode seq = new GroupingNode();
                    seq.add(new MoveNode( orientationToMoveMap[cubeOrientation][1].toInverse()));
                    seq.add(new MoveNode( orientationToMoveMap[cubeOrientation][0].toInverse()));
                    add(seq);
                } else {
                    add(new MoveNode( orientationToMoveMap[cubeOrientation][0].toInverse()));
                }
            } else {
                if (orientationToMoveMap[cubeOrientation].length == 2) {
                    GroupingNode seq = new GroupingNode();
                    seq.add(new MoveNode( orientationToMoveMap[cubeOrientation][0]));
                    seq.add(new MoveNode( orientationToMoveMap[cubeOrientation][1]));
                    insert(seq, 0);
                } else {
                    insert(new MoveNode( orientationToMoveMap[cubeOrientation][0]), 0);
                }
                insert(new NOPNode(), 1);
            }
        }
    }

}
