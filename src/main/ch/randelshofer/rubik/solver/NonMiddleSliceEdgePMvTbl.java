/*
 * @(#)NonMiddleSliceEdgePermutationMoveTable.java  0.0  2000-07-01
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
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
 * @version 0.0 2000-07-01
 */
public class NonMiddleSliceEdgePMvTbl extends MoveTable {
    private KociembaCube kcube;

    public NonMiddleSliceEdgePMvTbl(KociembaCube cube) {
        super(cube, KociembaCube.NON_MIDDLE_SLICE_EDGE_PERMUTATIONS, true);
        kcube = cube;
    }

    protected int ordinalFromCubeState() {
        return kcube.getNonMiddleSliceEdgePermutation();
    }

    protected void ordinalToCubeState(int ordinal) {
        kcube.setNonMiddleSliceEdgePermutation(ordinal);
   }
}
