package camilne.raytracer;

import org.joml.Vector3f;

public class HitResult {

    private final SceneObject object;
    private final Vector3f hitPosition;
    private final float t;

    public HitResult(SceneObject object, Vector3f hitPosition, float t) {
        this.object = object;
        this.hitPosition = hitPosition;
        this.t = t;
    }

    public SceneObject getObject() {
        return object;
    }

    public Vector3f getHitPosition() {
        return hitPosition;
    }

    public float getT() {
        return t;
    }
}
