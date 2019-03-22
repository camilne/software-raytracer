package camilne.raytracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.awt.image.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Raytracer {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int MAX_DEPTH = 4;
    private static final float FOV = 51.52f;
    private static final Color BACKGROUND = new Color(0, 0, 0);
    private static final Color AMBIENT = new Color(0.1f, 0.1f, 0.1f);

    private ExecutorService executors;

    public Raytracer() {
        executors = Executors.newFixedThreadPool(NUM_THREADS);
    }

    /**
     * Trace a scene and return the image.
     */
    public BufferedImage trace(RenderOptions options, Scene scene) {
        final var result = new BufferedImage(options.width, options.height, BufferedImage.TYPE_INT_RGB);

        final var camera = new Camera(new Vector3f(6, 3, 8));
        camera.focus(new Vector3f());

        final var aspectRatio = (float) options.width / options.height;
        final var scale = (float) Math.tan(FOV * Math.PI / 360);

        for (var j = 0; j < options.height; j++) {
            for (var i = 0; i < options.width; i++) {
                submit(i, j, aspectRatio, scale, camera, scene, options, result);
            }
        }

        // Wait for the tasks to finish rendering.
        executors.shutdown();
        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void submit(int i, int j, float aspectRatio, float scale, Camera camera, Scene scene,
                        RenderOptions options, BufferedImage result) {
        executors.submit(() -> {
            final var colors = new ArrayList<Color>();
            final var gridSize = 1f / (options.aa + 1);
            for (var aj = 0; aj < options.aa; aj++) {
                for (var ai = 0; ai < options.aa; ai++) {
                    final var pixelX = i + gridSize * (ai + 1);
                    final var pixelY = j + gridSize * (aj + 1);

                    final var x = (2f * pixelX / options.width - 1) * aspectRatio * scale;
                    final var y = 1 - 2f * pixelY / options.height * scale - 0.5f;

                    final var origin = new Vector3f(camera.getPosition());
                    final var direction = new Vector3f(camera.getForward());
                    direction.add(camera.getRight().mul(x, new Vector3f()));
                    direction.add(camera.getUp().mul(y, new Vector3f()));
                    direction.normalize();

                    final var ray = new Ray(origin, direction);
                    colors.add(trace(ray, scene, camera, 0));
                }
            }

            final var finalColor = new Color();
            for (final var color : colors) {
                finalColor.add(color);
            }
            finalColor.mul(1f / colors.size());
            result.setRGB(i, j, finalColor.clamp().toRGB());
        });
    }

    private Color trace(Ray ray, Scene scene, Camera camera, int depth) {
        final var result = scene.getClosestObject(ray);
        if (result.getObject() == null) {
            return BACKGROUND;
        }

        final var surface = result.getObject().getSurface(result.getHitPosition());

        final var finalColor = getPhongColor(scene, camera, surface);
        if (surface.getMaterial().isReflective() && depth < MAX_DEPTH) {
            finalColor.add(getReflectedColor(ray, surface, scene, camera, depth));
        }

        return finalColor.clamp();
    }

    private Color getPhongColor(final Scene scene, final Camera camera, final Surface surface) {
        final var finalColor = new Color();
        finalColor.add(AMBIENT.mul(surface.getMaterial().getDiffuse(), new Color()));

        for (final var light : scene.getLights()) {
            final var lightDirection = light.getPosition().sub(surface.getPosition(), new Vector3f()).normalize();
            final var amountDiffuse = MathUtil.clamp(surface.getNormal().dot(lightDirection), 0, 1);

            if (amountDiffuse > 0) {
                final var lightColor = getLightColor(surface.getPosition(), light, scene);
                final var diffuseColor = surface.getMaterial().getDiffuse().mul(amountDiffuse, new Color());
                final var diffuse = diffuseColor.mul(lightColor);
                finalColor.add(diffuse);

                final var reflectedVec =
                    lightDirection.reflect(surface.getNormal(), new Vector3f()).negate().normalize();
                final var cameraDirection = camera.getPosition().sub(surface.getPosition(), new Vector3f()).normalize();
                final var specularDot = MathUtil.clamp(reflectedVec.dot(cameraDirection), 0, 1);

                if (specularDot > 0) {
                    final var amountSpecular = (float) Math.pow(specularDot, surface.getMaterial().getShininess());
                    final var specular = lightColor.mul(amountSpecular, new Color());
                    finalColor.add(specular);
                }
            }
        }
        return finalColor;
    }

    private Color getReflectedColor(Ray ray, Surface surface, Scene scene, Camera camera, int depth) {
        final var reflectedDir = ray.getDirection().reflect(surface.getNormal(), new Vector3f());
        final var newRay = new Ray(new Vector3f(surface.getPosition()), reflectedDir);
        final var tracedColor = trace(newRay, scene, camera, depth + 1);
        return tracedColor.mul(surface.getMaterial().getDiffuse());
    }

    private Color getLightColor(Vector3fc point, Light light, Scene scene) {
        final var shadowRayDir = light.getPosition().sub(point, new Vector3f());
        final float lightDistance = shadowRayDir.length();
        final var shadowRay = new Ray(new Vector3f(point), shadowRayDir.normalize());
        final var hitResult = scene.getClosestObject(shadowRay);

        if (hitResult.getT() < lightDistance) {
            return new Color();
        }

        return light.getColor().mul(light.getPower() / lightDistance, new Color());
    }

}
