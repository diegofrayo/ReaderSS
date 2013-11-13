package com.diegorayo.readerss.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.diegorayo.readerss.api.RSSReaderAPI;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.ArgumentInvalidException;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */

public class RSSChannelActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * 
	 */
	private RSSReaderAPI API;

	/**
	 * 
	 */
	private RSSChannel currentRSSChannel;

	/**
	 * 
	 */
	private List<RSSLink> rssLinksList;

	/**
	 * 
	 */
	private boolean optionViewRSSLinksInApp;

	/**
	 * 
	 */
	private Dialog dialog;

	/**
	 * 
	 */
	private Spinner spinnerCategories;

	/**
	 * 
	 */
	private ArrayList<Category> categoryList;

	/**
	 * 
	 */
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_rss_channel);

		Intent it = getIntent();
		int idRSSChannel = it.getIntExtra("rss_channel_id", -1);

		if (idRSSChannel != -1) {

			API = new RSSReaderAPI(this);
			intent = new Intent();

			try {
			

				currentRSSChannel = API.getRSSChannelById(idRSSChannel);
				categoryList = (ArrayList<Category>) API.getListAllCategories();
				rssLinksList = API
						.getListRSSLinksOfRSSChannel(currentRSSChannel);
				optionViewRSSLinksInApp = API.getViewRSSLinksInApp();
	
				if (currentRSSChannel.isModified()) {

					currentRSSChannel.setModified(false);
					currentRSSChannel.setDateLastRSSLink(rssLinksList.get(0)
							.getDate());
					currentRSSChannel = API
							.editLastContentLengthXMLFileRSSChannel(currentRSSChannel);
				}

			} catch (EntityNullException e) {
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

			Intent backToRSSChannelActivity = new Intent(this,
					RSSChannelActivity.class);
			backToRSSChannelActivity.putExtra("", -1);
			startActivity(backToRSSChannelActivity);
		}

	}

	/**
	 * 
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

		TextView txtNameRSSCHannel = (TextView) dialog
				.findViewById(R.id.edt_name_rss_channel);
		txtNameRSSCHannel.setText(currentRSSChannel.getName());

		int currentCategoryRSSChannel = currentRSSChannel.getCategory().getId();
		for (int i = 0; i < categoryList.size(); i++) {

			int currentCategoryId = categoryList.get(i).getId();
			if (currentCategoryId == currentCategoryRSSChannel) {
				spinnerCategories.setSelection(i);
				break;
			}
		}

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
	private void updateInformationActivity() {

		// LLeno la lista de links rss
		ListView listView = (ListView) findViewById(R.id.listLinksRSSChannel);
		listView.setAdapter(null);
		listView.setAdapter(new MyAdapterListRSSLinks(this,
				R.layout.row_list_view_rss_links, 0, rssLinksList));
		listView.setOnItemClickListener(this);

		// LLeno la informacion basica del canal
		TextView textView = (TextView) findViewById(R.id.txtLastUpdate);
		textView.setText("Last Update: " + currentRSSChannel.getLastUpdate()
				+ " ");
	}

	/*---------------Methods Implements---------------*/

	@SuppressLint("ResourceAsColor")
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		if (optionViewRSSLinksInApp) {

		} else {
			v.setBackgroundResource(R.color.color_white);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(rssLinksList.get(position).getUrl()));
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_rss_channel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		try {

			switch (item.getItemId()) {

			case R.id.btn_update_rss_channel:

				if (UtilAPI.getConnectivityStatus(this) == true) {

					currentRSSChannel.setLastUpdate(UtilAPI
							.getCurrentDateAndTime());
					rssLinksList = API
							.downloadXMLFileAndGetListRSSLinksOfRSSChannel(currentRSSChannel);
					updateInformationActivity();

					intent.putExtra("category_parent_rss_channel_id",
							currentRSSChannel.getCategory().getId());
				} else {
					UtilActivities
							.createErrorDialog(
									this,
									ApplicationContext
											.getStringResource(R.string.no_internet_connection));
				}
				break;

			case R.id.btn_menu_edit_rss_channel:

				showDialogToEditRSSChannel();
				break;

			case R.id.btn_menu_delete_rss_channel:

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.qst_delete_rss_channel)
						.setPositiveButton(R.string.txt_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										try {

											API.deleteRSSChannel(currentRSSChannel
													.getId());

											intent.putExtra(
													"category_parent_rss_channel_id",
													currentRSSChannel
															.getCategory()
															.getId());

											onBackPressed();

										} catch (DataBaseTransactionException e) {
											UtilActivities.createErrorDialog(
													RSSChannelActivity.this,
													e.toString());
											e.printStackTrace();
										} catch (EntityNullException e) {
											UtilActivities.createErrorDialog(
													RSSChannelActivity.this,
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

			case R.id.btn_submenu_view_in_app:

				API.editViewRSSLinksInApp("1");
				optionViewRSSLinksInApp = API.getViewRSSLinksInApp();
				break;

			case R.id.btn_submenu_view_in_browser:

				API.editViewRSSLinksInApp("0");
				optionViewRSSLinksInApp = API.getViewRSSLinksInApp();
				break;
			}

		} catch (EntityNullException e) {
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
		} catch (ArgumentInvalidException e) {
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

				currentRSSChannel = API.editRSSChannel(currentRSSChannel
						.getId(), txtNameRSSChannel.getText().toString(),
						categorySelect.getId(), currentRSSChannel
								.getLastUpdate(), currentRSSChannel
								.isModified(), currentRSSChannel
								.getDateLastRSSLink(), currentRSSChannel
								.getLastContentLengthXMLFile());

				UtilActivities.createSuccessDialog(RSSChannelActivity.this,
						R.string.success_edit_rss_channel);
				dialog.dismiss();

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

		} catch (EntityNullException e) {
			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		} catch (ArgumentInvalidException e) {
			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		} catch (DataBaseTransactionException e) {
			UtilActivities.createErrorDialog(RSSChannelActivity.this,
					e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {

		API.closeConnection();
		API = null;
		if (intent.getIntExtra("category_parent_rss_channel_id", -1) != -1) {
			setResult(RESULT_OK, intent);
		}
		super.onBackPressed();
	}

}
