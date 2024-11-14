package MainViewController;

import AlertController.AlertController;
import CellController.CellController;
import ColorController.ColorController;
import FileLoaderController.FileLoaderController;
import FilterController.FilterController;
import RangeController.RangeController;
import ResizeController.ResizeController;
import SheetManager.SheetManager;
import SortController.SortController;
import TableViewController.TableViewController;
import VersionSelectorController.VersionSelectorController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewController {


    @FXML
    private HBox cellControllerBox;
    @FXML
    private Label sheetTitleLabel;
    @FXML
    private VBox filterVBox;
    @FXML
    private AnchorPane rangeVBox;
    @FXML
    private VBox versionSelectorBox;
    @FXML
    private VBox colorControllerBox;
    @FXML
    private VBox resizeVBox;
    @FXML
    private HBox fileLoaderHBox;
    @FXML
    private VBox sortVBox;
    @FXML
    private AnchorPane tableView;
    private VersionSelectorController versionSelectorController;
    private ResizeController resizeController;
    private CellController cellController;
    private FileLoaderController fileLoaderController;
    private RangeController rangeController;
    private FilterController filterController;
    private TableViewController tableViewController;
    private ColorController colorController;
    private SortController sortController;
    private final ObservableList<String[]> data = FXCollections.observableArrayList();
    private SheetManager sheetManager;
    private static final Logger logger = Logger.getLogger(MainViewController.class.getName());

    @FXML
    public void initialize() {
        sheetManager = new SheetManager();
        loadVersionSelector();
        loadFileLoaderController();
        loadCellController();
        loadColorController();
        loadResizeController();
        loadTableViewController();
        loadRangeController();
        loadFilterController();
        loadSortController();
        tableViewController.setupTableView();
    }

    public SheetManager getSheetManager() { return sheetManager; }

    public TableViewController getTableViewController() { return tableViewController; }

    public ResizeController getResizeController() { return resizeController; }

    public ColorController getColorController() { return colorController; }

    public VersionSelectorController getVersionSelectorController() { return versionSelectorController; }

    public RangeController getRangeController() { return rangeController; }

    public FilterController getFilterController() { return filterController; }

    public static void loadSelf(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Sheet Cell App");
        primaryStage.setScene(new Scene(root, 1000, 850));
        primaryStage.show();
    }


    public void loadTableViewController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TableViewController/TableView.fxml"));
            AnchorPane tableLoader = loader.load();
            tableViewController = loader.getController();
            tableViewController.setMainViewController(this);
            AnchorPane.setTopAnchor(tableView, 0.0);
            AnchorPane.setRightAnchor(tableView, 0.0);
            AnchorPane.setBottomAnchor(tableView, 0.0);
            AnchorPane.setLeftAnchor(tableView, 0.0);
            tableView.getChildren().add(tableLoader);
            logger.log(Level.INFO, "TableViewController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load TableViewController", ex);
            AlertController.showAlert("Failed to load TableViewController", ex.getMessage());
        }

    }

    public void loadFilterController()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FilterController/FilterController.fxml"));
            VBox filterLoader = loader.load();
            filterController = loader.getController();
            filterController.setMainViewController(this);
            filterVBox.getChildren().add(filterLoader);
            logger.log(Level.INFO, "SortController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load SortController", ex);
            AlertController.showAlert("Failed to load SortController", ex.getMessage());
        }
    }

    public void loadSortController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SortController/SortController.fxml"));
            VBox sortLoader = loader.load();
            sortController = loader.getController();
            sortController.setMainViewController(this);
            sortVBox.getChildren().add(sortLoader);
            logger.log(Level.INFO, "SortController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load SortController", ex);
        }

    }


    public void loadRangeController(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RangeController/RangeController.fxml"));
            AnchorPane rangeLoader = loader.load();
            rangeController = loader.getController();
            rangeController.setMainViewController(this);
            rangeVBox.getChildren().add(rangeLoader);
            logger.log(Level.INFO, "RangeController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load rangeController", ex);
            AlertController.showAlert("Failed to load rangeController", ex.getMessage());
        }
    }


    public void loadFileLoaderController(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FileLoaderController/FileLoader.fxml"));
            HBox fileLoader = loader.load();
            fileLoaderController = loader.getController();
            fileLoaderController.setMainViewController(this);
            fileLoaderHBox.getChildren().add(fileLoader);
            logger.log(Level.INFO, "FileLoader loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load FileLoaderController", ex);
            AlertController.showAlert("Failed to load FileLoaderController", ex.getMessage());
        }

    }

    private void loadResizeController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResizeController/ResizeController.fxml"));
            VBox resizeLoader = loader.load();
            resizeController = loader.getController();
            resizeController.setMainViewController(this);
            resizeVBox.getChildren().add(resizeLoader);
            logger.log(Level.INFO, "ResizeController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load ResizeController", ex);
            AlertController.showAlert("Failed to load ResizeController", ex.getMessage());
        }
    }

    private void loadColorController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ColorController/ColorController.fxml")); // Make sure the path is correct
            VBox colorControllerVbox = loader.load();
            colorController = loader.getController();
            colorController.setMainViewController(this);
            colorController.postInitialize();
            colorControllerBox.getChildren().add(colorControllerVbox);
            logger.log(Level.INFO, "ColorController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load ColorController", ex);
            AlertController.showAlert("Failed to load ColorController: ", ex.getMessage());
        }
    }


    private void loadCellController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CellController/CellController.fxml"));
            VBox cellControllerBox = loader.load();
            cellController = loader.getController();
            cellController.setMainViewController(this);
            this.cellControllerBox.getChildren().add(cellControllerBox);
            logger.log(Level.INFO, "CellController loaded");
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load CellController", ex);
            AlertController.showAlert("Failed to load CellController: ", ex.getMessage());
        }
    }

    public void updateUIAfterLoad() {
        Platform.runLater(() -> {
            String sheetName = sheetManager.getCurrentSheet() != null ?
                    sheetManager.getCurrentSheet().getName() : "No sheet loaded";

            setTitle(sheetName);
            cellController.setupHighlightCells(sheetManager.getCurrentSheet().getSheetLayout().getNumberOfRows(),
                    sheetManager.getCurrentSheet().getSheetLayout().getNumberOfColumns());
            clearStatusMessageAfterDelay();
            refreshTableView();
        });
    }

    private void clearStatusMessageAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Failed to load clear status message: ", ex);
            }
        }).start();
    }


    public void refreshTableView() { tableViewController.refreshTableView(); }

    public static String convertColIndexToLetter(int index) { return String.valueOf((char) ('A' + index)); }


    public void setTitle(String title) {
        Platform.runLater(() -> {
            sheetTitleLabel.setText(title);
        });
    }

    public CellController getCellController(){ return cellController; }

    private void loadVersionSelector() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VersionSelectorController/VersionSelector.fxml"));
            VBox versionSelector = loader.load();
            versionSelectorController = loader.getController();

            versionSelectorController.setSheetManager(sheetManager);
            versionSelectorController.setMainViewController(this);
            versionSelectorBox.getChildren().add(versionSelector);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load version selector: ", ex);
            AlertController.showAlert("Failed to load VersionSelector", ex.getMessage());
        }
    }

    public void loadSheetVersion(String version) {
        sheetManager.SetSheetAsCurrentSheet(version);
        refreshTableView();
    }
}