/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import urbosenti.core.data.DataManager;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.Content;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.State;
import urbosenti.user.User;
import urbosenti.user.UserPreference;

/**
 *
 * @author Guilherme
 */
public class UserDAO {

    public static final int COMPONENT_ID = 6;
    public static final int ENTITY_ID_OF_USER_MANAGEMENT = 1;
    public static final int ENTITY_ID_OF_USER_AUTHENTICATION = 2;
    public static final int ENTITY_ID_OF_USER_PROFILE_PREFERENCES = 3;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_POSITION = 1;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_LOGIN = 2;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_PASSWORD = 3;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_PRIVACY_TERM = 4;
    public static final int STATE_ID_OF_USER_MANAGEMENT_SYSTEM_DATA_SHARING_PERMISSION_BY_USER = 5;
    public static final int STATE_ID_OF_USER_MANAGEMENT_ANONYMOUS_UPLOAD = 6;
    public static final int STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED = 7;

    private SQLiteDatabase database;
    private final DataManager dataManager;

    public UserDAO(Object context, DataManager dataManager) {
        this.dataManager = dataManager;
        this.database = (SQLiteDatabase) context;
    }

    /**
     * Insere o usu�rio no sistema e atribui um id �nico nele, al�m das
     * prefer�ncias padr�o (falta fazer). Tamb�m nele � atribu�do todas as
     * prefer�ncias definidas como padr�o (Falta fazer).
     *
     * @param user
     */
    public void insert(User user) {

        try {
            Date now = new Date();
            Instance instance = new Instance();
            instance.setDescription(user.getLogin());
            instance.setEntity(this.dataManager.getEntityDAO().getEntity(COMPONENT_ID, ENTITY_ID_OF_USER_MANAGEMENT));
            instance.setRepresentativeClass(User.class.toString());
            List<State> states = this.dataManager.getEntityStateDAO().getInitialModelInstanceStates(instance.getEntity());
            int count = this.dataManager.getInstanceDAO().getEntityInstanceCount(instance.getEntity());
            states.get(STATE_ID_OF_USER_MANAGEMENT_USER_POSITION - 1).setContent(new Content(count, now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_USER_LOGIN - 1).setContent(new Content(user.getLogin(), now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_USER_PASSWORD - 1).setContent(new Content(user.getPassword(), now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_USER_PRIVACY_TERM - 1).setContent(new Content(user.getAcceptedPrivacyTerm(), now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_SYSTEM_DATA_SHARING_PERMISSION_BY_USER - 1).setContent(new Content(user.getAcceptedDataSharing(), now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_ANONYMOUS_UPLOAD - 1).setContent(new Content(user.getOptedByAnonymousUpload(), now));
            states.get(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED - 1).setContent(new Content(false, now));
            instance.setStates(states);
            instance.setModelId(count + 1);
            this.dataManager.getInstanceDAO().insert(instance);
            for (State s : instance.getStates()) {
                this.dataManager.getInstanceDAO().insertState(s, instance);
                this.dataManager.getInstanceDAO().insertPossibleStateContents(s, instance);
            }
            // adiciona a inst�ncia, ao fazer isso internamente esse m�todo preenche os novos valores
            user.setInstance(instance);
            user.setUserPreferences(this.getUserPreferences(instance));
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }

    /**
     * Atualiza as informa��es do usu�rio. As prefer�ncias de configura��o e
     * prefer�ncias do usu�rio, bem como o ID e o login n�o podem ser alteradas
     * por esse m�todo. Em resumo s� altera o password.
     *
     * @param user
     */
    public void updatePassword(User user) {
        try {
            if (user.getInstance() == null) {
                throw new Error("Inst�ncia do usu�rio n�o foi especificada!");
            }
            // busca o estado a partir da inst�ncia
            State state = UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_PASSWORD, user.getInstance());
            // verifica se o password n�o modificou
            if (state.getCurrentValue().toString().equals(user.getPassword())) {
                return;
            }
            state.setContent(new Content(user.getPassword()));
            this.dataManager.getInstanceDAO().insertContent(state);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }

    /**
     * Deleta o usu�rio indicado pelo id incluso no objeto passado por
     * par�metro e todos os dados associados a ele.
     *
     * @param user
     * @throws java.sql.SQLException
     */
    public void deleteAllUserInformation(User user) throws SQLException {
        // exclui os conte�dos monitorados nos estados da entidade
        this.dataManager.getStateDAO().deleteUserContents(user.getInstance());
        // exclui os conte�dos monitorados nos estados das inst�ncias
        this.dataManager.getInstanceDAO().deleteUserInstanceContents(user.getInstance());
        // exclui as mensagens associadas
        this.dataManager.getCommunicationDAO().deleteUserReports(user);
        // exclui demais dados da inst�ncia de usu�rio
        this.delete(user);
    }
    
    /**
     * Deleta somente o usu�rio indicado pelo id incluso no objeto passado por
     * par�metro.
     *
     * @param user
     */
    public void delete(User user) throws SQLException {
        // exclui esclui o modelo de estado da inst�ncia
        this.dataManager.getInstanceDAO().deleteInstanceStateContents(user.getInstance());
        // exclui o modelo de estado da inst�ncia
        this.dataManager.getInstanceDAO().deleteInstanceStates(user.getInstance());
        // exclui a inst�ncia
        this.dataManager.getInstanceDAO().deleteInstance(user.getInstance());
    }

    /**
     * Retorna o usu�rio cacastrado que possua os respectivos login e password.
     * Caso n�o encontro retorna null;
     *
     * @param login
     * @param password
     * @return
     * @throws java.sql.SQLException
     */
    public User get(String login, String password) throws SQLException {
        Instance instance;
        // busca a inst�ncia de usu�rio que possui login de acordo com o �timo conte�do do estado de login,
        instance = this.dataManager.getInstanceDAO().getInstanceByStateContent(
                login, STATE_ID_OF_USER_MANAGEMENT_USER_LOGIN, ENTITY_ID_OF_USER_MANAGEMENT, COMPONENT_ID);
        // se retornar nulo o usu�rio n�o existe
        if (instance == null) {
            return null;
        }
        // Compara os conte�dos atuais do usu�rio relativos ao login e ao password, se n�o forem retorna null
        if (UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_LOGIN, instance).toString().equals(login)
                && UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_PASSWORD, instance).toString().equals(password)) {
            // cria um usu�rio e atribui os dados
            User user = new User();
            user.setInstance(instance);
            user.setUserPreferences(this.getUserPreferences(instance));
            return user;
        }
        return null;
    }

    /**
     * Retorna o usu�rio com o id passado por par�metro. Se o usu�rio n�o for
     * encontrado retorna null;
     *
     * @param id
     * @return
     * @throws java.sql.SQLException
     */
    public User get(int id) throws SQLException {
        Instance instance;
        // busca a inst�ncia de usu�rio que possui login de acordo com o �timo conte�do do estado de login,
        instance = this.dataManager.getInstanceDAO().getInstance(id);
        // se retornar nulo o usu�rio n�o existe
        if (instance == null) {
            return null;
        }
        // cria um usu�rio e atribui os dados
        User user = new User();
        user.setInstance(instance);
        user.setUserPreferences(this.getUserPreferences(instance));
        return user;
    }

    /**
     * Returna o usu�rio sendo monitorado. Somento um usu�rio pode ser
     * monitorado pelo m�dulo de sensoriamento por vez nessa vers�o da
     * UrboSenti.
     *
     * @return retorna null se nenhum usu�rio estiver sendo monitorado
     * @throws java.sql.SQLException
     */
    public User getMonitoredUser() throws SQLException {
        //  busca quem est� sendo monitorado e atualiza para n�o monitorado
        Instance instance = this.dataManager.getInstanceDAO().getInstanceByStateContent(
                true, STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, ENTITY_ID_OF_USER_MANAGEMENT, COMPONENT_ID);
        if(instance != null){
            User user = new User();
            user.setInstance(instance);
            user.setUserPreferences(this.getUserPreferences(instance));
        }
        return null;
    }

    /**
     * Retorna a lista de todos os usu�rios cadastrados.
     *
     * @return
     * @throws java.sql.SQLException
     */
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList();
        // busca a todas as inst�ncias de usu�rio
        List<Instance> instances = this.dataManager.getInstanceDAO().getEntityInstanceModels(
                this.dataManager.getEntityDAO().getEntity(COMPONENT_ID, ENTITY_ID_OF_USER_MANAGEMENT));
        // percorre toda a lista a adiciona em objetos usu�rios
        for(Instance instance : instances){
            // cria um usu�rio e atribui os dados
            User user = new User();
            user.setInstance(instance);
            user.setUserPreferences(this.getUserPreferences(instance));
        }
        return users;
    }

    /**
     * Seleciona o usu�rio que ser� monitorado. Somente um usu�rio pode ser
     * monitorado por vez. Assim, o usu�rio anterior monitorado passa a ser
     * setado como n�o monitorado e o origundo por par�metro passa a ser o
     * monitorado.
     *
     * @param user
     * @throws java.sql.SQLException
     */
    public void setMonitoredUser(User user) throws SQLException {
        if (user.getInstance() == null) {
            throw new Error("Inst�ncia do usu�rio n�o foi especificada!");
        }
        //  busca quem est� sendo monitorado e atualiza para n�o monitorado
        Instance instance = this.dataManager.getInstanceDAO().getInstanceByStateContent(
                true, STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, ENTITY_ID_OF_USER_MANAGEMENT, COMPONENT_ID);
        // se retornar null ent�o ningu�m est� sendo monitorado
        if(instance != null){
            UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, instance)
                    .setContent(new Content(false));
            this.dataManager.getInstanceDAO().insertContent(
                    UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, instance));
        }
        // busca o estado a partir da inst�ncia
        State state = UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, user.getInstance());
        // verifica se o password n�o modificou
        state.setContent(new Content(true));
        this.dataManager.getInstanceDAO().insertContent(state);
        user.setIsBeingMonitored(true);
    }

