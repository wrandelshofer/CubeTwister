package ch.randelshofer.aliaswavefront3d;

import ch.randelshofer.geom3d.FaceNode;
import ch.randelshofer.geom3d.Point3D;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AliasWavefront3DWriter implements Closeable {
    public final static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#################0.#################", new DecimalFormatSymbols(Locale.ENGLISH));
    public final static DecimalFormat FLOAT_FORMAT = new DecimalFormat("#################0.######", new DecimalFormatSymbols(Locale.ENGLISH));
    private DecimalFormat numberFormat = DOUBLE_FORMAT;
    private boolean coalesceVertices = true;
    private boolean relativeIndices = false;
    /**
     * Whether to share coordinates between objects.
     */
    private boolean shareCoordinates = false;

    private final Writer w;

    public AliasWavefront3DWriter(Writer w) {
        this.w = w;
    }

    @Override
    public void close() throws IOException {
        w.close();
    }


    private String createVertexNormalKey(FaceNode node) {
        Point3D v = node.vertex;
        Point3D n = node.normal;
        return (coalesceVertices || n == null) ?
                num(v.x) + "," + num(v.y) + "," + num(v.z)
                : num(v.x) + "," + num(v.y) + "," + num(v.z) + " " + num(n.x) + "," + num(n.y) + "," + num(n.z);


    }

    private String createNormalKey(FaceNode node) {
        Point3D n = node.normal;
        return n == null ? "" : num(n.x) + "," + num(n.y) + "," + num(n.z);
    }

    private String createTextureKey(FaceNode node) {
        Point3D n = node.texture;
        return n == null ? "" : num(n.x) + "," + num(n.y) + "," + num(n.z);
    }

    public DecimalFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(DecimalFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    private String num(double value) {
        return numberFormat.format(value == -0.0 ? 0.0 : value);
    }


    public void write(Map<String, List<List<FaceNode>>> objects) throws IOException {

        Map<String, Integer> vertices = new LinkedHashMap<>();
        Map<String, Integer> normals = new LinkedHashMap<>();
        Map<String, Integer> textures = new LinkedHashMap<>();
        AtomicInteger vertexCounter = new AtomicInteger();
        AtomicInteger normalCounter = new AtomicInteger();
        AtomicInteger textureCounter = new AtomicInteger();

        boolean first = true;
        w.write("# WaveFront *.obj file\n\n");
        for (Map.Entry<String, List<List<FaceNode>>> objEntry : objects.entrySet()) {
            if (first) {
                first = false;
            } else {
                w.write("\n");
                if (!shareCoordinates) {
                    vertices.clear();
                    normals.clear();
                    textures.clear();
                }
            }
            String name = objEntry.getKey();
            List<List<FaceNode>> faces = objEntry.getValue();
            writeObject(w, vertices, normals, textures, name, faces, vertexCounter, normalCounter, textureCounter);
        }
    }

    public void writeObject(Writer w, Map<String, Integer> vertices, Map<String, Integer> normals, Map<String, Integer> textures,
                            String name, List<List<FaceNode>> faces, AtomicInteger vertexCounter,
                            AtomicInteger normalCounter,
                            AtomicInteger textureCounter) throws IOException {
        w.write("o ");
        w.write(name);
        w.write("\n");

        int oldSize = vertices.size();
        for (List<FaceNode> face : faces) {
            for (FaceNode vtn : face) {
                Point3D vertex = vtn.vertex;
                String key = createVertexNormalKey(vtn);
                if (!vertices.containsKey(key)) {
                    vertices.putIfAbsent(key, vertexCounter.incrementAndGet());
                    w.write("v " + num(vertex.x) + " " + num(vertex.y) + " " + num(vertex.z) + "\n");
                }
            }
        }
        if (vertices.size() > oldSize) {
            w.write("# " + (vertices.size() - oldSize) + " vertices\n\n");
        }

        oldSize = normals.size();
        for (List<FaceNode> face : faces) {
            for (FaceNode vtn : face) {
                Point3D normal = vtn.normal;
                String key = createNormalKey(vtn);
                if (normal != null && !normals.containsKey(key)) {
                    normals.putIfAbsent(key, normalCounter.incrementAndGet());
                    w.write("vn " + num(normal.x) + " " + num(normal.y) + " " + num(normal.z) + "\n");
                }
            }
        }
        if (normals.size() > oldSize) {
            w.write("# " + (normals.size() - oldSize) + " normals\n\n");
        }

        oldSize = textures.size();
        for (List<FaceNode> face : faces) {
            for (FaceNode vtn : face) {
                Point3D texture = vtn.texture;
                String key = createTextureKey(vtn);
                if (texture != null && !textures.containsKey(key)) {
                    textures.putIfAbsent(key, textureCounter.incrementAndGet());
                    w.write("vt " + num(texture.x) + " " + num(texture.y) + " " + num(texture.z) + "\n");
                }
            }
        }

        if (textures.size() > oldSize) {
            w.write("# " + (textures.size() - oldSize) + " texture coordinates\n\n");
        }

        for (List<FaceNode> face : faces) {
            w.write("f");
            for (FaceNode vtn : face) {
                Object vertexNormalKey = createVertexNormalKey(vtn);
                Object normalKey = createNormalKey(vtn);
                Object textureKey = createTextureKey(vtn);
                Integer vertex = vertices.get(vertexNormalKey);
                Integer texture = textures.get(textureKey);
                Integer normal = normals.get(normalKey);
                if (relativeIndices) {
                    w.write(" " + (vertex - vertexCounter.get() - 1)
                            + "/" + (texture == null ? "" : (texture - textureCounter.get() - 1))
                            + "/" + (normal == null ? "" : (normal - normalCounter.get() - 1)));
                } else {
                    w.write(" " + vertex
                            + "/" + (texture == null ? "" : texture)
                            + "/" + (normal == null ? "" : normal));
                }
            }
            w.write("\n");
        }
        w.write("# " + faces.size() + " faces\n");
    }

    public boolean isCoalesceVertices() {
        return coalesceVertices;
    }

    public void setCoalesceVertices(boolean coalesceVertices) {
        this.coalesceVertices = coalesceVertices;
    }

    public boolean isRelativeIndices() {
        return relativeIndices;
    }

    public void setRelativeIndices(boolean relativeIndices) {
        this.relativeIndices = relativeIndices;
    }

    public boolean isShareCoordinates() {
        return shareCoordinates;
    }

    public void setShareCoordinates(boolean shareCoordinates) {
        this.shareCoordinates = shareCoordinates;
    }
}
