package CellGraph;

import cell.Cell;

import java.util.List;

public interface CellGraphInterface {

    List<Cell> topologicalSort();
    void initializeGraph();
}
