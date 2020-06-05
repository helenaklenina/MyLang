package main.mylang.parser;

import main.mylang.exception.LangParseException;
import main.mylang.lexer.Lexem;
import main.mylang.token.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private Token endToken;
    private int t = -1;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        System.out.println("PARSER is on with length of string = " + tokens.size() + '\n');
    }

    public void lang() throws LangParseException {
        endToken = new Token("end");
//        expr();
        while (t < (tokens.size() - 1)) {
            expr();
        }

    }

    private void expr() throws LangParseException {
        value();
        assignOp();
        assignExpr();

    }

    private void var() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.VAR);
    }


    private void assignOp() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.ASSIGN_OP);
    }

    private void assignExpr() throws LangParseException {
        value();
        op();
        value();
    }

    private void logicOp() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.LOGIC_OP);

    }

    private void mulDiv() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.MUL_DIV);

    }

    private void plusMinus() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.PLUS_MINUS);

    }

    private void op() throws LangParseException {
        try{
            plusMinus();
        } catch (LangParseException e) {
            try {
                back(1);
                mulDiv();
            } catch (LangParseException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
    }

    private void value() throws LangParseException {
        try{
            var();
        } catch (LangParseException e) {
            try {
                back(1);
                digit();
            } catch (LangParseException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
    }

    private void digit() throws LangParseException {
        matchToken(getCurrentToken(), Lexem.DIGIT);
    }

    private void matchToken(Token token, Lexem lexem) throws LangParseException {
        if (!token.getLexem().equals(lexem)) {
            throw new LangParseException( lexem.name() + " expected " +
                    "but " + token.getLexem().name() + " founded" + "and t = " + Integer.toString(t));
        }
    }

    private Token getCurrentToken() throws LangParseException {
        if (t == tokens.size() - 1){
            return endToken;
        }
        if (t < tokens.size()) {
            System.out.println(Integer.toString(t + 1) + ' ' + tokens.get(t + 1).getLexem());
            return tokens.get(++t);
        } else {
            throw new LangParseException("EOF");
        }

    }

    private void back(int step) {
        t -= step;
    }


//    private Token getCurrentToken() throws LangParseException {
//        // TODO
//        Iterator<Token> iterator = tokens.iterator();
//        if (iterator.hasNext()) {
//            return iterator.next();
//        }
//
//        throw new LangParseException("EOF");
//    }

}

