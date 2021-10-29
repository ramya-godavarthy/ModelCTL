package modelCTL_java.Model;

import java.util.ArrayList;
import java.util.List;

public class KripkeStructure {

    private List<Transition> transistions = new ArrayList<Transition>();
    private List<State> states = new ArrayList<State>();
    private List<String> atoms = new ArrayList<String>();

    /*
    Constructor
     */
    public KripkeStructure() throws Exception {
        setTransistions(new ArrayList<Transition>());
        setStates(new ArrayList<State>());
        setAtoms(new ArrayList<String>());
    }

    public KripkeStructure(String kripkeStructureDefinition) throws Exception {
        String[] parsedStructure = kripkeStructureDefinition.replace(System.getProperty("line.separator"), "").replace("\n", "").split(";");
        if (parsedStructure == null || parsedStructure.length != 3)
            throw new Exception("Input file does not contain appropriate segments to construct kripke structure");

        String[] stateNames = parsedStructure[0].replace(" ", "").split(",");
        String[] transitions = parsedStructure[1].replace(" ", "").split(",");
        String[] stateAtomStructures = parsedStructure[2].split(",");

        /*
        Load States
         */

        for (String stateName : stateNames) {
            State state = new State(stateName.trim());
            if (getStates().contains(state))
                throw new Exception(String.format("State {0}  is defined more than once", stateName));
            getStates().add(state);
        }

        /*
        Load Transitions
         */

        for (String transition : transitions) {
            String[] parsedTransition = transition.split(":");
            if (parsedTransition == null || parsedTransition.length != 2)
                throw new Exception("Transition is not in the valid format");

            String transitionName = parsedTransition[0];
            String[] parsedFromToStates = parsedTransition[1].split("-");
            if (parsedFromToStates == null || parsedFromToStates.length != 2)
                throw new Exception(String.format("Transition " + transitionName + " is not in [from state] - [to state] format", transitionName));

            String fromStateName = parsedFromToStates[0];
            String toStateName = parsedFromToStates[1];
            State fromState = findStateByName(fromStateName);
            State toState = findStateByName(toStateName);
            if (fromState == null || toState == null)
                throw new Exception(String.format("Invalid state is detected in transition {0}", transitionName));

            Transition transitionObj = new Transition(transitionName, fromState, toState);
            if (!getTransitions().contains(transitionObj))
                getTransitions().add(transitionObj);
            else {
                throw new Exception(String.format("Transitions from state to state {1} are defined more than once", fromStateName, toStateName));
            }
        }

        /*
        Load Atoms
         */

        for (String stateAtomStructure : stateAtomStructures) {
            String[] parsedStateAtomStructure = stateAtomStructure.split(":");
            if (parsedStateAtomStructure == null || parsedStateAtomStructure.length != 2)
                throw new Exception(String.format("{0} is not a valid state: atoms definition", stateAtomStructure));

            String stateName = parsedStateAtomStructure[0].replace(" ", "");
            String atomNames = parsedStateAtomStructure[1].trim();
            String[] parsedAtoms = atomNames.split(" ");
            List<String> stateAtoms = new ArrayList<String>();
            for (String atom : parsedAtoms) {
                if (atom == null || (atom != null && atom.isEmpty())) {
                } else if (atom != null && !atom.isEmpty())
                    stateAtoms.add(atom);
                else
                    throw new Exception(String.format("Atom {0} is defined more than once for state {1}", atom, stateName));
            }
            State stateObj = findStateByName(stateName);
            if (stateObj == null)
                throw new Exception(String.format("State {0} is not defined", stateName));
            stateObj.setAtoms(stateAtoms);

            //Load to list of atoms
            for (String atom : stateAtoms) {
                if (!getAtoms().contains(atom))
                    getAtoms().add(atom);
            }
        }

    }

    public List<Transition> getTransitions() {
        return transistions;
    }

    public void setTransistions(List<Transition> transistions) {
        this.transistions = transistions;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<String> getAtoms() {
        return atoms;
    }

    /*
    Overloaded Constructor
     */

    public void setAtoms(List<String> atoms) {
        this.atoms = atoms;
    }

    /*
    Find the sate by it's name
     */

    public State findStateByName(String stateName) throws Exception {
        for (State state : getStates()) {
            if (state.getStateName().equals(stateName))
                return state;
        }
        return null;
    }

    /*
    Override toString method
     */

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(("STATES") + System.getProperty("line.separator"));
            sb.append(("-----------") + System.getProperty("line.separator"));
            sb.append(statesToString());
            sb.append(("TRANSITIONS") + System.getProperty("line.separator"));
            sb.append(("-------------------") + System.getProperty("line.separator"));
            sb.append(transitionsToString());
            return sb.toString();
        } catch (RuntimeException sbe) {
            throw sbe;
        } catch (Exception sbe) {
            throw new RuntimeException(sbe);
        }
    }

    public String statesToString() throws Exception {
        StringBuilder sb = new StringBuilder();
        List<String> stateStrings = new ArrayList<String>();
        for (State state : getStates()) {
            String atomNames = String.join(", ", state.getAtoms());
            stateStrings.add(String.format("{0}({1})", state.getStateName(), atomNames));
        }
        sb.append(String.join(", ", stateStrings));
        return sb.toString();
    }

    public String transitionsToString() throws Exception {
        StringBuilder sb = new StringBuilder();
        List<String> transitionString = new ArrayList<String>();
        for (Transition transition : getTransitions()) {
            transitionString.add(String.format("{0}({1} -> {2})", transition.getTransitionName(), transition.getFromState().getStateName(),
                    transition.getToState().getStateName()));
        }
        sb.append(String.join(", ", transitionString));
        return sb.toString();
    }
}
