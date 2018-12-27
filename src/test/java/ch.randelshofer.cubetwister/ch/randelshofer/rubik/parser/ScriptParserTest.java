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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ScriptParser with DefaultNotation.
 *
 * @author Wenrer Randelshofer
 */
public class ScriptParserTest {

    public ScriptParserTest() {
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param script the input script
     * @throws java.lang.Exception on failure
     */
    
    public void doParse(String script) throws Exception {
        System.out.println("testParse "+script);
        ScriptParser instance = new ScriptParser(new DefaultNotation());
        SequenceNode expected = null;
        SequenceNode actual = instance.parse(script);
        assertTrue(actual instanceof SequenceNode);
    }

    @TestFactory
    public List<DynamicTest> testParse() {
        return Arrays.asList(
            DynamicTest.dynamicTest("1",()-> doParse("RUFLDB R'U'F'L'D'B' R2U2F2L2D2B2")),
            DynamicTest.dynamicTest("2",()-> doParse("TRTUTFTLTDTB TR'TU'TF'TL'TD'TB' TR2TU2TF2TL2TD2TB2")),
            DynamicTest.dynamicTest("3",()-> doParse("MRMUMFMLMDMB MR'MU'MF'ML'MD'MB' MR2MU2MF2ML2MD2MB2")),
            DynamicTest.dynamicTest("4",()-> doParse("SRSUSFSLSDSB SR'SU'SF'SL'SD'SB' SR2SU2SF2SL2SD2SB2")),
            DynamicTest.dynamicTest("5",()-> doParse("CRCUCFCLCDCB CR'CU'CF'CL'CD'CB' CR2CU2CF2CL2CD2CB2")),
            DynamicTest.dynamicTest("6",()-> doParse("R . U · F")),
            DynamicTest.dynamicTest("7",()-> doParse("(R U F)")),
            DynamicTest.dynamicTest("9",()-> doParse("(R U F)3 D1")),
            DynamicTest.dynamicTest("10",()-> doParse("(R- U F)- (R' U F)'")),
            DynamicTest.dynamicTest("11",()-> doParse("(R' U F)*")),
            DynamicTest.dynamicTest("12",()-> doParse("<R>U")),
            DynamicTest.dynamicTest("13",()-> doParse("[R,U]")),
            DynamicTest.dynamicTest("14",()-> doParse("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)"))
        );
    }
}
