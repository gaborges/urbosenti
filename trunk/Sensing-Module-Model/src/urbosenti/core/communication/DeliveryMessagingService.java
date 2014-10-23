/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Guilherme
 */
public class DeliveryMessagingService {

    private CommunicationManager communicationManager;
    
    public DeliveryMessagingService(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
    }
    
    public boolean sendHttpMessage(String stringUrl,String contentType,String message) throws MalformedURLException, IOException{
        String result = "";
        URL url = new URL(stringUrl);
        //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        //conn.setRequestProperty("Accept", "application/json");
        //conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Type", contentType);
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(message.getBytes());
        os.flush();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        conn.disconnect();
        return true;
    }
    
    public String sendHttpMessageReturn(String stringUrl,String contentType,String message) throws MalformedURLException, IOException{
        String result = "";
        URL url = new URL(stringUrl);
        //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test/return");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        //conn.setRequestProperty("Accept", "application/json");
        //conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Type", contentType);
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(message.getBytes());
        os.flush();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
            result += output;
        }
        
        conn.disconnect();
        return result;
    }
}
