package camilne.raytracer;

public class MathUtil {

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float mix(float a, float b, float t) {
        return a * t + b * (1 - t);
    }

}
