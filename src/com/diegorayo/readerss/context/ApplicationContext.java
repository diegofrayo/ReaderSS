package com.diegorayo.readerss.context;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase utilizada para acceder a recursos como strings o assets. A
 *          esta clase siempre se puede acceder desde cualquier actividad,
 *          servicio, broadcast receiver de la aplicacion
 */
public class ApplicationContext extends Application {

	/**
	 * Contexto de la aplicacion
	 */
	private static Context mContext;

	public void onCreate() {

		super.onCreate();
		mContext = getApplicationContext();
	}

	/**
	 * Metodo para obtener un string del archivo Strings.xml
	 * 
	 * @param idString
	 * @return
	 */
	public static String getStringResource(int idString) {
		return mContext.getResources().getString(idString);
	}

	/**
	 * Metodo para obtener un recurso. Lo utilizo para utilizar una fuente o
	 * tipografia propia del proyecto y no del sistema
	 * 
	 * @return
	 */
	public static AssetManager getAssetsResource() {
		return mContext.getResources().getAssets();
	}

	public static Context getContext() {

		return mContext;
	}

}
