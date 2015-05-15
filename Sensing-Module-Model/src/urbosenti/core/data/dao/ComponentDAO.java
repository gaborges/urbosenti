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
import java.util.List;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Device;

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
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, component.getDescription());
        stmt.setString(2, component.getReferedClass());
        stmt.setInt(3, component.getDevice().getId());
        stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            component.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        System.out.println("INSERT INTO components (id,description,code_class,device_id) "
                + " VALUES (" + component.getId() + ",'" + component.getDescription() + "','" + component.getReferedClass() + "'," + component.getDevice().getId() + ");");
    }

    public List<Component> getDeviceComponents(Device device) throws SQLException {
        List<Component> components = new ArrayList();
        String sql = " SELECT id, description, code_class "
                + " FROM components\n"
                + " WHERE device_id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            components.add(
                    new Component(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getString("code_class")));
        }
        rs.close();
        stmt.close();
        return components;

    }

    public Component getComponent(int id) throws SQLException {
        Component component = null;
        String sql = " SELECT id, description, code_class "
                + " FROM components\n"
                + " WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            component
                    = new Component(
                            rs.getInt("id"),
                            rs.getString("possible_value"),
                            rs.getString("default_value"));
        }
        rs.close();
        stmt.close();
        return component;

    }
}
