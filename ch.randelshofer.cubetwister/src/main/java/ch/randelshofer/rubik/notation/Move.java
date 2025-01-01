/*
 * @(#)Move.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import org.jhotdraw.annotation.Nonnull;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;

/**
 * Descriptor for move tokens.
 * Instances of this class are immutable.
 *
 * @author Werner Randelshofer.
 */
public class Move implements Comparable<Move> {

    public final static Move R = new Move(3, 0, 4, 1);
    public final static Move L = new Move(3, 0, 1, -1);
    public final static Move U = new Move(3, 1, 4, 1);
    public final static Move D = new Move(3, 1, 1, -1);
    public final static Move F = new Move(3, 2, 4, 1);
    public final static Move B = new Move(3, 2, 1, -1);
    public final static Move RI = new Move(3, 0, 4, -1);
    public final static Move LI = new Move(3, 0, 1, 1);
    public final static Move UI = new Move(3, 1, 4, -1);
    public final static Move DI = new Move(3, 1, 1, 1);
    public final static Move FI = new Move(3, 2, 4, -1);
    public final static Move BI = new Move(3, 2, 1, 1);
    public final static Move R2 = new Move(3, 0, 4, 2);
    public final static Move L2 = new Move(3, 0, 1, 2);
    public final static Move U2 = new Move(3, 1, 4, 2);
    public final static Move D2 = new Move(3, 1, 1, 2);
    public final static Move F2 = new Move(3, 2, 4, 2);
    public final static Move B2 = new Move(3, 2, 1, 2);
    public final static Move CR = new Move(3, 0, 7, 1);
    public final static Move CL = new Move(3, 0, 7, -1);
    public final static Move CU = new Move(3, 1, 7, 1);
    public final static Move CD = new Move(3, 1, 7, -1);
    public final static Move CF = new Move(3, 2, 7, 1);
    public final static Move CB = new Move(3, 2, 7, -1);
    public final static Move CR2 = new Move(3, 0, 7, 2);
    public final static Move CL2 = new Move(3, 0, 7, 2);
    public final static Move CU2 = new Move(3, 1, 7, 2);
    public final static Move CD2 = new Move(3, 1, 7, 2);
    public final static Move CF2 = new Move(3, 2, 7, 2);
    public final static Move CB2 = new Move(3, 2, 7, 2);

    private final int axis;
    private final int layerMask;
    private final int angle;
    private final int layerCount;

    public Move(int layerCount, int axis, int layerMask, int angle) {
        this.axis = axis;
        this.layerMask = layerMask;
        this.angle = angle;
        this.layerCount = layerCount;
        if ((layerMask & ((1 << layerCount) - 1)) != layerMask) {
            throw new IllegalArgumentException("illegal layerMask=" + layerMask + " for layerCount=" + layerCount);
        }
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
    @Nonnull
    public Move toInverse() {
        return new Move(3, axis, layerMask, -angle);
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

    public int getLayerCount() {
        return layerCount;
    }


    @Nonnull
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

    public static int toLayerMask(@Nonnull String str) {
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

    public boolean equals(@Nonnull Move that) {
        return that.axis == this.axis &&
                that.layerMask == this.layerMask &&
                that.angle == this.angle;
    }

    public int hashCode() {
        return (axis << 24) | (layerMask << 10) | (angle << 8);
    }

    @Nonnull
    public String toString() {
        return "Move axis=" + axis + " mask=" + maskToString() + " angle=" + angle;
    }

    private String maskToString() {
        var buf = new StringBuilder();
        for (var i = 0; i < layerCount; i++) {
            buf.append(((layerMask >>> i) & 1) == 0 ? '○' : '●');
        }
        return buf.toString();
    }

    @Nonnull
    public List<Move> getResolvedList() {
        return List.of(this);
    }

    public int compareTo(@Nonnull Move that) {
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
