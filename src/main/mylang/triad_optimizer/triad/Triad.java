package main.mylang.triad_optimizer.triad;

import main.mylang.exception.NotImplementedException;
import main.mylang.exception.PerformException;
import main.mylang.lexer.Lexem;
import main.mylang.stack_machine.StackMachine;
import main.mylang.token.Token;

import java.util.*;

import static java.lang.Character.isDigit;

public class Triad {

    private Token operation;
    private Token second_op;
    private Token first_op;
    private boolean changed;
    private int rateResult;
    private int start;
    private int end;

    public Triad(Token operation, Token first_op, Token second_op,  int s, int e) {
        this.operation = operation;
        this.second_op = second_op;
        this.first_op = first_op;
        this.end = e;
        this.start = s;
        this.changed = true;
    }

    public int rate() throws NotImplementedException {
        if (changed) {
            try {
//                System.out.println("========RATE=========");
                List<Token> rpn = first_op.tokenize();
                rpn.addAll(second_op.tokenize());
                rpn.add(operation);
//                System.out.println("rpn: " + rpn);
                StackMachine stackMachine = new StackMachine(rpn);
                stackMachine.perform();
                Map<String, Integer> res = stackMachine.getContext().varTable;
//                System.out.println("varRomStack = " + res);
                rateResult = Integer.parseInt(stackMachine.getContext().stack.peek().getValue());
//                System.out.println("varRes = " + rateResult);
//                if (rateResult == 0 && Integer.toString(rateResult).equals(null)) {
//                    try {
//                        rateResult = Integer.parseInt((String) res.get(first_op.getValue()));
//                    } catch (IndexOutOfBoundsException e){
//                        rateResult = Integer.parseInt((String) res.get(second_op.getValue()));
//                    }
//                }

                changed = false;
            }
            catch (NotImplementedException | PerformException e) {
                //...
            }
        }
//        System.out.println("rateRes = " + rateResult);
        return rateResult;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
        changed = true;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
        changed = true;
    }

    public Token getOperation() throws NotImplementedException {
        return operation;
    }

    public void setOperation(Token operation) throws NotImplementedException {
        this.operation = operation;
        changed = true;
    }

    public Token getSecond_op() throws NotImplementedException {
        return second_op;
    }

    public void setSecond_op(Token second_op) throws NotImplementedException {
        this.second_op = second_op;
        changed = true;
    }

    public Token getFirst_op(){
        return first_op;
    }

    public void setFirst_op(Token first_op) {
        this.first_op =  first_op;
        changed = true;
    }

    public List<Token> tokenize() throws NotImplementedException {
        List<Token> tokens = new ArrayList<>();
        try {
            int res = rate();
            tokens.add(new Token(Lexem.DIGIT, Integer.toString(res)));
        } catch (Exception e) {
            tokens.addAll(first_op.tokenize());
            tokens.addAll(second_op.tokenize());
            tokens.add(operation);
        }

        return tokens;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Triad other = (Triad) obj;

        return (first_op.equals(other.first_op) && second_op.equals(other.second_op) &&
                operation.equals(other.operation) && start == other.start && end == other.end);
    }

    @Override
    public String toString() {
        return '(' + this.first_op.getValue() + ", " + this.second_op.getValue() + ')' + this.operation.getValue();
    }
}
