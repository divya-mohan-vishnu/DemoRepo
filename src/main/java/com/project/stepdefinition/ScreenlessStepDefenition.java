package com.project.stepdefinition;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.framework.core.CustomException;
import com.framework.cucumber.Hooks;
import com.framework.cucumber.ScenarioContext;
import com.project.reusablemethods.Constants;
import com.project.reusablemethods.ScreenlessUtils;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

public class ScreenlessStepDefenition {

	WebDriver driver;
	String projectName;
	Scenario scenario;
	String errorMessage;
	String PCI;
	ScenarioContext scenarioContext = ScenarioContext.getInstanceOfScenarioContextClass();

	Hooks hook = new Hooks();

	/************************************
	 * Screenless Step Definition Start
	 * @throws Exception
	 **************************************/
//	@Given("I trigger the Service Call using Endpoint URL")
//	public void i_trigger_the_Service_Call_using_Endpoint_URL(Map<String, String> datatableMap) throws Throwable  {
//		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();
//		objScreenlessUtils.IntialSetUp();
//		HashMap<String, String> configMap = objScreenlessUtils
//				.GetConfigSheetDetails(Constants.DirectoryPath + Constants.ScreenlessTestDataFilePath,datatableMap.get("ReferenceId"));
//		objScreenlessUtils.SendGetMethod(configMap.get("EndPointUrl"));
//		hook.PrintCustomLogs("Triggered service call");
//	}


//	@Given("I trigger the ServiceCall Step")
//	public void i_trigger_the_ServiceCall_Initial(Map<String, String> datatableMap) throws Exception   {
//		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();
////		objScreenlessUtils.IntialSetUp();
////		HashMap<String, String> configMap = objScreenlessUtils
////				.GetConfigSheetDetails(Constants.DirectoryPath + Constants.ScreenlessTestDataFilePath);
////		objScreenlessUtils.GenerateToken(configMap);
//
	//	String serviceCallResponse = objScreenlessUtils.TriggerServiceCall(datatableMap);
//		hook.PrintCustomLogs("ServiceCall Triggered");
//		if (serviceCallResponse != "")
//			hook.PrintCustomLogs(datatableMap.get("ReferenceId") + "^" + serviceCallResponse);
//		else
//			hook.PrintCustomLogs("Response File is not generated");
//	}
	
	@Given("I trigger the Service Call using Endpoint URL")
	public void i_trigger_the_Service_Call_using_Endpoint_URL(Map<String, String> datatableMap) throws Throwable  {
		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();
		objScreenlessUtils.IntialSetUp();
//		HashMap<String, String> configMap = objScreenlessUtils
//				.GetConfigSheetDetails(Constants.DirectoryPath + Constants.ScreenlessTestDataFilePath,datatableMap.get("ReferenceId"));
//		objScreenlessUtils.SendGetMethod(configMap.get("EndPointUrl"));
		
			String serviceCallResponse = objScreenlessUtils.TriggerServiceCallofGetMethod(datatableMap);
		hook.PrintCustomLogs("ServiceCall Triggered");
		if (serviceCallResponse != "")
			hook.PrintCustomLogs(datatableMap.get("ReferenceId") + "^" + serviceCallResponse);
		else
			hook.PrintCustomLogs("Response File is not generated");
	}
	
	@Given("I trigger the Service Call using Endpoint URL with post content")
	public void i_trigger_the_Service_Call_using_Endpoint_URL_POST(Map<String, String> datatableMap) throws Throwable  {
		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();
		objScreenlessUtils.IntialSetUp();
//		HashMap<String, String> configMap = objScreenlessUtils
//				.GetConfigSheetDetails(Constants.DirectoryPath + Constants.ScreenlessTestDataFilePath,datatableMap.get("ReferenceId"));
//		objScreenlessUtils.SendGetMethod(configMap.get("EndPointUrl"));
		
			String serviceCallResponse = objScreenlessUtils.TriggerServiceCallofPostMethod(datatableMap);
		hook.PrintCustomLogs("ServiceCall Triggered");
		if (serviceCallResponse != "")
			hook.PrintCustomLogs(datatableMap.get("ReferenceId") + "^" + serviceCallResponse);
		else
			hook.PrintCustomLogs("Response File is not generated");
	}

	@And("I validate the Service Call Response with the TestData Inputs")
	public void i_validate_the_Service_Call_Response_with_the_TestData_Inputs(Map<String, String> datatableMap) throws Throwable {

		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();

		String response = objScreenlessUtils.ResponseValidationOfGetMethod(datatableMap);

		hook.PrintCustomLogs("ResponseValidation Step Triggered");
		hook.PrintCustomLogs(datatableMap.get("ReferenceId") + "^" + response);
	}

	
	@And("I validate the Database values with the TestData Inputs")
	public void i_trigger_the_DB_Validation(Map<String, String> datatableMap) throws Throwable {

		ScreenlessUtils objScreenlessUtils = new ScreenlessUtils();
		String response = objScreenlessUtils.TriggerDatabaseValidation(datatableMap);

		hook.PrintCustomLogs("DatabaseValidation Step Triggered");
		hook.PrintCustomLogs(datatableMap.get("ReferenceId") + "^" + response);

	}
	
	/************************************
	 * Screenless Step Definition End
	 * 
	 * @throws Exception
	 **************************************/

}
