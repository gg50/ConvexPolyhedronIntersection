package main;

import com.badlogic.gdx.math.Vector3;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

/**
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class CubeCreator {
    private static Vector3 hhh, hhl, hlh, hll, lhh, lhl, llh, lll;
    private static Vector3 center;
    private static Face top, bottom, north, south, east, west;

    public static Polyhedron createCube(Vector3 corner1, Vector3 corner2) {
        log.traceEntry("({}. {})", corner1, corner2);
        initCorners(corner1, corner2);
        initFaces();
        fillFaces();
        return createCube();
    }

    private static void initCorners(Vector3 corner1, Vector3 corner2) {
        hhh = new Vector3(corner2.x, corner2.y, corner2.z);
        hhl = new Vector3(corner2.x, corner2.y, corner1.z);
        hlh = new Vector3(corner2.x, corner1.y, corner2.z);
        hll = new Vector3(corner2.x, corner1.y, corner1.z);
        lhh = new Vector3(corner1.x, corner2.y, corner2.z);
        lhl = new Vector3(corner1.x, corner2.y, corner1.z);
        llh = new Vector3(corner1.x, corner1.y, corner2.z);
        lll = new Vector3(corner1.x, corner1.y, corner1.z);
        float averageX = 0.5f * (corner1.x + corner2.x);
        float averageY = 0.5f * (corner1.y + corner2.y);
        float averageZ = 0.5f * (corner1.z + corner2.z);
        center = new Vector3(averageX, averageY, averageZ);
    }

    private static void initFaces() {
        log.traceEntry("()");
        top = new Face();
        bottom = new Face();
        north = new Face();
        south = new Face();
        east = new Face();
        west = new Face();
    }

    private static void fillFaces() {
        log.traceEntry("({})");
        fillFace(north, hll, hhl, hhh, hlh);
        fillFace(south, lll, lhl, lhh, llh);
        fillFace(top, hhh, hhl, lhl, lhh);
        fillFace(bottom, hlh, hll, lll, llh);
        fillFace(east, hhh, hlh, llh, lhh);
        fillFace(west, hhl, hll, lll, lhl);
    }

    private static void fillFace(Face face, Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4) {
        log.traceEntry("({}, {}, {}, {}, {})", face, v1, v2, v3, v4);
        face.addVertex(v1);
        face.addVertex(v2);
        face.addVertex(v3);
        face.addVertex(v4);
        face.rewind(center);
    }

    private static Polyhedron createCube() {
        Polyhedron cube = new Polyhedron();
        cube.addFace(top);
        cube.addFace(bottom);
        cube.addFace(north);
        cube.addFace(south);
        cube.addFace(east);
        cube.addFace(west);
        return cube;
    }
}
