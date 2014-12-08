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
public abstract class PushServiceReceiver implements Runnable{
    
    public final CommunicationManager communicationManager;

    public PushServiceReceiver(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
    }
        
    @Override
    public abstract void run();
    
    public void start() {
        // Create a Service to receive Push Messages in text format
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() {
        // stop the service
    }
    
    
}
