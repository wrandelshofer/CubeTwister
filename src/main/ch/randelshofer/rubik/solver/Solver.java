/*
 * @(#)Solver.java  1.0.2  2003-03-16
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

import ch.randelshofer.gui.*;
import ch.randelshofer.rubik.parser.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * This class performs a two phase IDA* search for a solution
 * to the scrambled cube.
 * <p>
 * Phase 1 searches the group spanned by 'U,D,R,L,F,B' until
 * a configuration is discovered where the three coordinates
 * of twist, flip, and choice are "correct" with respect to a
 * solved cube.  This means that no edge cubie is twisted, no
 * corner cubie is flipped, and the four middle slice edge
 * cubies are in the middle slice (but not necessarily in their
 * correct permutation within that slice).  At this point, we
 * have found a member of an element of the phase two group.
 * <p>
 * Phase 2 uses the resulting phase 1 configuration as the
 * starting point for a search of the group spanned by
 * 'U,D,R2,L2,F2,B2', the goal being to reach 'I', the identity
 * (i.e. the solved configuration).  Note that this group
 * preserves the three coordinates of the phase 1 search since
 * in phase 2 it is impossible to alter the twist, flip, or
 * choice aspects of the cube.  The U, D moves do not alter corner
 * or edge parity and do not affect the choice of the middle slice
 * edge cubies.  The same is true for the R2, L2, F2, B2 moves.
 * This can be verified by considering the effect these moves
 * have on cube parity (see cube.cpp for details on the parity
 * frame of reference used).
 * <p>
 * In search parlance, the pruning tables (a.k.a. "pattern
 * databases) constitute an "admissible" heuristic.  This
 * means that they always underestimate the distance (i.e.
 * number of moves required) to reach the goal state.  It
 * can be proven that any search, such as IDA*, that examines
 * nodes at progressively increasing cost values and employs
 * an admissible heuristic is "optimal".  This means that the
 * first solution found by the search is guaranteed to be of
 * minimal distance required to reach the target, or goal,
 * state.
 * <p>
 * Since the search is split into two sequential IDA* search
 * phases, the optimality condition above does not always
 * hold.  However, since we allow the phase 1 search to
 * iteratively deepen, if let run long enough, it will
 * eventually deepen to the point where it is capable of
 * finding a complete solution.  At this point, we know we
 * have an optimal solution as we have degenerated to a
 * a single IDA* search of the cube space, but this takes
 * a very long time to occur.  The main strength of the
 * two phase search is that it finds a near optimal solution
 * very quickly and outputs successively better solutions
 * until it eventually finds one that is optimal.  In most
 * cases though, the search is terminated early (due to lack
 * of patience) once an "adequate" solution is found.
 * <p>
 * For more information concerning IDA* and admissibility,
 * see the paper "Depth-First Iterative-Deepening: An Optimal
 * Admissable Tree Search" by Richard E. Korf.  This paper
 * appears in volume 25 of "Artificial Intelligence" 1985,
 * pp. 97-109.  Also, there are many texts on AI (Artificial
 * Intelligence) or books on search techniques that cover
 * these topics in depth.
 * <p>
 * This class has been derived from solver.cpp and solver.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0.1 2002-12-23 Text provided to progress monitor improved.
 * <br>1.0 2000-09-22
 */
public class Solver {
    // Solver return codes

