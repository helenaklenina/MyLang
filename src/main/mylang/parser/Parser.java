package main.mylang.parser;

import main.mylang.exception.EofException;
import main.mylang.exception.LangParseException;
import main.mylang.lexer.Lexem;
import main.mylang.token.Token;

import java.util.*;

public class Parser {

    private ListIterator<Token> iterator;
    private final List<Token> tokens;
    private Stack<Integer> depths;
//    int t = -1;
//    boolean closed = true;
//    Token endToken = new Token("end");

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.depths = new Stack<>();
        this.iterator = tokens.listIterator();
    }

    public void lang() throws LangParseException, EofException {
        while (iterator.hasNext()) {
            expr();
        }
    }

    private void expr() throws LangParseException, EofException {
//        boolean already = true;
        depths.push(0);
        try {
//            System.out.println("==================== try assign exp .....");
            assignExpr();
        } catch (LangParseException e){
            try{
//                System.out.println("==================== try log if or while .....");
                back(depths.pop());
                depths.push(0);
                log_kw_op();
            } catch (LangParseException ex){
                try {
//                    System.out.println("==================== try to output or input .....");
                    back(depths.pop());
                    depths.push(0);
                    input_output();
                } catch (LangParseException exc) {
                    try {
//                        System.out.println("==================== try to list & it's methos .....");
                        back(depths.pop());
                        depths.push(0);
                        listExpr();
                    } catch (LangParseException exce) {
                         try {
                             back(depths.pop());
                             depths.pop();
                             method_call();
                         } catch (LangParseException excep) {
                             throw new LangParseException(
                                     e.getMessage() + " / "
                                             + ex.getMessage()+ " / "
                                                    + exc.getMessage() + " / "
                                                            + exce.getMessage() + " / "
                                                                    + excep.getMessage()
                             );
                         }
                    }

                }
            }
        }
        depths.pop();
    }

    private void method_call() throws LangParseException, EofException {
        var();
        method();
        try {
            depths.push(0);
            valueExpr();
        }catch (LangParseException e) {
            back(depths.pop());
            depths.push(0);
        }
        end_line();
    }

    private void method() throws EofException, LangParseException {
        depths.push(0);
        try {
            add();
        }catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                get();
            }catch (LangParseException ex) {
                try {
                    back(depths.pop());
                    depths.push(0);
                    size();
                } catch (LangParseException exc) {
                    try {
                        back(depths.pop());
                        depths.push(0);
                        remove();
                    } catch (LangParseException exce) {
                        try {
                            back(depths.pop());
                            depths.push(0);
                            isEmpty();
                        } catch (LangParseException excep) {
                            throw new LangParseException(
                                    e.getMessage() + " / "
                                            + ex.getMessage()+ " / "
                                                    + exc.getMessage() + " / "
                                                            + exce.getMessage() + " / "
                                                                    + excep.getMessage()
                            );
                        }
                    }
                }
            }
        }

        depths.pop();
    }

    private void add() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.ADD);
    }

    private void get() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.GET);
    }

    private void size() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.SIZE);
    }

    private void remove() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.REMOVE);
    }

    private void isEmpty() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.EMPTY);
    }


    private boolean end() {
        System.out.println("successfully PRSERed  \n");
        return true;
    }

    private void assignExpr() throws LangParseException, EofException {
        var();
        assignOp();
        valueExpr();
        end_line();
    }

    private void valueExpr() throws LangParseException, EofException {
//        System.out.println("==================== valueExpr .....");
        depths.push(0);
        try{
            var();
        } catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                digit();
            } catch (LangParseException exp){
                try {
                    back(depths.pop());
                    depths.push(0);
//                    System.out.println("---------tird try------");
                    arithmExpr();
                } catch (LangParseException exc) {
                    throw new LangParseException(
                            e.getMessage() + " / "
                                    + exp.getMessage() + "/"
                                            +exc.getMessage()
                    );
                }
            }
        }

        try {

            while (true) {
                depths.push(0);
                op();
                arithmExpr();
                depths.pop();
            }
        } catch (LangParseException e) {
            back(depths.pop());
        }

        depths.pop();
    }


    private void arithmExpr() throws EofException, LangParseException {
//        System.out.println("================== try arithmBody");
        try {
            depths.push(0);
            open_bracket();
        }catch (LangParseException e) {
            back(depths.pop());
            depths.push(0);
        }

        valueExpr();

        try {

            while (true) {
                depths.push(0);
                op();
                arithmExpr();
                depths.pop();
            }
        } catch (LangParseException e) {
            back(depths.pop());
        }

        try {
            depths.push(0);
            close_bracket();
            depths.pop();
        }catch (LangParseException e) {
            back(depths.pop());
        }

        depths.pop();
    }

    private void mulDiv() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.MUL_DIV);

    }

    private void plusMinus() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.PLUS_MINUS);

    }

    private void op() throws LangParseException {
//        System.out.println("==================== OPeration .....");
        depths.push(0);
        try{
            plusMinus();
        } catch (LangParseException | EofException e) {
            try {
                back(depths.pop());
                depths.push(0);
                mulDiv();
            } catch (LangParseException | EofException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
        depths.pop();
    }

    private void var() throws LangParseException, EofException {
//        System.out.println("==================== VAR .....");
        match(getCurrentToken(), Lexem.VAR);
    }

    private void assignOp() throws LangParseException, EofException {
//        System.out.println("==================== assign .....");
        match(getCurrentToken(), Lexem.ASSIGN_OP);
    }

    private void digit() throws LangParseException, EofException {
//        System.out.println("==================== Digit .....");
        match(getCurrentToken(), Lexem.DIGIT);
    }

    private void log_kw_op() throws LangParseException,EofException {
        depths.push(0);
        try{
            log_condition();
        } catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                log_circle();
            } catch (LangParseException ex){
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }

        depths.pop();
    }

    private void log_condition() throws LangParseException, EofException {
//        System.out.println("========================= try log IF");
        if_log();
        log_body();
    }

    private void log_circle() throws LangParseException, EofException {
//        System.out.println("========================= try log WHILE");
        while_log();
        log_body();
    }

    private void if_log() throws LangParseException, EofException {
        if_kw();
        log_bracket();
    }


    private void while_log() throws  LangParseException, EofException {
        while_kw();
        log_bracket();
    }


    private void if_kw() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.IF_KW);
    }

    private void while_kw() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.WHILE_KW);
    }


    private void log_bracket() throws LangParseException, EofException {
//        System.out.println("========================= try to to a logic expr");
        open_bracket();
        log_expression();
        close_bracket();
    }

    private void open_bracket() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.OPEN_BRACKET);
    }

    private void log_expression() throws LangParseException, EofException {
        valueExpr();
        log_op();
        valueExpr();
    }


    private void log_op() throws LangParseException, EofException {
//        System.out.println("======================= LOG OPERATION");
        match(getCurrentToken(), Lexem.LOGIC_OP);
    }

    private void close_bracket() throws LangParseException, EofException {
//        System.out.println("------close-bracket-----------");
        match(getCurrentToken(), Lexem.CLOSE_BRACKET);
    }

    private void log_body() throws LangParseException, EofException {
        open_brace();
        int count = 0;
        try {
            while (true) {
                depths.push(0);
                expr();
                depths.pop();
                count++;
            }
        }catch (LangParseException e){
            if (count > 0) {
                back(depths.pop());
            }else {
                throw e;
            }
        }
        close_brace();
    }


    private void open_brace() throws LangParseException, EofException {
//        System.out.println("------   {    --------");
        match(getCurrentToken(), Lexem.OPEN_BRACE);
    }


    private void close_brace() throws LangParseException, EofException {
//        System.out.println("------   }    --------");
        match(getCurrentToken(), Lexem.CLOSE_BRACE);
    }

    private void end_line() throws LangParseException, EofException {
//        System.out.println("==================== EndLine .....");
        match(getCurrentToken(), Lexem.END_LINE);
    }

    private void input_output() throws LangParseException, EofException {
        depths.push(0);
        try{
            outPut();
        } catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                inPut();
            } catch (LangParseException ex){
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }

        depths.pop();
    }

    private void outPut() throws EofException, LangParseException {
//        System.out.println("==================== try to output ");
        outPut_op();
        valueExpr();
        end_line();
    }

    private void inPut() throws EofException, LangParseException {
//        System.out.println("==================== try to input ");
        inPut_op();
        valueExpr();
        end_line();

    }

    private void outPut_op() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.OUTPUT_OP);
    }

    private void inPut_op() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.INPUT_OP);
    }

    private void listExpr() throws EofException, LangParseException {
        listType();
        var();
        end_line();
    }

    private void listType() throws EofException, LangParseException {
//        System.out.println("==================== ListType .....");
        match(getCurrentToken(), Lexem.LIST);
    }

    private void match(Token token, Lexem lexem) throws LangParseException {

        if (!token.getLexem().equals(lexem)) {
            throw new LangParseException( lexem.name() + " expected " +
                    "but " + token.getLexem().name() + " found");
        }
    }

    private void back(int step) {
        for(int i = 0; i < step; i++){
            if(iterator.hasPrevious()){
                iterator.previous();
            }
        }
    }

    private Token getCurrentToken() throws EofException {
        if (!iterator.hasNext()) {
             end();
        }
        if(iterator.hasNext()) {
            Token token = iterator.next();
            depths.push(depths.pop()+1);
//            System.out.println(token.toString() );
            return token;
        } else {
            throw new EofException("EOF");
        }
    }
}

