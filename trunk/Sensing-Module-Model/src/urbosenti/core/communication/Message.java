/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.util.Date;
import urbosenti.core.device.Agent;

/**
 *
 * @author Guilherme
 */
public class Message {
    
    public static final int NORMAL_PRIORITY = 0;
    public static final int PREFERENTIAL_PRIORITY = 1;  
    
    private Agent sender;
    private Agent target;

    private String subject;
    private String contentType;
    
    private int priority;
    
    private Boolean anonymousUpload;
    
    private String content;
    
    private Date createdTime;
    
    private Boolean usesUrboSentiXMLEnvelope;
   
    public Message() {
        this.priority = Message.NORMAL_PRIORITY;
        this.usesUrboSentiXMLEnvelope = true;
        this.createdTime = new Date();
        this.anonymousUpload = false;
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

    public boolean isUsesUrboSentiXMLEnvelope() {
        return usesUrboSentiXMLEnvelope;
    }

    public void setUsesUrboSentiXMLEnvelope(boolean usesUrboSentiXMLEnvelope) {
        this.usesUrboSentiXMLEnvelope = usesUrboSentiXMLEnvelope;
    }        
    
    @Override
    public String toString() {
        return "Message{" +", sender=" + sender.toString() + ", target=" + target.toString() + ", subject=" + subject + ", contentType=" + contentType + ", content=" + content + '}';
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Boolean isAnonymousUpload() {
        return anonymousUpload;
    }

    public void setAnonymousUpload(Boolean anonymousUpload) {
        this.anonymousUpload = anonymousUpload;
    }    
}
