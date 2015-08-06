/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import urbosenti.adaptation.ExecutionPlan;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.Message;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.InteractionModel;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.State;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;

/**
 *
 * @author Guilherme
 */
public final class AdaptationDAO {

    public final static int COMPONENT_ID = 5;
    public static final int ENTITY_ID_OF_SYSTEM_PERFORMANCE_REPOSTS = 1;
    public static final int STATE_ID_OF_SYSTEM_PERFORMANCE_REPOSTS_LAST_GENERATED_REPORT_DATED = 1;
    public static final int STATE_ID_OF_SYSTEM_PERFORMANCE_REPOSTS_LAST_SENT_REPORT_DATED = 2;
    public static final int ENTITY_ID_STATES_OF_CONTROL = 2;
    public static final int STATE_ID_STATES_OF_CONTROL_IS_REGISTRATED_TO_RECEIVE_MAXIMUM_UPLOAD_RATE = 1; // vai para a instância

    private final Connection connection;
    private PreparedStatement stmt;
    private final DataManager dataManager;
    private List<AgentCommunicationLanguage> acls;

    public AdaptationDAO(Connection connection, DataManager dataManager) {
        this.dataManager = dataManager;
        this.connection = connection;
        this.acls = null ;
    }

    public void populateAcls() throws SQLException {
        if (acls == null) {
            this.acls = this.dataManager.getAgentCommunicationLanguageDAO().getAgentCommunicationLanguages();
            {
                for (AgentCommunicationLanguage acl : acls) {
                    acl.setCommunicativeActs(this.dataManager.getCommunicativeActDAO().getCommunicativeActs(acl));
                }
            }
        }
    }

    public Component getComponentDeviceModel() throws SQLException {
        Component deviceComponent = null;
        EntityStateDAO stateDAO = new EntityStateDAO(connection);
        String sql = "SELECT components.description as component_desc, code_class, entities.id as entity_id, "
                + " entities.description as entity_desc, entity_type_id, entity_types.description as type_desc\n"
                + " FROM components, entities, entity_types\n"
                + " WHERE components.id = ? and component_id = components.id and entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, COMPONENT_ID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (deviceComponent == null) {
                deviceComponent = new Component();
                deviceComponent.setId(COMPONENT_ID);
                deviceComponent.setDescription(rs.getString("component_desc"));
                deviceComponent.setReferedClass(rs.getString("code_class"));
            }
            Entity entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setDescription(rs.getString("entity_desc"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc"));
            entity.setEntityType(type);
            entity.setStates(stateDAO.getEntityStates(entity));
            deviceComponent.getEntities().add(entity);
        }
        rs.close();
        stmt.close();
        return deviceComponent;
    }

