/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.data.dao.AdaptationDAO;
import urbosenti.core.data.dao.CommunicationDAO;
import urbosenti.core.data.dao.EventModelDAO;
import urbosenti.core.data.dao.UserDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.InteractionModel;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.State;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemHandler;
import urbosenti.user.User;
import urbosenti.user.UserManager;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class AdaptationManager extends ComponentManager implements Runnable, SystemHandler {

    /**
     * int EVENT_UNKNOWN_EVENT_WARNING = 1; </ br>
     *
     * <ul><li>id: 1</li>
     * <li>evento: Evento desconhecido</li>
     * <li>parâmetros: string do evento</li></ul>
     *
     */
    private static final int EVENT_UNKNOWN_EVENT_WARNING = 1;
    /**
     * int EVENT_ADAPTATION_LOOP_ERROR = 2; </ br>
     *
     * <ul><li>id: 2</li>
     * <li>evento: Erro no ciclo de adaptação</li>
     * <li>parâmetros: string do erro</li></ul>
     *
     */
    private static final int EVENT_ADAPTATION_LOOP_ERROR = 2;
    /**
     *
     * @param This method receives a event from the Event Manager to process the
     * event applying changes directly in the component.
     */
    //private LocalKnowledge localKnowledge
    private Queue<Event> availableEvents;
    private DeviceManager deviceManager;
    private ContextManager contextManager;
    private UserManager userManager;
    private boolean running;
    private Boolean flag;
    private Boolean monitor;
    private DiscoveryAdapter discoveryAdapter;
    private AdaptationDAO adaptationDAO;
    // private AdaptationLoopControler adaptationLoopControler;

    public AdaptationManager(DeviceManager deviceManager) {
        super(deviceManager, AdaptationDAO.COMPONENT_ID);
        this.deviceManager = null;
        this.contextManager = null;
        this.userManager = null;
        this.running = true;
        this.availableEvents = new LinkedList();
        this.flag = true;
        this.monitor = true;
        this.discoveryAdapter = null;
        this.adaptationDAO = null;
    }

    public boolean discovery(DeviceManager deviceManager) {
        if (running) {
            return false;
        }
        this.deviceManager = deviceManager;

        // descobre o modelo
        return true;
    }

    public boolean discovery(DeviceManager deviceManager, ContextManager contextManager, UserManager userManager) {
        if (running) {
            return false;
        }
        this.deviceManager = deviceManager;
        this.contextManager = contextManager;
        this.userManager = userManager;

        // descobre o modelo
        // Device
        // Componentes em funcionamento
        // sensores e possíveis atuadores de cada componentes
        // Políticas e estratédias (funcionalidades/comportamentos), define restrições
        // User
        // Preferências do usuário, para privacidade, etc... -- Podem ser alterados on the fly
        // Restrições do usuário
        // Context
        // Descoberta de funções de contexto
        // Predição de contextos
        // Gerar conhecimento
        // Modelos de aprendizagem
        // Inferência
        // Apoio a descoberta e identificação de novos recursos
        // Possibilita gatinhos de eventos dinâmicos, como tempo etc...
        return true;
    }

    public boolean discovery(DeviceManager deviceManager, UserManager userManager) {
        if (running) {
            return false;
        }
        this.deviceManager = deviceManager;
        this.userManager = userManager;

        // descobre o modelo
        return true;
    }

    public boolean discovery(DeviceManager deviceManager, ContextManager contextManager) {
        if (running) {
            return false;
        }
        this.deviceManager = deviceManager;
        this.contextManager = contextManager;

        // descobre o modelo
        return true;
    }

    @Override
    public synchronized void newEvent(Event event) {
        /*
         add to queue
         wake up the Adaptation Mechanism
         */
        this.availableEvents.add(event);
        notifyAll();
    }

    public synchronized boolean start() {
        this.running = true;
        notifyAll();
        return true;
    }

    public synchronized boolean stop() {
        this.running = false;
        notifyAll();
        return true;
    }

    private synchronized boolean isRunning() {
        return this.running;
    }

    @Override
    public void run() {
        /* At first, the adaptation manager discovers the environment to rum*/
        this.discovery(deviceManager);
        // discoveryAdapter.discovery(deviceManager);
        /* It begin the monitoring process of events */
        //simplestAdaptationControlLoop();
        try {
            
            monitoring();
        } catch (InterruptedException ex) {
            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void monitoring() throws InterruptedException {
        Event event;

        while (isRunning()) {

            /* Monitoring */
            synchronized (this) {
                event = availableEvents.poll();
                if (event == null) {
                   // System.out.println("Esperando;;;");
                    wait();
                }
            }

            if (event != null) {
                switch (event.getOriginType()) {
                    case Event.INTERATION_EVENT:

                        //InteractionEvent;
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    case Event.COMPONENT_EVENT:
                        //ContextEvent
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                }
                /* Execute */
                System.out.println("Event: " + event.toString());
                //System.out.println("Message: "+ event.getValue().toString() );
            }
        }
    }

    @Override
    public void onCreate() {
        try {
            if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                System.out.println("Activating: " + getClass());
            }
            this.adaptationDAO = super.getDeviceManager().getDataManager().getAdaptationDAO();
            this.adaptationDAO.populateAcls();
            // Carregar dados e configurações que serão utilizados para execução em memória
            // Preparar configurações inicias para execução
            if (this.discoveryAdapter == null) {
                this.discoveryAdapter = new UrboSentiDiscoveryAdapter();
            }
            // Para tanto utilizar o DataManager para acesso aos dados.
            // retornar todos os extados
        } catch (SQLException ex) {
            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setExternalDiscoveryAdapter(DiscoveryAdapter discoveryAdapter) {
        this.discoveryAdapter = discoveryAdapter;
    }

    private boolean isRegistredToReceiveMaximumUploadRates; // será colocado na interface de comunicação de saída

    protected void simplestAdaptationControlLoop() {

        EventModel eventModel;
        Plan plan;
        Diagnosis diagnosis;
        ArrayList<Action> actionList;
        InteractionModel interactionModel;
        Action action;
        Content content;
        FeedbackAnswer response = null;
        HashMap<String, Object> values;
        while (isRunning()) {
            Event event = null;
            diagnosis = new Diagnosis();
            plan = new Plan();
            action = null;
            try {

                /* Monitoring */
                synchronized (this) {
                    while (event == null) {
                        event = availableEvents.poll();
                        if (event == null) {
                            if (urbosenti.util.DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                                System.out.println("Esperando evento.");
                            }
                            wait();
                        }
                    }
                }
                /**
                 * ************** Update world model **************
                 */
                // verifica se é uma interação ou um evento
                // se uma interação extrai a interação dos parâmetros do evento
                // de forma semelhante ao evento salva as informações
                // gera uma interação que é passada para análise -- o mesmo ocorre com o evento
                // buscar o modelo de evento
                eventModel = super.getDeviceManager().getDataManager().getEventModelDAO()
                        .get(event.getId(), event.getEntityId(), event.getComponentManager().getComponentId());
                // se não encontrar o evento
                if (eventModel == null) {
                    // gera um evento de evento desconhecido ou não existente e adiciona os dados do evento não reconecido nos parâmetros
                    // salva ele com um warning (implementar no adaptação) -- evento não conhecido 
                } else {  // caso encontre
                    // adiciona o id o BD, se for necessário armazenar então usa o gerado pelo banco de dados
                    event.setDatabaseId(eventModel.getId());
                    // verificar se o modelo de evento precisa ser salvo
                    if (eventModel.isNecessaryStore()) {
                        // salvar se sim
                        super.getDeviceManager().getDataManager().getEventModelDAO()
                                .insert(event, eventModel);
                    }
                    // verificar cada parâmetro, se relacionado a um estado 
                    for (Parameter p : eventModel.getParameters()) {
                        // se relacionado verifica se é um estado de instância
                        if (p.getRelatedState() != null) {
                            // preenche o conteúdo
                            content = new Content(Content.parseContent(
                                    p.getRelatedState().getDataType(),
                                    event.getParameters().get(p.getLabel())),
                                    event.getTime());
                            // se for estado de instância acessa o parâmetro instanceId e salva na referida instância, senão salva no estado
                            if (p.getRelatedState().isStateInstance()) {
                                //--- Testar se e uma instancia ou uma InstanceRepresentative, se não for, indica erro, para ficar genérico
                                //--- colocar todos os rótulos de instância como instance
                                // Existem 3 instâncias - User, CommunicationInterface e PushServiceReceiver
                                // Também aceitar que o parâmetro instance tenha uma instância
                                if (event.getComponentManager().getComponentId() == CommunicationDAO.COMPONENT_ID) {
                                    if (event.getParameters().get("interface") instanceof CommunicationInterface) {
                                        for (State instanceState : ((CommunicationInterface) event.getParameters().get("interface")).getInstance().getStates()) {
                                            if (p.getRelatedState().getModelId() == instanceState.getModelId()) {
                                                instanceState.setContent(content);
                                                super.getDeviceManager().getDataManager().getInstanceDAO().insertContent(instanceState);
                                                break;
                                            }
                                        }
                                    } else if (event.getParameters().get("interface") instanceof PushServiceReceiver) {
                                        for (State instanceState : ((PushServiceReceiver) event.getParameters().get("interface")).getInstance().getStates()) {
                                            if (p.getRelatedState().getModelId() == instanceState.getModelId()) {
                                                instanceState.setContent(content);
                                                super.getDeviceManager().getDataManager().getInstanceDAO().insertContent(instanceState);
                                                break;
                                            }
                                        }
                                    }
                                } else if (event.getComponentManager().getComponentId() == UserDAO.COMPONENT_ID) {
                                    for (State instanceState : ((User) event.getParameters().get("user")).getInstance().getStates()) {
                                        if (p.getRelatedState().getModelId() == instanceState.getModelId()) {
                                            instanceState.setContent(content);
                                            super.getDeviceManager().getDataManager().getInstanceDAO().insertContent(instanceState);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                // se for salva como estado de entidade salva
                                p.getRelatedState().setContent(content);
                                super.getDeviceManager().getDataManager().getStateDAO().insertContent(p.getRelatedState());
                            }
                        }
                    }
                    if (Event.INTERATION_EVENT == event.getEventType()) {
                        // extract interaction
                        event = adaptationDAO.extractInteractionFromMessageEvent(event);
                        // get Interaction model
                        interactionModel = super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(event);
                        // verificar cada parâmetro, se relacionado a um estado 
                        for (Parameter p : interactionModel.getParameters()) {
                            // se relacionado verifica se é um estado de instância
                            if (p.getRelatedState() != null) {
                                // se for salva como estado de instância
                                p.getRelatedState().setContent(
                                        new Content(Content.parseContent(
                                                        p.getRelatedState().getDataType(),
                                                        event.getParameters().get(p.getLabel())),
                                                event.getTime()));

                                super.getDeviceManager().getDataManager().getAgentTypeDAO().insertContent(p.getRelatedState());
                            }
                        }
                    }
                }
                /**
                 * ************** Analisys Process **************
                 */
                switch (event.getOriginType()) {
                    case Event.INTERATION_EVENT:
                        //InteractionEvent;

                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    case Event.COMPONENT_EVENT:
                        /* Analysis -- Diagnosis */
                        if (event.getId() == DeviceManager.EVENT_DEVICE_SERVICES_INITIATED) {
                            // para cada serviço de upload, verificar se já estão registrados para receber atualizações do tempo de expiração dos relatos? Se não Adaptação
                            // implementar
                            // change=1;
                            diagnosis.addChange(1);
                            // inicializar contador de envio de relatos  de funcionamento ao servidor
                            // change=2;
                            diagnosis.addChange(2);
                            // iniciar varredura para exclusão de mensagens expiradas
                            // change=3;
                            diagnosis.addChange(3);
                        }
                        // último diagnóstico a ser executado
                        if (diagnosis.getChanges().isEmpty()) {
                            diagnosis.addChange(Diagnosis.DIAGNOSIS_NO_ADAPTATION_NEEDED);
                        }

                }
                /* Planning -- Plan */
                // verifica o diagnóstico para encontrar se necessita de adaptação
                if (diagnosis.getChanges().get(0) != Diagnosis.DIAGNOSIS_NO_ADAPTATION_NEEDED) {
                    // para cada mudança verificar a mudança
                    for (Integer change : diagnosis.getChanges()) {
                        // vou bucar a mudança na verdade no banco de dados -- todas as ações estáticas por enquanto
                        switch (change) {
                            case 1:
                                // interação de subscribe
                                interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(2));
                                interactionModel.setContentToParameter("address", deviceManager.getCommunicationManager().getMainPushServiceReceiver().getInterfaceConfigurations());
                                interactionModel.setContentToParameter("interface", deviceManager.getCommunicationManager().getMainPushServiceReceiver().getInstance().getDescription());// pegar a interface principal ativa
                                interactionModel.setContentToParameter("uid", deviceManager.getUID());
                                interactionModel.setContentToParameter("layer", "System");
                                Address target = new Address(deviceManager.getBackendService().getAddress());
                                target.setLayer(Address.LAYER_SYSTEM);
                                target.setUid(deviceManager.getBackendService().getServiceUID());
                                values = new HashMap<String, Object>();
                                values.put("target", target);
                                values.put("interactionModel", interactionModel);
                                action = new Action();
                                action.setId(2); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            default:
                                // evento de erro. Diagnóstico não conhecido
                                break;
                        }
                    }
                }
                /**
                 * ************** Execute Process **************
                 */
                System.out.println("Event: " + event.toString());
                //System.out.println("Message: "+ event.getValue().toString() );
                // existe alguma ação?
                if (plan.getExecutionPlans().size() > 0) {
                    response = null;
                    // para cada plano de execução
                    for (ExecutionPlan ep : plan.getExecutionPlans()) {
                        // para cada ação até condição de parada
                        for (Action actionToExecute : ep.getQueueOfActions()) {
                            // verificar se é interação
                            if (actionToExecute.getActionType() == Event.INTERATION_EVENT) {
                                // se sim, gerar uma mensagem no formato da linguagem e popular os parâmetros de envio.
                                actionToExecute = adaptationDAO.makeInteractionMessage(actionToExecute);
                            }
                            // encontrar o componente para envio, no caso de interação o Componente Communication
                            for (ComponentManager cm : this.deviceManager.getComponentManagers()) {
                                if (cm.getComponentId() == actionToExecute.getTargetComponentId()) {
                                    // aplicar a ação e pegar o feedback
                                    response = cm.applyAction(actionToExecute);
                                    // atualizar a decisão da ação
                                    this.adaptationDAO.updateDecision(response, event, actionToExecute, ep);
                                    break;
                                }
                            }
                            // componente não encontrado
                            if (response == null) {
                                throw new Exception("Target component id:" + actionToExecute.getTargetComponentId() + " was not found!");
                            }
                            // verificar condição de parada
                            if (ep.getStoppingCondition() == ExecutionPlan.STOPPING_CONDITION_UNTIL_SUCCESS) {
                                if (response.getId() == FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL) {
                                    break;
                                }
                            } else if (ep.getStoppingCondition() == ExecutionPlan.STOPPING_CONDITION_UNTIL_FAIL_OR_END) {
                                if (response.getId() == FeedbackAnswer.ACTION_RESULT_FAILED || response.getId() == FeedbackAnswer.ACTION_RESULT_FAILED_TIMEOUT) {
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                //EVENT_ADAPTATION_LOOP_ERROR; // Adicionar um evento depois
            } catch (InterruptedException ex) {
                Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                //EVENT_ADAPTATION_LOOP_ERROR; // Adicionar um evento depois
            } catch (Exception ex) { // outras excessões desconhecidas
                Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                //EVENT_ADAPTATION_LOOP_ERROR; // Adicionar um evento depois
            }
        }
    }
    
}
