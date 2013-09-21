package com.diegorayo.rssreader.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.diegorayo.rssreader.entitys.RSSLink;

public class XMLFileParser {

	private URL url;

	public XMLFileParser(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public LinkedList<RSSLink> parse() throws SAXException, IOException,
			ParserConfigurationException {

		LinkedList<RSSLink> listRSSLinks = new LinkedList<RSSLink>();
		RSSLink rssLink;

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(this.getConnection());
		Element root = document.getDocumentElement();
		NodeList listItems = root.getElementsByTagName("item");

		for (int i = 0; i < listItems.getLength(); i++) {
			rssLink = new RSSLink();
			Node itemRead = listItems.item(i);
			NodeList propertiesItem = itemRead.getChildNodes();

			for (int j = 0; j < propertiesItem.getLength(); j++) {

				Node propertyRead = propertiesItem.item(j);
				String propertyName = propertyRead.getNodeName();
				String nodeValue = propertyRead.getFirstChild().getNodeValue();

				if (propertyName.equalsIgnoreCase("title")) {
					rssLink.setTitle(nodeValue);
				} else if (propertyName.equalsIgnoreCase("link")) {
					rssLink.setUrl(nodeValue);
				} else if (propertyName.equalsIgnoreCase("pubdate")) {
					rssLink.setDate(nodeValue);
				} else if (propertyName.equalsIgnoreCase("description")) {
					rssLink.setDescription(nodeValue);
				}
			}
			listRSSLinks.add(rssLink);
		}

		return listRSSLinks;
	}

	public InputStream getConnection() throws IOException {
		return url.openConnection().getInputStream();
	}
}
