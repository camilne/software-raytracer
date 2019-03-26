package camilne.raytracer.gui;

import camilne.raytracer.Color;
import camilne.raytracer.RenderableImage;
import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Arrays;

public class RenderableJavaFxImage extends WritableImage implements RenderableImage {

    private final PixelWriter writer;

    public RenderableJavaFxImage(int width, int height) {
        super(width, height);
        writer = getPixelWriter();
    }

    @Override
    public void writeRegion(int x, int y, int width, int height, Color[] colors) {
        Platform.runLater(() -> writer.setPixels(x, y, width, height, PixelFormat.getIntArgbInstance(),
            Arrays.stream(colors).mapToInt(Color::toARGB).toArray(), 0, width));
    }
    
}
