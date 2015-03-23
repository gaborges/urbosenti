/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import java.util.HashMap;
import urbosenti.core.device.model.Agent;

/**
 *
 * @author Guilherme
 */
public class Action {
    private int id;
    private String name;
    private int sourceLayer;
    private Agent origin;
    private int targetObjectId;
    private  HashMap<String, Object>  parameters;

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

    public Agent getOrigin() {
        return origin;
    }

    public void setOrigin(Agent origin) {
        this.origin = origin;
    }

    public int getTargetObjectId() {
        return targetObjectId;
    }

    public void setTargetObjectId(int targetObjectId) {
        this.targetObjectId = targetObjectId;
    }
    
}
