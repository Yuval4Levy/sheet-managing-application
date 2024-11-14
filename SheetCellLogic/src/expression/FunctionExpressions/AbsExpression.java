package expression.FunctionExpressions;

import exceptions.CoordinateException;
import sheet.Sheet;
import cell.CellType;
import cell.EffectiveValue;
import exceptions.ValueException;
import expression.Expression;
import sheet.SheetReadActions;
import cell.Cell;
import static expression.ExpressionParser.parse;

import java.util.List;

public class AbsExpression implements Expression {

    private Expression left;

    public AbsExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws CoordinateException, IllegalArgumentException, ValueException {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Abs function requires one arguments");
        }

        left = parse(arguments.get(0), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws ValueException, CoordinateException {
        EffectiveValue leftValue = left.evaluateExpression(sheet);
        double leftValueDouble = leftValue.extractValue(Double.class);
        double result = Math.abs(leftValueDouble);

        return new EffectiveValue(result, CellType.NUMERIC);
    }

    @Override
    public CellType getFunctionReturnType() { return CellType.NUMERIC; }
}
