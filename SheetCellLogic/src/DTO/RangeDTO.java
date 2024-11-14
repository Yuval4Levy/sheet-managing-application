package DTO;

import cell.Cell;
import exceptions.CoordinateException;
import exceptions.SheetRangeException;
import range.Range;

import java.util.ArrayList;

public class RangeDTO {

    private final String name;
    private final CoordinateDTO start;
    private final CoordinateDTO end;
    private final ArrayList<CellDTO> cells;
    private SheetDTO sheet;

    public RangeDTO(Range range, SheetDTO sheet) throws SheetRangeException {
        this.name = range.getName();
        this.start = new CoordinateDTO(range.getStart());
        this.end = new CoordinateDTO(range.getEnd());
        this.cells = new ArrayList<>();
        this.sheet = sheet;

        try {
            populateDTOCells(range);
        }
        catch (CoordinateException e) {
            throw new SheetRangeException(e.getMessage());
        }

    }

    public CoordinateDTO getStart() {return start;}

    public CoordinateDTO getEnd() {return end;}

    public ArrayList<CellDTO> getCells() {return cells;}

    private void populateDTOCells(Range range) throws CoordinateException {
        for(Cell cell: range.getCells()) {
            CellDTO cellDTO = new CellDTO(cell);
            cells.add(cellDTO);
        }
    }
}
