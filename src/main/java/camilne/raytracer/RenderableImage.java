package camilne.raytracer;

import java.io.File;
import java.io.IOException;

public interface RenderableImage {

    void writeRegion(int x, int y, int width, int height, Color[] colors);
    void save(File file) throws IOException;

}
