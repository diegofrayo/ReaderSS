package com.diegorayo.rssreader.exceptions;

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
	 * @param operationFailedName
	 * @param entityName
	 */
	public DataBaseTransactionException(String operationFailedName,
			String entityName) {

		super("");
	}
}
