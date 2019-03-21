package camilne.raytracer;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<SceneObject> objects;

    public Scene() {
        this.objects = new ArrayList<>();
    }

    public void add(SceneObject object) {
        this.objects.add(object);
    }

    public HitResult getClosestObject(Ray ray) {
        SceneObject minObject = null;
        var minT = Float.POSITIVE_INFINITY;

        for (final var object : objects) {
            final var objectT = object.hit(ray);
            if (objectT >= 0 && objectT < minT) {
                minT = objectT;
                minObject = object;
            }
        }

        final var hitPosition = ray.at(minT);
        return new HitResult(minObject, hitPosition, minT);
    }
}
