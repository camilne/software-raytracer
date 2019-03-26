package camilne.raytracer.gui;

import camilne.raytracer.Color;
import camilne.raytracer.RenderableImage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
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

    @Override
    public void save(File file) throws IOException {
        final var bufferedImage = SwingFXUtils.fromFXImage(this, null);
        ImageIO.write(bufferedImage, "png", file);
    }
}
