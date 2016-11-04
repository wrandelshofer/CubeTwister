/*
 * @(#)Cube.java  0.0  2000-07-01
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

import ch.randelshofer.rubik.parser.*;
import java.util.Arrays;
/**
 * A cube model containing basic cube definitions and
 * cube operations.  Applying a move maintains the
 * proper cube permutation and orientation for each
 * cubie. This class is designed to be subclassed in
 * order to extend it to a specific type of cube
 * useful for a particular solution method.
 * <p>
 * "x" denotes the chief facelets of cubicles. Cube faces not shown are
 * symmetrical to the opposing face.
 * <pre>
 *                              ____ ____ ____
 *                            /_x_ /_x_ /_x_ /|
 *                          /_x_ /_U_ /_x_ /| |
 *                        / x  / x  / x  /| |/|
 *                        ---- ---- ----  |/|x|
 *                       |    |    |    |/|R|/|
 *                        ---- ---- ---- x|/| |
 *                       |    | F  |    |/| |/
 *                        ---- ---- ----  |/
 *                       |    |    |    |/
 *                        ---- ---- ----
 * </pre>
 * An edge cubie is sane if its chief facelet aligns with the chief
 * facelet of its current cubicle, otherwise it is flipped (MetaMagical
 * Themas - Hofstadter).
 * <p>
 * The orientation of a corner cubie can be determined by the number of
 * 120 degree counter-clockwise twists required to align its chief
 * facelet into a position that is parallel to the chief facelet of
 * its home cubicle (August/September cube.lovers - Vanderschel/Saxe)
 * <p>
 * This class has been derived from cube.cpp and cube.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * <p>
 * @author Werner Randelshofer
 * @version 0.0 2000-07-01
 */
public class Cube implements Cloneable {
    // Cubie locations

    // Edge locations
    public final static int UF = 0;
    public final static int UL = 1;
    public final static int UB = 2;
    public final static int UR = 3;
    public final static int FU = 0;
    public final static int LU = 1;
    public final static int BU = 2;
    public final static int RU = 3;

    public final static int DF = 4;
    public final static int DL = 5;
    public final static int DB = 6;
    public final static int DR = 7;
    public final static int FD = 4;
    public final static int LD = 5;
    public final static int BD = 6;
    public final static int RD = 7;

    // Middle slice edges begin here
    public final static int RF = 8;
    public final static int FL = 9;
    public final static int LB = 10;
    public final static int BR = 11;
    public final static int FR = 8;
    public final static int LF = 9;
    public final static int BL = 10;
    public final static int RB = 11;

    public final static int FIRST_EDGE_CUBIE = UF;
    public final static int LAST_EDGE_CUBIE = BR;
    public final static int FIRST_MIDDLE_SLICE_EDGE_CUBIE = RF;
    public final static int LAST_MIDDLE_SLICE_EDGE_CUBIE = BR;
    public final static int NUMBER_OF_EDGE_CUBIES = LAST_EDGE_CUBIE + 1;

    // Corner locations
    public final static int URF = 0;
    public final static int UFL = 1;
    public final static int ULB = 2;
    public final static int UBR = 3;
    public final static int RFU = 0;
    public final static int FLU = 1;
    public final static int LBU = 2;
    public final static int BRU = 3;
    public final static int FUR = 0;
    public final static int LUB = 1;
    public final static int BUL = 2;
    public final static int RUB = 3;

    public final static int DFR = 4;
    public final static int DLF = 5;
    public final static int DBL = 6;
    public final static int DRB = 7;
    public final static int FRD = 4;
    public final static int LFD = 5;
    public final static int BLD = 6;
    public final static int RBD = 7;
    public final static int RDF = 4;
    public final static int FDL = 5;
    public final static int LDB = 6;
    public final static int BDR = 7;

    public final static int FIRST_CORNER_CUBIE = URF;
    public final static int LAST_CORNER_CUBIE = DRB;
    public final static int NUMBER_OF_CORNER_CUBIES = LAST_CORNER_CUBIE + 1;

    // Applies to all cubies
    public final static int INVALID_CUBIE = LAST_EDGE_CUBIE + 1;

    // Twists
    public final static int NO_QUARK = 0;
    public final static int QUARK = 1;
    public final static int ANTI_QUARK = 2;
    public final static int NUMBER_OF_TWISTS = ANTI_QUARK + 1;

    // Flips
    // Note: Must be 0 and 1 because of implementation
    // of method flip()!
    public final static int NOT_FLIPPED = 0;
    public final static int FLIPPED = 1;

    // Quarter and half turn moves
    public final static int R = 0;
    public final static int L = 1;
    public final static int U = 2;
    public final static int D = 3;
    public final static int F = 4;
    public final static int B = 5;

