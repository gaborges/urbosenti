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
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.Interaction;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.State;

/**
 *
 * @author Guilherme
 */
public class AgentTypeDAO {
    
    private final Connection connection;
    private PreparedStatement stmt;

    public AgentTypeDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(AgentType type) throws SQLException {
        String sql = "INSERT INTO agent_types (id, description) "
                + "VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        System.out.println("INSERT INTO agent_types (id, description) "
                + " VALUES ("+type.getId()+",'"+type.getDescription()+"');");
    }

    public void insertPossibleStateContents(State state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertState(State state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertParameters(Interaction interaction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertPossibleParameterContents(Parameter parameter, Interaction interaction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
