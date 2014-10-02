/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

/**
 *
 * @author Guilherme
 */
public class CommunicationInterface {
    // Suported Tecnologies
    // current tecnology
    private int id;
    private String name;
    private double mobileDataUse; // only for Mobile data Interface
    private double averageLatency;
    private double averageThroughput;
    private int status; // connected, disconected, disabled (not able to use)
    private int timeout; // ms

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMobileDataUse() {
        return mobileDataUse;
    }

    public void setMobileDataUse(double mobileDataUse) {
        this.mobileDataUse = mobileDataUse;
    }

    public double getAverageLatency() {
        return averageLatency;
    }

    public void setAverageLatency(double averageLatency) {
        this.averageLatency = averageLatency;
    }

    public double getAverageThroughput() {
        return averageThroughput;
    }

    public void setAverageThroughput(double averageThroughput) {
        this.averageThroughput = averageThroughput;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
        
}
