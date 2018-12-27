package ch.randelshofer.rubik.parser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @TestFactory
    public List<DynamicTest> testResolvedIterable() {
        return Arrays.asList(
                DynamicTest.dynamicTest("Move.1", () -> doResolvedIterable("R", "R")),
                DynamicTest.dynamicTest("Repetition.1", () -> doResolvedIterable("(R)2", "R R")),
                DynamicTest.dynamicTest("Inversion.1", () -> doResolvedIterable("(R)'", "R'")),
                DynamicTest.dynamicTest("Inversion.2", () -> doResolvedIterable("(R F)'", "F' R'")),
                DynamicTest.dynamicTest("Conjugation.1", () -> doResolvedIterable("<CU>R", "CU R CD")),
                DynamicTest.dynamicTest("Conjugation.2", () -> doResolvedIterable("<CU CF>(R)", "CU CF R CB CD")),
                DynamicTest.dynamicTest("Conjugation.3", () -> doResolvedIterable("<CU CF>(R B)", "CU CF R B CB CD")),
                DynamicTest.dynamicTest("Commutation.1", () -> doResolvedIterable("[CU,R]", "CU R CD R'")),
                DynamicTest.dynamicTest("Commutation.2", () -> doResolvedIterable("[CU CF,R]", "CU CF R CB CD R'")),
                DynamicTest.dynamicTest("Commutation.3", () -> doResolvedIterable("[CU CF,R B]", "CU CF R B CB CD B' R'")),

                DynamicTest.dynamicTest("NOP.1", () -> doResolvedIterable(".", "")),
                DynamicTest.dynamicTest("NOP.2", () -> doResolvedIterable("R . U · F", "R U F")),
                DynamicTest.dynamicTest("Sequence.1", () -> doResolvedIterable("(R U F)", "R U F")),
                DynamicTest.dynamicTest("Repetition.2", () -> doResolvedIterable("(R U F)3 D1", "R U F R U F R U F D")),
                DynamicTest.dynamicTest("Inversion.3", () -> doResolvedIterable("(R- U F)- (R' U F)'", "F' U' R F' U' R")),
                DynamicTest.dynamicTest("Reflection.1", () -> doResolvedIterable("(R' U F)*", "L D' B'")),
                DynamicTest.dynamicTest("Conjugation.4", () -> doResolvedIterable("<R>U", "R U R'")),
                DynamicTest.dynamicTest("Commutation.4", () -> doResolvedIterable("[R,U]", "R U R' U'")),
                DynamicTest.dynamicTest("Permutation.1", () -> doResolvedIterable("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)"))
        );
    }

    void doResolvedIterable(String script, String expected) throws Exception {
        System.out.println("doResolvedIterable script: " + script);
        System.out.println("  expected: " + expected);

        DefaultNotation notation = new DefaultNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        Map<String, MacroNode> macros = Collections.emptyMap();

        StringWriter out = new StringWriter();
        try (PrintWriter w = new PrintWriter(out)) {
            boolean first = true;
            for (Node node : instance.resolvedIterable(false)) {
                System.out.println(node);
                if (first) {
                    first = false;
                } else {
                    w.print(" ");
                }
                node.writeTokens(w, notation, macros);
            }
        }

        String actual = out.toString();

        assertEquals(expected, actual);
    }
}