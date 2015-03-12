package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgentMessage {

    private int id;
    private String description;
    private Interaction previousInteraction;
    private List<Content> contents;
    private Date time;

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    /**
     * Se for a primeira então retorna nulo.
     * @return 
     */
    public Interaction getPreviousInteraction() {
        return previousInteraction;
    }

    public void setPreviousInteraction(Interaction previousInteraction) {
        this.previousInteraction = previousInteraction;
    }

}
