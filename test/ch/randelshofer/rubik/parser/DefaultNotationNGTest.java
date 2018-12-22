/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;

import org.testng.annotations.Test;

/**
 * DefaultNotationNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DefaultNotationNGTest {

    public DefaultNotationNGTest() {
    }


    /**
     * Test of isToken method, of class DefaultNotation.
     * @param token a token
     * @param expResult expected result
     */
    @Test(dataProvider="tokens")
    public void testIsToken(String token, boolean expResult) {
        System.out.println("isToken:"+token);
        DefaultNotation instance = new DefaultNotation();
        boolean result = instance.isToken(token);
        assertEquals(result, expResult);
    }
    
        @DataProvider
    public static Object[][] tokens() {
        return new Object[][]{
            // token, isToken
            {"R",true},
                {"bla",false},
                {"TR",true},
                };}
}