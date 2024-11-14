package SimpleTableView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SimpleTableView {

    @FXML
    private TableView<String[]> tableView;

    private final ObservableList<String[]> data = FXCollections.observableArrayList();

    public void setData(ObservableList<String[]> newData) {
        // Set the new data
        data.setAll(newData);

        // Clear existing columns
        tableView.getColumns().clear();

        // Assuming the first row consists of headers if available
        if (!data.isEmpty()) {
            String[] headers = data.get(0);
            for (int i = 0; i < headers.length; i++) {
                TableColumn<String[], String> column = new TableColumn<>(headers[i]);
                final int colIndex = i;
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[colIndex]));
                tableView.getColumns().add(column);
            }
        }

        tableView.setItems(data);
    }

    public TableView<String[]> getTableView() {
        return tableView;
    }
}