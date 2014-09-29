/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import urbosenti.core.device.Agent;
import urbosenti.core.device.ComponentManager;

/**
 *
 * @author Guilherme
 */
public class ApplicationEvent extends Event{

    public ApplicationEvent(Object origin) {
        super(origin, Event.APPLICATION_EVENT);
    }
    
    public ApplicationEvent(Agent origin) {
        super(origin, Event.APPLICATION_EVENT);
    }
    
    public ApplicationEvent(ComponentManager origin) {
        super(origin, Event.APPLICATION_EVENT);
    }
    
}
