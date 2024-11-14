package cell;

import exceptions.CoordinateException;
import exceptions.ValueException;

public interface CellWriteActions {
    boolean UpdateCell(String newValue) throws CoordinateException, ValueException;
}
