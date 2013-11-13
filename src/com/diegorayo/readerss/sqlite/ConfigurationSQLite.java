package com.diegorayo.readerss.sqlite;

import java.util.HashMap;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class ConfigurationSQLite {

	/**
	 * 
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 */
	public ConfigurationSQLite(SQLiteDatabase db) {
		this.db = db;
	}

	public boolean getConfigurationRatingApp() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "rating app" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			String result = selection.getString(1);
			if (result.equals("0")) { // No ha puntuado la app
				return false;
			}
			return true; // Ya puntu� la app
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

	public boolean editConfigurationRatingApp() {

		ContentValues values = new ContentValues();
		values.put("value", "1");
		String whereArgs[] = new String[] { "rating app" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);

		if (idRow != -1) { // Operacion de edici�n exitosa
			return true;
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

	public String getUsername() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "username" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			String username = selection.getString(1);
			return username;
		}

		return null;
	}

	public String editUsername(String newUserName) {

		ContentValues values = new ContentValues();
		values.put("value", newUserName);
		String whereArgs[] = new String[] { "username" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);

		if (idRow != -1) { // Operacion de edici�n exitosa
			return newUserName;
		}

		return null;
	}

	public boolean getOpenFirstApp() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "first_open_app" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			String result = selection.getString(1);
			if (result.equals("0")) { // Primera vez que abre la app
				return true;
			}
			return false; // Ya la abri� alguna vez
		}

		return true; // Error en la transaccion, pero no lanza excepcion
	}

	public boolean editOpenFirstApp() {

		ContentValues values = new ContentValues();
		values.put("value", "1");
		String whereArgs[] = new String[] { "first_open_app" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);

		if (idRow != -1) { // Operacion de edici�n exitosa
			return true;
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

	public boolean getViewRSSLinksInApp() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "view_rss_links_in_app" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			String result = selection.getString(1);
			if (result.equals("1")) {
				return true;
			}
			return false;
		}

		return true;
	}

	public boolean editViewRSSLinksInApp(String option) {

		ContentValues values = new ContentValues();
		values.put("value", option);
		String whereArgs[] = new String[] { "view_rss_links_in_app" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);

		if (idRow != -1) {
			return true;
		}

		return false;
	}

	public HashMap<String, String> getConfiguration() {

		String[] columns = new String[] { "key", "value" };
		Cursor selection = db.query("configuration", columns, null, null, null,
				null, null);

		HashMap<String, String> configuration = new HashMap<String, String>();

		if (selection.moveToFirst()) {
			do {
				configuration.put(selection.getString(0),
						selection.getString(1));
			} while (selection.moveToNext());
		}

		return configuration;
	}

	public void insertConfiguration() {
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'rating app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'notify use app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'username',null );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'first_open_app','0' ); ");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'view_rss_links_in_app','0' ); ");
	}

}
