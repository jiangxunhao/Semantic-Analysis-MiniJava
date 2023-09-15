package minijava;

import java.util.*;

public class MiniJavaSymbolTableBuilder extends MiniJavaGrammarBaseListener{

    HashMap<String, LinkedList<ClassEnvironment>> SymbolTableBase;
    HashSet<String> namesInMethod;
    public void printError(String error) {
        System.err.println(error);
        //System.exit(-1);
    }

    @Override
    public void enterProgram(MiniJavaGrammarParser.ProgramContext ctx) {
        SymbolTableBase = new HashMap<String, LinkedList<ClassEnvironment>>();
    }

    @Override
    public void enterMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        String className = new String(ctx.ID(0).getText());
        LinkedList<ClassEnvironment> environments = new LinkedList<ClassEnvironment>();
        ClassEnvironment classEnvironment = new ClassEnvironment();
        environments.add(classEnvironment);
        SymbolTableBase.put(className, environments);
    }

    @Override
    public void enterClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        String className = new String(ctx.ID(0).getText());
        if(SymbolTableBase.containsKey(className)) {
            printError("Error: Repeated class declaration in \"" + className + "\"");
        }
        LinkedList<ClassEnvironment> environments = new LinkedList<ClassEnvironment>();
        ClassEnvironment classEnvironment = new ClassEnvironment();
        environments.add(classEnvironment);
        SymbolTableBase.put(className, environments);

    }

    @Override
    public void enterVardecl(MiniJavaGrammarParser.VardeclContext ctx) {
        String parentName = ctx.getParent().getToken(MiniJavaGrammarParser.ID,0).getText();
        HashMap<String, VariableValue> variableEnvironment;
        String variableName = new String(ctx.ID().getText());

        if(isClassVariable(ctx)){
            ClassEnvironment currentClassEnvironment = SymbolTableBase.get(parentName).get(0);
            variableEnvironment= currentClassEnvironment.getVariableEnvironment();

            if(variableEnvironment.containsKey(variableName)){
                printError("ERROR: Repeated variable declaration in \"" + variableName + "\"");
            }

        }else{
            String currentClassName = ctx.getParent().getParent().getToken(MiniJavaGrammarParser.ID,0).getText();
            ClassEnvironment currentClassEnvironment = SymbolTableBase.get(currentClassName).get(0);

            variableEnvironment = currentClassEnvironment.getMethodEnvironment().get(parentName).getMethodEnvironment().getVariableEnvironment();

            if(namesInMethod.contains(variableName)){
                printError("ERROR: Repeated variable declaration in \"" + variableName + "\"");
            }
            namesInMethod.add(variableName);
        }



        String variableType = new String(ctx.type().getText());
        VariableValue variableValue = new VariableValue(variableType, null);
        variableEnvironment.put(variableName, variableValue);

    }

    public boolean isClassVariable(MiniJavaGrammarParser.VardeclContext ctx) {
        boolean res = (ctx.getParent().getRuleIndex() == MiniJavaGrammarParser.RULE_classdecl);
        return res;
    }

    @Override
    public void enterMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        namesInMethod = new HashSet<String>();
        String className = ctx.getParent().getToken(MiniJavaGrammarParser.ID, 0).getText();
        HashMap<String, MethodValue> methodEnvironment = SymbolTableBase.get(className).get(0).getMethodEnvironment();

        String methodName = new String(ctx.ID().getText());
        if(methodEnvironment.containsKey(methodName)){
            printError("Error: Repeated function declaration in \"" + methodName + "\"");
        }

        String methodReturnType = new String(ctx.type().getText());
        MethodEnvironment methEnvironment = new MethodEnvironment();
        MethodValue methodValue = new MethodValue(methodReturnType, methEnvironment);
        methodEnvironment.put(methodName, methodValue);

    }

    @Override
    public void exitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        namesInMethod.clear();
    }

    @Override
    public void enterFormallist(MiniJavaGrammarParser.FormallistContext ctx) {
        String methodName = ctx.getParent().getToken(MiniJavaGrammarParser.ID, 0).getText();
        String className = ctx.getParent().getParent().getToken(MiniJavaGrammarParser.ID, 0).getText();
        ArrayList<VariableValue> formalEnvironment = SymbolTableBase.get(className).get(0).getMethodEnvironment().get(methodName).getMethodEnvironment().getFormalEnvironment();

        String formalName = ctx.ID().getText();
        String type = ctx.type().getText();
        VariableValue value = new VariableValue(type, formalName);
        formalEnvironment.add(value);

        namesInMethod.add(formalName);

    }

    @Override
    public void exitFormallist(MiniJavaGrammarParser.FormallistContext ctx) {

    }

    @Override
    public void enterFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {
        String methodName = ctx.getParent().getParent().getToken(MiniJavaGrammarParser.ID, 0).getText();
        String className = ctx.getParent().getParent().getParent().getToken(MiniJavaGrammarParser.ID, 0).getText();
        ArrayList<VariableValue> formalEnvironment = SymbolTableBase.get(className).get(0).getMethodEnvironment().get(methodName).getMethodEnvironment().getFormalEnvironment();

        String formalName = ctx.ID().getText();
        if(namesInMethod.contains(formalName)) {
            printError("ERROR: Repeated parameter name in \"" + formalName + "\"");
        }
        namesInMethod.add(formalName);

        String type = ctx.type().getText();
        VariableValue value = new VariableValue(type, formalName);
        formalEnvironment.add(value);



    }
}
