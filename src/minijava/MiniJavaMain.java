package minijava;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class MiniJavaMain {

    public static void main(String[] args) throws Exception {

        String inputFile = null;

        if (args.length > 0 )
            inputFile = args[0];

        InputStream is = System.in;

        if (inputFile != null ) is = new FileInputStream(inputFile);

        ANTLRInputStream input = new ANTLRInputStream(is);

        MiniJavaGrammarLexer lexer = new MiniJavaGrammarLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        MiniJavaGrammarParser parser = new MiniJavaGrammarParser(tokens);

        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();

        MiniJavaSymbolTableBuilder builder = new MiniJavaSymbolTableBuilder();

        walker.walk(builder, tree);

        //printSymbolTable(builder.SymbolTableBase);

        MiniJavaListener typecheck = new MiniJavaListener(parser, builder.SymbolTableBase);

        walker.walk(typecheck, tree);

    }

    public static void printSymbolTable(HashMap<String, LinkedList<ClassEnvironment>> SymbolTableBase){
        for(Map.Entry<String, LinkedList<ClassEnvironment>> entry: SymbolTableBase.entrySet()){
            ClassEnvironment classEnvironment = entry.getValue().get(0);
            System.out.println(entry.getKey() + " CLASS");

            HashMap<String, VariableValue> hmv = classEnvironment.getVariableEnvironment();
            for(Map.Entry<String, VariableValue> entry1: hmv.entrySet()){
                System.out.println(entry1.getKey()+" "+entry1.getValue().getType()+ " "+entry1.getValue().getValue());
            }

            HashMap<String, MethodValue> hmm = classEnvironment.getMethodEnvironment();
            for(Map.Entry<String, MethodValue> entry2: hmm.entrySet()){

                System.out.println(entry2.getKey()+" "+entry2.getValue().getReturnType()+ " Method");

                MethodEnvironment me = entry2.getValue().getMethodEnvironment();
                ArrayList<VariableValue> al = me.getFormalEnvironment();
                for(int i = 0; i < al.size(); i++){
                    System.out.println(i+" "+al.get(i).getType()+" "+al.get(i).getValue());
                }

                HashMap<String, VariableValue> hmva = me.getVariableEnvironment();
                for(Map.Entry<String, VariableValue> entry3: hmva.entrySet()){
                    System.out.println(entry3.getKey()+" "+entry3.getValue().getType()+ " "+entry3.getValue().getValue());
                }
            }
        }
    }

}
