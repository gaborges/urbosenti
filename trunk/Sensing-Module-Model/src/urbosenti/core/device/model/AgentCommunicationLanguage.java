package urbosenti.core.device.model;

public class AgentCommunicationLanguage {

    private int id;
    private String description;

    public AgentCommunicationLanguage(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public AgentCommunicationLanguage() {
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

}
