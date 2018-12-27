/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ScriptParser with DefaultNotation.
 *
 * @author Wenrer Randelshofer
 */
public class ScriptParserTest {
    private static boolean html = false;

    public ScriptParserTest() {
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param script the input script
     * @throws java.lang.Exception on failure
     */
    
    public void doParse(String script, String expected) throws Exception {
        if (!html) {
           // System.out.println("testParse " + script);
        }
        ScriptParser instance = new ScriptParser(new DefaultNotation());
        SequenceNode node = instance.parse(script);
        String actual=node.toString();
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" +  htmlEscape(script) + "</p>");
            System.out.println("      <p class=\"expected\">" +
                    htmlEscape(actual) + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
           // System.out.println("  actual: " + actual);
           // System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doParse(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }
        assertEquals(expected,actual);
    }

    private String htmlEscape(String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
    }



    @TestFactory
    public List<DynamicTest> testParse() {
        return Arrays.asList(
                DynamicTest.dynamicTest("1", () -> doParse("R", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("U", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:4 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("F", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:4 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("L", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:1 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("D", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:1 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("B", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:1 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("R'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:4 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("U'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:4 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("F'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:4 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("L'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:1 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("D'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:1 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("B'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:1 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("R2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("U2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:4 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("F2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:4 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("L2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:1 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("D2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:1 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("B2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:1 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MR", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:2 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MU", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:2 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MF", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:2 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("ML", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:2 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MD", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:2 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MB", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:2 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MR'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:2 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MU'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:2 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MF'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:2 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("ML'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:2 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MD'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:2 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MB'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:2 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MR2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:2 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MU2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:2 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MF2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:2 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("ML2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:2 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MD2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:2 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("MB2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:2 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TR", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:6 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TU", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:6 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TF", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:6 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TL", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:3 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TD", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:3 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TB", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:3 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TR'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:6 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TU'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:6 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TF'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:6 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TL'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:3 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TD'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:3 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TB'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:3 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TR2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:6 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TU2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:6 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TF2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:6 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TL2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:3 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TD2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:3 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("TB2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:3 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CR", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:7 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CU", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CF", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:7 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CL", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:7 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CD", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CB", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:7 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CR'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:7 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CU'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:7 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CF'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:7 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CL'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:7 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CD'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:7 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CB'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:7 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CR2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:7 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CU2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CF2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:7 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CL2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:7 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CD2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("CB2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:7 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SR", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:5 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SU", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:5 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SF", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:5 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SL", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:5 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SD", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:5 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SB", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:5 an:-1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SR'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:5 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SU'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:5 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SF'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:5 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SL'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:5 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SD'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:1 lm:5 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SB'", "SequenceNode{ StatementNode{ InversionNode{ MoveNode{ax:2 lm:5 an:-1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SR2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:5 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SU2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:5 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SF2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:5 an:2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SL2", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:5 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SD2", "SequenceNode{ StatementNode{ MoveNode{ax:1 lm:5 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("SB2", "SequenceNode{ StatementNode{ MoveNode{ax:2 lm:5 an:-2} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)", "SequenceNode{ StatementNode{ GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)'", "SequenceNode{ StatementNode{ InversionNode{ GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R)2", "SequenceNode{ StatementNode{ RepetitionNode{ 2, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3", "SequenceNode{ StatementNode{ RepetitionNode{ 3, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)'3", "SequenceNode{ StatementNode{ RepetitionNode{ 3, InversionNode{ GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3'", "SequenceNode{ StatementNode{ InversionNode{ RepetitionNode{ 3, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3''", "SequenceNode{ StatementNode{ InversionNode{ InversionNode{ RepetitionNode{ 3, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3'4", "SequenceNode{ StatementNode{ RepetitionNode{ 4, InversionNode{ RepetitionNode{ 3, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R)'", "SequenceNode{ StatementNode{ InversionNode{ GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R F)'", "SequenceNode{ StatementNode{ InversionNode{ GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R- U F)- (R' U F)'", "SequenceNode{ StatementNode{ InversionNode{ GroupingNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:4 an:1} } } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } StatementNode{ InversionNode{ GroupingNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:4 an:1} } } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU>R", "SequenceNode{ StatementNode{ ConjugationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } }, MoveNode{ax:0 lm:4 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU CF>(R)", "SequenceNode{ StatementNode{ ConjugationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } StatementNode{ MoveNode{ax:2 lm:7 an:1} } }, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU CF>(R B)", "SequenceNode{ StatementNode{ ConjugationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } StatementNode{ MoveNode{ax:2 lm:7 an:1} } }, GroupingNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:1 an:-1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("<R>U", "SequenceNode{ StatementNode{ ConjugationNode{ SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } }, MoveNode{ax:1 lm:4 an:1} } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU,R]", "SequenceNode{ StatementNode{ CommutationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } }, SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU CF,R]", "SequenceNode{ StatementNode{ CommutationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } StatementNode{ MoveNode{ax:2 lm:7 an:1} } }, SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU CF,R B]", "SequenceNode{ StatementNode{ CommutationNode{ SequenceNode{ StatementNode{ MoveNode{ax:1 lm:7 an:1} } StatementNode{ MoveNode{ax:2 lm:7 an:1} } }, SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:1 an:-1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("[R,U]", "SequenceNode{ StatementNode{ CommutationNode{ SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } }, SequenceNode{ StatementNode{ MoveNode{ax:1 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(R' U F)*", "SequenceNode{ StatementNode{ ReflectionNode{ GroupingNode{ StatementNode{ InversionNode{ MoveNode{ax:0 lm:4 an:1} } } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ MoveNode{ax:2 lm:4 an:1} } } } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "SequenceNode{ StatementNode{ corner perm(sign:2 0:0,2:2,3:0,1:2) } StatementNode{ edge perm(sign:1 0:0,4:1,2:0,1:1) } StatementNode{ side perm(sign:3 0:0) } StatementNode{ side perm(sign:0 0:0,5:0) } StatementNode{ side perm(sign:2 1:0,4:0) } StatementNode{ side perm(sign:2 2:0,3:3) } }")),
                DynamicTest.dynamicTest("1", () -> doParse(".", "SequenceNode{ StatementNode{ NOPNode{ } } }")),
                DynamicTest.dynamicTest("1", () -> doParse("R . U · F", "SequenceNode{ StatementNode{ MoveNode{ax:0 lm:4 an:1} } StatementNode{ NOPNode{ } } StatementNode{ MoveNode{ax:1 lm:4 an:1} } StatementNode{ NOPNode{ } } StatementNode{ MoveNode{ax:2 lm:4 an:1} } }")),
                DynamicTest.dynamicTest("1", () -> doParse("", "SequenceNode{ }"))

                );
    }
}
