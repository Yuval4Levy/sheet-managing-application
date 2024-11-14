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


public class SubExpression implements Expression {

    private Expression source;
    private Expression startIndex;
    private Expression endIndex;

    public SubExpression(List<String> arguments, Cell currentCell, Sheet sheet)
            throws CoordinateException, IllegalArgumentException, ValueException {
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("Sub function requires three arguments: source, start index, and end index");
        }

        source = parse(arguments.get(0), currentCell, sheet);
        startIndex = parse(arguments.get(1), currentCell, sheet);
        endIndex = parse(arguments.get(2), currentCell, sheet);
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) throws CoordinateException, ValueException {
        EffectiveValue sourceValue = source.evaluateExpression(sheet);
        EffectiveValue startValue = startIndex.evaluateExpression(sheet);
        EffectiveValue endValue = endIndex.evaluateExpression(sheet);
        String sourceString = sourceValue.extractValue(String.class);
        int start = startValue.extractValue(Integer.class);
        int end = endValue.extractValue(Integer.class);

        if (start < 0 || end > sourceString.length() || start >= end) {
            throw new IllegalArgumentException("Invalid start or end index for substring");
        }

        String result = sourceString.substring(start, end);

        return new EffectiveValue(result, CellType.STRING);
    }

    @Override
    public CellType getFunctionReturnType() { return CellType.STRING; }
}