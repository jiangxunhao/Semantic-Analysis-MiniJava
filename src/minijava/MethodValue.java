package minijava;

public class MethodValue{
    private String returnType;
    private MethodEnvironment methodEnvironment;

    public MethodValue(String returnType, MethodEnvironment methodEnvironment) {
        this.returnType = returnType;
        this.methodEnvironment = methodEnvironment;
    }

    public String getReturnType() {
        return returnType;
    }

    public MethodEnvironment getMethodEnvironment() {
        return methodEnvironment;
    }
}
