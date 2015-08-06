/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;


/**
 * Alvo baseados em IP
 * @author Guilherme
 */
public class Address {
    
    public static final int LAYER_SYSTEM = 2;
    public static final int LAYER_APPLICATION = 1;
    public static final String DEFAULT_UID_VALUE = "unknown";
    
    private String uid;
    private Integer layer;
    private String address;

    public Address() {
        this.layer = Address.LAYER_APPLICATION;
        this.uid = DEFAULT_UID_VALUE;
    }

    public Address(String address) {
        this();
        this.address = address;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public Integer getLayer() {
        return layer;
    }

    public String getAddress() {
        return address;
    }
    
}
