package range;

import cell.Cell;
import coordinate.Coordinate;
import exceptions.CoordinateException;
import exceptions.SheetRangeException;
import sheet.Sheet;

import java.util.ArrayList;
import java.util.List;

public class Range {

    private String name;
    private Coordinate start;
    private Coordinate end;
    private ArrayList<Cell> cells;
    private Sheet sheet;

    public Range(){}

    public Range(String name, Coordinate start, Sheet sheet, Coordinate end) throws SheetRangeException {
        this.name = name;
        this.start = start;
        this.end = end;
        this.sheet = sheet;

        try {
            validateRange();
            populateCells();
        }
        catch (CoordinateException e) {
            throw new SheetRangeException(e.getMessage());
        }

    }

    public String getName() { return name; }

    public Coordinate getStart() { return start; }

    public Coordinate getEnd() { return end; }

    public List<Cell> getCells() { return cells; }

    private void validateRange() throws CoordinateException {
        boolean isHorizontal = start.getRow() == end.getRow();
        boolean isVertical = start.getColumn() == end.getColumn();
        boolean isDiagonal = Math.abs(start.getRow() - end.getRow()) == Math.abs(start.getColumn() - end.getColumn());

        if (!(isHorizontal || isVertical || isDiagonal)) {
            throw new CoordinateException("Invalid range: A range must be horizontal, vertical, or diagonal.");
        }
    }

    private void populateCells() {
        cells = new ArrayList<>();
        int rowStart = start.getRow();
        int rowEnd = end.getRow();
        int colStart = start.getColumn();
        int colEnd = end.getColumn();

        if (rowStart == rowEnd) {
            for (int col = Math.min(colStart, colEnd); col <= Math.max(colStart, colEnd); col++) {
                Cell cell = sheet.getCell(rowStart, col);

                if (cell != null) {
                    cells.add(cell);
                }
            }
        }
        else if (colStart == colEnd) {
            for (int row = Math.min(rowStart, rowEnd); row <= Math.max(rowStart, rowEnd); row++) {
                Cell cell = sheet.getCell(row, colStart);
                if (cell != null) {
                    cells.add(cell);
                }
            }
        }
        else {
            int rowStep = (rowEnd > rowStart) ? 1 : -1;
            int colStep = (colEnd > colStart) ? 1 : -1;

            for (int row = rowStart, col = colStart;
                 rowStep > 0 ? row <= rowEnd : row >= rowEnd;
                 row += rowStep, col += colStep) {
                Cell cell = sheet.getCell(row, col);
                if (cell != null) {
                    cells.add(cell);
                }
            }
        }
    }

    public Coordinate[] getCoordinates() {
        Coordinate[] coordinates = new Coordinate[cells.size()];

        for (int i = 0; i < cells.size(); i++) {
            coordinates[i] = cells.get(i).getCoordinate();
        }

        return coordinates;
    }
}
