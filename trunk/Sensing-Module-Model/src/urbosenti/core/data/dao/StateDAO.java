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
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.State;

/**
 *
 * @author Guilherme
 */
public class StateDAO {
    private final Connection connection;
    private PreparedStatement stmt;

    public StateDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(State state) throws SQLException {
        String sql = "INSERT INTO entity_states (description,user_can_change,instance_state,entity_id,data_type_id,superior_limit,inferior_limit,initial_value,model_id) "
                + " VALUES (?,?,?,?,?,?,?,?,?);";
        state.setModelId(state.getId());
        this.stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, state.getDescription());
        this.stmt.setBoolean(2, state.isUserCanChange());
        this.stmt.setBoolean(3, state.isStateInstance());
        this.stmt.setInt(4, state.getEntity().getId());
        this.stmt.setInt(5, state.getDataType().getId());
        this.stmt.setObject(6,state.getSuperiorLimit());
        this.stmt.setObject(7,state.getInferiorLimit());
        this.stmt.setObject(8,state.getInitialValue());
        this.stmt.setInt(9, state.getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                state.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO entity_states (id,description,user_can_change,instance_state,entity_id,data_type_id,superior_limit,inferior_limit,initial_value,model_id)  "
                + " VALUES ("+state.getId()+",'"+state.getDescription()+"',"+state.isUserCanChange()+","+state.isStateInstance()+","+state.getEntity().getId()+
                ","+state.getDataType().getId()+",'"+state.getSuperiorLimit()+"','"+state.getInferiorLimit()+"','"+state.getInitialValue()+"',"+state.getModelId()+");");
    }

    public void insertPossibleContents(State state) throws SQLException {
        String sql = "INSERT INTO possible_instance_contents (possible_value, default_value, entity_state_id) "
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
            System.out.println("INSERT INTO possible_instance_contents (id,possible_value, default_value, entity_state_id) "
               + " VALUES ("+possibleContent.getId()+","+possibleContent.getValue()+","+possibleContent.isIsDefault()+","+state.getId()+");");
        }
    }
}
