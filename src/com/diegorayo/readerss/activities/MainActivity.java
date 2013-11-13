package com.diegorayo.readerss.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.diegorayo.readerss.api.RSSReaderAPI;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.ArgumentInvalidException;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * 
	 */
	private RSSReaderAPI API;

	/**
	 * 
	 */
	private ArrayList<Category> categoryList;

	/**
	 * 
	 */
	private Category categorySelected;

	/**
	 * 
	 */
	private Dialog dialog;

	/**
	 * 
	 */
	private Spinner spinnerCategories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		API = new RSSReaderAPI(this);

		// Si es la primera vez que abre la aplicacion
		if (API.getOpenFirstApp() == true) {

			try {

				API.createCategory("default");
				API.configurationApp();
				API.editOpenFirstApp();
				createDialogToInsertUsername();

			} catch (EntityNullException e) {
				e.printStackTrace();
			} catch (DataBaseTransactionException e) {
				e.printStackTrace();
			} catch (ArgumentInvalidException e) {
				e.printStackTrace();
			}
		}

		// Configuro el username
		updateUsername();

		// Compruebo que canales se han actualizado
		if (UtilAPI.getConnectivityStatus(this) == true) {
			// API.checkRSSChannelsModified();
		}

		// Configuro y despliego las categorias
		categoryList = (ArrayList<Category>) API.getListAllCategories();
		showListCategories();

	}

	private void updateUsername() {

		String username = " " + API.getUsername() + " ";
		TextView textViewWelcomeUser = (TextView) findViewById(R.id.txt_welcome_user);
		textViewWelcomeUser.setText(username);
	}

	/**
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
	 */
	private void showDialogToCreateRSSChannel() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_rss_channel);

		spinnerCategories = (Spinner) dialog
				.findViewById(R.id.spnListCategories);

		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_rss_channel);
		btnCreate.setOnClickListener(this);

		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		UtilActivities.insertCategoriesInSpinner(this, categoryList,
				spinnerCategories);
		dialog.show();
	}

	/**
	 * 
	 */
	private void showDialogToCreateCategory() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_category);

		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_category);
		btnCreate.setOnClickListener(this);

		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	/**
	 * 
	 */
	private void showDialogToEditCategory() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_edit_category);

		Button btnCreate = (Button) dialog.findViewById(R.id.btn_edit_category);
		btnCreate.setOnClickListener(this);

		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);

		TextView textViewName = (TextView) dialog
				.findViewById(R.id.edt_name_category);
		textViewName.setText(categorySelected.getName());

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	/**
	 * 
	 */
	private void createDialogToInsertUsername() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_insert_username);
		dialog.setCancelable(false);

		Button btnInsert = (Button) dialog
				.findViewById(R.id.btn_insert_username);
		btnInsert.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	/**
	 * 
	 */
	private void createDialogToEditUsername() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_insert_username);

		Button btnEdit = (Button) dialog.findViewById(R.id.btn_insert_username);
		btnEdit.setOnClickListener(this);
		btnEdit.setText(R.string.txt_edit);

		EditText editTextUserName = (EditText) dialog
				.findViewById(R.id.edt_new_username);
		editTextUserName.setText(API.getUsername());

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	/**
	 * 
	 * @param linearLayoutParent
	 * @param category
	 */
	private void deleteContainerInLayoutCategories(
			LinearLayout linearLayoutParent, Category category) {

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

	private void updateCategories(Category category, String operation) {

		LinearLayout linearLayoutParent = (LinearLayout) findViewById(R.id.layoutParentCategories);
		int numberChilds = linearLayoutParent.getChildCount();

		// Create category action
		if (operation.equals("C")) {
			createContainerCategory(category, linearLayoutParent);
			return;
		}

		// Edit category action
		if (operation.equals("E")) {

			for (int i = 0; i < numberChilds; i++) {

				LinearLayout currentChild = (LinearLayout) linearLayoutParent
						.getChildAt(i);
				if (currentChild.getId() == category.getId()) {

					CustomTextView textCategoryTitle = (CustomTextView) ((RelativeLayout) currentChild
							.getChildAt(0)).getChildAt(1);
					textCategoryTitle.setText(category.getName());
				}
			}
			return;
		}

		// Delete category action
		if (operation.equals("D")) {

			deleteContainerInLayoutCategories(linearLayoutParent, category);
			return;
		}

		// Insert/delete/edit rss channel action
		if (operation.equals("RSS")) {

			// Borro el container de la categoria y la vuelvo a crear con los
			// datos actualizados
			deleteContainerInLayoutCategories(linearLayoutParent, category);
			createContainerCategory(category, linearLayoutParent);
		}

	}

	/**
	 * 
	 * @param category
	 * @param linearLayoutParentContainer
	 */
	private void createContainerCategory(Category category,
			LinearLayout linearLayoutParentContainer) {

		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
						.getDisplayMetrics());

		int heightAndWidthIcons = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
						.getDisplayMetrics());

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		List<RSSChannel> currentList = API
				.getListRSSChannelsInACategory(category.getId());

		// Layout container
		LinearLayout linearLayoutContainer = new LinearLayout(this);
		layoutParams.setMargins(0, margin * 3, 0, margin * 3);
		linearLayoutContainer.setLayoutParams(layoutParams);
		linearLayoutContainer.setOrientation(LinearLayout.VERTICAL);
		linearLayoutContainer.setId(category.getId());
		linearLayoutContainer.setBackgroundResource(R.drawable.shadow);
		if (category.getId() == 1) {
			linearLayoutParentContainer.addView(linearLayoutContainer, 0);
		} else {
			linearLayoutParentContainer.addView(linearLayoutContainer,
					linearLayoutParentContainer.getChildCount());
		}

		// Layout title
		RelativeLayout relativeLayoutTitle = new RelativeLayout(this);
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		relativeLayoutTitle.setLayoutParams(layoutParams);
		relativeLayoutTitle
				.setBackgroundResource(R.drawable.bg_title_categories);

		// Childrens layout title
		// Button collapse
		Button btCollapse = new Button(this);
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
				heightAndWidthIcons, heightAndWidthIcons);
		relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		relativeLayoutParams.setMargins(margin * 2, 0, margin * 2, 0);
		btCollapse.setPadding(margin, 0, margin, 0);
		btCollapse.setLayoutParams(relativeLayoutParams);
		btCollapse.setBackgroundResource(R.drawable.ic_collapse);
		btCollapse.setId(100);
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
		textViewNameCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		textViewNameCategory.setTextColor(getResources().getColor(
				R.color.color_white));
		textViewNameCategory.setClickable(true);
		textViewNameCategory.setId(200);
		textViewNameCategory.setOnClickListener(this);

		// Linear layout of the buttons
		LinearLayout linearLayoutButtons = new LinearLayout(this);
		relativeLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		linearLayoutButtons.setLayoutParams(relativeLayoutParams);
		linearLayoutButtons.setOrientation(LinearLayout.HORIZONTAL);

		// Button delete
		Button btDeleteCategory = new Button(this);
		layoutParams = new LinearLayout.LayoutParams(heightAndWidthIcons,
				heightAndWidthIcons);
		layoutParams.setMargins(margin * 3, 0, margin * 2, 0);
		btDeleteCategory.setPadding(margin, 0, margin, 0);
		btDeleteCategory.setLayoutParams(layoutParams);
		btDeleteCategory.setBackgroundResource(R.drawable.ic_delete);
		btDeleteCategory.setOnClickListener(this);
		btDeleteCategory.setId(300);

		// Button edit
		Button btEditCategory = new Button(this);
		layoutParams = new LinearLayout.LayoutParams(heightAndWidthIcons,
				heightAndWidthIcons);
		btEditCategory.setPadding(margin * 2, 0, margin * 2, 0);
		btEditCategory.setLayoutParams(layoutParams);
		btEditCategory.setBackgroundResource(R.drawable.ic_edit);
		btEditCategory.setOnClickListener(this);
		btEditCategory.setId(400);

		// Add childrens
		if (category.getId() != 1) {
			linearLayoutButtons.addView(btEditCategory);
			linearLayoutButtons.addView(btDeleteCategory);
		}
		relativeLayoutTitle.addView(btCollapse);
		relativeLayoutTitle.addView(textViewNameCategory);
		relativeLayoutTitle.addView(linearLayoutButtons);

		// List view rss channels
		ListView listView = new ListView(this);
		listView.setAdapter(new MyAdapterListRSSChannel(this,
				R.layout.row_list_view_rss_channel, 0, currentList));
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				heightAndWidthIcons * 8);
		listView.setScrollContainer(false);
		listView.setLayoutParams(layoutParams);
		listView.setOnItemClickListener(this);
		listView.setId(category.getId());
		listView.setVisibility(View.GONE);

		linearLayoutContainer.addView(relativeLayoutTitle);
		linearLayoutContainer.addView(listView);
	}

	/**
	 * 
	 */
	private void showListCategories() {

		LinearLayout linearLayoutParent = (LinearLayout) findViewById(R.id.layoutParentCategories);
		for (Category category : categoryList) {
			createContainerCategory(category, linearLayoutParent);
		}
	}

	/*---------------Methods Implements---------------*/

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

				RSSChannel newRSSChannel = API.createRSSChannel(
						txtNameRSSChannel.getText().toString(),
						txtURL_RSSChannel.getText().toString(),
						categorySelect.getId());

				if (newRSSChannel != null) {
					UtilActivities.createSuccessDialog(MainActivity.this,
							R.string.success_new_rss_channel);
					dialog.dismiss();
					updateCategories(newRSSChannel.getCategory(), "RSS");
				}

				break;

			case R.id.btn_create_new_category:

				EditText editTextNameCategory = (EditText) dialog
						.findViewById(R.id.edt_name_category);

				Category newCategory = API.createCategory(editTextNameCategory
						.getText().toString());

				UtilActivities.createSuccessDialog(MainActivity.this,
						R.string.success_new_category);
				dialog.dismiss();

				categoryList = (ArrayList<Category>) API.getListAllCategories();
				updateCategories(newCategory, "C");
				break;

			case R.id.btn_edit_category:

				editTextNameCategory = (EditText) dialog
						.findViewById(R.id.edt_name_category);
				String textNameCategory = editTextNameCategory.getText()
						.toString();

				if (textNameCategory.equals(categorySelected.getName()) == false) {

					Category editCategory = API.editCategory(
							categorySelected.getId(), textNameCategory);

					UtilActivities.createSuccessDialog(MainActivity.this,
							R.string.success_edit_category);
					dialog.dismiss();

					categoryList = (ArrayList<Category>) API
							.getListAllCategories();
					updateCategories(editCategory, "E");

				}
				break;

			case R.id.btn_insert_username:

				EditText txtUsername = (EditText) dialog
						.findViewById(R.id.edt_new_username);

				API.editUsername(txtUsername.getText().toString());
				dialog.dismiss();

				updateUsername();
				break;

			case R.id.btn_cancel:

				dialog.dismiss();
				break;

			// btn collapse category
			case 100:

				collapseListView(v);
				break;

			// text name collapse category
			case 200:

				collapseListView(v);
				break;

			// btn show dialog delete category
			case 300:

				LinearLayout layoutParent = (LinearLayout) v.getParent()
						.getParent().getParent();
				final int idCategory = layoutParent.getId();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.qst_delete_category)
						.setPositiveButton(R.string.txt_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										try {

											Category deleteCategory = API
													.getCategoryById(idCategory);
											API.deleteCategory(idCategory);
											updateCategories(deleteCategory,
													"D");

										} catch (DataBaseTransactionException e) {
											UtilActivities.createErrorDialog(
													MainActivity.this,
													e.toString());
											e.printStackTrace();
										} catch (EntityNullException e) {
											UtilActivities.createErrorDialog(
													MainActivity.this,
													e.toString());
											e.printStackTrace();
										}

									}
								})
						.setNegativeButton(R.string.txt_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();

									}
								});
				builder.create();
				builder.show();
				break;

			// btn show dialog edit category
			case 400:

				layoutParent = (LinearLayout) v.getParent().getParent()
						.getParent();
				categorySelected = API.getCategoryById(layoutParent.getId());
				showDialogToEditCategory();
				break;
			}

		} catch (ArgumentInvalidException e) {
			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();
		} catch (DataBaseTransactionException e) {
			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();
		} catch (EntityNullException e) {
			UtilActivities.createErrorDialog(MainActivity.this, e.toString());
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				int idCategoryParentRSSChannel = data.getIntExtra(
						"category_parent_rss_channel_id", -1);
				if (idCategoryParentRSSChannel != -1) {
					updateCategories(
							API.getCategoryById(idCategoryParentRSSChannel),
							"RSS");
				}

				int idCategoryParentRSSChannelOld = data.getIntExtra(
						"category_parent_rss_channel_id_old", -1);
				if (idCategoryParentRSSChannelOld != -1) {
					updateCategories(
							API.getCategoryById(idCategoryParentRSSChannelOld),
							"RSS");
				}
			}
		}

		API = new RSSReaderAPI(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

		API.closeConnection();
		v.setBackgroundResource(R.color.color_white);

		Intent intentRSSChannelActivity = new Intent(this,
				RSSChannelActivity.class);
		intentRSSChannelActivity.putExtra("rss_channel_id", v.getId());
		this.startActivityForResult(intentRSSChannelActivity, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		try {

			switch (item.getItemId()) {

			case R.id.btn_add_category:

				showDialogToCreateCategory();
				break;

			case R.id.btn_add_rss_channel:

				if (UtilAPI.getConnectivityStatus(this) == true) {
					showDialogToCreateRSSChannel();
				} else {
					UtilActivities
							.createErrorDialog(
									this,
									ApplicationContext
											.getStringResource(R.string.no_internet_connection));
				}
				break;

			case R.id.btn_edit_username:

				createDialogToEditUsername();
				break;

			case R.id.btn_submenu_view_in_app:

				API.editViewRSSLinksInApp("1");
				break;

			case R.id.btn_submenu_view_in_browser:

				API.editViewRSSLinksInApp("0");
				break;
			}

		} catch (ArgumentInvalidException e) {
			UtilActivities.createErrorDialog(this, e.toString());
			e.printStackTrace();
		}

		return true;
	}

}

