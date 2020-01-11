/* @(#)PermutationNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.tree.MutableTreeNode;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A PermutationNode holds a single permutation and orientation change cycle of
 * cube parts of a single type.
 * The side effect of a permutation node to a Cube is a permutation and
 * orientation change the cube parts in the cycle.
 *
 * @author Werner Randelshofer
 */
public class PermutationCycleNode extends Node implements Cloneable {
    private final static long serialVersionUID = 1L;
    //private int signSymbol = -1;


    public final static int PLUS_SIGN = 3;
    public final static int PLUSPLUS_SIGN = 2;
    public final static int MINUS_SIGN = 1;
    public final static int NO_SIGN = 0;
    public final static int UNDEFINED_SIGN = -1;
    /**
     * Holds the sign of the permutation.
     * Values: PLUS_SIGN, PLUSPLUS_SIGN, MINUS_SIGN for Symbol.PERMUTATION_PLUS, .PPLUSPLUS, .PMINUS
     * or NO_SIGN if no sign symbol.
     */
    private int sign = UNDEFINED_SIGN;


    public final static int SIDE_PERMUTATION = 1;
    public final static int EDGE_PERMUTATION = 2;
    public final static int CORNER_PERMUTATION = 3;
    public final static int UNDEFINED_PERMUTATION = -1;
    /**
     * Holds the type of the permutation sequence.
     * May be SIDE_PERMUTATION, EDGE_PERMUTATION, or CORNER_PERMUTATION for side, edge, and corner permutation.
     * Is UNDEFINED if the number of items is undefined.
     */
    private int type = UNDEFINED_PERMUTATION;

    private int layerCount;

    /**
     * Creates a new PermutationNode.
     */
    public PermutationCycleNode() {
        this(-1, -1);
    }

    /**
     * Creates a new PermutationNode.
     *
     * @param startpos The start position of the node in the source code.
     * @param endpos   The end position of the node in the source code.
     */
    public PermutationCycleNode(int startpos, int endpos) {
        super(startpos, endpos);
        setAllowsChildren(true);
        this.layerCount = layerCount;
    }

    /**
     * Gets the full turn count of the subtree starting
     * at this node.
     */
    public int getFullTurnCount() {
        return 0;
    }

    /**
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    public int getQuarterTurnCount() {
        return 0;
    }

    public int getType() {
        return type;
    }

    public void setSignSymbol(@Nullable Symbol signSymbol) {
        int s;
        if (signSymbol == Symbol.PERMUTATION_MINUS) {
            s = MINUS_SIGN;
        } else if (signSymbol == Symbol.PERMUTATION_PLUSPLUS) {
            s = PLUSPLUS_SIGN;
        } else if (signSymbol == Symbol.PERMUTATION_PLUS) {
            s = PLUS_SIGN;
        } else if (signSymbol == null) {
            s = NO_SIGN;
        } else {
            throw new IllegalArgumentException("Illegal sign symbol:" + signSymbol);
        }
        if (s == PLUS_SIGN) {
            if (type == CORNER_PERMUTATION) {
                s = PLUSPLUS_SIGN;
            } else if (type == EDGE_PERMUTATION) {
                s = MINUS_SIGN;
            }
        }
        sign = s;
    }

    /**
     * Throws illegal argument exception if this
     * permutation already has permutation items
     * of a different type.
     *
     * @param type        PermutationNode.SIDE, .EDGE, .CORNER
     * @param signSymbol  Symbol.PERMUTATION_PLUS, .PMINUS or  .PPLUSPLUS or (0 if no sign symbol).
     * @param faceSymbols Array of 1, 2, or 3 entries of
     *                    Symbol.FACE_R, Symbol.FACE_U, Symbol.FACE_B, Symbol.FACE_L, Symbol.FACE_D or Symbol.FACE_F.
     * @param partNumber  A value &gt;= 0 used to disambiguate multiple edge parts
     *                    and multiple side parts in 4x4 cubes and 5x5 cubes.
     * @param layerCount  The number of layers of the cube.
     */
    public void addPermItem(int type, Symbol signSymbol, @Nonnull List<Symbol> faceSymbols, int partNumber, int layerCount) {
        addPermItem(type, signSymbol, faceSymbols.toArray(new Symbol[0]), partNumber, layerCount);
    }

