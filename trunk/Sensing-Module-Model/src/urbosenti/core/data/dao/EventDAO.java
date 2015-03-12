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
import static urbosenti.core.data.dao.ContextDAO.COMPONENT_ID;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.EventTarget;
import urbosenti.core.device.model.Implementation;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.TargetOrigin;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class EventDAO {
    public final static int  COMPONENT_ID = 2;
    
    private final Connection connection;
    private PreparedStatement stmt;

    public EventDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(EventModel event) throws SQLException {
        String sql = "INSERT INTO events (model_id,description,synchronous,implementation_type_id,entity_id) "
                + " VALUES (?,?,?,?,?);";
        event.setModelId(event.getId());
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, event.getId());
        this.stmt.setString(2, event.getDescription());
        this.stmt.setBoolean(3, event.isSynchronous());
        this.stmt.setInt(4, event.getImplementation().getId());
        this.stmt.setInt(5, event.getEntity().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                event.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO events (id,model_id,description,synchronous,implementation_type_id,entity_id) "
                    + " VALUES (" + event.getId() + "," + event.getModelId() + ",'" + event.getDescription() + "'," + event.isSynchronous() + ","
                    + event.getImplementation().getId() + "," + event.getEntity().getId() + ");");
        }
    }

    public void insertParameters(EventModel event) throws SQLException {
        String sql = "INSERT INTO event_parameters (description,optional,parameter_label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,event_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement;
        if (event.getParameters() != null) {
            for (Parameter parameter : event.getParameters()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, parameter.getDescription());
                statement.setBoolean(2, parameter.isOptional());
                statement.setString(3, parameter.getLabel());
                statement.setObject(4, parameter.getSuperiorLimit());
                statement.setObject(5, parameter.getInferiorLimit());
                statement.setObject(6, parameter.getInitialValue());
                statement.setInt(7, (parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId());
                statement.setInt(8, parameter.getDataType().getId());
                statement.setInt(9, event.getId());
                statement.execute();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        parameter.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                statement.close();
                if (DeveloperSettings.SHOW_DAO_SQL) {
                    System.out.println("INSERT INTO event_parameters (id,description,optional,parameter_label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,event_id) "
                            + " VALUES (" + parameter.getId() + ",'" + parameter.getDescription() + "','" + parameter.getLabel() + "','" + parameter.getSuperiorLimit()
                            + "','" + parameter.getInferiorLimit() + "','" + parameter.getInitialValue() + "'," + ((parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId())
                            + "," + parameter.getDataType().getId() + "," + event.getId() + ");");
                }

            }
        }
    }

    /**
     *
     * @param parameter
     * @throws SQLException
     */
    public void insertPossibleParameterContents(Parameter parameter) throws SQLException {
        String sql = "INSERT INTO possible_event_contents (possible_value, default_value, event_parameter_id) "
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
                if (DeveloperSettings.SHOW_DAO_SQL) {
                    System.out.println("INSERT INTO possible_event_contents (id,possible_value, default_value, event_parameter_id) "
                            + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
                }
            }
        }
    }

    public void insertTargets(EventModel event) throws SQLException {
        String sql = "INSERT INTO event_targets_origins (event_id,target_origin_id,mandatory) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        for (EventTarget target : event.getTargets()) {
            statement = this.connection.prepareStatement(sql);
            statement.setInt(1, event.getId());
            statement.setInt(2, target.getTarget().getId());
            statement.setBoolean(3, target.isMandatory());
            statement.execute();
            statement.close();
            if (DeveloperSettings.SHOW_DAO_SQL) {
                System.out.println("INSERT INTO event_targets_origins (event_id,target_origin_id,mandatory) "
                        + " VALUES (" + event.getId() + "," + target.getTarget().getId() + "," + target.isMandatory() + ");");
            }
        }
    }

    public Content getCurrentContentValue(Parameter parameter) throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time "
                + "FROM event_contents\n"
                + "WHERE event_parameter_id = ? ORDER BY id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, parameter.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(rs.getObject("reading_time", Date.class));
            content.setValue(parseContent(parameter.getDataType(),rs.getObject("reading_value")));
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
        String sql = "INSERT INTO event_contents (reading_value,reading_time,event_parameter_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, parameter.getContent().getValue());
        this.stmt.setObject(2, parameter.getContent().getTime());
        this.stmt.setInt(3, parameter.getId());
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
            System.out.println("INSERT INTO event_contents (id,reading_value,reading_time,event_parameter_id) "
                    + " VALUES (" + parameter.getContent().getId() + ",'" + parameter.getContent().getValue() + "',"
                    + ",'" + parameter.getContent().getTime().getTime() + "'," + "," + parameter.getId() + ");");
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
        while(rs.next()){
            if(deviceComponent == null){
                deviceComponent = new Component();
                deviceComponent.setId(COMPONENT_ID);
                deviceComponent.setDescription(rs.getString("component_desc"));
                deviceComponent.setReferedClass(rs.getString("code_class"));
            }
            Entity entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setDescription(rs.getString("entity_desc"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"),rs.getString("type_desc"));
            entity.setEntityType(type);
            entity.setStates(stateDAO.getEntityStates(entity));
            deviceComponent.getEntities().add(entity);
        }
        rs.close();
        stmt.close();
        return deviceComponent;
    }

    public List<EventModel> getEntityEvents(Entity entity) throws SQLException {
        List<EventModel> events = new ArrayList();
        EventModel event = null;
        String sql = "SELECT events.id as event_id, model_id, events.description as event_description, synchronous, "
                + " implementation_type_id, implementation_types.description as implementation_description "
                + " FROM events, implementation_types\n"
                + " WHERE entity_id = ? AND implementation_type_id = implementation_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            event = new EventModel();
            event.setId(rs.getInt("event_id"));
            event.setDescription(rs.getString("event_description"));
            event.setSynchronous(rs.getBoolean("synchronous"));
            event.setModelId(rs.getInt("model_id"));
            event.setEntity(entity);
            event.setImplementation(new Implementation(rs.getInt("implementation_type_id"), rs.getString("implementation_description")));
            event.setTargets(this.getEventTargets(event));
            event.setParameters(this.getEventParameters(event));
            events.add(event);
        }
        rs.close();
        stmt.close();
        return events;
    }

    private List<EventTarget> getEventTargets(EventModel event) throws SQLException {
        List<EventTarget> targets = new ArrayList();
        EventTarget target = null;
        String sql = "SELECT description, mandatory, target_origin_id "
                + " FROM event_targets_origins, targets_origins \n"
                + " WHERE event_id = ? AND target_origin_id = targets_origins.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, event.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            target = new EventTarget();
            target.setTarget(new TargetOrigin(rs.getInt("target_origin_id"), rs.getString("description")));
            target.setMandatory(rs.getBoolean("mandatory"));
            target.setEvent(event);
            targets.add(target);
        }
        rs.close();
        stmt.close();
        return targets;
    }

    private List<Parameter> getEventParameters(EventModel event) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter = null;
        String sql = "SELECT event_parameters.id as parameter_id, parameter_label, event_parameters.description as parameter_desc, \n"
                + "                optional, superior_limit, inferior_limit, entity_state_id,\n"
                + "                event_parameters.initial_value, data_type_id, data_types.initial_value as data_initial_value,\n"
                + "                data_types.description as data_desc\n"
                + "                FROM event_parameters, data_types\n"
                + "                WHERE event_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, event.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            parameter = new Parameter();
            parameter.setId(rs.getInt("parameter_id"));
            parameter.setLabel(rs.getString("parameter_label"));
            parameter.setDescription(rs.getString("parameter_desc"));
            parameter.setInferiorLimit(rs.getObject("inferior_limit"));
            parameter.setSuperiorLimit(rs.getObject("superior_limit"));
            parameter.setInitialValue(rs.getObject("initial_value"));
            parameter.setOptional(rs.getBoolean("optional"));
            if(rs.getInt("entity_state_id") > 0){
                EntityStateDAO dao = new EntityStateDAO(connection);
                parameter.setRelatedState(dao.getState(rs.getInt("entity_state_id")));
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
}
