package main.mylang.rpn_convertor;

import main.mylang.lexer.Lexem;
import main.mylang.token.Token;
import main.mylang.var_table.VarTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RpnConvertor {
    private List<Token> tokens;
    private VarTable varTable;

    private void fromStackToList(Stack<Token> stack, List<Token> list) {
        while(!stack.empty()) {
            list.add(stack.pop());
        }
    }

    public RpnConvertor(List<Token> tokens, VarTable varTable) {
        this.tokens = tokens;
        this.varTable = varTable;
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

            if (curType == Lexem.OUTPUT_OP) {
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
//                System.out.println("\n 1 STACK" + stack + "\n 1 LIST" + rpnList.toString());
                continue;
            }

            if (curType.getPriority() < 0){
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
            if(curType == Lexem.CLOSE_BRACKET) {
//                System.out.println("before while " + stack.indexOf(stack.peek()) + stack.peek().toString());
                // до тех пор, пока верхний эл-т стека не окжется отк скобкой,
                // вытеснять их в выходной список (скобки удалть)
                while (stack.peek().getLexem() != Lexem.OPEN_BRACKET) {
//                    System.out.println(stack.indexOf(stack.peek()) + stack.peek().toString());
                    rpnList.add(stack.pop());
//                    System.out.println("end of body while " + stack.indexOf(stack.peek()) + stack.peek().toString());
                }
                stack.pop();
//                System.out.println("after while " + stack.indexOf(stack.peek()) + stack.peek().toString());

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
//                        System.out.println("\n 2 STACK" + stack + "\n2 LIST" + rpnList.toString());
                        varTable.add(varTrans, Integer.toString(whileKwPosit.pop()));
                    }
                    varTable.add("p_" + Integer.toString(oldTransNumb),
                            Integer.toString(falseTransPointer));
                }
            }

            if (curType == Lexem.IF_KW || curType == Lexem.WHILE_KW) {
                rpnWithTrans.push(curType);
                if (curType == Lexem.WHILE_KW) {
                    whileKwPosit.push(rpnList.size());
                }
//                System.out.println("\n 2_3 STACK" + stack + "\n2_3 LIST" + rpnList.toString() + "\nRPN" + rpnWithTrans);
                continue;
            }

            // Если стек пуст или приоритет входыщего симпвола больше, чем верхний элемент стека, добавить в стек
            if (stack.empty() || stack.peek().getLexem().getPriority() < curType.getPriority()) {
                stack.push(curToken);
            } else {
                Token topToken = stack.pop();
//                System.out.println("1 " + topToken.toString());
                // Если стек не пуст и приоритет входыщего символа меньше или равен приоритету верхнего элемента стека,

                while (!stack.empty() && topToken.getLexem().getPriority() >= curType.getPriority()) {
//                    System.out.println("2 " + topToken.toString());
                    // вытеснять верхний элемент стека в выходной спи.сок до тех пор, пока условие выполняется
                    rpnList.add(topToken);
                    topToken = stack.pop();
//                    System.out.println("3 " + topToken.toString());
                }
                stack.push(topToken);
                stack.push(curToken);
            }
//            System.out.println("\n 3 STACK" + stack + "\n3 LIST" + rpnList.toString());

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

        // когда входные элемент закончились, вытеснить остающиеся в стеке элементы в выходной список
        fromStackToList(stack, rpnList);

        return rpnList;
    }
}
