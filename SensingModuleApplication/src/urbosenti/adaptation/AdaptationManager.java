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
import urbosenti.adaptation.models.ECADiagnosisModel;
import urbosenti.adaptation.models.StaticPlanningModel;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.ReconnectionService;
import urbosenti.core.communication.UploadService;
import urbosenti.core.data.dao.AdaptationDAO;
import urbosenti.core.data.dao.CommunicationDAO;
import urbosenti.core.data.dao.DeviceDAO;
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
     * <li>par�metros: string do evento</li></ul>
     *
     */
    public static final int EVENT_UNKNOWN_EVENT_WARNING = 1;
    /**
     * int EVENT_ADAPTATION_LOOP_ERROR = 2; </ br>
     *
     * <ul><li>id: 2</li>
     * <li>evento: Erro no ciclo de adapta��o</li>
     * <li>par�metros: string do erro</li></ul>
     *
     */
    public static final int EVENT_ADAPTATION_LOOP_ERROR = 2;
    /**
     * int EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED = 3; <br>
     *
     * <ul><li>id: 3</li>
     * <li>evento: Iniciar tarefa para Gerar relat�rios de funcionamento</li>
     * <li>par�metros: nenhum</li></ul>
     *
     */
    public static final int EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED = 3;
    /**
     * int EVENT_START_TASK_OF_CLEANING_REPORS = 4; </ br>
     *
     * <ul><li>id: 4</li>
     * <li>evento: Iniciar tarefa para limpar mensagens expiradas</li>
     * <li>par�metros: nenhum</li></ul>
     *
     */
    public static final int EVENT_START_TASK_OF_CLEANING_REPORS = 4;
    /**
     * int EVENT_START_TASK_OF_CHECKING_SERVICE_ERRORS = 5; </ br>
     *
     * <ul><li>id: 5</li>
     * <li>evento: Iniciar tarefa para verificar erros nos servi�os </li>
     * <li>par�metros: nenhum</li></ul>
     *
     */
    public static final int EVENT_START_TASK_OF_CHECKING_SERVICE_ERRORS = 5;
    /**
     * int EVENT_SYSTEM_REPORT_GENERATED = 6; </ br>
     *
     * <ul><li>id: 6</li>
     * <li>evento: Relat�rio de funcionamento gerado</li>
     * <li>par�metros: Relat�rio em JSON(report); Data da
     * gera��o(time)</li></ul>
     *
     */
    public static final int EVENT_SYSTEM_REPORT_GENERATED = 6;
    /*
     *********************************************************************
     ***************************** Actions ******************************* 
     *********************************************************************
     */
    /**
     * int ACTION_UPDATE_PERMISSION_TO_FUNCTIONALITY_REPORT = 1;
     *
     * <ul><li>id: 1</li>
     * <li>a��o: Alterar permiss�o para envio de relat�rios de
     * funcionamento</li>
     * <li>par�metros: novo valor (value)</li></ul>
     *
     */
    public static final int ACTION_UPDATE_PERMISSION_TO_FUNCTIONALITY_REPORT = 1;
    /**
     * int ACTION_STORE_INTERNAL_ERROR = 2;
     *
     * <ul><li>id: 2</li>
     * <li>a��o: Armazenar evento de erro interno para relato</li>
     * <li>par�metros: tipo de erro (type), descri��o (description), id da
     * instancia (instanceId) e tempo (time)</li></ul>
     *
     */
    public static final int ACTION_STORE_INTERNAL_ERROR = 2;
    /**
     * int ACTION_SEND_FUNCTIONALITY_REPORT = 3;
     *
     * <ul><li>id: 3</li>
     * <li>a��o: Enviar relat�rio de funcionamento</li>
     * <li>par�metros: Relat�rio em JSON (report); id do servi�o
     * (report)</li></ul>
     *
     */
    public static final int ACTION_REMOVE_SENT_FUNCTIONALITY_REPORTS = 3;
    /**
     * int ACTION_GENERATE_EVENT_ERROR_REPORTING = 4;
     * <ul><li>id: 4</li>
     * <li>a��o: Gerar Relat�rio de Funcionamento</li>
     * <li>par�metros: data final (time)</li></ul>
     *
     */
    public static final int ACTION_GENERATE_EVENT_ERROR_REPORTING = 4;
    /**
     * int ACTION_UPDATE_LAST_SENT_REPORT_DATE = 5;
     * <ul><li>id: 5</li>
     * <li>a��o: Atualizar �ltima data de envio de relat�rio</li>
     * <li>par�metros: data (time)</li></ul>
     *
     */
    public static final int ACTION_UPDATE_LAST_SENT_REPORT_DATE = 5;
    //private LocalKnowledge localKnowledge
    private final Queue<Event> availableEvents;
    private ContextManager contextManager;
    private UserManager userManager;
    private boolean running;
    private Boolean flag;
    private Boolean monitor;
    private AdaptationDAO adaptationDAO;
    // private AdaptationLoopControler adaptationLoopControler;
    private boolean isAllowedReportingFunctionsToUploadService;
    private Long intervalAmongModuleStateReports;
    private Long intervalCleanStoredMessages;
    private long scanIntervalOfServiceErrors;
    private long limitIntervalToUploadService;
    private long limitIntervalToUploadReconnectionService;
    private Date lastReportedDate;
    private AbstractDiagnosisModel diagnosisModel;
    private AbstractPlanningModel planningModel;

    public AdaptationManager(DeviceManager deviceManager) {
        super(deviceManager, AdaptationDAO.COMPONENT_ID);
        this.contextManager = null;
        this.userManager = null;
        this.running = true;
        this.availableEvents = new LinkedList();
        this.flag = true;
        this.monitor = true;
        this.adaptationDAO = null;
        this.diagnosisModel = null;
        this.planningModel = null;
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
        //simplestAdaptationControlLoop();
        adaptationControlLoop();
//        try {
//            
//            monitoring();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void onCreate() {
        try {
            if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                System.out.println("Activating: " + getClass());
            }
            this.adaptationDAO = super.getDeviceManager().getDataManager().getAdaptationDAO();
           
            // Carregar dados e configura��es que ser�o utilizados para execu��o em mem�ria
            this.adaptationDAO.loadStructureModels();
            // carrega o modelo de diagn�stico
            if(this.diagnosisModel==null){
                this.diagnosisModel = new ECADiagnosisModel(this.adaptationDAO, getDeviceManager());
            }
            if(this.planningModel==null){
                this.planningModel = new StaticPlanningModel(adaptationDAO, getDeviceManager());
            }
            // pegar diretamente do banco de dados depois de adicionado, por enquanto est�tico para testes
            this.intervalAmongModuleStateReports = 43200000L; // cada 12 horas
            this.intervalCleanStoredMessages = 3600000L; // cada hora
            this.scanIntervalOfServiceErrors = 60000L; // cada 1 minuto
            this.isAllowedReportingFunctionsToUploadService = true;
            this.limitIntervalToUploadService = 5000L;
            this.limitIntervalToUploadReconnectionService = 5000L;
            Object object = super.getDeviceManager().getDataManager().getEntityStateDAO()
                    .getEntityState(AdaptationDAO.COMPONENT_ID,
                            AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT,
                            AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_LAST_REPORTED_DATE)
                    .getCurrentValue();
            if (object==null || object.equals("null")) {
                this.lastReportedDate = new Date(0L);
            } else {
                this.lastReportedDate = (Date) object;
            }
            // colocar din�mico depois
//            this.intervalAmongModuleStateReports = Long.parseLong(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERVAL_TO_REPORT_SYSTEM_FUNCIONS).getCurrentValue().toString());
//            this.intervalCleanStoredMessages = Long.parseLong(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERNAL_TO_DELETE_EXPIRED_MESSAGES).getCurrentValue().toString());
//            this.isAllowedReportingFunctionsToUploadService = Boolean.parseBoolean(super.getDeviceManager().getDataManager().getEntityStateDAO()
//                    .getEntityState(AdaptationDAO.COMPONENT_ID, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT, AdaptationDAO.STATE_ID_OF_ADAPTATION_MANAGEMENT_ALLOWED_SEND_REPORT_SYSTEM_FUNCIONS).getCurrentValue().toString());
            // Para tanto utilizar o DataManager para acesso aos dados.
            // retornar todos os extados
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new Error(ex);
        }
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        HashMap<String, Object> values;
        Event event;
        String json;
        FeedbackAnswer answer = null;
        switch (action.getId()) {
            case ACTION_GENERATE_EVENT_ERROR_REPORTING:
                try {
                    json = this.adaptationDAO
                            .getErrorReporting(this.lastReportedDate, (Date) action.getParameters().get("time"));
                    values = new HashMap<String, Object>();
                    values.put("report", json);
                    values.put("time", action.getParameters().get("time"));
                    event = new SystemEvent(this);
                    event.setName("System Report Generated");
                    event.setId(AdaptationManager.EVENT_SYSTEM_REPORT_GENERATED);
                    event.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                    event.setParameters(values);
                    this.newEvent(event);
                } catch (SQLException ex) {
                    if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                        Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // retorno erro
                    answer = new FeedbackAnswer(FeedbackAnswer.ACTION_RESULT_FAILED, ex.toString());
                }
                break;
            case ACTION_REMOVE_SENT_FUNCTIONALITY_REPORTS:
                try {
                    // fazer ainda. Mais tarde
                    adaptationDAO.removeSentReportedErrors((Date) action.getParameters().get("time"));
                } catch (Exception ex) {
                    if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                        Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // retorno erro
                    answer = new FeedbackAnswer(FeedbackAnswer.ACTION_RESULT_FAILED, ex.toString());
                }
                break;
            case ACTION_STORE_INTERNAL_ERROR:
                action.getParameters().get("type");
                action.getParameters().get("description");
                action.getParameters().get("time");
                action.getParameters().get("instanceId");
                // armazena na a��o
                break;
            case ACTION_UPDATE_PERMISSION_TO_FUNCTIONALITY_REPORT:
                this.isAllowedReportingFunctionsToUploadService = (Boolean) action.getParameters().get("value");
                break;
            case ACTION_UPDATE_LAST_SENT_REPORT_DATE:
                this.lastReportedDate = (Date) action.getParameters().get("time");
                break;
            default:
                answer = new FeedbackAnswer(FeedbackAnswer.ACTION_DOES_NOT_EXIST);
                break;
        }
        // verifica se a a��o existe ou se houve algum resultado durante a execu��o
        if (action.getId() >= 1 && action.getId() <= 5) {
            answer = new FeedbackAnswer(FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL);
        }
        return answer;
    }

    protected void simplestAdaptationControlLoop() {

        EventModel eventModel;
        Event errorEvent;
        Plan plan;
        Diagnosis diagnosis;
        ArrayList<Action> actions;
        InteractionModel interactionModel;
        Action action;
        Content content;
        FeedbackAnswer response = null;
        HashMap<String, Object> values;
        ExecutionPlan executionPlan;
        Address target;
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
                if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                    System.out.println("Event: " + event.toString());
                }
                /**
                 * ************** Update world model **************
                 */
                // verifica se � uma intera��o ou um evento
                // se uma intera��o extrai a intera��o dos par�metros do evento
                // de forma semelhante ao evento salva as informa��es
                // gera uma intera��o que � passada para an�lise -- o mesmo ocorre com o evento
                // buscar o modelo de evento
                eventModel = super.getDeviceManager().getDataManager().getEventModelDAO()
                        .get(event.getId(), event.getEntityId(), event.getComponentManager().getComponentId());
                // se n�o encontrar o evento
                if (eventModel == null) {
                    // gera um evento de evento desconhecido ou n�o existente e adiciona os dados do evento n�o reconecido nos par�metros
                    // salva ele com um warning (implementar no adapta��o) -- evento n�o conhecido 
                    values = new HashMap<String, Object>();
                    values.put("error", "Evento n�o conhecido: " + event.toString());
                    errorEvent = new SystemEvent(this);
                    errorEvent.setName("Unknown event");
                    errorEvent.setId(AdaptationManager.EVENT_UNKNOWN_EVENT_WARNING);
                    errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                    errorEvent.setParameters(values);
                    this.newEvent(errorEvent);
                    throw new NullPointerException("Evento " + event.getId() + " " + event.getName() + " " + event.toString() + "n�o existe no banco de dados!");
                } else {  // caso encontre
                    // adiciona o id o BD, se for necess�rio armazenar ent�o usa o gerado pelo banco de dados
                    event.setDatabaseId(eventModel.getId());
                    // verificar se o modelo de evento precisa ser salvo
                    //if (eventModel.isNecessaryStore()) {
                    // salvar se sim
                    super.getDeviceManager().getDataManager().getEventModelDAO()
                            .insert(event, eventModel);
                    //}
                    // verificar cada par�metro, se relacionado a um estado 
                    for (Parameter p : eventModel.getParameters()) {
                        // se relacionado verifica se � um estado de inst�ncia
                        if (p.getRelatedState() != null) {
                            // preenche o conte�do
                            content = new Content(Content.parseContent(
                                    p.getRelatedState().getDataType(),
                                    event.getParameters().get(p.getLabel())),
                                    event.getTime());
                            // se for estado de inst�ncia acessa o par�metro instanceId e salva na referida inst�ncia, sen�o salva no estado
                            if (p.getRelatedState().isStateInstance()) {
                                //--- Testar se e uma instancia ou uma InstanceRepresentative, se n�o for, indica erro, para ficar gen�rico
                                //--- colocar todos os r�tulos de inst�ncia como instance
                                // Existem 3 inst�ncias - User, CommunicationInterface e PushServiceReceiver
                                // Tamb�m aceitar que o par�metro instance tenha uma inst�ncia
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
                                        for (State instanceState : ((ReconnectionService) event.getParameters().get("reconnectionService")).getInstance().getStates()) {
                                            if (p.getRelatedState().getModelId() == instanceState.getModelId()) {
                                                instanceState.setContent(content);
                                                super.getDeviceManager().getDataManager().getInstanceDAO().insertContent(instanceState);
                                                break;
                                            }
                                        }
                                    } else if (event.getParameters().get("uploadService") instanceof UploadService) {
                                        for (State instanceState : ((UploadService) event.getParameters().get("uploadService")).getInstance().getStates()) {
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
                        // verificar cada par�metro, se relacionado a um estado 
                        for (Parameter p : interactionModel.getParameters()) {
                            // se relacionado verifica se � um estado de inst�ncia
                            if (p.getRelatedState() != null) {
                                // se for salva como estado de inst�ncia
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

                switch (event.getEventType()) {
                    /*
                     *************************************************************
                     *************************************************************
                     ********************** Interaction Events *********************
                     *************************************************************
                     *************************************************************
                     */
                    case Event.INTERATION_EVENT:
                        //InteractionEvent;
                        if (event.getId() == AdaptationDAO.INTERACTION_TO_INFORM_NEW_MAXIMUM_UPLOAD_RATE) {
                            // verifica se tem permitido alterar
                            if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_REPORTS_POLICY) == 4) {
                                for (UploadService uploadService : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                    // procura o servi�o
                                    if (uploadService.getService().getServiceUID().equals(event.getParameters().get("uid").toString())) {
                                        // verifica se o valor � diferente do anterior
                                        if (uploadService.getUploadRate() != Double.parseDouble(event.getParameters().get("uploadRate").toString())) {
                                            // se sim
                                            values = new HashMap<String, Object>();
                                            values.put("uploadRate", event.getParameters().get("uploadRate"));  // Alterar taxa de upload	Nova Taxa	double	entre 1 e 0	uploadRate
                                            values.put("instanceId", uploadService.getInstance().getModelId());            // Id da inst�ncia	inteito 	int	instanceId
                                            diagnosis.addChange(new Change(5, values));
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (event.getId() == AdaptationDAO.INTERACTION_OF_FAIL_ON_SUBSCRIBE) { // Falha ao assinar
                            // n�o necess�rio agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_OF_MESSAGE_WONT_UNDERSTOOD) { // Mensagem n�o entendida
                            // n�o necess�rio agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_CONFIRM_REGISTRATION) { // Assinatura aceita
                            for (UploadService uploadService : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                // procura o servi�o
                                if (uploadService.getService().getServiceUID().equals(event.getParameters().get("uid").toString())) {
                                    // se encontrar plano est�tico para altera��o
                                    values = new HashMap<String, Object>();
                                    values.put("value", true); //	Alterar taxa de upload	Nova Taxa	double	entre 1 e 0	uploadRate
                                    values.put("instanceId", uploadService.getInstance().getModelId()); //  Id da inst�ncia	inteito 	int	instanceId
                                    diagnosis.addChange(new Change(6, values));
                                    break;
                                }
                            }
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_REFUSE_REGISTRATION) { // Assinatura recusada
                            // n�o necess�rio agora
                        } else if (event.getId() == AdaptationDAO.INTERACTION_TO_CANCEL_REGISTRATION) { // Assinatura cancelada
                            // n�o necess�ro agora
                        }
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    /*
                     *************************************************************
                     *************************************************************
                     ********************** Component Events *********************
                     *************************************************************
                     *************************************************************
                     */
                    case Event.COMPONENT_EVENT:
                        /* Analysis -- Diagnosis */
                        if (event.getComponentManager().getComponentId() == DeviceManager.DEVICE_COMPONENT_ID) {
                            if (event.getId() == DeviceManager.EVENT_DEVICE_SERVICES_INITIATED) {
                                // para cada servi�o de upload, verificar se j� est�o registrados para receber atualiza��es do tempo de expira��o dos relatos? Se n�o Adapta��o
                                // change=1;
                                // testa se a política do servi�o de upload � 4 = adaptativa, se n�o for n�o inicia isso
                                if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_REPORTS_POLICY) == 4) {
                                    // busca os servi�os de upload, verifica se eles est�o registrados para receber uploads
                                    for (UploadService service : getDeviceManager().getCommunicationManager().getUploadServices()) {
                                        if (!service.isSubscribedMaximumUploadRate()) {
                                            // intera��o de subscribe -- fazer para cada uploadService
                                            interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(AdaptationDAO.INTERACTION_TO_SUBSCRIBE_THE_MAXIMUM_UPLOAD_RATE));
                                            interactionModel.setContentToParameter("address", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInterfaceConfigurations().toString());
                                            interactionModel.setContentToParameter("interface", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInstance().getDescription());// pegar a interface principal ativa
                                            //interactionModel.setContentToParameter("uid", getDeviceManager().getBackendService().getApplicationUID());
                                            interactionModel.setContentToParameter("uid", service.getService().getApplicationUID());
                                            interactionModel.setContentToParameter("layer", "System");
                                            // est�tico
                                            // target = new Address(getDeviceManager().getBackendService().getAddress());
                                            // target.setLayer(Address.LAYER_SYSTEM);
                                            // target.setUid(getDeviceManager().getBackendService().getServiceUID());
                                            // din�mico
                                            target = new Address(service.getService().getAddress());
                                            target.setLayer(Address.LAYER_SYSTEM);
                                            target.setUid(service.getService().getServiceUID());
                                            values = new HashMap<String, Object>();
                                            values.put("target", target);
                                            values.put("interactionModel", interactionModel);
                                            values.put("address", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInterfaceConfigurations());
                                            values.put("interface", getDeviceManager().getCommunicationManager().getMainPushServiceReceiver().getInstance().getDescription());
                                            values.put("uid", service.getService().getApplicationUID());
                                            values.put("layer", "System");
                                            diagnosis.addChange(new Change(1, values));
                                            break;
                                        }
                                    }
                                }

                                // verificar se � permitido inicializar contador de envio de relatos  de funcionamento ao servidor
                                if (isAllowedReportingFunctionsToUploadService) {
                                    // inicializar contador de envio de relatos  de funcionamento ao servidor
                                    generatedEvent = new SystemEvent(this);
                                    generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                    generatedEvent.setName("Gatilho de relat�rios do sistema ativado");
                                    generatedEvent.setId(EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED);
                                    values = new HashMap<String, Object>();
                                    values.put("event", generatedEvent);
                                    values.put("time", this.intervalAmongModuleStateReports); // 
                                    values.put("date", new Date());
                                    values.put("method", EventManager.METHOD_DATE_PLUS_REPEATED_INTERVALS);
                                    values.put("handler", this);
                                    diagnosis.addChange(new Change(2, values));
                                }
                                // iniciar varredura para exclus�o de mensagens expiradas
                                // se a política de armazenamento for 4 faz isso, sen�o n�o
                                if (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MESSAGE_STORAGE_POLICY) == 4) {
                                    // iniciar varredura para exclus�o de mensagens expiradas
                                    generatedEvent = new SystemEvent(this);
                                    generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                    generatedEvent.setName("Gatilho para exclus�o de mensagens expiradas ativado");
                                    generatedEvent.setId(EVENT_START_TASK_OF_CLEANING_REPORS);
                                    values = new HashMap<String, Object>();
                                    values.put("event", generatedEvent);
                                    values.put("time", this.intervalCleanStoredMessages); // 
                                    values.put("date", new Date());
                                    values.put("method", EventManager.METHOD_DATE_PLUS_REPEATED_INTERVALS);
                                    values.put("handler", this);
                                    diagnosis.addChange(new Change(3, values));
                                }
                                // Inicia tarefa de varredura de erros em servi�os usando intervalo fixo (usado pelo caso 3);
                                generatedEvent = new SystemEvent(this);
                                generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                generatedEvent.setName("Gatilho para varredura de erros em servi�os ativado");
                                generatedEvent.setId(EVENT_START_TASK_OF_CHECKING_SERVICE_ERRORS);
                                values = new HashMap<String, Object>();
                                values.put("event", generatedEvent);
                                values.put("time", this.scanIntervalOfServiceErrors); // 
                                values.put("date", new Date());
                                values.put("method", EventManager.METHOD_DATE_PLUS_REPEATED_INTERVALS);
                                values.put("handler", this);
                                diagnosis.addChange(new Change(7, values));
                            }
                        } else if (event.getComponentManager().getComponentId() == DeviceManager.COMMUNICATION_COMPONENT_ID) {
                            if (event.getId() == CommunicationManager.EVENT_NEW_INPUT_COMMUNICATION_INTERFACE_ADDRESS) {
                                // busca estado anterior. Se for o mesmo n�o envia.
                                content = getDeviceManager().getDataManager().getInstanceDAO().getBeforeCurrentContentValue(
                                        CommunicationDAO.STATE_ID_OF_INPUT_COMMUNICATION_INTERFACE_CONFIGURATIONS,
                                        ((PushServiceReceiver) event.getParameters().get("interface")).getInstance().getModelId(),
                                        CommunicationDAO.ENTITY_ID_OF_INPUT_COMMUNICATION_INTERFACES,
                                        CommunicationDAO.COMPONENT_ID);
                                // se forem iguais n�o h� necessidade de atualizar
                                if (!content.getValue().toString().equals(event.getParameters().get("configurations").toString())) {
                                    // enviar novo endere�o ao backend
                                    // fazer intera��o
                                    interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(AdaptationDAO.INTERACTION_TO_INFORM_NEW_INPUT_ADDRESS));
                                    interactionModel.setContentToParameter("address", event.getParameters().get("address"));
                                    interactionModel.setContentToParameter("interface", ((PushServiceReceiver) event.getParameters().get("interface")).getDescription());
                                    interactionModel.setContentToParameter("uid", getDeviceManager().getBackendService().getApplicationUID());
                                    target = new Address(getDeviceManager().getBackendService().getAddress());
                                    target.setLayer(Address.LAYER_SYSTEM);
                                    target.setUid(getDeviceManager().getBackendService().getServiceUID());
                                    values = new HashMap<String, Object>();
                                    values.put("target", target);
                                    values.put("interactionModel", interactionModel);
                                    // adicionar a mudan�a
                                    diagnosis.addChange(new Change(4, values));
                                }
                            }
                        } else if (event.getComponentManager().getComponentId() == DeviceManager.EVENTS_COMPONENT_ID) {
                            if (event.getId() == EventManager.EVENT_TIME_TRIGGER_ACHIEVED) {
                                // apenas adiciona na fila: "event"
                                generatedEvent = (Event) event.getParameters().get("event");
                                generatedEvent.newTime();
                                this.newEvent(generatedEvent);
                                // Futuramente fazer um teste se precisa cancelar: values.put("trigger")
                            }
                        } else if (event.getComponentManager().getComponentId() == DeviceManager.USER_COMPONENT_ID) {

                        } else if (event.getComponentManager().getComponentId() == DeviceManager.ADAPTATION_COMPONENT_ID) {
                            // Iniciar varredura de erros nos servi�os
                            if (event.getId() == AdaptationManager.EVENT_START_TASK_OF_CHECKING_SERVICE_ERRORS) {
                                // servi�o de upload
                                up = getDeviceManager().getCommunicationManager().getUploadServices().get(0);
                                // �ltima vez que esse servi�o de upload enviou uma mensagem
                                Content c = getDeviceManager().getDataManager().getEventModelDAO()
                                        .getLastEventContentByLabelAndValue(up.getInstance().getId(), "uploadServiceId",
                                                CommunicationManager.EVENT_MESSAGE_DELIVERED, CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS,
                                                CommunicationDAO.COMPONENT_ID);
                                Date lastSentMessageByServiceUpload = (c == null) ? new Date(0L) : c.getTime();
                                /* Se servi�o de upload possui mensagens para enviar, possui conex�o e 
                                 n�o enviar a mensagem nem um intervalo definido pelo timeout da interface de 
                                 conex�o utilizada mais um valor de toler�ncia em milissegundos e politica de uso de 
                                 dados m�veis n�o permite o uso de dados m�veis e esta interface conectada. */
                                /*
                                 System.out.println("Checando erros do sersi�o de upload: ");
                                 System.out.println("N�o Desconectado? " + !getDeviceManager().getCommunicationManager().isCompletelyDisconnected());
                                 System.out.println("Relatos no banco de dados: " + getDeviceManager().getDataManager().getReportDAO().reportsCount(up.getService()));
                                 System.out.println("N�o Utiliza dados m�veis: " + !getDeviceManager().getCommunicationManager().getCurrentCommunicationInterface().isUsesMobileData());
                                 System.out.println("Timeout + intervalo limite: " + getDeviceManager().getCommunicationManager().getCurrentCommunicationInterface().getTimeout() + limitIntervalToUploadService);
                                 System.out.println("�ltimo relato enviado: " + (System.currentTimeMillis() - lastSentMessageByServiceUpload.getTime()));
                                 System.out.println("�ltimo erro da inst�ncia: " + adaptationDAO.getLastRecordedErrorFromInstance(up.getInstance().getId(), scanIntervalOfServiceErrors));
                                 */
                                if (up.isAllowedToPerformUpload()
                                        && !getDeviceManager().getCommunicationManager().isCompletelyDisconnected() // n�o est� desconectado
                                        && (getDeviceManager().getDataManager().getReportDAO().reportsCount(up.getService()) > 0) //  possui mensagens para enviar
                                        && (getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MOBILE_DATA_POLICY) != 6 // politica de dados m�veis para n�o enviar
                                        && !getDeviceManager().getCommunicationManager().getCurrentCommunicationInterface().isUsesMobileData()) // se n�o � interface de dados m�veis
                                        && (getDeviceManager().getCommunicationManager().getCurrentCommunicationInterface().getTimeout() + limitIntervalToUploadService)
                                        < (System.currentTimeMillis() - lastSentMessageByServiceUpload.getTime())) {
                                    if (adaptationDAO.getLastRecordedErrorFromInstance(up.getInstance().getId(), scanIntervalOfServiceErrors) == null) { // para checar o �ltimo intervalo de erro, checar se no �ltimo intervalo foi identificado um warning
                                        //Acorda o servi�o de upload;
                                        values = new HashMap<String, Object>();
                                        values.put("componentId", CommunicationDAO.COMPONENT_ID);
                                        values.put("entityId", up.getInstance().getEntity().getId());
                                        values.put("instanceId", up.getInstance().getModelId());
                                        diagnosis.addChange(new Change(8, values));

                                        // Salva no relat�rio de funcionamento um aviso;
                                        values = new HashMap<String, Object>();
                                        values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_WARNING);
                                        values.put("description", "UploadService to " + up.getService().getServiceUID() + " didn't executed on the expected interval and he was waked up.");
                                        values.put("time", event.getTime());
                                        values.put("instanceId", up.getInstance().getId()); // usado para verificar se j� deu um warning
                                        diagnosis.addChange(new Change(10, values));
                                    } else {
                                        // Reinicia o servi�o
                                        values = new HashMap<String, Object>();
                                        values.put("componentId", CommunicationDAO.COMPONENT_ID);
                                        values.put("entityId", up.getInstance().getEntity().getId());
                                        values.put("instanceId", up.getInstance().getModelId());
                                        diagnosis.addChange(new Change(9, values));

                                        // Salva no relat�rio de funcionamento o erro
                                        values = new HashMap<String, Object>();
                                        values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_ERROR);
                                        values.put("description", "UploadService to " + up.getService().getServiceUID() + " didn't executed on the expected interval and he was restarted.");
                                        values.put("time", event.getTime());
                                        values.put("instanceId", up.getInstance().getId()); // usado para verificar se j� deu um warning
                                        diagnosis.addChange(new Change(10, values));
                                    }
                                }
                                // auxiliar reconection service
                                // general reconection service
                                /*
                                 Se o modulo de sensoriamento estiver completamente desconectado e 
                                 servi�o de reconex�o geral n�o responder dentro do intervalo de maior 
                                 timeout da interface tentando reconectar mais o limite do intervalo de reconex�o.
                                 */
                                for (ReconnectionService rs : getDeviceManager().getCommunicationManager().getReconnectionServices()) {
                                    c = getDeviceManager().getDataManager().getEventModelDAO()
                                            .getLastEventContentByLabelAndValue(rs.getInstance().getId(), "reconnectionService",
                                                    CommunicationManager.EVENT_NEW_RECONNECTION_ATTEMPT,
                                                    CommunicationDAO.ENTITY_ID_OF_RECONNECTION,
                                                    CommunicationDAO.COMPONENT_ID);
                                    Date lastReconectionAttempt = (c == null ? new Date() : c.getTime());
                                    long timeout = 0;
                                    for (CommunicationInterface ci : getDeviceManager().getCommunicationManager().getCommunicationInterfaces()) {
                                        if (rs.getMethodOfReconnection() == ReconnectionService.METHOD_ONE_BY_TIME) {
                                            // soma todos
                                            timeout += ci.getTimeout();
                                        } else {
                                            // pega o maior
                                            if (ci.getTimeout() > timeout) {
                                                timeout = ci.getTimeout();
                                            }
                                        }
                                    }
//                                    System.out.println("Teste de erro do servi�o de reconex�o geral: ");
//                                    System.out.println("Alguma interface possuí conex�o? "+rs.hasSomeInterfaceConnection());
//                                    System.out.println("Timeout + limit + reconnectionTime = "+(timeout + limitIntervalToUploadReconnectionService + rs.getReconnectionTime()));
//                                    System.out.println("�ltima tentativa: "+(System.currentTimeMillis() - lastReconectionAttempt.getTime()));
//                                    System.out.println("�ltimo erro: "+adaptationDAO.getLastRecordedErrorFromInstance(rs.getInstance().getId(), scanIntervalOfServiceErrors));
                                    // deixar gen�rico em breve
                                    if ((!rs.hasSomeInterfaceConnection()) // est� desconectado
                                            && (timeout + limitIntervalToUploadReconnectionService + rs.getReconnectionTime())
                                            < (System.currentTimeMillis() - lastReconectionAttempt.getTime())) {
                                        if (adaptationDAO.getLastRecordedErrorFromInstance(rs.getInstance().getId(), scanIntervalOfServiceErrors) == null) {
                                            //Acorda o servi�o de upload;
                                            values = new HashMap<String, Object>();
                                            values.put("componentId", CommunicationDAO.COMPONENT_ID);
                                            values.put("entityId", rs.getInstance().getEntity().getId());
                                            values.put("instanceId", rs.getInstance().getModelId());
                                            diagnosis.addChange(new Change(8, values));

                                            // Salva no relat�rio de funcionamento um aviso;
                                            values = new HashMap<String, Object>();
                                            values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_WARNING);
                                            values.put("description", "ReconectionService instanceId: " + rs.getInstance().getId() + " didn't executed on the expected interval and he was waked up.");
                                            values.put("time", event.getTime());
                                            values.put("instanceId", rs.getInstance().getId()); // usado para verificar se j� deu um warning
                                            diagnosis.addChange(new Change(10, values));

                                        } else {
                                            // Reinicia o servi�o
                                            values = new HashMap<String, Object>();
                                            values.put("componentId", CommunicationDAO.COMPONENT_ID);
                                            values.put("entityId", rs.getInstance().getEntity().getId());
                                            values.put("instanceId", rs.getInstance().getModelId());
                                            diagnosis.addChange(new Change(9, values));

                                            // Salva no relat�rio de funcionamento o erro
                                            values = new HashMap<String, Object>();
                                            values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_ERROR);
                                            values.put("description", "ReconectionService instanceId: " + rs.getInstance().getId() + " didn't executed on the expected interval and he was restarted.");
                                            values.put("time", event.getTime());
                                            values.put("instanceId", rs.getInstance().getId()); // usado para verificar se j� deu um warning
                                            diagnosis.addChange(new Change(10, values));
                                        }
                                    }
                                }
                            } else // Iniciar exclus�o de mensagens expiradas
                            if (event.getId() == AdaptationManager.EVENT_START_TASK_OF_CLEANING_REPORS) {
                                diagnosis.addChange(new Change(11, null));
                            } else // Gerar de relat�rios de funcionamento
                            if (event.getId() == AdaptationManager.EVENT_GENERATED_EVENT_TO_REPORTING_TRIGGED) {
                                values = new HashMap<String, Object>();
                                values.put("time", new Date());
                                diagnosis.addChange(new Change(12, values));
                            } else // Relat�rio de funcionamento gerado
                            if (event.getId() == AdaptationManager.EVENT_SYSTEM_REPORT_GENERATED) {
                                // action: enviar relat�rio de funcionamento - fazer
                                //values.put("report", event.getParameters().get("report"));
                                //values.put("serviceId", getDeviceManager().getBackendService().getId());
                                // fazer intera��o
                                interactionModel = (super.getDeviceManager().getDataManager().getAgentTypeDAO().getInteractionModel(AdaptationDAO.INTERACTION_TO_REPORT_SENSING_MODULE_FUNCTIONALITY));
                                interactionModel.setContentToParameter("report", event.getParameters().get("report"));
                                interactionModel.setContentToParameter("uid", getDeviceManager().getBackendService().getApplicationUID());
                                target = new Address(getDeviceManager().getBackendService().getAddress());
                                target.setLayer(Address.LAYER_SYSTEM);
                                target.setUid(getDeviceManager().getBackendService().getServiceUID());
                                values = new HashMap<String, Object>();
                                values.put("target", target);
                                values.put("interactionModel", interactionModel);
                                values.put("report", event.getParameters().get("report"));
                                values.put("uid", getDeviceManager().getBackendService().getApplicationUID());
                                diagnosis.addChange(new Change(13, values));
                                // apagar relatos de funcionamento antigos - fazer
                                values = new HashMap<String, Object>();
                                values.put("time", event.getParameters().get("time"));
                                diagnosis.addChange(new Change(14, values));
                                // action: atualizar �ltima data de relato
                                diagnosis.addChange(new Change(15, values));
                            } else // Erro no loop de adapta��o
                            if (event.getId() == AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR) {
                                // Salva no relat�rio de funcionamento um aviso;
                                values = new HashMap<String, Object>();
                                values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_ERROR);
                                values.put("description", "Adaptation loop error was found:{" + event.getParameters().get("error") + "}");
                                values.put("time", event.getTime());
                                diagnosis.addChange(new Change(10, values));
                            } else // Erro no loop de adapta��o
                            if (event.getId() == AdaptationManager.EVENT_UNKNOWN_EVENT_WARNING) {
                                // Salva no relat�rio de funcionamento um aviso;
                                values = new HashMap<String, Object>();
                                values.put("type", AdaptationDAO.FUNCTIONALITY_STATUS_TYPE_WARNING);
                                values.put("description", "Unknown was found:{" + event.getParameters().get("error") + "}");
                                values.put("time", event.getTime());
                                diagnosis.addChange(new Change(10, values));
                            }
                        }
                        // �ltimo diagn�stico a ser executado
                        if (diagnosis.getChanges().isEmpty()) {
                            diagnosis.addChange(new Change(Diagnosis.DIAGNOSIS_NO_ADAPTATION_NEEDED, null));
                        }

                }
                /* Planning -- Plan */
                // verifica o diagn�stico para encontrar se necessita de adapta��o
                if (diagnosis.getChanges().get(0).getId() != Diagnosis.DIAGNOSIS_NO_ADAPTATION_NEEDED) {
                    // para cada mudan�a verificar a mudan�a
                    for (Change change : diagnosis.getChanges()) {
                        // pega os par�metros atribuídos pelo Analysis
                        values = change.getParameters();
                        // instancia a lista de a��es a ser adicionada no plano
                        actions = new ArrayList<Action>();
                        // vou bucar a mudan�a na verdade no banco de dados -- todas as a��es est�ticas por enquanto
                        switch (change.getId()) {
                            case 1: // Subscribe no servidor
                                action = new Action();
                                action.setId(AdaptationDAO.INTERACTION_TO_SUBSCRIBE_THE_MAXIMUM_UPLOAD_RATE); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                //action.setSynchronous(true);
                                actions.add(action);
                                break;
                            case 2: // inícializar o contador de envio de relat�rios de funcionamento ao servidor
                                action = new Action();
                                action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
                                action.setTargetComponentId(EventDAO.COMPONENT_ID);
                                action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
                                action.setParameters(values);
                                actions.add(action);
                                break;
                            case 3: // iniciar varredura para exclus�o de mensagens expiradas
                                action = new Action();
                                action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
                                action.setTargetComponentId(EventDAO.COMPONENT_ID);
                                action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
                                action.setParameters(values);
                                actions.add(action);
                                break;
                            case 4: // atualizar no servidor o novo endere�o
                                action = new Action();
                                action.setId(AdaptationDAO.INTERACTION_TO_SUBSCRIBE_THE_MAXIMUM_UPLOAD_RATE); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                //action.setSynchronous(false);
                                actions.add(action);
                                break;
                            case 5: // atualiza taxa de upload do servi�o
                                action = new Action();
                                action.setId(CommunicationManager.ACTION_UPDATE_UPLOAD_SERVICE_UPLOAD_RATE); // atualizar taxa de upload
                                action.setParameters(values);
                                action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS);
                                action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 6: // atualiza taxa de upload do servi�o
                                action = new Action();
                                action.setId(CommunicationManager.ACTION_UPDATE_UPLOAD_SERVICE_SUBSCRIBED_MAXIMUM_UPLOAD_RATE); // atualizar taxa de upload
                                action.setParameters(values);
                                action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS);
                                action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 7: // Inicia tarefa de varredura de erros em servi�os usando intervalo fixo (usado pelo caso 3);
                                action = new Action();
                                action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
                                action.setTargetComponentId(EventDAO.COMPONENT_ID);
                                action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
                                action.setParameters(values);
                                actions.add(action);
                                break;
                            case 8: // acordar servi�o
                                action = new Action();
                                action.setId(DeviceManager.ACTION_WAKE_UP_SERVICE); // atualizar taxa de upload
                                action.setParameters(values);
                                action.setTargetEntityId(DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES);
                                action.setTargetComponentId(DeviceDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 9: // reiniciar servi�o
                                action = new Action();
                                action.setId(DeviceManager.ACTION_RESTART_SERVICE);
                                action.setParameters(values);
                                action.setTargetEntityId(DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES);
                                action.setTargetComponentId(DeviceDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 10: // armazenar erro/aviso para relato do funcionamento do m�dulo
                                action = new Action();
                                action.setId(ACTION_STORE_INTERNAL_ERROR);
                                action.setParameters(values);
                                action.setTargetEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                action.setTargetComponentId(AdaptationDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 11: //Apagar mensagens expiradas
                                action = new Action();
                                action.setId(CommunicationManager.ACTION_DELETE_EXPIRED_MESSAGES);
                                action.setParameters(values);
                                action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_REPORTS_STORAGE);
                                action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 12: // Gerar de relat�rios de funcionamento
                                action = new Action();
                                action.setId(AdaptationManager.ACTION_GENERATE_EVENT_ERROR_REPORTING);
                                action.setParameters(values);
                                action.setTargetEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                action.setTargetComponentId(AdaptationDAO.COMPONENT_ID);
                                actions.add(action);
                                break;
                            case 13: // action: enviar relat�rio de funcionamento
                                action = new Action();
                                action.setId(AdaptationDAO.INTERACTION_TO_REPORT_SENSING_MODULE_FUNCTIONALITY); // registro no servidor
                                action.setActionType(Event.INTERATION_EVENT);
                                action.setParameters(values);
                                action.setSynchronous(false);
                                actions.add(action);
                                break;
                            case 14: // apagar relatos de funcionamento antigos
                                action = new Action();
                                action.setId(AdaptationManager.ACTION_REMOVE_SENT_FUNCTIONALITY_REPORTS);
                                action.setParameters(values);
                                action.setTargetEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                action.setTargetComponentId(AdaptationDAO.COMPONENT_ID);
                                //actions.add(action);
                                break;
                            case 15: // action: atualizar �ltima data de relato
                                action = new Action();
                                action.setId(AdaptationManager.ACTION_UPDATE_LAST_SENT_REPORT_DATE);
                                action.setParameters(values);
                                action.setTargetEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                                action.setTargetComponentId(AdaptationDAO.COMPONENT_ID);
                                // comentei para teste
                                actions.add(action);
                                break;
                            default:
                                // evento de erro. Diagn�stico n�o conhecido
                                break;
                        }
                        executionPlan = new ExecutionPlan(actions);
                        plan.addExecutionPlan(executionPlan);
                    }
                }
                /**
                 * ************** Execute Process **************
                 */
                //System.out.println("Message: "+ event.getValue().toString() );
                // existe alguma a��o?
                if (plan.getExecutionPlans().size() > 0) {
                    response = null;
                    // para cada plano de execu��o
                    for (ExecutionPlan ep : plan.getExecutionPlans()) {
                        // para cada a��o at� condi��o de parada
                        for (Action actionToExecute : ep.getQueueOfActions()) {
                            if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                                System.out.println("Action: " + actionToExecute.toString());
                            }
                            // verificar se � intera��o
                            if (actionToExecute.getActionType() == Event.INTERATION_EVENT) {
                                // se sim, gerar uma mensagem no formato da linguagem e popular os par�metros de envio.
                                actionToExecute = adaptationDAO.makeInteractionMessage(actionToExecute);
                            }
                            // encontrar o componente para envio, no caso de intera��o o Componente Communication
                            for (ComponentManager cm : getDeviceManager().getComponentManagers()) {
                                if (cm.getComponentId() == actionToExecute.getTargetComponentId()) {
                                    // aplicar a a��o e pegar o feedback
                                    response = cm.applyAction(actionToExecute);
                                    // atualizar a decis�o da a��o
                                    this.adaptationDAO.updateDecision(response, event, actionToExecute, ep);
                                    break;
                                }
                            }
                            // componente n�o encontrado
                            if (response == null) {
                                throw new Exception("Target component id:" + actionToExecute.getTargetComponentId() + " was not found!");
                            }
                            // verificar condi��o de parada
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
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            } catch (InterruptedException ex) {
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            } catch (Exception ex) { // outras excess�es desconhecidas
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            }
        }
    }

    protected void adaptationControlLoop() {

        Event errorEvent;
        Plan plan;
        Diagnosis diagnosis;
        FeedbackAnswer response;
        HashMap<String, Object> values;
        Object eventInteractionModel;
        while (isRunning()) {
            Event event = null;
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
                if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                    System.out.println("Event: " + event.toString());
                }
                /**
                 * ************** Update world model **************
                 */
                // verifica se � uma intera��o ou um evento
                // se uma intera��o extrai a intera��o dos par�metros do evento
                // de forma semelhante ao evento salva as informa��es
                // gera uma intera��o que � passada para an�lise -- o mesmo ocorre com o evento
                // buscar o modelo de evento
                try {
                    eventInteractionModel = this.adaptationDAO.updateWorldModel(event);
                } catch (NullPointerException ex) {
                    // gera um evento de evento desconhecido ou n�o existente e adiciona os dados do evento n�o reconecido nos par�metros
                    values = new HashMap();
                    values.put("error", "Evento n�o conhecido: " + event.toString());
                    errorEvent = new SystemEvent(this);
                    errorEvent.setName("Unknown event");
                    errorEvent.setId(AdaptationManager.EVENT_UNKNOWN_EVENT_WARNING);
                    errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                    errorEvent.setParameters(values);
                    this.newEvent(errorEvent);
                    throw new NullPointerException(ex.getMessage());
                }
                /**
                 * ************** Analisys Process **************
                 */
                diagnosis = this.diagnosisModel.analysis(event);
                
                /**
                 * ************** Planning Process **************
                 */
                plan = this.planningModel.planning(diagnosis, diagnosisModel);
                /**
                 * ************** Execute Process **************
                 */
                //System.out.println("Message: "+ event.getValue().toString() );
                // existe alguma a��o?
                if (plan.getExecutionPlans().size() > 0) {
                    response = null;
                    // para cada plano de execu��o
                    for (ExecutionPlan ep : plan.getExecutionPlans()) {
                        // para cada a��o at� condi��o de parada
                        for (Action actionToExecute : ep.getQueueOfActions()) {
                            if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
                                System.out.println("Action: " + actionToExecute.toString());
                            }
                            // verificar se � intera��o
                            if (actionToExecute.getActionType() == Event.INTERATION_EVENT) {
                                // se sim, gerar uma mensagem no formato da linguagem e popular os par�metros de envio.
                                actionToExecute = adaptationDAO.makeInteractionMessage(actionToExecute);
                            }
                            // encontrar o componente para envio, no caso de intera��o o Componente Communication
                            for (ComponentManager cm : getDeviceManager().getComponentManagers()) {
                                if (cm.getComponentId() == actionToExecute.getTargetComponentId()) {
                                    // aplicar a a��o e pegar o feedback
                                    response = cm.applyAction(actionToExecute);
                                    // atualizar a decis�o da a��o
                                    this.adaptationDAO.updateDecision(response, event, actionToExecute, ep);
                                    break;
                                }
                            }
                            // componente n�o encontrado
                            if (response == null) {
                                throw new Exception("Target component id:" + actionToExecute.getTargetComponentId() + " was not found!");
                            }
                            // verificar condi��o de parada
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
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            } catch (InterruptedException ex) {
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            } catch (Exception ex) { // outras excess�es desconhecidas
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                values = new HashMap<String, Object>();
                values.put("error", "Exception: " + ex + " Error in: " + event.toString());
                errorEvent = new SystemEvent(this);
                errorEvent.setName("Adaptation loop error");
                errorEvent.setId(AdaptationManager.EVENT_ADAPTATION_LOOP_ERROR);
                errorEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
                errorEvent.setParameters(values);
                this.newEvent(errorEvent);
            }
        }
    }

    public boolean isAllowedReportingFunctionsToUploadService() {
        return isAllowedReportingFunctionsToUploadService;
    }

    public Long getIntervalAmongModuleStateReports() {
        return intervalAmongModuleStateReports;
    }

    public Long getIntervalCleanStoredMessages() {
        return intervalCleanStoredMessages;
    }

    public long getScanIntervalOfServiceErrors() {
        return scanIntervalOfServiceErrors;
    }

    public long getLimitIntervalToUploadReconnectionService() {
        return limitIntervalToUploadReconnectionService;
    }

    public long getLimitIntervalToUploadService() {
        return limitIntervalToUploadService;
    }

    public void setDiagnosisModel(AbstractDiagnosisModel diagnosisModel) {
        this.diagnosisModel = diagnosisModel;
    }

    public void setPlanningModel(AbstractPlanningModel planningModel) {
        this.planningModel = planningModel;
    }
    
    public AdaptationDAO getAdaptationDAO() {
        return adaptationDAO;
    }
    
}
