module raytracer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.joml;
    requires java.desktop;

    opens camilne.raytracer.gui to javafx.fxml;
    exports camilne.raytracer.gui;
    exports camilne.raytracer;
}