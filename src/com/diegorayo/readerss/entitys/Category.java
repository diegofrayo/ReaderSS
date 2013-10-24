package com.diegorayo.readerss.entitys;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class Category {

	/**
	 * 
	 */
	private int id;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private List<RSSChannel> listRSSChannels;

	/**
	 * 
	 * @param name
	 */
	public Category(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public Category() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RSSChannel> getListRSSChannels() {
		return listRSSChannels;
	}

	public void setListRSSChannels(List<RSSChannel> listRSSChannels) {
		this.listRSSChannels = listRSSChannels;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

}
