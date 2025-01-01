/*
 * @(#)InterpreterTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.parser.ast.Node;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class InterpreterTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> testMoves() {
        ScriptNotation defaultNotation = new DefaultScriptNotation();

        return Arrays.asList(
                dynamicTest("R", () -> doTestMove(defaultNotation, "R", buildRubik(Move.R))),
                dynamicTest("RI", () -> doTestMove(defaultNotation, "R'", buildRubik(Move.RI))),
                dynamicTest("R2", () -> doTestMove(defaultNotation, "R2", buildRubik(Move.R, Move.R))),
                dynamicTest("R U R' U'", () -> doTestMove(defaultNotation, "R U R' U'", buildRubik(Move.R, Move.U, Move.RI, Move.UI))),
                dynamicTest("[R,U]", () -> doTestMove(defaultNotation, "[R,U]", buildRubik(Move.R, Move.U, Move.RI, Move.UI))),
                dynamicTest("R U R'", () -> doTestMove(defaultNotation, "R U R'", buildRubik(Move.R, Move.U, Move.RI))),
                dynamicTest("<R>U", () -> doTestMove(defaultNotation, "<R>U", buildRubik(Move.R, Move.U, Move.RI))),
                dynamicTest("<R>'U", () -> doTestMove(defaultNotation, "<R>'U", buildRubik(Move.RI, Move.U, Move.R)))
        );
    }

    @Nonnull
    private Cube buildRubik(@Nonnull Move... moves) {
        final RubiksCube cube = new RubiksCube();
        for (Move m : moves) {
            cube.transform(m.getAxis(), m.getLayerMask(), m.getAngle());
        }
        return cube;
    }


    private void doTestMove(@Nonnull ScriptNotation notation, String script, @Nonnull Cube expected) throws ParseException {
        ScriptParser instance = new ScriptParser(notation);
        final Node parsed = instance.parse(script);
        final RubiksCube actual = new RubiksCube();
        parsed.applyTo(actual, false);

        System.err.println("expected: " + Cubes.toPermutationString(expected, notation));
        System.err.println("actual: " + Cubes.toPermutationString(actual, notation));

        assertEquals(expected, actual);
    }

}
