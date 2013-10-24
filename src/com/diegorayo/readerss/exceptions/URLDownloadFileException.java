package com.diegorayo.readerss.exceptions;

import com.diegorayo.readerss.context.ApplicationContext;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class URLDownloadFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private int messageCode;

	public URLDownloadFileException(int messageCode) {
		this.messageCode = messageCode;
	}

	@Override
	public String toString() {

		return ApplicationContext.getStringResource(messageCode);
	}

}
