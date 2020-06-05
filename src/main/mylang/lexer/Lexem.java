package main.mylang.lexer;

import java.util.regex.Pattern;

public enum Lexem {

    VAR("^[a-zA-Z]+|([a-zA-Z][0-9]+)"),
    DIGIT("^(0|([1-9][0-9]*))"),
    ASSIGN_OP("="),
    PLUS_MINUS("\\+|\\-"),
    MUL_DIV("\\*|/"),
    LOGIC_OP(">|<|==|>=|<="),
    TYPE("int|str|list"),
    END("\\end");



    private final Pattern pattern;

    Lexem(String regexp){
        this.pattern = Pattern.compile(regexp);
    }

    public Pattern getPattern(){
        return this.pattern;
    }
}

