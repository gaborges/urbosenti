/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.Implementation;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class ImplementationTypeDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public ImplementationTypeDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Implementation type) throws SQLException {
        String sql = "INSERT INTO implementation_types (id, description) "
                + " VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO implementation_types (id, description) "
                    + " VALUES (" + type.getId() + ",'" + type.getDescription() + "');");
        }
    }

}
