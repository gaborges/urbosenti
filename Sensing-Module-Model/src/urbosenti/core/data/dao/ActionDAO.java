/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Parameter;

/**
 *
 * @author Guilherme
 */
public class ActionDAO {
    private final Connection connection;
    private PreparedStatement stmt;

    public ActionDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(ActionModel action) throws SQLException {
        String sql = "INSERT INTO actions (model_id,description,has_feedback,entity_id) "
                + " VALUES (?,?,?,?);";
        action.setModelId(action.getId());
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, action.getId());
        this.stmt.setString(2, action.getDescription());
        this.stmt.setBoolean(3, action.isHasFeedback());
        this.stmt.setInt(4, action.getEntity().getId());
        this.stmt.execute();
        stmt.close();
        System.out.println("INSERT INTO actions (id,model_id,description,has_feedback,entity_id) "
                + " VALUES ("+action.getId()+","+action.getModelId()+",'"+action.getDescription()+"',"+action.isHasFeedback()+","+action.getEntity().getId()+");");
    }

    public void insertFeedbackAnswers(ActionModel action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertParameters(ActionModel action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void insertPossibleParameterContents(Parameter parameter, ActionModel action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
