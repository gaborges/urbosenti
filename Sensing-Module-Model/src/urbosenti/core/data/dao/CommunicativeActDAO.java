/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.CommunicativeAct;
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
        System.out.println("INSERT INTO communicative_acts (id, description, agent_communication_language_id) "
                + " VALUES ("+type.getId()+",'"+type.getDescription()+"',"+type.getAgentCommunicationLanguage().getId()+");");
    }
    
}
