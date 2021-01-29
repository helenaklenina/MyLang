package main.mylang.rpn_convertor;

import main.mylang.lexer.Lexem;
import main.mylang.token.Token;
import main.mylang.var_table.VarTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RpnConvertor {
    private List<Token> tokens;
//    private VarTable varTable;

    private void fromStackToList(Stack<Token> stack, List<Token> list) {
        while (!stack.empty()) {
            list.add(stack.pop());
        }
    }

    public RpnConvertor(List<Token> tokens) {
        this.tokens = tokens;
//        this.varTable = varTable;
    }

    public List<Token> getRpn() {
        List<Token> rpnList = new ArrayList<>();
        Stack<Token> stack = new Stack<>();
        Stack<Lexem> rpnWithTrans = new Stack<>();
        Stack<Integer> whileKwPosit = new Stack<>();
        int transNumb = 0;

        for (Token curToken : tokens) {
            Lexem curType = curToken.getLexem();
//            System.out.println("currrent " + curToken.toString());
//            System.out.println("Stack" + stack);
//            System.out.println("STRING" + rpnList);
//            System.out.println("rpnTrans" + rpnWithTrans);
//            System.out.println("While pos" + whileKwPosit);
//            System.out.println("VarTable" + varTable);

            if (curType.getPriority() < 0) {
                continue;
            }

            if (curType == Lexem.OUTPUT_OP) {
                rpnList.add(new Token(Lexem.OUTPUT_NEWLINE, "\n"));
                fromStackToList(stack, rpnList);
                stack.add(new Token(Lexem.OUTPUT_OP, "<<"));
                continue;
            }

            if (curType == Lexem.INPUT_OP) {
                fromStackToList(stack, rpnList);
                stack.add(new Token(Lexem.INPUT_OP, ">>"));
                continue;
            }


            // Если этот символ - число (или переменная), то просто помещаем его в выходную строку.
            if (curType == Lexem.VAR || curType == Lexem.DIGIT) {
                rpnList.add(curToken);
                continue;
            }

            // Если символ - открыввающаяся скобка - (
            if (curType == Lexem.OPEN_BRACKET) {
                stack.push(curToken);
                continue;
            }

            // Если символ - обозночает конец строки - ;
            if (curType == Lexem.END_LINE) {
                fromStackToList(stack, rpnList);
                continue;
            }

            // Если символ - закрывающаяся скобка )
            if (curType == Lexem.CLOSE_BRACKET) {
                // до тех пор, пока верхний эл-т стека не окжется отк скобкой,
                // вытеснять их в выходной список (скобки удалть)
                while (stack.peek().getLexem() != Lexem.OPEN_BRACKET) {
                    rpnList.add(stack.pop());
                }
                stack.pop();

                continue;
            }

            // Если символ - фигурная открывающаяся скобка {
            if (curType == Lexem.OPEN_BRACE) {
                if (!rpnWithTrans.empty()) {
                    rpnList.add(new Token(Lexem.VAR, "p_" + Integer.toString(++transNumb)));
                    rpnList.add(new Token(Lexem.FALSE_TRANS, "!F"));
                }
                continue;
            }

            // Если символ - фигурная закрывающаяся скобка }
            if (curType == Lexem.CLOSE_BRACE) {
                if (!rpnWithTrans.empty()) {
                    int falseTransPointer = rpnList.size();
                    int oldTransNumb = transNumb;

                    if (rpnWithTrans.lastElement() == Lexem.WHILE_KW) {
                        falseTransPointer += 2;
                        String varTrans = "p_" + Integer.toString(++transNumb);
                        rpnList.add(new Token(Lexem.VAR, varTrans));
                        rpnList.add(new Token(Lexem.UNCONDITIONAL_TRANSITION, "!"));
                        VarTable.getInstance().add(varTrans, whileKwPosit.pop());
                    }
                    VarTable.getInstance().add("p_" + Integer.toString(oldTransNumb), falseTransPointer);
                    rpnWithTrans.pop();
                }
                continue;
            }

            if (curType == Lexem.IF_KW || curType == Lexem.WHILE_KW) {
                rpnWithTrans.push(curType);
                if (curType == Lexem.WHILE_KW) {
                    whileKwPosit.push(rpnList.size());
                }
                continue;
            }

            // Если стек пуст или приоритет входыщего симпвола больше, чем верхний элемент стека, добавить в стек
            if (stack.empty() || stack.peek().getLexem().getPriority() < curType.getPriority()) {
                stack.push(curToken);
            } else {
                Token topToken = stack.pop();
                // Если стек не пуст и приоритет входыщего символа меньше или равен приоритету верхнего элемента стека,

                while (!stack.empty() && topToken.getLexem().getPriority() >= curType.getPriority()) {
                    // вытеснять верхний элемент стека в выходной спи.сок до тех пор, пока условие выполняется
                    rpnList.add(topToken);
                    topToken = stack.pop();
                }
                stack.push(topToken);
                stack.push(curToken);
            }
        }

        // когда входные элемент закончились, вытеснить остающиеся в стеке элементы в выходной список
        fromStackToList(stack, rpnList);

        return rpnList;
    }
}