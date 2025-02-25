package com.framework.managers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.framework.core.Constants;
import com.framework.core.CustomException;
import com.framework.core.FrameworkOperations;

public class WebDriverManager {
	RemoteWebDriver driver;
	String hubUrl = "";
	String[] remoteUrl = new String[10];
	private static DriverType driverType;

	public WebDriver SetBrowserCapability(DriverType driverType, String remoteUrl)
			throws MalformedURLException, CustomException {
		DesiredCapabilities cap = null;

	//	System.out.println("Test Browser Name is " + driverType);
		switch (driverType) {
		case CHROME:

			cap = DesiredCapabilities.chrome();
			cap.setPlatform(Platform.WINDOWS);
			cap.setBrowserName("chrome");
			cap.setCapability("requireWindowFocus", true);
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		//	System.out.println("Chrome Capability detected");
			break;

		case FIREFOX:
			cap = DesiredCapabilities.firefox();
			cap.setPlatform(Platform.VISTA);
			cap.setCapability("marionette", false);
			cap.setBrowserName("firefox");
			cap.setCapability("requireWindowFocus", true);
			FirefoxProfile profile = new FirefoxProfile();
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(true);
			cap.setCapability(FirefoxDriver.PROFILE, profile);
			cap.setVersion("67.0.1");
			break;

		case IE:

			cap = DesiredCapabilities.internetExplorer();
			cap.setPlatform(Platform.WINDOWS);
			cap.setBrowserName("internet explorer");
			cap.setCapability("requireWindowFocus", true);
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			System.out.println("IE Capability detected");
			break;
		}

	//	System.out.println("Capability is " + cap);
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL(remoteUrl), cap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return driver;

	}

	public void goToUrl(String testUrl) throws CustomException {
		try {
			driver.get(testUrl);

		} catch (Exception ex) {
			throw new CustomException("Url not found or Session has timed out", ex);
		}
	}

	public WebDriver GetWebDriverBasedonTestBrowser(Collection<String> getTags, String projectName)
			throws ParserConfigurationException, SAXException, IOException, CustomException {
		WebDriver driver = null;
		String browser = "";
		String nodeUrl = "";
		boolean isbrowser_set = false;
		if (getTags.size() > 0) {

			for (String tag : getTags) {

				if (tag.contains("@browser")) {
					browser = tag.split("=")[1].replace("\"", "");
					FrameworkOperations frameworkOperations = new FrameworkOperations();
					HashMap<String, String> gridmap = frameworkOperations.ParseXML(projectName);
					for (Entry map : gridmap.entrySet()) {
						if (map.getValue().toString().equalsIgnoreCase(browser)) {
							nodeUrl = map.getKey().toString();
							isbrowser_set = true;
							driverType = getBrowser(map.getValue().toString());
							break;
						}
					}
		//			System.out.println(nodeUrl);
					boolean is_grid_up = CheckSeleniumGridIsUp(nodeUrl);
					if (!is_grid_up) {
						TriggerGridStartUpScripts(driverType);

					}
					driver = SetBrowserCapability(driverType, nodeUrl);
				}
			}
			if (!isbrowser_set) {
				driver = GetWebDriverfromGridConfig(projectName);
			}
		} else {
			driver = GetWebDriverfromGridConfig(projectName);
		}
		return driver;
	}

	private void TriggerGridStartUpScripts(DriverType driverType) throws IOException {
		FrameworkOperations frameworkOperations = new FrameworkOperations();
		switch (driverType) {
		case CHROME:
			frameworkOperations.TriggerBatchScript(Constants.BASE_PROJECT_PATH + "//Drivers//Grid_Chrome_StartUp.bat");
			break;

		case FIREFOX:
			frameworkOperations.TriggerBatchScript(Constants.BASE_PROJECT_PATH + "//Drivers//Grid_Firefox_StartUp.bat");
			break;

		case IE:
			frameworkOperations.TriggerBatchScript(Constants.BASE_PROJECT_PATH + "//Drivers//Grid_IE_StartUp.bat");
			break;
		}
	}

	private DriverType getBrowser(String browserName) {

		if (browserName == null || browserName.equalsIgnoreCase("chrome"))
			return DriverType.CHROME;
		else if (browserName.equalsIgnoreCase("firefox"))
			return DriverType.FIREFOX;
		else if (browserName.equals("iexplorer"))
			return DriverType.IE;
		else
			throw new RuntimeException(
					"Browser Name Key value in Configuration.properties is not matched : " + browserName);
	}

