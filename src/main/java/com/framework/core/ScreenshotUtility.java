package com.framework.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.veracode.annotation.FilePathCleanser;

public class ScreenshotUtility {

	private static String getScreenshot(WebDriver driver, String screenshotName, String destinationfolderpath)
			throws CustomException {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			destinationfolderpath = new StringBuilder(destinationfolderpath).append("//").append(screenshotName)
					.append(" ").append(".png").toString();
			if (destinationfolderpath != null) {
				if (destinationfolderpath.endsWith(".png")) {
					File finalDestination = new File(destinationfolderpath);
					FileUtils.copyFile(source, finalDestination);
				}
			}

		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error  - FilePath Exception ", ex);
		}

		return destinationfolderpath;
	}

	/*
	 * Method to take Passed or Failed Screenshot to PassedTestsScreenshots and
	 * FailedTestsScreenshots respectively
	 */
	public static String takeScreenshot(WebDriver webDriver, String failedOrPassedFolder, String suiteTcFolderName,
			String keywordName, String projectName) throws CustomException {
		try {
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("debugFlag");
			int len = (suiteTcFolderName.split("-")[0] + '-' + suiteTcFolderName.split("-")[1]).length();
			String destination, suiteName = suiteTcFolderName.split("-")[0];
			suiteName = suiteName.trim();
	//		System.out.println("Folder Name: " + suiteTcFolderName + " Length: " + len + " Suite Name: " + suiteName);
			destination = "";
			if (projectName.matches("[a-zA-Z0-9-_ ]*")) {
				if (debugFlag.equalsIgnoreCase("N")) {
					destination = Constants.BASE_CloudServer_PATH;
					// Adding code to fetch the path dynamically.
					destination = new StringBuilder(destination).append(projectName).append("\\resource\\")
							.append(failedOrPassedFolder).append("\\").append(suiteName).append("\\")
							.append(suiteTcFolderName.split("-")[1].trim()).append("\\")
							.append(suiteTcFolderName.substring(len + 1).replace(':', '.')).toString();
				} else {
					destination = Constants.BASE_PROJECT_PATH;
					// Adding code to fetch the path dynamically.
					destination = new StringBuilder(destination).append("//resource//").append(failedOrPassedFolder)
							.append("//").append(suiteName).append("//").append(suiteTcFolderName.split("-")[1].trim())
							.append("//").append(suiteTcFolderName.substring(len + 1).replace(':', '.')).toString();
				}
			}
			String screenshotPath = getScreenshot(webDriver, keywordName, destination);
			return screenshotPath;
		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error ", ex);

		}
	}

	/* Method to take all Screenshots to TestEvidence Folder */
	public static String takeAllScreenshots(WebDriver driver, String suiteTcFolderName, String keywordName,
			String screenshotName, String projectName, String timestamp) throws CustomException {
		try {

			suiteTcFolderName = suiteTcFolderName.split("-")[0] + '-' + suiteTcFolderName.split("-")[1];
			keywordName = keywordName + " ";
	//		System.out.println(suiteTcFolderName);
			String destination = Constants.BASE_PROJECT_PATH;
			// Adding code to fetch the path dynamically.
			destination = new StringBuilder(destination).append("//resource//").append("TestEvidence").append("//")
					.append(suiteTcFolderName.split("-")[0]).append("//").append(suiteTcFolderName.split("-")[1])
					.append("//").append(keywordName).append(timestamp.replace(":", ".")).toString();
			String screenshotPath = getScreenshot(driver, keywordName, destination);
			return screenshotPath;

		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error ", ex);

		}
	}

	public static String takeTestsScreenshots(WebDriver driver, String suiteTcFolderName, String keywordName,
			String screenshotName, String projectName, String timestamp) throws CustomException {
		try {
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String debugFlag = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("debugFlag");
			suiteTcFolderName = suiteTcFolderName.split("-")[0] + '-' + suiteTcFolderName.split("-")[1];
			keywordName = keywordName;

	//		System.out.println(suiteTcFolderName);
			String destination = "";
			if (debugFlag.equals("N")) {
				destination = Constants.BASE_CloudServer_PATH;
				destination = new StringBuilder(destination).append(projectName).append("\\resource\\")
						.append("TestEvidence").append("\\").append(runId).append("\\")
						.append(suiteTcFolderName.split("-")[0]).append("\\").append(suiteTcFolderName.split("-")[1])
						.append("\\").append(keywordName).toString();

			} else {
				destination = Constants.BASE_PROJECT_PATH;
				// Adding code to fetch the path dynamically.
				destination = new StringBuilder(destination).append("//resource//").append("TestEvidence").append("//")
						.append(runId).append("//").append(suiteTcFolderName.split("-")[0]).append("//")
						.append(suiteTcFolderName.split("-")[1]).append("//").append(keywordName).toString();
			}
			screenshotName = screenshotName + " " + (timestamp.replace(":", "."));
			String screenshotPath = getScreenshot(driver, screenshotName, destination);
			return screenshotPath;

		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error ", ex);

		}
	}

	public static void takeFullPageScreenshot(WebDriver driver, String screenshotName, String destination)
			throws CustomException {
		try {
			/*
			 * destination = new
			 * StringBuilder(destination).append("//").append(screenshotName).append(" ").
			 * append(".png") .toString();
			 */
			/*
			 * String timeStamp = new Timestamp((new Date()).getTime()).toString().replace('
			 * ', '_'); screenshotName = screenshotName + " " + (timeStamp.replace(":",
			 * "."));
			 */

			Shutterbug.shootPage(driver, ScrollStrategy.BOTH_DIRECTIONS, 500, true).withName(screenshotName).save(destination);
		//	Thread.sleep(5000);

		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error ", ex);

		}

	}

	public static String takeBDDScreenshot(WebDriver webDriver, String screenshotName, String destination)
			throws CustomException {
		try {

			String screenshotPath = getScreenshot(webDriver, screenshotName, destination);
			String sub[] = screenshotPath.split("\\\\");
			return sub[sub.length - 1];
		} catch (Exception ex) {

			throw new CustomException("ScreenShot Error ", ex);

		}
	}

}
