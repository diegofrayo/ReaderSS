package com.diegorayo.readerss.context;

import android.app.Application;
import android.content.Context;

public class ApplicationContext extends Application {

	private static Context mContext;

	public static String getStringResource(int idString) {
		return mContext.getResources().getString(idString);
	}

	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

}
