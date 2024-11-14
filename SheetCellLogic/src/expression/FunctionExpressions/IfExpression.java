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

public class IfExpression implements Expression {

    private Expression condition;
    private Expression thenExpression;
    private Expression elseExpression;

    public IfExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws ValueException, CoordinateException {
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("IF function requires exactly three arguments");
        }

        this.condition = parse(arguments.get(0), currentCell, sheet);
        this.thenExpression = parse(arguments.get(1), currentCell, sheet);
        this.elseExpression = parse(arguments.get(2), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet)
            throws CoordinateException, ValueException {
        EffectiveValue conditionValue = condition.evaluateExpression(sheet);

        // Ensure the condition value is of BOOLEAN type
        if (conditionValue.getCellType() != CellType.BOOLEAN) {
            throw new ValueException("Condition for IF function must be BOOLEAN");
        }

        boolean conditionResult = conditionValue.extractValue(Boolean.class);
        if (conditionResult) {
            return thenExpression.evaluateExpression(sheet);
        }
        else {
            return elseExpression.evaluateExpression(sheet);
        }
    }

    @Override
    public CellType getFunctionReturnType() {
        return thenExpression.getFunctionReturnType();
    }
}
