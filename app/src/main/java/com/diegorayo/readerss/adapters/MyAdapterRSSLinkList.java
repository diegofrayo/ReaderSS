package com.diegorayo.readerss.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.entitys.RSSLink;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Description
 */
public class MyAdapterRSSLinkList extends ArrayAdapter<RSSLink> {

	/**
	 * Se utiliza para generar una fila del list view con contenido
	 * personalizado. El contenido es llamado de un archivo xml
	 */
	private LayoutInflater layoutInflater;
	/**
	 * Se utiliza para obtener cada rsschannel de la lista, y mostrarlo en el
	 * listview
	 */
	private List<RSSLink> listRSSLinks;

	public MyAdapterRSSLinkList(Context context, int resource,
                                int textViewResourceId, List<RSSLink> listRSSLinks) {

		super(context, resource, textViewResourceId, listRSSLinks);
		this.layoutInflater = LayoutInflater.from(context);
		this.listRSSLinks = listRSSLinks;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		RSSLink rssLink = listRSSLinks.get(position);

		if (row == null) {

			row = layoutInflater
					.inflate(R.layout.row_list_view_rss_links, null);
		}

		TextView txt = (TextView) row
				.findViewById(R.id.txt_list_rss_link_title);
		txt.setText(rssLink.getTitle());

		if (rssLink.getDate() != null) {
			
			txt = (TextView) row.findViewById(R.id.txt_list_rss_link_date);
			txt.setText(rssLink.getDate() + " ");
		}

		if (rssLink.isNew()) {

			row.setBackgroundResource(R.color.color_bg_new_rss_link);
		} else {

			row.setBackgroundResource(R.color.color_white);
		}

		return row;
	}

}