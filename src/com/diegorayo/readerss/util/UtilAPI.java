package com.diegorayo.readerss.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class UtilAPI {

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getConnectivityStatus(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		// boolean isConnected = activeNetwork != null
		// && activeNetwork.isConnectedOrConnecting()
		// && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isNumber(char[] url) {

		for (char c : url) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateAndTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateAndTime = sdf.format(new Date());
		return currentDateAndTime;
	}
}
