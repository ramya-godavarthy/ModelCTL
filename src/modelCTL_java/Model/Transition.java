package modelCTL_java.Model;

public class Transition {

    private String transitionName;
    private State fromState;
    private State toState;

    public Transition() throws Exception {
    }

    public Transition(State fromState, State toState) throws Exception {
        setTransitionName("");
        setFromState(fromState);
        setToState(toState);
    }

    public Transition(String transitionName, State fromState, State toState) throws Exception {
        setTransitionName(transitionName);
        setFromState(fromState);
        setToState(toState);
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String value) {
        transitionName = value;
    }

    public State getFromState() {
        return fromState;
    }

    public void setFromState(State value) {
        fromState = value;
    }

    public State getToState() {
        return toState;
    }

    public void setToState(State value) {
        toState = value;
    }

    @Override

    public boolean equals(Object obj) {
        Transition input = (Transition) obj;
        return input.getFromState().equals(this.fromState) && input.getToState().equals(this.toState);
    }
}
