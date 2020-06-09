package main.mylang;

import main.mylang.exception.EofException;
import main.mylang.exception.LangParseException;
import main.mylang.exception.LangTokensException;
import main.mylang.lexer.Lexer;
import main.mylang.parser.Parser;
import main.mylang.rpn_convertor.RpnConvertor;
import main.mylang.stack_machine.StackMachine;
import main.mylang.var_table.VarTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MYLang {
    public static void main(String[] args) throws LangParseException, LangTokensException, IOException, EofException {
        System.out.println("START...");

        if (args.length < 1) {
            System.err.println("\nUsage: MYLang <filename>");
            System.exit(-1);
        }

        String rawInput = Files.readString(Paths.get(args[0]));

        Lexer lexer = new Lexer(rawInput);
        System.out.println("\nTOKENS: " + lexer.getTokens() + "\n");

        Parser parser = new Parser(lexer.getTokens());
        parser.lang();

        VarTable varTable = new VarTable();

        RpnConvertor convertor = new RpnConvertor(lexer.getTokens(), varTable);
        System.out.println("\nREVERSE POLISH NOTATION: " + convertor.getRpn() + "\n");
//        System.out.println("\nTransfers: " + varTable + "\n");

        StackMachine stackMachine = new StackMachine(convertor.getRpn(), varTable);
        stackMachine.perform();
    }
}
