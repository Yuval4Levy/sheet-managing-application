package FileLoaderController;

import AlertController.AlertController;
import MainViewController.MainViewController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLoaderController {

    public Button loadButton;
    @FXML
    private TextField filePathField;
    private Stage loadingStage;
    private static final Logger logger = Logger.getLogger(FileLoaderController.class.getName());
    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainView) {mainViewController = mainView;}

    @FXML
    private void initialize() {
        loadButton = new Button("Load File");
        filePathField.setPromptText("Enter File Path");
    }

    @FXML
    private void handleLoadButton() {
        String xmlFilePath = filePathField.getText().trim();
        AtomicBoolean loaded = new AtomicBoolean(false);

        if (!xmlFilePath.isEmpty()) {
            showLoadingWindow();
            new Thread(() -> {
                for (int i = 0; i <= 100; i += 10) {
                    try {
                        Thread.sleep(100);
                        String status = "Loading... " + i + "%";
                        Platform.runLater(() -> {
                            ((Label) loadingStage.getScene().getRoot().getChildrenUnmodifiable().getFirst()).setText(status);
                        });
                    }
                    catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, "Loading interrupted", ex);
                    }
                }

                try {
                    loaded.set(mainViewController.getSheetManager().loadSheetFromXml(xmlFilePath));
                } catch (Exception ex) {
                    AlertController.showAlert("Failed to load sheet", ex.getMessage());
                }

                closeLoadingWindow();
                if (loaded.get()) {
                    mainViewController.updateUIAfterLoad();
                    logger.log(Level.INFO, "Sheet loaded successfully to GUI.");
                }
            }).start();
        }
    }

    private void showLoadingWindow() {
        loadingStage = new Stage();
        Label loadingLabel = new Label("Loading... Please wait.");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 300, 100);

        layout.getChildren().add(loadingLabel);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("Loading");
        loadingStage.setScene(scene);
        loadingStage.show();
    }

    private void closeLoadingWindow() {
        if (loadingStage != null) {
            Platform.runLater(() -> loadingStage.close());
        }
    }
}
