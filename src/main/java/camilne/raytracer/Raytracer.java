package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.awt.image.*;
import java.util.ArrayList;

public class Raytracer {

    private static final float FOV = 51.52f;
    private static final Color BACKGROUND = new Color(0, 0, 0);
    private static final Color AMBIENT = new Color(0.1f, 0.1f, 0.1f);

    /**
     * Trace a scene and return the image.
     */
    public BufferedImage trace(RenderOptions options, Scene scene) {
        final var result = new BufferedImage(options.width, options.height, BufferedImage.TYPE_INT_RGB);

        final var camera = new Camera(new Vector3f(3, 1.5f, 4));
        camera.focus(new Vector3f());

        final var aspectRatio = (float) options.width / options.height;
        final var scale = (float) Math.tan(FOV * Math.PI / 360);

        for (var j = 0; j < options.height; j++) {
            for (var i = 0; i < options.width; i++) {
                final var colors = new ArrayList<Color>();
                final var gridSize = 1f / (options.aa + 1);
                for (var aj = 0; aj < options.aa; aj++) {
                    for (var ai = 0; ai < options.aa; ai++) {
                        final var pixelX = i + gridSize * (ai + 0.5f + (float) Math.random());
                        final var pixelY = j + gridSize * (aj + 0.5f + (float) Math.random());

                        final var x = (2f * pixelX / options.width - 1) * aspectRatio * scale;
                        final var y = 1 - 2f * pixelY / options.height * scale - 0.5f;

                        final var origin = new Vector3f(camera.getPosition());
                        final var direction = new Vector3f(camera.getForward());
                        direction.add(camera.getRight().mul(x, new Vector3f()));
                        direction.add(camera.getUp().mul(y, new Vector3f()));
                        direction.normalize();

                        final var ray = new Ray(origin, direction);
                        colors.add(cast(ray, scene, camera));
                    }
                }

                final var finalColor = new Color();
                for (final var color : colors) {
                    finalColor.add(color);
                }
                finalColor.mul(1f / colors.size());
                result.setRGB(i, j, finalColor.clamp().toRGB());
            }
        }

        return result;
    }

    private Color cast(Ray ray, Scene scene, Camera camera) {
        final var result = scene.getClosestObject(ray);
        if (result.getObject() == null) {
            return BACKGROUND;
        }

        final var surface = result.getObject().getSurface(result.getHitPosition());

        final var finalColor = new Color();
        for (final var light : scene.getLights()) {
            final var lightDirection = light.getPosition().sub(result.getHitPosition(), new Vector3f()).normalize();
            final var amountDiffuse = surface.getNormal().dot(lightDirection);
            if (amountDiffuse > 0) {
                final var lightColor = getLightColor(surface.getPosition(), light, scene);
                final var diffuse = surface.getDiffuse()
                    .mul(amountDiffuse, new Color())
                    .mul(lightColor);
                finalColor.add(diffuse);

                final var reflectedVec = lightDirection.reflect(surface.getNormal(), new Vector3f());
                final var cameraDirection = camera.getPosition().sub(surface.getPosition(), new Vector3f()).normalize();
                final var amountSpecular = (float) Math.pow(reflectedVec.dot(cameraDirection), 10f);
                final var specular = lightColor.mul(amountSpecular, new Color());
                finalColor.add(specular);
            }
        }

        return finalColor.add(AMBIENT).clamp();
    }

    private Color getLightColor(Vector3fc point, Light light, Scene scene) {
        final var shadowRayDir = light.getPosition().sub(point, new Vector3f());
        final var lightDistance = shadowRayDir.length();
        final var shadowRay = new Ray(new Vector3f(point), shadowRayDir.normalize());
        final var hitResult = scene.getClosestObject(shadowRay);

        if (hitResult.getT() < lightDistance) {
            return new Color();
        }

        return light.getColor().mul(light.getPower() / lightDistance, new Color());
    }

}
