package com.diegorayo.readerss.adapters;

import java.util.List;
import com.diegorayo.readerss.R;
import com.diegorayo.readerss.entitys.RSSChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAdapterListRSSChannel extends ArrayAdapter<RSSChannel> {

	LayoutInflater layoutInflater;
	List<RSSChannel> listRSSChannels;

	public MyAdapterListRSSChannel(Context context, int resource,
			int textViewResourceId, List<RSSChannel> listRSSChannels) {

		super(context, resource, textViewResourceId, listRSSChannels);
		this.layoutInflater = LayoutInflater.from(context);
		this.listRSSChannels = listRSSChannels;
	}

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

		txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_url);
		txt.setText(rssChannel.getUrl());

		txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_lastupdate);
		txt.setText(rssChannel.getLastUpdate());

		LinearLayout layoutParent = (LinearLayout) txt.getParent();
		layoutParent.setId(rssChannel.getId());

		return row;
	}

}