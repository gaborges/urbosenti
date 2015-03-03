/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device.model;

import java.util.List;

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
    private int id;
    private String systemAddress;
    private int systemPort;
    private AgentType agentType;
    private List<Conversation> conversations;
    private AddressAgentType addressType;
    
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSystemAddress() {
        return systemAddress;
    }

    public void setSystemAddress(String systemAddress) {
        this.systemAddress = systemAddress;
    }

    public int getSystemPort() {
        return systemPort;
    }

    public void setSystemPort(int systemPort) {
        this.systemPort = systemPort;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public AddressAgentType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressAgentType addressType) {
        this.addressType = addressType;
    }

    
}
