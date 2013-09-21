package com.diegorayo.rssreader.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnection extends SQLiteOpenHelper {

	public DatabaseConnection(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE 'category' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT UNIQUE);");
		db.execSQL("CREATE TABLE 'configuration' ('name' TEXT, 'state' BOOLEAN);");
		db.execSQL("CREATE TABLE 'rss_channel' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT, 'url' TEXT UNIQUE, 'category' INTEGER REFERENCES 'category' ('id') ON DELETE CASCADE ON UPDATE CASCADE);");
		db.execSQL("CREATE TABLE 'favorite_link_rss' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'title' TEXT UNIQUE, 'url' TEXT, 'rss_channel_parent' INTEGER);");
		db.execSQL("INSERT INTO 'category' ( 'id','name' ) VALUES ( '1','default' );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','state' ) VALUES ( 'rating app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','state' ) VALUES ( 'notify use app','0' );");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
