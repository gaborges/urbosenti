/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.concerns;

import urbosenti.core.data.dao.ConcernsDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.events.Action;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class ConcernManager extends ComponentManager {

    public ConcernManager(DeviceManager deviceManager) {
        super(deviceManager,ConcernsDAO.COMPONENT_ID);
    }
    
    @Override
    public void onCreate() {
        if(DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY){
            System.out.println("Activating: " + getClass());
        }
        // Carregar dados e configura��es que serão utilizados para executar em mem�ria
        // Preparar configura��es inicias para execu��o
        // Para tanto utilizar o DataManager para acesso aos dados.
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
