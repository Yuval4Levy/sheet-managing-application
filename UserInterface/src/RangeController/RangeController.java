package RangeController;

import MainViewController.MainViewController;
import AlertController.AlertController;
import exceptions.CoordinateException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import coordinate.Coordinate;
import exceptions.SheetRangeException;
import range.Range;

import java.util.ArrayList;
import java.util.Map;

public class RangeController {

    @FXML
    private AnchorPane RangeVBox;
    @FXML
    private Label rangesLabel;
    @FXML
    private ComboBox<String> rangeComboBox;

    @FXML
    private Button deleteRangeButton;

    @FXML
    private Button addRangeButton;

    private MainViewController mainViewController;

    @FXML
    public void initialize() {
        rangesLabel.setText("Ranges Manager");
        deleteRangeButton.setOnAction(event -> deleteSelectedRange());
        addRangeButton.setOnAction(event -> showAddRangeDialog());
        rangeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            highlightSelectedRange(newValue);
        });
    }

    public void setMainViewController(MainViewController mainView) {
        this.mainViewController = mainView;
        updateRangeComboBox();
    }

    public void updateRangeComboBox() {
        if (mainViewController.getSheetManager().getCurrentSheet() != null) {
            Map<String, Range> ranges = mainViewController.getSheetManager().getCurrentRanges();
            ArrayList<String> rangesNames = new ArrayList<>();

            for (Range range : ranges.values()) {
                rangesNames.add(range.getName());
            }

            rangeComboBox.getItems().setAll(rangesNames);
        }
    }

    @FXML
    private void deleteSelectedRange() {
        String selectedRange = rangeComboBox.getSelectionModel().getSelectedItem();
        if (selectedRange != null) {
            try {
                mainViewController.getSheetManager().deleteRangeFromCurrentSheet(selectedRange);
                updateRangeComboBox();
                mainViewController.refreshTableView();
            } catch (SheetRangeException e) {
                AlertController.showAlert("Error", "Cannot delete range: " + e.getMessage());
            }
        }
    }

    @FXML
    private void showAddRangeDialog() {
        Dialog<Range> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Range");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField rangeNameField = new TextField();
        rangeNameField.setPromptText("Range Name");

        TextField startRowField = new TextField();
        startRowField.setPromptText("Start Row");

        TextField startColField = new TextField();
        startColField.setPromptText("Start Column");

        TextField endRowField = new TextField();
        endRowField.setPromptText("End Row");

        TextField endColField = new TextField();
        endColField.setPromptText("End Column");

        grid.add(new Label("Range Name:"), 0, 0);
        grid.add(rangeNameField, 1, 0);
        grid.add(new Label("Start Row:"), 0, 1);
        grid.add(startRowField, 1, 1);
        grid.add(new Label("Start Column:"), 0, 2);
        grid.add(startColField, 1, 2);
        grid.add(new Label("End Row:"), 0, 3);
        grid.add(endRowField, 1, 3);
        grid.add(new Label("End Column:"), 0, 4);
        grid.add(endColField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    String rangeName = rangeNameField.getText();
                    Coordinate startCoordinate = mainViewController.getSheetManager().
                            parseCoordinate(startRowField.getText() + " " + startColField.getText());
                    Coordinate endCoordinate = mainViewController.getSheetManager().
                            parseCoordinate(endRowField.getText() + " " + endColField.getText());

                    return new Range(rangeName, startCoordinate, mainViewController.getSheetManager().getCurrentSheet(),
                            endCoordinate);
                } catch (NumberFormatException e) {
                    AlertController.showAlert("Input Error", "Please enter valid numbers for row and column.");

                    return null;
                } catch (CoordinateException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        });

        Platform.runLater(() -> rangeNameField.requestFocus());

        dialog.showAndWait().ifPresent(range -> {
            try {
                mainViewController.getSheetManager().addRangeToCurrentSheet(range.getName(), range.getStart(), range.getEnd());
                updateRangeComboBox();
            } catch (SheetRangeException e) {
                AlertController.showAlert("Error", "Cannot add range: " + e.getMessage());
            }
        });
    }

    private void highlightSelectedRange(String rangeName) {
        if (rangeName != null && !rangeName.isEmpty()) {
            Range range = mainViewController.getSheetManager().getCurrentRanges().get(rangeName);
            if (range != null) {
                mainViewController.getColorController().highlightChosenRange(range);
            }
        }
    }
}
