package main;

import com.badlogic.gdx.math.Vector3;
import creators.Cube;
import lombok.extern.log4j.Log4j2;
import representation.Polyhedron;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Vector3 p1 = new Vector3(-2, -2, -2);
        Vector3 p2 = new Vector3(2, 2, 2);
        Polyhedron cube1 = Cube.create(p1, p2);
        log.info("cube1 = {}", (cube1));
    }
}