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
    int t = -1;
    Token endToken = new Token("end");

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.depths = new Stack<>();
        this.iterator = tokens.listIterator();

    }

    public void lang() throws LangParseException, EofException {
        while(!end()) {
            expr();
        }
    }

    private void expr() throws LangParseException, EofException {
//        boolean already = true;
        depths.push(0);
        try {
            System.out.println("==================== try assign exp .....");
            assignExpr();
        } catch (LangParseException e){
            try{
                System.out.println("==================== try log if or while .....");
                back(depths.pop());
                depths.push(0);
                log_kw_op();
            } catch (LangParseException ex){
                try {
                    System.out.println("==================== try to output or input .....");
                    back(depths.pop());
                    depths.push(0);
                    input_output();
                } catch (LangParseException exc) {
//                    try {
////                        if (!already) {
//                            back(depths.pop());
//                            depths.push(0);
//                            expr();
////                        }
//                    } catch (LangParseException exce) {
//                        try {
//                            already = false;
//                            back(depths.pop());
//                            depths.push(0);
//                            close_residuary();
//                        }catch (LangParseException excep) {
                            throw new LangParseException(
                                    e.getMessage() + " / "
                                            + ex.getMessage()+ " / "
                                                    + exc.getMessage()
//                                            + " / "
//                                                            + exce.getMessage()
//                                                            + " / "
//                                                                    + excep.getMessage()
                            );
//                        }
//                    }

                }
            }
        }
        depths.pop();
    }

    private void close_residuary() throws LangParseException, EofException {
        depths.push(0);
        try {
            close_brace();
        }catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                close_bracket();
            }catch (LangParseException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
        depths.pop();
    }

    private boolean end() {
        System.out.println("successfully PRSERed  \n");
        return true;
    }

    private void assignExpr() throws LangParseException, EofException {
        var();
        assignOp();
        bodyExpr();
    }

    private void bodyExpr() throws EofException, LangParseException {
        System.out.println("==================== try bodyExpr");
        boolean already_been = false;
        depths.push(0);
        depths.push(0);
        try {
            value();
            end_line();
            already_been = true;
        }catch (LangParseException e) {
            try {
                back(depths.pop() + 1);
                depths.push(0);
                System.out.println(depths.size());
                arithmBody();
            }catch (LangParseException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
        if (!already_been) {
            end_line();
        }

        depths.pop();
    }

    private void arithmBody() throws EofException, LangParseException {
        System.out.println("================== try arithmBody");
        depths.push(0);
        try {
            value();
            op();
            bodyExpr();
        }catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                op_bracket();
            }catch (LangParseException ex) {
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }
        depths.pop();
    }

    private void op_bracket() throws EofException, LangParseException {
        open_bracket();
        arithmBody();
        close_bracket();
    }

    private void mulDiv() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.MUL_DIV);

    }

    private void plusMinus() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.PLUS_MINUS);

    }

    private void op() throws LangParseException {
        System.out.println("==================== OPeration .....");
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

    private void value() throws LangParseException, EofException {
        System.out.println("==================== body is value .....");
        depths.push(0);
        try{
            var();
        } catch (LangParseException e) {
            try {
                back(depths.pop());
                depths.push(0);
                digit();
            } catch (LangParseException ex){
                throw new LangParseException(
                        e.getMessage() + " / "
                                + ex.getMessage()
                );
            }
        }

        depths.pop();
    }

    private void var() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.VAR);
    }

    private void assignOp() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.ASSIGN_OP);
    }

    private void digit() throws LangParseException, EofException {
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
        System.out.println("========================= try log IF");
        if_log();
        log_body();
    }

    private void log_circle() throws LangParseException, EofException {
        System.out.println("========================= try log WHILE");
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
        System.out.println("========================= try to to a logic expr");
        open_bracket();
        log_expression();
        close_bracket();
    }

    private void open_bracket() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.OPEN_BRACKET);
    }

    private void log_expression() throws LangParseException, EofException {
        value();
        log_op();
        value();
    }


    private void log_op() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.LOGIC_OP);
    }

    private void close_bracket() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.CLOSE_BRACKET);
    }

    private void log_body() throws LangParseException, EofException {
        open_brace();
        expr();
        close_brace();
    }


    private void open_brace() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.OPEN_BRACE);
    }


    private void close_brace() throws LangParseException, EofException {
        match(getCurrentToken(), Lexem.CLOSE_BRACE);
    }

    private void end_line() throws LangParseException, EofException {
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
        System.out.println("==================== try to output ");
        outPut_op();
        // СДЕЛАТЬ ЗАВИСИМОСТЬ ОТ ТИПА
        value();
        end_line();
    }

    private void inPut() throws EofException, LangParseException {
        System.out.println("==================== try to input ");
        inPut_op();
        // СДЕЛАТЬ ЗАВИСИМОСТЬ ОТ ТИПА
        value();
        end_line();

    }

    private void outPut_op() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.OUTPUT_OP);
    }

    private void inPut_op() throws EofException, LangParseException {
        match(getCurrentToken(), Lexem.INPUT_OP);
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
            if (end()) {
                System.out.println("Parser is " + end());
            }
        }
//        if (iterator.hasNext()) {
//            System.out.println(Integer.toString(t + 1) + ' ' + tokens.get(t + 1).getLexem());
//            return tokens.get(++t);
//        } else {
//            throw new EofException("EOF");
//        }
        if (iterator.hasNext()) {
            Token token = iterator.next();
            depths.push(depths.pop()+1);
            System.out.println(token.toString() );
            return token;
        } else {
            throw new EofException("EOF");
        }
    }
}

