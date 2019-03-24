package camilne.raytracer;

import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final String IMAGE_PATH = "render.png";
    private static final String IMAGE_FORMAT = "png";

    public static void main(String[] args) throws IOException {
        final var raytracer = new Raytracer();
        final var scene = new Scene();

        scene.add(new Sphere(new Vector3f(-1, -1, -1), 1.5f,
            new Material(new Color(1f, 0.435f, 0.349f), 10f, true, true)));
        scene.add(new Sphere(new Vector3f(2, 0, -5), 2.5f,
            new Material(new Color(0.953f, 0.655f, 0.071f), 100f, true)));
        scene.add(new Sphere(new Vector3f(-1, -0.5f, 2), 2f,
            new Material(new Color(1, 1, 1), 0, true, true)));
        scene.add(new Sphere(new Vector3f(-8, 0.5f, 1), 3f,
            new Material(new Color(0.263f, 0.667f, 0.545f), 10f)));
        scene.add(new Sphere(new Vector3f(-8, 2.5f, -15), 5f,
            new Material(new Color(1f, 0.1f, 0.1f), 15f)));

        scene.add(new Plane(new Vector3f(0, -2.5f, 0), new Vector3f(0, 1, 0),
            new Material(new Color(0.161f, 0.2f, 0.361f), 1f)));
//        scene.add(new Plane(new Vector3f(-25, 0, 0), new Vector3f(1, 0, 0),
//            new Material(new Color(1f, 0.2f, 1), 1f)));
//        scene.add(new Plane(new Vector3f(0, 0, -30), new Vector3f(0, 0, 1),
//            new Material(new Color(0.8f, 1, 0.8f), 1f)));

        scene.add(new Light(new Vector3f(-2, 6, 6), new Color(0.6f, 1f, 0.7f), 2f, 0.1f));
        scene.add(new Light(new Vector3f(14, 10, 6), new Color(1f, 1f, 1f), 3f, 0.1f));
        scene.add(new Light(new Vector3f(0, 100000, 0), new Color(1f, 1f, 1f), 60000f, 3000f));

        final var renderOptions = new RenderOptions();
        renderOptions.width = 1920;
        renderOptions.height = 1080;
        renderOptions.aa = 2;

        final var startTime = System.nanoTime();
        final var result = raytracer.trace(renderOptions, scene);
        final var endTime = System.nanoTime();

        System.out.printf("Render took %.3f seconds\n", (endTime - startTime) / 1e9);

        final var resultFile = new File(IMAGE_PATH);
        ImageIO.write(result, IMAGE_FORMAT, resultFile);

        System.out.println("Saved image as " + IMAGE_PATH);
    }

}
