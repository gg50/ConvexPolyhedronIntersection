package representation;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

/**
 * Represents a convex polygon, visible from one side.
 * On that side, the vertices are indexed counter-clockwise.
 *
 * @version 1.0
 */
@EqualsAndHashCode(of = "vertices")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@ToString(of = "vertices")
public final class Face {
    final Array<Vector3> vertices = new Array<>(6);
    /** The start and end of the edge that's currently selected for clipping. */
    Vector3 startPoint, endPoint;
    /** The intersection point between the edge and face currently selected for clipping. */
    Vector3 intersection;
    /** Whether the start and end of the edge that's currently selected for clipping are inside the clipping face. */
    boolean startIsInsideClippingFace, endIsInsideClippingFace;

    public void addVertex(Vector3 vertex) {
        log.traceEntry("({})", vertex);
        if (!vertices.contains(vertex, false)) {
            vertices.add(vertex);
        }
    }

    private int getNumberOfEdges() {
        return vertices.size;
    }

    private Vector3 getStartPoint(int edgeIndex) {
        log.traceEntry("({})", edgeIndex);
        return vertices.get(edgeIndex);
    }

    private Vector3 getEndOfEdge(int edgeIndex) {
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

    private boolean pointIsInsideFace(Vector3 point) {
        log.traceEntry("({})", point);
        float determinant = getPointVsFaceDeterminant(point);
        return determinant <= 0;  // <= because we define on the face to be "inside the face"
    }

    public void rewind(Vector3 internalPoint) {
        log.traceEntry("({})", internalPoint);
        boolean isInside = pointIsInsideFace(internalPoint);
        if (!isInside) {
            log.debug("internalPoint {} was not inside the face. Reversing array.", internalPoint);
            log.debug("before = {}", vertices);
            vertices.reverse();
            log.debug("after = {}", vertices);
        }
    }

    private Vector3 getIntersectionPoint(Vector3 p1, Vector3 p2) {
        log.traceEntry("({}, {})", p1, p2);
        float determinant1 = getPointVsFaceDeterminant(p1);
        float determinant2 = getPointVsFaceDeterminant(p2);
        log.trace("determinant1 = {}", determinant1);
        log.trace("determinant2 = {}", determinant2);
        boolean equal = Float.compare(determinant1, determinant2) == 0;
        if (equal) {
            log.trace("determinants are the same");
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
            setStartAndEndOfEdge(i);
            setIntersectionWith(clippingFace);
            extendFace(workingFace);
        }
        return workingFace.sanityCheckFace();
    }

    private void setStartAndEndOfEdge(int i) {
        log.traceEntry("({})", i);
        startPoint = getStartPoint(i);
        endPoint = getEndOfEdge(i);
        log.debug("Start {};  End {}", startPoint, endPoint);
    }

    /**
     * Calculates the {@link #intersection} point of
     * the currently selected edge of this {@link Face} and <br/>
     * the given face.
     */
    private void setIntersectionWith(Face face) {
        log.traceEntry("({})", face);
        intersection = face.getIntersectionPoint(startPoint, endPoint);
        log.debug("Intersection: {}", intersection);
        startIsInsideClippingFace = face.pointIsInsideFace(startPoint);
        log.trace("startIsInsideClippingFace = {}", startIsInsideClippingFace);
        endIsInsideClippingFace = face.pointIsInsideFace(endPoint);
        log.trace("endIsInsideClippingFace = {}", endIsInsideClippingFace);
    }

    /**
     * Extends the given {@link Face} with the
     * current {@link #intersection} point and/or {@link #endPoint} of the currently selected edge.
     */
    private void extendFace(Face face) {
        log.traceEntry("({})", face);
        if (!startIsInsideClippingFace && endIsInsideClippingFace) {
            log.debug("Only end is inside the clipping face, adding intersection and end.");
            // This represents the edge going into and behind the face.
            // Thus we add the first point of this edge that is inside the face (the intersection),
            // and then the last point of this poin that is inside the face (the end point).
            face.addVertex(intersection);
            face.addVertex(endPoint);
        }
        if (startIsInsideClippingFace && endIsInsideClippingFace) {
            log.debug("Both inside, adding endPoint.");
            // The previous edge has entered the face.
            // This edge continues to be entirely in the face.
            // Since this edge's start point is already added (as the previous edge's end),
            // we need to add only the end of this edge.
            face.addVertex(endPoint);
        }
        if (startIsInsideClippingFace && !endIsInsideClippingFace) {
            log.debug("Only start is inside the clipping face, adding intersection.");
            // The previous edge ended in the face.
            // This edge leaves the face.
            // Since this edge's start point is already added (as the previous edge's end),
            // we need to add only the intersection of this edge with the face before it leave the face.
            face.addVertex(intersection);
        }
        if (!startIsInsideClippingFace && !endIsInsideClippingFace) {
            log.debug("Edge is outside the clipping face, not adding any vertices.");
        }
    }

    private Face sanityCheckFace() {
        log.traceEntry("()");
        int numberOfVertices = vertices.size;
        return numberOfVertices > 2 ? this : null;
    }
}
