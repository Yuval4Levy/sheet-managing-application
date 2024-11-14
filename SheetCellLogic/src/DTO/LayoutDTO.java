package DTO;

import layout.Layout;

public class LayoutDTO {
    private final int numberOfRows;
    private final int numberOfColumns;
    private final int columnWidthUnits;
    private final int rowsHeightUnits;

    public LayoutDTO(Layout layout) {
        numberOfRows = layout.getNumberOfRows();
        numberOfColumns = layout.getNumberOfColumns();
        columnWidthUnits = layout.getColumnWidthUnits();
        rowsHeightUnits = layout.getRowsHeightUnits();
    }

    public int getNumberOfRows() { return numberOfRows; }
    public int getNumberOfColumns() { return numberOfColumns; }
    public int getColumnWidthUnits() { return columnWidthUnits; }
    public int getRowsHeightUnits() { return rowsHeightUnits; }
}
