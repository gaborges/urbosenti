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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import urbosenti.core.communication.Address;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.MessageWrapper;
import urbosenti.core.device.model.Service;
import urbosenti.user.User;

/**
 *
 * @author Guilherme
 */
public class MessageReportDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public MessageReportDAO(Object context) {
        this.connection = (Connection) context;
    }

    public synchronized void insert(MessageWrapper report, Service service) throws SQLException {
        String sql = "INSERT INTO reports (subject, content_type, priority, content, anonymous_upload, "
                + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                + " checked, sent, service_id, timeout) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setInt(1, report.getMessage().getSubject()); // subject integer not null,
        this.stmt.setString(2, report.getMessage().getContentType());// content_type varchar(100) not null,
        this.stmt.setInt(3, report.getMessage().getPriority());// priority integer not null,
        this.stmt.setString(4, report.getMessage().getContent());// content text not null ,
        this.stmt.setBoolean(5, report.getMessage().isAnonymousUpload());// anonymous_upload boolean not null,
        this.stmt.setObject(6, report.getMessage().getCreatedTime());// created_time varchar(100) not null,
        this.stmt.setBoolean(7, report.getMessage().isUsesUrboSentiXMLEnvelope());// uses_urbosenti_xml_envelope boolean not null,
        this.stmt.setInt(8, report.getSize());// content_size integer,
        this.stmt.setString(9, report.getMessage().getTarget().getUid());// target_uid varchar(100) not null,
        this.stmt.setInt(10, report.getMessage().getTarget().getLayer());// target_layer integer not null,
        this.stmt.setString(11, report.getMessage().getTarget().getAddress());// target_address varchar(100),
        this.stmt.setString(12, report.getMessage().getOrigin().getUid());// origin_uid varchar(100) not null,
        this.stmt.setInt(13, report.getMessage().getOrigin().getLayer());// origin_layer integer not null,
        this.stmt.setString(14, report.getMessage().getOrigin().getAddress());// origin_address varchar(100),
        this.stmt.setBoolean(15, report.isChecked());// checked boolean not null,
        this.stmt.setBoolean(16, report.isSent());// sent boolean not null,
        this.stmt.setInt(17, service.getId());// service_id integer not null,
        this.stmt.setInt(18, report.getTimeout());// timeout integer
        this.stmt.execute();
//        ResultSet generatedKeys = stmt.getGeneratedKeys();
//        if (generatedKeys.next()) {
//            report.setId(generatedKeys.getInt(1));
//        } else {
//            throw new SQLException("Creating report failed, no ID obtained.");
//        }
        stmt.close();

    }

    //updateChecked
    public synchronized void updateChecked(MessageWrapper report) throws SQLException {
        String sql = "UPDATE reports SET checked = ? WHERE id = ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        report.setChecked();
        this.stmt.setBoolean(1, report.isChecked());
        this.stmt.setInt(2, report.getId());
        this.stmt.executeUpdate();
        this.stmt.close();
    }

    //updateSent
    public void updateSent(MessageWrapper report) throws SQLException {
        String sql = "UPDATE reports SET sent = ? WHERE id = ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        report.setSent(true);
        this.stmt.setBoolean(1, report.isSent());
        this.stmt.setInt(2, report.getId());
        this.stmt.executeUpdate();
        this.stmt.close();
    }

    //delete by id
    public synchronized void delete(MessageWrapper report) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, report.getId());
        this.stmt.executeUpdate();
        this.stmt.close();
    }

    //delete by id
    public synchronized void delete(int reportId) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, reportId);
        this.stmt.executeUpdate();
        this.stmt.close();
    }

    //delete all by params
    private synchronized void deleteAll(boolean unCheckedOnly, boolean isSentOnly, Service service) throws SQLException {
        String sql = "DELETE FROM reports ";
        if (unCheckedOnly && isSentOnly && service != null) {
            sql += " WHERE checked = ? AND sent = ? AND service_id = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, false);
            this.stmt.setBoolean(2, true);
            this.stmt.setInt(3, service.getId());
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (unCheckedOnly && service != null) {
            sql += " WHERE checked = ? AND service_id = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, false);
            this.stmt.setInt(2, service.getId());
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (isSentOnly && service != null) {
            sql += " WHERE sent = ? AND service_id = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, true);
            this.stmt.setInt(2, service.getId());
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (unCheckedOnly && isSentOnly) {
            sql += " WHERE checked = ? AND sent = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, false);
            this.stmt.setBoolean(2, true);
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (unCheckedOnly) {
            sql += " WHERE checked = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, false);
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (isSentOnly) {
            sql += " WHERE sent = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setBoolean(1, true);
            this.stmt.executeUpdate();
            this.stmt.close();
        } else if (service != null) {
            sql += " WHERE service_id = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, service.getId());
            this.stmt.executeUpdate();
            this.stmt.close();
        } else {
            this.stmt.executeUpdate(sql);
            this.stmt.close();
        }

    }

    //delete all sent
    public void deleteAllSent() throws SQLException {
        this.deleteAll(false, true, null);
    }

    //delete all sent to service
    public void deleteAllSent(Service service) throws SQLException {
        this.deleteAll(false, true, service);
    }

    //delete all unChecked
    public void deleteAllUnChecked() throws SQLException {
        this.deleteAll(true, false, null);
    }

    //delete all unChecked to service
    public void deleteAllUnChecked(Service service) throws SQLException {
        this.deleteAll(true, false, service);
    }

    //delete all
    public void deleteAll(Service service) throws SQLException {
        this.deleteAll(true, true, service);
    }

    //delete all
    public void deleteAll() throws SQLException {
        this.deleteAll(true, true, null);
    }

    //get by id
    public synchronized MessageWrapper get(int id) throws SQLException {
        MessageWrapper report = null;
        Message message = new Message();
        String sql = " SELECT subject, content_type, priority, content, anonymous_upload, "
                + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                + " checked, sent, timeout FROM reports WHERE id = ? ; ";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(rs.getDate("created_time"));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(id);
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
        }
        stmt.close();
        return report;
    }
    
    //get by id
    public synchronized MessageWrapper get(Date createdTime) throws SQLException {
        MessageWrapper report = null;
        Message message = new Message();
        String sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                + " checked, sent, timeout FROM reports WHERE created_time = ? ; ";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setObject(1, createdTime);
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(new Date(rs.getDate("created_time").getTime()));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(rs.getInt("id"));
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
        }
        stmt.close();
        return report;
    }

    //get the oldest [not sent, not checked]
    public synchronized MessageWrapper getOldest() throws SQLException {
        MessageWrapper report = null;
        Message message = new Message();
        String sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                + " checked, sent, timeout FROM reports ORDER BY id LIMIT 1 ; ";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(rs.getDate("created_time"));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(rs.getInt("id"));
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
        }
        stmt.close();
        return report;
    }

    //get the oldest by priority [not sent, not checked]
    public MessageWrapper getOldest(Service service) throws SQLException {
        return getOldest(false, false, -1, service);
    }

    //get the oldest by priority [not sent, not checked]
    public MessageWrapper getOldest(int priority, Service service) throws SQLException {
        return getOldest(false, false, priority, service);
    }

    //get the oldest by priority [not sent, not checked]
    public MessageWrapper getOldestCheckedNotSent(Service service) throws SQLException {
        return getOldest(false, true, -1, service);
    }

    //get the oldest by priority [not sent, not checked]
    public MessageWrapper getOldestCheckedNotSent(int priority, Service service) throws SQLException {
        return getOldest(false, true, priority, service);
    }

    //get the oldest [not sent, not checked]
    public synchronized MessageWrapper getOldest(boolean sent, boolean checked, int priority, Service service) throws SQLException {
        MessageWrapper report = null;
        Message message = new Message();
        String sql;
        if ((priority == Message.NORMAL_PRIORITY || priority == Message.PREFERENTIAL_PRIORITY) && service != null) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND priority = ? AND service_id = ? ORDER BY id LIMIT 1 ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, priority);
            this.stmt.setInt(4, service.getId());
        } else if (priority == Message.NORMAL_PRIORITY || priority == Message.PREFERENTIAL_PRIORITY) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND priority = ? ORDER BY id LIMIT 1 ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, priority);
        } else if (service != null) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND service_id = ? ORDER BY id LIMIT 1 ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, service.getId());
        } else {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? ORDER BY id LIMIT 1 ; ";
            this.stmt = this.connection.prepareStatement(sql);
        }
        this.stmt.setBoolean(1, sent);
        this.stmt.setBoolean(2, checked);
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(new Date(Long.parseLong(rs.getString("created_time"))));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(rs.getInt("id"));
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
        }
        stmt.close();
        return report;
    }

    //get all by service
    public synchronized List<MessageWrapper> getList(Service service) throws SQLException {
        List<MessageWrapper> messages = new ArrayList();
        MessageWrapper report = null;
        Message message = new Message();
        String sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                + " checked, sent, timeout FROM reports "
                + " WHERE service_id = ? ORDER BY id ; ";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, service.getId());
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        while (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(new Date(rs.getDate("created_time").getTime()));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(rs.getInt("id"));
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
            messages.add(report);
        }
        rs.close();
        stmt.close();
        return messages;
    }

    //get all by service
    public synchronized List<MessageWrapper> getList(boolean sent, boolean checked, int priority, Service service) throws SQLException {
        List<MessageWrapper> messages = new ArrayList();
        MessageWrapper report = null;
        Message message = new Message();
        String sql;
        if ((priority == Message.NORMAL_PRIORITY || priority == Message.PREFERENTIAL_PRIORITY) && service != null) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND priority = ? AND service_id = ? ORDER BY id ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, priority);
            this.stmt.setInt(4, service.getId());
        } else if (priority == Message.NORMAL_PRIORITY || priority == Message.PREFERENTIAL_PRIORITY) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND priority = ? ORDER BY id ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, priority);
        } else if (service != null) {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? AND service_id = ? ORDER BY id ; ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(3, service.getId());
        } else {
            sql = " SELECT id, subject, content_type, priority, content, anonymous_upload, "
                    + " created_time, uses_urbosenti_xml_envelope, content_size, target_uid, "
                    + " target_layer, target_address, origin_uid, origin_layer, origin_address, "
                    + " checked, sent, timeout FROM reports "
                    + " WHERE sent = ? AND checked = ? ORDER BY id ; ";
            this.stmt = this.connection.prepareStatement(sql);
        }
        this.stmt.setBoolean(1, sent);
        this.stmt.setBoolean(2, checked);
        this.stmt.execute();
        ResultSet rs = stmt.getResultSet();
        while (rs.next()) {
            Address origin = new Address(), target = new Address();
            message.setSubject(rs.getInt("subject"));
            message.setContent(rs.getString("content"));
            message.setContentType(rs.getString("content_type"));
            message.setCreatedTime(new Date(rs.getDate("created_time").getTime()));
            message.setUsesUrboSentiXMLEnvelope(rs.getBoolean("uses_urbosenti_xml_envelope"));
            message.setAnonymousUpload(rs.getBoolean("anonymous_upload"));
            if (rs.getInt("priority") == Message.PREFERENTIAL_PRIORITY) {
                message.setPreferentialPriority();
            } else {
                message.setNormalPriority();
            }
            origin.setLayer(rs.getInt("origin_layer"));
            origin.setAddress(rs.getString("origin_address"));
            origin.setUid(rs.getString("origin_uid"));
            target.setLayer(rs.getInt("target_layer"));
            target.setAddress(rs.getString("target_address"));
            target.setUid(rs.getString("target_uid"));
            report = new MessageWrapper(message);
            report.setSent(rs.getBoolean("sent"));
            if (rs.getBoolean("checked")) {
                report.setChecked();
            } else {
                report.setUnChecked();
            }
            report.setId(rs.getInt("id"));
            report.setTimeout(rs.getInt("timeout"));
            message.setOrigin(origin);
            message.setTarget(target);
            report.setMessage(message);
            messages.add(report);
        }
        rs.close();
        stmt.close();
        return messages;
    }

    //counting reports from service
    public synchronized int reportsCount(Service service) throws SQLException {
        int returnedValue = 0;
        String sql = "SELECT count(id) as count FROM reports WHERE service_id = ? ;";
        PreparedStatement prepareStatement = this.connection.prepareStatement(sql);
        prepareStatement.setInt(1, service.getId());
        prepareStatement.execute();
        ResultSet rs = prepareStatement.getResultSet();
        if (rs.next()) {
            returnedValue = rs.getInt("count");
        }
        rs.close();
        prepareStatement.close();
        return returnedValue;
    }

    //counting reports
    public int reportsCount() throws SQLException {
        String sql = "SELECT count(id) as count FROM reports ;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.execute();
        this.stmt.close();
        ResultSet rs = this.stmt.getResultSet();
        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    public void update(MessageWrapper report) throws SQLException {
        String sql = "UPDATE reports SET subject = ? , content_type = ?, priority = ?, content = ?, anonymous_upload = ?, "
                + " created_time = ?, uses_urbosenti_xml_envelope = ?, content_size = ?, target_uid = ?, "
                + " target_layer = ?, target_address = ?, origin_uid = ?, origin_layer = ?, origin_address = ?, "
                + " checked = ?, sent = ?, timeout = ? WHERE id = ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, report.getMessage().getSubject()); // subject integer not null,
        this.stmt.setString(2, report.getMessage().getContentType());// content_type varchar(100) not null,
        this.stmt.setInt(3, report.getMessage().getPriority());// priority integer not null,
        this.stmt.setString(4, report.getMessage().getContent());// content text not null ,
        this.stmt.setBoolean(5, report.getMessage().isAnonymousUpload());// anonymous_upload boolean not null,
        this.stmt.setObject(6, report.getMessage().getCreatedTime());// created_time varchar(100) not null,
        this.stmt.setBoolean(7, report.getMessage().isUsesUrboSentiXMLEnvelope());// uses_urbosenti_xml_envelope boolean not null,
        this.stmt.setInt(8, report.getSize());// content_size integer,
        this.stmt.setString(9, report.getMessage().getTarget().getUid());// target_uid varchar(100) not null,
        this.stmt.setInt(10, report.getMessage().getTarget().getLayer());// target_layer integer not null,
        this.stmt.setString(11, report.getMessage().getTarget().getAddress());// target_address varchar(100),
        this.stmt.setString(12, report.getMessage().getOrigin().getUid());// origin_uid varchar(100) not null,
        this.stmt.setInt(13, report.getMessage().getOrigin().getLayer());// origin_layer integer not null,
        this.stmt.setString(14, report.getMessage().getOrigin().getAddress());// origin_address varchar(100),
        this.stmt.setBoolean(15, report.isChecked());// checked boolean not null,
        this.stmt.setBoolean(16, report.isSent());// sent boolean not null,
        this.stmt.setInt(17, report.getTimeout());// timeout integer
        this.stmt.setInt(18, report.getId());// service_id integer not null
        this.stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Suporte a separação por usuário não está pronta ainda.
     * @param user 
     */
    void deleteAll(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized void deleteAllExpired(Integer timeLimit) throws SQLException {
        Long limit = System.currentTimeMillis()+timeLimit;
        String sql = "DELETE FROM reports WHERE created_time >= ? ;";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setLong(1, limit);
        this.stmt.execute();
        this.stmt.close();
    }

}
