package cell;

import coordinate.Coordinate;

import java.util.List;

public interface CellReadActions {

    Object getCellContent();
    Coordinate getCoordinate();
    int getVersion();
    EffectiveValue getEffectiveValue();
    List<Cell> getInfluencedCells();
    String getOriginalValue();

}
