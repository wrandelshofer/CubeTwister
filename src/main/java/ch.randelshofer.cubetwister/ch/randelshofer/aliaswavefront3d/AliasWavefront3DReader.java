package ch.randelshofer.aliaswavefront3d;

import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.Point3D;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AliasWavefront3DReader implements Closeable {
    private final BufferedReader r;

    public AliasWavefront3DReader(Reader r) {
        this.r = (r instanceof BufferedReader) ? (BufferedReader) r : new BufferedReader(r);
    }

    @Override
    public void close() throws IOException {
        r.close();
    }

    public Map<String, List<List<FaceNode>>> read() throws IOException {
        Map<String, List<List<FaceNode>>> objects = new LinkedHashMap<>();
        List<Point3D> vertices = new ArrayList<>();
        List<Point3D> normals = new ArrayList<>();
        List<Point3D> textures = new ArrayList<>();
        List<List<FaceNode>> faces = null;
        int lineNumber = 1;
        for (String line = r.readLine(); line != null; line = r.readLine(), lineNumber++) {
            line = line.replaceAll("\\s+", " ").replaceAll("\\s+$", "");

            if (line.startsWith("#") || line.isEmpty()) {
                // pass through comments and empty lines
            } else {
                String[] array = line.split(" ");
                switch (array[0]) {
                case "g":// new group
                    break;
                case "o":// new object
                    String name = line.substring(2);
                    faces = new ArrayList<>();
                    objects.put(name, faces);
                    break;
                case "usemtl":// use material
                case "mtllib":// material library
                    break;
                case "f":// new face
                    if (faces == null) {
                        faces = new ArrayList<>();
                        objects.put("unnamed", faces);
                    }

                    List<FaceNode> face = new ArrayList<>();
                    for (String vtn : Arrays.asList(array).subList(1, array.length)) {

                        String[] split = vtn.split("/");
                        Point3D vertex = vertices.get(-1 + Integer.parseInt(split[0]));
                        Point3D texture = split[1].isEmpty() ? null : textures.get(-1 + Integer.parseInt(split[1]));
                        Point3D normal = (split.length < 2 || split[2].isEmpty()) ? null : normals.get(-1 + Integer.parseInt(split[2]));
                        face.add(new FaceNode(vertex, texture, normal));
                    }
                    faces.add(face);
                    break;
                case "v":
                    // vertex
                    vertices.add(
                            new Point3D(
                                    Double.parseDouble(array[1]),
                                    Double.parseDouble(array[2]),
                                    Double.parseDouble(array[3])));
                    break;
                case "vn":
                    // vertex normal
                    normals.add(
                            new Point3D(
                                    Double.parseDouble(array[1]),
                                    Double.parseDouble(array[2]),
                                    Double.parseDouble(array[3])));
                    break;
                case "vt":
                    // vertex texture
                    textures.add(
                            new Point3D(
                                    Double.parseDouble(array[1]),
                                    Double.parseDouble(array[2]),
                                    Double.parseDouble(array[3])));
                    break;
                default:
                    throw new IOException("sanitizer does not understand \"" + array[0] + "\". in line " + (lineNumber + 1));
                }
            }

        }
        return objects;
    }

}
