package camilne.raytracer;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<SceneObject> objects;
    private List<Light> lights;

    public Scene() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    public void add(SceneObject object) {
        this.objects.add(object);
    }

    public void add(Light light) {
        this.lights.add(light);
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

    public List<SceneObject> getObjects() {
        return objects;
    }

    public List<Light> getLights() {
        return lights;
    }
}
