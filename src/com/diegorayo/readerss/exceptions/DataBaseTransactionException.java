package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Esta excepcion es lanzada cuando ocurre un error al actualizar,
 *          insertar o borrar un registro de alguna tabla de la base de datos
 *          SQLite de la aplicacion
 */
public class DataBaseTransactionException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String INSERT_OPERATION = "insert";

	public static final String UPDATE_OPERATION = "update";

	public static final String DELETE_OPERATION = "delete";

	/**
	 * Mensaje que va a ser lanzado por la excepcion
	 */
	private String message;

	/**
	 * 
	 * @param operationFailedName
	 *            - Nombre de la operacion que falló
	 * @param nameEntity
	 *            - Nombre de la entidad relacionada con el fallo
	 */
	public DataBaseTransactionException(String operationFailedName,
			String nameEntity) {

		this.message = ApplicationContext
				.getStringResource(R.string.exc_DataBaseTransactionException);
		this.message = message.replaceAll("1", operationFailedName);
		this.message = message.replaceAll("2", nameEntity);
	}

	@Override
	public String toString() {

		return message;
	}

}
