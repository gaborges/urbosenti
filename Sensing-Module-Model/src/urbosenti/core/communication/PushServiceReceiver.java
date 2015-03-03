/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.device.model.Agent;

/**
 *
 * @author Guilherme
 */
public abstract class PushServiceReceiver implements Runnable{
    
    public static final int STATUS_LISTENING = 1;
    public static final int STATUS_STOPPED = 0;
    private int status;
    private int id;
    public final CommunicationManager communicationManager;
    private Thread t;
        
    public PushServiceReceiver(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
        this.t = null;
        this.status = STATUS_STOPPED;
        this.communicationManager.addPushServiceReceiver(this);
    }
        
    @Override
    public abstract void run();
    
    public synchronized void start() {
        // Create a Service to receive Push Messages in text format
        if(t==null) {
            t = new Thread(this);
            t.start();
        }
        this.t.notifyAll();
        this.status = STATUS_LISTENING;
    }

    public synchronized void stop() {
        try {
            this.t.wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(PushServiceReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.status = STATUS_STOPPED;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
         
}
