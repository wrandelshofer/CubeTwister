/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DefaultNotationTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DefaultNotationTest {

    public DefaultNotationTest() {
    }


    /**
     * Test of isToken method, of class DefaultNotation.
     *
     * @param token     a token
     * @param expResult expected result
     */

    private void doTokens(String token, boolean expResult) {
        System.out.println("isToken:" + token);
        DefaultNotation instance = new DefaultNotation();
        boolean result = !instance.getSymbols(token).isEmpty();
        assertEquals(result, expResult);
    }

    @TestFactory
    public List<DynamicTest> testTokens() {
        return Arrays.asList(
                // token, isToken
                DynamicTest.dynamicTest("1", () -> doTokens("R", true)),
                DynamicTest.dynamicTest("1", () -> doTokens("bla", false)),
                DynamicTest.dynamicTest("1", () -> doTokens("TR", true))
        );
    }
}