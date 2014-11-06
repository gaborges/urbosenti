/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication.interfaces;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.CommunicationInterface;

/**
 *
 * @author Guilherme
 */
public class WiredCommunicationInterface extends CommunicationInterface{

    public WiredCommunicationInterface() {
        super();
        setId(1);
        setName("Wired Interface");
        setUsesMobileData(false);
        setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
    }
    
    @Override
    public boolean testConnection() throws IOException, UnsupportedOperationException {
        try {
               // URL do destino escolhido
               URL url = new URL("http://www.google.com");
 
               // abre a conexão
               HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
 
               // tenta buscar conteúdo da URL
               // se não tiver conexão, essa linha irá falhar
               urlConnect.connect();
               urlConnect.disconnect();
               //Object objData = urlConnect.getContent();
 
           } catch (UnknownHostException ex) {
               Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
               return false;
           }
           catch (IOException ex) {
               Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
               return false;
           }
           return true;
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
        setStatus(CommunicationInterface.STATUS_AVAILABLE);
        return true;
    }
}
