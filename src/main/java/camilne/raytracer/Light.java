package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Light {

    private Vector3f position;
    private Color color;
    private float power;

    public Light(Vector3f position, Color color, float power) {
        this.position = position;
        this.color = color;
        this.power = power;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public float getPower() {
        return power;
    }
}
