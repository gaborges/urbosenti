package urbosenti.core.device.model;

import java.util.Date;

public class FeedbackAnswer {

    public static int ACTION_RESULT_WAS_SUCCESSFUL = 1;
    public static int ACTION_RESULT_FAILED = 2;
    public static int ACTION_RESULT_FAILED_TIMEOUT = 3;
    public static int ACTION_DOES_NOT_EXIST = 4;
    private int id;
    private String description;
    private Date time;

    public FeedbackAnswer(int id, String description) {
        this.id = id;
        this.description = description;
        this.time = new Date();
    }
    
    public FeedbackAnswer(int id) {
        this.id = id;
        this.time = new Date();
    }

    public FeedbackAnswer() {
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
}
