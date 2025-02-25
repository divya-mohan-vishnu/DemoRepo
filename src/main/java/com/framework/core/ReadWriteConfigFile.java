package com.framework.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadWriteConfigFile {

	// GET XML VALUE
	public static String GetXMLData(String configFilePath, String tagName) {
		String nodeValue = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Path paths = Paths.get(configFilePath, new String[0]);
			if (Files.exists(paths, new LinkOption[0])) {
				Document doc = db.parse(configFilePath);

				doc.getDocumentElement().normalize();
				if (doc != null) {
					NodeList nl = doc.getElementsByTagName("Root");

					for (int i = 0; i < nl.getLength(); i++) {
						Node node = nl.item(i);
						NodeList nl2 = node.getChildNodes();
						for (int i2 = 0; i2 < nl2.getLength(); i2++) {
							node = nl2.item(i2);
				//			System.out.println(node.getNodeName());
				//			System.out.println(node.getTextContent());
							if (node.getTextContent() != null && node.getNodeName().equalsIgnoreCase(tagName)) {
								nodeValue = node.getTextContent();
							}

						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return nodeValue;
	}
}