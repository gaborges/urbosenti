package urbosenti.core.device.model;

import java.util.Date;

public class Content {

    private int id;
    private java.lang.Object value;
    private Date time;
    private double score;
    private Instance  monitoredInstance;
    private AgentMessage message;

    public Content(int id, java.lang.Object value, Date time, double score) {
        this.id = id;
        this.value = value;
        this.time = time;
        this.score = score;
    }

    public Content() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public java.lang.Object getValue() {
        return value;
    }

    public void setValue(java.lang.Object value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Instance getMonitoredInstance() {
        return monitoredInstance;
    }

    public void setMonitoredInstance(Instance monitoredInstance) {
        this.monitoredInstance = monitoredInstance;
    }

    public AgentMessage getMessage() {
        return message;
    }

    public void setMessage(AgentMessage message) {
        this.message = message;
    }
    
}
