package coordinate;

import java.util.Objects;

public class Coordinate implements CoordinateInterface{

    private final int row;
    private final int column;

    public Coordinate(int row, int column) { this.row = row; this.column = column;    }

    @Override
    public int getRow() { return this.row; }

    @Override
    public int getColumn() { return this.column; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coordinate that = (Coordinate) o;

        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() { return Objects.hash(row, column); }
}
