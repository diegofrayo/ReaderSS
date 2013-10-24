package com.diegorayo.readerss.sqlite;

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
		// db.close();

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
		values.put("value", "1");
		String whereArgs[] = new String[] { "rating app" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);
		// db.close();

		if (idRow != -1) { // Operacion de edición exitosa
			return true;
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

	public String getUsername() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "username" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);
		// db.close();

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
		// db.close();

		if (idRow != -1) { // Operacion de edición exitosa
			return newUserName;
		}

		return null;
	}

	public boolean getOpenFirstApp() {

		String[] columns = new String[] { "key", "value" };
		String[] whereArgs = new String[] { "first_open_app" };

		Cursor selection = db.query("configuration", columns, "key = ?",
				whereArgs, null, null, null);
		// db.close();

		if (selection.moveToFirst()) {
			String result = selection.getString(1);
			if (result.equals("0")) { // Primera vez que abre la app
				return true;
			}
			return false; // Ya la abrió alguna vez
		}
		System.out.println("Nooo");

		return true; // Error en la transaccion, pero no lanza excepcion
	}

	public boolean editOpenFirstApp() {

		ContentValues values = new ContentValues();
		values.put("value", "1");
		String whereArgs[] = new String[] { "first_open_app" };

		long idRow = db.update("configuration", values, "key = ?", whereArgs);
		// db.close();

		if (idRow != -1) { // Operacion de edición exitosa
			return true;
		}

		return false; // Error en la transaccion, pero no lanza excepcion
	}

}
