package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class AgentMessage {

    private int id;
    private String description;
    private List<Content> contents;

    public AgentMessage(int id, String description, Interaction interaction) {
        this.id = id;
        this.description = description;
    }

    public AgentMessage() {
        this.contents = new ArrayList();
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

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

}
