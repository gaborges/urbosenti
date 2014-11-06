/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication.interfaces;

import java.io.IOException;
import urbosenti.core.communication.CommunicationInterface;

/**
 *
 * @author Guilherme
 */
public class MobileDataCommunicationInterface extends CommunicationInterface{
    
    public MobileDataCommunicationInterface() {
        super();
        setId(1);
        setName("Wired Interface");
        setUsesMobileData(false);
        setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
    }
    
    @Override
    public boolean testConnection() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean connect() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean disconnect() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean isAvailable() throws IOException, UnsupportedOperationException {
        setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
        return false;
    }
}