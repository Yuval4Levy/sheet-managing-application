package expression;
import cell.CellType;
import exceptions.CoordinateException;
import exceptions.ValueException;
import expression.FunctionExpressions.*;
import sheet.Sheet;
import cell.Cell;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {

    public static Expression parse(String input, Cell currentCell, Sheet sheet) throws CoordinateException, ValueException {
        if (input.startsWith("{") && input.endsWith("}")) {
            List<String> arguments = parseFunctionActivation(input, currentCell, sheet);
            String functionName = arguments.get(0);

            return switch (functionName.toLowerCase()) {
                case "abs" -> new AbsExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "minus" -> new MinusExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "plus" -> new PlusExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "pow" -> new PowExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "mod" -> new ModExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "times" -> new TimesExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "divide" -> new DivideExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "concat" -> new ConcatExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "ref" -> new RefExpression(arguments.subList(1, arguments.size()),currentCell, sheet);
                case "sub" -> new SubExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "sum" -> new SumExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "average" -> new AverageExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "percent" -> new PercentExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "equal" -> new EqualExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "not" -> new NotExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "bigger" -> new BiggerExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "less" -> new LessExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "or" -> new OrExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "and" -> new AndExpression(arguments.subList(1, arguments.size()), currentCell, sheet);
                case "if" -> new IfExpression(arguments.subList(1, arguments.size()), currentCell, sheet);

                default -> new IdentityExpression(input, CellType.STRING);
            };
        } else {
            return parseRegularInput(input, currentCell, sheet);
        }
    }

    private static List<String> parseFunctionActivation(String input, Cell currentCell, Sheet sheet)
            throws CoordinateException, ValueException {
        String trimmedInput = input.substring(1, input.length() - 1).trim();
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArgument = new StringBuilder();
        int nestedFunctionLevel = 0;

        for (int i = 0; i < trimmedInput.length(); i++) {
            char currentChar = trimmedInput.charAt(i);

            if (currentChar == '{') {
                nestedFunctionLevel++;
                currentArgument.append(currentChar);
            } else if (currentChar == '}') {
                nestedFunctionLevel--;
                currentArgument.append(currentChar);
            } else if (currentChar == ',' && nestedFunctionLevel == 0) {
                arguments.add(currentArgument.toString().trim());
                currentArgument.setLength(0); // Reset for the next argument
            } else {
                currentArgument.append(currentChar);
            }
        }

        if (currentArgument.length() > 0) {
            arguments.add(currentArgument.toString().trim());
        }

        return arguments;
    }

    private static Expression parseRegularInput(String input, Cell currentCell, Sheet sheet)
            throws CoordinateException, ValueException {
        if (isCellReference(input)) {
            int row = (Integer.parseInt(input.replaceAll("[A-Z]", ""))) - 1;
            int column = input.charAt(0) - 'A';
            Cell referencedCell = sheet.getCell(row, column);

            if (referencedCell == null) {
                throw new CoordinateException("Invalid cell reference");
            }

            return new IdentityExpression(referencedCell.getEffectiveValue().getValue(), referencedCell.getCellType());
        }

        try {
            double doubleValue = Double.parseDouble(input);
            return new IdentityExpression(doubleValue, CellType.NUMERIC);
        } catch (NumberFormatException e) {
            if (input.equalsIgnoreCase("true")) {
                return new IdentityExpression(true, CellType.BOOLEAN); // Store as uppercase true
            }
            else if (input.equalsIgnoreCase("false")) {
                return new IdentityExpression(false, CellType.BOOLEAN); // Store as uppercase false

            }
            else {
                return new IdentityExpression(input, CellType.STRING);
            }
        }
    }

    private static boolean isCellReference(String input) { return input.matches("^[A-Z][1-9][0-9]*$"); }
}
