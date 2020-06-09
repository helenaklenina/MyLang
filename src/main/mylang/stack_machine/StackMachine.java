package main.mylang.stack_machine;

import main.mylang.lexer.Lexem;
import main.mylang.rpn_convertor.RpnConvertor;
import main.mylang.token.Token;
import main.mylang.var_table.VarTable;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

public class StackMachine {
    private List<Token> rpnList;
    private VarTable varTable;
    private Stack<Token> stack;
    int i;

    public StackMachine (List<Token> rpnList, VarTable varTable) {
        System.out.println("RESULT! ");
        this.rpnList = rpnList;
        this.varTable = varTable;
        stack = new Stack<>();
        i = 0;
    }

    public void perform() {
        for (; i < rpnList.size(); i++) {
            Token curToken = rpnList.get(i);
            Lexem curType = curToken.getLexem();
            String curValue = curToken.getValue();

//            System.out.println("Token: " + curToken);
//            System.out.println("Stack: " + stack);
//            System.out.println("VarTable: " + varTable + "\n");

            if (curType == Lexem.VAR || curType == Lexem.DIGIT ) {
                stack.push(curToken);
                continue;
            }

            switch (curType) {
                case ASSIGN_OP:
                    Token val = stack.pop();
                    Token var = stack.pop();

                    varTable.add(var.getValue(), val.getValue());
                    break;

                case PLUS_MINUS:
                    if (curValue.equals("+")){
                        sum();
                    } else {
                        dif();
                    }

                    break;

                case MUL_DIV:
                    if (curValue.equals("*")){
                        int val2 = toInt(stack.pop());
                        int val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 * val2)));
                    } else {
                        int val2 = toInt(stack.pop());
                        int val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 / val2)));
                    }

                    break;

                case LOGIC_OP:
                    int val2;
                    int val1;

                    if (curValue.equals("==")) {
                        val2 = toInt(stack.pop());
                        val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 == val2 ? 1 : 0)));
                    } else if (curValue.equals(">=")) {
                        val2 = toInt(stack.pop());
                        val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 >= val2 ? 1 : 0)));
                    } else if (curValue.equals("<=")) {
                        val2 = toInt(stack.pop());
                        val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 <= val2 ? 1 : 0)));
                    } else if (curValue.equals(">")) {
                        val2 = toInt(stack.pop());
                        val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 >
                                val2 ? 1 : 0)));
                    } else if (curValue.equals("<")) {
                        val2 = toInt(stack.pop());
                        val1 = toInt(stack.pop());

                        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 <
                                val2 ? 1 : 0)));
                    }

                    break;

                case OUTPUT_OP:
                    Token token = stack.pop();

                    String str = "";
                    if (token.getLexem() == Lexem.VAR) {
                        str = varTable.getValue(token.getValue());
                    }
                    else if (token.getLexem() == Lexem.DIGIT) {
                        str = token.getValue();
                    }
//                    if ((i + 2 >= rpnList.size()) ||
//                            ((i + 2 < rpnList.size()) && rpnList.get(i + 2).getLexem() != Lexem.OUTPUT_OP)) {
//                        System.out.println();
//                    } else {
                        System.out.println(str);
//                    }

                    break;

                case INPUT_OP:
                    Token token1 = stack.pop();
                    Scanner sc = new Scanner(System.in);

                    System.out.println(">>");
                    varTable.add(token1.getValue(), Integer.toString(sc.nextInt()));

                    break;

                case FALSE_TRANS:
                    Token point = stack.pop();
                    int transfer = toInt(stack.pop());
//
//                    System.out.println(point.getValue());
//                    System.out.println(transfer);

                    if (transfer <= 0) {
//                        System.out.println(varTable.getValue(point.getValue()));
                        i = Integer.parseInt(varTable.getValue(point.getValue()));
//                        System.out.println(i);
                    }

                    break;

                case UNCONDITIONAL_TRANSITION:
                    Token point1 = stack.pop();
                    i = Integer.parseInt(varTable.getValue(point1.getValue())) - 1;

                    break;

            }

        }
        System.out.println("VarTable: " + varTable + "\n");
    }

    private void sum() {
        int val2 = toInt(stack.pop());
        int val1 = toInt(stack.pop());

        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 + val2)));
    }

    private void dif() {
        int val2 = toInt(stack.pop());
        int val1 = toInt(stack.pop());

        stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 - val2)));
    }

    private int toInt(Token token) {
        int res;
        if (token.getLexem() == Lexem.VAR) {
            res = Integer.parseInt(varTable.getValue(token.getValue()));
        }
        else {
            res = Integer.parseInt(token.getValue());
        }

        return res;
    }
}
