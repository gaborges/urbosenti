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
import urbosenti.core.device.model.Agent;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class AgentDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public AgentDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Agent agent) throws SQLException {
        String sql = "INSERT INTO agents ( address, agent_type_id, service_id, layer) "
                + "VALUES (?,?,?,?);";
        stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, agent.getRelativeAddress());
        stmt.setInt(2, agent.getAgentType().getId());
        stmt.setInt(3, agent.getService().getId());
        stmt.setInt(4, agent.getLayer());
        stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            agent.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO agents (id, address, agent_type_id, service_id, layer)  "
                    + " VALUES (" + agent.getId() + ",'" + agent.getAddress() + "'," + agent.getAgentType().getId() + "," + agent.getService().getId() + "," + agent.getLayer() + ");");
        }
    }

}
