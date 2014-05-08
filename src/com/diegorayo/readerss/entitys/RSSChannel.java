package com.diegorayo.readerss.entitys;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 * 
 */
public class RSSChannel {

	/**
     *
     */
	private int id;

	/**
     *
     */
	private String url;

	/**
     *
     */
	private String name;

	/**
     *
     */
	private List<RSSLink> listRSSLinks;

	/**
     *
     */
	private Category category;

	/**
	 * Fecha y hora de la ultima vez que fue actualizado el canal por el usuario
	 */
	private String lastUpdate;

	/**
	 * Es la fecha del RSSLink mas reciente del canal
	 */
	private String dateLastRSSLink;

	/**
	 * @param url
	 * @param name
	 */
	public RSSChannel(String url, String name) {

		this.url = url;
		this.name = name;
	}

	public RSSChannel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RSSLink> getListRSSLinks() {
		return listRSSLinks;
	}

	public void setListRSSLinks(List<RSSLink> listRSSLinks) {
		this.listRSSLinks = listRSSLinks;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getDateLastRSSLink() {
		return dateLastRSSLink;
	}

	public void setDateLastRSSLink(String dateLastRSSLink) {
		this.dateLastRSSLink = dateLastRSSLink;
	}

	@Override
	public String toString() {

		return name;
	}

}
