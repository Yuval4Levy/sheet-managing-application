package cell;

import exceptions.ValueException;

public interface EffectiveValueInterface {

    Object getValue();
    CellType getCellType();
    <T> T extractValue(Class<T> type) throws ValueException;

}
