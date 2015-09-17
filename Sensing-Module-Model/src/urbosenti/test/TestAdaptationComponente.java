/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.core.data.dao.AdaptationDAO;
import urbosenti.core.data.dao.EventDAO;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.events.Action;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemEvent;
import urbosenti.core.events.timer.EventTimer;
import urbosenti.core.events.timer.TriggerRequest;

/**
 *
 * @author Guilherme
 */
public class TestAdaptationComponente {

    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:urbosenti.db");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            long startDate = 0L;
            long lastDate = new Date().getTime();
            System.out.println("start: "+startDate+" "+lastDate);
            ArrayList<Action> actions = new ArrayList<Action>();
            Action action;
            FeedbackAnswer feedbackAnswer;
            String sql = "SELECT  id, action_model_id, entity_id, component_id, action_type, response_time, feedback_id "
                    + " FROM generated_actions "
                    + " WHERE feedback_id <> ? AND response_time >= ? AND response_time <= ?  ORDER BY id;";
            System.out.println(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, FeedbackAnswer.ACTION_RESULT_WAS_SUCCESSFUL);
            stmt.setLong(2, startDate);
            stmt.setLong(3, lastDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                action = new Action();
                feedbackAnswer = new FeedbackAnswer(rs.getInt("feedback_id"));
                feedbackAnswer.setTime(new Date(rs.getLong("response_time")));
                action.setId(rs.getInt("action_model_id"));
                action.setDataBaseId(rs.getInt("id"));
                action.setActionType(rs.getInt("action_type"));
                action.setFeedbackAnswer(feedbackAnswer);
                action.setTargetEntityId(rs.getInt("entity_id"));
                action.setTargetComponentId(rs.getInt("component_id"));
                actions.add(action);
            }
            System.out.println("actions " + actions.size());
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TestAdaptationComponente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void test(AdaptationManager adaptationManager) {

        Event generatedEvent = new SystemEvent(adaptationManager);
        generatedEvent.setEntityId(AdaptationDAO.ENTITY_ID_OF_ADAPTATION_MANAGEMENT);
        generatedEvent.setId(AdaptationManager.EVENT_START_TASK_OF_CHECKING_SERVICE_ERRORS);
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("event", generatedEvent);
        values.put("time", 30000L);
        values.put("date", new Date());
        values.put("method", EventManager.METHOD_ONLY_INTERVAL);
        values.put("handler", this);
        Action action = new Action();
        action.setId(EventManager.ACTION_ADD_TEMPORAL_TRIGGER_EVENT); // registro no servidor
        action.setTargetComponentId(EventDAO.COMPONENT_ID);
        action.setTargetEntityId(EventDAO.ENTITY_ID_OF_TEMPORAL_TRIGGER_OF_DYNAMIC_EVENTS);
        action.setParameters(values);
        adaptationManager.getEventManager().applyAction(action);
    }

    public void test2(AdaptationManager adaptationManager) {
        try {
            adaptationManager.getDeviceManager().getDataManager().getAdaptationDAO()
                    .getErrorReporting(new Date(0), new Date());

        } catch (SQLException ex) {
            Logger.getLogger(TestAdaptationComponente.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
