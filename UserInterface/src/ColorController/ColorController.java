package ColorController;

import MainViewController.MainViewController;
import coordinate.Coordinate;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import range.Range;

import java.util.HashMap;
import java.util.Map;

public class ColorController {

    @FXML
    private VBox colorPickerVBox;

    @FXML
    private Button revertButton;

    @FXML
    private ColorPicker textColorPicker;

    @FXML
    private ColorPicker backgroundColorPicker;

    private MainViewController mainViewController;
    private Map<Coordinate, Color> cellTextColorMap = new HashMap<>();
    private Map<Coordinate, Color> cellBackgroundColorMap = new HashMap<>();

    @FXML
    public void initialize() {

        textColorPicker.setValue(Color.BLACK);
        backgroundColorPicker.setValue(Color.WHITE);
    }

    public Map<Coordinate, Color> getCellTextColorMap() {return cellTextColorMap;}
    public Map<Coordinate, Color> getCellBackgroundColorMap() {return cellBackgroundColorMap;}

    public void postInitialize() {
        textColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && mainViewController != null) {
                updateTextColorForSelectedCell(newValue);
            }
        });

        backgroundColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && mainViewController != null) {
                updateBackgroundColorForSelectedCell(newValue);
            }
        });
    }

    public void resetBackgroundColorForSelectedCell(Coordinate coordinate) {
        Color backgroundColor = mainViewController.getColorController().getCellBackgroundColorMap().getOrDefault(coordinate, Color.WHITE);

        backgroundColorPicker.setValue(backgroundColor);

    }

    public void resetTextColorForSelectedCell(Coordinate coordinate) {
        Color textColor = mainViewController.getColorController().getCellTextColorMap().getOrDefault(coordinate, Color.BLACK);

        textColorPicker.setValue(textColor);
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void updateCellColor(int rowIndex, int colIndex, Color textColor, Color backgroundColor) {
        Coordinate key = mainViewController.getSheetManager().getCurrentSheet().getCell(rowIndex,colIndex).getCoordinate();
        cellTextColorMap.put(key, textColor);
        cellBackgroundColorMap.put(key, backgroundColor);

        applyCellColors(rowIndex, colIndex, textColor, backgroundColor);
    }

    public Color getCellTextColor(int rowIndex, int colIndex) {
        Coordinate key = mainViewController.getSheetManager().getCurrentSheet().getCell(rowIndex,colIndex).getCoordinate();
        return cellTextColorMap.getOrDefault(key, Color.BLACK); // Default text color
    }

    public Color getCellBackgroundColor(int rowIndex, int colIndex) {
        Coordinate key = mainViewController.getSheetManager().getCurrentSheet().getCell(rowIndex,colIndex).getCoordinate();
        return cellBackgroundColorMap.getOrDefault(key, Color.WHITE); // Default background color
    }

    public void updateTextColorForSelectedCell(Color color) {
        int rowIndex = mainViewController.getTableViewController().getLatestChosenCellRow();
        int colIndex = mainViewController.getTableViewController().getLatestChosenCellCol() - 1;
        updateCellColor(rowIndex, colIndex, color, getCellBackgroundColor(rowIndex, colIndex));
    }

    public void updateBackgroundColorForSelectedCell(Color color) {
        int rowIndex = mainViewController.getTableViewController().getLatestChosenCellRow();
        int colIndex = mainViewController.getTableViewController().getLatestChosenCellCol() - 1;
        updateCellColor(rowIndex, colIndex, getCellTextColor(rowIndex, colIndex), color);
    }

    public void highlightChosenRange(Range range) {
        Color highlightColor = Color.YELLOW;

        Map<Coordinate, Color> originalBackgroundColors = new HashMap<>();
        for (Coordinate coord : range.getCoordinates()) {
            originalBackgroundColors.put(coord, getCellBackgroundColor(coord.getRow(), coord.getColumn()));
            updateCellColor(coord.getRow(), coord.getColumn(), getCellTextColor(coord.getRow(), coord.getColumn()), highlightColor);
        }

        mainViewController.refreshTableView();

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            for (Map.Entry<Coordinate, Color> entry : originalBackgroundColors.entrySet()) {
                Coordinate coord = entry.getKey();
                Color originalColor = entry.getValue();
                updateCellColor(coord.getRow(), coord.getColumn(), getCellTextColor(coord.getRow(), coord.getColumn()), originalColor);
            }
            mainViewController.refreshTableView();
        });
        pause.play();
    }

    public void applyCellColors(int rowIndex, int colIndex, Color textColor, Color backgroundColor) {

        String colorStyle = String.format("-fx-background-color: #%02X%02X%02X; -fx-text-fill: #%02X%02X%02X;",
                (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255),
                (int) (textColor.getRed() * 255),
                (int) (textColor.getGreen() * 255),
                (int) (textColor.getBlue() * 255));

        mainViewController.refreshTableView();
    }

    @FXML
    private void handleRevertButton() {

        for (Map.Entry<Coordinate, Color> entry : cellTextColorMap.entrySet()) {
            Coordinate coordinate = entry.getKey();
            cellTextColorMap.put(coordinate, javafx.scene.paint.Color.BLACK);
        }

        for (Map.Entry<Coordinate, Color> entry : cellTextColorMap.entrySet()) {
            Coordinate coordinate = entry.getKey();
            cellBackgroundColorMap.put(coordinate, Color.WHITE);
        }

        mainViewController.refreshTableView();
    }
}
