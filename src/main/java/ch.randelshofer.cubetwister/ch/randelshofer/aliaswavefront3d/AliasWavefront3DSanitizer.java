package ch.randelshofer.aliaswavefront3d;

import ch.randelshofer.geom3d.ConvexClipping;
import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.PolygonCleaner;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reads an Alias Wavefront 3D .obj file exported from Cinema4D
 * and replaces all double values by float values.
 * This is necessary, because Cinema4D actually exports float
 * values but prints them as double values.
 */
public class AliasWavefront3DSanitizer {
    private boolean coalesceVertices = true;
    private boolean removeCollinearPoints = true;
    private boolean fixZeroNormals = true;
    private Path outputPath;
    private List<Path> inputPaths = new ArrayList<>();
    private DecimalFormat numberFormat = AliasWavefront3DWriter.DOUBLE_FORMAT;
    private boolean removeTextureCoordinates = false;
    private boolean splitNonConvexFaces = true;
    private boolean shareCoordinates = true;
    private boolean relativeIndices = true;
    private Set<String> removeTextureFromObject = new LinkedHashSet<>();

    public static void main(String... args) throws Exception {
        AliasWavefront3DSanitizer gen = new AliasWavefront3DSanitizer();
        Integer error = null;
        boolean help = false;
        int i = 0;
        try {
            for (; i < args.length && error == null; i++) {
                switch (args[i].toLowerCase()) {
                case "-input":
                    gen.inputPaths.add(Path.of(args[++i]));
                    break;
                case "-output":
                    gen.outputPath = Path.of(args[++i]);
                    break;
                case "-precision":
                    switch (args[++i]) {
                    case "double":
                        gen.numberFormat = AliasWavefront3DWriter.DOUBLE_FORMAT;
                        break;
                    case "float":
                        gen.numberFormat = AliasWavefront3DWriter.FLOAT_FORMAT;
                        break;
                    default:
                        throw new IllegalArgumentException();
                    }
                    break;
                case "-coalescevertices":
                    gen.coalesceVertices = parseBoolean(args[++i]);
                    break;
                case "-fixzeronormals":
                    gen.fixZeroNormals = parseBoolean(args[++i]);
                    break;
                case "-removecollinearpoints":
                    gen.removeCollinearPoints = parseBoolean(args[++i]);
                    break;
                case "-removetexturecoordinates":
                    gen.removeTextureCoordinates = parseBoolean(args[++i]);
                    break;
                case "-removetexturefromobject":
                    gen.removeTextureFromObject.add(args[++i]);
                    break;
                case "-splitnonconvexfaces":
                    gen.splitNonConvexFaces = parseBoolean(args[++i]);
                    break;
                case "-relativeindices":
                    gen.relativeIndices = parseBoolean(args[++i]);
                    break;
                case "-sharecoordinates":
                    gen.shareCoordinates = parseBoolean(args[++i]);
                    break;
                case "-help":
                    help = true;
                    break;
                default:
                    error = i;
                    break;
                }
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            error = Math.min(args.length - 1, i);
        }
        if (error != null) {
            System.err.println("Illegal option: " + args[error]);
        }
        if (error != null || help) {
            System.err.println(""
                    + "Usage: AliasWavefront3DSanitizer <option>...\n"
                    + "where possible options include:\n"
                    + "    -input <path>    Input file, can be specified more than once, default=stdin.\n"
                    + "    -output <path>   Output file, default=stdout.\n"
                    + "    -precision float|double Output precision of coordinates, default=double.\n"
                    + "    -removeCollinearPoints <boolean>    Whether to remove collinear points, default=true.\n"
                    + "    -removeTextureCoordinates <boolean>    Whether to remove texture coordinates, default=false.\n"
                    + "    -coalesceVertices <boolean>    Whether to coalesce vertices with same coordinates but different normals, default=true.\n"
                    + "    -splitNonConvexFaces <boolean>    Whether to split non-convex faces up, default=true.\n"
                    + "    -fixZeroNormals <boolean>    Whether to fix normals of length zero, default=true.\n"
                    + "    -shareCoordinates <boolean>    Whether to share coordinates between objects, default=true.\n"
                    + "    -relativeIndices <boolean>    Whether to use relative indices, default=true.\n"

            );
            return;
        }
        gen.run();

    }

    private static boolean parseBoolean(String arg) {
        switch (arg) {
        case "true":
            return true;
        case "false":
            return false;
        default:
            throw new IllegalArgumentException();
        }
    }

    private List<List<FaceNode>> doRemoveCollinearPoints(List<List<FaceNode>> data, PolygonCleaner cleaner) {
        List<List<FaceNode>> cleanData = new ArrayList<>(data.size());
        for (List<FaceNode> datum : data) {
            List<FaceNode> c = new ArrayList<>(datum);
            cleaner.removeCollinearPoints3D(c, FaceNode::getVertex);
            if (c.size() > 2) {
                cleanData.add(c);
            }
        }
        return cleanData;
    }

    private void removeCollinearPoints(Map<String, List<List<FaceNode>>> objects) {
        PolygonCleaner cleaner = new PolygonCleaner();
        for (Map.Entry<String, List<List<FaceNode>>> entry : objects.entrySet()) {
            List<List<FaceNode>> data = entry.getValue();
            List<List<FaceNode>> cleanData = doRemoveCollinearPoints(data, cleaner);
            objects.put(entry.getKey(), cleanData);
        }
    }

    private void fixZeroNormals(Map<String, List<List<FaceNode>>> objects) {
        PolygonCleaner cleaner = new PolygonCleaner();
        for (List<List<FaceNode>> faces : objects.values()) {
            for (List<FaceNode> face : faces) {
                cleaner.fixZeroNormals(face, FaceNode::getVertex, FaceNode::getNormal, FaceNode::withNormal, FaceNode::withVertex);
            }
        }
    }

    private void removeTextureCoordinates(Map<String, List<List<FaceNode>>> objects) {
        for (List<List<FaceNode>> faces : objects.values()) {
            removeTextureCoordinates(faces);
        }
    }

    private void removeTextureCoordinates(List<List<FaceNode>> faces) {
        for (List<FaceNode> face : faces) {
            for (ListIterator<FaceNode> i = face.listIterator(); i.hasNext(); ) {
                FaceNode node = i.next();
                if (node.normal != null) {
                    i.set(new FaceNode(node.vertex, null, node.normal));
                }
            }
        }
    }

    private void removeTextureFromObject(Map<String, List<List<FaceNode>>> objects) {
        for (Map.Entry<String, List<List<FaceNode>>> entry : objects.entrySet()) {
            if (removeTextureFromObject.contains(entry.getKey())) {
                removeTextureCoordinates(entry.getValue());
            }
        }
    }

    public void run() throws Exception {
        Map<String, List<List<FaceNode>>> objects;
        if (inputPaths.isEmpty()) {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            objects = new AliasWavefront3DReader(r).read();
        } else {
            objects = new LinkedHashMap<>();
            for (Path inputPath : inputPaths) {
                try (AliasWavefront3DReader r = new AliasWavefront3DReader(Files.newBufferedReader(inputPath))) {
                    objects.putAll(r.read());
                }
            }
        }

        if (removeTextureCoordinates) {
            removeTextureCoordinates(objects);
        }
        if (!removeTextureFromObject.isEmpty()) {
            removeTextureFromObject(objects);
        }

        if (removeCollinearPoints) {
            removeCollinearPoints(objects);
        }

        if (fixZeroNormals) {
            fixZeroNormals(objects);
        }

        if (splitNonConvexFaces) {
            splitNonConvexFaces(objects);
        }

        if (outputPath == null) {
            PrintWriter w = new PrintWriter(System.out, true);
            AliasWavefront3DWriter ww = new AliasWavefront3DWriter(w);
            configureWriter(ww);
            ww.write(objects);
            w.flush();
        } else {
            try (AliasWavefront3DWriter ww = new AliasWavefront3DWriter(Files.newBufferedWriter(outputPath))) {
                configureWriter(ww);
                ww.write(objects);
            }
        }
    }

    public void configureWriter(AliasWavefront3DWriter ww) {
        ww.setNumberFormat(numberFormat);
        ww.setCoalesceVertices(coalesceVertices);
        ww.setShareCoordinates(shareCoordinates);
        ww.setRelativeIndices(relativeIndices);
    }

    private void splitNonConvexFaces(Map<String, List<List<FaceNode>>> objects) {
        PolygonCleaner cleaner = new PolygonCleaner();
        ConvexClipping earClipper = new ConvexClipping();
        for (Map.Entry<String, List<List<FaceNode>>> entry : objects.entrySet()) {
            List<List<FaceNode>> newFaceList = new ArrayList<>();
            List<List<FaceNode>> faces = entry.getValue();
            for (List<FaceNode> face : faces) {
                Function<FaceNode, Point2D.Double> f2d = cleaner.getPlaneProjection(face, FaceNode::getVertex);
                boolean convex = cleaner.isConvex(face, f2d);
                if (!convex) {
                    List<Point2D.Double> polygon = face.stream().map(f2d).collect(Collectors.toList());
                    List<List<Integer>> indexList = earClipper.clipConvexIndices(polygon);
                    for (List<Integer> newFaceIndices : indexList) {
                        List<FaceNode> newFace = new ArrayList<>();
                        for (Integer newFaceIndex : newFaceIndices) {
                            newFace.add(face.get(newFaceIndex));
                        }
                        newFaceList.add(newFace);
                    }
                } else {
                    newFaceList.add(face);
                }
            }
            entry.setValue(newFaceList);
        }

    }
}
