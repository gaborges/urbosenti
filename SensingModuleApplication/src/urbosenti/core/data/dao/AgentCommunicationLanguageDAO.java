/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.util.DeveloperSettings;

/**
 *
 * @author Guilherme
 */
public class AgentCommunicationLanguageDAO {

	public static final String TABLE_NAME = "agent_communication_languages";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DESCRIPTION = "description";
	private SQLiteDatabase database;

	public AgentCommunicationLanguageDAO(Object context) {
		this.database = (SQLiteDatabase) context;
	}

	public void insert(AgentCommunicationLanguage type) throws SQLException {
		// String sql = "INSERT INTO agent_communication_languages (id,
		// description) "
		// + "VALUES (?,?);";

		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, type.getId());
		values.put(COLUMN_DESCRIPTION, type.getDescription());

		this.database.insertOrThrow(TABLE_NAME, null, values);

		if (DeveloperSettings.SHOW_DAO_SQL) {
			Log.d("SQL_DEBUG", "INSERT INTO agent_communication_languages (id, description) " + " VALUES ("
					+ type.getId() + ",'" + type.getDescription() + "');");
		}
	}

	/**
	 * Retorna se a linguagem for conhecida ou n�o.
	 *
	 * @param acl
	 *            pode ser tanto o id da linguagem como a descri��o. No in�cio
	 *            do m�todo � tentado se o valor de ACL � um id, caso seja ser�
	 *            buscado pelo id
	 * @return
	 * @throws SQLException
	 */
	public AgentCommunicationLanguage getAgentCommunicationLanguageKnown(String acl) throws SQLException {
		Cursor cursor;
		try {
			// se a acl for um n�mero busca o id
			cursor = this.database.rawQuery("SELECT id,description FROM agent_communication_languages WHERE id = ? ;",
					new String[] { String.valueOf(Integer.parseInt(acl)) });
		} catch (NumberFormatException ex) {
			// se n�o for um n�mero busca pela descri��o
			cursor = this.database.rawQuery(
					"SELECT id,description FROM agent_communication_languages WHERE description = ? ;",
					new String[] { acl });
		}

		if (cursor.moveToNext()) {
			return new AgentCommunicationLanguage(cursor.getInt(cursor.getColumnIndex("id")),
					cursor.getString(cursor.getColumnIndex("description")));
		}
		return null;
	}

	/**
	 * Busca todas as Linguagens de comunica��o entra agentes cadastradas.
	 *
	 * @return retorna um objeto List contendo todas as
	 *         AgentCommunicationLanguage conhecidas
	 * @throws SQLException
	 */
	public List<AgentCommunicationLanguage> getAgentCommunicationLanguages() throws SQLException {
		List<AgentCommunicationLanguage> acls = new ArrayList();
		AgentCommunicationLanguage acl;
		Cursor cursor = this.database.rawQuery("SELECT id,description FROM agent_communication_languages; ", null);

		while (cursor.moveToNext()) {
			acl = new AgentCommunicationLanguage(cursor.getInt(cursor.getColumnIndex("id")),
					cursor.getString(cursor.getColumnIndex("description")));
			acls.add(acl);
		}
		return acls;
	}

	/**
	 * Verifica se a linguagem é conhecida ou não.
	 *
	 * @param acl
	 *            pode ser tanto o id da linguagem como a descri��o. No in�cio
	 *            do m�todo � tentado se o valor de ACL � um id, caso seja ser�
	 *            buscado pelo id
	 * @return
	 * @throws SQLException
	 */
	public boolean isAgentCommunicationLanguageKnown(AgentCommunicationLanguage acl, String textContent) {
		try {
			int id = Integer.parseInt(textContent);
			// se a acl for um n�mero verifica o id
			if (acl.getId() == id) {
				return true;
			}
		} catch (NumberFormatException ex) {
			// se n�o for um n�mero verifica pela descri��o
			if (acl.getDescription().toLowerCase().equals(textContent.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
