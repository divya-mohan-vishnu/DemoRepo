package com.project.reusablemethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;

//import com.experian.kiqquestions.KiqQuestionsFind;
//import com.experian.kiqquestions.ModelQuestionsSet;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.client.CommonUtils;
import com.framework.client.RequestExcelUtility;
import com.framework.client.RequestMain;
import com.framework.client.ResponseMain;
import com.framework.client.TableMain;
import com.framework.core.FrameworkOperations;
import com.framework.core.PropertyFileConfig;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class ScreenlessUtils {

	private String tokenUrl = "";
	private String user = "";
	private String username = "";
	private String userdomain = "";
	private String password = "";
	private String client_id = "";
	private String client_secret = "";
	private String endPointUrl = "";
	private static HashMap<String, String> configMapEnv;

//	public String TriggerServiceCall(Map<String, String> datatableMap) throws FileNotFoundException, Exception {
//		String referenceID = datatableMap.get("ReferenceId");
//		String inputTestDataPath = Constants.DirectoryPath + "\\" + Constants.ScreenlessTestDataFilePath;
//		String RequestPayload = new RequestMain().main(new String[] { inputTestDataPath, referenceID });
//		Constants.MasterDataID = RequestPayload.split(":::")[1];
//		RequestPayload = RequestPayload.split(":::")[0];
//
//		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//		RequestPayload = RequestPayload.replace("$TestCaseTime", sdf.format(new Date()));
//
//		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
//		String responseFilePath = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";
//
//		/********* for getting requests and responsefiles in folder *******/
//		String responseFilePathNew = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\ResponseFiles\\"
//				+ referenceID + "_Response" + ".json";
//		String requestFilePathNew = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\RequestFiles\\"
//				+ referenceID + "_Request" + ".json";
//
//		/****************************************************************/
//
//		System.out.println("****************************");
//
//		if (RequestPayload.contains("#SessionID")) {
//			RequestPayload = RequestPayload.replace("#SessionID", Constants.SessionID);
//			// KIQ
//			LinkedHashMap<String, ModelQuestionsSet> finalHM = KiqQuestionsFind
//					.FindAnswerForKiqQuestions(responseFilePath);
//
//			for (Entry<String, ModelQuestionsSet> entry : finalHM.entrySet()) {
//				System.out.println(entry.getKey() + " => " + entry.getValue());
//
//				if (RequestPayload.contains("#Answer1"))
//					RequestPayload = RequestPayload.replace("#Answer1", entry.getValue().getAnswer());
//
//				else if (RequestPayload.contains("#Answer2"))
//					RequestPayload = RequestPayload.replace("#Answer2", entry.getValue().getAnswer());
//
//				else if (RequestPayload.contains("#Answer3"))
//					RequestPayload = RequestPayload.replace("#Answer3", entry.getValue().getAnswer());
//
//				else if (RequestPayload.contains("#Answer4"))
//					RequestPayload = RequestPayload.replace("#Answer4", entry.getValue().getAnswer());
//
//				else if (RequestPayload.contains("#Answer5"))
//					RequestPayload = RequestPayload.replace("#Answer5", entry.getValue().getAnswer());
//			}
//		}
//
//		System.out.println(RequestPayload);
//		CommonUtils objCommonUtils = new CommonUtils();
//		String endPointUrl = objCommonUtils.GetEndPointUrl(inputTestDataPath);
//		// trigger POST operation
//		String response = SendPost(RequestPayload, endPointUrl);
//
//		JSONObject responseobj = new JSONObject(response);
//		FrameworkOperations objFrameworkOper = new FrameworkOperations();
//		String nodeValue = objFrameworkOper.loopThroughJson(responseobj, "sessionID");
//		Constants.SessionID = nodeValue;
//
//		/***************** writing request files to json *****************/
//		JSONObject requestobj = new JSONObject(RequestPayload);
//		objCommonUtils.WriteToFile(runId, RequestPayload, requestFilePathNew);
//		/*****************************************************************/
//
//		objCommonUtils.WriteToFile(runId, response, responseFilePath);
//
//		/***************** writing response files to json *****************/
//		objCommonUtils.WriteToFile(runId, response, responseFilePathNew);
//		/*****************************************************************/
//
//		return response;
//	}

	public String TriggerResponseValidation(Map<String, String> datatableMap) throws FileNotFoundException, Exception {

		Properties prop = PropertyFileConfig.readPropertiesFile();
		String ResponseMxOutput = null;
		DateTimeFormatter dateFormat_db = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		CommonUtils objCommonUtils = new CommonUtils();
		String referenceID = datatableMap.get("ReferenceId");
		String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		String inputTestDataPath = null;
		String directoryPath = System.getProperty("user.dir");
		if (optParam != null) {
			inputTestDataPath = directoryPath + "\\" + runId + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		} else {
			inputTestDataPath = directoryPath + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		}
//		String responseFilePath = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";
		String responseFilePath = directoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";

		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		String serviceCallResponse = objCommonUtils.GetFileContent(responseFilePath);

		String ResponseValidationPayload = new ResponseMain()
				.main(new String[] { serviceCallResponse, inputTestDataPath, referenceID, "json" });
		// System.out.println(ResponseValidationPayload);

		String endPointUrl = prop.getProperty("ResultUpdation_MS");
		String response = SendPostMethod(ResponseValidationPayload, endPointUrl);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> json;
		try {
			json = mapper.readValue(response, Map.class);
			ResponseMxOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			if (json.get("validationStatus").equals("FAILED"))
				ResponseMxOutput = "There is difference in data , " + ResponseMxOutput;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseMxOutput;
	}

//	public HashMap<String, String> GetConfigSheetDetails(String testDataPath) {
//		RequestExcelUtility excelUtil = new RequestExcelUtility();
//		HashMap<String, String> configMap = new HashMap<String, String>();
//		excelUtil.setExcel(testDataPath, "Config");
//		int getsheetRowCount = excelUtil.getRowCount();
//		int getSheetColumnCount = excelUtil.getcellCount();
//		int cell = 1;
//		String columnHeader = null;
//		String columnValue = null;
//		String findKeyword = null;
//		boolean is_Check = false;
//		for (int j = 1; j < getsheetRowCount; j++) {
//			if (!is_Check) {
//				findKeyword = excelUtil.getdata(j, 0);
//
//			//	is_Check = true;
//				for (int k = 0; k < getSheetColumnCount; k++) {
//					columnHeader = excelUtil.getdata(0, cell);
//
//					columnValue = excelUtil.getdata(j, cell);
//					if (columnHeader == "")
//						break;
//					if (columnValue != "") {
//						configMap.put(columnHeader, columnValue);
//
//					}
//					cell++;
//				}
//			} else
//				break;
//		}
//		return configMap;
//	}	

//	****************
	public HashMap<String, String> GetConfigSheetDetails(String testDataPath, String referenceId) {
		RequestExcelUtility excelUtil = new RequestExcelUtility();
		HashMap<String, String> configMap = new HashMap<String, String>();
		excelUtil.setExcel(testDataPath, "Config");
		int getsheetRowCount = excelUtil.getRowCount();
		int getSheetColumnCount = excelUtil.getcellCount();
		int cell = 1;
		String columnHeader = null;
		String columnValue = null;
		String findKeyword = null;
		boolean is_Check = false;
		int i = 0;
		while (i < getsheetRowCount) {
			findKeyword = excelUtil.getdata(i, 0);
			if (referenceId.equals(findKeyword)) {
				for (int j = 0; j < getSheetColumnCount; j++) {

					columnHeader = excelUtil.getdata(0, j);
					columnValue = excelUtil.getdata(i, j);

					if (columnHeader == "")
						break;
					if (columnValue != "") {
						configMap.put(columnHeader, columnValue);
					}
				}
			}
			i++;
		}
		excelUtil.closeExcel(testDataPath);
		return configMap;
	}

//	****************

	public void GenerateToken(HashMap<String, String> configMap) {

		configMapEnv = configMap; // configMap taken

		InputStreamReader reader = null;
		HttpsURLConnection connection = null;
		String token = null;
		String returnValue = null;
		try {
			tokenUrl = configMap.get("TokenUrl");
			// System.out.println("Token Url : " + tokenUrl);
			URL url = new URL(tokenUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			user = configMap.get("Username");
			userdomain = configMap.get("X_User_Domain");
			username = user + "@" + userdomain;
			password = configMap.get("Password");
			client_id = configMap.get("Client_ID");
			client_secret = configMap.get("Client_Secret");

			String jsonInputString = "{\r\n" + "    \"username\": \"" + username + "\",\r\n" + "    \"password\": \""
					+ password + "\",\r\n" + "    \"client_id\": \"" + client_id + "\",\r\n"
					+ "    \"client_secret\": \"" + client_secret + "\"\r\n" + "}";

			System.out.println(jsonInputString);

			byte[] input = jsonInputString.getBytes("utf-8");
			// System.out.println(String.valueOf(input.length));
			connection.setRequestProperty("X-Correlation-Id", configMap.get("X_Correlation_Id"));
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("X-User-Domain", configMap.get("X_User_Domain"));
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(input.length));
			connection.setRequestProperty("Host", configMap.get("Host"));

			try (OutputStream os = connection.getOutputStream()) {

				os.write(input, 0, input.length);
			}
			String charset = "UTF-8";
			InputStream gzippedResponse = connection.getInputStream();
			InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
			reader = new InputStreamReader(ungzippedResponse, charset);
			StringWriter writer = new StringWriter();
			char[] buffer1 = new char[10240];
			for (int length = 0; (length = reader.read(buffer1)) > 0;) {
				writer.write(buffer1, 0, length);
			}
			String response = writer.toString();
			System.out.println(response);

			/*
			 * BufferedReader br = new BufferedReader(new
			 * InputStreamReader(connection.getInputStream(), "utf-8")); StringBuilder
			 * response = new StringBuilder(); String responseLine = null; while
			 * ((responseLine = br.readLine()) != null) {
			 * response.append(responseLine.trim()); }
			 * System.out.println(response.toString());
			 */

			returnValue = response.toString();
			JSONObject json = new JSONObject(returnValue);
			token = json.getString("access_token");
			token = "Bearer " + token;
			Constants.Token = token;

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		// return token;
	}

	public void GenerateTokenHttpClient(HashMap<String, String> configMap) {

		tokenUrl = configMap.get("TokenUrl");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(tokenUrl);
		user = configMap.get("Username");
		userdomain = configMap.get("X_User_Domain");
		username = user + "@" + userdomain;
		password = configMap.get("Password");
		client_id = configMap.get("Client_ID");
		client_secret = configMap.get("Client_Secret");

		String JSON_STRING = "{\r\n" + "    \"username\": \"" + username + "\",\r\n" + "    \"password\": \"" + password
				+ "\",\r\n" + "    \"client_id\": \"" + client_id + "\",\r\n" + "    \"client_secret\": \""
				+ client_secret + "\"\r\n" + "}";

//		System.out.println(JSON_STRING);

		HttpEntity stringEntity = new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON);
		httpPost.setEntity(stringEntity);
		try {
			CloseableHttpResponse response2 = httpclient.execute(httpPost);
			// System.out.println(response2);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String SendPost(String postContent, String endPointUrl) {

		String host = configMapEnv.get("Host");
		String xuserdomain = configMapEnv.get("X-User-Domain");
		String xcorrelationId = configMapEnv.get("X-Correlation-Id");
		endPointUrl = configMapEnv.get("EndPointUrl"); //

		String response = "";
		InputStreamReader reader = null;
		HttpsURLConnection connection = null;

		try {
//			endPointUrl = "https://uat-us-api.experian.com/crosscore/npfsvyssbdue/services/v0/applications/3";

			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			byte[] input = postContent.getBytes("utf-8");

			connection.setRequestProperty("Authorization", Constants.Token);
			connection.setRequestProperty("Content-Type", "application/json; charset=utf8");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
			connection.setRequestProperty("Content-Length", String.valueOf(input.length));
			connection.setRequestProperty("Accept", "application/json");
//			connection.setRequestProperty("Host", "uat-us-api.experian.com");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("User-Agent", "Apache-HttpClient/4.5.5 (Java/13.0.7)");
//			connection.setRequestProperty("X-User-Domain", "experianust.com"); 
//			connection.setRequestProperty("X-Correlation-Id", "7b36be66-d071-414b-9762-64c1a19a944a");

			connection.setRequestProperty("Host", host);
			connection.setRequestProperty("X-User-Domain", xuserdomain);
			connection.setRequestProperty("X-Correlation-Id", xcorrelationId);

			try (OutputStream os = connection.getOutputStream()) {

				os.write(input, 0, input.length);
			}
			String charset = "UTF-8";
			InputStream gzippedResponse = connection.getInputStream();
			InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
			reader = new InputStreamReader(ungzippedResponse, charset);
			StringWriter writer = new StringWriter();
			char[] buffer1 = new char[10240];
			for (int length = 0; (length = reader.read(buffer1)) > 0;) {
				writer.write(buffer1, 0, length);
			}
			response = writer.toString();
//			System.out.println(response);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response1 = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response1.append(inputLine);
			}
			in.close();

//			System.out.println(response1.toString());

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		return response;

	}

	public void IntialSetUp() {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		DisableCertificate();
	}

	

	public void disableCertificateValidation() {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

		} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// set the allTrusting verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
		}

	}

	public static void DisableCertificate() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}

	// Get
	public String TriggerServiceCallofGetMethod(Map<String, String> datatableMap)
			throws FileNotFoundException, Exception {
		Properties prop = PropertyFileConfig.readPropertiesFile();
		String referenceID = datatableMap.get("ReferenceId");
		String inputTestDataPath = null;
		String directoryPath = System.getProperty("user.dir");
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		if (optParam != null) {
			inputTestDataPath = directoryPath + "\\" + runId + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		} else {
			inputTestDataPath = directoryPath + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		}

		String RequestPayload = new RequestMain().main(new String[] { inputTestDataPath, referenceID });
		Constants.MasterDataID = RequestPayload.split(":::")[1];
		RequestPayload = RequestPayload.split(":::")[0];

		String responseFilePath = directoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";

		/********* for getting requests and responsefiles in folder *******/
		String responseFilePathNew = directoryPath + "\\TestResults\\" + runId + "\\ResponseFiles\\" + referenceID
				+ "_Response" + ".json";
		String requestFilePathNew = directoryPath + "\\TestResults\\" + runId + "\\RequestFiles\\" + referenceID
				+ "_Request" + ".json";

		/****************************************************************/

		System.out.println("****************************");
		// System.out.println(RequestPayload);
		CommonUtils objCommonUtils = new CommonUtils();
		String endPointUrl = objCommonUtils.GetEndPointUrl(inputTestDataPath);
		// trigger GET operation
		String response = SendGetMethod(RequestPayload);
         
		///JSONObject responseobj = new JSONObject(response);
		//JSONArray responseobj = new JSONArray(response);
		/***************** writing request files to json *****************/
		JSONObject requestobj = new JSONObject(RequestPayload);
		objCommonUtils.WriteToFile(runId, RequestPayload, requestFilePathNew);
		/*****************************************************************/

		objCommonUtils.WriteToFile(runId, response, responseFilePath);

		/***************** writing response files to json *****************/
		objCommonUtils.WriteToFile(runId, response, responseFilePathNew);
		/*****************************************************************/

		return response;
	}
	
	// Get
	public String TriggerServiceCallofPostMethod(Map<String, String> datatableMap)
			throws FileNotFoundException, Exception {
		Properties prop = PropertyFileConfig.readPropertiesFile();
		String referenceID = datatableMap.get("ReferenceId");
		String inputTestDataPath = null;
		String directoryPath = System.getProperty("user.dir");
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		if (optParam != null) {
			inputTestDataPath = directoryPath + "\\" + runId + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		} else {
			inputTestDataPath = directoryPath + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		}

		String RequestPayload = new RequestMain().main(new String[] { inputTestDataPath, referenceID });
		Constants.MasterDataID = RequestPayload.split(":::")[1];
		RequestPayload = RequestPayload.split(":::")[0];

		String responseFilePath = directoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";

		/********* for getting requests and responsefiles in folder *******/
		String responseFilePathNew = directoryPath + "\\TestResults\\" + runId + "\\ResponseFiles\\" + referenceID
				+ "_Response" + ".json";
		String requestFilePathNew = directoryPath + "\\TestResults\\" + runId + "\\RequestFiles\\" + referenceID
				+ "_Request" + ".json";

		/****************************************************************/

		System.out.println("****************************");
		// System.out.println(RequestPayload);
		CommonUtils objCommonUtils = new CommonUtils();
	
		// trigger GET operation
		String response = SendPostMethod(RequestPayload);

		JSONObject responseobj = new JSONObject(response);
		/***************** writing request files to json *****************/
		JSONObject requestobj = new JSONObject(RequestPayload);
		objCommonUtils.WriteToFile(runId, RequestPayload, requestFilePathNew);
		/*****************************************************************/

		objCommonUtils.WriteToFile(runId, response, responseFilePath);

		/***************** writing response files to json *****************/
		objCommonUtils.WriteToFile(runId, response, responseFilePathNew);
		/*****************************************************************/

		return response;
	}

	// GET REQUEST
	public static String SendGetMethod(String RequestPayload) {

		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		StringBuilder response = new StringBuilder();
		try {

			FrameworkOperations objFrameworkOper = new FrameworkOperations();
			JSONObject requestobj = new JSONObject(RequestPayload);
			String endPointUrl = objFrameworkOper.loopThroughJson(requestobj, "url");
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			// connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Accept", "*/*");
//			connection.setRequestProperty("Host", "uat-us-api.experian.com");
			// connection.setRequestProperty("Host", "www.googleapis.com");
			// connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("Connection", "keep-alive");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			String directoryPath = System.getProperty("user.dir");
			// System.out.println("Response : " + response.toString());
			String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
			String responseFilePath = directoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";

			CommonUtils objCommonUtils = new CommonUtils();
			objCommonUtils.WriteToFile(runId, response.toString(), responseFilePath);

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		return response.toString();
	}

	
	private static String SendPostMethod(String responseContent, String endPointUrl) {

//		endPointUrl = configMapEnv.get("EndPointUrl");	

		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(500000000);
			connection.setReadTimeout(500000000);

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			OutputStream os = connection.getOutputStream();
			byte[] input = responseContent.getBytes("utf-8");
			os.write(input, 0, input.length);

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
//			System.out.println(response.toString());

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		return response.toString();
	}
	
	private static String SendPostMethod(String RequestPayload) {

//		endPointUrl = configMapEnv.get("EndPointUrl");	

		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		try {
			FrameworkOperations objFrameworkOper = new FrameworkOperations();
			JSONObject requestobj = new JSONObject(RequestPayload);
			String endPointUrl = objFrameworkOper.loopThroughJson(requestobj, "url");
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(500000000);
			connection.setReadTimeout(500000000);

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			OutputStream os = connection.getOutputStream();
			byte[] input = RequestPayload.getBytes("utf-8");
			os.write(input, 0, input.length);

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
//			System.out.println(response.toString());

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			connection.disconnect();
		}
		return response.toString();
	}
	
	public String ResponseValidationOfGetMethod(Map<String, String> datatableMap)
			throws FileNotFoundException, Exception {
		Properties prop = PropertyFileConfig.readPropertiesFile();
		String ResponseMxOutput = null;
		// DateTimeFormatter dateFormat_db = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		String inputTestDataPath = null;
		String directoryPath = System.getProperty("user.dir");
		if (optParam != null) {
			inputTestDataPath = directoryPath + "\\" + runId + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		} else {
			inputTestDataPath = directoryPath + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		}

		CommonUtils objCommonUtils = new CommonUtils();
		String referenceID = datatableMap.get("ReferenceId");

//		String responseFilePath = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";
		String responseFilePath = directoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";

		// SimpleDateFormat sdf = new
		// java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		String serviceCallResponse = objCommonUtils.GetFileContent(responseFilePath);

		String ResponseValidationPayload = new ResponseMain()
				.main(new String[] { serviceCallResponse, inputTestDataPath, referenceID, "json" });
		// System.out.println(ResponseValidationPayload);

		String endPointUrl = prop.getProperty("RESPONSE_MS");
		String response = FrameworkOperations.SendPostMethod(ResponseValidationPayload, endPointUrl);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> json;
		try {
			json = mapper.readValue(response, Map.class);
			ResponseMxOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			if (json.get("validationStatus").equals("FAILED"))
				ResponseMxOutput = "There is difference in data , " + ResponseMxOutput;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseMxOutput;
	}

	public String TriggerDatabaseValidation(Map<String, String> datatableMap) throws IOException {
		Properties prop = PropertyFileConfig.readPropertiesFile();
		String ResponseMxOutput = null;
		DateTimeFormatter dateFormat_db = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		CommonUtils objCommonUtils = new CommonUtils();
		String referenceID = datatableMap.get("ReferenceId");
		String directoryPath = System.getProperty("user.dir");
		String inputTestDataPath = null;
		String runId = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("runId");
		String optParam = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("optParam");
		if (optParam != null) {
			inputTestDataPath = directoryPath + "\\" + runId + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		} else {
			inputTestDataPath = directoryPath + "\\" + prop.getProperty("ScreenlessTestDataFilePath");
		}
		
		
		// String responseFilePath = Constants.DirectoryPath + "\\TestResults\\" + runId
		// + "\\" + "Response" + ".xml";
		String responseFilePath = Constants.DirectoryPath + "\\TestResults\\" + runId + "\\" + "Response" + ".json";
		String serviceCallResponse = objCommonUtils.GetFileContent(responseFilePath);
		String masterTestDataPath = Constants.DirectoryPath + "\\" + Constants.MasterDataFilePath;
		String tableValidationPayload = new TableMain()
				.main(new String[] { inputTestDataPath, referenceID, masterTestDataPath, Constants.MasterDataID });

		if (tableValidationPayload.contains("#date")) {
			tableValidationPayload = tableValidationPayload.replace("#date", LocalDateTime.now().format(dateFormat_db));
		}

		tableValidationPayload = tableValidationPayload.replaceAll("#powerCurveID", Constants.PCID);
		tableValidationPayload = tableValidationPayload.replaceAll("#prev1_powerCurveID", Constants.Prev1_PCID);
		tableValidationPayload = tableValidationPayload.replaceAll("#prev2_powerCurveID", Constants.Prev2_PCID);
		tableValidationPayload = tableValidationPayload.replaceAll("#prev3_powerCurveID", Constants.Prev3_PCID);
		System.out.println(tableValidationPayload);
		String endPointUrl = null;
		
		try {
			
			endPointUrl = prop.getProperty("DBTable_MS");

			String response = objCommonUtils.SendPostMethod(tableValidationPayload, endPointUrl);
			if (!response.equals("")) {
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> json;

				json = mapper.readValue(response, Map.class);
				ResponseMxOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
				boolean isStatus = objCommonUtils.SetTestStatus(json);

				// if (json.get("testStatus") != null) {

				if (!isStatus) {

					ResponseMxOutput = ResponseMxOutput.replace(" \"testStatus\" : null", " \"testStatus\" : FAILED");
				} else {

					ResponseMxOutput = ResponseMxOutput.replace(" \"testStatus\" : null", " \"testStatus\" : PASSED");
				}
			}

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return ResponseMxOutput;
	}

}
