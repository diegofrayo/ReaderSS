package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Excepcion lanzada cuando un metodo va a utilizar una entidad
 *          ingresada por parametro, pero ésta es nula
 */
public class NullEntityException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Atributo que contiene el nombre de la entidad que lanzó la excepcion
	 */
	private String nameEntity;

	public NullEntityException(String typeEntity) {

		this.nameEntity = typeEntity;
	}

	@Override
	public String toString() {

		String message = ApplicationContext
				.getStringResource(R.string.exc_NullEntityException);
		message = message.replaceAll("%", this.nameEntity);

		return message;
	}

}
