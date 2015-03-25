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
    private String name;
    private int origin;
    private int targetEntityId;
    private User user;
    private HashMap<String, Object>  parameters;

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
    
}
