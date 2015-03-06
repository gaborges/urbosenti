/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.InteractionType;
/**
 *
 * @author Guilherme
 */
public class InteractionTypeDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public InteractionTypeDAO(Object context) {
        this.connection = (Connection) context;
    }
    
    public void insert(InteractionType type) throws SQLException {
        String sql = "INSERT INTO interaction_types (id, description) "
                + "VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        System.out.println("INSERT INTO interaction_types (id, description) "
                + " VALUES ("+type.getId()+",'"+type.getDescription()+"');");
    }
    
}
