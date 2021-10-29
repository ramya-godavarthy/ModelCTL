package modelCTL_java.Model;

import java.util.ArrayList;
import java.util.List;

public class State {

    private List<String> atoms;
    private String stateName;

    public State() throws Exception {
        List<String> list = new ArrayList<String>();
        setAtoms(list);
    }

    public State(String stateName) throws Exception {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName.trim();
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public List<String> getAtoms() {
        return atoms;
    }

    public void setAtoms(List<String> atoms) {
        this.atoms = atoms;
    }

    @Override
    public boolean equals(Object obj) {
        return this.stateName.equals(((State) obj).getStateName());
    }
}
