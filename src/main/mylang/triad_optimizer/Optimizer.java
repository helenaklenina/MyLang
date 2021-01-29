package main.mylang.triad_optimizer;

import main.mylang.exception.NotImplementedException;
import main.mylang.exception.PerformException;
import main.mylang.lexer.Lexem;
import main.mylang.token.Token;
import main.mylang.triad_optimizer.triad.ConstTriad;
import main.mylang.triad_optimizer.triad.SameTriad;
import main.mylang.triad_optimizer.triad.Triad;
import main.mylang.var_table.VarTable;

import java.io.IOException;
import java.util.*;


public class Optimizer {
    private List<Token> rpn;
    private VarTable varTable;
//    private Token inputToken;

    private class WrapInt {
        int value;

        public WrapInt(int value) {
            this.value = value;
        }
    }

    public Optimizer(List rpn) {
        this.rpn = rpn;
//        this.varTable = varTable;
    }

    public List optimize() throws NotImplementedException { // ПолИЗ
//      1. поиск триад
        List<Triad> triads = getTriads();
        System.out.println("Triads: " + triads + '\n' );

//      2. оптимизация триад
//        Свертка
        optimTriads(triads);
        System.out.println("\nOptimize triads: " + triads + '\n' );

//        Избавление от ненужных триад
        excludeUnnecessaryOperation(triads);
        System.out.println("\nWith out unnecessory triads: " + triads + '\n');

//      3. вставиться триады в нужные места откуда их взяли
        replaceTriads(triads);
//              				|	|
//        ПолИЗ уменьшается, соответственно нужно:
//            1. Передвигать безусловные переходы (!) и переходы по лжи (!F)
        adjustTransitions();
        rpn.removeIf(token -> (token.getLexem() == Lexem.EMPTY_LEXEME));

        return rpn;
    }


    public List<Triad> getTriads() {
        List<Triad> triads = new ArrayList<>();
        boolean ifOrLoop = false;
        int pos = 0;

        for (int lexId = 0; lexId < rpn.size(); ++lexId) {
            Token curToken = rpn.get(lexId);
            if (curToken.getLexem() == Lexem.FALSE_TRANS) {
                // Jumping to the false transition
                if (!ifOrLoop) {
                    pos = VarTable.getInstance().getValue(rpn.get(lexId - 1).getValue());
                }
                ifOrLoop = true;
            }
            if (lexId == pos) {
                ifOrLoop = false;
            }

//            if (curToken.getLexem() == Lexem.INPUT_OP) {
//                inputToken = rpn.get(lexId - 1);
//            }

            if (curToken.getLexem() == Lexem.ASSIGN_OP) {
                if (!ifOrLoop) {
                    makeTriads(triads, new WrapInt(lexId));
                }
            }
        }
        return triads;
    }

    private void makeTriads(List<Triad> triads, WrapInt id) {
        Token operation = rpn.get(id.value);
        int endPos = id.value;
        Stack<Token> args = new Stack<>();
        List<Lexem> ARGUMENTS = List.of(Lexem.DIGIT, Lexem.VAR);
        List<Lexem> OPERATIONS = List.of(Lexem.MUL_DIV, Lexem.LOGIC_OP, Lexem.PLUS_MINUS);

        for (int i = 0; i < 2; i++) {
            Token current = rpn.get(--id.value);
            Lexem type = current.getLexem();
            if (ARGUMENTS.indexOf(type) > -1) {
                args.push(current);
//                System.out.println(args);
            } else if (OPERATIONS.indexOf(type) > -1) {
                makeTriads(triads, id);
//                Тут должна как-то сохранятся ссылка и потом классться в аргументы, и попаться в триаду
                Token link = new Token(Lexem.LINK, '^' + Integer.toString((triads.size() - 1)));
//                System.out.println("link: " + link);
                args.push(link);
            } else {
                return;
            }
        }

        Token firstArg = args.pop();
        Token secondArg = args.pop();
        Triad triad = new Triad(operation, firstArg, secondArg,  id.value, endPos);
        triads.add(triad);
    }

