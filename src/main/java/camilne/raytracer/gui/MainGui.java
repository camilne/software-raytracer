package camilne.raytracer.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        final var loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();

        final var scene = new Scene(root);

        stage.setTitle("Java Ray Tracer");
        stage.setScene(scene);

        final var controller = (MainController)loader.getController();
        stage.setOnCloseRequest((e) -> controller.shutdown());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
