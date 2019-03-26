package camilne.raytracer;

public interface RenderableImage {

    void writeRegion(int x, int y, int width, int height, Color[] colors);

}
