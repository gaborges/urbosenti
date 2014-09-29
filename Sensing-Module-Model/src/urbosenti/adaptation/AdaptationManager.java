/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.context.ContextManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Event;
import urbosenti.user.UserManager;

/**
 *
 * @author Guilherme
 */
public class AdaptationManager implements Runnable {

    /**
     *
     * @param This method receives a event from the Event Manager to process the
     * event applying changes directly in the component.
     */
    //private LocalKnowledge localKnowledge
    private Queue<Event> availableEvents;
    private DeviceManager deviceManager;
    private ContextManager contextManager;
    private UserManager userManager;
    private boolean running;
    private Boolean flag;

    public AdaptationManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
        this.contextManager = null;
        this.userManager = null;
        this.running = true;
        this.availableEvents = new LinkedList<Event>();
    }

    public AdaptationManager(DeviceManager deviceManager, ContextManager contextManager, UserManager userManager) {
        this(deviceManager);
        this.contextManager = contextManager;
        this.userManager = userManager;
    }

    protected void discovery(DeviceManager d, ContextManager c, UserManager u) {
    }

    public synchronized void newEvent(Event event) {
        /*
         add to queue
         wake up the Adaptation Mechanism
         */
        this.availableEvents.add(event);
        notifyAll();
    }

    public synchronized boolean start() {
        this.running = true;
        return true;
    }

    public synchronized boolean stop() {
        this.running = false;
        return true;
    }

    @Override
    public void run() {
        /* At first, the adaptation manager discovers the environment to rum*/
        this.discovery(deviceManager, contextManager, userManager);
        /* It begin the monitoring process of events */
        try {
            monitoring();
        } catch (InterruptedException ex) {
            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void monitoring() throws InterruptedException {
        while (running) {
            /* Monitoring */
            Event event;
            synchronized (flag) {
                event = availableEvents.poll();
                if (event == null) {
                    wait();
                }
            }

            if (event != null) {
                switch (event.getOriginType()) {
                    case Event.INTERATION_EVENT:
                        //InteractionEvent;
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    case Event.INTERNAL_COMPONENT_EVENT:
                        //ContextEvent
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                }
                /* Execute */
            }
        }
    }
}
