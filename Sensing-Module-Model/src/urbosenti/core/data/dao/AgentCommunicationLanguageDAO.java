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
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class AgentCommunicationLanguageDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    public AgentCommunicationLanguageDAO(Object context) {
        this.connection = (Connection) context;
    }

    public void insert(AgentCommunicationLanguage type) throws SQLException {
        String sql = "INSERT INTO agent_communication_languages (id, description) "
                + "VALUES (?,?);";
        this.stmt = this.connection.prepareStatement(sql);
        this.stmt.setInt(1, type.getId());
        this.stmt.setString(2, type.getDescription());
        this.stmt.execute();
        this.stmt.close();
        if (DeveloperSettings.SHOW_DAO_SQL) {
            System.out.println("INSERT INTO agent_communication_languages (id, description) "
                    + " VALUES (" + type.getId() + ",'" + type.getDescription() + "');");
        }
    }

    /**
     * Retorna se a linguagem é conhecida ou não.
     *
     * @param acl pode ser tanto o id da linguagem como a descrição. No início
     * do método é tentado se o valor de ACL é um id, caso seja será buscado
     * pelo id
     * @return
     * @throws SQLException
     */
    public AgentCommunicationLanguage getAgentCommunicationLanguageKnown(String acl) throws SQLException {
        try {
            // se a acl for um número busca o id
            String sql = "SELECT id,description FROM agent_communication_languages "
                    + " WHERE id = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, Integer.parseInt(acl));

        } catch (NumberFormatException ex) {
            // se não for um número busca pela descrição
            String sql = "SELECT id,description FROM agent_communication_languages "
                    + " WHERE description = ? ;";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, acl);
        }

        ResultSet rs = this.stmt.executeQuery();
        this.stmt.close();
        if (rs.next()) {
            return new AgentCommunicationLanguage(rs.getInt("id"), rs.getString("description"));
        }
        return null;
    }

    /**
     * Busca todas as Linguagens de comunicação entra agentes cadastradas.
     *
     * @return retorna um objeto List contendo todas as
     * AgentCommunicationLanguage conhecidas
     * @throws SQLException
     */
    public List<AgentCommunicationLanguage> getAgentCommunicationLanguages() throws SQLException {
        List<AgentCommunicationLanguage> acls = new ArrayList();
        AgentCommunicationLanguage acl;
        String sql = "SELECT id,description FROM agent_communication_languages; ";
        this.stmt = this.connection.prepareStatement(sql);
        ResultSet rs = this.stmt.executeQuery();
        while (rs.next()) {
            acl = new AgentCommunicationLanguage(rs.getInt("id"), rs.getString("description"));
            acls.add(acl);
        }
        rs.close();
        stmt.close();
        return acls;
    }

    /**
     * Verifica se a linguagem é conhecida ou não.
     *
     * @param acl pode ser tanto o id da linguagem como a descrição. No início
     * do método é tentado se o valor de ACL é um id, caso seja será buscado
     * pelo id
     * @return
     * @throws SQLException
     */
    public boolean isAgentCommunicationLanguageKnown(AgentCommunicationLanguage acl, String textContent) {
        try {
            int id = Integer.parseInt(textContent);
            // se a acl for um número verifica o id
            if(acl.getId() == id){
                return true;
            }
        } catch (NumberFormatException ex) {
            // se não for um número verifica pela descrição
            if(acl.getDescription().toLowerCase().equals(textContent.toLowerCase())){
                return true;
            }
        }
        return false;
    }

}