    private void optimTriads(List<Triad> triads) {
        int idOfInputTriad = -1;
        for (int i = 0; i < triads.size(); i++) {
            Triad cur = triads.get(i);

//            System.out.println("\nCurrent triad:" + cur);
//            System.out.println("Var Table: " + VarTable.getInstance().toString());
//            System.out.println(inputToken);
            try {
//                if (inputToken.getValue().equals(cur.getFirst_op().getValue()) ||
//                        inputToken.getValue().equals(cur.getFirst_op().getValue())) {
//                    idOfInputTriad = i;
//                    triads.remove(cur);
//                    --i;
//                    continue;
//                }
//
//                if (isLink(cur.getFirst_op())) {
//                    if (linkToint(cur.getFirst_op().getValue()) == idOfInputTriad) {
//                        triads.remove(cur);
//                        --i;
//                        continue;
//                    }
//                }
//
//                if (isLink(cur.getSecond_op())) {
//                    if (linkToint(cur.getSecond_op().getValue()) == idOfInputTriad ) {
//                        triads.remove(cur);
//                        --i;
//                        continue;
//                    }
//                }

                if (cur.getOperation().getLexem() == Lexem.ASSIGN_OP) {
                    optimAssign(triads, i);
                } else {
                    optimExpr(triads, i);
                }
            } catch (NotImplementedException e) {
                //...
            }
        }
    }

    private void optimAssign(List<Triad> triads, int i) {
        try {
            Triad triad = triads.get(i);
            if (isVariable(triad.getFirst_op().getValue())) {
                Token secOp = triad.getSecond_op();
                if (isLink(secOp)){
                    triad.setSecond_op(followingLink(triads, secOp.getValue()));
                }
                VarTable.getInstance().add(triad.getFirst_op().getValue(), Integer.valueOf(triad.getSecond_op().getValue()));
            }
        } catch (Exception e) {
            System.out.println("ERROR IN OPTIMIZE ASSIGN");
        }
    }

    private void optimExpr(List<Triad> triads, int i) {
        try {
            Triad curTriad = triads.get(i);

            if (isLink(curTriad.getFirst_op())) {
                curTriad.setFirst_op(followingLink(triads, curTriad.getFirst_op().getValue()));
                triads.set(i, curTriad);
            }

            if (isLink(curTriad.getSecond_op())) {
                curTriad.setSecond_op(followingLink(triads, curTriad.getSecond_op().getValue()));
                triads.set(i, curTriad);
            }

            triads.set(i, curTriad);

            int result = curTriad.rate();

            Triad constTriad = new ConstTriad(new Token(Lexem.DIGIT, Integer.toString(result)), curTriad.getStart(), curTriad.getEnd());
            triads.set(i, constTriad);

        } catch (NotImplementedException e) {
            // ....
        }
    }

    private void excludeUnnecessaryOperation(List<Triad> triads) throws NotImplementedException {
        Map<Token, Integer> numbersOfDependencies = makeNumbersOfDependencies(triads);

        replaceWithSameTriads(triads, numbersOfDependencies);
        System.out.println("Triads after excluding algorithm: " + triads + "\n");

        fixRefsToTriad(triads);
        triads.removeIf(triad -> (triad instanceof SameTriad));
    }

    private Map<Token, Integer> makeNumbersOfDependencies(List<Triad> triads) throws NotImplementedException {
        Map<Token, Integer> numbersOfDependencies = new HashMap<>();

        for (int i = 0; i < triads.size(); ++i) {
            Triad curTriad = triads.get(i);
            if (curTriad instanceof ConstTriad) {
                continue;
            }
            int firstNumb = getNumberOfDependencies(numbersOfDependencies, curTriad.getFirst_op());
            int secondNum = getNumberOfDependencies(numbersOfDependencies, curTriad.getSecond_op());

            numbersOfDependencies.put(new Token(Lexem.LINK, '^' + Integer.toString(i)),
                                                                        Math.max(firstNumb, secondNum));
            try {
                if (curTriad.getOperation().getLexem() == Lexem.ASSIGN_OP) {
                    numbersOfDependencies.put(curTriad.getFirst_op(), i + 1);
                }
            } catch (NotImplementedException e) {
//                ...
            }
//            System.out.println(numbersOfDependencies);
        }
        return numbersOfDependencies;
    }

    private int getNumberOfDependencies(Map<Token, Integer> numbersOfDependencies, Token arg) {
        if (arg.getLexem() == Lexem.DIGIT) {
            return 0;
        }

        Integer number = numbersOfDependencies.get(arg);

        if (number == null) {
//            System.out.println( arg);
            numbersOfDependencies.put(arg, 0);
            number = 0;
        }
//        System.out.println( number.intValue());

        return number.intValue();
    }

