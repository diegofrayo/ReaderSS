package com.diegorayo.rssreader.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfigurationSQLite {

	private SQLiteDatabase db;

	public ConfigurationSQLite(SQLiteDatabase db) {
		this.db = db;
	}

	public boolean getConfigurationRatingApp() {
		String[] columns = new String[] { "name", "state" };
		String[] whereArgs = new String[] { "rating app" };
		Cursor selection = db.query("rss_channel", columns, "name = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			int result = selection.getInt(1);
			if (result == 0) {
				return false;
			}
			return true;
		}

		return false;
	}

	public boolean editConfigurationRatingApp() {
		ContentValues values = new ContentValues();
		values.put("state", 1);
		String whereArgs[] = new String[] { "rating app" };
		long idRow = db.update("rss_channel", values, "name = ?", whereArgs);
		if (idRow != -1) {
			return true;
		}
		return false;
	}

}
