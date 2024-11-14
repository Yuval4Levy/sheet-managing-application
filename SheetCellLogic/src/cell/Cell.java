package cell;

import exceptions.CoordinateException;
import exceptions.ValueException;
import expression.Expression;
import expression.ExpressionParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DTO.CoordinateDTO;
import coordinate.Coordinate;
import sheet.Sheet;


public class Cell implements CellReadActions, CellWriteActions {

    private CellType cellType;
    private int version;
    private final Coordinate coordinate;
    private EffectiveValue effectiveValue;
    private String originalValue;
    private List<Cell> influencingOn;
    private List<Cell> dependsOn;
    private Sheet sheet;


    public Cell(int version, Coordinate coordinate, String originalValue, Sheet sheet) throws CoordinateException, ValueException {
        this.version = version;
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.sheet = sheet;
        this.influencingOn = new ArrayList<>();
        this.dependsOn = new ArrayList<>();
        calculateEffectiveValue(originalValue);
        this.cellType = effectiveValue.getCellType();

    }

    @Override
    public EffectiveValue getCellContent() { return effectiveValue; }

    @Override
    public boolean UpdateCell(String newValue) throws CoordinateException, ValueException {
        this.originalValue = newValue;
        return calculateEffectiveValue(newValue);
    }

    public void incrementVersion() { this.version++; }

    @Override
    public int getVersion() { return version; }

    public void setVersion(int version) { this.version = version; }

    @Override
    public Coordinate getCoordinate() { return coordinate; }

    public void setEffectiveValue(EffectiveValue value) { this.effectiveValue = value; }

    public void setSheet(Sheet newSheet) {this.sheet = newSheet; }

    public void setCellType(CellType value) { this.cellType = value; }

    public void setInfluencingOn() { this.influencingOn = new ArrayList<>(); }

    public void setDependsOn() { this.dependsOn = new ArrayList<>(); }

    public void setOriginalValue(String value) { this.originalValue = value; }

    @Override
    public EffectiveValue getEffectiveValue() { return effectiveValue; }

    public String getOriginalValue() { return originalValue; }

    @Override
    public List<Cell> getInfluencedCells() { return influencingOn; }

    public CellType getCellType() { return cellType; }


    public void addInfluencedCell(Cell cellToAdd) { this.influencingOn.add(cellToAdd); }

    public void addDependencyCell(Cell cellToAdd) { this.dependsOn.add(cellToAdd); }

    public boolean calculateEffectiveValue(String originalValue) throws CoordinateException, ValueException {
        EffectiveValue newValue;

        if (!(Objects.equals(originalValue, "EMPTY"))) {
            Expression expression = ExpressionParser.parse(originalValue, this, sheet);
            newValue = expression.evaluateExpression(sheet);
        }
        else{
            newValue = new EffectiveValue("", CellType.STRING);
        }
        if (newValue == this.effectiveValue) {
            return false;
        } else {
            this.effectiveValue = newValue;
            cellType = newValue.getCellType();

            return true;
        }
    }

    public List<CoordinateDTO> getInfluencedCoordinates() {
        List<CoordinateDTO> influencedCoordinates = new ArrayList<>();

        for (Cell influencedCell : influencingOn) {
            influencedCoordinates.add(new CoordinateDTO(influencedCell.getCoordinate()));
        }

        return influencedCoordinates;
    }

    public List<CoordinateDTO> getDependentCoordinates() {
        List<CoordinateDTO> dependentCoordinates = new ArrayList<>();

        for (Cell dependentCell : dependsOn) {
            dependentCoordinates.add(new CoordinateDTO(dependentCell.getCoordinate()));
        }

        return dependentCoordinates;
    }

    public List<Cell> getDepentendCells() { return dependsOn; }
}
