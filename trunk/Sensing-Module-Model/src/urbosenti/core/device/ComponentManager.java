/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import java.util.Date;
import urbosenti.core.events.Event;
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
    private DeviceManager deviceManager;

    protected ComponentManager() {
    }
        
    // Method used by the DeviceManager
    public ComponentManager(EventManager eventManager){
        this.eventManager = eventManager;
    }
     
    // Method used by other Components
    public ComponentManager(DeviceManager deviceManager){
        this.deviceManager = deviceManager;
        this.eventManager = this.deviceManager.getEventManager();
    }
    
    public abstract void onCreate();

    public boolean isEventExpired(Event event){
        if (event.isHasTimeout()){
            if((new Date().getTime() - event.getTime().getTime()) > event.getTimeout()) return true;
        }
        return false;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
    
}
