package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.R;
/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class DataBaseTransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public static final String OPERATION_INSERT = "insert";

	/**
	 * 
	 */
	public static final String OPERATION_UPDATE = "update";

	/**
	 * 
	 */
	public static final String OPERATION_DELETE = "delete";

	/**
	 * 
	 */
	private String message;

	/**
	 * 
	 * @param operationFailedName
	 * @param entityName
	 */
	public DataBaseTransactionException(String operationFailedName,
			String entityName) {

		this.message = ApplicationContext
				.getStringResource(R.string.exc_DataBaseTransactionException);
		this.message = message.replaceAll("1", operationFailedName);
		this.message = message.replaceAll("2", entityName);
	}

	@Override
	public String toString() {

		return message;
	}

}
