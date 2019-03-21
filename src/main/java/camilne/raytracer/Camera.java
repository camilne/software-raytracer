package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera {

    private final Vector3fc X = new Vector3f(1, 0, 0);
    private final Vector3fc Y = new Vector3f(0, 1, 0);
    private final Vector3fc Z = new Vector3f(0, 0, 1);

    private Vector3f position;
    private Vector3f forward;
    private Vector3f up;
    private Vector3f right;

    public Camera(Vector3f position) {
        this.position = position;
        this.forward = new Vector3f(0, 0, -1);
        this.up = new Vector3f();
        this.right = new Vector3f();
    }

    /**
     * Look at a point from the current position.
     * @param point The point to look at.
     */
    public void focus(Vector3f point) {
        forward = point.sub(position, forward).normalize();
        right = forward.cross(Y, right).normalize();
        up = right.cross(forward, up).normalize();
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getForward() {
        return forward;
    }

    public Vector3fc getUp() {
        return up;
    }

    public Vector3fc getRight() {
        return right;
    }
}
