package com.diegorayo.readerss.api;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.URLUtil;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.FileSystemException;
import com.diegorayo.readerss.exceptions.InvalidArgumentException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;
import com.diegorayo.readerss.files.FilesManagement;
import com.diegorayo.readerss.sqlite.CategorySQLite;
import com.diegorayo.readerss.sqlite.DatabaseConnection;
import com.diegorayo.readerss.sqlite.RSSChannelSQLite;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 2 <br />
 */
public class API {

	/**
	 * Conexion a la base de datos
	 */
	private DatabaseConnection dbConnection;

	/**
	 * Atributo utilizado para crear la conexion a la base de datos, y para los
	 * metodos que crean dialogos de informacion
	 */
	private Context context;

	/**
	 * @param context
	 *            - Parametro necesario para crear la conexion a la base de
	 *            datos Sqlite
	 */
	public API(Context context) {

		this.dbConnection = new DatabaseConnection(context, "DatabaseApp.db",
				null, 2);
		this.context = context;
	}

	/**
	 * Metodo para cerrar la conexion a la base de datos
	 */
	public void closeDatabaseConnection() {

		this.dbConnection.close();
	}

	/*--------------------------------------------*/

	/*
	 * ------ Category Methods ------
	 */

	public Category createCategory(String name) throws NullEntityException,
			DataBaseTransactionException, InvalidArgumentException,
			FileSystemException {

		if (!name.trim().equals("")) {

			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getWritableDatabase());

			Category newCategory = new Category(name);
			newCategory = categoryHelper.create(newCategory);

			boolean createDirectory = FilesManagement
					.createDirectory(newCategory.getName());

			if (createDirectory) {

				return newCategory;
			} else {

				categoryHelper.deleteCategory(newCategory.getId());
				throw new FileSystemException(R.string.error_create_folder);
			}
		}

