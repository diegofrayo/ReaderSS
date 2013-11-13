package com.diegorayo.readerss.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;
import android.webkit.URLUtil;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.ArgumentInvalidException;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;
import com.diegorayo.readerss.files.FilesManagement;
import com.diegorayo.readerss.sqlite.CategorySQLite;
import com.diegorayo.readerss.sqlite.ConfigurationSQLite;
import com.diegorayo.readerss.sqlite.DatabaseConnection;
import com.diegorayo.readerss.sqlite.FavoriteRSSLinkSQLite;
import com.diegorayo.readerss.sqlite.RSSChannelSQLite;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

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
	 */
	public static final String PATH = ApplicationContext
			.getStringResource(R.string.path_app_files);

	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 * @param context
	 */
	public RSSReaderAPI(Context context) {
		super();
		this.dbConnection = new DatabaseConnection(context, "DatabaseApp.db",
				null, 1);
		this.context = context;
	}

	/**
	 * 
	 */
	public void closeConnection() {
		this.dbConnection.close();
	}

	@Override
	public RSSChannel createRSSChannel(String name, String url, int idCategory)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException {

		boolean urlIsANumber = UtilAPI.isNumber(url.toCharArray());

		if (!name.trim().equals("")
				&& (URLUtil.isValidUrl(url) || urlIsANumber)) {

			if (urlIsANumber) {
				url = "http://www.facebook.com/feeds/page.php?id=" + url
						+ "&format=rss20";
			}

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel newRSSChannel = new RSSChannel(url, name);
			newRSSChannel.setCategory(categoryHelper
					.getCategoryById(idCategory));
			newRSSChannel.setModified(true);
			newRSSChannel.setLastContentLengthXMLFile(0);
			newRSSChannel.setLastUpdate(UtilAPI.getCurrentDateAndTime());
			newRSSChannel = rssChannelHelper.createRSSChannel(newRSSChannel);

			try {

				FilesManagement filesManagement = new FilesManagement();
				int lastContentLengthXMLFile = filesManagement
						.downloadXMLFile(newRSSChannel);
				newRSSChannel
						.setLastContentLengthXMLFile(lastContentLengthXMLFile);

				return rssChannelHelper
						.editLastContentLengthXMLFileRSSChannel(newRSSChannel);

			} catch (URLDownloadFileException e) {
				deleteRSSChannel(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			} catch (UnknownHostException e) {
				deleteRSSChannel(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				deleteRSSChannel(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			} catch (SAXException e) {
				deleteRSSChannel(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				deleteRSSChannel(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		throw new ArgumentInvalidException();
	}

	@Override
	public RSSChannel editRSSChannel(int idRSSChannel, String name,
			int idCategory, String lastUpdate, boolean modified,
			String dateLastRSSLink, int lastContentLengthXMLFile)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException {

		if (!name.trim().equals("")) {

			FilesManagement filesManagement = new FilesManagement();
			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel oldRSSChannel = rssChannelHelper
					.getRSSChannelById(idRSSChannel);

			RSSChannel rssChannelToEdit = new RSSChannel(null, name);
			rssChannelToEdit.setId(idRSSChannel);
			rssChannelToEdit.setCategory(categoryHelper
					.getCategoryById(idCategory));
			rssChannelToEdit.setLastUpdate(lastUpdate);
			rssChannelToEdit.setModified(modified);
			rssChannelToEdit.setDateLastRSSLink(dateLastRSSLink);
			rssChannelToEdit
					.setLastContentLengthXMLFile(lastContentLengthXMLFile);

			rssChannelToEdit = rssChannelHelper
					.editRSSChannel(rssChannelToEdit);

			// Si el nombre cambió, se renombra el archivo
			if (rssChannelToEdit.getName().equals(oldRSSChannel.getName()) == false) {

				filesManagement.renameFile(oldRSSChannel,
						rssChannelToEdit.getName());
				oldRSSChannel.setName(rssChannelToEdit.getName());
			}

			// Si la categoria se editó, se cambia de carpeta
			if (rssChannelToEdit.getCategory().getId() != oldRSSChannel
					.getCategory().getId()) {

				filesManagement.moveFile(oldRSSChannel, rssChannelToEdit);
			}

			return rssChannelToEdit;

		}

		throw new ArgumentInvalidException();
	}

	@Override
	public RSSChannel editLastContentLengthXMLFileRSSChannel(
			RSSChannel rssChannel) throws DataBaseTransactionException,
			EntityNullException {

		if (rssChannel != null) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());

			return rssChannelHelper
					.editLastContentLengthXMLFileRSSChannel(rssChannel);

		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

	@Override
	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException, EntityNullException {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());
		RSSChannel rssChannelDelete = rssChannelHelper
				.getRSSChannelById(idRSSChannel);

		rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getWritableDatabase());
		boolean response = rssChannelHelper.deleteRSSChannel(idRSSChannel);

		FilesManagement filesManagement = new FilesManagement();
		filesManagement.deleteFile(rssChannelDelete);

		return response;
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
			newCategory = categoryHelper.createCategory(newCategory);

			FilesManagement filesManagement = new FilesManagement();
			boolean createDirectory = filesManagement
					.createDirectory(newCategory);

			if (createDirectory) {
				return newCategory;
			} else {
				categoryHelper.deleteCategory(newCategory.getId());
				throw new DataBaseTransactionException(
						DataBaseTransactionException.OPERATION_INSERT,
						Category.class.getSimpleName());
			}
		}

		throw new ArgumentInvalidException();
	}

	@Override
	public Category editCategory(int idCategory, String name)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException {

		if (!name.trim().equals("")) {

			FilesManagement filesManagement = new FilesManagement();
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			Category oldCategory = categoryHelper.getCategoryById(idCategory);

			categoryHelper = new CategorySQLite(
					dbConnection.getWritableDatabase());

			Category categoryToEdit = new Category(name);
			categoryToEdit.setId(idCategory);
			categoryToEdit = categoryHelper.editCategory(categoryToEdit);

			filesManagement.renameFolder(oldCategory, name);

			return categoryToEdit;
		}

		throw new ArgumentInvalidException();
	}

	@Override
	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException, EntityNullException {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());
		Category categoryDelete = categoryHelper.getCategoryById(idCategory);

		categoryHelper = new CategorySQLite(dbConnection.getWritableDatabase());
		boolean response = categoryHelper.deleteCategory(idCategory);

		FilesManagement filesManagement = new FilesManagement();
		filesManagement.deleteFolder(categoryDelete);

		return response;
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

		throw new ArgumentInvalidException();
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
	public void configurationApp() {
		new ConfigurationSQLite(dbConnection.getWritableDatabase())
				.insertConfiguration();
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
				dbConnection.getWritableDatabase());

		return configurationSQLiteHelper.editConfigurationRatingApp();
	}

	@Override
	public String getUsername() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getReadableDatabase());

		return configurationSQLiteHelper.getUsername();
	}

	@Override
	public String editUsername(String newUserName)
			throws ArgumentInvalidException {

		if (!newUserName.trim().equals("")) {

			ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
					dbConnection.getWritableDatabase());

			return configurationSQLiteHelper.editUsername(newUserName);
		}

		throw new ArgumentInvalidException();
	}

	public boolean getOpenFirstApp() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getReadableDatabase());

		return configurationSQLiteHelper.getOpenFirstApp();
	}

	public boolean editOpenFirstApp() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getWritableDatabase());

		return configurationSQLiteHelper.editOpenFirstApp();
	}

	@Override
	public boolean getViewRSSLinksInApp() {

		ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
				dbConnection.getReadableDatabase());

		return configurationSQLiteHelper.getViewRSSLinksInApp();
	}

	@Override
	public boolean editViewRSSLinksInApp(String option)
			throws ArgumentInvalidException {

		if (!option.trim().equals("")) {

			ConfigurationSQLite configurationSQLiteHelper = new ConfigurationSQLite(
					dbConnection.getWritableDatabase());

			return configurationSQLiteHelper.editViewRSSLinksInApp(option);
		}

		throw new ArgumentInvalidException();
	}

	@Override
	public List<RSSLink> getListRSSLinksOfRSSChannel(RSSChannel rssChannel)
			throws EntityNullException, SAXException, IOException,
			ParserConfigurationException {

		if (rssChannel != null) {

			List<RSSLink> list = new FilesManagement().readFileXML(rssChannel);

			if (rssChannel.getDateLastRSSLink() == null) {
				for (RSSLink rssLink : list) {
					rssLink.setNew(true);
				}
			} else {
				for (RSSLink rssLink : list) {
					if (rssLink.getDate().equals(
							rssChannel.getDateLastRSSLink())) {
						break;
					} else {
						rssLink.setNew(true);
					}
				}
			}

			return list;
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

	@Override
	public List<RSSLink> downloadXMLFileAndGetListRSSLinksOfRSSChannel(
			RSSChannel rssChannel) throws EntityNullException, SAXException,
			IOException, ParserConfigurationException,
			URLDownloadFileException, ArgumentInvalidException,
			DataBaseTransactionException {

		if (rssChannel != null) {

			FilesManagement filesManagement = new FilesManagement();
			int lastContentLengthXMLFile = filesManagement
					.downloadXMLFile(rssChannel);

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			rssChannel.setLastContentLengthXMLFile(lastContentLengthXMLFile);
			rssChannelHelper.editLastContentLengthXMLFileRSSChannel(rssChannel);

			return getListRSSLinksOfRSSChannel(rssChannel);
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());

	}

	@Override
	public void checkRSSChannelsModified() {

		FilesManagement filesManagement = new FilesManagement();
		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());

		List<RSSChannel> listAllRSSChannels = rssChannelHelper
				.getListAllRSSChannels();

		rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getWritableDatabase());

		for (RSSChannel rssChannel : listAllRSSChannels) {

			try {

				HttpURLConnection urlConnection = (HttpURLConnection) new URL(
						rssChannel.getUrl()).openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.connect();
				int contentLength = urlConnection.getContentLength();

				// System.out.println("URL" + contentLength);
				// System.out.println("DB"
				// + rssChannel.getLastContentLengthXMLFile());

				if (rssChannel.getLastContentLengthXMLFile() != contentLength
						&& contentLength != 0) {

					filesManagement.downloadXMLFile(rssChannel);
					rssChannel.setLastUpdate(UtilAPI.getCurrentDateAndTime());
					rssChannel.setLastContentLengthXMLFile(contentLength);
					rssChannel.setModified(true);
					rssChannelHelper
							.editLastContentLengthXMLFileRSSChannel(rssChannel);

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (EntityNullException e) {
				e.printStackTrace();
			} catch (URLDownloadFileException e) {
				e.printStackTrace();
			} catch (DataBaseTransactionException e) {
				e.printStackTrace();
			}

		}

	}

}
