/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.user;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import urbosenti.core.data.dao.UserDAO;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.TargetOrigin;
import urbosenti.core.events.Action;
import urbosenti.core.events.ApplicationEvent;
import urbosenti.core.events.Event;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class UserManager extends ComponentManager {

    /**
     * int EVENT_USER_ADDED = 1;
     *
     * <ul><li>id: 1</li>
     * <li>evento: Usu�rio inclu�do</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_ADDED = 1;
    /**
     * int EVENT_USER_UPDATED = 2;
     *
     * <ul><li>id: 2</li>
     * <li>evento: Usu�rio alterado</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_UPDATED = 2;
    /**
     * int EVENT_USER_DELETED = 3;
     *
     * <ul><li>id: 3</li>
     * <li>evento: Usu�rio exclu�do</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_DELETED = 3;
    /**
     * int EVENT_USER_READ = 4;
     *
     * <ul><li>id: 4</li>
     * <li>evento: Usu�rio acessado para leitura</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_READ = 4;
    /**
     * int EVENT_USER_CHOSEN_TO_BE_MONITORED = 5;
     *
     * <ul><li>id: 5</li>
     * <li>evento: Perfil escolhido de usu�rio a ser monitorado</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_CHOSEN_TO_BE_MONITORED = 5;
    /**
     * int EVENT_USER_IS_LOGGED_IN = 6;
     *
     * <ul><li>id: 6</li>
     * <li>evento: Usu�rio Executou Login</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_IS_LOGGED_IN = 6;
    /**
     * int EVENT_USER_IS_LOGGED_OUT = 7;
     *
     * <ul><li>id: 7</li>
     * <li>evento: Usu�rio Executou Logoff</li>
     * <li>par�metros: Usu�rio</li></ul>
     */
    public static final int EVENT_USER_IS_LOGGED_OUT = 7;
    /**
     * int EVENT_USER_CHANGED_SYSTEM_CONFIGURATION = 8;
     *
     * <ul><li>id: 8</li>
     * <li>evento: Configura��o de sistema alterada pelo usu�rio</li>
     * <li>par�metros: Componente,Entidade,Estado,Novo Valor</li></ul>
     */
    public static final int EVENT_USER_CHANGED_SYSTEM_CONFIGURATION = 8;
    /**
     * int EVENT_USER_CHANGED_PRIVACY_CONFIGURATION = 9;
     *
     * <ul><li>id: 9</li>
     * <li>evento: Configura��o de privacidade alterada</li>
     * <li>par�metros: Estado,Novo Valor</li></ul>
     */
    public static final int EVENT_USER_CHANGED_PRIVACY_CONFIGURATION = 9;
    private static User loggedUser;

    public UserManager(DeviceManager deviceManager) {
        super(deviceManager, UserDAO.COMPONENT_ID);
    }

    @Override
    public void onCreate() {
        // Carregar dados e configura��es que ser�o utilizados para execu��o em memória
        // Preparar configura��es inicias para execu��o
        // Para tanto utilizar o DataManager para acesso aos dados.
        if(DeveloperSettings.SHOW_FUNCTION_DEBUG_ACTIVITY){
            System.out.println("Activating: " + getClass());
        }
    }

    /**
     * A��es disponibilizadas por esse componente:
     * <ul>
     * <li>Nenhuma a��o ainda � suportada</li>
     * </ul>
     *
     * @param action cont�m objeto a��o.
     * @return
     *
     */
    @Override
    public FeedbackAnswer applyAction(Action action) {
        // n�o tem a��es ainda
        return new FeedbackAnswer(FeedbackAnswer.ACTION_DOES_NOT_EXIST);
    }

    /**
     *
     * <br><br>Eventos poss�veis
     * <ul>
     * <li>UserManager.EVENT_USER_ADDED - Usu�rio inclu�do</li>
     * <li>UserManager.EVENT_USER_UPDATED - Usu�rio alterado</li>
     * <li>UserManager.EVENT_USER_DELETED - Usu�rio exclu�do</li>
     * <li>UserManager.EVENT_USER_READ - Usu�rio acessado para leitura</li>
     * <li>UserManager.EVENT_USER_CHOSEN_TO_BE_MONITORED - Perfil escolhido de
     * usu�rio a ser monitorado</li>
     * <li>UserManager.EVENT_USER_IS_LOGGED_IN - Usu�rio Executou Login</li>
     * <li>UserManager.EVENT_USER_IS_LOGGED_OUT - Usu�rio Executou Logoff</li>
     * <li>UserManager.EVENT_USER_CHANGED_SYSTEM_CONFIGURATION - Configura��o de
     * sistema alterada pelo usu�rio</li>
     * <li>UserManager.EVENT_USER_CHANGED_PRIVACY_CONFIGURATION - Configura��o
     * de privacidade alterada</li>
     * </ul>
     *
     * @param eventId indica o evento entre os acima
     * @param params
     * @see #EVENT_USER_ADDED
     * @see #EVENT_USER_UPDATED
     * @see #EVENT_USER_DELETED
     * @see #EVENT_USER_READ
     * @see #EVENT_USER_CHOSEN_TO_BE_MONITORED
     * @see #EVENT_USER_IS_LOGGED_IN
     * @see #EVENT_USER_IS_LOGGED_OUT
     * @see #EVENT_USER_CHANGED_SYSTEM_CONFIGURATION
     * @see #EVENT_USER_CHANGED_SYSTEM_CONFIGURATION
     */
    public void newInternalEvent(int eventId, Object... params) {
        User user;
        Event event;
        HashMap<String, Object> values;
        switch (eventId) {
            case EVENT_USER_ADDED: // 1 - Usu�rio inclu�do
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(1);
                event.setName("Usu�rio inclu�do");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_MANAGEMENT);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_USER_UPDATED: // 2 - usu�rio alterado
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(2);
                event.setName("Usu�rio alterado");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_MANAGEMENT);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_DELETED: // 3 - usu�rio exclu�do
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(3);
                event.setName("Usu�rio exclu�do");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_MANAGEMENT);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_READ: // 4 - usu�rio acessado para leituras
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(4);
                event.setName("Usu�rio acessado para leitura");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_MANAGEMENT);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_CHOSEN_TO_BE_MONITORED: // 5 - usu�rio escolhido para ser monitorado
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(5);
                event.setName("Perfil escolhido de usu�rio a ser monitorado");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_MANAGEMENT);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_IS_LOGGED_IN: // 6 - usu�rio efetuou login
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(6);
                event.setName("Usu�rio Executou Login");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_AUTHENTICATION);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_IS_LOGGED_OUT: // usu�rio efetuou logout
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(7);
                event.setName("Usu�rio Executou Logoff");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_AUTHENTICATION);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_CHANGED_SYSTEM_CONFIGURATION: // 8 - Usu�rio alterou uma configura��o de sistema
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);
                values.put("component", (ComponentManager) params[1]);
                values.put("entity", (Integer) params[2]);
                values.put("state", (Integer) params[3]);
                values.put("newValue", params[4]);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(8);
                event.setName("Configura��o de sistema alterada pelo usu�rio");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_PROFILE_PREFERENCES);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_USER_CHANGED_PRIVACY_CONFIGURATION: // 9 - usu�rio alterou uma configura��o de privacidade
                user = (User) params[0];

                // Par�metros do evento
                values = new HashMap();
                values.put("user", user);
                values.put("state", (Integer) params[1]);
                values.put("newValue", params[2]);

                // Cria o evento
                event = new ApplicationEvent(this);
                event.setId(9);
                event.setName("Configura��o de privacidade alterada");
                event.setTime(new Date());
                event.setParameters(values);
                event.setEntityId(UserDAO.ENTITY_ID_OF_USER_PROFILE_PREFERENCES);

                // envia o evento
                getEventManager().newEvent(event);
                break;
        }
    }

    public User login(String userName, String password) throws SQLException {
        for (User user : getDeviceManager().getDataManager().getUserDAO().getUsers()) {
            // Checa se o usu�rio existe
            if (user.getLogin().equals(userName) && user.getPassword().equals(password)) {
                // Atribui o usu�rio em uma vari�vel est�tica
                loggedUser = user;
                // Gera o evento do login
                this.newInternalEvent(UserManager.EVENT_USER_IS_LOGGED_IN, user);
                // retorna o usu�rio
                return user;
            }
        }
        return null;
    }

    public boolean logout() {
        // Gera o evento
        this.newInternalEvent(UserManager.EVENT_USER_IS_LOGGED_OUT, loggedUser);
        // torna o usu�rio nulo
        loggedUser = null;
        return true;
    }

    /**
     * Insere um usu�rio. Contudo, antes deve verificar se o nome do usu�rio e a
     * senha s�o v�lidas, e se ele aceitou o termo de privacidade.
     *
     * @param userName
     * @param password
     * @param hasAccepptedThePrivacyTerm
     * @param hasAccepptedShareData
     * @param hasChoseAnonymousUpload
     * @return
     */
    public boolean insertUser(String userName, String password, Boolean hasAccepptedThePrivacyTerm, Boolean hasAccepptedShareData, Boolean hasChoseAnonymousUpload) throws SQLException {
        // Verifica se o nome do usu�rio e a senha s�o v�lidos
        if (!this.checkUsernameValidity(userName) || !this.checkPasswordValidity(password)) {
            return false;
        }
        // Verifica se o usu�rio aceitou o termo de privacidade
        if (!hasAccepptedThePrivacyTerm) {
            return false;
        }
        // Cria um usu�rio e adiciona as informa��es
        User user = new User();
        user.setLogin(userName);
        user.setPassword(password);
        user.setAcceptedDataSharing(hasAccepptedShareData);
        user.setAcceptedPrivacyTerm(hasAccepptedThePrivacyTerm);
        user.setOptedByAnonymousUpload(hasChoseAnonymousUpload);

        // insere o usu�rio no banco
        getDeviceManager().getDataManager().getUserDAO().insert(user);

        // Gera o evento
        this.newInternalEvent(UserManager.EVENT_USER_ADDED, user);

        // retorna o valor
        return true;
    }

    /**
     * Atualiza o usu�rio. Contudo o usu�rio deve estar logado.
     *
     * @param user
     * @return
     */
    public boolean updateUser(User user) throws SQLException {
        // checa se usu�rio est� logado
        if (loggedUser == null) {
            return false;
        }
        // checa se ambos s�o os mesmos e executa a a��o
        if (user.getId() == loggedUser.getId()) {
            // Atualiza
            getDeviceManager().getDataManager().getUserDAO().updatePassword(user);
            loggedUser = getDeviceManager().getDataManager().getUserDAO().get(loggedUser.getId());
            // Gera o evento
            this.newInternalEvent(UserManager.EVENT_USER_UPDATED, user);
        }
        return true;
    }

    /**
     * Remove o usu�rio do sistema. Contdo isso somente ser� poss�vel se forem
     * passados o login do usu�rio a senha e se esse usu�rio n�o estiver senso
     * monitorado em execu��o
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean deleteUser(String userName, String password) throws SQLException {
        // verifica se o usu�rio existe
        User user = getDeviceManager().getDataManager().getUserDAO().get(password, password);
        if (user == null) {
            return false;
        } else {
            // Se o usu�rio est� sendo monitodado ele n�o pode ser deletado
            if (this.userIsBeingMonitored(user)) {
                return false;
            }
            // Se o usu�rio existe remove ele 
            getDeviceManager().getDataManager().getUserDAO().deleteAllUserInformation(user);
            // Gera o evento -- evento exclui as outras informa��es associadas
            this.newInternalEvent(UserManager.EVENT_USER_DELETED, user);
            // Checa se ele est� logado e realiza o logout
            if (loggedUser.getId() == user.getId()) {
                return this.logout();
            }
            return true;
        }
    }

    /**
     * O nome do usu�rio somente � v�lido se conter 4 caract�res ou mais e se
     * outro usu�rio n�o estiver utiliando o mesmo nome
     *
     * @param userName
     * @return true se passar pelas valida��es
     * @throws java.sql.SQLException
     */
    public boolean checkUsernameValidity(String userName) throws SQLException {
        // verifica se n�o est� vaziu o nome ou se tem menos que 4 caracteres
        if (userName.length() < 4) {
            return false;
        }
        // vetifica se � um nome v�lido entre os existentes
        for (User user : getDeviceManager().getDataManager().getUserDAO().getUsers()) {
            if (user.getLogin().equals(userName)) {
                return false;
            }
        }
        // retorna veradeiro se passou por todas as valida��es
        return true;
    }

    /**
     * Um password somente � v�lido se tiver mais que 4 caracteres
     *
     * @param password
     * @return
     */
    public boolean checkPasswordValidity(String password) {
        // verifica se n�o est� vaziu o nome ou se tem menos que 4 caracteres
        return password.length() >= 4; // retorna veradeiro se passou por todas as valida��es
    }

    /**
     * Verifica se o usu�rio passado por par�metro est� sendo monitorado.
     *
     * @param user
     * @return
     */
    private boolean userIsBeingMonitored(User user) throws SQLException {
        // Verifica se este usu�rio � o que est� senso monitorado
        // busca todas as informa��es sobre o usu�rio
        User returnedUser = getDeviceManager().getDataManager().getUserDAO().getMonitoredUser();
        // se retornar nulo nenhum usu�rio est� sendo monitorado
        if (returnedUser == null) {
            return false;
        }
        // verifica se o usu�rio existe
        return returnedUser.getId() == user.getId();
    }

    /**
     * Torna o usu�rio logado o usu�rio monitorado pelo sistema, caso ele esteja
     * em login.
     *
     * @return retorna true se o usu�rio existir e retorna false caso n�o
     * exista.
     */
    public boolean setMonitoredUser() throws SQLException {
        // busca todas as informa��es sobre o usu�rio
        User user = loggedUser;
        // verifica se o usu�rio existe
        if (user != null) {
            // atualiza esse usu�rio como sendo monitorado e remove do anterior
            getDeviceManager().getDataManager().getUserDAO().setMonitoredUser(user);
            // gera o evento
            this.newInternalEvent(UserManager.EVENT_USER_CHOSEN_TO_BE_MONITORED, user);
            // retorna o resultado do m�todo
            return true;
        }
        return false;
    }

    /**
     * Torna o usu�rio que cont�m o login e senha passados por par�metro o
     * usu�rio monitorado pelo sistema, caso ele exista.
     *
     * @param userName
     * @param password
     * @return retorna true se o usu�rio existir e retorna false caso n�o
     * exista.
     * @throws java.sql.SQLException
     */
    public boolean setMonitoredUser(String userName, String password) throws SQLException {
        // busca todas as informa��es sobre o usu�rio
        User user = getDeviceManager().getDataManager().getUserDAO().get(userName, password);
        // verifica se o usu�rio existe
        if (user != null) {
            // atualiza esse usu�rio como sendo monitorado
            getDeviceManager().getDataManager().getUserDAO().setMonitoredUser(user);
            // gera o evento
            this.newInternalEvent(UserManager.EVENT_USER_CHOSEN_TO_BE_MONITORED, user);
            // retorna o resultado do m�todo
            return true;
        } else {
            return false;
        }
    }

    /**
     * Torna o usu�rio passado por par�metro o usu�rio monitorado pelo sistema,
     * caso ele exista.
     *
     * @param user
     * @return retorna true se o usu�rio existir e retorna false caso n�o
     * exista.
     * @throws java.sql.SQLException
     */
    public boolean setMonitoredUser(User user) throws SQLException {
        // busca todas as informa��es sobre o usu�rio
        User returnedUser = getDeviceManager().getDataManager().getUserDAO().get(user.getId());
        // verifica se o usu�rio existe
        if (returnedUser != null) {
            // atualiza esse usu�rio como sendo monitorado
            getDeviceManager().getDataManager().getUserDAO().setMonitoredUser(returnedUser);
            // gera o evento
            this.newInternalEvent(UserManager.EVENT_USER_CHOSEN_TO_BE_MONITORED, returnedUser);
            // retorna o resultado do m�todo
            return true;
        } else {
            return false;
        }
    }

    /**
     * Atualiza a configura��o do usu�rio sobre seu termo de privacidade.
     *
     * @param user
     * @param newValue
     * @return
     * @throws java.sql.SQLException
     */
    public boolean updateUserPrivacyTerm(User user, boolean newValue) throws SQLException {
        user = getDeviceManager().getDataManager().getUserDAO().updatePrivacyConfiguration(User.STATE_PRIVACY_TERM, user, newValue);
        if (user != null) {
            this.newInternalEvent(UserManager.EVENT_USER_CHANGED_PRIVACY_CONFIGURATION, User.STATE_PRIVACY_TERM, newValue);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Atualiza a configura��o do usu�rio relativa a comportilhar dados de
     * sistema.
     *
     * @param user
     * @param newValue
     * @return
     * @throws java.sql.SQLException
     */
    public boolean updateUserDataSharing(User user, boolean newValue) throws SQLException {
        user = getDeviceManager().getDataManager().getUserDAO().updatePrivacyConfiguration(User.STATE_PRIVACY_DATA_SHARING, user, newValue);
        if (user != null) {
            this.newInternalEvent(UserManager.EVENT_USER_CHANGED_PRIVACY_CONFIGURATION, User.STATE_PRIVACY_DATA_SHARING, newValue);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Atualiza a configura��o do usu�rio relativa ao anonimado da informa��o
     * compartilhada.
     *
     * @param user
     * @param newValue
     * @return
     * @throws java.sql.SQLException
     */
    public boolean updateUserAnonymousUpload(User user, boolean newValue) throws SQLException {
        user = getDeviceManager().getDataManager().getUserDAO().updatePrivacyConfiguration(User.STATE_PRIVACY_ANONYMOUS_UPLOAD, user, newValue);
        if (user != null) {
            this.newInternalEvent(UserManager.EVENT_USER_CHANGED_PRIVACY_CONFIGURATION, User.STATE_PRIVACY_ANONYMOUS_UPLOAD, newValue);
            return true;
        } else {
            return false;
        }
    }

    /**
     * M�todo utilizado para trocar configura��es do sistema. N�o implementado
     * ainda pois necessita da parte da representa��o do conhecimento. Al�m de
     * definir se ser� feito de maneira din�mica ou n�o. Possivelmente haver�
     * valida��es em tempo real baseado nas caracter�sticas do conhecimento.
     *
     * @param user
     * @param componentManager
     * @param entityId
     * @param stateId
     * @param newValue
     * @return
     */
    public boolean updateSystemPreference(User user, ComponentManager componentManager, int entityId, int stateId, Object newValue) {
        return this.updateSystemPreference(user, componentManager.getComponentId(), entityId, stateId, newValue);
    }

    /**
     * M�todo utilizado para trocar configura��es do sistema. N�o implementado
     * ainda pois necessita da parte da representa��o do conhecimento. Al�m de
     * definir se ser� feito de maneira din�mica ou n�o. Possivelmente haver�
     * valida��es em tempo real baseado nas caracter�sticas do conhecimento.
     *
     * @param user
     * @param componentId
     * @param entityId
     * @param stateId
     * @param newValue
     * @return
     */
    public boolean updateSystemPreference(User user, int componentId, int entityId, int stateId, Object newValue) {
        // verifica o componente
        // busca a a��o que pode ser executada para alterar este estado
        ActionModel actionModel = super.getDeviceManager().getDataManager().getUserDAO().getActionStateModel(componentId, entityId, stateId);
        if (actionModel != null) {
            // Par�metros
            HashMap<String, Object> hashMap = new HashMap();
            for (Parameter p : actionModel.getParameters()) {
                hashMap.put(p.getLabel(), newValue);
            }
            // Cria a a��o
            Action action = new Action();
            action.setId(actionModel.getModelId());
            action.setParameters(hashMap);
            action.setName(actionModel.getDescription());
            action.setOrigin(TargetOrigin.APPLICATION_LAYER);
            action.setUser(user);
            action.setTargetEntityId(entityId);
            // A a��o ao componente que deve receber a a��o
            for (ComponentManager componentManager : super.getDeviceManager().getComponentManagers()) {
                if (componentManager.getComponentId() == componentId) {
                    componentManager.applyAction(action);
                    // Gerar evento da mudan�a de configura��o do sistema
                    this.newInternalEvent(EVENT_USER_CHANGED_SYSTEM_CONFIGURATION, componentId, entityId, stateId, newValue);
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    public User getMonitoredUser() throws SQLException{
        return this.getDeviceManager().getDataManager().getUserDAO().getMonitoredUser();
    }
    
}
