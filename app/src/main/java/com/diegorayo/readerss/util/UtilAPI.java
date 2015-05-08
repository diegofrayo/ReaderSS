package com.diegorayo.readerss.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase que contiene metodos que van a ser utilizados en la clase
 *          API
 */
public class UtilAPI {

	/**
	 * @param context
	 *            - Necesario para conocer el estado de conexion a internet
	 * @return true: si hay conectividad a internet | false: si no hay
	 *         conectividad
	 */
	public static boolean getConnectivityStatus(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnected();

		return isConnected;
	}

	/**
	 * Metodo para obtener la fecha y hora actual
	 * 
	 * @return Una cadena con la fecha y hora
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateAndTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateAndTime = sdf.format(new Date());

		return currentDateAndTime;
	}

	/**
	 * Metodo para obtener el nombre de usuario del telefono (La cuenta de
	 * gmail)
	 * 
	 * @param context
	 * @return
	 */
	public static String getUsernameGoogle(Context context) {

		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		List<String> possibleEmails = new LinkedList<String>();

		for (Account account : accounts) {

			possibleEmails.add(account.name);
		}

		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {

			String email = possibleEmails.get(0);
			String[] parts = email.split("@");

			if (parts.length > 0 && parts[0] != null) {

				return parts[0];
			}
		}

		return "";
	}

}
