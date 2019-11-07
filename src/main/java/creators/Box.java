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
public class Box {
    Vector3 hhh, hhl, hlh, hll, lhh, lhl, llh, lll;
    Vector3 center;
    Face top, bottom, north, south, east, west;

    public Polyhedron create(Vector3 corner1, Vector3 corner2) {
        return create(corner1, corner2, new Vector3(0f, 0f, 0f));
    }

    public Polyhedron create(Vector3 corner1, Vector3 corner2, Vector3 skew) {
        log.traceEntry("({}, {}, {})", corner1, corner2, skew);
        initCorners(corner1, corner2, skew);
        initFaces();
        fillFaces();
        return createBox();
    }

    private void initCorners(Vector3 corner1, Vector3 corner2, Vector3 skew) {
        log.traceEntry("({}, {}, {})", corner1, corner2, skew);
        hhh = new Vector3(corner2.x + skew.x, corner2.y, corner2.z);
        hhl = new Vector3(corner2.x + skew.x, corner2.y, corner1.z);
        hlh = new Vector3(corner2.x - skew.x, corner1.y, corner2.z);
        hll = new Vector3(corner2.x - skew.x, corner1.y, corner1.z);
        lhh = new Vector3(corner1.x + skew.x, corner2.y, corner2.z);
        lhl = new Vector3(corner1.x + skew.x, corner2.y, corner1.z);
        llh = new Vector3(corner1.x - skew.x, corner1.y, corner2.z);
        lll = new Vector3(corner1.x - skew.x, corner1.y, corner1.z);
        center = corner1.cpy().add(corner2).scl(0.5f);
    }

    private void initFaces() {
        log.traceEntry("()");
        top = new Face();
        bottom = new Face();
        north = new Face();
        south = new Face();
        east = new Face();
        west = new Face();
    }

    private void fillFaces() {
        log.traceEntry("({})");
        fillFace(north, hll, hhl, hhh, hlh);
        fillFace(south, lll, lhl, lhh, llh);
        fillFace(top, hhh, hhl, lhl, lhh);
        fillFace(bottom, hlh, hll, lll, llh);
        fillFace(east, hhh, hlh, llh, lhh);
        fillFace(west, hhl, hll, lll, lhl);
    }

    private void fillFace(Face face, Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4) {
        log.traceEntry("({}, {}, {}, {}, {})", face, v1, v2, v3, v4);
        face.addVertex(v1);
        face.addVertex(v2);
        face.addVertex(v3);
        face.addVertex(v4);
        face.rewind(center);
    }

    private Polyhedron createBox() {
        log.traceEntry("()");
        Polyhedron box = new Polyhedron();
        box.addFace(top);
        box.addFace(bottom);
        box.addFace(north);
        box.addFace(south);
        box.addFace(east);
        box.addFace(west);
        return box;
    }
}
