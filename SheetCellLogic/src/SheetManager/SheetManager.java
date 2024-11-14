package SheetManager;

import DTO.CellDTO;
import DTO.SheetDTO;
import cell.Cell;
import coordinate.Coordinate;
import coordinate.CoordinateFactory;
import exceptions.*;
import generated.*;
import range.Range;
import sheet.Sheet;
import XmlDataExtractor.XmlDataExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SheetManager implements SheetManagerInterface {
    private int version;
    private List<Sheet> sheets;
    private Sheet currentSheet;
    private static final Logger logger = Logger.getLogger(SheetManager.class.getName());

    public SheetManager() {
        this.version = 0;
        this.sheets = new ArrayList<>();
    }

    public String generateUniqueSheetName(String baseName) {
        String newName = stripNumericSuffix(baseName);
        int count = 1;

        for (Sheet sheet : sheets) {
            if (sheet.getName().startsWith(newName)){
                count++;
            }
        }

        newName = STR."\{newName} \{count}";
        logger.info(STR."Creating unique name: \{newName}");
        return newName;
    }

    private String stripNumericSuffix(String name) {
        return name.replaceAll("\\s\\d+$", "");
    }

    @Override
    public int getVersion() { return version; }

    @Override
    public void setVersion(int version) { this.version = version; }

    @Override
    public List<Sheet> getSheets() { return sheets; }

    @Override
    public Sheet getCurrentSheet() { return currentSheet; }

    public void setCurrentSheet() { currentSheet = sheets.getLast(); }

    public Sheet getSheet(int versionNumber){
        if( versionNumber < 1 || versionNumber > sheets.size() ){
            return null;
        }
        else {
            return sheets.get(versionNumber - 1);
        }
    }

    @Override
    public Cell getCellFromSheet(int row, int col) { return this.getCurrentSheet().getCell(row, col); }

    public boolean addCellToCurrentSheetAndUpdateVersion(int row, int col, String input) {
        try {
            String uniqueName = generateUniqueSheetName(this.getCurrentSheet().getName());
            Sheet newSheet = this.getCurrentSheet().deepCopy(uniqueName);

            newSheet.addNewCell(row, col, input);
            updateVersion(newSheet);

            logger.info("Cell added to current sheet successfully.");
            return true;
        } catch (CoordinateException | IllegalArgumentException | ValueException ex) {
            logger.log(Level.SEVERE, "Couldn't add cell to current sheet", ex);
            return false;
        }
    }

    public void SetSheetAsCurrentSheet(String name) {
        if (name == null) {
            logger.log(Level.SEVERE, "Sheet name cannot be null");
            return;
        }

        for (Sheet sheet : sheets) {
            if (sheet.getName().equals(name)) {
                currentSheet = sheet;
                logger.log(Level.INFO, STR."Current sheet set to: \{currentSheet.getName()}");
                return;
            }
        }

        logger.log(Level.SEVERE, STR."Sheet not found\{name}");
    }

    @Override
    public void updateVersion(Sheet sheet) {
        this.sheets.add(sheet);
        this.version++;
        setCurrentSheet();
    }

    @Override
    public boolean recalculateCellsAndUpdateSheet(int rowOfCell, int colOfCell, String input) {
        try {
            Sheet currentSheet = getCurrentSheet();
            String uniqueName = generateUniqueSheetName(currentSheet.getName());
            Sheet newSheet = currentSheet.deepCopy(uniqueName);
            newSheet.updateCellAndCalculateSheet(rowOfCell, colOfCell, input);
            updateVersion(newSheet);

            return true;
        } catch (IllegalArgumentException | CoordinateException | ValueException ex) {
            logger.log(Level.SEVERE, "Couldn't update sheet because", ex);
            return false;
        }
    }

    @Override
    public boolean createNewSheet(int numberOfRows, int numberOfColumns, int columnWidthUnits,
                                  int rowsHeightUnits, String baseName) {
        try {
            String uniqueName = generateUniqueSheetName(baseName);

            Sheet newSheet = new Sheet(numberOfRows, numberOfColumns,
                    columnWidthUnits, rowsHeightUnits, uniqueName);

            this.sheets.add(newSheet);
            updateVersion(newSheet);

            return true;
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "Couldn't create new sheet because: ", ex);
            return false;
        }
    }

    public boolean loadSheetFromXml(String xmlFilePath) throws Exception {
        try {
            STLSheet stlSheet = XmlDataExtractor.extractSTLSheet(xmlFilePath);
            Sheet newSheet = convertToSheet(stlSheet);
            updateVersion(newSheet);
            logger.info("Sheet loaded from XML successfully.");
            return true;
        } catch (java.io.FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Couldn't load sheet from XML file", ex);
            throw new Exception(STR."Couldn't load sheet from XML file: \{ex.getMessage()}");
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, "XML parsing error", ex);
            throw new Exception(STR."XML parsing error: \{ex.getMessage()}");
        } catch (CoordinateException | ValueException ex) {
            logger.log(Level.SEVERE, "Error while loading cells", ex);
            throw new Exception(STR."Error while loading cells: \{ex.getMessage()}");
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "Error creating sheet", ex);
            throw new Exception(STR."Error creating sheet: \{ex.getMessage()}");
        }
    }

    public static void fillInSheet(Sheet sheet) {

        for(int i = 0 ; i<sheet.getSheetLayout().getNumberOfColumns(); i++)
        {
            for(int j = 0 ; j<sheet.getSheetLayout().getNumberOfRows(); j++)
            {
                if(sheet.getCell(j, i) == null){
                    try {
                        sheet.addNewCell(j, i, "EMPTY");
                    }
                    catch (Exception ex) {
                        logger.log(Level.SEVERE, "Error while adding new cell", ex);
                    }
                }
            }
        }
    }

    private Sheet convertToSheet(STLSheet stlSheet) throws CoordinateException, ValueException, IllegalArgumentException {
        Sheet newSheet = new Sheet(stlSheet.getSTLLayout().getRows(), stlSheet.getSTLLayout().getColumns(),
                stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits(),
                stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits(),
                stlSheet.getName());

        fillInSheet(newSheet);

        STLRanges stlRanges = stlSheet.getSTLRanges();
        if (stlRanges != null) {
            for (STLRange stlRange : stlRanges.getSTLRange()) {
                String rangeName = stlRange.getName();
                Coordinate start = parseCoordinate(stlRange.getSTLBoundaries().getFrom());
                Coordinate end = parseCoordinate(stlRange.getSTLBoundaries().getTo());
                newSheet.addRange(rangeName, start, end);
            }
        }

        STLCells stlCells = stlSheet.getSTLCells();
        for (STLCell stlCell : stlCells.getSTLCell()) {
            int row = stlCell.getRow() - 1;
            int col = ConvertStlColumnToInt(stlCell.getColumn());
            String value = stlCell.getSTLOriginalValue();

            newSheet.updateCellAndCalculateSheet(row, col, value);
        }

        return newSheet;
    }


    public static int ConvertStlColumnToInt(String column) {
        int columnIndex = 0;

        for (char c : column.toCharArray()) {
             columnIndex = columnIndex * 26 + (c - 'A' + 1);
        }

        return columnIndex - 1;
    }

    public ArrayList<Cell> filterColumn(String columnName, List<Cell> valuesToCompare)
    {
        try {
            Coordinate start = parseCoordinate(columnName + "1");
            Coordinate end = parseCoordinate(columnName + getCurrentSheet().getSheetLayout().getNumberOfRows());
            return getCurrentSheet().filterRange(start, end, valuesToCompare);
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, "Error while filtering column", ex);
            return null;
        }
    }

    public SheetDTO createLatestSheetDTO() { return new SheetDTO(getCurrentSheet()); }

    public SheetDTO createSheetDTO(int versionNumber) {
        Sheet sheet = getSheet(versionNumber);

        if (sheet == null) {
            return null;
        }
        else {
            return new SheetDTO(sheet);
        }
    }

    public CellDTO createCellDTO(int row, int col) {
        Cell cell = getCellFromSheet(row, col);
        if (cell != null) {
            return new CellDTO(cell);
        }
        return null;
    }

    public List<String> getAvailableVersions() { return sheets.stream().map(Sheet::getName).collect(Collectors.toList()); }

    public void addRangeToCurrentSheet(String name, Coordinate start, Coordinate end) throws SheetRangeException {
        try {
            getCurrentSheet().addRange(name, start, end);
            logger.info("Added range " + name + " to sheet " + getCurrentSheet().getName());
        }
        catch (SheetRangeException ex) {
            logger.log(Level.SEVERE, "Couldn't add range " + name, ex);
            throw ex;
        }
    }

    public void deleteRangeFromCurrentSheet(String name) throws SheetRangeException {
        try{
            getCurrentSheet().removeRange(name);
            logger.info("Removed range " + name + " from sheet " + getCurrentSheet().getName());
        }
        catch (SheetRangeException ex) {
            logger.log(Level.SEVERE, "Couldn't remove range " + name, ex);
            throw ex;
        }
    }

    public Coordinate parseCoordinate(String coordinateString) throws CoordinateException {
        int row = Integer.parseInt(coordinateString.replaceAll("[^0-9]", "")) - 1;
        int col = ConvertStlColumnToInt(coordinateString.replaceAll("[^A-Z]", ""));

        return CoordinateFactory.getCoordinate(row, col);
    }

    public Map<String, Range> getCurrentRanges() {
        return getCurrentSheet().getRanges();
    }
}