// private Button btEditCategory;
// private Button btEditRSSChannel;
// private Button btDeleteRSSChannel;
// private Button btDeleteCategory;
// private Button btCategoryList;
// private Button btFavoriteLinksRSS;
// private Button btAddRSSChannel;
// private Button btAddCategory;

// btEditCategory = (Button) findViewById(R.id.btn_edit_category);
// btEditCategory.setOnClickListener(this);
//
// btEditRSSChannel = (Button) findViewById(R.id.btn_edit_rss_channel);
// btEditRSSChannel.setOnClickListener(this);
//
// btDeleteCategory = (Button) findViewById(R.id.btn_delete_category);
// btDeleteCategory.setOnClickListener(this);
//
// btDeleteRSSChannel = (Button)
// findViewById(R.id.btn_delete_rss_channel);
// btDeleteRSSChannel.setOnClickListener(this);
//
// btCategoryList = (Button) findViewById(R.id.btn_list_categories);
// btCategoryList.setOnClickListener(this);

// btFavoriteLinksRSS = (Button) findViewById(R.id.btn_favorite_links);
// btFavoriteLinksRSS.setOnClickListener(this);

// btAddCategory = (Button) findViewById(R.id.btn_add_category);
// btAddCategory.setOnClickListener(this);
//
// btAddRSSChannel = (Button) findViewById(R.id.btn_add_rss_channel);
// btAddRSSChannel.setOnClickListener(this);
