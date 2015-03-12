package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Esta excepcion es lanzada por un metodo al cual fue ingresado un
 *          parametro con un valor no valido
 */
public class InvalidArgumentException extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {

		return ApplicationContext
				.getStringResource(R.string.exc_InvalidArgumentException);
	}
}
