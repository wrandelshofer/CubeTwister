package ch.randelshofer.rubik.parser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveMetricsTest {
    @TestFactory
    public List<DynamicTest> testMetrics() {
        return Arrays.asList(
                DynamicTest.dynamicTest("1", () -> doMetrics("R", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("2", () -> doMetrics("R2", 1, 1, 1, 2, 1)),
                DynamicTest.dynamicTest("3", () -> doMetrics("R'", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("4", () -> doMetrics("R R", 1, 1, 1, 2, 2)),
                DynamicTest.dynamicTest("5", () -> doMetrics("R R R", 1, 1, 1, 1, 3)),
                DynamicTest.dynamicTest("6", () -> doMetrics("R R R2", 0, 0, 0, 0, 3)),
                DynamicTest.dynamicTest("10", () -> doMetrics("CR", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("11", () -> doMetrics("CR2", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("12", () -> doMetrics("CR'", 0, 0, 0, 0, 1)),
                DynamicTest.dynamicTest("13", () -> doMetrics("R CR R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("24", () -> doMetrics("R U", 2, 2, 2, 2, 2)),
                DynamicTest.dynamicTest("25", () -> doMetrics("R R U", 2, 2, 2, 3, 3)),
                DynamicTest.dynamicTest("25", () -> doMetrics("R U R", 3, 3, 3, 3, 3)),
                DynamicTest.dynamicTest("26", () -> doMetrics("R R U2", 2, 2, 2, 4, 3)),
                DynamicTest.dynamicTest("27", () -> doMetrics("R U2 R", 3, 3, 3, 4, 3)),
                DynamicTest.dynamicTest("34", () -> doMetrics("CR R R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("33", () -> doMetrics("R CU R", 2, 2, 2, 2, 3)),
                DynamicTest.dynamicTest("34", () -> doMetrics("CU R R", 1, 1, 1, 2, 3)),
                DynamicTest.dynamicTest("41", () -> doMetrics("(R)1", 1, 1, 1, 1, 1)),
                DynamicTest.dynamicTest("42", () -> doMetrics("(R)2", 1, 1, 1, 2, 2)),
                DynamicTest.dynamicTest("43", () -> doMetrics("(R)3", 1, 1, 1, 1, 3)),
                DynamicTest.dynamicTest("44", () -> doMetrics("(R)4", 0, 0, 0, 0, 4)),
                DynamicTest.dynamicTest("51", () -> doMetrics("MR2 MF2 MU2", 3, 3, 6, 12, 3))
        );
    }

    private void doMetrics(String script, int expectedBtm, int expectedLtm, int expectedFtm, int expectedQtm, int expectedCount) throws IOException {
        System.out.println("doMetrics script: "+script);
        System.out.println("  expected: "+expectedBtm+"btm, "+expectedLtm+"ltm, "+expectedFtm+"ftm, "+expectedQtm+"qtm, "+expectedCount+" moves");
        ScriptParser parser = new ScriptParser(new DefaultNotation());
        SequenceNode ast = parser.parse(script);

        MoveMetrics instance = new MoveMetrics();
        instance.accept(ast);

        int ftm = instance.getFaceTurnCount();
        int ltm = instance.getLayerTurnCount();
        int qtm = instance.getQuarterTurnCount();
        int btm = instance.getBlockTurnCount();
        int count = instance.getMoveCount();
        System.out.println("  actual  : "+btm+"btm, "+ltm+"ltm, "+ftm+"ftm, "+qtm+"qtm, "+count+" moves");

        assertEquals(expectedBtm,btm);
        assertEquals(expectedFtm,ftm);
        assertEquals(expectedLtm,ltm);
        assertEquals(expectedQtm,qtm);
        assertEquals(expectedCount,count);
    }
}