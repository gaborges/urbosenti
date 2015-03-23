/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        testaEnvioMensagemComRetorno();
         
        // Teste do serviço de upload - ok
        //testaUploadServer();
                
    }
    
    public void testaEnvioMensagemSemRetorno(){
        Service service = new Service();
        service.setServiceUID("666");
        service.setApplicationUID("333");
        service.setAddress("http://localhost:8090/Test2Server/webresources/generic");
        // Envio normal de mensagem sem retorno
        Agent target = new Agent();
        target.setRelativeAddress("/");
        target.setLayer(Agent.LAYER_APPLICATION);
        target.setService(service);
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setLayer(Agent.LAYER_SYSTEM);
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando");
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
        Agent target = new Agent();
        target.setServiceAddress("http://localhost:8090/Test2Server/webresources/generic");
        target.setLayer(Agent.LAYER_APPLICATION);
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setLayer(Agent.LAYER_APPLICATION);
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando Retorno");
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
        
        Service service = new Service();
        service.setServiceUID("666");
        service.setApplicationUID("333");
        service.setAddress("http://localhost:8090/Test2Server/webresources/generic");
        Agent target = new Agent();
        target.setService(service);
        target.setLayer(Agent.LAYER_APPLICATION);
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        target.setService(service);
        origin.setLayer(Agent.LAYER_APPLICATION);
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando");
        m.setContentType("application/xml");
        m.setContent("oiiiiii");

        
        deviceManager.getCommunicationManager().addUploadServer(target);
        deviceManager.getCommunicationManager().sendReport(m);
        Thread t = new Thread(deviceManager.getCommunicationManager());
        t.start();
      
    }

}
