/*
 * @(#)MiddleSliceEdgePermutationMoveTable.java  0.0  2000-07-01
 *
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
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
