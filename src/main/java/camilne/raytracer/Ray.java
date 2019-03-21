package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Ray {

    private final Vector3f origin;
    private final Vector3f direction;

    public Ray(final Vector3f origin, final Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vector3f at(float t) {
        final var result = new Vector3f();
        origin.add(direction.mul(t, result), result);
        return result;
    }

    public Vector3fc getOrigin() {
        return origin;
    }

    public Vector3fc getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Origin: " + origin.toString() + " Dir: " + direction.toString();
    }
}
