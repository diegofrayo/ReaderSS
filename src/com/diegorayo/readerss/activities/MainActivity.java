package com.diegorayo.readerss.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.diegorayo.readerss.adapters.MyAdapterListRSSChannel;
import com.diegorayo.readerss.api.RSSReaderAPI;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.exceptions.ArgumentInvalidException;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.util_activities.UtilActivities;
import com.diegorayo.readerss.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Menu;
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
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private RSSReaderAPI API;
	private ArrayList<Category> categoryList;

	private Button btAddRSSChannel;
	private Button btFavoriteLinksRSS;
	private Button btAddCategory;

	private Dialog dialog;
	private Spinner spinnerCategories;

	// private Button btEditCategory;
	// private Button btEditRSSChannel;
	// private Button btDeleteRSSChannel;
	// private Button btDeleteCategory;
	// private Button btCategoryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		btAddCategory = (Button) findViewById(R.id.btn_add_category);
		btAddCategory.setOnClickListener(this);

		btAddRSSChannel = (Button) findViewById(R.id.btn_add_rss_channel);
		btAddRSSChannel.setOnClickListener(this);

		btFavoriteLinksRSS = (Button) findViewById(R.id.btn_favorite_links);
		btFavoriteLinksRSS.setOnClickListener(this);

		API = new RSSReaderAPI(this);

		Toast t = Toast.makeText(this, getFilesDir().getAbsolutePath(),
				Toast.LENGTH_SHORT);
		t.show();

		// Si es la primera vez que abre la aplicacion
		if (API.getOpenFirstApp() == true) {

			try {

				API.createCategory("default");
				API.createCategory("futbol");
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
		String username = API.getUsername();
		TextView txtWelcomeUser = (TextView) findViewById(R.id.txt_welcome_user);
		txtWelcomeUser.setText(username);

		// Configuro y despliego las categorias
		categoryList = (ArrayList<Category>) API.getListAllCategories();
		showListCategories();

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

	}

	@SuppressWarnings("deprecation")
	private void showDialogToCreateRSSChannel() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_rss_channel);

		spinnerCategories = (Spinner) dialog
				.findViewById(R.id.spnListCategories);

		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_rss_channel);
		btnCreate.setOnClickListener(this);

		Button btnCancel = (Button) dialog
				.findViewById(R.id.btn_cancel_new_rss_channel);
		btnCancel.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		UtilActivities.insertCategoriesInSpinner(this, categoryList,
				spinnerCategories);
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void showDialogToCreateCategory() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_create_category);

		Button btnCreate = (Button) dialog
				.findViewById(R.id.btn_create_new_category);
		btnCreate.setOnClickListener(this);

		Button btnCancel = (Button) dialog
				.findViewById(R.id.btn_cancel_new_category);
		btnCancel.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void createDialogToInsertUsername() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_insert_username);

		Button btnInsert = (Button) dialog
				.findViewById(R.id.btn_insert_username);
		btnInsert.setOnClickListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void showListCategories() {

		LinearLayout linearLayoutParent = (LinearLayout) findViewById(R.id.layoutParentCategories);

		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
						.getDisplayMetrics());

		for (Category category : categoryList) {

			List<RSSChannel> currentList = API
					.getListRSSChannelsInACategory(category.getId());

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			// Layout container
			LinearLayout linearLayoutContainer = new LinearLayout(this);
			layoutParams.setMargins(0, margin * 4, 0, margin * 4);
			linearLayoutContainer.setLayoutParams(layoutParams);
			linearLayoutContainer.setOrientation(LinearLayout.VERTICAL);
			linearLayoutContainer.setId(category.getId());
			linearLayoutContainer.setBackgroundResource(R.drawable.shadow);
			linearLayoutParent.addView(linearLayoutContainer);

			// Layout title
			RelativeLayout relativeLayoutTitle = new RelativeLayout(this);
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			relativeLayoutTitle.setLayoutParams(layoutParams);

			relativeLayoutTitle
					.setBackgroundResource(R.drawable.bg_title_categories);

			// Childrens layout title
			// Button collape
			Button btCollapse = new Button(this);
			RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			relativeLayoutParams.setMargins(margin, 0, margin, 0);
			btCollapse.setLayoutParams(relativeLayoutParams);
			btCollapse.setBackgroundResource(R.drawable.ic_collapse);
			btCollapse.setId(111);
			btCollapse.setOnClickListener(this);

			// Text view name category
			TextView txtNameCategory = new TextView(this);
			relativeLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
			relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF,
					btCollapse.getId());
			relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

			txtNameCategory.setLayoutParams(relativeLayoutParams);
			txtNameCategory.setText(category.getName());
			txtNameCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			txtNameCategory.setTextColor(getResources().getColor(
					R.color.secundary_text));
			txtNameCategory.setTypeface(null, Typeface.BOLD);

			// Linear layout of the buttons
			LinearLayout linearLayoutButtons = new LinearLayout(this);
			relativeLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			linearLayoutButtons.setLayoutParams(relativeLayoutParams);
			linearLayoutButtons.setOrientation(LinearLayout.HORIZONTAL);

			// Button delete
			Button btDeleteCategory = new Button(this);
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(margin, 0, 0, 0);
			btDeleteCategory.setLayoutParams(layoutParams);
			btDeleteCategory.setBackgroundResource(R.drawable.ic_delete);
			btDeleteCategory.setOnClickListener(this);
			btDeleteCategory.setId(222);

			// Button edit
			Button btEditCategory = new Button(this);
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			btEditCategory.setLayoutParams(layoutParams);
			btEditCategory.setBackgroundResource(R.drawable.ic_edit);
			btEditCategory.setOnClickListener(this);
			btEditCategory.setId(333);

			// Add childrens
			linearLayoutButtons.addView(btEditCategory);
			linearLayoutButtons.addView(btDeleteCategory);
			relativeLayoutTitle.addView(btCollapse);
			relativeLayoutTitle.addView(txtNameCategory);
			relativeLayoutTitle.addView(linearLayoutButtons);

			// List view rss channels
			ListView listView = new ListView(this);
			listView.setAdapter(new MyAdapterListRSSChannel(this,
					R.layout.row_list_view_rss_channel, 0, currentList));
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			listView.setScrollContainer(false);
			listView.setLayoutParams(layoutParams);
			listView.setOnItemClickListener(this);

			linearLayoutContainer.addView(relativeLayoutTitle);
			linearLayoutContainer.addView(listView);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

		RSSChannel r = API.getRSSChannelById(v.getId());
		Toast t = Toast.makeText(this, r.getId() + ": " + r.getName(),
				Toast.LENGTH_SHORT);
		t.show();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_add_category:

			showDialogToCreateCategory();

			break;

		case R.id.btn_add_rss_channel:

			showDialogToCreateRSSChannel();
			break;

		case R.id.btn_create_new_rss_channel:

			EditText txtNameRSSChannel = (EditText) dialog
					.findViewById(R.id.edt_new_name_rss_channel);
			EditText txtURL_RSSChannel = (EditText) dialog
					.findViewById(R.id.edt_new_url_rss_channel);

			Category categorySelect = (Category) spinnerCategories
					.getSelectedItem();

			try {

				RSSChannel newRSSChannel = API.createRSSChannel(
						txtNameRSSChannel.getText().toString(),
						txtURL_RSSChannel.getText().toString(),
						categorySelect.getId());

				if (newRSSChannel != null) {
					UtilActivities.createSuccessDialog(MainActivity.this,
							R.string.success_new_rss_channel);
					dialog.dismiss();
				} else {
					// Lanzar excepcion propia
				}

			} catch (ArgumentInvalidException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (DataBaseTransactionException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (EntityNullException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (SAXException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			}

			break;

		case R.id.btn_create_new_category:

			EditText txtNameCategory = (EditText) dialog
					.findViewById(R.id.edt_new_name_category);

			try {

				API.createCategory(txtNameCategory.getText().toString());
				UtilActivities.createSuccessDialog(MainActivity.this,
						R.string.success_new_category);
				dialog.dismiss();
				categoryList = (ArrayList<Category>) API.getListAllCategories();
				showListCategories();

			} catch (ArgumentInvalidException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (DataBaseTransactionException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			} catch (EntityNullException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();
			}

			break;

		case R.id.btn_insert_username:

			EditText txtUsername = (EditText) dialog
					.findViewById(R.id.edt_new_username);

			try {

				API.editUsername(txtUsername.getText().toString());
				dialog.dismiss();

			} catch (ArgumentInvalidException e) {
				UtilActivities.createErrorDialog(MainActivity.this,
						e.toString());
				e.printStackTrace();

			}

			break;

		case R.id.btn_cancel_new_rss_channel:
			dialog.dismiss();
			break;

		case R.id.btn_cancel_new_category:
			dialog.dismiss();
			break;

		// btn collapse
		case 111:

			LinearLayout layoutParent = (LinearLayout) v.getParent()
					.getParent();
			int visibilityList = layoutParent.getChildAt(1).getVisibility();
			if (visibilityList == View.VISIBLE) {
				layoutParent.getChildAt(1).setVisibility(View.GONE);
			} else {
				layoutParent.getChildAt(1).setVisibility(View.VISIBLE);
			}
			break;

		// btn delete category
		case 222:

			layoutParent = (LinearLayout) v.getParent().getParent().getParent();

			break;

		// btn edit category
		case 333:

			layoutParent = (LinearLayout) v.getParent().getParent().getParent();

			Category c = API.getCategoryById(layoutParent.getId());
			Toast t = Toast.makeText(this, c.getId() + ": " + c.getName(),
					Toast.LENGTH_SHORT);
			t.show();

			break;

		}

		// case R.id.btn_delete_category:
		//
		// categoryList = (ArrayList<Category>) API.getListAllCategories();
		// break;
		//
		// case R.id.btn_delete_rss_channel:
		//
		// break;
		//
		// case R.id.btn_edit_category:
		//
		// LinearLayout l = (LinearLayout) findViewById(R.id.hola);
		// if (l.getVisibility() == View.VISIBLE) {
		// l.setVisibility(View.INVISIBLE);
		// } else {
		// l.setVisibility(View.VISIBLE);
		// }
		//
		// break;

		// case R.id.btn_edit_rss_channel:
		//
		// break;
		//
		// case R.id.btn_favorite_links:
		//
		// break;
		//
		// case R.id.btn_list_categories:
		//
		// break;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

}
