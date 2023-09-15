package minijava;

public class VariableValue {
    private String type;
    private Object value;

    public VariableValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
