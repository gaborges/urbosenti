/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import java.util.List;
import urbosenti.core.device.model.Device;

/**
 *
 * @author Guilherme
 */
public interface BaseComponentManager {
      /**
     * Retorna a estrutura base de dados do dispositivo. Contendo todas as informações sobre as estruturas dos componentes.
     * @return 
     */
    public Device getBaseDeviceStructure();
    public List<ComponentManager> getComponentManagers();
    public void addComponentManager(ComponentManager componentManager);
}
