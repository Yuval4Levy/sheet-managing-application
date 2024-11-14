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

public class NotExpression implements Expression {

    private Expression operand;

    public NotExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws ValueException, CoordinateException {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("NOT function requires exactly one argument");
        }

        this.operand = parse(arguments.getFirst(), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet)
            throws CoordinateException, ValueException {
        EffectiveValue operandValue = operand.evaluateExpression(sheet);

        if (operandValue.getCellType() != CellType.BOOLEAN) {
            throw new ValueException("Operand for NOT function must be BOOLEAN");
        }

        boolean operandBoolean = operandValue.extractValue(Boolean.class);
        boolean result = !operandBoolean;

        return new EffectiveValue(result, CellType.BOOLEAN);
    }

    @Override
    public CellType getFunctionReturnType() {
        return CellType.BOOLEAN;
    }
}
