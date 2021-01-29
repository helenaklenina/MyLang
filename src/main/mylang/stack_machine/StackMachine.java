package main.mylang.stack_machine;

import main.mylang.exception.PerformException;
import main.mylang.lexer.Lexem;
import main.mylang.list.MyList;
import main.mylang.token.Token;
import main.mylang.var_table.VarTable;

import java.beans.IntrospectionException;
import java.util.*;

public class StackMachine {
//    private List<Token> rpnList;
//    private VarTable varTable;
    private List<MyList> allLists;
//    private Stack<Token> context.stack;
//    int i;

//    public StackMachine (List<Token> rpnList, VarTable varTable) {
//        System.out.println("<============== Program output ==============>");
//        this.rpnList = rpnList;
//        this.varTable = varTable;
//        this.allLists = new ArrayList<>();
//        context.stack = new Stack<>();
//        i = 0;
//    }

    public static class Context {
        public List<Token> rpnList;
        public int pos;
        public Stack<Token> stack;
        public boolean newLine;
        public Map<String, Integer> varTable;

        public Context() {
            this.pos = 0;
            this.newLine = true;
            this.stack = new Stack<>();
            this.rpnList = new ArrayList<>();
            this.varTable = VarTable.getInstance().getData();
        }
    }

    private Context context;

    public StackMachine(List<Token> rpnList) {
        this.context = new Context();
        this.context.rpnList = rpnList;
        this.allLists = new ArrayList<>();
    }

    public Context getContext() {
        this.context.varTable = VarTable.getInstance().getData();
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
        VarTable.getInstance().setData(this.context.varTable);
    }

//    public State getState() {
//        return this.state;
//    }
//
//    public void setState(State state) {
//        this.state = state;
//    }

    public void perform() throws PerformException {
        perform(context.rpnList.size());
    }

    public void perform(int n) throws PerformException {
        for (int i = 0;
             (i < n) || (context.pos < context.rpnList.size());
             ++i, ++context.pos) {
            Token curToken = context.rpnList.get(context.pos);
            Lexem curType = curToken.getLexem();
            String curValue = curToken.getValue();

//            System.out.println("\nCurToken" + curToken + " i: " + i + " n; " + n);
//            System.out.println("Stack " + context.stack);
//            System.out.println("varTable " + context.varTable.toString());

//            if (curType == Lexem.FUNCTION) {
            // If next is new thread call
//                if ((context.pos != context.rpnList.size() - 1) &&
//                        (context.rpnList.get(context.pos + 1).getLexem() == Lexem.NEW_THREAD)) {
//                    continue;
//                }

//                state = State.FUNCTION_CALL;
//                return;
//            }
//            if (curType == Lexem.NEW_THREAD) {
//                state = State.NEW_THREAD_CALL;
//                return;
//            }
//            if (curType == Lexem.RETURN_KW) {
//                state = State.RETURN_CALL;
//                return;
//            }

            if (curType == Lexem.VAR || curType == Lexem.DIGIT) {
                context.stack.push(curToken);
            }
            else {
                switch (curType) {
                    case ASSIGN_OP:
                        Token val = context.stack.pop();
                        Token var = context.stack.pop();

                        if (isValueOrDigit(val) && isVariable(var)) {
                            VarTable.getInstance().add(var.getValue(), Integer.valueOf(val.getValue()));

                        }

                        break;

                    case PLUS_MINUS:
                        if (curValue.equals("+")) {
                            sum();
                        } else {
                            dif();
                        }

                        break;

                    case MUL_DIV:
                        if (curValue.equals("*")) {
                            int val2 = toInt(context.stack.pop());
                            int val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 * val2)));
                        } else {
                            int val2 = toInt(context.stack.pop());
                            int val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 / val2)));
                        }

                        break;

                    case LOGIC_OP:
                        int val2;
                        int val1;

                        if (curValue.equals("==")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 == val2 ? 1 : 0)));
                        } else if (curValue.equals(">=")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 >= val2 ? 1 : 0)));
                        } else if (curValue.equals("<=")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 <= val2 ? 1 : 0)));
                        } else if (curValue.equals("!=")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 != val2 ? 1 : 0)));
                        } else if (curValue.equals(">")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 >
                                    val2 ? 1 : 0)));
                        } else if (curValue.equals("<")) {
                            val2 = toInt(context.stack.pop());
                            val1 = toInt(context.stack.pop());

                            context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 <
                                    val2 ? 1 : 0)));
                        }

                        break;

                    case LIST:
                        Token token0 = context.stack.pop();

                        MyList list = new MyList(token0.getValue());
                        context.rpnList.addAll(list);
                        context.varTable.put(token0.getValue(), Integer.valueOf(list.toString()));

                        break;

                    case ADD:
                        Token tokenNode = context.stack.pop();
                        String oldList = context.stack.pop().getValue();
                        for (MyList l : allLists) {
                            if (l.getName().equals(oldList) && tokenNode.getLexem() == Lexem.DIGIT) {
                                l.add(Integer.parseInt(tokenNode.getValue()));
                                context.varTable.replace(oldList, Integer.valueOf(l.toString()));
                            } else if (l.getName().equals(oldList) && tokenNode.getLexem() == Lexem.VAR) {
                                int value = context.varTable.get(tokenNode.getValue());
                                l.add(value);
                                context.varTable.replace(oldList, Integer.valueOf(l.toString()));
                            }
                        }

                        break;

                    case SIZE:
                        String nodeList = context.stack.pop().getValue();

                        for (MyList l : allLists) {
                            if (l.getName().equals(nodeList)) {
                                context.stack.push(new Token(Lexem.DIGIT, Integer.toString(l.size())));

                            }
                        }

                        break;

                    case GET:
                        Token tokNode = context.stack.pop();
                        String lis = context.stack.pop().getValue();
                        int index = context.varTable.get(tokNode.getValue());

                        //                    System.out.println(index);

                        for (MyList l : allLists) {
                            if (l.getName().equals(lis)) {
                                context.stack.push(new Token(Lexem.DIGIT, Integer.toString(index)));
                            }
                        }

                        break;

                    case OUTPUT_NEWLINE:
                        context.newLine = true;
                        continue;

                    case OUTPUT_OP:
                        output();

                        //                    Token token = context.stack.pop();
                        //
                        //                    String str = "";
                        //                    if (token.getLexem() == Lexem.VAR) {
                        //                        str = context.varTable.get(token.getValue());
                        //                    }
                        //                    else if (token.getLexem() == Lexem.DIGIT) {
                        //                        str = token.getValue();
                        //                    }
                        //                    System.out.println(str);


                        break;

                    case INPUT_OP:
                        input();
                        //                    Token token1 = context.stack.pop();
                        //                    Scanner sc = new Scanner(System.in);
                        //
                        //                    System.out.println(">>");
                        //                    context.varTable.put(token1.getValue(), Integer.toString(sc.nextInt()));

                        break;

                    case FALSE_TRANS:
                        falseTransition();

                        //                    Token point = context.stack.pop();
                        //                    int transfer = toInt(context.stack.pop());
                        //
                        //                    if (transfer <= 0) {
                        //                        i = Integer.parseInt(context.varTable.get(point.getValue()));
                        //                    }

                        break;

                    case UNCONDITIONAL_TRANSITION:
                        unconditionalTransition();
