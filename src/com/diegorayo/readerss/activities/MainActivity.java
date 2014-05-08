package com.diegorayo.readerss.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.adapters.CustomTextView;
import com.diegorayo.readerss.adapters.MyAdapterListRSSChannel;
import com.diegorayo.readerss.api.API;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.FileSystemException;
import com.diegorayo.readerss.exceptions.InvalidArgumentException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Actividad principal (Home)
 */
public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * Clase que provee todos los metodos y funcionalidades de la aplicacion
	 */
	private API api;

	/**
	 * Lista de categorias del usuario
	 */
	private ArrayList<Category> categoryList;

	/**
	 * Categoria seleccionada por el usuario. Se utiliza en varios dialogs
	 */
	private Category categorySelected;

	/**
	 * Utilizado para los metodos en donde se tienen que mostrar Dialogs
	 */
	private Dialog dialog;

	// Ids de elementos creados en metodos java, y no en xml
	private final int ID_BTN_COLLAPSE = 100;

	private final int ID_BTN_DELETE_CATEGORY = 400;
	private final int ID_BTN_EDIT_CATEGORY = 300;
	private final int ID_TEXTVIEW_CATEGORY_NAME = 200;

	/**
	 * Spinner que va a contener la lista de categorias. Es utilizado en varios
	 * Dialogs
	 */
	private Spinner spinnerCategories;

	/*
	 * Metodo cuando se preciona un boton (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		try {

			switch (v.getId()) {

			case R.id.btn_create_new_rss_channel:

				EditText txtNameRSSChannel = (EditText) dialog
						.findViewById(R.id.edt_name_rss_channel);
				EditText txtURL_RSSChannel = (EditText) dialog
						.findViewById(R.id.edt_url_rss_channel);

				Category categorySelect = (Category) spinnerCategories
						.getSelectedItem();

				RSSChannel newRSSChannel = api.createRSSChannel(
						txtNameRSSChannel.getText().toString(),
						txtURL_RSSChannel.getText().toString(),
						categorySelect.getId());

				UtilActivities.createSuccessDialog(this,
						R.string.success_new_rss_channel);
				dialog.dismiss();
				updateCategories(newRSSChannel.getCategory(), "RSS");

				break;

			case R.id.btn_create_new_category:

				EditText editTextNameCategory = (EditText) dialog
						.findViewById(R.id.edt_name_category);

				Category newCategory = api.createCategory(editTextNameCategory
						.getText().toString());

				UtilActivities.createSuccessDialog(this,
						R.string.success_new_category);
				dialog.dismiss();

				categoryList = (ArrayList<Category>) api.getListAllCategories();
				updateCategories(newCategory, "C");

				break;

			case R.id.btn_edit_category:

				editTextNameCategory = (EditText) dialog
						.findViewById(R.id.edt_name_category);
				String textNameCategory = editTextNameCategory.getText()
						.toString();

				// Si realmente SI se modificó el nombre de la categoria
				if (textNameCategory.equals(categorySelected.getName()) == false) {

					Category editCategory = api.editCategory(
							categorySelected.getId(), textNameCategory);

					UtilActivities.createSuccessDialog(MainActivity.this,
							R.string.success_edit_category);
					dialog.dismiss();

					categoryList = (ArrayList<Category>) api
							.getListAllCategories();
					updateCategories(editCategory, "E");
				}

				break;

			case R.id.btn_cancel:

				dialog.dismiss();
				break;

			// Cuando se pulsa el boton collapse de algun container de una
			// categoria
			case ID_BTN_COLLAPSE:

				collapseListView(v);
				break;

			// Hace lo mismo que el boton collapse
			case ID_TEXTVIEW_CATEGORY_NAME:

				collapseListView(v);
				break;

			case ID_BTN_DELETE_CATEGORY:

				// Obtengo el container de la categoria que se va a eliminar
				LinearLayout layoutParent = (LinearLayout) v.getParent()
						.getParent().getParent();
				final int idCategory = layoutParent.getId();

				// Parametro que contiene la funcion OnClick para crear el
				// dialogo de confirmacion
				DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						try {

							Category deleteCategory = api
									.getCategoryById(idCategory);
							api.deleteCategory(idCategory);
							updateCategories(deleteCategory, "D");

						} catch (DataBaseTransactionException e) {

							UtilActivities.createErrorDialog(MainActivity.this,
									e.toString());
							e.printStackTrace();

						} catch (NullEntityException e) {

							UtilActivities.createErrorDialog(MainActivity.this,
									e.toString());
							e.printStackTrace();

						} catch (FileSystemException e) {

							UtilActivities.createErrorDialog(MainActivity.this,
									e.toString());
							e.printStackTrace();
						}
					}
				};

				// Creo el dialogo de confirmacion
				UtilActivities.createConfirmDialog(this,
						R.string.txt_qst_delete_rss_channel, onClickListener);

				break;

			case ID_BTN_EDIT_CATEGORY:

				layoutParent = (LinearLayout) v.getParent().getParent()
						.getParent();
				categorySelected = api.getCategoryById(layoutParent.getId());
				showDialogToEditCategory();

				break;
			}

		} catch (InvalidArgumentException e) {

			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();

		} catch (DataBaseTransactionException e) {

			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();

		} catch (NullEntityException e) {

			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();

		} catch (FileSystemException e) {

			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();
		}
	}

	/*
	 * Metodo que se utiliza cuando se selecciona un item de un listview durante
	 * varios segundos
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// Obtengo el item seleccionado
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {

		case R.id.btn_menu_delete_rss_channel:

			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					try {

						api.deleteRSSChannel((int) info.id);

					} catch (DataBaseTransactionException e) {

						UtilActivities.createErrorDialog(MainActivity.this,
								e.toString());
						e.printStackTrace();
					} catch (NullEntityException e) {

						UtilActivities.createErrorDialog(MainActivity.this,
								e.toString());
						e.printStackTrace();
					} catch (FileSystemException e) {

						e.printStackTrace();
					}
				}
			};

			UtilActivities.createConfirmDialog(this,
					R.string.txt_qst_delete_category, onClickListener);

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

		inflater.inflate(R.menu.menu_contextual_main_activity, menu);
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
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

		case R.id.btn_add_category:

			showDialogToCreateCategory();
			break;

		case R.id.btn_add_rss_channel:

			if (UtilAPI.getConnectivityStatus(this) == true) {

				showDialogToCreateRSSChannel();
			} else {

				UtilActivities.createErrorDialog(this, ApplicationContext
						.getStringResource(R.string.error_no_internet_connection));
			}

			break;

		case R.id.btn_submenu_view_in_app:

			api.editConfigurationToViewRSSLinks(false);
			break;

		case R.id.btn_submenu_view_in_browser:

			api.editConfigurationToViewRSSLinks(true);
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

		api = new API(this);

		// Si se mandaron parametros de otra actividad
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {

				// Lo que hace es actualizar el container de las 2 categorias
				// actualizadas

				// Si es diferente a -1, es porque se editó la categoria del
				// RSSchannel de la actividad que se acaba de cerrar
				int idCategoryParentRSSChannel = data.getIntExtra(
						"category_parent_rss_channel_id", -1);

				if (idCategoryParentRSSChannel != -1) {

					updateCategories(
							api.getCategoryById(idCategoryParentRSSChannel),
							"RSS");
				}

				// Si es diferente a -1, es porque se editó la categoria del
				// RSSchannel de la actividad que se acaba de cerrar
				int idCategoryParentRSSChannelOld = data.getIntExtra(
						"category_parent_rss_channel_id_old", -1);

				if (idCategoryParentRSSChannelOld != -1) {
					updateCategories(
							api.getCategoryById(idCategoryParentRSSChannelOld),
							"RSS");
				}
			}
		}
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
		setContentView(R.layout.activity_main);

		api = new API(this);

		try {

			// Solo se debe ejecutar la primera vez que se ejecuta la aplicacion
			api.configureApp();

		} catch (NullEntityException e) {

			e.printStackTrace();
		} catch (DataBaseTransactionException e) {

			e.printStackTrace();
		} catch (InvalidArgumentException e) {

			e.printStackTrace();
		} catch (FileSystemException e) {

			e.printStackTrace();
		}

		// Configuro el username
		updateUsername();

		// Configuro y despliego las categorias
		categoryList = (ArrayList<Category>) api.getListAllCategories();
		showListCategories();
	}

	/**
	 * Metodo utilizado para esconder/mostrar un ListView que contiene los
	 * RSSChannel de una categoria
	 * 
	 * @param v
	 */
	private void collapseListView(View v) {

		LinearLayout layoutParent = (LinearLayout) v.getParent().getParent();
		ListView listViewAnimate = (ListView) layoutParent.getChildAt(1);
		int visibilityList = listViewAnimate.getVisibility();

		if (visibilityList == View.VISIBLE) {

			layoutParent.getChildAt(1).setVisibility(View.GONE);
		} else {

			layoutParent.getChildAt(1).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * @param category
	 * @param linearLayoutParentContainer
	 */
	private void createCategoryContainer(Category category,
			LinearLayout linearLayoutParentContainer) {

		// Margenes del container respecto a su padre
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
						.getDisplayMetrics());

		// El tamaño de los iconos eliminar/editar
		int heightAndWidthIcons = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
						.getDisplayMetrics());

		List<RSSChannel> currentList = api
				.getListRSSChannelsInACategory(category.getId());

		// Layout container params
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
				heightAndWidthIcons, heightAndWidthIcons);

		// Layout container
		LinearLayout linearLayoutContainer = new LinearLayout(this);
		layoutParams.setMargins(0, margin * 3, 0, margin * 3);
		linearLayoutContainer.setLayoutParams(layoutParams);
		linearLayoutContainer.setOrientation(LinearLayout.VERTICAL);
		linearLayoutContainer.setId(category.getId());
		linearLayoutContainer.setBackgroundResource(R.drawable.bg_containers_shadow);

		// Para que la categoria default quede de primera
		if (category.getName().equals("default")) {

			linearLayoutParentContainer.addView(linearLayoutContainer, 0);
		} else {

			linearLayoutParentContainer.addView(linearLayoutContainer,
					linearLayoutParentContainer.getChildCount());
		}

		{
			// Layout title
			RelativeLayout relativeLayoutTitle = new RelativeLayout(this);
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			relativeLayoutTitle.setLayoutParams(layoutParams);
			relativeLayoutTitle
					.setBackgroundResource(R.color.color_bg_titles);

			{
				// Childrens layout title
				// Button collapse
				Button btCollapse = new Button(this);
				relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				relativeLayoutParams.setMargins(margin * 2, 0, margin * 2, 0);
				btCollapse.setPadding(margin, 0, margin, 0);
				btCollapse.setLayoutParams(relativeLayoutParams);
				btCollapse.setBackgroundResource(R.drawable.ic_collapse);
				btCollapse.setId(ID_BTN_COLLAPSE);
				btCollapse.setOnClickListener(this);

				// Text view name category
				CustomTextView textViewNameCategory = new CustomTextView(this);
				relativeLayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF,
						btCollapse.getId());
				relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				relativeLayoutParams.addRule(RelativeLayout.LEFT_OF, 99);
				textViewNameCategory.setLayoutParams(relativeLayoutParams);
				textViewNameCategory.setText(category.getName());
				textViewNameCategory
						.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				textViewNameCategory.setTextColor(getResources().getColor(
						R.color.color_white));
				textViewNameCategory.setClickable(true);
				textViewNameCategory.setId(ID_TEXTVIEW_CATEGORY_NAME);
				textViewNameCategory.setOnClickListener(this);

				// Linear layout of the buttons
				LinearLayout linearLayoutButtons = new LinearLayout(this);
				relativeLayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				linearLayoutButtons.setLayoutParams(relativeLayoutParams);
				linearLayoutButtons.setOrientation(LinearLayout.HORIZONTAL);

				{
					// Button delete
					Button btDeleteCategory = new Button(this);
					layoutParams = new LinearLayout.LayoutParams(
							heightAndWidthIcons, heightAndWidthIcons);
					layoutParams.setMargins(margin * 3, 0, margin * 2, 0);
					btDeleteCategory.setPadding(margin, 0, margin, 0);
					btDeleteCategory.setLayoutParams(layoutParams);
					btDeleteCategory
							.setBackgroundResource(R.drawable.ic_delete);
					btDeleteCategory.setOnClickListener(this);
					btDeleteCategory.setId(ID_BTN_DELETE_CATEGORY);

					// Button edit
					Button btEditCategory = new Button(this);
					layoutParams = new LinearLayout.LayoutParams(
							heightAndWidthIcons, heightAndWidthIcons);
					btEditCategory.setPadding(margin * 2, 0, margin * 2, 0);
					btEditCategory.setLayoutParams(layoutParams);
					btEditCategory.setBackgroundResource(R.drawable.ic_edit);
					btEditCategory.setOnClickListener(this);
					btEditCategory.setId(ID_BTN_EDIT_CATEGORY);

					if (category.getName().equals("default") == false) {

						linearLayoutButtons.addView(btEditCategory);
						linearLayoutButtons.addView(btDeleteCategory);
					}
				}

				relativeLayoutTitle.addView(btCollapse);
				relativeLayoutTitle.addView(textViewNameCategory);
				relativeLayoutTitle.addView(linearLayoutButtons);
			}

			// List view rss channels
			ListView listView = new ListView(this);
			listView.setAdapter(new MyAdapterListRSSChannel(this,
					R.layout.row_list_view_rss_channel, 0, currentList));
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, heightAndWidthIcons * 8);
			listView.setScrollContainer(false);
			listView.setLayoutParams(layoutParams);
			listView.setOnItemClickListener(this);
			listView.setId(category.getId());
			listView.setVisibility(View.GONE);
			registerForContextMenu(listView);

			// Agrega el titulo de la categoria, y el listview con la lista de
			// RSSChannels
			linearLayoutContainer.addView(relativeLayoutTitle);
			linearLayoutContainer.addView(listView);
		}
	}

	/**
	 * Metodo para eliminar un container perteneciente a una categoria. Se
	 * utiliza cuando se elimina una categoria
	 * 
	 * @param linearLayoutParent
	 * @param category
	 */
	private void deleteCategoryContainer(Category category,
			LinearLayout linearLayoutParent) {

		int numberChilds = linearLayoutParent.getChildCount();

		for (int i = 0; i < numberChilds; i++) {

			LinearLayout currentChild = (LinearLayout) linearLayoutParent
					.getChildAt(i);

			if (currentChild.getId() == category.getId()) {

				linearLayoutParent.removeView(currentChild);
				break;
			}
		}
	}

	/**
	 * Metodo que crea y configura un Dialog para crear una categoria
	 */
	private void showDialogToCreateCategory() {

		// Creo el dialog
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_category);

		// Configuro el boton de crear RSSChannel
		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_category);
		btnCreate.setOnClickListener(this);

		// Configuro el boton de cancelar
		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		// Configuro atributos visuales del Dialog
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	/**
	 * Metodo que crea y configura un Dialog para crear un RSSChannel
	 */
	private void showDialogToCreateRSSChannel() {

		// Creo el dialog
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_rss_channel);

		// Le asigno un spinner con el lista de las categorias
		spinnerCategories = (Spinner) dialog
				.findViewById(R.id.spnListCategories);
		UtilActivities.insertCategoriesInSpinner(this, categoryList,
				spinnerCategories);

		// Configuro el boton de crear RSSChannel
		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_rss_channel);
		btnCreate.setOnClickListener(this);

		// Configuro el boton de cancelar
		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		// Configuro atributos visuales del Dialog
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
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
		textViewName.setText(categorySelected.getName());

		// Configuro atributos visuales del Dialog
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	/**
	 * Despliega en pantalla todas las categorias con su contenido
	 */
	private void showListCategories() {

		LinearLayout linearLayoutParent = (LinearLayout) findViewById(R.id.layoutParentCategories);

		for (Category category : categoryList) {

			createCategoryContainer(category, linearLayoutParent);
		}
	}

	/**
	 * Metodo para actualizar el container principal, que contiene los
	 * subcontainer pertenecientes a cada categorias
	 * 
	 * @param category
	 * @param operation
	 */
	private void updateCategories(Category category, String operation) {

		LinearLayout linearLayoutParent = (LinearLayout) findViewById(R.id.layoutParentCategories);
		int numberChilds = linearLayoutParent.getChildCount();

		// Create category action
		if (operation.equals("C")) {

			// Se agrega un subcontainer, porque se creó una nueva categoria
			createCategoryContainer(category, linearLayoutParent);
			return;
		}

		// Edit category action
		if (operation.equals("E")) {

			for (int i = 0; i < numberChilds; i++) {

				LinearLayout currentChild = (LinearLayout) linearLayoutParent
						.getChildAt(i);

				if (currentChild.getId() == category.getId()) {

					// Se edita el titulo de un subcontainer, porque se editó
					// una categoria
					CustomTextView textCategoryTitle = (CustomTextView) ((RelativeLayout) currentChild
							.getChildAt(0)).getChildAt(1);
					textCategoryTitle.setText(category.getName());
				}
			}

			return;
		}

		// Delete category action
		if (operation.equals("D")) {

			// Se elimina un subcontainer porque se eliminó una categoria
			deleteCategoryContainer(category, linearLayoutParent);
			return;
		}

		// Insert/delete/edit rss channel action
		if (operation.equals("RSS")) {

			// Borro el container de la categoria y la vuelvo a crear con los
			// datos actualizados
			deleteCategoryContainer(category, linearLayoutParent);
			createCategoryContainer(category, linearLayoutParent);
		}
	}

	/**
	 * Metodo para poner en pantalla, un mensaje de bienvenida con el nombre de
	 * usuario del telefono
	 */
	private void updateUsername() {

		TextView textViewWelcomeUser = (TextView) findViewById(R.id.word_welcome_user);
		textViewWelcomeUser.setText(api.getUsernameGoogle());
	}

}
