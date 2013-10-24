package com.diegorayo.readerss.util_activities;

import java.util.List;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class UtilActivities {

	public static void createErrorDialog(Context context, String messageError) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageError)
				.setTitle(R.string.txt_error)
				.setPositiveButton(R.string.txt_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.setIcon(R.drawable.ic_error);
		builder.create();
		builder.show();

	}

	public static void createSuccessDialog(Context context, int idMessage) {

		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setMessage(ApplicationContext.getStringResource(idMessage))
		// .setTitle(R.string.txt_success)
		// .setPositiveButton(R.string.txt_ok,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });
		// builder.setIcon(R.drawable.ic_success);
		// builder.create();
		// builder.show();

		Toast toast = Toast.makeText(
				context,
				ApplicationContext.getStringResource(R.string.txt_success)
						+ "\n"
						+ ApplicationContext.getStringResource(idMessage),
				Toast.LENGTH_LONG);

		toast.show();
	}

	public static void insertCategoriesInSpinner(Context context,
			List<Category> categoryList, Spinner spinner) {

		ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(
				context, android.R.layout.simple_spinner_item, categoryList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

}
