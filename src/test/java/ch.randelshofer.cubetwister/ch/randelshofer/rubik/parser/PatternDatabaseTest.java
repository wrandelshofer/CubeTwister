/*
 * @(#)PatternDatabaseTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeFactory;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.notation.DefaultNotation;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contains test that use TSV files exported from the pattern database.
 * <p>
 * (The pattern database is not part of this project.)
 */
public class PatternDatabaseTest {


    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test2xPocket() throws Exception {
        return createTests(2, "/Users/Shared/Homepage/rubik/database/2xPocket/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                "ltm-not-used",
                //"btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test3xRubik() throws Exception {
        return createTests(3, "/Users/Shared/Homepage/rubik/database/3xRubik/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                "ltm",
                //"btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "author",
                "description_de",
                "description_en"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test4xRevenge() throws Exception {
        return createTests(4, "/Users/Shared/Homepage/rubik/database/4xRevenge/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                //"ltm",
                "btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test5xProfessors() throws Exception {
        return createTests(5, "/Users/Shared/Homepage/rubik/database/5xProfessor/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                //"ltm",
                "btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test6xVCube6() throws Exception {
        return createTests(6, "/Users/Shared/Homepage/rubik/database/6xVCube6/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                //"ltm",
                "btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "description_de",
                "description_en",
                "author"

        ));
    }

    @Nonnull
    @TestFactory
    public Stream<DynamicTest> test7xVCube7s() throws Exception {
        return createTests(7, "/Users/Shared/Homepage/rubik/database/7xVCube7/PatternExportTab.txt", Arrays.asList(
                "id",
                "number",
                "category_de",
                "category_en",
                "sub_category_de",
                "sub_category_en",
                "name_de",
                "name_en",
                "script",
                //"ltm",
                "btm",
                "ftm",
                "qtm",
                "order",
                "permutation",
                "description_de",
                "description_en",
                "author"

        ));
    }

    private static DataRecord createDataRecord(String line, List<String> headers) {
        String[] data = line.replace('\u000b', '\n').split("\t");
        return new DataRecord(data, headers);
    }

    private Stream<DynamicTest> createTests(int layerCount, String filename, List<String> headers) throws Exception {
        return Files.lines(Paths.get(filename), Charset.forName("x-MacRoman"))
                .map(line -> createDataRecord(line, headers))
                .map(p -> DynamicTest.dynamicTest(p.get("id") + " " + p.get("number"), () -> doPatternTest(layerCount, p)))
                ;
    }

    /**
     * Javascript Array with String key and numeric indices.
     */
    private static class DataRecord implements Cloneable, Iterable<String> {
        String[] data;
        List<String> keys;

        public DataRecord(String[] data, List<String> keys) {
            this.keys = keys;
            if (data.length < keys.size()) {
                this.data = new String[keys.size()];
                System.arraycopy(data, 0, this.data, 0, data.length);
            } else {
                this.data = data;
            }
            if (this.data.length != keys.size()) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public Iterator<String> iterator() {
            return Arrays.asList(data).iterator();
        }

        public int length() {
            return data.length;
        }

        public String get(int index) {
            return data[index];
        }

        public boolean hasKey(String key) {
            return keys.contains(key);
        }

        public String get(String key) {
            int index = keys.indexOf(key);
            return index == -1 ? null : data[index];
        }

        public void set(int index, String value) {
            data[index] = value;
        }

        public void set(String key, String value) {
            ;
            int index = ensureCapacity(key);
            data[index] = value;
        }

        private int ensureCapacity(String key) {
            int index = keys.indexOf(key);
            if (index == -1) {
                String[] tmp = data;
                data = new String[data.length + 1];
                System.arraycopy(tmp, 0, data, 0, tmp.length);
                keys = new ArrayList<>(keys);
                keys.add(key);
                return keys.size() - 1;
            }
            return index;
        }


        @Override
        public DataRecord clone() {
            try {
                DataRecord that = (DataRecord) super.clone();
                that.data = this.data.clone();
                return that;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(data);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DataRecord)) {
                return false;
            }
            DataRecord that = (DataRecord) o;
            return Arrays.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }


    private void doPatternTest(int layerCount, DataRecord data) throws Exception {
        DefaultNotation notation = new DefaultNotation(layerCount);
        ScriptParser parser = new ScriptParser(notation);
        Cube cube = CubeFactory.create(layerCount);
        Cube expectedCube = CubeFactory.create(layerCount);

        Node ast;
        try {
            ast = parser.parse(data.get("script"));
        } catch (ParseException e) {
            System.err.println(data);
            throw e;
        }
        cube.reset();
        ast.applyTo(cube, false);

        DataRecord computed = data.clone();

        MoveMetrics mm = ast.resolvedStream(false).collect(() -> new MoveMetrics(false), MoveMetrics::accept, MoveMetrics::combine);
        System.out.println("expected: " + data);

        computed.set("order", "" + Cubes.getVisibleOrder(cube));

        // Note: we can only check against values which are present in teh database

        computed.set("ltm", data.get("ltm") == null ? null : mm.getLayerTurnCount() + (data.get("ltm").contains("*") ? "*" : ""));
        computed.set("qtm", data.get("qtm") == null ? null : mm.getQuarterTurnCount() + (data.get("qtm").contains("*") ? "*" : ""));
        computed.set("ftm", data.get("ftm") == null ? null : mm.getFaceTurnCount() + (data.get("ftm").contains("*") ? "*" : ""));
        computed.set("btm", data.get("btm") == null ? null : mm.getBlockTurnCount() + (data.get("btm").contains("*") ? "*" : ""));


        // use the same permutation if it yields the same result
        computed.set("permutation", Cubes.toPermutationString(cube, notation).replace('\n', ' '));
        try {
            expectedCube.reset();
            if (data.get("permutation") != null) {
                parser.parse(data.get("permutation")).applyTo(expectedCube);
                if (cube.equals(expectedCube)) {
                    computed.set("permutation", data.get("permutation"));
                }
            }
        } catch (ParseException e) {
            // this will show up as a difference in the permutation string
        }

        //if (!data.equals(computed)) {
        System.out.println("computed: " + computed);
        // }
        DataRecord actual = data.clone();
        for (String key : data.keys) {
            if (data.get(key) != null && data.get(key).length() > 0) {
                actual.set(key, computed.get(key));
            }
        }


        assertEquals(data, actual);
    }


}
