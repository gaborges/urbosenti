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
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.EventTarget;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;

/**
 *
 * @author Guilherme
 */
public class EventDAO {

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
        System.out.println("INSERT INTO events (id,model_id,description,synchronous,implementation_type_id,entity_id) "
                + " VALUES (" + event.getId() + "," + event.getModelId() + ",'" + event.getDescription() + "'," + event.isSynchronous() + ","
                + event.getImplementation().getId() + "," + event.getEntity().getId() + ");");
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
                System.out.println("INSERT INTO event_parameters (id,description,optional,parameter_label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,event_id) "
                        + " VALUES (" + parameter.getId() + ",'" + parameter.getDescription() + "','" + parameter.getLabel() + "','" + parameter.getSuperiorLimit()
                        + "','" + parameter.getInferiorLimit() + "','" + parameter.getInitialValue() + "'," + ((parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId())
                        + "," + parameter.getDataType().getId() + "," + event.getId() + ");");

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
                System.out.println("INSERT INTO possible_event_contents (id,possible_value, default_value, event_parameter_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
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
            System.out.println("INSERT INTO event_targets_origins (event_id,target_origin_id,mandatory) "
                    + " VALUES (" + event.getId() + "," + target.getTarget().getId() + "," + target.isMandatory() + ");");
        }
    }
}
