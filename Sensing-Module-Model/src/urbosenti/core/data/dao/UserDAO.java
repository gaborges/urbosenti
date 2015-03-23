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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.State;
import urbosenti.user.User;

/**
 *
 * @author Guilherme
 */
public class UserDAO {

    public static final int COMPONENT_ID = 6; 
    public static final int ENTITY_ID_OF_USER_MANAGEMENT = 1;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_POSITION = 1;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_LOGIN = 2;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_PASSWORD = 3;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_PRIVACY_TERM = 4;
    public static final int STATE_ID_OF_USER_MANAGEMENT_SYSTEM_DATA_SHARING_PERMISSION_BY_USER = 5;
    public static final int STATE_ID_OF_USER_MANAGEMENT_ANONYMOUS_UPLOAD = 6;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED = 7;
            
    private final List<User> users;
    private final Connection connection;
    private PreparedStatement stmt;
    private final DataManager dataManager;

    public UserDAO(Object context, DataManager dataManager) {
        this.dataManager = dataManager;
        this.users = new ArrayList();
        this.connection = (Connection) context;
    }
    /**
     * Insere o usuário no sistema e atribui um id único nele, além das
     * preferências padrão (falta fazer). Também nele é atribuído todas as
     * preferências definidas como padrão (Falta fazer).
     *
     * @param user
     */
    public void insert(User user) {
        // verifica um ID disponível
        if (users.size() > 0) {
            int greaterId = 0;
            for (User u : users) {
                if (greaterId < u.getId()) {
                    greaterId = u.getId();
                }
            }
            user.setId(greaterId + 1);
        } else {
            user.setId(1);
        }
        // Insere as configuração de preferência padrão para o usuário no banco
        // atribui as preferências no objeto do usuário
        users.add(user);
    }

    /**
     * Atualiza as informações do usuário. As preferências de configuração e
     * preferências do usuário, bem como o ID e o login não podem ser alteradas
     * por esse método.
     *
     * @param user
     */
    public void update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()
                    && users.get(i).getLogin().equals(user.getLogin())) {
                users.set(i, user);
                break;
            }
        }
    }

    /**
     * Deleta o usuário indicado pelo id e o login incluso no objeto passado por
     * parâmetro.
     *
     * @param user
     */
    public void delete(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()
                    && users.get(i).getLogin().equals(user.getLogin())) {
                users.remove(i);
                break;
            }
        }
    }

    /**
     * Retorna o usuário cacastrado que possua os respectivos login e password.
     * Caso não encontro retorna null;
     *
     * @param login
     * @param password
     * @return
     */
    public User get(String login, String password) {
        for (User user : users) {
            if (user.getPassword().equals(password)
                    && user.getLogin().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retorna o usuário com o id passado por parâmetro. Se o usuário não for
     * encontrado retorna null;
     *
     * @param id
     * @return
     */
    public User get(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    /**
     * Returna o usuário sendo monitorado. Somento um usuário pode ser
     * monitorado pelo módulo de sensoriamento por vez nessa versão da
     * UrboSenti.
     *
     * @return
     */
    public User getMonitoredUser() {
        for (User user : users) {
            if (user.isBeingMonitored()) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retorna a lista de todos os usuários cadastrados.
     *
     * @return
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Seleciona o usuário que será monitorado. Somente um usuário pode ser
     * monitorado por vez. Assim, o usuário anterior monitorado passa a ser
     * setado como não monitorado e o origundo por parâmetro passa a ser o
     * monitorado.
     *
     * @param user
     */
    public void setMonitoredUser(User user) {
        for (User u : users) {
            if (u.getId() == user.getId()) {
                u.setIsBeingMonitored(true);
            } else {
                u.setIsBeingMonitored(false);
            }
        }
    }

    /**
     * Atualiza a configuração de privacidade alterada desde que o usuário exista. Se ele não existir retorna null;
     * @param privacyState
     * @param user
     * @param newValue
     * @return Retorna o usuário alterado. Se ele não existir retorna null.
     */
    public User updatePrivacyConfiguration(int privacyState, User user, boolean newValue) {
        User u = get(user.getId());
        if(u!= null){
            switch (privacyState) {
                case User.STATE_PRIVACY_TERM:
                    u.setAcceptedPrivacyTerm(newValue);
                    break;
                case User.STATE_PRIVACY_DATA_SHARING:
                    u.setAcceptedDataSharing(newValue);
                    break;
                case User.STATE_PRIVACY_ANONYMOUS_UPLOAD:
                    u.setOptedByAnonymousUpload(newValue);
                    break;
            }
            return u;
        } 
        return null;
    }
    
    public Component getComponentDeviceModel() throws SQLException {
        Component deviceComponent = null;
        EntityStateDAO stateDAO = new EntityStateDAO(connection);
        String sql = "SELECT components.description as component_desc, code_class, entities.id as entity_id, "
                + " entities.description as entity_desc, entity_type_id, entity_types.description as type_desc\n"
                + " FROM components, entities, entity_types\n"
                + " WHERE components.id = ? and component_id = components.id and entity_type_id = entity_types.id;";
        stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, COMPONENT_ID);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            if(deviceComponent == null){
                deviceComponent = new Component();
                deviceComponent.setId(COMPONENT_ID);
                deviceComponent.setDescription(rs.getString("component_desc"));
                deviceComponent.setReferedClass(rs.getString("code_class"));
            }
            Entity entity = new Entity();
            entity.setId(rs.getInt("entity_id"));
            entity.setDescription(rs.getString("entity_desc"));
            EntityType type = new EntityType(rs.getInt("entity_type_id"),rs.getString("type_desc"));
            entity.setEntityType(type);
            entity.setStates(stateDAO.getEntityStates(entity));
            deviceComponent.getEntities().add(entity);
        }
        rs.close();
        stmt.close();
        return deviceComponent;
    }

    public ActionModel getActionStateModel(int componentId, int entityId, int stateId) {
        try {
            // primeiro pega o state ID
            State entityState = this.dataManager.getEntityStateDAO().getEntityState(componentId, entityId, stateId);
            // depois pega a ação
            return this.dataManager.getActionModelDAO().getActionState(entityState);
            throw new UnsupportedOperationException("Not supported yet.");
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
