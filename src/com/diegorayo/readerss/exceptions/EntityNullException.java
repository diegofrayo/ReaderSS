package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.R;
/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class EntityNullException extends Exception {

	private static final long serialVersionUID = 1L;
	private String typeEntity;

	public EntityNullException(String typeEntity) {
		this.typeEntity = typeEntity;
	}

	@Override
	public String toString() {

		String message = ApplicationContext
				.getStringResource(R.string.exc_EntityNullException);
		message = message.replaceAll("%", this.typeEntity);

		return message;
	}

}
