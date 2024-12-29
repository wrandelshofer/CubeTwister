/*
 * @(#)FlipMoveTable.java
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
public class FlipMoveTable extends MoveTable {
    private KociembaCube kcube;

    public FlipMoveTable(KociembaCube cube) {
        super(cube, KociembaCube.FLIPS, false);
        kcube = cube;
    }

    protected int ordinalFromCubeState() {
        return kcube.getFlip();
    }

    protected void ordinalToCubeState(int ordinal) {
        kcube.setFlip(ordinal);
   }
}
