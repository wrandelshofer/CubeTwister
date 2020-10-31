/*
 * @(#)Icosahedron.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

/**
 * Creates a polygon mesh of an icosahedron.
 * <p>
 * An icosahedron with an edge length of {@code 2} can be constructed from
 * 3 orthogonal golden rectangles.
 * <pre>
 *     (0, ±1, ±φ)
 *     (±1, ±φ, 0)
 *     (±φ, 0, ±1)
 *
 * where
 *     φ = ( 1 + sqrt(5) ) / 2   is the golden ratio
 * </pre>
 * Formulas:
 * <pre>
 *     Given edge length 'a'.
 *
 *     circumscribed sphere = a/4 * sqrt( 10 + 2 * sqrt(5) )
 *     inscribed sphere = a/12 * sqrt(3) * ( 3 + sqrt(5) )
 *     midsphere = a/4 * (1 + sqrt(5) )
 * </pre>
 */
public class Icosahedron {
    public List<List<FaceNode>> create(double radius) {
        double a = 4 / sqrt(10 + 2 * sqrt(5));
        double ah = a / 2;
        double phi = (1 + sqrt(5)) * ah / 2;

        List<Point3D> normals = new ArrayList();
        List<List<Integer>> faces = new ArrayList<>();

        normals.add(new Point3D(0, ah, phi));
        normals.add(new Point3D(0, -ah, phi));
        normals.add(new Point3D(phi, 0, ah));
        normals.add(new Point3D(phi, 0, -ah));

        normals.add(new Point3D(0, ah, -phi));
        normals.add(new Point3D(0, -ah, -phi));
        normals.add(new Point3D(-phi, 0, -ah));
        normals.add(new Point3D(-phi, 0, ah));

        normals.add(new Point3D(ah, phi, 0));
        normals.add(new Point3D(-ah, phi, 0));
        normals.add(new Point3D(-ah, -phi, 0));
        normals.add(new Point3D(ah, -phi, 0));

        faces.add(Arrays.asList(0, 8, 9));
        faces.add(Arrays.asList(0, 2, 8));
        faces.add(Arrays.asList(2, 3, 8));
        faces.add(Arrays.asList(3, 4, 8));
        faces.add(Arrays.asList(4, 9, 8));
        faces.add(Arrays.asList(6, 9, 4));
        faces.add(Arrays.asList(7, 9, 6));
        faces.add(Arrays.asList(7, 0, 9));
        faces.add(Arrays.asList(1, 10, 11));
        faces.add(Arrays.asList(1, 11, 2));
        faces.add(Arrays.asList(11, 3, 2));
        faces.add(Arrays.asList(11, 5, 3));
        faces.add(Arrays.asList(11, 10, 5));
        faces.add(Arrays.asList(10, 6, 5));
        faces.add(Arrays.asList(10, 7, 6));
        faces.add(Arrays.asList(10, 1, 7));
        faces.add(Arrays.asList(0, 7, 1));
        faces.add(Arrays.asList(0, 1, 2));
        faces.add(Arrays.asList(3, 5, 4));
        faces.add(Arrays.asList(5, 6, 4));

        List<List<FaceNode>> faceNodes = new ArrayList<>();
        for (List<Integer> face : faces) {
            List<FaceNode> fa = new ArrayList<>(face.size());
            for (Integer index : face) {
                Point3D normal = normals.get(index);
                assert normal.squaredLength() == 1.0 : "bad normal " + normal;
                fa.add(n(radius, normal));
            }
            faceNodes.add(fa);
        }
        return faceNodes;
    }

    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    public List<List<FaceNode>> create(double radius, int subdivide) {
        List<List<FaceNode>> faces = create(radius);
        TriConsumer<Double, List<FaceNode>, List<List<FaceNode>>> f = null;
        while (subdivide > 1) {
            if (subdivide % 2 == 0) {
                subdivide /= 2;
                f = this::subdivideFace2;
            } else if (subdivide % 3 == 0) {
                subdivide /= 3;
                f = this::subdivideFace3;
            } else if (subdivide % 5 == 0) {
                subdivide /= 5;
                f = this::subdivideFace5;
            } else {
                break;
            }
            List<List<FaceNode>> subdivided = new ArrayList<>();
            for (List<FaceNode> face : faces) {
                f.accept(radius, face, subdivided);
            }
            faces = subdivided;
        }

        // Merge points that are almost identical
        mergeAlmostIdenticalFaceNodes(faces);


        return faces;
    }

