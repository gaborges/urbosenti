package urbosenti.core.device.model;

public class TargetOrigin {

    private int id;
    private String description;
    private boolean optional;

    public TargetOrigin() {
    }
    
    public TargetOrigin(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public TargetOrigin(int id, String description, boolean optional) {
        this.id = id;
        this.description = description;
        this.optional = optional;
    }
    
    public TargetOrigin(String description, boolean optional) {
        this.description = description;
        this.optional = optional;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

}
