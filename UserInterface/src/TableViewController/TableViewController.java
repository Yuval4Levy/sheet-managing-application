package TableViewController;

import MainViewController.MainViewController;
import coordinate.Coordinate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;import cell.Cell;


public class TableViewController {

    @FXML
    private TableView<String[]> tableView;
    private MainViewController mainViewController;
    private final ObservableList<String[]> data = FXCollections.observableArrayList();
    private static final Logger logger = Logger.getLogger(TableViewController.class.getName());


    @FXML
    private void initialize() {
        tableView.setItems(data);
    }

    public void setMainViewController(MainViewController mainView) {mainViewController = mainView;}

    public ObservableList<String[]> getData() {return data;}

    private int latestChosenCellRow;

    private int latestChosenCellCol;

    public TableView<String[]> getTableView() {return tableView;}

    public int getLatestChosenCellRow() {return latestChosenCellRow;}

    public int getLatestChosenCellCol() {return latestChosenCellCol;}

    public void setupTableView() {
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                TablePosition<String[], ?> pos = tableView.getFocusModel().getFocusedCell();
                latestChosenCellRow = pos.getRow();
                latestChosenCellCol = pos.getColumn();

                try {

                    Cell currentCell = mainViewController.getSheetManager().getCurrentSheet().getCell
                            (latestChosenCellRow, latestChosenCellCol - 1);

                    if (currentCell != null) {
                        Coordinate cellCoordinate = mainViewController.getSheetManager().getCurrentSheet().getCell
                                (latestChosenCellRow, latestChosenCellCol - 1).getCoordinate();
                        mainViewController.getColorController().resetBackgroundColorForSelectedCell(cellCoordinate);
                        mainViewController.getColorController().resetTextColorForSelectedCell(cellCoordinate);
                    }
                }
                catch (NullPointerException ex) {
                    logger.log(Level.SEVERE, "Cell not found", ex);
                }

                if (latestChosenCellRow >= 0 && latestChosenCellCol >= 0) {
                    tableView.getSelectionModel().select(latestChosenCellRow, tableView.getColumns().get(latestChosenCellCol));
                    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                        updateCellInfoFromSelection(latestChosenCellRow, latestChosenCellCol);
                        mainViewController.getCellController().highlightCells(latestChosenCellRow, latestChosenCellCol);
                    }));
                    timeline.play();
                }
            }
        });
    }

    private void updateCellInfoFromSelection(int rowIndex, int colIndex) {
        if (mainViewController.getCellController() != null && rowIndex >= 0 && colIndex >= 0) {
            mainViewController.getCellController().updateCellInfo(rowIndex, colIndex);
        }
    }

    public void updateColumnHeaders(int numberOfColumns) {
        tableView.getColumns().clear();

        for (int row = 0; row < data.size(); row++) {
            ObservableList<String> rowHighlights = FXCollections.observableArrayList();

            for (int col = 0; col < numberOfColumns; col++) {
                rowHighlights.add("");
            }

            mainViewController.getCellController().getHighlightedCells().add(rowHighlights);
        }

        TableColumn<String[], String> indexColumn = new TableColumn<>("Row Index");
        indexColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(tableView.getItems().indexOf(cellData.getValue()) + 1)));
        indexColumn.setPrefWidth(100);
        tableView.getColumns().add(indexColumn);

        for (int col = 0; col < numberOfColumns; col++) {
            TableColumn<String[], String> column = new TableColumn<>(mainViewController.convertColIndexToLetter(col));
            column.setPrefWidth(100);
            final int columnIndex = col;
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex]));
            column.setCellFactory(tc -> new TableCell<String[], String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        int rowIndex = getIndex();
                        Coordinate cellCoordinate = null;
                        Color textColor = Color.BLACK;
                        Color backgroundColor = Color.WHITE;
                        Cell cell = mainViewController.getSheetManager().getCurrentSheet().getCell
                                (rowIndex, columnIndex);

                        if (cell != null) {
                            cellCoordinate = cell.getCoordinate();
                        }

                        if (cellCoordinate != null) {
                            textColor = mainViewController.getColorController().getCellTextColorMap().getOrDefault
                                    (cellCoordinate, textColor);
                            backgroundColor = mainViewController.getColorController().getCellBackgroundColorMap().getOrDefault
                                    (cellCoordinate, backgroundColor);
                        }

                        if (backgroundColor.equals(Color.WHITE)) {
                            if (mainViewController.getCellController().getHighlightedCells() != null &&
                                    mainViewController.getCellController().getHighlightedCells().size() > rowIndex) {
                                String highlight = mainViewController.getCellController().getHighlightedCells().get
                                        (rowIndex).get
                                        (columnIndex);
                                if (!highlight.isEmpty()) {
                                    backgroundColor = Color.web(highlight);
                                }
                            }
                        }

                        String colorStyle = String.format("-fx-background-color: #%02X%02X%02X; -fx-text-fill: #%02X%02X%02X;",
                                (int) (backgroundColor.getRed() * 255),
                                (int) (backgroundColor.getGreen() * 255),
                                (int) (backgroundColor.getBlue() * 255),
                                (int) (textColor.getRed() * 255),
                                (int) (textColor.getGreen() * 255),
                                (int) (textColor.getBlue() * 255));
                        setStyle(colorStyle);
                    }
                }
            });

            tableView.getColumns().add(column);
        }

        tableView.setItems(data);
    }

    public void refreshTableView() {
        Platform.runLater(() -> {
            data.clear();
            int numberOfRows = mainViewController.getSheetManager().getCurrentSheet().getSheetLayout().getNumberOfRows();
            int numberOfColumns = mainViewController.getSheetManager().getCurrentSheet().getSheetLayout().getNumberOfColumns();

            for (int row = 0; row < numberOfRows; row++) {
                String[] rowData = new String[numberOfColumns];
                for (int col = 0; col < numberOfColumns; col++) {
                    Cell cell = mainViewController.getSheetManager().getCellFromSheet(row, col);
                    rowData[col] = (cell != null && cell.getEffectiveValue() != null) ?
                            cell.getEffectiveValue().toString() : "";
                }

                data.add(rowData);
            }

            updateColumnHeaders(numberOfColumns);
            mainViewController.getResizeController().populateColumnSelector();
            mainViewController.getVersionSelectorController().refresh();
            mainViewController.getRangeController().updateRangeComboBox();
            mainViewController.getFilterController().populateColumnComboBox();
        });
    }
}
