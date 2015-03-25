/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.user;

import java.util.List;
import urbosenti.core.device.model.Instance;

/**
 *
 * @author Guilherme
 */
public class User {

    /**
     * Indica o estado termo de privacidade. Valor 1;
     */
    public static final int STATE_PRIVACY_TERM = 1;
    /**
     * Indica o estado sobre a permissão do usuário para compartilhar dados. Valor 2
     */
    public static final int STATE_PRIVACY_DATA_SHARING = 2;
    /**
     * Indica o estado sobre a opção do usuário pelos dados enviados serem
     * tratados como anônimos. Valor 3
     */
    public static final int STATE_PRIVACY_ANONYMOUS_UPLOAD = 3;

    private int id;
    private String login;
    private String password;
    private Boolean acceptedPrivacyTerm;
    private Boolean acceptedDataSharing;
    private Boolean optedByAnonymousUpload;
    private Boolean isBeingMonitored;
    private int userPosition;
    private Instance instance;
    private List<UserPreference> userPreferences;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAcceptedPrivacyTerm() {
        return acceptedPrivacyTerm;
    }

    public void setAcceptedPrivacyTerm(Boolean acceptedPrivacyTerm) {
        this.acceptedPrivacyTerm = acceptedPrivacyTerm;
    }

    public Boolean getAcceptedDataSharing() {
        return acceptedDataSharing;
    }

    public void setAcceptedDataSharing(Boolean acceptedDataSharing) {
        this.acceptedDataSharing = acceptedDataSharing;
    }

    public Boolean getOptedByAnonymousUpload() {
        return optedByAnonymousUpload;
    }

    public void setOptedByAnonymousUpload(Boolean hasChoseAnonymousUpload) {
        this.optedByAnonymousUpload = hasChoseAnonymousUpload;
    }

    public List<UserPreference> getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(List<UserPreference> userPreferences) {
        this.userPreferences = userPreferences;
    }

    public Boolean isBeingMonitored() {
        return isBeingMonitored;
    }

    public void setIsBeingMonitored(Boolean isBeingMonitored) {
        this.isBeingMonitored = isBeingMonitored;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public int getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(int userPosition) {
        this.userPosition = userPosition;
    }
    
}
