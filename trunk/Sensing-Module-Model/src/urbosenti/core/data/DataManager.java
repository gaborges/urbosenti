/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.io.File;
import urbosenti.core.data.dao.CommunicationDAO;
import urbosenti.core.data.dao.UserDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.data.dao.ActionDAO;
import urbosenti.core.data.dao.AgentAddressTypeDAO;
import urbosenti.core.data.dao.AgentCommunicationLanguageDAO;
import urbosenti.core.data.dao.AgentDAO;
import urbosenti.core.data.dao.AgentTypeDAO;
import urbosenti.core.data.dao.CommunicativeActDAO;
import urbosenti.core.data.dao.ComponentDAO;
import urbosenti.core.data.dao.DataTypeDAO;
import urbosenti.core.data.dao.DeviceDAO;
import urbosenti.core.data.dao.EntityDAO;
import urbosenti.core.data.dao.EntityTypeDAO;
import urbosenti.core.data.dao.EventDAO;
import urbosenti.core.data.dao.ImplementationTypeDAO;
import urbosenti.core.data.dao.InstanceDAO;
import urbosenti.core.data.dao.InteractionDirectionDAO;
import urbosenti.core.data.dao.InteractionTypeDAO;
import urbosenti.core.data.dao.ServiceDAO;
import urbosenti.core.data.dao.StateDAO;
import urbosenti.core.data.dao.TargetOriginDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.AsynchronouslyManageableComponent;

/**
 *
 * @author Guilherme
 */
public class DataManager extends ComponentManager implements AsynchronouslyManageableComponent{

    private final List<CommunicationInterface> supportedCommunicationInterfaces;
    private CommunicationDAO communicationDAO;
    private UserDAO userDAO;
    private Object knowledgeRepresentation;
    private String knowledgeDataType;
    private EntityTypeDAO entityTypeDAO;
    private DataTypeDAO dataTypeDAO;
    private ImplementationTypeDAO implementationTypeDAO;
    private AgentCommunicationLanguageDAO agentCommunicationLanguageDAO;
    private CommunicativeActDAO communicativeActDAO;
    private InteractionTypeDAO interactionTypeDAO;
    private InteractionDirectionDAO interactionDirectionDAO;
    private TargetOriginDAO targetOriginDAO;
    private AgentAddressTypeDAO agentAddressTypeDAO;
    private DeviceDAO deviceDAO;
    private ServiceDAO serviceDAO;
    private AgentDAO agentDAO;
    private AgentTypeDAO agentTypeDAO;
    private ComponentDAO componentDAO;
    private EntityDAO entityDAO;
    private StateDAO stateDAO;
    private EventDAO eventDAO;
    private ActionDAO actionDAO;
    private InstanceDAO instanceDAO;
    
    public DataManager(DeviceManager deviceManager) {
        super(deviceManager);
        this.supportedCommunicationInterfaces = new ArrayList<CommunicationInterface>();
        this.knowledgeRepresentation = null;
    }
    
