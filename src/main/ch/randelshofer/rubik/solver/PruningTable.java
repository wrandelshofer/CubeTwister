/*
 * @(#)PruningTable.java  0.0  2000-07-01
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
import java.io.*;
import javax.swing.*;

/**
 * Constructs a pruning table from a pair of move mapping
 * tables.  The pruning table contains an entry corresponding
 * to each possible pairing of entries from the two tables.
 * Thus the number of pruning table entries is the product of
 * the number of entries of the two move mapping tables.  A
 * breadth first search is performed until the table is filled.
 * Each table entry contains the number of moves away from
 * the cube home configuration that is required to reach that
 * particular configuration.  Since a breadth first search is
 * performed, the distances are minimal and therefore they
 * constitute an admissible heuristic.  When an admissible
 * heuristic is used to prune a heuristic search such as
 * IDA*, the search is guaranteed to find an optimal (i.e.
 * least number of moves possible) solution.
 *
 * This class has been derived from pruningt.cpp and pruningt.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version 0.0 2000-07-01
 */
public class PruningTable extends Object {
    /**
     * Empty table entry.
     */
    private final static int EMPTY = 0x0f;
    
    // Copies of important variables
    private MoveTable moveTable1;
    private MoveTable moveTable2;
    private int homeOrdinal1;
    private int homeOrdinal2;
    private int moveTable1Size;
    private int moveTable2Size;
    
    
    // Number of entries in the pruning table
    private int tableSize;
    
    // Actual size, in bytes, allocated for the table
    private int allocationSize;
    
    // The table pointer
    private byte[] table;
    
    // Tables for dealing with nybble packing/unpacking
    private final static int[] OFFSET_TO_ENTRY_MASK = {
        EMPTY<<0,  EMPTY<<4
    };
    
    private static int[] OFFSET_TO_SHIFT_COUNT =  {
        0, 4
    };
    
    /**
     * Constructor - Must provide a pair of move mapping tables
     * and the associated ordinal corresponding to the cube's
     * "home" configuration.  The home ordinals correspond to
     * the root node of the search.
     */
    public PruningTable(MoveTable moveTable1, MoveTable moveTable2,
    int homeOrdinal1, int homeOrdinal2) {
        this.moveTable1 = moveTable1;
        this.moveTable2 = moveTable2;
        this.homeOrdinal1 = homeOrdinal1;
        this.homeOrdinal2 = homeOrdinal2;
        
        // Initialize table sizes
        moveTable1Size = moveTable1.size();
        moveTable2Size = moveTable2.size();
        tableSize = moveTable1Size*moveTable2Size;
        
        // Allocate the table
        //   round up to an int and determine
        //   the number of bytes to be allocated
        allocationSize = ((tableSize+7)/8)*4;
        
        // AllocationSize = TableSize/2;
        table = new byte[allocationSize];
        
    }
    
    /**
     * Initialize the pruning table by either generating it
     * or loading it from an existing file.
     */
    public void initialize(File file, ProgressObserver  pm, String pmNote) {
        if (!file.exists()) {
            // If the pruning table file is absent...
            // Generate the table and save it to a file
            pm.setNote(pmNote+"Generating pruning table.");
            generate(pm, pmNote);
            pm.setNote(pmNote+"Saving move table "+file.getName()+".");
            try {
                OutputStream out = new FileOutputStream(file);
                save(out);
                out.close();
                pm.setNote(pmNote+"Done Saving.");
            } catch (IOException e) {
                pm.setNote(pmNote+"Save failed: "+e.getMessage());
            }
        } else {
            // The move mapping table file exists
            // Load the existing file
            pm.setNote(pmNote+"Loading pruning table "+file.getName()+".");
            try {
                InputStream in = new FileInputStream(file);
                load(in);
                in.close();
            } catch (IOException e) {
                pm.setNote(pmNote+"Load failed: "+e.getMessage());
                pm.setNote(pmNote+"Generating pruning table.");
                generate(pm, pmNote);
                pm.setNote(pmNote+"Done generating.");
            }
        }
    }
    
    /**
     * Convert a pruning table index to the associated pair
     * of move mapping table indices.
     */
    /*
    public void pruningTableIndexToMoveTableIndices(int index, int& ordinal1, int& ordinal2) {
        // Split the pruning table index
        ordinal1 = index / moveTable2Size;
        ordinal2 = index % moveTable2Size;
    }
     */
    
