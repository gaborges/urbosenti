/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

/**
 *
 * @author Guilherme
 */
public class Agent {
    
    public static final String LAYER_SYSTEM = "system";
    public static final String LAYER_APPLICATION = "application";
    
    private String uid;
    private String address;
    private String layer;
    private String description;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 
     * @return Retorna a camada do agente:
     *  Agent.LAYER_SYSTEM = "system";
     *  Agent.LAYER_APPLICATION = "application";
     */
    public String getLayer() {
        return layer;
    }

    /**
     * 
     * @param layer pode conter os valores:
     *  Agent.LAYER_SYSTEM = "system";
     *  Agent.LAYER_APPLICATION = "application";
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Agent{" + "uid=" + uid + ", address=" + address + ", layer=" + layer + ", description=" + description + '}';
    }

}
