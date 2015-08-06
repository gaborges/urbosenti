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
import urbosenti.core.data.DataManager;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Device;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.Service;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public final class DeviceDAO {

    public static final int COMPONENT_ID = 1;
    public static final int ENTITY_ID_OF_SERVICE_REGISTRATION = 1;
    public static final int ENTITY_ID_OF_URBOSENTI_SERVICES = 2;
    public static final int ENTITY_ID_OF_BASIC_DEVICE_INFORMATIONS = 3;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_ACTIVITY_STATUS = 1;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_ADAPTATION_COMPONENT_STATUS = 2;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_LOCATION_COMPONENT_STATUS = 3;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_CONTEXT_COMPONENT_STATUS = 4;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_USER_COMPONENT_STATUS = 5;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_CONCERNS_COMPONENT_STATUS = 6;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_RESOURCES_COMPONENT_STATUS = 7;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_WIRED_COMMUNICATION_INTERFACE_STATUS = 8;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_WIFI_COMMUNICATION_INTERFACE_STATUS = 9;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_MOBILE_DATA_COMMUNICATION_INTERFACE_STATUS = 10;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_DTN_COMMUNICATION_INTERFACE_STATUS = 11;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_GCM_INPUT_INTERFACE_STATUS = 12;
    public static final int STATE_ID_OF_URBOSENTI_SERVICES_SOCKET_INPUT_INTERFACE_STATUS = 13;
    public static final int STATE_ID_OF_SERVICE_REGISTRATION_FOR_SERVICE_UID = 1;
    public static final int STATE_ID_OF_SERVICE_REGISTRATION_FOR_REMOTE_PASSWORD = 2;
    public static final int STATE_ID_OF_SERVICE_REGISTRATION_FOR_APPLICATION_UID = 3;
    public static final int STATE_ID_OF_SERVICE_REGISTRATION_FOR_SERVICE_EXPIRATION_TIME = 4;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_STORAGE_SYSTEM = 1;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORES = 2;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_CORE_FREQUENCY_CLOCK = 3;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_CPU_MODEL = 4;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_NATIVE_OPERATIONAL_SYSTEM = 5;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_MEMORY_RAM = 6;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_DEVICE_MODEL = 7;
    public static final int STATE_ID_OF_BASIC_DEVICE_INFORMATIONS_ABOUT_BATTERY_CAPACITY = 8;

    public static final int DEVICE_DB_ID = 1;
    private final Connection connection;
    private PreparedStatement stmt;
    private final DataManager dataManager;

    public DeviceDAO(Object context, DataManager dataManager) {
        this.dataManager = dataManager;
        this.connection = (Connection) context;
    }

    public void insert(Device device) throws SQLException {
        String sql = "INSERT INTO devices (description, generalDefinitionsVersion, deviceVersion, agentModelVersion) "
                + "VALUES (?,?,?,?);";
        stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, device.getDescription());
        stmt.setDouble(2, device.getDeviceVersion());
        stmt.setDouble(3, device.getGeneralDefinitionsVersion());
        stmt.setDouble(4, device.getAgentModelVersion());
        stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            device.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO devices (id,description, generalDefinitionsVersion, deviceVersion, agentModelVersion) "
                    + " VALUES (" + device.getId() + ",'" + device.getDescription() + "'," + device.getDeviceVersion() + "," + device.getGeneralDefinitionsVersion() + "," + device.getAgentModelVersion() + ");");
            //System.out.println("INSERT INTO devices (id,description)  VALUES ("+device.getId()+",'"+device.getDescription()+"');");
        }
    }

    public int getCount() throws SQLException {
        int count = 0;
        String sql = "SELECT count(*) FROM devices;";
        stmt = this.connection.prepareStatement(sql);
        stmt.execute();
        ResultSet res = stmt.getResultSet();
        if (res.next()) {
            count = res.getInt(1);
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        return count;
    }

    public double getStoredDeviceVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT deviceVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId() > 0) ? device.getId() : 1);
        stmt.execute();
        ResultSet res = stmt.getResultSet();
        if (res.next()) {
            count = res.getDouble(1);
        } else {
            count = 0.0;
        }
        stmt.close();
        return count;
    }

    public double getStoredAgentModelVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT agentModelVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId() > 0) ? device.getId() : 1);
        stmt.execute();
        ResultSet res = stmt.getResultSet();
        if (res.next()) {
            count = res.getDouble(1);
        } else {
            count = 0.0;
        }
        stmt.close();
        return count;
    }

    public double getStoredGeneralDefinitionsVersion(Device device) throws SQLException {
        double count = 0.0;
        String sql = "SELECT generalDefinitionsVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, (device.getId() > 0) ? device.getId() : 1);
        stmt.execute();
        ResultSet res = stmt.getResultSet();
        if (res.next()) {
            count = res.getDouble(1);
        } else {
            count = 0.0;
        }
        stmt.close();
        return count;
    }

    public Device getDevice() throws SQLException {
        Device device = null;
        String sql = "SELECT agentModelVersion,description,deviceVersion,generalDefinitionsVersion FROM devices WHERE id = ?;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, DEVICE_DB_ID);
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            device = new Device();
            device.setId(DEVICE_DB_ID);
            device.setDescription(rs.getString("description"));
            device.setAgentModelVersion(rs.getDouble("agentModelVersion"));
            device.setDeviceVersion(rs.getDouble("deviceVersion"));
            device.setGeneralDefinitionsVersion(rs.getDouble("generalDefinitionsVersion"));
        }
        stmt.close();
        return device;
    }

    public Device getDeviceModel(DataManager dataManager) throws SQLException {
        Device device = getDevice();
        device.setComponents(dataManager.getComponentDAO().getDeviceComponents(device));
        for (Component component : device.getComponents()) {
            component.setEntities(dataManager.getEntityDAO().getComponentEntities(component));
            for (Entity entity : component.getEntities()) {
                entity.setActions(dataManager.getActionModelDAO().getEntityActions(entity));
                entity.setEvents(dataManager.getEventModelDAO().getEntityEvents(entity));
                entity.setInstaces(dataManager.getInstanceDAO().getEntityInstances(entity));
                entity.setStates(dataManager.getEntityStateDAO().getEntityStates(entity));
            }
        }
        device.setServices(dataManager.getServiceDAO().getDeviceServices(device));
        for (Service service : device.getServices()) {
            service.getAgent().getAgentType().setInteraction(dataManager.getAgentTypeDAO().getAgentInteractions(service.getAgent().getAgentType()));
            service.getAgent().getAgentType().setStates(dataManager.getAgentTypeDAO().getAgentStates(service.getAgent().getAgentType()));
            service.getAgent().setConversations(dataManager.getAgentTypeDAO().getAgentConversations(service.getAgent()));
        }
        return device;
    }

    public Component getComponentDeviceModel() throws SQLException {
        Component deviceComponent = null;
        EntityStateDAO stateDAO = new EntityStateDAO(connection);
        String sql = "SELECT components.description as component_desc, code_class, entities.id as entity_id, "
                + " entities.description as entity_desc, entity_type_id, entity_types.description as type_desc\n"
                + " FROM components, entities, entity_types\n"
                + " WHERE components.id = ? and component_id = components.id and entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, COMPONENT_ID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (deviceComponent == null) {
                deviceComponent = new Component();
                deviceComponent.setId(COMPONENT_ID);
                deviceComponent.setDescription(rs.getString("component_desc"));
                deviceComponent.setReferedClass(rs.getString("code_class"));
            }
            Entity entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setDescription(rs.getString("entity_desc"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc"));
            entity.setEntityType(type);
            entity.setStates(stateDAO.getEntityStates(entity));
            deviceComponent.getEntities().add(entity);
        }
        rs.close();
        stmt.close();
        return deviceComponent;
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
