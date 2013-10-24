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

import com.diegorayo.readerss.entitys.RSSLink;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class XMLFileParser {

	/**
	 * 
	 * @param url
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public LinkedList<RSSLink> parse(String url) throws SAXException,
			IOException, ParserConfigurationException {

		LinkedList<RSSLink> listRSSLinks = new LinkedList<RSSLink>();
		RSSLink rssLink;

		File fileXML = new File(url);
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
					rssLink.setTitle(this.getTextNode(propertyRead));
				} else if (propertyName.equalsIgnoreCase("link")) {
					rssLink.setUrl(propertyRead.getFirstChild().getNodeValue());
				} else if (propertyName.equalsIgnoreCase("pubdate")) {
					rssLink.setDate(propertyRead.getFirstChild().getNodeValue());
				} else if (propertyName.equalsIgnoreCase("description")) {
					rssLink.setDescription(this.getTextNode(propertyRead));
				}
			}
			listRSSLinks.add(rssLink);
		}

		return listRSSLinks;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static boolean documentIsXMLFile(String urlLocal) throws SAXException,
			IOException, ParserConfigurationException {

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(urlLocal);
		Element root = document.getDocumentElement();
		NodeList listItems = root.getElementsByTagName("channel");

		if (listItems.getLength() == 1) {
			return true;
		}

		return false;
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

		return text.toString();
	}

}