    public final static int RI = 6;
    public final static int LI = 7;
    public final static int UI = 8;
    public final static int DI = 9;
    public final static int FI = 10;
    public final static int BI = 11;

    public final static int R2 = 12;
    public final static int L2 = 13;
    public final static int U2 = 14;
    public final static int D2 = 15;
    public final static int F2 = 16;
    public final static int B2 = 17;

    public final static int FIRST_MOVE = R;
    public final static int LAST_MOVE = B2;
    public final static int NUMBER_OF_CLOCKWISE_QUARTER_TURN_MOVES = B + 1;
    public final static int NUMBER_OF_MOVES = LAST_MOVE + 1;
    public final static int INVALID_MOVE = LAST_MOVE + 1;

    /** The cubies. */
    protected int[] cornerCubiePermutations = new int[NUMBER_OF_CORNER_CUBIES];
    protected int[] cornerCubieOrientations = new int[NUMBER_OF_CORNER_CUBIES];

    protected int[] edgeCubiePermutations = new int[NUMBER_OF_EDGE_CUBIES];
    protected int[] edgeCubieOrientations = new int[NUMBER_OF_EDGE_CUBIES];


    // Move tables
    private final static int[] CLOCKWISE_TWISTS = {
        QUARK, ANTI_QUARK, NO_QUARK
    };

    private final static int[] COUNTER_CLOCKWISE_TWISTS = {
        ANTI_QUARK, NO_QUARK, QUARK
    };


    /** Names. */
    private final static String[] MOVE_NAMES = {
        "R",  "L",  "U",  "D",  "F",  "B",
        "R'", "L'", "U'", "D'", "F'", "B'",
        "R2", "L2", "U2", "D2", "F2", "B2"
    };

    /** 
     * Symbols.
     * XXX - This is deprecated.
     * @see ch.randelshofer.rubik.parser.ScriptParser
     */
    private final static int[] MOVE_SYMBOLS = {
        R,  L, U, D, F, B,
        RI, LI, UI, DI, FI, BI,
        R2, L2, U2, D2, F2, B2
    };

    /** Move inverse. */
    private final static int[] INVERSE_MOVES = {
        RI, LI, UI, DI, FI, BI,
        R,  L,  U,  D,  F,  B,
        R2, L2, U2, D2, F2, B2
    };

    /** Opposing faces. */
    private final static int[] OPPOSITE_FACES = {
        L,  R,  D,  U,  B,  F
    };

    /** 
     * Move Parser Symbols for the ScriptParer.
     * @see ch.randelshofer.rubik.parser.ScriptParser
     */
    private final static Move[] MOVE_PARSER_SYMBOLS = {
        Move.R,  Move.L, Move.U, Move.D, Move.F, Move.B,
        Move.RI, Move.LI, Move.UI, Move.DI, Move.FI, Move.BI,
        Move.R2, Move.L2, Move.U2, Move.D2, Move.F2, Move.B2
    };

    /** Default constructor. */
    public Cube() {
        backToHome();
    }

    /** Overriden equality test method. */
    public boolean equals(Object o) {
        return (o instanceof Cube) ? equals((Cube) o) : false;
    }
    /** Overloaded equality test method. */
    public boolean equals(Cube cube) {
        return Arrays.equals(this.cornerCubiePermutations, cube.cornerCubiePermutations)
            && Arrays.equals(this.cornerCubieOrientations, cube.cornerCubieOrientations)
            && Arrays.equals(this.edgeCubiePermutations, cube.edgeCubiePermutations)
            && Arrays.equals(this.edgeCubieOrientations, cube.edgeCubieOrientations);
    }

    /** Overriden hashCode method. */
    public int hashCode() {
        int hash = 0;
        for (int i=0; i < NUMBER_OF_CORNER_CUBIES; i++) {
            hash = hash << 1 + cornerCubiePermutations[i];
        }
        for (int i=0; i < NUMBER_OF_EDGE_CUBIES; i++) {
            hash = hash << 1 + edgeCubiePermutations[i];
        }
        return hash;
    }

    /** Reset cube back to HOME position. */
    public void backToHome() {
        int cubie;
        for (cubie = FIRST_EDGE_CUBIE; cubie <= LAST_EDGE_CUBIE; cubie++) {
            edgeCubiePermutations[cubie] = cubie;
            edgeCubieOrientations[cubie] = NOT_FLIPPED;
        }
        for (cubie = FIRST_CORNER_CUBIE; cubie <= LAST_CORNER_CUBIE; cubie++) {
            cornerCubiePermutations[cubie] = cubie;
            cornerCubieOrientations[cubie] = NO_QUARK;
        }
    }