    /**
     * Convert a pair of move mapping table indices to the
     * associated pruning table index.
     */
    public int moveTableIndicesToPruningTableIndex(int ordinal1, int ordinal2) {
        // Combine move table indices
        return ordinal1 * moveTable2Size + ordinal2;
    }
    
    /**
     * Get a pruning table value corresponding to the specified index.
     */
    public int getValue(int index) {
        // Retrieve the proper nibble
        int offset = index % 2;
        return (table[index / 2] & OFFSET_TO_ENTRY_MASK[offset]) >> OFFSET_TO_SHIFT_COUNT[offset];
    }
    
    /**
     * Set a pruning table value at the specified index.
     */
    public void setValue(int index, int value) {
        // Set the proper nybble
        int i = index / 2;
        int offset = index % 2;
        table[i] = (byte) ((table[i] & ~OFFSET_TO_ENTRY_MASK[offset]) | (value << OFFSET_TO_SHIFT_COUNT[offset]));
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
        // Output the pruning table in human readable form
        int index;
        for (index = 0; index < tableSize; index++) {
            System.out.println(index + ": " + getValue(index));
        }
    }
    
    /** Generate the table using breadth first search. */
    private void generate(ProgressObserver  pm, String pmNote) {
        /*unsigned*/ int depth = 0; // Current search depth
        int numberOfNodes;              // Number of nodes generated
        int ordinal1, ordinal2; // Table coordinates
        int index, index2;              // Table indices
        int move;
        int power;
        
        // Initialize all tables entries to "empty"
        for (index = 0; index < tableSize; index++) {
            setValue(index, EMPTY);
        }
        
        // Get root coordinates of search tree
        //   and initialize to zero
        setValue(moveTableIndicesToPruningTableIndex(homeOrdinal1, homeOrdinal2), depth);
        numberOfNodes = 1;      // Count root node here
        pm.setNote(pmNote+" Generating "+tableSize+" prune table nodes.");
        // While empty table entries exist...
        while (numberOfNodes < tableSize) {
            // Scan all entries looking for entries
            //   corresponding to the current depth
            for (index = 0; index < tableSize; index++) {
                // Expand the nodes at the current depth only
                if (getValue(index) == depth) {
                    // Apply each possible move
                    for (move = Cube.R; move <= Cube.B; move++) {
                        ////pruningTableIndexToMoveTableIndices(index, ordinal1, ordinal2);
                        ordinal1 = index / moveTable2Size;
                        ordinal2 = index % moveTable2Size;
                        // Apply each of the three quarter turns
                        for (power = 1; power < 4; power++) {
                            // Use the move mapping table to find the child node
                            ordinal1 = moveTable1.get(ordinal1, move);
                            ordinal2 = moveTable2.get(ordinal2, move);
                            index2 = moveTableIndicesToPruningTableIndex(ordinal1, ordinal2);
                            
                            // Update previously unexplored nodes only
                            if (getValue(index2) == EMPTY) {
                                setValue(index2, depth+1);
                                numberOfNodes++;
                            }
                            // An optimization that could be done, but is probably not worthwhile
                            // if (phase2 && move != Cube.U && move != Cube.D && power == 1)
                            //      break;
                        }
                    }
                }
            }
            depth++;
            pm.setNote(pmNote+" Depth " + depth + " completed, "+ (tableSize - numberOfNodes)+" nodes to go.");
            pm.setProgress(pm.getProgress() + 1);
            if (depth > EMPTY) {
                throw new InternalError();
            }
        }
    }
    
    /** Save the table to a file. */
    private void save(OutputStream outputStream)
    throws IOException {
        /*
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(outputStream));
        for (int i=0; i < table.length; i++) {
            out.write(table[i]);
        }
        out.flush();
         */
        outputStream.write(table);
        outputStream.flush();
    }
    
    /** Load the table from a file. */
    private void load(InputStream inputStream)
    throws IOException {
        DataInputStream in = new DataInputStream(/*new BufferedOutputStream(*/inputStream/*)*/);
        in.readFully(table);
    }
    
}
