package camilne.raytracer;

import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final int IMAGE_WIDTH = 1200;
    private static final int IMAGE_HEIGHT = 720;
    private static final String IMAGE_PATH = "render.png";
    private static final String IMAGE_FORMAT = "png";

    public static void main(String[] args) throws IOException {
        final var raytracer = new Raytracer();
        final var scene = new Scene();
        scene.add(new Sphere(new Vector3f(), 1.5f));

        final var startTime = System.nanoTime();
        final var result = raytracer.trace(IMAGE_WIDTH, IMAGE_HEIGHT, scene);
        final var endTime = System.nanoTime();

        System.out.printf("Render took %.3f seconds\n", (endTime - startTime) / 1e9);

        final var resultFile = new File(IMAGE_PATH);
        ImageIO.write(result, IMAGE_FORMAT, resultFile);
    }

}
