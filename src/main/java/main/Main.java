package main;

import com.badlogic.gdx.math.Vector3;
import creators.Box;
import creators.Tetrahedron;
import lombok.extern.log4j.Log4j2;
import representation.Polyhedron;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Polyhedron box1 = createCube1();
        log.info("box1 = {}", (box1));
        Polyhedron box2 = createCube2();
        log.info("box2 = {}", box2);
        Polyhedron tetrahedron1 = createTetrahedron1();
        log.info("tetrahedron1 = {}", tetrahedron1);
        Polyhedron clip = box1.clip(box2);
        log.info("clip = {}", clip);
    }

    private static Polyhedron createCube1() {
        log.traceEntry("()");
        int v = 2;
        Vector3 p1 = new Vector3(-v, -v, -v);
        Vector3 p2 = new Vector3(v, v, v);
        Vector3 skew = new Vector3(-2.5f, 0f, 0f);
        return Box.create(p1, p2, skew);
    }

    private static Polyhedron createCube2() {
        log.traceEntry("()");
        Vector3 p3 = new Vector3(1f, 1f, 1f);
        float v = 10f;
        Vector3 p4 = new Vector3(v, v, v);
        return Box.create(p3, p4);
    }

    private static Polyhedron createTetrahedron1() {
        log.traceEntry("()");
        Vector3 v1 = new Vector3(0f, 0f, -10f);
        Vector3 v2 = new Vector3(10f, 0f, 0f);
        Vector3 v3 = new Vector3(-10f, 0f, 0f);
        Vector3 v4 = new Vector3(0f, 10f, -5f);
        return Tetrahedron.create(v1, v2, v3, v4);
    }
}