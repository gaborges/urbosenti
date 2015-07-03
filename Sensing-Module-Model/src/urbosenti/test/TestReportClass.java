/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.Message; 
import urbosenti.core.communication.MessageWrapper;
import urbosenti.core.data.dao.MessageReportDAO;
import urbosenti.core.device.model.Service;

/**
 *
 * @author Guilherme
 */
public class TestReportClass {
    public static void main(String[] args) {
        Connection connection = null;
        try {
          Class.forName("org.sqlite.JDBC");
          connection = DriverManager.getConnection("jdbc:sqlite:urbosenti.db");
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        
        insert(connection);
        //updateChecked
        //updateSent
        //delete by id
        //delete all checked
        //delete all
        //get by id
        //get by time
        //get the oldest
        //get the oldest by priority
        //get all by service
        //get all by service with pagination
    }
    
    public static void insert(Connection connection){
        Message m = new Message();
        Address target = new Address();
        target.setAddress("http://localhost:8090/Test2Server/webresources/generic");
        target.setUid("666");
        target.setLayer(Address.LAYER_APPLICATION);
        
        Address origin = new Address();
        origin.setUid("1232456789");
        origin.setLayer(Address.LAYER_APPLICATION);
        
        m.setTarget(target);
        m.setOrigin(origin);
        m.setContentType("application/xml");
        m.setContent("oiiiiii");
        m.setSubject(Message.SUBJECT_UPLOAD_REPORT);
        MessageWrapper messageWrapper = new MessageWrapper(m);
        try {
            // 2 - Cria o envelope XML da UrboSenti correspondente da mensagem
            messageWrapper = MessageWrapper.createAndBuild(m);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Service s = new Service();
        s.setId(1);
        MessageReportDAO dao = new MessageReportDAO(connection);
        try {
            dao.insert(messageWrapper, s);
        } catch (SQLException ex) {
            Logger.getLogger(TestReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