    private void mergeAlmostIdenticalFaceNodes(List<List<FaceNode>> faces) {
        Map<Point3D, FaceNode> mergeMap = new LinkedHashMap<>();
        for (List<FaceNode> face : faces) {
            for (ListIterator<FaceNode> j = face.listIterator(); j.hasNext(); ) {
                FaceNode faceNode = j.next();
                Point3D v = faceNode.normal;
                Point3D approx = new Point3D(round(v.x * 100), round(v.y * 100), round(v.z * 100));
                mergeMap.putIfAbsent(approx, faceNode);
                FaceNode ff = mergeMap.get(approx);
                j.set(ff);
            }
        }
    }


    /**
     * Subdivides each edge into 2 edges.
     * <pre>
     *      a
     *      /\
     *     /  \
     *  ac ---- ab
     *    /\  /\
     *   /  \/  \
     *   --------
     * c    bc    b
     * </pre>
     *
     * @param radius     the radius of the sphere
     * @param face       the input face
     * @param subdivided the output faces
     */
    private void subdivideFace2(double radius, List<FaceNode> face, List<List<FaceNode>> subdivided) {
        Point3D a = face.get(0).normal;
        Point3D b = face.get(1).normal;
        Point3D c = face.get(2).normal;

        Point3D ab = a.add(b).normalized();
        Point3D bc = b.add(c).normalized();
        Point3D ac = a.add(c).normalized();

        subdivided.add(Arrays.asList(n(radius, a), n(radius, ab), n(radius, ac)));
        subdivided.add(Arrays.asList(n(radius, ab), n(radius, bc), n(radius, ac)));
        subdivided.add(Arrays.asList(n(radius, ab), n(radius, b), n(radius, bc)));
        subdivided.add(Arrays.asList(n(radius, bc), n(radius, c), n(radius, ac)));
    }

    /**
     * Subdivides each edge into 3 edges.
     * <pre>
     *         a
     *        /\
     *       /  \
     *  ac1  ---- ab1
     *      /\  /\
     *     /  \/  \
     * ac2 --abc--- ab2
     *    /\  /\  /\
     *   /  \/  \/  \
     *   ------------
     *  c  bc2 bc1   b
     * </pre>
     *
     * @param r          the radius of the sphere
     * @param face       the input face
     * @param subdivided the output faces
     */
    private void subdivideFace3(double r, List<FaceNode> face, List<List<FaceNode>> subdivided) {
        Point3D a = face.get(0).normal;
        Point3D b = face.get(1).normal;
        Point3D c = face.get(2).normal;

        Point3D ab1 = slerp(a, b, 1 / 3.0);
        Point3D ab2 = slerp(a, b, 2 / 3.0);
        Point3D bc1 = slerp(b, c, 1 / 3.0);
        Point3D bc2 = slerp(b, c, 2 / 3.0);
        Point3D ac1 = slerp(a, c, 1 / 3.0);
        Point3D ac2 = slerp(a, c, 2 / 3.0);
        Point3D abc = a.add(b).add(c).normalized();

        subdivided.add(Arrays.asList(n(r, ac2), n(r, bc2), n(r, c)));
        subdivided.add(Arrays.asList(n(r, ac2), n(r, abc), n(r, bc2)));
        subdivided.add(Arrays.asList(n(r, abc), n(r, bc1), n(r, bc2)));
        subdivided.add(Arrays.asList(n(r, abc), n(r, ab2), n(r, bc1)));
        subdivided.add(Arrays.asList(n(r, ab2), n(r, b), n(r, bc1)));
        subdivided.add(Arrays.asList(n(r, a), n(r, ab1), n(r, ac1)));
        subdivided.add(Arrays.asList(n(r, ab1), n(r, ab2), n(r, abc)));
        subdivided.add(Arrays.asList(n(r, ac1), n(r, ab1), n(r, abc)));
        subdivided.add(Arrays.asList(n(r, ac1), n(r, abc), n(r, ac2)
        ));
    }

