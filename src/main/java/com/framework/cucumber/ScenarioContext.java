package com.framework.cucumber;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.xml.sax.SAXException;

import com.framework.core.CustomException;
import com.framework.managers.WebDriverManager;

import cucumber.api.Scenario;

public class ScenarioContext {
    // instance of singleton class
    public static ScenarioContext scenarioContext;
    public WebDriver driver;
    public Scenario scenario;
    
 

	public ScenarioContext() {
		// TODO Auto-generated constructor stub
	}

	public WebDriver GetWebDriver(Scenario scenario){
		
	//	System.out.println(scenario.getName());
		String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
				.getParameter("projectName");
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
	
		Collection<String> getTags = scenario.getSourceTagNames();
		try {
			WebDriverManager webdrivermanager=new WebDriverManager();
			this.driver = webdrivermanager.GetWebDriverBasedonTestBrowser(getTags, projectName);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	//	System.out.println("WebDriver instance is " + driver);
		return driver;
		 
    }

    public static ScenarioContext getInstanceOfScenarioContextClass(){
        if (scenarioContext == null) {
            scenarioContext = new ScenarioContext();
        }
        return scenarioContext;
    }

    public WebDriver getDriver(){
    	driver=scenarioContext.getDriver();
        return driver;
    }
    
    public Scenario getScenario() {
        return scenario;
    }
    
}
