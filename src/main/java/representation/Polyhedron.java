package representation;

import com.badlogic.gdx.utils.Array;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

/**
 * An array of {@link Face}s.
 *
 * @version 1.0
 */
@Log4j2
@Value
public final class Polyhedron {
    private final Array<Face> faces = new Array<>(8);

    public void addFace(Face face) {
        log.traceEntry("({})", face);
        faces.add(face);
    }

    public Polyhedron clip(Polyhedron clippingPolyhedron) {
        log.traceEntry("({})", clippingPolyhedron);
        Polyhedron workingPolyhedron = this;
        for (Face clippingFace : clippingPolyhedron.faces) {
            workingPolyhedron = clip(workingPolyhedron, clippingFace);
            log.trace("workingPolyhedron = {}", workingPolyhedron);
        }
        return sanityCheck(workingPolyhedron);
    }

    private Polyhedron clip(Polyhedron inPolyhedron, Face clippingFace) {
        log.traceEntry("({}, {})", inPolyhedron, clippingFace);
        Polyhedron outPolyhedron = clipPolyhedronByFace(inPolyhedron, clippingFace);
        Face workingFace = clipFaceByPolyhedron(clippingFace, inPolyhedron);
        if (workingFace != null) {
            log.debug("Working face {} was not null, so we add it to the outPolyhedron.", workingFace);
            outPolyhedron.addFace(workingFace);
        } else {
            log.debug("Working face was null, so we don't add it to the outPolyhedron.");
        }
        return outPolyhedron;
    }

    private Polyhedron clipPolyhedronByFace(Polyhedron inPolyhedron, Face clippingFace) {
        log.traceEntry("({}, {})", inPolyhedron, clippingFace);
        Polyhedron outPolyhedron = new Polyhedron();
        for (Face inFace : inPolyhedron.faces) {
            Face clippedFace = inFace.clipFace(clippingFace);
            log.debug("clippedFace = {}", clippedFace);
            if (clippedFace != null) {
                log.debug("Clipped Face was not null, so we add it.");
                outPolyhedron.addFace(clippedFace);
            }
        }
        return outPolyhedron;
    }

    private Face clipFaceByPolyhedron(Face clippingFace, Polyhedron inPolyhedron) {
        log.traceEntry("({}, {})", inPolyhedron, clippingFace);
        Face workingFace = clippingFace;
        for (Face face : inPolyhedron.faces) {
            if (workingFace == null) {
                break;
            } else {
                log.trace("workingFace = {}", workingFace);
            }
            workingFace = workingFace.clipFace(face);
        }
        return workingFace;
    }

    private Polyhedron sanityCheck(Polyhedron workingPolyhedron) {
        log.traceEntry("({})", workingPolyhedron);
        int numberOfFaces = workingPolyhedron.faces.size;
        log.trace("numberOfFaces = {}", numberOfFaces);
        if (numberOfFaces > 2) {
            return workingPolyhedron;
        } else {
            return null;
        }
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
