package ch.randelshofer.geom3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads an Alias Wavefront 3D .obj file exported from Cinema4D
 * and replaces all double values by float values.
 * This is necessary, because Cinema4D actually exports float
 * values but prints them as double values.
 */
public class AliasWavefront3DSanitizer {
    public void sanitize(Path inputPath, Path outputPath) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(inputPath, StandardCharsets.US_ASCII);
             BufferedWriter w = Files.newBufferedWriter(outputPath, StandardCharsets.US_ASCII)) {
            int lineNumber = 0;
            for (String line = r.readLine(); line != null; line = r.readLine(), lineNumber++) {
                line = line.replaceAll("\\s+", " ").replaceAll("\\s+$", "");

                if (line.startsWith("#") || line.isEmpty()) {
                    // pass through comments and empty lines
                    w.write(line);
                    w.write("\n");
                } else {
                    String[] array = line.split(" ");
                    switch (array[0]) {
                        case "g":// new group
                        case "o":// new object
                        case "usemtl":// use material
                        case "mtllib":// material library
                        case "f":// new face
                            w.write(line);
                            w.write("\n");
                            break;
                        case "v":
                            // vertex
                            w.write("v ");
                            w.write(Float.toString((float) Double.parseDouble(array[1])));
                            w.write(" ");
                            w.write(Float.toString((float) Double.parseDouble(array[2])));
                            w.write(" ");
                            w.write(Float.toString((float) Double.parseDouble(array[3])));
                            w.write("\n");
                            break;
                        case "vn":
                            // vertex normal
                            w.write("vn ");
                            w.write(Float.toString((float) Double.parseDouble(array[1])));
                            w.write(" ");
                            w.write(Float.toString((float) Double.parseDouble(array[2])));
                            w.write(" ");
                            w.write(Float.toString((float) Double.parseDouble(array[3])));
                            w.write("\n");
                            break;
                        default:
                            throw new IOException("sanitizer does not understand \"" + array[0] + "\". in line " + (lineNumber + 1));
                    }
                }

            }
        }
    }

    public static void main(String... args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("InputFile OutputFile expected");
        }
        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);
        new AliasWavefront3DSanitizer().sanitize(inputPath, outputPath);


    }
}
