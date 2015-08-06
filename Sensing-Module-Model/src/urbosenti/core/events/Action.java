/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import java.util.HashMap;
import urbosenti.user.User;

/**
 *
 * @author Guilherme
 */
public class Action {
    
    private int id;
    private int dataBaseId;
    private String name;
    private int origin;
    private int targetEntityId;
    private int targetComponentId;
    private User user;
    private HashMap<String, Object>  parameters;
    private int actionType;
    private boolean synchronous;

    public Action() {
        this.actionType = Event.COMPONENT_EVENT;
        this.synchronous = false;
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

    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(int targetObjectId) {
        this.targetEntityId = targetObjectId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getTargetComponentId() {
        return targetComponentId;
    }

    public void setTargetComponentId(int targetComponentId) {
        this.targetComponentId = targetComponentId;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public int getDataBaseId() {
        return dataBaseId;
    }

    public void setDataBaseId(int dataBaseId) {
        this.dataBaseId = dataBaseId;
    }
    
}
