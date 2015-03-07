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
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.Interaction;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;

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
}
