package com.diegorayo.readerss.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
@SuppressLint("SimpleDateFormat")
public class RSSChannelSQLite {

	/**
	 * 
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 */
	public RSSChannelSQLite(SQLiteDatabase db) {
		super();
		this.db = db;
	}

	public RSSChannel createRSSChannel(RSSChannel rssChannel)
			throws DataBaseTransactionException, EntityNullException {

		if (rssChannel != null) {
			if (rssChannel.getCategory() != null) {
				ContentValues values = new ContentValues();
				// values.put("id", 0);
				values.put("name", rssChannel.getName());
				values.put("url", rssChannel.getUrl());
				values.put("category", rssChannel.getCategory().getId());

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String currentDateAndTime = sdf.format(new Date());
				values.put("last_update", currentDateAndTime);

				long idRow = db.insert("rss_channel", null, values);
				// db.close();

				if (idRow != -1) {
					rssChannel.setId((int) idRow);
					return rssChannel;
				}

				throw new DataBaseTransactionException(
						DataBaseTransactionException.OPERATION_INSERT,
						RSSChannel.class.getSimpleName());
			}

			throw new EntityNullException(Category.class.getSimpleName());
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

	public RSSChannel editRSSChannel(RSSChannel rssChannel)
			throws DataBaseTransactionException, EntityNullException {

		if (rssChannel != null) {
			if (rssChannel.getCategory() != null) {
				ContentValues values = new ContentValues();
				values.put("name", rssChannel.getName());
				values.put("url", rssChannel.getUrl());
				values.put("category", rssChannel.getCategory().getId());
				values.put("last_update", rssChannel.getLastUpdate());
				String whereArgs[] = new String[] { rssChannel.getId() + "" };
				// db.close();

				long idRow = db.update("rss_channel", values, "id = ?",
						whereArgs);
				if (idRow != -1) {
					return rssChannel;
				}

				throw new DataBaseTransactionException(
						DataBaseTransactionException.OPERATION_UPDATE,
						RSSChannel.class.getSimpleName());
			}

			throw new EntityNullException(Category.class.getSimpleName());
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException {

		String whereArgs[] = new String[] { idRSSChannel + "" };

		long idRow = db.delete("rss_channel", "id = ?", whereArgs);

		if (idRow == 1) {
			FavoriteRSSLinkSQLite favoriteRSSLinkHelper = new FavoriteRSSLinkSQLite(
					db);
			favoriteRSSLinkHelper.editFavoriteRSSLink(idRSSChannel);
			// db.close();
			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.OPERATION_DELETE,
				RSSChannel.class.getSimpleName());
	}

	public RSSChannel getRSSChannelById(int idRSSChannel) {

		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update" };
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
			rssChannel.setUrl(selection.getString(2));
			rssChannel.setCategory(categoryRSSChannel);
			rssChannel.setLastUpdate(selection.getString(4));

			// db.close();
			return rssChannel;
		}

		// db.close();
		return null;
	}

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		List<RSSChannel> listRSSChannels = new ArrayList<RSSChannel>();
		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update" };
		String[] whereArgs = new String[] { idCategory + "" };

		Cursor selection = db.query("rss_channel", columns, "category = ?",
				whereArgs, null, null, null);

		if (selection.moveToFirst()) {
			CategorySQLite categoryHelper = new CategorySQLite(db);
			Category categoryRSSChannels = categoryHelper
					.getCategoryById(idCategory);
			do {
				RSSChannel rssChannel = new RSSChannel();
				rssChannel.setId(selection.getInt(0));
				rssChannel.setName(selection.getString(1));
				rssChannel.setUrl(selection.getString(2));
				rssChannel.setCategory(categoryRSSChannels);
				rssChannel.setLastUpdate(selection.getString(4));
				listRSSChannels.add(rssChannel);
			} while (selection.moveToNext());
		}

		// db.close();

		return listRSSChannels;
	}

}
