package minijava;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Method;
import java.util.*;

public class MiniJavaListener extends MiniJavaGrammarBaseListener {

    MiniJavaGrammarParser parser;
    HashMap<String, LinkedList<ClassEnvironment>> SymbolTableBase;


    public void printError(String error) {
        System.err.println(error);
        System.exit(-1);
    }


    public MiniJavaListener(MiniJavaGrammarParser parser, HashMap<String, LinkedList<ClassEnvironment>> SymbolTableBase) {
        this.parser = parser;
        this.SymbolTableBase = SymbolTableBase;
    }

    @Override
    public void enterProgram(MiniJavaGrammarParser.ProgramContext ctx) {

    }


    @Override
    public void exitProgram(MiniJavaGrammarParser.ProgramContext ctx) {

    }

    @Override
    public void enterMainclass(MiniJavaGrammarParser.MainclassContext ctx) {

    }

    @Override
    public void exitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {

    }

    @Override
    public void enterClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        if(ctx.EXTENDS() != null){
            String currentClassName = ctx.ID(0).getText();
            String extendsClassName = ctx.ID(1).getText();
            if(!SymbolTableBase.containsKey(extendsClassName)){
                printError("Error: Cannot extend class in \"" + extendsClassName + "\"");
            }
            LinkedList<ClassEnvironment> currentEnvironments = SymbolTableBase.get(currentClassName);
            ClassEnvironment extendsEnvironments = SymbolTableBase.get(extendsClassName).get(0);

            /*ClassEnvironment lastNode = SymbolTableBase.get(extendsClassName).getLast();
            System.out.println(lastNode);
            if(lastNode == currentEnvironments.get(0)){
                printError("Error: There is a circle in hesitation with \"" + currentClassName + "\"");
                System.exit(-1);
            }
            */

            currentEnvironments.add(extendsEnvironments);


        }

    }

    @Override
    public void exitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {

    }

    @Override
    public void enterVardecl(MiniJavaGrammarParser.VardeclContext ctx) {


    }


    @Override
    public void exitVardecl(MiniJavaGrammarParser.VardeclContext ctx) {

    }

    //
    @Override
    public void enterMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {

    }

    //
    @Override
    public void exitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {

    }

    //
    @Override
    public void enterFormallist(MiniJavaGrammarParser.FormallistContext ctx) {

    }

    //
    @Override
    public void exitFormallist(MiniJavaGrammarParser.FormallistContext ctx) {


    }

    //
    @Override
    public void enterFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {

    }


    //
    @Override
    public void exitFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {

    }


    @Override
    public void enterType(MiniJavaGrammarParser.TypeContext ctx) {

    }

    @Override
    public void exitType(MiniJavaGrammarParser.TypeContext ctx) {
        //System.out.println("exitType");
    }
