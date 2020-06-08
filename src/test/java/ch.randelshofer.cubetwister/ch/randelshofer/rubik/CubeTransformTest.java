/*
 * @(#)CubeTransformTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeFactory;
import ch.randelshofer.rubik.cube.Cubes;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * This class generates tests for the VirtualCube JavaScript applets.
 */
public class CubeTransformTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> generateTransformTests() {
        return Arrays.asList(
                dynamicTest("PocketCube", () -> doGenerateTransformTests(2)),
                dynamicTest("RubiksCube", () -> doGenerateTransformTests(3)),
                dynamicTest("RevengeCube", () -> doGenerateTransformTests(4)),
                dynamicTest("ProfessorCube", () -> doGenerateTransformTests(5)),
                dynamicTest("Cube6", () -> doGenerateTransformTests(6)),
                dynamicTest("Cube7", () -> doGenerateTransformTests(7))
        );
    }

    private void doGenerateTransformTests(int layerCount) {
        Cube cube = CubeFactory.create(layerCount);
        for (int axis = 0; axis < 3; axis++) {
            for (int layer = 0; layer < layerCount; layer++) {
                int layerMask = 1 << layer;
                for (int angle = -1; angle <= 2; angle++) {
                    if (angle == 0) {
                        continue;
                    }

                    cube.reset();
                    cube.transform(axis, layerMask, angle);
                    String expected = Cubes.toPermutationString(cube);
                    System.out.println(
                            "  <article>\n" +
                                    "    <section class=\"unittest\">\n" +
                                    "      <p class=\"input\">" + layerCount + "," + axis + "," + layerMask + "," + angle + "</p>\n" +
                                    "      <p class=\"expected\">" + expected.replaceAll("\\n", "\\\\n") + "</p>\n" +
                                    "      <p class=\"actual\"></p>\n" +
                                    "    </section>\n" +
                                    "  </article>\n");
                }
            }
        }
    }

}
