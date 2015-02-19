/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import java.util.Date;
import java.util.HashMap;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.concerns.ConcernManager;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.data.DataManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.ApplicationEvent;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemEvent;
import urbosenti.localization.LocalizationManager;
import urbosenti.resources.ResourceManager;
import urbosenti.user.UserManager;

/**
 *
 * @author Guilherme
 */
public class DeviceManager extends ComponentManager implements AsynchronouslyManageableComponent {

    /**
     * int EVENT_DEVICE_REGISTRATION_SUCCESSFUL = 1;
     *
     * <ul><li>id: 1</li>
     * <li>evento: Registro efetuado com sucesso</li>
     * <li>parâmetros: UID da Aplicação, Tempo para expiração</li></ul>
     */
    public static final int EVENT_DEVICE_REGISTRATION_SUCCESSFUL = 1;
    /**
     * int EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL = 2;
     *
     * <ul><li>id: 2</li>
     * <li>evento: Erro ao registrar ao servidor</li>
     * <li>parâmetros: Código do Erro, Descrição</li></ul>
     */
    public static final int EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL = 2;
    /**
     * int EVENT_DEVICE_SERVICES_INITIATED = 3;
     *
     * <ul><li>id: 3</li>
     * <li>evento: Serviços Iniciados</li>
     * <li>parâmetros: Nenhum</li></ul>
     */
    public static final int EVENT_DEVICE_SERVICES_INITIATED = 3;
    /**
     * int EVENT_DEVICE_SERVICES_STOPPED = 4;
     *
     * <ul><li>id: 4</li>
     * <li>evento: Serviços Parados</li>
     * <li>parâmetros: Nenhum</li></ul>
     */
    public static final int EVENT_DEVICE_SERVICES_STOPPED = 4;
    /**
     * int EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED = 5;
     *
     * <ul><li>id: 5</li>
     * <li>evento: Informações do dispositivo coletadas com sucesso</li>
     * <li>parâmetros: Espaço disponível de Armazenamento (B), Núcleos no CPU,
     * Frequência por núcleos do CPU, Modelo do CPU, S.O. Nativo, Memória RAM
     * (B), Modelo de dispositivo, Capacidade da Bateria</li></ul>
     */
    public static final int EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED = 5;
    /**
     * int EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION = 6;
     *
     * <ul><li>id: 6</li>
     * <li>evento: Não foi possível acessar as informações</li>
     * <li>parâmetros: Código do Erro, Descrição</li></ul>
     */
    public static final int EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION = 6;
    /* Identificadores dos componentes internos */
    public final static int DEVICE_COMPONENT = 1;
    public final static int DATA_COMPONENT = 2;
    public final static int EVENTS_COMPONENT = 3;
    public final static int COMMUNICATION_COMPONENT = 4;
    public final static int ADAPTATION_COMPONENT = 5;
    public final static int USER_COMPONENT = 6;
    public final static int CONTEXT_COMPONENT = 7;
    public final static int RECURSOS_COMPONENT = 8;
    public final static int LOCALIZACAO_COMPONENT = 9;
    public final static int CONCERNS_COMPONENT = 10;
    private CommunicationManager communicationManager = null;
    private DataManager dataManager = null;
    private EventManager eventManager = null;
    private ContextManager contextManager = null;
    private AdaptationManager adaptationManager = null;
    private UserManager userManager = null;
    private LocalizationManager localizationManager = null;
    private ResourceManager resourceManager = null;
    private ConcernManager concernManager = null;
    private String UID;

    /**
     *
     * @return
     */
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public DeviceManager() {
        super(new EventManager());
        this.eventManager = super.getEventManager();
        this.communicationManager = new CommunicationManager(this);
        this.dataManager = new DataManager(this);
    }

