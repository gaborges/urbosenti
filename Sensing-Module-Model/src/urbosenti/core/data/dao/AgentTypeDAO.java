/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import urbosenti.core.device.model.Agent;
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.core.device.model.AgentMessage;
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.CommunicativeAct;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Conversation;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Direction;
import urbosenti.core.device.model.InteractionModel;
import urbosenti.core.device.model.InteractionType;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class AgentTypeDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public AgentTypeDAO(Object context) {
        this.connection = (Connection) context;
    }

    public AgentType get(int id) throws SQLException {
        AgentType agentType = null;
        String sql = "SELECT id, description "
                + "FROM agent_types\n"
                + "WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            agentType = new AgentType();
            agentType.setId(rs.getInt("id"));
            agentType.setDescription(rs.getString("description"));
        }
        rs.close();
        stmt.close();
        return agentType;
    }

    public void insert(AgentType type) throws SQLException {
        String sql = "INSERT INTO agent_types (id, description) "
                + "VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        System.out.println("INSERT INTO agent_types (id, description) "
                + " VALUES (" + type.getId() + ",'" + type.getDescription() + "');");
    }

    public void insertInteraction(InteractionModel interaction) throws SQLException {
        String sql = "INSERT INTO interactions (id,description,agent_type_id, communicative_act_id, interaction_type_id, direction_id, interaction_id) "
                + " VALUES (?,?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, interaction.getId());
        this.stmt.setString(2, interaction.getDescription());
        this.stmt.setInt(3, interaction.getAgentType().getId());
        this.stmt.setInt(4, interaction.getCommunicativeAct().getId());
        this.stmt.setInt(5, interaction.getInteractionType().getId());
        this.stmt.setInt(6, interaction.getDirection().getId());
        this.stmt.setInt(7, (interaction.getPrimaryInteraction() == null) ? -1 : interaction.getPrimaryInteraction().getId());
        this.stmt.execute();
        stmt.close();
        System.out.println("INSERT INTO interactions (id,description,agent_type_id,communicative_act_id, interaction_type_id, direction_id, interaction_id) "
                + " VALUES (" + interaction.getId() + ",'" + interaction.getDescription() + "'," + interaction.getAgentType().getId() + ","
                + interaction.getCommunicativeAct().getId() + "," + interaction.getInteractionType().getId() + "," + interaction.getDirection().getId() + ","
                + ((interaction.getPrimaryInteraction() == null) ? -1 : interaction.getPrimaryInteraction().getId()) + ");");
    }

    public void insertPossibleStateContents(State state) throws SQLException {
        String sql = "INSERT INTO possible_agent_state_contents (possible_value, default_value, agent_state_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (state.getPossibleContents() != null) {
            for (PossibleContent possibleContent : state.getPossibleContents()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, Content.parseContent(state.getDataType(),possibleContent.getValue()));
                statement.setBoolean(2, possibleContent.isIsDefault());
                statement.setInt(3, state.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    possibleContent.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                statement.close();
                System.out.println("INSERT INTO possible_agent_state_contents (id,possible_value, default_value, agent_state_id) "
                        + " VALUES (" + possibleContent.getId() + "," + Content.parseContent(state.getDataType(),possibleContent.getValue()) + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
            }
        }
    }

    public void insertState(State state) throws SQLException {
        String sql = "INSERT INTO agent_states (id,description,agent_type_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (?,?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        // trata o tipo de dado do estado
        state.setSuperiorLimit(Content.parseContent(state.getDataType(), state.getSuperiorLimit()));
        state.setInferiorLimit(Content.parseContent(state.getDataType(), state.getInferiorLimit()));
        state.setInitialValue(Content.parseContent(state.getDataType(), state.getInitialValue()));
        this.stmt.setInt(1, state.getId());
        this.stmt.setString(2, state.getDescription());
        this.stmt.setInt(3, state.getAgentType().getId());
        this.stmt.setInt(4, state.getDataType().getId());
        this.stmt.setObject(5, state.getSuperiorLimit());
        this.stmt.setObject(6, state.getInferiorLimit());
        this.stmt.setObject(7, state.getInitialValue());
        this.stmt.execute();
        stmt.close();
        System.out.println("INSERT INTO agent_states (id,description,agent_type_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (" + state.getId() + ",'" + state.getDescription() + "'," + state.getAgentType().getId() + "," + state.getDataType().getId()
                + ",'" + state.getSuperiorLimit() + "','" + state.getInferiorLimit() + "','" + state.getInitialValue() + "');");
    }

    public void insertParameters(InteractionModel interaction) throws SQLException {
        String sql = "INSERT INTO interaction_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,agent_state_id,data_type_id,interaction_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement;
        if (interaction.getParameters() != null) {
            for (Parameter parameter : interaction.getParameters()) {
                // trata o tipo de dado do estado
                parameter.setSuperiorLimit(Content.parseContent(parameter.getDataType(), parameter.getSuperiorLimit()));
                parameter.setInferiorLimit(Content.parseContent(parameter.getDataType(), parameter.getInferiorLimit()));
                parameter.setInitialValue(Content.parseContent(parameter.getDataType(), parameter.getInitialValue()));
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, parameter.getDescription());
                statement.setBoolean(2, parameter.isOptional());
                statement.setString(3, parameter.getLabel());
                statement.setObject(4, parameter.getSuperiorLimit());
                statement.setObject(5, parameter.getInferiorLimit());
                statement.setObject(6, parameter.getInitialValue());
                statement.setInt(7, (parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId());
                statement.setInt(8, parameter.getDataType().getId());
                statement.setInt(9, interaction.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    parameter.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                statement.close();
                System.out.println("INSERT INTO interaction_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,agent_state_id,data_type_id,interaction_id) "
                        + " VALUES (" + parameter.getId() + ",'" + parameter.getDescription() + "','" + parameter.getLabel() + "','" + parameter.getSuperiorLimit()
                        + "','" + parameter.getInferiorLimit() + "','" + parameter.getInitialValue() + "'," + ((parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId())
                        + "," + parameter.getDataType().getId() + "," + interaction.getId() + ");");
            }
        }

    }

    public void insertPossibleParameterContents(Parameter parameter) throws SQLException {
        String sql = "INSERT INTO possible_interaction_contents (possible_value, default_value, interaction_parameter_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (parameter.getPossibleContents() != null) {
            for (PossibleContent possibleContent : parameter.getPossibleContents()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1,Content.parseContent(parameter.getDataType(),possibleContent.getValue()));
                statement.setBoolean(2, possibleContent.isIsDefault());
                statement.setInt(3, parameter.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    possibleContent.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                statement.close();
                System.out.println("INSERT INTO possible_interaction_contents (id,possible_value, default_value, interaction_parameter_id) "
                        + " VALUES (" + possibleContent.getId() + "," + Content.parseContent(parameter.getDataType(),possibleContent.getValue()) + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
            }
        }
    }

    public Content getCurrentContentValue(State state) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + "FROM agent_state_contents\n"
                + "WHERE agent_state_id = ? ORDER BY id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(state.getDataType(), rs.getObject("reading_value")));
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(State state) throws SQLException {
        String sql = "INSERT INTO agent_state_contents (reading_value,reading_time,agent_state_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, Content.parseContent(state.getDataType(), state.getContent().getValue()));
        this.stmt.setObject(2, state.getContent().getTime().getTime());
        this.stmt.setInt(3, state.getId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            state.getContent().setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO agent_state_contents (id,reading_value,reading_time,agent_state_id) "
                    + " VALUES (" + state.getContent().getId() + ",'" + Content.parseContent(state.getDataType(), state.getContent().getValue()) + "',"
                    + ",'" + state.getContent().getTime().getTime() + "'," + "," + state.getId() + ");");
        }
    }

    public Content getCurrentContentValue(Parameter parameter) throws SQLException {
        Content content = null;
        String sql = "SELECT messages.id as content_id, reading_value, reading_time, message_id, message_time, interaction_id, conversation_id, "
                + "interaction_id, created_time,  finished_time, agent_id\n"
                + " FROM interaction_contents, messages, conversations\n"
                + " WHERE interaction_parameter_id = ? AND messages.id = message_id AND conversation_id = conversations.id \n"
                + " ORDER BY messages.id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, parameter.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("content_id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(parameter.getDataType(), rs.getObject("reading_value")));
            content.setScore(rs.getDouble("score"));
            content.setMessage(new AgentMessage());
            content.getMessage().setId(rs.getInt("message_id"));
            content.getMessage().setTime(new Date(Long.parseLong(rs.getString("message_time"))));
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(Parameter parameter) throws SQLException {
        String sql = "INSERT INTO interaction_contents (reading_value,reading_time,interaction_parameter_id,message_id) "
                + " VALUES (?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, Content.parseContent(parameter.getDataType(), parameter.getContent().getValue()));
        this.stmt.setObject(2, parameter.getContent().getTime().getTime());
        this.stmt.setInt(3, parameter.getId());
        this.stmt.setInt(4, parameter.getContent().getMessage().getId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                parameter.getContent().setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO interaction_contents (id,reading_value,reading_time,interaction_parameter_id,message_id) "
                    + " VALUES (" + parameter.getContent().getId() + ",'" + Content.parseContent(parameter.getDataType(), parameter.getContent().getValue()) + "',"
                    + ",'" + parameter.getContent().getTime().getTime() + "'," + "," + parameter.getId() + "," + parameter.getContent().getMessage().getId() + ");");
        }
    }

    List<InteractionModel> getAgentInteractions(AgentType agentType) throws SQLException {
        List<InteractionModel> interactions = new ArrayList();
        InteractionModel interaction = null;
        String sql = "SELECT interactions.id as id, interactions.description as interaction_desc, communicative_act_id, communicative_acts.description as act_desc,  \n"
                + "              interaction_type_id, interaction_types.description as type_desc, direction_id, interaction_directions.description as direction_desc, "
                + "              interaction_id, agent_communication_language_id, agent_communication_languages.description as language_description\n"
                + " FROM interactions, communicative_acts, interaction_types, interaction_directions, agent_communication_languages\n"
                + " WHERE agent_type_id = ? AND communicative_act_id = communicative_acts.id AND interaction_types.id = interaction_type_id "
                + " AND direction_id = interaction_directions.id and agent_communication_languages.id = agent_communication_language_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, agentType.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            interaction = new InteractionModel();
            interaction.setId(rs.getInt("id"));
            interaction.setDescription(rs.getString("interaction_desc"));
            interaction.setCommunicativeAct(
                    new CommunicativeAct(
                            rs.getInt("communicative_act_id"),
                            rs.getString("act_desc"),
                            new AgentCommunicationLanguage(rs.getInt("agent_communication_language_id"), rs.getString("language_description"))));
            interaction.setDirection(new Direction(rs.getInt("direction_id"), rs.getString("direction_desc")));
            interaction.setAgentType(agentType);
            interaction.setInteractionType(new InteractionType(rs.getInt("interaction_type_id"), rs.getString("type_desc")));
            if (rs.getInt("interaction_id") > 0) {
                interaction.setPrimaryInteraction(this.getPrimaryInteraction(rs.getInt("interaction_id")));
            }
            interaction.setParameters(this.getInteractionParameters(interaction));
            interactions.add(interaction);
        }
        rs.close();
        stmt.close();
        return interactions;
    }

    List<State> getAgentStates(AgentType agentType) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT agent_states.id as state_id, agent_states.description as state_desc, "
                + " superior_limit, inferior_limit, \n"
                + " agent_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + " data_types.description as data_desc\n"
                + " FROM agent_states, data_types\n"
                + " WHERE agent_type_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, agentType.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setDescription(rs.getString("state_desc"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(Content.parseContent(state.getDataType(),rs.getObject("data_initial_value")));
            state.setDataType(type);
            state.setInferiorLimit(Content.parseContent(type,rs.getObject("inferior_limit")));
            state.setSuperiorLimit(Content.parseContent(type,rs.getObject("superior_limit")));
            state.setInitialValue(Content.parseContent(type,rs.getObject("initial_value")));
            state.setUserCanChange(false);
            
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentContentValue(state);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senÃ£o adiciona o conteÃºdo no estado
                state.setContent(c);
            }
            states.add(state);
        }
        rs.close();
        stmt.close();
        return states;
    }

    private InteractionModel getPrimaryInteraction(int id) throws SQLException {
        InteractionModel interaction = null;
        String sql = "SELECT agent_types.description as agent_type_description, interactions.description as interaction_desc, communicative_act_id, communicative_acts.description as act_desc,  \n"
                + "              interaction_type_id, interaction_types.description as type_desc, direction_id, interaction_directions.description as direction_desc, "
                + "              interaction_id, agent_communication_language_id, agent_communication_languages.description as language_description, agent_type_id\n"
                + " FROM interactions, communicative_acts, interaction_types, interaction_directions, agent_communication_languages, agent_types \n"
                + " WHERE interactions.id = ? AND communicative_act_id = communicative_acts.id AND interaction_types.id = interaction_type_id "
                + " AND direction_id = interaction_directions.id AND agent_communication_languages.id = agent_communication_language_id AND agent_types.id = agent_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            interaction = new InteractionModel();
            interaction.setId(id);
            interaction.setDescription(rs.getString("interaction_desc"));
            interaction.setCommunicativeAct(
                    new CommunicativeAct(
                            rs.getInt("communicative_act_id"),
                            rs.getString("act_desc"),
                            new AgentCommunicationLanguage(rs.getInt("agent_communication_language_id"), rs.getString("language_description"))));
            interaction.setDirection(new Direction(rs.getInt("direction_id"), rs.getString("direction_desc")));
            interaction.setAgentType(new AgentType(rs.getInt("agent_type_id"), rs.getString("agent_type_description")));
            interaction.setInteractionType(new InteractionType(rs.getInt("interaction_type_id"), rs.getString("type_desc")));
            interaction.setParameters(this.getInteractionParameters(interaction));
        }
        rs.close();
        stmt.close();
        return interaction;
    }

    private List<Parameter> getInteractionParameters(InteractionModel interaction) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter = null;
        String sql = "SELECT interaction_parameters.id as id, label, interaction_parameters.description as parameter_desc, \n"
                + "                optional, superior_limit, inferior_limit, agent_state_id,\n"
                + "                interaction_parameters.initial_value, data_type_id, data_types.initial_value as data_initial_value,\n"
                + "                data_types.description as data_desc\n"
                + "                FROM interaction_parameters, data_types\n"
                + "                WHERE interaction_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, interaction.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            parameter = new Parameter();
            parameter.setId(rs.getInt("id"));
            parameter.setDescription(rs.getString("parameter_desc"));
            parameter.setLabel(rs.getString("label"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            parameter.setDataType(type);
            parameter.setInferiorLimit(Content.parseContent(type, rs.getObject("inferior_limit")));
            parameter.setSuperiorLimit(Content.parseContent(type,rs.getObject("superior_limit")));
            parameter.setInitialValue(Content.parseContent(type,rs.getObject("initial_value")));
            parameter.setOptional(rs.getBoolean("optional"));
            if (rs.getInt("agent_state_id") > 0) {
                parameter.setRelatedState(this.getInteractionState(rs.getInt("agent_state_id")));
            }
            
            // pegar o valor atual
            Content c = this.getCurrentContentValue(parameter);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senÃ£o adiciona o conteÃºdo no estado
                parameter.setContent(c);
            }
            parameters.add(parameter);
        }
        rs.close();
        stmt.close();
        return parameters;
    }

    private State getInteractionState(int id) throws SQLException {
        State state = null;
        String sql = "SELECT agent_states.id as state_id, agent_states.description as state_desc, \n"
                + "                superior_limit, inferior_limit, \n"
                + "                agent_states.initial_value, data_type_id, data_types.initial_value as data_initial_value,  \n"
                + "                data_types.description as data_desc \n"
                + "                FROM agent_states, data_types \n"
                + "                WHERE agent_states.id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setDescription(rs.getString("state_desc"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
            state.setInferiorLimit(Content.parseContent(type,rs.getObject("inferior_limit")));
            state.setSuperiorLimit(Content.parseContent(type,rs.getObject("superior_limit")));
            state.setInitialValue(Content.parseContent(type,rs.getObject("initial_value")));
            state.setUserCanChange(false);
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentContentValue(state);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senÃ£o adiciona o conteÃºdo no estado
                state.setContent(c);
            }
        }
        rs.close();
        stmt.close();
        return state;
    }

    private List<PossibleContent> getPossibleStateContents(State state) throws SQLException {
        List<PossibleContent> possibleContents = new ArrayList();
        String sql = " SELECT id, possible_value, default_value "
                + " FROM possible_agent_state_contents\n"
                + " WHERE agent_state_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            possibleContents.add(
                    new PossibleContent(
                            rs.getInt("id"),
                            Content.parseContent(state.getDataType(), rs.getString("possible_value")),
                            rs.getBoolean("default_value")));
        }
        rs.close();
        stmt.close();
        return possibleContents;
    }

    List<Conversation> getAgentConversations(Agent agent) throws SQLException {
        List<Conversation> conversations = new ArrayList();
        Conversation conversation = null;
        String sql = " SELECT id, created_time, finished_time "
                + " FROM conversations\n"
                + " WHERE agent_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, agent.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            conversation = new Conversation();
            conversation.setId(rs.getInt("id"));
            if (rs.getDate("finished_time") != null) {
                conversation.setFinishedTime( new Date(Long.parseLong(rs.getString("finished_time"))));
            }
            conversation.setMessages(this.getAgentMessages(conversation));
            conversations.add(conversation);
        }
        rs.close();
        stmt.close();
        return conversations;
    }

    private List<AgentMessage> getAgentMessages(Conversation conversation) throws SQLException {
        List<AgentMessage> messages = new ArrayList();
        AgentMessage message = null;
        String sql = " SELECT id, message_time,interaction_id "
                + " FROM messages\n"
                + " WHERE conversation_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, conversation.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            message = new AgentMessage();
            message.setId(rs.getInt("id"));
            message.setTime(new Date(Long.parseLong(rs.getString("message_time"))));
            if (rs.getInt("interaction_id") > 0) {
                message.setPreviousInteraction(this.getInteraction(rs.getInt("interaction_id")));
            }
            message.setContents(this.getInteractionParameterContents(message));
            messages.add(message);
        }
        rs.close();
        stmt.close();
        return messages;
    }

    private InteractionModel getInteraction(int id) throws SQLException {
        InteractionModel interaction = null;
        String sql = "SELECT agent_types.description as agent_type_description, interactions.description as interaction_desc, communicative_act_id, communicative_acts.description as act_desc,  \n"
                + "              interaction_type_id, interaction_types.description as type_desc, direction_id, interaction_directions.description as direction_desc, "
                + "              interaction_id, agent_communication_language_id, agent_communication_languages.description as language_description, agent_type_id\n"
                + " FROM interactions, communicative_acts, interaction_types, interaction_directions, agent_communication_languages, agent_types \n"
                + " WHERE interactions.id = ? AND communicative_act_id = communicative_acts.id AND interaction_types.id = interaction_type_id "
                + " AND direction_id = interaction_directions.id AND agent_communication_languages.id = agent_communication_language_id AND agent_types.id = agent_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            interaction = new InteractionModel();
            interaction.setId(id);
            interaction.setDescription(rs.getString("interaction_desc"));
            interaction.setCommunicativeAct(
                    new CommunicativeAct(
                            rs.getInt("communicative_act_id"),
                            rs.getString("act_desc"),
                            new AgentCommunicationLanguage(rs.getInt("agent_communication_language_id"), rs.getString("language_description"))));
            interaction.setDirection(new Direction(rs.getInt("direction_id"), rs.getString("direction_desc")));
            interaction.setAgentType(new AgentType(rs.getInt("agent_type_id"), rs.getString("agent_type_description")));
            interaction.setInteractionType(new InteractionType(rs.getInt("interaction_type_id"), rs.getString("type_desc")));
            if (rs.getInt("interaction_id") > 0) {
                interaction.setPrimaryInteraction(this.getPrimaryInteraction(rs.getInt("interaction_id")));
            }
            interaction.setParameters(this.getInteractionParameters(interaction));
        }
        rs.close();
        stmt.close();
        return interaction;
    }

    private List<Content> getInteractionParameterContents(AgentMessage message) throws SQLException {
        List<Content> contents = new ArrayList();
        Content content = null;
        String sql = " SELECT id, reading_value, reading_time, interaction_parameter_id "
                + " FROM interaction_contents \n"
                + " WHERE message_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, message.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            content = new Content();
            content.setId(rs.getInt("id"));
            if (rs.getDate("reading_time") != null) {
                content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            }
            content.setParameter(this.getParameter(content));
            content.setValue(rs.getObject("reading_value"));
            content.setMessage(message);
            contents.add(content);
        }
        rs.close();
        stmt.close();
        return contents;
    }

    private Parameter getParameter(Content content) throws SQLException {
        Parameter parameter = null;
        String sql = "SELECT interaction_parameters.id as interation_id, label, interaction_parameters.description as parameter_desc, \n"
                + "                optional, superior_limit, inferior_limit, agent_state_id,\n"
                + "                interaction_parameters.initial_value, data_type_id, data_types.initial_value as data_initial_value,\n"
                + "                data_types.description as data_desc\n"
                + "                FROM interaction_parameters, data_types, interaction_contents \n"
                + "                WHERE interaction_contents.id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, content.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            parameter = new Parameter();
            parameter.setId(rs.getInt("interation_id"));
            parameter.setDescription(rs.getString("parameter_desc"));
            parameter.setLabel(rs.getString("label"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            parameter.setInferiorLimit(Content.parseContent(type, rs.getObject("inferior_limit")));
            parameter.setSuperiorLimit(Content.parseContent(type, rs.getObject("superior_limit")));
            parameter.setInitialValue(Content.parseContent(type, rs.getObject("initial_value")));
            parameter.setOptional(rs.getBoolean("optional"));
            if (rs.getInt("agent_state_id") > 0) {
                parameter.setRelatedState(this.getInteractionState(rs.getInt("agent_state_id")));
            }
            parameter.setContent(content);
            parameter.setDataType(type);
        }
        rs.close();
        stmt.close();
        return parameter;
    }

    public InteractionModel getInteractionModel(Event event) throws SQLException {
        return getInteraction(event.getId());
    }

    public InteractionModel getInteractionModel(int interactionId) throws SQLException {
        return getInteraction(interactionId);
    }
    
    public List<Parameter> getInteractionParameterContents(Action action) throws SQLException {
        return getInteractionParameterContents(action.getDataBaseId());
    }
    
    public List<Parameter> getInteractionParameterContents(int dataBaseEventId) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter;
        Content content;
        String sql = "interaction_contents.id as content_id, reading_value, reading_time, interaction_parameters.id as parameter_id, "
                + " label, interaction_parameters.description as parameter_desc, optional, superior_limit, "
                + " inferior_limit, agent_state_id, interaction_parameters.initial_value, data_type_id, "
                + " data_types.initial_value as data_initial_value, data_types.description as data_desc "
                + " FROM interaction_contents, interaction_parameters, data_types "
                + " WHERE generated_event_id = 1  AND interaction_parameter_id = interaction_parameters.id AND data_types.id = data_type_id "
                + " ORDER BY generated_event_id ; ";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, dataBaseEventId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            parameter = new Parameter();
            parameter.setId(rs.getInt("parameter_id"));
            parameter.setDescription(rs.getString("parameter_desc"));
            parameter.setLabel(rs.getString("label"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            parameter.setDataType(type);
            // trata o tipo de dado do estado
            parameter.setSuperiorLimit(Content.parseContent(
                    parameter.getDataType(), parameter.getSuperiorLimit()));
            parameter.setInferiorLimit(Content.parseContent(
                    parameter.getDataType(), parameter.getInferiorLimit()));
            parameter.setInitialValue(Content.parseContent(
                    parameter.getDataType(), parameter.getInitialValue()));
            parameter.setInferiorLimit(rs.getObject("inferior_limit"));
            parameter.setSuperiorLimit(rs.getObject("superior_limit"));
            parameter.setInitialValue(rs.getObject("initial_value"));
            parameter.setOptional(rs.getBoolean("optional"));
            if (rs.getInt("agent_state_id") > 0) {
                parameter.setRelatedState(getInteractionState(rs
                        .getInt("agent_state_id")));
            }
            // pega o valor utilizado na ação
            content = new Content();
            content.setId(rs.getInt("content_id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(parameter.getDataType(),
                    rs.getObject("reading_value")));
            parameter.setContent(content);

            parameter.setPossibleContents(this.getPossibleContents(parameter));
            parameters.add(parameter);
        }
        rs.close();
        stmt.close();
        return parameters;
    }
    
    private List<PossibleContent> getPossibleContents(Parameter parameter)
            throws SQLException {
        List<PossibleContent> possibleContents = new ArrayList();
        String sql = " SELECT id, possible_value, default_value "
                + " FROM possible_interaction_contents\n"
                + " WHERE interaction_parameter_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, parameter.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            possibleContents.add(new PossibleContent(rs.getInt("id"), Content
                    .parseContent(parameter.getDataType(),
                            rs.getString("possible_value")), rs
                    .getBoolean("default_value")));
        }
        rs.close();
        stmt.close();
        return possibleContents;
    }
}
