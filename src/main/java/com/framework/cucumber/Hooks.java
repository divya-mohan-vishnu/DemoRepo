package com.framework.cucumber;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;


import com.framework.core.Constants;
import com.framework.core.CustomException;
import com.framework.core.FrameworkOperations;
import com.framework.core.ReadWriteConfigFile;


import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;


public class Hooks {

	private Scenario scenario;
	private String configPath;
	private WebDriver driver;
	private String debugMode;
	private String projectId;
	private String runId;
	private String projectName;
	ScenarioContext scenarioContext;
	public static String hookLogs;

	@Before
	public void BeforeHookMethod(Scenario scenario) {
		this.scenario = scenario;
//	System.out.println(this.scenario.getName());
	//	System.out.println("Before Hook Method");
		scenarioContext = new ScenarioContext();
	//	driver=	scenarioContext.GetWebDriver(scenario);
	//	scenarioContext.driver=driver;
		scenarioContext.scenario=scenario;
		ScenarioContext.scenarioContext=scenarioContext;
		
	}

	@Before("@skip_scenario")
	public void skip_scenario(Scenario scenario) {

		System.out.println("SKIP SCENARIO: " + scenario.getName());
//
		Assert.assertTrue(false);

	}

	

	@AfterStep
	public void AfterStep(Scenario scenario) {
		try {
			if (scenario.isFailed()) {
				this.scenario = scenario;
				CaptureFailedScreenshot();
				// driver.close();
			}
		} finally {

		}

	}

	public void CaptureFailedScreenshot() {
		driver = ScenarioContext.scenarioContext.driver;
		FrameworkOperations frameworkOperations = new FrameworkOperations();
		String feature_name = frameworkOperations.GetFeatureName(scenario.getUri().toString());
		frameworkOperations.GetScreenShot(driver, feature_name.split("\\.")[0] + "-" + scenario.getName(),
				"Screenshot.png");
	}

	public void CaptureScreenshot(String screenName) {
		driver = ScenarioContext.scenarioContext.driver;
		scenario = ScenarioContext.scenarioContext.scenario;
		FrameworkOperations frameworkOperations = new FrameworkOperations();
		String feature_name = frameworkOperations.GetFeatureName(scenario.getUri().toString());
		frameworkOperations.GetTestEvidence(driver, feature_name.split("\\.")[0] + "-" + scenario.getName(),
				screenName);

	}

	public void PrintCustomLogs(String custom_logs) throws CustomException, SQLException {
		scenario = ScenarioContext.scenarioContext.getScenario();
		scenario.write(custom_logs);

		String debugFlag = "";
		FrameworkOperations frameworkOperations = new FrameworkOperations();
		debugMode = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
				.getParameter("debugFlag");

		if (debugMode.equals("N")) {
			// DBDriver DB = new DBDriver();
			CucumberStepListener TS = new CucumberStepListener();
			long MSID = TS.GetKeywordMasterExecutionID();
		//	System.out.println(MSID);

			String destination = "";
			String feature_name = frameworkOperations.GetFeatureName(scenario.getUri().toString());
			feature_name = feature_name.split("\\.")[0] + "-" + scenario.getName();
			String suiteName = feature_name.split("-")[0];
			suiteName = suiteName.trim();
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("projectName");
			destination = Constants.BASE_CloudServer_PATH;
			if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
				destination = new StringBuilder(destination).append(projectName).append("//resource//")
						.append("TestEvidence").append("//").append(runId).append("//").append(suiteName).append("//")
						.append(feature_name.split("-")[1].trim()).append("//").toString();

			}
			if (custom_logs.contains(".png")) {

				String[] custom_log = custom_logs.split("-");
				destination = destination + custom_log[custom_log.length - 1].trim() + ".png";
				custom_logs = custom_logs + ";" + destination;
			}

			
			hookLogs = custom_logs;
		}
		
	}
	
	@After
	public void tearDown(Scenario scenario) {
		driver = scenarioContext.driver;
		if (driver != null) {
			try {

				if (scenario.isFailed()) {
					final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
					scenario.embed(screenshot, "image/png");
					// CaptureFailedScreenshot();
				}
			} finally {
				driver.quit();
			}
		}
	}
}
