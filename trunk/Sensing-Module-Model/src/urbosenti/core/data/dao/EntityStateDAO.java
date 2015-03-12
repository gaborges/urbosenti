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
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Entity;
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
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                state.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
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
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    possibleContent.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            statement.close();
            if (DeveloperSettings.SHOW_DAO_SQL) {
                System.out.println("INSERT INTO possible_entity_contents (id,possible_value, default_value, entity_state_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
            }
        }
    }

    public List<State> getEntityStates(Entity entity) throws SQLException {
        List<State> states = new ArrayList();
        State state = null;
        String sql = "SELECT entity_states.id as state_id, model_id, entity_states.description as state_desc, "
                + "user_can_change, instance_state, superior_limit, inferior_limit, \n"
                + "entity_states.initial_value, data_type_id, data_types.initial_value as data_initial_value, "
                + "data_types.description as data_desc\n"
                + "FROM entity_states, data_types\n"
                + "WHERE entity_id = ? and data_types.id = data_type_id;";
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
            if (c != null) { // se c for nulo deve usar os valores iniciais, senão adiciona o conteúdo no estado
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
            content.setTime(rs.getObject("reading_time", Date.class));
            content.setValue(parseContent(state.getDataType(), (rs.getObject("reading_value"))));
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

    public void insertContent(State state) throws SQLException {
        String sql = "INSERT INTO entity_state_contents (reading_value,reading_time,monitored_user_instance_id,entity_state_id) "
                + " VALUES (?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, state.getContent().getValue());
        this.stmt.setObject(2, state.getContent().getTime());
        if (state.getContent().getMonitoredInstance() != null) {
            this.stmt.setInt(3, state.getContent().getMonitoredInstance().getId());
        }
        this.stmt.setInt(4, state.getId());
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
            if (c != null) { // se c for nulo deve usar os valores iniciais, senão adiciona o conteúdo no estado
                state.setContent(c);
            }
        }
        rs.close();
        stmt.close();
        return state;
    }

}
