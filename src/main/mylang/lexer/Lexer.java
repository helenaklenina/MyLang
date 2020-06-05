package main.mylang.lexer;

import main.mylang.exception.LangTokensException;
import main.mylang.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final String rawInput;

    public Lexer(String rawInput) {
        this.rawInput = rawInput;
        System.out.println("LEXER was created: \"" + this.rawInput + "\"\n");
    }

    public List<Token> getTokens() throws LangTokensException {

        List<Token> tokens = new ArrayList<Token>();
        int lineIndex = 0;

        for (String line : getLines(rawInput)) {
            try {
                lineIndex++;

                while (line.matches("(\\s*\\S+\\s*)+")) {
                    line = line.trim();
//                    System.out.println(line);

                    Lexem currentLexem = Lexem.values()[0];
                    String currentRegex = "^(" + currentLexem.getPattern().pattern() + ")";
                    Matcher matcher = Pattern.compile(currentRegex).matcher(line);

                    // Finding relevant lexeme type or throwing exception
                    while (!matcher.find()) {
                        currentLexem = getNextLexem(currentLexem);
                        currentRegex = "^(" + currentLexem.getPattern().pattern() + ")";
                        matcher.usePattern(Pattern.compile(currentRegex));
                    }

                    String value = matcher.group(0);
                    tokens.add(new Token(currentLexem, value));

                    // Replacing first founded value with spaces
                    line = matcher.replaceFirst("");
                }

            } catch (IndexOutOfBoundsException ex) {
                Scanner scanner = new Scanner(line);
                String unknownSymbol = scanner.next();
                scanner.close();

                throw new LangTokensException(
                        "Unknown symbol at line " + lineIndex + " : " + unknownSymbol
                );
            }

        }

        return tokens;
    }

    private Lexem getNextLexem(Lexem currentLexem) throws IndexOutOfBoundsException {
        int index = currentLexem.ordinal();
        Lexem[] lexems = Lexem.values();

        if (index >= lexems.length) {
            throw new IndexOutOfBoundsException();
        }
        return lexems[index + 1];
    }

    private List<String> getLines(String str) {
        Scanner scanner = new Scanner(str);
        List<String> lines = new ArrayList<String>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines.add(line);
        }

        scanner.close();
        return lines;
    }
}