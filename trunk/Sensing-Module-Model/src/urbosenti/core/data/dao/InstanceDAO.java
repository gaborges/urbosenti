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
        this.stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, instance.getDescription());
        this.stmt.setString(2, instance.getRepresentativeClass());
        this.stmt.setInt(3, instance.getEntity().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                instance.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO instances (id,description,representative_class, entity_id) "
                + " VALUES ("+instance.getId()+",'"+instance.getDescription()+"','"+instance.getRepresentativeClass()+"',"+instance.getEntity().getId()+");");
    }

    public void insertState(State state, Instance instance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertPossibleStateContents(State state, Instance instance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
