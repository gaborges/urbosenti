/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.MessageWrapper;

/**
 *
 * @author Guilherme
 */
public class WiredCommunicationInterface extends CommunicationInterface {

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
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

               // tenta buscar conteúdo da URL
            // se não tiver conexão, essa linha irá falhar
            urlConnect.connect();
            urlConnect.disconnect();
               //Object objData = urlConnect.getContent();

        } catch (UnknownHostException ex) {
            Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean testConnection(URL url) throws IOException, UnsupportedOperationException {
        try {
            // abre a conexão
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            // tenta buscar conteúdo da URL
            // se não tiver conexão, essa linha irá falhar
            urlConnect.connect();
            urlConnect.disconnect();

        } catch (UnknownHostException ex) {
            Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(WiredCommunicationInterface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean connect(String address) throws IOException, UnsupportedOperationException {
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

    @Override
    public Object sendMessageWithResponse(CommunicationManager communicationManager, MessageWrapper messageWrapper) throws SocketTimeoutException, IOException {
        String result = "";
        // se não contem http, então é por socket direto
    	if(!messageWrapper.getMessage().getTarget().getAddress().contains("http://")){
    		// formato 143.54.12.47:8084
    		String splited[] = messageWrapper.getMessage().getTarget().getAddress().split(":");
    		if(splited.length <2){
                    throw new IOException("Failed : Address '"+messageWrapper.getMessage().getTarget().getAddress()+"' has a unknown format for socket connections, please use the format 'ip:port'.");
    		}
    		Socket socket = new Socket(splited[0],Integer.parseInt(splited[1]));
    		socket.getOutputStream().write(messageWrapper.getEnvelopedMessage().getBytes());
    		socket.getOutputStream().flush();
    		socket.getOutputStream().close();
    		socket.close();
    	} else {
            URL url = new URL(messageWrapper.getMessage().getTarget().getAddress());
            //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test/return");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Content-Type", messageWrapper.getMessage().getContentType());
            // Se possuí timeout customizado o utiliza
            if (messageWrapper.getTimeout() > 0) {
                conn.setReadTimeout(messageWrapper.getTimeout());
            }

            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(messageWrapper.getEnvelopedMessage().getBytes());
            os.flush();

            // HttpURLConnection.HTTP_NOT_FOUND --- Posso usar para um evento de endereço não existe
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            br.close();
            conn.disconnect();
        }
        return result;
    }

    @Override
    public boolean sendMessage(CommunicationManager communicationManager, MessageWrapper messageWrapper) throws SocketTimeoutException, IOException {
        // se não contem http, então é por socket direto
    	if(!messageWrapper.getMessage().getTarget().getAddress().contains("http://")){
    		// formato 143.54.12.47:8084
    		String splited[] = messageWrapper.getMessage().getTarget().getAddress().split(":");
    		if(splited.length <2){
    			throw new IOException("Failed : Address '"+messageWrapper.getMessage().getTarget().getAddress()+"' has a unknown format for socket connections, please use the format 'ip:port'.");
    		}
    		Socket socket = new Socket(splited[0],Integer.parseInt(splited[1]));
    		socket.getOutputStream().write(messageWrapper.getEnvelopedMessage().getBytes());
    		socket.getOutputStream().flush();
    		socket.getOutputStream().close();
    		socket.close();
    	} else {
            URL url = new URL(messageWrapper.getTargetAddress());
            //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Se possuí timeout customizado o utiliza
            if (messageWrapper.getTimeout() > 0) {
                conn.setReadTimeout(messageWrapper.getTimeout());
            }
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Content-Type", messageWrapper.getMessage().getContentType());
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(messageWrapper.getEnvelopedMessage().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new IOException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            conn.disconnect();
        }
        return true;
    }

    @Override
    public Object receiveMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object receiveMessage(int timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
