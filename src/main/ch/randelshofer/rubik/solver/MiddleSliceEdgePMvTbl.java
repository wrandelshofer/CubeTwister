/* @(#)MiddleSliceEdgePermutationMoveTable.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

/**
 * Phase 1 move mapping table for twists.
 *
 * This class has been derived from kocimovt.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MiddleSliceEdgePMvTbl extends MoveTable {
    private KociembaCube kcube;

    public MiddleSliceEdgePMvTbl(KociembaCube cube) {
        super(cube, KociembaCube.MIDDLE_SLICE_EDGE_PERMUTATIONS, true);
        kcube = cube;
    }

    protected int ordinalFromCubeState() {
        return kcube.getMiddleSliceEdgePermutation();
    }

    protected void ordinalToCubeState(int ordinal) {
        kcube.setMiddleSliceEdgePermutation(ordinal);
   }

}