		throw new InvalidArgumentException();
	}

	public Category editCategory(int idCategory, String newName)
			throws InvalidArgumentException, DataBaseTransactionException,
			NullEntityException, FileSystemException {

		if (!newName.trim().equals("")) {

			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());
			Category oldCategory = categoryHelper.getCategoryById(idCategory);

			categoryHelper = new CategorySQLite(
					dbConnection.getWritableDatabase());
			Category categoryToEdit = new Category(newName);
			categoryToEdit.setId(idCategory);
			categoryToEdit = categoryHelper.edit(categoryToEdit);

			boolean response = FilesManagement.renameFolder(
					oldCategory.getName(), newName);

			if (response) {

				return categoryToEdit;
			} else {

				categoryHelper.edit(oldCategory);
				throw new FileSystemException(R.string.error_edit_folder);
			}
		}

		throw new InvalidArgumentException();
	}

	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException, NullEntityException,
			FileSystemException {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());
		Category categoryDelete = categoryHelper.getCategoryById(idCategory);

		categoryHelper = new CategorySQLite(dbConnection.getWritableDatabase());
		categoryHelper.deleteCategory(idCategory);

		boolean response = FilesManagement.deleteFolder(categoryDelete
				.getName());

		if (response) {

			return response;
		} else {

			categoryHelper.create(categoryDelete);
			throw new FileSystemException(R.string.error_delete_folder);
		}
	}

	public Category getCategoryById(int idCategory) {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());

		return categoryHelper.getCategoryById(idCategory);
	}

	public List<Category> getListAllCategories() {

		CategorySQLite categoryHelper = new CategorySQLite(
				dbConnection.getReadableDatabase());

		return categoryHelper.getListAllCategories();
	}

	/*--------------------------------------------*/

	/*
	 * ------ RSS Channel Methods ------
	 */

	public RSSChannel createRSSChannel(String name, String url, int idCategory)
			throws DataBaseTransactionException, InvalidArgumentException,
			FileSystemException, NullEntityException {

		if (!name.trim().equals("") && URLUtil.isValidUrl(url)) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel newRSSChannel = new RSSChannel(url, name);
			newRSSChannel.setCategory(categoryHelper
					.getCategoryById(idCategory));
			newRSSChannel.setLastUpdate(UtilAPI.getCurrentDateAndTime());

			newRSSChannel = rssChannelHelper.create(newRSSChannel);

			try {

				FilesManagement.downloadXMLFile(newRSSChannel);

				return newRSSChannel;

			} catch (URLDownloadFileException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();

			} catch (UnknownHostException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();

			} catch (IOException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();

			} catch (SAXException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();

			} catch (ParserConfigurationException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();

			} catch (NullEntityException e) {

				rssChannelHelper.delete(newRSSChannel.getId());
				UtilActivities.createErrorDialog(context, e.getMessage());
				e.printStackTrace();
			}

		}

		throw new InvalidArgumentException();
	}

	public RSSChannel editRSSChannel(int idRSSChannel, String newName,
			int newIdCategory) throws InvalidArgumentException,
			DataBaseTransactionException, NullEntityException,
			FileSystemException {

		if (!newName.trim().equals("")) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());
			CategorySQLite categoryHelper = new CategorySQLite(
					dbConnection.getReadableDatabase());

			RSSChannel oldRSSChannel = rssChannelHelper
					.getRSSChannelById(idRSSChannel);

			RSSChannel rssChannelToEdit = oldRSSChannel;
			// rssChannelToEdit.setId(idRSSChannel);
			rssChannelToEdit.setName(newName);
			rssChannelToEdit.setCategory(categoryHelper
					.getCategoryById(newIdCategory));

			rssChannelToEdit = rssChannelHelper.edit(rssChannelToEdit);

			// Si el nombre cambió, se renombra el archivo
			if (rssChannelToEdit.getName().equals(oldRSSChannel.getName()) == false) {

				boolean response = FilesManagement.renameFile(oldRSSChannel
						.getCategory().getName(), oldRSSChannel.getName(),
						rssChannelToEdit.getName());

				if (!response) {

					rssChannelHelper.edit(oldRSSChannel);
					throw new FileSystemException(R.string.error_edit_file);
				}
			}

			// Si la categoria se editó, se cambia de carpeta
			if (rssChannelToEdit.getCategory().getId() != oldRSSChannel
					.getCategory().getId()) {

				boolean response = FilesManagement.moveFile(rssChannelToEdit
						.getName(), oldRSSChannel.getCategory().getName(),
						rssChannelToEdit.getCategory().getName());

				if (!response) {

					rssChannelHelper.edit(oldRSSChannel);
					throw new FileSystemException(R.string.error_edit_folder);
				}
			}

			return rssChannelToEdit;
		}

		throw new InvalidArgumentException();
	}

	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException, NullEntityException,
			FileSystemException {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());
		RSSChannel rssChannelToDelete = rssChannelHelper
				.getRSSChannelById(idRSSChannel);

		rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getWritableDatabase());
		rssChannelHelper.delete(idRSSChannel);

		boolean response = FilesManagement.deleteFile(rssChannelToDelete);

		if (!response) {

			rssChannelHelper.create(rssChannelToDelete);
			throw new FileSystemException(R.string.error_delete_file);
		}

		return response;
	}

	public RSSChannel getRSSChannelById(int idRSSChannel) {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());

		return rssChannelHelper.getRSSChannelById(idRSSChannel);
	}

	public RSSChannel editLastUpdateRSSChannel(RSSChannel rssChannel)
			throws DataBaseTransactionException, NullEntityException {

		if (rssChannel != null) {

			RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
					dbConnection.getWritableDatabase());

			return rssChannelHelper.editLastUpdateRSSChannel(rssChannel);
		}

		throw new NullEntityException(RSSChannel.class.getSimpleName());
	}

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory) {

		RSSChannelSQLite rssChannelHelper = new RSSChannelSQLite(
				dbConnection.getReadableDatabase());

		return rssChannelHelper.getListRSSChannelsInACategory(idCategory);
	}

	/*--------------------------------------------*/

	/*
	 * ------ RSS Links ------
	 */

	public RSSChannel getListRSSLinksOfRSSChannel(RSSChannel rssChannel)
			throws NullEntityException, SAXException, IOException,
			ParserConfigurationException, DataBaseTransactionException {

		if (rssChannel != null) {

			rssChannel.setListRSSLinks(FilesManagement.readFileXML(rssChannel));
			List<RSSLink> list = rssChannel.getListRSSLinks();

			// Caso de que todos sean nuevos. Esto pasa cuando se crea el
			// RSSChannel, y no se ha leido por primera vez
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

			rssChannel.setDateLastRSSLink(list.get(0).getDate());
			rssChannel = editLastUpdateRSSChannel(rssChannel);

			return rssChannel;
		}

		throw new NullEntityException(RSSChannel.class.getSimpleName());
	}

	public List<RSSLink> downloadXMLFileAndGetListRSSLinksOfRSSChannel(
			RSSChannel rssChannel) throws NullEntityException, SAXException,
			IOException, ParserConfigurationException,
			URLDownloadFileException, InvalidArgumentException,
			DataBaseTransactionException {

		if (rssChannel != null) {

			FilesManagement.downloadXMLFile(rssChannel);

			return getListRSSLinksOfRSSChannel(rssChannel).getListRSSLinks();
		}

		throw new NullEntityException(RSSChannel.class.getSimpleName());
	}

	/*--------------------------------------------*/

	/*
	 * ------ Configuration Methods ------
	 */

	public boolean getConfigurationToViewRSSLinks() {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"Preferences", Context.MODE_PRIVATE);

		return sharedPreferences.getBoolean("open_links_in_browser", true);
	}

	public void editConfigurationToViewRSSLinks(boolean option) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"Preferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putBoolean("open_links_in_browser", option);
		editor.commit();
	}

	public void configureApp() throws NullEntityException,
			DataBaseTransactionException, InvalidArgumentException,
			FileSystemException {

		Category defaultCategory = getCategoryById(1);

		if (defaultCategory == null) {

			createCategory("default");

			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"Preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();

			editor.putBoolean("open_links_in_browser", true);
			editor.commit();
		}
	}

	public String getUsernameGoogle() {

		return UtilAPI.getUsernameGoogle(context);
	}

}
