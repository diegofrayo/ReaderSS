package com.diegorayo.readerss.entitys;

/**
 * @author Diego Rayo
 * @version 2 <br />
 * 
 */
public class RSSLink {

	/**
	 * 
	 */
	private int id;

	/**
	 * 
	 */
	private String title;

	/**
	 * 
	 */
	private String url;

	/**
	 * 
	 */
	private String date;

	/**
	 * 
	 */
	private String description;

	/**
	 * 
	 */
	private RSSChannel rssChannelParent;

	/**
	 * Al actualizar un canal rss, este atributo ayuda a saber si el link ya
	 * habia sido visto por el usuario o es nuevo para el
	 */
	private boolean isNew;

	/**
	 * 
	 * @param title
	 * @param url
	 * @param date
	 */
	public RSSLink(String title, String url, String date) {
		super();
		this.title = title;
		this.url = url;
		this.date = date;
		this.isNew = false;
	}

	public RSSLink() {
		this.isNew = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RSSChannel getRSSChannelParent() {
		return rssChannelParent;
	}

	public void setRSSChannelParent(RSSChannel rssChannelParent) {
		this.rssChannelParent = rssChannelParent;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {

		return title;
	}

}
