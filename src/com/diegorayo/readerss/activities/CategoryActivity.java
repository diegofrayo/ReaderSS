package com.diegorayo.readerss.activities;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.adapters.MyAdapterListRSSChannels;
import com.diegorayo.readerss.api.API;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.FileSystemException;
import com.diegorayo.readerss.exceptions.InvalidArgumentException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Actividad para mostrar una categoria con sus rsschannels
 */
@SuppressLint("NewApi")
public class CategoryActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * Clase que provee todos los metodos y funcionalidades de la aplicacion
	 */
	private API api;

	/**
	 * Lista de los RSSChannel de la categoria actual
	 */
	private List<RSSChannel> rssChannelList;

	/**
	 * Categoria seleccionada por el usuario. Se utiliza en varios dialogs
	 */
	private Category currentCategory;

	/**
	 * Utilizado para los metodos en donde se tienen que mostrar Dialogs
	 */
	private Dialog dialog;

	/**
	 * Utilizado para pasar parametros cuando el usuario se devuelva a la
	 * actividad anterior
	 */
	private Intent intent;

	/*
	 * Metodo que se dispara cuando se inicia la actividad (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_category);
		UtilActivities.inflateHeaderApp(this);

		Intent it = getIntent();
		int idCategory = it.getIntExtra("category_id", -1);

		if (idCategory != -1) {

			api = new API();
			currentCategory = api.getCategoryById(idCategory);
			rssChannelList = api.getListRSSChannelsInACategory(idCategory);
			intent = new Intent();
			generateListViewRSSChannels();
			updateInformationActivity();
		} else {

			// Vuelvo a la actividad anterior
			it = new Intent(this, CategoryActivity.class);
			it.putExtra("", -1);
			startActivity(it);
		}

	}

	/*
	 * Metodo cuando se preciona un boton (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		try {

			switch (v.getId()) {

			case R.id.btn_edit_category:

				EditText editTextNameCategory = (EditText) dialog
						.findViewById(R.id.edt_name_category);
				String textNameCategory = editTextNameCategory.getText()
						.toString();

				// Si realmente SI se modificó el nombre de la categoria
				if (textNameCategory.equals(currentCategory.getName()) == false) {

					currentCategory = api.editCategory(currentCategory.getId(),
							textNameCategory);

					UtilActivities.createSuccessDialog(CategoryActivity.this,
							R.string.success_edit_category);
					dialog.dismiss();

					intent.putExtra("changes_category", true);
					updateInformationActivity();
				}

				break;

			case R.id.btn_cancel:

				dialog.dismiss();
				break;

			}

		} catch (InvalidArgumentException e) {

			UtilActivities.createErrorDialog(CategoryActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (DataBaseTransactionException e) {

			UtilActivities.createErrorDialog(CategoryActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (NullEntityException e) {

			UtilActivities.createErrorDialog(CategoryActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (FileSystemException e) {

			UtilActivities.createErrorDialog(CategoryActivity.this,
					e.toString());
			e.printStackTrace();
		}
	}

	/*
	 * Metodo que se utiliza cuando se selecciona un item de un menu que es
	 * desplegado despues de presionar un item de un listview durante varios
	 * segundos
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// Obtengo el item seleccionado
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		DialogInterface.OnClickListener onClickListener = null;

		switch (item.getItemId()) {

		case R.id.btn_menu_context_delete_rss_channel:

			onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					try {

						RSSChannel rssChannelSelected = rssChannelList
								.get((int) info.id);
						api.deleteRSSChannel(rssChannelSelected.getId());
						generateListViewRSSChannels();
						intent.putExtra("changes_category", true);

					} catch (DataBaseTransactionException e) {

						UtilActivities.createErrorDialog(CategoryActivity.this,
								e.toString());
						e.printStackTrace();
					} catch (NullEntityException e) {

						UtilActivities.createErrorDialog(CategoryActivity.this,
								e.toString());
						e.printStackTrace();
					} catch (FileSystemException e) {

						e.printStackTrace();
					}
				}
			};

			UtilActivities.createConfirmDialog(this,
					R.string.txt_qst_delete_rss_channel, onClickListener);

			return true;

		case R.id.btn_menu_context_copy_url:

			RSSChannel rssChannelSelected = rssChannelList.get((int) info.id);

			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ApplicationContext
					.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(rssChannelSelected.getUrl());

			UtilActivities.createSuccessDialog(CategoryActivity.this,
					R.string.success_url_copiada);

			return true;

		default:

			return super.onContextItemSelected(item);
		}
	}

	/*
	 * Este metodo se utiliza para crear un menu contextual que va a ser
	 * desplegado cuando se selecciona un item de un listview durante varios
	 * segundos (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		ListView listView = (ListView) v;
		menu.setHeaderTitle(listView.getAdapter().getItem(info.position)
				.toString());

		inflater.inflate(R.menu.menu_contextual_category_activity, menu);
	}

	/*
	 * Metodo para crear el menu contextual de la actividad (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_activity_category, menu);
		return true;
	}

	/*
	 * Metodo que se utiliza cuando se selecciona un RssChannel. Sucede cuando
	 * se seleccion un item de un listview (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

		api.closeDatabaseConnection();
		api = null;

		Intent intentRSSChannelActivity = new Intent(this,
				RSSChannelActivity.class);
		intentRSSChannelActivity.putExtra("rss_channel_id", v.getId());

		// Significa que inicia una nueva actividad, y cuando esta se acaba,
		// vuelve a la actual
		this.startActivityForResult(intentRSSChannelActivity, 1);
	}

	/*
	 * Metodo que se utiliza cuando se selecciona algun item del menu contextual
	 * de la actividad (non-Javadoc) (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.btn_menu_edit_category:

			showDialogToEditCategory();

			break;

		case R.id.btn_menu_delete_category:

			// Parametro que contiene la funcion OnClick para crear el
			// dialogo de confirmacion
			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					try {

						api.deleteCategory(currentCategory.getId());
						onBackPressed();

					} catch (DataBaseTransactionException e) {

						UtilActivities.createErrorDialog(CategoryActivity.this,
								e.toString());
						e.printStackTrace();

					} catch (NullEntityException e) {

						UtilActivities.createErrorDialog(CategoryActivity.this,
								e.toString());
						e.printStackTrace();

					} catch (FileSystemException e) {

						UtilActivities.createErrorDialog(CategoryActivity.this,
								e.toString());
						e.printStackTrace();
					}
				}
			};

			// Creo el dialogo de confirmacion
			UtilActivities.createConfirmDialog(this,
					R.string.txt_qst_delete_rss_channel, onClickListener);

			break;

		}

		return true;
	}

	/*
	 * Metodo que se dispara, cuando despues de que el usuario estaba en otra
	 * actividad, vuelve a esta (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		api = new API();

		// Si se mandaron parametros de otra actividad
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {

				generateListViewRSSChannels();
				intent.putExtra("changes_category", true);
			}
		}
	}

	@Override
	public void onBackPressed() {

		api.closeDatabaseConnection();
		api = null;

		if (intent.getBooleanExtra("changes_category", false) != false) {

			setResult(RESULT_OK, intent);
		}

		super.onBackPressed();
	}

	/**
	 * Metodo que crea y configura un Dialog para editar una categoria
	 */
	private void showDialogToEditCategory() {

		// Creo el dialog
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_edit_category);

		// Configuro el boton de crear RSSChannel
		Button btnCreate = (Button) dialog.findViewById(R.id.btn_edit_category);
		btnCreate.setOnClickListener(this);

		// Configuro el boton de cancelar
		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		// Asigno al textview del nombre de la categoria, el valor actual
		TextView textViewName = (TextView) dialog
				.findViewById(R.id.edt_name_category);
		textViewName.setText(currentCategory.getName());

		// Configuro atributos visuales del Dialog
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	/**
	 * Metodo para generar la lista de rsschannels en la actividad
	 */
	private void generateListViewRSSChannels() {

		rssChannelList = api.getListRSSChannelsInACategory(currentCategory
				.getId());

		ListView listView = (ListView) this
				.findViewById(R.id.list_view_list_rss_channels);
		listView.setAdapter(new MyAdapterListRSSChannels(this,
				R.layout.row_list_view_rss_channel, 0, rssChannelList));
		listView.setScrollContainer(false);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);
	}

	private void updateInformationActivity() {

		TextView txtNameCategory = (TextView) findViewById(R.id.txtv_name_category);
		txtNameCategory.setText(currentCategory.getName());
	}

}
