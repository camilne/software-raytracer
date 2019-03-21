package camilne.raytracer;

import org.joml.Vector3f;

import java.awt.*;
import java.awt.image.*;

public class Raytracer {

    private static final float FOV = 51.52f;

    /**
     * Trace a scene and return the image.
     */
    public BufferedImage trace(int width, int height, Scene scene) {
        final var result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        final var camera = new Camera(new Vector3f(3, 1.5f, 4));
        camera.focus(new Vector3f());

        final var aspectRatio = (float) width / height;
        final var scale = (float) Math.tan(FOV * Math.PI / 360);

        for (var j = 0; j < height; j++) {
            for (var i = 0; i < width; i++) {
                final var x = (2 * (i + 0.5f) / width - 1) * aspectRatio * scale;
                final var y = 1 - 2 * (j + 0.5f) / height * scale - 0.5f;

                final var origin = new Vector3f(camera.getPosition());
                final var direction = new Vector3f(camera.getForward());
                direction.add(camera.getRight().mul(x, new Vector3f()));
                direction.add(camera.getUp().mul(y, new Vector3f()));
                direction.normalize();

                final var ray = new Ray(origin, direction);
                result.setRGB(i, j, cast(ray, scene, camera).getRGB());
            }
        }

        return result;
    }
    
    private Color cast(Ray ray, Scene scene, Camera camera) {
        final var result = scene.getClosestObject(ray);
        if (result.getObject() == null) {
            return Color.BLACK;
        }

        final var surface = result.getObject().getSurface(result.getHitPosition());
        final var direction = camera.getPosition().sub(result.getHitPosition(), new Vector3f()).normalize();
        final var amountDiffuse = surface.getNormal().dot(direction);

        if (amountDiffuse <= 0) {
            return Color.DARK_GRAY;
        }

        return multiplyColor(surface.getDiffuse(), amountDiffuse);
    }

    private Color multiplyColor(Color color, float amount) {
        amount /= 255f;
        return new Color(color.getRed() * amount, color.getGreen() * amount, color.getBlue() * amount);
    }

}
