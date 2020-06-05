package main.mylang;

import main.mylang.exception.LangParseException;
import main.mylang.exception.LangTokensException;
import main.mylang.lexer.Lexer;
import main.mylang.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UILang {
    public static void main(String[] args) throws LangParseException, LangTokensException, IOException {
        System.out.println("START...");

        if (args.length < 1) {
            System.err.println("Usage: UILang <filename>");
            System.exit(-1);
        }

        String rawInput = Files.readString(Paths.get(args[0]));

        Lexer lexer = new Lexer(rawInput);
        System.out.println("\nTOKENS: " + lexer.getTokens() + "\n");

        Parser parser = new Parser(lexer.getTokens());
        parser.lang();

//        VarTable varTable = new VarTable();

    }
}
