package camilne.raytracer;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class RenderableBufferedImage extends BufferedImage implements RenderableImage {

    public RenderableBufferedImage(int width, int height) {
        super(width, height, TYPE_3BYTE_BGR);
    }

    @Override
    public void writeRegion(int x, int y, int width, int height, Color[] colors) {
        setRGB(x, y, width, height, Arrays.stream(colors).mapToInt(Color::toRGB).toArray(), 0, width);
    }

    @Override
    public void save(File file) throws IOException {
        ImageIO.write(this, "png", file);
    }
}
