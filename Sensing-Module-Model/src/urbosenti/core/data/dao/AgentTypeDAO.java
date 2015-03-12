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
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Conversation;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Direction;
import urbosenti.core.device.model.Interaction;
import urbosenti.core.device.model.InteractionType;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;
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

    public void insertInteraction(Interaction interaction) throws SQLException {
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
        String sql = "INSERT INTO possible_interaction_state_contents (possible_value, default_value, interaction_state_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (state.getPossibleContents() != null) {
            for (PossibleContent possibleContent : state.getPossibleContents()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, possibleContent.getValue());
                statement.setBoolean(2, possibleContent.isIsDefault());
                statement.setInt(3, state.getId());
                statement.execute();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        possibleContent.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                statement.close();
                System.out.println("INSERT INTO possible_interaction_state_contents (id,possible_value, default_value, interaction_state_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
            }
        }
    }

    public void insertState(State state) throws SQLException {
        String sql = "INSERT INTO interaction_states (id,description,agent_type_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (?,?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, state.getId());
        this.stmt.setString(2, state.getDescription());
        this.stmt.setInt(3, state.getAgentType().getId());
        this.stmt.setInt(4, state.getDataType().getId());
        this.stmt.setObject(5, state.getSuperiorLimit());
        this.stmt.setObject(6, state.getInferiorLimit());
        this.stmt.setObject(7, state.getInitialValue());
        this.stmt.execute();
        stmt.close();
        System.out.println("INSERT INTO interaction_states (id,description,agent_type_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (" + state.getId() + ",'" + state.getDescription() + "'," + state.getAgentType().getId() + "," + state.getDataType().getId()
                + ",'" + state.getSuperiorLimit() + "','" + state.getInferiorLimit() + "','" + state.getInitialValue() + "');");
    }

    public void insertParameters(Interaction interaction) throws SQLException {
        String sql = "INSERT INTO interaction_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,interaction_state_id,data_type_id,interaction_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement;
        if (interaction.getParameters() != null) {
            for (Parameter parameter : interaction.getParameters()) {
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
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        parameter.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                statement.close();
                System.out.println("INSERT INTO interaction_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,interaction_state_id,data_type_id,interaction_id) "
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
                statement.setObject(1, possibleContent.getValue());
                statement.setBoolean(2, possibleContent.isIsDefault());
                statement.setInt(3, parameter.getId());
                statement.execute();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        possibleContent.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                statement.close();
                System.out.println("INSERT INTO possible_interaction_contents (id,possible_value, default_value, interaction_parameter_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
            }
        }
    }

    public Content getCurrentContentValue(State state) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + "FROM interaction_state_contents\n"
                + "WHERE interaction_state_id = ? ORDER BY id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(rs.getObject("reading_time", Date.class));
            content.setValue(parseContent(state.getDataType(), rs.getObject("reading_value")));
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(State state) throws SQLException {
        String sql = "INSERT INTO interaction_state_contents (reading_value,reading_time,interaction_state_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, state.getContent().getValue());
        this.stmt.setObject(2, state.getContent().getTime());
        this.stmt.setInt(3, state.getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                state.getContent().setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO interaction_state_contents (id,reading_value,reading_time,interaction_state_id) "
                    + " VALUES (" + state.getContent().getId() + ",'" + state.getContent().getValue() + "',"
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
            content.setTime(rs.getObject("reading_time", Date.class));
            content.setValue(parseContent(parameter.getDataType(), rs.getObject("reading_value")));
            content.setScore(rs.getDouble("score"));
            content.setMessage(new AgentMessage());
            content.getMessage().setId(rs.getInt("message_id"));
        }
        rs.close();
        stmt.close();
        return content;
    }

    private Object parseContent(DataType dataType, Object value) {
        switch (dataType.getId()) {
            case 1://<dataType id="1" initialValue="0">byte</dataType>
                return Byte.parseByte(value.toString());
            case 2: // <dataType id="2" initialValue="0">short</dataType>
                return Short.parseShort(value.toString());
            case 3: // <dataType id="3" initialValue="0">int</dataType>
                return Integer.parseInt(value.toString());
            case 4: // <dataType id="4" initialValue="0">long</dataType>
                return Long.parseLong(value.toString());
            case 5: // <dataType id="5" initialValue="0.0">float</dataType>
                return Float.parseFloat(value.toString());
            case 6: // <dataType id="6" initialValue="0.0">double</dataType>
                return Double.parseDouble(value.toString());
            case 7: // <dataType id="7" initialValue="false">boolean</dataType>
                return Boolean.parseBoolean(value.toString());
            case 8: // <dataType id="8" initialValue="0">char</dataType>
                return value.toString();
            case 9: // <dataType id="9" initialValue="unknown">String</dataType>
                return value.toString();
            case 10: // <dataType id="10" initialValue="null">Object</dataType>
                return value;
        }
        return null;
    }

    public void insertContent(Parameter parameter) throws SQLException {
        String sql = "INSERT INTO interaction_contents (reading_value,reading_time,interaction_parameter_id,message_id) "
                + " VALUES (?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, parameter.getContent().getValue());
        this.stmt.setObject(2, parameter.getContent().getTime());
        this.stmt.setInt(3, parameter.getId());
        this.stmt.setInt(4, parameter.getContent().getMessage().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                parameter.getContent().setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO interaction_contents (id,reading_value,reading_time,interaction_parameter_id,message_id) "
                    + " VALUES (" + parameter.getContent().getId() + ",'" + parameter.getContent().getValue() + "',"
                    + ",'" + parameter.getContent().getTime().getTime() + "'," + "," + parameter.getId() + "," + parameter.getContent().getMessage().getId() + ");");
        }
    }

    List<Interaction> getAgentInteractions(AgentType agentType) throws SQLException {
        List<Interaction> interactions = new ArrayList();
        Interaction interaction = null;
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
            interaction = new Interaction();
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
        String sql = "SELECT interaction_states.id as state_id, interaction_states.description as state_desc, "
                + " superior_limit, inferior_limit, \n"
                + " interaction_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + " data_types.description as data_desc\n"
                + " FROM interaction_states, data_types\n" // trocar nome interaction_states para - agent_states
                + " WHERE agent_type_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, agentType.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setUserCanChange(false);
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentContentValue(state);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senão adiciona o conteúdo no estado
                state.setContent(c);
            }
            states.add(state);
        }
        rs.close();
        stmt.close();
        return states;
    }

    private Interaction getPrimaryInteraction(int id) throws SQLException {
        Interaction interaction = null;
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
            interaction = new Interaction();
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

    private List<Parameter> getInteractionParameters(Interaction interaction) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter = null;
        String sql = "SELECT interaction_parameters.id as interation_id, label, interaction_parameters.description as parameter_desc, \n"
                + "                optional, superior_limit, inferior_limit, interaction_state_id,\n"
                + "                interaction_parameters.initial_value, data_type_id, data_types.initial_value as data_initial_value,\n"
                + "                data_types.description as data_desc\n"
                + "                FROM interaction_parameters, data_types\n"
                + "                WHERE interation_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, interaction.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            parameter = new Parameter();
            parameter.setId(rs.getInt("interation_id"));
            parameter.setDescription(rs.getString("parameter_desc"));
            parameter.setLabel(rs.getString("label"));
            parameter.setInferiorLimit(rs.getObject("inferior_limit"));
            parameter.setSuperiorLimit(rs.getObject("superior_limit"));
            parameter.setInitialValue(rs.getObject("initial_value"));
            parameter.setOptional(rs.getBoolean("optional"));
            if (rs.getInt("interaction_state_id") > 0) {
                parameter.setRelatedState(this.getInteractionState(rs.getInt("interaction_state_id")));
            }
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            parameter.setDataType(type);
            // pegar o valor atual
            Content c = this.getCurrentContentValue(parameter);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senão adiciona o conteúdo no estado
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
        String sql = "SELECT interaction_states.id as state_id, interaction_states.description as state_desc, \n"
                + "                superior_limit, inferior_limit, \n"
                + "                interaction_states.initial_value, data_type_id, data_types.initial_value as data_initial_value,  \n"
                + "                data_types.description as data_desc \n"
                + "                FROM interaction_states, data_types \n"
                + "                WHERE interaction_states.id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setUserCanChange(false);
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentContentValue(state);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senão adiciona o conteúdo no estado
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
                + " FROM possible_interaction_state_contents\n"
                + " WHERE interaction_state_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            possibleContents.add(
                    new PossibleContent(
                            rs.getInt("id"),
                            rs.getString("possible_value"),
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
                conversation.setFinishedTime(rs.getDate("finished_time"));
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
            message.setTime(rs.getDate("message_time"));
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

    private Interaction getInteraction(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<Content> getInteractionParameterContents(AgentMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}