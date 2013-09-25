package com.diegorayo.rssreader.api;

import java.util.List;

import com.diegorayo.rssreader.entitys.Category;
import com.diegorayo.rssreader.entitys.RSSChannel;
import com.diegorayo.rssreader.entitys.RSSLink;
import com.diegorayo.rssreader.exceptions.ArgumentInvalidException;
import com.diegorayo.rssreader.exceptions.DataBaseTransactionException;
import com.diegorayo.rssreader.exceptions.EntityNullException;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public interface IRSSReaderAPI {

	public RSSChannel createRSSChannel(String name, String url, int idCategory)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException;

	public RSSChannel editRSSChannel(int idRSSChannel, String name, String url,
			int idCategory) throws ArgumentInvalidException,
			DataBaseTransactionException, EntityNullException;

	public boolean deleteRSSChannel(int idRSSChannel)
			throws DataBaseTransactionException;

	public RSSChannel getRSSChannelById(int idRSSChannel);

	public Category createCategory(String name) throws EntityNullException,
			DataBaseTransactionException, ArgumentInvalidException;

	public Category editCategory(int idCategory, String name)
			throws ArgumentInvalidException, DataBaseTransactionException,
			EntityNullException;

	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException;

	public Category getCategoryById(int idCategory);

	public List<Category> getListAllCategories();

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory);

	public boolean addFavoriteRSSLink(String title, String url, String date,
			int idRSSChannelParent) throws DataBaseTransactionException,
			EntityNullException, ArgumentInvalidException;

	public boolean deleteFavoriteRSSLink(int idRSSLink) throws DataBaseTransactionException;

	public List<RSSLink> getListAllFavoritesRSSLinks();

	public boolean getConfigurationRatingApp();

	public boolean editConfigurationRatingApp();

}
