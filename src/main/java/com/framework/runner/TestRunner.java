package com.framework.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.core.Constants;
import com.framework.core.CustomException;
import com.framework.core.FrameworkOperations;
import com.framework.core.PropertyFileConfig;
import com.framework.core.ReadWriteConfigFile;
import com.framework.cucumber.Wrapper;
import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(features = { "Features" }, glue = { "com.project.stepdefinition", "com.framework.cucumber",
		"com.framework.main" }, plugin = { "html:target/cucumber-html-report", "json:target/cucumber.json",
				"pretty:target/cucumber-pretty.txt", "usage:target/cucumber-usage.json",
				"junit:target/cucumber-results.html" })
public class TestRunner {
	private String configPath;

	private TestNGCucumberRunner testNGCucumberRunner;

	private String debugMode;

	private String projectId;

	private String runId;

	private String projectName;

	Properties prop;

	@BeforeSuite
	public void BeforeSuiteClass() throws Exception {
		try {
			prop = PropertyFileConfig.readPropertiesFile();
			Intialize();
			this.testNGCucumberRunner = new TestNGCucumberRunner(getClass());
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	@BeforeClass(alwaysRun = true)
	public void setUpClass() throws Exception {
		try {
			this.testNGCucumberRunner = new TestNGCucumberRunner(getClass());
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	@Test(description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {
		this.testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());
	}

	@DataProvider
	public Object[][] scenarios() {
		return this.testNGCucumberRunner.provideScenarios();
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() throws Exception {
		this.testNGCucumberRunner.finish();
	}

	private void Intialize() throws IOException {
		String filePathdir = System.getProperty("user.dir");
		filePathdir = filePathdir.replace("\\", "//");

		FrameworkOperations.ClearRunidFolders(filePathdir);
		
		this.configPath = filePathdir + "//Configuration//Config.xml";
		this.debugMode = ReadWriteConfigFile.GetXMLData(this.configPath, "DebugFlag");
		this.projectId = ReadWriteConfigFile.GetXMLData(this.configPath, "ProjectId");
		String AIDebug = "";
		String jiraProjectKey = "";
		String ratio = "";
		AIDebug = ReadWriteConfigFile.GetXMLData(this.configPath, "AIDefectLogin");
		if (AIDebug.equalsIgnoreCase("Y")) {
			jiraProjectKey = ReadWriteConfigFile.GetXMLData(this.configPath, "JiraProjectKey");
			ratio = ReadWriteConfigFile.GetXMLData(this.configPath, "Ratio");
		}

		runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		if (runId == null || runId.length() == 0) {
			this.runId = FrameworkOperations.SendGetMethod(prop.getProperty("RUNID_MS") + this.projectId);
			
			this.runId = this.runId.split(":")[1];
			this.runId = this.runId.substring(0, this.runId.length() - 1);
		}
		this.projectName = FrameworkOperations.SendGetMethod(prop.getProperty("ProjectName_MS") + this.runId);
		System.out.println();
		this.projectName = this.projectName.split(":")[1];
		this.projectName = this.projectName.substring(1, this.projectName.length() - 2);
		// System.out.println("Execution In Progress For RunID: " + this.runId + "
		// Project Name: " + this.projectName);
		FrameworkOperations.ConfigureCucumberOptions(this.projectName, this.runId, this.debugMode);
		FrameworkOperations.ConfigureTestDataInputs(this.projectName, this.runId, this.debugMode);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("projectName",
				this.projectName);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("runId", this.runId);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("debugFlag", this.debugMode);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("aiDefect", AIDebug);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("jiraProjectkey",
				jiraProjectKey);
		Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().addParameter("ratio", ratio);
	}

	@AfterSuite
	public void AfterSuite() throws Exception {
		try {

			String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("projectName");
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String isUI = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("isUI");
			FrameworkOperations frameworkOperations = new FrameworkOperations();
			HashMap<String, String> gridmap = frameworkOperations.ParseXML(projectName);
			String nodeUrl = "";
			for (Map.Entry<String, String> map : gridmap.entrySet()) {
				if (map.getValue().equals("CHROME")) {
					nodeUrl = map.getKey().toString();
					break;
				}
			}
			String AIDefectLogin = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("aiDefect");
			String ratio = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("ratio");
			String jiraProjectkey = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("jiraProjectkey");
			if (AIDefectLogin != null) {
				if (AIDefectLogin.equals("Y")) {
					String URL = "https://automateddefects-testenablement-dev.apps.internal.appcanvas.net/jiraUtils/createDefects?runId="
							+ runId + "&ratio=" + ratio + "&projectkey=" + jiraProjectkey;
					String str1 = MyGETRequest(URL);
					// System.out.println(str1);
					Wrapper.ResultUpdationMain resultUpdationMainobject = new Wrapper.ResultUpdationMain();
					Wrapper.ResultUpdationOBJECT resultUpdationObject = new Wrapper.ResultUpdationOBJECT();
					Wrapper.ResultUpdation resultUpdation = new Wrapper.ResultUpdation();
					Wrapper.StepMap stepMap = new Wrapper.StepMap();
					Wrapper.TestCaseService testcaseMap = new Wrapper.TestCaseService();
					List<Wrapper.StepMap> stepList = new ArrayList<>();
					List<Wrapper.ResultUpdation> resultUpdationList = new ArrayList<>();
					List<Wrapper.TestCaseService> testCaseList = new ArrayList<>();
					stepMap.setStepName("AIAutoDefectReporting");
					stepMap.setStepDescription("AIAutoDefectReporting");
					stepMap.setStatus("PASSED");
					stepMap.setExecutionTime((new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")).format(new Date()));
					if (str1 == null || str1.equals("[]")) {
						stepMap.setLogName("There are no defects to add");
						stepMap.setLogContent(
								"Currently No defects to add or similar Defects are already added into Jira");
					} else {
						stepMap.setLogName("Defect");
						stepMap.setLogContent(str1);
					}
					stepList.add(stepMap);
					resultUpdation.setTestSuiteName(projectName + "- AI Defect Logging");
					resultUpdationObject.setRunId(runId);
					resultUpdationObject.setUser(System.getProperty("user.name"));
					resultUpdationObject.setIsLastTC("Y");
					testcaseMap.setSteps(stepList);
					testcaseMap.setTestExecutionStatus("PASSED");
					testcaseMap.setTestCaseName("JiraAutoDefectReporting");
					testcaseMap.setTestCaseDescription("Defects are getting added to " + jiraProjectkey);
					testcaseMap.setExecutionTime((new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")).format(new Date()));
					resultUpdationObject.setResultUpdations(resultUpdationList);
					testCaseList.add(testcaseMap);
					resultUpdation.setTestCaseList(testCaseList);
					resultUpdationList.add(resultUpdation);
					resultUpdationObject.setResultUpdations(resultUpdationList);
					resultUpdationMainobject.setInput(resultUpdationObject);
					ObjectMapper mapper = new ObjectMapper();
					String resultUpdationMxInput = mapper.writerWithDefaultPrettyPrinter()
							.writeValueAsString(resultUpdationMainobject);
					stepList.clear();
					testCaseList.clear();
					resultUpdationList.clear();
					FrameworkOperations.SendPostMethod(resultUpdationMxInput, prop.getProperty("ResultUpdation_MS"));
				} else {
					CheckForAI(projectName, runId);
				}
			} else {
				CheckForAI(projectName, runId);
			}
			// Thread.sleep(20000L);
			int count = 0;
			String response = "";
			String newURL = prop.getProperty("TestExeStatus_MS") + runId;
			try {
				do {
					response = CheckforTestStatus(newURL);
					// System.out.println(response);
					if (response != "null")
						continue;
					TimeUnit.SECONDS.sleep(5L);
					count++;
				} while (response.equals("null"));
			} catch (Exception exception) {
			}
			String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("debugFlag");
			if (debugFlag.equals("N")) {
				List<String> executionResult = new ArrayList<>();
				String[] args = { runId, projectName, nodeUrl };
				String URL = prop.getProperty("Report_MS") + runId;
				String reportFilePath = MyGETRequest(URL);
				// System.out.println(reportFilePath);
				try {
					if (reportFilePath == null|| reportFilePath.length() == 0)
						do {
							executionResult = CheckforExecutionStatus(newURL);
							// System.out.println(executionResult.get(0));
						} while (((String) executionResult.get(0)).equals("InProgress"));
				} catch (Exception exception) {
				}
				// reportFilePath = executionResult.get(1);
				if (reportFilePath != null) {
					reportFilePath = reportFilePath.split(",")[0];
					reportFilePath = reportFilePath.replaceAll(" ", "%20");
					reportFilePath = reportFilePath.split(":")[1];
					System.out.println("Report FilePath is " + reportFilePath);
					File file = new File(reportFilePath);
					if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
						throw new Exception("Browser Application is not supported");
				}

			
			} else {
				GenerateReportfromJson(Long.parseLong(runId), projectName, nodeUrl);
			}
		} catch (Exception exception) {
			
		}
		System.exit(0);
	}

	private void GenerateReportfromJson(long runId, String projectName, String nodeUrl)
			throws IOException, JSONException, CustomException {
		FrameworkOperations objFrameworkOperations = new FrameworkOperations();
		List<Wrapper.TestSuiteMap> suitemapList = objFrameworkOperations.ConvertJsontoReport(runId, projectName);
		objFrameworkOperations.GenerateLocalReport(suitemapList, projectName, runId);
	}

	private void CheckForAI(String projectName, String runId) throws JsonProcessingException {
		String timeStamp = (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")).format(new Date());
		Wrapper.ResultUpdationMain resultUpdationMainobject = new Wrapper.ResultUpdationMain();
		Wrapper.ResultUpdationOBJECT resultUpdationObject = new Wrapper.ResultUpdationOBJECT();
		Wrapper.ResultUpdation resultUpdation = new Wrapper.ResultUpdation();
		Wrapper.StepMap stepMap = new Wrapper.StepMap();
		Wrapper.TestCaseService testcaseMap = new Wrapper.TestCaseService();
		List<Wrapper.StepMap> stepList = new ArrayList<>();
		List<Wrapper.ResultUpdation> resultUpdationList = new ArrayList<>();
		List<Wrapper.TestCaseService> testCaseList = new ArrayList<>();
		stepMap.setStepName("AI defectReporting is not enabled for this execution");
		stepMap.setStepDescription("AI defectReporting is not enabled for this execution");
		stepMap.setStatus("PASSED");
		stepMap.setExecutionTime(timeStamp);
		stepMap.setLogContent("AI defectReporting is not enabled for this execution");
		stepList.add(stepMap);
		resultUpdation.setTestSuiteName(projectName);
		resultUpdationObject.setRunId(runId);
		resultUpdationObject.setUser(System.getProperty("user.name"));
		resultUpdationObject.setIsLastTC("Y");
		testcaseMap.setSteps(stepList);
		testcaseMap.setTestExecutionStatus("PASSED");
		testcaseMap.setTestCaseName("AI defectReporting is not enabled for this execution");
		testcaseMap.setTestCaseDescription("AI defectReporting is not enabled for this execution");
		testcaseMap.setExecutionTime((new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")).format(new Date()));
		resultUpdationObject.setResultUpdations(resultUpdationList);
		testCaseList.add(testcaseMap);
		resultUpdation.setTestCaseList(testCaseList);
		resultUpdationList.add(resultUpdation);
		resultUpdationObject.setResultUpdations(resultUpdationList);
		resultUpdationMainobject.setInput(resultUpdationObject);
		ObjectMapper mapper = new ObjectMapper();
		String resultUpdationMxInput = mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(resultUpdationMainobject);
		// System.out.println(resultUpdationMxInput);
		stepList.clear();
		testCaseList.clear();
		resultUpdationList.clear();
		FrameworkOperations.SendPostMethod(resultUpdationMxInput, prop.getProperty("ResultUpdation_MS"));
	}

	public static String encode(String queryParameter) {
		String encodedQueryParameter = null;
		try {
			encodedQueryParameter = URLEncoder.encode(queryParameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedQueryParameter;
	}

	public String MyGETRequest(String URL) throws Exception {
		StringBuffer response = new StringBuffer();
		URL urlForGetRequest = new URL(URL);
		String readLine = null;
		String Path = null;
		try {
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));

				while ((readLine = in.readLine()) != null)
					response.append(readLine);
				in.close();
				// System.out.println("JSON String Result " + response.toString());
				String[] value = (new String(response)).split(":");
				Path = value[1];
			} else {
				System.out.println("GET NOT WORKED");
			}
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {

		} catch (Exception exception) {
		}
		return response.toString();
	}

	public String CheckforTestStatus(String URL) throws Exception {
		URL urlForGetRequest = new URL(URL);
		String readLine = null;
		String testStatus = null;
		try {
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null)
					response.append(readLine);
				in.close();
				String jsonString = response.toString();
				JSONObject jsonObject = new JSONObject(jsonString);
				testStatus = jsonObject.getString("testStatus");
			} else {
				System.out.println("GET NOT WORKED");
			}
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {

		} catch (Exception exception) {
		}
		return testStatus;
	}

	public List<String> CheckforExecutionStatus(String URL) throws Exception {
		URL urlForGetRequest = new URL(URL);
		String readLine = null;
		List<String> execution = new ArrayList<>();
		try {
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null)
					response.append(readLine);
				in.close();
				String jsonString = response.toString();
				JSONObject jsonObject = new JSONObject(jsonString);
				execution.add(jsonObject.getString("executionStatus"));
				execution.add(jsonObject.getString("reportLocation"));
			} else {
				System.out.println("GET NOT WORKED");
			}
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {

		} catch (Exception exception) {
		}
		return execution;
	}
}
