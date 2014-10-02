/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import java.util.List;
import urbosenti.adaptation.AdaptationManager;

/**
 *
 * @author Guilherme
 */
public class EventManager implements AsynchronouslyManageableComponent {

    private boolean enableSystemHandler;
    private AdaptationManager systemHandler;
    private List<ApplicationHandler> applicationHandlers;

    public EventManager(AdaptationManager systemHandler) {
        this();
        this.systemHandler = systemHandler;
        enableSystemHandler = true;
    }

    public EventManager() {
        systemHandler = null;
        enableSystemHandler = false;
    }

    public void newEvent(Event event) {
        /* if is a application event foward to the Application Handler*/
        switch (event.getOriginType()) {
            case Event.APPLICATION_EVENT:
                for (ApplicationHandler ah : applicationHandlers) {
                    ah.newEvent(event);
                }
                break;
            /* if is a system event foward to the SystemHandler*/
            case Event.SYSTEM_EVENT:
                if (systemHandler != null && enableSystemHandler) { /* if the SystemHandler is inactive the message is sent to the Application Handler*/
                    systemHandler.newEvent(event);
                } else {
                    for (ApplicationHandler ah : applicationHandlers) {
                        ah.newEvent(event);
                    }
                }
                break;
        }
    }

    public void setSystemHandler(AdaptationManager systemHandler) {
        this.systemHandler = systemHandler;
    }
        
    public void enableSystemHandler() {
        this.enableSystemHandler = true;
    }

    public void disableSystemHandler() {
        this.enableSystemHandler = true;
    }

    public void subscribe(ApplicationHandler applicationHandler) {
        applicationHandlers.add(applicationHandler);
    }

    public void unsubscribe(ApplicationHandler applicationHandler) {
        applicationHandlers.remove(applicationHandler);
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
