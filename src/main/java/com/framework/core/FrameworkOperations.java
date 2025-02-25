package com.framework.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import com.framework.cucumber.Wrapper.StepMap;
import com.framework.cucumber.Wrapper.TestCaseMap;
import com.framework.cucumber.Wrapper.TestSuiteMap;
import com.framework.runner.TestRunner;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

public class FrameworkOperations {
	private Semaphore outputSem;
	private String output;
	private Semaphore errorSem;
	private String error;
	private Process p;
	long feature_id = 1;
	long scenario_id = 1;
	long step_id = 1;
	String nodeValue = "";

	// GET REQUEST
	public static String SendGetMethod(String endPointUrl) {
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();
		try {

			// java.net.URL url = new URL(null, endPointUrl,new
			// sun.net.www.protocol.https.Handler());
			URL url = new URL(endPointUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			System.setProperty("java.net.useSystemProxies", "true");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			// System.out.println(response.toString());

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
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

	// POST REQUEST
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

			OutputStream os = connection.getOutputStream();
			byte[] input = responseContent.getBytes("utf-8");
			os.write(input, 0, input.length);

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			// System.out.println(response.toString());

		} catch (Exception e) {
			// System.out.println("Error : " + e.getMessage());
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

	// SET CUCUMBER OPTIONS
	public static void ConfigureCucumberOptions(String projectname, String runId, String debugFlag) throws IOException {
		String cucumberOptions = "";
		String filePathdir = System.getProperty("user.dir");
		filePathdir = filePathdir.replace("\\", "//");
		if (debugFlag.equals("Y")) {

			cucumberOptions = filePathdir + "//featurefiles//" + "// --tags @ETAF --plugin json:" + filePathdir
					+ "//resource//ExtendResults//cucumber_" + runId + ".json";
		} else {

			String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("optParam");
			if (optParam == null || optParam.length() == 0) {

				cucumberOptions = filePathdir + "//featurefiles//"
						+ "// --tags @ETAF --plugin com.framework.cucumber.CucumberStepListener ";
			} else {
				File directory = new File(filePathdir + "//" + runId + "//featurefiles//");
				boolean res = directory.mkdirs();
				String sourceFeaturefilelocation = "C://testautomation//projects//" + projectname + "//resources//"
						+ runId + "//featurefiles//";
				String destFeaturefilelocation = filePathdir + "//" + runId + "//featurefiles//";
				File src = new File(sourceFeaturefilelocation);
				File dest = new File(destFeaturefilelocation);
				FileUtils.copyDirectory(src, dest);
				cucumberOptions = filePathdir + "//" + runId + "//featurefiles//"
						+ "// --tags @ETAF --plugin com.framework.cucumber.CucumberStepListener ";
			}

			/*
			 * ChannelSftp channelSftp;
			 * 
			 * try { channelSftp = SSHJConfig.setupJsch(); channelSftp.connect();
			 * channelSftp.cd(".."); channelSftp.cd(".."); channelSftp.cd(
			 * "/C://testautomation//projects//" + projectname + "//resources//" + runId +
			 * "//featurefiles//");
			 * 
			 * Vector files = channelSftp.ls(channelSftp.pwd()); for (int i = 0; i <
			 * files.size(); i++) { LsEntry ls = (LsEntry) files.get(i);
			 * channelSftp.get(channelSftp.pwd() + ls.getFilename(), filePathdir + "//" +
			 * runId + "//Featurefile//");
			 * 
			 * }
			 * 
			 * 
			 * } catch (JSchException | IOException | SftpException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
		}

		System.setProperty("cucumber.options", cucumberOptions);
		// System.out.println("CUCUMBER OPTIONS : " +
		// System.getProperty("cucumber.options"));
	}

	public void TriggerTestNGWithOptParam(String optParam) {

		/*
		 * TestNG testng = new TestNG(); List<String> suites = Lists.newArrayList();
		 * suites.add("testng.xml"); testng.setTestSuites(suites); testng.run();
		 */

		XmlSuite xmlSuite;
		XmlTest xmlTest;
		TestNG testng = new TestNG();
		Map<String, String> testNgParams = new HashMap<String, String>();
		testNgParams.put("optParam", optParam);
		xmlSuite = new XmlSuite();
		xmlSuite.setName("Suite");
		XmlClass xmlClass = new XmlClass(TestRunner.class);
		xmlSuite.setParameters(testNgParams);
		xmlSuite.setDataProviderThreadCount(2);
		xmlTest = new XmlTest(xmlSuite);
		xmlTest.setName("Test");
		xmlTest.setVerbose(2);
		xmlTest.setThreadCount(2);

		List<XmlClass> xmlClasses = new ArrayList<XmlClass>();
		xmlClasses.add(xmlClass);
		xmlTest.setXmlClasses(xmlClasses);

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(xmlSuite);
		testng.setXmlSuites(suites); // testng.setParallel(ParallelMode.TESTS);
		testng.run();

	}

	public void TriggerTestNG() {

		/*
		 * TestNG testng = new TestNG(); List<String> suites = Lists.newArrayList();
		 * suites.add("testng.xml"); testng.setTestSuites(suites); testng.run();
		 */

		XmlSuite xmlSuite;
		XmlTest xmlTest;
		TestNG testng = new TestNG();
		Map<String, String> testNgParams = new HashMap<String, String>();
    //    testNgParams.put("optParam", "46685");
		xmlSuite = new XmlSuite();
		xmlSuite.setName("Suite");
		XmlClass xmlClass = new XmlClass(TestRunner.class);
		xmlSuite.setParameters(testNgParams);
		xmlSuite.setDataProviderThreadCount(2);
		xmlTest = new XmlTest(xmlSuite);
		xmlTest.setName("Test");
		xmlTest.setVerbose(2);
		xmlTest.setThreadCount(2);

		List<XmlClass> xmlClasses = new ArrayList<XmlClass>();
		xmlClasses.add(xmlClass);
		xmlTest.setXmlClasses(xmlClasses);

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(xmlSuite);
		testng.setXmlSuites(suites); // testng.setParallel(ParallelMode.TESTS);
		testng.run();

	}

	public String GetFeatureName(String filepath) {
		String[] featuresplit = filepath.split("/");
		String feature_name = featuresplit[featuresplit.length - 1];
		feature_name = feature_name.split(":")[0];
		return feature_name;
	}

	public String LogErrorMessage(String feature_scenario_folder, String errorMessage) throws CustomException {

		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
				.getParameter("projectName");

		String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
				.getParameter("debugFlag");
		String destination = "";
		if (debugFlag.equalsIgnoreCase("N")) {
			destination = Constants.BASE_CloudServer_PATH;
			int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1]).length();
			String suiteName = feature_scenario_folder.split("-")[0];
			suiteName = suiteName.trim();
			// System.out.println(
			// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
			// " + suiteName);

			if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
				destination = new StringBuilder(destination).append(projectName).append("//resource//")
						.append("FailedTestsScreenshots").append("//").append(runId).append("//").append(suiteName)
						.append("//").append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();

			}
		} else {
			destination = Constants.BASE_PROJECT_PATH;

			int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1]).length();
			String suiteName = feature_scenario_folder.split("-")[0];
			suiteName = suiteName.trim();
			// System.out.println(
			// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
			// " + suiteName);
			// Adding code to fetch the path dynamically.
			if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
				destination = new StringBuilder(destination).append("//resource//").append("FailedTestsScreenshots")
						.append("//").append(runId).append("//").append(suiteName).append("//")
						.append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();

			}
		}
		destination = GenerateLogFile(errorMessage, destination);

		return destination;
	}

	private String GenerateLogFile(String errorMessage, String destination) {
		PrintWriter writer = null;
		// BufferedWriter writer = null;
		try {
			// create a temporary file
			String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			destination = destination + "Log" + timeLog + ".txt";
			if (destination != null) {
				if (destination.endsWith(".txt")) {

					File file = new File(destination); // put the file inside the folder
					file.getParentFile().mkdirs();
					file.createNewFile();
					try (Writer Writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(destination), "utf-8"))) {
						Writer.write(errorMessage);
					}
				}
			}

			// File logFile = new File(destination);
			// writer = new BufferedWriter(new FileWriter(logFile));
			// writer.write(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
		return destination;
	}

	public void GetTestEvidence(WebDriver driver, String feature_scenario_folder, String screenshotName) {

		try {
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("projectName");

			String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("debugFlag");
			String destination = "";
			if (debugFlag.equalsIgnoreCase("N")) {
				destination = Constants.BASE_CloudServer_PATH;
				int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1])
						.length();
				String suiteName = feature_scenario_folder.split("-")[0];
				suiteName = suiteName.trim();
				// System.out.println(
				// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
				// " + suiteName);

				if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
					destination = new StringBuilder(destination).append(projectName).append("//resource//")
							.append("TestEvidence").append("//").append(runId).append("//").append(suiteName)
							.append("//").append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();

				}
			} else {
				destination = Constants.BASE_PROJECT_PATH;
				int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1])
						.length();
				String suiteName = feature_scenario_folder.split("-")[0];
				suiteName = suiteName.trim();
				// System.out.println(
				// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
				// " + suiteName);

				if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
					// Adding code to fetch the path dynamically.
					destination = new StringBuilder(destination).append("//resource//").append("TestEvidence")
							.append("//").append(runId).append("//").append(suiteName).append("//")
							.append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();

				}
			}

			ScreenshotUtility.takeFullPageScreenshot(driver, screenshotName, destination);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

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

	public HashMap<String, String> ParseXML(String projectName)
			throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, String> gridmap = new HashMap<String, String>();
		// String path = "C://testautomation//projects//" + projectName +
		// "//resource//Configuration//gridConfig.xml";
		// Adding code to fetch the path dynamically.
		String filePathdir = System.getProperty("user.dir");
		filePathdir = filePathdir.replace("\\", "//");
		String path = Constants.BASE_PROJECT_PATH + "//Configuration//gridConfig.xml";
		File fXmlFile = new File(path);
		if (fXmlFile.exists()) {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes())
				xmlReader(doc.getChildNodes(), gridmap);
		}

		return gridmap;
	}

	/* Reading the Hub and node details */
	private void xmlReader(NodeList nodeList, HashMap<String, String> gridmap) {
		String hubOrNode;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentItem = nodeList.item(i);
			if (currentItem.getNodeType() == Node.ELEMENT_NODE) {
				hubOrNode = currentItem.getNodeName();
				if (hubOrNode.equals("node")) {
					String url = currentItem.getAttributes().getNamedItem("url").getNodeValue();
					String browser = currentItem.getAttributes().getNamedItem("browser").getNodeValue();
					if (browser.equals("InternetExplorer"))
						browser = "IE";
					gridmap.put(url, browser.toUpperCase());
				} else {
					NodeList nl = currentItem.getChildNodes();
					xmlReader(nl, gridmap);
				}
			}
		}

	}

	public void GetScreenShot(WebDriver driver, String feature_scenario_folder, String screenshotName) {

		try {
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("projectName");
			String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("debugFlag");
			String destination = "";
			if (debugFlag.equalsIgnoreCase("N")) {
				destination = Constants.BASE_CloudServer_PATH;
				int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1])
						.length();
				String suiteName = feature_scenario_folder.split("-")[0];
				suiteName = suiteName.trim();
				// System.out.println(
				// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
				// " + suiteName);
				if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
					destination = new StringBuilder(destination).append(projectName).append("//resource//")
							.append("FailedTestsScreenshots").append("//").append(runId).append("//").append(suiteName)
							.append("//").append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();
				}
			} else {
				destination = Constants.BASE_PROJECT_PATH;

				int len = (feature_scenario_folder.split("-")[0] + '-' + feature_scenario_folder.split("-")[1])
						.length();
				String suiteName = feature_scenario_folder.split("-")[0];
				suiteName = suiteName.trim();
				// System.out.println(
				// "Folder Name: " + feature_scenario_folder + " Length: " + len + " Suite Name:
				// " + suiteName);
				if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
					// Adding code to fetch the path dynamically.
					destination = new StringBuilder(destination).append("//resource//").append("FailedTestsScreenshots")
							.append("//").append(runId).append("//").append(suiteName).append("//")
							.append(feature_scenario_folder.split("-")[1].trim()).append("//").toString();
				}
			}
			ScreenshotUtility.takeFullPageScreenshot(driver, screenshotName, destination);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

	}

	public static String SendPostMethodForLocal(String responseContent, String endPointUrl) {
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

			OutputStream os = connection.getOutputStream();
			byte[] input = responseContent.getBytes("utf-8");
			os.write(input, 0, input.length);

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			// System.out.println(response.toString());

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
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

	public void TriggerBatchScript(String batchscriptFilePath) throws IOException {
		try {
			Process p = Runtime.getRuntime().exec(batchscriptFilePath);
			p.waitFor();

		} catch (IOException ex) {

		} catch (InterruptedException ex) {

		}

	}

	private class OutputReader extends Thread {
		public OutputReader() {
			try {
				outputSem = new Semaphore(1);
				outputSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				StringBuffer readBuffer = new StringBuffer();
				BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff);
					// System.out.println(buff);
				}
				output = readBuffer.toString();
				outputSem.release();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ErrorReader extends Thread {
		public ErrorReader() {
			try {
				errorSem = new Semaphore(1);
				errorSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			BufferedReader isr = null;
			try {
				StringBuffer readBuffer = new StringBuffer();
				isr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff);
				}
				error = readBuffer.toString();
				errorSem.release();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (error.length() > 0)
				System.out.println(error);
		}
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

	public List<TestSuiteMap> ConvertJsontoReport(long runId, String projectName)
			throws IOException, JSONException, CustomException {
		List<TestSuiteMap> suitemapList;
		suitemapList = readFeatureDetails(runId, projectName);
		return suitemapList;
	}

	public List<TestSuiteMap> readFeatureDetails(long runId, String projectName) throws JSONException, IOException {
		ArrayList<TestSuiteMap> testSuiteMapList = new ArrayList<TestSuiteMap>();
		// Adding code to fetch the path dynamically.
		String filePathdir = System.getProperty("user.dir");
		filePathdir = filePathdir.replace("\\", "//");
		String jsonfilePath = filePathdir + "//resource//ExtendResults//cucumber_" + runId + ".json";
		InputStream is = new FileInputStream(jsonfilePath);
		String json_string = IOUtils.toString(is, "UTF-8");
		JSONArray root_jsonarray = new JSONArray(json_string);
		for (int i = 0; i < root_jsonarray.length(); i++) {
			TestSuiteMap featureMap = new TestSuiteMap();
			JSONObject root_jsonobject = root_jsonarray.getJSONObject(i);
			// System.out.println(root_jsonobject);
			String feature_keyword = root_jsonobject.getString("keyword");
			String feature_name = root_jsonobject.getString("uri");
			File f = new File(feature_name);
			// System.out.println(f.getName());
			// System.out.println(feature_name);
			featureMap.setId(feature_id);
			featureMap.setTestsuiteName(f.getName());
			ReadScenarioDetails(featureMap, root_jsonobject, projectName, runId);
			testSuiteMapList.add(featureMap);
			feature_id++;
		}
		return testSuiteMapList;
	}

	private void ReadScenarioDetails(TestSuiteMap featureMap, JSONObject root_jsonobject, String projectName,
			long runId) throws JSONException {
		ArrayList<TestCaseMap> testcaseMapList = new ArrayList<TestCaseMap>();
		HashMap<Long, List<TestCaseMap>> testcaseHashMap = new HashMap<Long, List<TestCaseMap>>();

		String elements = root_jsonobject.getString("elements");
		JSONArray element_jsonarray = new JSONArray(elements);
		for (int j = 0; j < element_jsonarray.length(); j++) {

			TestCaseMap scenarioMap = new TestCaseMap();
			JSONObject element_jsonobject = element_jsonarray.getJSONObject(j);
			String scenario_keyword = element_jsonobject.getString("keyword");
			String scenario_name = element_jsonobject.getString("name");
			System.out.println(scenario_name);
			scenarioMap.setId(scenario_id);
			scenarioMap.setTestCaseName(scenario_name);
			scenarioMap.setTestCaseDesc(scenario_name);

			try {
				ReadStepDetails(featureMap, scenarioMap, element_jsonobject, projectName, runId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new java.util.Date());
			Date date1 = null;
			try {
				date1 = new SimpleDateFormat("yyyy.MM.dd").parse(timeStamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			scenarioMap.setTestExecutionTime(date1);
			String browser = "";
			HashMap<String, String> gridmap = null;
			try {
				gridmap = ParseXML(projectName);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Entry map : gridmap.entrySet()) {
				browser = map.getValue().toString();
				break;
			}
			scenarioMap.setTestBrowser(browser);
			scenarioMap.setTestExecutionStatus("PASSED");
			for (List<StepMap> valueListsteps : scenarioMap.getStepHashMap().values()) {
				for (StepMap keywordMap : valueListsteps) {
					if (keywordMap.getStatus().equalsIgnoreCase("FAILED")) {
						scenarioMap.setTestExecutionStatus("FAILED");
					}
				}
			}
			testcaseMapList.add(scenarioMap);
			if (!testcaseMapList.isEmpty()) {
				testcaseHashMap.put(featureMap.getId(), testcaseMapList);
			}
			featureMap.setHashmapTestCase(testcaseHashMap);
			scenario_id++;
		}

	}

	private void ReadStepDetails(TestSuiteMap featureMap, TestCaseMap scenarioMap, JSONObject element_jsonobject,
			String projectName, long runId) throws JSONException, ParseException {

		ArrayList<StepMap> keywordMapList = new ArrayList<StepMap>();
		HashMap<Long, List<StepMap>> keywordHashMap = new HashMap<Long, List<StepMap>>();

		String steps = element_jsonobject.getString("steps");
		JSONArray step_jsonarray = new JSONArray(steps);
		for (int k = 0; k < step_jsonarray.length(); k++) {
			JSONObject step_jsonobject = step_jsonarray.getJSONObject(k);
//			System.out.println(step_jsonobject);
			String step_name = step_jsonobject.getString("name");
//			System.out.println(step_name);
			StepMap keywordmap = new StepMap();
			keywordmap.setStepId(step_id);
			keywordmap.setStepName(step_name);
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new java.util.Date());
			Date date1 = new SimpleDateFormat("yyyy.MM.dd").parse(timeStamp);
			boolean isFlag = false;
			// keywordmap.setKeywordExecutionTime(date1);

			////////////////////////////// for printing BDD Custom logs and screen shots
			////////////////////////////// ////////////////////////////////////
			try {
				JSONArray arrObj = step_jsonobject.getJSONArray("output");
				String step_custom_logs = "";
				for (int j = 0; j < arrObj.length(); j++) {
					String log = arrObj.getString(j);
					if (log.contains("There is difference in data")) {
						isFlag = true;
						keywordmap.setStatus("FAILED");
						keywordmap.setLogContent("There is difference in data");

					} else if (log.contains("java.lang.NoSuchFieldException")) {
						keywordmap.setStatus("FAILED");
						isFlag = true;
						keywordmap.setLogContent("java.lang.NoSuchFieldException");

					} else if (log.contains("TCP/IP")) {
						keywordmap.setStatus("FAILED");
						isFlag = true;
						keywordmap.setLogContent("TCP/IP Connection Error");

					} else if (log.contains("There is no Data found in Sheet")) {
						keywordmap.setStatus("FAILED");
						isFlag = true;
						keywordmap.setLogContent("There is no Data found in Sheet");
					}

					if (log.contains("^")) {
						String[] arr = log.split("\\^");
						keywordmap.setLogName(arr[0]);
						keywordmap.setLogContent(arr[1]);
						String destination = Constants.BASE_PROJECT_PATH;

						if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
							destination = new StringBuilder(destination).append("//resource//").append("TestLogFiles")
									.append("//").append(runId).append("//").append(featureMap.getTestsuiteName())
									.append("//").append(scenarioMap.getTestCaseName().replaceAll(" ", ""))
									.append(step_name.replaceAll(" ", "")).append("//").append(keywordmap.getLogName())
									+ ".json";

							// System.out.println(destination);
							File file = new File(destination);
							file.getParentFile().mkdirs();
							file.createNewFile();
							FileOutputStream fooStream = null;
							fooStream = new FileOutputStream(file, false);
							byte[] myBytes = keywordmap.getLogContent().getBytes();
							fooStream.write(myBytes);
							fooStream.close();

							file.createNewFile();
						}

						step_custom_logs += destination;

					} else {
						step_custom_logs += log;
					}
					// System.out.println(step_custom_logs);
					step_custom_logs += ";";
				}

				keywordmap.setLogs(step_custom_logs);
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
			////////////////////////////// for printing BDD Custom logs and screen shots
			////////////////////////////// ////////////////////////////////////

			String step_result = step_jsonobject.getString("result");
			JSONObject step_result_object = new JSONObject(step_result);
			String step_status = step_result_object.getString("status");
			// System.out.println(step_status);

			if (step_status.equalsIgnoreCase("failed")) {
				String error_message = step_result_object.getString("error_message");
				System.out.println(error_message);

				// Adding code to fetch the path dynamically.
				String filePathdir = System.getProperty("user.dir");
				filePathdir = filePathdir.replace("\\", "//");
				String log_filepath = filePathdir + "//resource//FailedTestsScreenshots//" + runId + "//"
						+ featureMap.getTestsuiteName().split("\\.")[0] + "//" + scenarioMap.getTestCaseName() + "//";
				log_filepath = GenerateLogFile(error_message, log_filepath);
				keywordmap.setFailedScreenshotDet(log_filepath);
				keywordmap.setStatus(step_status);
				String step_duration = step_result_object.getString("duration");
				// System.out.println(step_duration);
			} else if (step_status.equalsIgnoreCase("undefined")) {
				String error_message = "Steps are not implemented for the scenario";
				// Adding code to fetch the path dynamically.
				String filePathdir = System.getProperty("user.dir");
				filePathdir = filePathdir.replace("\\", "//");
				String log_filepath = filePathdir + "//resource//FailedTestsScreenshots//" + runId + "//"
						+ featureMap.getTestsuiteName().split("\\.")[0] + "//" + scenarioMap.getTestCaseName() + "//";
				log_filepath = GenerateLogFile(error_message, log_filepath);
				keywordmap.setFailedScreenshotDet(log_filepath);
			} else if (step_status.equalsIgnoreCase("passed")) {
				keywordmap.setStatus(step_status);
				String step_duration = step_result_object.getString("duration");
				// System.out.println(step_duration);
			}
			keywordmap.setStatus(step_status);
			if (isFlag)
				keywordmap.setStatus("FAILED");
			keywordMapList.add(keywordmap);
			if (!keywordMapList.isEmpty()) {
				keywordHashMap.put(scenarioMap.getId(), keywordMapList);
			}
			scenarioMap.setStepHashMap(keywordHashMap);
		}

	}

	public void GenerateLocalReport(List<TestSuiteMap> suitemapList, String projectName, long runId) {
		Boolean check_report_flush = Boolean.valueOf(true);
		try {
			String fileName;
			String destination_path = "";
			String testEvidencePath = "";
			String timeStamp = new Timestamp(new Date().getTime()).toString().replace(' ', '_');
			fileName = ("ExtentReport_" + runId + "_" + timeStamp.replace(':', '.') + ".html");
			fileName = fileName.replaceAll(" ", "");
			String reportLoc = "";

			destination_path = Constants.BASE_PROJECT_PATH;
			reportLoc = destination_path + "\\resource\\ExtendResults\\" + fileName;

			/*
			 * reportLoc = Constants.BASE_CloudServer_PATH + projectName +
			 * "\\resource\\ExtendResults\\" + fileName; } else { reportLoc =
			 * Constants.BASE_PROJECT_PATH + projectName + "//resource" +
			 * "//ExtendResults//" + fileName; }
			 */
			ExtentHtmlReporter htmlReports = new ExtentHtmlReporter(reportLoc);
			ExtentReports extent = new ExtentReports();
			extent.attachReporter(htmlReports);
			htmlReports.config().setReportName("Regression Testing");
			htmlReports.config().setTheme(Theme.STANDARD);

			htmlReports.config().setDocumentTitle("HtmlReportsTestResults");
			ExtentTest logger_feature;
			for (TestSuiteMap testSuiteMap : suitemapList) {
				logger_feature = extent.createTest((testSuiteMap.getTestsuiteName()));
				for (List<TestCaseMap> valueListTestcase : testSuiteMap.getHashmapTestCase().values()) {
					for (TestCaseMap testcase : valueListTestcase) {
						if (testcase.getTestExecutionStatus() != null) {
							ExtentTest logger_scenario = logger_feature.createNode(testcase.getTestCaseName());

							logger_scenario.log(Status.INFO, "Scenario : '" + testcase.getTestCaseDesc()
									+ "' Executed In '" + testcase.getTestBrowser() + "'");
							logger_scenario.log(Status.INFO, "Scenario : '" + testcase.getTestCaseDesc()
									+ "' Executed On '" + testcase.getTestExecutionTime() + "'");
							logger_scenario.log(Status.INFO, "Check Scenario Execution Details Listed Below: ");
							logger_scenario.log(Status.INFO,
									"Scenario Executed time : " + testcase.getTestExecutionTime());
							if (testcase.getTestExecutionStatus().equalsIgnoreCase("PASSED")) {
								logger_scenario.log(Status.PASS,
										"Scenario Execution Status : '" + testcase.getTestCaseName() + "' Passed");
							} else if (testcase.getTestExecutionStatus().equalsIgnoreCase("FAILED")) {
								logger_scenario.log(Status.FAIL,
										"Scenario Execution Status : '" + testcase.getTestCaseName()
												+ "' Failed, Please check the Scenario Steps for more details");
							} else if (testcase.getTestExecutionStatus().equalsIgnoreCase("SKIPPED")) {
								logger_scenario.log(Status.SKIP,
										"Test Execution Status : '" + testcase.getTestCaseName() + "' Skipped ");
							}
							if (!testcase.getTestExecutionStatus().equalsIgnoreCase("SKIPPED")) {
								int stepCount;

								if (testcase.getStepHashMap() != null) {
									for (List<StepMap> keywordList : testcase.getStepHashMap().values()) {
										stepCount = 1;
										String testCaseFunctionality;
										ExtentTest image;
										for (StepMap keyword : keywordList) {
											testCaseFunctionality = keyword.getStepName();
											logger_scenario.log(Status.INFO,
													"Test Step '" + stepCount + "' : '" + testCaseFunctionality);

											if (keyword.getStatus().equalsIgnoreCase("PASSED")) {
												logger_scenario.log(Status.PASS,
														"'" + testCaseFunctionality + "' Passed");

											} else if (keyword.getStatus().equalsIgnoreCase("FAILED")) {

												logger_scenario.log(Status.FAIL,
														"'" + testCaseFunctionality + "' Failed");

												if (keyword.getFailedScreenshotDet() != null) {
													if (!keyword.getFailedScreenshotDet().equals("")) {

														// System.out.println(
														// "Keyword GetFailed Screenshot Details Column is "
														// + keyword.getFailedScreenshotDet());

														if (keyword.getFailedScreenshotDet().contains("//")) {

															/*
															 * logger_scenario.addScreenCaptureFromPath(
															 * GetFileName(keyword.getFailedScreenshotDet(), ".png",
															 * projectName));
															 */

															String imagePath = GetFileName(
																	keyword.getFailedScreenshotDet(), ".png",
																	projectName);
															if (imagePath != "") {
																logger_scenario.log(Status.INFO,
																		" Please check the Failed Screen shot Details");
																// System.out.println("Image File Path is " +
																// imagePath);
																logger_scenario.fail("",
																		MediaEntityBuilder
																				.createScreenCaptureFromPath(imagePath)
																				.build());
															}

															// System.out.println(keyword.getFailedScreenshotDet());
															PrintBDDLogFile(keyword.getFailedScreenshotDet(),
																	logger_scenario, projectName, destination_path);

														} else {
															logger_scenario.log(Status.INFO,
																	keyword.getFailedScreenshotDet());
														}
													}
												}
											} else if (keyword.getStatus().equalsIgnoreCase("SKIPPED")) {
												logger_scenario.log(Status.FAIL,
														"'" + testCaseFunctionality + "' Skipped");
											}

											///////// Print Custom Logs along with Screen shot ///////////////////
											if (keyword.getLogs() != null) {
												logger_scenario.log(Status.INFO, "Please check the Execution Flow ");

												if (keyword.getLogs().contains(";")) {

													String[] split = keyword.getLogs().split(";");
													for (String splittedLogs : split) {
														// System.out.println(splittedLogs);
														if (splittedLogs.contains("-")) {

															if (destination_path == Constants.BASE_CloudServer_PATH) {
																testEvidencePath = destination_path + projectName + "\\"
																		+ "resource\\TestEvidence\\" + runId + "\\"
																		+ testSuiteMap.getTestsuiteName()
																				.split("\\.")[0]
																		+ "\\" + testcase.getTestCaseName() + "\\"
																		+ splittedLogs.split("-")[1].trim() + " .png";
															} else {
																testEvidencePath = destination_path + "\\"
																		+ "resource\\TestEvidence\\" + runId + "\\"
																		+ testSuiteMap.getTestsuiteName()
																				.split("\\.")[0]
																		+ "\\" + testcase.getTestCaseName() + "\\"
																		+ splittedLogs.split("-")[1].trim() + ".png";
															}

															// System.out.println(testEvidencePath);
															logger_scenario.info("", MediaEntityBuilder
																	.createScreenCaptureFromPath(testEvidencePath)
																	.build());
														} else if (CheckFilePath(splittedLogs)) {
															logger_scenario.log(Status.INFO, "<a href='" + splittedLogs
																	+ "'> " + keyword.getLogName() + "</a>");

														} else

															logger_scenario.log(Status.INFO, splittedLogs);
													}

												} else {
													logger_scenario.log(Status.INFO, "'" + keyword.getLogs() + "'");
												}
											}
											stepCount = stepCount + 1;

										}
									}
									PrintAPIFiles(logger_scenario, runId, projectName, testSuiteMap.getTestsuiteName(),
											testcase.getTestCaseName(), destination_path);
									PrintFailedLogs(logger_scenario, runId, projectName,
											testSuiteMap.getTestsuiteName(), testcase.getTestCaseName(),
											destination_path);
									if (testcase.getTestcaseLogs() != null) {
										// Adding code to fetch the path dynamically.
										if (destination_path == Constants.BASE_CloudServer_PATH) {
											testEvidencePath = destination_path + projectName + "\\"
													+ "resource\\TestEvidence\\" + runId + "\\"
													+ testSuiteMap.getTestsuiteName().split("\\.")[0] + "\\"
													+ testcase.getTestCaseName() + "\\";
										} else {
											testEvidencePath = destination_path + "\\" + "resource\\TestEvidence\\"
													+ runId + "\\" + testSuiteMap.getTestsuiteName().split("\\.")[0]
													+ "\\" + testcase.getTestCaseName() + "\\";
										}

										PrintScenarioLogs(logger_scenario, testcase, testEvidencePath);
									}

								}
							} else {
								logger_scenario.log(Status.INFO,
										"There are no keywords added for the Test Case " + testcase.getTestCaseName());
							}
						}
						// this.report.endTest(this.logger);
						extent.flush();
						check_report_flush = Boolean.valueOf(true);
					}
				}
			}

			// UpdateFileNameToDB(Constants.BASE_PROJECT_PATH + projectName +
			// "//resource//ExtendResults//" + fileName,
			// runId);

			String filePath = null;

			// Adding code to fetch the path dynamically.
			filePath = Constants.BASE_PROJECT_PATH + "//resource" + "//ExtendResults//";

			// Thread.sleep(1000L);
			if (check_report_flush.booleanValue()) {
				// webDriver.get(filePath + fileName);
				System.out.println("Report FilePath is " + filePath + fileName);
				/*
				 * webDriver.close(); webDriver.quit();
				 */
			} else {
				// webDriver.close();
				// webDriver.quit();
				System.out.println("Reports are not generated properly");
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private boolean CheckFilePath(String logPath) {
		File file = new File(logPath);

		if (file.exists())
			return true;
		else
			return false;
	}

	private void PrintScenarioLogs(ExtentTest logger_scenario, TestCaseMap testcase, String testEvidencePath)
			throws IOException {
		logger_scenario.log(Status.INFO, "Please check the Scenario Execution Flow");
		for (String scenarioLogs : testcase.getTestcaseLogs()) {
			if (scenarioLogs.contains("-")) {
				logger_scenario.log(Status.INFO, "'" + scenarioLogs.split("-")[0] + "'");
				logger_scenario.info("", MediaEntityBuilder
						.createScreenCaptureFromPath(testEvidencePath + scenarioLogs.split("-")[1].trim() + " .png")
						.build());
			} else {
				logger_scenario.log(Status.INFO, "'" + scenarioLogs + "'");
			}

		}
	}

	private void PrintAPIFiles(ExtentTest logger_scenario, long runId, String projectName, String featureName,
			String scenarioName, String destination_path) {
		// Adding code to fetch the path dynamically.
		String request_file_path = "";
		String response_file_path = "";
		if (destination_path == Constants.BASE_CloudServer_PATH) {
			request_file_path = destination_path + projectName + "//resource//APIFiles//Request//" + runId + "//"
					+ featureName.split("\\.")[0] + "//" + scenarioName;
			response_file_path = destination_path + projectName + "//resource//APIFiles//Response//" + runId + "//"
					+ featureName.split("\\.")[0] + "//" + scenarioName;
		} else {
			request_file_path = destination_path + "//resource//APIFiles//Request//" + runId + "//"
					+ featureName.split("\\.")[0] + "//" + scenarioName;
			response_file_path = destination_path + "//resource//APIFiles//Response//" + runId + "//"
					+ featureName.split("\\.")[0] + "//" + scenarioName;
		}

		File dir = new File(request_file_path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File req_child : directoryListing) {
				logger_scenario.log(Status.INFO, "<a href='" + req_child.getAbsolutePath() + "'>  Request file</a>");
			}
		}

		dir = new File(response_file_path);
		directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File resp_child : directoryListing) {
				logger_scenario.log(Status.INFO, "<a href='" + resp_child.getAbsolutePath() + "'>  Response file</a>");
			}
		}

	}

	private void PrintBDDLogFile(String logFilePath, ExtentTest logger_scenario, String projectName,
			String destination_path) throws CustomException {

		// System.out.println(logFilePath);
		// System.out.println("Log File Path is " + logFilePath);
		logger_scenario.log(Status.INFO, "Please check the Log file");
		logger_scenario.log(Status.INFO, "<a href='" + logFilePath + "'>  Log  file</a>");
	}

	private void PrintFailedLogs(ExtentTest logger_scenario, long runId, String projectName, String featureName,
			String scenarioName, String destination_path) throws IOException {
		// Adding code to fetch the path dynamically.
		String filepath = "";
		if (destination_path == Constants.BASE_CloudServer_PATH) {
			filepath = destination_path + projectName + "//resource//Logs//" + runId + "//"
					+ featureName.split("\\.")[0] + "//" + scenarioName;
		} else {
			filepath = destination_path + "//resource//Logs//" + runId + "//" + featureName.split("\\.")[0] + "//"
					+ scenarioName;
		}
		File dir = new File(filepath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {

			for (File req_child : directoryListing) {
				InputStream is = new FileInputStream(req_child.getAbsolutePath());
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));
				String line = buf.readLine();
				StringBuilder sb = new StringBuilder();
				while (line != null) {
					sb.append(line).append("\n");
					line = buf.readLine();
				}
				int flag = 1;
				String fileAsString = sb.toString();
				// System.out.println("Contents : " + fileAsString);
				String[] details = fileAsString.split(";");
				if (details != null) {
					if (details.length > 1) {
						for (String det : details) {
							if (!det.equals(""))
								if (flag <= 1) {
									logger_scenario.log(Status.INFO,
											"List of Difference with Expected Output File is listed below :");
								}
							logger_scenario.log(Status.INFO, det);
						}

					} else {
						logger_scenario.log(Status.INFO, details[0]);
					}
				}
			}
		} else {
			// logger_scenario.log(Status.INFO, "There is no difference with the Expected
			// Output File");
		}

	}

	private String GetFileName(String failedScreenshotDet, String fileExt, String projectName) throws CustomException {
		String screenshotFilePath = "";

		if (fileExt.equals(".png")) {
			String[] split_path = failedScreenshotDet.split("//");
			split_path = Arrays.copyOf(split_path, split_path.length - 1);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < split_path.length; i++) {
				builder.append(split_path[i]).append("//");
			}
			failedScreenshotDet = builder.toString();

			if (failedScreenshotDet != "" && failedScreenshotDet.contains("//")) {
				File dir = new File(failedScreenshotDet);
				File[] files = dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(fileExt);
					}
				});

				for (File file : files) {
					// System.out.println(file);
					screenshotFilePath = file.getAbsolutePath();

					screenshotFilePath = screenshotFilePath.substring(27);
					String[] array = screenshotFilePath.split("//");
					String append = "";
					for (String string : array) {
						append = append + string + "\\";
					}
					append = append.substring(0, append.length() - 1);

					screenshotFilePath = Constants.BASE_PROJECT_PATH + append;

				}

			}
		}

		// System.out.println(screenshotFilePath);
		return screenshotFilePath;
	}

	public String loopThroughJson(Object input, String nodeKey) throws JSONException {

		try {
			if (input instanceof JSONObject) {

				Iterator<?> keys = ((JSONObject) input).keys();

				while (keys.hasNext()) {

					String key = (String) keys.next();

					if (!(((JSONObject) input).get(key) instanceof JSONArray))
						if (((JSONObject) input).get(key) instanceof JSONObject) {
							loopThroughJson(((JSONObject) input).get(key), nodeKey);
						} else {
							// System.out.println(key + "=" + ((JSONObject) input).get(key));
							if (key.toString().equals(nodeKey)) {
								nodeValue = ((JSONObject) input).get(key).toString();
								break;
							}
						}
					else
						loopThroughJson(new JSONArray(((JSONObject) input).get(key).toString()), nodeKey);
				}
			}

			if (input instanceof JSONArray) {
				for (int i = 0; i < ((JSONArray) input).length(); i++) {
					JSONObject a = ((JSONArray) input).getJSONObject(i);
					loopThroughJson(a, nodeKey);
				}
			}
		} catch (

		Exception ex) {

		}

		return nodeValue;
	}

	public static void ConfigureTestDataInputs(String projectName, String runId, String debugFlag) {

		String filePathdir = System.getProperty("user.dir");
		if (debugFlag.equals("N")) {

			String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("optParam");
			if (optParam != null) {
				filePathdir = filePathdir.replace("\\", "//");
				File directory = new File(filePathdir + "//" + runId + "//testdata//");
				boolean res = directory.mkdirs();
				String sourceinputfilelocation = "C://testautomation//projects//" + projectName + "//testdata//";
				String destinputfilelocation = filePathdir + "//" + runId + "//testdata//";
				File src = new File(sourceinputfilelocation);
				File dest = new File(destinputfilelocation);
				try {
					FileUtils.copyDirectory(src, dest);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				directory = new File(filePathdir + "//" + runId + "//inputtemplates//");
				res = directory.mkdirs();
				sourceinputfilelocation = "C://testautomation//projects//" + projectName + "//inputtemplates//";
				destinputfilelocation = filePathdir + "//" + runId + "//inputtemplates//";
				src = new File(sourceinputfilelocation);
				dest = new File(destinputfilelocation);
				try {
					FileUtils.copyDirectory(src, dest);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		}

	}

	public static void ClearRunidFolders(String directory) {
		try {
			File fin = new File(directory);
			for (File file : fin.listFiles()) {				
				String str = file.getName();
				if (str.matches("[0-9]+")) {					
					FileDeleteStrategy.FORCE.delete(file);
				}
			}
		} catch (Exception ex) {

			System.out.println(ex);

		}
	}

}
