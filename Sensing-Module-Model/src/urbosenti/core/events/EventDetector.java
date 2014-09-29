/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

/**
 *
 * @author Guilherme
 */
public interface EventDetector {
    public Event trigger(Object ... params);
}
