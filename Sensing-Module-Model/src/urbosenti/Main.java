/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti;

import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.EventManager;

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
        EventManager e = new EventManager();
        DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
        deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação
        
        // Processo de Descoberta, executa todos os onCreate
        deviceManager.onCreate();
        // Execução - inicia todos os serviços e threads em background
        PushServiceReceiver teste = new PushServiceReceiver(deviceManager.getCommunicationManager());
        
        // Cria as threads para 1 - comunicação (pushs), e 2  para do mecanismo de adaptação
        
        // Indica que o processo de adaptação já passou pelo processo de descoberta. Deve ser executado antes do Create.
        // OBS.: O adaptation Manager Atual é um agente reativo. Futuramente será proativo
        deviceManager.getAdaptationManager().start(); 
        
        // Inicia o serviço de Push, para receber mensagens do servidor. OBS.: Utilizar implementações nativas, com GCM ou o Iphone Push Service
        teste.startPushReceiverService();
        
        Thread t = new Thread(deviceManager.getAdaptationManager());
        t.start();
        
        for (int i = 0; i < 1000000; i++) {
            
        }
           
        deviceManager.getAdaptationManager().stop();
        
              
        // Daqui para baixo seria coisas da aplicação
        // Aplicação de sensoriamento blablabla
    }
}
