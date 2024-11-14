package ResizeController;

import MainViewController.MainViewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ResizeController {

    @FXML
    private VBox resizeVbox;
    @FXML
    private ComboBox<String> alignmentComboBox;
    @FXML
    private ComboBox<String> columnSelectorComboBox;
    @FXML
    private Slider columnWidthSlider;
    @FXML
    private Slider rowHeightSlider;
    private MainViewController mainViewController;

    @FXML
    public void initialize() {
        setupAlignmentComboBox();
        setupRowHeightSlider();
        setupColumnWidthSlider();
    }

    public void setMainViewController(MainViewController mainView) { mainViewController = mainView; }

    private void setupAlignmentComboBox() {
        alignmentComboBox.getItems().addAll("Left", "Center", "Right");
        alignmentComboBox.getSelectionModel().select("Center"); // Default to "Center"
    }

    @FXML
    private void handleAlignmentChange() {
        String selectedAlignment = alignmentComboBox.getSelectionModel().getSelectedItem();
        String selectedColumn = columnSelectorComboBox.getSelectionModel().getSelectedItem();

        updateColumnAlignment(selectedAlignment, selectedColumn);
    }

    private void updateColumnAlignment(String alignment, String columnName) {
        for (TableColumn<String[], ?> column : mainViewController.getTableViewController().getTableView().getColumns()) {
            if (column.getText().equals(columnName)) {
                TableColumn<String[], String> typedColumn = (TableColumn<String[], String>) column;

                typedColumn.setCellFactory(tc -> new TableCell<String[], String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        }
                        else {
                            setText(item);
                            switch (alignment) {
                                case "Left":
                                    setStyle("-fx-alignment: CENTER_LEFT;");
                                    break;
                                case "Center":
                                    setStyle("-fx-alignment: CENTER;");
                                    break;
                                case "Right":
                                    setStyle("-fx-alignment: CENTER_RIGHT;");
                                    break;
                            }
                        }
                    }
                });

                break;
            }
        }
    }

    @FXML
    private void handleColumnSelection() {
        String selectedColumn = columnSelectorComboBox.getSelectionModel().getSelectedItem();

        if (selectedColumn != null) {
            updateColumnAlignment(alignmentComboBox.getSelectionModel().getSelectedItem(), selectedColumn);
        }
    }

    public void populateColumnSelector() {
        columnSelectorComboBox.getItems().clear();
        for (TableColumn<String[], ?> column : mainViewController.getTableViewController().getTableView().getColumns()) {
            columnSelectorComboBox.getItems().add(column.getText());
        }

        if (!columnSelectorComboBox.getItems().isEmpty()) {
            columnSelectorComboBox.getSelectionModel().select(0);
        }
    }

    private void setupRowHeightSlider() {
        rowHeightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleRowHeightChange(newValue.doubleValue());
        });
    }

    private void setupColumnWidthSlider() {
        columnWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleColumnWidthChange(newValue.doubleValue());
        });
    }

    @FXML
    private void handleRowHeightChange(double newHeight) {
        mainViewController.getTableViewController().getTableView().setFixedCellSize(newHeight);
        mainViewController.getTableViewController().getTableView().layout();
    }

    @FXML
    private void handleColumnWidthChange(double newWidth) {
        for (TableColumn<String[], ?> column : mainViewController.getTableViewController().getTableView().getColumns()) {
            column.setPrefWidth(newWidth);
        }

        mainViewController.getTableViewController().getTableView().layout();
    }


}


