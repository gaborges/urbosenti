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
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;

/**
 *
 * @author Guilherme
 */
public class ActionDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public ActionDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(ActionModel action) throws SQLException {
        String sql = "INSERT INTO actions (model_id,description,has_feedback,entity_id) "
                + " VALUES (?,?,?,?);";
        action.setModelId(action.getId());
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, action.getId());
        this.stmt.setString(2, action.getDescription());
        this.stmt.setBoolean(3, action.isHasFeedback());
        this.stmt.setInt(4, action.getEntity().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                action.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO actions (id,model_id,description,has_feedback,entity_id) "
                + " VALUES (" + action.getId() + "," + action.getModelId() + ",'" + action.getDescription() + "'," + action.isHasFeedback() + "," + action.getEntity().getId() + ");");
    }

    public void insertFeedbackAnswers(ActionModel action) throws SQLException {
        String sql = "INSERT INTO possible_action_contents (description, action_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (action.getFeedbackAnswers() != null) {
            for (FeedbackAnswer feedbackAnswer : action.getFeedbackAnswers()) {
                statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, feedbackAnswer.getDescription());
                statement.setInt(2, action.getId());
                statement.execute();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        feedbackAnswer.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                statement.close();
                System.out.println("INSERT INTO possible_action_contents (id,description, action_id) "
                        + " VALUES (" + feedbackAnswer.getId() + ",'" + feedbackAnswer.getDescription() + "'," + action.getId() + ");");
            }
        }
    }

    public void insertParameters(ActionModel action) throws SQLException {
        String sql = "INSERT INTO action_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,action_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement;
        for (Parameter parameter : action.getParameters()) {
            statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, parameter.getDescription());
            statement.setBoolean(2, parameter.isOptional());
            statement.setString(3, parameter.getLabel());
            statement.setObject(4, parameter.getSuperiorLimit());
            statement.setObject(5, parameter.getInferiorLimit());
            statement.setObject(6, parameter.getInitialValue());
            statement.setInt(7, (parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId());
            statement.setInt(8, parameter.getDataType().getId());
            statement.setInt(9, action.getId());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    parameter.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            statement.close();
            System.out.println("INSERT INTO action_parameters (id,description,optional,label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,event_id) "
                    + " VALUES (" + parameter.getId() + ",'" + parameter.getDescription() + "','" + parameter.getLabel() + "','" + parameter.getSuperiorLimit()
                    + "','" + parameter.getInferiorLimit() + "','" + parameter.getInitialValue() + "'," + ((parameter.getRelatedState() == null) ? -1 : parameter.getRelatedState().getId())
                    + "," + parameter.getDataType().getId() + "," + action.getId() + ");");

        }
    }

    /**
     *
     * @param parameter
     * @throws SQLException
     */
    public void insertPossibleParameterContents(Parameter parameter) throws SQLException {
        String sql = "INSERT INTO possible_action_contents (possible_value, default_value, action_parameter_id) "
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
                System.out.println("INSERT INTO possible_action_contents (id,possible_value, default_value, event_parameter_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + parameter.getId() + ");");
            }
        }
    }
}
