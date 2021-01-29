package main.mylang.token;

import main.mylang.exception.NotImplementedException;
import main.mylang.lexer.Lexem;
import main.mylang.triad_optimizer.triad.Triad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Token implements Tokenizable {

    private Lexem lexem;
    private String value;

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

    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "\n      " + "lexem: " + lexem +
                ", value: '" + value + '\'';
    }

    @Override
    public List<Token> tokenize() throws NotImplementedException {
        return new ArrayList<>(Arrays.asList(this));
    }
}
