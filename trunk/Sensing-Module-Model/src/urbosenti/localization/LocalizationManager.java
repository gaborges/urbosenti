/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.localization;

import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.EventManager;

/**
 *
 * @author Guilherme
 */
public class LocalizationManager extends ComponentManager implements AsynchronouslyManageableComponent{

    public LocalizationManager(DeviceManager deviceManager) {
        super(deviceManager);
    }

    @Override
    public void onCreate() {
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
