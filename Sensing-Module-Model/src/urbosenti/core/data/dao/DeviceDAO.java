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
        String sql = "INSERT INTO devices (description,generalDefinitionsVersion,deviceVersion,agentModelVersion) "
                + "VALUES (?,?,?,?);";
        stmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, device.getDescription());
        stmt.setDouble(2, device.getDeviceVersion());
        stmt.setDouble(3, device.getGeneralDefinitionsVersion());
        stmt.setDouble(4, device.getAgentModelVersion());
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
        System.out.println("INSERT INTO devices (id,description, generalDefinitionsVersion, deviceVersion, agentModelVersion) "
                + " VALUES ("+device.getId()+",'"+device.getDescription()+"',"+device.getDeviceVersion()+","+device.getGeneralDefinitionsVersion()+","+device.getAgentModelVersion()+");");
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
    
    public Device getDevice(){
        return null;
    }

}
