package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private int id;
    private String description;
    private EntityType EntityType;
    private List<Instace> instaces;
    private List<State> states;
    private List<EventModel> events;
    private List<ActionModel> actions;

    public Entity(int id, String description, EntityType objectType, List<Instace> instace, List<State> state, List<EventModel> event, List<ActionModel> action) {
        this.id = id;
        this.description = description;
        this.EntityType = objectType;
        this.instaces = instace;
        this.states = state;
        this.events = event;
        this.actions = action;
    }
    
    public Entity(String description, EntityType objectType, List<Instace> instace, List<State> state, List<EventModel> event, List<ActionModel> action) {
        this.description = description;
        this.EntityType = objectType;
        this.instaces = instace;
        this.states = state;
        this.events = event;
        this.actions = action;
    }
    
    public Entity(String description) {
        this.description = description;
        this.actions = new ArrayList();
        this.events = new ArrayList();
        this.instaces = new ArrayList();
        this.states = new ArrayList();
    }
    
    public Entity() {
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

    public EntityType getEntityType() {
        return EntityType;
    }

    public void setEntityType(EntityType objectType) {
        this.EntityType = objectType;
    }

    public List<Instace> getInstaces() {
        return instaces;
    }

    public void setInstaces(List<Instace> instace) {
        this.instaces = instace;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<EventModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventModel> events) {
        this.events = events;
    }

    public List<ActionModel> getActions() {
        return actions;
    }

    public void setActions(List<ActionModel> actions) {
        this.actions = actions;
    }

}