    /** A solution was not found. */
    public final static int NOT_FOUND = 0;
    /** A sub-optimal solution was found. */
    public final static int FOUND = 1;
    /** An optimal solution was found. */
    public final static int OPTIMUM_FOUND = 2;
    /** An absurdly large number. */
    private final static int HUGE = 10000;
    /**
     * The search was aborted.
     * (i.e. phase 2 did not yield an improved solution).
     */
    public final static int ABORT = 3;
    // Search variables for the two phase IDA* search
    /** Number of nodes expanded. */
    private int nodes;
    /** Current heuristic threshold (cutoff). */
    private int threshold1, threshold2;
    /** New threshold as determined by current search pass. */
    private int newThreshold1, newThreshold2;
    /** List of applied moves. */
    private int[] solutionMoves1 = new int[32], solutionMoves2 = new int[32];
    /** List of powers associated with each move. */
    private int[] solutionPowers1 = new int[32], solutionPowers2 = new int[32];
    /** Length of each solution. */
    private int solutionLength1, solutionLength2;
    /** Minimum solution length found so far. */
    private int minSolutionLength;
    /**
     * A cube used for two purposes:
     * 1- Initially used by the solver for initializing the move mapping tables
     * 2- Contains a copy of the scrambled cube that is used at the phase 1/phase 2
     * transition to compute the initial phase 2 coordinates.
     */
    private KociembaCube cube;
    /** Phase 1 move mapping table. */
    private static TwistMoveTable twistMoveTable;
    /** Phase 1 move mapping table. */
    private static FlipMoveTable flipMoveTable;
    /** Phase 1 move mapping table. */
    private static ChoiceMoveTable choiceMoveTable;
    /** Phase 2 move mapping table. */
    private static CornerPermutationMoveTable cornerPermutationMoveTable;
    /** Phase 2 move mapping table. */
    private static NonMiddleSliceEdgePMvTbl nonMiddleSliceEdgePermutationMoveTable;
    /** Phase 2 move mapping table. */
    private static MiddleSliceEdgePMvTbl middleSliceEdgePermutationMoveTable;
    /** Phase 1 pruning table. */
    private static PruningTable twistAndFlipPruningTable;
    /** Phase 1 pruning table. */
    private static PruningTable twistAndChoicePruningTable;
    /** Phase 1 pruning table. */
    private static PruningTable flipAndChoicePruningTable;
    /** Phase 2 pruning table. */
    private static PruningTable cornerAndSlicePruningTable;
    /** Phase 2 pruning table. */
    private static PruningTable edgeAndSlicePruningTable;
    /**
     * This flag is set to true, when the tables are initialized.
     */
    private static boolean isInitialized;
    /**
     * The solver returns a suboptimal solution after it has
     * expanded SEARCH_LIMIT nodes without finding the optimal
     * solution. Set a smaller limit if you think the solver
     * is too slow. Set to 1 if the solver shall return the
     * first solution only.
     */
    private final static int SEARCH_LIMIT = 10000000;
    /**
     * Progress monitor is used for visual feedback of the solver.
     */
    private ProgressObserver progressMonitor;
    /**
     * Holds the solution of the solver or null if no solution
     * has been found so far.
     */
    private SequenceNode solution;
    /**
     * This notation is used for output of intermediate results.
     */
    private Notation notation;