	private WebDriver GetWebDriverfromGridConfig(String projectName)
			throws ParserConfigurationException, SAXException, IOException, MalformedURLException, CustomException {
		WebDriver driver;
		String browser = "", nodeUrl = "";
		FrameworkOperations frameworkOperations = new FrameworkOperations();

		HashMap<String, String> gridmap = frameworkOperations.ParseXML(projectName);
		for (Entry map : gridmap.entrySet()) {
			browser = map.getValue().toString();
			nodeUrl = map.getKey().toString();
			driverType = getBrowser(map.getValue().toString());
			break;
		}

	//	System.out.println(nodeUrl);

		driver = SetBrowserCapability(driverType, nodeUrl);
		return driver;
	}

	public boolean CheckSeleniumGridIsUp(String nodeUrl) {
		boolean hubFlag = false;
		boolean nodeFlag = false;
		try {
			// DBDriver dbdriver = new DBDriver();
			parsegridconfigxml();
			String[] hubAddress = hubUrl.split("/");
			String hubConsole = hubAddress[0] + "//" + hubAddress[2] + "/grid/console";

			// System.out.println("Hub Console - " + hubConsole);
			HttpURLConnection urlConn = null;
			URL url = new URL(hubConsole);
			urlConn = (HttpURLConnection) url.openConnection();
			int responseCode = urlConn.getResponseCode();
			if (responseCode == 200) {
				hubFlag = true;
				System.out.println("Selenium Grid Hub is up and running");

				String[] nodeAddress = nodeUrl.split("/");
				String nodeConsole = nodeAddress[0] + "//" + nodeAddress[2] + "/grid/console";
				url = new URL(nodeConsole);
				urlConn = (HttpURLConnection) url.openConnection();
				responseCode = urlConn.getResponseCode();
				if (responseCode == 200) {
					System.out.println("Selenium Grid Node is up and running on port");
					nodeFlag = true;

				} else {
					System.out.println("Selenium Grid Node is not up and running on port");
					// dbdriver.updateRunTableStatus("Aborted", "Please start up Selenium Node",
					// runId);
					nodeFlag = false;
				}

			} else {
				System.out.println("Selenium Grid Hub is not up and running");
				// dbdriver.updateRunTableStatus("Aborted", "Please start up Selenium Hub",
				// runId);
				hubFlag = false;
			}

		} catch (Exception ex) {
			System.out.println("There is some issue with Selenium Grid Connection or Selenium Hub has not started");
		}

		if (hubFlag && nodeFlag)
			return true;
		else
			return false;

	}

	/* Parse Selenium Grid Config XML File */
	private void parsegridconfigxml() throws ParserConfigurationException, SAXException, IOException, CustomException {
		try {
			// Adding code to fetch the path dynamically.
			String directoryPath = System.getProperty("user.dir");
			directoryPath = directoryPath.replace("\\", "//");
			String file_Path = Constants.Pathdir + "//Configuration//gridConfig.xml";
		//	System.out.println(file_Path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			if (file_Path != null) {
				Document doc = dBuilder.parse(file_Path);
				doc.getDocumentElement().normalize();
				if (doc.hasChildNodes())
					xmlReader(doc.getChildNodes());
			}

		} catch (Exception ex) {

			throw new CustomException("XML Exception ", ex);
		}

	}

	/* Reading the Hub and Node details */
	private void xmlReader(NodeList nodeList) {
		String hubOrNode;

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				hubOrNode = tempNode.getNodeName();
				if (tempNode.hasAttributes() && (hubOrNode.equals("hub") || hubOrNode.equals("node"))) {
					NamedNodeMap nodeMap = tempNode.getAttributes();

					int nodeCount = 0;
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);
						if (hubOrNode.equals("hub"))
							hubUrl = node.getNodeValue();
						else {

							if (node.getNodeName().equalsIgnoreCase("url"))
								remoteUrl[nodeCount] = node.getNodeValue();

						}
					}
					if (hubOrNode.equals("node"))
						nodeCount++;
				}

				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					xmlReader(tempNode.getChildNodes());

				}
			}
		}
	}

	public enum DriverType {
		FIREFOX, CHROME, IE
	}
}
