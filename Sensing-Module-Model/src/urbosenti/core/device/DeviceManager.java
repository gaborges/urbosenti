/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.device;

import urbosenti.adaptation.AdaptationManager;
import urbosenti.concerns.ConcernManager;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.data.DataManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.EventManager;
import urbosenti.localization.LocalizationManager;
import urbosenti.resources.ResourceManager;
import urbosenti.user.UserManager;

/**
 *
 * @author Guilherme
 */
public class DeviceManager extends ComponentManager implements AsynchronouslyManageableComponent{

    public final static int DEVICE_COMPONENT = 0;
    public final static int DATA_COMPONENT = 1;
    public final static int EVENTS_COMPONENT = 2;
    public final static int COMMUNICATION_COMPONENT = 3;
    public final static int ADAPTATION_COMPONENT = 4;
    public final static int USER_COMPONENT = 5;
    public final static int CONTEXT_COMPONENT = 6;
    public final static int RECURSOS_COMPONENT = 7;
    public final static int LOCALIZACAO_COMPONENT = 8;
    public final static int CONCERNS_COMPONENT = 9;
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
        if (this.userManager!=null) this.userManager.onCreate();
        if (this.contextManager!=null) this.contextManager.onCreate();
        if (this.localizationManager!=null) this.localizationManager.onCreate();
        if (this.resourceManager!=null) this.resourceManager.onCreate();
        if (this.concernManager!=null) this.concernManager.onCreate();
        
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
    
    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addSupportedCommunicationInterface(CommunicationInterface communicationInterface) {
        dataManager.addSupportedCommunicationInterface(communicationInterface);
    }

}
