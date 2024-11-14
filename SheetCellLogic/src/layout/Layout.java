package layout;

public class Layout {

    private final int numberOfRows;
    private final int numberOfColumns;
    private final int columnWidthUnits;
    private final int rowsHeightUnits;

    public Layout(int numberOfRows, int numberOfColumns, int columnWidthUnits, int rowsHeightUnits) {
        if (numberOfRows <= 0 || numberOfColumns <= 0) {
            throw new IllegalArgumentException("Number of rows and columns must be greater than 0");
        }
        if (numberOfRows > 51 || numberOfColumns > 21) {
            throw new IllegalArgumentException("Number of rows must be smaller than 51 and columns must be smaller than 21");
        }

        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.columnWidthUnits = columnWidthUnits;
        this.rowsHeightUnits = rowsHeightUnits;
    }

    public int getNumberOfRows() { return numberOfRows; }
    public int getNumberOfColumns() { return numberOfColumns; }
    public int getColumnWidthUnits() { return columnWidthUnits; }
    public int getRowsHeightUnits() { return rowsHeightUnits; }

}
