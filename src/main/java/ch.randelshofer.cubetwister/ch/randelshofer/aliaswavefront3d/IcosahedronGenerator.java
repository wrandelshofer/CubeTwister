package ch.randelshofer.aliaswavefront3d;

import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.Icosahedron;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads an Alias Wavefront 3D .obj file exported from Cinema4D
 * and replaces all double values by float values.
 * This is necessary, because Cinema4D actually exports float
 * values but prints them as double values.
 */
public class IcosahedronGenerator {
    private Path outputPath;
    private double radius = 1.0;
    private int subdivisions = 0;

    public static void main(String... args) throws Exception {
        IcosahedronGenerator gen = new IcosahedronGenerator();
        Integer error = null;
        boolean help = false;
        int i = 0;
        try {
            for (; i < args.length && error == null; i++) {
                switch (args[i]) {
                case "-output":
                    gen.outputPath = Path.of(args[++i]);
                    break;
                case "-radius":
                    gen.radius = Double.parseDouble(args[++i]);
                    break;
                case "-subdiv":
                    gen.subdivisions = Integer.parseInt(args[++i]);
                    break;
                case "-help":
                    help = true;
                    break;
                default:
                    error = i;
                    break;
                }
            }

        } catch (IllegalArgumentException e) {
            error = i;
        }
        if (error != null) {
            System.err.println("Illegal option: " + args[error]);
        }
        if (error != null || help) {
            System.err.println(""
                    + "Usage: IcosahederGenerator <option>...\n"
                    + "where possible options include:\n"
                    + "    -output <path>   Output file, default=stdout.\n"
                    + "    -radius <double> Radius of the icosahedron, default=1.0.\n"
                    + "    -subdiv <int>    Subdivisions of the icosahedron, default=0.\n"

            );
            return;
        }
        gen.run();

    }

    private void run() throws IOException {
        List<List<FaceNode>> faces = new Icosahedron().create(radius, subdivisions);

        Map<String, List<List<FaceNode>>> objects = new LinkedHashMap<>();
        objects.put("icosahedron", faces);
        if (outputPath == null) {
            PrintWriter w = new PrintWriter(System.out, true);
            new AliasWavefront3DWriter(w).write(objects);
            w.flush();
        } else {
            try (AliasWavefront3DWriter w = new AliasWavefront3DWriter(Files.newBufferedWriter(outputPath))) {
                w.write(objects);
            }
        }
    }


}
