package main.mylang.triad_optimizer.triad;

import main.mylang.exception.NotImplementedException;
import main.mylang.exception.PerformException;
import main.mylang.lexer.Lexem;
import main.mylang.stack_machine.StackMachine;
import main.mylang.token.Token;
import main.mylang.triad_optimizer.triad.Triad;

import java.util.List;

public class ConstTriad extends Triad {

    public ConstTriad(Token first_op, int s, int e) {
        super(null, first_op, new Token(Lexem.DIGIT, "0"), s, e);
    }

    @Override
    public int rate() throws NotImplementedException {
        return Integer.parseInt(getFirst_op().getValue());
    }

    @Override
    public List<Token> tokenize() throws NotImplementedException {
        throw new NotImplementedException("Can't tokenize Constant Triad");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "C(" + getFirst_op().getValue() + ", " + '0' + ')';
    }

    @Override
    public int getStart() {
        return super.getStart();
    }

    @Override
    public void setStart(int start) {
        super.setStart(start);
    }

    @Override
    public int getEnd() {
        return super.getEnd();
    }

    @Override
    public void setEnd(int end) {
        super.setEnd(end);
    }

    @Override
    public Token getOperation() throws NotImplementedException {
        throw new NotImplementedException("Can't get operation of Constant Triad");
    }

    @Override
    public void setOperation(Token operation) throws NotImplementedException {
        throw new NotImplementedException("Can't set operation of Constant Triad");
    }

    @Override
    public Token getSecond_op() throws NotImplementedException {
        throw new NotImplementedException("Can't get second argument of Constant Triad");
    }

    @Override
    public void setSecond_op(Token second_op) throws NotImplementedException{
        throw new NotImplementedException("Can't set second argument of Constant Triad");
    }

    @Override
    public Token getFirst_op() {
        return super.getFirst_op();
    }

    @Override
    public void setFirst_op(Token first_op) {
        super.setFirst_op(first_op);
    }
}