    /**
     * Subdivides each edge into 5 edges.
     * <pre>
     *             a
     *            /\
     *           /  \
     *      ac1  ---- ab1
     *          /\  /\
     *         /  \/  \
     *     ac2 --abc--- ab2
     *        /\  /\  /\
     *       /  \/  \/  \
     *  ac3 ------------ ab3
     *      /\  /\  /\  /\
     *     /  \/  \/  \/  \
     * ac4 -bc43-bc42-bc41- ab4
     *    /\  /\  /\  /\  /\
     *   /  \/  \/  \/  \/  \
     *   --------------------
     *  c  bc4 bc3 bc2 bc1   b
     * </pre>
     *
     * @param r          the radius of the sphere
     * @param face       the input face
     * @param subdivided the output faces
     */
    private void subdivideFace5(double r, List<FaceNode> face, List<List<FaceNode>> subdivided) {
        Point3D a = face.get(0).normal;
        Point3D b = face.get(1).normal;
        Point3D c = face.get(2).normal;

        // We can subdivide the triangle a,ab4,ac4 by applying subdivideFace twice on it.
        Point3D ab4 = slerp(a, b, 4 / 5.0);
        Point3D ac4 = slerp(a, c, 4 / 5.0);
        List<List<FaceNode>> sub = new ArrayList<>();
        subdivideFace2(r, Arrays.asList(n(r, a), n(r, ab4), n(r, ac4)), sub);
        for (List<FaceNode> subFace : sub) {
            subdivideFace2(r, subFace, subdivided);
        }

        Point3D bc1 = slerp(b, c, 1 / 5.0);
        Point3D bc2 = slerp(b, c, 2 / 5.0);
        Point3D bc3 = slerp(b, c, 3 / 5.0);
        Point3D bc4 = slerp(b, c, 4 / 5.0);
        Point3D bc41 = slerp(ab4, ac4, 1 / 4.0);
        Point3D bc42 = slerp(ab4, ac4, 2 / 4.0);
        Point3D bc43 = slerp(ab4, ac4, 3 / 4.0);
        subdivided.add(Arrays.asList(n(r, ac4), n(r, bc4), n(r, c)));
        subdivided.add(Arrays.asList(n(r, ac4), n(r, bc43), n(r, bc4)));
        subdivided.add(Arrays.asList(n(r, bc43), n(r, bc3), n(r, bc4)));
        subdivided.add(Arrays.asList(n(r, bc43), n(r, bc42), n(r, bc3)));
        subdivided.add(Arrays.asList(n(r, bc42), n(r, bc2), n(r, bc3)));
        subdivided.add(Arrays.asList(n(r, bc42), n(r, bc41), n(r, bc2)));
        subdivided.add(Arrays.asList(n(r, bc41), n(r, bc1), n(r, bc2)));
        subdivided.add(Arrays.asList(n(r, bc41), n(r, ab4), n(r, bc1)));
        subdivided.add(Arrays.asList(n(r, ab4), n(r, b), n(r, bc1)));


    }

    private boolean linear = false;

    @Nonnull
    private Point3D slerp(Point3D a, Point3D b, double t) {
        // linear interpolation
        if (linear) {
            return b.subtract(a).multiply(t).add(a).normalized();
        }

        Quaternion q = Quaternion.ofTwoVectors(a, b).slerp(t);
        return q.transform(a);
    }

    /**
     * Creates a node on the sphere surface.
     *
     * @param radius the radius of the sphere
     * @param point  the point on the unit sphere
     * @return face node
     */
    @Nonnull
    private FaceNode n(double radius, Point3D point) {
        return new FaceNode(point.multiply(radius), null, point);
    }
}
