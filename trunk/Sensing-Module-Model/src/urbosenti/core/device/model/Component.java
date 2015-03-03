package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private int id;
    private String description;
    private String referedClass;
    private List<Entity> entities;

    public Component(int id, String description, String referedClass) {
        this.id = id;
        this.description = description;
        this.referedClass = referedClass;
    }
    
    public Component(String description, String referedClass) {
        this.description = description;
        this.referedClass = referedClass;
        this.entities = new ArrayList();
    }

    public Component() {
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

    public String getReferedClass() {
        return referedClass;
    }

    public void setReferedClass(String referedClass) {
        this.referedClass = referedClass;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

}