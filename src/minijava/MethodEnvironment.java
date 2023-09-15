package minijava;

import java.beans.beancontext.BeanContextServiceAvailableEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class MethodEnvironment {
    private ArrayList<VariableValue> formalEnvironment;
    private HashMap<String, VariableValue> variableEnvironment;

    public MethodEnvironment() {
        formalEnvironment = new ArrayList<VariableValue>();
        variableEnvironment = new HashMap<String, VariableValue>();
    }

    public ArrayList<VariableValue> getFormalEnvironment() {
        return formalEnvironment;
    }

    public HashMap<String, VariableValue> getVariableEnvironment() {
        return variableEnvironment;
    }

}