    private void replaceWithSameTriads(List<Triad> triads, Map<Token, Integer> numbersOfDependencies) {
        for (int i = 0; i < triads.size(); ++i) {
            Triad iTriad = triads.get(i);
            if (iTriad instanceof ConstTriad || iTriad instanceof SameTriad) {
                continue;
            }
            for (int j = i + 1; j < triads.size(); ++j) {
                Triad jTriad = triads.get(j);
                if (jTriad instanceof ConstTriad || jTriad instanceof SameTriad) {
                    continue;
                }
                try {
                    Token iToken = new Token(Lexem.LINK, "^" + Integer.toString(i));
                    Token jToken = new Token(Lexem.LINK, "^" + Integer.toString(j));
                    if (iTriad.getFirst_op().equals(jTriad.getFirst_op()) &&
                            iTriad.getSecond_op().equals(jTriad.getSecond_op()) &&
                            iTriad.getOperation().equals(jTriad.getOperation()) &&
                            (numbersOfDependencies.get(iToken).compareTo(numbersOfDependencies.get(jToken)) == 0)) {
                        triads.set(j, new SameTriad(jTriad, i));
                    }
                }
                catch (NotImplementedException ex) {
                    //...
                }
            }
        }
    }


    private void fixRefsToTriad(List<Triad> triads) {
        for (int i = 0; i < triads.size(); ++i) {
            try {
                Triad curTriad = triads.get(i);
                curTriad.setFirst_op(getFixedTriadArgument(triads, curTriad.getFirst_op()));
                curTriad.setSecond_op(getFixedTriadArgument(triads, curTriad.getSecond_op()));
                triads.set(i, curTriad);
            }
            catch (NotImplementedException ex) {
                //...
            }
        }
    }

    private Token getFixedTriadArgument(List<Triad> triads, Token arg) {
        if (arg.getLexem() == Lexem.LINK) {
            Token triadLink = (Token) arg;
            try {
                if (triads.get(Integer.parseInt(triadLink.getValue())) instanceof SameTriad) {
                    SameTriad sameTriad = (SameTriad) triads.get(Integer.parseInt(triadLink.getValue()));
                    triadLink.setValue(String.valueOf(sameTriad.getSameTriadNumber()));
                }

                int index = Integer.parseInt(triadLink.getValue());
                triadLink.setValue(String.valueOf(index - getSameTriadCountBeforeIndex(triads, index)));

                return triadLink;
            } catch (NumberFormatException e) {
                return arg;
            }
        }

        return arg;
    }

    private int getSameTriadCountBeforeIndex(List<Triad> triads, int index) {
        int counter = 0;
        for (int i = 0; i < index; ++i) {
            if (triads.get(i) instanceof SameTriad) {
                ++counter;
            }
        }

        return counter;
    }

    private void replaceTriads(List<Triad> triads) {
        for (Triad triad : triads) {
            try {
                if (triad.getOperation().getLexem() == Lexem.ASSIGN_OP) {

                    for (int i = triad.getEnd() - 1; i > triad.getStart(); --i) {
                        rpn.set(i, new Token(Lexem.EMPTY_LEXEME, ""));
                    }

                    List<Token> secondArgumentTokens = triad.getSecond_op().tokenize();
                    for (int i = 0; i < secondArgumentTokens.size(); ++i) {
                        rpn.set(triad.getStart() + 1 + i, secondArgumentTokens.get(i));
                    }
                }
            }
            catch (NotImplementedException e) {
                //...
            }
        }
    }

    private void adjustTransitions() {
        for (int i = 0; i < rpn.size(); ++i) {
            if (rpn.get(i).getLexem() == Lexem.UNCONDITIONAL_TRANSITION ||
                    rpn.get(i).getLexem() == Lexem.FALSE_TRANS) {
                String transitionVarName = rpn.get(i - 1).getValue();
                int transitionVarValue = VarTable.getInstance().getValue(transitionVarName);
                transitionVarValue -= getEmptyLexemCountBeforeIndex(transitionVarValue);
                VarTable.getInstance().add(transitionVarName, transitionVarValue);
            }
        }
    }

    private int getEmptyLexemCountBeforeIndex(int index) {
        int counter = 0;
        for (int i = 0; i < index; ++i) {
            if (rpn.get(i).getLexem() == Lexem.EMPTY_LEXEME) {
                ++counter;
            }
        }

        return counter;
    }

    private boolean isVariable(String operand) {
        if (operand.matches("[a-zA-Z]+")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValue(String value) {
        if (value.matches("[-+]?\\d+")) {
            return true;
        } else {
            return  false;
        }
    }

    private boolean isLink(Token operand) {
        if (operand.getLexem() == Lexem.LINK) {
            return true;
        } else {
            return false;
        }
    }

    private int linkToint(String value) {
        return Integer.valueOf(value.replaceFirst("[\\^]", ""));
    }

    private Token followingLink(List<Triad> triads, String operLink) {
        Triad linkTriad = triads.get(linkToint(operLink));
        Token oper = new Token(Lexem.DIGIT, "0");
        if (linkTriad instanceof ConstTriad) {
            oper = linkTriad.getFirst_op();
        }
        return oper;
    }
}