    /**
     * Initializes both the move mapping and pruning tables required
     * by the search.
     */
    public static synchronized void initializeTables(ProgressObserver pm) {
        if (isInitialized == false) {
            isInitialized = true;

            // Create a directory where the tables can be stored.
            File tableDir = new File(
                    System.getProperty("user.home")
                    + File.separatorChar + "Library"
                    + File.separatorChar + "Caches"
                    + File.separatorChar + "ch.randelshofer.cubetwister"
                    + File.separatorChar + "RubiksCube");
            tableDir.mkdirs();

            if (pm == null) {
                pm = new ProgressView("Initializing Solver", "Looking for cached tables...", 0, 64);
            }
            pm.setCancelable(false);
            KociembaCube kc = new KociembaCube();

            // Phase 1 move mapping tables
            twistMoveTable = new TwistMoveTable(kc);
            flipMoveTable = new FlipMoveTable(kc);
            choiceMoveTable = new ChoiceMoveTable(kc);

            // Phase 2 move mapping tables
            cornerPermutationMoveTable = new CornerPermutationMoveTable(kc);
            nonMiddleSliceEdgePermutationMoveTable = new NonMiddleSliceEdgePMvTbl(kc);
            middleSliceEdgePermutationMoveTable = new MiddleSliceEdgePMvTbl(kc);

            // Phase 1 pruning tables
            twistAndFlipPruningTable = new PruningTable(
                    twistMoveTable, flipMoveTable,
                    kc.getTwist(), kc.getFlip());
            twistAndChoicePruningTable = new PruningTable(
                    twistMoveTable, choiceMoveTable,
                    kc.getTwist(), kc.getChoice());
            flipAndChoicePruningTable = new PruningTable(
                    flipMoveTable, choiceMoveTable,
                    kc.getFlip(), kc.getChoice());

            // Phase 2 pruning tables
            cornerAndSlicePruningTable = new PruningTable(
                    cornerPermutationMoveTable, middleSliceEdgePermutationMoveTable,
                    kc.getCornerPermutation(), kc.getMiddleSliceEdgePermutation());
            edgeAndSlicePruningTable = new PruningTable(
                    nonMiddleSliceEdgePermutationMoveTable, middleSliceEdgePermutationMoveTable,
                    kc.getNonMiddleSliceEdgePermutation(), kc.getMiddleSliceEdgePermutation());

            // Phase 1 move mapping tables
            twistMoveTable.initialize(new File(tableDir, "Twist.mtb"), pm, "Twist move table:");
            pm.setProgress(1);

            flipMoveTable.initialize(new File(tableDir, "Flip.mtb"), pm, "Flip move table:");
            pm.setProgress(2);

            choiceMoveTable.initialize(new File(tableDir, "Choice.mtb"), pm, "Choice move table:");
            pm.setProgress(3);

            // Phase 2 move mapping tables
            //cornerPermutationMoveTable.initialize(new File(tableDir, "CrnrPerm.mtb"), pm, "Permutation move table: ");
            cornerPermutationMoveTable.initialize(new File(tableDir, "CrnrPerm.mtb"), pm, "Corner move table:");
            pm.setProgress(4);

            //nonMiddleSliceEdgePermutationMoveTable.initialize(new File(tableDir, "EdgePerm.mtb"), pm, "Non middle slice edge permutation move table: ");
            nonMiddleSliceEdgePermutationMoveTable.initialize(new File(tableDir, "EdgePerm.mtb"), pm, "Non middle slice edge move table:");
            pm.setProgress(5);

            //middleSliceEdgePermutationMoveTable.initialize(new File(tableDir, "SlicPerm.mtb"), pm, "Middle slice edge permutation move table: ");
            middleSliceEdgePermutationMoveTable.initialize(new File(tableDir, "SlicPerm.mtb"), pm, "Middle slice edge move table:");
            pm.setProgress(6);

            // Phase 1 pruning tables
            twistAndFlipPruningTable.initialize(new File(tableDir, "TwstFlip.ptb"), pm, "Twist and flip pruning table:");
            pm.setProgress(16);

            twistAndChoicePruningTable.initialize(new File(tableDir, "TwstChce.ptb"), pm, "Twist and choice pruning table:");
            pm.setProgress(26);

            flipAndChoicePruningTable.initialize(new File(tableDir, "FlipChce.ptb"), pm, "Flip and choice pruning table:");
            pm.setProgress(36);

            // Phase 2 pruning tables

            // Obviously a CornerAndEdgePruningTable doesn't make sense as it's size
            //   would be extremely large (i.e. 8!*8!)

            cornerAndSlicePruningTable.initialize(new File(tableDir, "CrnrSlic.ptb"), pm, "Corner and slice pruning table:");
            pm.setProgress(51);

            edgeAndSlicePruningTable.initialize(new File(tableDir, "EdgeSlic.ptb"), pm, "Edge and slice pruning table:");
            pm.setProgress(64);

            pm.complete();
            pm.close();
        }
    }

    /**
     * Perform the two phase search.
     *
     * @return  NOT_FOUND, FOUND, OPTIMUM_FOUND or ABORT.
     */
    public int solve(ProgressObserver progressMonitor, KociembaCube scrambledCube, Notation notation) {
        int iteration = 1;
        int result = NOT_FOUND;

        // Set up the notation and the progress monitor
        // both are used for output of intermediate results.
        this.notation = notation;
        this.progressMonitor = progressMonitor;
        progressMonitor.setNote("Initializing Solver...");
        progressMonitor.setMinimum(0);
        progressMonitor.setMaximum(SEARCH_LIMIT);

        // Initialize pruning tables
        initializeTables(null);
        progressMonitor.setNote("Searching...");

        // We haven't found anything yet
        solution = null;

        // Make a copy of the scrambled cube for use later on
        cube = (KociembaCube) scrambledCube.clone();

        // Any solution disovered will look better than this one!
        minSolutionLength = HUGE;

        // Establish initial cost estimate to goal state
        threshold1 = getPhase1Cost(cube.getTwist(), cube.getFlip(), cube.getChoice());

        solutionLength1 = 0;

        // Start counting expanded nodes here.
        nodes = 0;
        do {
            newThreshold1 = HUGE;  // Any cost will be less than this

            // Perform the phase 1 recursive IDA* search
            result = search1(cube.getTwist(), cube.getFlip(), cube.getChoice(), 0);

            // Establish a new threshold for a deeper search
            threshold1 = newThreshold1;

            // Count iterative deepenings
            iteration++;
        } while (result == NOT_FOUND);

        progressMonitor.complete();
        progressMonitor.close();
        if (progressMonitor.isCanceled()) {
            return ABORT;
        }
        return (result != OPTIMUM_FOUND && solution != null) ? FOUND : result;
    }

