/*
 * @(#)CubeExporter.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.test;

import ch.randelshofer.aliaswavefront3d.AliasWavefront3DWriter;
import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.idx3d_SceneToFaceNodes;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.cube3d.AbstractCubeIdx3D;
import ch.randelshofer.rubik.cube3d.ProfessorCubeIdx3D;
import idx3d.idx3d_Scene;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Exports a 3D model of a cube to a Alias Wavefront 3D ".obj" file.
 */
public class CubeAliasWavefront3DExporter {
    public static void main(String[] args) {
        //export(new RubiksCubeIdx3D(), Paths.get("rubikscube.obj"));
        export(new ProfessorCubeIdx3D(), Paths.get("professorcube.obj"));
    }

    public static void export(AbstractCubeIdx3D cube3d, Path path) {
        DefaultCubeAttributes attributes = (DefaultCubeAttributes) cube3d.getAttributes();
        cube3d.setUnitScaleFactor(1.0f);
        idx3d_Scene scene = (idx3d_Scene) cube3d.getScene();
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.US_ASCII)) {
            AliasWavefront3DWriter writer = new AliasWavefront3DWriter(w);
            writer.setRelativeIndices(true);
            writer.setShareCoordinates(false);
            writer.setCoalesceVertices(false);
            writer.setNumberFormat(AliasWavefront3DWriter.FLOAT_FORMAT);
            Map<String, List<List<FaceNode>>> objects = new idx3d_SceneToFaceNodes().toFaceNodes(scene);
            writer.write(objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
