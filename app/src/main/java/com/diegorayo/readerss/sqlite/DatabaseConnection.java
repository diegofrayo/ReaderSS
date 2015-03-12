package com.diegorayo.readerss.sqlite;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.NullEntityException;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase utilizada para la crear la base de datos SQLite al momento de
 *          iniciar la aplicacion. Tambien sirve para editarla cuando hay algun
 *          cambio de version e ella
 */
public class DatabaseConnection extends SQLiteOpenHelper {

	/**
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

		db.execSQL("CREATE TABLE 'category' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL UNIQUE)");
		db.execSQL("CREATE TABLE 'rss_channel' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT NOT NULL UNIQUE, 'url' TEXT NOT NULL UNIQUE, 'category' INTEGER NOT NULL REFERENCES 'category' ('id') ON DELETE CASCADE ON UPDATE CASCADE, 'last_update' DATE, 'date_last_rss_link' DATE)");
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

		List<Category> categoryList = categorySQLite.getListAllCategories();
		List<RSSChannel> rssChannelList = rssChannelSQLite
				.getListAllRSSChannels();

		db.execSQL("DROP TABLE IF EXISTS 'category' ;");
		db.execSQL("DROP TABLE IF EXISTS 'rss_channel' ;");

		onCreate(db);

		try {

			for (Category category : categoryList) {

				categorySQLite.create(category);
			}

			for (RSSChannel rssChannel : rssChannelList) {

				rssChannelSQLite.create(rssChannel);
			}

		} catch (NullEntityException e) {

			e.printStackTrace();
		} catch (DataBaseTransactionException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {

		super.onOpen(db);

		if (!db.isReadOnly()) {

			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

}