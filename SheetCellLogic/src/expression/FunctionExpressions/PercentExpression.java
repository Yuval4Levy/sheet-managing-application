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

public class PercentExpression implements Expression {


    private Expression left;
    private Expression right;

    public PercentExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws ValueException, CoordinateException {

        left = parse(arguments.get(0), currentCell, sheet);
        right = parse(arguments.get(1), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet)
            throws CoordinateException, ValueException {
        EffectiveValue leftValue = left.evaluateExpression(sheet);
        EffectiveValue rightValue = right.evaluateExpression(sheet);
        double leftValueDouble = leftValue.extractValue(Double.class);
        double rightValueDouble = rightValue.extractValue(Double.class);
        double result = (leftValueDouble * rightValueDouble) / 100;

        return new EffectiveValue(result, CellType.NUMERIC);
    }

    @Override
    public CellType getFunctionReturnType() { return CellType.NUMERIC; }
}
