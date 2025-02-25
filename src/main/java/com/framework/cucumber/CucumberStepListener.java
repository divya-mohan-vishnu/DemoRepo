package com.framework.cucumber;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.testng.Reporter;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.core.CustomException;
import com.framework.core.FrameworkOperations;
import com.framework.core.PropertyFileConfig;
import com.framework.cucumber.Wrapper.ResultUpdation;
import com.framework.cucumber.Wrapper.ResultUpdationMain;
import com.framework.cucumber.Wrapper.ResultUpdationOBJECT;
import com.framework.cucumber.Wrapper.StepMap;
import com.framework.cucumber.Wrapper.TestCaseMap;
import com.framework.cucumber.Wrapper.TestCaseService;
import com.framework.cucumber.Wrapper.TestRun;
import com.framework.cucumber.Wrapper.TestSuiteMap;

import cucumber.api.PickleStepTestStep;
import cucumber.api.TestCase;
import cucumber.api.TestStep;
import cucumber.api.event.ConcurrentEventListener;
import cucumber.api.event.EmbedEvent;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestCaseStarted;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.api.event.WriteEvent;
import gherkin.pickles.PickleTag;

public class CucumberStepListener implements ConcurrentEventListener {

	TestCaseMap scenarioMap;
	public static long MSID;
	public static long stepid = 1;;
	HashMap<Long, String> step_map_det = new HashMap<Long, String>();
	HashMap<Long, String> scenario_map_det = new HashMap<Long, String>();

	// Service Implementation
	ResultUpdationOBJECT resultUpdationObject = new ResultUpdationOBJECT();
	ResultUpdationMain resultUpdationMainobject = new ResultUpdationMain();
	List<StepMap> stepList = new ArrayList<StepMap>();
	List<TestCaseService> testCaseList = new ArrayList<TestCaseService>();
	List<ResultUpdation> resultUpdationList = new ArrayList<ResultUpdation>();
	TestCaseService testcaseMap = new TestCaseService();
	ResultUpdation resultUpdation = new ResultUpdation();
	ResultUpdationOBJECT resultUpdateObject = new ResultUpdationOBJECT();

