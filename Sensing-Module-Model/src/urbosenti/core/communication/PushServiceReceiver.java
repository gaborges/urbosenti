/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.device.model.Instance;

/**
 *
 * @author Guilherme
 */
public abstract class PushServiceReceiver implements Runnable {

    public static final boolean STATUS_LISTENING = true;
    public static final boolean STATUS_STOPPED = false;
    private boolean status;
    private int id;
    public final CommunicationManager communicationManager;
    private final HashMap<String, String> interfaceConfigurations;
    private Thread t;
    private final Boolean flag;
    private String description;
    private Instance instance;

    public PushServiceReceiver(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
        this.t = null;
        this.status = STATUS_STOPPED;
        this.communicationManager.addPushServiceReceiver(this);
        this.flag = true;
        this.interfaceConfigurations = new HashMap();
    }

    @Override
    public abstract void run();

    public void start() {
        // Create a Service to receive Push Messages in text format
        synchronized (flag) {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
            this.status = STATUS_LISTENING;
        }
    }

    public void stop() {
        try {
            synchronized (flag) {
                this.t.wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(PushServiceReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.status = STATUS_STOPPED;
    }

    public void resume() {
        synchronized (flag) {
            this.t.notifyAll();
            this.status = STATUS_LISTENING;
        }
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public HashMap<String, String> getInterfaceConfigurations() {
        return interfaceConfigurations;
    }

    /**
     * Descobre o endereço e adiciona nas configurações de interface
     * (HashMap<String,String> interfaceConfigurations;)
     * @throws java.io.IOException
     */
    public abstract void addressDiscovery() throws IOException;
}
