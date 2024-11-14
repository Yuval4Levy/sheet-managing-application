package LoadingWindowController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class LoadingWindowController {
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label statusLabel;

    public void setStatus(String status) { statusLabel.setText(status); }

    public void setProgress(double progress) { progressIndicator.setProgress(progress); }
}