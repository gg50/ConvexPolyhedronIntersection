package representation;

import com.badlogic.gdx.utils.Array;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

/**
 * @version 1.0
 */
@Log4j2
@Value
public final class Polyhedron {
    private final Array<Face> faces = new Array<>(8);

    public int getNumberOfFaces() {
        log.traceEntry("()");
        return faces.size;
    }

    public void addFace(Face face) {
        log.traceEntry("({})", face);
        faces.add(face);
    }

    public Face getFace(int index) {
        log.traceEntry("({})", index);
        return faces.get(index);
    }

    public Polyhedron clip(Polyhedron clippingPolyhedron) {
        log.traceEntry("({})", clippingPolyhedron);
        Polyhedron workingPolyhedron = this;
        int numberOfFaces = clippingPolyhedron.getNumberOfFaces();
        for (int i = 0; i < numberOfFaces; i++) {
            Face clippingFace = clippingPolyhedron.getFace(i);
            workingPolyhedron = clip(workingPolyhedron, clippingFace);
        }
        return workingPolyhedron;
    }

    private Polyhedron clip(Polyhedron inPolyhedron, Face clippingFace) {
        log.traceEntry("({}, {})", inPolyhedron, clippingFace);
        Polyhedron outPolyhedron = new Polyhedron();
        int numberOfFaces = inPolyhedron.getNumberOfFaces();
        for (int i = 0; i < numberOfFaces; i++) {
            Face inFace = inPolyhedron.getFace(i);
            Face clippedFace = inFace.clipFace(clippingFace);
            if (clippedFace != null) {
                outPolyhedron.addFace(clippedFace);
            }
        }
        Face workingFace = clippingFace;
        for (int i = 0; i < numberOfFaces; i++) {
            if (workingFace == null) {
                break;
            }
            Face face = inPolyhedron.getFace(i);
            workingFace = workingFace.clipFace(face);
        }
        if (workingFace != null) {
            outPolyhedron.addFace(workingFace);
        }
        return outPolyhedron;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Face face : faces) {
            sb.append("\t");
            sb.append(face);
            sb.append(",\n");
        }
        int length = sb.length();
        sb.replace(length - 2, length, "");  // Remove extra comma and newline after last face.
        return "Polyhedron(faces=[\n" + sb.toString() + "\n])";
    }
}
