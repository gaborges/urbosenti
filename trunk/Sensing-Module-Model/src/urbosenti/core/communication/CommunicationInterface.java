/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Guilherme
 */
public abstract class CommunicationInterface<Object> {
    
    public final static int STATUS_DISCONNECTED = 0;
    public final static int STATUS_CONNECTED = 1;
    public final static int STATUS_UNAVAILABLE = 2;
    public final static int STATUS_AVAILABLE = 3;
    // Suported Tecnologies
    // current tecnology
    private int id;
    private String name;
    private double mobileDataUse; // only for Mobile data Interface
    private double averageLatency;
    private double averageThroughput;
    private int status; // connected, disconected, disabled (not able to use)  able (able to use, but not tested)
    private int score;
    private int timeout; // ms
    private boolean usesMobileData;

    public CommunicationInterface() {
        averageLatency = 0;
        averageThroughput = 0;
        mobileDataUse = 0;
        score = 0;
        timeout = 0;
        usesMobileData = false;
    }
    
    public boolean isUsesMobileData() {
        return usesMobileData;
    }

    public void setUsesMobileData(boolean usesMobileData) {
        this.usesMobileData = usesMobileData;
    }
    
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
    
    public void updateEvaluationMetrics(MessageWrapper messageWrapper, Date initialTime, Date finalTime){
        messageWrapper.setSentTime(new Date());
        messageWrapper.setResponseTime(finalTime.getTime() - initialTime.getTime());
        messageWrapper.setSent(true);
        messageWrapper.setUsedCommunicationInterface(this);
        // Adiciona a latência média se naõ for a primeira vez
        if(this.getAverageLatency() > 0){
            this.setAverageLatency((this.getAverageLatency() + messageWrapper.getResponseTime())/2);
        }else{
            this.setAverageLatency(messageWrapper.getResponseTime());
        }  
        // Adiciona a troughput médio se não for a primeira vez
        if(this.getAverageThroughput()> 0){
            this.setAverageThroughput((this.getAverageThroughput() + messageWrapper.getSize())/2);
        }else{
            this.setAverageThroughput(messageWrapper.getSize());
        }  
    }
    
    public void updateEvaluationMetrics(MessageWrapper messageWrapper, long responseTime){
        messageWrapper.setSentTime(new Date());
        messageWrapper.setResponseTime(responseTime);
        messageWrapper.setSent(true);
        messageWrapper.setUsedCommunicationInterface(this);
        // Adiciona a latência média se naõ for a primeira vez
        if(this.getAverageLatency() > 0){
            this.setAverageLatency((this.getAverageLatency() + messageWrapper.getResponseTime())/2);
        }else{
            this.setAverageLatency(messageWrapper.getResponseTime());
        }  
        // Adiciona a troughput médio se não for a primeira vez
        if(this.getAverageThroughput()> 0){
            this.setAverageThroughput((this.getAverageThroughput() + messageWrapper.getSize())/2);
        }else{
            this.setAverageThroughput(messageWrapper.getSize());
        }  
    }
    
     public void updateEvaluationMetrics(MessageWrapper messageWrapper, Date initialTime){
        messageWrapper.setSentTime(new Date());
        messageWrapper.setResponseTime((new Date()).getTime() - initialTime.getTime());
        messageWrapper.setSent(true);
        messageWrapper.setUsedCommunicationInterface(this);
        // Adiciona a latência média se naõ for a primeira vez
        if(this.getAverageLatency() > 0){
            this.setAverageLatency((this.getAverageLatency() + messageWrapper.getResponseTime())/2);
        }else{
            this.setAverageLatency(messageWrapper.getResponseTime());
        }  
        // Adiciona a troughput médio se não for a primeira vez
        if(this.getAverageThroughput()> 0){
            this.setAverageThroughput((this.getAverageThroughput() + messageWrapper.getSize())/2);
        }else{
            this.setAverageThroughput(messageWrapper.getSize());
        }  
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    /**
     * 
     * @return retorna true se foi suportada pelo sistema senão retorna false; OBS.: o Desenvolvedor deve definir é é possível ou não utilizá-la por implementação
     * @throws IOException
     * @throws UnsupportedOperationException 
     */    
    public abstract boolean isAvailable() throws UnsupportedOperationException,IOException;
    public abstract boolean testConnection() throws IOException, UnsupportedOperationException;
    public abstract boolean connect(String address) throws IOException, UnsupportedOperationException;
    public abstract boolean disconnect() throws IOException, UnsupportedOperationException;
    public abstract boolean sendMessage(CommunicationManager communicationManager, MessageWrapper messageWrapper) throws java.net.SocketTimeoutException,IOException;
    public abstract Object sendMessageWithResponse(CommunicationManager communicationManager, MessageWrapper messageWrapper) throws java.net.SocketTimeoutException,IOException;
    public abstract Object receiveMessage() throws java.net.SocketTimeoutException,IOException;
    public abstract Object receiveMessage(int timeout) throws java.net.SocketTimeoutException,IOException;
}
