package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Notation;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class InterpreterTest {
    @TestFactory
    public List<DynamicTest> testMoves() {
        Notation defaultNotation = new DefaultNotation();

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

    private Cube buildRubik(Move... moves) {
        final RubiksCube cube = new RubiksCube();
        for (Move m : moves) {
            cube.transform(m.getAxis(), m.getLayerMask(), m.getAngle());
        }
        return cube;
    }


    private void doTestMove(Notation notation, String script, Cube expected) throws ParseException {
        ScriptParser instance = new ScriptParser(notation);
        final Node parsed = instance.parse(script);
        final RubiksCube actual = new RubiksCube();
        parsed.applyTo(actual, false);

        System.err.println("expected: " + Cubes.toPermutationString(expected, notation));
        System.err.println("actual: " + Cubes.toPermutationString(actual, notation));

        assertEquals(expected, actual);
    }

}
