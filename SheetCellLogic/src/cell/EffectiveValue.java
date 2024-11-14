package cell;

import exceptions.ValueException;

public class EffectiveValue implements EffectiveValueInterface{

    private final Object value;
    private final CellType type;

    public EffectiveValue(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object getValue() {return value; }

    @Override
    public CellType getCellType() {return type; }

    @Override
    public <T> T extractValue(Class<T> requestedType) throws ValueException {
        if (requestedType.isAssignableFrom(value.getClass())) { // Check against value's class
            return requestedType.cast(value);
        }
        else {
            throw new ValueException("Value is not of type " + requestedType);
        }
    }

    @Override
    public String toString() {
        String result;

        result = (type == CellType.BOOLEAN) ? value.toString().toUpperCase() : String.valueOf(value);

        return result;
    }
}