    public void addPermItem(int type, @Nullable Symbol signSymbol, @Nonnull Symbol[] faceSymbols, int partNumber, int layerCount) {
        if (this.type == UNDEFINED_PERMUTATION) {
            this.type = type;
        }
        if (this.type != type) {
            throw new IllegalArgumentException("Permutation of different part types is not supported. Current type:" + this.type + " Added type:" + type + " Current length:" + getChildCount());
        }

        // Evaluate the sign symbol.
        int s;
        if (signSymbol == null) {
            s = NO_SIGN;
        } else {
            switch (signSymbol) {
                case PERMUTATION_MINUS:
                    s = MINUS_SIGN;
                    break;
                case PERMUTATION_PLUSPLUS:
                    s = PLUSPLUS_SIGN;
                    break;
                case PERMUTATION_PLUS:
                    s = PLUS_SIGN;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal sign symbol:" + signSymbol);
            }
        }
        if (s == PLUS_SIGN) {
            if (type == CORNER_PERMUTATION) {
                s = PLUSPLUS_SIGN;
            } else if (type == EDGE_PERMUTATION) {
                s = MINUS_SIGN;
            }
        }
        if (getChildren().size() == 0) {
            sign = s;
        } else if (type != SIDE_PERMUTATION && s != NO_SIGN) {
            throw new IllegalArgumentException("Illegal sign.");
        }

        // Evaluate the face symbols and construct the permutation item.
        PermutationItemNode permItem = new PermutationItemNode();
        int loc = -1;
        switch (type) {
            case UNDEFINED_PERMUTATION:
                break;
            case SIDE_PERMUTATION: {
                switch (faceSymbols[0]) {
                    case FACE_R:
                        loc = 0;
                        break;
                    case FACE_U:
                        loc = 1;
                        break;
                    case FACE_F:
                        loc = 2;
                        break;
                    case FACE_L:
                        loc = 3;
                        break;
                    case FACE_D:
                        loc = 4;
                        break;
                    case FACE_B:
                        loc = 5;
                        break;
                }

                if (layerCount <= 3) {
                    if (partNumber != 0) {
                        throw new IllegalArgumentException("Illegal side part number " + partNumber);
                    }
                } else {
                    if (partNumber < 0 || partNumber > (2 << (layerCount - 2)) - 1) {
                        throw new IllegalArgumentException("Illegal side part number " + partNumber);
                    }

                }
                loc += 6 * partNumber;

                permItem.setLocation(loc);
                permItem.setOrientation((getChildCount() == 0) ? 0 : s);

                break;
            }
            case EDGE_PERMUTATION: {
                if (signSymbol != null && signSymbol != Symbol.PERMUTATION_PLUS) {
                    throw new IllegalArgumentException("Illegal sign for edge part. [" + signSymbol + "]");
                }

                // We imply here that PR < PU < PF < PL < PD < PB.
                Symbol low = (faceSymbols[0].compareTo(faceSymbols[1]) < 0) ? faceSymbols[0] : faceSymbols[1];
                Symbol high = (faceSymbols[0].compareTo(faceSymbols[1]) > 0) ? faceSymbols[0] : faceSymbols[1];
                Symbol first = faceSymbols[0];
                boolean rotated = false;
                if (low == Symbol.FACE_R && high == Symbol.FACE_U) {
                    loc = 0;
                    rotated = first == Symbol.FACE_R;
                } else if (low == Symbol.FACE_R && high == Symbol.FACE_F) {
                    loc = 1;
                    rotated = first == Symbol.FACE_F;
                } else if (low == Symbol.FACE_R && high == Symbol.FACE_D) {
                    loc = 2;
                    rotated = first == Symbol.FACE_R;
                } else if (low == Symbol.FACE_U && high == Symbol.FACE_B) {
                    loc = 3;
                    rotated = first == Symbol.FACE_U;
                } else if (low == Symbol.FACE_R && high == Symbol.FACE_B) {
                    loc = 4;
                    rotated = first == Symbol.FACE_B;
                } else if (low == Symbol.FACE_D && high == Symbol.FACE_B) {
                    loc = 5;
                    rotated = first == Symbol.FACE_D;
                } else if (low == Symbol.FACE_U && high == Symbol.FACE_L) {
                    loc = 6;
                    rotated = first == Symbol.FACE_L;
                } else if (low == Symbol.FACE_L && high == Symbol.FACE_B) {
                    loc = 7;
                    rotated = first == Symbol.FACE_B;
                } else if (low == Symbol.FACE_L && high == Symbol.FACE_D) {
                    loc = 8;
                    rotated = first == Symbol.FACE_L;
                } else if (low == Symbol.FACE_U && high == Symbol.FACE_F) {
                    loc = 9;
                    rotated = first == Symbol.FACE_U;
                } else if (low == Symbol.FACE_F && high == Symbol.FACE_L) {
                    loc = 10;
                    rotated = first == Symbol.FACE_F;
                } else if (low == Symbol.FACE_F && high == Symbol.FACE_D) {
                    loc = 11;
                    rotated = first == Symbol.FACE_D;

                } else {
                    throw new IllegalArgumentException("Impossible edge part \"" + low.getName() + high.getName() + "\".");
                }

                if (layerCount <= 3) {
                    if (partNumber != 0) {
                        throw new IllegalArgumentException("Illegal edge part number " + partNumber);
                    }
                } else {
                    if (partNumber < 0 || partNumber >= layerCount - 2) {
                        throw new IllegalArgumentException("Illegal edge part number " + partNumber);
                    }
                    loc += 12 * partNumber;
                }


                permItem.setLocation(loc);
                permItem.setOrientation((rotated) ? 1 : 0);

                break;
            }
            case CORNER_PERMUTATION: {
                if (signSymbol == Symbol.PERMUTATION_PLUSPLUS) {
                    throw new IllegalArgumentException("Illegal sign for corner part.");
                }

                // We imply that PR < PU < PF < PL < PD < PB is an invariant.
                Symbol[] sorted = faceSymbols.clone();
                Arrays.sort(sorted);
                Symbol low = sorted[0];
                Symbol mid = sorted[1];
                Symbol high = sorted[2];

                // Values for rotation:
                //   0 = Initial position clockwise
                //   1 = Orientation 1 clockwise
                //   2 = Orientation 2 clockwise
                //   3 = Initial position counterclockwise
                //   4 = Orientation 1 counterclockwise
                //   5 = Orientation 2 counterclockwise
                int rotation = 0;
                if (low == Symbol.FACE_R
                        && mid == Symbol.FACE_U
                        && high == Symbol.FACE_F) {
                    loc = 0;
                    if (faceSymbols[0] == Symbol.FACE_U) {
                        rotation = (faceSymbols[1] == Symbol.FACE_R) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_R) {
                        rotation = (faceSymbols[1] == Symbol.FACE_F) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_U) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_R
                        && mid == Symbol.FACE_F
                        && high == Symbol.FACE_D) {
                    loc = 1;
                    if (faceSymbols[0] == Symbol.FACE_D) {
                        rotation = (faceSymbols[1] == Symbol.FACE_F) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_F) {
                        rotation = (faceSymbols[1] == Symbol.FACE_R) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_D) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_R
                        && mid == Symbol.FACE_U
                        && high == Symbol.FACE_B) {
                    loc = 2;
                    if (faceSymbols[0] == Symbol.FACE_U) {
                        rotation = (faceSymbols[1] == Symbol.FACE_B) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_B) {
                        rotation = (faceSymbols[1] == Symbol.FACE_R) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_U) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_R
                        && mid == Symbol.FACE_D
                        && high == Symbol.FACE_B) {
                    loc = 3;
                    if (faceSymbols[0] == Symbol.FACE_D) {
                        rotation = (faceSymbols[1] == Symbol.FACE_R) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_R) {
                        rotation = (faceSymbols[1] == Symbol.FACE_B) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_D) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_U
                        && mid == Symbol.FACE_L
                        && high == Symbol.FACE_B) {
                    loc = 4;
                    if (faceSymbols[0] == Symbol.FACE_U) {
                        rotation = (faceSymbols[1] == Symbol.FACE_L) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_L) {
                        rotation = (faceSymbols[1] == Symbol.FACE_B) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_U) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_L
                        && mid == Symbol.FACE_D
                        && high == Symbol.FACE_B) {
                    loc = 5;
                    if (faceSymbols[0] == Symbol.FACE_D) {
                        rotation = (faceSymbols[1] == Symbol.FACE_B) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_B) {
                        rotation = (faceSymbols[1] == Symbol.FACE_L) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_D) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_U
                        && mid == Symbol.FACE_F
                        && high == Symbol.FACE_L) {
                    loc = 6;
                    if (faceSymbols[0] == Symbol.FACE_U) {
                        rotation = (faceSymbols[1] == Symbol.FACE_F) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_F) {
                        rotation = (faceSymbols[1] == Symbol.FACE_L) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_U) ? 1 : 4;
                    }
                } else if (low == Symbol.FACE_F
                        && mid == Symbol.FACE_L
                        && high == Symbol.FACE_D) {
                    loc = 7;
                    if (faceSymbols[0] == Symbol.FACE_D) {
                        rotation = (faceSymbols[1] == Symbol.FACE_L) ? 0 : 3;
                    } else if (faceSymbols[0] == Symbol.FACE_L) {
                        rotation = (faceSymbols[1] == Symbol.FACE_F) ? 2 : 5;
                    } else {
                        rotation = (faceSymbols[1] == Symbol.FACE_D) ? 1 : 4;
                    }
                } else {
                    throw new IllegalArgumentException("Impossible corner part \"" + low.getName() + mid.getName() + high.getName() + "\".");
                }

                permItem.setLocation(loc);
                permItem.setOrientation(rotation);

                for (int i = 0; i < getChildCount(); i++) {
                    PermutationItemNode existingItem = (PermutationItemNode) getChildAt(i);
                    if (existingItem.getOrientation() / 3 != permItem.getOrientation() / 3) {
                        throw new IllegalArgumentException("Corner permutation cannot be clockwise and anticlockwise at the same time.");
                    }
                }

                break;
            }
        }

