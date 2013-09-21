package com.diegorayo.rssreader.entitys;

public class RSSLink {

	private int id;
	private String title;
	private String url;
	private String date;
	private String description;
	private RSSChannel rssChannelParent;

	public RSSLink(String title, String url, String date) {
		super();
		this.title = title;
		this.url = url;
		this.date = date;
	}

	public RSSLink() {
		super();
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

	public RSSChannel getRssChannelParent() {
		return rssChannelParent;
	}

	public void setRssChannelParent(RSSChannel rssChannelParent) {
		this.rssChannelParent = rssChannelParent;
	}

}
