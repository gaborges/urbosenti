/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import urbosenti.core.communication.Message;
import urbosenti.core.device.Agent;
import urbosenti.core.device.DeviceManager;

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
        // Envio normal de mensagem sem retorno
        // testaEnvioMensagemNormalSemRetorno(); // OK
        
                
        // Envio prioritário de mensagem sem retorno
        
        // Envio normal de mensagem com retorno
        testaEnvioMensagemNormalComRetorno();
        
        
        
        // Envio prioritário de mensagem com retorno
        
        // Teste de desconexão
        
        // Teste de reconexão
        
        // Teste de armazenamento
         
    }
    
    public void testaEnvioMensagemNormalSemRetorno(){
        // Envio normal de mensagem sem retorno
        Agent target = new Agent();
        target.setAddress("http://143.54.12.47:8084/TestServer/webresources/test/");
        target.setUid("666");
        target.setLayer("application");
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setUid("1232456789");
        origin.setLayer("application");
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando");
        m.setContentType("text/plain");
        m.setContent("oiiiiii");
        
        deviceManager.getCommunicationManager().sendMessage(m);
    }
    
    public void testaEnvioMensagemPrioritariaSemRetorno(){
        // Envio normal de mensagem sem retorno
        Agent target = new Agent();
        target.setAddress("http://143.54.12.47:8084/TestServer/webresources/test/");
        target.setUid("666");
        target.setLayer("application");
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setUid("1232456789");
        origin.setLayer("application");
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando");
        m.setContentType("text/plain");
        m.setContent("oiiiiii");
        m.setPreferentialPriority(); // Prioridade Preferêncial
        
        deviceManager.getCommunicationManager().sendMessage(m);
    }
    
    public void testaEnvioMensagemNormalComRetorno(){
        Agent target = new Agent();
        target.setAddress("http://143.54.12.47:8084/TestServer/webresources/test/return");
        target.setUid("666");
        target.setLayer("application");
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setUid("1232456789");
        origin.setLayer("application");
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando Retorno");
        m.setContentType("text/plain");
        m.setContent("oi22222");
        
        String result = deviceManager.getCommunicationManager().sendMessageWithResponse(m);
        
        System.out.println("Result: " + result);
    }
    
    public void testaEnvioMensagemPrioritariaComRetorno(){
        Agent target = new Agent();
        target.setAddress("http://143.54.12.47:8084/TestServer/webresources/test/return");
        target.setUid("666");
        target.setLayer("application");
        target.setDescription("Backend Module");
        
        Agent origin = new Agent();
        origin.setUid("1232456789");
        origin.setLayer("application");
        origin.setDescription("Sensing Module");
        
        Message m = new Message();
        m.setTarget(target);
        m.setSender(origin);
        m.setSubject("Textando Retorno");
        m.setContentType("text/plain");
        m.setContent("oi22222");
        m.setPreferentialPriority(); // Prioridade Preferêncial
        
        
    }

}