//                        Token point1 = context.stack.pop();
//                        i = Integer.parseInt((String) context.varTable.get(Integer.parseInt(point1.getValue()))) - 1;

                        break;
                    default:
                        throw new PerformException("Unexpected token " + curType + ": " +
                                curValue + " during execution");

                }
            }

        }
//        System.out.println("VarTable: " + context.varTable + "\n");

//        if ((context.pos == context.rpnList.size()) &&
//                (state == State.FUNCTION_EXECUTION)) {
//            state = State.FUNCTION_END;
//        } else if ((context.pos == context.rpnList.size()) &&
//                (state == State.NORMAL)) {
//            state = State.END;
//        }
//    }

    }


    private void input() throws PerformException {
        Token token = context.stack.pop();
        isVariable(token);
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n>> ");

        String str = scanner.nextLine();
        this.context.newLine = false;

        VarTable.getInstance().add(token.getValue(), Integer.valueOf(str));
    }

    private void output() throws PerformException {
        Token token = context.stack.pop();

        String str = "";
        if (token.getLexem() == Lexem.VAR) {
            Integer value = VarTable.getInstance().getValue(token.getValue());
            str = String.valueOf(value);
//            if (type.equals("list")) {
//                str = ((GotlList) value).toString();
//            }
        }
        else if (token.getLexem() == Lexem.DIGIT) {
            str = token.getValue();
        }
        else {
            throw new PerformException("Expected variable or digit but got " +
                    token.getLexem() + ": " +
                    token.getValue());
        }

        if (context.newLine) {
            System.out.print("\n<< " );
        }
        System.out.print(str);

        context.newLine = false;
    }

    private void falseTransition() throws PerformException {
        Token pointer = context.stack.pop();
        Token condition = context.stack.pop();

//        System.out.println(pointer);
        isVariable(pointer);

        int conditionValue = toInt(condition);
//        System.out.println(conditionValue);
        if (conditionValue <= 0) {
            // -1 because where is ++context.pos in cycle
            context.pos = toInt(pointer) - 1;
        }
    }

    private void unconditionalTransition() throws PerformException {
//        Token point1 = context.stack.pop();
//        i = Integer.parseInt((String) context.varTable.get(Integer.parseInt(point1.getValue()))) - 1;


        Token pointer = context.stack.pop();
        if (pointer.getLexem() != Lexem.VAR) {
            throw new PerformException("Expected variable, but got " +
                    pointer.getLexem() + ": " +
                    pointer.getValue());
        }

        // -1 because where is ++context.pos in cycle
        context.pos = toInt(pointer) - 1;
    }


    private void sum() throws PerformException {
        int val2 = toInt(context.stack.pop());
        int val1 = toInt(context.stack.pop());

        context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 + val2)));
    }

    private void dif() throws PerformException {
        int val2 = toInt(context.stack.pop());
        int val1 = toInt(context.stack.pop());

        context.stack.push(new Token(Lexem.DIGIT, Integer.toString(val1 - val2)));
    }

    private boolean isValueOrDigit(Token token) throws PerformException {
        if (token.getLexem() != Lexem.DIGIT && token.getLexem() != Lexem.VAR) {
            throw new PerformException("Expected value" + Lexem.DIGIT + "but found" + token.getValue());
        } else {
            return  true;
        }
    }

    private boolean isVariable(Token variable) throws PerformException {
        if (variable.getLexem() != Lexem.VAR) {
            throw new PerformException("Expected variable" + Lexem.VAR + "but found" + variable.getValue());
        } else {
            return true;
        }
    }

    private int toInt(Token token) throws PerformException {
        int  res;
        if (token.getLexem() == Lexem.VAR) {
            try {
                String value = token.getValue();
                res = VarTable.getInstance().getValue(value);
            } catch (NullPointerException e) {
//                 return (Integer);
                throw new PerformException("Variable " + token.getValue() + " wasn't defined");
            }
        } else{
            res = Integer.parseInt(token.getValue());
        }

        return res;

    }

}
