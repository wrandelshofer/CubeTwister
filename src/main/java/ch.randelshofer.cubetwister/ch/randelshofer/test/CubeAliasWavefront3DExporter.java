/*
 * @(#)CubeExporter.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.test;

import ch.randelshofer.aliaswavefront3d.AliasWavefront3DWriter;
import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.idx3d_SceneToFaceNodes;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube3d.RubiksCubeIdx3D;
import idx3d.idx3d_Scene;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Exports a 3D model of a cube to a Alias Wavefront 3D ".obj" file.
 */
public class CubeAliasWavefront3DExporter {
    public static void main(String[] args) {
        RubiksCubeIdx3D model = new RubiksCubeIdx3D();
        DefaultCubeAttributes attributes = (DefaultCubeAttributes) model.getAttributes();
        model.setUnitScaleFactor(1.0f);
        idx3d_Scene scene = (idx3d_Scene) model.getScene();
        try (Writer w = Files.newBufferedWriter(Paths.get("rubikscube.obj"), StandardCharsets.US_ASCII)) {
            AliasWavefront3DWriter writer = new AliasWavefront3DWriter(w);
            writer.setRelativeIndices(true);
            writer.setShareCoordinates(false);
            writer.setCoalesceVertices(false);
            Map<String, List<List<FaceNode>>> objects = new idx3d_SceneToFaceNodes().toFaceNodes(scene);
            writer.write(objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