    @Override
    public void onCreate() {        
        // Conecta ao banco
        Connection connection = null;
        try {
          Class.forName("org.sqlite.JDBC");
          connection = DriverManager.getConnection("jdbc:sqlite:urbosenti.db");
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        System.out.println("Opened database successfully");
        // conecta
        // Gerente do conhecimento
        StoringGlobalKnowledgeModel kp = new StoringGlobalKnowledgeModel(this);
        try {
            // Cria o banco de dados
            kp.createDataBase(connection);
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Error during the database creation!");
        }
        // instancia todos os DAO;
        communicationDAO = new CommunicationDAO(connection);
        userDAO = new UserDAO(connection);
        // General Definition DAOs
        this.agentTypeDAO = new AgentTypeDAO(connection);
        this.entityTypeDAO = new EntityTypeDAO(connection);
        this.dataTypeDAO = new DataTypeDAO(connection);
        this.implementationTypeDAO = new ImplementationTypeDAO(connection);
        this.agentCommunicationLanguageDAO = new AgentCommunicationLanguageDAO(connection);
        this.communicativeActDAO = new CommunicativeActDAO(connection);
        this.interactionTypeDAO = new  InteractionTypeDAO(connection);
        this.interactionDirectionDAO = new InteractionDirectionDAO(connection);
        this.targetOriginDAO = new TargetOriginDAO(connection);
        this.agentAddressTypeDAO = new AgentAddressTypeDAO(connection);
        this.deviceDAO = new DeviceDAO(connection);
        this.serviceDAO = new ServiceDAO(connection);
        this.agentDAO = new AgentDAO(connection);
        // Device DAO
        this.componentDAO = new ComponentDAO(connection);
        this.entityDAO = new EntityDAO(connection);
        this.stateDAO = new StateDAO(connection);
        this.eventDAO = new EventDAO(connection);
        this.actionDAO = new ActionDAO(connection);
        this.instanceDAO = new InstanceDAO(connection);
        // Carrega interfaces de comunicação disponíveis (testa disponibilidade antes de executar - lookback)
        for(CommunicationInterface ci : supportedCommunicationInterfaces){
            try {
                if(ci.isAvailable()){
                    this.communicationDAO.addAvailableCommunicationInterface(ci);
                     System.out.println("Interface successfully added: "+ci.getName());
                }
            } catch (UnsupportedOperationException ex){
                System.out.println("Not implemented yet: "+ci.getName());
                //Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "Not implemented yet.", ex);
            } catch (IOException ex) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        // Verifica se o conhecimento foi adicionado
        if (this.knowledgeRepresentation == null){
            throw new Error("Knowledge representation do not specified!");
        }
        // Carregar dados e configurações que serão utilizados para execução em memória
        if (this.knowledgeDataType.equals("xmlFile")) {
            File file = (File) this.knowledgeRepresentation ;
            try {
                /*
                 Fazer metodos de validação depois. Eles devem testar se tem todos os atributos obrigatórios,
                 e se existem os valores especificados com as configurações gerais e pependências
                 */
//            if(kp.validateGeneralConfigurations(file)) throw new Error ("");
//            if(kp.validateDeviceModel(file)) System.exit(-1);
//            if(kp.validateAgentModel(file)) System.exit(-1);
                /*
                 processa o arquivo de entrada com o modelo de conhecimento e coloca em memória. Atualmente pronto.
                 */
                kp.loadingGeneralDefinitions(file);
                kp.loadingDevice(file);
                kp.loadingAgentModels(file);
                /*
                 grava no banco de dados os dados processados -- falta fazer. Primeiro fazer gerar os SQLs, depois fazer os DAO
                 OBS.: Sempre que esses métodos são executados ele verifica a versão salva dos modeloas anteriores e substitui somente 
                 caso o conhecimento de entrada possuir uma versão mais recente, ou maior.
                 */
                kp.saveGeneralDefinitions(connection);
                kp.saveDevice(connection);
                kp.saveAgentModels(connection);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new Error("Knowledge data type specified not supported!");
        }
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
        // Descobrir todo o conhecimento e criar o banco
    }
    
    public CommunicationDAO getCommunicationDAO(){
        
        return communicationDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addSupportedCommunicationInterface(CommunicationInterface ci){
        supportedCommunicationInterfaces.add(ci);
    }

    public void setKnowledgeRepresentation(Object o, String dataType) {
        this.knowledgeRepresentation = o;
        this.knowledgeDataType = dataType;
    }

    public EntityTypeDAO getEntityTypeDAO() {
        return entityTypeDAO;
    }

    public DataTypeDAO getDataTypeDAO() {
        return dataTypeDAO;
    }

    public ImplementationTypeDAO getImplementationTypeDAO() {
        return implementationTypeDAO;
    }

    public AgentCommunicationLanguageDAO getAgentCommunicationLanguageDAO() {
        return agentCommunicationLanguageDAO;
    }

    public CommunicativeActDAO getCommunicativeActDAO() {
        return communicativeActDAO;
    }

    public InteractionTypeDAO getInteractionTypeDAO() {
        return interactionTypeDAO;
    }

    public InteractionDirectionDAO getInteractionDirectionDAO() {
        return interactionDirectionDAO;
    }

    public TargetOriginDAO getTargetOriginDAO() {
        return targetOriginDAO;
    }

    public AgentAddressTypeDAO getAgentAddressTypeDAO() {
        return agentAddressTypeDAO;
    }

    public DeviceDAO getDeviceDAO() {
        return deviceDAO;
    }

    public ServiceDAO getServiceDAO() {
        return serviceDAO;
    }

    public AgentDAO getAgentDAO() {
        return agentDAO;
    }

    public AgentTypeDAO getAgentTypeDAO() {
        return agentTypeDAO;
    }

    public ComponentDAO getComponentDAO() {
        return componentDAO;
    }

    public EntityDAO getEntityDAO() {
        return entityDAO;
    }

    public StateDAO getStateDAO() {
        return stateDAO;
    }

    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public ActionDAO getActionDAO() {
        return actionDAO;
    }

    public InstanceDAO getInstanceDAO() {
        return instanceDAO;
    }
    
}
