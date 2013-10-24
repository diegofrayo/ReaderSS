package com.diegorayo.readerss.api;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.ArgumentInvalidException;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public interface IRSSReaderAPI {

	/*
	 * ------ RSS Channel Methods ------
	 */

	public RSSChannel createRSSChannel(String name, String url, int idCategory)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException, IOException, SAXException,
			ParserConfigurationException, URLDownloadFileException;

	public RSSChannel editRSSChannel(int idRSSChannel, String name, String url,
			int idCategory) throws ArgumentInvalidException,
			DataBaseTransactionException, EntityNullException;

	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException, EntityNullException;

	public RSSChannel getRSSChannelById(int idRSSChannel);

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory);

	/*--------------------------------------------*/

	/*
	 * ------ Category Methods ------
	 */

	public Category createCategory(String name) throws EntityNullException,
			DataBaseTransactionException, ArgumentInvalidException, Exception;

	public Category editCategory(int idCategory, String name)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException;

	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException, EntityNullException;

	public Category getCategoryById(int idCategory);

	public List<Category> getListAllCategories();

	/*--------------------------------------------*/

	/*
	 * ------ Favorite RSS Links Methods ------
	 */

	public boolean addFavoriteRSSLink(String title, String url, String date,
			int idRSSChannelParent) throws DataBaseTransactionException,
			EntityNullException, ArgumentInvalidException;

	public boolean deleteFavoriteRSSLink(int idRSSLink)
			throws DataBaseTransactionException;

	public List<RSSLink> getListAllFavoritesRSSLinks();

	/*--------------------------------------------*/

	/*
	 * ------ Configuration Methods ------
	 */

	public boolean getConfigurationRatingApp();

	public boolean editConfigurationRatingApp();

	public String getUsername();

	public String editUsername(String newUserName)
			throws ArgumentInvalidException;
}
