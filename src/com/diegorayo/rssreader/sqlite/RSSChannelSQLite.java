package com.diegorayo.rssreader.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.diegorayo.rssreader.entitys.Category;
import com.diegorayo.rssreader.entitys.RSSChannel;

public class RSSChannelSQLite {

	private SQLiteDatabase db;

	public RSSChannelSQLite(SQLiteDatabase db) {
		super();
		this.db = db;
	}

	public RSSChannel createRSSChannel(RSSChannel rssChannel) {

		if (rssChannel != null) {
			if (rssChannel.getCategory() != null) {
				ContentValues values = new ContentValues();
				values.put("name", rssChannel.getName());
				values.put("url", rssChannel.getUrl());
				values.put("category", rssChannel.getCategory().getId());
				long idRow = db.insert("rss_channel", null, values);
				if (idRow != -1) {
					rssChannel.setId((int) idRow);
					return rssChannel;
				}
			}
		}

		return null;

	}

	public RSSChannel editRSSChannel(RSSChannel rssChannel) {

		if (rssChannel != null) {
			ContentValues values = new ContentValues();
			values.put("name", rssChannel.getName());
			values.put("url", rssChannel.getUrl());
			values.put("category", rssChannel.getCategory().getId());
			String whereArgs[] = new String[] { rssChannel.getId() + "" };
			long idRow = db.update("rss_channel", values, "id = ?", whereArgs);
			if (idRow != -1) {
				return rssChannel;
			}
		}

		return null;

	}

	public boolean deleteRSSChannel(int idRSSChannel) {

		String whereArgs[] = new String[] { idRSSChannel + "" };
		long idRow = db.delete("rss_channel", "id = ?", whereArgs);
		if (idRow != 0) {
			FavoriteRSSLinkSQLite favoriteRSSLinkHelper = new FavoriteRSSLinkSQLite(
					db);
			favoriteRSSLinkHelper.editFavoriteRSSLink(idRSSChannel);
			return true;
		}

		return false;
	}

	public RSSChannel getRSSChannelById(int idRSSChannel) {

		String[] columns = new String[] { "id", "name", "url", "category" };
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
			return rssChannel;
		}

		return null;
	}

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {
		String[] columns = new String[] { "id", "name", "url", "category" };
		String[] whereArgs = new String[] { idCategory + "" };
		Cursor selection = db.query("rss_channel", columns, "category = ?",
				whereArgs, null, null, null);
		List<RSSChannel> listRSSChannels = new ArrayList<RSSChannel>();

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
				listRSSChannels.add(rssChannel);
			} while (selection.moveToNext());

		}

		return listRSSChannels;
	}

}
