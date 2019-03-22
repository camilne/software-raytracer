package camilne.raytracer;

public class Material {

    private final Color diffuse;
    private final float shininess;
    private final boolean reflective;

    public Material(Color diffuse, float shininess) {
        this(diffuse, shininess, false);
    }

    public Material(Color diffuse, float shininess, boolean reflective) {
        this.diffuse = diffuse;
        this.shininess = shininess;
        this.reflective = reflective;
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public float getShininess() {
        return shininess;
    }

    public boolean isReflective() {
        return reflective;
    }
}
