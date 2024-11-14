package DTO;

import coordinate.Coordinate;

import java.util.Objects;

public class CoordinateDTO {

    private final int row;
    private final String column;

    public CoordinateDTO(Coordinate coordinate) {
        this.row = coordinate.getRow();
        this.column = ConvertCoordinateColumnToString(coordinate.getColumn());
    }

    public int getRow() { return row; }

    public String getColumn() { return column; }

    public static String ConvertCoordinateColumnToString(int Column) { return String.valueOf((char) ('A' + Column)); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateDTO that = (CoordinateDTO) o;

        return row == that.row && column.equals(that.column);
    }

    @Override
    public int hashCode() { return Objects.hash(row, column); }

    @Override
    public String toString() { return column + (row + 1); }

 }
