package com.diegorayo.readerss.sqlite;

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

		db.execSQL("CREATE TABLE 'category' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT UNIQUE);");
		db.execSQL("CREATE TABLE 'configuration' ('key' TEXT, 'value' TEXT);");
		db.execSQL("CREATE TABLE 'rss_channel' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT, 'url' TEXT UNIQUE, 'category' INTEGER REFERENCES 'category' ('id') ON DELETE CASCADE ON UPDATE CASCADE, 'last_update' TEXT);");
		db.execSQL("CREATE TABLE 'favorite_link_rss' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' TEXT UNIQUE, 'url' TEXT, 'date' TEXT, 'rss_channel_parent' INTEGER REFERENCES 'rss_channel' ('id'));");

		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'rating app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'notify use app','0' );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'username',null );");
		db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'first_open_app','0' ); ");

		// db.execSQL("INSERT INTO 'category' ( 'id','name' ) VALUES ( '2','futbolfutbolfutbol' ); ");
		//
		// db.execSQL("INSERT INTO 'rss_channel' ( 'id','name','url','category','last_update' ) VALUES ( '1','hola','http://stackoverflow.com/questions/2617969/how-can-i-change-','3',NULL ); ");
		// db.execSQL("INSERT INTO 'rss_channel' ( 'id','name','url','category','last_update' ) VALUES ( '4','vfvdf','iuyiuuy','2',NULL ); ");
		// db.execSQL("INSERT INTO 'rss_channel' ( 'id','name','url','category','last_update' ) VALUES ( '2','miau','miau.com','2',NULL ); ");
		// db.execSQL("INSERT INTO 'rss_channel' ( 'id','name','url','category','last_update' ) VALUES ( '3','gdg','http://stackoverflow.com/questions/4919703/how-to-set-property-androiddrawabletop-of-a-button-at-runtime','3',NULL ); ");
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

		db.execSQL("DROP TABLE IF EXISTS 'category' ;");
		db.execSQL("DROP TABLE IF EXISTS 'configuration' ;");
		db.execSQL("DROP TABLE IF EXISTS 'rss_channel' ;");
		db.execSQL("DROP TABLE IF EXISTS 'favorite_link_rss' ;");

		onCreate(db);

	}

}
