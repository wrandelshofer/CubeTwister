/*
 * @(#)DefaultNotation.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

/**
 * DefaultNotation supports Superset ENG for the 3x3 cube.
 *
 * @author Werner Randelshofer
 */
public class DefaultScriptNotation extends AbstractScriptNotation {
    private String name;

    /**
     * Creates a new instance.
     */
    public DefaultScriptNotation() {
        this(3);
    }

    public DefaultScriptNotation(int layerCount) {
        name = "default";
        setLayerCount(layerCount);

        addToken(Symbol.NOP, "·");
        addToken(Symbol.NOP, ".");
        addToken(Symbol.FACE_R, "r");
        addToken(Symbol.FACE_U, "u");
        addToken(Symbol.FACE_F, "f");
        addToken(Symbol.FACE_L, "l");
        addToken(Symbol.FACE_D, "d");
        addToken(Symbol.FACE_B, "b");
        addToken(Symbol.PERMUTATION_PLUS, "+");
        addToken(Symbol.PERMUTATION_MINUS, "-");
        addToken(Symbol.PERMUTATION_PLUSPLUS, "++");
        addToken(Symbol.PERMUTATION_BEGIN, "(");
        addToken(Symbol.PERMUTATION_END, ")");
        addToken(Symbol.PERMUTATION_DELIMITER, ",");
        //addToken(Symbol.DELIMITER ,"");
        //addToken(Symbol.INVERSION_BEGIN ,"(");
        //addToken(Symbol.INVERSION_END ,")");
        //addToken(Symbol.INVERSION_DELIMITER ,"");
        addToken(Symbol.INVERSION_OPERATOR, "'");
        addToken(Symbol.INVERSION_OPERATOR, "-");
        //addToken(Symbol.REFLECTION_BEGIN ,"(");
        //addToken(Symbol.REFLECTION_END ,")");
        //addToken(Symbol.REFLECTION_DELIMITER ,"");
        addToken(Symbol.REFLECTION_OPERATOR, "*");
        addToken(Symbol.GROUPING_BEGIN, "(");
        addToken(Symbol.GROUPING_END, ")");
        //addToken(Symbol.REPETITION_BEGIN ,"");
        //addToken(Symbol.REPETITION_END ,"");
        //addToken(Symbol.REPETITION_DELIMITER ,"");
        addToken(Symbol.COMMUTATION_BEGIN, "[");
        addToken(Symbol.COMMUTATION_END, "]");
        addToken(Symbol.COMMUTATION_DELIMITER, ",");
        addToken(Symbol.CONJUGATION_BEGIN, "<");
        addToken(Symbol.CONJUGATION_END, ">");
        //addToken(Symbol.CONJUGATION_DELIMITER ,":");
        addToken(Symbol.ROTATION_BEGIN, "<");
        addToken(Symbol.ROTATION_END, ">'");
        //addToken(Symbol.ROTATION_OPERATOR ,"::");
        // addToken(Symbol.MACRO ,"");
        addToken(Symbol.MULTILINE_COMMENT_BEGIN, "/*");
        addToken(Symbol.MULTILINE_COMMENT_END, "*/");
        addToken(Symbol.SINGLELINE_COMMENT_BEGIN, "//");

        // Layer masks
        int all = (1 << layerCount) - 1;
        int outer = 1 << (layerCount - 1);
        int inner = 1;

        for (int angle = 1; angle <= 2; angle++) {
            String suffix = angle == 1 ? "" : "2";

            // Face twists
            addMoves(layerCount, outer, inner, angle, "", suffix);

            // Cube rotations
            addMoves(layerCount, all, all, angle, "C", suffix);

            // Mid-layer twists
            int midLayer = layerCount / 2;
            for (int layer = 0; layer < layerCount - 2; layer++) {
                int innerMiddle = (layerCount % 2 == 0)
                        ? ((1 << (layer + 1)) - 1) << (midLayer - (layer + 1) / 2 - (layer + 1) % 2)
                        : ((1 << (layer + 1)) - 1) << (midLayer - (layer + 1) / 2);
                //int outerMiddle = (layerCount % 2 == 0)
                //        ? innerMiddle << 1
                //        : innerMiddle;
                int outerMiddle = Integer.reverse(innerMiddle) >>> (32 - layerCount); // reverse, reverses at 32 bits, must shift right by 32-layerCount
                if (innerMiddle == all) {
                    continue;
                }
                if (layer == 0) {
                    addMoves(layerCount, outerMiddle, innerMiddle, angle, "M", suffix);
                }
                addMoves(layerCount, outerMiddle, innerMiddle, angle, "M" + (layer + 1), suffix);
            }

            // Wide twists
            int wide = all ^ (inner | outer);
            if (wide != 0) {
                addMoves(layerCount, wide, wide, angle, "W", suffix);
            }

            // Tier twists
            for (int layer = 0; layer < layerCount; layer++) {
                int innerTier = (1 << (layer + 1)) - 1;
                //int outerTier = all ^ ((1 << (layerCount - layer - 1)) - 1);
                int outerTier = Integer.reverse(innerTier) >>> (32 - layerCount);
                if (layer == 1) {
                    addMoves(layerCount, outerTier, innerTier, angle, "T", suffix);
                }
                addMoves(layerCount, outerTier, innerTier, angle, "T" + (layer + 1), suffix);
            }

            // N-th layer twists
            for (int layer = 0; layer < layerCount - 1; layer++) {
                int innerLayer = 1 << layer;
                //int outerLayer = 1 << (layerCount - layer - 1);
                int outerLayer = Integer.reverse(innerLayer) >>> (32 - layerCount);
                if (layer == 1) {
                    addMoves(layerCount, outerLayer, innerLayer, angle, "N", suffix);
                }
                addMoves(layerCount, outerLayer, innerLayer, angle, "N" + (layer + 1), suffix);
            }
            // N-th layer range twists
            for (int from = 1; from < layerCount - 2; from++) {
                int innerFrom = (1 << from) - 1;
                int outerFrom = Integer.reverse(innerFrom) >>> (32 - layerCount);
                for (int to = from; to < layerCount - 1; to++) {
                    int innerTo = (1 << (to + 1)) - 1;
                    int outerTo = Integer.reverse(innerTo) >>> (32 - layerCount);
                    int innerRange = (innerTo ^ innerFrom);
                    int outerRange = (outerTo ^ outerFrom);
                    addMoves(layerCount, outerRange, innerRange, angle, "N" + (from + 1) + "-" + (to + 1), suffix);
                }
            }

            // Verge twists (tier twists without face)
            for (int layer = 1; layer < layerCount - 1; layer++) {
                int innerTier = ((1 << (layer + 1)) - 1) << 1;
                int outerTier = Integer.reverse(innerTier) >>> (32 - layerCount);
                if (layer == 1) {
                    addMoves(layerCount, outerTier, innerTier, angle, "V", suffix);
                }
                addMoves(layerCount, outerTier, innerTier, angle, "V" + (layer + 1), suffix);
            }
            // Slice twists
            for (int layer = 0; layer < midLayer; layer++) {
                int innerTier = (1 << (layer + 1)) - 1;
                int outerTier = all ^ ((1 << (layerCount - layer - 1)) - 1);
                int slice = innerTier | outerTier;
                if (slice == all) {
                    continue;
                }
                if (layer == 0) {
                    addMoves(layerCount, slice, slice, angle, "S", suffix);
                }
                addMoves(layerCount, slice, slice, angle, "S" + (layer + 1), suffix);
            }
            // Slice range twists
            for (int from = 1; from < layerCount - 2; from++) {
                int innerFrom = (1 << (from)) - 1;
                int outerFrom = all ^ ((1 << (layerCount - from)) - 1);
                for (int to = from; to < layerCount - 1; to++) {
                    int innerTo = (1 << (to + 1)) - 1;
                    int outerTo = all ^ ((1 << (layerCount - to - 1)) - 1);
                    int innerSlice = all ^ (innerTo ^ innerFrom);
                    int outerSlice = all ^ (outerTo ^ outerFrom);
                    addMoves(layerCount, outerSlice, innerSlice, angle, "S" + (from + 1) + "-" + (to + 1), suffix);
                }
            }

        }

        putSyntax(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX);
        putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        putSyntax(Symbol.GROUPING, Syntax.CIRCUMFIX);
        putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        putSyntax(Symbol.REPETITION, Syntax.SUFFIX);
        putSyntax(Symbol.REFLECTION, Syntax.SUFFIX);
        putSyntax(Symbol.INVERSION, Syntax.SUFFIX);
        putSyntax(Symbol.MOVE, Syntax.PRIMARY);
        putSyntax(Symbol.NOP, Syntax.PRIMARY);

    }

    private void addMoves(int layerCount, int outer, int inner, int angle, String prefix, String suffix) {
        addMove(new Move(layerCount, 0, outer, angle), prefix + "R" + suffix);
        addMove(new Move(layerCount, 1, outer, angle), prefix + "U" + suffix);
        addMove(new Move(layerCount, 2, outer, angle), prefix + "F" + suffix);
        addMove(new Move(layerCount, 0, inner, -angle), prefix + "L" + suffix);
        addMove(new Move(layerCount, 1, inner, -angle), prefix + "D" + suffix);
        addMove(new Move(layerCount, 2, inner, -angle), prefix + "B" + suffix);
    }

    /**
     * Public for testing.
     */
    @Override
    public void putSyntax(Symbol symbol, Syntax syntax) {
        super.putSyntax(symbol, syntax);
    }

    public void addToken(Symbol symbol, String token) {
        super.addToken(symbol, token);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
