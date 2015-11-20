/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.resources;

import urbosenti.core.data.dao.ResourcesDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.events.Action;

/**
 *
 * @author Guilherme
 */
public class ResourceManager extends ComponentManager {

    public ResourceManager(DeviceManager deviceManager) {
        super(deviceManager,ResourcesDAO.COMPONENT_ID);
    }

    @Override
    public void onCreate() {
    	// Carregar dados e configura��es que ser�o utilizados para executar em mem�ria
        // Preparar configura��es inicias para execu��o
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
