/*
 * @(#)Move.java  2007-06-16
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.util.SingleElementList;
import java.io.*;
import java.util.List;

/**
 * Symbol for move tokens.
 * Instances of this class are immutable.
 * <p>
 * This class must be Java 1.1 compliant.
 *
 * @author Werner Randelshofer.
 * @version 2.0 2007-06-16 Renamed from Twist to Move.
 * <br>1.0 May 1, 2006 Created.
 */
public class Move implements Comparable<Move> {

    public final static Move R = new Move(0, 4, 1);
    public final static Move L = new Move(0, 1, -1);
    public final static Move U = new Move(1, 4, 1);
    public final static Move D = new Move(1, 1, -1);
    public final static Move F = new Move(2, 4, 1);
    public final static Move B = new Move(2, 1, -1);
    public final static Move RI = new Move(0, 4, -1);
    public final static Move LI = new Move(0, 1, 1);
    public final static Move UI = new Move(1, 4, -1);
    public final static Move DI = new Move(1, 1, 1);
    public final static Move FI = new Move(2, 4, -1);
    public final static Move BI = new Move(2, 1, 1);
    public final static Move R2 = new Move(0, 4, 2);
    public final static Move L2 = new Move(0, 1, 2);
    public final static Move U2 = new Move(1, 4, 2);
    public final static Move D2 = new Move(1, 1, 2);
    public final static Move F2 = new Move(2, 4, 2);
    public final static Move B2 = new Move(2, 1, 2);
    public final static Move CR = new Move(0, 7, 1);
    public final static Move CL = new Move(0, 7, -1);
    public final static Move CU = new Move(1, 7, 1);
    public final static Move CD = new Move(1, 7, -1);
    public final static Move CF = new Move(2, 7, 1);
    public final static Move CB = new Move(2, 7, -1);
    public final static Move CR2 = new Move(0, 7, 2);
    public final static Move CL2 = new Move(0, 7, 2);
    public final static Move CU2 = new Move(1, 7, 2);
    public final static Move CD2 = new Move(1, 7, 2);
    public final static Move CF2 = new Move(2, 7, 2);
    public final static Move CB2 = new Move(2, 7, 2);
    int axis;
    int layerMask;
    int angle;

    public Move(int axis, int layerMask, int angle) {
        this.axis = axis;
        this.layerMask = layerMask;
        this.angle = angle;
    }

    public boolean equals(Object o) {
        if (o instanceof Move) {
            return equals((Move) o);
        } else {
            return false;
        }
    }

    /**
     * Returns an inverse Move of this Move.
     */
    public Move toInverse() {
        return new Move(axis, layerMask, -angle);
    }

    public int getAxis() {
        return axis;
    }

    public int getAngle() {
        return angle;
    }

    public int getLayerMask() {
        return layerMask;
    }

    public String getLayerList() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if ((layerMask & (1 << i)) != 0) {
                if (buf.length() > 0) {
                    buf.append(',');
                }
                buf.append(i + 1);
            }
        }
        return buf.toString();
    }

    public static int toLayerMask(String str) {
        StreamTokenizer tt = new StreamTokenizer(new StringReader(str));
        tt.resetSyntax();
        tt.parseNumbers();
        int layerMask = 0;
        try {
            while (tt.nextToken() != StreamTokenizer.TT_EOF) {
                if (tt.ttype == StreamTokenizer.TT_NUMBER) {
                    int layer = ((int) tt.nval) - 1;
                    layerMask |= 1 << layer;
                } else if (tt.ttype == ',') {
                } else {
                    throw new IOException("Unexpected token " + (char) tt.ttype);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            layerMask = 0;
        }
        return layerMask;
    }

    public boolean equals(Move that) {
        return that.axis == this.axis &&
                that.layerMask == this.layerMask &&
                that.angle == this.angle;
    }

    public int hashCode() {
        return /*symbol.hashCode() |*/ (axis << 24) | (layerMask << 10) | (angle << 8);
    }

    public String toString() {
        return "Move axis=" + axis + " mask=" + layerMask + " angle=" + angle;
    }

    public List<Move> getResolvedList() {
        return new SingleElementList<Move>(this);
    }

    public int compareTo(Move that) {

        int result;
        result = this.layerMask - that.layerMask;
        if (result == 0) {
            result = this.axis - that.axis;
            if (result == 0) {
                result = this.angle - that.angle;
            }
        }
        return result;
    }
}
