package camilne.raytracer;

public class Material {

    private final Color diffuse;
    private final float shininess;

    public Material(Color diffuse, float shininess) {
        this.diffuse = diffuse;
        this.shininess = shininess;
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public float getShininess() {
        return shininess;
    }
}
