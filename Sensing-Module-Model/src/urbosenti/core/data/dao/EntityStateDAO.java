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
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class EntityStateDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public EntityStateDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(State state) throws SQLException {
        String sql = "INSERT INTO entity_states (description,user_can_change,instance_state,entity_id,data_type_id,superior_limit,inferior_limit,initial_value,model_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        state.setModelId(state.getId());
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, state.getDescription());
        this.stmt.setBoolean(2, state.isUserCanChange());
        this.stmt.setBoolean(3, state.isStateInstance());
        this.stmt.setInt(4, state.getEntity().getId());
        this.stmt.setInt(5, state.getDataType().getId());
        this.stmt.setObject(6, state.getSuperiorLimit());
        this.stmt.setObject(7, state.getInferiorLimit());
        this.stmt.setObject(8, state.getInitialValue());
        this.stmt.setInt(9, state.getId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            state.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO entity_states (id,description,user_can_change,instance_state,entity_id,data_type_id,superior_limit,inferior_limit,initial_value,model_id)  "
                    + " VALUES (" + state.getId() + ",'" + state.getDescription() + "'," + state.isUserCanChange() + "," + state.isStateInstance() + "," + state.getEntity().getId()
                    + "," + state.getDataType().getId() + ",'" + state.getSuperiorLimit() + "','" + state.getInferiorLimit() + "','" + state.getInitialValue() + "'," + state.getModelId() + ");");
        }
    }

    public void insertPossibleContents(State state) throws SQLException {
        String sql = "INSERT INTO possible_entity_contents (possible_value, default_value, entity_state_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        for (PossibleContent possibleContent : state.getPossibleContents()) {
            statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setObject(1, possibleContent.getValue());
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
            if (DeveloperSettings.SHOW_DAO_SQL) {
                System.out.println("INSERT INTO possible_entity_contents (id,possible_value, default_value, entity_state_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
            }
        }
    }

    public List<State> getEntityStateModels(Entity entity) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT entity_states.id as state_id, model_id, entity_states.description as state_desc, "
                + "user_can_change, instance_state, superior_limit, inferior_limit, \n"
                + "entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "data_types.description as data_desc\n"
                + "FROM entity_states, data_types\n"
                + "WHERE entity_id = ? AND data_types.id = data_type_id AND instance_state = 0 ORDER BY model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
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

    public List<State> getInitialModelInstanceStates(Entity entity) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT entity_states.id as state_id, model_id, entity_states.description as state_desc, "
                + "user_can_change, instance_state, superior_limit, inferior_limit, \n"
                + "entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "data_types.description as data_desc\n"
                + "FROM entity_states, data_types\n"
                + "WHERE entity_id = ? AND data_types.id = data_type_id AND instance_state = 1 ORDER BY model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
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

    public List<State> getAllEntityAndInitialModelInstanceStates(Entity entity) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT entity_states.id as state_id, model_id, entity_states.description as state_desc, "
                + "user_can_change, instance_state, superior_limit, inferior_limit, \n"
                + "entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "data_types.description as data_desc\n"
                + "FROM entity_states, data_types\n"
                + "WHERE entity_id = ? AND data_types.id = data_type_id ORDER BY model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
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

    public List<PossibleContent> getPossibleStateContents(State state) throws SQLException {
        List<PossibleContent> possibleContents = new ArrayList();
        String sql = " SELECT id, possible_value, default_value "
                + " FROM possible_entity_contents\n"
                + " WHERE entity_state_id = ?;";
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

    public Content getCurrentContentValue(State state) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time, monitored_user_instance_id "
                + "FROM entity_state_contents\n"
                + "WHERE entity_state_id = ? ORDER BY id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(state.getDataType(), (rs.getObject("reading_value"))));
            if (rs.getInt("monitored_user_instance_id") > 0) {
                Instance i = new Instance();
                i.setId(rs.getInt("monitored_user_instance_id"));
                content.setMonitoredInstance(i);
            }
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(State state) throws SQLException {
        String sql = "INSERT INTO entity_state_contents (reading_value,reading_time,monitored_user_instance_id,entity_state_id) "
                + " VALUES (?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, state.getContent().getValue());
        this.stmt.setObject(2, (state.getContent().getTime() == null) ? System.currentTimeMillis() : state.getContent().getTime().getTime());
        if (state.getContent().getMonitoredInstance() != null) {
            this.stmt.setInt(3, state.getContent().getMonitoredInstance().getId());
        }
        this.stmt.setInt(4, state.getId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            state.getContent().setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO entity_state_contents (id, reading_value,reading_time,monitored_user_instance_id,entity_state_id)   "
                    + " VALUES (" + state.getContent().getId() + ",'" + state.getContent().getValue() + "',"
                    + ((state.getContent().getMonitoredInstance() != null) ? state.getContent().getMonitoredInstance().getId() : "null")
                    + ",'" + state.getContent().getTime().getTime() + "'," + "," + state.getId() + ");");
        }
    }

    State getState(int id) throws SQLException {
        State state = null;
        String sql = "SELECT entity_states.id as state_id, model_id, entity_states.description as state_desc, \n"
                + "                user_can_change, instance_state, superior_limit, inferior_limit, \n"
                + "                entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value,  \n"
                + "                data_types.description as data_desc \n"
                + "                FROM entity_states, data_types \n"
                + "                WHERE entity_states.id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
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

    public State getEntityState(int componentId, int entityModelId, int stateModelId) throws SQLException {
        State state = null;
        String sql = "SELECT entity_states.id as state_id, entity_states.description as state_desc, "
                + "               user_can_change, instance_state, superior_limit, inferior_limit,  "
                + "                entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "                data_types.description as data_desc "
                + "              FROM entities, entity_states, data_types "
                + "              WHERE entity_states.model_id = ?  AND entities.model_id = ? AND component_id = ? "
                + "               AND data_types.id = data_type_id AND entity_id = entities.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, stateModelId);
        stmt.setInt(2, entityModelId);
        stmt.setInt(3, componentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(stateModelId);
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
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

    /**
     * Recebe por parÃ¢metro uma instÃ¢ncia de usuÃ¡rio e retorna todos os estados
     * que podem ser alterados pelo usuÃ¡rio que nÃ£o sÃ£o de instÃ¢ncias. Esses
     * estados retornam com o valor atual da Ãºltima vez que o usuÃ¡rio alterou o
     * conteÃºdo. Caso este nÃ£o exista retorna o valor inicial. OBS.: Retorna
     * todos os dados atÃ© os componentes a partir da visÃ£o do estado.
     *
     * @param instance
     * @return
     * @throws java.sql.SQLException
     */
    public List<State> getUserEntityStates(Instance instance) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT entity_states.id as state_id, entity_states.description as state_desc, user_can_change, instance_state, superior_limit, "
                + " entity_states.model_id as state_model_id, inferior_limit, entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + " data_types.description as data_desc, entity_id, entities.description as entity_desc, component_id, components.description as component_desc, "
                + " code_class, entities.model_id as entity_model_id, entity_type_id, entity_types.description as entity_type_desc\n "
                + " FROM entities, entity_states, data_types, components, entity_types\n"
                + " WHERE user_can_change = 1 AND instance_state = 0 AND data_types.id = data_type_id AND entity_id = entities.id AND component_id = components.id "
                + " AND entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("state_model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(rs.getBoolean("instance_state"));
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
            Entity entity = new Entity();
            entity.setDescription(rs.getString("entity_desc"));
            entity.setId(rs.getInt("entity_id"));
            entity.setModelId(rs.getInt("entity_model_id"));
            entity.setEntityType(new EntityType(rs.getInt("entity_type_id"), rs.getString("entity_type_desc")));
            entity.setComponent(new Component(rs.getInt("component_id"), rs.getString("component_desc"), rs.getString("code_class")));
            state.setEntity(entity);
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentInstanceContentValue(state,instance);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senÃ£o adiciona o conteÃºdo no estado
                state.setContent(c);
            }
            states.add(state);
        }
        rs.close();
        stmt.close();
        return states;
    }
    
    private Content getCurrentInstanceContentValue(State state, Instance instance) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + " FROM entity_state_contents "
                + " WHERE entity_state_id = ? AND monitored_user_instance_id = ? "
                + " ORDER BY id DESC "
                + " LIMIT 1; ";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        stmt.setInt(2, instance.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(state.getDataType(), (rs.getObject("reading_value"))));
            content.setMonitoredInstance(instance);
        }
        rs.close();
        stmt.close();
        return content;
    }

    void deleteUserContents(Instance instance) throws SQLException {
        String sql = "DELETE FROM entity_state_contents WHERE monitored_user_instance_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, instance.getId());
        stmt.executeUpdate();
        stmt.close();
    }
}
