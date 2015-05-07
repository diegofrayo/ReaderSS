package com.diegorayo.readerss.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase que contiene metodos que van a ser utilizados en las
 *          actividades
 */
public class UtilActivities {

    /**
     * Metodo utlizado para crear un dialogo de confirmacion
     *
     * @param context
     * @param idMessage       - Mensaje que va a ser mostrado en el dialogo
     * @param onClickListener - Evento del boton OK
     */
    public static void createConfirmDialog(Context context, int idMessage,
                                           DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getString(idMessage))
                .setPositiveButton(R.string.word_ok, onClickListener)
                .setNegativeButton(R.string.word_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        builder.create();
        builder.show();
    }

    /**
     * Metodo para crear un dialogo de error
     *
     * @param context
     * @param message - Mensaje que va a ser mostrado en el dialogo
     */
    public static void createErrorDialog(Context context, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setTitle(R.string.word_error)
                .setPositiveButton(R.string.word_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        builder.setIcon(R.drawable.ic_error);
        builder.create();
        builder.show();
    }

    /**
     * Metodo para crear un mensaje de exito
     *
     * @param context
     * @param idMessage - Mensaje que va a ser mostrado
     */
    public static void createSuccessDialog(Context context, int idMessage) {

        Toast toast = Toast.makeText(
                context,
                ApplicationContext.getStringResource(R.string.word_success)
                        + ":\n"
                        + ApplicationContext.getStringResource(idMessage),
                Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Metodo para configurar el action bar de las actividades
     *
     * @param activity
     */
    public static void configureActionBar(ActionBarActivity activity) {

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_title_app);
        actionBar.setTitle(activity.getString(R.string.app_name));
    }

    /**
     * Metodo para llenar con contenido un spinner
     *
     * @param context
     * @param categoryList
     * @param spinner
     */
    public static void insertCategoriesInSpinner(Context context,
                                                 List<Category> categoryList, Spinner spinner) {

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(
                context, android.R.layout.simple_spinner_dropdown_item,
                categoryList);
        spinner.setAdapter(dataAdapter);
    }

    /**
     * Metodo para poner en pantalla, un mensaje de bienvenida con el nombre de
     * usuario del telefono
     */
    public static void updateUsername(Activity activity, String username) {

        TextView textViewWelcomeUser = (TextView) activity
                .findViewById(R.id.word_welcome_username);
        textViewWelcomeUser.setText(" " + username + " ");
    }

}
