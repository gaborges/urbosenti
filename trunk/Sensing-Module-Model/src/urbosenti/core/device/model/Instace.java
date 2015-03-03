package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class Instace {

    private int id;
    private String description;
    private String representativeClass;
    private List<State> states;

    public Instace(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public Instace(int id, String description, String representativeClass) {
        this.id = id;
        this.description = description;
        this.representativeClass = representativeClass;
        this.states = new ArrayList();
    }
    
    public Instace() {
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

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public String getRepresentativeClass() {
        return representativeClass;
    }

    public void setRepresentativeClass(String representativeClass) {
        this.representativeClass = representativeClass;
    }
}
