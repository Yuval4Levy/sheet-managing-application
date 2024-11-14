package expression.FunctionExpressions;

import cell.CellType;
import cell.EffectiveValue;
import expression.Expression;
import sheet.SheetReadActions;

public class IdentityExpression implements Expression {

    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue evaluateExpression(SheetReadActions sheet) {
        return new EffectiveValue(value, type);
    }

    @Override
    public CellType getFunctionReturnType() {
        return type;
    }
}
