/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import java.util.Date;
import java.util.List;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventDetector;
import urbosenti.core.events.EventManager;

/**
 *
 * @author Guilherme
 */
public abstract class ComponentManager {
    /**
     *
     */
    private EventManager eventManager;
    
    public ComponentManager(EventManager eventManager){
        this.eventManager = eventManager;
    }
     
    protected abstract void onCreate();

    public abstract void applyAction(Action action);
    public boolean isEventExpired(Event event){
        if (event.isHasTimeout()){
            if((new Date().getTime() - event.getTime().getTime()) > event.getTimeout()) return true;
        }
        return false;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
   
}
