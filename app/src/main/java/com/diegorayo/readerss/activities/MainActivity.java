package com.diegorayo.readerss.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.adapters.MyAdapterCategoryList;
import com.diegorayo.readerss.api.API;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.FileSystemException;
import com.diegorayo.readerss.exceptions.InvalidArgumentException;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.util.UtilAPI;
import com.diegorayo.readerss.util.UtilActivities;

import java.util.ArrayList;

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
     * Utilizado para los metodos en donde se tienen que mostrar Dialogs
     */
    private Dialog dialog;

    /**
     * Spinner que va a contener la lista de categorias. Es utilizado en varios
     * Dialogs
     */
    private Spinner spinnerCategories;

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

                    UtilActivities.createErrorDialog(MainActivity.this,
                            (String) msg.obj);
                } else {

                    UtilActivities.createSuccessDialog(MainActivity.this,
                            R.string.success_new_rss_channel);
                    dialog.dismiss();
                    generateListViewCategories();
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
        setContentView(R.layout.activity_main);

        UtilActivities.inflateHeaderApp(this);

        api = new API();

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
        UtilActivities.updateUsername(this, api.getUsernameGoogle());

        // Configuro y despliego las categorias
        generateListViewCategories();
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

                case R.id.btn_create_new_rss_channel:

                    progressDialog = ProgressDialog.show(this, "",
                            ApplicationContext
                                    .getStringResource(R.string.txt_load_data),
                            true, true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg = progressHandler.obtainMessage();

                            EditText txtNameRSSChannel = (EditText) dialog
                                    .findViewById(R.id.edt_name_rss_channel);
                            EditText txtURL_RSSChannel = (EditText) dialog
                                    .findViewById(R.id.edt_url_rss_channel);

                            Category categorySelect = (Category) spinnerCategories
                                    .getSelectedItem();

                            try {

                                Object rssChannel = api.createRSSChannel(
                                        txtNameRSSChannel.getText().toString(),
                                        txtURL_RSSChannel.getText().toString(),
                                        categorySelect.getId());

                                msg.obj = rssChannel;

                            } catch (InvalidArgumentException e) {

                                e.printStackTrace();
                                msg.obj = e.toString();
                            } catch (DataBaseTransactionException e) {

                                e.printStackTrace();
                                msg.obj = e.toString();
                            } catch (NullEntityException e) {

                                e.printStackTrace();
                                msg.obj = e.toString();
                            } catch (FileSystemException e) {

                                e.printStackTrace();
                                msg.obj = e.toString();
                            }

                            progressHandler.sendMessage(msg);
                        }
                    }).start();

                    break;

                case R.id.btn_create_new_category:

                    EditText editTextNameCategory = (EditText) dialog
                            .findViewById(R.id.edt_name_category);

                    api.createCategory(editTextNameCategory.getText().toString());

                    UtilActivities.createSuccessDialog(this,
                            R.string.success_new_category);
                    dialog.dismiss();

                    generateListViewCategories();

                    break;

                case R.id.btn_cancel:

                    dialog.dismiss();
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

            case R.id.btn_menu_delete_category:

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {

                            Category categorySelected = categoryList
                                    .get((int) info.id);

                            api.deleteCategory(categorySelected.getId());
                            generateListViewCategories();

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
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
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

        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("category", categoryList.get(arg2));

        // Significa que inicia una nueva actividad, y cuando esta se acaba,
        // vuelve a la actual
        this.startActivityForResult(intent, 1);
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

            case R.id.btn_menu_add_category:

                showDialogToCreateCategory();
                break;

            case R.id.btn_menu_add_rss_channel:

                if (UtilAPI.getConnectivityStatus(this) == true
                        && categoryList.size() > 0) {

                    showDialogToCreateRSSChannel();
                } else {

                    UtilActivities
                            .createErrorDialog(
                                    this,
                                    ApplicationContext
                                            .getStringResource(R.string.error_no_internet_connection));
                }

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

                generateListViewCategories();
            }
        }
    }

    /**
     * Metodo para generar la lista de categorias en la actividad
     */
    private void generateListViewCategories() {

        categoryList = (ArrayList<Category>) api.getListAllCategories();

        ListView listView = (ListView) this
                .findViewById(R.id.list_view_list_categories);
        listView.setAdapter(new MyAdapterCategoryList(this,
                R.layout.row_list_view_categories, 0, categoryList, api));
        listView.setScrollContainer(false);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
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

}
