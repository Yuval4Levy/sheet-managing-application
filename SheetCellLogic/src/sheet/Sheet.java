package sheet;

import cell.Cell;
import cell.CellType;
import coordinate.Coordinate;
import CellGraph.CellGraph;
import coordinate.CoordinateFactory;
import exceptions.SheetRangeException;
import range.Range;
import exceptions.CoordinateException;
import exceptions.ValueException;

import java.util.*;
import java.util.stream.Collectors;

import layout.Layout;

import static SheetManager.SheetManager.ConvertStlColumnToInt;
import static SheetManager.SheetManager.fillInSheet;

public class Sheet implements SheetReadActions, SheetWriteActions {

    private String name;
    private final Map<Coordinate, Cell> cellMap = new HashMap<>();
    private CellGraph cellDependencyGraph;
    private Layout sheetLayout;
    private final Map<String, Range> ranges;

    public Sheet(int numberOfRows, int numberOfColumns, int columnWidthUnits, int rowsHeightUnits,
                 String sheetName) throws IllegalArgumentException {

        if (sheetName == null) {
            throw new IllegalArgumentException("Sheet name cannot be null");
        }

        this.name = sheetName; // No unique name generation here
        sheetLayout = new Layout(numberOfRows, numberOfColumns, columnWidthUnits, rowsHeightUnits);
        this.cellDependencyGraph = new CellGraph(this);
        ranges = new HashMap<>();
    }

    public Layout getSheetLayout() { return sheetLayout; }

    public String getName() { return name; }

    public Map<String, Range> getRanges() { return ranges; }

    public Sheet deepCopy(String baseName) throws CoordinateException, ValueException {
        Sheet copy = new Sheet(
                getSheetLayout().getNumberOfRows(),
                getSheetLayout().getNumberOfColumns(),
                getSheetLayout().getColumnWidthUnits(),
                getSheetLayout().getRowsHeightUnits(),
                baseName
        );

        fillInSheet(copy);

        for (Map.Entry<String, Range> entry : ranges.entrySet()) {
            Range range = entry.getValue();
            Coordinate newStart = copy.getCell(range.getStart().getRow(), range.getStart().getColumn()).getCoordinate();
            Coordinate newEnd = copy.getCell(range.getEnd().getRow(), range.getEnd().getColumn()).getCoordinate();
            copy.addRange(range.getName(), newStart, newEnd);
        }

        for (Map.Entry<Coordinate, Cell> entry : cellMap.entrySet()) {
            Coordinate coordinate = entry.getKey();
            Cell originalCell = entry.getValue();
            Cell copiedCell = copy.getCell(coordinate.getRow(),coordinate.getColumn());
            copiedCell.setSheet(copy);
            copiedCell.setVersion(originalCell.getVersion());
            copiedCell.setCellType(originalCell.getCellType());
            copiedCell.setEffectiveValue(originalCell.getEffectiveValue());
            copiedCell.setCellType(originalCell.getCellType());
            copiedCell.setDependsOn();
            for(Cell cell : originalCell.getDepentendCells())
            {
                copiedCell.addDependencyCell(copy.getCell(cell.getCoordinate().getRow(), cell.getCoordinate().getColumn()));
            }

            copiedCell.setInfluencingOn();
            for(Cell cell : originalCell.getInfluencedCells())
            {
                copiedCell.addInfluencedCell(copy.getCell(cell.getCoordinate().getRow(), cell.getCoordinate().getColumn()));
            }

            copiedCell.setOriginalValue(originalCell.getOriginalValue());
        }

        copy.cellDependencyGraph = new CellGraph(copy);
        for (Cell cell : copy.getAllCells()) {
            copy.cellDependencyGraph.addCell(cell);
        }

        return copy;
    }





    @Override
    public Cell getCell(int row, int col) {
        try {
            Coordinate coordinate = CoordinateFactory.getCoordinate(row, col);
            return cellMap.get(coordinate);
        } catch (CoordinateException e) {
            return null;
        }
    }

    public void addNewCell(int row, int column, String input) throws IndexOutOfBoundsException,
            CoordinateException, ValueException {
        int version;

        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException("tried to create a coordinate outside the sheet bounds");
        }

        Coordinate newCoordinate = CoordinateFactory.addNewCoordinate(row, column);
        if (cellMap.containsKey(newCoordinate)) {
            throw new CoordinateException("Cell is already in the sheet");
        }

