package creators;

import com.badlogic.gdx.math.Vector3;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import representation.Face;
import representation.Polyhedron;

/**
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@UtilityClass
public class Tetrahedron {
    // p1, p2, p3 form the bottom.
    // p4 is above them
    // The front wall view is p2, p4, p3
    // p1 is behind.
    Vector3 p1, p2, p3, p4;
    Vector3 center;
    Face bottom, left, right, front;

    public Polyhedron create(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4) {
        log.traceEntry("({}, {}, {}, {})", v1, v2, v3, v4);
        initVertices(v1, v2, v3, v4);
        initFaces();
        return createTetrahedron();
    }

    private void initVertices(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4) {
        log.traceEntry("({}, {}, {}, {})", v1, v2, v3, v4);
        p1 = v1;
        p2 = v2;
        p3 = v3;
        p4 = v4;
        center = v1.cpy().add(v2).add(v3).add(v4).scl(0.25f);
    }

    private void initFaces() {
        log.traceEntry("()");
        bottom = createTriangle(p1, p2, p3);
        left = createTriangle(p1, p3, p4);
        right = createTriangle(p1, p4, p2);
        front = createTriangle(p4, p3, p2);
    }

    private Face createTriangle(Vector3 v1, Vector3 v2, Vector3 v3) {
        log.traceEntry("({}, {}, {})", v1, v2, v3);
        Face triangle = new Face();
        triangle.addVertex(v1);
        triangle.addVertex(v2);
        triangle.addVertex(v3);
        triangle.rewind(center);
        return triangle;
    }

    private Polyhedron createTetrahedron() {
        log.traceEntry("()");
        Polyhedron tetrahedron = new Polyhedron();
        tetrahedron.addFace(bottom);
        tetrahedron.addFace(left);
        tetrahedron.addFace(right);
        tetrahedron.addFace(front);
        return tetrahedron;
    }
}
