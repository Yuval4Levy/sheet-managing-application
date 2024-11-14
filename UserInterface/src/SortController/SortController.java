package SortController;

import AlertController.AlertController;
import coordinate.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import MainViewController.MainViewController;
import SimpleTableView.SimpleTableView;
import cell.Cell;
import javafx.stage.Stage;
import sheet.Sheet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SortController {

    @FXML
    private VBox sortVBox;
    @FXML
    private TextField startRangeField;
    @FXML
    private TextField endRangeField;
    @FXML
    private TextField columnsField;
    private MainViewController mainViewController;
    private static final Logger logger = Logger.getLogger(SortController.class.getName());

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private void handleSortButtonAction() {
        try {
            String startRangeText = startRangeField.getText();
            String endRangeText = endRangeField.getText();
            String columnsText = columnsField.getText();

            Coordinate startCoordinate = mainViewController.getSheetManager().parseCoordinate(startRangeText);
            Coordinate endCoordinate = mainViewController.getSheetManager().parseCoordinate(endRangeText);
            List<String> columns = Arrays.asList(columnsText.split(",\\s*"));
            for(String column : columns) {
                column = column.toUpperCase();
            }

            Sheet sortedSheet = mainViewController.getSheetManager().getCurrentSheet()
                    .sortRangeByColumns(startCoordinate, endCoordinate, columns);

            showSortedSheetInPopup(sortedSheet);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            AlertController.showAlert("Error", e.getMessage());
        }
    }

    private void showSortedSheetInPopup(Sheet sortedSheet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../SimpleTableView/SimpleTableView.fxml")); // Update the path to your FXML file if necessary
            AnchorPane root = loader.load();
            SimpleTableView controller = loader.getController();

            // Prepare the data to be displayed in the popup TableView
            ObservableList<String[]> sortedData = FXCollections.observableArrayList();
            int numberOfRows = sortedSheet.getSheetLayout().getNumberOfRows();
            int numberOfColumns = sortedSheet.getSheetLayout().getNumberOfColumns();

            String[] headers = new String[numberOfColumns];
            for (int col = 0; col < numberOfColumns; col++) {
                headers[col] = "Column " + (col + 1);
            }
            sortedData.add(headers);

            for (int row = 0; row < numberOfRows; row++) {
                String[] rowData = new String[numberOfColumns];
                for (int col = 0; col < numberOfColumns; col++) {
                    Cell cell = sortedSheet.getCell(row, col);
                    rowData[col] = (cell != null && cell.getEffectiveValue() != null) ?
                            cell.getEffectiveValue().toString() : "";
                }
                sortedData.add(rowData);
            }

            controller.setData(sortedData);

            Stage popupStage = new Stage();
            popupStage.setTitle("Sorted Sheet View");
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.show();
        } catch (IOException e) {
            AlertController.showAlert("Error", e.getMessage());
        }
    }

}
