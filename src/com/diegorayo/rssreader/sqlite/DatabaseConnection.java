package com.diegorayo.rssreader.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class DatabaseConnection extends SQLiteOpenHelper {

	/**
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
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
		db.execSQL("CREATE TABLE 'configuration' ('name' TEXT, 'value' TEXT);");
		db.execSQL("CREATE TABLE 'rss_channel' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT, 'url' TEXT UNIQUE, 'category' INTEGER REFERENCES 'category' ('id') ON DELETE CASCADE ON UPDATE CASCADE);");
		db.execSQL("CREATE TABLE 'favorite_link_rss' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'title' TEXT UNIQUE, 'url' TEXT, 'date' TEXT, 'rss_channel_parent' INTEGER REFERENCES 'rss_channel' ('id'));");
		db.execSQL("INSERT INTO 'category' ( 'id','name' ) VALUES ( '1','default' );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','value' ) VALUES ( 'rating app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','value' ) VALUES ( 'notify use app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','value' ) VALUES ( 'username',null );");
		db.execSQL("INSERT INTO 'configuration' ( 'name','value' ) VALUES ( 'password', null );");

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

		db.execSQL("DROP TABLE IF EXISTS 'category'");
		db.execSQL("DROP TABLE IF EXISTS 'configuration'");
		db.execSQL("DROP TABLE IF EXISTS 'rss_channel'");
		db.execSQL("DROP TABLE IF EXISTS 'favorite_link_rss'");

		onCreate(db);

	}

}
