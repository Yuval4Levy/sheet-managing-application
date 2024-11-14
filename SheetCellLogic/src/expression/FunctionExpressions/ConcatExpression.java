package expression.FunctionExpressions;

import exceptions.CoordinateException;
import sheet.Sheet;
import cell.Cell;
import cell.CellType;
import cell.EffectiveValue;
import exceptions.ValueException;
import expression.Expression;
import sheet.SheetReadActions;
import static expression.ExpressionParser.parse;

import java.util.List;

public class ConcatExpression implements Expression {

    private Expression left;
    private Expression right;

    public ConcatExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws CoordinateException, IllegalArgumentException, ValueException {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Concat function requires two arguments");
        }

        left = parse(arguments.get(0), currentCell, sheet);
        right = parse(arguments.get(1), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws CoordinateException, ValueException {
        EffectiveValue leftValue = left.evaluateExpression(sheet);
        EffectiveValue rightValue = right.evaluateExpression(sheet);
        String leftValueString = leftValue.extractValue(String.class);
        String rightValueString = rightValue.extractValue(String.class);
        String result = STR."\{leftValueString} \{rightValueString}";

        return new EffectiveValue(result, CellType.STRING);
    }

    @Override
    public CellType getFunctionReturnType() { return CellType.STRING; }
}