    /**
     * Entrai a mensagem do evento. O evento passado por parâmetro representa o
     * evento id=4 do componente de comunicação referente ao recebimento de
     * messagens. Esse evento é um evento de interação e os parâmetros de
     * entrada são:
     * <ul><li>message: urbosenti.core.communication.Message, contém a mensagem,
     * bem como quem enviou a mensagem</li>
     * <li>sender: urbosenti.core.communication.Address, contém o endereço de
     * quem enviou a mensagem</li></ul>
     *
     * @param event
     * @return
     * @throws ClassCastException
     * @throws NumberFormatException
     * @throws SQLException
     * @throws Exception
     */
    public Event extractInteractionFromMessageEvent(Event event) throws ClassCastException, NumberFormatException, SQLException, Exception {
        Address sender = (Address) event.getParameters().get("sender");
        Message message = (Message) event.getParameters().get("messege");
        AgentCommunicationLanguage usedAgentCommunicationLanguage = null;
        /**
         * **** Validações ******
         */
        // verifica se o UID da mensagem existe no sistema, se sim retorna o agente referente e o serviço
        Service service = dataManager.getServiceDAO().getServiceByUid(message.getOrigin().getUid());
        // se volta null o serviço não está cadastrado
        if (service == null) {
            throw new Exception("Service UID '" + message.getOrigin().getUid() + "' not found.");
        }
        // se retorna null, o serviço não possui agente para interação
        if (service.getAgent() == null) {
            // caso não
            // excessão agente não registrado
            throw new Exception("Agent of service UID '" + message.getOrigin().getUid() + "' not was found.");
        }
        /**
         * *** Extrair a mensagem *****
         */
        /* Formato:
         <content>
         <acl>fipa</acl>
         <interactionId>1</interactionId>
         <message>
         <fipa-message act="inform" >
         <ontology>UrboSenti 1.0</ontology>
         <protocol>UrboSenti-interaction-1.0</protocol>
         <language>json</language> 
         <content>{chave:"valor"}</content>
         </fipa-message>
         </message>
         </content> */
        // extrai a acl e verifica qual a linguagem, verificando se o tipo de agente suporta, caso não, exceção
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(message.getContent())));
        // <content>
        Element root = doc.getDocumentElement(), messageContent;
        // <acl>fipa</acl>
        // testa se a ACL é conhecida, se não for gera exceção, se sim retorna o id
        for (AgentCommunicationLanguage acl : acls) {
            if (dataManager.getAgentCommunicationLanguageDAO()
                    .isAgentCommunicationLanguageKnown(acl, root.getElementsByTagName("acl").item(0).getTextContent())) {
                usedAgentCommunicationLanguage = acl;
                break;
            } else {
                throw new Exception("Agent Communication Language '" + root.getElementsByTagName("acl").item(0).getTextContent()
                        + "' from UID:'" + message.getOrigin().getUid()
                        + "', addess: '" + message.getOrigin().getAddress() + "' is not supported.");
            }
        }
        // <interactionId>1</interactionId>
        int interactionId = Integer.parseInt(root.getElementsByTagName("interactionId").item(0).getTextContent());
        InteractionModel interactionModel = dataManager.getAgentTypeDAO().getInteractionModel(interactionId);
        if (interactionModel == null) {
            throw new Exception("Interaction model referred by the value interactionId:'" + interactionId + "' was not found.");
        }
        /**
         * *** Processar conteúdo segundo Agent Communication Language *****
         */
        // se for FIPA executa esse processo, se houvesse outras a implementação seria dada por esta
        // extrai a mensagem e processa na linguagem conhecida (FIPA)
        if (usedAgentCommunicationLanguage.getId() == AgentCommunicationLanguage.AGENT_COMMUNICATIVE_LANGUAGE_FIPA_ID) {
            // <fipa-message>
            messageContent = (Element) root.getElementsByTagName("fipa-message").item(0);
            /**
             * A informação de que ato comunicativo está sendo passado é
             * despresível por causa do interactionId
             * <fipa-message act="inform" >
             * String communicativeAct = messageContent.getAttribute("act");
             */
            /**
             * Elementos estáticos por enquanto, não há necessidade de
             * implementar dinamicamente. Trabalho futuro, deixar dinâmico.
             * <ontology>UrboSenti 1.0</ontology>
             * <protocol>UrboSenti-interaction-1.0</protocol>
             * <language>xml</language>
             */
            // Processar conteúdo da mensagem adicionando os elementos como parâmetros no HashMap do objetivo event
            // <content><chave>valor</chave></content>
            Element content = (Element) messageContent.getElementsByTagName("content").item(0);
            for (Parameter p : interactionModel.getParameters()) {
                if (content.getElementsByTagName(p.getLabel()).item(0) != null) {
                    event.getParameters().put(p.getLabel(),
                            Content.parseContent(
                                    p.getDataType(),
                                    content.getElementsByTagName(p.getLabel()).item(0).getTextContent()));
                }
            }
        }
        // adiciona o interaction ID no evento
        event.setId(interactionId);
        // retorna o evento de interação com os parâmetros e o interaction id referentes do Interaction Model
        return event;
    }

