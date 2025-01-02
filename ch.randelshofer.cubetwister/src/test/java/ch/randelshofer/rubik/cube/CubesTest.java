/*
 * @(#)CubesTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.cube;

import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.ScriptNotations;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.rubik.parser.ast.Node;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * CubesTest.
 *
 * @author Werner Randelshofer
 */
public class CubesTest {

    public CubesTest() {
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testOrder() {
        return Arrays.asList(
                dynamicTest("A210.15, 1", () -> doTestOrder(4, "MU2 F2 WR- WF- WR F2 MU2 WR- WF WR", 2, 4)),
                dynamicTest("A250.02, 1", () -> doTestOrder(4, "MD WR2 MD- L2 B2 MU- WR2 MU SR2 F2 R2", 2, 4)),
                dynamicTest("A220.29, 3", () -> doTestOrder(5, "(r5,++f7,++l7,+r6,-f8,+l6) (u5,b5,d5,+u6,+b6,-d8) (f5,l5,-r8,+f6,-l8,++r7) (d6,+u7,+b7,+d7,++u8,++b8)", 3, 6)),
                dynamicTest("A410.12, 1", () -> doTestOrder(4, "MR2 F2 MR- F2 ML D2 ML- D2 MR D2 MR- D2 MR\n" +
                        "WD2 MR D2 MR- WD2 MR D2\n" +
                        "SD R U- WF U R- D- WR U", 6, 12)),
                dynamicTest("A601.01, 1", () -> doTestOrder(4, "MR2 WD2 MR2 F2 WD2 F2 MD2", 4, 4)),
                dynamicTest("A601.05, 1", () -> doTestOrder(4, "WD WR- MF WD- WR2 WD- MF- WR-\n" +
                        "MR2 MU2 MR2 (WF2 MU-)2", 12, 12)),
                dynamicTest("A870.02, 1", () -> doTestOrder(4, "L- WF R D SR2 T3D- SR B- WR F- U- WF U\n" +
                        "TF2 MR2 TF2 MR2 F2 MR2\n" +
                        "B- U- R- (MR MU- MR MU MR2) R U B", 24, 24)),
                dynamicTest("AA601.05, 1", () -> doTestOrder(4,
                        "(r1,-r4,++f3,++u3) (u1,++r3,+f2,-f4,+u2,-u4,+r2,f1) (l1,-b4,b1,-d4,d1,+l2) (d2,+l3,b2,+d3,++l4,+b3)",
                        24, 24)),
                dynamicTest("A210.03, 1", () -> doTestOrder(6,
                        "U- D- · NR- NL · ND2 WR MD2 M2R- · U D · M2R MD2 WR- ND2 · NR NL-",
                        2, 2))
        );
    }

    private final static boolean VERBOSE = false;

    /**
     * Test the getOrder methods of Cubes.
     */
    private void doTestOrder(int layerCount, String input, int expectedVisibleOrder, int expectedFullOrder) throws IOException {
        if (VERBOSE) {
            System.out.println("doTestOrder input: " + input);
        }
        Cube cube = CubeFactory.create(layerCount);
        ScriptNotation notation = new DefaultScriptNotation(layerCount);
        ScriptParser parser = new ScriptParser(notation);
        Node ast = parser.parse(input);
        ast.applyTo(cube, false);
        int actualVisibleOrder = Cubes.getVisibleOrder(cube);
        int actualFullOrder = Cubes.getOrder(cube);
        if (VERBOSE) {
            System.out.println("  expected: " + expectedVisibleOrder + "v " + expectedFullOrder + "r");
            System.out.println("  actual: " + actualVisibleOrder + "v " + actualFullOrder + "r");
        }
        assertEquals(expectedVisibleOrder, actualVisibleOrder, "visibleOrder");
        assertEquals(expectedFullOrder, actualFullOrder, "fullOrder");
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testToVisualPermutationString_RubiksCube_Notation() {
        return Arrays.asList(
                dynamicTest("A210.03, 1", () -> doToVisualPermutationString_RubiksCube_Notation(
                        6, "U- D- · NR- NL · ND2 WR MD2 M2R- · U D · M2R MD2 WR- ND2 · NR NL-", "(u1,d2) (u3,d4) (u5,d6) (u7,d8)")),
                dynamicTest("-", () -> doToVisualPermutationString_RubiksCube_Notation(3, "", "()")),
                dynamicTest("R", () -> doToVisualPermutationString_RubiksCube_Notation(3, "R", "(ubr,bdr,dfr,fur)\n" +
                        "(ur,br,dr,fr)"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testToPermutationStringWithDefaultNotation() {
        return Arrays.asList(
                dynamicTest("A210.03, 1", () -> doToPermutationStringWithDefaultNotation(
                        6, "U- D- · NR- NL · ND2 WR MD2 M2R- · U D · M2R MD2 WR- ND2 · NR NL-", "(u1,+d2) (u3,+d4) (u5,+d6) (u7,+d8)"))
        );
    }

    /**
     * Test of toVisualPermutationString method, of class Cubes.
     */
    private void doToVisualPermutationString_RubiksCube_Notation(int layerCount, String input, String expected) throws IOException {
        System.out.println("toVisualPermutationString input: " + input);
        Cube cube = CubeFactory.create(layerCount);
        ScriptNotation notation = new DefaultScriptNotation(layerCount);
        System.out.println(ScriptNotations.dumpNotation(notation));
        ScriptParser parser = new ScriptParser(notation);
        Node ast = parser.parse(input);
        ast.applyTo(cube, false);
        String actual = Cubes.toVisualPermutationString(cube, notation);
        System.out.println("  expected: " + expected);
        System.out.println("  actual: " + actual);
        assertEquals(expected, actual);
    }


    /**
     * Test of toVisualPermutationString method, of class Cubes.
     */
    private void doToPermutationStringWithDefaultNotation(int layerCount, String input, String expected) throws IOException {
        System.out.println("toVisualPermutationString input: " + input);
        Cube cube = CubeFactory.create(layerCount);
        ScriptNotation notation = new DefaultScriptNotation(layerCount);
        System.out.println(ScriptNotations.dumpNotation(notation));
        ScriptParser parser = new ScriptParser(notation);
        Node ast = parser.parse(input);
        ast.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);
        System.out.println("  expected: " + expected);
        System.out.println("  actual: " + actual);
        assertEquals(expected, actual);
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testToPermutationStringWithVariousNotations() {
        DefaultScriptNotation precircumfixNotation = new DefaultScriptNotation();
        precircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        DefaultScriptNotation prefixNotation = new DefaultScriptNotation();
        prefixNotation.removeToken(Symbol.INVERSION_OPERATOR, "-");
        prefixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        DefaultScriptNotation postcircumfixNotation = new DefaultScriptNotation();
        postcircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX);
        DefaultScriptNotation suffixNotation = new DefaultScriptNotation();
        suffixNotation.putSyntax(Symbol.PERMUTATION, Syntax.SUFFIX);
        return Arrays.asList(
                dynamicTest(".", () -> doToPermutationString_Cube_Notation(".", precircumfixNotation, "()")),
                dynamicTest("R,precircumfix", () -> doToPermutationString_Cube_Notation("R", precircumfixNotation, "(ubr,bdr,dfr,fur)\n" +
                        "(ur,br,dr,fr)\n" +
                        "(+r)")),
                dynamicTest("R,prefix", () -> doToPermutationString_Cube_Notation("R", prefixNotation, "(ubr,bdr,dfr,fur)\n" +
                        "(ur,br,dr,fr)\n" +
                        "+(r)")),
                dynamicTest("R,suffix", () -> doToPermutationString_Cube_Notation("R", suffixNotation, "(ubr,bdr,dfr,fur)\n" +
                        "(ur,br,dr,fr)\n" +
                        "(r)+")),
                dynamicTest("R,postcircumfix", () -> doToPermutationString_Cube_Notation("R", postcircumfixNotation, "(ubr,bdr,dfr,fur)\n" +
                        "(ur,br,dr,fr)\n" +
                        "(r+)")),

                dynamicTest("corners,precircumfix", () -> doToPermutationString_Cube_Notation("(+urf) (-dbl)", precircumfixNotation, "(+urf) (-dbl)")),
                dynamicTest("corners,prefix", () -> doToPermutationString_Cube_Notation("+(urf) -(dbl)", prefixNotation, "+(urf) -(dbl)")),
                dynamicTest("corners,suffix", () -> doToPermutationString_Cube_Notation("(urf)+ (dbl)-", suffixNotation, "(urf)+ (dbl)-")),
                dynamicTest("corners,postcirfumcix", () -> doToPermutationString_Cube_Notation("(urf+) (dbl-)", postcircumfixNotation, "(urf+) (dbl-)"))
        );
    }

    /**
     * Test of toPermutationString method, of class Cubes.
     */
    public void doToPermutationString_Cube_Notation(String input, @Nonnull ScriptNotation notation, String expected) throws IOException {
        System.out.println("toPermutationString input: " + input);
        Cube cube = new RubiksCube();
        ScriptParser parser = new ScriptParser(notation);
        Node ast = parser.parse(input);
        ast.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);
        System.out.println("  expected: " + expected);
        System.out.println("  actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of toVisualPermutationString method, of class Cubes.
     */
    @Test
    public void testToVisualPermutationString_Cube_Notation() {
        System.out.println("toVisualPermutationString");
        Cube cube = new RubiksCube();
        String expResult = "()";
        String result = Cubes.toVisualPermutationString(cube);
        assertEquals(expResult, result);
    }


}
