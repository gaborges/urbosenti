/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import urbosenti.core.device.Agent;

/**
 *
 * @author Guilherme
 */
public class Message {
    
    public static final int NORMAL_PRIORITY = 0;
    public static final int PREFERENTIAL_PRIORITY = 1;
    
    private MessageHeader header;
    
    private Agent sender;
    private Agent target;

    private String subject;
    private String contentType;
    
    private int priority;
    
    private String content;

    // Critérios utilizados para avaliação ao enviar a mensagem
    private double size;
    private double responseTime;
    
    private CommunicationInterface usedCommunicationInterface;
    
    public Message() {
        this.priority = Message.NORMAL_PRIORITY;
    }   
    
    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Agent getSender() {
        return sender;
    }

    public void setSender(Agent sender) {
        this.sender = sender;
    }

    public Agent getTarget() {
        return target;
    }

    public void setTarget(Agent target) {
        this.target = target;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getPriority() {
        return priority;
    }

    public void setNormalPriority() {
        this.priority = Message.NORMAL_PRIORITY;
    }
    
    public void setPreferentialPriority() {
        this.priority = Message.PREFERENTIAL_PRIORITY;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public CommunicationInterface getUsedCommunicationInterface() {
        return usedCommunicationInterface;
    }

    public void setUsedCommunicationInterface(CommunicationInterface usedCommunicationInterface) {
        this.usedCommunicationInterface = usedCommunicationInterface;
    }
        
    @Override
    public String toString() {
        return "Message{" + "header=" + header + ", sender=" + sender.toString() + ", target=" + target.toString() + ", subject=" + subject + ", contentType=" + contentType + ", content=" + content + '}';
    }

    
}