package com.diegorayo.readerss.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import com.diegorayo.readerss.R;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.diegorayo.readerss.api.RSSReaderAPI;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.EntityNullException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class FilesManagement {

	/**
	 * 
	 * @param rssChannel
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws EntityNullException
	 */
	public LinkedList<RSSLink> readFileXML(RSSChannel rssChannel)
			throws SAXException, IOException, ParserConfigurationException,
			EntityNullException {

		return new XMLFileParser().parse(createPathFile(rssChannel));
	}

	/**
	 * 
	 * @param rssChannel
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws EntityNullException
	 * @throws URLDownloadFileException
	 */
	@SuppressWarnings("resource")
	public boolean downloadXMLFile(RSSChannel rssChannel) throws IOException,
			SAXException, ParserConfigurationException, EntityNullException,
			URLDownloadFileException {

		if (rssChannel != null) {
			if (rssChannel.getCategory() != null) {

				File pathFile = new File(RSSReaderAPI.PATH, rssChannel
						.getCategory().getName());

				if (pathFile.exists() == false) {
					pathFile.mkdirs();
				}

				File downloadFile = new File(pathFile, rssChannel.getName()
						+ ".xml");

				FileOutputStream fout = new FileOutputStream(downloadFile);

				// create the new connection
				HttpURLConnection urlConnection = (HttpURLConnection) new URL(
						rssChannel.getUrl()).openConnection();

				// set up some things on the connection
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);

				// and connect!
				urlConnection.connect();

				if (urlConnection.getContentLength() > 600000) {
					throw new URLDownloadFileException(
							R.string.exc_URLDownloadFileException_1);
				}

				// this will be used in reading the data from the internet
				InputStream inputStream = urlConnection.getInputStream();

				// this is the total size of the file
				// int totalSize = urlConnection.getContentLength();

				// variable to store total downloaded bytes
				// int downloadedSize = 0;

				// create a buffer...
				byte[] buffer = new byte[1024];
				int bufferLength = 0; // used to store a temporary size of the
										// buffer

				// now, read through the input buffer and write the contents to
				// the
				// file
				while ((bufferLength = inputStream.read(buffer)) > 0) {

					// add the data in the buffer to the file in the file output
					// stream (the file on the sd card
					fout.write(buffer, 0, bufferLength);

					// add up the size so we know how much is downloaded
					// downloadedSize += bufferLength;

					// this is where you would do something to report the
					// prgress,
					// like this maybe
					// updateProgress(downloadedSize, totalSize);

				}

				fout.close();

				if (XMLFileParser.documentIsXMLFile(createPathFile(rssChannel)) == false) {
					deleteFile(rssChannel);
					throw new URLDownloadFileException(
							R.string.exc_URLDownloadFileException_2);
				}

				return true;

			}
			throw new EntityNullException(Category.class.getSimpleName());
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

	/**
	 * 
	 * @param rssChannel
	 * @throws EntityNullException
	 */
	public void deleteFile(RSSChannel rssChannel) throws EntityNullException {

		File downloadFile = new File(createPathFile(rssChannel));
		downloadFile.delete();
	}

	/**
	 * 
	 * @param category
	 * @throws EntityNullException
	 */
	public void deleteFolder(Category category) throws EntityNullException {

		if (category != null) {

			File pathFile = new File(RSSReaderAPI.PATH, category.getName());

			File[] listFiles = pathFile.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				listFiles[i].delete();
			}

			pathFile.delete();
		}

		throw new EntityNullException(Category.class.getSimpleName());
	}

	/**
	 * 
	 * @param rssChannel
	 * @param newName
	 * @throws EntityNullException
	 */
	public void renameFile(RSSChannel rssChannel, String newName)
			throws EntityNullException {

		File oldFile = new File(createPathFile(rssChannel));
		rssChannel.setName(newName);
		File newFile = new File(createPathFile(rssChannel));

		oldFile.renameTo(newFile);
	}

	/**
	 * 
	 * @param category
	 * @param newName
	 * @throws EntityNullException
	 */
	public void renameFolder(Category category, String newName)
			throws EntityNullException {

		if (category != null) {

			File oldFile = new File(RSSReaderAPI.PATH + File.separator
					+ category.getName());

			File newFile = new File(RSSReaderAPI.PATH + File.separator
					+ newName);

			oldFile.renameTo(newFile);

		}

		throw new EntityNullException(Category.class.getSimpleName());
	}

	public void moveFile(RSSChannel oldRSSChannel, RSSChannel editRSSChannel)
			throws EntityNullException {

		File oldFile = new File(createPathFile(oldRSSChannel));
		File newFile = new File(createPathFile(editRSSChannel));

		oldFile.renameTo(newFile);
	}

	public boolean createDirectory(Category category)
			throws EntityNullException {

		if (category != null) {
			File directory = new File(RSSReaderAPI.PATH + File.separator
					+ category.getName());
			return directory.mkdirs();
		}

		throw new EntityNullException(Category.class.getSimpleName());
	}

	/**
	 * 
	 * @param rssChannel
	 * @return
	 * @throws EntityNullException
	 */
	private String createPathFile(RSSChannel rssChannel)
			throws EntityNullException {

		if (rssChannel != null) {
			if (rssChannel.getCategory() != null) {

				String pathFile = RSSReaderAPI.PATH + File.separator
						+ rssChannel.getCategory().getName() + File.separator
						+ rssChannel.getName() + ".xml";
				return "file:///" + pathFile;
			}

			throw new EntityNullException(Category.class.getSimpleName());
		}

		throw new EntityNullException(RSSChannel.class.getSimpleName());
	}

}
