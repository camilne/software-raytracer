package camilne.raytracer;

public class RenderOptions {

    public final Camera camera;
    public final Scene scene;
    public RenderableImage target;
    public int width = 1200;
    public int height = 720;
    public int aa = 1;

    public RenderOptions(Camera camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
    }

}
