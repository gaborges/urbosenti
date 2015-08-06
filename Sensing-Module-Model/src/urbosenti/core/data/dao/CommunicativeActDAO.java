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
import java.util.ArrayList;
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.core.device.model.CommunicativeAct;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class CommunicativeActDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public CommunicativeActDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(CommunicativeAct type) throws SQLException {
        String sql = "INSERT INTO communicative_acts (id, description, agent_communication_language_id) "
                + "VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.setInt(3, type.getAgentCommunicationLanguage().getId());
        this.stmt.execute();
        this.stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO communicative_acts (id, description, agent_communication_language_id) "
                    + " VALUES (" + type.getId() + ",'" + type.getDescription() + "'," + type.getAgentCommunicationLanguage().getId() + ");");
        }
    }
    
    public ArrayList<CommunicativeAct> getCommunicativeActs(AgentCommunicationLanguage acl) throws SQLException {
        ArrayList<CommunicativeAct> communicativeActs = new ArrayList();
        CommunicativeAct communicativeAct;
        String sql = "SELECT id,description FROM agent_communication_languages; "; 
        this.stmt = this.connection.prepareStatement(sql);
        ResultSet rs = this.stmt.executeQuery();
        while (rs.next()) {
            communicativeAct = new CommunicativeAct(rs.getInt("id"), rs.getString("description"), acl);
            communicativeActs.add(communicativeAct);
        }
        rs.close();
        stmt.close();
        return communicativeActs;
    }
}
