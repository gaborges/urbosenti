package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class State {

    private int id;
    private boolean userCanChange;
    private boolean stateInstance;
    private String description;
    private java.lang.Object superiorLimit;
    private java.lang.Object inferiorLimit;
    private java.lang.Object initialValue;
    private List<PossibleContent> possibleContents;
    private DataType dataType;
    private Content content;

    public State(int id, DataType dataType) {
        this.id = id;
        this.userCanChange = false;
        this.stateInstance = false;
        this.description = "";
        this.dataType = dataType;
        this.superiorLimit = null;
        this.inferiorLimit = null;
        this.initialValue = dataType.getInitialValue();
        this.possibleContents = new ArrayList();
    }

    public State() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUserCanChange() {
        return userCanChange;
    }

    public void setUserCanChange(boolean userCanChange) {
        this.userCanChange = userCanChange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.lang.Object getSuperiorLimit() {
        return superiorLimit;
    }

    public void setSuperiorLimit(Object superiorLimit) {
        this.superiorLimit = superiorLimit;
    }

    public java.lang.Object getInferiorLimit() {
        return inferiorLimit;
    }

    public void setInferiorLimit(Object inferiorLimit) {
        this.inferiorLimit = inferiorLimit;
    }

    public java.lang.Object getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Object defaultValue) {
        this.initialValue = defaultValue;
    }

    public List<PossibleContent> getPossibleContents() {
        return possibleContents;
    }

    public void setPossibleContent(List<PossibleContent> possibleContents) {
        this.possibleContents = possibleContents;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isStateInstance() {
        return stateInstance;
    }

    public void setStateInstance(boolean stateInstance) {
        this.stateInstance = stateInstance;
    }

}
