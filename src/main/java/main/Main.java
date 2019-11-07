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
        Polyhedron tetrahedron2 = createTetrahedron2();
        log.info("tetrahedron1 = {}", tetrahedron2);

        log.info("box1.clip(box2) = {}", box1.clip(box2));
        log.info("box1.clip(tetrahedron1) = {}", box1.clip(tetrahedron1));
        log.info("box1.clip(tetrahedron2) = {}", box1.clip(tetrahedron2));
        log.info("box2.clip(tetrahedron1) = {}", box2.clip(tetrahedron1));
        log.info("box2.clip(tetrahedron2) = {}", box2.clip(tetrahedron2));
        log.info("tetrahedron1.clip(tetrahedron2) = {}", tetrahedron1.clip(tetrahedron2));
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
        float v1 = 0f;
        Vector3 p3 = new Vector3(v1, v1, v1);
        float v2 = 10f;
        Vector3 p4 = new Vector3(v2, v2, v2);
        return Box.create(p3, p4);
    }

    private static Polyhedron createTetrahedron1() {
        log.traceEntry("()");
        Vector3 v1 = new Vector3(0f, 0f, -5f);
        Vector3 v2 = new Vector3(10f, 0f, 5f);
        Vector3 v3 = new Vector3(-10f, 0f, 5f);
        Vector3 v4 = new Vector3(0f, 10f, 0f);
        return Tetrahedron.create(v1, v2, v3, v4);
    }

    private static Polyhedron createTetrahedron2() {
        log.traceEntry("()");
        Vector3 v1 = new Vector3(0f, 10f, 5f);
        Vector3 v2 = new Vector3(10f, 10f, -5f);
        Vector3 v3 = new Vector3(-10f, 10f, -5f);
        Vector3 v4 = new Vector3(0f, 0f, 0f);
        return Tetrahedron.create(v1, v2, v3, v4);
    }
}