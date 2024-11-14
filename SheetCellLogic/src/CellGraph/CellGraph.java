package CellGraph;

import cell.Cell;
import coordinate.Coordinate;
import sheet.Sheet;

import java.util.*;

public class CellGraph {
    private final Map<Coordinate, Cell> cellMap;

    public CellGraph(Sheet sheet) {
        this.cellMap = new HashMap<>();
        buildGraph(sheet);
    }

    private void buildGraph(Sheet sheet) {
        for (Cell cell : sheet.getAllCells()) {
            addCell(cell);
        }

        for (Cell cell : cellMap.values()) {
            for (Cell dependentCell : cell.getInfluencedCells()) {
                dependentCell.addDependencyCell(cell);
            }
        }
    }

    public void addCell(Cell cell) {
        cellMap.put(cell.getCoordinate(), cell);
    }

    public Cell getCell(Coordinate coordinate) {
        return cellMap.get(coordinate);
    }

    public List<Cell> topologicalSort() {
        List<Cell> sorted = new ArrayList<>();
        Set<Cell> visited = new HashSet<>();
        Set<Cell> visiting = new HashSet<>();

        for (Cell cell : cellMap.values()) {
            if (!visited.contains(cell)) {
                if (topologicalSortUtil(cell, visited, visiting, sorted)) {
                    return new ArrayList<>();
                }
            }
        }

        Collections.reverse(sorted);
        return sorted;
    }

    private boolean topologicalSortUtil(Cell cell, Set<Cell> visited, Set<Cell> visiting, List<Cell> sorted) {
        visiting.add(cell);

        for (Cell dependentCell : cell.getInfluencedCells()) {
            if (visiting.contains(dependentCell)) {
                return true;
            }
            if (!visited.contains(dependentCell)) {
                if (topologicalSortUtil(dependentCell, visited, visiting, sorted)) {
                    return true;
                }
            }
        }

        visiting.remove(cell);
        visited.add(cell);
        sorted.add(cell);

        return false;
    }

    public boolean detectCircularDependency(Cell cell) {
        Set<Cell> visited = new HashSet<>();
        Set<Cell> visiting = new HashSet<>();

        return detectCircularDependencyUtil(cell, visited, visiting);
    }

    private boolean detectCircularDependencyUtil(Cell cell, Set<Cell> visited, Set<Cell> visiting) {
        visiting.add(cell);

        for (Cell dependentCell : cell.getInfluencedCells()) {
            if (visiting.contains(dependentCell)) {
                return true;
            }
            if (!visited.contains(dependentCell)) {
                if (detectCircularDependencyUtil(dependentCell, visited, visiting)) {
                    return true;
                }
            }
        }

        visiting.remove(cell);
        visited.add(cell);

        return false;
    }
}