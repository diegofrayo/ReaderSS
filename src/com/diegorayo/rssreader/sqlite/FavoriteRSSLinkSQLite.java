package com.diegorayo.rssreader.sqlite;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.diegorayo.rssreader.entitys.RSSLink;

public class FavoriteRSSLinkSQLite {

	private SQLiteDatabase db;

	public FavoriteRSSLinkSQLite(SQLiteDatabase db) {
		super();
		this.db = db;
	}

	public RSSLink createFavoriteRSSLink(RSSLink rssLink) {

		if (rssLink != null) {
			ContentValues values = new ContentValues();
			values.put("title", rssLink.getTitle());
			values.put("url", rssLink.getUrl());
			values.put("rss_channel_parent", rssLink.getRssChannelParent()
					.getId());
			long idRow = db.insert("favorite_link_rss", null, values);
			if (idRow != -1) {
				rssLink.setId((int) idRow);
				return rssLink;
			}
		}

		return null;

	}

	public boolean editFavoriteRSSLink(int idRSSChannelParent) {

		ContentValues values = new ContentValues();
		values.put("rss_parent", -1);
		String whereArgs[] = new String[] { idRSSChannelParent + "" };
		long idRow = db.update("favorite_link_rss", values, "rss_parent = ?",
				whereArgs);
		if (idRow != -1) {
			return true;
		}

		return false;

	}

	public boolean deleteFavoriteRSSLink(int idRSSLink) {

		String whereArgs[] = new String[] { idRSSLink + "" };
		long idRow = db.delete("favorite_link_rss", "id = ?", whereArgs);
		if (idRow != 0) {
			return true;
		}

		return false;
	}

	public List<RSSLink> getListFavoriteRSSLinks() {
		String[] columns = new String[] { "id", "title", "url",
				"rss_channel_parent" };
		Cursor selection = db.query("favorite_link_rss", columns, null, null,
				null, null, null);
		List<RSSLink> listRSSLinks = new ArrayList<RSSLink>();

		if (selection.moveToFirst()) {
			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(db);

			do {
				RSSLink rssLink = new RSSLink();
				rssLink.setId(selection.getInt(0));
				rssLink.setTitle(selection.getString(1));
				rssLink.setUrl(selection.getString(2));
				int idRSSChannelParent = selection.getInt(3);
				if (idRSSChannelParent != -1) {
					rssLink.setRssChannelParent(rssChannelHelper
							.getRSSChannelById(idRSSChannelParent));
				}
				listRSSLinks.add(rssLink);
			} while (selection.moveToNext());

		}

		return listRSSLinks;
	}

}
