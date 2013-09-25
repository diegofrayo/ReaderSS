package com.diegorayo.rssreader.api;

import java.util.List;

import android.content.Context;
import android.webkit.URLUtil;

import com.diegorayo.rssreader.entitys.Category;
import com.diegorayo.rssreader.entitys.RSSChannel;
import com.diegorayo.rssreader.entitys.RSSLink;
import com.diegorayo.rssreader.exceptions.ArgumentInvalidException;
import com.diegorayo.rssreader.exceptions.DataBaseTransactionException;
import com.diegorayo.rssreader.exceptions.EntityNullException;
import com.diegorayo.rssreader.sqlite.CategorySQLite;
import com.diegorayo.rssreader.sqlite.ConfigurationSQLite;
import com.diegorayo.rssreader.sqlite.DatabaseConnection;
import com.diegorayo.rssreader.sqlite.FavoriteRSSLinkSQLite;
import com.diegorayo.rssreader.sqlite.RSSChannelSQLite;

/**
 * 
 * @author Diego Rayo
 * @version 1 <br />
 *          En cada metodo de esta clase, se debe validar los tipos de datos
 *          ingresados, y tambien la logica de negocio. Por cuestiones de tiempo
 *          solo validé los tipos de datos ingresados
 */
public class RSSReaderAPI implements IRSSReaderAPI {

	/**
	 * 
	 */
	private DatabaseConnection dbConnection;

	/**
	 * 
	 * @param context
	 */
	public RSSReaderAPI(Context context) {
		super();
		this.dbConnection = new DatabaseConnection(context, "DatabaseApp.db",
				null, 2);
	}

	@Override
	public RSSChannel createRSSChannel(String name, String url, int idCategory)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException {

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

		throw new ArgumentInvalidException("");
	}

	@Override
	public RSSChannel editRSSChannel(int idRSSChannel, String name, String url,
			int idCategory) throws ArgumentInvalidException,
			DataBaseTransactionException, EntityNullException {

		if (!name.trim().equals("") && URLUtil.isValidUrl(url)) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel rssChannelToEdit = new RSSChannel(url, name);
			rssChannelToEdit.setId(idRSSChannel);
			rssChannelToEdit.setCategory(categoryHelper
					.getCategoryById(idCategory));

			return rssChannelHelper.editRSSChannel(rssChannelToEdit);

		}

		throw new ArgumentInvalidException("");
	}

	@Override
	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getWritableDatabase());
		return rssChannelHelper.deleteRSSChannel(idRSSChannel);
	}

	@Override
	public RSSChannel getRSSChannelById(int idRSSChannel) {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());
		return rssChannelHelper.getRSSChannelById(idRSSChannel);
	}

	@Override
	public Category createCategory(String name) throws EntityNullException,
			DataBaseTransactionException, ArgumentInvalidException {

		if (!name.trim().equals("")) {
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getWritableDatabase());
			Category newCategory = new Category(name);

			return categoryHelper.createCategory(newCategory);
		}

		throw new ArgumentInvalidException("");
	}

	@Override
	public Category editCategory(int idCategory, String name)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException {

		if (!name.trim().equals("")) {
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getWritableDatabase());
			Category newCategory = new Category(name);

			return categoryHelper.editCategory(newCategory);
		}

		throw new ArgumentInvalidException("");
	}

	@Override
	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getWritableDatabase());
		return categoryHelper.deleteCategory(idCategory);
	}

	@Override
	public Category getCategoryById(int idCategory) {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());
		return categoryHelper.getCategoryById(idCategory);
	}

	@Override
	public List<Category> getListAllCategories() {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());
		return categoryHelper.getListAllCategories();
	}

	@Override
	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());
		return rssChannelHelper.getListRSSChannelsInACategory(idCategory);
	}

	@Override
	public boolean addFavoriteRSSLink(String title, String url, String date,
			int idRSSChannelParent) throws DataBaseTransactionException,
			EntityNullException, ArgumentInvalidException {

		if (!date.trim().equals("") && !title.trim().equals("")
				&& URLUtil.isValidUrl(url)) {

			FavoriteRSSLinkSQLite favoriteRSSLinkSQLiteHelper = new FavoriteRSSLinkSQLite(
					dbConnection.getWritableDatabase());
			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getReadableDatabase());

			RSSChannel rssChannelParent = rssChannelHelper
					.getRSSChannelById(idRSSChannelParent);
			RSSLink newRSSLink = new RSSLink(title, url, date);
			newRSSLink.setRSSChannelParent(rssChannelParent);

			// Invoco al metodo. Se tiene que ejecutar bien, o lanza excepcion
			favoriteRSSLinkSQLiteHelper.createFavoriteRSSLink(newRSSLink);

			return true;
		}

		throw new ArgumentInvalidException("");
	}

	@Override
	public boolean deleteFavoriteRSSLink(int idRSSLink)
			throws DataBaseTransactionException {

		FavoriteRSSLinkSQLite favoriteRSSLinkSQLiteHelper = new FavoriteRSSLinkSQLite(
				dbConnection.getWritableDatabase());
		return favoriteRSSLinkSQLiteHelper.deleteFavoriteRSSLink(idRSSLink);
	}

	@Override
	public List<RSSLink> getListAllFavoritesRSSLinks() {

		FavoriteRSSLinkSQLite favoriteRSSLinkSQLiteHelper = new FavoriteRSSLinkSQLite(
				dbConnection.getWritableDatabase());
		return favoriteRSSLinkSQLiteHelper.getListFavoriteRSSLinks();
	}

	@Override
	public boolean getConfigurationRatingApp() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getReadableDatabase());
		return configurationSQLiteHelper.getConfigurationRatingApp();
	}

	@Override
	public boolean editConfigurationRatingApp() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getReadableDatabase());
		return configurationSQLiteHelper.editConfigurationRatingApp();
	}

}
