package FilterController;

import AlertController.AlertController;
import cell.Cell;
import coordinate.Coordinate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import MainViewController.MainViewController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterController {

    @FXML
    private VBox sortVBox;
    @FXML
    private ComboBox<String> columnComboBox;
    @FXML
    private TextField cellInputField;
    private MainViewController mainViewController;
    private static final Logger logger = Logger.getLogger(FilterController.class.getName());

    @FXML
    private void initialize() {


    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void populateColumnComboBox() {
        columnComboBox.getItems().clear();
        for (TableColumn<String[], ?> column : mainViewController.getTableViewController().getTableView().getColumns()) {
            columnComboBox.getItems().add(column.getText());
        }

        if (!columnComboBox.getItems().isEmpty()) {
            columnComboBox.getSelectionModel().select(0);
        }
    }

    @FXML
    private void handleFilterButtonAction() {
        try {
            String selectedColumn = columnComboBox.getSelectionModel().getSelectedItem();
            if (!Objects.equals(selectedColumn, "Row Index")) {
                String cellInput = cellInputField.getText();
                List<String> cellInputs = Arrays.asList(cellInput.split(",\\s*"));
                List<Cell> cells = new ArrayList<>();

                for (String cellString : cellInputs) {
                    Coordinate coordinate = mainViewController.getSheetManager().parseCoordinate(cellString);
                    cells.add(mainViewController.getSheetManager().getCurrentSheet().getCell
                            (coordinate.getRow(), coordinate.getColumn()));
                }

                List<Cell> sortedCells = mainViewController.getSheetManager().filterColumn(selectedColumn, cells);
                if (sortedCells != null && !sortedCells.isEmpty()) {
                    List<String> sortedCellStrings = sortedCells.stream()
                            .map(cell -> selectedColumn + cell.getCoordinate().getRow()
                                    + ": " + cell.getEffectiveValue().toString())
                            .collect(Collectors.toList());

                    showSortedResultsInPopup(sortedCellStrings);
                } else {
                    AlertController.showAlert("Empty filtering", "No elements were found in sorting");
                }
            }
            else {
                AlertController.showAlert("Empty filtering", "No column selected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            AlertController.showAlert("Error", e.getMessage());
        }
    }

    private void showSortedResultsInPopup(List<String> sortedResults) {
        ListView<String> resultsListView = new ListView<>();
        Dialog<Void> dialog = new Dialog<>();
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);


        resultsListView.getItems().addAll(sortedResults);
        dialog.setTitle("Filtered Results");
        dialog.getDialogPane().setContent(resultsListView);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.showAndWait();
    }

}
