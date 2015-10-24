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
import urbosenti.core.device.BaseComponentManager;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.EventTarget;
import urbosenti.core.device.model.Implementation;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.TargetOrigin;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.core.events.SystemEvent;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class EventModelDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public EventModelDAO(Object context) {
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
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            event.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
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
                //  trata o tipo de dado do estado
                parameter.setSuperiorLimit(Content.parseContent(parameter.getDataType(), parameter.getSuperiorLimit()));
                parameter.setInferiorLimit(Content.parseContent(parameter.getDataType(), parameter.getInferiorLimit()));
                parameter.setInitialValue(Content.parseContent(parameter.getDataType(), parameter.getInitialValue()));
                statement.setObject(4, parameter.getSuperiorLimit());
                statement.setObject(5, parameter.getInferiorLimit());
                statement.setObject(6, parameter.getInitialValue());
                statement.setInt(7, (parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId());
                statement.setInt(8, parameter.getDataType().getId());
                statement.setInt(9, event.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    parameter.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
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
                statement.setObject(1, Content.parseContent(parameter.getDataType(), possibleContent.getValue()));
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
                if (DeveloperSettings.SHOW_DAO_SQL) {
                    System.out.println("INSERT INTO possible_event_contents (id,possible_value, default_value, event_parameter_id) "
                            + " VALUES (" + possibleContent.getId() + "," + Content.parseContent(parameter.getDataType(), possibleContent.getValue()) + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
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
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(parameter.getDataType(), rs.getObject("reading_value")));
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(Parameter parameter, Event event) throws SQLException {
        String sql = "INSERT INTO event_contents (reading_value,reading_time,event_parameter_id, generated_event_id) "
                + " VALUES (?,?,?,?);";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setObject(1, Content.parseContent(parameter.getDataType(), parameter.getContent().getValue()));
        preparedStatement.setObject(2, parameter.getContent().getTime().getTime());
        preparedStatement.setInt(3, parameter.getId());
        preparedStatement.setInt(4, event.getDatabaseId());
        preparedStatement.execute();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            parameter.getContent().setId(generatedKeys.getInt(1));
        }
        preparedStatement.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO event_contents (id,reading_value,reading_time,event_parameter_id) "
                    + " VALUES (" + parameter.getContent().getId() + ",'" + Content.parseContent(parameter.getDataType(), parameter.getContent().getValue()) + "',"
                    + ",'" + parameter.getContent().getTime().getTime() + "'," + "," + parameter.getId() + ");");
        }
    }

    public List<EventModel> getEntityEventModels(Entity entity) throws SQLException {
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
            DataType type = new DataType();
            type.setId(rs.getInt("data_type_id"));
            type.setDescription(rs.getString("data_desc"));
            type.setInitialValue(rs.getObject("data_initial_value"));
            parameter.setDataType(type);
            parameter.setInferiorLimit(Content.parseContent(type, rs.getObject("inferior_limit")));
            parameter.setSuperiorLimit(Content.parseContent(type, rs.getObject("superior_limit")));
            parameter.setInitialValue(Content.parseContent(type, rs.getObject("initial_value")));
            parameter.setOptional(rs.getBoolean("optional"));
            if (rs.getInt("entity_state_id") > 0) {
                EntityStateDAO dao = new EntityStateDAO(connection);
                parameter.setRelatedState(dao.getState(rs.getInt("entity_state_id")));
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

    public EventModel get(int modelId, int entityModelId, int componentId) throws SQLException {
        EventModel event = null;
        String sql = "SELECT events.id as event_id, events.model_id, events.description as event_description, synchronous, \n"
                + "                implementation_type_id, implementation_types.description as implementation_description \n"
                + "                FROM events, implementation_types, entities, components \n"
                + "                WHERE events.model_id = ? AND entities.model_id = ? AND components.id = ? "
                + "                AND implementation_type_id = implementation_types.id AND entity_id = entities.id\n"
                + "                AND component_id = components.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, modelId);
        stmt.setInt(2, entityModelId);
        stmt.setInt(3, componentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            event = new EventModel();
            event.setId(rs.getInt("event_id"));
            event.setDescription(rs.getString("event_description"));
            event.setSynchronous(rs.getBoolean("synchronous"));
            event.setModelId(rs.getInt("model_id"));
            event.setImplementation(new Implementation(rs.getInt("implementation_type_id"), rs.getString("implementation_description")));
            event.setTargets(this.getEventTargets(event));
            event.setParameters(this.getEventParameters(event));
        }
        rs.close();
        stmt.close();
        return event;
    }

    public EventModel get(int id) throws SQLException {
        EventModel event = null;
        String sql = "SELECT events.id as event_id, events.model_id, events.description as event_description, synchronous, \n"
                + "                implementation_type_id, implementation_types.description as implementation_description \n"
                + "                FROM events, implementation_types "
                + "                WHERE id = ? AND implementation_type_id = implementation_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            event = new EventModel();
            event.setId(rs.getInt("event_id"));
            event.setDescription(rs.getString("event_description"));
            event.setSynchronous(rs.getBoolean("synchronous"));
            event.setModelId(rs.getInt("model_id"));
            event.setImplementation(new Implementation(rs.getInt("implementation_type_id"), rs.getString("implementation_description")));
            event.setTargets(this.getEventTargets(event));
            event.setParameters(this.getEventParameters(event));
        }
        rs.close();
        stmt.close();
        return event;
    }

    /**
     * Gera o evento e salva o valor do parâmetros. Se algum parâmetro
     * obrigatório estiver com valor nulo é gerada uma exceção.
     *
     * @param event
     * @param eventModel
     * @throws SQLException
     * @throws Exception
     */
    public void insert(Event event, EventModel eventModel) throws SQLException, Exception {
        // Criar evento
        String sql = "INSERT INTO generated_events (event_id,entity_id,component_id,time,timeout,event_type) "
                + " VALUES (?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, eventModel.getId());
        this.stmt.setInt(2, event.getEntityId());
        this.stmt.setInt(3, event.getComponentManager().getComponentId());
        this.stmt.setObject(4, event.getTime());
        this.stmt.setObject(5, event.getTimeout());
        this.stmt.setInt(6, event.getEventType());
        this.stmt.execute();
        this.stmt.close();
        int lastGeneratedEventId = this.getGeneratedIdByTime(eventModel.getId(),event.getTime());
        if (lastGeneratedEventId > 0) {
            event.setDatabaseId(lastGeneratedEventId);
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        // adicionar conteúdos dos parâmetros
        for (Parameter p : eventModel.getParameters()) {
            if (event.getParameters().get(p.getLabel()) == null && !p.isOptional()) {
                throw new Exception("Parameter " + p.getLabel() + " from the event " + eventModel.getDescription() + " id " + eventModel.getId()
                        + " was not found. Such parameter is not optional!");
            } else {
                if (event.getParameters().get(p.getLabel()) != null) {
                    p.setContent(new Content(
                            Content.parseContent(p.getDataType(), event.getParameters().get(p.getLabel())),
                            event.getTime()));
                    this.insertContent(p, event);
                }
            }
        }
    }

    public Date getLastEventTime(int eventModelId, int entityModelId, int componentModelId) throws SQLException, Exception {
        Date eventTime = null;
        String sql = "SELECT time FROM  enerated_events "
                + " WHERE event_id = ? AND entity_id = ? AND component_id = ? ORDER BY time DESC "
                + " LIMIT 1;";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, eventModelId);
        this.stmt.setInt(2, entityModelId);
        this.stmt.setInt(3, componentModelId);;
        this.stmt.execute();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            eventTime = new Date(rs.getLong("time"));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        return eventTime;
    }

    public Content getLastEventContentByLabelAndValue(Object value, String label, int eventModelId, int entityModelId, int componentModelId) throws SQLException, Exception {
        Content content = null;
        String sql = " SELECT event_contents.id as id, reading_value, time "
                + " FROM  generated_events, event_contents , event_parameters, events "
                + " WHERE reading_value = ? AND parameter_label = ? AND events.model_id = ? AND generated_events.entity_id = ? AND component_id = ? "
                + " AND generated_event_id = generated_events.id AND event_parameter_id = event_parameters.id AND events.id = generated_events.event_id "
                + " ORDER BY time DESC LIMIT 1;";
        PreparedStatement stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setObject(1, value);
        stmt.setString(2, label);
        stmt.setInt(3, eventModelId);
        stmt.setInt(4, entityModelId);
        stmt.setInt(5, componentModelId);;
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            content = new Content();
            content.setTime(new Date(rs.getLong("time")));
            content.setId(rs.getInt("id"));
            content.setValue(value);
        }
        stmt.close();
        return content;
    }

    public Event getEvent(Action action, BaseComponentManager bcm) throws SQLException{
        return getEvent(action.getDataBaseId(),bcm);
    }

    public Event getEvent(int dataBaseEventId,BaseComponentManager bcm) throws SQLException {
        Event event = null;
        String sql = " SELECT generated_events.id, generated_events.event_id, generated_events.entity_id, generated_events.component_id, generated_events.time, "
                + " timeout, generated_events.event_type, description, synchronous "
                + " FROM generated_events, generated_actions, events "
                + " WHERE generated_actions.id = ? AND events.id = generated_events.event_id AND generated_actions.event_id = generated_events.id;";
        
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, dataBaseEventId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            for(ComponentManager cm : bcm.getComponentManagers()){
                if(rs.getInt("component_id")==cm.getComponentId()){
                    event = new SystemEvent(cm);
                }
            }          
            event.setId(rs.getInt("event_id"));
            event.setDatabaseId(rs.getInt("id"));
            event.setEventType(rs.getInt("event_type"));
            event.setEntityId(rs.getInt("entity_id"));
            event.setTime(new Date(rs.getLong("time")));
            event.setTimeout(rs.getInt("timeout"));
            event.setName(rs.getString("description"));
            event.setSynchronous(rs.getBoolean("synchronous"));
        }
        rs.close();
        stmt.close();
        return event;
    }

    public List<Parameter> getEventParameterContents(Action action) throws SQLException {
        return getEventParameterContents(action.getDataBaseId());
    }
    
    public List<Parameter> getEventParameterContents(int dataBaseEventId) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter;
        Content content;
        String sql = "SELECT event_contents.id as content_id, reading_value, reading_time, event_parameters.id as parameter_id, "
                + " parameter_label as label, event_parameters.description as parameter_desc, optional, superior_limit, "
                + " inferior_limit, entity_state_id, event_parameters.initial_value, data_type_id, "
                + " data_types.initial_value as data_initial_value, data_types.description as data_desc "
                + " FROM event_contents, event_parameters, data_types "
                + " WHERE generated_event_id = ?  AND event_parameter_id = event_parameters.id AND data_types.id = data_type_id "
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
            if (rs.getInt("entity_state_id") > 0) {
                EntityStateDAO dao = new EntityStateDAO(connection);
                parameter.setRelatedState(dao.getState(rs
                        .getInt("entity_state_id")));
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
                + " FROM possible_event_contents\n"
                + " WHERE event_parameter_id = ?;";
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
    
    public int getLastGeneratedEventId() throws SQLException{
        Integer id = 0;
        PreparedStatement ps = this.connection.prepareStatement("SELECT id FROM generated_events ORDER BY id DESC LIMIT 1;");
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            id = rs.getInt("id");
        }
        return id;
    }

    private int getGeneratedIdByTime(int databaseEventId, Date time) throws SQLException {
        Integer id = 0;
        PreparedStatement ps = this.connection.prepareStatement("SELECT id FROM generated_events WHERE time = ? AND event_id = ? ;");
        ps.setLong(1, time.getTime());
        ps.setInt(2, databaseEventId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            id = rs.getInt("id");
        }
        return id;
    }
}
