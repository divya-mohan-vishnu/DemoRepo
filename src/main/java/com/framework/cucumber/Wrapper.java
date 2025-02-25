package com.framework.cucumber;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Wrapper {
	
	
	/* Test Suite Class */
	public static class TestSuiteMap {
		private long id;
		// private String testsuiteId;
		private String testSuiteDesc;
		private String testsuiteName;
		private String executionStatus;
		private String testBrowser;
		private String testUrl;
		private HashMap<Long, List<TestCaseMap>> hashmapTestCase;
		private TestRun testRun;
		private int totalTestCount;
		private String projectName;

		public TestSuiteMap() {
			testRun = new TestRun();
		}

		/*
		 * public String getTestsuiteId() { return testsuiteId; }
		 * 
		 * public void setTestsuiteId(String testsuiteId) { this.testsuiteId =
		 * testsuiteId; }
		 */
		public String getTestBrowser() {
			return testBrowser;
		}

		public void setTestBrowser(String testBrowser) {
			this.testBrowser = testBrowser;
		}

		public String getTestUrl() {
			return testUrl;
		}

		public void setTestUrl(String testUrl) {
			this.testUrl = testUrl;
		}

		public HashMap<Long, List<TestCaseMap>> getHashmapTestCase() {
			return hashmapTestCase;
		}

		public void setHashmapTestCase(HashMap<Long, List<TestCaseMap>> hashmapTestCase) {
			this.hashmapTestCase = hashmapTestCase;
		}

		public TestRun getTestRun() {
			return testRun;
		}

		public void setTestRun(TestRun testRun) {
			this.testRun = testRun;
		}

		public String getTestsuiteName() {
			return testsuiteName;
		}

		public void setTestsuiteName(String testsuiteName) {
			this.testsuiteName = testsuiteName;
		}

		public int getTotalTestCount() {
			return totalTestCount;
		}

		public void setTotalTestCount(int totalTestCount) {
			this.totalTestCount = totalTestCount;
		}

		public String getExecutionStatus() { // whether the suite has been executed
			return executionStatus;
		}

		public void setExecutionStatus(String executionStatus) {
			this.executionStatus = executionStatus;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getTestSuiteDesc() {
			return testSuiteDesc;
		}

		public void setTestSuiteDesc(String testSuiteDesc) {
			this.testSuiteDesc = testSuiteDesc;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}
	}

	/* Test Case Class */
	public static class TestCaseMap {
		private long id;
		// private String testCaseId;
		private String testCaseName;
		private String testCaseDesc;
		private Boolean testExecutionFlag;
		private long testcase_ExecutionId;
		private String testExecutionStatus;
		private Date testExecutionTime;
		private List<String> testInfos;
		private String testBrowser;
		private String nodeUrl;
		// private String testSuiteId;
		private Boolean isExecute;
		private int testCount;
		private List<String> testcaseLogs;

		private HashMap<Long, List<StepMap>> stepHashMap;

		/*
		 * public String getTestCaseId() { return testCaseId; }
		 * 
		 * public void setTestCaseId(String testCaseId) { this.testCaseId = testCaseId;
		 * }
		 */

		public String getTestCaseName() {
			return testCaseName;
		}

		public void setTestCaseName(String testCaseName) {
			this.testCaseName = testCaseName;
		}

		public Boolean getTestExecutionFlag() {
			return testExecutionFlag;
		}

		public void setTestExecutionFlag(Boolean testExecutionFlag) {
			this.testExecutionFlag = testExecutionFlag;
		}

		
		public int getTestCount() {
			return testCount;
		}

		public void setTestCount(int testCount) {
			this.testCount = testCount;
		}

		public String getTestExecutionStatus() {
			return testExecutionStatus;
		}

		public void setTestExecutionStatus(String testExecutionStatus) {
			this.testExecutionStatus = testExecutionStatus;
		}

		public Date getTestExecutionTime() {
			return testExecutionTime;
		}

		public void setTestExecutionTime(Date testExecutionTime) {
			this.testExecutionTime = testExecutionTime;
		}

		public List<String> getTestInfos() {
			return testInfos;
		}

		public void setTestInfos(List<String> testInfos) {
			this.testInfos = testInfos;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getTestCaseDesc() {
			return testCaseDesc;
		}

		public void setTestCaseDesc(String testCaseDesc) {
			this.testCaseDesc = testCaseDesc;
		}

		public String getTestBrowser() {
			return testBrowser;
		}

		public void setTestBrowser(String testBrowser) {
			this.testBrowser = testBrowser;
		}

		public String getNodeUrl() {
			return nodeUrl;
		}

		public void setNodeUrl(String nodeUrl) {
			this.nodeUrl = nodeUrl;
		}

		public Boolean getIsExecute() {
			return isExecute;
		}

		public void setIsExecute(Boolean isExecute) {
			this.isExecute = isExecute;
		}

		public long getTestcase_ExecutionId() {
			return testcase_ExecutionId;
		}

		public void setTestcase_ExecutionId(long testcase_ExecutionId) {
			this.testcase_ExecutionId = testcase_ExecutionId;
		}

		public List<String> getTestcaseLogs() {
			return testcaseLogs;
		}

		public void setTestcaseLogs(List<String> testcaseLogs) {
			this.testcaseLogs = testcaseLogs;
		}

		public HashMap<Long, List<StepMap>> getStepHashMap() {
			return stepHashMap;
		}

		public void setStepHashMap(HashMap<Long, List<StepMap>> stepHashMap) {
			this.stepHashMap = stepHashMap;
		}

		/*
		 * public String getTestSuiteId() { return testSuiteId; }
		 * 
		 * public void setTestSuiteId(String testSuitId) { this.testSuiteId =
		 * testSuitId; }
		 */

	}
	
	public static class TestRun {
		private long runId;
		private String testRunStatus;
		private Date testRunDate;

		public String getTestRunStatus() {
			return testRunStatus;
		}

		public void setTestRunStatus(String testRunStatus) {
			this.testRunStatus = testRunStatus;
		}

		public Date getTestRunDate() {
			return testRunDate;
		}

		public void setTestRunDate(Date testRunDate) {
			this.testRunDate = testRunDate;
		}

		public long getRunId() {
			return runId;
		}

		public void setRunId(long runId) {
			this.runId = runId;
		}

	}
public static class StepMap {
	
	
	private long StepId;
	private String failedScreenshotName;
	private String failedScreenshotDet;
	private String logs;
	@JsonProperty("StepName")
	private String StepName;

	@JsonProperty("Status")
	private String Status;
	
	@JsonProperty("StepDescription")
	private String StepDescription;

	@JsonProperty("ExecutionTime")
	private String ExecutionTime;

	@JsonProperty("LogName")
	private String LogName;

	@JsonProperty("LogContent")
	private String LogContent;

	@JsonIgnore
	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	@JsonIgnore
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
	
	@JsonIgnore
	public String getStepDescription() {
		return StepDescription;
	}

	public void setStepDescription(String stepDescription) {
		StepDescription = stepDescription;
	}

	@JsonIgnore
	public String getExecutionTime() {
		return ExecutionTime;
	}

	public void setExecutionTime(String executionTime) {
		ExecutionTime = executionTime;
	}

	@JsonIgnore
	public String getLogName() {
		return LogName;
	}

	public void setLogName(String logName) {
		LogName = logName;
	}

	@JsonIgnore
	public String getLogContent() {
		return LogContent;
	}

	public void setLogContent(String logContent) {
		LogContent = logContent;
	}

	public long getStepId() {
		return StepId;
	}

	public void setStepId(long stepId) {
		StepId = stepId;
	}

	public String getFailedScreenshotName() {
		return failedScreenshotName;
	}

	public void setFailedScreenshotName(String failedScreenshotName) {
		this.failedScreenshotName = failedScreenshotName;
	}

	public String getFailedScreenshotDet() {
		return failedScreenshotDet;
	}

	public void setFailedScreenshotDet(String failedScreenshotDet) {
		this.failedScreenshotDet = failedScreenshotDet;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}
}



public static class TestCaseService {
	@JsonProperty("TestCaseName")
	private String TestCaseName;
	@JsonProperty("TestCaseDescription")
	private String TestCaseDescription;
	@JsonProperty("ExecutionTime")
	private String ExecutionTime;
	@JsonProperty("TestExecutionStatus")
	private String TestExecutionStatus;
	@JsonProperty("Steps")
	private List<StepMap> Steps;

	@JsonIgnore
	public String getTestCaseName() {
		return TestCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		TestCaseName = testCaseName;
	}

	@JsonIgnore
	public String getTestCaseDescription() {
		return TestCaseDescription;
	}

	public void setTestCaseDescription(String testCaseDescription) {
		TestCaseDescription = testCaseDescription;
	}

	@JsonIgnore
	public String getExecutionTime() {
		return ExecutionTime;
	}

	public void setExecutionTime(String executionTime) {
		ExecutionTime = executionTime;
	}

	@JsonIgnore
	public String getTestExecutionStatus() {
		return TestExecutionStatus;
	}

	public void setTestExecutionStatus(String testExecutionStatus) {
		TestExecutionStatus = testExecutionStatus;
	}

	@JsonIgnore
	public List<StepMap> getSteps() {
		return Steps;
	}

	public void setSteps(List<StepMap> steps) {
		Steps = steps;
	}
}

public static class ResultUpdation {
	@JsonProperty("TestSuiteName")
	private String TestSuiteName;

	@JsonProperty("TestCaseList")
	private List<TestCaseService> TestCaseList;

	@JsonIgnore
	public String getTestSuiteName() {
		return TestSuiteName;
	}

	public void setTestSuiteName(String testSuiteName) {
		TestSuiteName = testSuiteName;
	}

	@JsonIgnore
	public List<TestCaseService> getTestCaseList() {
		return TestCaseList;
	}

	public void setTestCaseList(List<TestCaseService> testCaseList) {
		TestCaseList = testCaseList;
	}
}

public static class ResultUpdationOBJECT {
	@JsonProperty("RunId")
	private String RunId;

	@JsonProperty("User")
	private String User;
	

	@JsonProperty("IsLastTC")
	private String IsLastTC;


	@JsonProperty("ResultUpdations")
	private List<ResultUpdation> ResultUpdations;
	
	@JsonIgnore
	public String getIsLastTC() {
		return IsLastTC;
	}

	public void setIsLastTC(String isLastTC) {
		IsLastTC = isLastTC;
	}

	@JsonIgnore
	public String getRunId() {
		return RunId;
	}

	public void setRunId(String runId) {
		RunId = runId;
	}

	@JsonIgnore
	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		User = user;
	}

	@JsonIgnore
	public List<ResultUpdation> getResultUpdations() {
		return ResultUpdations;
	}

	public void setResultUpdations(List<ResultUpdation> resultUpdations) {
		ResultUpdations = resultUpdations;
	}
}

public static class ResultUpdationMain {

	@JsonProperty("input")
	private ResultUpdationOBJECT input;

	@JsonIgnore
	public ResultUpdationOBJECT getInput() {
		return input;
	}

	public void setInput(ResultUpdationOBJECT input) {
		this.input = input;
	}
}}