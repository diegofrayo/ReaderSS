package com.diegorayo.readerss.adapters;

import java.util.List;
import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.RSSChannel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class MyAdapterListRSSChannel extends ArrayAdapter<RSSChannel> {

	/**
	 * 
	 */
	LayoutInflater layoutInflater;

	/**
	 * 
	 */
	List<RSSChannel> listRSSChannels;

	public MyAdapterListRSSChannel(Context context, int resource,
			int textViewResourceId, List<RSSChannel> listRSSChannels) {

		super(context, resource, textViewResourceId, listRSSChannels);
		this.layoutInflater = LayoutInflater.from(context);
		this.listRSSChannels = listRSSChannels;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		RSSChannel rssChannel = (RSSChannel) listRSSChannels.get(position);

		if (row == null) {
			row = layoutInflater.inflate(R.layout.row_list_view_rss_channel,
					null);
		}

		TextView txt = (TextView) row
				.findViewById(R.id.txt_list_rss_channel_name);
		txt.setText(rssChannel.getName());

		// txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_url);
		// txt.setText(rssChannel.getUrl());

		txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_lastupdate);
		txt.setText(ApplicationContext
				.getStringResource(R.string.txt_last_update)
				+ ": "
				+ rssChannel.getLastUpdate() + " ");

		RelativeLayout layoutParent = (RelativeLayout) txt.getParent();
		layoutParent.setId(rssChannel.getId());

		if (rssChannel.isModified()) {
			row.setBackgroundResource(R.color.color_bg_new);
		}

		return row;
	}

}