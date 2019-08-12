/* @(#)CubesTest.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.parser.Node;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Arrays;
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
        Node ast = parser.parse(input);
        ast.applyTo(cube,false);
        String actual = Cubes.toVisualPermutationString(cube, notation);
        System.out.println("  expected: "+expected);
        System.out.println("  actual: "+actual);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> testToPermutationString_Cube_Notation() {
        DefaultNotation precircumfixNotation = new DefaultNotation();
        precircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        DefaultNotation prefixNotation = new DefaultNotation();
        prefixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        DefaultNotation postcircumfixNotation = new DefaultNotation();
        postcircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX);
        DefaultNotation suffixNotation = new DefaultNotation();
        suffixNotation.putSyntax(Symbol.PERMUTATION, Syntax.SUFFIX);
      return  Arrays.asList(
          dynamicTest(".",()-> doToPermutationString_Cube_Notation(".",precircumfixNotation,"()")),
              dynamicTest("R,precircumfix",()-> doToPermutationString_Cube_Notation("R",precircumfixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(+r)")),
              dynamicTest("R,prefix",()-> doToPermutationString_Cube_Notation("R",prefixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "+(r)")),
              dynamicTest("R,suffix",()-> doToPermutationString_Cube_Notation("R",suffixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(r)+")),
              dynamicTest("R,postcirfumix",()-> doToPermutationString_Cube_Notation("R",postcircumfixNotation,"(ubr,bdr,dfr,fur)\n" +
                      "(ur,br,dr,fr)\n" +
                      "(r+)")),

              dynamicTest("corners,precircumfix",()-> doToPermutationString_Cube_Notation("(+urf) (-dbl)",precircumfixNotation,"(+urf) (-dbl)")),
              dynamicTest("corners,prefix",()-> doToPermutationString_Cube_Notation("+(urf) -(dbl)",prefixNotation,"+(urf) -(dbl)")),
              dynamicTest("corners,suffix",()-> doToPermutationString_Cube_Notation("(urf)+ (dbl)-",suffixNotation,"(urf)+ (dbl)-")),
              dynamicTest("corners,postcirfumcix",()-> doToPermutationString_Cube_Notation("(urf+) (dbl-)",postcircumfixNotation,"(urf+) (dbl-)"))
        );
    }
    /**
     * Test of toPermutationString method, of class Cubes.
     */
    public void doToPermutationString_Cube_Notation(String input, Notation notation, String expected) throws IOException {
        System.out.println("toPermutationString input: "+input);
        Cube cube = new RubiksCube();
        ScriptParser parser =new ScriptParser(notation);
        Node ast = parser.parse(input);
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
    public void testToVisualPermutationString_Cube_Notation() {
        System.out.println("toVisualPermutationString");
        Cube cube = new RubiksCube();
        String expResult = "()";
        String result = Cubes.toVisualPermutationString(cube);
        assertEquals(expResult, result);
    }



}
