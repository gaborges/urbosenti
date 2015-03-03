package urbosenti.core.device.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class EventModel {

    private int id;
    private String description;
    private boolean synchronous;
    private List<TargetOrigin> targets;
    private Implementation implementation;
    private List<Parameter> parameters;

    public EventModel(int id, String description, Implementation implementation) {
        this();
        this.id = id;
        this.description = description;
        this.synchronous = false;
        this.implementation = implementation;
    }

    public EventModel() {
        this.parameters = new ArrayList();
        this.targets = new ArrayList();
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

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

      public Implementation getImplementation() {
        return implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public List<TargetOrigin> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetOrigin> targets) {
        this.targets = targets;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    
}
