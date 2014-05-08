package com.diegorayo.readerss.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.diegorayo.readerss.R;
import com.diegorayo.readerss.context.ApplicationContext;
import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.entitys.RSSChannel;
import com.diegorayo.readerss.entitys.RSSLink;
import com.diegorayo.readerss.exceptions.NullEntityException;
import com.diegorayo.readerss.exceptions.URLDownloadFileException;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase utilizadaa para gestionar archivos xml y carpetas.
 */
public class FilesManagement {

	/**
	 * Metodo para crear un directorio. Cada categoria creada va a tener un
	 * directorio
	 * 
	 * @param nameCategory
	 * @return
	 */
	public static boolean createDirectory(String nameCategory) {

		File directory = new File(createAbsolutePath(nameCategory, null));
		return directory.mkdirs();
	}

	/**
	 * Metodo para borrar un archivo
	 * 
	 * @param rssChannel
	 * @return
	 */
	public static boolean deleteFile(RSSChannel rssChannel) {

		File deleteFile = new File(createAbsolutePath(rssChannel.getCategory()
				.getName(), rssChannel.getName()));

		return deleteFile.delete();
	}

	/**
	 * Metodo para borrar una categoria con todos sus archivos
	 * 
	 * @param nameCategory
	 * @return
	 */
	public static boolean deleteFolder(String nameCategory) {

		File pathFile = new File(createAbsolutePath(nameCategory, null));

		File[] listFiles = pathFile.listFiles();

		for (int i = 0; i < listFiles.length; i++) {

			listFiles[i].delete();
		}

		return pathFile.delete();
	}

	/**
	 * Metodo para descargar un archivo XML perteneciente a un RSSChannel
	 * 
	 * @param rssChannel
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NullEntityException
	 * @throws URLDownloadFileException
	 */
	public static void downloadXMLFile(RSSChannel rssChannel)
			throws IOException, SAXException, ParserConfigurationException,
			NullEntityException, URLDownloadFileException {

		if (rssChannel != null) {

			if (rssChannel.getCategory() != null) {

				File downloadFile = new File(createAbsolutePath(rssChannel
						.getCategory().getName(), rssChannel.getName()));

				FileOutputStream fout = new FileOutputStream(downloadFile);

				// create the new connection
				HttpURLConnection urlConnection = (HttpURLConnection) new URL(
						rssChannel.getUrl()).openConnection();

				// set up some things on the connection
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);

				// and connect!
				urlConnection.connect();

				int sizeFile = urlConnection.getContentLength();

				if (sizeFile > 200000) {

					deleteFile(rssChannel);
					fout.close();
					urlConnection.disconnect();
					throw new URLDownloadFileException(
							R.string.exc_URLDownloadFileException_1);
				}

				// this will be used in reading the data from the internet
				InputStream inputStream = urlConnection.getInputStream();

				// create a buffer...
				byte[] buffer = new byte[1024];
				int bufferLength = 0;

				// now, read through the input buffer and write the contents to
				// the file
				while ((bufferLength = inputStream.read(buffer)) > 0) {

					// add the data in the buffer to the file in the file output
					// stream (the file on the sd card
					fout.write(buffer, 0, bufferLength);
				}

				fout.close();
				inputStream.close();
				urlConnection.disconnect();

				try {

					XMLFileParser.documentIsXMLFile(createAbsolutePath(
							rssChannel.getCategory().getName(),
							rssChannel.getName()));
				} catch (Exception e) {

					if (downloadFile.exists()) {

						deleteFile(rssChannel);
					}

					e.printStackTrace();
					throw new URLDownloadFileException(
							R.string.exc_URLDownloadFileException_2);
				}

				return;
			}

			throw new NullEntityException(Category.class.getSimpleName());
		}

		throw new NullEntityException(RSSChannel.class.getSimpleName());
	}

	/**
	 * Metodo que se utiliza para mover un archivo de una carpeta a otra
	 * 
	 * @param fileName
	 * @param currentPath
	 * @param newPath
	 * @return
	 */
	public static boolean moveFile(String fileName, String currentPath,
			String newPath) {

		File oldFile = new File(createAbsolutePath(currentPath, fileName));
		File newFile = new File(createAbsolutePath(newPath, fileName));

		return oldFile.renameTo(newFile);
	}

	/**
	 * Metodo que lee un archivo XML
	 * 
	 * @param rssChannel
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static LinkedList<RSSLink> readFileXML(RSSChannel rssChannel)
			throws SAXException, IOException, ParserConfigurationException,
			NullEntityException {

		return new XMLFileParser().parse(createAbsolutePath(rssChannel
				.getCategory().getName(), rssChannel.getName()));
	}

	/**
	 * Metodo se utiliza cuando se edita un RSSChannel.
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean renameFile(String pathFiles, String oldName,
			String newName) {

		File oldFile = new File(createAbsolutePath(pathFiles, oldName));
		File newFile = new File(createAbsolutePath(pathFiles, newName));

		return oldFile.renameTo(newFile);
	}

	/**
	 * Metodo utilizado cuando se edita el nombre de una categoria. Le cambia el
	 * nombre a una carpeta
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean renameFolder(String oldName, String newName) {

		File oldFile = new File(createAbsolutePath(oldName, null));
		File newFile = new File(createAbsolutePath(newName, null));

		return oldFile.renameTo(newFile);
	}

	/**
	 * Metodo que retorna la ruta absoluta de un archivo o una carpeta
	 * 
	 * @param folderName
	 * @param fileName
	 * @return
	 */
	private static String createAbsolutePath(String folderName, String fileName) {

		String absolutePath = ApplicationContext
				.getStringResource(R.string.path_app_files)
				+ File.separator
				+ folderName;

		if (fileName != null) {

			absolutePath += File.separator + fileName + ".xml";
		}

		return absolutePath;
	}
}
