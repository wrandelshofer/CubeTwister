package org.kociemba.twophase;

/** Representation of the cube on the coordinate level. */
public class CoordCube {

    static final short N_TWIST = 2187;// 3^7 possible corner orientations
    static final short N_FLIP = 2048;// 2^11 possible edge flips
    public static final short N_SLICE1 = 495;// 12 choose 4 possible positions of FR,FL,BL,BR edges
    public static final short N_SLICE2 = 24;// 4! permutations of FR,FL,BL,BR edges in phase2
    static final short N_PARITY = 2; // 2 possible corner parities
    static final short N_URFtoDLF = 20160;// 8!/(8-6)! permutation of URF,UFL,ULB,UBR,DFR,DLF corners
    static final short N_FRtoBR = 11880; // 12!/(12-4)! permutation of FR,FL,BL,BR edges
    static final short N_URtoUL = 1320; // 12!/(12-3)! permutation of UR,UF,UL edges
    static final short N_UBtoDF = 1320; // 12!/(12-3)! permutation of UB,DR,DF edges
    static final short N_URtoDF = 20160; // 8!/(8-6)! permutation of UR,UF,UL,UB,DR,DF edges in phase2

    static final int N_URFtoDLB = 40320;// 8! permutations of the corners
    static final int N_URtoBR = 479001600;// 8! permutations of the corners

    static final short N_MOVE = 18;

    // All coordinates are 0 for a solved cube except for UBtoDF, which is 114
    public short twist;
    public short flip;
    public short parity;
    public short FRtoBR;
    public short URFtoDLF;
    public short URtoUL;
    public short UBtoDF;
    public int URtoDF;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Generate a CoordCube from a CubieCube
    public CoordCube(CubieCube c) {
        twist = c.getTwist();
        flip = c.getFlip();
        parity = c.cornerParity();
        FRtoBR = c.getFRtoBR();
        URFtoDLF = c.getURFtoDLF();
        URtoUL = c.getURtoUL();
        UBtoDF = c.getUBtoDF();
        URtoDF = c.getURtoDF();// only needed in phase2
    }

    // A move on the coordinate level
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void move(int m) {
        twist = twistMove[twist][m];
        flip = flipMove[flip][m];
        parity = parityMove[parity][m];
        FRtoBR = FRtoBR_Move[FRtoBR][m];
        URFtoDLF = URFtoDLF_Move[URFtoDLF][m];
        URtoUL = URtoUL_Move[URtoUL][m];
        UBtoDF = UBtoDF_Move[UBtoDF][m];
        if (URtoUL < 336 && UBtoDF < 336)// updated only if UR,UF,UL,UB,DR,DF
        // are not in UD-slice
        {
            URtoDF = MergeURtoULandUBtoDF[URtoUL][UBtoDF];
        }
    }