        version = (Objects.equals(input, "EMPTY")) ? 0 : 1;
        Cell newCell = new Cell(version, newCoordinate, input, this);
        cellMap.put(newCoordinate, newCell);
    }

    @Override
    public void updateCellAndCalculateSheet(int row, int col, String newValue)
            throws IndexOutOfBoundsException, CoordinateException, IllegalStateException, ValueException {
        if (!isInBounds(row, col)) {
            throw new IndexOutOfBoundsException("tried to reach a coordinate outside the sheet bounds");
        }

        Coordinate coordinate = CoordinateFactory.getCoordinate(row, col);
        Cell cell = cellMap.get(coordinate);

        if (cell == null) {
            addNewCell(row, col, newValue);
        } else {
            if (cellDependencyGraph.detectCircularDependency(cell)) {
                throw new IllegalStateException("Circular dependency detected, cannot update cell");
            }

            boolean hasChanged = cell.UpdateCell(newValue);
            if (hasChanged) {
                updateDependentCells(cell, hasChanged);
            }
        }
    }

    private void updateDependentCells(Cell cell, boolean hasChanged) throws CoordinateException, ValueException {
        if (hasChanged) {
            cell.incrementVersion();
            List<Cell> influencedCells = new ArrayList<>(cell.getInfluencedCells());
            List<Cell> cellsToUpdate = new ArrayList<>();

            for (Cell dependentCell : influencedCells) {
                boolean dependentHasChanged = dependentCell.UpdateCell(dependentCell.getOriginalValue());
                if (dependentHasChanged) {
                    cellsToUpdate.add(dependentCell);
                }
            }

            for (Cell dependentCell : cellsToUpdate) {
                updateDependentCells(dependentCell, true);
            }
        }
    }

    public Collection<Cell> getAllCells() { return cellMap.values(); }

    private boolean isInBounds(int row, int col) {
        return (row >= 0 && row < getSheetLayout().getNumberOfRows() &&
                col >= 0 && col < getSheetLayout().getNumberOfColumns());
    }

    public void addRange(String name, Coordinate start, Coordinate end) throws SheetRangeException {
        if (ranges.containsKey(name)) {
            throw new SheetRangeException("Range with name: " + name + " already exists in this sheet.");
        }

        Range newRange = new Range(name, start, this, end);
        ranges.put(name, newRange);
    }

    public void removeRange(String name) throws SheetRangeException {
        if (!ranges.containsKey(name)) {
            throw new SheetRangeException("A range with name: " + name + " does not exist in this sheet.");
        }

        if(rangeInUse(name)) {
            throw new SheetRangeException("A range with name: " + name + " is in use in your sheet, cannot delete.");
        }

        ranges.remove(name);
    }

    private boolean rangeInUse(String name) {
        for(Cell cell : cellMap.values()) {
            if(cell.getOriginalValue().contains(name)){
                return true;
            }
        }

        return false;
    }

    public Range getRangeByName(String rangeName) throws SheetRangeException {
        Range range = ranges.get(rangeName);
        if(range == null) {
            throw new SheetRangeException("Range with name " + rangeName + "doesn't exist");
        }

        return range;
    }

    public Sheet sortRangeByColumns(Coordinate topLeft, Coordinate bottomRight, List<String> columnLetters) throws CoordinateException, ValueException {
        List<Integer> columnIndices = columnLetters.stream()
                .map(letter -> ConvertStlColumnToInt(letter))
                .collect(Collectors.toList());
        int startRow = topLeft.getRow();
        int endRow = bottomRight.getRow();
        List<List<Cell>> rowsToSort = new ArrayList<>();

        for (int row = startRow; row <= endRow; row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int col = topLeft.getColumn(); col <= bottomRight.getColumn(); col++) {
                rowCells.add(getCell(row, col));
            }
            rowsToSort.add(rowCells);
        }

        Comparator<List<Cell>> comparator = null;
        for (int colIndex : columnIndices) {
            Comparator<List<Cell>> colComparator = Comparator.comparing(row -> {
                if (colIndex < row.size()) {
                    Cell cell = row.get(colIndex);
                    if (cell != null && cell.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                        try {
                            return cell.getEffectiveValue().extractValue(Double.class);
                        } catch (ValueException e) {
                            return Double.MAX_VALUE;
                        }
                    }
                }
                return Double.MAX_VALUE;
            });
            comparator = comparator == null ? colComparator : comparator.thenComparing(colComparator);
        }

        if (comparator != null) {
            rowsToSort.sort(comparator);
        }

        Sheet sortedSheet = createStaticCopy(topLeft, bottomRight, this.getName() + "_sorted");

        for (int row = startRow; row <= endRow; row++) {
            List<Cell> sortedRow = rowsToSort.get(row - startRow);
            for (int col = topLeft.getColumn(), i = 0; col <= bottomRight.getColumn(); col++, i++) {
                Cell cell = sortedRow.get(i);
                if (cell != null) {
                    Cell staticCell = new Cell(cell.getVersion(), CoordinateFactory.getCoordinate(row, col), cell.getEffectiveValue().toString(), sortedSheet);
                    sortedSheet.cellMap.put(CoordinateFactory.getCoordinate(row, col), staticCell);
                }
            }
        }

        return sortedSheet;
    }


    public Sheet createStaticCopy(Coordinate topLeft, Coordinate bottomRight, String baseName) throws CoordinateException, ValueException {
        Sheet staticCopy = new Sheet(
                getSheetLayout().getNumberOfRows(),
                getSheetLayout().getNumberOfColumns(),
                getSheetLayout().getColumnWidthUnits(),
                getSheetLayout().getRowsHeightUnits(),
                baseName
        );

        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            for (int col = topLeft.getColumn(); col <= bottomRight.getColumn(); col++) {
                Cell originalCell = getCell(row, col);
                if (originalCell != null) {
                    Cell staticCell = new Cell(originalCell.getVersion(), CoordinateFactory.getCoordinate(row, col), originalCell.getEffectiveValue().toString(), staticCopy);
                    staticCopy.cellMap.put(CoordinateFactory.getCoordinate(row, col), staticCell);
                }
            }
        }

        return staticCopy;
    }

    public ArrayList<Cell> filterRange(Coordinate start, Coordinate end, List<Cell> valuesToCompare)
    {
        ArrayList<Cell> filteredCells = new ArrayList<>();
        Range filterRange = new Range("temporary range", start, this, end);

        for (Cell entry : filterRange.getCells()) {
            for (Cell compare : valuesToCompare) {
                if (entry.getEffectiveValue().getValue().equals(compare.getEffectiveValue().getValue())) {
                    filteredCells.add(entry);
                }
            }
        }

        return filteredCells;
    }
}
