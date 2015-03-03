package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class AgentType {

    private int id;
    private String description;
    private List<Interaction> interaction;
    private List<State> states;

    public AgentType(int id, String description) {
        this.id = id;
        this.description = description;
        this.interaction = new ArrayList();
        this.states = new ArrayList();
    }

    public AgentType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Interaction> getInteraction() {
        return interaction;
    }

    public void setInteraction(List<Interaction> interaction) {
        this.interaction = interaction;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

}
