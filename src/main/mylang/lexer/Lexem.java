package main.mylang.lexer;

import java.util.regex.Pattern;

public enum Lexem {


    IF_KW("if", 5),
    WHILE_KW("while", 5),
    VAR("^[a-zA-Z]+|([a-zA-Z][0-9]+)", 0),
    DIGIT("^(0|([1-9][0-9]*))", 0),
    ASSIGN_OP("=", 2),
    INPUT_OP("<<", 2),
    OUTPUT_OP(">>", 2),
    PLUS_MINUS("\\+|\\-", 3),
    MUL_DIV("\\*|/", 4),
    LOGIC_OP(">|<|==|>=|<=", 2),
    OPEN_BRACE("\\{", -1),
    CLOSE_BRACE("\\}", 1),
    OPEN_BRACKET("\\(", 1),
    CLOSE_BRACKET("\\)", 1),
    TYPE("int|str|list", 2),
    END_LINE("\\;", 10),
    END("\\end", 10);



    private final Pattern pattern;
    private int priority;

    Lexem(String regexp, int priority){
        this.pattern = Pattern.compile(regexp);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public Pattern getPattern(){
        return this.pattern;
    }
}

