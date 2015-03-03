package urbosenti.core.device.model;

import java.util.ArrayList;
import java.util.List;

public class ActionModel {

    private int id;
    private String description;
    private boolean hasFeedback;
    private TargetOrigin origin;
    private List<Parameter> parameters;
    private List<FeedbackAnswer> feedbackAnswers;

    public ActionModel() {
        this.description = "unknown";
        this.hasFeedback = false;
        this.origin = null;
        this.parameters = new ArrayList();
        this.feedbackAnswers = new ArrayList();
    }

    public ActionModel(int id, String description) {
        this.id = id;
        this.description = description;
        this.hasFeedback = false;
        this.origin = null;
        this.parameters = new ArrayList();
        this.feedbackAnswers = new ArrayList();
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

    public boolean isHasFeedback() {
        return hasFeedback;
    }

    public void setHasFeedback(boolean hasFeedback) {
        this.hasFeedback = hasFeedback;
    }

    public TargetOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(TargetOrigin origin) {
        this.origin = origin;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<FeedbackAnswer> getFeedbackAnswers() {
        return feedbackAnswers;
    }

    public void setFeedbackAnswers(List<FeedbackAnswer> feedbackAnswers) {
        this.feedbackAnswers = feedbackAnswers;
    }
    
}
