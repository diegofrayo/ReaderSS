package com.diegorayo.readerss.sqlite;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;

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
		db.execSQL("CREATE TABLE 'rss_channel' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT UNIQUE, 'url' TEXT UNIQUE, 'category' INTEGER REFERENCES 'category' ('id') ON DELETE CASCADE ON UPDATE CASCADE, 'last_update' DATE, 'modified' BOOLEAN, 'date_last_rss_link' TEXT default '', 'last_content_length_xml_file' INTEGER);");
		db.execSQL("CREATE TABLE 'favorite_link_rss' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' TEXT UNIQUE, 'url' TEXT, 'date' TEXT, 'rss_channel_parent' INTEGER REFERENCES 'rss_channel' ('id'));");

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

		CategorySQLite categorySQLite = new CategorySQLite(db);
		RSSChannelSQLite rssChannelSQLite = new RSSChannelSQLite(db);
		ConfigurationSQLite configurationSQLite = new ConfigurationSQLite(db);

		List<Category> categoryList = categorySQLite.getListAllCategories();
		List<RSSChannel> rssChannelList = rssChannelSQLite
				.getListAllRSSChannels();
		HashMap<String, String> configuration = configurationSQLite
				.getConfiguration();

		db.execSQL("DROP TABLE IF EXISTS 'category' ;");
		db.execSQL("DROP TABLE IF EXISTS 'configuration' ;");
		db.execSQL("DROP TABLE IF EXISTS 'rss_channel' ;");
		db.execSQL("DROP TABLE IF EXISTS 'favorite_link_rss' ;");

		onCreate(db);

		try {

			for (Category category : categoryList) {
				categorySQLite.createCategory(category);
			}
			for (RSSChannel rssChannel : rssChannelList) {
				rssChannelSQLite.createRSSChannel(rssChannel);
			}

			db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'rating app','"
					+ configuration.get("rating app") + "' );");

			db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'notify use app','"
					+ configuration.get("notify use app") + "' );");

			db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'username','"
					+ configuration.get("username") + "' );");

			db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'first_open_app','"
					+ configuration.get("first_open_app") + "' );");

			db.execSQL("INSERT INTO 'configuration' ( 'key','value' ) VALUES ( 'view_rss_links_in_app','"
					+ configuration.get("view_rss_links_in_app") + "' );");

		} catch (EntityNullException e) {
			e.printStackTrace();
		} catch (DataBaseTransactionException e) {
			e.printStackTrace();
		}
	}

}
