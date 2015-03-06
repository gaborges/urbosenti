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
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EventModel;

/**
 *
 * @author Guilherme
 */
public class EntityDAO {
    private final Connection connection;
    private PreparedStatement stmt;

    public EntityDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Entity entity) throws SQLException {
        String sql = "INSERT INTO entities (description,entity_type_id, component_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, entity.getDescription());
        this.stmt.setInt(2, entity.getEntityType().getId());
        this.stmt.setInt(3, entity.getComponent().getId());
        this.stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO entities (id,description,entity_type_id, component_id) "
                + " VALUES ("+entity.getId()+",'"+entity.getDescription()+"',"+entity.getEntityType().getId()+","+entity.getComponent().getId()+");");
    }
}
