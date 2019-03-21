package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Surface {

    private final Vector3f position;
    private final Vector3f normal;
    private final Material material;

    public Surface(Vector3f position, Vector3f normal, Material material) {
        this.position = position;
        this.normal = normal;
        this.material = material;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getNormal() {
        return normal;
    }

    public Material getMaterial() {
        return material;
    }
}
