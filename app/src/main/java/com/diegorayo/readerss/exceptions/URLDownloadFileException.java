package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Esta excepcion es lanzada cuando se ingresa por parametro a un
 *          metodo, una URL no valida. Tambien cuando se ingresa una URL de un
 *          archivo demasiado pesado (mayor a 2MB)
 */
public class URLDownloadFileException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Esta excepcion puede lanzar 2 mensajes guardados en la clase R. Este
	 * atributo representa el valor del Id de alguno de esos 2 mensajes
	 */
	private int codeMessage;

	public URLDownloadFileException(int codeMessage) {

		this.codeMessage = codeMessage;
	}

	@Override
	public String toString() {

		return ApplicationContext.getStringResource(codeMessage);
	}

}
