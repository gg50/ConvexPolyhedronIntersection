package main;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

/**
 * Represents a convex polygon, visible from one side.
 * On that side, the vertices are indexed counter-clockwise.
 *
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@ToString(of = {"vertices"})
public class Face {
    IntSet vertexHashes = new IntSet(3);
    Array<Vector3> vertices = new Array<>(3);

    public void addVertex(Vector3 vertex) {
        log.traceEntry("({})", vertex);
        int hash = vertex.hashCode();
        if (!vertexHashes.contains(hash)) {
            vertices.add(vertex);
            vertexHashes.add(hash);
        }
    }

    public int getNumberOfEdges() {
        return vertices.size;
    }

    public Vector3 getVertex(int index) {
        log.traceEntry("({})", index);
        return vertices.get(index);
    }

    public Vector3 getStartOfEdge(int edgeIndex) {
        log.traceEntry("({})", edgeIndex);
        return vertices.get(edgeIndex);
    }

    public Vector3 getEndOfEdge(int edgeIndex) {
        log.traceEntry("({})", edgeIndex);
        // Loop around to starting edge if needed.
        int index = (edgeIndex + 1) % vertices.size;
        return vertices.get(index);
    }

    private float getPointVsFaceDeterminant(Vector3 point) {
        log.traceEntry("({})", point);
        // Distance from plane to point.
        if (vertices.size < 3) {
            log.error("Face has less than 3 vertices. Cannot calculate determinant.");
            log.debug("this face = {}", this);
            log.debug("point = {}", point);
        }
        Vector3 a = vertices.get(0);
        Vector3 b = vertices.get(1);
        Vector3 c = vertices.get(2);
        Vector3 bDash = b.cpy().sub(point);
        Vector3 cDash = c.cpy().sub(point);
        Vector3 pDash = point.cpy().sub(a);
        return bDash.x * (cDash.y * pDash.z - cDash.z * pDash.y)
                - bDash.y * (cDash.x * pDash.z - cDash.z * pDash.x)
                + bDash.z * (cDash.x * pDash.y - cDash.y * pDash.x);
    }

    public boolean pointIsInsideFace(Vector3 point) {
        log.traceEntry("({})", point);
        float determinant = getPointVsFaceDeterminant(point);
        return determinant <= 0;  // <= because we define on the face to be "inside the face"
    }

    public void rewind(Vector3 internalPoint) {
        log.traceEntry("({})", internalPoint);
        boolean isInside = pointIsInsideFace(internalPoint);
        if (!isInside) {
            vertices.reverse();
        }
    }

    public Vector3 getIntersectionPoint(Vector3 p1, Vector3 p2) {
        log.traceEntry("({}, {})", p1, p2);
        float determinant1 = getPointVsFaceDeterminant(p1);
        float determinant2 = getPointVsFaceDeterminant(p2);
        boolean equal = Float.compare(determinant1, determinant2) == 0;
        if (equal) {
            return p1.cpy().add(p2).scl(0.5f);
        }
        Vector3 intersect = p2.cpy().sub(p1);
        float factor = -determinant1 / (determinant2 - determinant1);
        intersect.scl(factor);
        intersect.add(p1);
        return intersect;
    }

    public Face clipFace(Face clippingFace) {
        log.traceEntry("({})", clippingFace);
        Face workingFace = new Face();
        int numberOfEdges = getNumberOfEdges();
        for (int i = 0; i < numberOfEdges; i++) {
            Vector3 startPoint = getStartOfEdge(i);
            Vector3 endPoint = getEndOfEdge(i);
            boolean p1IsInsideClippingFace = clippingFace.pointIsInsideFace(startPoint);
            boolean p2IsInsideClippingFace = clippingFace.pointIsInsideFace(endPoint);
            if (p1IsInsideClippingFace && p2IsInsideClippingFace) {
                workingFace.addVertex(endPoint);
            } else if (p1IsInsideClippingFace) {
                Vector3 intersection = clippingFace.getIntersectionPoint(startPoint, endPoint);
                workingFace.addVertex(intersection);
            } else if (p2IsInsideClippingFace) {
                Vector3 intersection = clippingFace.getIntersectionPoint(startPoint, endPoint);
                workingFace.addVertex(intersection);
                workingFace.addVertex(endPoint);
            }
        }
        if (workingFace.getNumberOfEdges() > 2) {
            return workingFace;
        } else {
            return null;
        }
    }

    private int getNumberOfSegments() {
        log.traceEntry("()");
        return vertices.size - 2;
    }
}