    /**
     * Returns the solution generated by solve().
     * @return The solution or null if the solver has been aborted.
     */
    public SequenceNode getSolution() {
        return solution;
    }

    /**
     * Remembers a solution found during the search.
     */
    private void rememberSolution() {
        int i;

        // Solution holds the current solution
        solution = new SequenceNode(notation.getLayerCount());

        // The StringBuilder is created for output on the progress monitor
        StringBuilder buf = new StringBuilder();


        for (i = 0; i < solutionLength1; i++) {
            int translatedMove = translateMove(solutionMoves1[i], solutionPowers1[i], false);
            solution.add(new MoveNode(notation.getLayerCount(), Cube.symbolOfMove(translatedMove)));

            buf.append(Cube.nameOfMove(translatedMove));
            buf.append(' ');
        }

        solution.add(new NOPNode(notation.getLayerCount()));
        buf.append(". ");
        for (i = 0; i < solutionLength2; i++) {
            int translatedMove = translateMove(solutionMoves2[i], solutionPowers2[i], true);
            solution.add(new MoveNode(notation.getLayerCount(), Cube.symbolOfMove(translatedMove)));

            buf.append(Cube.nameOfMove(translatedMove));
            buf.append(' ');
        }

        buf.append('(');
        buf.append(Integer.toString(solutionLength1 + solutionLength2));
        buf.append('f');
        if (solutionLength2 == 0) {
            buf.append('*');
        }
        buf.append(')');

        // Track search progress
        //  progressMonitor.setNote("Found: "+buf.toString());
        //try {
        String s;
        try {
            s = solution.toString(notation);
        } catch (IOException e) {
            s = solution.toString();
        }
        progressMonitor.setNote(
                "Found: (" + (solutionLength1 + solutionLength2)
                + ((solutionLength2 == 0) ? "f*) " : "f) ")
                + s);
        /*
        } catch (IOException e) {
        progressMonitor.setNote(e.getMessage());
        }*/
    }

    /** Initiates the second phase of the search. */
    private int solve2(KociembaCube cube) {
        int iteration = 1;
        int result = NOT_FOUND;

        // Track search progress
        progressMonitor.setProgress(nodes);

        // Establish initial cost estimate to goal state
        threshold2 = getPhase2Cost(
                cube.getCornerPermutation(),
                cube.getNonMiddleSliceEdgePermutation(),
                cube.getMiddleSliceEdgePermutation());

        solutionLength2 = 0;

        do {
            newThreshold2 = HUGE;  // Any cost will be less than this

            // Perform the phase 2 recursive IDA* search
            result = search2(
                    cube.getCornerPermutation(),
                    cube.getNonMiddleSliceEdgePermutation(),
                    cube.getMiddleSliceEdgePermutation(),
                    0);

            // Establish a new threshold for a deeper search
            threshold2 = newThreshold2;

            // Count iterative deepenings
            iteration++;
        } while (result == NOT_FOUND);

        return result;
    }

