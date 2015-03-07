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
import urbosenti.core.device.model.Device;

/**
 *
 * @author Guilherme
 */
public class DeviceDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public DeviceDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Device device) throws SQLException {
        String sql = "INSERT INTO devices (description) "
                + "VALUES (?);";
        stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, device.getDescription());
        //stmt.setDouble(2, device.getDeviceVersion());
        //stmt.setDouble(3, device.getGeneralDefinitionsVersion());
        //stmt.setDouble(4, device.getAgentModelVersion());
        stmt.execute();
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                device.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        //System.out.println("INSERT INTO devices (id,description, generalDefinitionsVersion, deviceVersion, agentModelVersion) "
        //        + " VALUES ("+device.getId()+",'"+device.getDescription()+"',"+device.getDeviceVersion()+","+device.getGeneralDefinitionsVersion()+","+device.getAgentModelVersion()+");");
        System.out.println("INSERT INTO devices (id,description)  VALUES (\"+device.getId()+\",'\"+device.getDescription()+\"');");
    }

    public int getCount() throws SQLException {
        int count = 0;
        String sql = "SELECT count(*) FROM devices;";
        stmt = this.connection.prepareStatement(sql);
        stmt.execute();
        try (ResultSet res = stmt.getResultSet()) {
            if (res.next()) {
                count = res.getInt(1);
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        stmt.close();
        return count;
    }
    
    public double getStoredDeviceVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT deviceVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId()>0)?device.getId():1);
        stmt.execute();
        try (ResultSet res = stmt.getResultSet()) {
            if (res.next()) {
                count = res.getDouble(1);
            }else {
                count = 0.0;
            }
        }
        stmt.close();
        return count;
    }
    
    
    public double getStoredAgentModelVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT agentModelVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId()>0)?device.getId():1);
        stmt.execute();
        try (ResultSet res = stmt.getResultSet()) {
            if (res.next()) {
                count = res.getDouble(1);
            }else {
                count = 0.0;
            }
        }
        stmt.close();
        return count;
    }
    
    public double getStoredGeneralDefinitionsVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT generalDefinitionsVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId()>0)?device.getId():1);
        stmt.execute();
        try (ResultSet res = stmt.getResultSet()) {
            if (res.next()) {
                count = res.getDouble(1);
            } else {
                count = 0.0;
            }
        }
        stmt.close();
        return count;
    }
    
    public Device getDevice(){
        return null;
    }

    public void updateStoredAgentModelVersion(Device device) throws SQLException {
        String sql = "UPDATE devices SET agentModelVersion = ? WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setDouble(1, device.getAgentModelVersion());
        stmt.setInt(2, device.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void updateStoredGeneralDefinitionsVersion(Device device) throws SQLException {
        String sql = "UPDATE devices SET generalDefinitionsVersion = ? WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setDouble(1, device.getGeneralDefinitionsVersion());
        stmt.setInt(2, device.getId());
        stmt.executeUpdate();
        stmt.close();
    }
    
    public void updateStoredDeviceVersion(Device device) throws SQLException {
        String sql = "UPDATE devices SET deviceVersion = ? WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setDouble(1, device.getDeviceVersion());
        stmt.setInt(2, device.getId());
        stmt.executeUpdate();
        stmt.close();
    }
}