//    private Event proccessFIPAInteractionMessage (Element root){
//        
//    }
    public Action makeInteractionMessage(Action action) throws ClassCastException, NumberFormatException, SQLException, Exception {
        String finalString;
        InteractionModel interactionModel = (InteractionModel) action.getParameters().get("interactionModel");
        /**
         * *** Extrair a mensagem *****
         */
        //<content> -- será adicionado na mensagem
        // <acl>fipa</acl>
        finalString = "<acl>fipa</acl>";
        // <interactionId>1</interactionId>
        finalString += "<interactionId>" + interactionModel.getId() + "</interactionId>";
        // <message>
        finalString += "<message>";
        //<fipa-message act="inform" >
        finalString += "<fipa-message act=\"" + interactionModel.getCommunicativeAct().getDescription() + "\" >";
        // <ontology>UrboSenti 1.0</ontology>
        finalString += "<ontology>UrboSenti 1.0</ontology>";
        // <protocol>UrboSenti-interaction-1.0</protocol>
        finalString += "<protocol>UrboSenti-interaction-1.0</protocol>";
        // <language>xml</language> 
        finalString += "<language>xml</language>";
        // <content>{chave:"valor"}</content>
        finalString += "<content>";
        for (Parameter p : interactionModel.getParameters()) {
            finalString += "<" + p.getLabel() + ">";
            finalString += p.getContent().getValue().toString();
            finalString += "</" + p.getLabel() + ">";
        }
        finalString += "</content>";
        // </fipa-message>
        finalString += "</fipa-message>";
        // </message>
        finalString += "</message>";
        /**
         * * alterar o action **
         */
        Message message = new Message();
        message.setContent(finalString);
        message.setTarget((Address) action.getParameters().get("target"));
        message.setSubject(Message.SUBJECT_SYSTEM_MESSAGE);
        message.setContentType("text/xml");
        message.setUsesUrboSentiXMLEnvelope(true);
        // Preparar ação para envio: envio de mensagens assíncrona
        action.getParameters().put("message", message);
        if (action.isSynchronous()) {
            action.setId(23);
        } else {
            action.setId(22);
        }
        action.setOrigin(Address.LAYER_SYSTEM);
        action.setTargetComponentId(CommunicationDAO.COMPONENT_ID);
        action.setTargetEntityId(CommunicationDAO.ENTITY_ID_OF_SENDING_MESSAGES);
        action.setActionType(Event.INTERATION_EVENT);
        return action;
    }

    public void updateDecision(FeedbackAnswer response, Event event, Action actionToExecute, ExecutionPlan ep) throws SQLException {
        // regsitra a ação
        this.dataManager.getActionModelDAO().insertAction(response, event, actionToExecute, ep);
        // se a resposta da ação foi sucesso, verifica  se algum dos parâmetros do actionModel é relacionado com algum estado
        if (response.getId() == FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL) {
            Content content;
            ActionModel actionModel = this.dataManager.getActionModelDAO().getAction(actionToExecute.getDataBaseId());
            for (Parameter p : actionModel.getParameters()) {
                content = new Content(Content.parseContent(
                        p.getRelatedState().getDataType(),
                        actionToExecute.getParameters().get(p.getLabel())),
                        response.getTime());
                //verifica  se algum dos parâmetros do actionModel é relacionado com algum estado
                if (p.getRelatedState() != null) {
                    // verifica se é estado de instância ou de entidade
                    if (p.getRelatedState().isStateInstance()) {
                        Instance instance = dataManager.getInstanceDAO().getInstance(actionToExecute.getTargetComponentId(),actionToExecute.getTargetEntityId(),
                                 (Integer)actionToExecute.getParameters().get("instanceId"));
                        for(State s : instance.getStates()){
                            if(s.getModelId() == p.getRelatedState().getModelId()){
                                s.setContent(content);
                                this.dataManager.getInstanceDAO().insertContent(s);
                                break;
                            }
                        }
                    } else {
                        p.getRelatedState().setContent(content);
                        this.dataManager.getEntityStateDAO().insertContent(p.getRelatedState());
                    }
                }
            }

        }
    }

}
