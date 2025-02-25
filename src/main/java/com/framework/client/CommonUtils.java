package com.framework.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.framework.core.CustomException;
import com.project.reusablemethods.Constants;

public class CommonUtils {

	public static String SendPostMethod(String responseContent, String endPointUrl) {
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();
		try {

			URL url = new URL(endPointUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(500000);
			connection.setReadTimeout(500000);

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Host", "application/json");
			OutputStream os = connection.getOutputStream();
			byte[] input = responseContent.getBytes("utf-8");
			os.write(input, 0, input.length);

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {

				response.append(responseLine.trim());
			}
			System.out.println(response.toString());

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());
			System.out.println("Error : " + e.getMessage());
			try {
				System.out.println(connection.getResponseMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (e.getMessage().contains("Server returned HTTP response code: 500 for URL"))
				response.append(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		return response.toString();
	}

	public String GetEndPointUrl(String testDataPath) {
		RequestExcelUtility excelUtility = new RequestExcelUtility();

		excelUtility.setExcel(testDataPath, "Config");
		return excelUtility.getdata(2, 10);

	}

	public static String SetPowerCurveID(String demoResponse) {
		String powercurveID = "";
		InputStream responseinputStream = new ByteArrayInputStream(demoResponse.getBytes(StandardCharsets.UTF_8));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			// System.out.println(demoResponse);
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expr;
			String xpathExpression = "";
			if (demoResponse.contains("PowercurveID")) {
				xpathExpression = "//PowercurveID/text()";
			} else if (demoResponse.contains("ApplicationID")) {
				xpathExpression = "//ApplicationID/text()";

			} else if (demoResponse.contains("PowerCurveId")) {
				xpathExpression = "//PowerCurveId/text()";

			} else if (demoResponse.contains("PowerCurveID")) {
				xpathExpression = "//PowerCurveID/text()";

			}

			doc = db.parse(responseinputStream);
			String xml = GetCDATAXMLBlock(demoResponse);
			// System.out.println(xml);
			if (xml != "") {
				InputStream cdatainputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
				Document cdataDoc = null;
				try {
					cdataDoc = db.parse(cdatainputStream);
				} catch (Exception ex) {
				}

				if (cdataDoc != null) {

					expr = xpath.compile(xpathExpression);

					NodeList nl = (NodeList) expr.evaluate(cdataDoc, XPathConstants.NODESET);

					if (nl.getLength() > 0) {
						Node node = nl.item(0);
						powercurveID = node.getTextContent();
					}

				}
			} else {
				expr = xpath.compile(xpathExpression);

				NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

				if (nl.getLength() > 0) {
					Node node = nl.item(0);
					powercurveID = node.getTextContent();
				}

			}

		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return powercurveID;

	}

	public static String GetCDATAXMLBlock(String resp) {
		InputStream responseinputStream = new ByteArrayInputStream(resp.getBytes(StandardCharsets.UTF_8));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		String cdataContent = "";
		try {
			db = dbf.newDocumentBuilder();
			// System.out.println(resp);

			doc = db.parse(responseinputStream);
			NodeList nodes = doc.getElementsByTagName("item");
			for (int i = 0; i < nodes.getLength(); i++) {
				if (cdataContent == "") {
					Node node = nodes.item(i);
					// System.out.println(node.getNodeName());
					// System.out.println(node.getNodeValue());

					// System.out.println(node.getParentNode().getNodeName());
					// System.out.println(node.getParentNode().getNodeValue());
					if (node.hasChildNodes()) {
						NodeList childNodes = node.getChildNodes();
						for (int j = 0; j < childNodes.getLength(); j++) {
							Node child = childNodes.item(j);
							if (child.getNodeName().contains("#cdata-section")) {
								cdataContent = child.getTextContent();

								break;
							}

							// System.out.println(child.getParentNode().getNodeName());
							// System.out.println(child.getParentNode().getNodeValue());
						}
					}
				}
			}

		} catch (Exception ex) {
		}
		return cdataContent;
	}

	public void WriteToFile(String runId, String content, String responseFilePath) {
		try {
			File file = new File(responseFilePath);
			file.getParentFile().mkdirs();
			FileWriter filewriter = new FileWriter(responseFilePath);
			filewriter.write(content);
			filewriter.flush();

			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String GetFileContent(String responseFilePath) {
		String fileContent = "";
		JSONParser h = new JSONParser();

		FileReader reader = null;
		try {
			reader = new FileReader(responseFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner myReader = new Scanner(reader);
		while (myReader.hasNextLine()) {
			fileContent += myReader.nextLine();
		}
		myReader.close();
		return fileContent;
	}

	public boolean SetTestStatus(Map<String, String> dbJson) {
		boolean isStatus = true;
		for (Entry dbresult : dbJson.entrySet()) {
			if (dbresult.getKey().toString().equalsIgnoreCase("testStatus")
					&& (dbresult.getValue().toString().equalsIgnoreCase("FAILED"))) {
				isStatus = false;
				break;
			}
			if (dbresult.getKey().toString().equalsIgnoreCase("tableResultList")) {

				if (dbresult.getValue() != null) {
					String[] arr = dbresult.getValue().toString().split(",");
					for (String val : arr) {
						// System.out.println(val);
						if (val.trim().equalsIgnoreCase("validationStatus=FAILED")) {

							isStatus = false;
							break;
						}
					}

				}
			}

		}
		return isStatus;
	}

	/* Convert File to Document */
	public Document fileToDocumentGenerator(String templateFilePath) throws CustomException {
		Document doc_template = null;
		try {
			StringBuilder sb_template = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new FileReader(templateFilePath))) {
				String sCurrentLine = "";
				while ((sCurrentLine = br.readLine()) != null) {
					sb_template.append(sCurrentLine.trim());
				}
			}

			// System.out.println(sb_template.toString());
			InputStream templateinputStream = new ByteArrayInputStream(
					sb_template.toString().getBytes(StandardCharsets.UTF_16));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc_template = db.parse(templateinputStream);

		} catch (Exception ex) {

			throw new CustomException("Soap Request/Response Error ", ex);
		}
		return doc_template;
	}

	/* Convert Xml String to Document */
	public Document XmlStringToDocumentGenerator(String xmlString) throws CustomException {
		try {
			// System.out.println(xmlString);
			InputStream templateinputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc_template = db.parse(templateinputStream);
			doc_template.getDocumentElement().normalize();// Exception
			return doc_template;
		} catch (Exception ex) {

			throw new CustomException("Soap Request/Response Error ", ex);
		}
	}

	/* Convert Document to String */
	public String documentToStringGenerator(Document doc_template)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		String soapRequestContent;
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc_template), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		soapRequestContent = output.replaceAll(">\\s+<", "><");
		soapRequestContent = soapRequestContent.replaceAll("space", "  ");

		return soapRequestContent;
	}

	public boolean GetStepStatus(String logContent) {
		boolean isStatus = true;

		String[] arr = logContent.toString().split(",");
		for (String val : arr) {
			// System.out.println(val);
			if (val.trim().contains("\"testStatus\":\"FAILED\"")) {

				isStatus = false;
				break;
			}
		}
		return isStatus;
	}
}
