/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import java.io.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.*;

/**
 * Tests for ScriptParser with DefaultNotation.
 * @author Wenrer Randelshofer
 */
public class ScriptParserNGTest {
    
    public ScriptParserNGTest() {
    }

    /**
     * Test of parse method, of class ScriptParser.
     * @param script the input script
     */
    @Test(dataProvider="scripts")
    public void testParse(String script) throws Exception {
        System.out.println("parse");
        ScriptParser instance = new ScriptParser(new DefaultNotation());
        SequenceNode expResult = null;
        SequenceNode result = instance.parse(script);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @DataProvider
    public static Object[][] scripts() {
        return new Object[][] {
            
        };
    }
 }
