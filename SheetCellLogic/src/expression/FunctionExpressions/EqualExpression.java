package expression.FunctionExpressions;

import cell.Cell;
import cell.CellType;
import cell.EffectiveValue;
import exceptions.CoordinateException;
import exceptions.ValueException;
import expression.Expression;
import sheet.Sheet;
import sheet.SheetReadActions;

import java.util.List;

import static expression.ExpressionParser.parse;

public class EqualExpression implements Expression {

    private Expression left;
    private Expression right;

    public EqualExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws ValueException, CoordinateException {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("EQUAL function requires exactly two arguments");
        }

        this.left = parse(arguments.get(0), currentCell, sheet);
        this.right = parse(arguments.get(1), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet)
            throws CoordinateException, ValueException {
        EffectiveValue leftValue = left.evaluateExpression(sheet);
        EffectiveValue rightValue = right.evaluateExpression(sheet);

        if (leftValue.getCellType() != rightValue.getCellType()) {
            return new EffectiveValue(false, CellType.BOOLEAN);
        }

        boolean result = leftValue.getValue().equals(rightValue.getValue());

        return new EffectiveValue(result, CellType.BOOLEAN);
    }

    @Override
    public CellType getFunctionReturnType() {
        return CellType.BOOLEAN;
    }
}
