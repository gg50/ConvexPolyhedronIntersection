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
            workingPolyhedron = workingPolyhedron.clip(clippingFace);
            log.trace("workingPolyhedron = {}", workingPolyhedron);
        }
        return workingPolyhedron.sanityCheck();
    }

    /**
     * Clips this {@link Polyhedron} by the given {@link Face}.  <br/>
     * Uses {@link #clipByFace(Face)} to create  <br/>
     * a new {@link Polyhedron} that is the same as this except  <br/>
     * any part that is outside the clippingFace is clipped out  <br/>
     * and the part that would be left empty (at the clipping) is filled by
     * the {@link Face} created by {@link #clipFace(Face)}.
     */
    private Polyhedron clip(Face clippingFace) {
        log.traceEntry("({})", clippingFace);
        Polyhedron outPolyhedron = clipByFace(clippingFace);
        Face workingFace = clipFace(clippingFace);
        if (workingFace != null) {
            log.debug("Working face {} was not null, so we add it to the outPolyhedron.", workingFace);
            outPolyhedron.addFace(workingFace);
        } else {
            log.debug("Working face was null, so we don't add it to the outPolyhedron.");
        }
        return outPolyhedron;
    }

    /**
     * Clips this {@link Polyhedron} by the given {@link Face}.  <br/>
     * Creates a new {@link Polyhedron} that is the same as this except  <br/>
     * any part that is outside the clippingFace is clipped out.
     */
    private Polyhedron clipByFace(Face clippingFace) {
        log.traceEntry("({})", clippingFace);
        Polyhedron outPolyhedron = new Polyhedron();
        for (Face inFace : faces) {
            Face clippedFace = inFace.clipFace(clippingFace);
            log.debug("clippedFace = {}", clippedFace);
            if (clippedFace != null) {
                log.debug("Clipped Face was not null, so we add it.");
                outPolyhedron.addFace(clippedFace);
            }
        }
        return outPolyhedron;
    }

    /**
     * Clips the given {@link Face} by the {@link #faces} of this {@link Polyhedron}.  <br/>
     * Creates a new {@link Face} that is the same as the given except  <br/>
     * any part that is outside this {@link Polyhedron} is clipped out.
     */
    private Face clipFace(Face clippingFace) {
        log.traceEntry("({})", clippingFace);
        Face workingFace = clippingFace;
        for (Face face : faces) {
            if (workingFace == null) {
                break;
            } else {
                log.trace("workingFace = {}", workingFace);
            }
            workingFace = workingFace.clipFace(face);
        }
        return workingFace;
    }

    private Polyhedron sanityCheck() {
        log.traceEntry("({})");
        int numberOfFaces = faces.size;
        log.trace("numberOfFaces = {}", numberOfFaces);
        return numberOfFaces > 2 ? this : null;
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
