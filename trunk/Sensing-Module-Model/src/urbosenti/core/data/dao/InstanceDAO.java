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
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;

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
        String sql = "INSERT INTO instances (description,representative_class,entity_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, instance.getDescription());
        this.stmt.setString(2, instance.getRepresentativeClass());
        this.stmt.setInt(3, instance.getEntity().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                instance.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO instances (id,description,representative_class, entity_id) "
                + " VALUES (" + instance.getId() + ",'" + instance.getDescription() + "','" + instance.getRepresentativeClass() + "'," + instance.getEntity().getId() + ");");
    }

    public void insertState(State state, Instance instance) throws SQLException {
        String sql = "INSERT INTO instance_states (description,user_can_change,instance_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (?,?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, state.getDescription());
        this.stmt.setBoolean(2, state.isUserCanChange());
        this.stmt.setInt(3, instance.getId());
        this.stmt.setInt(4, state.getDataType().getId());
        this.stmt.setObject(5, state.getSuperiorLimit());
        this.stmt.setObject(6, state.getInferiorLimit());
        this.stmt.setObject(7, state.getInitialValue());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                state.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO instance_states (description,user_can_change,instance_id,data_type_id,superior_limit,inferior_limit,initial_value) "
                + " VALUES (" + state.getId() + ",'" + state.getDescription() + "'," + state.isUserCanChange() + "," + instance.getId()
                + "," + state.getDataType().getId() + ",'" + state.getSuperiorLimit() + "','" + state.getInferiorLimit() + "','" + state.getInitialValue() + "');");
    }

    public void insertPossibleStateContents(State state, Instance instance) throws SQLException {
        String sql = "INSERT INTO possible_action_contents (possible_value, default_value, instance_state_id) "
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
                System.out.println("INSERT INTO possible_action_contents (id,possible_value, default_value, instance_state_id) "
                        + " VALUES (" + possibleContent.getId() + "," + possibleContent.getValue() + "," + possibleContent.isIsDefault() + "," + state.getId() + ");");
            }
        }
    }
}
