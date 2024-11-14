package coordinate;

import exceptions.CoordinateException;

import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {

    private static Map<String, Coordinate> coordinateMap = new HashMap<String, Coordinate>();

    public static Coordinate getCoordinate(int row, int column) throws CoordinateException {
        String key = row + "," + column;
        Coordinate coordinate = coordinateMap.get(key);

        if (coordinate == null) {
            throw new CoordinateException("Tried to find a non existing coordinate");
        }

        return coordinate;
    }

    public static Coordinate addNewCoordinate(int row, int column){
        String key = row + "," + column;
        Coordinate coordinate = new Coordinate(row, column);

        coordinateMap.put(key, coordinate);

        return coordinate;
    }


}
