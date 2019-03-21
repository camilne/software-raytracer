package camilne.raytracer;

import org.joml.Vector3f;

public class Sphere extends SceneObject {

    private final Vector3f position;
    private final float radius;
    private final float radius2;

    public Sphere(final Vector3f position, float radius) {
        this.position = position;
        this.radius = radius;
        this.radius2 = radius * radius;
    }

    @Override
    public float hit(Ray ray) {
        final var L = ray.getOrigin().sub(position, new Vector3f());
        final var a = ray.getDirection().dot(ray.getDirection());
        final var b = 2 * ray.getDirection().dot(L);
        final var c = L.dot(L) - radius2;

        final var d = b * b - 4 * a * c;
        float t0, t1;
        if (d < 0) {
            return -1;
        } else if (d == 0) {
            t0 = t1 = -0.5f * b / a;
        } else {
            float q = (b > 0) ?
                -0.5f * (b + (float)Math.sqrt(d)) :
                -0.5f * (b - (float)Math.sqrt(d));
            t0 = q / a;
            t1 = c / q;
        }

        if (t0 > t1) {
            final var tmp = t0;
            t0 = t1;
            t1 = tmp;
        }

        if (t0 < 0) {
            t0 = t1;
            if (t0 < 0) {
                return -1;
            }
        }

        return t0;
    }

    @Override
    public Surface getSurface(Vector3f hitPos) {
        final var normal = hitPos.sub(position, new Vector3f()).normalize();
        // Adjust the hit position by epsilon
        final var adjustedHitPos = new Vector3f();
        hitPos.add(normal.mul(1e-5f, adjustedHitPos), adjustedHitPos);
        return new Surface(adjustedHitPos, normal, new Color(1f, 1f, 1f));
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }
}
