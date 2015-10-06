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
import urbosenti.adaptation.ExecutionPlan;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.FeedbackAnswer;
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
public class ActionModelDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public ActionModelDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(ActionModel action) throws SQLException {
        String sql = "INSERT INTO actions (model_id,description,has_feedback,entity_id) "
                + " VALUES (?,?,?,?);";
        action.setModelId(action.getId());
        this.stmt = this.connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, action.getId());
        this.stmt.setString(2, action.getDescription());
        this.stmt.setBoolean(3, action.isHasFeedback());
        this.stmt.setInt(4, action.getEntity().getId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            action.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }

        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out
                    .println("INSERT INTO actions (id,model_id,description,has_feedback,entity_id) "
                            + " VALUES ("
                            + action.getId()
                            + ","
                            + action.getModelId()
                            + ",'"
                            + action.getDescription()
                            + "',"
                            + action.isHasFeedback()
                            + ","
                            + action.getEntity().getId() + ");");
        }
    }

    public void insertFeedbackAnswers(ActionModel action) throws SQLException {
        String sql = "INSERT INTO possible_action_contents (description, action_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (action.getFeedbackAnswers() != null) {
            for (FeedbackAnswer feedbackAnswer : action.getFeedbackAnswers()) {
                statement = this.connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, feedbackAnswer.getDescription());
                statement.setInt(2, action.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    feedbackAnswer.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Creating user failed, no ID obtained.");
                }
                statement.close();
                if (DeveloperSettings.SHOW_DAO_SQL) {
                    System.out
                            .println("INSERT INTO possible_action_contents (id,description, action_id) "
                                    + " VALUES ("
                                    + feedbackAnswer.getId()
                                    + ",'"
                                    + feedbackAnswer.getDescription()
                                    + "'," + action.getId() + ");");
                }
            }
        }
    }

    public void insertParameters(ActionModel action) throws SQLException {
        String sql = "INSERT INTO action_parameters (description,optional,label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,action_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement;
        for (Parameter parameter : action.getParameters()) {
            statement = this.connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, parameter.getDescription());
            statement.setBoolean(2, parameter.isOptional());
            statement.setString(3, parameter.getLabel());
            // trata o tipo de dado do estado
            parameter.setSuperiorLimit(Content.parseContent(
                    parameter.getDataType(), parameter.getSuperiorLimit()));
            parameter.setInferiorLimit(Content.parseContent(
                    parameter.getDataType(), parameter.getInferiorLimit()));
            parameter.setInitialValue(Content.parseContent(
                    parameter.getDataType(), parameter.getInitialValue()));
            statement.setObject(4, parameter.getSuperiorLimit());
            statement.setObject(5, parameter.getInferiorLimit());
            statement.setObject(6, parameter.getInitialValue());
            statement.setInt(7, (parameter.getRelatedState() == null) ? -1
                    : parameter.getRelatedState().getId());
            statement.setInt(8, parameter.getDataType().getId());
            statement.setInt(9, action.getId());
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                parameter.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            statement.close();
            if (DeveloperSettings.SHOW_DAO_SQL) {
                System.out
                        .println("INSERT INTO action_parameters (id,description,optional,label,superior_limit,inferior_limit,initial_value,entity_state_id,data_type_id,event_id) "
                                + " VALUES ("
                                + parameter.getId()
                                + ",'"
                                + parameter.getDescription()
                                + "',"
                                + parameter.isOptional()
                                + ",'"
                                + parameter.getLabel()
                                + "','"
                                + parameter.getSuperiorLimit()
                                + "','"
                                + parameter.getInferiorLimit()
                                + "','"
                                + parameter.getInitialValue()
                                + "',"
                                + ((parameter.getRelatedState() == null) ? -1
                                        : parameter.getRelatedState().getId())
                                + ","
                                + parameter.getDataType().getId()
                                + ","
                                + action.getId() + ");");
            }

        }
    }

    /**
     *
     * @param parameter
     * @throws SQLException
     */
    public void insertPossibleParameterContents(Parameter parameter)
            throws SQLException {
        String sql = "INSERT INTO possible_action_contents (possible_value, default_value, action_parameter_id) "
                + " VALUES (?,?,?);";
        PreparedStatement statement;
        if (parameter.getPossibleContents() != null) {
            for (PossibleContent possibleContent : parameter
                    .getPossibleContents()) {
                statement = this.connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, Content.parseContent(
                        parameter.getDataType(), possibleContent.getValue()));
                statement.setBoolean(2, possibleContent.isIsDefault());
                statement.setInt(3, parameter.getId());
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    possibleContent.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Creating user failed, no ID obtained.");
                }

                statement.close();
                if (DeveloperSettings.SHOW_DAO_SQL) {
                    System.out
                            .println("INSERT INTO possible_action_contents (id,possible_value, default_value, event_parameter_id) "
                                    + " VALUES ("
                                    + possibleContent.getId()
                                    + ","
                                    + Content.parseContent(
                                            parameter.getDataType(),
                                            possibleContent.getValue())
                                    + ","
                                    + possibleContent.isIsDefault()
                                    + ","
                                    + parameter.getId() + ");");
                }
            }
        }
    }

    public Content getCurrentContentValue(Parameter parameter)
            throws SQLException {
        Content content = null;
        String sql = "SELECT id, reading_value, reading_time, score "
                + "FROM action_contents\n"
                + "WHERE action_parameter_id = ? ORDER BY id DESC LIMIT 1;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, parameter.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            content = new Content();
            // pegar o valor atual
            content.setId(rs.getInt("id"));
            content.setTime(new Date(Long.parseLong(rs.getString("reading_time"))));
            content.setValue(Content.parseContent(parameter.getDataType(),
                    rs.getObject("reading_value")));
            content.setScore(rs.getDouble("score"));
        }
        rs.close();
        stmt.close();
        return content;
    }

    public void insertContent(Parameter parameter, Action action) throws SQLException {
        String sql = "INSERT INTO action_contents (reading_value,reading_time,action_parameter_id,score,generated_action_id) "
                + " VALUES (?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        this.stmt.setObject(1, parameter.getContent().getValue());
        this.stmt.setObject(2, parameter.getContent().getTime().getTime());
        this.stmt.setInt(3, parameter.getId());
        this.stmt.setDouble(4, parameter.getContent().getScore());
        this.stmt.setInt(5, action.getDataBaseId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            parameter.getContent().setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }

        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out
                    .println("INSERT INTO action_contents (id,reading_value,reading_time,action_parameter_id,score) "
                            + " VALUES ("
                            + parameter.getContent().getId()
                            + ",'"
                            + parameter.getContent().getValue()
                            + "',"
                            + ",'"
                            + parameter.getContent().getTime().getTime()
                            + "'," + parameter.getId() + ");");
        }
    }

    List<ActionModel> getEntityActions(Entity entity) throws SQLException {
        List<ActionModel> actions = new ArrayList();
        ActionModel action = null;
        String sql = "SELECT id, model_id, description, has_feedback "
                + "FROM actions\n" + "WHERE entity_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entity.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new ActionModel();
            action.setId(rs.getInt("id"));
            action.setDescription(rs.getString("description"));
            action.setHasFeedback(rs.getBoolean("has_feedback"));
            action.setModelId(rs.getInt("model_id"));
            action.setEntity(entity);
            action.setFeedbackAnswers(this.getActionFeedbackAnswers(action));
            action.setParameters(this.getActionParameters(action));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    ActionModel getActionModel(int id) throws SQLException {
        ActionModel action = null;
        String sql = "SELECT actions.id, actions.model_id, actions.description, has_feedback, entities.description as entity_description, entity_id "
                + "FROM actions, entities "
                + "WHERE actions.id = ? AND entity_id = entities.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new ActionModel();
            action.setId(rs.getInt("id"));
            action.setDescription(rs.getString("description"));
            action.setHasFeedback(rs.getBoolean("has_feedback"));
            action.setModelId(rs.getInt("model_id"));
            action.setEntity(new Entity());
            action.getEntity().setDescription(
                    rs.getString("entity_description"));
            action.getEntity().setId(rs.getInt("entity_id"));
            action.setFeedbackAnswers(this.getActionFeedbackAnswers(action));
            action.setParameters(this.getActionParameters(action));
        }
        rs.close();
        stmt.close();
        return action;
    }

    ActionModel getActionModel(int modelId, int entityModelId, int componentId) throws SQLException {
        ActionModel action = null;
        String sql = "SELECT actions.id, actions.model_id, actions.description, has_feedback, entities.description as entity_description, entity_id "
                + "FROM actions, entities "
                + " WHERE actions.model_id = ? AND entities.model_id = ? AND component_id = ? AND entity_id = entities.id; ";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, modelId);
        stmt.setInt(2, entityModelId);
        stmt.setInt(3, componentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new ActionModel();
            action.setId(rs.getInt("id"));
            action.setDescription(rs.getString("description"));
            action.setHasFeedback(rs.getBoolean("has_feedback"));
            action.setModelId(rs.getInt("model_id"));
            action.setEntity(new Entity());
            action.getEntity().setDescription(
                    rs.getString("entity_description"));
            action.getEntity().setId(rs.getInt("entity_id"));
            action.setFeedbackAnswers(this.getActionFeedbackAnswers(action));
            action.setParameters(this.getActionParameters(action));
        }
        rs.close();
        stmt.close();
        return action;
    }

    private List<FeedbackAnswer> getActionFeedbackAnswers(ActionModel action)
            throws SQLException {
        List<FeedbackAnswer> answers = new ArrayList();
        FeedbackAnswer answer = null;
        String sql = "SELECT id, description "
                + " FROM action_feedback_answer\n" + " WHERE action_id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, action.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            answer = new FeedbackAnswer();
            answer.setId(rs.getInt("id"));
            answer.setDescription(rs.getString("description"));
            answers.add(answer);
        }
        rs.close();
        stmt.close();
        return answers;
    }

    private List<Parameter> getActionParameters(ActionModel action)
            throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter = null;
        String sql = "SELECT action_parameters.id as parameter_id, label, action_parameters.description as parameter_desc, \n"
                + "                optional, superior_limit, inferior_limit, entity_state_id,\n"
                + "                action_parameters.initial_value, data_type_id, data_types.initial_value as data_initial_value,\n"
                + "                data_types.description as data_desc\n"
                + "                FROM action_parameters, data_types\n"
                + "                WHERE action_id = ? and data_types.id = data_type_id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, action.getId());
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
            // pegar o valor atual
            Content c = this.getCurrentContentValue(parameter);
            if (c != null) { // se c for nulo deve usar os valores iniciais,
                // senÃ£o adiciona o conteÃºdo no estado
                parameter.setContent(c);
            }
            parameter.setPossibleContents(this.getPossibleContents(parameter));
            parameters.add(parameter);
        }
        rs.close();
        stmt.close();
        return parameters;
    }

    ActionModel getActionState(State entityState) throws SQLException {
        ActionModel action = null;
        String sql = "SELECT action_id, actions.model_id as action_model_id, actions.description as action_desc, has_feedback, entity_id, "
                + " action_parameters.id as parameter_id, action_parameters.description as parameter_desc, label, optional, superior_limit, "
                + " inferior_limit, action_parameters.initial_value, data_type_id, data_types.description as data_type_desc, "
                + " data_types.initial_value as type_initial_value "
                + " FROM actions, action_parameters, data_types "
                + " WHERE entity_state_id = ?  AND action_id = actions.id AND data_type_id = data_types.id; ";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entityState.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            action = new ActionModel();
            action.setId(rs.getInt("action_id"));
            action.setDescription(rs.getString("action_desc"));
            action.setHasFeedback(rs.getBoolean("has_feedback"));
            action.setModelId(rs.getInt("action_model_id"));
            action.setEntity(new Entity());
            action.getEntity().setId(rs.getInt("entity_id"));
            action.setFeedbackAnswers(this.getActionFeedbackAnswers(action));
            action.getParameters().add(
                    new Parameter(rs.getString("label"), new DataType(rs
                                    .getInt("data_type_id"), rs
                                    .getString("data_type_desc"))));
            action.getParameters().get(0).setId(rs.getInt("parameter_id"));
            action.getParameters()
                    .get(0)
                    .setInitialValue(
                            Content.parseContent(action.getParameters().get(0)
                                    .getDataType(),
                                    rs.getObject("initial_value")));
            action.getParameters()
                    .get(0)
                    .setContent(
                            getCurrentContentValue(action.getParameters()
                                    .get(0)));
            action.getParameters().get(0).setRelatedState(entityState);
            action.getParameters()
                    .get(0)
                    .setSuperiorLimit(
                            Content.parseContent(action.getParameters().get(0)
                                    .getDataType(),
                                    rs.getObject("superior_limit")));
            action.getParameters()
                    .get(0)
                    .setInferiorLimit(
                            Content.parseContent(action.getParameters().get(0)
                                    .getDataType(),
                                    rs.getObject("inferior_limit")));
            action.getParameters()
                    .get(0)
                    .setPossibleContents(
                            this.getPossibleContents(action.getParameters()
                                    .get(0)));
        }
        rs.close();
        stmt.close();
        return action;
    }

    private List<PossibleContent> getPossibleContents(Parameter parameter)
            throws SQLException {
        List<PossibleContent> possibleContents = new ArrayList();
        String sql = " SELECT id, possible_value, default_value "
                + " FROM possible_action_contents\n"
                + " WHERE action_parameter_id = ?;";
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

    void insertAction(FeedbackAnswer response, Event event, Action actionToExecute, ExecutionPlan ep) throws SQLException {
        String sql = "INSERT INTO generated_actions (action_model_id, entity_id, component_id, parameters, "
                + " response_time, feedback_id, feedback_description, action_type, execution_plan_id, event_id, event_type) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement statement = this.connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, actionToExecute.getId());
        statement.setInt(2, actionToExecute.getTargetEntityId());
        statement.setInt(3, actionToExecute.getTargetComponentId());
        statement.setString(4, actionToExecute.getParameters().toString());
        statement.setLong(5, response.getTime().getTime());
        statement.setInt(6, response.getId());
        statement.setString(7, response.getDescription());
        statement.setInt(8, actionToExecute.getActionType());
        statement.setInt(9, ep.getId());
        statement.setInt(10, event.getDatabaseId());
        statement.setInt(11, event.getEventType());
        statement.execute();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            actionToExecute.setDataBaseId(generatedKeys.getInt(1));
        } else {
            throw new SQLException(
                    "Creating user failed, no ID obtained. ");
        }
        statement.close();
    }

    public ArrayList<Action> getActions(int actionModelId, int entityId, int componentId) throws SQLException {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action action;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions WHERE action_model_id = ? AND entity_id = ? AND component_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, actionModelId);
        this.stmt.setInt(2, entityId);
        this.stmt.setInt(3, componentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    public ArrayList<Action> getActions(int feedbackId) throws SQLException {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action action;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT  id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions "
                + " WHERE feedback_id = ? ORDER BY id;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, feedbackId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    public ArrayList<Action> getActions(int feedbackId, Date startDate, Date lastDate) throws SQLException {
        return this.getActions(feedbackId, startDate.getTime(), lastDate.getTime());
    }

    public ArrayList<Action> getActions(int feedbackId, long startDate, long lastDate) throws SQLException {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action action;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT  id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions "
                + " WHERE feedback_id = ? AND response_time >= ? AND response_time <= ?  ORDER BY id;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, feedbackId);
        this.stmt.setLong(2, startDate);
        this.stmt.setLong(3, lastDate);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    public ArrayList<Action> getActionsFeedbackErrors(Date startDate, Date lastDate) throws SQLException {
        return getActionsFeedbackErros(startDate.getTime(), lastDate.getTime());
    }

    public ArrayList<Action> getActionsFeedbackErros(long startDate, long lastDate) throws SQLException {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action action;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT  id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions "
                + " WHERE feedback_id <> ? AND response_time >= ? AND response_time <= ?  ORDER BY id;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL);
        this.stmt.setLong(2, startDate);
        this.stmt.setLong(3, lastDate);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    public ArrayList<Action> getActions(int actionModelId, int entityId, int componentId, Date startDate, Date lastDate) throws SQLException {
        return this.getActions(actionModelId, entityId, componentId, startDate.getTime(), lastDate.getTime());
    }

    public ArrayList<Action> getActions(int actionModelId, int entityId, int componentId, long startDate, long lastDate) throws SQLException {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action action;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions WHERE action_model_id = ? AND entity_id = ? AND component_id = ?"
                + " AND response_time >= ? AND response_time <= ? ORDER BY id;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, actionModelId);
        this.stmt.setInt(2, entityId);
        this.stmt.setInt(3, componentId);
        this.stmt.setLong(4, startDate);
        this.stmt.setLong(5, lastDate);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
            actions.add(action);
        }
        rs.close();
        stmt.close();
        return actions;
    }

    public Action getAction(int actionId) throws SQLException {
        Action action = null;
        FeedbackAnswer feedbackAnswer;
        String sql = "SELECT id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                + " FROM generated_actions WHERE id = ? ;";
        stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, actionId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            action = new Action();
            feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
            feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
            action.setId(rs.getInt("action_model_id"));
            action.setDataBaseId(rs.getInt("id"));
            action.setActionType(rs.getInt("action_type"));
            action.setFeedbackAnswer(feedbackAnswer);
            action.setTargetEntityId(rs.getInt("entity_id"));
            action.setTargetComponentId(rs.getInt("component_id"));
        }
        rs.close();
        stmt.close();
        return action;
    }

    public List<Parameter> getActionParameterContents(Action action) throws SQLException {
        return getActionParameterContents(action.getDataBaseId());
    }

    public List<Parameter> getActionParameterContents(int dataBaseActionId) throws SQLException {
        List<Parameter> parameters = new ArrayList();
        Parameter parameter;
        Content content;
        String sql = "SELECT action_contents.id as content_id, reading_value, reading_time, score,"
                + "     action_parameters.id as parameter_id, label, action_parameters.description as parameter_desc, "
                + "     optional, superior_limit, inferior_limit, entity_state_id, action_parameters.initial_value, "
                + "     data_type_id, data_types.initial_value as data_initial_value, data_types.description as data_desc "
                + " FROM action_contents, action_parameters, data_types WHERE generated_action_id = ? "
                + " AND action_parameter_id = action_parameters.id AND data_types.id = data_type_id "
                + " ORDER BY generated_action_id ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, dataBaseActionId);
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
            content.setScore(rs.getDouble("score"));
            parameter.setContent(content);

            parameter.setPossibleContents(this.getPossibleContents(parameter));
            parameters.add(parameter);
        }
        rs.close();
        stmt.close();
        return parameters;
    }
}
