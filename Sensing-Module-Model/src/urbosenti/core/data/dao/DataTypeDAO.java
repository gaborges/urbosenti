/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.DataType;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class DataTypeDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public DataTypeDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(DataType type) throws SQLException {
        String sql = "INSERT INTO data_types (id, description, initial_value) "
                + " VALUES (?,?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.setObject(3, Content.parseContent(type,type.getInitialValue()));
        this.stmt.execute();
        this.stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO data_types (id, description,initial_value) "
                    + " VALUES (" + type.getId() + ",'" + type.getDescription() + "'," + Content.parseContent(type,type.getInitialValue()) + ");");
        }
    }

}
