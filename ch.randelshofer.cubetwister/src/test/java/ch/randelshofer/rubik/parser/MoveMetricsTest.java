/*
 * @(#)MoveMetricsTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import ch.randelshofer.rubik.parser.ast.Node;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveMetricsTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> testAccept() {
        return Arrays.asList(
                DynamicTest.dynamicTest("1", () -> doAccept(3, "R", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("2", () -> doAccept(3, "R2", 1, 1, 1, 2, 1)),
                DynamicTest.dynamicTest("3", () -> doAccept(3, "R'", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("4", () -> doAccept(3, "R R", 1, 1, 1, 2, 2)),
                DynamicTest.dynamicTest("5", () -> doAccept(3, "R R R", 1, 1, 1, 1, 3)),
                DynamicTest.dynamicTest("6", () -> doAccept(3, "R R R2", 0, 0, 0, 0, 3)),
                DynamicTest.dynamicTest("10", () -> doAccept(3, "CR", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("11", () -> doAccept(3, "CR2", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("12", () -> doAccept(3, "CR'", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("13", () -> doAccept(3, "R CR R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("24", () -> doAccept(3, "R U", 2, 2, 2, 2, 2)),
                DynamicTest.dynamicTest("25", () -> doAccept(3, "R R U", 2, 2, 2, 3, 3)),
                DynamicTest.dynamicTest("25", () -> doAccept(3, "R U R", 3, 3, 3, 3, 3)),
                DynamicTest.dynamicTest("26", () -> doAccept(3, "R R U2", 2, 2, 2, 4, 3)),
                DynamicTest.dynamicTest("27", () -> doAccept(3, "R U2 R", 3, 3, 3, 4, 3)),
                DynamicTest.dynamicTest("34", () -> doAccept(3, "CR R R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("33", () -> doAccept(3, "R CU R", 2, 2, 2, 2, 3)),
                DynamicTest.dynamicTest("34", () -> doAccept(3, "CU R R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("41", () -> doAccept(3, "(R)1", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("42", () -> doAccept(3, "(R)2", 1, 1, 1, 2, 2)),
                DynamicTest.dynamicTest("43", () -> doAccept(3, "(R)3", 1, 1, 1, 1, 3)),
                DynamicTest.dynamicTest("44", () -> doAccept(3, "(R)4", 0, 0, 0, 0, 4)),
                DynamicTest.dynamicTest("51", () -> doAccept(3, "R MR L'", 0, 0, 0, 0, 3)),
                DynamicTest.dynamicTest("101", () -> doAccept(3, "MR2 MF2 MU2", 3, 3, 6, 12, 3))
        );
    }

    /**
     * Set this to true to generate HTML output for the JavaScript
     * version of this test.
     */
    private static boolean html = false;

    private void doAccept(int layerCount, String script, int expectedBtm, int expectedLtm, int expectedFtm, int expectedQtm, int expectedCount) throws IOException {
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" + script + "</p>");
            System.out.println("      <p class=\"expected\">" + expectedBtm + "btm, " + expectedLtm + "ltm, " + expectedFtm + "ftm, " + expectedQtm + "qtm, " + expectedCount + " moves" + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
            System.out.println("doMetrics script: " + script);
            System.out.println("  expected: " + expectedBtm + "btm, " + expectedLtm + "ltm, " + expectedFtm + "ftm, " + expectedQtm + "qtm, " + expectedCount + " moves");
        }
        ScriptParser parser = new ScriptParser(new DefaultScriptNotation(layerCount));
        Node ast = parser.parse(script);

        MoveMetrics instance = new MoveMetrics();
        instance.accept(ast);

        int ftm = instance.getFaceTurnCount();
        int ltm = instance.getLayerTurnCount();
        int qtm = instance.getQuarterTurnCount();
        int btm = instance.getBlockTurnCount();
        int count = instance.getMoveCount();
        if (!html) {
            System.out.println("  actual  : " + btm + "btm, " + ltm + "ltm, " + ftm + "ftm, " + qtm + "qtm, " + count + " moves");
        }
        assertEquals(expectedBtm, btm);
        assertEquals(expectedFtm, ftm);
        assertEquals(expectedLtm, ltm);
        assertEquals(expectedQtm, qtm);
        assertEquals(expectedCount, count);
    }
}