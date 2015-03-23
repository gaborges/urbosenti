/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import urbosenti.core.device.BaseComponentManager;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.model.Device;

/**
 *
 * @author Guilherme
 */
public abstract class DiscoveryAdapter {
    
    public abstract Device discovery(BaseComponentManager baseComponentManager);
    
}
