package camilne.raytracer;

import org.joml.Vector3f;

public abstract class SceneObject {

    /**
     * Check if the ray hits the object and returns the ray position if it hits. If it doesn't hit, then returns -1.
     * @param ray The ray to cast.
     * @return The parametric ray position if it hits, -1 otherwise.
     */
    public abstract float hit(Ray ray);

    public abstract Surface getSurface(Vector3f hitPos);

}
