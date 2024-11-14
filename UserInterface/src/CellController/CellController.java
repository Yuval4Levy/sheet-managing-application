package CellController;

import MainViewController.MainViewController;
import DTO.CellDTO;
import cell.Cell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.Collections;

public class CellController {

    @FXML
    private TextField cellIdField;
    @FXML
    private TextField originalValueField;
    @FXML
    private TextField effectiveValueField;
    @FXML
    private TextField cellVersionField;
    @FXML
    private Button updateValueButton;
    private MainViewController mainViewController;
    private ObservableList<ObservableList<String>> highlightedCells;

    public ObservableList<ObservableList<String>> getHighlightedCells() { return highlightedCells; }

    public void setMainViewController(MainViewController mainViewController) { this.mainViewController = mainViewController; }

    @FXML
    private void initialize() { updateValueButton.setOnAction(event -> handleUpdateValue()); }

    public void setupHighlightCells(int numRows, int numCols) {
        highlightedCells = FXCollections.observableArrayList();

        for (int i = 0; i < numRows; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();

            for (int j = 0; j < numCols; j++) {
                row.add("");
            }

            highlightedCells.add(row);
        }
    }

    public void updateCellInfo(int row, int col) {
        CellDTO cellDTO = mainViewController.getSheetManager().createCellDTO(row, col - 1);

        if (cellDTO != null) {
            cellIdField.setText(convertToCellId(col, row));
            originalValueField.setText(cellDTO.getOriginalValue() != null ? cellDTO.getOriginalValue() : "");
            effectiveValueField.setText(cellDTO.getEffectiveValue() != null ? cellDTO.getEffectiveValue().toString() : "");
            cellVersionField.setText(String.valueOf(cellDTO.getVersion()));
        }
        else {
            clearCellInfo();
            System.err.println("No cell found at row: " + row + "column: " + col);
        }
    }

    private String convertToCellId(int col, int row) {
        char columnLetter = (char) ('A' + col);
        return columnLetter + Integer.toString(row + 1);
    }

    private void clearCellInfo() {
        cellIdField.clear();
        originalValueField.clear();
        effectiveValueField.clear();
        cellVersionField.clear();
    }

    public void handleUpdateValue() {
        int selectedRow = mainViewController.getTableViewController().getLatestChosenCellRow();
        int colIndex = mainViewController.getTableViewController().getLatestChosenCellCol();
        String newOriginalValue = originalValueField.getText();

        if (selectedRow >= 0 && colIndex >= 1) {
            if (mainViewController.getSheetManager().getCurrentSheet().getCell(selectedRow, colIndex - 1) != null) {
                mainViewController.getSheetManager().recalculateCellsAndUpdateSheet(selectedRow, colIndex - 1, newOriginalValue);
            }
            else {
                mainViewController.getSheetManager().addCellToCurrentSheetAndUpdateVersion(selectedRow, colIndex - 1, newOriginalValue);
            }
        }

        if (mainViewController != null) {
            mainViewController.refreshTableView();
            mainViewController.getVersionSelectorController().refresh();
            cellVersionField.setText(String.valueOf(mainViewController.getSheetManager().getVersion())); // Update version field
        }
        else {
            System.err.println("Failed to update the original value.");

        }
        clearCellInfo();
    }

    public void highlightCells(int row, int col) {
        resetCellStyles();
        Cell selectedCell = mainViewController.getSheetManager().getCellFromSheet(row, col - 1);

        if (selectedCell != null) {
            for (Cell influenced : selectedCell.getInfluencedCells()) {
                int influencedRow = influenced.getCoordinate().getRow();
                int influencedCol = influenced.getCoordinate().getColumn();

                highlightCell(influencedRow, influencedCol, "lightgreen");
            }

            for (Cell influencing : selectedCell.getDepentendCells()) {
                int influencingRow = influencing.getCoordinate().getRow();
                int influencingCol = influencing.getCoordinate().getColumn();

                highlightCell(influencingRow, influencingCol, "lightblue");
            }
        }
    }

    private void resetCellStyles() {
        for (ObservableList<String> highlightedCell : highlightedCells) {
            Collections.fill(highlightedCell, "");
        }

        mainViewController.refreshTableView();
    }

    private void highlightCell(int row, int col, String color) {
        if (row >= 0 && row < mainViewController.getTableViewController().getData().size()
                && col >= 0 && col < mainViewController.getTableViewController().getData().getFirst().length) {
            highlightedCells.get(row).set(col, color);
        }

        mainViewController.refreshTableView();
    }
}