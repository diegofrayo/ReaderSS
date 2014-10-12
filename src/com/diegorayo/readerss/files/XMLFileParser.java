package com.diegorayo.readerss.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.annotation.SuppressLint;

import com.diegorayo.readerss.entitys.RSSLink;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class XMLFileParser {

	/**
	 * Metodo para leer un archivo XML en el dispositivo, y obtener sus
	 * propiedades
	 * 
	 * @param url
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@SuppressLint("NewApi")
	public LinkedList<RSSLink> parse(String url) throws SAXException,
			IOException, ParserConfigurationException, SAXParseException {

		LinkedList<RSSLink> listRSSLinks = new LinkedList<RSSLink>();
		RSSLink rssLink;

		File fileXML = new File(url);

		if (fileXML.exists()) {

			FileInputStream fileInputStream = new FileInputStream(fileXML);
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(fileInputStream);

			Element root = document.getDocumentElement();
			NodeList listItems = root.getElementsByTagName("item");

			for (int i = 0; i < listItems.getLength(); i++) {

				rssLink = new RSSLink();
				Node itemRead = listItems.item(i);
				NodeList propertiesItem = itemRead.getChildNodes();

				for (int j = 0; j < propertiesItem.getLength(); j++) {

					Node propertyRead = propertiesItem.item(j);
					String propertyName = propertyRead.getNodeName();

					if (propertyName.equalsIgnoreCase("title")) {

						String text = this.getTextNode(propertyRead);
						// text = text.replaceAll("&#xE1;", "á")
						// .replaceAll("&#xE9;", "é")
						// .replaceAll("&#xED;", "í")
						// .replaceAll("&#xF3;", "ó")
						// .replaceAll("&#xFA;", "ú")
						// .replaceAll("&#xC1;", "Á")
						// .replaceAll("&#xC9;", "É")
						// .replaceAll("&#xCD;", "Í")
						// .replaceAll("&#xD3;", "Ó")
						// .replaceAll("&#xDA;", "Ú")
						// .replaceAll("&#xA0;", "#")
						// .replaceAll("&#xBF;", "¿")
						// .replaceAll("&#x22;", "\"")
						// .replaceAll("&#x201C;", "\"")
						// .replaceAll("&#x201D;", "\"")
						// .replaceAll("&#xF1;", "ñ")
						// .replaceAll("&#xA1;", "¡")
						// .replaceAll("&#x26;", "&")
						// .replaceAll("&#x3E;", ">")
						// .replaceAll("&#x27;", "'")
						// .replaceAll("&#x2026;", "...");
						rssLink.setTitle(text);
					} else if (propertyName.equalsIgnoreCase("link")) {

						rssLink.setUrl(propertyRead.getFirstChild()
								.getNodeValue());
					} else if (propertyName.equalsIgnoreCase("pubdate")) {

						rssLink.setDate(propertyRead.getFirstChild()
								.getNodeValue().substring(0, 25));
					} else if (propertyName.equalsIgnoreCase("dc:date")) {

						rssLink.setDate(propertyRead.getFirstChild()
								.getNodeValue().substring(0, 10));
					}

					// else if (propertyName.equalsIgnoreCase("description")) {
					// rssLink.setDescription(this.getTextNode(propertyRead));
					// }
				}

				listRSSLinks.add(rssLink);
			}
		}

		return listRSSLinks;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private String getTextNode(Node node) {

		StringBuilder text = new StringBuilder();
		NodeList fragments = node.getChildNodes();

		for (int k = 0; k < fragments.getLength(); k++) {
			text.append(fragments.item(k).getNodeValue());
		}

		String string = text.toString();
		return string;
	}

	/**
	 * Este metodo sirve para saber si el documento descargado al RSSChannel, es
	 * realmente un archivo XML valido
	 * 
	 * @param rssChannel
	 * @return
	 * @throws Exception
	 */
	public static boolean documentIsXMLFile(String absolutePathFile)
			throws Exception {

		File fileXML = new File(absolutePathFile);

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(fileXML);
		Element root = document.getDocumentElement();
		NodeList listItems = root.getElementsByTagName("channel");

		if (listItems.getLength() == 1) {

			return true;
		}

		return false;
	}

}
