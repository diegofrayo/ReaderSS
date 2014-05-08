package com.diegorayo.readerss.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.util.UtilAPI;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase que contiene los metodos (CRUD) de la entidad RSSChannel.
 *          Utiliza SQLite
 */
public class RSSChannelSQLite {

	/**
	 * Conexion a la base de datos
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 *            - Conexion a la base de datos
	 */
	public RSSChannelSQLite(SQLiteDatabase db) {

		this.db = db;
	}

	public RSSChannel create(RSSChannel rssChannel)
			throws DataBaseTransactionException, NullEntityException {

		if (rssChannel.getCategory() != null) {

			ContentValues values = new ContentValues();
			values.put("name", rssChannel.getName());
			values.put("url", rssChannel.getUrl());
			values.put("category", rssChannel.getCategory().getId());
			values.put("last_update", rssChannel.getLastUpdate());
			values.put("date_last_rss_link", rssChannel.getDateLastRSSLink());

			long idRow = db.insert("rss_channel", null, values);

			if (idRow != -1) {

				rssChannel.setId((int) idRow);
				return rssChannel;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.INSERT_OPERATION,
					RSSChannel.class.getSimpleName());
		}

		throw new NullEntityException(Category.class.getSimpleName());
	}

	public RSSChannel edit(RSSChannel rssChannel)
			throws DataBaseTransactionException, NullEntityException {

		if (rssChannel.getCategory() != null) {

			ContentValues values = new ContentValues();
			values.put("name", rssChannel.getName());
			values.put("category", rssChannel.getCategory().getId());
			values.put("last_update", rssChannel.getLastUpdate());
			values.put("date_last_rss_link", rssChannel.getDateLastRSSLink());
			String whereArgs[] = new String[] { rssChannel.getId() + "" };

			long idRow = db.update("rss_channel", values, "id = ?", whereArgs);

			if (idRow != -1) {

				return rssChannel;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.UPDATE_OPERATION,
					RSSChannel.class.getSimpleName());
		}

		throw new NullEntityException(Category.class.getSimpleName());
	}

	public boolean delete(int idRSSChannel) throws DataBaseTransactionException {

		String whereArgs[] = new String[] { idRSSChannel + "" };

		long idRow = db.delete("rss_channel", "id = ?", whereArgs);

		if (idRow == 1) {

			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.DELETE_OPERATION,
				RSSChannel.class.getSimpleName());
	}

	public RSSChannel getRSSChannelById(int idRSSChannel) {

		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "date_last_rss_link" };
		String[] whereArgs = new String[] { idRSSChannel + "" };

		Cursor selection = db.query("rss_channel", columns, "id = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {

			CategorySQLite categoryHelper = new CategorySQLite(db);
			Category categoryRSSChannel = categoryHelper
					.getCategoryById(selection.getInt(3));

			RSSChannel rssChannel = new RSSChannel();
			rssChannel.setId(selection.getInt(0));
			rssChannel.setName(selection.getString(1));
			rssChannel.setCategory(categoryRSSChannel);
			rssChannel.setUrl(selection.getString(2));
			rssChannel.setLastUpdate(selection.getString(4));
			rssChannel.setDateLastRSSLink(selection.getString(5));

			return rssChannel;
		}

		return null;
	}

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		List<RSSChannel> rssChannelList = new ArrayList<RSSChannel>();
		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "date_last_rss_link" };
		String[] whereArgs = new String[] { idCategory + "" };

		Cursor selection = db.query("rss_channel", columns, "category = ?",
				whereArgs, null, null, "name asc");

		if (selection.moveToFirst()) {

			CategorySQLite categoryHelper = new CategorySQLite(db);
			Category categoryRSSChannels = categoryHelper
					.getCategoryById(idCategory);
			do {

				RSSChannel rssChannel = new RSSChannel();
				rssChannel.setId(selection.getInt(0));
				rssChannel.setName(selection.getString(1));
				rssChannel.setCategory(categoryRSSChannels);
				rssChannel.setUrl(selection.getString(2));
				rssChannel.setLastUpdate(selection.getString(4));
				rssChannel.setDateLastRSSLink(selection.getString(5));

				rssChannelList.add(rssChannel);

			} while (selection.moveToNext());
		}

		return rssChannelList;
	}

	public List<RSSChannel> getListAllRSSChannels() {

		List<RSSChannel> listRSSChannels = new ArrayList<RSSChannel>();
		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "date_last_rss_link" };

		Cursor selection = db.query("rss_channel", columns, "", null, null,
				null, "name asc");

		if (selection.moveToFirst()) {

			CategorySQLite categoryHelper = new CategorySQLite(db);

			do {

				Category categoryRSSChannel = categoryHelper
						.getCategoryById(selection.getInt(3));

				RSSChannel rssChannel = new RSSChannel();
				rssChannel.setId(selection.getInt(0));
				rssChannel.setName(selection.getString(1));
				rssChannel.setUrl(selection.getString(2));
				rssChannel.setLastUpdate(selection.getString(4));
				rssChannel.setDateLastRSSLink(selection.getString(5));
				rssChannel.setCategory(categoryRSSChannel);

				listRSSChannels.add(rssChannel);

			} while (selection.moveToNext());
		}

		return listRSSChannels;
	}

	public RSSChannel editLastUpdateRSSChannel(RSSChannel rssChannel)
			throws DataBaseTransactionException, NullEntityException {

		if (rssChannel != null) {

			String whereArgs[] = new String[] { rssChannel.getId() + "" };
			ContentValues values = new ContentValues();
			values.put("last_update", UtilAPI.getCurrentDateAndTime());
			values.put("date_last_rss_link", rssChannel.getDateLastRSSLink());

			long idRow = db.update("rss_channel", values, "id = ?", whereArgs);

			if (idRow != -1) {

				return rssChannel;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.UPDATE_OPERATION,
					RSSChannel.class.getSimpleName());
		}

		throw new NullEntityException(RSSChannel.class.getSimpleName());
	}

}
