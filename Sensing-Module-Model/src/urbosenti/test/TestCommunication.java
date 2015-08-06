/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.Message;
import urbosenti.core.device.model.Agent;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.Service;

/**
 *
 * @author Guilherme
 */
public class TestCommunication {
    
    private DeviceManager deviceManager;

    public TestCommunication(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }
    
    public void test1(){
        // Envio de mensagem sem retorno
        // testaEnvioMensagemSemRetorno(); // OK
        
        // Envio de mensagem com retorno
        // testaEnvioMensagemComRetorno();
         
        // Teste do serviço de upload - ok
        testaUploadServer();
                
    }
    
    public void testaEnvioMensagemSemRetorno(){
        // Envio normal de mensagem sem retorno
        Address target = new Address("http://localhost:8090/Test2Server/webresources/generic");
        target.setUid("12345");
        
        Address origin = new Address();
        origin.setLayer(Address.LAYER_SYSTEM);
        origin.setUid("6666");
        
        Message m = new Message();
        m.setTarget(target);
        m.setOrigin(origin);
        m.setSubject(Message.SUBJECT_APPLICATION_MESSAGE);
        m.setContentType("application/xml");
        m.setContent("oiiiiii");
        try {
            deviceManager.getCommunicationManager().sendMessage(m);
        } catch (java.net.ConnectException ex){
            System.out.println("Host não acessível!");
        }catch (SocketTimeoutException ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testaEnvioMensagemComRetorno(){
        Address target = new Address("http://localhost:8090/Test2Server/webresources/generic");
        target.setUid("12345");
        
        Address origin = new Address();
        origin.setUid("6666");
        
        Message m = new Message();
        m.setTarget(target);
        m.setOrigin(origin);
        m.setSubject(Message.SUBJECT_APPLICATION_MESSAGE);
        m.setContentType("application/xml");
        m.setContent("oi22222");
        
        String result ="";
        try {
            result = deviceManager.getCommunicationManager().sendMessageWithResponse(m);
        } catch (java.net.SocketTimeoutException ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }        
        System.out.println("Result: " + result);
    }
    
    public void testaUploadServer(){
//        // Serviço do report
//        Service service = new Service();
//        service.setServiceUID("666");
//        service.setApplicationUID("333");
//        service.setAddress("http://localhost:8090/Test2Server/webresources/generic");
//        // adiciona o serviço       
//        deviceManager.getCommunicationManager().addUploadServer(service);
//        
        // Inicia o serviço de envio de mensagens
//        Thread t = new Thread(deviceManager.getCommunicationManager());
//        t.start();        
        
        // Adiciona a origem, e é somente necessário se for uma mensagem de 
        // sistema, caso não seja somente a mensagem é necessária
//        Address origin = new Address();
//        origin.setLayer(Address.LAYER_SYSTEM);
        
        // Cria a mensagem
        Message m = new Message();
        //m.setOrigin(origin);
        //m.setSubject(Message.SUBJECT_UPLOAD_REPORT);
        //m.setContentType("application/xml");
        m.setContent("oiiiiii");
        
        try {
            // Envia o relato
            deviceManager.getCommunicationManager().addReportToSend(m);
        } catch (SQLException ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TestCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }

}
