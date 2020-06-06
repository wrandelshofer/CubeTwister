package ch.randelshofer.geom3d;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.randelshofer.geom3d.Geom2D.subtract;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class PolygonCleaner {
    public <X> void removeCollinearPoints3D(List<Point3D> poly) {
        removeCollinearPoints3D(poly, Function.identity());
    }

    public <X> void removeCollinearPoints3D(List<X> poly, Function<X, Point3D> fv) {
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point3D v0 = fv.apply(poly.get(i));
            Point3D v1 = fv.apply(poly.get((i + 1) % n));
            Point3D v2 = fv.apply(poly.get((i + 2) % n));
            if (Point3D.areCollinear(v0, v1, v2, 1e-6)) {
                poly.remove((i + 1) % n);
                i--;
                n--;
            }
        }
    }

    public <X> void removeCollinearPoints2D(List<Point2D.Double> poly) {
        removeCollinearPoints2D(poly, Function.identity());
    }

    public <X> void removeCollinearPoints2D(List<X> poly, Function<X, Point2D.Double> f) {
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point2D.Double v0 = f.apply(poly.get(i));
            Point2D.Double v1 = f.apply(poly.get((i + 1) % n));
            Point2D.Double v2 = f.apply(poly.get(i + 2 % n));
            Point2D.Double left = new Point2D.Double(v1.x - v0.x, v1.y - v0.y);
            Point2D.Double right = new Point2D.Double(v2.x - v0.x, v2.y - v0.y);
            if (abs(Triangulate.kross(left, right)) <= 1e-6) {
                poly.remove((i + 1 % n));
                ;
                i--;
                n--;
            }
        }
    }

    /**
     * @param poly a polygon with
     * @param fv   gets a vertex from a node
     * @param fn   gets a normal from a node
     * @param <X>  the node type of the polygon
     */
    public <X> boolean fixZeroNormals(List<X> poly, Function<X, Point3D> fv, Function<X, Point3D> fn, BiFunction<X, Point3D, X> withNormal, BiFunction<X, Point3D, X> withVertex) {
        boolean hasZeroNormal = hasZeroNormals(poly, fv, fn);
        if (hasZeroNormal) {
            Point3D sphereCenter = isOnSphere(poly, fv, fn);
            Point3D planeNormal = isOnPlane(poly, fv, fn);
            Point3D faceNormal = computeFaceNormal(poly, fv);

            if (sphereCenter != null) {
                // The face may point inwards or outwards of the sphere
                boolean outward = fv.apply(poly.get(0)).subtract(sphereCenter).dot(faceNormal) > 0;
                double factor = outward ? 1.0 : -1.0;

                for (int i = 0, n = poly.size(); i < n; i++) {
                    X x = poly.get(i);
                    if (fn.apply(x).squaredLength() == 0) {
                        poly.set(i, withNormal.apply(x, fv.apply(x).subtract(sphereCenter).multiply(factor).normalized()));
                    }
                }
            } else {
                for (int i = 0, n = poly.size(); i < n; i++) {
                    X x = poly.get(i);
                    if (fn.apply(x).squaredLength() == 0) {
                        poly.set(i, withNormal.apply(x, faceNormal));
                    }
                }
            }
        }
        return hasZeroNormal;
    }

    private <X> Point3D computeFaceNormal(List<X> poly, Function<X, Point3D> fv) {
        int i = getExtremalPoint(poly, fv);
        int n = poly.size();
        Point3D p0 = fv.apply(poly.get(i));
        Point3D pP = fv.apply(poly.get((i + 1) % n));
        Point3D pM = fv.apply(poly.get((i - 1 + n) % n));
        return pP.subtract(p0).cross(pM.subtract(p0)).normalized();
    }

    private <X> Point3D computeFaceTangent(List<X> poly, Function<X, Point3D> fv) {
        int i = getExtremalPoint(poly, fv);
        int n = poly.size();
        Point3D p0 = fv.apply(poly.get(i));
        Point3D pP = fv.apply(poly.get((i + 1) % n));
        return pP.subtract(p0);
    }

    private <X> boolean hasZeroNormals(List<X> poly, Function<X, Point3D> fv, Function<X, Point3D> fn) {
        boolean hasZeroNormal = false;
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point3D v0 = fv.apply(poly.get(i));
            Point3D vn0 = fn.apply(poly.get(i));
            if (vn0.squaredLength() == 0) {
                hasZeroNormal = true;
                break;
            }
        }
        return hasZeroNormal;
    }

    /**
     * The face is on a sphere if non-zero normals intersect at the same point.
     *
     * @param poly a polygon
     * @param fv   vertex function
     * @param fn   normal function
     * @param <X>  vertex type
     * @return the center of the sphere or null
     */
    public <X> Point3D isOnSphere(List<X> poly, Function<X, Point3D> fv, Function<X, Point3D> fn) {


        // Check if all non-zero normals meet at the same point
        Point3D center = null;
        for (int i = 0, n = poly.size(); i < n; i++) {
            int j = (i + 1) % n;
            Point3D o1 = fv.apply(poly.get(i));
            Point3D d1 = fn.apply(poly.get(i));
            Point3D o2 = fv.apply(poly.get(j));
            Point3D d2 = fn.apply(poly.get(j));

            if (d1.squaredLength() != 0 && d2.squaredLength() != 0) {
                Point3D intersection = Intersections.intersectNormals(o1, d1, o2, d2);
                if (intersection == null) {
                    return null;
                }
                if (center == null) {
                    center = intersection;
                } else {
                    if (center.subtract(intersection).squaredLength() > 1e-2) {
                        // return null;
                    }
                }
            }
        }
/*
        // Check if all points have the same distance from the center
        if (center!=null) {
            double distance = 0;
            for (int i = 0, n = poly.size(); i < n; i++) {
                Point3D o1 = fv.apply(poly.get(i));
                if (i == 0)
                    distance = o1.subtract(center).squaredLength();
                else if (abs(distance - o1.subtract(center).squaredLength()) > 100) {
                    return null;
                }
            }
        }*/


        return center;
    }

    /**
     * The face is on a plane if non-zero normals are equal.
     *
     * @param poly a polygon
     * @param fv   vertex function
     * @param fn   normal function
     * @param <X>  vertex type
     * @return the face normal or null
     */
    public <X> Point3D isOnPlane(List<X> poly, Function<X, Point3D> fv, Function<X, Point3D> fn) {
        // Check if all non-zero normals are equal
        Set<Point3D> normals = new LinkedHashSet<>();
        for (X x : poly) {
            Point3D normal = fn.apply(x);
            double squaredLength = normal.squaredLength();
            if (!Double.isNaN(squaredLength) && squaredLength != 0) {
                normals.add(normal);
            }
        }
        return normals.size() == 1 ? normals.iterator().next() : null;
    }

    /**
     * Gets the index of an extremal point of the polygon.
     *
     * @param poly a polygon
     * @param fv   to vertex function
     * @param <X>  the vertex type
     * @return an extremal point
     */
    public <X> int getExtremalPoint(List<X> poly, Function<X, Point3D> fv) {
        double minx = Double.POSITIVE_INFINITY, maxx = Double.NEGATIVE_INFINITY,
                miny = Double.POSITIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY,
                minz = Double.POSITIVE_INFINITY, maxz = Double.NEGATIVE_INFINITY;
        int ix = 0, iy = 0, iz = 0;
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point3D p = fv.apply(poly.get(i));
            maxx = max(p.x, maxx);
            maxy = max(p.y, maxy);
            maxz = max(p.z, maxz);
            if (p.x < minx) {
                minx = p.x;
                ix = i;
            }
            if (p.y < miny) {
                miny = p.y;
                iy = i;
            }
            if (p.z < minz) {
                minz = p.z;
                iz = i;
            }
        }
        double exx = maxx - minx, exy = maxy - miny, exz = maxz - minz;
        if (exx > exy) {
            if (exx > exz) {
                // x is the plane with most extent
                return ix;
            }
        } else {
            if (exy > exz) {
                // y is the plane with most extent
                return iy;
            }
        }
        // z is the plane with most extent
        return iz;
    }

    /**
     * Gets the index of an extremal point of the polygon.
     *
     * @param poly a polygon
     * @param fv   to vertex function
     * @param <X>  the vertex type
     * @return an extremal point
     */
    public <X> int getExtremalPoint2D(List<X> poly, Function<X, Point2D.Double> fv) {
        double minx = Double.POSITIVE_INFINITY, maxx = Double.NEGATIVE_INFINITY,
                miny = Double.POSITIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;
        int ix = 0, iy = 0, iz = 0;
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point2D p = fv.apply(poly.get(i));
            double x = p.getX();
            double y = p.getY();
            maxx = max(x, maxx);
            maxy = max(y, maxy);
            if (x < minx) {
                minx = x;
                ix = i;
            }
            if (y < miny) {
                miny = y;
                iy = i;
            }
        }
        double exx = maxx - minx, exy = maxy - miny;
        if (exx > exy) {
            // x is the plane with most extent
            return ix;
        } else {
            // y is the plane with most extent
            return iy;
        }
    }

    /**
     * Returns true if all points of the polygon are on its convex hull.
     *
     * @param poly
     * @param fv
     * @param <X>
     * @return
     */
    public <X> boolean isConvex(List<X> poly, Function<X, Point2D.Double> fv) {
        if (poly.size() <= 3) {
            return true;
        }
        List<Point2D.Double> points = poly.stream().map(fv::apply).collect(Collectors.toList());
        List<Point2D.Double> convexHull = ConvexHull.getConvexHull2D(points);
        return points.size() == convexHull.size();
    }

    /**
     * Gets the extent of the polygon.
     *
     * @param poly a polygon
     * @param fv   to vertex function
     * @param <X>  the vertex type
     * @return the extent in 3D space
     */
    public <X> Point3D getExtent(List<X> poly, Function<X, Point3D> fv) {
        double minx = Double.POSITIVE_INFINITY, maxx = Double.NEGATIVE_INFINITY,
                miny = Double.POSITIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY,
                minz = Double.POSITIVE_INFINITY, maxz = Double.NEGATIVE_INFINITY;
        int ix = 0, iy = 0, iz = 0;
        for (int i = 0, n = poly.size(); i < n; i++) {
            Point3D p = fv.apply(poly.get(i));
            maxx = max(p.x, maxx);
            maxy = max(p.y, maxy);
            maxz = max(p.z, maxz);
            minx = min(p.x, minx);
            miny = min(p.y, miny);
            minz = min(p.z, minz);
        }
        return new Point3D(maxx - minx, maxy - miny, maxz - minz);
    }

    public <X> List<Point2D.Double> projectIntoPlane(List<X> poly, Function<X, Point3D> fv) {
        Function<X, Point2D.Double> mappingTo2D = getPlaneProjection(poly, fv);
        List<Point2D.Double> list = new ArrayList<>();
        for (X x : poly) {
            list.add(mappingTo2D.apply(x));
        }
        System.out.println(
                list.stream().map(p -> p.getX() + "," + p.getY()).collect(Collectors.joining(" ")));
        return list;
    }

    /**
     * Computes a mapping of the polygon to 2D space.
     * <p>
     * We map the polygon to its plane.
     *
     * @param poly a polygon
     * @param fv   to vertex function
     * @param <X>  the vertex type
     * @return the mapping function
     */
    public <X> Function<X, Point2D.Double> getPlaneProjection(List<X> poly, Function<X, Point3D> fv) {
        int ie = getExtremalPoint(poly, fv);// index of extremal point
        int n = poly.size();
        Point3D eM = fv.apply(poly.get((ie + n - 1) % n));// point before extremal point
        Point3D e = fv.apply(poly.get(ie));// extremal point
        Point3D eP = fv.apply(poly.get((ie + 1) % n));// point after extremal point
        Point3D left = eM.subtract(e);
        Point3D right = eP.subtract(e);
        Point3D normal = right.cross(left).normalized();
        Point3D xaxis = normal.cross(left).normalized();
        Point3D yaxis = normal.cross(xaxis).normalized();

        return x -> {
            Point3D p = fv.apply(x).subtract(e);
            return new Point2D.Double(xaxis.dot(p), yaxis.dot(p));
        };
    }

    public boolean isClockwise(List<Point2D.Double> polygon) {
        int e = getExtremalPoint2D(polygon, Function.identity());
        int n = polygon.size();
        Point2D.Double left = subtract(polygon.get((e - 1 + n) % n), polygon.get(e));
        Point2D.Double right = subtract(polygon.get((e + 1) % n), polygon.get(e));
        return Triangulate.kross(left, right) > 0;
    }


}
