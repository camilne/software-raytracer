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
    private static final Color AMBIENT = new Color(0.03f, 0.03f, 0.03f);
    private static final int NUM_SHADOW_SAMPLES = 64;

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
        final Color finalColor = new Color();
        if ((surface.getMaterial().isReflective() || surface.getMaterial().isTransparent()) && depth < MAX_DEPTH) {
            // Calculate the reflected color.
            final var reflectedColor = new Color();
            if (surface.getMaterial().isReflective()) {
                reflectedColor.add(getReflectedColor(ray, surface, scene, camera, depth));
            }

            // Calculate the refracted color.
            final var refractedColor = new Color();
            final var ior = 1.1f;
            final var fresnel = computeFresnel(ray.getDirection(), surface.getNormal(), ior);
            if (surface.getMaterial().isTransparent() && fresnel < 1) {
                refractedColor.add(getRefractedColor(ray, surface, ior, scene, camera, depth));
            }

            // Mix the reflection and refraction together.
            if (surface.getMaterial().isReflective() && surface.getMaterial().isTransparent()) {
                finalColor.add(reflectedColor.mul(fresnel).add(refractedColor.mul(1 - fresnel)));
            }
            else {
                finalColor.add(reflectedColor).add(refractedColor);
            }
        } else {
            finalColor.add(getPhongColor(scene, camera, surface));
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
                final var lightColor = getSoftLightColor(surface.getPosition(), light, scene);
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
        final var outside = (ray.getDirection().dot(surface.getNormal()) < 0);
        final var origin = new Vector3f(surface.getPosition());
        if (!outside) {
            origin.sub(surface.getNormal().mul(2e-4f, new Vector3f()), origin);
        }

        final var newRay = new Ray(origin, reflectedDir);
        return trace(newRay, scene, camera, depth + 1).mul(surface.getMaterial().getDiffuse());
    }

    private Color getRefractedColor(Ray ray, Surface surface, float ior, Scene scene, Camera camera, int depth) {
        final var refractedDir = refract(ray.getDirection(), surface.getNormal(), ior).normalize();
        final var outside = (ray.getDirection().dot(surface.getNormal()) < 0);
        final var origin = new Vector3f(surface.getPosition());
        if (outside) {
            origin.sub(surface.getNormal().mul(2e-4f, new Vector3f()), origin);
        }

        final var newRay = new Ray(origin, refractedDir);
        return trace(newRay, scene, camera, depth + 1).mul(surface.getMaterial().getDiffuse());
    }

    private Vector3f refract(Vector3fc incoming, Vector3fc normal, float ior) {
        float cosi = MathUtil.clamp(incoming.dot(normal), -1, 1);
        float etai = 1f;
        float etat = ior;
        final var newNormal = new Vector3f(normal);
        if (cosi < 0) {
            cosi = -cosi;
        }
        else {
            final var tmp = etai;
            etai = etat;
            etat = tmp;
            newNormal.negate();
        }

        final float eta = etai / etat;
        final float k = 1 - eta * eta * (1 - cosi * cosi);

        if (k < 0) {
            return new Vector3f();
        }
        return incoming.mul(eta, new Vector3f()).add(newNormal.mul((eta * cosi - (float) Math.sqrt(k))));
    }

    private float computeFresnel(Vector3fc incoming, Vector3fc normal, float ior) {
        float cosi = MathUtil.clamp(incoming.dot(normal), -1, 1);
        float etai = 1f;
        float etat = ior;
        if (cosi > 0) {
            final var tmp = etai;
            etai = etat;
            etat = tmp;
        }

        final float sint = etai / etat * (float) Math.sqrt(Math.max(0, 1 - cosi * cosi));

        // Total internal reflection
        if (sint >= 1) {
            return 1;
        }
        else {
            final var cost = (float) Math.sqrt(Math.max(0, 1 - sint * sint));
            cosi = Math.abs(cosi);
            final var rS = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
            final var rP = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
            return (rS * rS + rP * rP) / 2f;
        }
    }

    private Color getLightColor(Vector3fc point, Light light, Scene scene) {
            final var shadowRayDir = light.getPosition().sub(point, new Vector3f());
            final float lightDistance = shadowRayDir.length();
            final var shadowRay = new Ray(new Vector3f(point), shadowRayDir.normalize());
            final var hitResult = scene.getClosestOpaqueObject(shadowRay);

            if (hitResult.getT() < lightDistance) {
                return getSoftLightColor(point, light, scene);
            }

            return light.getColor().mul(light.getPower() / lightDistance, new Color());
    }

    private Color getSoftLightColor(Vector3fc point, Light light, Scene scene) {
        final var finalColor = new Color();
        for (var i = 0; i < NUM_SHADOW_SAMPLES; i++) {
            final var shadowRayDir = light.getRandomPosition().sub(point);
            final float lightDistance = shadowRayDir.length();
            final var shadowRay = new Ray(new Vector3f(point), shadowRayDir.normalize());
            final var hitResult = scene.getClosestOpaqueObject(shadowRay);

            if (hitResult.getT() < lightDistance) {
                continue;
            }

            finalColor.add(light.getColor().mul(light.getPower() / lightDistance, new Color()));
        }
        return finalColor.mul(1f / NUM_SHADOW_SAMPLES);
    }

}
