package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Light {

    private Vector3f position;
    private Color color;
    private float power;
    private float radius;

    public Light(Vector3f position, Color color, float power, float radius) {
        this.position = position;
        this.color = color;
        this.power = power;
        this.radius = radius;
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

    public Vector3f getRandomPosition() {
        // http://mathworld.wolfram.com/SpherePointPicking.html
        float x1 = 1;
        float x2 = 1;
        while (x1 * x1 + x2 * x2 >= 1) {
            x1 = (float) (Math.random() * 2 - 1);
            x2 = (float) (Math.random() * 2 - 1);
        }
        final var factor = 2 * (float) Math.sqrt(1 - x1 * x1 - x2 * x2);
        final var offset = new Vector3f(
            x1 * factor,
            x2 * factor,
            1 - 2 * (x1 * x1 + x2 * x2)
        );
        return offset.normalize().mul(radius).add(position);
    }
}
