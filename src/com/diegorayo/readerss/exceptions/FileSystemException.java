package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Esta excepcion es lanzada cuando hay problema al manejar los
 *          archivos del sistema. (crear, actualizar, mover o eliminar carpetas
 *          y archivos)
 */
public class FileSystemException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Representa el codigo del mensaje que se va a lanzar
	 */
	private int idMessage;

	/**
	 * 
	 * @param idMessage
	 */
	public FileSystemException(int idMessage) {

		super();
		this.idMessage = idMessage;
	}

	@Override
	public String toString() {

		return ApplicationContext.getStringResource(idMessage);
	}
}
