package expression;

import cell.CellType;
import cell.EffectiveValue;
import exceptions.CoordinateException;
import exceptions.SheetException;
import exceptions.ValueException;
import sheet.SheetReadActions;

public interface Expression {
    CellType getFunctionReturnType();
    EffectiveValue evaluateExpression(SheetReadActions sheet) throws SheetException, CoordinateException, ValueException;

}
