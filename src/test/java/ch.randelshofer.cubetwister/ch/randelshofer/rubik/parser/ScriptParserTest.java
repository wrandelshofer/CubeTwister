package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.Cube6;
import ch.randelshofer.rubik.Cube7;
import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.PocketCube;
import ch.randelshofer.rubik.ProfessorCube;
import ch.randelshofer.rubik.RevengeCube;
import ch.randelshofer.rubik.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ScriptParserTest {
    private static boolean html = false;

    DefaultNotation defaultNotatioon = new DefaultNotation();
    DefaultNotation precircumfix = new DefaultNotation();
    DefaultNotation preinfix = new DefaultNotation();
    DefaultNotation postinfix = new DefaultNotation();
    DefaultNotation prefix = new DefaultNotation();
    DefaultNotation circumfix = new DefaultNotation();
    DefaultNotation postcircumfix = new DefaultNotation();
    DefaultNotation suffix = new DefaultNotation();
    DefaultNotation mixed = new DefaultNotation();
    DefaultNotation mixedB = new DefaultNotation();
    DefaultNotation notationWithMacros = new DefaultNotation();

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
                dynamicTest("No precedence: 3 * R", () -> doParse(mixed, "3 * R", "0..5 Sequence{ 0..5 Repetition{ 3, 4..5 Move{ 0:4:1 } } }")),
                dynamicTest("No precedence: 3 * «R»", () -> doParse(mixed, "3 * «R»", "0..7 Sequence{ 0..7 Repetition{ 3, 4..7 Reflection{ 5..6 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * R rot CU", () -> doParse(mixed, "3 * R rot CU", "0..12 Sequence{ 0..12 Rotation{ 10..12 Move{ 1:7:1 } 0..5 Repetition{ 3, 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 4 * 3 * R rot CU", () -> doParse(mixed, "4 * 3 * R rot CU", "0..16 Sequence{ 0..16 Rotation{ 14..16 Move{ 1:7:1 } 0..9 Repetition{ 4, 4..9 Repetition{ 3, 8..9 Move{ 0:4:1 } } } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R'", () -> doParse(mixed, "<CU>R'", "0..6 Sequence{ 0..6 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..6 Inversion{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Preinfix: 3 * R'", () -> doParse(mixed, "3 * R'", "0..6 Sequence{ 0..6 Repetition{ 3, 4..6 Inversion{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("Prefix precedes Preinfix: 3 * <CU>R", () -> doParse(mixed, "3 * <CU>R", "0..9 Sequence{ 0..9 Repetition{ 3, 4..9 Conjugation{ 5..7 Sequence{ 5..7 Move{ 1:7:1 } } 8..9 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * R rot CU", () -> doParse(mixed, "3 * R rot CU", "0..12 Sequence{ 0..12 Rotation{ 10..12 Move{ 1:7:1 } 0..5 Repetition{ 3, 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: 3 * CU comm R", () -> doParse(mixed, "3 * CU comm R", "0..13 Sequence{ 0..13 Commutation{ 0..6 Repetition{ 3, 4..6 Move{ 1:7:1 } } 12..13 Move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: (3 * CU) comm R", () -> doParse(mixed, "(3 * CU) comm R", "0..15 Sequence{ 0..15 Commutation{ 0..8 Grouping{ 1..7 Repetition{ 3, 5..7 Move{ 1:7:1 } } } 14..15 Move{ 0:4:1 } } }"))
        );
    }

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
                dynamicTest("No precedence: R 3", () -> doParse(mixedB, "R 3", "0..3 Sequence{ 0..3 Repetition{ 3, 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("No precedence: «R» 3", () -> doParse(mixedB, "«R» 3", "0..5 Sequence{ 0..5 Repetition{ 3, 0..3 Reflection{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Postinfix: R rot CU 3", () -> doParse(mixedB, "R rot CU 3", "0..10 Sequence{ 0..10 Rotation{ 6..10 Repetition{ 3, 6..8 Move{ 1:7:1 } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Postinfix: R rot CU 3 4", () -> doParse(mixedB, "R rot CU 3 4", "0..12 Sequence{ 0..12 Rotation{ 6..12 Repetition{ 4, 6..10 Repetition{ 3, 6..8 Move{ 1:7:1 } } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R'", () -> doParse(mixedB, "<CU>R'", "0..6 Sequence{ 0..6 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..6 Inversion{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: R' 3", () -> doParse(mixedB, "R' 3", "0..4 Sequence{ 0..4 Repetition{ 3, 0..2 Inversion{ 0..1 Move{ 0:4:1 } } } }")),
                dynamicTest("Suffix precedes Prefix: <CU>R 3", () -> doParse(mixedB, "<CU>R 3", "0..7 Sequence{ 0..7 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..7 Repetition{ 3, 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: R rot CU 3", () -> doParse(mixedB, "R rot CU 3", "0..10 Sequence{ 0..10 Rotation{ 6..10 Repetition{ 3, 6..8 Move{ 1:7:1 } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("Suffix precedes Preinfix: CU comm R 3", () -> doParse(mixedB, "CU comm R 3", "0..11 Sequence{ 0..11 Commutation{ 0..2 Move{ 1:7:1 } 8..11 Repetition{ 3, 8..9 Move{ 0:4:1 } } } }")),
                dynamicTest("No precedence: CU 3 comm R", () -> doParse(mixedB, "CU 3 comm R", "0..11 Sequence{ 0..11 Commutation{ 0..4 Repetition{ 3, 0..2 Move{ 1:7:1 } } 10..11 Move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: (CU 3) comm R", () -> doParse(mixedB, "(CU 3) comm R", "0..13 Sequence{ 0..13 Commutation{ 0..6 Grouping{ 1..5 Repetition{ 3, 1..3 Move{ 1:7:1 } } } 12..13 Move{ 0:4:1 } } }")),
                dynamicTest("Explicit precedence: CU comm (R 3)", () -> doParse(mixedB, "CU comm (R 3)", "0..13 Sequence{ 0..13 Commutation{ 0..2 Move{ 1:7:1 } 8..13 Grouping{ 9..12 Repetition{ 3, 9..10 Move{ 0:4:1 } } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParseDefaultNotation() {
        return Arrays.asList(
                dynamicTest("<empty>", () -> doParse(defaultNotatioon, "", "0..0 Sequence{ }")),
                dynamicTest(".", () -> doParse(defaultNotatioon, ".", "0..1 Sequence{ 0..1 NOP{ } }")),
                dynamicTest("R", () -> doParse(defaultNotatioon, "R", "0..1 Sequence{ 0..1 Move{ 0:4:1 } }")),
                dynamicTest("U", () -> doParse(defaultNotatioon, "U", "0..1 Sequence{ 0..1 Move{ 1:4:1 } }")),
                dynamicTest("F", () -> doParse(defaultNotatioon, "F", "0..1 Sequence{ 0..1 Move{ 2:4:1 } }")),
                dynamicTest("L", () -> doParse(defaultNotatioon, "L", "0..1 Sequence{ 0..1 Move{ 0:1:-1 } }")),
                dynamicTest("D", () -> doParse(defaultNotatioon, "D", "0..1 Sequence{ 0..1 Move{ 1:1:-1 } }")),
                dynamicTest("B", () -> doParse(defaultNotatioon, "B", "0..1 Sequence{ 0..1 Move{ 2:1:-1 } }")),
                dynamicTest("R'", () -> doParse(defaultNotatioon, "R'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("U'", () -> doParse(defaultNotatioon, "U'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 1:4:1 } } }")),
                dynamicTest("F'", () -> doParse(defaultNotatioon, "F'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 2:4:1 } } }")),
                dynamicTest("L'", () -> doParse(defaultNotatioon, "L'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 0:1:-1 } } }")),
                dynamicTest("D'", () -> doParse(defaultNotatioon, "D'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 1:1:-1 } } }")),
                dynamicTest("B'", () -> doParse(defaultNotatioon, "B'", "0..2 Sequence{ 0..2 Inversion{ 0..1 Move{ 2:1:-1 } } }")),
                dynamicTest("R2", () -> doParse(defaultNotatioon, "R2", "0..2 Sequence{ 0..2 Move{ 0:4:2 } }")),
                dynamicTest("U2", () -> doParse(defaultNotatioon, "U2", "0..2 Sequence{ 0..2 Move{ 1:4:2 } }")),
                dynamicTest("F2", () -> doParse(defaultNotatioon, "F2", "0..2 Sequence{ 0..2 Move{ 2:4:2 } }")),
                dynamicTest("L2", () -> doParse(defaultNotatioon, "L2", "0..2 Sequence{ 0..2 Move{ 0:1:-2 } }")),
                dynamicTest("D2", () -> doParse(defaultNotatioon, "D2", "0..2 Sequence{ 0..2 Move{ 1:1:-2 } }")),
                dynamicTest("B2", () -> doParse(defaultNotatioon, "B2", "0..2 Sequence{ 0..2 Move{ 2:1:-2 } }")),
                dynamicTest("MR", () -> doParse(defaultNotatioon, "MR", "0..2 Sequence{ 0..2 Move{ 0:2:1 } }")),
                dynamicTest("MU", () -> doParse(defaultNotatioon, "MU", "0..2 Sequence{ 0..2 Move{ 1:2:1 } }")),
                dynamicTest("MF", () -> doParse(defaultNotatioon, "MF", "0..2 Sequence{ 0..2 Move{ 2:2:1 } }")),
                dynamicTest("ML", () -> doParse(defaultNotatioon, "ML", "0..2 Sequence{ 0..2 Move{ 0:2:-1 } }")),
                dynamicTest("MD", () -> doParse(defaultNotatioon, "MD", "0..2 Sequence{ 0..2 Move{ 1:2:-1 } }")),
                dynamicTest("MB", () -> doParse(defaultNotatioon, "MB", "0..2 Sequence{ 0..2 Move{ 2:2:-1 } }")),
                dynamicTest("MR'", () -> doParse(defaultNotatioon, "MR'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:2:1 } } }")),
                dynamicTest("MU'", () -> doParse(defaultNotatioon, "MU'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:2:1 } } }")),
                dynamicTest("MF'", () -> doParse(defaultNotatioon, "MF'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:2:1 } } }")),
                dynamicTest("ML'", () -> doParse(defaultNotatioon, "ML'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:2:-1 } } }")),
                dynamicTest("MD'", () -> doParse(defaultNotatioon, "MD'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:2:-1 } } }")),
                dynamicTest("MB'", () -> doParse(defaultNotatioon, "MB'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:2:-1 } } }")),
                dynamicTest("MR2", () -> doParse(defaultNotatioon, "MR2", "0..3 Sequence{ 0..3 Move{ 0:2:2 } }")),
                dynamicTest("MU2", () -> doParse(defaultNotatioon, "MU2", "0..3 Sequence{ 0..3 Move{ 1:2:2 } }")),
                dynamicTest("MF2", () -> doParse(defaultNotatioon, "MF2", "0..3 Sequence{ 0..3 Move{ 2:2:2 } }")),
                dynamicTest("ML2", () -> doParse(defaultNotatioon, "ML2", "0..3 Sequence{ 0..3 Move{ 0:2:-2 } }")),
                dynamicTest("MD2", () -> doParse(defaultNotatioon, "MD2", "0..3 Sequence{ 0..3 Move{ 1:2:-2 } }")),
                dynamicTest("MB2", () -> doParse(defaultNotatioon, "MB2", "0..3 Sequence{ 0..3 Move{ 2:2:-2 } }")),
                dynamicTest("TR", () -> doParse(defaultNotatioon, "TR", "0..2 Sequence{ 0..2 Move{ 0:6:1 } }")),
                dynamicTest("TU", () -> doParse(defaultNotatioon, "TU", "0..2 Sequence{ 0..2 Move{ 1:6:1 } }")),
                dynamicTest("TF", () -> doParse(defaultNotatioon, "TF", "0..2 Sequence{ 0..2 Move{ 2:6:1 } }")),
                dynamicTest("TL", () -> doParse(defaultNotatioon, "TL", "0..2 Sequence{ 0..2 Move{ 0:3:-1 } }")),
                dynamicTest("TD", () -> doParse(defaultNotatioon, "TD", "0..2 Sequence{ 0..2 Move{ 1:3:-1 } }")),
                dynamicTest("TB", () -> doParse(defaultNotatioon, "TB", "0..2 Sequence{ 0..2 Move{ 2:3:-1 } }")),
                dynamicTest("TR'", () -> doParse(defaultNotatioon, "TR'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:6:1 } } }")),
                dynamicTest("TU'", () -> doParse(defaultNotatioon, "TU'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:6:1 } } }")),
                dynamicTest("TF'", () -> doParse(defaultNotatioon, "TF'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:6:1 } } }")),
                dynamicTest("TL'", () -> doParse(defaultNotatioon, "TL'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:3:-1 } } }")),
                dynamicTest("TD'", () -> doParse(defaultNotatioon, "TD'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:3:-1 } } }")),
                dynamicTest("TB'", () -> doParse(defaultNotatioon, "TB'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:3:-1 } } }")),
                dynamicTest("TR2", () -> doParse(defaultNotatioon, "TR2", "0..3 Sequence{ 0..3 Move{ 0:6:2 } }")),
                dynamicTest("TU2", () -> doParse(defaultNotatioon, "TU2", "0..3 Sequence{ 0..3 Move{ 1:6:2 } }")),
                dynamicTest("TF2", () -> doParse(defaultNotatioon, "TF2", "0..3 Sequence{ 0..3 Move{ 2:6:2 } }")),
                dynamicTest("TL2", () -> doParse(defaultNotatioon, "TL2", "0..3 Sequence{ 0..3 Move{ 0:3:-2 } }")),
                dynamicTest("TD2", () -> doParse(defaultNotatioon, "TD2", "0..3 Sequence{ 0..3 Move{ 1:3:-2 } }")),
                dynamicTest("TB2", () -> doParse(defaultNotatioon, "TB2", "0..3 Sequence{ 0..3 Move{ 2:3:-2 } }")),
                dynamicTest("CR", () -> doParse(defaultNotatioon, "CR", "0..2 Sequence{ 0..2 Move{ 0:7:1 } }")),
                dynamicTest("CU", () -> doParse(defaultNotatioon, "CU", "0..2 Sequence{ 0..2 Move{ 1:7:1 } }")),
                dynamicTest("CF", () -> doParse(defaultNotatioon, "CF", "0..2 Sequence{ 0..2 Move{ 2:7:1 } }")),
                dynamicTest("CL", () -> doParse(defaultNotatioon, "CL", "0..2 Sequence{ 0..2 Move{ 0:7:-1 } }")),
                dynamicTest("CD", () -> doParse(defaultNotatioon, "CD", "0..2 Sequence{ 0..2 Move{ 1:7:-1 } }")),
                dynamicTest("CB", () -> doParse(defaultNotatioon, "CB", "0..2 Sequence{ 0..2 Move{ 2:7:-1 } }")),
                dynamicTest("CR'", () -> doParse(defaultNotatioon, "CR'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:7:1 } } }")),
                dynamicTest("CU'", () -> doParse(defaultNotatioon, "CU'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:7:1 } } }")),
                dynamicTest("CF'", () -> doParse(defaultNotatioon, "CF'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:7:1 } } }")),
                dynamicTest("CL'", () -> doParse(defaultNotatioon, "CL'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:7:-1 } } }")),
                dynamicTest("CD'", () -> doParse(defaultNotatioon, "CD'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:7:-1 } } }")),
                dynamicTest("CB'", () -> doParse(defaultNotatioon, "CB'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:7:-1 } } }")),
                dynamicTest("CR2", () -> doParse(defaultNotatioon, "CR2", "0..3 Sequence{ 0..3 Move{ 0:7:2 } }")),
                dynamicTest("CU2", () -> doParse(defaultNotatioon, "CU2", "0..3 Sequence{ 0..3 Move{ 1:7:2 } }")),
                dynamicTest("CF2", () -> doParse(defaultNotatioon, "CF2", "0..3 Sequence{ 0..3 Move{ 2:7:2 } }")),
                dynamicTest("CL2", () -> doParse(defaultNotatioon, "CL2", "0..3 Sequence{ 0..3 Move{ 0:7:-2 } }")),
                dynamicTest("CD2", () -> doParse(defaultNotatioon, "CD2", "0..3 Sequence{ 0..3 Move{ 1:7:-2 } }")),
                dynamicTest("CB2", () -> doParse(defaultNotatioon, "CB2", "0..3 Sequence{ 0..3 Move{ 2:7:-2 } }")),
                dynamicTest("SR", () -> doParse(defaultNotatioon, "SR", "0..2 Sequence{ 0..2 Move{ 0:5:1 } }")),
                dynamicTest("SU", () -> doParse(defaultNotatioon, "SU", "0..2 Sequence{ 0..2 Move{ 1:5:1 } }")),
                dynamicTest("SF", () -> doParse(defaultNotatioon, "SF", "0..2 Sequence{ 0..2 Move{ 2:5:1 } }")),
                dynamicTest("SL", () -> doParse(defaultNotatioon, "SL", "0..2 Sequence{ 0..2 Move{ 0:5:-1 } }")),
                dynamicTest("SD", () -> doParse(defaultNotatioon, "SD", "0..2 Sequence{ 0..2 Move{ 1:5:-1 } }")),
                dynamicTest("SB", () -> doParse(defaultNotatioon, "SB", "0..2 Sequence{ 0..2 Move{ 2:5:-1 } }")),
                dynamicTest("SR'", () -> doParse(defaultNotatioon, "SR'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:5:1 } } }")),
                dynamicTest("SU'", () -> doParse(defaultNotatioon, "SU'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:5:1 } } }")),
                dynamicTest("SF'", () -> doParse(defaultNotatioon, "SF'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:5:1 } } }")),
                dynamicTest("SL'", () -> doParse(defaultNotatioon, "SL'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 0:5:-1 } } }")),
                dynamicTest("SD'", () -> doParse(defaultNotatioon, "SD'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 1:5:-1 } } }")),
                dynamicTest("SB'", () -> doParse(defaultNotatioon, "SB'", "0..3 Sequence{ 0..3 Inversion{ 0..2 Move{ 2:5:-1 } } }")),
                dynamicTest("SR2", () -> doParse(defaultNotatioon, "SR2", "0..3 Sequence{ 0..3 Move{ 0:5:2 } }")),
                dynamicTest("SU2", () -> doParse(defaultNotatioon, "SU2", "0..3 Sequence{ 0..3 Move{ 1:5:2 } }")),
                dynamicTest("SF2", () -> doParse(defaultNotatioon, "SF2", "0..3 Sequence{ 0..3 Move{ 2:5:2 } }")),
                dynamicTest("SL2", () -> doParse(defaultNotatioon, "SL2", "0..3 Sequence{ 0..3 Move{ 0:5:-2 } }")),
                dynamicTest("SD2", () -> doParse(defaultNotatioon, "SD2", "0..3 Sequence{ 0..3 Move{ 1:5:-2 } }")),
                dynamicTest("SB2", () -> doParse(defaultNotatioon, "SB2", "0..3 Sequence{ 0..3 Move{ 2:5:-2 } }")),
                dynamicTest("(R U F)", () -> doParse(defaultNotatioon, "(R U F)", "0..7 Sequence{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } }")),
                dynamicTest("(R U F)'", () -> doParse(defaultNotatioon, "(R U F)'", "0..8 Sequence{ 0..8 Inversion{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } }")),
                dynamicTest("(R)2", () -> doParse(defaultNotatioon, "(R)2", "0..4 Sequence{ 0..4 Repetition{ 2, 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(defaultNotatioon, "R3", "0..2 Sequence{ 0..2 Repetition{ 3, 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(defaultNotatioon, "(R U F)3", "0..8 Sequence{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(defaultNotatioon, "(R U F)'3", "0..9 Sequence{ 0..9 Repetition{ 3, 0..8 Inversion{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(defaultNotatioon, "(R U F)3'", "0..9 Sequence{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(defaultNotatioon, "(R U F)3''", "0..10 Sequence{ 0..10 Inversion{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(defaultNotatioon, "(R U F)3'4", "0..10 Sequence{ 0..10 Repetition{ 4, 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(defaultNotatioon, "(R)'", "0..4 Sequence{ 0..4 Inversion{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(defaultNotatioon, "(R F)'", "0..6 Sequence{ 0..6 Inversion{ 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(defaultNotatioon, "(R- U F)- (R' U F)'",
                        "0..19 Sequence{ 0..9 Inversion{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } 10..19 Inversion{ 10..18 Grouping{ 11..13 Inversion{ 11..12 Move{ 0:4:1 } } 14..15 Move{ 1:4:1 } 16..17 Move{ 2:4:1 } } } }")),
                dynamicTest("<CU>R", () -> doParse(defaultNotatioon, "<CU>R", "0..5 Sequence{ 0..5 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Move{ 0:4:1 } } }")),
                dynamicTest("<CU CF>(R)", () -> doParse(defaultNotatioon, "<CU CF>(R)", "0..10 Sequence{ 0..10 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..10 Grouping{ 8..9 Move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>'(R)", () -> doParse(defaultNotatioon, "<CU CF>'(R)", "0..11 Sequence{ 0..11 Rotation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 8..11 Grouping{ 9..10 Move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>(R B)", () -> doParse(defaultNotatioon, "<CU CF>(R B)", "0..12 Sequence{ 0..12 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..12 Grouping{ 8..9 Move{ 0:4:1 } 10..11 Move{ 2:1:-1 } } } }")),
                dynamicTest("<R>U", () -> doParse(defaultNotatioon, "<R>U", "0..4 Sequence{ 0..4 Conjugation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Move{ 1:4:1 } } }")),
                dynamicTest("[CU,R]", () -> doParse(defaultNotatioon, "[CU,R]", "0..6 Sequence{ 0..6 Commutation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Sequence{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R]", () -> doParse(defaultNotatioon, "[CU CF,R]", "0..9 Sequence{ 0..9 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..8 Sequence{ 7..8 Move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R B]", () -> doParse(defaultNotatioon, "[CU CF,R B]", "0..11 Sequence{ 0..11 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..10 Sequence{ 7..8 Move{ 0:4:1 } 9..10 Move{ 2:1:-1 } } } }")),
                dynamicTest("[R,U]", () -> doParse(defaultNotatioon, "[R,U]", "0..5 Sequence{ 0..5 Commutation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Sequence{ 3..4 Move{ 1:4:1 } } } }")),
                dynamicTest("R U R'", () -> doParse(defaultNotatioon, "R U R'", "0..6 Sequence{ 0..1 Move{ 0:4:1 } 2..3 Move{ 1:4:1 } 4..6 Inversion{ 4..5 Move{ 0:4:1 } } }")),
                dynamicTest("R U R' U'", () -> doParse(defaultNotatioon, "R U R' U'", "0..9 Sequence{ 0..1 Move{ 0:4:1 } 2..3 Move{ 1:4:1 } 4..6 Inversion{ 4..5 Move{ 0:4:1 } } 7..9 Inversion{ 7..8 Move{ 1:4:1 } } }")),
                dynamicTest("(R)*", () -> doParse(defaultNotatioon, "(R)*", "0..4 Sequence{ 0..4 Reflection{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(defaultNotatioon, "(R' U F)*", "0..9 Sequence{ 0..9 Reflection{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }")),
                dynamicTest("R . U · F", () -> doParse(defaultNotatioon, "R . U · F", "0..9 Sequence{ 0..1 Move{ 0:4:1 } 2..3 NOP{ } 4..5 Move{ 1:4:1 } 6..7 NOP{ } 8..9 Move{ 2:4:1 } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParsePrefixNotation() {
        return Arrays.asList(
                dynamicTest("2(R)", () -> doParse(prefix, "2(R)", "0..4 Sequence{ 0..4 Repetition{ 2, 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("3R", () -> doParse(prefix, "3R", "0..2 Sequence{ 0..2 Repetition{ 3, 1..2 Move{ 0:4:1 } } }")),
                dynamicTest("3(R U F)", () -> doParse(prefix, "3(R U F)", "0..8 Sequence{ 0..8 Repetition{ 3, 1..8 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }")),
                dynamicTest("3'(R U F)", () -> doParse(prefix, "3'(R U F)", "0..9 Sequence{ 0..9 Repetition{ 3, 1..9 Inversion{ 2..9 Grouping{ 3..4 Move{ 0:4:1 } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } } }")),
                dynamicTest("'3(R U F)", () -> doParse(prefix, "'3(R U F)", "0..9 Sequence{ 0..9 Inversion{ 1..9 Repetition{ 3, 2..9 Grouping{ 3..4 Move{ 0:4:1 } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } } }")),
                dynamicTest("''3(R U F)", () -> doParse(prefix, "''3(R U F)", "0..10 Sequence{ 0..10 Inversion{ 1..10 Inversion{ 2..10 Repetition{ 3, 3..10 Grouping{ 4..5 Move{ 0:4:1 } 6..7 Move{ 1:4:1 } 8..9 Move{ 2:4:1 } } } } } }")),
                dynamicTest("4'3(R U F)", () -> doParse(prefix, "4'3(R U F)", "0..10 Sequence{ 0..10 Repetition{ 4, 1..10 Inversion{ 2..10 Repetition{ 3, 3..10 Grouping{ 4..5 Move{ 0:4:1 } 6..7 Move{ 1:4:1 } 8..9 Move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(prefix, "'(R)", "0..4 Sequence{ 0..4 Inversion{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(prefix, "'(R F)", "0..6 Sequence{ 0..6 Inversion{ 1..6 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(prefix, "-(-R U F) '('R U F)", "0..19 Sequence{ 0..9 Inversion{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } 10..19 Inversion{ 11..19 Grouping{ 12..14 Inversion{ 13..14 Move{ 0:4:1 } } 15..16 Move{ 1:4:1 } 17..18 Move{ 2:4:1 } } } }")),
                dynamicTest("<CU>R", () -> doParse(prefix, "<CU>R", "0..5 Sequence{ 0..5 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Move{ 0:4:1 } } }")),
                dynamicTest("<CU CF>(R)", () -> doParse(prefix, "<CU CF>(R)", "0..10 Sequence{ 0..10 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..10 Grouping{ 8..9 Move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF>(R B)", () -> doParse(prefix, "<CU CF>(R B)", "0..12 Sequence{ 0..12 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..12 Grouping{ 8..9 Move{ 0:4:1 } 10..11 Move{ 2:1:-1 } } } }")),
                dynamicTest("<R>U", () -> doParse(prefix, "<R>U", "0..4 Sequence{ 0..4 Conjugation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Move{ 1:4:1 } } }")),
                dynamicTest("[CU]R", () -> doParse(prefix, "[CU]R", "0..5 Sequence{ 0..5 Commutation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Move{ 0:4:1 } } }")),
                dynamicTest("[CU CF]R", () -> doParse(prefix, "[CU CF]R", "0..8 Sequence{ 0..8 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..8 Move{ 0:4:1 } } }")),
                dynamicTest("[CU CF](R B)", () -> doParse(prefix, "[CU CF](R B)", "0..12 Sequence{ 0..12 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..12 Grouping{ 8..9 Move{ 0:4:1 } 10..11 Move{ 2:1:-1 } } } }")),
                dynamicTest("[R]U", () -> doParse(prefix, "[R]U", "0..4 Sequence{ 0..4 Commutation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Move{ 1:4:1 } } }")),
                dynamicTest("*(R)", () -> doParse(prefix, "*(R)", "0..4 Sequence{ 0..4 Reflection{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(prefix, "*('R U F)", "0..9 Sequence{ 0..9 Reflection{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParcePrecircumfixNotation() {
        return Arrays.asList(
                dynamicTest("2(R)", () -> doParse(precircumfix, "2(R)", "0..4 Sequence{ 0..4 Repetition{ 2, 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("3R", () -> doParse(precircumfix, "3R", "0..2 Sequence{ 0..2 Repetition{ 3, 1..2 Move{ 0:4:1 } } }")),
                dynamicTest("3(R U F)", () -> doParse(precircumfix, "3(R U F)", "0..8 Sequence{ 0..8 Repetition{ 3, 1..8 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }")),
                dynamicTest("3'(R U F)", () -> doParse(precircumfix, "3'(R U F)", "0..9 Sequence{ 0..9 Repetition{ 3, 1..9 Inversion{ 2..9 Grouping{ 3..4 Move{ 0:4:1 } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } } }")),
                dynamicTest("'3(R U F)", () -> doParse(precircumfix, "'3(R U F)", "0..9 Sequence{ 0..9 Inversion{ 1..9 Repetition{ 3, 2..9 Grouping{ 3..4 Move{ 0:4:1 } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } } }")),
                dynamicTest("''3(R U F)", () -> doParse(precircumfix, "''3(R U F)", "0..10 Sequence{ 0..10 Inversion{ 1..10 Inversion{ 2..10 Repetition{ 3, 3..10 Grouping{ 4..5 Move{ 0:4:1 } 6..7 Move{ 1:4:1 } 8..9 Move{ 2:4:1 } } } } } }")),
                dynamicTest("4'3(R U F)", () -> doParse(precircumfix, "4'3(R U F)", "0..10 Sequence{ 0..10 Repetition{ 4, 1..10 Inversion{ 2..10 Repetition{ 3, 3..10 Grouping{ 4..5 Move{ 0:4:1 } 6..7 Move{ 1:4:1 } 8..9 Move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(precircumfix, "'(R)", "0..4 Sequence{ 0..4 Inversion{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(precircumfix, "'(R F)", "0..6 Sequence{ 0..6 Inversion{ 1..6 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(precircumfix, "-(-R U F) '('R U F)", "0..19 Sequence{ 0..9 Inversion{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } 10..19 Inversion{ 11..19 Grouping{ 12..14 Inversion{ 13..14 Move{ 0:4:1 } } 15..16 Move{ 1:4:1 } 17..18 Move{ 2:4:1 } } } }")),
                dynamicTest("<CU,R>", () -> doParse(precircumfix, "<CU,R>", "0..6 Sequence{ 0..6 Conjugation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Sequence{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF,R>", () -> doParse(precircumfix, "<CU CF,R>", "0..9 Sequence{ 0..9 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..8 Sequence{ 7..8 Move{ 0:4:1 } } } }")),
                dynamicTest("<CU CF,R B>", () -> doParse(precircumfix, "<CU CF,R B>", "0..11 Sequence{ 0..11 Conjugation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..10 Sequence{ 7..8 Move{ 0:4:1 } 9..10 Move{ 2:1:-1 } } } }")),
                dynamicTest("<R,U>", () -> doParse(precircumfix, "<R,U>", "0..5 Sequence{ 0..5 Conjugation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Sequence{ 3..4 Move{ 1:4:1 } } } }")),
                dynamicTest("[CU,R]", () -> doParse(precircumfix, "[CU,R]", "0..6 Sequence{ 0..6 Commutation{ 1..3 Sequence{ 1..3 Move{ 1:7:1 } } 4..5 Sequence{ 4..5 Move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R]", () -> doParse(precircumfix, "[CU CF,R]", "0..9 Sequence{ 0..9 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..8 Sequence{ 7..8 Move{ 0:4:1 } } } }")),
                dynamicTest("[CU CF,R B]", () -> doParse(precircumfix, "[CU CF,R B]", "0..11 Sequence{ 0..11 Commutation{ 1..6 Sequence{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 7..10 Sequence{ 7..8 Move{ 0:4:1 } 9..10 Move{ 2:1:-1 } } } }")),
                dynamicTest("[R,U]", () -> doParse(precircumfix, "[R,U]", "0..5 Sequence{ 0..5 Commutation{ 1..2 Sequence{ 1..2 Move{ 0:4:1 } } 3..4 Sequence{ 3..4 Move{ 1:4:1 } } } }")),
                dynamicTest("*(R)", () -> doParse(precircumfix, "*(R)", "0..4 Sequence{ 0..4 Reflection{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(precircumfix, "*('R U F)", "0..9 Sequence{ 0..9 Reflection{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParseSuffixNotation() {
        return Arrays.asList(
                dynamicTest("(R)2", () -> doParse(suffix, "(R)2", "0..4 Sequence{ 0..4 Repetition{ 2, 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(suffix, "R3", "0..2 Sequence{ 0..2 Repetition{ 3, 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(suffix, "(R U F)3", "0..8 Sequence{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(suffix, "(R U F)'3", "0..9 Sequence{ 0..9 Repetition{ 3, 0..8 Inversion{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(suffix, "(R U F)3'", "0..9 Sequence{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(suffix, "(R U F)3''", "0..10 Sequence{ 0..10 Inversion{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(suffix, "(R U F)3'4", "0..10 Sequence{ 0..10 Repetition{ 4, 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(suffix, "(R)'", "0..4 Sequence{ 0..4 Inversion{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(suffix, "(R F)'", "0..6 Sequence{ 0..6 Inversion{ 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(suffix, "(R- U F)- (R' U F)'", "0..19 Sequence{ 0..9 Inversion{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } 10..19 Inversion{ 10..18 Grouping{ 11..13 Inversion{ 11..12 Move{ 0:4:1 } } 14..15 Move{ 1:4:1 } 16..17 Move{ 2:4:1 } } } }")),
                dynamicTest("R<CU>", () -> doParse(suffix, "R<CU>", "0..5 Sequence{ 0..5 Conjugation{ 2..4 Sequence{ 2..4 Move{ 1:7:1 } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R)<CU CF>", () -> doParse(suffix, "(R)<CU CF>", "0..10 Sequence{ 0..10 Conjugation{ 4..9 Sequence{ 4..6 Move{ 1:7:1 } 7..9 Move{ 2:7:1 } } 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R B)<CU CF>", () -> doParse(suffix, "(R B)<CU CF>", "0..12 Sequence{ 0..12 Conjugation{ 6..11 Sequence{ 6..8 Move{ 1:7:1 } 9..11 Move{ 2:7:1 } } 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:1:-1 } } } }")),
                dynamicTest("U<R>", () -> doParse(suffix, "U<R>", "0..4 Sequence{ 0..4 Conjugation{ 2..3 Sequence{ 2..3 Move{ 0:4:1 } } 0..1 Move{ 1:4:1 } } }")),
                dynamicTest("R[CU]", () -> doParse(suffix, "R[CU]", "0..5 Sequence{ 0..5 Commutation{ 2..4 Sequence{ 2..4 Move{ 1:7:1 } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("R[CU CF]", () -> doParse(suffix, "R[CU CF]", "0..8 Sequence{ 0..8 Commutation{ 2..7 Sequence{ 2..4 Move{ 1:7:1 } 5..7 Move{ 2:7:1 } } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R B)[CU CF]", () -> doParse(suffix, "(R B)[CU CF]", "0..12 Sequence{ 0..12 Commutation{ 6..11 Sequence{ 6..8 Move{ 1:7:1 } 9..11 Move{ 2:7:1 } } 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:1:-1 } } } }")),
                dynamicTest("U[R]", () -> doParse(suffix, "U[R]", "0..4 Sequence{ 0..4 Commutation{ 2..3 Sequence{ 2..3 Move{ 0:4:1 } } 0..1 Move{ 1:4:1 } } }")),
                dynamicTest("(R)*", () -> doParse(suffix, "(R)*", "0..4 Sequence{ 0..4 Reflection{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(suffix, "(R' U F)*", "0..9 Sequence{ 0..9 Reflection{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParsePostcircumfixNotation() {
        return Arrays.asList(
                dynamicTest("(R)2", () -> doParse(postcircumfix, "(R)2", "0..4 Sequence{ 0..4 Repetition{ 2, 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("R3", () -> doParse(postcircumfix, "R3", "0..2 Sequence{ 0..2 Repetition{ 3, 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R U F)3", () -> doParse(postcircumfix, "(R U F)3", "0..8 Sequence{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)'3", () -> doParse(postcircumfix, "(R U F)'3", "0..9 Sequence{ 0..9 Repetition{ 3, 0..8 Inversion{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3'", () -> doParse(postcircumfix, "(R U F)3'", "0..9 Sequence{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F)3''", () -> doParse(postcircumfix, "(R U F)3''", "0..10 Sequence{ 0..10 Inversion{ 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R U F)3'4", () -> doParse(postcircumfix, "(R U F)3'4", "0..10 Sequence{ 0..10 Repetition{ 4, 0..9 Inversion{ 0..8 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(postcircumfix, "(R)'", "0..4 Sequence{ 0..4 Inversion{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(postcircumfix, "(R F)'", "0..6 Sequence{ 0..6 Inversion{ 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(postcircumfix, "(R- U F)- (R' U F)'", "0..19 Sequence{ 0..9 Inversion{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } 10..19 Inversion{ 10..18 Grouping{ 11..13 Inversion{ 11..12 Move{ 0:4:1 } } 14..15 Move{ 1:4:1 } 16..17 Move{ 2:4:1 } } } }")),
                dynamicTest("<R,CU>", () -> doParse(postcircumfix, "<R,CU>", "0..6 Sequence{ 0..6 Conjugation{ 3..5 Sequence{ 3..5 Move{ 1:7:1 } } 1..2 Sequence{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("<(R),CU CF>", () -> doParse(postcircumfix, "<(R),CU CF>", "0..11 Sequence{ 0..11 Conjugation{ 5..10 Sequence{ 5..7 Move{ 1:7:1 } 8..10 Move{ 2:7:1 } } 1..4 Sequence{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } } }")),
                dynamicTest("<(R B),CU CF>", () -> doParse(postcircumfix, "<(R B),CU CF>", "0..13 Sequence{ 0..13 Conjugation{ 7..12 Sequence{ 7..9 Move{ 1:7:1 } 10..12 Move{ 2:7:1 } } 1..6 Sequence{ 1..6 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 2:1:-1 } } } } }")),
                dynamicTest("<U, R>", () -> doParse(postcircumfix, "<U, R>", "0..6 Sequence{ 0..6 Conjugation{ 3..5 Sequence{ 4..5 Move{ 0:4:1 } } 1..2 Sequence{ 1..2 Move{ 1:4:1 } } } }")),
                dynamicTest("[R,CU]", () -> doParse(postcircumfix, "[R,CU]", "0..6 Sequence{ 0..6 Commutation{ 3..5 Sequence{ 3..5 Move{ 1:7:1 } } 1..2 Sequence{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("[R,CU CF]", () -> doParse(postcircumfix, "[R,CU CF]", "0..9 Sequence{ 0..9 Commutation{ 3..8 Sequence{ 3..5 Move{ 1:7:1 } 6..8 Move{ 2:7:1 } } 1..2 Sequence{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("[R B,CU CF]", () -> doParse(postcircumfix, "[R B,CU CF]", "0..11 Sequence{ 0..11 Commutation{ 5..10 Sequence{ 5..7 Move{ 1:7:1 } 8..10 Move{ 2:7:1 } } 1..4 Sequence{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:1:-1 } } } }")),
                dynamicTest("[U,R]", () -> doParse(postcircumfix, "[U,R]", "0..5 Sequence{ 0..5 Commutation{ 3..4 Sequence{ 3..4 Move{ 0:4:1 } } 1..2 Sequence{ 1..2 Move{ 1:4:1 } } } }")),
                dynamicTest("(R)*", () -> doParse(postcircumfix, "(R)*", "0..4 Sequence{ 0..4 Reflection{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(postcircumfix, "(R' U F)*", "0..9 Sequence{ 0..9 Reflection{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParsePreinfixNotation() {
        return Arrays.asList(
                dynamicTest("2 times (R)", () -> doParse(preinfix, "2 times (R)", "0..11 Sequence{ 0..11 Repetition{ 2, 8..11 Grouping{ 9..10 Move{ 0:4:1 } } } }")),
                dynamicTest("3 times R", () -> doParse(preinfix, "3 times R", "0..9 Sequence{ 0..9 Repetition{ 3, 8..9 Move{ 0:4:1 } } }")),
                dynamicTest("3 times (R U F)", () -> doParse(preinfix, "3 times (R U F)", "0..15 Sequence{ 0..15 Repetition{ 3, 8..15 Grouping{ 9..10 Move{ 0:4:1 } 11..12 Move{ 1:4:1 } 13..14 Move{ 2:4:1 } } } }")),
                dynamicTest("3 times '(R U F)", () -> doParse(preinfix, "3 times '(R U F)", "0..16 Sequence{ 0..16 Repetition{ 3, 8..16 Inversion{ 9..16 Grouping{ 10..11 Move{ 0:4:1 } 12..13 Move{ 1:4:1 } 14..15 Move{ 2:4:1 } } } } }")),
                dynamicTest("'3 times (R U F)", () -> doParse(preinfix, "'3 times (R U F)", "0..16 Sequence{ 0..16 Inversion{ 1..16 Repetition{ 3, 9..16 Grouping{ 10..11 Move{ 0:4:1 } 12..13 Move{ 1:4:1 } 14..15 Move{ 2:4:1 } } } } }")),
                dynamicTest("''3 times (R U F)", () -> doParse(preinfix, "''3 times (R U F)", "0..17 Sequence{ 0..17 Inversion{ 1..17 Inversion{ 2..17 Repetition{ 3, 10..17 Grouping{ 11..12 Move{ 0:4:1 } 13..14 Move{ 1:4:1 } 15..16 Move{ 2:4:1 } } } } } }")),
                dynamicTest("4 times '3 times (R U F)", () -> doParse(preinfix, "4 times '3 times (R U F)", "0..24 Sequence{ 0..24 Repetition{ 4, 8..24 Inversion{ 9..24 Repetition{ 3, 17..24 Grouping{ 18..19 Move{ 0:4:1 } 20..21 Move{ 1:4:1 } 22..23 Move{ 2:4:1 } } } } } }")),
                dynamicTest("'(R)", () -> doParse(preinfix, "'(R)", "0..4 Sequence{ 0..4 Inversion{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("'(R F)", () -> doParse(preinfix, "'(R F)", "0..6 Sequence{ 0..6 Inversion{ 1..6 Grouping{ 2..3 Move{ 0:4:1 } 4..5 Move{ 2:4:1 } } } }")),
                dynamicTest("-(-R U F) '('R U F)", () -> doParse(preinfix, "-(-R U F) '('R U F)", "0..19 Sequence{ 0..9 Inversion{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } 10..19 Inversion{ 11..19 Grouping{ 12..14 Inversion{ 13..14 Move{ 0:4:1 } } 15..16 Move{ 1:4:1 } 17..18 Move{ 2:4:1 } } } }")),
                dynamicTest("CU conj R", () -> doParse(preinfix, "CU conj R", "0..9 Sequence{ 0..9 Conjugation{ 0..2 Move{ 1:7:1 } 8..9 Move{ 0:4:1 } } }")),
                dynamicTest("(CU CF) conj (R)", () -> doParse(preinfix, "(CU CF) conj (R)", "0..16 Sequence{ 0..16 Conjugation{ 0..7 Grouping{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 13..16 Grouping{ 14..15 Move{ 0:4:1 } } } }")),
                dynamicTest("(CU CF) conj (R B)", () -> doParse(preinfix, "(CU CF) conj (R B)", "0..18 Sequence{ 0..18 Conjugation{ 0..7 Grouping{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 13..18 Grouping{ 14..15 Move{ 0:4:1 } 16..17 Move{ 2:1:-1 } } } }")),
                dynamicTest("CU comm R", () -> doParse(preinfix, "CU comm R", "0..9 Sequence{ 0..9 Commutation{ 0..2 Move{ 1:7:1 } 8..9 Move{ 0:4:1 } } }")),
                dynamicTest("(CU CF) comm (R B)", () -> doParse(preinfix, "(CU CF) comm (R B)", "0..18 Sequence{ 0..18 Commutation{ 0..7 Grouping{ 1..3 Move{ 1:7:1 } 4..6 Move{ 2:7:1 } } 13..18 Grouping{ 14..15 Move{ 0:4:1 } 16..17 Move{ 2:1:-1 } } } }")),
                dynamicTest("*(R)", () -> doParse(preinfix, "*(R)", "0..4 Sequence{ 0..4 Reflection{ 1..4 Grouping{ 2..3 Move{ 0:4:1 } } } }")),
                dynamicTest("*('R U F)", () -> doParse(preinfix, "*('R U F)", "0..9 Sequence{ 0..9 Reflection{ 1..9 Grouping{ 2..4 Inversion{ 3..4 Move{ 0:4:1 } } 5..6 Move{ 1:4:1 } 7..8 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParsePostinfixNotation() {
        return Arrays.asList(
                dynamicTest("(R) times 2", () -> doParse(postinfix, "(R) times 2", "0..11 Sequence{ 0..11 Repetition{ 2, 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("R times 3", () -> doParse(postinfix, "R times 3", "0..9 Sequence{ 0..9 Repetition{ 3, 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R U F) times 3", () -> doParse(postinfix, "(R U F) times 3", "0..15 Sequence{ 0..15 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } }")),
                dynamicTest("(R U F)' times 3", () -> doParse(postinfix, "(R U F)' times 3", "0..16 Sequence{ 0..16 Repetition{ 3, 0..8 Inversion{ 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F) times 3'", () -> doParse(postinfix, "(R U F) times 3'", "0..16 Sequence{ 0..16 Inversion{ 0..15 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } }")),
                dynamicTest("(R U F) times 3' times 4", () -> doParse(postinfix, "(R U F) times 3' times 4", "0..24 Sequence{ 0..24 Repetition{ 4, 0..16 Inversion{ 0..15 Repetition{ 3, 0..7 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 1:4:1 } 5..6 Move{ 2:4:1 } } } } } }")),
                dynamicTest("(R)'", () -> doParse(postinfix, "(R)'", "0..4 Sequence{ 0..4 Inversion{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R F)'", () -> doParse(postinfix, "(R F)'", "0..6 Sequence{ 0..6 Inversion{ 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:4:1 } } } }")),
                dynamicTest("(R- U F)- (R' U F)'", () -> doParse(postinfix, "(R- U F)- (R' U F)'", "0..19 Sequence{ 0..9 Inversion{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } 10..19 Inversion{ 10..18 Grouping{ 11..13 Inversion{ 11..12 Move{ 0:4:1 } } 14..15 Move{ 1:4:1 } 16..17 Move{ 2:4:1 } } } }")),
                dynamicTest("R conj CU", () -> doParse(postinfix, "R conj CU", "0..9 Sequence{ 0..9 Conjugation{ 7..9 Move{ 1:7:1 } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R B) conj (CU CF)", () -> doParse(postinfix, "(R B) conj (CU CF)", "0..18 Sequence{ 0..18 Conjugation{ 11..18 Grouping{ 12..14 Move{ 1:7:1 } 15..17 Move{ 2:7:1 } } 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:1:-1 } } } }")),
                dynamicTest("R comm CU", () -> doParse(postinfix, "R comm CU", "0..9 Sequence{ 0..9 Commutation{ 7..9 Move{ 1:7:1 } 0..1 Move{ 0:4:1 } } }")),
                dynamicTest("(R B) comm (CU CF)", () -> doParse(postinfix, "(R B) comm (CU CF)", "0..18 Sequence{ 0..18 Commutation{ 11..18 Grouping{ 12..14 Move{ 1:7:1 } 15..17 Move{ 2:7:1 } } 0..5 Grouping{ 1..2 Move{ 0:4:1 } 3..4 Move{ 2:1:-1 } } } }")),
                dynamicTest("(R)*", () -> doParse(postinfix, "(R)*", "0..4 Sequence{ 0..4 Reflection{ 0..3 Grouping{ 1..2 Move{ 0:4:1 } } } }")),
                dynamicTest("(R' U F)*", () -> doParse(postinfix, "(R' U F)*", "0..9 Sequence{ 0..9 Reflection{ 0..8 Grouping{ 1..3 Inversion{ 1..2 Move{ 0:4:1 } } 4..5 Move{ 1:4:1 } 6..7 Move{ 2:4:1 } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParsePermutation() {
        return Arrays.asList(
                dynamicTest("defaultNotation (+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)",
                        () -> doParse(defaultNotatioon, "(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "0..61 Sequence{ 0..18 Permutation{ Corner sign:2 0:0 2:2 3:0 1:2 } 19..33 Permutation{ Edge sign:1 0:0 4:1 2:0 1:1 } 34..38 Permutation{ Side sign:3 0:0 } 39..44 Permutation{ Side sign:0 0:0 5:0 } 45..52 Permutation{ Side sign:2 1:0 4:0 } 53..61 Permutation{ Side sign:2 2:0 3:3 } }")),

                dynamicTest("precircumfixNotation R as permutation",
                        () -> doParse(precircumfix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (+r)",
                                "0..36 Sequence{ 0..17 Permutation{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 Permutation{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 Permutation{ Side sign:3 0:0 } }")),
                dynamicTest("prefixNotation R as permutation", () -> doParse(prefix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) +(r)",
                        "0..36 Sequence{ 0..17 Permutation{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 Permutation{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 Permutation{ Side sign:3 0:0 } }")),
                dynamicTest("suffixNotation R as permutation", () -> doParse(suffix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (r)+",
                        "0..36 Sequence{ 0..19 Permutation{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..33 Permutation{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 Permutation{ Side sign:3 0:0 } }")),
                dynamicTest("postcircumfixNotation R as permutation", () -> doParse(postcircumfix, "(ubr,bdr,dfr,fur) (ur,br,dr,fr) (r+)",
                        "0..36 Sequence{ 0..17 Permutation{ Corner sign:0 2:0 3:1 1:0 0:1 } 18..31 Permutation{ Edge sign:0 0:0 4:1 2:0 1:1 } 32..36 Permutation{ Side sign:3 0:0 } }")),


                dynamicTest("defaultNotation R /*comment*/ U F", () -> doParse(defaultNotatioon, "R /*comment*/ U F", "0..17 Sequence{ 0..1 Move{ 0:4:1 } 14..15 Move{ 1:4:1 } 16..17 Move{ 2:4:1 } }"))
        );

    }

    @TestFactory
    public List<DynamicTest> testParseMacros() {
        return Arrays.asList(
                dynamicTest("CRU", () -> doParse(notationWithMacros, "CRU", "0..3 Sequence{ 0..3 Macro{ 0..3 Sequence{ 0..3 Move{ 0:7:1 } 0..3 Move{ 1:7:1 } } } }")),
                dynamicTest("R CRU", () -> doParse(notationWithMacros, "R CRU", "0..5 Sequence{ 0..1 Move{ 0:4:1 } 2..5 Macro{ 2..5 Sequence{ 2..5 Move{ 0:7:1 } 2..5 Move{ 1:7:1 } } } }")),
                dynamicTest("CRU2", () -> doParse(notationWithMacros, "CRU2", "0..4 Sequence{ 0..4 Repetition{ 2, 0..3 Macro{ 0..3 Sequence{ 0..3 Move{ 0:7:1 } 0..3 Move{ 1:7:1 } } } } }"))
        );
    }

    @TestFactory
    public List<DynamicTest> testParseAppliedPermutation() {
        final DefaultNotation notation7x7 = new DefaultNotation(7);
        final Cube7 cube7 = new Cube7();
        cube7.transform(0, 32+64, 1);

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


    public void doParsePermutation(Notation notation, Cube cube) throws Exception {
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
    public void doParse(Notation notation, String script, String expected) throws Exception {
        ScriptParser instance = new ScriptParser(notation);
        Node node = instance.parse(script);
        String actual = dump(node);
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" + htmlEscape(script) + "</p>");
            System.out.println("      <p class=\"expected\">" +
                    htmlEscape(actual) + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
            // System.out.println("  actual: " + actual);
            System.out.println(" DynamicTest.dynamicTest(\"" + notation.getName() + "\", () -> doParse(" + notation.getName() + ", \"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }
        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);
        assertEquals(expected, actual);

    }

    private String dump(Node node) {
        StringBuilder buf = new StringBuilder();
        dump(node, buf);
        return buf.toString();
    }

    private void dump(Node node, StringBuilder b) {
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
        b.append(node.getClass().getSimpleName().substring(0, node.getClass().getSimpleName().lastIndexOf("Node")));
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
        } else if (node instanceof PermutationNode) {
            PermutationNode m = (PermutationNode) node;
            b.append(' ');
            switch (m.getType()) {
                case PermutationNode.SIDE_PERMUTATION:
                    b.append("Side");
                    break;
                case PermutationNode.EDGE_PERMUTATION:
                    b.append("Edge");
                    break;
                case PermutationNode.CORNER_PERMUTATION:
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

    private String htmlEscape(String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    @TestFactory
    public List<DynamicTest> testParseFailures() {
        return Arrays.asList(
                dynamicTest("defaultNotation knurps", () -> doFailure(defaultNotatioon, "knurps", "Statement: Keyword or Number expected. Found \"knurps\".")),
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