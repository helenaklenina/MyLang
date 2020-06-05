package main.mylang.token;

import main.mylang.lexer.Lexem;

public class Token {

    private final Lexem lexem;
    private final String value;

    public Token(Lexem lexem, String value) {
        this.lexem = lexem;
        this.value = value;
    }

    public Token(String value) {
        this.value = value;
        this.lexem = Lexem.END;
    }

    public Lexem getLexem() {
        return lexem;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '\n' + "Token {" +
                "lexem: " + lexem +
                ", value: '" + value + '\'' +
                '}';
    }

}