    /** Phase 1 recursive IDA* search routines. */
    private int search1(int twist, int flip, int choice, int depth) {
        int cost, totalCost;
        int move;
        int power;
        int twist2, flip2, choice2;
        int result;

        // Compute cost estimate to phase 1 goal state
        cost = getPhase1Cost(twist, flip, choice);  // h

        if (cost == 0) {
            // Phase 1 solution found...

            solutionLength1 = depth;  // Save phase 1 solution length

            // We need an appropriately initialized cube in order
            //   to begin phase 2.  First, create a new cube that
            //   is a copy of the initial scrambled cube.  Then we
            //   apply the phase 1 move sequence to that cube.  The
            //   phase 2 search can then determine the initial
            //   phase 2 coordinates (corner, edge, and slice
            //   permutation) from this cube.
            //
            //   Note: No attempt is made to merge moves of the same
            //   face adjacent to the phase 1 & phase 2 boundary since
            //   the shorter sequence will quickly be found.

            KociembaCube phase2Cube = (KociembaCube) cube.clone();
            for (int i = 0; i < solutionLength1; i++) {
                for (power = 0; power < solutionPowers1[i]; power++) {
                    phase2Cube.applyMove(solutionMoves1[i]);
                }
            }
            // Invoke Phase 2
            result = solve2(phase2Cube);
            // Return if we found the optimum or if we found a solution in phase 2 exclusively
            // (that is depth 0 in phase 1).
            if (result == OPTIMUM_FOUND || result == FOUND && depth == 0) {
                return result;
            }

            // Abort if search takes too long
            if ((nodes > SEARCH_LIMIT && solution != null) || progressMonitor.isCanceled()) {
                return ABORT;
            }
        }

        // See if node should be expanded
        totalCost = depth + cost;  // g + h

        if (totalCost <= threshold1) {
            // Expand node

            // If this happens, we should have found the
            //   optimal solution at this point, so we
            //   can exit indicating such.  Note: the first
            //   complete solution found in phase1 is optimal
            //   due to it being an addmissible IDA* search.
            if (depth >= minSolutionLength) {
                return OPTIMUM_FOUND;
            }

            for (move = Cube.R; move <= Cube.B; move++) {
                if (isDisallowed(move, solutionMoves1, depth)) {
                    continue;
                }

                twist2 = twist;
                flip2 = flip;
                choice2 = choice;

                solutionMoves1[depth] = move;
                for (power = 1; power < 4; power++) {
                    solutionPowers1[depth] = power;
                    twist2 = twistMoveTable.get(twist2, move);
                    flip2 = flipMoveTable.get(flip2, move);
                    choice2 = choiceMoveTable.get(choice2, move);
                    nodes++;

                    // Apply the move
                    result = search1(twist2, flip2, choice2, depth + 1);
                    if (result != NOT_FOUND) {
                        return result;
                    }
                }
            }
        } else {
            // Maintain minimum cost exceeding threshold

            if (totalCost < newThreshold1) {
                newThreshold1 = totalCost;
            }
        }
        return NOT_FOUND;
    }

    /** Phase 2 recursive IDA* search routines. */
    private int search2(
            int cornerPermutation,
            int nonMiddleSliceEdgePermutation,
            int middleSliceEdgePermutation,
            int depth) {

        int cost, totalCost;
        int move;
        int power, powerLimit;
        int cornerPermutation2;
        int nonMiddleSliceEdgePermutation2;
        int middleSliceEdgePermutation2;
        int result;

        // Compute cost estimate to goal state
        cost = getPhase2Cost(
                cornerPermutation,
                nonMiddleSliceEdgePermutation,
                middleSliceEdgePermutation // h
                );

        if (cost == 0) {
            // Solution found...
            solutionLength2 = depth;  // Save phase 2 solution length
            if (solutionLength1 + solutionLength2 < minSolutionLength) {
                minSolutionLength = solutionLength1 + solutionLength2;
            }
            rememberSolution();
            return (solutionLength2 == 0) ? OPTIMUM_FOUND : FOUND;
        }

        // See if node should be expanded
        totalCost = depth + cost;  // g + h

        if (totalCost <= threshold2) {
            // Expand node

            // No point in continuing to search for solutions of equal or greater
            //   length than the current best solution
            if (solutionLength1 + depth >= minSolutionLength - 1) {
                return ABORT;
            }

            for (move = Cube.R; move <= Cube.B; move++) {
                if (isDisallowed(move, solutionMoves2, depth)) {
                    continue;
                }

                cornerPermutation2 = cornerPermutation;
                nonMiddleSliceEdgePermutation2 = nonMiddleSliceEdgePermutation;
                middleSliceEdgePermutation2 = middleSliceEdgePermutation;

                solutionMoves2[depth] = move;
                powerLimit = 4;
                if (move != Cube.U && move != Cube.D) {
                    powerLimit = 2;
                }

                for (power = 1; power < powerLimit; power++) {
                    cornerPermutation2 =
                            cornerPermutationMoveTable.get(cornerPermutation2, move);
                    nonMiddleSliceEdgePermutation2 =
                            nonMiddleSliceEdgePermutationMoveTable.get(nonMiddleSliceEdgePermutation2, move);
                    middleSliceEdgePermutation2 =
                            middleSliceEdgePermutationMoveTable.get(middleSliceEdgePermutation2, move);

                    solutionPowers2[depth] = power;
                    nodes++;

                    // Apply the move
                    result = search2(
                            cornerPermutation2,
                            nonMiddleSliceEdgePermutation2,
                            middleSliceEdgePermutation2, depth + 1);
                    if (result != NOT_FOUND) {
                        return result;
                    }
                }
            }
        } else {
            // Maintain minimum cost exceeding threshold

            if (totalCost < newThreshold2) {
                newThreshold2 = totalCost;
            }
        }
        return NOT_FOUND;
    }

