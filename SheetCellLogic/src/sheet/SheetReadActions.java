package sheet;

import cell.Cell;
import exceptions.CoordinateException;

public interface SheetReadActions {
    Cell getCell(int row, int column) throws CoordinateException;
}
