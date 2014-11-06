/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilherme
 */
public class ReconnectionService implements Runnable {

    private CommunicationManager communicationManager;
    private List<CommunicationInterface> communicationInterfaces;
    private boolean reconnected;
    /**
     * default 60 segundos Representado em milisegundos então (60 000)
     */
    private int reconnectionTime;
    /**
     * Se 1 = Tenta somente em um método. Default. Se 2 = Tenta em todos os
     * métodos.
     */
    private int methodOfReconnection;

    public ReconnectionService(CommunicationManager cm, List<CommunicationInterface> communicationInterfaces) {
        this.communicationManager = cm;
        this.communicationInterfaces = communicationInterfaces;
        this.reconnectionTime = 60000;
        this.methodOfReconnection = 1;
        this.reconnected = false;
    }

    public synchronized void setReconnectionTime(int reconnectionTime) {
        this.reconnectionTime = reconnectionTime;
    }

    public synchronized int getReconnectionTime() {
        return reconnectionTime;
    }

    public void setReconnectionMethodOneAtTime() {
        this.methodOfReconnection = 1;
    }

    public void setReconnectionMethodAllAtOnce() {
        this.methodOfReconnection = 2;
    }

    @Override
    public void run() {
        try {
            reconectionProcess();
        } catch (InterruptedException ex) {
            Logger.getLogger(ReconnectionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReconnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reconectionProcess() throws InterruptedException, IOException {
        CommunicationInterface current;

        if (this.methodOfReconnection == 1) {
            while (!reconnected) {
                Iterator<CommunicationInterface> iterator = communicationInterfaces.iterator();
                while (iterator.hasNext()) {
                    current = iterator.next();
                    synchronized (this) {
                        wait(reconnectionTime);
                    }
                    if (current.testConnection()) {
                        communicationManager.notifyReconnection(current);
                        reconnected = true;
                    }
                }
            }
        }
        if (this.methodOfReconnection == 2) {
            while (!reconnected) {
                synchronized (this) {
                        wait(reconnectionTime);
                    }
                Iterator<CommunicationInterface> iterator = communicationInterfaces.iterator();
                while (iterator.hasNext()) {
                    current = iterator.next();
                    if (current.testConnection()) {
                        communicationManager.notifyReconnection(current);
                        reconnected = true;
                    } else {
                        communicationManager.notifyReconnectionNotSucceed(current);
                    }
                }
            }
        }
    }
}
