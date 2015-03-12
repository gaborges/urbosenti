/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.TargetOrigin;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class TargetOriginDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public TargetOriginDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(TargetOrigin type) throws SQLException {
        String sql = "INSERT INTO targets_origins (id, description) "
                + "VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO targets_origins (id, description) "
                    + " VALUES (" + type.getId() + ",'" + type.getDescription() + "');");
        }
    }

}
