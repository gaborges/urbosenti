/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

/**
 *
 * @author Guilherme
 */
public abstract class ApplicationHandler {
    /**
     *
     * @param This method receives a event from the Event Manager to process the event applying changes directly in the component.
     */
    public abstract void newEvent(Event event);
    
}
