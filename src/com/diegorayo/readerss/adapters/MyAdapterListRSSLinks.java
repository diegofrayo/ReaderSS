package com.diegorayo.readerss.adapters;

import java.util.List;
import com.diegorayo.readerss.R;
import com.diegorayo.readerss.entitys.RSSLink;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class MyAdapterListRSSLinks extends ArrayAdapter<RSSLink> {

	/**
	 * 
	 */
	LayoutInflater layoutInflater;

	/**
	 * 
	 */
	List<RSSLink> listRSSLinks;

	public MyAdapterListRSSLinks(Context context, int resource,
			int textViewResourceId, List<RSSLink> listRSSLinks) {

		super(context, resource, textViewResourceId, listRSSLinks);
		this.layoutInflater = LayoutInflater.from(context);
		this.listRSSLinks = listRSSLinks;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		RSSLink rssLink = (RSSLink) listRSSLinks.get(position);

		if (row == null) {
			row = layoutInflater
					.inflate(R.layout.row_list_view_rss_links, null);
		}

		TextView txt = (TextView) row
				.findViewById(R.id.txt_list_rss_link_title);
		txt.setText(rssLink.getTitle());

		txt = (TextView) row.findViewById(R.id.txt_list_rss_link_date);
		txt.setText(rssLink.getDate() + " ");

		if (rssLink.isNew()) {
			row.setBackgroundResource(R.color.color_bg_new);
		}

		return row;
	}

}