    /** Set state from permutation and orientation vectors */
    public void setState(
            int[] cornerPermutation, int[] cornerOrientation,
            int[] edgePermutation, int[] edgeOrientation) {

        int cubie;
        for (cubie = FIRST_EDGE_CUBIE; cubie <= LAST_EDGE_CUBIE; cubie++) {
                this.edgeCubiePermutations[cubie] = edgePermutation[cubie];
                this.edgeCubieOrientations[cubie] = edgeOrientation[cubie];
        }
        for (cubie = FIRST_CORNER_CUBIE; cubie <= LAST_CORNER_CUBIE; cubie++) {
                this.cornerCubiePermutations[cubie] = cornerPermutation[cubie];
                this.cornerCubieOrientations[cubie] = cornerOrientation[cubie];
        }
    }

    /** Apply move. */
    public void applyMove(int move) {
        switch (move) {
        case R:   moveR();       break;
        case U:   moveU();       break;
        case L:   moveL();       break;
        case D:   moveD();       break;
        case F:   moveF();       break;
        case B:   moveB();       break;
        case RI:  moveRI();      break;
        case LI:  moveLI();      break;
        case UI:  moveUI();      break;
        case DI:  moveDI();      break;
        case FI:  moveFI();      break;
        case BI:  moveBI();      break;
        case R2:  moveR2();      break;
        case L2:  moveL2();      break;
        case U2:  moveU2();      break;
        case D2:  moveD2();      break;
        case F2:  moveF2();      break;
        case B2:  moveB2();      break;
        }
    }


    /** Get inverse of move. */
    public static int inverseOfMove(int move) {
        return INVERSE_MOVES[move];
    }

    /** Turns a quarter turn move to a half turn move (e.g. R and Ri become R2). */
    public static int quarterTurnToHalfTurnMove(int move) {
        return R2 + move % (B + 1);
    }

    /** Get opposite face of a move. */
    public static int opposingFace(int move) {
        return OPPOSITE_FACES[move];
    };

    /** Get the name of a move. */
    public static String nameOfMove(int move) {
        return MOVE_NAMES[move];
    }

    /** Get the move from the move name. */
    public static int moveNameToMove(String moveName) {
        for (int i = 0; i < NUMBER_OF_MOVES; i++) {
            if (moveName.equals(MOVE_NAMES[i])) {
                return i;
            }
        }
        return INVALID_MOVE;
    }

    /** 
     * Get the ScriptParser twist from a move.
     * @see ch.randelshofer.rubik.parser.ScriptParser
     */
    public static Move symbolOfMove(int move) {
        return MOVE_PARSER_SYMBOLS[move];
    }


