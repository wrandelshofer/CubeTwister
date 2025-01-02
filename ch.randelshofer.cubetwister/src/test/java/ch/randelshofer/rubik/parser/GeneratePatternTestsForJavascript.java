package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeFactory;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.parser.ast.Node;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ch.randelshofer.rubik.parser.PatternDatabaseRecord.createDataRecord;


/**
 * This class generates tests for the VirtualCube JavaScript applets.
 */
@Disabled
public class GeneratePatternTestsForJavascript {

    private static final String ID_KEY = "id";
    private static final String SCRIPT_KEY = "script";
    private static final String BTM_KEY = "btm";
    private static final String FTM_KEY = "ftm";
    private static final String QTM_KEY = "qtm";
    private static final String ORDER_KEY = "order";
    private static final String PERMUTATION_KEY = "permutation";
    private static final String NUMBER_KEY = "number";
    private static final String LAYER_COUNT_KEY = "layerCount";

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test2xPocketMoves() throws Exception {
        return createTests(2);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test3xRubikMoves() throws Exception {
        return createTests(3);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test4xRevengeMoves() throws Exception {
        return createTests(4);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test5xProfessorMoves() throws Exception {
        return createTests(5);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test6xVCube6Moves() throws Exception {
        return createTests(6);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test7xVCube7Moves() throws Exception {
        return createTests(7);
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test2xPocket() throws Exception {
        return createTests(2, "../resources//database/2xPocket/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                "ltm-not-used",
                //"btm",
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test3xRubik() throws Exception {
        return createTests(3, "../resources//database/3xRubik/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                "ltm",
                //"btm",
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "author",
                "description_de",
                "description_en"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test4xRevenge() throws Exception {
        return createTests(4, "../resources//database/4xRevenge/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                //"ltm",
                BTM_KEY,
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test5xProfessors() throws Exception {
        return createTests(5, "../resources//database/5xProfessor/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                //"ltm",
                BTM_KEY,
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test6xVCube6() throws Exception {
        return createTests(6, "../resources//database/6xVCube6/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                //"ltm",
                BTM_KEY,
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test7xVCube7s() throws Exception {
        return createTests(7, "../resources//database/7xVCube7/PatternExportTab.txt", Arrays.asList(
                ID_KEY,
                NUMBER_KEY,
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                SCRIPT_KEY,
                //"ltm",
                BTM_KEY,
                FTM_KEY,
                QTM_KEY,
                ORDER_KEY,
                PERMUTATION_KEY,
                "description_de",
                "description_en",
                "author"

        ));
    }

    private Stream<DynamicTest> createTests(int layerCount, String filename, List<String> headers) throws Exception {
        return Files.lines(Paths.get(filename), Charset.forName("x-MacRoman"))
                .map(line -> createDataRecord(line, headers))
                .map(p -> DynamicTest.dynamicTest(p.get(ID_KEY) + " " + p.get(NUMBER_KEY), () -> doPatternTest(layerCount, p)))
                ;
    }

    private static Map<Integer, DefaultScriptNotation> defaultNotationMap = new HashMap<>();

    private static DefaultScriptNotation getDefaultNotation(int layerCount) {
        return defaultNotationMap.computeIfAbsent(layerCount, DefaultScriptNotation::new);
    }

    private Stream<DynamicTest> createTests(int layerCount) throws Exception {
        DefaultScriptNotation notation = getDefaultNotation(layerCount);
        ScriptParser parser = new ScriptParser(notation);
        Cube cube = CubeFactory.create(layerCount);
        List<PatternDatabaseRecord> list = new ArrayList<>();
        List<String> keys = List.of(ID_KEY, NUMBER_KEY, LAYER_COUNT_KEY, SCRIPT_KEY, BTM_KEY, FTM_KEY, QTM_KEY, ORDER_KEY, PERMUTATION_KEY);
        for (Move move : notation.getAllMoveSymbols()) {
            for (String token : notation.getAllMoveTokens(move)) {
                cube.reset();
                Node parsedScript = parser.parse(token);
                parsedScript.applyTo(cube, false);
                MoveMetrics mm = parsedScript.resolvedStream(false).collect(() -> new MoveMetrics(false), MoveMetrics::accept, MoveMetrics::combine);
                String[] data = new String[keys.size()];
                data[1] = "1";
                data[2] = Integer.toString(layerCount);
                data[0] = "Move." + token;
                data[3] = token;
                data[4] = Integer.toString(mm.getBlockTurnCount());
                data[5] = Integer.toString(mm.getFaceTurnCount());
                data[6] = Integer.toString(mm.getQuarterTurnCount());
                data[7] = Integer.toString(Cubes.getVisibleOrder(cube));
                data[8] = Cubes.toPermutationString(cube, notation);
                list.add(new PatternDatabaseRecord(data, keys));
            }
        }


        return list.stream()
                .map(p -> DynamicTest.dynamicTest(p.get(ID_KEY), () -> doPatternTest(layerCount, p)))
                ;
    }

    private void doPatternTest(int layerCount, PatternDatabaseRecord p) throws IOException {
        Appendable w = System.out;
        w.append("{ ");
        w.append("id: \"" + p.get(ID_KEY) + "\", ");
        w.append("layerCount: " + layerCount + ", ");
        w.append("script: \"" + p.get(SCRIPT_KEY).replaceAll("\\n", "\\\\n") + "\", ");
        w.append("btm: " + (p.get(BTM_KEY) == null ? null : p.get(BTM_KEY).replaceAll("\\*", "")) + ", ");
        w.append("ftm: " + p.get(FTM_KEY).replaceAll("\\*", "") + ", ");
        w.append("qtm: " + p.get(QTM_KEY).replaceAll("\\*", "") + ", ");
        w.append("order: " + p.get(ORDER_KEY) + ", ");
        w.append("perm: \"" + p.get(PERMUTATION_KEY).replaceAll("\\n", "\\\\n") + "\", ");
        w.append("},\n");
    }
}
