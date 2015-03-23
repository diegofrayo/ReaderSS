package com.diegorayo.readerss.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.api.API;
import com.diegorayo.readerss.entitys.Category;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Adapatdor para generar un listview con la lista de categorias
 */
public class MyAdapterCategoryList extends ArrayAdapter<Category> {

	/**
	 * Se utiliza para generar una fila del list view con contenido
	 * personalizado. El contenido es llamado de un archivo xml
	 */
	private LayoutInflater layoutInflater;

	/**
	 * Se utiliza para obtener cada categoria de la lista, y mostrarla en el
	 * listview
	 */
	private List<Category> categoryList;

	private API api;

	public MyAdapterCategoryList(Context context, int resource,
                                 int textViewResourceId, List<Category> categoryList, API api) {

		super(context, resource, textViewResourceId, categoryList);
		this.layoutInflater = LayoutInflater.from(context);
		this.categoryList = categoryList;
		this.api = api;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		Category category = categoryList.get(position);

		if (row == null) {

			row = layoutInflater.inflate(R.layout.row_list_view_categories,
					null);
		}

		TextView txt = (TextView) row
				.findViewById(R.id.txt_list_categories_title);
		txt.setText(category.getName());

		txt = (TextView) row
				.findViewById(R.id.txt_list_categories_number_rss_channels);
		txt.setText("(" + api.countRSSChannelsInCategory(category.getId())
				+ ")");

		row.setId(position);

		return row;
	}

}