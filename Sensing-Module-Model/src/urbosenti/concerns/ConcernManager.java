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
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
