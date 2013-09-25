package com.diegorayo.rssreader.sqlite;

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

		String[] columns = new String[] { "name", "value" };
		String[] whereArgs = new String[] { "rating app" };

		Cursor selection = db.query("rss_channel", columns, "name = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			String result = selection.getString(1);
			if (result.equals("0")) { // No ha puntuado la app
				return false;
			}
			return true; // Ya puntuó la app
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

	public boolean editConfigurationRatingApp() {
		ContentValues values = new ContentValues();
		values.put("value", 1);
		String whereArgs[] = new String[] { "rating app" };

		long idRow = db.update("rss_channel", values, "name = ?", whereArgs);

		if (idRow != -1) { // Operacion de edición exitosa
			return true;
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

}