//


    @Override
    public void enterStatement(MiniJavaGrammarParser.StatementContext ctx) {
        if(ctx.getToken(MiniJavaGrammarParser.IF,0) != null || ctx.getToken(MiniJavaGrammarParser.WHILE,0) != null) {
            String type = enterExp(ctx.expr(0));
            if(!type.equals("boolean")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }
        }else if(ctx.getToken(MiniJavaGrammarParser.SYSTEMOUT,0) != null) {
            String type = enterExp(ctx.expr(0));
            if(!type.equals("int")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }
        }else if(ctx.getToken(MiniJavaGrammarParser.LSQUARE,0) != null) {
            String idType = findType(ctx.ID().getText(), ctx);
            if(!idType.equals("int[]")) {
                printError("Error: Invalid type in \"" + ctx.ID().getText() + "\"");
            }

            String expType1 = enterExp(ctx.expr(0));
            if(!expType1.equals("int")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }

            String expType2 = enterExp(ctx.expr(1));
            if(!expType2.equals("int")) {
                printError("Error: Invalid type in \"" + ctx.expr(1).getText() + "\"");
            }
        }else if(ctx.getToken(MiniJavaGrammarParser.EQUALS,0) != null) {
            String idType = findType(ctx.ID().getText(), ctx);
            String expType = enterExp(ctx.expr(0));
            if(!idType.equals(expType)) {
                printError("Error: Invalid type in \"" + ctx.getText() + "\"");
            }
        }
    }

    public String findType(String variableName, ParserRuleContext ctx) {
        String className = getClassName(ctx);

        String methodName = getMethodName(ctx);
        String type = null;

        LinkedList<ClassEnvironment> environments = SymbolTableBase.get(className);
        if(methodName == null) {
            Iterator<ClassEnvironment> iterator = environments.iterator();
            while(iterator.hasNext()) {
                ClassEnvironment classEnvironment = iterator.next();
                if(classEnvironment.getVariableEnvironment().containsKey(variableName)){
                    type = classEnvironment.getVariableEnvironment().get(variableName).getType();
                    break;
                }
            }
        }else{
            Iterator<ClassEnvironment> iterator = environments.iterator();
            while(iterator.hasNext()) {
                ClassEnvironment classEnvironment = iterator.next();
                HashMap<String, MethodValue> methodEnvironment = classEnvironment.getMethodEnvironment();
                if(methodEnvironment.containsKey(methodName)) {
                    HashMap<String, VariableValue> variableEnvironment = methodEnvironment.get(methodName).getMethodEnvironment().getVariableEnvironment();
                    if(variableEnvironment.containsKey(variableName)) {
                        type = variableEnvironment.get(variableName).getType();
                        break;
                    }

                    ArrayList<VariableValue> formalEnvironment = methodEnvironment.get(methodName).getMethodEnvironment().getFormalEnvironment();
                    for(int i = 0; i < formalEnvironment.size(); i++){
                        if(formalEnvironment.get(i).getValue().equals(variableName)){
                            type = formalEnvironment.get(i).getType();
                            break;
                        }
                    }
                    break;

                }

                HashMap<String, VariableValue> variableEnvironment = classEnvironment.getVariableEnvironment();
                if(variableEnvironment.containsKey(variableName)) {
                    type = variableEnvironment.get(variableName).getType();
                    break;
                }

            }
        }

        if(type == null){
            printError("Error: Cannot find the \"" + variableName + "\"");
        }

        return type;
    }

    public String enterExp(MiniJavaGrammarParser.ExprContext ctx) {
        String type = null;
        if(ctx.op() != null){
            String lType = enterExp(ctx.expr(0));
            String rType = enterExp(ctx.expr(1));
            if(ctx.op().AND() != null) {

                if(!lType.equals("boolean")) {
                    printError("Error: Invalid type in \"" + ctx.expr(0) + "\"");
                }

                if(!rType.equals("boolean")){
                    printError("Error: Invalid type in \"" + ctx.expr(1) + "\"");
                }

                type = "boolean";

            }else if(ctx.op().LT() != null) {
                if(!lType.equals("int")) {
                    printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
                }


                if(!rType.equals("int")) {
                    printError("Error: Invalid type in \"" + ctx.expr(1).getText() + "\"");
                }

                type = "boolean";
            }else{

                if(!lType.equals("int")) {
                    printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
                }


                if(!rType.equals("int")) {
                    printError("Error: Invalid type in \"" + ctx.expr(1).getText() + "\"");
                }

                type = "int";
            }


        }else if(ctx.INT() != null) {
            String subType = enterExp(ctx.expr(0));
            if(!subType.equals("int")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }

            type = "int[]";

        }else if(ctx.LENGTH() != null) {
            String subType = enterExp(ctx.expr(0));
            if(!subType.equals("int[]")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }

            type = "int";

        }else if(ctx.TRUE() != null || ctx.FALSE() != null) {
            type = "boolean";

        }else if(ctx.THIS() != null) {
            type = getClassName(ctx);

        }else if(ctx.NOT() != null) {
            String subType = enterExp(ctx.expr(0));
            if(!subType.equals("boolean")){
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }
            type = "boolean";

        }else if(ctx.NEW() != null) {
            type = ctx.ID().getText();
            if(!SymbolTableBase.containsKey(type)){
                printError("Error: Invalid type in \"" + type + "\"");
            }

        }else if(ctx.INTEGER() != null) {
            type = "int";

        }else if(ctx.LSQUARE() != null) {
            String type1 = enterExp(ctx.expr(0));
            if(!type1.equals("int[]")) {
                printError("Error: Invalid type in \"" + ctx.expr(0).getText() + "\"");
            }

            String type2 = enterExp(ctx.expr(1));
            if(!type2.equals("int")){
                printError("Error: Invalid type in \"" + ctx.expr(1).getText() + "\"");
            }

            type = "int";

        }else if(ctx.DOT() != null) {
            String expType = enterExp(ctx.expr(0));
            if(!SymbolTableBase.containsKey(expType)){
                printError("Error: Invalid class in \"" + ctx.expr(0).getText() + "\"");
            }
            String methodName = ctx.ID().getText();
            Iterator<ClassEnvironment> iterator = SymbolTableBase.get(expType).iterator();
            while(iterator.hasNext()){
                ClassEnvironment classEnvironment = iterator.next();
                if(!classEnvironment.getMethodEnvironment().containsKey(methodName)){
                    continue;
                }else{
                    type = classEnvironment.getMethodEnvironment().get(methodName).getReturnType();
                    break;
                }
            }
            if(type == null){
                printError("Error: Invalid function in \"" + methodName + "\"");
            }

        }else if(ctx.LPAREN() != null) {
            type = enterExp(ctx.expr(0));

        }else if(ctx.ID() != null){
            type = findType(ctx.ID().getText(), ctx);
        }

        return type;
    }

    public String getClassName(ParserRuleContext ctx) {
        String className = null;
        int n = ctx.depth();
        for(int i = 2; i < n; i++){
            ctx = ctx.getParent();
        }
        className = ctx.getToken(MiniJavaGrammarParser.ID, 0).getText();
        return className;
    }

    public String getMethodName(ParserRuleContext ctx) {
        String methodName = null;
        for(ctx = ctx.getParent(); ctx != null; ctx = ctx.getParent()) {
            if(ctx.getStart().getText().equals("public")){
                methodName = ctx.getToken(MiniJavaGrammarParser.ID,0).getText();
                break;
            }
        }

        return methodName;
    }

    @Override
    public void exitExpr(MiniJavaGrammarParser.ExprContext ctx) {

    }

    @Override
    public void enterOp(MiniJavaGrammarParser.OpContext ctx) {

    }

    @Override
    public void exitOp(MiniJavaGrammarParser.OpContext ctx) {

    }

    @Override
    public void enterExprlist(MiniJavaGrammarParser.ExprlistContext ctx) {

    }

    @Override
    public void exitExprlist(MiniJavaGrammarParser.ExprlistContext ctx) {

    }

    @Override
    public void enterExprrest(MiniJavaGrammarParser.ExprrestContext ctx) {

    }

    @Override
    public void exitExprrest(MiniJavaGrammarParser.ExprrestContext ctx) {

    }
}


