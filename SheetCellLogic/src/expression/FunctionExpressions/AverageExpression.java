package expression.FunctionExpressions;

import cell.Cell;
import cell.CellType;
import cell.EffectiveValue;
import exceptions.ValueException;
import expression.Expression;
import range.Range;
import sheet.Sheet;
import sheet.SheetReadActions;

import java.util.List;

public class AverageExpression implements Expression {
    private final Range range;

    public AverageExpression(List<String> arguments, Cell currentCell, Sheet sheet) throws ValueException {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("AVERAGE function requires one argument");
        }

        String rangeName = arguments.get(0);
        try {
            this.range = sheet.getRangeByName(rangeName);
            for(Cell cell : range.getCells()) {
                currentCell.addDependencyCell(cell);
                cell.addInfluencedCell(currentCell);
            }
        } catch (Exception e) {
            throw new ValueException("Invalid range name: " + rangeName);
        }
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws ValueException {
        double sum = 0.0;
        int count = 0;
        for (Cell cell : range.getCells()) {
            EffectiveValue cellValue = cell.getEffectiveValue();
            if (cellValue.getCellType() == CellType.NUMERIC) {
                sum += cellValue.extractValue(Double.class);
                count++;
            }
        }
        double average = count == 0 ? 0 : sum / count;
        return new EffectiveValue(average, CellType.NUMERIC);
    }

    @Override
    public CellType getFunctionReturnType() {
        return CellType.NUMERIC;
    }
}
