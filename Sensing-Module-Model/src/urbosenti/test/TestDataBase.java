/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.Main;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.interfaces.DTNCommunicationInterface;
import urbosenti.core.communication.interfaces.MobileDataCommunicationInterface;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.communication.interfaces.WirelessCommunicationInterface;
import urbosenti.core.communication.receivers.SocketPushServiceReceiver;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Agent;
import urbosenti.core.device.model.AgentMessage;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Conversation;
import urbosenti.core.device.model.Device;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.InteractionModel;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.State;
import urbosenti.core.events.Action;

/**
 *
 * @author Guilherme
 */
public class TestDataBase {

    public static void main(String[] args) {
        /**
         * *** Configuração do middleware pelo framework ****
         */

        // Instanciar componentes -- Device manager e os demais onDemand
        DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
        deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação

        // Adicionar as interfaces de comunicação suportadas --- Inicialmente manual. Após adicionar um processo automático
        deviceManager.addSupportedCommunicationInterface(new WiredCommunicationInterface());
        deviceManager.setOSDiscovery(new DesktopOperationalSystemDiscovery());
        // Adiciona o AplicationHandler da aplicação para tratamento de eventos da aplicação
        ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
        deviceManager.getEventManager().subscribe(handler);
        // Execução - inicia todos os serviços e threads em background. Intanciar serviço de recebimento de mensagens
        PushServiceReceiver teste = new SocketPushServiceReceiver(deviceManager.getCommunicationManager());
        //DeliveryMessagingService delivaryService = new DeliveryMessagingService(deviceManager.getCommunicationManager());
        deviceManager.addSupportedInputCommunicationInterface(teste);
        // Atribuir o modelo de conhecimento do dispositivo que será descoberto pelo mecanismo de adaptação --- Falta fazer - Guilherme    
        deviceManager.setDeviceKnowledgeRepresentationModel(new File("deviceKnowledgeModel.xml"), "xmlFile");

        /**
         * *** Processo de descoberta das configurações adicionadas ****
         */
        // Processo de Descoberta, executa todos os onCreate's de todos os Componentes habilidatos do módudo de sensoriamento
        deviceManager.onCreate();

        DataManager data = deviceManager.getDataManager();

        Entity entity = new Entity();
        entity.setId(1);
        if (true) {
            return;
        }
        try {
            /**
             * **** Adição e consulta de conteúdo *****
             */
//            List<State> entityStates = data.getEntityStateDAO().getEntityStates(entity);
//            State state = entityStates.get(1);
//            // testa inserção com conteúdo
//            Content c = new Content();
//            c.setTime(new Date());
//            c.setValue("juca663");
//            state.setContent(c);
//            data.getEntityStateDAO().insertContent(state);
//            // testa busca com conteúdo string
//            // testa conteúdo double
//            entity.setId(7);
//            entityStates = data.getEntityStateDAO().getEntityStates(entity);
//            state = entityStates.get(2);
//            c = new Content();
//            c.setTime(new Date());
//            c.setValue(0.9);
//            state.setContent(c);
//            data.getEntityStateDAO().insertContent(state);
//            // testa busca com conteúdo double
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println("TaxaDeUpload: "+((Double)state.getCurrentValue())+" Date: "+dateFormat.format(state.getContent().getTime()));
//            
            /**
             * **** Retornar o modelo de dispositivo *****
             */
//            Component device = data.getCommunicationDAO().getComponentDeviceModel();
//            for(Entity e : device.getEntities()){
//                System.out.println("Entity: "+e.getDescription());
//                for(State s : e.getStates()){
//                    System.out.println("State: "+s.getId()+","+s.getDescription()+", content: "+s.getCurrentValue());
//                    if(s.getId() == 5 || s.getId() == 9 ){
//                        System.out.println("Content Boolean? "+(s.getContent().getValue() instanceof Boolean) );
//                        Content c = new Content();
//                        c.setTime(new Date());
//                        c.setValue(true);
//                        s.setContent(c);
//                        data.getEntityStateDAO().insertContent(s);
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(TestDataBase.class.getName()).log(Level.SEVERE, null, ex);
//        }

            /**
             * **** Retornar o modelo geral do dispositivo *****
             */
            Device device = data.getDeviceDAO().getDeviceModel(data);

            for (Component c : device.getComponents()) {
                for (Entity e : c.getEntities()) {
                    System.out.println("Entity: " + e.getDescription());
                    for (State s : e.getStates()) {
                        //System.out.println("State: "+s.getId()+","+s.getDescription()+", content: "+s.getCurrentValue());
                        //if(s.getId() == 5 || s.getId() == 9 ){
                        //    System.out.println("Content Boolean? "+(s.getContent().getValue() instanceof Boolean) );
//                        Content c = new Content();
//                        c.setTime(new Date());
//                        c.setValue(true);
//                        s.setContent(c);
//                        data.getEntityStateDAO().insertContent(s);
                        //}
                    }
                    for (ActionModel action : e.getActions()) {
                        System.out.println("Action: " + action.getId() + "," + action.getDescription());
                    }
                }
            }
            for (Service service : device.getServices()) {
                System.out.println("Service: " + service.getServiceType().getDescription());
                for (State s : service.getAgent().getAgentType().getStates()) {
                    System.out.println("State: " + s.getId() + "," + s.getDescription() + ", content: " + s.getCurrentValue());
                }
                for (InteractionModel i : service.getAgent().getAgentType().getInteraction()) {
                    System.out.println("Interaction " + i.getId() + ", " + i.getDescription());
                }
                for (Conversation c : service.getAgent().getConversations()) {
                    for (AgentMessage m : c.getMessages()) {

                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TestDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
