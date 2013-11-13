package com.diegorayo.readerss.sqlite;

import java.util.ArrayList;
import java.util.List;

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

				values.put("name", rssChannel.getName());
				values.put("url", rssChannel.getUrl());
				values.put("category", rssChannel.getCategory().getId());
				values.put("last_update", rssChannel.getLastUpdate());
				values.put("modified", rssChannel.isModified());
				values.put("last_content_length_xml_file",
						rssChannel.getLastContentLengthXMLFile());

				long idRow = db.insert("rss_channel", null, values);

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
				values.put("category", rssChannel.getCategory().getId());
				values.put("last_update", rssChannel.getLastUpdate());
				values.put("modified", rssChannel.isModified());
				values.put("date_last_rss_link",
						rssChannel.getDateLastRSSLink());
				values.put("last_content_length_xml_file",
						rssChannel.getLastContentLengthXMLFile());

				String whereArgs[] = new String[] { rssChannel.getId() + "" };

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

			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.OPERATION_DELETE,
				RSSChannel.class.getSimpleName());
	}

	public RSSChannel getRSSChannelById(int idRSSChannel) {

		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "modified", "date_last_rss_link",
				"last_content_length_xml_file" };
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
			rssChannel.setModified(selection.getInt(5) > 0);
			rssChannel.setDateLastRSSLink(selection.getString(6));
			rssChannel.setLastContentLengthXMLFile(selection.getInt(7));

			return rssChannel;
		}

		return null;
	}

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		List<RSSChannel> listRSSChannels = new ArrayList<RSSChannel>();
		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "modified", "date_last_rss_link",
				"last_content_length_xml_file" };
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
				rssChannel.setUrl(selection.getString(2));
				rssChannel.setCategory(categoryRSSChannels);
				rssChannel.setLastUpdate(selection.getString(4));
				rssChannel.setModified(selection.getInt(5) > 0);
				rssChannel.setDateLastRSSLink(selection.getString(6));
				rssChannel.setLastContentLengthXMLFile(selection.getInt(7));
				listRSSChannels.add(rssChannel);
			} while (selection.moveToNext());
		}

		return listRSSChannels;
	}

	public List<RSSChannel> getListAllRSSChannels() {

		List<RSSChannel> listRSSChannels = new ArrayList<RSSChannel>();
		String[] columns = new String[] { "id", "name", "url", "category",
				"last_update", "modified", "date_last_rss_link",
				"last_content_length_xml_file" };

		Cursor selection = db.query("rss_channel", columns, "", null, null,
				null, "name asc");

		if (selection.moveToFirst()) {
			CategorySQLite categoryHelper = new CategorySQLite(db);
			do {
				RSSChannel rssChannel = new RSSChannel();
				rssChannel.setId(selection.getInt(0));
				rssChannel.setName(selection.getString(1));
				rssChannel.setUrl(selection.getString(2));
				rssChannel.setLastUpdate(selection.getString(4));
				rssChannel.setModified(selection.getInt(5) > 0);
				rssChannel.setDateLastRSSLink(selection.getString(6));
				rssChannel.setLastContentLengthXMLFile(selection.getInt(7));

				Category categoryRSSChannel = categoryHelper
						.getCategoryById(selection.getInt(3));
				rssChannel.setCategory(categoryRSSChannel);

				listRSSChannels.add(rssChannel);
			} while (selection.moveToNext());
		}

		return listRSSChannels;
	}

	public RSSChannel editLastContentLengthXMLFileRSSChannel(
			RSSChannel rssChannel) throws DataBaseTransactionException,
			EntityNullException {

		if (rssChannel != null) {

			ContentValues values = new ContentValues();
			values.put("last_update", rssChannel.getLastUpdate());
			values.put("modified", rssChannel.isModified());
			values.put("last_content_length_xml_file",
					rssChannel.getLastContentLengthXMLFile());

			if (rssChannel.getDateLastRSSLink() != null) {
				values.put("date_last_rss_link",
						rssChannel.getDateLastRSSLink());
			}

			String whereArgs[] = new String[] { rssChannel.getId() + "" };

			long idRow = db.update("rss_channel", values, "id = ?", whereArgs);
			if (idRow != -1) {
				return rssChannel;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.OPERATION_UPDATE,
					RSSChannel.class.getSimpleName());

		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}
}
