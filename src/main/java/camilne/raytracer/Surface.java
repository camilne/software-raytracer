package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Surface {

    private final Vector3f position;
    private final Vector3f normal;
    private final Color diffuse;

    public Surface(Vector3f position, Vector3f normal, Color diffuse) {
        this.position = position;
        this.normal = normal;
        this.diffuse = diffuse;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getNormal() {
        return normal;
    }

    public Color getDiffuse() {
        return diffuse;
    }
}
