package DTO;

import cell.Cell;
import coordinate.Coordinate;
import coordinate.CoordinateFactory;
import range.Range;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SheetDTO {
    private String name;
    private final Map<CoordinateDTO, CellDTO> cellMap = new HashMap<CoordinateDTO, CellDTO>();
    private final LayoutDTO layout;

    public SheetDTO(sheet.Sheet sheet) {
        name = sheet.getName();
        layout = new LayoutDTO(sheet.getSheetLayout());
        Collection<Cell> cells = sheet.getAllCells();
        for (Cell cell : cells) {
            CellDTO cellDTO = new CellDTO(cell);
            cellMap.put(cellDTO.getCoordinate(), cellDTO);
        }
    }

    public String getName() { return name; }

    public Map<CoordinateDTO, CellDTO> getCellMap() { return cellMap; }

    public LayoutDTO getLayout() { return layout; }

    public CellDTO getCell(int row, int column) {
        Coordinate coordinate = CoordinateFactory.addNewCoordinate(row, column);
        CoordinateDTO coordinateDTO = new CoordinateDTO(coordinate);

        return cellMap.get(coordinateDTO);
    }


}
