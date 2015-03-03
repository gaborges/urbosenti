package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class Interaction {

    private int id;
    private String description;
    private List<Parameter> parameters;
    private InteractionType interactionType;
    private Direction direction;
    private Interaction primaryInteraction;
    private CommunicativeAct communicativeAct;

    public Interaction(int id, String description) {
        this();
        this.id = id;
        this.description = description;
    }

    public Interaction() {
        this.parameters = new ArrayList();
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

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public CommunicativeAct getCommunicativeAct() {
        return communicativeAct;
    }

    public void setCommunicativeAct(CommunicativeAct communicativeAct) {
        this.communicativeAct = communicativeAct;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Interaction getPrimaryInteraction() {
        return primaryInteraction;
    }

    public void setPrimaryInteraction(Interaction primaryInteraction) {
        this.primaryInteraction = primaryInteraction;
    }

}
