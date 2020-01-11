package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeFactory;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.notation.DefaultNotation;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternDatabaseTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> testTsv() {
        return Arrays.asList(
                DynamicTest.dynamicTest("2xPocket", () -> doTsvTest(2, "/Users/Shared/Homepage/rubik/database/2xPocket/PatternExportTab.txt")),
                DynamicTest.dynamicTest("3xRubik", () -> doTsvTest(3, "/Users/Shared/Homepage/rubik/database/3xRubik/PatternExportTab.txt")),
                DynamicTest.dynamicTest("4xRevenge", () -> doTsvTest(3, "/Users/Shared/Homepage/rubik/database/4xRevenge/PatternExportTab.txt")),
                DynamicTest.dynamicTest("5xProfessor", () -> doTsvTest(3, "/Users/Shared/Homepage/rubik/database/5xProfessor/PatternExportTab.txt")),
                DynamicTest.dynamicTest("6xVCube6", () -> doTsvTest(3, "/Users/Shared/Homepage/rubik/database/6xVCube6/PatternExportTab.txt")),
                DynamicTest.dynamicTest("7xVCube7", () -> doTsvTest(3, "/Users/Shared/Homepage/rubik/database/7xVCube7/PatternExportTab.txt"))
        );

    }

    private void doTsvTest(int layerCount, String tsvFileName) throws IOException {
        DefaultNotation notation = new DefaultNotation(layerCount);
        ScriptParser parser = new ScriptParser(notation);
        Cube cube = CubeFactory.create(layerCount);
        Cube expectedCube = CubeFactory.create(layerCount);
        boolean success = true;
        Stream<String> lines = Files.lines(Paths.get(tsvFileName), Charset.forName("x-MacRoman"));
        for (String[] cols : (Iterable<String[]>) () -> lines.map(line -> line.split("\t")).iterator()) {
            String id = cols[0];
            String number = cols[1];
            String category_de = cols[2];
            String category_en = cols[3];
            String sub_category_de = cols[4];
            String sub_category_en = cols[5];
            String name_de = cols[6];
            String name_en = cols[7];
            String script = cols[8];
            String ltm = cols[9];
            String ftm = cols[10];
            String qtm = cols[11];
            String order = cols[12];
            String permutation = cols.length > 13 ? cols[13] : "";
            String description_de = cols.length > 14 ? cols[14] : "";
            String description_en = cols.length > 15 ? cols[15] : "";
            String labeledLine = "id=" + id + " nb=" + number + " script=" + script + " ltm=" + ltm + " ftm=" + ftm + " qtm=" + qtm + " perm=" + permutation;
            Node ast;
            try {
                ast = parser.parse(script);
            } catch (ParseException e) {
                System.err.println(labeledLine);
                throw e;
            }
            cube.reset();
            ast.applyTo(cube, false);
            String actualPerm = Cubes.toPermutationString(cube, notation);

            int expectedFtm = parseTurnMetricValue(ftm);
            int expectedQtm = parseTurnMetricValue(qtm);
            int expectedLtm = parseTurnMetricValue(ltm);
            int actualLtm = MoveMetrics.getLayerTurnCount(ast);
            int actualFtm = MoveMetrics.getFaceTurnCount(ast);
            int actualQtm = MoveMetrics.getQuarterTurnCount(ast);


            //+" perm="+actualPerm);
            List<AssertionError> errors = new ArrayList<>();
            if (expectedLtm != -1) {
                try {
                    assertEquals(expectedLtm, actualLtm, "ltm");
                } catch (AssertionError e) {
                    errors.add(e);
                }
            }
            try {
                assertEquals(expectedFtm, actualFtm, "ftm");
            } catch (AssertionError e) {
                errors.add(e);
            }
            try {
                assertEquals(expectedQtm, actualQtm, "qtm");
            } catch (AssertionError e) {
                errors.add(e);
            }
            try {
                expectedCube.reset();
                parser.parse(permutation).applyTo(expectedCube);
                if (!cube.equals(expectedCube)) {
                    throw new AssertionError("permutation ==> expected: <" + permutation + "> but was: <" + actualPerm + ">");
                }
            } catch (ParseException e) {
                errors.add(new AssertionError("permutation ==> expected: <" + permutation + "> but was: <" + actualPerm + ">"));
            } catch (AssertionError e) {
                errors.add(e);
            }
            if (!errors.isEmpty()) {
                success = false;
                System.out.println(labeledLine);
                for (AssertionError e : errors) {
                    System.out.print("   " + e.getMessage());
                }
                System.out.println();
//                System.out.println("   ast:"+ast.toString());
//                System.out.println("   resolved:"+                ast.toResolvedList(false).stream().map(Object::toString).collect(Collectors.joining(", "))                );
                //   System.out.println("\n   ftm=" + actualLtm + " ftm=" + actualFtm + " qtm=" + actualQtm+ " perm=" + permutation);
            }
        }
        if (!success) {
            throw new AssertionError("found unexpected values");
        }
    }

    private int parseTurnMetricValue(String ftm) {
        if (ftm.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(ftm.endsWith("*") ? ftm.substring(0, ftm.length() - 1) : ftm);
    }

}
