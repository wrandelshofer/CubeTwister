/* @(#)MoveTable.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

import ch.randelshofer.gui.ProgressObserver;
import org.jhotdraw.annotation.Nonnull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
/**
 * An abstract base class used for creating move mapping
 * tables.  Functions for converting between an ordinal
 * and its associated cube state must be overridden in
 * the derived class.
 *
 * This class has been derived from movetabl.cpp and movetabl.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 */
public abstract class MoveTable extends Object {
    private final static boolean DEBUG = false;
    private Cube cube;
    
    /** Number of entries in the pruning table. */
    private int tableSize;
    private boolean isPhase2;
    
    /** The table pointer. */
    private int[][] table;
    
    /**
     * The constructor must be provided with a cube to be
     * manipulated during table generation, the size
     * of the table (number of entries), and whether
     * or not the table is a phase 2 table.  If the
     * table is a phase 2 table, then only quarter
     * turn moves are allowed for F,B,L, and R.
     */
    public MoveTable(Cube cube, int tableSize, boolean phase2) {
        this.cube = cube;
        this.tableSize = tableSize;
        this.isPhase2 = phase2;
        table = new int[tableSize][Cube.NUMBER_OF_CLOCKWISE_QUARTER_TURN_MOVES];
    }
    
    public MoveTable(Cube cube, int tableSize) {
        this(cube, tableSize, false);
    }

    /**
     * Initialize the pruning table by either generating it
     * or loading it from an existing file.
     */
    public void initialize(@Nonnull File file, @Nonnull ProgressObserver pm, String tableName) {
        if (!file.exists()) {
            // If the move mapping table file is absent...
            // Generate the table and save it to a file
            pm.setNote(MessageFormat.format("Generating {0}.", new Object[]{tableName}));
            generate(pm);
            pm.setNote(MessageFormat.format("Saving {0} to {1}.", new Object[]{tableName, file.getName()}));
            try {
                OutputStream out = new FileOutputStream(file);
                save(out);
                out.close();
                pm.setNote(MessageFormat.format("Done Saving {0}.", new Object[] {tableName}));
            } catch (IOException e) {
                pm.setNote(MessageFormat.format("Unable to save {0}: {1}.", new Object[] {tableName, e.getMessage()}));
            }
        } else {
            // The move mapping table file exists
            // Load the existing file
            pm.setNote(MessageFormat.format("Loading {0} from {1}.", new Object[] {tableName, file.getName()}));
            try {
                InputStream in = new FileInputStream(file);
                load(in);
                in.close();
            } catch (IOException e) {
                pm.setNote(MessageFormat.format("Unable to load {0}: {1}", new Object[] {tableName, file.getName()}));
                pm.setNote(MessageFormat.format("Generating {0}.", new Object[] {tableName}));
                generate(pm);
            }
        }
    }
    
    /**
     * Overloaded subscript operator allows standard C++ indexing
     * (i.e. MoveTable[i][j]) for accessing table values.
     */
    public int get(int ordinal, int move) {
        return table[ordinal][move];
    }
    
    /**
     * Obtain the size of the table (number of logical entries).
     */
    public int size() {
        return tableSize;
    }
    
    /**
     * Dump table contents.
     */
    public void dump() {
        int ordinal;
        int move, move2;
        // For each table entry...
        for (ordinal = 0; ordinal < tableSize; ordinal++) {
            System.out.print(ordinal + ": ");
            // For each possible move...
            for (move = Cube.R; move <= Cube.B; move++) {
                move2 = move;
                if (isPhase2) {
                    if (move != Cube.U && move != Cube.D) {
                        move2 = Cube.quarterTurnToHalfTurnMove(move);
                    }
                }
                System.out.print(Cube.nameOfMove(move2) + ":" + table[ordinal][move] + " ");
            }
            System.out.println();
        }
    }
    
    
    // These functions must be overloaded in the derived
    //   class in order to provide the appropriate mapping
    //   between ordinal and cube state.
    protected abstract int ordinalFromCubeState();
    protected abstract void ordinalToCubeState(int ordinal);
    
    /** Generate the table. */
    private void generate(ProgressObserver  pm) {
        int ordinal;
        int move, move2;
        
        // Insure the cubies are in their proper slice
        cube.backToHome();
        
        // Initialize each table entry
        for (ordinal = 0; ordinal < tableSize; ordinal++) {
            // Establish the proper cube state for the current ordinal
            ordinalToCubeState(ordinal);
            
            // Initialize the possible moves for each entry
            for (move = Cube.R; move <= Cube.B; move++) {
                // Apply this move
                
                // Phase 1 is the group spanned by <U,D,R,L,F,B>
                // Phase 2 is the group spanned by <U,D,R2,L2,F2,B2>
                move2 = move;
                if (isPhase2 && move != Cube.U && move != Cube.D) {
                    move2 = Cube.quarterTurnToHalfTurnMove(move);
                }
                cube.applyMove(move2);
                
                // Compute a new ordinal from the new cube state
                if (DEBUG && ordinal == 17) {
                    cube.dump();
                    System.out.println(ordinal + ":" + move2 + ":" + ordinalFromCubeState());
                }
                table[ordinal][move] = ordinalFromCubeState();

                // Unapply this move
                cube.applyMove(Cube.inverseOfMove(move2));
            }
        }
    }

    /**
     * Save the table to a file.
     */
    private void save(@Nonnull OutputStream outputStream)
            throws IOException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(outputStream));
        int ordinal, move;
        for (ordinal = 0; ordinal < tableSize; ordinal++) {
            for (move = 0; move < table[ordinal].length; move++) {
                out.writeInt(table[ordinal][move]);
            }
        }
        out.flush();
    }

    /**
     * Load the table from a file.
     */
    private void load(@Nonnull InputStream inputStream)
            throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(inputStream));
        int ordinal, move;
        for (ordinal = 0; ordinal < tableSize; ordinal++) {
            for (move = 0; move < table[ordinal].length; move++) {
                table[ordinal][move] = in.readInt();
            }
        }
    }
    
}

