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
import java.util.List;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;

/**
 *
 * @author Guilherme
 */
public class EntityDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public EntityDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(Entity entity) throws SQLException {
        String sql = "INSERT INTO entities (description,entity_type_id, component_id, model_id) "
                + " VALUES (?,?,?,?);";
        this.stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.stmt.setString(1, entity.getDescription());
        this.stmt.setInt(2, entity.getEntityType().getId());
        this.stmt.setInt(3, entity.getComponent().getId());
        this.stmt.setInt(4, entity.getModelId());
        this.stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            entity.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
        System.out.println("INSERT INTO entities (id,description,entity_type_id, component_id,model_id) "
                + " VALUES (" + entity.getId() + ",'" + entity.getDescription() + "'," + entity.getEntityType().getId() + "," + entity.getComponent().getId() + "," + entity.getModelId() + ");");
    }

    public List<Entity> getComponentEntities(Component component) throws SQLException {
        List<Entity> entities = new ArrayList();
        Entity entity = null;
        String sql = "SELECT entities.id as entity_id, entity_type_id, model_id, "
                + " entities.description as entity_desc, entity_types.description as type_desc\n"
                + " FROM entities, entity_types\n"
                + " WHERE component_id = ? and entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, component.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setDescription(rs.getString("entity_desc"));
            entity.setModelId(rs.getInt("model_id"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc"));
            entity.setEntityType(type);
            entities.add(entity);
        }
        rs.close();
        stmt.close();
        return entities;
    }

    public Entity getEntity(int componentId, int entityModelId) throws SQLException {
        Entity entity = null;
        String sql = "SELECT entities.id as entity_id, entity_type_id, model_id, "
                + " entities.description as entity_desc, entity_types.description as type_desc\n"
                + " FROM entities, entity_types\n"
                + " WHERE model_id = ? AND component_id = ? AND entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, entityModelId);
        stmt.setInt(2, componentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setModelId(rs.getInt("model_id"));
            entity.setDescription(rs.getString("entity_desc"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"), rs.getString("type_desc"));
            entity.setEntityType(type);
        }
        rs.close();
        stmt.close();
        return entity;
    }
}
