/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

/**
 *
 * @author Guilherme
 */
public class ReconnectionService implements Runnable{

    private CommunicationManager communicationManager;
    
    public ReconnectionService(CommunicationManager cm) {
        communicationManager = cm;
    }
   
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void reconectionProcess(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
