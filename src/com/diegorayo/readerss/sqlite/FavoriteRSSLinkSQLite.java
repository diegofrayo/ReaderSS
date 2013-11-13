package com.diegorayo.readerss.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class FavoriteRSSLinkSQLite {

	/**
	 * 
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 */
	public FavoriteRSSLinkSQLite(SQLiteDatabase db) {
		super();
		this.db = db;
	}

	public RSSLink createFavoriteRSSLink(RSSLink rssLink)
			throws DataBaseTransactionException, EntityNullException {

		if (rssLink != null) {
			if (rssLink.getRSSChannelParent() != null) {
				ContentValues values = new ContentValues();
				values.put("title", rssLink.getTitle());
				values.put("url", rssLink.getUrl());
				values.put("rss_channel_parent", rssLink.getRSSChannelParent()
						.getId());

				long idRow = db.insert("favorite_link_rss", null, values);
				

				if (idRow != -1) {
					rssLink.setId((int) idRow);
					return rssLink;
				}

				throw new DataBaseTransactionException(
						DataBaseTransactionException.OPERATION_INSERT,
						RSSLink.class.getSimpleName());
			}

			throw new EntityNullException(RSSChannel.class.getSimpleName());
		}

		throw new EntityNullException(RSSLink.class.getSimpleName());
	}

	public boolean editFavoriteRSSLink(int idRSSChannelParent)
			throws DataBaseTransactionException {

		ContentValues values = new ContentValues();
		values.put("rss_channel_parent", -1);
		String whereArgs[] = new String[] { idRSSChannelParent + "" };

		long idRow = db.update("favorite_link_rss", values, "rss_channel_parent = ?",
				whereArgs);
		

		if (idRow != -1) {
			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.OPERATION_UPDATE,
				RSSLink.class.getSimpleName());
	}

	public boolean deleteFavoriteRSSLink(int idRSSLink)
			throws DataBaseTransactionException {

		String whereArgs[] = new String[] { idRSSLink + "" };

		long idRow = db.delete("favorite_link_rss", "id = ?", whereArgs);
		if (idRow == 1) {
			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.OPERATION_DELETE,
				RSSLink.class.getSimpleName());
	}

	public List<RSSLink> getListFavoriteRSSLinks() {

		List<RSSLink> listFavoriteRSSLinks = new ArrayList<RSSLink>();
		String[] columns = new String[] { "id", "title", "url",
				"rss_channel_parent" };

		Cursor selection = db.query("favorite_link_rss", columns, null, null,
				null, null, null);

		if (selection.moveToFirst()) {
			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(db);
			do {
				RSSLink rssLink = new RSSLink();
				rssLink.setId(selection.getInt(0));
				rssLink.setTitle(selection.getString(1));
				rssLink.setUrl(selection.getString(2));
				int idRSSChannelParent = selection.getInt(3);

				// Si el RSSChannel padre existe
				if (idRSSChannelParent != -1) {

					// Entonces obtiene sus datos
					rssLink.setRSSChannelParent(rssChannelHelper
							.getRSSChannelById(idRSSChannelParent));
				}

				listFavoriteRSSLinks.add(rssLink);
			} while (selection.moveToNext());
		}

		

		return listFavoriteRSSLinks;
	}

}
