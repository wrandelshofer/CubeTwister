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
                DynamicTest.dynamicTest("1", () -> doResolvedIterable("R", "R")),
                DynamicTest.dynamicTest("2", () -> doResolvedIterable("(R)2", "R R")),
                DynamicTest.dynamicTest("3.1", () -> doResolvedIterable("(R)'", "R'")),
                DynamicTest.dynamicTest("3.2", () -> doResolvedIterable("(R F)'", "F' R'")),
                DynamicTest.dynamicTest("4.1", () -> doResolvedIterable("<CU>R", "CU R CD")),
                DynamicTest.dynamicTest("4.2", () -> doResolvedIterable("<CU CF>(R)", "CU CF R CB CD")),
                DynamicTest.dynamicTest("4.3", () -> doResolvedIterable("<CU CF>(R B)", "CU CF R B CB CD")),
                DynamicTest.dynamicTest("5.1", () -> doResolvedIterable("[CU,R]", "CU R CD R'")),
                DynamicTest.dynamicTest("5.2", () -> doResolvedIterable("[CU CF,R]", "CU CF R CB CD R'")),
                DynamicTest.dynamicTest("5.3", () -> doResolvedIterable("[CU CF,R B]", "CU CF R B CB CD B' R'"))
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
                if (node instanceof MoveNode) {
                    if (first) {
                        first = false;
                    } else {
                        w.print(" ");
                    }
                    node.writeTokens(w, notation, macros);
                }
            }
        }

        String actual = out.toString();

        assertEquals(expected, actual);
    }
}