    /** Dump cube state. */
    public void dump() {
        System.out.println(toString());
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Cube(\n  cornerPerm:{");
        for (int i=0; i < NUMBER_OF_CORNER_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubiePermutations[i]);
        }
        b.append("}\n  cornerOrient:{");
        for (int i=0; i < NUMBER_OF_CORNER_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(cornerCubieOrientations[i]);
        }
        b.append("}\n  edgePerm:{");
        for (int i=0; i < NUMBER_OF_EDGE_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(edgeCubiePermutations[i]);
        }
        b.append("}\n  edgeOrient:{");
        for (int i=0; i < NUMBER_OF_EDGE_CUBIES; i++) {
            if (i > 0) { b.append(','); }
            b.append(edgeCubieOrientations[i]);
        }
        b.append("}\n)\n)");
        return b.toString();
    }


// Cube moves
    protected void moveR() {
        fourCycleCorner(URF, UBR, DRB, DFR);

        clockwiseTwist       (URF);
        counterClockwiseTwist(UBR);
        clockwiseTwist       (DRB);
        counterClockwiseTwist(DFR);

        fourCycleEdge(UR, BR, DR, RF);

        flip(UR);       flip(BR);       flip(DR);       flip(RF);
    }
    protected void moveL() {
        fourCycleCorner(ULB, UFL, DLF, DBL);

        clockwiseTwist       (ULB);
        counterClockwiseTwist(UFL);
        clockwiseTwist       (DLF);
        counterClockwiseTwist(DBL);

        fourCycleEdge(UL, FL, DL, LB);

        flip(UL);       flip(FL);       flip(DL);       flip(LB);
    }

    protected void moveU() {
        fourCycleCorner(ULB, UBR, URF, UFL);

        fourCycleEdge(UB, UR, UF, UL);
    }

    protected void moveD() {
        fourCycleCorner(DLF, DFR, DRB, DBL);

        fourCycleEdge(DF, DR, DB, DL);
    }

    protected void moveF() {
        fourCycleCorner(UFL, URF, DFR, DLF);

        clockwiseTwist       (UFL);
        counterClockwiseTwist(URF);
        clockwiseTwist       (DFR);
        counterClockwiseTwist(DLF);

        fourCycleEdge(UF, RF, DF, FL);
    }

    protected void moveB() {
        fourCycleCorner(UBR, ULB, DBL, DRB);

        clockwiseTwist       (UBR);
        counterClockwiseTwist(ULB);
        clockwiseTwist       (DBL);
        counterClockwiseTwist(DRB);

        fourCycleEdge(UB, LB, DB, BR);
    }

    protected void moveRI() {
        fourCycleCorner(UBR, URF, DFR, DRB);

        counterClockwiseTwist(UBR);
        clockwiseTwist       (URF);
        counterClockwiseTwist(DFR);
        clockwiseTwist       (DRB);

        fourCycleEdge(UR, RF, DR, BR);

        flip(UR);       flip(RF);       flip(DR);       flip(BR);
    }

    protected void moveLI() {
        fourCycleCorner(UFL, ULB, DBL, DLF);

        counterClockwiseTwist(UFL);
        clockwiseTwist       (ULB);
        counterClockwiseTwist(DBL);
        clockwiseTwist       (DLF);

        fourCycleEdge(UL, LB, DL, FL);

        flip(UL);       flip(LB);       flip(DL);       flip(FL);
    }

    protected void moveUI() {
        fourCycleCorner(UBR, ULB, UFL, URF);

        fourCycleEdge(UB, UL, UF, UR);
    }

    protected void moveDI() {
        fourCycleCorner(DFR, DLF, DBL, DRB);

        fourCycleEdge(DF, DL, DB, DR);
    }

    protected void moveFI() {
        fourCycleCorner(URF, UFL, DLF, DFR);

        counterClockwiseTwist(URF);
        clockwiseTwist       (UFL);
        counterClockwiseTwist(DLF);
        clockwiseTwist       (DFR);

        fourCycleEdge(UF, FL, DF, RF);
    }

    protected void moveBI() {
        fourCycleCorner(ULB, UBR, DRB, DBL);

        counterClockwiseTwist(ULB);
        clockwiseTwist       (UBR);
        counterClockwiseTwist(DRB);
        clockwiseTwist       (DBL);

        fourCycleEdge(UB, BR, DB, LB);
    }

    protected void moveR2() {
        moveR(); moveR();
    }

    protected void moveL2() {
        moveL(); moveL();
    }

    protected void moveU2() {
        moveU(); moveU();
    }

    protected void moveD2() {
        moveD(); moveD();
    }

    protected void moveF2() {
        moveF(); moveF();
    }

    protected void moveB2() {
        moveB(); moveB();
    }


    /** Cycle four edge cubies. */
    protected void fourCycleEdge(
                        int first,
                        int second,
                        int third,
                        int fourth) {
        cycleFour(edgeCubiePermutations, first, second, third, fourth);
        cycleFour(edgeCubieOrientations, first, second, third, fourth);
    }


    /** Cycle four corner cubies. */
    protected void fourCycleCorner(
                        int first,
                        int second,
                        int third,
                        int fourth) {
        cycleFour(cornerCubiePermutations, first, second, third, fourth);
        cycleFour(cornerCubieOrientations, first, second, third, fourth);
    }


    /** Cycle four vector elements. */
    protected void cycleFour(int[] vector,
                   int first,
                   int second,
                   int third,
                   int fourth) {
        int temp       = vector[fourth];
        vector[fourth] = vector[third];
        vector[third]  = vector[second];
        vector[second] = vector[first];
        vector[first]  = temp;
    }


    /** Flip an edge cubie. */
    protected void flip(int edgeCubie) {
        edgeCubieOrientations[edgeCubie] ^= 1;
    }



    /** Corner cubie twists. */
    protected void clockwiseTwist(int cornerCubie) {
        // Note: the same effect could be accomplished by:
        //   CornerCubieOrientations[cubie] = (CornerCubieOrientations[cubie]+1)%3;
        //   but for some reason, I prefer the lookup table approach.
        cornerCubieOrientations[cornerCubie] = CLOCKWISE_TWISTS[cornerCubieOrientations[cornerCubie]];
    }


    protected void counterClockwiseTwist(int cornerCubie) {
        cornerCubieOrientations[cornerCubie] = COUNTER_CLOCKWISE_TWISTS[cornerCubieOrientations[cornerCubie]];
    }

    /**
     * Clones the cube.
     */
    @Override
    public Cube clone() {
        try {
            Cube that = (Cube) super.clone();

            that.cornerCubiePermutations = cornerCubiePermutations.clone();
            that.cornerCubieOrientations = cornerCubieOrientations.clone();
            that.edgeCubiePermutations = edgeCubiePermutations.clone();
            that.edgeCubieOrientations = edgeCubieOrientations.clone();

            return that;

        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }
}
