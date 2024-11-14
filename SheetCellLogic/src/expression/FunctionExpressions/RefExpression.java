package expression.FunctionExpressions;

import cell.*;
import coordinate.Coordinate;
import coordinate.CoordinateFactory;
import exceptions.CoordinateException;
import exceptions.SheetException;
import expression.Expression;
import sheet.SheetReadActions;
import sheet.Sheet;

import java.util.List;


public class RefExpression implements Expression {

    private Coordinate coordinate;

    public RefExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws CoordinateException, IllegalArgumentException {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Ref function requires one argument");
        }

        String cellRef = arguments.get(0);
        parseCellReference(cellRef);
        Cell influencingCell = sheet.getCell(coordinate.getRow(), coordinate.getColumn());
        influencingCell.addInfluencedCell(currentCell);
        currentCell.addDependencyCell(influencingCell);
    }

    private void parseCellReference(String cellRef) throws CoordinateException {
        cellRef = cellRef.toUpperCase();
        String columnPart = cellRef.replaceAll("[^A-Z]", "");
        String rowPart = cellRef.replaceAll("[^0-9]", "");

        if (columnPart.isEmpty() || rowPart.isEmpty()) {
            throw new IllegalArgumentException("Invalid cell reference: " + cellRef);
        }

        int row = Integer.parseInt(rowPart) - 1;
        int column = ConvertColumnLetterToIndex(columnPart);

        coordinate = CoordinateFactory.getCoordinate(row, column);
    }

    private int ConvertColumnLetterToIndex(String column) {
        int columnIndex = 0;

        for (char c : column.toCharArray()) {
            columnIndex = columnIndex * 26 + (c - 'A' + 1);
        }

        return columnIndex - 1;
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws SheetException, CoordinateException {
        try {
            return sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getEffectiveValue();
        } catch (SheetException ex) {
            System.out.println("Sheet Exception: " + ex.getMessage());

            return new EffectiveValue(null, null);
        }
    }

    @Override
    public CellType getFunctionReturnType() { return CellType.UNKNOWN; }
}