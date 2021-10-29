package modelCTL_java.Model;

/*
Class for Ctl Formula
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CtlFormula {
    public HashMap<String, String> convertionString;
    private KripkeStructure kripke;
    private State state;
    private String expression;

    public CtlFormula(String expression, State state, KripkeStructure kripke) throws Exception {
        this.convertionString = new HashMap<String, String>();
        this.convertionString.put("and", "&");
        this.convertionString.put("or", "|");
        this.convertionString.put("->", ">");
        this.convertionString.put("not", "!");
        this.setKripke(kripke);
        this.setState(state);
        this.setExpression(convertToSystemFormula(expression));
    }

    public KripkeStructure getKripke() {
        return kripke;
    }

    public void setKripke(KripkeStructure kripke) {
        this.kripke = kripke;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public HashMap<String, String> getConvertionString() {
        return convertionString;
    }

    public void setConvertionString(HashMap<String, String> convertionString) {
        this.convertionString = convertionString;
    }

    /*
    Determine whether given state satisfy given CTl formula
     */

    /*
    Replace symbols with known system symbols
     */
    public String convertToSystemFormula(String expression) throws Exception {
        for (Object entryset : this.convertionString.entrySet()) {
            Entry<String, String> entry = (Entry<String, String>) entryset;
            expression = expression.replace(entry.getKey(), entry.getValue());
        }
        return expression;
    }

    /*
    Determine Type of SAT for a given expression
     */

    public boolean isSatisfy() throws Exception {
        List<State> states = sat(getExpression());
        return states.contains(getState());
    }

    /*
    Determine States that Satisfy given expression
     */

    private BinarySymbolInfo getTypeSAT(String expression) throws Exception {
        //remove extra brackets
        expression = removeExtraBrackets(expression);
        //look for binary implies
        if (expression.indexOf(">") != -1) {
            BinarySymbolInfo info = isBinaryOp(expression, ">");
            if (info != null && info.isBinary()) {
                info.setTypeSat(TypeSAT.Implies);
                return info;
            }
        }
        // look for binary and
        if (expression.contains("&")) {
            BinarySymbolInfo info = isBinaryOp(expression, "&");
            if (info != null && info.isBinary()) {
                info.setTypeSat(TypeSAT.And);
                return info;
            }
        }
        // look for binary or
        if (expression.contains("|")) {
            BinarySymbolInfo info = isBinaryOp(expression, "|");
            if (info != null && info.isBinary()) {
                info.setTypeSat(TypeSAT.Or);
                return info;
            }
        }
        // look for binary AU
        if (expression.startsWith("A(")) {
            String strippedExpression = expression.substring(2, expression.length() - 1);
            BinarySymbolInfo info = isBinaryOp(strippedExpression, "U");
            if (info != null && info.isBinary()) {
                info.setTypeSat(TypeSAT.AU);
                return info;
            }
        }
        // look for binary EU
        if (expression.startsWith("E(")) {
            String strippedExpression = expression.substring(2, expression.length() - 1);
            BinarySymbolInfo info = isBinaryOp(strippedExpression, "U");
            if (info != null && info.isBinary()) {
                info.setTypeSat(TypeSAT.EU);
                return info;
            }
        }
        // look for unary T, F, !, AX, EX, AG, EG, AF, EF, atomic
        if (expression.equals("T")) {
            return new BinarySymbolInfo(false, expression, "", TypeSAT.AllTrue);
        }
        if (expression.equals("F")) {
            return new BinarySymbolInfo(false, expression, "", TypeSAT.AllFalse);
        }
        if (isAtomic(expression)) {
            return new BinarySymbolInfo(false, expression, "", TypeSAT.Atomic);
        }
        if (expression.startsWith("!")) {
            String leftExpression = expression.substring(1);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.Not);
        }
        if (expression.startsWith("AX")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.AX);
        }
        if (expression.startsWith("EX")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.EX);
        }
        if (expression.startsWith("EF")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.EF);
        }
        if (expression.startsWith("EG")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.EG);
        }
        if (expression.startsWith("AF")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.AF);

        }
        if (expression.startsWith("AG")) {
            String leftExpression = expression.substring(2);
            return new BinarySymbolInfo(false, leftExpression, "", TypeSAT.AG);
        }

        return null;
    }

    private List<State> sat(String expression) throws Exception {
        String mssg = String.format("Original Expression: {0}", expression);

        List<State> states = new ArrayList<State>();
        BinarySymbolInfo info = getTypeSAT(expression);

        if (info == null) {
            throw new Exception("Invalid CTL expression");
           // return states;
        }
        switch (info.getTypeSat()) {
            case AllTrue:
                // all states
                states.addAll(this.kripke.getStates());
                break;
            case AllFalse:
                break;
            case Atomic:
                for (State state : this.kripke.getStates()) {
                    // empty
                    if (state.getAtoms().contains(info.getLeftExpression()))
                        states.add(state);

                }
                break;
            case Not:
                // S − SAT (φ1)
                states.addAll(this.kripke.getStates());
                List<State> f1States = sat(info.getLeftExpression());
                for (State state : f1States) {
                    states.remove(state);

                }
                break;
            case And:
                // SAT (φ1) ∩ SAT (φ2)
                List<State> andF1States = sat(info.getLeftExpression());
                List<State> andF2States = sat(info.getRightExpression());
                for (State state : andF1States) {
                    if (andF2States.contains(state))
                        states.add(state);

                }
                break;
            case Or:
                // SAT (φ1) ∪ SAT (φ2)
                List<State> orF1States = sat(info.getLeftExpression());
                List<State> orF2States = sat(info.getRightExpression());
                states = orF1States;
                for (State state : orF2States) {
                    if (!states.contains(state))
                        states.add(state);

                }
                break;
            case Implies:
                // SAT (¬φ1 ∨ φ2)
                // TODO: reevaluate impliesFormula
                String impliesFormula = "!" + info.getLeftExpression() + "|" + info.getRightExpression();
                states = sat(impliesFormula);
                break;
            case AX:
                // SAT (¬EX¬φ1)
                // TODO: reevaluate axFormula
                String axFormula = "!" + "EX" + "!" + info.getLeftExpression();
                states = sat(axFormula);
                // check if states actually has link to next state
                List<State> tempStates = new ArrayList<State>();
                for (State sourceState : states) {
                    for (Transition transition : this.kripke.getTransitions()) {
                        if (sourceState.equals(transition.getFromState())) {
                            tempStates.add(sourceState);
                            break;
                        }

                    }
                }
                states = tempStates;
                break;
            case EX:
                // SATEX(φ1)
                // TODO: reevaluate exFormula
                String exFormula = info.getLeftExpression();
                states = sAT_EX(exFormula);
                break;
            case AU:
                // A[φ1 U φ2]
                // SAT(¬(E[¬φ2 U (¬φ1 ∧¬φ2)] ∨ EG¬φ2))
                // TODO: reevaluate auFormulaBuilder
                StringBuilder auFormulaBuilder = new StringBuilder();
                auFormulaBuilder.append("!(E(!");
                auFormulaBuilder.append(info.getRightExpression());
                auFormulaBuilder.append("U(!");
                auFormulaBuilder.append(info.getLeftExpression());
                auFormulaBuilder.append("&!");
                auFormulaBuilder.append(info.getRightExpression());
                auFormulaBuilder.append("))|(EG!");
                auFormulaBuilder.append(info.getRightExpression());
                auFormulaBuilder.append("))");
                states = sat(auFormulaBuilder.toString());
                break;
            case EU:
                // SATEU(φ1, φ2)
                // TODO: reevaluate euFormula
                states = sAT_EU(info.getLeftExpression(), info.getRightExpression());
                break;
            case EF:
                // SAT (E( U φ1))
                // TODO: reevaluate efFormula
                String efFormula = "E(TU" + info.getLeftExpression() + ")";
                states = sat(efFormula);
                break;
            case EG:
                // SAT(¬AF¬φ1)
                // TODO: reevaulate egFormula
                String egFormula = "!AF!".concat(info.getLeftExpression());
                states = sat(egFormula);
                break;
            case AF:
                // SATAF (φ1)
                // TODO: reevaluate afFormula
                String afFormula = info.getLeftExpression();
                states = sAT_AF(afFormula);
                break;
            case AG:
                // SAT (¬EF¬φ1)
                // TODO: reevaluate agFormula
                String agFormula = "!EF!".concat(info.getLeftExpression());
                states = sat(agFormula);
                break;
            case Unknown:
                throw new Exception("Invalid CTL expression");

        }
        return states;
    }

    /*
    Handling EX
     */
    private List<State> sAT_EX(String expression) throws Exception {
        // X := SAT (φ);
        // Y := pre∃(X);
        // return Y
        List<State> x = new ArrayList<State>();
        List<State> y = new ArrayList<State>();
        x = sat(expression);
        y = preE(x);
        return y;
    }

    /*
    Handling EU
     */
    private List<State> sAT_EU(String leftExpression, String rightExpression) throws Exception {
        List<State> w = new ArrayList<State>();
        List<State> x = new ArrayList<State>();
        List<State> y = new ArrayList<State>();
        w = sat(leftExpression);
        x.addAll(this.kripke.getStates());
        y = sat(rightExpression);
        while (!areListStatesEqual(x, y)) {
            x = y;
            List<State> newY = new ArrayList<State>();
            List<State> preEStates = preE(y);
            newY.addAll(y);
            List<State> wAndPreE = new ArrayList<State>();
            for (State state : w) {
                if (preEStates.contains(state))
                    wAndPreE.add(state);

            }
            for (State state : wAndPreE) {
                if (!newY.contains(state))
                    newY.add(state);

            }
            y = newY;
        }
        return y;
    }

    /*
    Handling AF
     */
    private List<State> sAT_AF(String expression) throws Exception {
        List<State> x = new ArrayList<State>();
        x.addAll(kripke.getStates());
        List<State> y = new ArrayList<State>();
        y = sat(expression);
        while (!areListStatesEqual(x, y)) {
            x = y;
            List<State> newY = new ArrayList<State>();
            List<State> preAStates = preA(y);
            newY.addAll(y);
            for (State state : preAStates) {
                if (!newY.contains(state))
                    newY.add(state);

            }
            y = newY;
        }
        return y;
    }

    /*
    PreE
     */
    private List<State> preE(List<State> y) throws Exception {
        // {s ∈ S | exists s, (s → s and s ∈ Y )}
        List<State> states = new ArrayList<State>();
        List<Transition> transitions = new ArrayList<Transition>();
        for (State sourceState : this.kripke.getStates()) {
            for (State destState : y) {
                Transition myTransition = new Transition(sourceState, destState);
                if (this.kripke.getTransitions().contains(myTransition)) {
                    if (!states.contains(sourceState))
                        states.add(sourceState);

                }

            }
        }
        return states;
    }

    /*
    PreA
     */
    private List<State> preA(List<State> y) throws Exception {
        // pre∀(Y ) = pre∃y − pre∃(S − Y)
        List<State> PreEY = preE(y);
        List<State> S_Minus_Y = new ArrayList<State>();
        S_Minus_Y.addAll(this.kripke.getStates());
        for (State state : y) {
            S_Minus_Y.remove(state);

        }
        List<State> PreE_S_Minus_Y = preE(S_Minus_Y);
        for (State state : PreE_S_Minus_Y) {
            // PreEY - PreE(S-Y)
            PreEY.remove(state);

        }
        return PreEY;
    }

    /*
    Determine Whether the list contain same set of states
     */
    private boolean areListStatesEqual(List<State> list1, List<State> list2) throws Exception {
        if (list1.size() != list2.size())
            return false;

        for (State state : list1) {
            if (!list2.contains(state))
                return false;

        }
        return true;
    }

    /*
    Determine whether this is an atom
     */
    private boolean isAtomic(String expression) throws Exception {
        return this.kripke.getAtoms().contains(expression);
    }

    /*
    Determine whether given expression contains binary operation for the next checking
     */
    private BinarySymbolInfo isBinaryOp(String expression, String symbol) throws Exception {
        boolean isBinaryOp = false;
        String leftExpression = null;
        String rightExpression = null;
        if (expression.contains(symbol)) {
            Integer openParanthesisCount = 0;
            Integer closeParanthesisCount = 0;
            for (Integer i = 0; i < expression.length(); i++) {
                String currentChar = expression.substring(i, (i) + (1));
                if (currentChar.equals(symbol) && openParanthesisCount == closeParanthesisCount) {
                    leftExpression = expression.substring(0, (0) + (i));
                    rightExpression = (expression.substring(i + 1, (i + 1) + (expression.length() - i - 1)));
                    isBinaryOp = true;
                    break;
                } else if (currentChar.equals("(")) {
                    openParanthesisCount++;
                } else if (currentChar.equals(")")) {
                    closeParanthesisCount++;
                }

            }
        }
        if (isBinaryOp)
            return new BinarySymbolInfo(isBinaryOp, leftExpression, rightExpression);
        return null;
    }

    /*
    Removing extra brackets
     */
    private String removeExtraBrackets(String expression) throws Exception {
        String newExpression = expression;
        int openParanthesis = 0;
        int closeParanthesis = 0;
        if (expression.startsWith("(") && expression.endsWith(")")) {
            for (int i = 0; i < expression.length() - 1; i++) {
                String charExpression = expression.substring(i, (i) + (1));
                if (charExpression.equals("("))
                    openParanthesis++;
                else if (charExpression.equals(")"))
                    closeParanthesis++;

            }
            if (openParanthesis - 1 == closeParanthesis)
                newExpression = expression.substring(1, (expression.length() - 1));

        }

        return newExpression;
    }

    public enum TypeSAT {
        Unknown, AllTrue, AllFalse, Atomic, Not, And, Or, Implies, AX, EX, AU, EU, EF, EG, AF, AG
    }
}

