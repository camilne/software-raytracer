package camilne.raytracer.gui;

import camilne.raytracer.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.converter.IntegerStringConverter;
import org.joml.Vector3f;

import java.util.function.UnaryOperator;

@SuppressWarnings("Duplicates")
public class MainController {

    private static final int MIN_WIDTH = 1;
    private static final int MAX_WIDTH = 3840;
    private static final int MIN_HEIGHT = 1;
    private static final int MAX_HEIGHT = 2160;

    @FXML
    private TextField widthInput;
    @FXML
    private TextField heightInput;
    @FXML
    private Slider aaSlider;
    @FXML
    private Button renderButton;

    @FXML
    private ImageView targetView;

    private Raytracer raytracer;
    private Thread raytracingThread;

    public MainController() {
        raytracer = new Raytracer();
    }

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> integerFilter = (change) -> {
            final var newText = change.getControlNewText();
            if (newText.matches("([0-9]+)?")) {
                return change;
            }
            return null;
        };

        widthInput.setTextFormatter(new TextFormatter<>(integerFilter));
        heightInput.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    public void shutdown() {
        if (raytracingThread != null && raytracingThread.isAlive()) {
            raytracingThread.interrupt();
        }
        raytracer.cancel();
    }

    @FXML
    private void onRenderPressed() {
        try {
            validateInput();
        } catch (InvalidInputException e) {
            final var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Invalid input");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        final var camera = new Camera(new Vector3f(6, 3, 8));
        camera.focus(new Vector3f());

        final var scene = new Scene();

        scene.add(new Sphere(new Vector3f(-1, -1, -1), 1.5f,
            new Material(new Color(1f, 0.435f, 0.349f), 10f, true, true)));
        scene.add(new Sphere(new Vector3f(2, 0, -5), 2.5f,
            new Material(new Color(0.953f, 0.655f, 0.071f), 100f, true)));
        scene.add(new Sphere(new Vector3f(-1, -0.5f, 2), 2f,
            new Material(new Color(1, 1, 1), 0, true, true)));
        scene.add(new Sphere(new Vector3f(-8, 0.5f, 1), 3f,
            new Material(new Color(0.263f, 0.667f, 0.545f), 10f)));
        scene.add(new Sphere(new Vector3f(-8, 2.5f, -15), 5f,
            new Material(new Color(1f, 0.1f, 0.1f), 15f)));

        scene.add(new Plane(new Vector3f(0, -2.5f, 0), new Vector3f(0, 1, 0),
            new Material(new Color(0.161f, 0.2f, 0.361f), 1f)));

        scene.add(new Light(new Vector3f(-2, 6, 6), new Color(0.6f, 1f, 0.7f), 2f, 0.1f));
        scene.add(new Light(new Vector3f(14, 10, 6), new Color(1f, 1f, 1f), 3f, 0.1f));
        scene.add(new Light(new Vector3f(0, 100000, 0), new Color(1f, 1f, 1f), 60000f, 3000f));

        final var targetWidth = Integer.parseInt(widthInput.getText());
        final var targetHeight = Integer.parseInt(heightInput.getText());
        final var target = new RenderableJavaFxImage(targetWidth, targetHeight);
        targetView.setImage(target);

        final var renderOptions = new RenderOptions(camera, scene);
        renderOptions.target = target;
        renderOptions.width = targetWidth;
        renderOptions.height = targetHeight;
        renderOptions.aa = MathUtil.clamp((int) aaSlider.getValue(), 0, 16);

        renderButton.setDisable(true);
        raytracingThread = new Thread(() -> {
            raytracer.trace(renderOptions);
            Platform.runLater(() -> {
                renderButton.setDisable(false);
            });
        });
        raytracingThread.start();
    }

    private void validateInput() throws InvalidInputException {
        validateInteger("width", widthInput, MIN_WIDTH, MAX_WIDTH);
        validateInteger("height", heightInput, MIN_HEIGHT, MAX_HEIGHT);
    }

    private void validateInteger(String name, TextField field, int min, int max) throws InvalidInputException {
        try {
            final var value = Integer.parseInt(field.getText());
            field.setText(String.valueOf(MathUtil.clamp(value, min, max)));

        } catch (NumberFormatException e) {
            throw new InvalidInputException(String.format("%s must be an integer between %d and %d", name, min, max));
        }
    }

}
