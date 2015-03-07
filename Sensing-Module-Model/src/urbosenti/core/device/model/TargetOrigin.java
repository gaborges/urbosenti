package urbosenti.core.device.model;

public class TargetOrigin {

    private int id;
    private String description;

    public TargetOrigin() {
    }

    public TargetOrigin(int id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public TargetOrigin(String description) {
        this.description = description;
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
