package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.R;
/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class ArgumentInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {

		return ApplicationContext
				.getStringResource(R.string.exc_ArgumentInvalidException);
	}
}
