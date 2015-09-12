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
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.adaptation.ExecutionPlan;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.Message;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.BaseComponentManager;
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
    public static final int ENTITY_ID_OF_ADAPTATION_MANAGEMENT = 1;
    public static final int STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERVAL_TO_REPORT_SYSTEM_FUNCIONS = 1;
    public static final int STATE_ID_OF_ADAPTATION_MANAGEMENT_INTERNAL_TO_DELETE_EXPIRED_MESSAGES = 2;
    public static final int STATE_ID_OF_ADAPTATION_MANAGEMENT_ALLOWED_SEND_REPORT_SYSTEM_FUNCIONS = 3;
    public static final int STATE_ID_OF_ADAPTATION_MANAGEMENT_LAST_REPORTED_DATE = 4;
    public static final int INTERACTION_TO_INFORM_NEW_MAXIMUM_UPLOAD_RATE = 1;
    public static final int INTERACTION_TO_SUBSCRIBE_THE_MAXIMUM_UPLOAD_RATE = 2;
    public static final int INTERACTION_TO_UNSUBSCRIBE_THE_MAXIMUM_UPLOAD_RATE = 3;
    public static final int INTERACTION_OF_FAIL_ON_SUBSCRIBE = 4;
    public static final int INTERACTION_OF_MESSAGE_WONT_UNDERSTOOD = 5;
    public static final int INTERACTION_TO_CONFIRM_REGISTRATION = 6;
    public static final int INTERACTION_TO_REFUSE_REGISTRATION = 7;
    public static final int INTERACTION_TO_CANCEL_REGISTRATION = 8;
    public static final int INTERACTION_TO_INFORM_NEW_INPUT_ADDRESS = 9;
    public static final int INTERACTION_TO_REPORT_SENSING_MODULE_FUNCTIONALITY = 10;
    public static final int FUNCTIONALITY_STATUS_TYPE_INFO = 1;
    public static final int FUNCTIONALITY_STATUS_TYPE_WARNING = 1;
    public static final int FUNCTIONALITY_STATUS_TYPE_ERROR = 1;

    private final Connection connection;
    private PreparedStatement stmt;
    private final DataManager dataManager;
    private List<AgentCommunicationLanguage> acls;

    public AdaptationDAO(Connection connection, DataManager dataManager) {
        this.dataManager = dataManager;
        this.connection = connection;
        this.acls = null;
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
        Message message = (Message) event.getParameters().get("message");
        AgentCommunicationLanguage usedAgentCommunicationLanguage = null;
        boolean aclIsUnkown = false;
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
                aclIsUnkown = true;
                break;
            }
        }
        if (!aclIsUnkown) {
            throw new Exception("Agent Communication Language '" + root.getElementsByTagName("acl").item(0).getTextContent()
                    + "' from UID:'" + message.getOrigin().getUid()
                    + "', address: '" + message.getOrigin().getAddress() + "' is not supported.");
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
            finalString += action.getParameters().get(p.getLabel());
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

    public void updateDecision(FeedbackAnswer response, Event event, Action actionToExecute, ExecutionPlan ep) throws SQLException,java.lang.NullPointerException {
        // regsitra a ação
        this.dataManager.getActionModelDAO().insertAction(response, event, actionToExecute, ep);
        // se a resposta da ação foi sucesso, verifica  se algum dos parâmetros do actionModel é relacionado com algum estado
        if (response.getId() == FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL) {
            Content content;
            ActionModel actionModel = this.dataManager.getActionModelDAO().getActionModel(
                    actionToExecute.getId(),
                    actionToExecute.getTargetEntityId(),
                    actionToExecute.getTargetComponentId());
            // atualiza parâmetros
            for (Parameter p : actionModel.getParameters()) {
                content = new Content(Content.parseContent(
                        p.getDataType(),
                        actionToExecute.getParameters().get(p.getLabel())),
                        response.getTime());
                //verifica  se algum dos parâmetros do actionModel é relacionado com algum estado
                if (p.getRelatedState() != null) {
                    // verifica se é estado de instância ou de entidade
                    if (p.getRelatedState().isStateInstance()) {
                        int instanceId;
                        if (actionToExecute.getParameters().get("interface") != null) {
                            instanceId = (Integer) actionToExecute.getParameters().get("interface");
                        } else {
                            instanceId = (Integer) actionToExecute.getParameters().get("instanceId");
                        }
                        // recuperar a instância
                        Instance instance = dataManager.getInstanceDAO().getInstance(
                                actionToExecute.getTargetComponentId(),
                                actionToExecute.getTargetEntityId(),
                                instanceId);
                        // procurar o estado para salvar o conteúdo
                        for (State s : instance.getStates()) {
                            if (s.getModelId() == p.getRelatedState().getModelId()) {
                                s.setContent(content);
                                this.dataManager.getInstanceDAO().insertContent(s);
                                break;
                            }
                        }
                    } else {
                        // relacionado com estado de entidade. Atualiza diretamente
                        p.getRelatedState().setContent(content);
                        this.dataManager.getEntityStateDAO().insertContent(p.getRelatedState());
                    }
                }
                // Testa se o parâmetro existe e é objegratório, depois salva o conteúdo do parâmetro
                if (actionToExecute.getParameters().get(p.getLabel()) == null && !p.isOptional()) {
                    throw new Error("Parameter " + p.getLabel() + " from the event " + actionModel.getDescription() + " id " + actionModel.getId()
                            + " was not found. Such parameter is not optional!");
                } else {
                    if (actionToExecute.getParameters().get(p.getLabel()) != null) {
                        p.setContent(content);
                        dataManager.getActionModelDAO().insertContent(p, actionToExecute);
                    }
                }
            }
        }
    }

    /**
     *
     * @param instanceId instância
     * @param lastIntervalOfServiceErrors intervalo para busca
     * @return null if do not find any record or the generated action ID if was
     * find.
     * @throws SQLException
     */
    public Integer getLastRecordedErrorFromInstance(int instanceId, long lastIntervalOfServiceErrors) throws SQLException {
        String sql = "SELECT  feedback_id, generated_actions.id as id FROM generated_actions, action_contents, action_parameters "
                + " WHERE  generated_action_id = generated_actions.id AND action_parameter_id = action_parameters.id AND reading_time > ? "
                + " AND reading_value = ? AND label = ? AND action_model_id = ? AND  entity_id = ? AND component_id = ? "
                + " ORDER BY reading_time DESC;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setObject(1, (System.currentTimeMillis() - (lastIntervalOfServiceErrors * 1.3)));
        this.stmt.setObject(2, instanceId);
        this.stmt.setString(3, "instanceId");
        this.stmt.setInt(4, AdaptationManager.ACTION_STORE_INTERNAL_ERROR);
        this.stmt.setInt(5, AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
        this.stmt.setInt(6, AdaptationDAO.COMPONENT_ID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            return rs.getInt("id");
        }
        rs.close();
        stmt.close();
        return null;
    }

    public String getErrorReporting(Date startDate,Date endDate) throws SQLException {
        String json = "{ ";
        // buscar todas as ações de erro a partir da última data até a data atual
        List<Action> actions = dataManager.getActionModelDAO()
                .getActions(AdaptationManager.ACTION_STORE_INTERNAL_ERROR,
                        ENTITY_ID_OF_ADAPTATION_MANAGEMENT, COMPONENT_ID, startDate, endDate);
        List<Action> feedbackErros = dataManager.getActionModelDAO()
                .getActionsFeedbackErrors(startDate, endDate);
        List<Parameter> parameters;
        // se não houver nenhuma ação com erro, cria uma de sucesso. 
        if (actions.isEmpty() && feedbackErros.isEmpty()) {
            json += "\"nodeStatusReported\" : \"stable\"";
        } else {
            // cria erros
            json += "\"nodeStatusReported\" : \"error\", ";
            // para cada ação buscar os parâmetros
            json += "\"errors\" : [ ";
            /*
             Para o erro:
             - Tipo de erro
             - Descrição
             - Tempo
             - Instance id (se houver)
             "message" : ["type":"2", "description":"Instância demorou para iniciar", "time":"1439426402937", "instanceId": 1 ] ,
             */
            for (Action action : actions) {
                json += " { \"message\" : [ ";
                parameters = this.dataManager.getActionModelDAO().getActionParameterContents(action);
                for (Parameter parameter : parameters) {
                    json += " {\"" + parameter.getLabel() + "\": \"" + parameter.getContent().getValue().toString() + "\" }"
                            + ((parameters.get(parameters.size() - 1).getId() == parameter.getId()) ? "" : " ,");

                }
                json += " ] } " + ((actions.get(actions.size() - 1).getDataBaseId() == action.getDataBaseId()) ? "" : ", ");
            }
            if(!feedbackErros.isEmpty()){
                 json += ", ";
            }
            /*
             Para cada feedback error
             - tempo de resposta
             - modelo de ação
             - entidade_acao
             - componente_acao
             - parâmetros_acao
             - modelo de evento inicial, 
             - entidade_evento,
             - componente_evento,
             - parâmetros_evento,
             "feedback" : ["responseTime":560 , "actionType":0, "actionModel":1 , "actionEntity":1 , "actionComponent": 1, 
                "actionParameters": ["param1":"value1","param2":"value2"], "eventType":0, "eventModel": 1, "eventEntity":1, 
                "eventComponent":1, "eventTime":1439426402937 , "eventParameters" : ["param1":"value1","param2":"value2"] ] ,
             "feedback" : ["responseTime":931 , "actionType":1, "interactionModel":1 , "targetServiceId":1 , 
                "interactionParameters":["param1":"value1", "param2":"value2"], "eventType":0, "eventModel": 1, "eventEntity":1,
                "eventComponent":1, "eventParameters" : ["param1":"value1", "param2":"value2"] , "eventTime": 1439426402937   ] 
             */
            for (Action action : feedbackErros) {
                Event event = this.dataManager.getEventModelDAO().getEvent(action,(BaseComponentManager)this.dataManager.getDeviceManager());
                json += " {\"feedback\" : [ ";
                // se a ação é interação
                if(event.getEventType()==Event.INTERATION_EVENT){
                    parameters = this.dataManager.getAgentTypeDAO().getInteractionParameterContents(action);
                    json += "\"{ responseTime\":"+(action.getFeedbackAnswer().getTime().getTime()-event.getTime().getTime())+", ";
                    for (Parameter parameter : parameters) {
                        json += " \"" + parameter.getLabel() + "\": \"" + parameter.getContent().getValue().toString() + "\"}"
                                + (parameters.get(parameters.size() - 1).getId() == parameter.getId() ? "" : " ,");
                    }
                } else {
                    json += "\"{ responseTime\":"+(action.getFeedbackAnswer().getTime().getTime()-event.getTime().getTime())+", ";
                    json += "\"actionType\":"+(action.getActionType())+", ";
                    json += "\"actionModel\":"+(action.getId())+", ";
                    json += "\"actionEntity\":"+(action.getTargetEntityId())+", ";
                    json += "\"actionComponent\":"+(action.getTargetComponentId())+", ";
                    json += "\"actionParameters\":[";
                    parameters = this.dataManager.getActionModelDAO().getActionParameterContents(action);
                    for (Parameter parameter : parameters) {
                        json += " {\"" + parameter.getLabel() + "\": \"" + parameter.getContent().getValue().toString() + "\"}"
                                + (parameters.get(parameters.size() - 1).getId() == parameter.getId() ? "" : " ,");

                    }
                    json += " ] ";
                    json += "\"eventTime\":"+(event.getTime().getTime())+", ";
                    json += "\"eventType\":"+(event.getEventType())+", ";
                    json += "\"eventModel\":"+(event.getId())+", ";
                    json += "\"eventEntity\":"+(event.getEntityId())+", ";
                    json += "\"eventComponent\":"+(event.getComponentManager().getComponentId())+", ";
                    json += "\"eventParameters\":[";
                    parameters = this.dataManager.getEventModelDAO().getEventParameterContents(action);
                    for (Parameter parameter : parameters) {
                        json += " {\"" + parameter.getLabel() + "\": \"" + parameter.getContent().getValue().toString() + "\"}"
                                + (parameters.get(parameters.size() - 1).getId() == parameter.getId() ? "" : " ,");
                    }
                }
                json += " ] } " + ((actions.get(actions.size() - 1).getDataBaseId() == action.getDataBaseId() || feedbackErros.isEmpty()) ? "" : ", ");
            }
            json += " ] } ";
        }
        json += "}";
        return json;
    }

    /**
     * Remove todas os relatos de funcionamento armazenados até a data epecificada (Ainda falta terminar)
     * @param date 
     */
    public void removeSentReportedErrors(Date date) throws Exception {
        // fazer mais tarde.
        throw new Exception("Exceção para teste.");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
