/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.EventManager;

/**
 *
 * @author Guilherme
 */
public class DataManager extends ComponentManager implements AsynchronouslyManageableComponent{

    private List<CommunicationInterface> supportedCommunicationInterfaces;
    CommunicationDAO communicationDAO;
    
    public DataManager(DeviceManager deviceManager) {
        super(deviceManager);
        this.supportedCommunicationInterfaces = new ArrayList<CommunicationInterface>();
    }
    
    @Override
    public void onCreate() {
        communicationDAO = new CommunicationDAO();
        // Carrega interfaces de comunicação disponíveis (testa disponibilidade antes de executar - lookback)
        for(CommunicationInterface ci : supportedCommunicationInterfaces){
            try {
                if(ci.isAvailable()){
                    this.communicationDAO.addAvailableCommunicationInterface(ci);
                     System.out.println("Interface successfully added: "+ci.getName());
                }
            } catch (UnsupportedOperationException ex){
                System.out.println("Not implemented yet: "+ci.getName());
                //Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "Not implemented yet.", ex);
            } catch (IOException ex) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
        // Descobrir todo o conhecimento e criar o banco
    }
    
    public CommunicationDAO getCommunicationDAO(){
        
        return communicationDAO;
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addSupportedCommunicationInterface(CommunicationInterface ci){
        supportedCommunicationInterfaces.add(ci);
    }
}
