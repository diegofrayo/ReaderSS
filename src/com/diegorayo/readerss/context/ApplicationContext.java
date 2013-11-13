package com.diegorayo.readerss.context;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class ApplicationContext extends Application {

	/**
	 * 
	 */
	private static Context mContext;

	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	/**
	 * 
	 * @param idString
	 * @return
	 */
	public static String getStringResource(int idString) {
		return mContext.getResources().getString(idString);
	}

	/**
	 * 
	 * @return
	 */
	public static AssetManager getAssetsResource() {
		return mContext.getResources().getAssets();
	}

}
