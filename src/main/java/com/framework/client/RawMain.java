package com.framework.client;



import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;



public class RawMain {

	/* Application Generation Client Side TableMain Class */
	public static String main(String[] args) {

		JSONObject inputObj = null;
		System.out.println("Raw Validation Client Side Started ");

		if (args.length != 0) {
			if (args.length != 1) {

		//		System.out.println("\nARGS : " + "\n1. " + args[0] + "\n2. " + args[1] +"\n\n");

				String jar_filepath = "";
				try {
					jar_filepath = new File(RawMain.class.getProtectionDomain().getCodeSource().getLocation().toURI())
							.getPath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				String excelinputPath = args[0];
				String referenceID = args[1];
				
				String fileContent=args[2];
				
				String powercurveID=args[3];

				RawValidations objValidations = new RawValidations();

				jar_filepath = jar_filepath.substring(0, jar_filepath.lastIndexOf(File.separator));
				String referenceidFolder = objValidations.GetInputReferenceID(referenceID);
				String response_filepath = jar_filepath + "\\" + referenceidFolder + "\\" + referenceID + ".json";
				objValidations.DeleteFileIfExists(response_filepath);
				HashMap<String, String> excelDataMap = objValidations.ReadExcelData(excelinputPath, referenceID);
		//		System.out.println(excelDataMap);
				String dbSheetName = "Config";
				String connectionString = objValidations.GetConnectionString(excelinputPath, dbSheetName);
				String remoteHost = objValidations.GetRemoteHost(excelinputPath, dbSheetName);
				 String projectID = objValidations.GetProjectID(excelinputPath, dbSheetName);
				inputObj = objValidations.ConstructInputJson(excelDataMap, connectionString, referenceidFolder,
						excelinputPath, referenceID,projectID,fileContent,remoteHost,powercurveID);

				objValidations.WriteToFile(inputObj, referenceID, response_filepath);
				
				System.out.println("Raw Validation Client Side Completed");

				System.out.println("\n\nRaw Validation Client Side Completed.  :  " + referenceID + ".json");

			} else {
				System.out.println("Parameter Expected; Input Reference Id");
			}

		} else {

			System.out.println("Parameters Expected; Please pass Test Input Data File Path and Input Reference Id");
		}
		return inputObj.toString();
	}

}
