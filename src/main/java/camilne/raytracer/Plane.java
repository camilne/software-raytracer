package camilne.raytracer;

import org.joml.Vector3f;

public class Plane extends SceneObject {

    private final Vector3f point;
    private final Vector3f normal;
    private final Material material;

    public Plane(Vector3f point, Vector3f normal, Material material) {
        this.point = point;
        this.normal = normal;
        this.material = material;
    }

    @Override
    public float hit(Ray ray) {
        final var d = normal.dot(ray.getDirection());
        if (Math.abs(d) > 1e-5f) {
            final var t = point.sub(ray.getOrigin(), new Vector3f()).dot(normal) / d;
            if (t >= 0) {
                return t;
            }
        }

        return -1;
    }

    @Override
    public Surface getSurface(Vector3f hitPos) {
        // Adjust the hit position by epsilon
        final var adjustedHitPos = new Vector3f();
        hitPos.add(normal.mul(1e-5f, adjustedHitPos), adjustedHitPos);

        if (isBlackTile(hitPos.x, hitPos.y, hitPos.z)) {
            return new Surface(adjustedHitPos, normal, new Material(
                new Color(),
                material.getShininess()
            ));
        }

        return new Surface(adjustedHitPos, normal, material);
    }

    private boolean isBlackTile(float x, float y, float z) {
        final var scale = 2;
        return (((int) Math.abs(Math.floor(x / scale)) % 2)
            ^ ((int) Math.abs(Math.floor(y / scale)) % 2)
            ^ ((int) Math.abs(Math.floor(z / scale)) % 2)) == 0;
    }

    public Vector3f getPoint() {
        return point;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Material getMaterial() {
        return material;
    }
}
