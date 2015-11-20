/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import urbosenti.core.device.model.Agent;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.concerns.ConcernManager;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.ReconnectionService;
import urbosenti.core.communication.UploadService;
import urbosenti.core.data.DataManager;
import urbosenti.core.data.dao.CommunicationDAO;
import urbosenti.core.data.dao.DeviceDAO;
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Device;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.State;
import urbosenti.core.device.model.TargetOrigin;
import urbosenti.core.events.Action;
import urbosenti.core.events.ApplicationEvent;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemEvent;
import urbosenti.localization.LocalizationManager;
import urbosenti.resources.ResourceManager;
import urbosenti.user.UserManager;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public final class DeviceManager extends ComponentManager implements BaseComponentManager {

    /**
     * int EVENT_DEVICE_REGISTRATION_SUCCESSFUL = 1;
     *
     * <ul><li>id: 1</li>
     * <li>evento: Registro efetuado com sucesso</li>
     * <li>par�metros: UID da Aplica��o, Tempo para expira��o</li></ul>
     */
    public static final int EVENT_DEVICE_REGISTRATION_SUCCESSFUL = 1;
    /**
     * int EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL = 2;
     *
     * <ul><li>id: 2</li>
     * <li>evento: Erro ao registrar ao servidor</li>
     * <li>par�metros: C�digo do Erro, Descri��o</li></ul>
     */
    public static final int EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL = 2;
    /**
     * int EVENT_DEVICE_SERVICES_INITIATED = 3;
     *
     * <ul><li>id: 3</li>
     * <li>evento: Servi�os Iniciados</li>
     * <li>par�metros: Nenhum</li></ul>
     */
    public static final int EVENT_DEVICE_SERVICES_INITIATED = 3;
    /**
     * int EVENT_DEVICE_SERVICES_STOPPED = 4;
     *
     * <ul><li>id: 4</li>
     * <li>evento: Servi�os Parados</li>
     * <li>par�metros: Nenhum</li></ul>
     */
    public static final int EVENT_DEVICE_SERVICES_STOPPED = 4;
    /**
     * int EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED = 5;
     *
     * <ul><li>id: 5</li>
     * <li>evento: Informa��es do dispositivo coletadas com sucesso</li>
     * <li>par�metros: Espa�o dispon�vel de Armazenamento (B), N�cleos no CPU,
     * Frequ�ncia por n�cleos do CPU, Modelo do CPU, S.O. Nativo, Mem�ria RAM
     * (B), Modelo de dispositivo, Capacidade da Bateria</li></ul>
     */
    public static final int EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED = 5;
    /**
     * int EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION = 6;
     *
     * <ul><li>id: 6</li>
     * <li>evento: N�o foi poss�vel acessar as informa��es</li>
     * <li>par�metros: C�digo do Erro, Descri��o</li></ul>
     */
    public static final int EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION = 6;

    /*
     *********************************************************************
     ***************************** Actions ******************************* 
     *********************************************************************
     */
    /**
     * int ACTION_WAKE_UP_SERVICE = 1;
     *
     * <ul><li>id: 1</li>
     * <li>a��o: Acordar um servi�o</li>
     * <li>par�metros: componentId, entityId, instanceId</li></ul>
     *
     */
    public static final int ACTION_WAKE_UP_SERVICE = 1;
    /**
     * int ACTION_RESTART_SERVICE = 1;
     *
     * <ul><li>id: 2</li>
     * <li>a��o: Reiniciar um servi�o</li>
     * <li>par�metros: componentId, entityId, instanceId</li></ul>
     *
     */
    public static final int ACTION_RESTART_SERVICE = 2;
    /**
     * int ACTION_STOP_SERVICE = 1;
     *
     * <ul><li>id: 3</li>
     * <li>a��o: Parar um servi�o</li>
     * <li>par�metros: componentId, entityId, instanceId</li></ul>
     *
     */
    public static final int ACTION_STOP_SERVICE = 3;
    /* Identificadores dos componentes internos */
    public static int DEVICE_COMPONENT_ID;
    public static int EVENTS_COMPONENT_ID;
    public static int DATA_COMPONENT_ID;
    public static int COMMUNICATION_COMPONENT_ID;
    public static int ADAPTATION_COMPONENT_ID;
    public static int USER_COMPONENT_ID;
    public static int CONTEXT_COMPONENT_ID;
    public static int LOCALIZACAO_COMPONENT_ID;
    public static int RESOURCE_COMPONENT_ID;
    public static int CONCERNS_COMPONENT_ID;
    private CommunicationManager communicationManager = null;
    private DataManager dataManager = null;
    private EventManager eventManager = null;
    private ContextManager contextManager = null;
    private AdaptationManager adaptationManager = null;
    private UserManager userManager = null;
    private LocalizationManager localizationManager = null;
    private ResourceManager resourceManager = null;
    private ConcernManager concernManager = null;
    private OperatingSystemDiscovery OSDiscovery = null;
    private List<ComponentManager> enabledComponentManagers;
    private Boolean isRunning;

    public DeviceManager() {
        super();
        this.dataManager = new DataManager(this);
        this.eventManager = new EventManager(this);
        this.communicationManager = new CommunicationManager(this);
        this.setComponentId(DeviceDAO.COMPONENT_ID);
        DEVICE_COMPONENT_ID = DeviceDAO.COMPONENT_ID;
        EVENTS_COMPONENT_ID = urbosenti.core.data.dao.EventDAO.COMPONENT_ID;
        DATA_COMPONENT_ID = urbosenti.core.data.dao.DataDAO.COMPONENT_ID;
        COMMUNICATION_COMPONENT_ID = urbosenti.core.data.dao.CommunicationDAO.COMPONENT_ID;
        ADAPTATION_COMPONENT_ID = urbosenti.core.data.dao.AdaptationDAO.COMPONENT_ID;
        USER_COMPONENT_ID = urbosenti.core.data.dao.UserDAO.COMPONENT_ID;
        CONTEXT_COMPONENT_ID = urbosenti.core.data.dao.ContextDAO.COMPONENT_ID;
        LOCALIZACAO_COMPONENT_ID = urbosenti.core.data.dao.LocationDAO.COMPONENT_ID;
        RESOURCE_COMPONENT_ID = urbosenti.core.data.dao.ResourcesDAO.COMPONENT_ID;
        CONCERNS_COMPONENT_ID = urbosenti.core.data.dao.ConcernsDAO.COMPONENT_ID;
        this.dataManager.setComponentId(DATA_COMPONENT_ID);
        this.communicationManager.setComponentId(COMMUNICATION_COMPONENT_ID);
        this.eventManager.setComponentId(EVENTS_COMPONENT_ID);
        this.isRunning = false;
    }

    public CommunicationManager getCommunicationManager() {
        return communicationManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public AdaptationManager getAdaptationManager() {
        return adaptationManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ConcernManager getConcernManager() {
        return concernManager;
    }

    public void enableContextComponent() {
        this.contextManager = new ContextManager(this);
        this.contextManager.setComponentId(CONTEXT_COMPONENT_ID);
    }

    public void enableAdaptationComponent() {
        this.adaptationManager = new AdaptationManager(this);
        this.eventManager.setSystemHandler(adaptationManager);
        this.eventManager.enableSystemHandler();
        this.adaptationManager.setComponentId(ADAPTATION_COMPONENT_ID);
    }

    public void enableUserComponent() {
        this.userManager = new UserManager(this);
        this.userManager.setComponentId(USER_COMPONENT_ID);
    }

    public void enableLocalizationComponent() {
        this.localizationManager = new LocalizationManager(this);
        this.localizationManager.setComponentId(LOCALIZACAO_COMPONENT_ID);
    }

    public void enableResourceComponent() {
        this.resourceManager = new ResourceManager(this);
        this.resourceManager.setComponentId(RESOURCE_COMPONENT_ID);
    }

    public void enableConcernComponent() {
        this.concernManager = new ConcernManager(this);
        this.concernManager.setComponentId(CONCERNS_COMPONENT_ID);
    }

    @Override
    public void onCreate() { // Before the execution
        if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
            System.out.println("Activating: " + getClass());
        }
        // instancia um estado para ser usado na ativa�a� dos m�dulos sobdemanda
        State entityState;
        // Conte�do usado para habilitar os componentes da urbosenti
        Content content = new Content();
        content.setTime(new Date());
        content.setValue(true);
        try {
            // instancia uma lista de componentes habilitados
            this.enabledComponentManagers = new ArrayList();
            // habilita do componente de dados
            this.dataManager.onCreate(); // Deve ser o primeiro a ser executado. Os demais ir�o utilizar esse gerente para acessar dados
            this.enabledComponentManagers.add(this.dataManager);
            // habilita o componente de eventos
            this.eventManager.onCreate();
            this.enabledComponentManagers.add(this.eventManager);
            // Faz a descoberta que interfaces est�o dispon�veis
            this.outputCommunicationInterfacesDiscovery();
            this.inputCommunicationInterfacesDiscovery();
            this.deviceDiscovery(); // descobre as informa��es do dispositivo
            // inicia conficura��es dos servi�os
            this.setUpCommunicationUrboSentiServices();
            this.communicationManager.onCreate(); // Inst�ncia os monitores gerais de funcionalidade e os atuadores gerais
            this.enabledComponentManagers.add(this.communicationManager);
            // ativa��o dos M�dulos sobdemanda
            if (this.userManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_USER_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.userManager.onCreate();
                this.enabledComponentManagers.add(this.userManager);
            }
            if (this.contextManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_CONTEXT_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.contextManager.onCreate();
                this.enabledComponentManagers.add(this.contextManager);
            }
            if (this.localizationManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_LOCATION_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.localizationManager.onCreate();
                this.enabledComponentManagers.add(this.localizationManager);
            }
            if (this.resourceManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_RESOURCES_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.resourceManager.onCreate();
                this.enabledComponentManagers.add(this.resourceManager);
            }
            if (this.concernManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_CONCERNS_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.concernManager.onCreate();
                this.enabledComponentManagers.add(this.concernManager);
            }

            // adaptation Discovery
            if (adaptationManager != null) {
                // Estado respectivo que simboliza que este compoenente est� ativo
                entityState = this.dataManager.getEntityStateDAO()
                        .getEntityState(
                                DeviceDAO.COMPONENT_ID,
                                DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES,
                                DeviceDAO.STATE_ID_OF_URBOSENTI_SERVICES_ADAPTATION_COMPONENT_STATUS);
                // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                if ((entityState.getContent() == null) ? true : !((Boolean) entityState.getContent().getValue())) {
                    // adiciona o conte�do
                    entityState.setContent(content);
                    // torna habilitado no conhecimento
                    this.dataManager.getEntityStateDAO().insertContent(entityState);
                }
                // onCreate do componente
                this.adaptationManager.onCreate();
                this.enabledComponentManagers.add(this.adaptationManager);
            }
            // dar start em todos os servi�os (Adaptation, interfaces de entrada, etc...) ---- falta isso 
            // Executar evento - UrboSenti Servi�os Iniciados
            // this.newInternalEvent(EVENT_DEVICE_SERVICES_INITIATED);
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new Error(ex);
        }
        this.enabledComponentManagers.add(this);
        if (DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY) {
            System.out.println("onCreate Process Finished");
        }
    }

    /**
     * A��es disponibilizadas por esse componente por fun��o:
     * <p>
     * <b>Entidade Alvo</b>: Fun��o dos Servi�os UrboSenti</p>
     * <ul>
     * <li>01 - Acordar um servi�o - componentId, entityId, instanceId</li>
     * <li>02 - Reiniciar um servi�o - componentId, entityId, instanceId</li>
     * <li>03 - Parar servi�o - componentId, entityId, instanceId</li>
     * </ul>
     *
     * @param action cont�m objeto a��o.
     * @return
     *
     */
    @Override
    public FeedbackAnswer applyAction(Action action) {
        FeedbackAnswer answer = null;
        Integer componentId = Integer.parseInt((action.getParameters().get("componentId").toString())),
                entityId = Integer.parseInt((action.getParameters().get("entityId").toString())),
                instanceId = Integer.parseInt((action.getParameters().get("instanceId").toString()));
        switch (action.getId()) {
            case 1: // Acordar um servi�o
                if (componentId == CommunicationDAO.COMPONENT_ID) {
                    if (entityId == CommunicationDAO.ENTITY_ID_OF_RECONNECTION) {
                        for (ReconnectionService rs : communicationManager.getReconnectionServices()) {
                            if (instanceId == rs.getInstance().getModelId()) {
                                rs.wakeUp();
                            }
                        }
                    } else if (entityId == CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS) {
                        for (UploadService up : communicationManager.getUploadServices()) {
                            if (instanceId == up.getInstance().getModelId()) {
                                up.wakeUp();
                            }
                        }
                    }
                }
                break;
            case 2: // Reiniciar um servi�o
                if (componentId == CommunicationDAO.COMPONENT_ID) {
                    if (entityId == CommunicationDAO.ENTITY_ID_OF_RECONNECTION) {
                        for (ReconnectionService rs : communicationManager.getReconnectionServices()) {
                            if (instanceId == rs.getInstance().getModelId()) {
                                rs.stop();
                                rs.start();
                            }
                        }
                    } else if (entityId == CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS) {
                        for (UploadService up : communicationManager.getUploadServices()) {
                            if (instanceId == up.getInstance().getModelId()) {
                                up.stop();
                                up.start();
                            }
                        }
                    }
                }
                break;
            case 4: // Parar servi�o
                if (componentId == CommunicationDAO.COMPONENT_ID) {
                    if (entityId == CommunicationDAO.ENTITY_ID_OF_RECONNECTION) {
                        for (ReconnectionService rs : communicationManager.getReconnectionServices()) {
                            if (instanceId == rs.getInstance().getModelId()) {
                                rs.start();
                            }
                        }
                    } else if (entityId == CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS) {
                        for (UploadService up : communicationManager.getUploadServices()) {
                            if (instanceId == up.getInstance().getModelId()) {
                                up.start();
                            }
                        }
                    }
                }
                break;
        }
        // verifica se a a��o existe ou se houve algum resultado durante a execu��o
        if (answer == null && action.getId() >= 1 && action.getId() <= 3) {
            answer = new FeedbackAnswer(FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL);
        } else if (answer == null) {
            answer = new FeedbackAnswer(FeedbackAnswer.ACTION_DOES_NOT_EXIST);
        }
        return answer;
    }

    /**
     * <br><br>Eventos poss�veis:
     * <ul>
     * <li>DeviceManager.EVENT_DEVICE_REGISTRATION_SUCCESSFUL - Registro
     * efetuado com sucesso</li>
     * <li>DeviceManager.EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL - Erro ao
     * registrar ao servidor</li>
     * <li>DeviceManager.EVENT_DEVICE_SERVICES_INITIATED - Servi�os
     * Iniciados</li>
     * <li>DeviceManager.EVENT_DEVICE_SERVICES_STOPPED - Servi�os Parados</li>
     * <li>DeviceManager.EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED -
     * Informa��es do dispositivo coletadas com sucesso</li>
     * <li>DeviceManager.EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION - N�o
     * foi poss�vel acessar as informa��es</li>
     * </ul>
     *
     * @param eventId identificador do evento citado acima
     * @param params Par�metros oriundo dos objetos do componente <br><br>
     * @see #EVENT_DEVICE_REGISTRATION_SUCCESSFUL
     * @see #EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL
     * @see #EVENT_DEVICE_SERVICES_INITIATED
     * @see #EVENT_DEVICE_SERVICES_STOPPED
     * @see #EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED
     * @see #EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION
     */
    public void newInternalEvent(int eventId, Object... params) {
        Event event;
        HashMap<String, Object> values;
        switch (eventId) {
            case EVENT_DEVICE_REGISTRATION_SUCCESSFUL:
                // Par�metros do evento
                values = new HashMap();
                values.put("uid", params[0]);
                values.put("expirationTime", params[1]);

                // Se o tratador de eventos de sistema estiver ativo gera um evento para ele tamb�m
                if (adaptationManager != null) {
                    // Cria o evento de sistema
                    event = new SystemEvent(this);
                    event.setId(1);
                    event.setName("Registro efetuado com sucesso");
                    event.setTime(new Date());
                    event.setParameters(values);
                    event.setEntityId(DeviceDAO.ENTITY_ID_OF_SERVICE_REGISTRATION);

                    // envia o evento
                    getEventManager().newEvent(event);
                }

                // Cria o evento de aplica��o, alertando a aplica��o
                event = new ApplicationEvent(this);
                event.setId(1);
                event.setName("Registro efetuado com sucesso");
                event.setTime(new Date());
                event.setParameters(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL:
                // Par�metros do evento
                values = new HashMap();
                values.put("errorCode", params[0]);
                values.put("description", params[1]);

                // Se o tratador de eventos de sistema estiver ativo gera um evento para ele tamb�m
                if (adaptationManager != null) {
                    // Cria o evento de sistema
                    event = new SystemEvent(this);
                    event.setId(2);
                    event.setName("Erro ao registrar ao servidor");
                    event.setTime(new Date());
                    event.setParameters(values);
                    event.setEntityId(DeviceDAO.ENTITY_ID_OF_SERVICE_REGISTRATION);

                    // envia o evento
                    getEventManager().newEvent(event);
                }

                // Cria o evento de aplica��o, alertando a aplica��o
                event = new ApplicationEvent(this);
                event.setId(2);
                event.setName("Erro ao registrar ao servidor");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(DeviceDAO.ENTITY_ID_OF_SERVICE_REGISTRATION);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_SERVICES_INITIATED:
                // n�o possu� par�metros
                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(3);
                event.setName("Servi�os Iniciados");
                event.setTime(new Date());
                event.setEntityId(DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES);
                event.setParameters(new HashMap<String, Object>());
                event.getParameters().put("serviceStatus", true);
                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_SERVICES_STOPPED:
                // n�o possu� par�metros
                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(4);
                event.setName("Servi�os Parados");
                event.setTime(new Date());
                event.setEntityId(DeviceDAO.ENTITY_ID_OF_URBOSENTI_SERVICES);
                event.setParameters(new HashMap<String, Object>());
                event.getParameters().put("serviceStatus", false);
                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED: // Informa��es do dispositivo coletadas com sucesso
                // Par�metros do evento
                values = new HashMap();
                values.put("storage", params[0]);
                values.put("cores", params[1]);
                values.put("clock", params[2]);
                values.put("cpuModel", params[3]);
                values.put("nativeSO", params[4]);
                values.put("RAM", params[5]);
                values.put("deviceModel", params[6]);
                values.put("battery", params[7]);

                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(5);
                event.setName("Informa��es do dispositivo coletadas com sucesso");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(DeviceDAO.ENTITY_ID_OF_BASIC_DEVICE_INFORMATIONS);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION: // N�o foi poss�vel acessar as informa��es
                // Par�metros do evento
                values = new HashMap();
                values.put("errorCode", params[0]);
                values.put("description", params[1]);

                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(6);
                event.setName("N�o foi poss�vel acessar as informa��es");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(DeviceDAO.ENTITY_ID_OF_BASIC_DEVICE_INFORMATIONS);

                // envia o evento
                getEventManager().newEvent(event);

                break;
        }
    }

    public void addSupportedCommunicationInterface(CommunicationInterface communicationInterface) {
        dataManager.addSupportedCommunicationInterface(communicationInterface);
    }

    public boolean registerSensingModule(Service backendServer) throws IOException {
        try {
            // verifica se j� est� registrado, se sim retorna true, se n�o, continua executando
            if (this.isSensingModuleRegistred(backendServer)) {
                return true;
            }
            // busca as informa��es do dispositivo
            Entity entity = this.dataManager.getEntityDAO().getEntity(
                    DeviceDAO.COMPONENT_ID,
                    DeviceDAO.ENTITY_ID_OF_BASIC_DEVICE_INFORMATIONS);
            // busca as interfaces de comunica��o de entrada
            List<Instance> instances = this.dataManager.getInstanceDAO().getEntityInstances(
                    CommunicationDAO.ENTITY_ID_OF_INPUT_COMMUNICATION_INTERFACES, CommunicationDAO.COMPONENT_ID);
            // busca os estados da entidade
            entity.setStateModels(this.dataManager.getEntityStateDAO().getEntityStateModels(entity));
            // cria o xml
            // gerar mensagem em XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("device"), deviceSetup, inputCommunicationInterface, extra;
            for (State s : entity.getStates()) {
                deviceSetup = doc.createElement("deviceSetup");
                if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_MODEL) {
                    deviceSetup.setAttribute("name", "cpuModel");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORES) {
                    deviceSetup.setAttribute("name", "cpuCore");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORE_FREQUENCY_CLOCK) {
                    deviceSetup.setAttribute("name", "cpuClock");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_DEVICE_MODEL) {
                    deviceSetup.setAttribute("name", "deviceModel");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_NATIVE_OPERATIONAL_SYSTEM) {
                    deviceSetup.setAttribute("name", "nativeOS");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_STORAGE_SYSTEM) {
                    deviceSetup.setAttribute("name", "storage");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_BATTERY_CAPACITY) {
                    deviceSetup.setAttribute("name", "battery");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_MEMORY_RAM) {
                    deviceSetup.setAttribute("name", "memory");
                    deviceSetup.setTextContent(s.getCurrentValue().toString());
                    root.appendChild(deviceSetup);
                }
            }
            for (Instance instance : instances) {
                inputCommunicationInterface = doc.createElement("inputCommunicationInterface");
                inputCommunicationInterface.setAttribute("type", instance.getDescription());
                for (State state : instance.getStates()) {
                    if (state.getModelId() == CommunicationDAO.STATE_ID_OF_INPUT_COMMUNICATION_INTERFACE_CONFIGURATIONS) {
                        if (!state.getCurrentValue().equals("unknown") && !state.getCurrentValue().equals("null")) {
                            // Retorna a representa��o em String de um HashMap
                            String savedExtras = (String) state.getCurrentValue();
                            //System.out.println("dbExtra; "+savedExtras);
                            savedExtras = savedExtras.replace("{", "");
                            savedExtras = savedExtras.replace("}", "");
                            // System.out.println("dbExtra; "+savedExtras);
                            String[] pairs = savedExtras.split(",");
                            // Adiciona no elemento
                            for (String pair : pairs) {
                                //System.out.println("pair: "+pair);
                                String[] keyValue = pair.split("=");
                                //System.out.println("key-l "+keyValue.length);
                                extra = doc.createElement("extra");
                                //System.out.println(keyValue[0].trim()+" -> "+keyValue[1].trim());
                                extra.setAttribute("name", keyValue[0].trim()); // Key
                                extra.setTextContent(keyValue[1].trim()); // Value
                                inputCommunicationInterface.appendChild(extra);
                            }
                        }
                    }
                }
                root.appendChild(inputCommunicationInterface);
            }
            // Converter Documento para STRING
            StringWriter stw = new StringWriter();
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(root), new StreamResult(stw));

            Address serviceAddress = new Address(backendServer.getAddress());
            if (backendServer.getServiceUID().length() > 0) {
                serviceAddress.setUid(backendServer.getServiceUID());
            }
            Message message = new Message();
            message.setContent(stw.getBuffer().toString());
            message.setSubject(Message.SUBJECT_REGISTRATION);
            message.setTarget(serviceAddress);
            message.setContentType("text/xml");
            // envia a mensagem de registro e recebe o novo registro
            String response = this.communicationManager.sendMessageWithResponse(message);
            // processa o XML - criar o documento e converte a String em DOC 
            doc = builder.parse(new InputSource(new StringReader(response)));
            Element registry = doc.getDocumentElement();
            // Adicio a as informa��es nas respectivas vari�veis
            backendServer.setApplicationUID(registry.getElementsByTagName("applicationUid").item(0).getTextContent());
            backendServer.setServiceUID(registry.getElementsByTagName("serviceUid").item(0).getTextContent());
            String password = registry.getElementsByTagName("password").item(0).getTextContent();
            Long expirationTime = Long.parseLong(registry.getElementsByTagName("expirationTime").item(0).getTextContent());
            // busca a entidade de registro
            entity = this.dataManager.getEntityDAO()
                    .getEntity(DeviceDAO.COMPONENT_ID, DeviceDAO.ENTITY_ID_OF_SERVICE_REGISTRATION);
            entity.setStateModels(this.dataManager.getEntityStateDAO().getEntityStateModels(entity));
            // salva as informa��es
            for (State s : entity.getStates()) {
                Date nowDate = new Date();
                if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_APPLICATION_UID) {
                    s.setContent(new Content(backendServer.getApplicationUID(), nowDate));
                    this.dataManager.getEntityStateDAO().insertContent(s);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_SERVICE_UID) {
                    s.setContent(new Content(backendServer.getServiceUID(), nowDate));
                    this.dataManager.getEntityStateDAO().insertContent(s);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_REMOTE_PASSWORD) {
                    s.setContent(new Content(password, nowDate));
                    this.dataManager.getEntityStateDAO().insertContent(s);
                } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_SERVICE_EXPIRATION_TIME) {
                    s.setContent(new Content(expirationTime, nowDate));
                    this.dataManager.getEntityStateDAO().insertContent(s);
                }
            }
            // atualiza as configura��es do servidor
            this.dataManager.getServiceDAO().updateServiceUIDs(backendServer);
            // gera o evento
            this.newInternalEvent(EVENT_DEVICE_REGISTRATION_SUCCESSFUL, backendServer.getApplicationUID(), expirationTime);
            // retorna true
            return true;
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.newInternalEvent(EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL, 1, ex.toString());
        } catch (ParserConfigurationException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.newInternalEvent(EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL, 2, ex.toString());
        } catch (TransformerException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.newInternalEvent(EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL, 3, ex.toString());
        } catch (SAXException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.newInternalEvent(EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL, 4, ex.toString());
        }
        return false;
    }

    /**
     * Verifica se o servi�o foi registrado
     *
     * @param backendService
     * @return
     * @throws SQLException
     */
    public boolean isSensingModuleRegistred(Service backendService) throws SQLException {
        // Busca os dado do servi�o backend
        Entity entity = this.dataManager.getEntityDAO()
                .getEntity(DeviceDAO.COMPONENT_ID, DeviceDAO.ENTITY_ID_OF_SERVICE_REGISTRATION);
        // busca os estados da entidade
        entity.setStateModels(this.dataManager.getEntityStateDAO().getEntityStateModels(entity));
        // Verifica se todas as informa��es est�o cadastradas, 
        // se qualquer uma delas n�o estiver retorna false;
        for (State s : entity.getStates()) {
            if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_SERVICE_UID) {
                if (!backendService.getServiceUID().equals(s.getCurrentValue())) {
                    return false;
                }
            } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_REMOTE_PASSWORD) {
                if (s.getCurrentValue().equals(s.getDataType().getInitialValue())) {
                    return false;
                }
            } else if (s.getModelId() == DeviceDAO.STATE_ID_OF_SERVICE_REGISTRATION_FOR_APPLICATION_UID) {
                if (!backendService.getApplicationUID().equals(s.getCurrentValue())) {
                    return false;
                }
            }
        }
        // retorna verdadeiro se as informa��es existem
        return true;
    }

    public HashMap<String, Object> discoverDeviceStates() {
        // primeiro verifica se o objeto de descoberta foi atribu�do
        if (this.OSDiscovery != null) {
            // retorna o que for descoberto
            return this.OSDiscovery.discovery();
        }
        return null;
    }

    public void setDeviceKnowledgeRepresentationModel(Object o, String dataType) {
        this.dataManager.setKnowledgeRepresentation(o, dataType);
    }

    public void setOSDiscovery(OperatingSystemDiscovery OSDiscovery) {
        this.OSDiscovery = OSDiscovery;
    }

    private void deviceDiscovery() {
        try {
            // carrega as informa��es b�sicas da entidade do dispositivo
            Entity entity = this.dataManager.getEntityDAO().getEntity(
                    DeviceDAO.COMPONENT_ID,
                    DeviceDAO.ENTITY_ID_OF_BASIC_DEVICE_INFORMATIONS);
            entity.setStateModels(this.dataManager.getEntityStateDAO().getEntityStateModels(entity));
            Content content;
            Date discoveryTime = new Date();
            // verifica se os estados j� foram descobertos, se j� foram para o processo de descoberta
            // Se qualquer estado possuir um conte�do, ent�o j� foi executado o processo de descoberta.
            for (State s : entity.getStates()) {
                if (s.getContent() != null) {
                    return;
                }
            }
            // descobre as informa��es do dispositivo baseado no objeto de descoberta do sistema operacional
            HashMap<String, Object> discoveredInformation = discoverDeviceStates();
            // Se retornar nulo armazena os estados iniciais, pois o objeto de descoberta n�o foi inserido, ou n�o encontrou nenhum estado
            if (discoveredInformation != null) {
                // espa�o de armazenamento do dispositivo
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.AVAILABLE_STORAGE_SPACE)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_STORAGE_SYSTEM - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.AVAILABLE_STORAGE_SPACE)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_STORAGE_SYSTEM - 1)
                            .setContent(content);
                }
                // Quantidade de n�cleos de processamento
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.CPU_CORE_COUNT)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORES - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.CPU_CORE_COUNT)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORES - 1)
                            .setContent(content);
                }
                // frequ�ncia dos processadores
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.CPU_CORE_FREQUENCY)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORE_FREQUENCY_CLOCK - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.CPU_CORE_FREQUENCY)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORE_FREQUENCY_CLOCK - 1)
                            .setContent(content);
                }
                // Modelo de CPU
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.CPU_MODEL)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_MODEL - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.CPU_MODEL)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_MODEL - 1)
                            .setContent(content);
                }
                // Modelo do dispositivo
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.DEVICE_MODEL)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_DEVICE_MODEL - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.DEVICE_MODEL)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_DEVICE_MODEL - 1)
                            .setContent(content);
                }
                // Capacidade de armazenamento da bateria do dispositivo
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.BATTERY_CAPACITY)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_BATTERY_CAPACITY - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.BATTERY_CAPACITY)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_BATTERY_CAPACITY - 1)
                            .setContent(content);
                }
                // Sistema operacional nativo
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.NATIVE_OPERATION_SYSTEM)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_NATIVE_OPERATIONAL_SYSTEM - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.NATIVE_OPERATION_SYSTEM)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_NATIVE_OPERATIONAL_SYSTEM - 1)
                            .setContent(content);
                }
                // Mem�ria RAM dispon�vel pelo dispositivo
                if (discoveredInformation.containsKey(OperatingSystemDiscovery.RAM_AVAILABLE)) {
                    content = new Content();
                    content.setValue(Content.parseContent(
                            entity.getStates().get(DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_MEMORY_RAM - 1)
                            .getDataType(),
                            discoveredInformation.get(OperatingSystemDiscovery.RAM_AVAILABLE)));
                    content.setTime(discoveryTime);
                    entity.getStates().get(
                            DeviceDAO.STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_MEMORY_RAM - 1)
                            .setContent(content);
                }
            }
            // Para cada valor checa se for retornado algum valor conhecido sen�o, armazena o valor inicial
            for (State s : entity.getStates()) {
                if (s.getContent() == null) {
                    content = new Content();
                    content.setValue(s.getInitialValue());
                    content.setTime(discoveryTime);
                    s.setContent(content);
                }
                this.dataManager.getEntityStateDAO().insertContent(s);
            }
            // gera um evento contendo todos dados descobertos -- n�o necess�rio, pois � realizado antes do m�dulo de adapta��o ser descoberto    
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void outputCommunicationInterfacesDiscovery() throws SQLException {
        // Verifica interfaces dispon�veis
        for (CommunicationInterface ci : this.dataManager.getCommunicationDAO().getAvailableInterfaces()) {
            Content content = new Content();
            content.setTime(new Date());
            content.setValue(true);
            // verifica se o ID de cada uma dessas inst�ncias existe, se existe relaciona as duas sen�o cria uma nova inst�ncia.
            Instance instance = this.dataManager.getInstanceDAO().getInstance(
                    ci.getId(), CommunicationDAO.ENTITY_ID_OF_OUTPUT_COMMUNICATION_INTERFACES, CommunicationDAO.COMPONENT_ID);
            if (instance != null) {
                ci.setInstance(instance);
                for (State s : instance.getStates()) {
                    if (s.getModelId() == CommunicationDAO.STATE_ID_OF_OUTPUT_COMMUNICATION_INTERFACE_IS_ENABLED
                            // verifica se o componente j� foi ativado, se foi n�o precisa atualizar o estado 
                            && ((s.getContent() == null) ? true : !((Boolean) s.getContent().getValue()))) {
                        s.setContent(content);
                        this.dataManager.getInstanceDAO().insertContent(s);
                        break;
                    }
                }
            } else { // cria uma nova inst�ncia se ela n�o estiver cadastrada
                instance = new Instance(
                        0,
                        ci.getName(),
                        CommunicationInterface.class.toString());
                instance.setEntity(this.dataManager.getEntityDAO().getEntity(CommunicationDAO.COMPONENT_ID, CommunicationDAO.ENTITY_ID_OF_OUTPUT_COMMUNICATION_INTERFACES));
                // busca o maior model ID dos estados e adiciona + 1 para atribuir na interface
                List<State> states = this.dataManager.getInstanceDAO().getInstanceStates(instance);
                int maior = 0;
                for (State s : states) {
                    if (s.getModelId() > maior) {
                        maior = s.getModelId();
                    }
                }
                instance.setModelId(maior + 1); // atribui o pr�ximo model_id vafo
                this.dataManager.getInstanceDAO().insert(instance);
                // Pega os estados que s�o de inst�ncia
                states = this.dataManager.getEntityStateDAO().getInitialModelInstanceStates(instance.getEntity());
                instance.setStates(states);
                for (State s : states) {
                    this.dataManager.getInstanceDAO().insertState(s, instance);
                }
                ci.setInstance(instance);
                for (State s : instance.getStates()) {
                    if (s.getModelId() == CommunicationDAO.STATE_ID_OF_OUTPUT_COMMUNICATION_INTERFACE_IS_ENABLED) {
                        s.setContent(content);
                        this.dataManager.getInstanceDAO().insertContent(s);
                        break;
                    }
                }
            }
        }
    }

    private void inputCommunicationInterfacesDiscovery() throws SQLException {
        // Verifica interfaces dispon�veis
        for (PushServiceReceiver psr : this.communicationManager.getPushServiceReveivers()) {
            Content content = new Content();
            content.setTime(new Date());
            content.setValue(true);
            // verifica se o ID de cada uma dessas inst�ncias existe, se existe relaciona as duas sen�o cria uma nova inst�ncia.
            Instance instance = this.dataManager.getInstanceDAO().getInstance(
                    psr.getId(), CommunicationDAO.ENTITY_ID_OF_INPUT_COMMUNICATION_INTERFACES, CommunicationDAO.COMPONENT_ID);
            if (instance != null) {
                psr.setInstance(instance);
            } else { // cria uma nova inst�ncia se ela n�o estiver cadastrada
                instance = new Instance(
                        0,
                        psr.getDescription(),
                        CommunicationInterface.class.toString());
                instance.setEntity(this.dataManager.getEntityDAO().getEntity(CommunicationDAO.COMPONENT_ID, CommunicationDAO.ENTITY_ID_OF_INPUT_COMMUNICATION_INTERFACES));
                // busca o maior model ID dos estados e adiciona + 1 para atribuir na interface
                List<State> states = this.dataManager.getInstanceDAO().getInstanceStates(instance);
                int maior = 0;
                for (State s : states) {
                    if (s.getModelId() > maior) {
                        maior = s.getModelId();
                    }
                }
                instance.setModelId(maior + 1); // atribui o pr�ximo model_id vafo
                this.dataManager.getInstanceDAO().insert(instance);
                // Pega os estados que s�o de inst�ncia
                states = this.dataManager.getEntityStateDAO().getInitialModelInstanceStates(instance.getEntity());
                instance.setStates(states);
                for (State s : states) {
                    this.dataManager.getInstanceDAO().insertState(s, instance);
                }
                psr.setInstance(instance);
            }
            try {
                // Descobre o endere�o atual e adiciona no estado correspondente
                psr.addressDiscovery();
                HashMap<String, String> interfaceConfigurations = psr.getInterfaceConfigurations();
                content = new Content(interfaceConfigurations);
                for (State s : instance.getStates()) {
                    if (s.getModelId() == CommunicationDAO.STATE_ID_OF_INPUT_COMMUNICATION_INTERFACE_CONFIGURATIONS) {
                        s.setContent(content);
                        this.dataManager.getInstanceDAO().insertContent(s);
                        break;
                    }
                }
            } catch (IOException ex) {
                if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                    Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void addSupportedInputCommunicationInterface(PushServiceReceiver inputCommunicationInterface) {
        communicationManager.addPushServiceReceiver(inputCommunicationInterface);
    }

    public List<Service> getRemoteServices() {
        try {
            return this.getDataManager().getServiceDAO().getDeviceServices();
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new Error(ex);
        }
    }

    public Service getBackendService() {
        try {
            return this.getDataManager().getServiceDAO().getService(1);
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new Error(ex);
        }
    }

    /**
     * Adiciona um servi�o que n�o necessita de agente
     *
     * @param service
     */
    public void insertRemoteService(Service service) {
        insertRemoteService(service, AgentType.NO_RELATED_AGENT);
    }

    /**
     * Adiciona um servi�o e cria o agente baseado em configura��es padr�o e o
     * tipo de agente passado. Caso o tipo de agente for 0 ent�o n�o � criado
     * nenhum agente
     *
     * @param service
     * @param agentType
     * @param agentLayer
     * @param relativeAddress
     */
    public void insertRemoteService(Service service, int agentType, int agentLayer, String relativeAddress) {
        try {
            if (AgentType.NO_RELATED_AGENT == agentType) {
                this.dataManager.getServiceDAO().insert(service);
            } else {
                Agent agent = new Agent();

                agent.setAgentType(this.dataManager.getAgentTypeDAO().get(agentType));

                if (agent.getAgentType() == null) {
                    throw new Error("Specified agent type not exists.");
                }
                agent.setDescription(agent.getAgentType().getDescription());
                agent.setLayer(agentLayer);
                agent.setRelativeAddress(relativeAddress);
                agent.setService(service);
                service.setAgent(agent);
                this.dataManager.getServiceDAO().insert(service);
                this.dataManager.getAgentDAO().insert(agent);
            }
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void insertRemoteService(Service service, int agentType) {
        this.insertRemoteService(service, agentType, TargetOrigin.SYSTEM_LAYER, "/");
    }

    @Override
    public Device getBaseDeviceStructure() {
        try {
            return this.dataManager.getDeviceDAO().getDeviceModel(dataManager);
        } catch (SQLException ex) {
            if (DeveloperSettings.SHOW_EXCEPTION_ERRORS) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public List<ComponentManager> getComponentManagers() {
        return enabledComponentManagers;
    }

    @Override
    public void addComponentManager(ComponentManager componentManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Inicia todos os servi�os da UrboSenti. Se estes j� estiverem
     * inicializados n�o faz nada.
     *
     * @throws IOException
     */
    public void startUrboSentiServices() throws IOException, SQLException {
        // if is not running execute all threads
        if (!isRunning) {
            // Cria as threads
            Thread systemHandler = new Thread(adaptationManager);

            /**
             * *** Configura��es necess�rias para os componentes ****
             */
            // tratador de eventos do sistema
            if(this.getAdaptationManager()!=null){
                this.getAdaptationManager().start();
            }
            // Registrar do n� de sensoriamento movel no backend server :
            // 1 - busca o servidor backend cadastrado no conhecimento inicial
            Service backendServer = this.getBackendService();
            // 2 - Registrar no Servidor Backend
            this.registerSensingModule(backendServer);
            // 3 - Adicionar o servidor da aplica��o como servidor para upload na fun��o de servi�os
            // busca o servidor de upload respectivo, se n�o possui, cria um e adiciona

            /**
             * ************** Iniciar os servi�os *************
             */
            // interfaces de comunica��o de entrada
            for (PushServiceReceiver receivers : communicationManager.getPushServiceReveivers()) {
                receivers.start();
            }
            // tratador de eventos do sistema
            systemHandler.start();
            // servi�o de upload de relatos
            this.communicationManager.startAllCommunicationServices();
            // Evento que os servi�os est�o em funcionamento
            this.newInternalEvent(EVENT_DEVICE_SERVICES_INITIATED);
            // Service now is running
            this.isRunning = true;
        }
    }

    /**
     * Para todos os servi�os da UrboSenti
     */
    public void stopUrboSentiServices() {
        // Para o adaptation Manager
        if(this.getAdaptationManager()!=null){
            this.adaptationManager.stop();
        }
        // Para  servi�o de Upload
        this.communicationManager.stopAllCommunicationServices();
        // Para os listeners
        for (PushServiceReceiver receivers : communicationManager.getPushServiceReveivers()) {
            receivers.stop();
        }
        // Calcela todas as triggers de eventos
        this.eventManager.stopAllTriggers();
        // troca o estado atual para fora de execu��o
        this.isRunning = false;
        // Evento que os servi�os est�o parados
        this.newInternalEvent(EVENT_DEVICE_SERVICES_STOPPED);
    }

    public Boolean isRunning() {
        return isRunning;
    }

    public void setUpCommunicationUrboSentiServices() throws SQLException {
        Entity entity;
        // pega a inst�ncia de servi�o de upload para o backend, se n�o existir cria
        Instance instance = dataManager.getInstanceDAO().getInstance(1, CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS, COMMUNICATION_COMPONENT_ID);
        if (instance == null) {
            instance = new Instance(1, "Backend upload service", "urbosenti.core.communication.UploadService");
            entity = this.dataManager.getEntityDAO().getEntity(CommunicationDAO.ENTITY_ID_OF_SERVICE_OF_UPLOAD_REPORTS, COMMUNICATION_COMPONENT_ID);
            instance.setStates(this.dataManager.getEntityStateDAO().getInitialModelInstanceStates(entity));
            for (State s : instance.getStates()) {
                if (s.getId() == CommunicationDAO.STATE_ID_OF_UPLOAD_PERIODIC_REPORTS_SERVICE_ID) {
                    s.setContent(new Content(this.getBackendService().getId()));
                }
            }
            this.dataManager.getInstanceDAO().insert(instance);
        }
        // atribui a inst�ncia de servi�o de upload
        this.communicationManager.addUploadServer(new UploadService(this.getBackendService(), communicationManager, instance));
        // cria a inst�ncia dos demais servi�os de upload --- Trabalho futuro
        // cria as inst�ncias de servi�os de reconex�o
        instance = dataManager.getInstanceDAO().getInstance(1, CommunicationDAO.ENTITY_ID_OF_RECONNECTION, COMMUNICATION_COMPONENT_ID);
        this.communicationManager.setGeneralReconectionService(new ReconnectionService(communicationManager, dataManager.getCommunicationDAO().getAvailableInterfaces(), instance));
    }

}
