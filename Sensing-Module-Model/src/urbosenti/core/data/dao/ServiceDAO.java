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
import urbosenti.core.device.model.Agent;
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.Device;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.ServiceType;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class ServiceDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public ServiceDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Service service) throws SQLException {
        String sql = "INSERT INTO services (description,service_uid,application_uid,address,service_type_id,device_id) "
                + "VALUES (?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, service.getDescription());
        stmt.setString(2, service.getServiceUID());
        stmt.setString(3, (service.getApplicationUID() == null) ? "" : service.getApplicationUID());
        stmt.setString(4, service.getAddress());
        stmt.setInt(5, service.getServiceType().getId());
        stmt.setInt(6, service.getDevice().getId());
        stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            service.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO services (id,description,service_uid,application_uid,address,service_type_id,device_id) "
                    + " VALUES (" + service.getId() + ",'" + service.getDescription() + "','"
                    + service.getServiceUID() + "','" + service.getApplicationUID() + "','" + service.getAddress() + "',"
                    + service.getServiceType().getId() + "," + service.getDevice().getId() + ");");
        }
    }

    public List<Service> getDeviceServices(Device device) throws SQLException {
        List<Service> services = new ArrayList();
        Service service = null;
        String sql = "SELECT services.id as service_id, services.description as service_description, service_uid, application_uid, address, "
                + " service_type_id, service_types.description as type_description\n"
                + " FROM services, service_types\n"
                + " WHERE device_id = ? AND service_types.id = service_type_id ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            service = new Service();
            service.setId(rs.getInt("service_id"));
            service.setDescription(rs.getString("service_description"));
            service.setAddress(rs.getString("address"));
            service.setServiceUID(rs.getString("service_uid"));
            service.setApplicationUID((rs.getString("application_uid").length() <= 6) ? "" : rs.getString("application_uid"));
            service.setServiceType(new ServiceType(rs.getInt("service_type_id"), rs.getString("type_description")));
            service.setAgent(this.getServiceAgent(service));
            services.add(service);
        }
        rs.close();
        stmt.close();
        return services;
    }

    public List<Service> getDeviceServices() throws SQLException {
        List<Service> services = new ArrayList();
        Service service = null;
        String sql = "SELECT services.id as service_id, services.description as service_description, service_uid, application_uid, address, "
                + " service_type_id, service_types.description as type_description\n"
                + " FROM services, service_types\n"
                + " WHERE service_types.id = service_type_id ;";
        stmt = this.connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            service = new Service();
            service.setId(rs.getInt("service_id"));
            service.setDescription(rs.getString("service_description"));
            service.setAddress(rs.getString("address"));
            service.setServiceUID(rs.getString("service_uid"));
            service.setApplicationUID((rs.getString("application_uid").length() <= 6) ? "" : rs.getString("application_uid"));
            service.setServiceType(new ServiceType(rs.getInt("service_type_id"), rs.getString("type_description")));
            service.setAgent(this.getServiceAgent(service));
            services.add(service);
        }
        rs.close();
        stmt.close();
        return services;
    }

    public Service getService(int id) throws SQLException {
        Service service = null;
        String sql = "SELECT services.id as service_id, services.description as service_description, service_uid, application_uid, address, "
                + " service_type_id, service_types.description as type_description\n"
                + " FROM services, service_types\n"
                + " WHERE services.id = ? AND service_types.id = service_type_id ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            service = new Service();
            service.setId(rs.getInt("service_id"));
            service.setDescription(rs.getString("service_description"));
            service.setAddress(rs.getString("address"));
            service.setServiceUID(rs.getString("service_uid"));
            service.setApplicationUID((rs.getString("application_uid").length() <= 6) ? "" : rs.getString("application_uid"));
            service.setServiceType(new ServiceType(rs.getInt("service_type_id"), rs.getString("type_description")));
            service.setAgent(this.getServiceAgent(service));
        }
        rs.close();
        stmt.close();
        return service;
    }

    private Agent getServiceAgent(Service service) throws SQLException {
        Agent agent = null;
        String sql = "SELECT agents.id as agent_id,address, agent_type_id, description, layer \n"
                + " FROM agents, agent_types\n"
                + " WHERE service_id = ? AND agent_types.id = agent_type_id ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, service.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            agent = new Agent();
            agent.setId(rs.getInt("agent_id"));
            agent.setDescription(rs.getString("description"));
            agent.setRelativeAddress(rs.getString("address"));
            agent.setAgentType(new AgentType(rs.getInt("agent_type_id"), rs.getString("description")));
            agent.setLayer(rs.getInt("layer"));
            agent.setService(service);
        }
        rs.close();
        stmt.close();
        return agent;
    }

    public void updateServiceUIDs(Service service) throws SQLException {
        String sql = "UPDATE services SET service_uid = ?,application_uid = ? "
                + " WHERE id = ?;";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, service.getServiceUID());
        stmt.setString(2, service.getApplicationUID());
        stmt.setInt(3, service.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public Service getServiceByUid(String uid) throws SQLException {
        Service service = null;
        String sql = "SELECT services.id as service_id, services.description as service_description, service_uid, application_uid, address, "
                + " service_type_id, service_types.description as type_description\n"
                + " FROM services, service_types\n"
                + " WHERE services.service_uid = ? AND service_types.id = service_type_id ;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, uid);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            service = new Service();
            service.setId(rs.getInt("service_id"));
            service.setDescription(rs.getString("service_description"));
            service.setAddress(rs.getString("address"));
            service.setServiceUID(rs.getString("service_uid"));
            service.setApplicationUID((rs.getString("application_uid").length() <= 6) ? "" : rs.getString("application_uid"));
            service.setServiceType(new ServiceType(rs.getInt("service_type_id"), rs.getString("type_description")));
            service.setAgent(this.getServiceAgent(service));
        }
        rs.close();
        stmt.close();
        return service;
    }
}
