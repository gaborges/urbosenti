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
import urbosenti.core.device.model.Component;

/**
 *
 * @author Guilherme
 */
public class ComponentDAO {
    private final Connection connection;
    private PreparedStatement stmt;

    public ComponentDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Component component) throws SQLException {
        String sql = "INSERT INTO components (description,code_class,device_id) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, component.getDescription());
        stmt.setString(2, component.getReferedClass());
        stmt.setInt(3, component.getDevice().getId());
        stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                component.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        System.out.println("INSERT INTO components (id,description,code_class,device_id) "
                + " VALUES ("+component.getId()+",'"+component.getDescription()+"','"+component.getReferedClass()+"',"+component.getDevice().getId()+");");
    }
}