    public DeviceManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.communicationManager = new CommunicationManager(this);
        this.dataManager = new DataManager(this);
    }

    protected boolean adaptationDiscovery(AdaptationManager adaptationManager) {
        this.adaptationManager = adaptationManager;
        return adaptationManager.discovery(this);
    }

    protected boolean adaptationDiscovery(AdaptationManager adaptationManager, ContextManager contextManager) {
        this.contextManager = contextManager;
        this.adaptationManager = adaptationManager;
        return adaptationManager.discovery(this, contextManager);
    }

    protected boolean adaptationDiscovery(AdaptationManager adaptationManager, UserManager userManager) {
        this.adaptationManager = adaptationManager;
        this.userManager = userManager;
        return adaptationManager.discovery(this, userManager);
    }

    protected boolean adaptationDiscovery(AdaptationManager adaptationManager, ContextManager contextManager, UserManager userManager) {
        this.contextManager = contextManager;
        this.adaptationManager = adaptationManager;
        this.userManager = userManager;
        return adaptationManager.discovery(this, contextManager, userManager);
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
    }

    public void enableAdaptationComponent() {
        this.adaptationManager = new AdaptationManager(this);
        this.eventManager.setSystemHandler(adaptationManager);
        this.eventManager.enableSystemHandler();
    }

    public void enableUserComponent() {
        this.userManager = new UserManager(this);
    }

    public void enableLocalizationComponent() {
        this.localizationManager = new LocalizationManager(this);
    }

    public void enableResourceComponent() {
        this.resourceManager = new ResourceManager(this);
    }

    public void enableConcernComponent() {
        this.concernManager = new ConcernManager(this);
    }

    @Override
    public void onCreate() { // Before the execution
        this.dataManager.onCreate(); // Deve ser o primeiro a ser executado. Os demais irão utilizar esse gerente para acessar dados
        this.communicationManager.onCreate(); // Instancia os monitores gerais de funcionalidade e os atuadores gerais

        // ativação dos Módulos sobdemanda
        if (this.userManager != null) {
            this.userManager.onCreate();
        }
        if (this.contextManager != null) {
            this.contextManager.onCreate();
        }
        if (this.localizationManager != null) {
            this.localizationManager.onCreate();
        }
        if (this.resourceManager != null) {
            this.resourceManager.onCreate();
        }
        if (this.concernManager != null) {
            this.concernManager.onCreate();
        }

        // adaptation Discovery
        if (adaptationManager != null) {
            if (contextManager != null) {
                if (userManager != null) {
                    adaptationDiscovery(adaptationManager, contextManager, userManager);
                } else {
                    adaptationDiscovery(adaptationManager, contextManager);
                }
            } else if (userManager != null) {
                adaptationDiscovery(adaptationManager, userManager);
            } else {
                adaptationDiscovery(adaptationManager);
            }
            this.adaptationManager.onCreate();
        }
    }

    /**
     * Ações disponibilizadas por esse componente:
     * <ul>
     * <li>Nenhuma ação ainda é suportada</li>
     * </ul>
     *
     * @param action contém objeto ação.
     *
     */
    @Override
    public void applyAction(Action action) {
        // não necessita de ações ainda.
    }

    /**
     * <br><br>Eventos possíveis:
     * <ul>
     * <li>DeviceManager.EVENT_DEVICE_REGISTRATION_SUCCESSFUL - Registro
     * efetuado com sucesso</li>
     * <li>DeviceManager.EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL - Erro ao
     * registrar ao servidor</li>
     * <li>DeviceManager.EVENT_DEVICE_SERVICES_INITIATED - Serviços
     * Iniciados</li>
     * <li>DeviceManager.EVENT_DEVICE_SERVICES_STOPPED - Serviços Parados</li>
     * <li>DeviceManager.EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED -
     * Informações do dispositivo coletadas com sucesso</li>
     * <li>DeviceManager.EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION - Não
     * foi possível acessar as informações</li>
     * </ul>
     *
     * @param eventId identificador do evento citado acima
     * @param params Parâmetros oriundo dos objetos do componente <br><br>
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
                // Parâmetros do evento
                values = new HashMap();
                values.put("uid", params[0]);
                values.put("expirationTime", params[1]);

                // Se o tratador de eventos de sistema estiver ativo gera um evento para ele também
                if (adaptationManager != null) {
                    // Cria o evento de sistema
                    event = new SystemEvent(this);
                    event.setId(1);
                    event.setName("Registro efetuado com sucesso");
                    event.setTime(new Date());
                    event.setValue(values);

                    // envia o evento
                    getEventManager().newEvent(event);
                }

                // Cria o evento de aplicação, alertando a aplicação
                event = new ApplicationEvent(this);
                event.setId(1);
                event.setName("Registro efetuado com sucesso");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_DEVICE_REGISTRATION_UNSUCCESSFUL:
                // Parâmetros do evento
                values = new HashMap();
                values.put("errorCode", params[0]);
                values.put("description", params[1]);

                // Se o tratador de eventos de sistema estiver ativo gera um evento para ele também
                if (adaptationManager != null) {
                    // Cria o evento de sistema
                    event = new SystemEvent(this);
                    event.setId(2);
                    event.setName("Erro ao registrar ao servidor");
                    event.setTime(new Date());
                    event.setValue(values);

                    // envia o evento
                    getEventManager().newEvent(event);
                }

                // Cria o evento de aplicação, alertando a aplicação
                event = new ApplicationEvent(this);
                event.setId(2);
                event.setName("Erro ao registrar ao servidor");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_SERVICES_INITIATED:
                // não possuí parâmetros
                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(3);
                event.setName("Serviços Iniciados");
                event.setTime(new Date());
                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_SERVICES_STOPPED:
                // não possuí parâmetros
                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(4);
                event.setName("Serviços Parados");
                event.setTime(new Date());
                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DEVICE_INFORMATION_SUCCESSFULLY_COLLECTED: // Informações do dispositivo coletadas com sucesso
                // Parâmetros do evento
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
                event.setName("Informações do dispositivo coletadas com sucesso");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_DEVICE_ERROR_WHILE_COLLECTING_INFORMATION: // Não foi possível acessar as informações
                // Parâmetros do evento
                values = new HashMap();
                values.put("errorCode", params[0]);
                values.put("description", params[1]);

                // Cria o evento de sistema
                event = new SystemEvent(this);
                event.setId(6);
                event.setName("Não foi possível acessar as informações");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
        }
    }

    public void addSupportedCommunicationInterface(CommunicationInterface communicationInterface) {
        dataManager.addSupportedCommunicationInterface(communicationInterface);
    }

    public boolean registerSensingModule(Agent backendServer) {
        discoverDeviceStates();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean discoverDeviceStates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void validateDeviceKnowledgeRepresentationModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void validateAgentKnowledgeRepresentationModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setDeviceKnowledgeRepresentationModel(Object o, String dataType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setAgentKnowledgeRepresentationModel(Object o, String dataType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
