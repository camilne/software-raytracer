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

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> integerFilter = (change) -> {
            final var newText = change.getControlNewText();
            if (newText.matches("([0-9]+)?")) {
                return change;
            }
            return null;
        };

        widthInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        heightInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
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
            return;
        }

        final var raytracer = new Raytracer();

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

        final var targetWidth = MathUtil.clamp(Integer.parseInt(widthInput.getText()), 1, 3840);
        final var targetHeight = MathUtil.clamp(Integer.parseInt(heightInput.getText()), 1, 2160);
        final var target = new RenderableJavaFxImage(targetWidth, targetHeight);
        targetView.setImage(target);

        final var renderOptions = new RenderOptions(camera, scene);
        renderOptions.target = target;
        renderOptions.width = targetWidth;
        renderOptions.height = targetHeight;
        renderOptions.aa = MathUtil.clamp((int) aaSlider.getValue(), 0, 16);

        renderButton.setDisable(true);
        new Thread(() -> {
            raytracer.trace(renderOptions);
            Platform.runLater(() -> {
                renderButton.setDisable(false);
            });
        }).start();
    }

    private void validateInput() throws InvalidInputException {
        try {
            Integer.parseInt(widthInput.getText());
            Integer.parseInt(heightInput.getText());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Either width or height is not a valid integer");
        }
    }

}
