/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.ReconnectionService;
import urbosenti.core.communication.UploadService;
import urbosenti.core.data.dao.AdaptationDAO;
import urbosenti.core.data.dao.CommunicationDAO;
import urbosenti.core.data.dao.EventDAO;
import urbosenti.core.data.dao.UserDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.InteractionModel;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.State;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemEvent;
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
    public static final int EVENT_UNKNOWN_EVENT_WARNING = 1;
    /**
     * int EVENT_ADAPTATION_LOOP_ERROR = 2; </ br>
     *
     * <ul><li>id: 2</li>
     * <li>evento: Erro no ciclo de adaptação</li>
     * <li>parâmetros: string do erro</li></ul>
     *
     */
    public static final int EVENT_ADAPTATION_LOOP_ERROR = 2;
    /**
     * int EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED = 3; </ br>
     *
     * <ul><li>id: 3</li>
     * <li>evento: nenhum</li>
     * <li>parâmetros: nenhum</li></ul>
     *
     */
    public static final int EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED = 3;
    /**
     * int EVENT_START_TASK_OF_CLEANING_REPORS = 4; </ br>
     *
     * <ul><li>id: 4</li>
     * <li>evento: nenhum</li>
     * <li>parâmetros: nenhum</li></ul>
     *
     */
    private static final int EVENT_START_TASK_OF_CLEANING_REPORS = 4;
    //private LocalKnowledge localKnowledge
    private Queue<Event> availableEvents;
    private ContextManager contextManager;
    private UserManager userManager;
    private boolean running;
    private Boolean flag;
    private Boolean monitor;
    private DiscoveryAdapter discoveryAdapter;
    private AdaptationDAO adaptationDAO;
    // private AdaptationLoopControler adaptationLoopControler;
    private boolean isAllowedReportingFunctionsToUploadService;
    private Long intervalBetweenReports;
    private Long intervalCleanReports;

    public AdaptationManager(DeviceManager deviceManager) {
        super(deviceManager, AdaptationDAO.COMPONENT_ID);
        this.contextManager = null;
        this.userManager = null;
        this.running = true;
        this.availableEvents = new LinkedList();
        this.flag = true;
        this.monitor = true;
        this.discoveryAdapter = null;
        this.adaptationDAO = null;
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
        //this.discovery(deviceManager);
        // discoveryAdapter.discovery(deviceManager);
        /* It begin the monitoring process of events */
        simplestAdaptationControlLoop();
//        try {
//            
//            monitoring();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
            // pegar diretamente do banco de dados depois de adicionado, por enquanto estático para testes
            this.intervalBetweenReports = 30000L;
            this.intervalCleanReports = 60000L;
            this.isAllowedReportingFunctionsToUploadService = true;
            // colocar dinâmico depois
//            this.intervalBetweenReports = Long.parseLong(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERVAL_TO_REPORT_SYSTEM_FUNCIONS).getCurrentValue().toString());
//            this.intervalCleanReports = Long.parseLong(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERNAL_TO_DELETE_EXPIRED_MESSAGES).getCurrentValue().toString());
//            this.isAllowedReportingFunctionsToUploadService = Boolean.parseBoolean(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_ALLOWED_SEND_REPORT_SYSTEM_FUNCIONS).getCurrentValue().toString());
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

    protected void simplestAdaptationControlLoop() {

        EventModel eventModel;
        Plan plan;
        Diagnosis diagnosis;
        ArrayList<Action> actionList;
        ArrayList<Action> actions;
        InteractionModel interactionModel;
        Action action;
        Content content;
        FeedbackAnswer response = null;
        HashMap<String, Object> values;
        ExecutionPlan executionPlan;
        Address target;
        State entityState;
        UploadService up = null;
        while (isRunning()) {
            Event event = null, generatedEvent;
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
                                    } else if (event.getParameters().get("reconnectionService") instanceof ReconnectionService) {
                                        for (State instanceState : ((PushServiceReceiver) event.getParameters().get("reconnectionService")).getInstance().getStates()) {
                                            if (p.getRelatedState().getModelId() == instanceState.getModelId()) {
                                                instanceState.setContent(content);
                                                super.getDeviceManager().getDataManager().getInstanceDAO().insertContent(instanceState);
                                                break;
                                            }
                                        }
                                    } else if (event.getParameters().get("uploadService") instanceof UploadService) {
                                        for (State instanceState : ((PushServiceReceiver) event.getParameters().get("uploadService")).getInstance().getStates()) {
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
                        if (event.getId() == AdaptationDAO.INTERACTION_TO_INFORM_NEW_MAXIMUM_UPLOAD_RATE) {
                            // verifica se tem permitido alterar
                            if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_REPORTS_POLICY) == 4) {
                                for (UploadService uploadService : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                    // procura o serviço
                                    if (uploadService.getService().getServiceUID().equals(event.getParameters().get("uid").toString())) {
                                        // verifica se o valor é diferente do anterior
                                        if (uploadService.getUploadRate() != Double.parseDouble(event.getParameters().get("uploadRate").toString())) {
                                            // se sim
                                            diagnosis.addChange(5);
                                            up = uploadService;
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (event.getId() == AdaptationDAO.INTERACTION_OF_FAIL_ON_SUBSCRIBE) { // Falha ao assinar
                            // não necessário agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_OF_MESSAGE_WONT_UNDERSTOOD) { // Mensagem não entendida
                            // não necessário agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_CONFIRM_REGISTRATION) { // Assinatura aceita
                            for (UploadService uploadService : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                // procura o serviço
                                if (uploadService.getService().getServiceUID().equals(event.getParameters().get("uid").toString())) {
                                    // se encontrar plano estático para alteração
                                    diagnosis.addChange(6);
                                    up = uploadService;
                                    break;
                                }
                            }
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_REFUSE_REGISTRATION) { // Assinatura recusada
                            // não necessário agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_CANCEL_REGISTRATION) { // Assinatura cancelada
                            // não necessáro agora
                        }
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    case Event.COMPONENT_EVENT:
                        /* Analysis -- Diagnosis */
                        if (event.getComponentManager().getComponentId() == DeviceManager.DEVICE_COMPONENT_ID) {
                            if (event.getId() == DeviceManager.EVENT_DEVICE_SERVICES_INITIATED) {
                                // para cada serviço de upload, verificar se já estão registrados para receber atualizações do tempo de expiração dos relatos? Se não Adaptação
                                // change=1;
                                // testa se a política do serviço de upload é 4 = adaptativa, se não for não inicia isso
                                if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_REPORTS_POLICY) == 4) {
                                    // busca os serviços de upload, verifica se eles estão registrados para receber uploads
                                    for (UploadService service : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                        if (!service.isSubscribedMaximumUploadRate()) {
                                            diagnosis.addChange(1);
                                            break;
                                        }
                                    }
                                }
                                // inicializar contador de envio de relatos  de funcionamento ao servidor
                                // change=2;
                                // verificar se é permitido
                                if (isAllowedReportingFunctionsToUploadService) {
                                    diagnosis.addChange(2);
                                }
                                // iniciar varredura para exclusão de mensagens expiradas
                                // change=3;
                                // se a política de armazenamento for 4 faz isso, senão não
                                if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MESSAGE_STORAGE_POLICY) == 4) {
                                    diagnosis.addChange(3);
                                }
                            }
                        } else if (event.getComponentManager().getComponentId() == DeviceManager.COMMUNICATION_COMPONENT_ID) {
                            if (event.getId() == CommunicationManager.EVENT_NEW_INPUT_COMMUNICATION_INTERFACE_ADDRESS) {
                                // busca estado anterior. Se for o mesmo não envia.
                                content = getDeviceManager().getDataManager().getInstanceDAO().getBeforeCurrentContentValue(
                                        CommunicationDAO.STATE_ID_OF_INPUT_COMMUNICATION_INTERFACE_CONFIGURATIONS,
                                        ((PushServiceReceiver) event.getParameters().get("interface")).getInstance().getModelId(),
                                        CommunicationDAO.ENTITY_ID_OF_INPUT_COMMUNICATION_INTERFACES,
                                        CommunicationDAO.COMPONENT_ID);
                                // se forem iguais não há necessidade de atualizar
                                if (!content.getValue().toString().equals(event.getParameters().get("configurations").toString())) {
                                    // enviar novo endereço
                                    diagnosis.addChange(4);
                                }
                            }
                        } else if (event.getComponentManager().getComponentId() == DeviceManager.EVENTS_COMPONENT_ID) {

                        } else if (event.getComponentManager().getComponentId() == DeviceManager.USER_COMPONENT_ID) {

                        } else if (event.getComponentManager().getComponentId() == DeviceManager.ADAPTATION_COMPONENT_ID) {

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
                            case 1: // Subscribe no servidor
                                // interação de subscribe -- fazer para cada uploadService
                                interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(2));
                                interactionModel.setContentToParameter("address", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInterfaceConfigurations().toString());
                                interactionModel.setContentToParameter("interface", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInstance().getDescription());// pegar a interface principal ativa
                                interactionModel.setContentToParameter("uid", getDeviceManager().getBackendService().getApplicationUID());
                                interactionModel.setContentToParameter("layer", "System");
                                target = new Address(getDeviceManager().getBackendService().getAddress());
                                target.setLayer(Address.LAYER_SYSTEM);
                                target.setUid(getDeviceManager().getBackendService().getServiceUID());
                                values = new HashMap<String, Object>();
                                values.put("target", target);
                                values.put("interactionModel", interactionModel);
                                values.put("address", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInterfaceConfigurations());
                                values.put("interface", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInstance().getDescription());
                                values.put("uid", getDeviceManager().getBackendService().getApplicationUID());
                                values.put("layer", "System");
                                action = new Action();
                                action.setId(2); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                //action.setSynchronous(true);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
                                break;
                            case 2: // inícializar o contador de envio de relatórios de funcionamento ao servidor
                                generatedEvent = new SystemEvent(this);
                                generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                generatedEvent.setId(EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED);
                                values = new HashMap<String, Object>();
                                values.put("event", generatedEvent);
                                values.put("time", this.intervalBetweenReports); // 
                                values.put("date", new Date());
                                values.put("method", EventManager.METHOD_DATE_PLUS_REPEATED_INTERVALS);
                                values.put("origin", this);
                                action = new Action();
                                action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
                                action.setTargetComponentId(EventDAO.COMPONENT_ID);
                                action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
                                action.setParameters(values);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
                                break;
                            case 3:
                                generatedEvent = new SystemEvent(this);
                                generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                generatedEvent.setId(EVENT_START_TASK_OF_CLEANING_REPORS);
                                values = new HashMap<String, Object>();
                                values.put("event", generatedEvent);
                                values.put("time", this.intervalCleanReports); // 
                                values.put("date", new Date());
                                values.put("method", EventManager.METHOD_DATE_PLUS_REPEATED_INTERVALS);
                                values.put("origin", this);
                                action = new Action();
                                action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
                                action.setTargetComponentId(EventDAO.COMPONENT_ID);
                                action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
                                action.setParameters(values);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
                                break;
                            case 4: // atualizar no servidor o novo endereço
                                // fazer interação
                                interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(9));
                                interactionModel.setContentToParameter("address", event.getParameters().get("address"));
                                interactionModel.setContentToParameter("interface", ((PushServiceReceiver) event.getParameters().get("interface")).getDescription());
                                interactionModel.setContentToParameter("uid", getDeviceManager().getBackendService().getApplicationUID());
                                target = new Address(getDeviceManager().getBackendService().getAddress());
                                target.setLayer(Address.LAYER_SYSTEM);
                                target.setUid(getDeviceManager().getBackendService().getServiceUID());
                                values = new HashMap<String, Object>();
                                values.put("target", target);
                                values.put("interactionModel", interactionModel);
                                action = new Action();
                                action.setId(2); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                //action.setSynchronous(false);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
                                break;
                            case 5: // atualiza taxa de upload do serviço
                                //09	Alterar taxa de upload	Nova Taxa	double	entre 1 e 0	uploadRate
                                //      Id da instância	inteito 	int	instanceId
                                values = new HashMap<String, Object>();
                                values.put("uploadRate", event.getParameters().get("uploadRate"));
                                values.put("instanceId", up.getInstance().getModelId());
                                action = new Action();
                                action.setId(9); // atualizar taxa de upload
                                action.setParameters(values);
                                action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS);
                                action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
                                break;
                            case 6: // atualiza taxa de upload do serviço
                                //09	Alterar taxa de upload	Nova Taxa	double	entre 1 e 0	uploadRate
                                //      Id da instância	inteito 	int	instanceId
                                values = new HashMap<String, Object>();
                                values.put("value", true);
                                values.put("instanceId", up.getInstance().getModelId());
                                action = new Action();
                                action.setId(CommunicationManager.ACTION_UPDATE_UPLOAD_SERVICE_SUBSCRIBED_MAXIMUM_UPLOAD_RATE); // atualizar taxa de upload
                                action.setParameters(values);
                                action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS);
                                action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
                                actions = new ArrayList<Action>();
                                actions.add(action);
                                executionPlan = new ExecutionPlan(actions);
                                plan.addExecutionPlan(executionPlan);
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
                            for (ComponentManager cm : getDeviceManager().getComponentManagers()) {
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
