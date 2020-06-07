package main.mylang.rpn_convertor;

import main.mylang.lexer.Lexem;
import main.mylang.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RpnConvertor {
    private List<Token> tokens;

    private void fromStackToList(Stack<Token> stack, List<Token> list) {
        while(!stack.empty()) {
            list.add(stack.pop());
        }
    }

    public RpnConvertor(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getRpn() {
        List<Token> rpnList = new ArrayList<>();
        Stack<Token> stack = new Stack<>();

        for (Token curToken : tokens) {
            Lexem curType = curToken.getLexem();
//            System.out.println("currrent " + curToken.toString());

            // Если этот символ - число (или переменная), то просто помещаем его в выходную строку.
            if (curType == Lexem.VAR || curType == Lexem.DIGIT) {
                rpnList.add(curToken);
//                System.out.println("\n 1 STACK" + stack + "\n 1 LIST" + rpnList.toString());
                continue;
            }

            if (curType == Lexem.OPEN_BRACKET) {
                stack.push(curToken);
                continue;
            }

            if (curType == Lexem.END_LINE) {
                fromStackToList(stack, rpnList);
                continue;
            }

            if(curType == Lexem.CLOSE_BRACKET) {
//                System.out.println("before while " + stack.indexOf(stack.peek()) + stack.peek().toString());
                while (stack.peek().getLexem() != Lexem.OPEN_BRACKET) {
                    System.out.println(stack.indexOf(stack.peek()) + stack.peek().toString());
                    rpnList.add(stack.pop());
                    System.out.println("end of body while " + stack.indexOf(stack.peek()) + stack.peek().toString());
                }stack.pop();
                System.out.println("after while " + stack.indexOf(stack.peek()) + stack.peek().toString());
                continue;
            }

            if (stack.empty() || stack.peek().getLexem().getPriority() < curType.getPriority()) {
                stack.push(curToken);
            } else {
                Token topToken = stack.pop();
                System.out.println("1 " + topToken.toString());
                while (!stack.empty() && topToken.getLexem().getPriority() >= curType.getPriority()) {
                    System.out.println("2 " + topToken.toString());
                    rpnList.add(topToken);
                    topToken = stack.pop();
                    System.out.println("3 " + topToken.toString());
                } stack.push(curToken);
            }
//            System.out.println("\n 2 STACK" + stack + "\n2 LIST" + rpnList.toString());

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

        fromStackToList(stack, rpnList);

        return rpnList;
    }
}
