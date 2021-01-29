package main.mylang.triad_optimizer.triad;

import main.mylang.exception.NotImplementedException;
import main.mylang.lexer.Lexem;
import main.mylang.token.Token;

import java.util.List;

public class SameTriad extends Triad {
    private Triad oldTriad;
    private int sameTriadNumber;

    public SameTriad(Triad oldTriad, int sameTriadNumber) {
        super(null, new Token(Lexem.DIGIT, Integer.toString(sameTriadNumber)), new Token(Lexem.DIGIT, "0"), oldTriad.getStart(), oldTriad.getEnd());
        this.oldTriad = oldTriad;
        this.sameTriadNumber = sameTriadNumber;
    }

    public int getSameTriadNumber() {
        return sameTriadNumber;
    }

    @Override
    public int rate() throws NotImplementedException {
        return super.rate();
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
        throw new NotImplementedException("Can't get operation of Same Triad");
    }

    @Override
    public void setOperation(Token operation) throws NotImplementedException {
        throw new NotImplementedException("Can't set operation of Same Triad");
    }

    @Override
    public Token getSecond_op() throws NotImplementedException {
        throw new NotImplementedException("Can't get second argument of Same Triad");
    }

    @Override
    public void setSecond_op(Token second_op) throws NotImplementedException{
        throw new NotImplementedException("Can't set second argument of Same Triad");
    }

    @Override
    public Token getFirst_op() {
        return super.getFirst_op();
    }

    @Override
    public void setFirst_op(Token first_op) {
        super.setFirst_op(first_op);
    }

    @Override
    public List<Token> tokenize() throws NotImplementedException {
        return super.tokenize();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "SAME(" + getFirst_op().getValue() + ", " + '0' + ')';
    }
}
