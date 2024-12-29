/*
 * @(#)idx3d_SceneToFaceNodes.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

import idx3d.idx3d_Group;
import idx3d.idx3d_Matrix;
import idx3d.idx3d_Node;
import idx3d.idx3d_Object;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Triangle;
import idx3d.idx3d_Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class idx3d_SceneToFaceNodes {

    public Map<String, List<List<FaceNode>>> toFaceNodes(idx3d_Scene scene) {
        LinkedHashMap<String, List<List<FaceNode>>> objects = new LinkedHashMap<>();
        toFaceNodes(objects, scene, new idx3d_Matrix(), new idx3d_Matrix(), 0);
        return objects;
    }

    float[] allZeros = new float[3];

    private void toFaceNodes(LinkedHashMap<String, List<List<FaceNode>>> objects, idx3d_Node node, idx3d_Matrix matrix, idx3d_Matrix normalMatrix, int depth) {
        if (node instanceof idx3d_Scene) {

        } else {
            System.out.println(
                    ".".repeat(depth) + "node " + node.getClass() + " " + (node instanceof idx3d_Object ? ((idx3d_Object) node).name + " " + ((idx3d_Object) node).triangles : ""));
            //System.out.println("  .matrix " + node.matrix);
        }

        idx3d_Matrix childMatrix = matrix;
        idx3d_Matrix childNormalMatrix = normalMatrix;

        if (node instanceof idx3d_Object) {
            idx3d_Object obj = (idx3d_Object) node;
            List<List<FaceNode>> faces = new ArrayList<>();
            for (idx3d_Triangle triangle : obj.triangleData) {
                List<FaceNode> face = new ArrayList<>();
                float[] u = triangle.u;
                float[] v = triangle.v;

                if (triangle.p1.equals(triangle.p2) || triangle.p1.equals(triangle.p3) || triangle.p2.equals(triangle.p3)) {
                    System.err.println("degenerated triangle! " + triangle.p1 + " " + triangle.p2 + " " + triangle.p3);
                    continue;
                }

                idx3d_Vector p = triangle.p1.pos.transform(matrix);
                idx3d_Vector n = triangle.p1.n.transform(normalMatrix);
                boolean noTexture = Arrays.equals(allZeros, u) && Arrays.equals(allZeros, v);

                face.add(new FaceNode(new Point3D(p.x, p.y, -p.z), noTexture ? null : new Point3D(u[0], 1 - v[0], 0), new Point3D(-n.x, -n.y, n.z)));
                p = triangle.p2.pos.transform(matrix);
                n = triangle.p2.n.transform(normalMatrix);
                face.add(new FaceNode(new Point3D(p.x, p.y, -p.z), noTexture ? null : new Point3D(u[1], 1 - v[1], 0), new Point3D(-n.x, -n.y, n.z)));
                p = triangle.p3.pos.transform(matrix);
                n = triangle.p3.n.transform(normalMatrix);
                face.add(new FaceNode(new Point3D(p.x, p.y, -p.z), noTexture ? null : new Point3D(u[2], 1 - v[2], 0), new Point3D(-n.x, -n.y, n.z)));
                faces.add(face);
            }
            String id = obj.name == null || obj.name.isEmpty() ? "o" + System.identityHashCode(obj) : obj.name;
            objects.put(id, faces);
        } else if (node instanceof idx3d_Group) {
            idx3d_Group group = (idx3d_Group) node;
            idx3d_Matrix mx = group.matrix;
            childMatrix = new idx3d_Matrix();
            childMatrix.set(matrix);
            childMatrix.preTransform(mx);
            idx3d_Matrix nmx = group.normalmatrix;
            childNormalMatrix = new idx3d_Matrix();
            childNormalMatrix.set(normalMatrix);
            childNormalMatrix.preTransform(nmx);
        }


        for (idx3d_Node child : node.children()) {
            toFaceNodes(objects, child, childMatrix, childNormalMatrix, depth + 1);
        }
    }
}
