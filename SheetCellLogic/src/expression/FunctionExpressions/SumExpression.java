package expression.FunctionExpressions;

import cell.Cell;
import cell.CellType;
import cell.EffectiveValue;
import exceptions.SheetRangeException;
import exceptions.ValueException;
import expression.Expression;
import range.Range;
import sheet.Sheet;
import sheet.SheetReadActions;

import java.util.List;

public class SumExpression implements Expression {
    private final Range range;

    public SumExpression(List<String> arguments, Cell currentCell, Sheet sheet) throws ValueException, SheetRangeException {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("SUM function requires one argument");
        }

        String rangeName = arguments.getFirst();
        try {
            this.range = sheet.getRangeByName(rangeName);
            for (Cell cell : range.getCells()) {
                currentCell.addDependencyCell(cell);
                cell.addInfluencedCell(currentCell);
            }
        } catch (SheetRangeException ex) {
            throw ex;
        }
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws ValueException {
        double sum = 0.0;
        for (Cell cell : range.getCells()) {
            EffectiveValue cellValue = cell.getEffectiveValue();
            if (cellValue.getCellType() == CellType.NUMERIC) {
                sum += cellValue.extractValue(Double.class);
            }
        }
        return new EffectiveValue(sum, CellType.NUMERIC);
    }

    @Override
    public CellType getFunctionReturnType() {
        return CellType.NUMERIC;
    }
}