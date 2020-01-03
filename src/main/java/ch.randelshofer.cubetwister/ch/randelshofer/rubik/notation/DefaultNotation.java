/* @(#)DefaultNotation.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

/**
 * DefaultNotation supports Superset ENG for the 3x3 cube.
 *
 * @author Werner Randelshofer
 */
public class DefaultNotation extends AbstractNotation {
    private String name;

    /** Creates a new instance. */
    public DefaultNotation() {
        this(3);
    }

    public DefaultNotation(int layerCount) {
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
        int inner = 1;
        int middle = 1<<(layerCount/2);
        int outer = 1<<(layerCount-1);
        int all = inner | middle | outer;

        for (int i=1;i<=2;i++) {
            String suffix=i==1?"":"2";

            addMove(new Move(3, 0, outer, 1*i), "R"+suffix);
            addMove(new Move(3, 1, outer, 1*i), "U"+suffix);
            addMove(new Move(3, 2, outer, 1*i), "F"+suffix);
            addMove(new Move(3, 0, inner, -1*i), "L"+suffix);
            addMove(new Move(3, 1, inner, -1*i), "D"+suffix);
            addMove(new Move(3, 2, inner, -1*i), "B"+suffix);

            addMove(new Move(3, 0, middle, 1*i), "MR"+suffix);
            addMove(new Move(3, 1, middle, 1*i), "MU"+suffix);
            addMove(new Move(3, 2, middle, 1*i), "MF"+suffix);
            addMove(new Move(3, 0, middle, -1*i), "ML"+suffix);
            addMove(new Move(3, 1, middle, -1*i), "MD"+suffix);
            addMove(new Move(3, 2, middle, -1*i), "MB"+suffix);

            addMove(new Move(3, 0, outer | middle, 1*i), "TR"+suffix);
            addMove(new Move(3, 1, outer | middle, 1*i), "TU"+suffix);
            addMove(new Move(3, 2, outer | middle, 1*i), "TF"+suffix);
            addMove(new Move(3, 0, inner | middle, -1*i), "TL"+suffix);
            addMove(new Move(3, 1, inner | middle, -1*i), "TD"+suffix);
            addMove(new Move(3, 2, inner | middle, -1*i), "TB"+suffix);

            addMove(new Move(3, 0, outer | inner, 1*i), "SR"+suffix);
            addMove(new Move(3, 1, outer | inner, 1*i), "SU"+suffix);
            addMove(new Move(3, 2, outer | inner, 1*i), "SF"+suffix);
            addMove(new Move(3, 0, inner | outer, -1*i), "SL"+suffix);
            addMove(new Move(3, 1, inner | outer, -1*i), "SD"+suffix);
            addMove(new Move(3, 2, inner | outer, -1*i), "SB"+suffix);

            addMove(new Move(3, 0, all, 1*i), "CR"+suffix);
            addMove(new Move(3, 1, all, 1*i), "CU"+suffix);
            addMove(new Move(3, 2, all, 1*i), "CF"+suffix);
            addMove(new Move(3, 0, all, -1*i), "CL"+suffix);
            addMove(new Move(3, 1, all, -1*i), "CD"+suffix);
            addMove(new Move(3, 2, all, -1*i), "CB"+suffix);
        }

        putSyntax(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX);
        putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        putSyntax(Symbol.GROUPING, Syntax.CIRCUMFIX);
        putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        putSyntax(Symbol.REPETITION, Syntax.SUFFIX);
        putSyntax(Symbol.REFLECTION, Syntax.SUFFIX);
        putSyntax(Symbol.INVERSION, Syntax.SUFFIX);
        putSyntax(Symbol.MOVE,Syntax.PRIMARY);
        putSyntax(Symbol.NOP,Syntax.PRIMARY);

    }

    /** Putlic for testing. */
    @Override
    public void putSyntax(Symbol symbol, Syntax syntax) {
        super.putSyntax(symbol, syntax);
    }

    public void addToken(Symbol symbol, String token) {
        super.addToken(symbol,token);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
