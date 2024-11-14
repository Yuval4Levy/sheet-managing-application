package sheet;
import exceptions.CoordinateException;
import exceptions.SheetException;
import exceptions.ValueException;

public interface SheetWriteActions {
    void updateCellAndCalculateSheet(int row, int column, String value)
            throws CoordinateException, SheetException, ValueException;
}
