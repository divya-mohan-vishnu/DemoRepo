package com.framework.client;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.testng.Reporter;

import com.project.reusablemethods.Constants;

public class RequestMain {

	/* PCO Request Creation RequestMain Class */
	public static String main(String[] args) {
		String requestBody = "";
		System.out.println("*********************Request Creation JAR Started*********************");

		if (args.length != 0) {
			if (args.length != 1) {

				String jar_filepath = "";
				try {
					jar_filepath = new File(
							RequestMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				String excelinputPath = args[0];
				String referenceID = args[1];

		//		System.out.println("ARGS : \n 1. " + excelinputPath + "\n 2. " + referenceID + "\n\n");
				RequestValidations objValidations = new RequestValidations();
				 jar_filepath = jar_filepath.substring(0,
				 jar_filepath.lastIndexOf(File.separator));
//				/**********/
//				String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
//						.getParameter("runId");
//				jar_filepath = Constants.DirectoryPath + "\\TestResults\\" + runId;
//				/*************/
				String requestFilepath = jar_filepath + "\\RequestFiles\\" + referenceID + ".json";
				objValidations.DeleteFileIfExists(requestFilepath);
				HashMap<String, String> excelDataMap = objValidations.ReadExcelData(excelinputPath, referenceID);

				HashMap<String, String> testDataMap = objValidations.GetTestDataDetails(excelDataMap, excelinputPath);

		//		System.out.println("  \nTestDataID " + excelDataMap.get("TestDataID"));

				String dbSheetName = "Config";
				objValidations.GetDBConfigExcelDetails(excelinputPath, dbSheetName, testDataMap);

				requestBody = objValidations.CreateJsonRequestFile(excelDataMap, testDataMap, excelinputPath);
				objValidations.WriteXmlstringToDom(requestBody, requestFilepath);
				requestBody += ":::" + excelDataMap.get("TestDataID") + ":::" + testDataMap.get("Projectid") + ":::"
						+ excelDataMap.get("TestDataFile");
		//		System.out.println("Request File Created : " + requestFilepath);
				System.out.println("*********************Request Creation JAR Completed*********************");
				
			} else {
				System.out.println("Parameter Expected; Input Reference Id");
			}

		} else {

			System.out.println("Parameters Expected; Please pass Test Input Data File Path and Input Reference Id");
		}
		return requestBody;
	}

}
