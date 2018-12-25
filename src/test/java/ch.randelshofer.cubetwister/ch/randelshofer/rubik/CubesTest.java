/* @(#)CubesTest.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.rubik.parser.DefaultNotation;
import ch.randelshofer.rubik.parser.Move;
import ch.randelshofer.rubik.parser.MutableNotation;
import ch.randelshofer.rubik.parser.Notation;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.rubik.parser.SequenceNode;
import ch.randelshofer.rubik.parser.Symbol;
import ch.randelshofer.rubik.parser.Syntax;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * CubesTest.
 * @author Werner Randelshofer
 */
public class CubesTest {
    
    public CubesTest() {
    }

    /**
     * Test of create method, of class Cubes.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        int layerCount = 3;
        Cube expResult = null;
        Cube result = Cubes.create(layerCount);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @TestFactory
    public List<DynamicTest> testToVisualPermutationString_RubiksCube_Notation() {
        return Arrays.asList(
                dynamicTest("-",()->doToVisualPermutationString_RubiksCube_Notation("","()")),
               dynamicTest("R", ()->doToVisualPermutationString_RubiksCube_Notation("R","(ubr,bdr,dfr,fur)\n" +
                       "(ur,br,dr,fr)"))
        );
    }

    /**
     * Test of toVisualPermutationString method, of class Cubes.
     */
    private void doToVisualPermutationString_RubiksCube_Notation(String input, String expected) throws IOException {
        System.out.println("toVisualPermutationString input: "+input);
        RubiksCube cube = new RubiksCube();
        Notation notation = new DefaultNotation();
        ScriptParser parser =new ScriptParser(notation);
        SequenceNode ast = parser.parse(input);
        ast.applyTo(cube,false);
        String actual = Cubes.toVisualPermutationString(cube, notation);
        System.out.println("  expected: "+expected);
        System.out.println("  actual: "+actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of getOrder method, of class Cubes.
     */
    @Test
    public void testGetOrder() {
        System.out.println("getOrder");
        Cube cube = null;
        int expResult = 0;
        int result = Cubes.getOrder(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVisibleOrder method, of class Cubes.
     */
    @Test
    public void testGetVisibleOrder() {
        System.out.println("getVisibleOrder");
        Cube cube = null;
        int expResult = 0;
        int result = Cubes.getVisibleOrder(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toNormalizedStickersString method, of class Cubes.
     */
    @Test
    public void testToNormalizedStickersString() {
        System.out.println("toNormalizedStickersString");
        Cube cube = null;
        String expResult = "";
        String result = Cubes.toNormalizedStickersString(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toMappedStickersString method, of class Cubes.
     */
    @Test
    public void testToMappedStickersString() {
        System.out.println("toMappedStickersString");
        Cube cube = null;
        int[] mappings = null;
        String expResult = "";
        String result = Cubes.toMappedStickersString(cube, mappings);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMappedStickers method, of class Cubes.
     */
    @Test
    public void testGetMappedStickers_Cube_intArr() {
        System.out.println("getMappedStickers");
        Cube cube = new RubiksCube();
        int[] mappings = null;
        int[][] expResult = null;
        int[][] result = Cubes.getMappedStickers(cube, mappings);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMappedStickers method, of class Cubes.
     */
    @Test
    public void testGetMappedStickers_Cube_intArrArr() {
        System.out.println("getMappedStickers");
        Cube cube = null;
        int[][] mappings = null;
        int[][] expResult = null;
        int[][] result = Cubes.getMappedStickers(cube, mappings);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFaceOfSticker method, of class Cubes.
     */
    @Test
    public void testGetFaceOfSticker() {
        System.out.println("getFaceOfSticker");
        CubeAttributes attr = null;
        int stickerIndex = 0;
        int expResult = 0;
        int result = Cubes.getFaceOfSticker(attr, stickerIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @TestFactory
    public List<DynamicTest> testToPermutationString_Cube() {
        MutableNotation precircumfixNotation = new MutableNotation();
        precircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        MutableNotation prefixNotation = new MutableNotation();
        prefixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        MutableNotation postcircumfixNotation = new MutableNotation();
        postcircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX);
        MutableNotation suffixNotation = new MutableNotation();
        suffixNotation.putSyntax(Symbol.PERMUTATION, Syntax.SUFFIX);
      return  Arrays.asList(
          dynamicTest(".",()->doToPermutationString_Cube(".",precircumfixNotation,"()")),
              dynamicTest("R,precircumfix",()->doToPermutationString_Cube("R",precircumfixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(+r)")),
              dynamicTest("R,prefix",()->doToPermutationString_Cube("R",prefixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "+(r)")),
              dynamicTest("R,suffix",()->doToPermutationString_Cube("R",suffixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(r)+")),
              dynamicTest("R,postcirfumcix",()->doToPermutationString_Cube("R",postcircumfixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(r+)"))
        );
    }
    /**
     * Test of toPermutationString method, of class Cubes.
     */
    public void doToPermutationString_Cube(String input,Notation notation, String expected) throws IOException {
        System.out.println("toPermutationString input: "+input);
        Cube cube = new RubiksCube();
        ScriptParser parser =new ScriptParser(notation);
        SequenceNode ast = parser.parse(input);
        ast.applyTo(cube,false);
        String actual = Cubes.toPermutationString(cube, notation);
        System.out.println("  expected: "+expected);
        System.out.println("  actual: "+actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of toVisualPermutationString method, of class Cubes.
     */
    @Test
    public void testToVisualPermutationString_Cube() {
        System.out.println("toVisualPermutationString");
        Cube cube = null;
        String expResult = "";
        String result = Cubes.toVisualPermutationString(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toPermutationString method, of class Cubes.
     */
    @Test
    public void testToPermutationString_Cube_Notation() {
        System.out.println("toPermutationString");
        Cube cube = null;
        Notation notation = null;
        String expResult = "";
        String result = Cubes.toPermutationString(cube, notation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toCornerPermutationString method, of class Cubes.
     */
    @Test
    public void testToCornerPermutationString_Cube_Notation() {
        System.out.println("toCornerPermutationString");
        Cube cube = null;
        Notation notation = null;
        String expResult = "";
        String result = Cubes.toCornerPermutationString(cube, notation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toCornerPermutationString method, of class Cubes.
     */
    @Test
    public void testToCornerPermutationString_Cube() {
        System.out.println("toCornerPermutationString");
        Cube cube = null;
        String expResult = "";
        String result = Cubes.toCornerPermutationString(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toEdgePermutationString method, of class Cubes.
     */
    @Test
    public void testToEdgePermutationString_Cube_Notation() {
        System.out.println("toEdgePermutationString");
        Cube cube = null;
        Notation notation = null;
        String expResult = "";
        String result = Cubes.toEdgePermutationString(cube, notation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toEdgePermutationString method, of class Cubes.
     */
    @Test
    public void testToEdgePermutationString_Cube() {
        System.out.println("toEdgePermutationString");
        Cube cube = null;
        String expResult = "";
        String result = Cubes.toEdgePermutationString(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toSidePermutationString method, of class Cubes.
     */
    @Test
    public void testToSidePermutationString_Cube_Notation() {
        System.out.println("toSidePermutationString");
        Cube cube = null;
        Notation notation = null;
        String expResult = "";
        String result = Cubes.toSidePermutationString(cube, notation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toVisualSidePermutationString method, of class Cubes.
     */
    @Test
    public void testToVisualSidePermutationString() {
        System.out.println("toVisualSidePermutationString");
        Cube cube = null;
        Notation notation = null;
        String expResult = "";
        String result = Cubes.toVisualSidePermutationString(cube, notation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toSidePermutationString method, of class Cubes.
     */
    @Test
    public void testToSidePermutationString_Cube() {
        System.out.println("toSidePermutationString");
        Cube cube = null;
        String expResult = "";
        String result = Cubes.toSidePermutationString(cube);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setToStickers method, of class Cubes.
     */
    @Test
    public void testSetToStickers() {
        System.out.println("setToStickers");
        Cube cube = null;
        int[] stickers = null;
        Cubes.setToStickers(cube, stickers);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setToStickersString method, of class Cubes.
     */
    @Test
    public void testSetToStickersString() throws Exception {
        System.out.println("setToStickersString");
        Cube cube = null;
        String stickersString = "";
        String faces = "";
        Cubes.setToStickersString(cube, stickersString, faces);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


    public static void main(String[] args) throws Exception {
        CubesTest test = new CubesTest();
        for (Method method : CubesTest.class.getDeclaredMethods()) {
            if (method.getAnnotation(Test.class)!=null) {
                method.invoke(test);
            }
        }

    }
}
