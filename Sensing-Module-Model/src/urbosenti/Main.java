/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.interfaces.DTNCommunicationInterface;
import urbosenti.core.communication.interfaces.MobileDataCommunicationInterface;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.communication.interfaces.WirelessCommunicationInterface;
import urbosenti.core.communication.receivers.SocketPushServiceReceiver;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.Service;
import urbosenti.test.ConcreteApplicationHandler;
import urbosenti.test.DesktopOperationalSystemDiscovery;
import urbosenti.test.TestAdaptationComponente;
import urbosenti.test.TestCommunication;

/**
 *
 * @author Guilherme
 *
 *
 * falta todas as funções. 1) testar o push para interação. OBS.: falta
 * implementar os onCreate 2) modelar segundo modelo que fiz ontem 3) modelar e
 * implementar toda a comunicação e os eventos internos
 */
public class Main {

    public static void main(String[] args) {
        
        /**
         * *** Configuração do middleware pelo framework ****
         */
        // Instanciar componentes -- Device manager e os demais onDemand
        DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
        deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação

        // Adicionar as interfaces de comunicação suportadas --- Inicialmente manual. Após adicionar um processo automático
        deviceManager.addSupportedCommunicationInterface(new WiredCommunicationInterface());
        //deviceManager.addSupportedCommunicationInterface(new WirelessCommunicationInterface()); // não implementado
        //deviceManager.addSupportedCommunicationInterface(new MobileDataCommunicationInterface()); // não implementado
        //deviceManager.addSupportedCommunicationInterface(new DTNCommunicationInterface()); // não implementado

        // Adiciona o AplicationHandler da aplicação para tratamento de eventos da aplicação
        ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
        deviceManager.getEventManager().subscribe(handler);
        // Execução - inicia todos os serviços e threads em background. Intanciar serviço de recebimento de mensagens
        PushServiceReceiver teste = new SocketPushServiceReceiver(deviceManager.getCommunicationManager());
        //DeliveryMessagingService delivaryService = new DeliveryMessagingService(deviceManager.getCommunicationManager());
        deviceManager.addSupportedInputCommunicationInterface(teste);

        deviceManager.setOSDiscovery(new DesktopOperationalSystemDiscovery());
        // Atribuir o modelo de conhecimento do dispositivo que será descoberto pelo mecanismo de adaptação --- Falta fazer - Guilherme    
        deviceManager.setDeviceKnowledgeRepresentationModel(new File("deviceKnowledgeModel.xml"), "xmlFile");
        // deviceManager.validateDeviceKnowledgeRepresentationModel();
        // deviceManager.setAgentKnowledgeRepresentationModel(Object o,String dataType);
        // deviceManager.validateAgentKnowledgeRepresentationModel();

        // Timer feito pela aplicação pode ser adicionado usando esse método:
        // deviceManager.getEventManager().setExternalEventTimer(new AndroidEventTimerFactory("AndroidEventTimer"));
        /**
         * *** Processo de descoberta das configurações adicionadas ****
         */
        // Processo de Descoberta, executa todos os onCreate's de todos os Componentes habilidatos do módudo de sensoriamento
        deviceManager.onCreate();
        
        try {
            /**
             * *** Processo de inicialização dos serviços ****
             */
            automaticStartup(deviceManager);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //manualStartup(deviceManager, teste);
        /**
         * *** Inicio das funções e aplicação de sensoriamento ****
         */
        // Aplicação de sensoriamento blablabla
        // Testes
        TestCommunication tc = new TestCommunication(deviceManager);
        //for (int i = 1; i < 100; i++) {
        tc.test1();
        //}
        System.out.println("Teste finalizado");
        try {
            Thread.sleep(120000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        deviceManager.stopUrboSentiServices();
        System.exit(0);
    }

    public static void manualStartup(DeviceManager deviceManager, PushServiceReceiver teste) {
        deviceManager.getAdaptationManager().start();

        // Inicia o serviço de upload -- Testar em breve
        //Thread uploader = new Thread(deviceManager.getCommunicationManager());
        //uploader.start();
        // Inicia o serviço de Push, para receber mensagens do servidor. OBS.: Utilizar implementações nativas, com GCM ou o Iphone Push Service
        teste.start();

        Thread t = new Thread(deviceManager.getAdaptationManager());
        t.start();

        deviceManager.getAdaptationManager().stop();

        /**
         * *** Registro do nó de sensoriamento móvel no servidor de aplicação -
         * falta fazer a função e o servidor ****
         */
        // busca o servidor backend cadastrado no conhecimento inicial
        Service backendServer = deviceManager.getBackendService();

        try {
            // Registrar no Servidor Backend
            deviceManager.registerSensingModule(backendServer);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        try {
            // Adicionar o servidor da aplicação como servidor para upload
            deviceManager.setUpCommunicationUrboSentiServices();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Iniciar serviço de upload
        deviceManager.getCommunicationManager().startAllCommunicationServices();
    }

    public static void automaticStartup(DeviceManager deviceManager) throws IOException, SQLException {
        deviceManager.startUrboSentiServices();
    }

}
