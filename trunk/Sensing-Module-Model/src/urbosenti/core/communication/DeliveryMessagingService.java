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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated - Em breve será excluído dessa implementação, a não ser que
 * façamos um sistema de multiplos uploads ao mesmo tempo
 * @author Guilherme
 */
public class DeliveryMessagingService {

    private CommunicationManager communicationManager;

    public DeliveryMessagingService(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
    }

    @Deprecated
    public boolean sendHttpMessage(String stringUrl, String contentType, String message) throws MalformedURLException, IOException {
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

    public String sendHttpMessageReturn(CommunicationManager cm, MessageWrapper messageWrapper, CommunicationInterface ci) throws IOException, java.net.SocketTimeoutException {
        String result = "";
        URL url = new URL(messageWrapper.getMessage().getTarget().getAddress());
        //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test/return");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        //conn.setRequestProperty("Accept", "application/json");
        //conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Type", messageWrapper.getMessage().getContentType());
        // Se possuí timeout customizado o utiliza
        if (messageWrapper.getTimeout() > 0) {
            conn.setReadTimeout(messageWrapper.getTimeout());
        }
        Date iniTime = new Date();

        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(messageWrapper.getEnvelopedMessage().getBytes());
        os.flush();

        // HttpURLConnection.HTTP_NOT_FOUND --- Posso usar para um evento de endereço não existe
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
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
        // Adicionar métricas de avaliação **************************************8
        messageWrapper.setResponseTime((new Date()).getTime() - iniTime.getTime());
        messageWrapper.setSentTime(new Date());
        messageWrapper.setSent(true);
        messageWrapper.setUsedCommunicationInterface(ci);
        // Adiciona a latência média se naõ for a primeira vez
        if (ci.getAverageLatency() > 0) {
            ci.setAverageLatency((ci.getAverageLatency() + messageWrapper.getResponseTime()) / 2);
        } else {
            ci.setAverageLatency(messageWrapper.getResponseTime());
        }
        // Adiciona a troughput médio se não for a primeira vez
        if (ci.getAverageThroughput() > 0) {
            ci.setAverageThroughput((ci.getAverageThroughput() + messageWrapper.getSize()) / 2);
        } else {
            ci.setAverageThroughput(messageWrapper.getSize());
        }
        // Adicionar métricas de avaliação **************************************8
        return result;
    }

    boolean sendHttpMessage(CommunicationManager cm, MessageWrapper messageWrapper, CommunicationInterface ci) {
        try {
            String result = "";
            URL url = new URL(messageWrapper.getMessage().getTarget().getAddress());
            //URL url = new URL("http://143.54.12.47:8084/TestServer/webresources/test");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Se possuí timeout customizado o utiliza
            if (messageWrapper.getTimeout() > 0) {
                conn.setReadTimeout(messageWrapper.getTimeout());
            }
            Date iniTime = new Date();

            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Content-Type", messageWrapper.getMessage().getContentType());
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(messageWrapper.getEnvelopedMessage().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            conn.disconnect();
            // Adicionar métricas de avaliação **************************************8
            messageWrapper.setResponseTime((new Date()).getTime() - iniTime.getTime());
            messageWrapper.setSentTime(new Date());
            messageWrapper.setSent(true);
            messageWrapper.setUsedCommunicationInterface(ci);
            // Adiciona a latência média se naõ for a primeira vez
            if (ci.getAverageLatency() > 0) {
                ci.setAverageLatency((ci.getAverageLatency() + messageWrapper.getResponseTime()) / 2);
            } else {
                ci.setAverageLatency(messageWrapper.getResponseTime());
            }
            // Adiciona a troughput médio se não for a primeira vez
            if (ci.getAverageThroughput() > 0) {
                ci.setAverageThroughput((ci.getAverageThroughput() + messageWrapper.getSize()) / 2);
            } else {
                ci.setAverageThroughput(messageWrapper.getSize());
            }
            // Adicionar métricas de avaliação **************************************8
            return true;
        } catch (java.net.SocketTimeoutException ex) {
            Logger.getLogger(DeliveryMessagingService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(DeliveryMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
