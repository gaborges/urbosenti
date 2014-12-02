/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti;

import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.interfaces.DTNCommunicationInterface;
import urbosenti.core.communication.interfaces.MobileDataCommunicationInterface;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.communication.interfaces.WirelessCommunicationInterface;
import urbosenti.core.device.DeviceManager;
import urbosenti.test.ConcreteApplicationHandler;
import urbosenti.test.TestCommunication;

/**
 *
 * @author Guilherme
 * 
 * 
 * falta todas as funções. 
 * 1) testar o push para interação. OBS.: falta implementar os onCreate
 * 2) modelar segundo modelo que fiz ontem
 * 3) modelar e  implementar toda a comunicação e os eventos internos
 */
public class Main {
    public static void main(String[] args) {
        // Instanciar componentes -- Device manager e os demais onDemand
        DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
        deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação
     
        // Adicionar as interfaces de comunicação suportadas --- Inicialmente manual. Após adicionar um processo automático
        deviceManager.addSupportedCommunicationInterface(new WiredCommunicationInterface());
        deviceManager.addSupportedCommunicationInterface(new WirelessCommunicationInterface()); // não implementado
        deviceManager.addSupportedCommunicationInterface(new MobileDataCommunicationInterface()); // não implementado
        deviceManager.addSupportedCommunicationInterface(new DTNCommunicationInterface()); // não implementado
        
        // Processo de Descoberta, executa todos os onCreate
        deviceManager.onCreate();
        deviceManager.setUID("uid:123456789asdf");
        // Adiciona o AplicationHandler da aplicação
        ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
        deviceManager.getEventManager().subscribe(handler);
        // Execução - inicia todos os serviços e threads em background
        PushServiceReceiver teste = new PushServiceReceiver(deviceManager.getCommunicationManager());
        //DeliveryMessagingService delivaryService = new DeliveryMessagingService(deviceManager.getCommunicationManager());
        
        // Cria as threads para 1 - comunicação (pushs), e 2  para do mecanismo de adaptação
        
        // Indica que o processo de adaptação já passou pelo processo de descoberta. Deve ser executado antes do Create.
        // OBS.: O adaptation Manager Atual é um agente reativo. Futuramente será proativo
        deviceManager.getAdaptationManager().start(); 
        
        // Inicia o serviço de Push, para receber mensagens do servidor. OBS.: Utilizar implementações nativas, com GCM ou o Iphone Push Service
        teste.startPushReceiverService();
        
        Thread t = new Thread(deviceManager.getAdaptationManager());
        t.start();
                      
        // Testes
        TestCommunication tc = new TestCommunication(deviceManager);
        tc.test1();
        try {
            Thread.sleep(120000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        deviceManager.getAdaptationManager().stop();
        
              
        // Daqui para baixo seria coisas da aplicação
        // Aplicação de sensoriamento blablabla
    }
      
    
}