package camilne.raytracer;

import org.joml.Vector3f;

public class Color {

    private Vector3f color;

    public Color() {
        color = new Vector3f();
    }

    public Color(float r, float g, float b) {
        color = new Vector3f(r, g, b);
    }

    public Color add(Color other) {
        color.add(other.color);
        return this;
    }

    public Color add(Color other, Color dest) {
        color.add(other.color, dest.color);
        return dest;
    }

    public Color mul(float amount) {
        color.mul(amount);
        return this;
    }

    public Color mul(float amount, Color dest) {
        color.mul(amount, dest.color);
        return dest;
    }

    public Color mul(Color other) {
        color.mul(other.color);
        return this;
    }

    public Color mul(Color other, Color dest) {
        color.mul(other.color, dest.color);
        return dest;
    }

    public Color clamp() {
        color.x = Math.max(Math.min(color.x, 1), 0);
        color.y = Math.max(Math.min(color.y, 1), 0);
        color.z = Math.max(Math.min(color.z, 1), 0);
        return this;
    }

    public float r() {
        return color.x;
    }

    public float g() {
        return color.y;
    }

    public float b() {
        return color.z;
    }

    public int toRGB() {
        clamp();
        return new java.awt.Color(correct(color.x), correct(color.y), correct(color.z)).getRGB();
    }

    private float correct(float color) {
        return color;
        //return (float) Math.pow(color, 1 / 2.2);
    }

}