    /** Phase 1 cost heuristics. */
    private int getPhase1Cost(int twist, int flip, int choice) {
        // Combining admissible heuristics by taking their maximum
        //   produces an improved admissible heuristic.
        int cost = twistAndFlipPruningTable.getValue(twist * flipMoveTable.size() + flip);
        int cost2 = twistAndChoicePruningTable.getValue(twist * choiceMoveTable.size() + choice);
        if (cost2 > cost) {
            cost = cost2;
        }
        cost2 = flipAndChoicePruningTable.getValue(flip * choiceMoveTable.size() + choice);

        return (cost2 > cost) ? cost2 : cost;
    }

    /** Phase 2 cost heuristics. */
    private int getPhase2Cost(
            int cornerPermutation,
            int nonMiddleSliceEdgePermutation,
            int middleSliceEdgePermutation) {

        // Combining admissible heuristics by taking their maximum
        //   produces an improved admissible heuristic.
        int cost = cornerAndSlicePruningTable.getValue(
                cornerPermutation * middleSliceEdgePermutationMoveTable.size() + middleSliceEdgePermutation);
        int cost2 = edgeAndSlicePruningTable.getValue(
                nonMiddleSliceEdgePermutation * middleSliceEdgePermutationMoveTable.size() + middleSliceEdgePermutation);
        return (cost2 > cost) ? cost2 : cost;
    }

    /**
     * Predicate to determine if a move is redundant (leads to
     * (a node that is explored elsewhere) and should therefore
     * be disallowed.
     */
    private boolean isDisallowed(int move, int[] solutionMoves, int depth) {
        if (depth > 0) {

            // Disallow successive moves of a single face (RR2 is same as R')
            if (solutionMoves[depth - 1] == move) {
                return true;
            }

            //   Disallow a move of an opposite face if the current face
            //     moved is B,L, or D. (BF, LR, DU are same as FB,RL,UD)
            if ((move == Cube.F) && solutionMoves[depth - 1] == Cube.B) {
                return true;
            }
            if ((move == Cube.R) && solutionMoves[depth - 1] == Cube.L) {
                return true;
            }
            if ((move == Cube.U) && solutionMoves[depth - 1] == Cube.D) {
                return true;
            }

            // Disallow 3 or more consecutive moves of opposite faces
            //   (UDU is same as DU2 and U2D)
            if ((depth > 1) && solutionMoves[depth - 2] == move
                    && solutionMoves[depth - 1] == Cube.opposingFace(move)) {

                return true;
            }
        }
        return false;  // This move is allowed
    }

    /**
     * Translates moves from a (face, power) representation to a
     * single move string representation (e.g. R,3 becomes R').
     * Also if the move was applied during phase 2 and is either
     * R,L,F, or B, then a power of 2 is assumed.  This is done
     * since the phase 2 move mapping tables are in terms of half
     * turn moves for R,L,F, and B and the power used is 1, not 2.
     * In this way, we do not have to burden the phase 2 search with
     * determining the correct power for display purposes only.
     * I hope that's clear.
     */
    private int translateMove(int move, int power, boolean phase2) {
        int translatedMove = move;

        if (phase2 && move != Cube.U && move != Cube.D) {
            power = 2;
        }

        if (power == 2) {
            translatedMove = Cube.quarterTurnToHalfTurnMove(move);
        } else if (power == 3) {
            translatedMove = Cube.inverseOfMove(move);
        }

        return translatedMove;
    }
}
