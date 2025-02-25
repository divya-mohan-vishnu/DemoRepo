package com.framework.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TableValidations {

	public HashMap<String, String> ReadExcelData(String excelinputPath, String referenceID) {
		HashMap<String, String> excelDataMap = new HashMap<String, String>();
		TableExcelUtility excelUtil = new TableExcelUtility();
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

	public JSONObject ConstructInputJson(HashMap<String, String> excelDataMap, HashMap<String, String> configDataMap,
			String masterTestDataPath, String masterTestDataId, String referenceid, String excelinputPath,
			String projectID) {
		JSONObject rootjsonObj = new JSONObject();
		JSONObject inputjsonObj = new JSONObject();
		JSONArray tablejsonArray = ConstructTableJsonBlocks(excelDataMap, masterTestDataPath, masterTestDataId,
				referenceid, excelinputPath);
		System.out.println("hit");
		try {
			inputjsonObj.put("PowerCurveID", "#powerCurveID");
			if (excelDataMap.get("DefaultTableMapping") != null) {
				inputjsonObj.put("DefaultTableMapping", excelDataMap.get("DefaultTableMapping"));
			}
			inputjsonObj.put("DatabaseConnectionString", configDataMap.get("DatabaseConnectionString"));
			inputjsonObj.put("DatabaseUser", configDataMap.get("DBUser"));
			inputjsonObj.put("DatabasePassword", configDataMap.get("DBPassword"));
			inputjsonObj.put("ProjectID", projectID);
			inputjsonObj.put("TableValidations", tablejsonArray);
			rootjsonObj.put("input", inputjsonObj);
			System.out.println("rootjsonObj" + rootjsonObj);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return rootjsonObj;
	}

	public HashMap<String, String> GetConnectionString(String excelPath, String sheetName) {

		HashMap<String, String> configDataMap = new HashMap<String, String>();
		TableExcelUtility excelUtility = new TableExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);

		List<String> keyList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		String columnHeader = null;
		String columnValue = null;
		int cell = 0;

		int getsheetRowCount = excelUtility.getRowCount();

		do {
			columnHeader = excelUtility.getdata(0, cell);
			cell++;
			if (columnHeader != "") {

				keyList.add(columnHeader);

			}
		} while (columnHeader != "");
		cell = 0;

		for (int j = 1; j < getsheetRowCount; j++) {

			for (int k = 0; k < keyList.size(); k++) {
				columnValue = excelUtility.getdata(j, cell);
				valueList.add(columnValue);
				cell++;

			}
		}

		for (int i = 0; i < keyList.size(); i++) {
			if (valueList.get(i) != "") {
				configDataMap.put(keyList.get(i), valueList.get(i));
			}
		}

		return configDataMap;
	}

	public String GetProjectID(String excelPath, String sheetName) {
		String projectID = "";

		TableExcelUtility excelUtility = new TableExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		projectID = excelUtility.getdata(1, 1);

		return projectID;
	}

	public String GetDBUser(String excelPath, String sheetName) {
		String dbUser = "";

		TableExcelUtility excelUtility = new TableExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		dbUser = excelUtility.getdata(1, 1);

		return dbUser;
	}

	public String GetDBPassword(String excelPath, String sheetName) {
		String dbPassword = "";

		TableExcelUtility excelUtility = new TableExcelUtility();
		excelUtility.setExcel(excelPath, sheetName);
		dbPassword = excelUtility.getdata(1, 2);

		return dbPassword;
	}

	public JSONArray ConstructTableJsonBlocks(HashMap<String, String> excelDataMap, String masterTestDataPath,
			String masterTestDataId, String referenceid, String testdataPath) {
		HashMap<String, String> testDataMap = null;
		if (excelDataMap.toString().contains("$")) {
			testDataMap = GetTestDataDetails(masterTestDataPath, masterTestDataId);
		}
		int flag = 1;

		JSONArray tablejsonArray = new JSONArray();
		do {
			HashMap<String, String> tableMap = new HashMap<String, String>();

			for (Entry excelmap : excelDataMap.entrySet()) {

				if (excelmap.getKey().toString().contains("Table" + flag)) {

					tableMap.put(excelmap.getKey().toString(), excelmap.getValue().toString());

				}

			}
			JSONObject tableJsonObj = ConstructTableJsonBlock(tableMap, masterTestDataPath, masterTestDataId,
					referenceid, testdataPath, testDataMap);

			tablejsonArray.put(tableJsonObj);
			flag++;

		} while (excelDataMap.containsKey("Table" + flag + ".SqlQuery"));

		return tablejsonArray;

	}

	private JSONObject ConstructTableJsonBlock(HashMap<String, String> tableMap, String masterTestDataPath,
			String masterTestDataId, String referenceid, String testdatafilePath, HashMap<String, String> testDataMap) {

		JSONObject tableObj = new JSONObject();
		JSONObject constraintObj = new JSONObject();
		JSONObject validationObj = new JSONObject();
		JSONObject mappingObj = new JSONObject();

		int mappingFlag = 1;
		try {
			for (Entry map : tableMap.entrySet()) {
				if (map.getKey().toString().contains("Name")) {
					tableObj.put("TableName", map.getValue().toString());
				}

				if (map.getKey().toString().contains("SqlQuery")) {
					tableObj.put("SqlQuery", map.getValue().toString());
				}

				if (map.getKey().toString().contains("Constraint")) {
					if (map.getValue().toString().split("=")[1] != null) {

						if (map.getValue().toString().split("=")[1].contains("$")) {
							String masterSheetValue = GetTestDataValueFromMasterSheet(
									map.getValue().toString().split("=")[1], testDataMap);
							if (!masterSheetValue.equals("")) {
								constraintObj.put(map.getValue().toString().split("=")[0], masterSheetValue);
							}
						} else {
							constraintObj.put(map.getValue().toString().split("=")[0],
									map.getValue().toString().split("=")[1]);
						}
					} else {
						constraintObj.put(map.getValue().toString().split("=")[0], "");
					}
				}

				if (!referenceid.equalsIgnoreCase("BureauValidation")) {
					if (map.getKey().toString().contains("Validation")) {
						if (map.getValue().toString().contains("=")) {
							if (!map.getValue().toString().split("=")[1].equals(null)) {
								if (map.getValue().toString().split("=")[1].contains("$")) {

									String masterSheetValue = GetTestDataValueFromMasterSheet(
											map.getValue().toString().split("=")[1], testDataMap);
									validationObj.put(map.getValue().toString().split("=")[0], masterSheetValue);
								} else {
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
						}
					}
				} else {
					if (map.getKey().toString().contains("ValidationMapping")) {
						String validationmappingFilePath = map.getValue().toString();
						testdatafilePath = testdatafilePath.substring(0, testdatafilePath.lastIndexOf(File.separator));
						String testDataFilePath = testdatafilePath + "\\" + validationmappingFilePath;
						HashMap<String, String> validationMap = ReadBulkValidationTestData(testDataFilePath);

						for (Entry validationmap : validationMap.entrySet()) {
							if (validationmap.getValue().toString().contains("$")) {

								String masterSheetValue = GetTestDataValueFromMasterSheet(
										validationmap.getValue().toString(), testDataMap);

								validationObj.put(validationmap.getKey().toString(), masterSheetValue);
							} else {
								validationObj.put(validationmap.getKey().toString(),
										validationmap.getValue().toString());
							}

						}
					}
				}

				if (map.getKey().toString().contains("TableMapping")) {
					String mapFlag = map.getKey().toString().substring(map.getKey().toString().length() - 1);
					mappingObj.put("Mapping" + mapFlag, map.getValue().toString());
					mappingFlag++;
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
		} catch (JSONException ex) {

		}

		// todo-- construct json table block
		// msg ->$powercurveid --todo in server side

		return tableObj;
	}

	private HashMap<String, String> ReadBulkValidationTestData(String validationmappingFilePath) {
		HashMap<String, String> validationDataMap = new HashMap<String, String>();
		TableExcelUtility excelUtil = new TableExcelUtility();

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
		if (!testDataFilePath.contains("null")) {
			TableExcelUtility excelUtil = new TableExcelUtility();

			excelUtil.setFirstSheet(testDataFilePath);

			String columnHeader = null;
			String columnValue = null;
			int cell = 1;
			String keyword = testDataId;
			int getsheetRowCount = excelUtil.getRowCount();
			int getSheetColumnCount = excelUtil.getcellCount();

			cell = 1;
			String findKeyword = null;
			boolean is_Check = false;
			for (int j = 1; j < getsheetRowCount; j++) {
				if (!is_Check) {
					findKeyword = excelUtil.getdata(j, 0);

					if (keyword.equals(findKeyword)) {
						is_Check = true;
						for (int k = 0; k < getSheetColumnCount; k++) {
							columnHeader = excelUtil.getdata(0, cell);

							columnValue = excelUtil.getdata(j, cell);
							if (columnHeader == "")
								break;
							if (columnValue != "") {
								testDataMap.put(columnHeader, columnValue);

							}
							cell++;
						}
					}
				} else
					break;
			}

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
		TableExcelUtility excelUtility = new TableExcelUtility();
		excelUtility.closeExcel(excelinputPath);

	}

}