        add(permItem);
    }

    public int getPermItemCount() {
        return getChildCount();
    }

    @Override
    public void applyTo(@Nonnull Cube cube, boolean inverse) {
        if (inverse) {
            applyInverseTo(cube);
        } else {
            applyForwardTo(cube);
        }
    }

    private void applyForwardTo(@Nonnull Cube cube) {
        if (getChildCount() == 0) {
            return;
        }

        PermutationItemNode[] seq = new PermutationItemNode[getChildCount()];
        for (int i = 0; i < seq.length; i++) {
            seq[i] = (PermutationItemNode) getChildAt(i);
        }

        int[] loc = null;
        int[] orient = null;
        int modulo = 0;
        switch (type) {
            case SIDE_PERMUTATION:
                modulo = 4;
                loc = cube.getSideLocations();
                orient = cube.getSideOrientations();
                break;
            case CORNER_PERMUTATION:
                modulo = 3;
                loc = cube.getCornerLocations();
                orient = cube.getCornerOrientations();
                break;
            case EDGE_PERMUTATION:
                modulo = 2;
                loc = cube.getEdgeLocations();
                orient = cube.getEdgeOrientations();
                break;
        }

        // Adjust the orientation of the parts
        {
            int newOrient;
            int i;
            for (i = 0; i < seq.length - 1; i++) {
                newOrient = (seq[i + 1].getOrientation() - seq[i].getOrientation() + orient[seq[i].getLocation()]) % modulo;
                orient[seq[i].getLocation()] = (newOrient < 0) ? modulo + newOrient : newOrient;
            }

            newOrient = (sign - seq[i].getOrientation() + seq[0].getOrientation() + orient[seq[i].getLocation()]) % modulo;
            orient[seq[i].getLocation()] = (newOrient < 0) ? modulo + newOrient : newOrient;
        }

        // Adjust the location of the parts
        int tempLoc = loc[seq[seq.length - 1].getLocation()];
        int tempOrient = orient[seq[seq.length - 1].getLocation()];
        for (int i = seq.length - 1; i > 0; i--) {
            loc[seq[i].getLocation()] = loc[seq[i - 1].getLocation()];
            orient[seq[i].getLocation()] = orient[seq[i - 1].getLocation()];
        }
        loc[seq[0].getLocation()] = tempLoc;
        orient[seq[0].getLocation()] = tempOrient;

        // Apply the changes to the cube
        switch (type) {
            case SIDE_PERMUTATION:
                cube.setSides(loc, orient);
                break;
            case CORNER_PERMUTATION: {
                cube.setCorners(loc, orient);
                break;
            }
            case EDGE_PERMUTATION: {
                cube.setEdges(loc, orient);
                break;
            }
        }
    }

    private void applyInverseTo(@Nonnull Cube cube) {
        if (getChildCount() == 0) {
            return;
        }

        PermutationItemNode[] seq = new PermutationItemNode[getChildCount()];
        for (int i = 0; i < seq.length; i++) {
            seq[i] = (PermutationItemNode) getChildAt(i);
        }

        int[] loc = null;
        int[] orient = null;
        int modulo = 0;
        switch (type) {
            case SIDE_PERMUTATION:
                modulo = 4;
                loc = cube.getSideLocations();
                orient = cube.getSideOrientations();
                break;
            case CORNER_PERMUTATION:
                modulo = 3;
                loc = cube.getCornerLocations();
                orient = cube.getCornerOrientations();
                break;
            case EDGE_PERMUTATION:
                modulo = 2;
                loc = cube.getEdgeLocations();
                orient = cube.getEdgeOrientations();
                break;
        }

        // Adjust the orientation of the parts
        {
            int newOrient;
            int i;
            for (i = seq.length - 1; i > 0; i--) {
                newOrient = (seq[i - 1].getOrientation() - seq[i].getOrientation() + orient[seq[i].getLocation()]) % modulo;
                orient[seq[i].getLocation()] = (newOrient < 0) ? modulo + newOrient : newOrient;
            }
            newOrient = (-sign + seq[seq.length - 1].getOrientation() - seq[i].getOrientation() + orient[seq[i].getLocation()]) % modulo;
            orient[seq[i].getLocation()] = (newOrient < 0) ? modulo + newOrient : newOrient;
        }

        // Adjust the location of the parts
        int tempLoc = loc[seq[0].getLocation()];
        int tempOrient = orient[seq[0].getLocation()];
        for (int i = 1; i < seq.length; i++) {
            loc[seq[i - 1].getLocation()] = loc[seq[i].getLocation()];
            orient[seq[i - 1].getLocation()] = orient[seq[i].getLocation()];
        }
        loc[seq[seq.length - 1].getLocation()] = tempLoc;
        orient[seq[seq.length - 1].getLocation()] = tempOrient;

        // Apply the changes to the cube
        switch (type) {
            case SIDE_PERMUTATION:
                cube.setSides(loc, orient);
                break;
            case CORNER_PERMUTATION: {
                cube.setCorners(loc, orient);
                break;
            }
            case EDGE_PERMUTATION: {
                cube.setEdges(loc, orient);
                break;
            }
        }
    }

    @Override
    public void invert() {
        List<Node> a = new ArrayList<>(getChildren());
        removeAllChildren();

        if (a.size() > 0) {
            var old = (PermutationItemNode) a.get(0);
            var inverted = new PermutationItemNode();
            inverted.setOrientation(old.getOrientation());
            inverted.setLocation(old.getLocation());
            add(inverted);
        }
        for (int i = a.size() - 1; i >= 1; i--) {
            var old = (PermutationItemNode) a.get(i);
            var inverted = new PermutationItemNode();
            inverted.setOrientation(old.getOrientation());
            inverted.setLocation(old.getLocation());
            add(inverted);
        }
        switch (type) {
            case UNDEFINED_PERMUTATION:
                break;
            case SIDE_PERMUTATION:
                if (sign != 0) {
                    sign = 4 - sign;
                    for (int i = 1; i < getChildCount(); i++) {
                        var inverted = (PermutationItemNode) getChildAt(i);
                        inverted.setOrientation((sign + inverted.getOrientation()) % 4);
                    }
                }
                break;
            case CORNER_PERMUTATION:
                if (sign != 0) {
                    sign = 3 - sign;
                    for (int i = 1; i < getChildCount(); i++) {
                        var inverted = (PermutationItemNode) getChildAt(i);
                        inverted.setOrientation((sign + inverted.getOrientation()) % 3);
                    }
                }
                break;
            case EDGE_PERMUTATION:
                if (sign != 0) {
                    for (int i = 1; i < getChildCount(); i++) {
                        var inverted = (PermutationItemNode) getChildAt(i);
                        inverted.setOrientation(sign ^ inverted.getOrientation());
                    }
                }
                break;
        }
    }

    @Override
    public void reflect() {
        // Implementation missing
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Nonnull
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (inverse) {
            PermutationCycleNode inversedNode = clone();
            inversedNode.invert();
            return List.<Node>of(inversedNode).iterator();
        } else {
            return List.<Node>of(this).iterator();
        }
    }

    /**
     * Transforms the node by the given ScriptParser.symbol constant.
     */
    @Override
    public void transform(int axis, int layerMask, int angle) {
        Cube cube = Cubes.create(layerCount);
        if (axis == -1 || angle == 0 || layerMask == -1) {
            return;
        }
        cube.transform(axis, layerMask, angle);
        applyForwardTo(cube);
        cube.transform(axis, layerMask, -angle);

        //sequence.clear();
        int[] loc = null;
        int[] orient = null;
        int modulo = 0;
        switch (type) {
            case CORNER_PERMUTATION:
                modulo = 3;
                loc = cube.getCornerLocations();
                orient = cube.getCornerOrientations();
                break;
            case EDGE_PERMUTATION:
                modulo = 2;
                loc = cube.getEdgeLocations();
                orient = cube.getEdgeOrientations();
                break;
            case SIDE_PERMUTATION:
                modulo = 4;
                loc = cube.getSideLocations();
                orient = cube.getSideOrientations();
                break;
        }

        removeAllChildren();
        boolean[] visitedLocs = new boolean[loc.length];

        // search the first permutated item and addElement it to the sequence
        int i;
        for (i = 0; i < loc.length && loc[i] == i && orient[i] == 0; i++) {
        }

        PermutationItemNode item = new PermutationItemNode();
        item.setLocation(i);
        item.setOrientation(0);
        add(item);

        visitedLocs[i] = true;
        int prevOrient = 0;

        // search the nextElement item of the sequence
        int j;
        for (j = 0; loc[j] != i; j++) {
        }

        // perform all the other items of the sequence
        while (!visitedLocs[j]) {
            visitedLocs[j] = true;

            prevOrient = (modulo + prevOrient + orient[j]) % modulo;

            item = new PermutationItemNode();
            item.setLocation(j);
            item.setOrientation(prevOrient);
            add(item);

            int k;
            for (k = 0; loc[k] != j; k++) {
            }
            j = k;
        }
        // compute the sign
        sign = (modulo + prevOrient + orient[i]) % modulo;
    }

    public int getSign() {
        return sign;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        w.print(getStartPosition());
        w.print("..");
        w.print(getEndPosition());
        switch (type) {
            case SIDE_PERMUTATION:
                w.print("SidePermutation{");
                break;
            case CORNER_PERMUTATION:
                w.print("CornerPermutation{");
                break;
            case EDGE_PERMUTATION:
                w.print("EdgePermutation{");
                break;
            default:
                w.print("IllegalPermutation{");
                break;
        }

        w.print("sign:");
        w.print(sign);
        w.print(" ");

        Iterator<Node> iter = getChildren().iterator();
        boolean first = true;
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                w.print(',');
            }
            Node current = iter.next();
            if (current instanceof PermutationItemNode) {
                PermutationItemNode item = (PermutationItemNode) current;
                w.print(item.getLocation());
                w.print(':');
                w.print(item.getOrientation());
            } else {
                w.print(current.toString());
            }
        }
        w.print('}');
        w.close();
        return sw.toString();
    }

    @Nonnull
    @Override
    public PermutationCycleNode clone() {
        PermutationCycleNode theClone = (PermutationCycleNode) super.clone();
        theClone.removeAllChildren();
        for (Node item : this.getChildren()) {
            theClone.add(item.clone());
        }
        return theClone;
    }

    private final static Symbol[][] SIDE_SYMBOLS = {
            {Symbol.FACE_R},
            {Symbol.FACE_U},//
            {Symbol.FACE_F},
            {Symbol.FACE_L},
            {Symbol.FACE_D},
            {Symbol.FACE_B},//
    };
    @Nonnull Symbol[][] EDGE_SYMBOLS = {
            {Symbol.FACE_U, Symbol.FACE_R}, //"ur"
            {Symbol.FACE_R, Symbol.FACE_F}, //"rf"
            {Symbol.FACE_D, Symbol.FACE_R}, //"dr"
            {Symbol.FACE_B, Symbol.FACE_U}, //"bu"
            {Symbol.FACE_R, Symbol.FACE_B}, //"rb"
            {Symbol.FACE_B, Symbol.FACE_D}, //"bd"
            {Symbol.FACE_U, Symbol.FACE_L}, //"ul"
            {Symbol.FACE_L, Symbol.FACE_B}, //"lb"
            {Symbol.FACE_D, Symbol.FACE_L}, //"dl"
            {Symbol.FACE_F, Symbol.FACE_U}, //"fu"
            {Symbol.FACE_L, Symbol.FACE_F}, //"lf"
            {Symbol.FACE_F, Symbol.FACE_D} //"fd"
    };
    private final static Symbol[][] CORNER_SYMBOLS = {
            {Symbol.FACE_U, Symbol.FACE_R, Symbol.FACE_F},// urf
            {Symbol.FACE_D, Symbol.FACE_F, Symbol.FACE_R},// dfr
            {Symbol.FACE_U, Symbol.FACE_B, Symbol.FACE_R},// ubr
            {Symbol.FACE_D, Symbol.FACE_R, Symbol.FACE_B},// drb
            {Symbol.FACE_U, Symbol.FACE_L, Symbol.FACE_B},// ulb
            {Symbol.FACE_D, Symbol.FACE_B, Symbol.FACE_L},// dbl
            {Symbol.FACE_U, Symbol.FACE_F, Symbol.FACE_L},// ufl
            {Symbol.FACE_D, Symbol.FACE_L, Symbol.FACE_F}// dlf
    };

    @Override
    public void writeTokens(Writer w, @Nonnull Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        if (!p.isSupported(Symbol.PERMUTATION)) {
            //throw new IOException("This notation does not support permutations.");
            return;
        }

        Syntax permutationPosition = p.getSyntax(Symbol.PERMUTATION);

        Symbol[][] symbols = null;
        int modulo = 0;
        Symbol s = null;
        switch (type) {
            case CORNER_PERMUTATION:
                symbols = CORNER_SYMBOLS;
                modulo = 3;
                switch (sign) {
                    case 1:
                        s = Symbol.PERMUTATION_MINUS;
                        break;
                    case 2:
                        s = Symbol.PERMUTATION_PLUS;
                        break;
                }
                break;
            case EDGE_PERMUTATION:
                symbols = EDGE_SYMBOLS;
                modulo = 2;
                switch (sign) {
                    case 1:
                        s = Symbol.PERMUTATION_PLUS;
                        break;
                }
                break;
            case SIDE_PERMUTATION:
                symbols = SIDE_SYMBOLS;
                modulo = 4;
                switch (sign) {
                    case 1:
                        s = Symbol.PERMUTATION_MINUS;
                        break;
                    case 2:
                        s = Symbol.PERMUTATION_PLUSPLUS;
                        break;
                    case 3:
                        s = Symbol.PERMUTATION_PLUS;
                        break;
                }
                break;
        }

        if (permutationPosition == Syntax.PREFIX) {
            if (s != null) {
                p.writeToken(w, s);
            }
            p.writeToken(w, Symbol.PERMUTATION_BEGIN);
        } else if (permutationPosition == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.PERMUTATION_BEGIN);
            if (s != null) {
                p.writeToken(w, s);
            }
        } else if (permutationPosition == Syntax.POSTCIRCUMFIX) {
            p.writeToken(w, Symbol.PERMUTATION_BEGIN);
        } else if (permutationPosition == Syntax.SUFFIX) {
            p.writeToken(w, Symbol.PERMUTATION_BEGIN);
        }


        Iterator<Node> iter = getChildren().iterator();
        while (iter.hasNext()) {
            PermutationItemNode item = (PermutationItemNode) iter.next();

            switch (type) {
                case CORNER_PERMUTATION:
                    if (item.getOrientation() >= modulo) {
                        p.writeToken(w, symbols[item.getLocation()][(6 - item.getOrientation()) % modulo]);
                        p.writeToken(w, symbols[item.getLocation()][(5 - item.getOrientation()) % modulo]);
                        p.writeToken(w, symbols[item.getLocation()][(7 - item.getOrientation()) % modulo]);
                    } else {
                        for (int i = 0; i < symbols[item.getLocation()].length; i++) {
                            p.writeToken(w, symbols[item.getLocation()][(i + 3 - item.getOrientation()) % modulo]);
                        }
                    }
                    break;
                case EDGE_PERMUTATION:
                    for (int i = 0; i < symbols[item.getLocation()].length; i++) {
                        p.writeToken(w, symbols[item.getLocation()][(i + item.getOrientation()) % modulo]);
                    }
                    break;
                case SIDE_PERMUTATION:
                    if (permutationPosition == Syntax.PREFIX //
                            || permutationPosition == Syntax.PRECIRCUMFIX//
                            || permutationPosition == Syntax.POSTCIRCUMFIX
                    ) {
                        switch (item.getOrientation()) {
                            case 1:
                                p.writeToken(w, Symbol.PERMUTATION_MINUS);
                                break;
                            case 2:
                                p.writeToken(w, Symbol.PERMUTATION_PLUSPLUS);
                                break;
                            case 3:
                                p.writeToken(w, Symbol.PERMUTATION_PLUS);
                                break;
                        }
                    }
                    p.writeToken(w, symbols[item.getLocation()][0]);
                    if (permutationPosition == Syntax.SUFFIX) {
                        switch (item.getOrientation()) {
                            case 1:
                                p.writeToken(w, Symbol.PERMUTATION_MINUS);
                                break;
                            case 2:
                                p.writeToken(w, Symbol.PERMUTATION_PLUSPLUS);
                                break;
                            case 3:
                                p.writeToken(w, Symbol.PERMUTATION_PLUS);
                                break;
                        }
                    }
                    break;
            }

            if (iter.hasNext()) {
                p.writeToken(w, Symbol.PERMUTATION_DELIMITER);
            }
        }

        if (permutationPosition == Syntax.PREFIX) {
            p.writeToken(w, Symbol.PERMUTATION_END);
        } else if (permutationPosition == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.PERMUTATION_END);
        } else if (permutationPosition == Syntax.POSTCIRCUMFIX) {
            if (s != null) {
                p.writeToken(w, s);
            }
            p.writeToken(w, Symbol.PERMUTATION_END);
        } else if (permutationPosition == Syntax.SUFFIX) {
            p.writeToken(w, Symbol.PERMUTATION_END);
            if (s != null) {
                p.writeToken(w, s);
            }
        }
    }

    public void insert(MutableTreeNode newChild, int childIndex) {
        if (newChild instanceof PermutationItemNode) {
            super.insert(newChild, childIndex);
        } else {
            throw new IllegalArgumentException("Illegal child: " + newChild);
        }
    }
}