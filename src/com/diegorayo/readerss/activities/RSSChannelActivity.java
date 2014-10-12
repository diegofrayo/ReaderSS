package com.diegorayo.readerss.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 *          Actividad para mostrar un rsschannel con sus links y su informacion
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
	 * Lista de los RSSChannel de la categoria actual
	 */
	private List<RSSLink> rssLinksList;

	/**
	 * Categoria seleccionada por el usuario. Se utiliza en varios dialogs
	 */
	private RSSChannel currentRSSChannel;

	/**
	 * Utilizado para los metodos en donde se tienen que mostrar Dialogs
	 */
	private Dialog dialog;

	/**
	 * Spinner que va a contener la lista de categorias. Es utilizado en varios
	 * Dialogs
	 */
	private Spinner spinnerCategories;

	/**
	 * Utilizado para pasar parametros cuando el usuario se devuelva a la
	 * actividad anterior
	 */
	private Intent intent;

	/**
	 * Atributo que dice si el contenido de los RSSLinks se van a ver en el
	 * navegador, o en la aplicacion
	 */
	private boolean optionViewRSSLinksInBrowser;

	/**
	 * Barra de rpogreso utilizada al crear un rsschannel
	 */
	private ProgressDialog progressDialog;

	/**
	 * Manejador de respuesta de un hilo. Esto se ejecuta cuando el hilo
	 * termina, y ejecuta sentencias para manipular componentes de la interfaz
	 * de usuario
	 */
	@SuppressLint("HandlerLeak")
	private final Handler progressHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj != null) {

				if (msg.obj instanceof String) {

					UtilActivities.createErrorDialog(RSSChannelActivity.this,
							(String) msg.obj);
				} else {

					try {

						generateListViewRSSLinks();

					} catch (NullEntityException | SAXException | IOException
							| ParserConfigurationException
							| DataBaseTransactionException e) {

						e.printStackTrace();
					}

					updateInformationActivity();
				}

			}

			progressDialog.dismiss();
		}
	};

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

		UtilActivities.inflateHeaderApp(this);

		Intent it = getIntent();
		int idRSSChannel = it.getIntExtra("rss_channel_id", -1);

		if (idRSSChannel != -1) {

			api = new API();
			currentRSSChannel = api.getRSSChannelById(idRSSChannel);

			try {

				categoryList = (ArrayList<Category>) api.getListAllCategories();
				currentRSSChannel = api
						.getListRSSLinksOfRSSChannel(currentRSSChannel);
				rssLinksList = currentRSSChannel.getListRSSLinks();
				optionViewRSSLinksInBrowser = api
						.getConfigurationToViewRSSLinks();
				intent = new Intent();
				generateListViewRSSLinks();
				updateInformationActivity();

			} catch (NullEntityException | SAXException | IOException
					| ParserConfigurationException
					| DataBaseTransactionException e) {

				UtilActivities.createErrorDialog(RSSChannelActivity.this,
						e.toString());
				e.printStackTrace();

				// Vuelvo a la actividad anterior
				it = new Intent(this, RSSChannelActivity.class);
				it.putExtra("", -1);
				startActivity(it);
			}

		} else {

			// Vuelvo a la actividad anterior
			it = new Intent(this, RSSChannelActivity.class);
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

			case R.id.btn_edit_rss_channel:

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

				intent.putExtra("changes_rss_channel", true);
				updateInformationActivity();

				break;

			case R.id.btn_cancel:

				dialog.dismiss();
				break;

			}

		} catch (InvalidArgumentException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (DataBaseTransactionException e) {

			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();

		} catch (NullEntityException e) {

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
	 * Metodo que se utiliza cuando se selecciona un item de un menu que es
	 * desplegado despues de presionar un item de un listview durante varios
	 * segundos
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// Obtengo el item seleccionado
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {

		case R.id.item_menu_shared_link:

			RSSLink rssLinkSelected = rssLinksList.get((int) info.id);

			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			String shareBody = rssLinkSelected.getUrl();
			String shareSubject = rssLinkSelected.getTitle();

			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					shareSubject);
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

			startActivity(Intent.createChooser(sharingIntent, getResources()
					.getString(R.string.word_shared)));

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

		inflater.inflate(R.menu.menu_contextual_rss_channel_activity, menu);
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
		getMenuInflater().inflate(R.menu.menu_activity_rss_channel, menu);
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
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		if (optionViewRSSLinksInBrowser) {

			v.setBackgroundResource(R.color.color_white);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(rssLinksList.get(position).getUrl()));
			startActivity(intent);
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

		switch (item.getItemId()) {

		case R.id.btn_menu_edit_rss_channel:

			showDialogToEditRSSChannel();
			break;

		case R.id.btn_menu_update_rss_channel:

			if (UtilAPI.getConnectivityStatus(this) == true) {

				currentRSSChannel
						.setLastUpdate(UtilAPI.getCurrentDateAndTime());

				progressDialog = ProgressDialog.show(this, "",
						ApplicationContext
								.getStringResource(R.string.txt_load_data),
						true, true);

				new Thread(new Runnable() {
					@Override
					public void run() {

						Message msg = progressHandler.obtainMessage();

						try {

							currentRSSChannel = api
									.downloadXMLFileAndGetListRSSLinksOfRSSChannel(currentRSSChannel);
							rssLinksList = currentRSSChannel.getListRSSLinks();
							intent.putExtra("changes_rss_channel",
									currentRSSChannel.getCategory().getId());
							msg.obj = currentRSSChannel;

						} catch (NullEntityException e) {

							msg.obj = e.toString();
							e.printStackTrace();

						} catch (SAXException e) {

							msg.obj = e.getMessage();
							e.printStackTrace();

						} catch (IOException e) {

							msg.obj = e.getMessage();
							e.printStackTrace();

						} catch (ParserConfigurationException e) {

							msg.obj = e.getMessage();
							e.printStackTrace();

						} catch (URLDownloadFileException e) {

							msg.obj = e.toString();
							e.printStackTrace();

						} catch (InvalidArgumentException e) {

							msg.obj = e.toString();
							e.printStackTrace();

						} catch (DataBaseTransactionException e) {

							msg.obj = e.toString();
							e.printStackTrace();
						}

						progressHandler.sendMessage(msg);
					}
				}).start();

			} else {

				UtilActivities
						.createErrorDialog(
								this,
								ApplicationContext
										.getStringResource(R.string.error_no_internet_connection));
			}

			break;

		case R.id.btn_menu_delete_rss_channel:

			// Parametro que contiene la funcion OnClick para crear el
			// dialogo de confirmacion
			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					try {

						api.deleteRSSChannel(currentRSSChannel.getId());
						intent.putExtra("changes_rss_channel", true);
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

			// Creo el dialogo de confirmacion
			UtilActivities.createConfirmDialog(this,
					R.string.txt_qst_delete_rss_channel, onClickListener);

			break;

		}

		return true;
	}

	@Override
	public void onBackPressed() {

		api.closeDatabaseConnection();
		api = null;

		if (intent.getBooleanExtra("changes_rss_channel", false) != false) {

			setResult(RESULT_OK, intent);
		}

		super.onBackPressed();
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
	 * Metodo para generar la lista de rsschannels en la actividad
	 * 
	 * @throws DataBaseTransactionException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws NullEntityException
	 */
	private void generateListViewRSSLinks() throws NullEntityException,
			SAXException, IOException, ParserConfigurationException,
			DataBaseTransactionException {

		rssLinksList = currentRSSChannel.getListRSSLinks();

		ListView listView = (ListView) this
				.findViewById(R.id.list_view_list_rss_links);
		listView.setAdapter(new MyAdapterListRSSLinks(this,
				R.layout.row_list_view_rss_links, 0, rssLinksList));
		listView.setScrollContainer(false);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);

	}

	/**
	 * Metodo para actualizar informacion en la actividad
	 */
	private void updateInformationActivity() {

		TextView txt = (TextView) findViewById(R.id.txt_name_rss_channel);
		txt.setText(currentRSSChannel.getName() + " ");

		txt = (TextView) findViewById(R.id.txt_url_rss_channel);
		txt.setText("Website: " + currentRSSChannel.getWebsite() + " ");

		txt = (TextView) findViewById(R.id.txt_category_rss_channel);
		txt.setText("Category: " + currentRSSChannel.getCategory().getName()
				+ " ");

		txt = (TextView) findViewById(R.id.txt_last_update);
		txt.setText("Last Update: " + currentRSSChannel.getLastUpdate() + " ");

		txt = (TextView) findViewById(R.id.txt_posts_rss_channel);
		txt.setText("Posts (" + rssLinksList.size() + ")");
	}

}