	private EventHandler<TestCaseStarted> testCaseStartedHandler = new EventHandler<TestCaseStarted>() {

		@Override
		public void receive(TestCaseStarted event) {
			try {
				handleTestSourceRead(event);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CustomException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	private void handleTestSourceRead(TestCaseStarted event)
			throws IOException, NumberFormatException, CustomException, InterruptedException {
		try {

			// System.out.println("Cucumber Option is");
			// System.out.println(System.getProperty("cucumber.options"));
			long feature_Id = 0;
			long scenario_Id = 0;
			String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("projectName");
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			// micro service for run ID and Project name
			TestCase testcase = event.testCase;
			File f = new File(testcase.getUri().toString());
			resultUpdation.setTestSuiteName(f.getName());// + "-" + testcase.getName()
			resultUpdationObject.setRunId(runId);
			resultUpdationObject.setUser(System.getProperty("user.name"));
			resultUpdationObject.setIsLastTC("N");

			TestSuiteMap featureMap = new TestSuiteMap();
			FrameworkOperations frameworkOperations = new FrameworkOperations();
			Collection<PickleTag> getTags = testcase.getTags();
			String testBrowser = GetTestBrowser(getTags, projectName);
			String feature_name = testcase.getUri().toString();
			feature_name = frameworkOperations.GetFeatureName(feature_name);

			featureMap.setProjectName(projectName);
			TestRun objTestRun = new TestRun();
			objTestRun.setRunId(Long.parseLong(runId));
			featureMap.setTestRun(objTestRun);

			featureMap.setId(feature_Id);
			scenario_map_det.put(scenario_Id, testcase.getName());
			scenarioMap = new TestCaseMap();
			scenarioMap.setId(scenario_Id);
			scenarioMap.setTestCaseName(testcase.getName());
			AddEntriesToStepMap(testcase);
			featureMap.setProjectName("BDDProject");
			// System.out.println("TC event handler started");
			// System.out.println(event.testCase.getUri());
			// System.out.println(event.testCase.getTestSteps());
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

	}

	private void AddEntriesToStepMap(TestCase testcase) throws CustomException, SQLException {

		List<TestStep> testSteps = testcase.getTestSteps();
		for (TestStep testStep : testSteps) {

			if (testStep instanceof PickleStepTestStep) {
				PickleStepTestStep pts = (PickleStepTestStep) testStep;
				step_map_det.put(stepid++, pts.getStepText());

			}

		}

	};

	private EventHandler<TestCaseFinished> testCaseFinishedHandler = new EventHandler<TestCaseFinished>() {

		@Override
		public void receive(TestCaseFinished event) {

			try {
				handleTestSourceRead(event);
			} catch (CustomException e) {
			} catch (InterruptedException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void handleTestSourceRead(TestCaseFinished event) throws CustomException, InterruptedException {
			TestCase testcase = event.testCase;
			for (Entry scenariodet : scenario_map_det.entrySet()) {
				if (scenariodet.getValue().equals(testcase.getName())) {
					try {
						String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
						testcaseMap.setSteps(stepList);
						testcaseMap.setTestExecutionStatus(event.result.getStatus().toString());
						testcaseMap.setTestCaseName(testcase.getName());
						testcaseMap.setTestCaseDescription(testcase.getName());
						testcaseMap.setExecutionTime(timeStamp);
						testCaseList.add(testcaseMap);
						resultUpdation.setTestCaseList(testCaseList);
						resultUpdationList.add(resultUpdation);
						resultUpdationObject.setResultUpdations(resultUpdationList);
						resultUpdationMainobject.setInput(resultUpdationObject);
						ObjectMapper mapper = new ObjectMapper();
						// Printing Result Updation Microservice Payload
						String resultUpdationMxInput = mapper.writerWithDefaultPrettyPrinter()
								.writeValueAsString(resultUpdationMainobject);
						// System.out.println(resultUpdationMxInput);
						stepList.clear();
						testCaseList.clear();
						resultUpdationList.clear();
						// Calling service to send content

						/*
						 * FrameworkOperations.SendPostMethod(resultUpdationMxInput,
						 * "https://etafresultupdate-testenablement-dev.apps.internal.appcanvas.net/resultupdation"
						 * );
						 */

						Properties prop = PropertyFileConfig.readPropertiesFile();
						FrameworkOperations.SendPostMethodForLocal(resultUpdationMxInput,
								prop.getProperty("ResultUpdation_MS"));
						System.out.println("completed");
						// Adding wait time to get the DB update complete
						// Thread.sleep(30000);//(180000)
						// TimeUnit.SECONDS.sleep(60);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// Thread.sleep(500);
			}
			step_map_det.clear();
		}
	};

	private EventHandler<TestStepStarted> testStepStartedHandler = new EventHandler<TestStepStarted>() {

		@Override
		public void receive(TestStepStarted event) {
			/*
			 * try { handleTestSourceRead(event); } catch (CustomException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch (InterruptedException
			 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 */
		}

		private void handleTestSourceRead(TestStepStarted event) throws CustomException, InterruptedException {
			TestStep step = event.testStep;
			if (step instanceof PickleStepTestStep) {
				// Thread.sleep(500);
				PickleStepTestStep pts = (PickleStepTestStep) step;
				String stepname = pts.getStepText();
				// System.out.println(stepname);
			}
			// System.out.println("Running step:" + event.testStep.toString());
		}
		// AddStepMasterEntrytoDatabase()
	};

	private EventHandler<TestStepFinished> testStepfinishedHandler = new EventHandler<TestStepFinished>() {

		@Override
		public void receive(TestStepFinished event) {

			try {
				handleTestSourceRead(event);
			} catch (CustomException e) {
			}

		}

		private void handleTestSourceRead(TestStepFinished event) throws CustomException {

			/*
			 * org.openqa.selenium.WebDriver ((TakesScreenshot)
			 * DriverManager.getwebDriver()).getScreenshotAs(OutputType.BYTES);
			 */
			TestCase testcase = event.getTestCase();
			Hooks objhook = new Hooks();
			// System.out.println(testcase.getUri());
			// System.out.println("Scenario name for Step" + event.getTestCase().getName());
			TestStep step = event.testStep;
			if (step instanceof PickleStepTestStep) {
				PickleStepTestStep pts = (PickleStepTestStep) step;
				String stepname = pts.getStepText();
				// System.out.println(stepname);
				StepMap stepMap = new StepMap();
				for (Entry stepdet : step_map_det.entrySet()) {
					// System.out.println(stepdet);
					if (stepdet.getValue().equals(stepname)) {
						// System.out.println(event.result.getStatus().toString());
						String errorFilePath = "";
						MSID = Long.parseLong(stepdet.getKey().toString());
						if (event.result.getStatus().toString().equals("FAILED")) {
							FrameworkOperations frameworkOperations = new FrameworkOperations();
							String feature_name = testcase.getUri().toString();

							feature_name = frameworkOperations.GetFeatureName(feature_name);
							// T errorFilePath = frameworkOperations.LogErrorMessage(
							// T feature_name.split("\\.")[0] + "-" + testcase.getName(),
							// T event.result.getErrorMessage().toString());
							// System.out.println("Log content failed ");
							// System.out.println("Failed message"+errorFilePath);
						}
						try {
							String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
							stepMap.setStepName(stepname);
							stepMap.setStepDescription(stepname);
							stepMap.setStatus(event.result.getStatus().toString());
							stepMap.setExecutionTime(timeStamp);
							if (event.result.getStatus().toString().equals("FAILED")) {
								stepMap.setLogName("Failed Log content");
								stepMap.setLogContent(event.result.getError().toString());
								// System.out.println("get error "+event.result.getError().toString());
							} else if (event.result.getStatus().toString().equals("SKIPPED")) {
								stepMap.setLogName("Skipped");
								stepMap.setLogContent("Skipped");
							}

							else {
								if (objhook.hookLogs != "") {
									if (objhook.hookLogs.contains("^")) {
										String[] arr = objhook.hookLogs.split("\\^");
										stepMap.setLogName(arr[0]);
										stepMap.setLogContent(arr[1]);
										if (arr[1].equals("null")) {
											stepMap.setStatus("FAILED");
										}
										try {
											String Lcontent = stepMap.getLogContent();
											if (Lcontent.startsWith("There is difference in data")) {
												Lcontent = Lcontent.substring(Lcontent.indexOf('{'));
											}
											
											//changes on 26-07-2023 for jsonarray response
											
											//org.json.JSONObject objJson = new org.json.JSONObject(Lcontent);
											//FrameworkOperations frameworkOperations = new FrameworkOperations();
											//boolean getStatus = frameworkOperations.GetStepStatus(objJson.toString());
											
											boolean getStatus=false;
											 FrameworkOperations frameworkOperations = new FrameworkOperations();
											if(Lcontent.startsWith("{")) {    //newly added this condition for jsonobject
											     org.json.JSONObject objJson = new org.json.JSONObject(Lcontent);
											     getStatus = frameworkOperations.GetStepStatus(objJson.toString());
											}      
											if(Lcontent.startsWith("[")) {    //newly added this condition for jsonarray
											     org.json.JSONArray objJson = new org.json.JSONArray(Lcontent);
											     getStatus = frameworkOperations.GetStepStatus(objJson.toString());
											} 
											      
										 if (!getStatus)
												stepMap.setStatus("FAILED");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
									if (objhook.hookLogs.contains("There is difference in data")) {
										stepMap.setStatus("FAILED");
										stepMap.setLogContent(objhook.hookLogs);

									} else if (objhook.hookLogs.contains("java.lang.NoSuchFieldException")) {
										stepMap.setStatus("FAILED");
										stepMap.setLogContent(objhook.hookLogs);

									} else if (objhook.hookLogs.contains("TCP/IP")) {
										stepMap.setStatus("FAILED");
										stepMap.setLogContent(objhook.hookLogs);

									} else if (objhook.hookLogs.contains("There is no Data found in Sheet")) {
										stepMap.setStatus("FAILED");
										stepMap.setLogContent(objhook.hookLogs);
									} else {
										stepMap.setLogContent(objhook.hookLogs);
									}
								}
							}
							stepList.add(stepMap);
							objhook.hookLogs = "";
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}
					}
				}

			}
		}

	};

	private EventHandler<EmbedEvent> embedEventhandler = new EventHandler<EmbedEvent>() {

		@Override
		public void receive(EmbedEvent event) {
			byte[] screenshot = event.data;
			String logs = event.mimeType;
			// System.out.println(logs);
		}
	};

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);
		publisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);
		publisher.registerHandlerFor(TestStepStarted.class, testStepStartedHandler);
		publisher.registerHandlerFor(TestStepFinished.class, testStepfinishedHandler);
		publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);
		publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);

	}

	private EventHandler<WriteEvent> writeEventhandler = new EventHandler<WriteEvent>() {

		@Override
		public void receive(WriteEvent event) {
			String scenario_logs = event.text;
			// System.out.println(scenario_logs);
			long scenarioId = scenarioMap.getId();
			String scenarioName = String.valueOf(scenarioId);
			// System.out.println(scenarioId);
		}
	};

	public String GetTestBrowser(Collection<PickleTag> getTags, String projectName)
			throws ParserConfigurationException, SAXException, IOException {
		String browser = "";
		if (getTags.size() > 0) {
			for (PickleTag tag : getTags) {
				if (tag.getName().contains("@browser")) {
					browser = tag.getName().split("=")[1];
				}
			}
		} else {
			FrameworkOperations frameworkOperations = new FrameworkOperations();
			HashMap<String, String> gridmap = frameworkOperations.ParseXML(projectName);
			for (Entry map : gridmap.entrySet()) {
				browser = map.getValue().toString();
				break;
			}
		}
		return browser;
	}

	public long GetKeywordMasterExecutionID() {

		return MSID;
	}

}
