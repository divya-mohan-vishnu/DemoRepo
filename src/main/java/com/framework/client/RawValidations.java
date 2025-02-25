package com.framework.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.json.JsonObject;

import org.apache.commons.codec.language.RefinedSoundex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RawValidations {

	public HashMap<String, String> ReadExcelData(String excelinputPath, String referenceID) {
		HashMap<String, String> excelDataMap = new HashMap<String, String>();
		RawExcelUtility excelUtil = new RawExcelUtility();
		try {
			String[] arr = referenceID.split("_");
			arr = Arrays.copyOf(arr, arr.length - 1);
			String inputData = "";
			for (String input : arr) {
				inputData += input;
			}
			excelUtil.setExcel(excelinputPath, arr[0]);

			List<String> keyList = new ArrayList<String>();
			List<String> valueList = new ArrayList<String>();
			String columnHeader = null;
			String columnValue = null;
			int cell = 1;

			int getsheetRowCount = excelUtil.getRowCount();

			do {
				columnHeader = excelUtil.getdata(0, cell);
				cell++;
				if (columnHeader != "") {

					keyList.add(columnHeader);

				}
			} while (columnHeader != "");
			cell = 1;
			String findKeyword = null;
			for (int j = 1; j < getsheetRowCount; j++) {
				findKeyword = excelUtil.getdata(j, 0);
				
				if (referenceID.equals(findKeyword)) {
					for (int k = 0; k < keyList.size(); k++) {
						columnValue = excelUtil.getdata(j, cell);
						valueList.add(columnValue);
						cell++;
					}
				}
			}

			for (int i = 0; i < keyList.size(); i++) {
				if (valueList.get(i) != "") {
					excelDataMap.put(keyList.get(i), valueList.get(i));
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return excelDataMap;
	}

	public String GetInputReferenceID(String referenceID) {
		String[] arr = referenceID.split("_");
		arr = Arrays.copyOf(arr, arr.length - 1);
		String inputData = "";
		for (String input : arr) {
			inputData += input;
		}

		return arr[0];
	}

	public JSONObject ConstructInputJson(HashMap<String, String> excelDataMap, String connectionString,
			String referenceid, String excelinputPath, String refID, String projectID, String fileContent, String remoteHost, String powercurveID) {
		String type = "";
		String[] refSplit = refID.split("_");
		String lastOne = refSplit[refSplit.length - 1];
		if (lastOne.equalsIgnoreCase("REQ"))
			type = "Request";
		else
			type = "Response";
		JSONObject rootjsonObj = new JSONObject();
		JSONObject inputjsonObj = new JSONObject();
		JSONArray tablejsonArray = ConstructTableJsonBlocks(excelDataMap, referenceid, excelinputPath, refID);
		try {
			if(powercurveID!="")
			{
				inputjsonObj.put("PowerCurveID", powercurveID);
			}
			else
			{
			inputjsonObj.put("PowerCurveID", "#powerCurveID");
			}
			if(fileContent=="") {
			inputjsonObj.put("FilePath", excelDataMap.get("FilePath"));
			}
			else
			{
				inputjsonObj.put("FileContent", fileContent);
			}
			inputjsonObj.put("FileType", type);
			inputjsonObj.put("RemoteHost", remoteHost);
			inputjsonObj.put("ProjectID", projectID);
			if (excelDataMap.get("DefaultTableMapping") != null) {
				inputjsonObj.put("DefaultTableMapping", excelDataMap.get("DefaultTableMapping"));
			}
			inputjsonObj.put("DatabaseConnectionString", connectionString);

			inputjsonObj.put("TableValidations", tablejsonArray);
			rootjsonObj.put("input", inputjsonObj);
		//	System.out.println(rootjsonObj);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return rootjsonObj;
	}

	public String GetConnectionString(String excelPath, String sheetName) {
		String connectionString = "";

		RawExcelUtility excelUtility = new RawExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		connectionString = excelUtility.getdata(1, 0);

		return connectionString;
	}
	
	public String GetRemoteHost(String excelPath, String sheetName) {
		String remoteHost = "";

		RawExcelUtility excelUtility = new RawExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		remoteHost = excelUtility.getdata(1, 3);

		return remoteHost;
	}


	public String GetProjectID(String excelPath, String sheetName) {
		String projectID = "";

		RawExcelUtility excelUtility = new RawExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		projectID = excelUtility.getdata(1, 1);

		return projectID;
	}

	public JSONArray ConstructTableJsonBlocks(HashMap<String, String> excelDataMap, String referenceid,
			String testdataPath, String refID) {
		int flag = 1;

		JSONArray tablejsonArray = new JSONArray();
		do {
			HashMap<String, String> tableMap = new HashMap<String, String>();
			for (Entry excelmap : excelDataMap.entrySet()) {

				if (excelmap.getKey().toString().contains("Table" + flag)) {

					tableMap.put(excelmap.getKey().toString(), excelmap.getValue().toString());

				}

			}
			JSONObject tableJsonObj = ConstructTableJsonBlock(tableMap, referenceid, testdataPath, refID);

			tablejsonArray.put(tableJsonObj);
			flag++;

		} while (excelDataMap.containsKey("Table" + flag + ".Name"));

		return tablejsonArray;

	}

	private JSONObject ConstructTableJsonBlock(HashMap<String, String> tableMap, String referenceid,
			String testdatafilePath, String refID) {

		JSONObject tableObj = new JSONObject();
		JSONObject constraintObj = new JSONObject();
		JSONObject validationObj = new JSONObject();
		JSONObject mappingObj = new JSONObject();
		JSONObject responsetablemappingObj = new JSONObject();
		List<HashMap<String, String>> list = null;
		// HashMap<String, String> testDataMap = GetTestDataDetails(masterTestDataPath,
		// masterTestDataId);
		int mappingFlag = 1;
		int xmlresponsemappingFlag = 1;
		try {
			for (Entry map : tableMap.entrySet()) {
				if (map.getKey().toString().contains("Name")) {
					tableObj.put("TableName", map.getValue().toString());
				}

				if (map.getKey().toString().contains("Constraint")) {
					if (map.getValue().toString().split("=")[1] != null) {

						constraintObj.put(map.getValue().toString().split("=")[0],
								map.getValue().toString().split("=")[1]);

					} else {
						constraintObj.put(map.getValue().toString().split("=")[0], "");
					}
				}

				if (!referenceid.equalsIgnoreCase("BureauValidation")) {
					if (map.getKey().toString().contains("Validation")) {
						if (map.getValue().toString().contains("=")) {

							String[] splitEquals = map.getValue().toString().split("=");
							String mapSplitVal = "";
							int count = 0;
							for (String splitVal : splitEquals) {
								if (count > 0) {
									mapSplitVal += splitVal + "=";
								}
								count++;
							}
							mapSplitVal = mapSplitVal.substring(0, mapSplitVal.length() - 1);
							validationObj.put(map.getValue().toString().split("=")[0], mapSplitVal);

						}
					}
				} else {
					if (map.getKey().toString().contains("ValidationMapping")) {
						String validationmappingFilePath = map.getValue().toString();
						testdatafilePath = testdatafilePath.substring(0, testdatafilePath.lastIndexOf(File.separator));
						String testDataFilePath = testdatafilePath + "\\" + validationmappingFilePath;
						HashMap<String, String> validationMap = ReadBulkValidationTestData(testDataFilePath);

						for (Entry validationmap : validationMap.entrySet()) {

							validationObj.put(validationmap.getKey().toString(), validationmap.getValue().toString());

						}
					}
				}

				if (map.getKey().toString().contains("TableMapping")) {
					String mapFlag = map.getKey().toString().substring(map.getKey().toString().length() - 1);
					mappingObj.put("Mapping" + mapFlag, map.getValue().toString());
					mappingFlag++;
				}

				if (map.getKey().toString().contains("DBColumnRawResponseMapping")) {

					String mappingSheet = map.getValue().toString();
					
					
				
					
					
					HashMap<String, List<HashMap<String, String>>> jsonMap = ReadJsonResponseBulkValidationTestData(
							mappingSheet, refID,testdatafilePath);
				//	System.out.println(jsonMap);

					for (Entry jsondbMap : jsonMap.entrySet()) {

						list = (List<HashMap<String, String>>) jsondbMap.getValue();
						for (HashMap<String, String> jsonDBList : list) {
					//		System.out.println(jsonDBList);
							for (Entry hashMap : jsonDBList.entrySet()) {
								responsetablemappingObj.put(hashMap.getKey().toString(), hashMap.getValue().toString());
							}

						}

					}

				}

			}

			if (!constraintObj.has("PowerCurveId")) {
				constraintObj.put("PowerCurveId", "");
			}
			if (constraintObj.length() != 0) {
				tableObj.put("Constraints", constraintObj);
			}
			if (validationObj.length() != 0) {
				tableObj.put("TableColumnValueMapping", validationObj);
			}
			if (mappingObj.length() != 0) {
				tableObj.put("TargetTableMapping", mappingObj);
			}
			if (responsetablemappingObj.length() != 0) {
				tableObj.put("DBColumnRawResponseMapping", responsetablemappingObj);
			}
		} catch (JSONException ex) {

		}

		// todo-- construct json table block
		// msg ->$powercurveid --todo in server side

		return tableObj;
	}

	private HashMap<String, String> ReadBulkValidationTestData(String validationmappingFilePath) {
		HashMap<String, String> validationDataMap = new HashMap<String, String>();
		RawExcelUtility excelUtil = new RawExcelUtility();

		excelUtil.setFirstSheet(validationmappingFilePath);
		List<String> keyList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		String columnValue = null;
		int cell = 1;

		int getsheetRowCount = excelUtil.getRowCount();

		for (int j = 1; j < getsheetRowCount; j++) {
			keyList.add(excelUtil.getdata(j, 0));
			columnValue = excelUtil.getdata(j, 1);
			valueList.add(columnValue);

		}

		for (int i = 0; i < keyList.size(); i++) {

			validationDataMap.put(keyList.get(i), valueList.get(i));

		}
		return validationDataMap;

	}

	private HashMap<String, List<HashMap<String, String>>> ReadJsonResponseBulkValidationTestData(
			String mappingSheetName, String referenceID,String testDataPath) {
		HashMap<String, List<HashMap<String, String>>> validationDataMap = new HashMap<String, List<HashMap<String, String>>>();
		RawExcelUtility excelUtil = new RawExcelUtility();

		excelUtil.setExcel(testDataPath, mappingSheetName);
		List<String> keyList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		String columnValue = null;
		String columnHeader = null;
		int cell = 0;

		List<HashMap<String, String>> dbJsonMapList = new ArrayList<HashMap<String, String>>();

		int getsheetRowCount = excelUtil.getRowCount();

		do {
			columnHeader = excelUtil.getdata(0, cell);
			cell++;
			if (columnHeader != "") {

				keyList.add(columnHeader);

			}
		} while (columnHeader != "");

		String findKeyword = null;
		for (int j = 1; j < getsheetRowCount; j++) {
			cell = 1;
			findKeyword = excelUtil.getdata(j, 0);
			if (referenceID.equals(findKeyword)) {
				for (int k = 0; k < keyList.size() - 1; k++) {
					columnValue = excelUtil.getdata(j, cell);
					valueList.add(columnValue);
					cell++;
				}
				HashMap<String, String> dbJsonMap = new HashMap<String, String>();

				dbJsonMap.put(valueList.get(0), valueList.get(1));
				dbJsonMapList.add(dbJsonMap);
				valueList.clear();
			}
		}

		validationDataMap.put(referenceID, dbJsonMapList);
		return validationDataMap;

	}

	private String GetTestDataValueFromMasterSheet(String sheetValue, HashMap<String, String> testDataMap) {
		String masterSheetValue = "";

		sheetValue = sheetValue.replaceAll("[\\[\\](){}#$]", "");
		for (Entry map : testDataMap.entrySet()) {
			if (sheetValue.equalsIgnoreCase(map.getKey().toString())) {
				masterSheetValue = map.getValue().toString();
				break;
			}
		}
		return masterSheetValue;
	}

	public HashMap<String, String> GetTestDataDetails(String testDataFilePath, String testDataId) {

		HashMap<String, String> testDataMap = new HashMap<String, String>();
		RawExcelUtility excelUtil = new RawExcelUtility();

		excelUtil.setFirstSheet(testDataFilePath);
		List<String> keyList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		String columnHeader = null;
		String columnValue = null;
		int cell = 1;
		String keyword = testDataId;
		int getsheetRowCount = excelUtil.getRowCount();

		do {
			columnHeader = excelUtil.getdata(0, cell);
			cell++;
			if (columnHeader != "") {

				keyList.add(columnHeader);

			}
		} while (columnHeader != "");
		cell = 1;
		String findKeyword = null;
		for (int j = 1; j < getsheetRowCount; j++) {
			findKeyword = excelUtil.getdata(j, 0);
			if (keyword.equals(findKeyword)) {
				for (int k = 0; k < keyList.size(); k++) {
					columnValue = excelUtil.getdata(j, cell);
					valueList.add(columnValue);
					cell++;
				}
			}
		}

		for (int i = 0; i < keyList.size(); i++) {

			testDataMap.put(keyList.get(i), valueList.get(i));

		}
		return testDataMap;

	}

	public void DeleteFileIfExists(String requestFilepath) {
		File file = new File(requestFilepath);
		try {
			boolean result = Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void WriteToFile(JSONObject inputObj, String referenceID, String response_filepath) {

		String outputJson = inputObj.toString();

		ObjectMapper mapper = new ObjectMapper();

		Object json;
		try {
			json = mapper.readValue(outputJson, Object.class);
			outputJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

			File file = new File(response_filepath);
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileOutputStream fooStream = new FileOutputStream(file, false);
			byte[] myBytes = outputJson.getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (JsonParseException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void CloseWB(String excelinputPath) {
		RawExcelUtility excelUtility = new RawExcelUtility();
		excelUtility.closeExcel(excelinputPath);
		
	}
}
