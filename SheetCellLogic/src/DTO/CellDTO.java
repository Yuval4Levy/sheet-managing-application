package DTO;

import cell.Cell;
import cell.CellType;
import cell.EffectiveValue;

import java.util.List;

public class CellDTO {

    private CellType cellType;
    private int version;
    private final CoordinateDTO coordinate;
    private EffectiveValue effectiveValue;
    private String originalValue;
    private List<CoordinateDTO> dependentCoordinates;
    private List<CoordinateDTO> influencedCoordinates;

    public CellDTO(Cell cell) {
        this.cellType = cell.getCellType();
        this.version = cell.getVersion();
        this.coordinate = new CoordinateDTO(cell.getCoordinate());
        this.influencedCoordinates = cell.getInfluencedCoordinates();
        this.dependentCoordinates = cell.getDependentCoordinates();
        this.effectiveValue = cell.getEffectiveValue();
        this.originalValue = cell.getOriginalValue();
    }

    public int getVersion() { return version; }
    public CellType getCellType() { return cellType; }
    public CoordinateDTO getCoordinate() { return coordinate; }
    public EffectiveValue getEffectiveValue() { return effectiveValue; }
    public String getOriginalValue() { return originalValue; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Cell Information:\n")
                .append("Coordinate: ").append(coordinate).append("\n")
                .append("Original Value: ").append(originalValue != null ? originalValue : "None").append("\n")
                .append("Effective Value: ").append(effectiveValue != null ? effectiveValue : "None").append("\n")
                .append("Version: ").append(version).append("\n")
                .append("Cell Type: ").append(cellType).append("\n")
                .append("Dependent Coordinates: ").append(formatCoordinates(dependentCoordinates)).append("\n")
                .append("Influenced Coordinates: ").append(formatCoordinates(influencedCoordinates)).append("\n");
        return sb.toString();
    }

    private String formatCoordinates(List<CoordinateDTO> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        for (CoordinateDTO coord : coordinates) {
            sb.append(coord).append(" ");
        }

        return sb.toString().trim();
    }
}