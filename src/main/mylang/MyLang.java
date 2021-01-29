package main.mylang;

import main.mylang.exception.*;
import main.mylang.lexer.Lexer;
import main.mylang.parser.Parser;
import main.mylang.rpn_convertor.RpnConvertor;
import main.mylang.stack_machine.StackMachine;
import main.mylang.token.Token;
import main.mylang.triad_optimizer.Optimizer;
import main.mylang.var_table.VarTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class MyLang {

    private static List<Token> mainRpn;

    public static void main(String[] args) throws LangParseException, LangTokensException, IOException, EofException, PerformException, NotImplementedException {
        int i = 0;
        for (; i < args.length; i++) {
            System.out.println("START...");

            if (args.length < 1) {
                System.err.println("\nUsage: MYLang <filename>");
                System.exit(-1);
            }

            String rawInput = Files.readString(Paths.get(args[i]));

            Lexer lexer = new Lexer(rawInput);
            System.out.println("\nTOKENS: " + lexer.getTokens() + "\n");

            Parser parser = new Parser(lexer.getTokens());
            parser.lang();

            RpnConvertor convertor = new RpnConvertor(lexer.getTokens());
            mainRpn = convertor.getRpn();
            System.out.println("Reverse Polish Notation: " + mainRpn + "\n");
            System.out.println("Table of variables: " + VarTable.getInstance() + "\n");

            Optimizer optimizer = new Optimizer(mainRpn);
            List rpn = optimizer.optimize();

            System.out.println("\noptimized REVERSE POLISH NOTATION: " + rpn + "\n");
            System.out.println("New table of variables: " + VarTable.getInstance() + "\n");


            StackMachine stackMachine = new StackMachine(rpn);
            stackMachine.perform();

        }
    }


    private static void clearVarTable() {
        Iterator<String> it = VarTable.getInstance().keySet().iterator();
        while (it.hasNext()) {
            if (it.next().charAt(0) != '_') {
                it.remove();
            }
        }
    }
}
