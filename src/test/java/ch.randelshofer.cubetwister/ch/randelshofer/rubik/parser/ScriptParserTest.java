package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cube6;
import ch.randelshofer.rubik.cube.Cube7;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube.PocketCube;
import ch.randelshofer.rubik.cube.ProfessorCube;
import ch.randelshofer.rubik.cube.RevengeCube;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ScriptParserTest {
    private static boolean html = true;

    @Nonnull DefaultNotation defaultNotatioon = new DefaultNotation();
    @Nonnull DefaultNotation defaultNotatioon4 = new DefaultNotation(4);
    @Nonnull DefaultNotation precircumfix = new DefaultNotation();
    @Nonnull DefaultNotation preinfix = new DefaultNotation();
    @Nonnull DefaultNotation postinfix = new DefaultNotation();
    @Nonnull DefaultNotation prefix = new DefaultNotation();
    @Nonnull DefaultNotation circumfix = new DefaultNotation();
    @Nonnull DefaultNotation postcircumfix = new DefaultNotation();
    @Nonnull DefaultNotation suffix = new DefaultNotation();
    @Nonnull DefaultNotation mixed = new DefaultNotation();
    @Nonnull DefaultNotation mixedB = new DefaultNotation();
    @Nonnull DefaultNotation notationWithMacros = new DefaultNotation();

    {

        precircumfix.setName("precircumfix");
        precircumfix.addToken(Symbol.CONJUGATION_DELIMITER, ",");
        precircumfix.putSyntax(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX);
        precircumfix.putSyntax(Symbol.CONJUGATION, Syntax.PRECIRCUMFIX);
        precircumfix.putSyntax(Symbol.ROTATION, Syntax.PRECIRCUMFIX);
        precircumfix.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        precircumfix.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        precircumfix.putSyntax(Symbol.REFLECTION, Syntax.PREFIX);
        precircumfix.putSyntax(Symbol.INVERSION, Syntax.PREFIX);

        prefix.setName("prefix");
        prefix.putSyntax(Symbol.COMMUTATION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.REFLECTION, Syntax.PREFIX);
        prefix.putSyntax(Symbol.INVERSION, Syntax.PREFIX);

        circumfix.setName("circumfix");
        circumfix.addToken(Symbol.REFLECTION_BEGIN, "*");
        circumfix.addToken(Symbol.REFLECTION_END, "*");
        circumfix.addToken(Symbol.INVERSION_BEGIN, "'");
        circumfix.addToken(Symbol.INVERSION_END, "'");
        circumfix.putSyntax(Symbol.COMMUTATION, Syntax.PREFIX);
        circumfix.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        circumfix.putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        circumfix.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        circumfix.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        circumfix.putSyntax(Symbol.REFLECTION, Syntax.CIRCUMFIX);
        circumfix.putSyntax(Symbol.INVERSION, Syntax.CIRCUMFIX);

        postcircumfix.setName("postcircumfix");
        postcircumfix.addToken(Symbol.CONJUGATION_DELIMITER, ",");
        postcircumfix.putSyntax(Symbol.COMMUTATION, Syntax.POSTCIRCUMFIX);
        postcircumfix.putSyntax(Symbol.CONJUGATION, Syntax.POSTCIRCUMFIX);
        postcircumfix.putSyntax(Symbol.ROTATION, Syntax.POSTCIRCUMFIX);
        postcircumfix.putSyntax(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX);

        suffix.setName("suffix");
        suffix.putSyntax(Symbol.COMMUTATION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.CONJUGATION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.ROTATION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.PERMUTATION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.REPETITION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.REFLECTION, Syntax.SUFFIX);
        suffix.putSyntax(Symbol.INVERSION, Syntax.SUFFIX);

        preinfix.setName("preinfix");
        preinfix.addToken(Symbol.COMMUTATION_OPERATOR, "comm");
        preinfix.addToken(Symbol.CONJUGATION_OPERATOR, "conj");
        preinfix.addToken(Symbol.ROTATION_OPERATOR, "rot");
        preinfix.addToken(Symbol.REPETITION_OPERATOR, "times");
        preinfix.putSyntax(Symbol.COMMUTATION, Syntax.PREINFIX);
        preinfix.putSyntax(Symbol.CONJUGATION, Syntax.PREINFIX);
        preinfix.putSyntax(Symbol.ROTATION, Syntax.PREINFIX);
        preinfix.putSyntax(Symbol.REPETITION, Syntax.PREINFIX);
        preinfix.putSyntax(Symbol.PERMUTATION, Syntax.PREINFIX);
        preinfix.putSyntax(Symbol.INVERSION, Syntax.PREFIX);
        preinfix.putSyntax(Symbol.REFLECTION, Syntax.PREFIX);

        postinfix.setName("postinfix");
        postinfix.addToken(Symbol.COMMUTATION_OPERATOR, "comm");
        postinfix.addToken(Symbol.CONJUGATION_OPERATOR, "conj");
        postinfix.addToken(Symbol.ROTATION_OPERATOR, "rot");
        postinfix.addToken(Symbol.REPETITION_OPERATOR, "times");
        postinfix.putSyntax(Symbol.COMMUTATION, Syntax.POSTINFIX);
        postinfix.putSyntax(Symbol.CONJUGATION, Syntax.POSTINFIX);
        postinfix.putSyntax(Symbol.ROTATION, Syntax.POSTINFIX);
        postinfix.putSyntax(Symbol.PERMUTATION, Syntax.POSTINFIX);
        postinfix.putSyntax(Symbol.REPETITION, Syntax.POSTINFIX);
        postinfix.putSyntax(Symbol.INVERSION, Syntax.SUFFIX);
        postinfix.putSyntax(Symbol.REFLECTION, Syntax.SUFFIX);

        mixed.setName("mixed A");
        mixed.addToken(Symbol.ROTATION_OPERATOR, "rot");
        mixed.addToken(Symbol.COMMUTATION_OPERATOR, "comm");
        mixed.addToken(Symbol.CONJUGATION_OPERATOR, "conj");
        mixed.addToken(Symbol.REPETITION_OPERATOR, "*");
        mixed.addToken(Symbol.REFLECTION_BEGIN, "«");
        mixed.addToken(Symbol.REFLECTION_END, "»");
        mixed.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        mixed.putSyntax(Symbol.COMMUTATION, Syntax.PREINFIX);
        mixed.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        mixed.putSyntax(Symbol.ROTATION, Syntax.POSTINFIX);
        mixed.putSyntax(Symbol.REPETITION, Syntax.PREINFIX);
        mixed.putSyntax(Symbol.INVERSION, Syntax.SUFFIX);
        mixed.putSyntax(Symbol.REFLECTION, Syntax.CIRCUMFIX);

        mixedB.setName("mixed B");
        mixedB.addToken(Symbol.ROTATION_OPERATOR, "rot");
        mixedB.addToken(Symbol.COMMUTATION_OPERATOR, "comm");
        mixedB.addToken(Symbol.CONJUGATION_OPERATOR, "conj");
        mixedB.addToken(Symbol.REPETITION_OPERATOR, "*");
        mixedB.addToken(Symbol.REFLECTION_BEGIN, "«");
        mixedB.addToken(Symbol.REFLECTION_END, "»");
        mixedB.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        mixedB.putSyntax(Symbol.COMMUTATION, Syntax.PREINFIX);
        mixedB.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        mixedB.putSyntax(Symbol.ROTATION, Syntax.POSTINFIX);
        mixedB.putSyntax(Symbol.REPETITION, Syntax.SUFFIX);
        mixedB.putSyntax(Symbol.INVERSION, Syntax.SUFFIX);
        mixedB.putSyntax(Symbol.REFLECTION, Syntax.CIRCUMFIX);

        notationWithMacros.setName("macros");
        notationWithMacros.putMacro("CRU", "CR CU");

    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testOperatorPrecedenceA() {
        // Expected
        // Precedence : Syntax : Associativity
        //  1 : CIRCUMFIX : inside-to-outside
        //  1 : SUFFIX : left-to-right
        //  2 : PREFIX : right-to-left
        //  2 : PRECIRCUMFIX : right-to-left
        //  2 : POSTINFIX : right-to-left
        //  2 : PREINFIX : right-to-left

        return Arrays.asList(
                dynamicTest("No precedence: 3 * R", () -> doParse(mixed, "3 * R", "0..5 sequence{ 0..5 repetition{ 3, 4..5 move{ 0:4:1 } } }")),
                dynamicTest("No precedence: 3 * «R»", () -> doParse(mixed, "3 * «R»", "0..7 sequence{ 0..7 repetition{ 3, 4..7 reflection{ 5..6 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * R rot CU", () -> doParse(mixed, "3 * R rot CU", "0..12 sequence{ 0..12 rotation{ 10..12 move{ 1:7:1 } 0..5 repetition{ 3, 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 4 * 3 * R rot CU", () -> doParse(mixed, "4 * 3 * R rot CU", "0..16 sequence{ 0..16 rotation{ 14..16 move{ 1:7:1 } 0..9 repetition{ 4, 4..9 repetition{ 3, 8..9 move{ 0:4:1 } } } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R'", () -> doParse(mixed, "<CU>R'", "0..6 sequence{ 0..6 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..6 inversion{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Preinfix: 3 * R'", () -> doParse(mixed, "3 * R'", "0..6 sequence{ 0..6 repetition{ 3, 4..6 inversion{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("Prefix precedes Preinfix: 3 * <CU>R", () -> doParse(mixed, "3 * <CU>R", "0..9 sequence{ 0..9 repetition{ 3, 4..9 conjugation{ 5..7 sequence{ 5..7 move{ 1:7:1 } } 8..9 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * R rot CU", () -> doParse(mixed, "3 * R rot CU", "0..12 sequence{ 0..12 rotation{ 10..12 move{ 1:7:1 } 0..5 repetition{ 3, 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * CU comm R", () -> doParse(mixed, "3 * CU comm R", "0..13 sequence{ 0..13 commutation{ 0..6 repetition{ 3, 4..6 move{ 1:7:1 } } 12..13 move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: (3 * CU) comm R", () -> doParse(mixed, "(3 * CU) comm R", "0..15 sequence{ 0..15 commutation{ 0..8 grouping{ 1..7 repetition{ 3, 5..7 move{ 1:7:1 } } } 14..15 move{ 0:4:1 } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testOperatorPrecedenceB() {
        // Expected
        // Precedence : Syntax : Associativity
        //  1 : CIRCUMFIX : inside-to-outside
        //  1 : SUFFIX : left-to-right
        //  2 : PREFIX : right-to-left
        //  2 : PRECIRCUMFIX : right-to-left
        //  2 : POSTINFIX : right-to-left
        //  2 : PREINFIX : right-to-left

        return Arrays.asList(
                dynamicTest("No precedence: R 3", () -> doParse(mixedB, "R 3", "0..3 sequence{ 0..3 repetition{ 3, 0..1 move{ 0:4:1 } } }")),
                dynamicTest("No precedence: «R» 3", () -> doParse(mixedB, "«R» 3", "0..5 sequence{ 0..5 repetition{ 3, 0..3 reflection{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Postinfix: R rot CU 3", () -> doParse(mixedB, "R rot CU 3", "0..10 sequence{ 0..10 rotation{ 6..10 repetition{ 3, 6..8 move{ 1:7:1 } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Postinfix: R rot CU 3 4", () -> doParse(mixedB, "R rot CU 3 4", "0..12 sequence{ 0..12 rotation{ 6..12 repetition{ 4, 6..10 repetition{ 3, 6..8 move{ 1:7:1 } } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R'", () -> doParse(mixedB, "<CU>R'", "0..6 sequence{ 0..6 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..6 inversion{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: R' 3", () -> doParse(mixedB, "R' 3", "0..4 sequence{ 0..4 repetition{ 3, 0..2 inversion{ 0..1 move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R 3", () -> doParse(mixedB, "<CU>R 3", "0..7 sequence{ 0..7 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..7 repetition{ 3, 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: R rot CU 3", () -> doParse(mixedB, "R rot CU 3", "0..10 sequence{ 0..10 rotation{ 6..10 repetition{ 3, 6..8 move{ 1:7:1 } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Preinfix: CU comm R 3", () -> doParse(mixedB, "CU comm R 3", "0..11 sequence{ 0..11 commutation{ 0..2 move{ 1:7:1 } 8..11 repetition{ 3, 8..9 move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: CU 3 comm R", () -> doParse(mixedB, "CU 3 comm R", "0..11 sequence{ 0..11 commutation{ 0..4 repetition{ 3, 0..2 move{ 1:7:1 } } 10..11 move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: (CU 3) comm R", () -> doParse(mixedB, "(CU 3) comm R", "0..13 sequence{ 0..13 commutation{ 0..6 grouping{ 1..5 repetition{ 3, 1..3 move{ 1:7:1 } } } 12..13 move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: CU comm (R 3)", () -> doParse(mixedB, "CU comm (R 3)", "0..13 sequence{ 0..13 commutation{ 0..2 move{ 1:7:1 } 8..13 grouping{ 9..12 repetition{ 3, 9..10 move{ 0:4:1 } } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseDefaultNotation() {
        return Arrays.asList(
                dynamicTest("<empty>", () -> doParse(defaultNotatioon, "", "0..0 sequence{ }")),
                dynamicTest("<space>", () -> doParse(defaultNotatioon, " ", "0..0 sequence{ }")),
                dynamicTest("<nbsp>", () -> doParse(defaultNotatioon, "\u00a0", "0..0 sequence{ }")),
                dynamicTest(".", () -> doParse(defaultNotatioon, ".", "0..1 sequence{ 0..1 nop{ } }")),
                dynamicTest("R", () -> doParse(defaultNotatioon, "R", "0..1 sequence{ 0..1 move{ 0:4:1 } }")),
                dynamicTest("U", () -> doParse(defaultNotatioon, "U", "0..1 sequence{ 0..1 move{ 1:4:1 } }")),
                dynamicTest("F", () -> doParse(defaultNotatioon, "F", "0..1 sequence{ 0..1 move{ 2:4:1 } }")),
                dynamicTest("L", () -> doParse(defaultNotatioon, "L", "0..1 sequence{ 0..1 move{ 0:1:-1 } }")),
                dynamicTest("D", () -> doParse(defaultNotatioon, "D", "0..1 sequence{ 0..1 move{ 1:1:-1 } }")),
                dynamicTest("B", () -> doParse(defaultNotatioon, "B", "0..1 sequence{ 0..1 move{ 2:1:-1 } }")),
                dynamicTest("R'", () -> doParse(defaultNotatioon, "R'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 0:4:1 } } }")),
                dynamicTest("U'", () -> doParse(defaultNotatioon, "U'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 1:4:1 } } }")),
                dynamicTest("F'", () -> doParse(defaultNotatioon, "F'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 2:4:1 } } }")),
                dynamicTest("L'", () -> doParse(defaultNotatioon, "L'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 0:1:-1 } } }")),
                dynamicTest("D'", () -> doParse(defaultNotatioon, "D'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 1:1:-1 } } }")),
                dynamicTest("B'", () -> doParse(defaultNotatioon, "B'", "0..2 sequence{ 0..2 inversion{ 0..1 move{ 2:1:-1 } } }")),
                dynamicTest("R2", () -> doParse(defaultNotatioon, "R2", "0..2 sequence{ 0..2 move{ 0:4:2 } }")),
                dynamicTest("U2", () -> doParse(defaultNotatioon, "U2", "0..2 sequence{ 0..2 move{ 1:4:2 } }")),
                dynamicTest("F2", () -> doParse(defaultNotatioon, "F2", "0..2 sequence{ 0..2 move{ 2:4:2 } }")),
                dynamicTest("L2", () -> doParse(defaultNotatioon, "L2", "0..2 sequence{ 0..2 move{ 0:1:-2 } }")),
                dynamicTest("D2", () -> doParse(defaultNotatioon, "D2", "0..2 sequence{ 0..2 move{ 1:1:-2 } }")),
                dynamicTest("B2", () -> doParse(defaultNotatioon, "B2", "0..2 sequence{ 0..2 move{ 2:1:-2 } }")),
                dynamicTest("MR", () -> doParse(defaultNotatioon, "MR", "0..2 sequence{ 0..2 move{ 0:2:1 } }")),
                dynamicTest("MU", () -> doParse(defaultNotatioon, "MU", "0..2 sequence{ 0..2 move{ 1:2:1 } }")),
                dynamicTest("MF", () -> doParse(defaultNotatioon, "MF", "0..2 sequence{ 0..2 move{ 2:2:1 } }")),
                dynamicTest("ML", () -> doParse(defaultNotatioon, "ML", "0..2 sequence{ 0..2 move{ 0:2:-1 } }")),
                dynamicTest("MD", () -> doParse(defaultNotatioon, "MD", "0..2 sequence{ 0..2 move{ 1:2:-1 } }")),
                dynamicTest("MB", () -> doParse(defaultNotatioon, "MB", "0..2 sequence{ 0..2 move{ 2:2:-1 } }")),
                dynamicTest("MR'", () -> doParse(defaultNotatioon, "MR'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:2:1 } } }")),
                dynamicTest("MU'", () -> doParse(defaultNotatioon, "MU'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:2:1 } } }")),
                dynamicTest("MF'", () -> doParse(defaultNotatioon, "MF'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:2:1 } } }")),
                dynamicTest("ML'", () -> doParse(defaultNotatioon, "ML'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:2:-1 } } }")),
                dynamicTest("MD'", () -> doParse(defaultNotatioon, "MD'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:2:-1 } } }")),
                dynamicTest("MB'", () -> doParse(defaultNotatioon, "MB'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:2:-1 } } }")),
                dynamicTest("MR2", () -> doParse(defaultNotatioon, "MR2", "0..3 sequence{ 0..3 move{ 0:2:2 } }")),
                dynamicTest("MU2", () -> doParse(defaultNotatioon, "MU2", "0..3 sequence{ 0..3 move{ 1:2:2 } }")),
                dynamicTest("MF2", () -> doParse(defaultNotatioon, "MF2", "0..3 sequence{ 0..3 move{ 2:2:2 } }")),
                dynamicTest("ML2", () -> doParse(defaultNotatioon, "ML2", "0..3 sequence{ 0..3 move{ 0:2:-2 } }")),
                dynamicTest("MD2", () -> doParse(defaultNotatioon, "MD2", "0..3 sequence{ 0..3 move{ 1:2:-2 } }")),
                dynamicTest("MB2", () -> doParse(defaultNotatioon, "MB2", "0..3 sequence{ 0..3 move{ 2:2:-2 } }")),
                dynamicTest("TR", () -> doParse(defaultNotatioon, "TR", "0..2 sequence{ 0..2 move{ 0:6:1 } }")),
                dynamicTest("TU", () -> doParse(defaultNotatioon, "TU", "0..2 sequence{ 0..2 move{ 1:6:1 } }")),
                dynamicTest("TF", () -> doParse(defaultNotatioon, "TF", "0..2 sequence{ 0..2 move{ 2:6:1 } }")),
                dynamicTest("TL", () -> doParse(defaultNotatioon, "TL", "0..2 sequence{ 0..2 move{ 0:3:-1 } }")),
                dynamicTest("TD", () -> doParse(defaultNotatioon, "TD", "0..2 sequence{ 0..2 move{ 1:3:-1 } }")),
                dynamicTest("TB", () -> doParse(defaultNotatioon, "TB", "0..2 sequence{ 0..2 move{ 2:3:-1 } }")),
                dynamicTest("TR'", () -> doParse(defaultNotatioon, "TR'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:6:1 } } }")),
                dynamicTest("TU'", () -> doParse(defaultNotatioon, "TU'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:6:1 } } }")),
                dynamicTest("TF'", () -> doParse(defaultNotatioon, "TF'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:6:1 } } }")),
                dynamicTest("TL'", () -> doParse(defaultNotatioon, "TL'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:3:-1 } } }")),
                dynamicTest("TD'", () -> doParse(defaultNotatioon, "TD'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:3:-1 } } }")),
                dynamicTest("TB'", () -> doParse(defaultNotatioon, "TB'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:3:-1 } } }")),
                dynamicTest("TR2", () -> doParse(defaultNotatioon, "TR2", "0..3 sequence{ 0..3 move{ 0:6:2 } }")),
                dynamicTest("TU2", () -> doParse(defaultNotatioon, "TU2", "0..3 sequence{ 0..3 move{ 1:6:2 } }")),
                dynamicTest("TF2", () -> doParse(defaultNotatioon, "TF2", "0..3 sequence{ 0..3 move{ 2:6:2 } }")),
                dynamicTest("TL2", () -> doParse(defaultNotatioon, "TL2", "0..3 sequence{ 0..3 move{ 0:3:-2 } }")),
                dynamicTest("TD2", () -> doParse(defaultNotatioon, "TD2", "0..3 sequence{ 0..3 move{ 1:3:-2 } }")),
                dynamicTest("TB2", () -> doParse(defaultNotatioon, "TB2", "0..3 sequence{ 0..3 move{ 2:3:-2 } }")),
                dynamicTest("CR", () -> doParse(defaultNotatioon, "CR", "0..2 sequence{ 0..2 move{ 0:7:1 } }")),
                dynamicTest("CU", () -> doParse(defaultNotatioon, "CU", "0..2 sequence{ 0..2 move{ 1:7:1 } }")),
                dynamicTest("CF", () -> doParse(defaultNotatioon, "CF", "0..2 sequence{ 0..2 move{ 2:7:1 } }")),
                dynamicTest("CL", () -> doParse(defaultNotatioon, "CL", "0..2 sequence{ 0..2 move{ 0:7:-1 } }")),
                dynamicTest("CD", () -> doParse(defaultNotatioon, "CD", "0..2 sequence{ 0..2 move{ 1:7:-1 } }")),
                dynamicTest("CB", () -> doParse(defaultNotatioon, "CB", "0..2 sequence{ 0..2 move{ 2:7:-1 } }")),
                dynamicTest("CR'", () -> doParse(defaultNotatioon, "CR'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:7:1 } } }")),
                dynamicTest("CU'", () -> doParse(defaultNotatioon, "CU'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:7:1 } } }")),
                dynamicTest("CF'", () -> doParse(defaultNotatioon, "CF'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:7:1 } } }")),
                dynamicTest("CL'", () -> doParse(defaultNotatioon, "CL'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:7:-1 } } }")),
                dynamicTest("CD'", () -> doParse(defaultNotatioon, "CD'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:7:-1 } } }")),
                dynamicTest("CB'", () -> doParse(defaultNotatioon, "CB'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:7:-1 } } }")),
                dynamicTest("CR2", () -> doParse(defaultNotatioon, "CR2", "0..3 sequence{ 0..3 move{ 0:7:2 } }")),
                dynamicTest("CU2", () -> doParse(defaultNotatioon, "CU2", "0..3 sequence{ 0..3 move{ 1:7:2 } }")),
                dynamicTest("CF2", () -> doParse(defaultNotatioon, "CF2", "0..3 sequence{ 0..3 move{ 2:7:2 } }")),
                dynamicTest("CL2", () -> doParse(defaultNotatioon, "CL2", "0..3 sequence{ 0..3 move{ 0:7:-2 } }")),
                dynamicTest("CD2", () -> doParse(defaultNotatioon, "CD2", "0..3 sequence{ 0..3 move{ 1:7:-2 } }")),
                dynamicTest("CB2", () -> doParse(defaultNotatioon, "CB2", "0..3 sequence{ 0..3 move{ 2:7:-2 } }")),
                dynamicTest("SR", () -> doParse(defaultNotatioon, "SR", "0..2 sequence{ 0..2 move{ 0:5:1 } }")),
                dynamicTest("SU", () -> doParse(defaultNotatioon, "SU", "0..2 sequence{ 0..2 move{ 1:5:1 } }")),
                dynamicTest("SF", () -> doParse(defaultNotatioon, "SF", "0..2 sequence{ 0..2 move{ 2:5:1 } }")),
                dynamicTest("SL", () -> doParse(defaultNotatioon, "SL", "0..2 sequence{ 0..2 move{ 0:5:-1 } }")),
                dynamicTest("SD", () -> doParse(defaultNotatioon, "SD", "0..2 sequence{ 0..2 move{ 1:5:-1 } }")),
                dynamicTest("SB", () -> doParse(defaultNotatioon, "SB", "0..2 sequence{ 0..2 move{ 2:5:-1 } }")),
                dynamicTest("SR'", () -> doParse(defaultNotatioon, "SR'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:5:1 } } }")),
                dynamicTest("SU'", () -> doParse(defaultNotatioon, "SU'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:5:1 } } }")),
                dynamicTest("SF'", () -> doParse(defaultNotatioon, "SF'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:5:1 } } }")),
                dynamicTest("SL'", () -> doParse(defaultNotatioon, "SL'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 0:5:-1 } } }")),
                dynamicTest("SD'", () -> doParse(defaultNotatioon, "SD'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 1:5:-1 } } }")),
                dynamicTest("SB'", () -> doParse(defaultNotatioon, "SB'", "0..3 sequence{ 0..3 inversion{ 0..2 move{ 2:5:-1 } } }")),
                dynamicTest("SR2", () -> doParse(defaultNotatioon, "SR2", "0..3 sequence{ 0..3 move{ 0:5:2 } }")),
                dynamicTest("SU2", () -> doParse(defaultNotatioon, "SU2", "0..3 sequence{ 0..3 move{ 1:5:2 } }")),
                dynamicTest("SF2", () -> doParse(defaultNotatioon, "SF2", "0..3 sequence{ 0..3 move{ 2:5:2 } }")),
                dynamicTest("SL2", () -> doParse(defaultNotatioon, "SL2", "0..3 sequence{ 0..3 move{ 0:5:-2 } }")),
                dynamicTest("SD2", () -> doParse(defaultNotatioon, "SD2", "0..3 sequence{ 0..3 move{ 1:5:-2 } }")),
                dynamicTest("SB2", () -> doParse(defaultNotatioon, "SB2", "0..3 sequence{ 0..3 move{ 2:5:-2 } }")),
                dynamicTest("R U F", () -> doParse(defaultNotatioon, "R U F", "0..5 sequence{ 0..1 move{ 0:4:1 } 2..3 move{ 1:4:1 } 4..5 move{ 2:4:1 } }")),
                dynamicTest("(R U F)", () -> doParse(defaultNotatioon, "(R U F)", "0..7 sequence{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } }")),
                dynamicTest("(R U F)'", () -> doParse(defaultNotatioon, "(R U F)'", "0..8 sequence{ 0..8 inversion{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } }")),
                dynamicTest("(R)2", () -> doParse(defaultNotatioon, "(R)2", "0..4 sequence{ 0..4 repetition{ 2, 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(defaultNotatioon, "R3", "0..2 sequence{ 0..2 repetition{ 3, 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(defaultNotatioon, "(R U F)3", "0..8 sequence{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(defaultNotatioon, "(R U F)'3", "0..9 sequence{ 0..9 repetition{ 3, 0..8 inversion{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(defaultNotatioon, "(R U F)3'", "0..9 sequence{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(defaultNotatioon, "(R U F)3''", "0..10 sequence{ 0..10 inversion{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(defaultNotatioon, "(R U F)3'4", "0..10 sequence{ 0..10 repetition{ 4, 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(defaultNotatioon, "(R)'", "0..4 sequence{ 0..4 inversion{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(defaultNotatioon, "(R F)'", "0..6 sequence{ 0..6 inversion{ 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(defaultNotatioon, "(R- U F)- (R' U F)'",
                        "0..19 sequence{ 0..9 inversion{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } 10..19 inversion{ 10..18 grouping{ 11..13 inversion{ 11..12 move{ 0:4:1 } } 14..15 move{ 1:4:1 } 16..17 move{ 2:4:1 } } } }")),
                dynamicTest("<CU>R", () -> doParse(defaultNotatioon, "<CU>R", "0..5 sequence{ 0..5 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 move{ 0:4:1 } } }")),
                dynamicTest("<CU CF>(R)", () -> doParse(defaultNotatioon, "<CU CF>(R)", "0..10 sequence{ 0..10 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..10 grouping{ 8..9 move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>'(R)", () -> doParse(defaultNotatioon, "<CU CF>'(R)", "0..11 sequence{ 0..11 rotation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 8..11 grouping{ 9..10 move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>(R B)", () -> doParse(defaultNotatioon, "<CU CF>(R B)", "0..12 sequence{ 0..12 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..12 grouping{ 8..9 move{ 0:4:1 } 10..11 move{ 2:1:-1 } } } }")),
                dynamicTest("<R>U", () -> doParse(defaultNotatioon, "<R>U", "0..4 sequence{ 0..4 conjugation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 move{ 1:4:1 } } }")),
                dynamicTest("[CU,R]", () -> doParse(defaultNotatioon, "[CU,R]", "0..6 sequence{ 0..6 commutation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 sequence{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R]", () -> doParse(defaultNotatioon, "[CU CF,R]", "0..9 sequence{ 0..9 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..8 sequence{ 7..8 move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R B]", () -> doParse(defaultNotatioon, "[CU CF,R B]", "0..11 sequence{ 0..11 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..10 sequence{ 7..8 move{ 0:4:1 } 9..10 move{ 2:1:-1 } } } }")),
                dynamicTest("[R,U]", () -> doParse(defaultNotatioon, "[R,U]", "0..5 sequence{ 0..5 commutation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 sequence{ 3..4 move{ 1:4:1 } } } }")),
                dynamicTest("R U R'", () -> doParse(defaultNotatioon, "R U R'", "0..6 sequence{ 0..1 move{ 0:4:1 } 2..3 move{ 1:4:1 } 4..6 inversion{ 4..5 move{ 0:4:1 } } }")),
                dynamicTest("R U /* a comment */ R' U'", () -> doParse(defaultNotatioon, "R U /* a comment */ R' U'", "0..25 sequence{ 0..1 move{ 0:4:1 } 2..3 move{ 1:4:1 } 20..22 inversion{ 20..21 move{ 0:4:1 } } 23..25 inversion{ 23..24 move{ 1:4:1 } } }")),
                dynamicTest("R*", () -> doParse(defaultNotatioon, "R*", "0..2 sequence{ 0..2 reflection{ 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R' U F)*", () -> doParse(defaultNotatioon, "(R' U F)*", "0..9 sequence{ 0..9 reflection{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }")),
                dynamicTest("R . U · F", () -> doParse(defaultNotatioon, "R . U · F", "0..9 sequence{ 0..1 move{ 0:4:1 } 2..3 nop{ } 4..5 move{ 1:4:1 } 6..7 nop{ } 8..9 move{ 2:4:1 } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParsePrefixNotation() {
        return Arrays.asList(
                dynamicTest("2(R)", () -> doParse(prefix, "2(R)", "0..4 sequence{ 0..4 repetition{ 2, 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("3R", () -> doParse(prefix, "3R", "0..2 sequence{ 0..2 repetition{ 3, 1..2 move{ 0:4:1 } } }")),
                dynamicTest("3(R U F)", () -> doParse(prefix, "3(R U F)", "0..8 sequence{ 0..8 repetition{ 3, 1..8 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }")),
                dynamicTest("3'(R U F)", () -> doParse(prefix, "3'(R U F)", "0..9 sequence{ 0..9 repetition{ 3, 1..9 inversion{ 2..9 grouping{ 3..4 move{ 0:4:1 } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } } }")),
                dynamicTest("'3(R U F)", () -> doParse(prefix, "'3(R U F)", "0..9 sequence{ 0..9 inversion{ 1..9 repetition{ 3, 2..9 grouping{ 3..4 move{ 0:4:1 } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } } }")),
                dynamicTest("''3(R U F)", () -> doParse(prefix, "''3(R U F)", "0..10 sequence{ 0..10 inversion{ 1..10 inversion{ 2..10 repetition{ 3, 3..10 grouping{ 4..5 move{ 0:4:1 } 6..7 move{ 1:4:1 } 8..9 move{ 2:4:1 } } } } } }")),
                dynamicTest("4'3(R U F)", () -> doParse(prefix, "4'3(R U F)", "0..10 sequence{ 0..10 repetition{ 4, 1..10 inversion{ 2..10 repetition{ 3, 3..10 grouping{ 4..5 move{ 0:4:1 } 6..7 move{ 1:4:1 } 8..9 move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(prefix, "'(R)", "0..4 sequence{ 0..4 inversion{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(prefix, "'(R F)", "0..6 sequence{ 0..6 inversion{ 1..6 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(prefix, "-(-R U F) '('R U F)", "0..19 sequence{ 0..9 inversion{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } 10..19 inversion{ 11..19 grouping{ 12..14 inversion{ 13..14 move{ 0:4:1 } } 15..16 move{ 1:4:1 } 17..18 move{ 2:4:1 } } } }")),
                dynamicTest("<CU>R", () -> doParse(prefix, "<CU>R", "0..5 sequence{ 0..5 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 move{ 0:4:1 } } }")),
                dynamicTest("<CU CF>(R)", () -> doParse(prefix, "<CU CF>(R)", "0..10 sequence{ 0..10 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..10 grouping{ 8..9 move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>(R B)", () -> doParse(prefix, "<CU CF>(R B)", "0..12 sequence{ 0..12 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..12 grouping{ 8..9 move{ 0:4:1 } 10..11 move{ 2:1:-1 } } } }")),
                dynamicTest("<R>U", () -> doParse(prefix, "<R>U", "0..4 sequence{ 0..4 conjugation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 move{ 1:4:1 } } }")),
                dynamicTest("[CU]R", () -> doParse(prefix, "[CU]R", "0..5 sequence{ 0..5 commutation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 move{ 0:4:1 } } }")),
                dynamicTest("[CU CF]R", () -> doParse(prefix, "[CU CF]R", "0..8 sequence{ 0..8 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..8 move{ 0:4:1 } } }")),
                dynamicTest("[CU CF](R B)", () -> doParse(prefix, "[CU CF](R B)", "0..12 sequence{ 0..12 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..12 grouping{ 8..9 move{ 0:4:1 } 10..11 move{ 2:1:-1 } } } }")),
                dynamicTest("[R]U", () -> doParse(prefix, "[R]U", "0..4 sequence{ 0..4 commutation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 move{ 1:4:1 } } }")),
                dynamicTest("*(R)", () -> doParse(prefix, "*(R)", "0..4 sequence{ 0..4 reflection{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(prefix, "*('R U F)", "0..9 sequence{ 0..9 reflection{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParcePrecircumfixNotation() {
        return Arrays.asList(
                dynamicTest("2(R)", () -> doParse(precircumfix, "2(R)", "0..4 sequence{ 0..4 repetition{ 2, 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("3R", () -> doParse(precircumfix, "3R", "0..2 sequence{ 0..2 repetition{ 3, 1..2 move{ 0:4:1 } } }")),
                dynamicTest("3(R U F)", () -> doParse(precircumfix, "3(R U F)", "0..8 sequence{ 0..8 repetition{ 3, 1..8 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }")),
                dynamicTest("3'(R U F)", () -> doParse(precircumfix, "3'(R U F)", "0..9 sequence{ 0..9 repetition{ 3, 1..9 inversion{ 2..9 grouping{ 3..4 move{ 0:4:1 } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } } }")),
                dynamicTest("'3(R U F)", () -> doParse(precircumfix, "'3(R U F)", "0..9 sequence{ 0..9 inversion{ 1..9 repetition{ 3, 2..9 grouping{ 3..4 move{ 0:4:1 } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } } }")),
                dynamicTest("''3(R U F)", () -> doParse(precircumfix, "''3(R U F)", "0..10 sequence{ 0..10 inversion{ 1..10 inversion{ 2..10 repetition{ 3, 3..10 grouping{ 4..5 move{ 0:4:1 } 6..7 move{ 1:4:1 } 8..9 move{ 2:4:1 } } } } } }")),
                dynamicTest("4'3(R U F)", () -> doParse(precircumfix, "4'3(R U F)", "0..10 sequence{ 0..10 repetition{ 4, 1..10 inversion{ 2..10 repetition{ 3, 3..10 grouping{ 4..5 move{ 0:4:1 } 6..7 move{ 1:4:1 } 8..9 move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(precircumfix, "'(R)", "0..4 sequence{ 0..4 inversion{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(precircumfix, "'(R F)", "0..6 sequence{ 0..6 inversion{ 1..6 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(precircumfix, "-(-R U F) '('R U F)", "0..19 sequence{ 0..9 inversion{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } 10..19 inversion{ 11..19 grouping{ 12..14 inversion{ 13..14 move{ 0:4:1 } } 15..16 move{ 1:4:1 } 17..18 move{ 2:4:1 } } } }")),
                dynamicTest("<CU,R>", () -> doParse(precircumfix, "<CU,R>", "0..6 sequence{ 0..6 conjugation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 sequence{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF,R>", () -> doParse(precircumfix, "<CU CF,R>", "0..9 sequence{ 0..9 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..8 sequence{ 7..8 move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF,R B>", () -> doParse(precircumfix, "<CU CF,R B>", "0..11 sequence{ 0..11 conjugation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..10 sequence{ 7..8 move{ 0:4:1 } 9..10 move{ 2:1:-1 } } } }")),
                dynamicTest("<R,U>", () -> doParse(precircumfix, "<R,U>", "0..5 sequence{ 0..5 conjugation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 sequence{ 3..4 move{ 1:4:1 } } } }")),
                dynamicTest("[CU,R]", () -> doParse(precircumfix, "[CU,R]", "0..6 sequence{ 0..6 commutation{ 1..3 sequence{ 1..3 move{ 1:7:1 } } 4..5 sequence{ 4..5 move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R]", () -> doParse(precircumfix, "[CU CF,R]", "0..9 sequence{ 0..9 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..8 sequence{ 7..8 move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R B]", () -> doParse(precircumfix, "[CU CF,R B]", "0..11 sequence{ 0..11 commutation{ 1..6 sequence{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 7..10 sequence{ 7..8 move{ 0:4:1 } 9..10 move{ 2:1:-1 } } } }")),
                dynamicTest("[R,U]", () -> doParse(precircumfix, "[R,U]", "0..5 sequence{ 0..5 commutation{ 1..2 sequence{ 1..2 move{ 0:4:1 } } 3..4 sequence{ 3..4 move{ 1:4:1 } } } }")),
                dynamicTest("*(R)", () -> doParse(precircumfix, "*(R)", "0..4 sequence{ 0..4 reflection{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(precircumfix, "*('R U F)", "0..9 sequence{ 0..9 reflection{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseSuffixNotation() {
        return Arrays.asList(
                dynamicTest("(R)2", () -> doParse(suffix, "(R)2", "0..4 sequence{ 0..4 repetition{ 2, 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(suffix, "R3", "0..2 sequence{ 0..2 repetition{ 3, 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(suffix, "(R U F)3", "0..8 sequence{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(suffix, "(R U F)'3", "0..9 sequence{ 0..9 repetition{ 3, 0..8 inversion{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(suffix, "(R U F)3'", "0..9 sequence{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(suffix, "(R U F)3''", "0..10 sequence{ 0..10 inversion{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(suffix, "(R U F)3'4", "0..10 sequence{ 0..10 repetition{ 4, 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(suffix, "(R)'", "0..4 sequence{ 0..4 inversion{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(suffix, "(R F)'", "0..6 sequence{ 0..6 inversion{ 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(suffix, "(R- U F)- (R' U F)'", "0..19 sequence{ 0..9 inversion{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } 10..19 inversion{ 10..18 grouping{ 11..13 inversion{ 11..12 move{ 0:4:1 } } 14..15 move{ 1:4:1 } 16..17 move{ 2:4:1 } } } }")),
                dynamicTest("R<CU>", () -> doParse(suffix, "R<CU>", "0..5 sequence{ 0..5 conjugation{ 2..4 sequence{ 2..4 move{ 1:7:1 } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R)<CU CF>", () -> doParse(suffix, "(R)<CU CF>", "0..10 sequence{ 0..10 conjugation{ 4..9 sequence{ 4..6 move{ 1:7:1 } 7..9 move{ 2:7:1 } } 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R B)<CU CF>", () -> doParse(suffix, "(R B)<CU CF>", "0..12 sequence{ 0..12 conjugation{ 6..11 sequence{ 6..8 move{ 1:7:1 } 9..11 move{ 2:7:1 } } 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:1:-1 } } } }")),
                dynamicTest("U<R>", () -> doParse(suffix, "U<R>", "0..4 sequence{ 0..4 conjugation{ 2..3 sequence{ 2..3 move{ 0:4:1 } } 0..1 move{ 1:4:1 } } }")),
                dynamicTest("R[CU]", () -> doParse(suffix, "R[CU]", "0..5 sequence{ 0..5 commutation{ 2..4 sequence{ 2..4 move{ 1:7:1 } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("R[CU CF]", () -> doParse(suffix, "R[CU CF]", "0..8 sequence{ 0..8 commutation{ 2..7 sequence{ 2..4 move{ 1:7:1 } 5..7 move{ 2:7:1 } } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R B)[CU CF]", () -> doParse(suffix, "(R B)[CU CF]", "0..12 sequence{ 0..12 commutation{ 6..11 sequence{ 6..8 move{ 1:7:1 } 9..11 move{ 2:7:1 } } 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:1:-1 } } } }")),
                dynamicTest("U[R]", () -> doParse(suffix, "U[R]", "0..4 sequence{ 0..4 commutation{ 2..3 sequence{ 2..3 move{ 0:4:1 } } 0..1 move{ 1:4:1 } } }")),
                dynamicTest("(R)*", () -> doParse(suffix, "(R)*", "0..4 sequence{ 0..4 reflection{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(suffix, "(R' U F)*", "0..9 sequence{ 0..9 reflection{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParsePostcircumfixNotation() {
        return Arrays.asList(
                dynamicTest("(R)2", () -> doParse(postcircumfix, "(R)2", "0..4 sequence{ 0..4 repetition{ 2, 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(postcircumfix, "R3", "0..2 sequence{ 0..2 repetition{ 3, 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(postcircumfix, "(R U F)3", "0..8 sequence{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(postcircumfix, "(R U F)'3", "0..9 sequence{ 0..9 repetition{ 3, 0..8 inversion{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(postcircumfix, "(R U F)3'", "0..9 sequence{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(postcircumfix, "(R U F)3''", "0..10 sequence{ 0..10 inversion{ 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(postcircumfix, "(R U F)3'4", "0..10 sequence{ 0..10 repetition{ 4, 0..9 inversion{ 0..8 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(postcircumfix, "(R)'", "0..4 sequence{ 0..4 inversion{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(postcircumfix, "(R F)'", "0..6 sequence{ 0..6 inversion{ 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(postcircumfix, "(R- U F)- (R' U F)'", "0..19 sequence{ 0..9 inversion{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } 10..19 inversion{ 10..18 grouping{ 11..13 inversion{ 11..12 move{ 0:4:1 } } 14..15 move{ 1:4:1 } 16..17 move{ 2:4:1 } } } }")),
                dynamicTest("<R,CU>", () -> doParse(postcircumfix, "<R,CU>", "0..6 sequence{ 0..6 conjugation{ 3..5 sequence{ 3..5 move{ 1:7:1 } } 1..2 sequence{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("<(R),CU CF>", () -> doParse(postcircumfix, "<(R),CU CF>", "0..11 sequence{ 0..11 conjugation{ 5..10 sequence{ 5..7 move{ 1:7:1 } 8..10 move{ 2:7:1 } } 1..4 sequence{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } } }")),
                dynamicTest("<(R B),CU CF>", () -> doParse(postcircumfix, "<(R B),CU CF>", "0..13 sequence{ 0..13 conjugation{ 7..12 sequence{ 7..9 move{ 1:7:1 } 10..12 move{ 2:7:1 } } 1..6 sequence{ 1..6 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 2:1:-1 } } } } }")),
                dynamicTest("<U, R>", () -> doParse(postcircumfix, "<U, R>", "0..6 sequence{ 0..6 conjugation{ 3..5 sequence{ 4..5 move{ 0:4:1 } } 1..2 sequence{ 1..2 move{ 1:4:1 } } } }")),
                dynamicTest("[R,CU]", () -> doParse(postcircumfix, "[R,CU]", "0..6 sequence{ 0..6 commutation{ 3..5 sequence{ 3..5 move{ 1:7:1 } } 1..2 sequence{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("[R,CU CF]", () -> doParse(postcircumfix, "[R,CU CF]", "0..9 sequence{ 0..9 commutation{ 3..8 sequence{ 3..5 move{ 1:7:1 } 6..8 move{ 2:7:1 } } 1..2 sequence{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("[R B,CU CF]", () -> doParse(postcircumfix, "[R B,CU CF]", "0..11 sequence{ 0..11 commutation{ 5..10 sequence{ 5..7 move{ 1:7:1 } 8..10 move{ 2:7:1 } } 1..4 sequence{ 1..2 move{ 0:4:1 } 3..4 move{ 2:1:-1 } } } }")),
                dynamicTest("[U,R]", () -> doParse(postcircumfix, "[U,R]", "0..5 sequence{ 0..5 commutation{ 3..4 sequence{ 3..4 move{ 0:4:1 } } 1..2 sequence{ 1..2 move{ 1:4:1 } } } }")),
                dynamicTest("(R)*", () -> doParse(postcircumfix, "(R)*", "0..4 sequence{ 0..4 reflection{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(postcircumfix, "(R' U F)*", "0..9 sequence{ 0..9 reflection{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParsePreinfixNotation() {
        return Arrays.asList(
                dynamicTest("2 times (R)", () -> doParse(preinfix, "2 times (R)", "0..11 sequence{ 0..11 repetition{ 2, 8..11 grouping{ 9..10 move{ 0:4:1 } } } }")),
                dynamicTest("3 times R", () -> doParse(preinfix, "3 times R", "0..9 sequence{ 0..9 repetition{ 3, 8..9 move{ 0:4:1 } } }")),
                dynamicTest("3 times (R U F)", () -> doParse(preinfix, "3 times (R U F)", "0..15 sequence{ 0..15 repetition{ 3, 8..15 grouping{ 9..10 move{ 0:4:1 } 11..12 move{ 1:4:1 } 13..14 move{ 2:4:1 } } } }")),
                dynamicTest("3 times '(R U F)", () -> doParse(preinfix, "3 times '(R U F)", "0..16 sequence{ 0..16 repetition{ 3, 8..16 inversion{ 9..16 grouping{ 10..11 move{ 0:4:1 } 12..13 move{ 1:4:1 } 14..15 move{ 2:4:1 } } } } }")),
                dynamicTest("'3 times (R U F)", () -> doParse(preinfix, "'3 times (R U F)", "0..16 sequence{ 0..16 inversion{ 1..16 repetition{ 3, 9..16 grouping{ 10..11 move{ 0:4:1 } 12..13 move{ 1:4:1 } 14..15 move{ 2:4:1 } } } } }")),
                dynamicTest("''3 times (R U F)", () -> doParse(preinfix, "''3 times (R U F)", "0..17 sequence{ 0..17 inversion{ 1..17 inversion{ 2..17 repetition{ 3, 10..17 grouping{ 11..12 move{ 0:4:1 } 13..14 move{ 1:4:1 } 15..16 move{ 2:4:1 } } } } } }")),
                dynamicTest("4 times '3 times (R U F)", () -> doParse(preinfix, "4 times '3 times (R U F)", "0..24 sequence{ 0..24 repetition{ 4, 8..24 inversion{ 9..24 repetition{ 3, 17..24 grouping{ 18..19 move{ 0:4:1 } 20..21 move{ 1:4:1 } 22..23 move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(preinfix, "'(R)", "0..4 sequence{ 0..4 inversion{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(preinfix, "'(R F)", "0..6 sequence{ 0..6 inversion{ 1..6 grouping{ 2..3 move{ 0:4:1 } 4..5 move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(preinfix, "-(-R U F) '('R U F)", "0..19 sequence{ 0..9 inversion{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } 10..19 inversion{ 11..19 grouping{ 12..14 inversion{ 13..14 move{ 0:4:1 } } 15..16 move{ 1:4:1 } 17..18 move{ 2:4:1 } } } }")),
                dynamicTest("CU conj R", () -> doParse(preinfix, "CU conj R", "0..9 sequence{ 0..9 conjugation{ 0..2 move{ 1:7:1 } 8..9 move{ 0:4:1 } } }")),
                dynamicTest("(CU CF) conj (R)", () -> doParse(preinfix, "(CU CF) conj (R)", "0..16 sequence{ 0..16 conjugation{ 0..7 grouping{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 13..16 grouping{ 14..15 move{ 0:4:1 } } } }")),
                dynamicTest("(CU CF) conj (R B)", () -> doParse(preinfix, "(CU CF) conj (R B)", "0..18 sequence{ 0..18 conjugation{ 0..7 grouping{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 13..18 grouping{ 14..15 move{ 0:4:1 } 16..17 move{ 2:1:-1 } } } }")),
                dynamicTest("CU comm R", () -> doParse(preinfix, "CU comm R", "0..9 sequence{ 0..9 commutation{ 0..2 move{ 1:7:1 } 8..9 move{ 0:4:1 } } }")),
                dynamicTest("(CU CF) comm (R B)", () -> doParse(preinfix, "(CU CF) comm (R B)", "0..18 sequence{ 0..18 commutation{ 0..7 grouping{ 1..3 move{ 1:7:1 } 4..6 move{ 2:7:1 } } 13..18 grouping{ 14..15 move{ 0:4:1 } 16..17 move{ 2:1:-1 } } } }")),
                dynamicTest("*(R)", () -> doParse(preinfix, "*(R)", "0..4 sequence{ 0..4 reflection{ 1..4 grouping{ 2..3 move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(preinfix, "*('R U F)", "0..9 sequence{ 0..9 reflection{ 1..9 grouping{ 2..4 inversion{ 3..4 move{ 0:4:1 } } 5..6 move{ 1:4:1 } 7..8 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParsePostinfixNotation() {
        return Arrays.asList(
                dynamicTest("(R) times 2", () -> doParse(postinfix, "(R) times 2", "0..11 sequence{ 0..11 repetition{ 2, 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("R times 3", () -> doParse(postinfix, "R times 3", "0..9 sequence{ 0..9 repetition{ 3, 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R U F) times 3", () -> doParse(postinfix, "(R U F) times 3", "0..15 sequence{ 0..15 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)' times 3", () -> doParse(postinfix, "(R U F)' times 3", "0..16 sequence{ 0..16 repetition{ 3, 0..8 inversion{ 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F) times 3'", () -> doParse(postinfix, "(R U F) times 3'", "0..16 sequence{ 0..16 inversion{ 0..15 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F) times 3' times 4", () -> doParse(postinfix, "(R U F) times 3' times 4", "0..24 sequence{ 0..24 repetition{ 4, 0..16 inversion{ 0..15 repetition{ 3, 0..7 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 1:4:1 } 5..6 move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(postinfix, "(R)'", "0..4 sequence{ 0..4 inversion{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(postinfix, "(R F)'", "0..6 sequence{ 0..6 inversion{ 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(postinfix, "(R- U F)- (R' U F)'", "0..19 sequence{ 0..9 inversion{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } 10..19 inversion{ 10..18 grouping{ 11..13 inversion{ 11..12 move{ 0:4:1 } } 14..15 move{ 1:4:1 } 16..17 move{ 2:4:1 } } } }")),
                dynamicTest("R conj CU", () -> doParse(postinfix, "R conj CU", "0..9 sequence{ 0..9 conjugation{ 7..9 move{ 1:7:1 } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R B) conj (CU CF)", () -> doParse(postinfix, "(R B) conj (CU CF)", "0..18 sequence{ 0..18 conjugation{ 11..18 grouping{ 12..14 move{ 1:7:1 } 15..17 move{ 2:7:1 } } 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:1:-1 } } } }")),
                dynamicTest("R comm CU", () -> doParse(postinfix, "R comm CU", "0..9 sequence{ 0..9 commutation{ 7..9 move{ 1:7:1 } 0..1 move{ 0:4:1 } } }")),
                dynamicTest("(R B) comm (CU CF)", () -> doParse(postinfix, "(R B) comm (CU CF)", "0..18 sequence{ 0..18 commutation{ 11..18 grouping{ 12..14 move{ 1:7:1 } 15..17 move{ 2:7:1 } } 0..5 grouping{ 1..2 move{ 0:4:1 } 3..4 move{ 2:1:-1 } } } }")),
                dynamicTest("(R)*", () -> doParse(postinfix, "(R)*", "0..4 sequence{ 0..4 reflection{ 0..3 grouping{ 1..2 move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(postinfix, "(R' U F)*", "0..9 sequence{ 0..9 reflection{ 0..8 grouping{ 1..3 inversion{ 1..2 move{ 0:4:1 } } 4..5 move{ 1:4:1 } 6..7 move{ 2:4:1 } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParsePermutation() {
        return Arrays.asList(
                dynamicTest("defaultNotation (+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)",
                        () -> doParse(defaultNotatioon, "(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "0..61 sequence{ 0..18 permutationcycle{ Corner sign:2 0:0 2:2 3:0 1:2 } 19..33 permutationcycle{ Edge sign:1 0:0 4:1 2:0 1:1 } 34..38 permutationcycle{ Side sign:3 0:0 } 39..44 permutationcycle{ Side sign:0 0:0 5:0 } 45..52 permutationcycle{ Side sign:2 1:0 4:0 } 53..61 permutationcycle{ Side sign:2 2:0 3:3 } }")),

                dynamicTest("precircumfixNotation R as permutation",
                        () -> doParse(precircumfix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (+r)",
                                "0..36 sequence{ 0..17 permutationcycle{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 permutationcycle{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 permutationcycle{ Side sign:3 0:0 } }")),
                dynamicTest("prefixNotation R as permutation", () -> doParse(prefix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) +(r)",
                        "0..36 sequence{ 0..17 permutationcycle{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 permutationcycle{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 permutationcycle{ Side sign:3 0:0 } }")),
                dynamicTest("suffixNotation R as permutation", () -> doParse(suffix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (r)+",
                        "0..36 sequence{ 0..19 permutationcycle{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..33 permutationcycle{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 permutationcycle{ Side sign:3 0:0 } }")),
                dynamicTest("postcircumfixNotation R as permutation", () -> doParse(postcircumfix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (r+)",
                        "0..36 sequence{ 0..17 permutationcycle{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 permutationcycle{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 permutationcycle{ Side sign:3 0:0 } }")),


                dynamicTest("defaultNotation R /*comment*/ U F", () -> doParse(defaultNotatioon, "R /*comment*/ U F", "0..17 sequence{ 0..1 move{ 0:4:1 } 14..15 move{ 1:4:1 } 16..17 move{ 2:4:1 } }"))
        );

    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseMacros() {
        return Arrays.asList(
                dynamicTest("CRU", () -> doParse(notationWithMacros, "CRU", "0..3 sequence{ 0..3 macro{ 0..3 sequence{ 0..3 move{ 0:7:1 } 0..3 move{ 1:7:1 } } } }")),
                dynamicTest("R CRU", () -> doParse(notationWithMacros, "R CRU", "0..5 sequence{ 0..1 move{ 0:4:1 } 2..5 macro{ 2..5 sequence{ 2..5 move{ 0:7:1 } 2..5 move{ 1:7:1 } } } }")),
                dynamicTest("CRU2", () -> doParse(notationWithMacros, "CRU2", "0..4 sequence{ 0..4 repetition{ 2, 0..3 macro{ 0..3 sequence{ 0..3 move{ 0:7:1 } 0..3 move{ 1:7:1 } } } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseMixedCubes() {
        return Arrays.asList(
                dynamicTest("4:MB2", () -> doParse(defaultNotatioon4, "MB", "0..2 sequence{ 0..2 move{ 2:2:-1 } }")),
                dynamicTest("4:(MB2 MR2)2 U- (MB2 MR2)2 U", () -> doParse(defaultNotatioon4, "(MB2 MR2)2 U- (MB2 MR2)2 U", "0..26 sequence{ 0..10 repetition{ 2, 0..9 grouping{ 1..4 move{ 2:2:-2 } 5..8 move{ 0:4:2 } } } 11..13 inversion{ 11..12 move{ 1:8:1 } } 14..24 repetition{ 2, 14..23 grouping{ 15..18 move{ 2:2:-2 } 19..22 move{ 0:4:2 } } } 25..26 move{ 1:8:1 } }")),
                dynamicTest("4:U2 WR MD2 WR- U- WR MD2 WR- U-", () -> doParse(defaultNotatioon4, "U2 WR MD2 WR- U- WR MD2 WR- U-", "0..30 sequence{ 0..2 move{ 1:8:2 } 3..5 move{ 0:6:1 } 6..9 move{ 1:2:-2 } 10..13 inversion{ 10..12 move{ 0:6:1 } } 14..16 inversion{ 14..15 move{ 1:8:1 } } 17..19 move{ 0:6:1 } 20..23 move{ 1:2:-2 } 24..27 inversion{ 24..26 move{ 0:6:1 } } 28..30 inversion{ 28..29 move{ 1:8:1 } } }"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseAppliedPermutation() {
        final DefaultNotation notation7x7 = new DefaultNotation(7);
        final Cube7 cube7 = new Cube7();
        cube7.transform(0, 32 + 64, 1);

        final DefaultNotation notation6x6 = new DefaultNotation(6);
        final Cube6 cube6 = new Cube6();
        cube6.transform(0, 16+32, 1);

        final DefaultNotation notation5x5 = new DefaultNotation(5);
        final ProfessorCube cube5 = new ProfessorCube();
        cube5.transform(0, 8+16, 1);

        final DefaultNotation notation4x4 = new DefaultNotation(4);
        final RevengeCube cube4 = new RevengeCube();
        cube4.transform(0, 4+8, 1);

        final DefaultNotation notation3x3 = new DefaultNotation(3);
        final RubiksCube cube3 = new RubiksCube();
        cube3.transform(0, 2+4, 1);

        final DefaultNotation notation2x2 = new DefaultNotation(2);
        final PocketCube cube2 = new PocketCube();
        cube2.transform(0, 2, 1);

        return Arrays.asList(
                dynamicTest("notation2x2 R", () -> doParsePermutation(notation2x2, cube2)),
                dynamicTest("notation3x3 TR", () -> doParsePermutation(notation3x3, cube3)),
                dynamicTest("notation4x4 TR", () -> doParsePermutation(notation4x4, cube4)),
                dynamicTest("notation5x5 TR", () -> doParsePermutation(notation5x5, cube5)),
                dynamicTest("notation6x6 TR", () -> doParsePermutation(notation6x6, cube6)),
                dynamicTest("notation7x7 TR", () -> doParsePermutation(notation7x7, cube7))

        );

    }


    public void doParsePermutation(@Nonnull Notation notation, @Nonnull Cube cube) throws Exception {
        final String expected = Cubes.toPermutationString(cube, notation);
        System.out.println("expected: " + expected);

        final ScriptParser parser = new ScriptParser(notation);
        final Node script = parser.parse(expected);
        cube.reset();
        script.applyTo(cube, false);
        final String actual = Cubes.toPermutationString(cube, notation);

        System.out.println("actual  : " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param notation
     * @param script   the input script
     * @throws java.lang.Exception on failure
     */
    public void doParse(@Nonnull Notation notation, @Nonnull String script, String expected) throws Exception {
        ScriptParser instance = new ScriptParser(notation);
        Node node = instance.parse(script);
        String actual = dump(node);
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" + notation.getLayerCount() + ":" + htmlEscape(script) + "</p>");
            System.out.println("      <p class=\"expected\">" +
                    htmlEscape(actual) + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
            // System.out.println("  actual: " + actual);
            System.out.println(" DynamicTest.dynamicTest(\"" + notation.getName() + "\", () -> doParse(" + notation.getName() + ", \"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
            System.out.println("expected: " + expected);
            System.out.println("actual:   " + actual);
        }
        assertEquals(expected, actual);

    }

    @Nonnull
    private String dump(Node node) {
        StringBuilder buf = new StringBuilder();
        dump(node, buf);
        return buf.toString();
    }

    private void dump(Node node, @Nonnull StringBuilder b) {
        if (node instanceof PermutationItemNode) {
            PermutationItemNode m = (PermutationItemNode) node;
            b.append(m.getLocation())
                    .append(':')
                    .append(m.getOrientation());
            return;
        }
        b.append(node.getStartPosition());
        b.append("..");
        b.append(node.getEndPosition());
        b.append(" ");
        b.append(node.getClass().getSimpleName().substring(0, node.getClass().getSimpleName().lastIndexOf("Node")).toLowerCase());
        b.append("{");
        if (node instanceof MoveNode) {
            MoveNode m = (MoveNode) node;
            b.append(' ')
                    .append(m.getAxis())
                    .append(":").append(m.getLayerMask())
                    .append(":").append(m.getAngle());
        } else if (node instanceof RepetitionNode) {
            RepetitionNode m = (RepetitionNode) node;
            b.append(' ')
                    .append(m.getRepeatCount())
                    .append(',');
        } else if (node instanceof PermutationCycleNode) {
            PermutationCycleNode m = (PermutationCycleNode) node;
            b.append(' ');
            switch (m.getType()) {
                case PermutationCycleNode.SIDE_PERMUTATION:
                    b.append("Side");
                    break;
                case PermutationCycleNode.EDGE_PERMUTATION:
                    b.append("Edge");
                    break;
                case PermutationCycleNode.CORNER_PERMUTATION:
                    b.append("Corner");
                    break;
            }
            b.append(" sign:")
                    .append(m.getSign());
        }

        for (Node n : node.getChildren()) {
            b.append(' ');
            dump(n, b);
        }
        b.append(' ');
        b.append("}");
    }

    @Nonnull
    private String htmlEscape(@Nonnull String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testParseFailures() {
        return Arrays.asList(
                dynamicTest("defaultNotation knurps", () -> doFailure(defaultNotatioon, "knurps", "Statement: Keyword or Number expected. Found \"knurps\".")),
                dynamicTest("defaultNotation B R (X3U MR- MU- · MR ML · MD ML- U-) R- B-", () -> doFailure(defaultNotatioon, "B R (X3U MR- MU- · MR ML · MD ML- U-) R- B-", "Permutation: PermutationItem expected. Found \"X\".")),
                dynamicTest("preinfixNotation <CU CF> conj (R)", () -> doFailure(preinfix, "<CU CF> conj (R)", "Preinfix: Operand expected. Found \"<\"."))
        );
    }

    public void doFailure(Notation notation, String script, String expected) throws Exception {
        ScriptParser instance = new ScriptParser(notation);
        try {
            instance.parse(script);
            fail("should fail to parse " + script);
        } catch (ParseException e) {
            assertEquals(expected, e.getMessage());
        }
    }
}