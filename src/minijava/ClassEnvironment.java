package minijava;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ClassEnvironment {
    private HashMap<String, VariableValue> variableEnvironment;
    private HashMap<String, MethodValue> methodEnvironment;

    public ClassEnvironment() {
        variableEnvironment = new HashMap<String, VariableValue>();
        methodEnvironment = new HashMap<String, MethodValue>();
    }

    public HashMap<String, VariableValue> getVariableEnvironment() {
        return variableEnvironment;
    }

    public HashMap<String, MethodValue> getMethodEnvironment() {
        return methodEnvironment;
    }

}
