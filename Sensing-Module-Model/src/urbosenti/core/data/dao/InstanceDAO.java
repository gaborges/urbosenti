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
public class InstanceDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public InstanceDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Instance instance) throws SQLException {
        String sql = "INSERT INTO instances (description,representative_class,entity_id,model_id) "
                + " VALUES (?,?,?,?);";
        instance.setModelId(instance.getId());
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, instance.getDescription());
        this.stmt.setString(2, instance.getRepresentativeClass());
        this.stmt.setInt(3, instance.getEntity().getId());
        if (instance.getId() > 0) {
            this.stmt.setInt(4, instance.getId());
        }
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            instance.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO instances (id,description,representative_class, entity_id, model_id) "
                    + " VALUES (" + instance.getId() + ",'" + instance.getDescription() + "','" + instance.getRepresentativeClass() + "',"
                    + instance.getEntity().getId() + "," + ((instance.getModelId() > 0) ? instance.getModelId() : null) + ");");
        }
    }

    public void insertState(State state, Instance instance) throws SQLException {
        String sql = "INSERT INTO instance_states (description,user_can_change,instance_id,data_type_id,superior_limit,inferior_limit,initial_value,state_model_id) "
                + " VALUES (?,?,?,?,?,?,?,?);";
        if (state.getModelId() < 1) {
            state.setModelId(state.getId());
        }
        // trata o tipo de dado do estado
        state.setSuperiorLimit(Content.parseContent(state.getDataType(), state.getSuperiorLimit()));
        state.setInferiorLimit(Content.parseContent(state.getDataType(), state.getInferiorLimit()));
        state.setInitialValue(Content.parseContent(state.getDataType(), state.getInitialValue()));
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, state.getDescription());
        this.stmt.setBoolean(2, state.isUserCanChange());
        this.stmt.setInt(3, instance.getId());
        this.stmt.setInt(4, state.getDataType().getId());
        this.stmt.setObject(5, state.getSuperiorLimit());
        this.stmt.setObject(6, state.getInferiorLimit());
        this.stmt.setObject(7, state.getInitialValue());
        this.stmt.setInt(8, state.getModelId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            state.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO instance_states (id,description,user_can_change,instance_id,data_type_id,superior_limit,inferior_limit,initial_value,state_model_id) "
                    + " VALUES (" + state.getId() + ",'" + state.getDescription() + "'," + state.isUserCanChange() + "," + instance.getId()
                    + "," + state.getDataType().getId() + ",'" + state.getSuperiorLimit() + "','" + state.getInferiorLimit() + "','" + state.getInitialValue() + "'," + state.getModelId() + ");");
        }
    }

    public void insertPossibleStateContents(State state, Instance instance) throws SQLException {
        String sql = "INSERT INTO possible_instance_contents (possible_value, default_value, instance_state_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (state.getPossibleContents() != null) {
            for (PossibleContent possibleContent : state.getPossibleContents()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, Content.parseContent(state.getDataType(), possibleContent.getValue()));
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
                    System.out.println("INSERT INTO possible_instance_contents (id,possible_value, default_value, instance_state_id) "
                            + " VALUES (" + possibleContent.getId() + "," + Content.parseContent(state.getDataType(), possibleContent.getValue()) + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
                }
            }
        }
    }

    public Content getCurrentContentValue(State state) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + "FROM instance_state_contents\n"
                + "WHERE instance_state_id = ? ORDER BY id DESC LIMIT 1;";
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
        String sql = "INSERT INTO instance_state_contents (reading_value,reading_time,instance_state_id) "
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
            System.out.println("INSERT INTO instance_state_contents (id,reading_value,reading_time,instance_state_id) "
                    + " VALUES (" + state.getContent().getId() + ",'" + Content.parseContent(state.getDataType(), state.getContent().getValue()) + "',"
                    + "'" + state.getContent().getTime().getTime() + "'," + state.getId() + ");");
        }
    }

    public List<Instance> getEntityInstances(Entity entity) throws SQLException {
        List<Instance> instances = new ArrayList();
        Instance instance = null;
        String sql = "SELECT id,  description, model_id, representative_class "
                + " FROM instances\n"
                + " WHERE entity_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            instance = new Instance();
            instance.setId(rs.getInt("id"));
            instance.setDescription(rs.getString("description"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setModelId(rs.getInt("model_id"));
            instance.setEntity(entity);
            instance.setStates(this.getInstanceStates(instance));
            instances.add(instance);
        }
        rs.close();
        stmt.close();
        return instances;
    }

    public List<State> getInstanceStates(Instance instance) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT instance_states.id as state_id, instance_states.description as state_desc, state_model_id, "
                + "user_can_change, superior_limit, inferior_limit, \n"
                + "instance_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "data_types.description as data_desc\n"
                + "FROM instance_states, data_types\n "
                + "WHERE instance_id = ? and data_types.id = data_type_id ORDER BY state_model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, instance.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setDescription(rs.getString("state_desc"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(Content.parseContent(type, rs.getObject("data_initial_value")));
            state.setDataType(type);
            state.setInferiorLimit(Content.parseContent(type, rs.getObject("inferior_limit")));
            state.setSuperiorLimit(Content.parseContent(type, rs.getObject("superior_limit")));
            state.setInitialValue(Content.parseContent(type, rs.getObject("initial_value")));
            state.setStateInstance(true);
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            state.setModelId(rs.getInt("state_model_id"));

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
                + " FROM possible_instance_contents\n"
                + " WHERE instance_state_id = ?;";
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

    public Instance getInstance(int id) throws SQLException {
        Instance instance = null;
        String sql = "SELECT instances.description as instance_desc, representative_class, entity_id, entities.description, model_id as entity_model_id, \n"
                + " instances.model_id as model_id, entity_type_id, entity_types.description as type_desc, component_id, components.description as comp_desc, code_class\n"
                + " FROM instances, entities,entity_types, components \n"
                + " WHERE  instances.id = ? AND entities.id = entity_id AND entity_types.id = entity_type_id AND components.id = component_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            instance = new Instance();
            instance.setId(id);
            instance.setDescription(rs.getString("description"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setEntity(new Entity());
            instance.setModelId(rs.getInt("model_id"));
            instance.getEntity().setId(rs.getInt("id"));
            instance.getEntity().setDescription(rs.getString("instance_desc"));
            instance.getEntity().setModelId(rs.getInt("entity_model_id"));
            instance.getEntity().setEntityType(
                    new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc")));
            instance.getEntity().setComponent(
                    new Component(rs.getInt("component_id"), rs.getString("comp_desc"), rs.getString("code_class'")));
            instance.setStates(this.getInstanceStates(instance));
        }
        rs.close();
        stmt.close();
        return instance;
    }

    public Instance getInstance(int modelId, int entityModelId, int componentId) throws SQLException {
        Instance instance = null;
        String sql = "SELECT instances.description as instance_desc, representative_class, entity_id, entities.description as entity_desc, instances.model_id,\n"
                + " entity_type_id, entity_types.description as type_desc, component_id, components.description as comp_desc, code_class, instances.id as instance_id\n"
                + " FROM instances, entities,entity_types, components \n"
                + " WHERE instances.model_id = ? AND entities.model_id = ? AND entities.id = entity_id AND entity_types.id = entity_type_id AND components.id = component_id AND component_id = ?"
                + " ORDER BY instances.model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, modelId);
        stmt.setInt(2, entityModelId);
        stmt.setInt(3, componentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            instance = new Instance();
            instance.setId(rs.getInt("instance_id"));
            instance.setDescription(rs.getString("instance_desc"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setEntity(new Entity());
            instance.setModelId(rs.getInt("model_id"));
            instance.getEntity().setId(rs.getInt("entity_id"));
            instance.getEntity().setDescription(rs.getString("entity_desc"));
            instance.getEntity().setModelId(entityModelId);
            instance.getEntity().setEntityType(
                    new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc")));
            instance.getEntity().setComponent(
                    new Component(rs.getInt("component_id"), rs.getString("comp_desc"), rs.getString("code_class")));
            instance.setStates(this.getInstanceStates(instance));
        }
        rs.close();
        stmt.close();
        return instance;
    }

    int getEntityInstanceCount(Entity entity) throws SQLException {
        int count = 0;
        String sql = "SELECT count(*) FROM instances WHERE entity_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.execute();
        ResultSet res = stmt.getResultSet();
        if (res.next()) {
            count = res.getInt(1);
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        return count;
    }

    Instance getInstanceByStateContent(Object contentValue, int stateModelId, int entityModelId, int componentId) throws SQLException {
        Instance instance = null;
        String sql = "SELECT instances.description as instance_desc, representative_class, instances.entity_id, entities.description as entity_desc, code_class, "
                + " instances.model_id, instance_id, entity_type_id, entity_types.description as type_desc, component_id, components.description as comp_desc "
                + "                 FROM instances, entities,entity_types, components , instance_states, instance_state_contents "
                + "                 WHERE state_model_id = ? AND entities.model_id = ? AND component_id = ? AND reading_value = ? "
                + "                  AND entities.id = instances.entity_id AND entity_types.id = entity_type_id "
                + "                  AND components.id = component_id AND instance_states.id = instance_state_id "
                + "                  AND instance_id = instances.id "
                + "                 ORDER BY instance_state_contents.id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, stateModelId);
        stmt.setInt(2, entityModelId);
        stmt.setInt(3, componentId);
        stmt.setObject(4, contentValue);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            instance = new Instance();
            instance.setId(rs.getInt("instance_id"));
            instance.setDescription(rs.getString("instance_desc"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setEntity(new Entity());
            instance.setModelId(rs.getInt("model_id"));
            instance.getEntity().setId(rs.getInt("entity_id"));
            instance.getEntity().setDescription(rs.getString("entity_desc"));
            instance.getEntity().setModelId(entityModelId);
            instance.getEntity().setEntityType(
                    new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc")));
            instance.getEntity().setComponent(
                    new Component(rs.getInt("component_id"), rs.getString("comp_desc"), rs.getString("code_class")));
            instance.setStates(this.getInstanceStates(instance));
        }
        rs.close();
        stmt.close();
        return instance;
    }

    /**
     * Recebe por parÃ¢metro uma instÃ¢ncia de usuÃ¡rio e retorna todos os estados
     * que podem ser alterados pelo usuÃ¡rio em cada interface. Esses estados
     * retornam com o valor atual da Ãºltima vez que o usuÃ¡rio alterou o
     * conteÃºdo. Caso este nÃ£o exista retorna o valor inicial. OBS.: Retorna
     * todos os dados atÃ© os componentes a partir da visÃ£o do estado.
     *
     * @param instance
     * @return
     */
    public List<State> getUserInstanceStates(Instance instance, Instance userInstance) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT instance_states.id as state_id, instance_states.description as state_desc, user_can_change, superior_limit, "
                + " state_model_id, inferior_limit, instance_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + " data_types.description as data_desc "
                + " FROM instance_states, data_types \n"
                + " WHERE user_can_change = 1 AND data_types.id = data_type_id; ";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            state = new State();
            state.setId(rs.getInt("state_id"));
            state.setModelId(rs.getInt("state_model_id"));
            state.setDescription(rs.getString("state_desc"));
            state.setInferiorLimit(rs.getObject("inferior_limit"));
            state.setSuperiorLimit(rs.getObject("superior_limit"));
            state.setInitialValue(rs.getObject("initial_value"));
            state.setStateInstance(true);
            state.setUserCanChange(rs.getBoolean("user_can_change"));
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            state.setDataType(type);
            state.setPossibleContent(this.getPossibleStateContents(state));
            // pegar o valor atual
            Content c = this.getCurrentUserInstanceContentValue(state, userInstance);
            if (c != null) { // se c for nulo deve usar os valores iniciais, senÃ£o adiciona o conteÃºdo no estado
                state.setContent(c);
            }
            states.add(state);
        }
        rs.close();
        stmt.close();
        return states;
    }

    /**
     * Recebe por parÃ¢metro uma instÃ¢ncia de usuÃ¡rio e retorna todas as
     * instÃ¢ncias que possuem estados que podem ser alterados pelo usuÃ¡rio,
     * juntamente com esses estados e seu valor atual em relaÃ§Ã£o ao usuÃ¡rio.
     * Esses estados retornam com o valor atual da Ãºltima vez que o usuÃ¡rio
     * alterou o conteÃºdo. Caso este nÃ£o exista retorna o valor inicial. OBS.:
     * Retorna todos os dados atÃ© os componentes a partir da visÃ£o do estado.
     *
     * @param userInstance
     * @return
     */
    public List<Instance> getUserInstances(Instance userInstance) throws SQLException {
        List<Instance> instances = new ArrayList();
        Instance instance = null;
        // Buscar todas as instÃ¢ncias que possuam algum estado que pode ser alterado pelo usuÃ¡rio
        String sql = "SELECT instances.id as instance_id,  instances.description as instance_desc, instances.model_id as instance_model_id, "
                + " representative_class, (SELECT count(*) FROM instance_states WHERE user_can_change == 1  AND instance_id = instances.id) as count, "
                + " entity_id, entities.description as entity_desc, component_id, components.description as component_desc, code_class, "
                + " entities.model_id as entity_model_id, entity_type_id, entity_types.description as entity_type_desc\n"
                + "    FROM instances, entities, components, entity_types \n"
                + "    WHERE (SELECT count(*) FROM instance_states WHERE user_can_change == 1  AND instance_id = instances.id) > 0 "
                + " AND entity_id = entities.id AND component_id = components.id AND entity_type_id = entity_types.id AND instance_id = instances.id;";
        stmt = this.connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            instance = new Instance();
            instance.setId(rs.getInt("instance_id"));
            instance.setDescription(rs.getString("instance_desc"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setModelId(rs.getInt("instance_model_id"));
            instance.setEntity(new Entity());
            instance.getEntity().setId(rs.getInt("entity_id"));
            instance.getEntity().setDescription(rs.getString("entity_desc"));
            instance.getEntity().setModelId(rs.getInt("entity_model_id"));
            instance.getEntity().setEntityType(
                    new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc")));
            instance.getEntity().setComponent(
                    new Component(rs.getInt("component_id"), rs.getString("comp_desc"), rs.getString("code_class")));
            instance.setStates(this.getUserInstanceStates(instance, userInstance));
            instances.add(instance);
        }
        rs.close();
        stmt.close();
        return instances;
    }

    private Content getCurrentUserInstanceContentValue(State state, Instance userInstance) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + " FROM instance_state_contents "
                + " WHERE instance_state_id = ? AND monitored_user_instance_id = ? "
                + " ORDER BY id DESC "
                + " LIMIT 1; ";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, state.getId());
        stmt.setInt(2, userInstance.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(state.getDataType(), (rs.getObject("reading_value"))));
            content.setMonitoredInstance(userInstance);
        }
        rs.close();
        stmt.close();
        return content;
    }

    void deleteInstance(Instance instance) throws SQLException {
        String sql = "DELETE * FROM entity_state_contents WHERE monitored_user_instance_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, instance.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    void deleteInstanceStates(Instance instance) throws SQLException {
        String sql = "DELETE * FROM entity_states WHERE instance_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, instance.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    void deleteInstanceStateContents(Instance instance) throws SQLException {
        for (State state : instance.getStates()) {
            String sql = "DELETE * FROM instance_state_contents WHERE instance_state_id = ? ;";
            stmt = this.connection.prepareStatement(sql);
            stmt.setInt(1, state.getId());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    void deleteUserInstanceContents(Instance instance) throws SQLException {
        String sql = "DELETE * FROM instance_state_contents WHERE monitored_user_instance_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, instance.getId());
        stmt.executeUpdate();
        stmt.close();
    }
    
    public List<Instance> getEntityInstances(int entityModelId, int componentId) throws SQLException {
        Instance instance = null;
        List<Instance> instances = new ArrayList();
        String sql = "SELECT instances.description as instance_desc, representative_class, entity_id, entities.description as entity_desc, instances.model_id,\n"
                + " entity_type_id, entity_types.description as type_desc, component_id, components.description as comp_desc, code_class, instances.id as instance_id\n"
                + " FROM instances, entities,entity_types, components \n"
                + " WHERE entities.model_id = ? AND entities.id = entity_id AND entity_types.id = entity_type_id AND components.id = component_id AND component_id = ?"
                + " ORDER BY instances.model_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entityModelId);
        stmt.setInt(2, componentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            instance = new Instance();
            instance.setId(rs.getInt("instance_id"));
            instance.setDescription(rs.getString("instance_desc"));
            instance.setRepresentativeClass(rs.getString("representative_class"));
            instance.setEntity(new Entity());
            instance.setModelId(rs.getInt("model_id"));
            instance.getEntity().setId(rs.getInt("entity_id"));
            instance.getEntity().setDescription(rs.getString("entity_desc"));
            instance.getEntity().setModelId(entityModelId);
            instance.getEntity().setEntityType(
                    new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc")));
            instance.getEntity().setComponent(
                    new Component(rs.getInt("component_id"), rs.getString("comp_desc"), rs.getString("code_class")));
            instance.setStates(this.getInstanceStates(instance));
            instances.add(instance);
        }
        rs.close();
        stmt.close();
        return instances;
    }
    
    public Content getBeforeCurrentContentValue(int stateModelId, int instanceModelId, int entityModelId, int componentModelId) throws SQLException {
        Content content = null;
        String sql = "SELECT instance_state_contents.id as id, reading_value, reading_time, data_type_id " +
"                FROM instance_state_contents, instance_states, instances, entities\n" +
"                WHERE state_model_id = ? AND instances.model_id = ? AND entities.model_id = ? AND component_id = ?" +
"                AND entity_id = entities.id AND instance_state_id == instance_states.id AND instance_id = instances.id " +
"                AND data_types.id = data_type_id ORDER BY id DESC LIMIT 2;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, stateModelId);
        this.stmt.setInt(2, instanceModelId);
        this.stmt.setInt(3, entityModelId);
        this.stmt.setInt(4, componentModelId);
        ResultSet rs = stmt.executeQuery();
        // somente o último é que interessa
        if (rs.next()) {
            rs.next();
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(new DataType(rs.getInt("data_type_id"), sql), rs.getObject("reading_value")));
        }
        rs.close();
        stmt.close();
        return content;
    }

}
