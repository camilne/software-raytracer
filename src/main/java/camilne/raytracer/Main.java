package camilne.raytracer;

import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final String IMAGE_PATH = "render4x.png";
    private static final String IMAGE_FORMAT = "png";

    public static void main(String[] args) throws IOException {
        final var raytracer = new Raytracer();
        final var scene = new Scene();
        scene.add(new Sphere(new Vector3f(), 1f, new Material(new Color(0.3f, 0.7f, 0.9f), 10f)));
        scene.add(new Sphere(new Vector3f(2, -2, -5), 2.5f, new Material(new Color(1f, 1f, 0.3f), 100f)));
        scene.add(new Light(new Vector3f(-1, 4, 5), new Color(0.6f, 1f, 0.7f), 3));
        scene.add(new Light(new Vector3f(7, 3, 6), new Color(1f, 1, 1f), 2f));

        final var renderOptions = new RenderOptions();
        renderOptions.aa = 4;

        final var startTime = System.nanoTime();
        final var result = raytracer.trace(renderOptions, scene);
        final var endTime = System.nanoTime();

        System.out.printf("Render took %.3f seconds\n", (endTime - startTime) / 1e9);

        final var resultFile = new File(IMAGE_PATH);
        ImageIO.write(result, IMAGE_FORMAT, resultFile);
    }

}
