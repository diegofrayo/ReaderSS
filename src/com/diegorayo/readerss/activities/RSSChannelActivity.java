package com.diegorayo.readerss.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.adapters.MyAdapterListRSSLinks;
import com.diegorayo.readerss.api.API;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.FileSystemException;
import com.diegorayo.readerss.exceptions.InvalidArgumentException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Actividad que muestra la informacion y el contenido de un RSSChannel
 */
public class RSSChannelActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * Clase que provee todos los metodos y funcionalidades de la aplicacion
	 */
	private API api;

	/**
	 * Lista de categorias del usuario. Utilizadas en el spinner
	 */
	private ArrayList<Category> categoryList;

	/**
	 * Es el RSSChannel perteneciente a la actividad
	 */
	private RSSChannel currentRSSChannel;

	/**
	 * Utilizado para los metodos en donde se tienen que mostrar Dialogs
	 */
	private Dialog dialog;

	/**
	 * Utilizada para ingresarle datos cuando se elimina o actualiza el
	 * RSSChannel. Despues este intent es devuelto a la actividad anterior
	 */
	private Intent intent;

	/**
	 * Atributo que dice si el contenido de los RSSLinks se van a ver en el
	 * navegador, o en la aplicacion
	 */
	private boolean optionViewRSSLinksInBrowser;

	/**
	 * La Lista de RSSLinks del RSSchannel
	 */
	private List<RSSLink> rssLinksList;

	/**
	 * Spinner que va a contener la lista de categorias. Es utilizado en varios
	 * Dialogs
	 */
	private Spinner spinnerCategories;

	/*
	 * Metodo que se dispara cuando se pulsa el boton "atras", y se cierra esta
	 * actividad (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		api.closeDatabaseConnection();
		api = null;

		// Si se editó el RSSChannel, se mandan datos a la actividad anterior
		if (intent.getIntExtra("category_parent_rss_channel_id", -1) != -1) {

			setResult(RESULT_OK, intent);
		}

		super.onBackPressed();
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

			case R.id.btn_edit_rss_channel:

				int oldIdCategory = currentRSSChannel.getCategory().getId();

				EditText txtNameRSSChannel = (EditText) dialog
						.findViewById(R.id.edt_name_rss_channel);

				Category categorySelect = (Category) spinnerCategories
						.getSelectedItem();

				currentRSSChannel = api.editRSSChannel(currentRSSChannel
						.getId(), txtNameRSSChannel.getText().toString(),
						categorySelect.getId());

				UtilActivities.createSuccessDialog(RSSChannelActivity.this,
						R.string.success_edit_rss_channel);
				dialog.dismiss();

				// Si se editó la categoria del RSSChannel
				if (oldIdCategory != currentRSSChannel.getCategory().getId()) {

					intent.putExtra("category_parent_rss_channel_id_old",
							oldIdCategory);
				}

				intent.putExtra("category_parent_rss_channel_id",
						currentRSSChannel.getCategory().getId());
				break;

			case R.id.btn_cancel:

				dialog.dismiss();
				break;
			}

		} catch (NullEntityException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (InvalidArgumentException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (DataBaseTransactionException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (FileSystemException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		}
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
		getMenuInflater().inflate(R.menu.activity_rss_channel, menu);
		return true;
	}

	/*
	 * Metodo que se utiliza cuando se selecciona un RssLink. Sucede cuando se
	 * seleccion un item de un listview (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@SuppressLint("ResourceAsColor")
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		if (optionViewRSSLinksInBrowser) {

			v.setBackgroundResource(R.color.color_white);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(rssLinksList.get(position).getUrl()));
			startActivity(intent);

		} else {

			// Se lanza una actividad de la aplicacion
		}
	}

	/*
	 * Metodo que se utiliza cuando se selecciona algun item del menu contextual
	 * de la actividad (non-Javadoc) (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		try {

			switch (item.getItemId()) {

			case R.id.btn_update_rss_channel:

				if (UtilAPI.getConnectivityStatus(this) == true) {

					currentRSSChannel.setLastUpdate(UtilAPI
							.getCurrentDateAndTime());
					rssLinksList = api
							.downloadXMLFileAndGetListRSSLinksOfRSSChannel(currentRSSChannel);
					updateInformationActivity();

					intent.putExtra("category_parent_rss_channel_id",
							currentRSSChannel.getCategory().getId());
				} else {

					UtilActivities
							.createErrorDialog(
									this,
									ApplicationContext
											.getStringResource(R.string.error_no_internet_connection));
				}

				break;

			case R.id.btn_menu_edit_rss_channel:

				showDialogToEditRSSChannel();
				break;

			case R.id.btn_menu_delete_rss_channel:

				DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						try {

							api.deleteRSSChannel(currentRSSChannel.getId());

							intent.putExtra("category_parent_rss_channel_id",
									currentRSSChannel.getCategory().getId());

							onBackPressed();

						} catch (DataBaseTransactionException e) {

							UtilActivities.createErrorDialog(
									RSSChannelActivity.this, e.toString());
							e.printStackTrace();

						} catch (NullEntityException e) {

							UtilActivities.createErrorDialog(
									RSSChannelActivity.this, e.toString());
							e.printStackTrace();

						} catch (FileSystemException e) {

							UtilActivities.createErrorDialog(
									RSSChannelActivity.this, e.toString());
							e.printStackTrace();
						}
					}
				};

				UtilActivities.createConfirmDialog(this,
						R.string.txt_qst_delete_rss_channel, onClickListener);

				break;

			case R.id.btn_submenu_view_in_app:

				api.editConfigurationToViewRSSLinks(false);
				optionViewRSSLinksInBrowser = api
						.getConfigurationToViewRSSLinks();
				break;

			case R.id.btn_submenu_view_in_browser:

				api.editConfigurationToViewRSSLinks(true);
				optionViewRSSLinksInBrowser = api
						.getConfigurationToViewRSSLinks();
				break;
			}

		} catch (NullEntityException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		} catch (SAXException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.getMessage());
			e.printStackTrace();
		} catch (URLDownloadFileException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.getMessage());
			e.printStackTrace();
		} catch (InvalidArgumentException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		} catch (DataBaseTransactionException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		}

		return true;
	}

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
		setContentView(R.layout.activity_rss_channel);

		Intent it = getIntent();
		int idRSSChannel = it.getIntExtra("rss_channel_id", -1);

		// Si mandaron el ID del RSSChannel de la actividad anterior
		if (idRSSChannel != -1) {

			api = new API(this);
			intent = new Intent();

			try {

				currentRSSChannel = api.getRSSChannelById(idRSSChannel);
				categoryList = (ArrayList<Category>) api.getListAllCategories();
				rssLinksList = api.getListRSSLinksOfRSSChannel(
						currentRSSChannel).getListRSSLinks();
				optionViewRSSLinksInBrowser = api
						.getConfigurationToViewRSSLinks();
				currentRSSChannel.setDateLastRSSLink(rssLinksList.get(0)
						.getDate());

			} catch (NullEntityException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.toString());
				e.printStackTrace();

			} catch (SAXException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.getMessage());
				e.printStackTrace();

			} catch (IOException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.getMessage());
				e.printStackTrace();

			} catch (ParserConfigurationException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.getMessage());
				e.printStackTrace();

			} catch (DataBaseTransactionException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.toString());
				e.printStackTrace();
			}

			updateInformationActivity();

		} else {

			// Se devuelve a la actividad anterior
			Intent backToRSSChannelActivity = new Intent(this,
					RSSChannelActivity.class);
			backToRSSChannelActivity.putExtra("", -1);
			startActivity(backToRSSChannelActivity);
		}
	}

	/**
	 * Metodo utilizado para crear el dialog, para editar el RSSChannel
	 */
	private void showDialogToEditRSSChannel() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_edit_rss_channel);

		spinnerCategories = (Spinner) dialog
				.findViewById(R.id.spnListCategories);
		UtilActivities.insertCategoriesInSpinner(this, categoryList,
				spinnerCategories);

		Button btnEdit = (Button) dialog
				.findViewById(R.id.btn_edit_rss_channel);
		btnEdit.setOnClickListener(this);

		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		TextView txtNameRSSChannel = (TextView) dialog
				.findViewById(R.id.edt_name_rss_channel);
		txtNameRSSChannel.setText(currentRSSChannel.getName());

		int currentCategoryRSSChannel = currentRSSChannel.getCategory().getId();

		// Selecciona la categoria del RSSChannel en el spinner
		for (int i = 0; i < categoryList.size(); i++) {

			int currentCategoryId = categoryList.get(i).getId();

			if (currentCategoryId == currentCategoryRSSChannel) {

				spinnerCategories.setSelection(i);
				break;
			}
		}

		// Configuracion para mostrar el dialog
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	/**
	 * Metodo para actualizar informacion en la actividad
	 */
	private void updateInformationActivity() {

		// LLeno la lista de links rss
		ListView listView = (ListView) findViewById(R.id.listLinksRSSChannel);
		listView.setAdapter(null);
		listView.setAdapter(new MyAdapterListRSSLinks(this,
				R.layout.row_list_view_rss_links, 0, rssLinksList));
		listView.setOnItemClickListener(this);

		// LLeno la informacion basica del RSSChannel
		// TextView textView = (TextView) findViewById(R.id.txtLastUpdate);
		// textView.setText("Last Update: " + currentRSSChannel.getLastUpdate()
		// + " ");
	}

}
