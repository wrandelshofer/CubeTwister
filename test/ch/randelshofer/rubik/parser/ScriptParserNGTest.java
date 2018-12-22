/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import java.io.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.*;

/**
 * Tests for ScriptParser with DefaultNotation.
 *
 * @author Wenrer Randelshofer
 */
public class ScriptParserNGTest {

    public ScriptParserNGTest() {
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param script the input script
     * @throws java.lang.Exception on failure
     */
    @Test(dataProvider = "scripts")
    public void testParse(String script) throws Exception {
        System.out.println("testParse "+script);
        ScriptParser instance = new ScriptParser(new DefaultNotation());
        SequenceNode expected = null;
        SequenceNode actual = instance.parse(script);
        assertTrue(actual instanceof SequenceNode);
    }

    @DataProvider
    public static Object[][] scripts() {
        return new Object[][]{
            {"RUFLDB R'U'F'L'D'B' R2U2F2L2D2B2"},
            {"TRTUTFTLTDTB TR'TU'TF'TL'TD'TB' TR2TU2TF2TL2TD2TB2"},
            {"MRMUMFMLMDMB MR'MU'MF'ML'MD'MB' MR2MU2MF2ML2MD2MB2"},
            {"SRSUSFSLSDSB SR'SU'SF'SL'SD'SB' SR2SU2SF2SL2SD2SB2"},
            {"CRCUCFCLCDCB CR'CU'CF'CL'CD'CB' CR2CU2CF2CL2CD2CB2"},
            {"R . U · F"},
            {"(R U F)"},
            {"(R U F)3 D1"},
            {"(R- U F)- (R' U F)'"},
            {"(R' U F)*"},
            {"<R>U"},
            {"[R,U]"},
            {"(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)"},};
    }
}
