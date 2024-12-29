/*
 * @(#)CornerPermutationMoveTable.java
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
public class CornerPermutationMoveTable extends MoveTable {
    private KociembaCube kcube;

    public CornerPermutationMoveTable(KociembaCube cube) {
        super(cube, KociembaCube.CORNER_PERMUTATIONS, true);
        kcube = cube;
    }

    protected int ordinalFromCubeState() {
        return kcube.getCornerPermutation();
    }

    protected void ordinalToCubeState(int ordinal) {
        kcube.setCornerPermutation(ordinal);
   }
}
