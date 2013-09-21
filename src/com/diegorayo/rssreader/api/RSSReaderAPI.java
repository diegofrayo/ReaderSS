package com.diegorayo.rssreader.api;

import java.util.List;

import android.content.Context;
import android.webkit.URLUtil;

import com.diegorayo.rssreader.entitys.Category;
import com.diegorayo.rssreader.entitys.RSSChannel;
import com.diegorayo.rssreader.entitys.RSSLink;
import com.diegorayo.rssreader.sqlite.CategorySQLite;
import com.diegorayo.rssreader.sqlite.DatabaseConnection;
import com.diegorayo.rssreader.sqlite.RSSChannelSQLite;

public class RSSReaderAPI implements IRSSReaderAPI {

	private DatabaseConnection dbConnection;

	public RSSReaderAPI(Context context) {
		super();
		this.dbConnection = new DatabaseConnection(context, "DatabaseApp.db",
				null, 1);
	}

	@Override
	public RSSChannel createRSSChannel(String name, String url, int idCategory) {
		if (!name.trim().equals("") && URLUtil.isValidUrl(url)) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel newRSSChannel = new RSSChannel(url, name);
			newRSSChannel.setCategory(categoryHelper
					.getCategoryById(idCategory));

			return rssChannelHelper.createRSSChannel(newRSSChannel);
		}

		return null;
	}

	@Override
	public RSSChannel editRSSChannel(int idRSSChannel, String name, String url,
			int idCategory) {

		if (!name.trim().equals("") && URLUtil.isValidUrl(url)) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel newRSSChannel = new RSSChannel(url, name);
			newRSSChannel.setId(idRSSChannel);
			newRSSChannel.setCategory(categoryHelper
					.getCategoryById(idCategory));

			return rssChannelHelper.editRSSChannel(newRSSChannel);
		}

		return null;
	}

	@Override
	public boolean deleteRSSChannel(int idRSSChannel) {

		return false;
	}

	@Override
	public RSSChannel getRSSChannelById(int idRSSChannel) {

		return null;
	}

	@Override
	public Category createCategory(String name) {

		return null;
	}

	@Override
	public Category editCategory(int idCategory, String name) {

		return null;
	}

	@Override
	public Category deleteCategory(int idCategory) {

		return null;
	}

	@Override
	public Category getCategoryById(int idCategory) {

		return null;
	}

	@Override
	public List<Category> getListAllCategories() {

		return null;
	}

	@Override
	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		return null;
	}

	@Override
	public List<RSSLink> getListRSSLinksARSSChannel(int categoryRSSChannel) {

		return null;
	}

	@Override
	public List<RSSChannel> getListRSSLinksInACategory(int idCategory) {

		return null;
	}

	@Override
	public boolean addFavoriteRSSLink(String title, String url,
			int idRSSChannelParent) {

		return false;
	}

	@Override
	public boolean deleteFavoriteRSSLink(int id) {

		return false;
	}

	@Override
	public List<RSSChannel> getListAllFavoritesRSSLinks() {

		return null;
	}

	@Override
	public boolean getConfigurationRatingApp() {

		return false;
	}

	@Override
	public boolean editConfigurationRatingApp() {

		return false;
	}

}
