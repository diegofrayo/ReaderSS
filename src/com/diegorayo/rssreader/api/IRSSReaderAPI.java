package com.diegorayo.rssreader.api;

import java.util.List;

import com.diegorayo.rssreader.entitys.Category;
import com.diegorayo.rssreader.entitys.RSSChannel;
import com.diegorayo.rssreader.entitys.RSSLink;

public interface IRSSReaderAPI {

	public RSSChannel createRSSChannel(String name, String url, int idCategory);

	public RSSChannel editRSSChannel(int idRSSChannel, String name, String url,
			int idCategory);

	public boolean deleteRSSChannel(int idRSSChannel);

	public RSSChannel getRSSChannelById(int idRSSChannel);

	public Category createCategory(String name);

	public Category editCategory(int idCategory, String name);

	public Category deleteCategory(int idCategory);

	public Category getCategoryById(int idCategory);

	public List<Category> getListAllCategories();

	public List<RSSChannel> getListRSSChannelsInACategory(int idCategory);

	public List<RSSLink> getListRSSLinksARSSChannel(int categoryRSSChannel);

	public List<RSSChannel> getListRSSLinksInACategory(int idCategory);

	public boolean addFavoriteRSSLink(String title, String url,
			int idRSSChannelParent);

	public boolean deleteFavoriteRSSLink(int id);

	public List<RSSChannel> getListAllFavoritesRSSLinks();

	public boolean getConfigurationRatingApp();

	public boolean editConfigurationRatingApp();

}