    // ******************************************Phase 1 move tables*****************************************************
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the twists of the corners
    // twist < 2187 in phase 2.
    // twist = 0 in phase 2.
    public static short[][] twistMove = new short[N_TWIST][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_TWIST; i++) {
            a.setTwist(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.cornerMultiply(CubieCube.moveCube[j]);
                    twistMove[i][3 * j + k] = a.getTwist();
                }
                a.cornerMultiply(CubieCube.moveCube[j]);// 4. faceturn restores
                // a
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the flips of the edges
    // flip < 2048 in phase 1
    // flip = 0 in phase 2.
    public static short[][] flipMove = new short[N_FLIP][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_FLIP; i++) {
            a.setFlip(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    flipMove[i][3 * j + k] = a.getFlip();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
                // a
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /** Parity of the corner permutation. 
     * This is the same as the parity for the edge permutation of a valid cube.
     * parity has values 0 and 1 
     */
    public static short[][] parityMove = {//
        {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1},//
        {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0}//
    };

    // ***********************************Phase 1 and 2 movetable********************************************************
    /** Move table for the four UD-slice edges FR, FL, BL and BR
     * FRtoBRMove &lt; 11880 in phase 1
     * FRtoBRMove &lt; 24 in phase 2
     *  FRtoBRMove = 0 for solved cube 
    */
    public static short[][] FRtoBR_Move = new short[N_FRtoBR][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_FRtoBR; i++) {
            a.setFRtoBR(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    FRtoBR_Move[i][3 * j + k] = a.getFRtoBR();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // *******************************************Phase 1 and 2 movetable************************************************
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for permutation of six corners. The positions of the DBL and DRB corners are determined by the parity.
    // URFtoDLF < 20160 in phase 1
    // URFtoDLF < 20160 in phase 2
    // URFtoDLF = 0 for solved cube.
    public static short[][] URFtoDLF_Move = new short[N_URFtoDLF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URFtoDLF; i++) {
            a.setURFtoDLF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.cornerMultiply(CubieCube.moveCube[j]);
                    URFtoDLF_Move[i][3 * j + k] = a.getURFtoDLF();
                }
                a.cornerMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the permutation of six U-face and D-face edges in phase2. The positions of the DL and DB edges are
    // determined by the parity.
    // URtoDF < 665280 in phase 1
    // URtoDF < 20160 in phase 2
    // URtoDF = 0 for solved cube.
    public static short[][] URtoDF_Move = new short[N_URtoDF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URtoDF; i++) {
            a.setURtoDF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    URtoDF_Move[i][3 * j + k] = (short) a.getURtoDF();
                    // Table values are only valid for phase 2 moves!
                    // For phase 1 moves, casting to short is not possible.
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // **************************helper move tables to compute URtoDF for the beginning of phase2************************
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the three edges UR,UF and UL in phase1.
    public static short[][] URtoUL_Move = new short[N_URtoUL][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URtoUL; i++) {
            a.setURtoUL(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    URtoUL_Move[i][3 * j + k] = a.getURtoUL();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the three edges UB,DR and DF in phase1.
    public static short[][] UBtoDF_Move = new short[N_UBtoDF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_UBtoDF; i++) {
            a.setUBtoDF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    UBtoDF_Move[i][3 * j + k] = a.getUBtoDF();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Table to merge the coordinates of the UR,UF,UL and UB,DR,DF edges at the beginning of phase2
    public static short[][] MergeURtoULandUBtoDF = new short[336][336];

    static {
        // for i, j <336 the six edges UR,UF,UL,UB,DR,DF are not in the
        // UD-slice and the index is <20160
        for (short uRtoUL = 0; uRtoUL < 336; uRtoUL++) {
            for (short uBtoDF = 0; uBtoDF < 336; uBtoDF++) {
                MergeURtoULandUBtoDF[uRtoUL][uBtoDF] = (short) CubieCube.getURtoDF(uRtoUL, uBtoDF);
            }
        }
    }

    // == Pruning tables for the search ==
    
    /** Pruning table for the permutation of the corners and the UD-slice edges in phase2.
     * The pruning table entries give a lower estimation for the number of moves to reach the solved cube.
     */
    public static byte[] Slice_URFtoDLF_Parity_Prun = new byte[N_SLICE2 * N_URFtoDLF * N_PARITY / 2];

    static {
        for (int i = 0; i < N_SLICE2 * N_URFtoDLF * N_PARITY / 2; i++) {
            Slice_URFtoDLF_Parity_Prun[i] = -1;
        }
        int depth = 0;
        setPruning(Slice_URFtoDLF_Parity_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE2 * N_URFtoDLF * N_PARITY) {
            for (int i = 0; i < N_SLICE2 * N_URFtoDLF * N_PARITY; i++) {
                int parity = i % 2;
                int URFtoDLF = (i / 2) / N_SLICE2;
                int slice = (i / 2) % N_SLICE2;
                if (getPruning(Slice_URFtoDLF_Parity_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        switch (j) {
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 12:
                            case 14:
                            case 15:
                            case 17:
                                continue;
                            default:
                                int newSlice = FRtoBR_Move[slice][j];
                                int newURFtoDLF = URFtoDLF_Move[URFtoDLF][j];
                                int newParity = parityMove[parity][j];
                                if (getPruning(Slice_URFtoDLF_Parity_Prun, (N_SLICE2 * newURFtoDLF + newSlice) * 2 + newParity) == 0x0f) {
                                    setPruning(Slice_URFtoDLF_Parity_Prun, (N_SLICE2 * newURFtoDLF + newSlice) * 2 + newParity,
                                            (byte) (depth + 1));
                                    done++;
                                }
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /** Pruning table for the permutation of the edges in phase2.
     * The pruning table entries give a lower estimation for the number of moves to reach the solved cube.
     */
    public static byte[] Slice_URtoDF_Parity_Prun = new byte[N_SLICE2 * N_URtoDF * N_PARITY / 2];

    static {
        for (int i = 0; i < N_SLICE2 * N_URtoDF * N_PARITY / 2; i++) {
            Slice_URtoDF_Parity_Prun[i] = -1;
        }
        int depth = 0;
        setPruning(Slice_URtoDF_Parity_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE2 * N_URtoDF * N_PARITY) {
            for (int i = 0; i < N_SLICE2 * N_URtoDF * N_PARITY; i++) {
                int parity = i % 2;
                int URtoDF = (i / 2) / N_SLICE2;
                int slice = (i / 2) % N_SLICE2;
                if (getPruning(Slice_URtoDF_Parity_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        switch (j) {
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 12:
                            case 14:
                            case 15:
                            case 17:
                                continue;
                            default:
                                int newSlice = FRtoBR_Move[slice][j];
                                int newURtoDF = URtoDF_Move[URtoDF][j];
                                int newParity = parityMove[parity][j];
                                if (getPruning(Slice_URtoDF_Parity_Prun, (N_SLICE2 * newURtoDF + newSlice) * 2 + newParity) == 0x0f) {
                                    setPruning(Slice_URtoDF_Parity_Prun, (N_SLICE2 * newURtoDF + newSlice) * 2 + newParity,
                                            (byte) (depth + 1));
                                    done++;
                                }
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Pruning table for the twist of the corners and the position (not permutation) of the UD-slice edges in phase1
    // The pruning table entries give a lower estimation for the number of moves to reach the H-subgroup.
    public static byte[] Slice_Twist_Prun = new byte[N_SLICE1 * N_TWIST / 2 + 1];

    static {
        for (int i = 0; i < N_SLICE1 * N_TWIST / 2 + 1; i++) {
            Slice_Twist_Prun[i] = -1;
        }
        int depth = 0;
        setPruning(Slice_Twist_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE1 * N_TWIST) {
            for (int i = 0; i < N_SLICE1 * N_TWIST; i++) {
                int twist = i / N_SLICE1, slice = i % N_SLICE1;
                if (getPruning(Slice_Twist_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        int newSlice = FRtoBR_Move[slice * 24][j] / 24;
                        int newTwist = twistMove[twist][j];
                        if (getPruning(Slice_Twist_Prun, N_SLICE1 * newTwist + newSlice) == 0x0f) {
                            setPruning(Slice_Twist_Prun, N_SLICE1 * newTwist + newSlice, (byte) (depth + 1));
                            done++;
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /** Pruning table for the flip of the edges and the position (not permutation) of the UD-slice edges in phase1
     * The pruning table entries give a lower estimation for the number of moves to reach the H-subgroup.
     */
    public static byte[] Slice_Flip_Prun = new byte[N_SLICE1 * N_FLIP / 2];

    static {
        for (int i = 0; i < N_SLICE1 * N_FLIP / 2; i++) {
            Slice_Flip_Prun[i] = -1;
        }
        int depth = 0;
        setPruning(Slice_Flip_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE1 * N_FLIP) {
            for (int i = 0; i < N_SLICE1 * N_FLIP; i++) {
                int flip = i / N_SLICE1, slice = i % N_SLICE1;
                if (getPruning(Slice_Flip_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        int newSlice = FRtoBR_Move[slice * 24][j] / 24;
                        int newFlip = flipMove[flip][j];
                        if (getPruning(Slice_Flip_Prun, N_SLICE1 * newFlip + newSlice) == 0x0f) {
                            setPruning(Slice_Flip_Prun, N_SLICE1 * newFlip + newSlice, (byte) (depth + 1));
                            done++;
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Set pruning value in table. Two values are stored in one byte.
    public static void setPruning(byte[] table, int index, byte value) {
        if ((index & 1) == 0) {
            table[index / 2] &= 0xf0 | value;
        } else {
            table[index / 2] &= 0x0f | (value << 4);
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Extract pruning value
    public static byte getPruning(byte[] table, int index) {
        if ((index & 1) == 0) {
            return (byte) (table[index / 2] & 0x0f);
        } else {
            return (byte) ((table[index / 2] & 0xf0) >>> 4);
        }
    }
}
