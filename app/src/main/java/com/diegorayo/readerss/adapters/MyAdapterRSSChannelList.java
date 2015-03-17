package com.diegorayo.readerss.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.RSSChannel;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Adaptador para generar un listview con la lista de rsschannels de
 *          una actividad
 */
public class MyAdapterRSSChannelList extends ArrayAdapter<RSSChannel> {

	/**
	 * Se utiliza para generar una fila del list view con contenido
	 * personalizado. El contenido es llamado de un archivo xml
	 */
	private LayoutInflater layoutInflater;

	/**
	 * Se utiliza para obtener cada rsschannel de la lista, y mostrarlo en el
	 * listview
	 */
	private List<RSSChannel> listRSSChannels;

	public MyAdapterRSSChannelList(Context context, int resource,
                                   int textViewResourceId, List<RSSChannel> listRSSChannels) {

		super(context, resource, textViewResourceId, listRSSChannels);
		this.layoutInflater = LayoutInflater.from(context);
		this.listRSSChannels = listRSSChannels;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		RSSChannel rssChannel = listRSSChannels.get(position);

		if (row == null) {

			row = layoutInflater.inflate(R.layout.row_list_view_rss_channel,
					null);
		}

		TextView txt = (TextView) row
				.findViewById(R.id.txt_list_rss_channel_name);
		txt.setText(rssChannel.getName());

		txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_url);
		txt.setText( rssChannel.getWebsite() + " ");

		txt = (TextView) row.findViewById(R.id.txt_list_rss_channel_lastupdate);
		txt.setText(ApplicationContext
				.getStringResource(R.string.word_last_update)
				+ ": "
				+ rssChannel.getLastUpdate() + " ");

		row.setId(rssChannel.getId());

		return row;
	}

}