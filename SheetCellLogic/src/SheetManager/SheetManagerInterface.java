package SheetManager;
import exceptions.SheetCreationException;
import exceptions.ValueException;
import sheet.Sheet;
import cell.Cell;

import java.util.List;

public interface SheetManagerInterface {
    int getVersion();
    void setVersion(int version);
    Sheet getCurrentSheet();
    List<Sheet> getSheets();
    void updateVersion(Sheet sheet);
    Cell getCellFromSheet(int row, int column);
    boolean recalculateCellsAndUpdateSheet(int rowOfCell, int colOfCell,
                                           String input) throws ValueException;
    boolean createNewSheet(int numberOfRows, int numberOfColumns, int columnWidthUnits,
                           int rowsHeightUnits, String name);
}
