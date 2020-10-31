/*
 * @(#)DefaultNotationTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import ch.randelshofer.cubetwister.doc.Template;
import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeFactory;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.xml.XMLPreorderIterator;
import nanoxml.XMLElement;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * DefaultNotationTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DefaultNotationTest {

    public DefaultNotationTest() {
    }

    @Test
    public void testCompareWithCubeTwisterTemplateFile() throws Exception {
        XMLElement root = new XMLElement(new HashMap<>(), true, false);
        try (InputStreamReader r = new InputStreamReader(Template.getTemplate().openStream(), StandardCharsets.UTF_8)) {
            root.parseFromReader(r);
        }
        List<String> notationNames = new ArrayList<>();
        for (XMLElement elem : (Iterable<XMLElement>) () -> new XMLPreorderIterator(root)) {
            if ("Notation".equals(elem.getName())) {
                for (XMLElement child : elem.getChildren()) {
                    if ("Name".equals(child.getName())) {
                        notationNames.add(child.getContent());
                    }
                }
            }
        }
        for (String name : notationNames) {
            var notationFromTemplate = new CubeMarkupNotation();
            notationFromTemplate.readXML(root, name);
            if (name.startsWith("Superset ENG")) {
                System.out.println("notation: " + name);
                System.out.println("layerCount: " + notationFromTemplate.getLayerCount());
                var defaultNotation = new DefaultNotation(notationFromTemplate.getLayerCount());
                for (String token : notationFromTemplate.getTokens()) {
                    if (notationFromTemplate.getSymbols(token).contains(Symbol.MOVE)) {
                        Move moveFromTemplate = notationFromTemplate.getMoveFromToken(token);
                        Move moveFromDefault = defaultNotation.getMoveFromToken(token);
                        if (moveFromDefault != null) {
                            assertEquals(moveFromTemplate, moveFromDefault, name + ", " + token);
                        }
                    }
                }
            }
        }
    }

    private static String maskToString(int layerCount, int mask) {
        var buf = new StringBuilder();
        var bin = Integer.toBinaryString(mask).replace('0', '○').replace('1', '●');
        for (var i = 0; i < layerCount - bin.length(); i++) {
            buf.append('○');
        }
        buf.append(bin);
        return buf.toString();
    }

    /**
     * Test of isToken method, of class DefaultNotation.
     *
     * @param layerCount
     * @param token      a token
     * @param expResult  expected result
     */

    private void doTokens(int layerCount, String token, boolean expResult) {
        System.out.println("isToken:" + token);
        DefaultNotation instance = new DefaultNotation(layerCount);
        boolean result = !instance.getSymbols(token).isEmpty();
        assertEquals(result, expResult);
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testTokens() {
        return Arrays.asList(
                // token, isToken
                DynamicTest.dynamicTest("3:R", () -> doTokens(3, "R", true)),
                DynamicTest.dynamicTest("3:bla", () -> doTokens(3, "bla", false)),
                DynamicTest.dynamicTest("3:TR", () -> doTokens(3, "TR", true))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testPermutation() {
        return Arrays.asList(
                dynamicTest("4:MR2", () -> doTestPermutation(4, "MR2", "(bu2,fd2) (bd2,fu2)\n" +
                        "(d1,+u2) (b1,-f4) (d2,+u3) (f3,+b4)")),
                dynamicTest("4:MB2", () -> doTestPermutation(4, "MB2", "(ur1,dl1) (dr1,ul1)\n(r1,-l4) (u1,+d2) (u2,+d3) (l3,+r4)"))
        );
    }

    private void doTestPermutation(int layerCount, String script, String expected) throws ParseException {
        DefaultNotation instance = new DefaultNotation(layerCount);
        Cube cube = CubeFactory.create(layerCount);
        new ScriptParser(instance).parse(script).applyTo(cube);
        String actual = Cubes.toPermutationString(cube, instance);
        assertEquals(expected, actual);

    }

}