    /**
     * Atualiza a configura��o de privacidade alterada desde que o usu�rio
     * exista. Se ele n�o existir retorna null;
     *
     * @param privacyState
     * @param user
     * @param newValue
     * @return Retorna o usu�rio alterado. Se ele n�o existir retorna null.
     * @throws java.sql.SQLException
     */
    public User updatePrivacyConfiguration(int privacyState, User user, boolean newValue) throws SQLException {
        User u = get(user.getId());
        if (u != null) {
            switch (privacyState) {
                case User.STATE_PRIVACY_TERM:
                    // busca o estado a partir da inst�ncia e adiciona o novo conte�do
                    UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, user.getInstance())
                            .setContent(new Content(newValue));
                    // persiste o novo conte�do
                    this.dataManager.getInstanceDAO().insertContent(
                            UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_USER_BEING_MONITORED, user.getInstance()));
                    u.setAcceptedPrivacyTerm(newValue);
                    break;
                case User.STATE_PRIVACY_DATA_SHARING:
                    // busca o estado a partir da inst�ncia e adiciona o novo conte�do
                    UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_SYSTEM_DATA_SHARING_PERMISSION_BY_USER, user.getInstance())
                            .setContent(new Content(newValue));
                    // persiste o novo conte�do
                    this.dataManager.getInstanceDAO().insertContent(
                            UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_SYSTEM_DATA_SHARING_PERMISSION_BY_USER, user.getInstance()));
                    u.setAcceptedDataSharing(newValue);
                    break;
                case User.STATE_PRIVACY_ANONYMOUS_UPLOAD:
                    // busca o estado a partir da inst�ncia e adiciona o novo conte�do
                    UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_ANONYMOUS_UPLOAD, user.getInstance())
                            .setContent(new Content(newValue));
                    // persiste o novo conte�do
                    this.dataManager.getInstanceDAO().insertContent(
                            UserDAO.getStateFromUserInstance(STATE_ID_OF_USER_MANAGEMENT_ANONYMOUS_UPLOAD, user.getInstance()));
                    u.setOptedByAnonymousUpload(newValue);
                    break;
            }
            return u;
        }
        return null;
    }

    public Component getComponentDeviceModel() throws SQLException {
        Component deviceComponent = null;
        String sql = "SELECT components.description as component_desc, code_class, entities.id as entity_id, "
                + " entities.description as entity_desc, entity_type_id, entity_types.description as type_desc\n"
                + " FROM components, entities, entity_types\n"
                + " WHERE components.id = ? and component_id = components.id and entity_type_id = entity_types.id;";
        
        Cursor cursor = this.database.rawQuery(sql,	new String[]{String.valueOf(COMPONENT_ID)});

        while (cursor.moveToNext()) {
            if (deviceComponent == null) {
                deviceComponent = new Component();
                deviceComponent.setId(COMPONENT_ID);
                deviceComponent.setDescription(cursor.getString(cursor.getColumnIndex("component_desc")));
                deviceComponent.setReferedClass(cursor.getString(cursor.getColumnIndex("code_class")));
            }
            Entity entity = new Entity();
            entity.setId(cursor.getInt(cursor.getColumnIndex("entity_id")));
            entity.setDescription(cursor.getString(cursor.getColumnIndex("entity_desc")));
            EntityType type = new EntityType(cursor.getInt(cursor.getColumnIndex("entity_type_id")), cursor.getString(cursor.getColumnIndex("type_desc")));
            entity.setEntityType(type);
            entity.setStateModels(this.dataManager.getEntityStateDAO().getEntityStateModels(entity));
            deviceComponent.getEntities().add(entity);
        }
        return deviceComponent;
    }

    public ActionModel getActionStateModel(int componentId, int entityId, int stateId) {
        try {
            // primeiro pega o state ID
            State entityState = this.dataManager.getEntityStateDAO().getEntityState(componentId, entityId, stateId);
            // depois pega a a��o
            return this.dataManager.getActionModelDAO().getActionState(entityState);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Retorna do estado de uma interface a partir do seu modelId. Caso n�o
     * encontre retorna null.
     *
     * @param stateId
     * @param instance
     * @return
     */
    public static State getStateFromUserInstance(int stateId, Instance instance) {
        for (int i = 0; i < instance.getStates().size(); i++) {
            if (stateId == instance.getStates().get(i).getModelId()) {
                return instance.getStates().get(i);
            }
        }
        return null;
    }

    public List<UserPreference> getUserPreferences(User user) throws SQLException {
        return this.getUserPreferences(user.getInstance());
    }

    private List<UserPreference> getUserPreferences(Instance instance) throws SQLException {
        List<UserPreference> list = new ArrayList();
        for (State s : this.dataManager.getEntityStateDAO().getUserEntityStates(instance)) {
            list.add(new UserPreference(s));
        }
        for (Instance i : this.dataManager.getInstanceDAO().getUserInstances(instance)) {
            for (State s : i.getStates()) {
                list.add(new UserPreference(s, i));
            }
        }
        return list;
    }
}
