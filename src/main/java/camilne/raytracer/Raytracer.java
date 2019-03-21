package camilne.raytracer;

import java.awt.*;
import java.awt.image.*;

public class Raytracer {

    /**
     * Trace a scene and return the image.
     */
    public BufferedImage trace(int width, int height) {
        final var result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (var j = 0; j < result.getHeight(); j++) {
            for (var i = 0; i < result.getWidth(); i++) {
                result.setRGB(i, j, cast().getRGB());
            }
        }

        return result;
    }

    private Color cast() {
        return Color.MAGENTA;
    }

}
