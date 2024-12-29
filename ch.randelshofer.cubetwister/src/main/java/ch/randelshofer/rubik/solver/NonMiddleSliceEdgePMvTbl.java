/*
 * @(#)NonMiddleSliceEdgePMvTbl.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.solver;

/**
 * Phase 1 move mapping table for twists.
 *
 * This class has been derived from kocimovt.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
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
