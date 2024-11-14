package VersionSelectorController;

import SheetManager.SheetManager;
import MainViewController.MainViewController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionSelectorController {

    @FXML
    private ComboBox<String> versionComboBox;
    private static final Logger logger = Logger.getLogger(VersionSelectorController.class.getName());
    private SheetManager sheetManager;
    private MainViewController mainViewController;

    @FXML
    public void initialize() {
        if (sheetManager != null) {
            versionComboBox.getItems().addAll(sheetManager.getAvailableVersions());
        }

        versionComboBox.setOnAction(event -> {
            String selectedVersion = versionComboBox.getSelectionModel().getSelectedItem();

            if (selectedVersion != null) {
                loadVersion(selectedVersion);
            }
        });

        logger.log(Level.INFO, "Version selector initialized");
    }

    public void setSheetManager(SheetManager sheetManager) {
        this.sheetManager = sheetManager;
        if (versionComboBox != null) {
            versionComboBox.getItems().clear();
            versionComboBox.getItems().addAll(sheetManager.getAvailableVersions());

            logger.log(Level.INFO, "Populated versions:" + sheetManager.getAvailableVersions());
        }
        else {
            logger.log(Level.SEVERE, "Version selector combo box is null");
        }
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void loadVersion(String version) {
        if (mainViewController != null) {
            mainViewController.loadSheetVersion(version);
        }
        else {
            logger.log(Level.SEVERE,"MainViewController reference is null.");
        }
    }

    public void refresh() { setSheetManager(mainViewController.getSheetManager()); }
}