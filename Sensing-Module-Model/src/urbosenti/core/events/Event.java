/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import java.util.Date;

/**
 *
 * @author Guilherme
 */
public abstract class Event {
    /*
     * Types of event:
     * * INTERNAL_COMPONENT_EVENT is used in events of an internal component of the  sensing module
     * * EXTERAL_COMPONENT_EVENT is used in custom events built in the sensing application components that use the sensing module API
     * * INTERATION_EVENT is used in social interactions with other agents
     */

    public static final int INTERNAL_COMPONENT_EVENT = 0;
    public static final int EXTERAL_COMPONENT_EVENT = 1;
    public static final int INTERATION_EVENT = 2;

    /*
     * Types of origin:
     * * SYSTEM_EVENT  is a event to be handled by the system automaticaly, if the adaptation component was activated
     * * APPLICATION_EVENT is a event to be handled by the sensing application
     */
    public static final int SYSTEM_EVENT = 0;
    public static final int APPLICATION_EVENT = 1;

    private int id;
    private String name;
    private boolean synchronous;
    private int eventType;
    private int originType;
    private Object origin; // for internal events is a ComponentManager otherwise is a Agent
    private Object value;  // it is a value used to handle the event, if necessary.

    private boolean hasTimeout;
    private int timeout; // in ms
    private Date time;

    public Event(Object origin, int originType) {
        this.synchronous = false;
        this.eventType = Event.INTERNAL_COMPONENT_EVENT;
        this.hasTimeout = false;
        this.time = new Date();
        this.timeout = 1000;
        this.origin = origin;
        this.originType = originType;
    }

    public void setSynchronousSettings(int timeout, boolean hasTimeout) {
        this.timeout = timeout;
        this.hasTimeout = hasTimeout;
        this.synchronous = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    /**
     * @return Types of event:
     * <ul>
     * <li> Event.INTERNAL_COMPONENT_EVENT is used in events of an internal
     * component of the sensing module</li>
     * <li> Event.EXTERAL_COMPONENT_EVENT is used in custom events built in the
     * sensing application components that use the sensing module API</li>
     * <li> Event.INTERATION_EVENT is used in social interactions with other
     * agents </li>
     * </ul>
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * @param Types of origin:
     * <ul>
     * <li> Event.INTERNAL_COMPONENT_EVENT is used in events of an internal
     * component of the sensing module</li>
     * <li> Event.EXTERAL_COMPONENT_EVENT is used in custom events built in the
     * sensing application components that use the sensing module API</li>
     * <li> Event.INTERATION_EVENT is used in social interactions with other
     * agents </li>
     * </ul>
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    /**
     * @return Types of origin:
     * <ul>
     * <li> Event.INTERNAL_COMPONENT_EVENT is used in events of an internal
     * component of the sensing module</li>
     * <li> Event.EXTERAL_COMPONENT_EVENT is used in custom events built in the
     * sensing application components that use the sensing module API</li>
     * <li> Event.INTERATION_EVENT is used in social interactions with other
     * agents </li>
     * </ul>
     */
    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isHasTimeout() {
        return hasTimeout;
    }

    public void setHasTimeout(boolean hasTimeout) {
        this.hasTimeout = hasTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @param Types of origin:
     * <ul>
     * <li>Event.SYSTEM_EVENT is a event to be handled by the system
     * automaticaly, if the adaptation component was activated</li>
     * <li>Event.APPLICATION_EVENT is a event to be handled by the sensing
     * application</li>
     * </ul>
     */
    public void setOriginType(int originType) {
        this.originType = originType;
    }

    /**
     * @return Types of origin:
     * <ul>
     * <li>Event.SYSTEM_EVENT is a event to be handled by the system
     * automaticaly, if the adaptation component was activated</li>
     * <li>Event.APPLICATION_EVENT is a event to be handled by the sensing
     * application</li>
     * </ul>
     */
    public int getOriginType() {
        return originType;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", name=" + name + ", synchronous=" + synchronous + ", eventType=" + eventType + ", originType=" + originType + ", origin=" + origin + ", value=" + value + ", hasTimeout=" + hasTimeout + ", timeout=" + timeout + ", time=" + time + '}';
    }

    /**
     * Verifica se o evento foi expirado, caso seja uma ação síncrona que
     * demorou mais que o tempo de espera para retornar a ação.
     *
     * @param event
     * @return
     */
    public static boolean isEventExpired(Event event) {
        if (event.isHasTimeout()) {
            if ((new Date().getTime() - event.getTime().getTime()) > event.getTimeout()) {
                return true;
            }
        }
        return false;
    }

}
