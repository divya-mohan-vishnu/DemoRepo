package com.framework.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.framework.core.CustomException;

public class ConfigManager {
	Properties pro;
	FileInputStream fis;
	static String projectName;

	public ConfigManager(String projectname) throws CustomException {
		try {
			projectName = projectname;
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = (filePathdir.replace("\\", "//"));
			String filePath = filePathdir + "//resource//Configuration//Config.properties";
			File src = new File(filePath);
			fis = new FileInputStream(src);
			pro = new Properties();
			pro.load(fis);
		} catch (Exception e) {

			throw new CustomException("Error found in ConfigManager Utility class", e);
		}
	}

	public String SheetPath() {
		return pro.getProperty("SheetPath");

	}

	public String getchromepath() {
		return pro.getProperty("chromepath");

	}
	
	public String getTestNGpath() {
		return pro.getProperty("TestNGFilePath");

	}

	public String getXmlPath() {
		return pro.getProperty("xmlPath");

	}

	public String getSoapAuthorisationToken() {
		return pro.getProperty("auth_token_soap");
	}

	public String getExperianReference() {
		return pro.getProperty("experianReference");
	}

	public String getSpareFlag() {
		return pro.getProperty("spareflag");
	}

	public String getPowerCurveId() {
		return pro.getProperty("powercurveId");
	}

	public String getEndPointUrl() {
		return pro.getProperty("endpointurl");
	}

	public String getDBConnectionString() {
		return pro.getProperty("dbconnectionstring");
	}

	public String getFeatureId() {
		return pro.getProperty("featureId");
	}
	
	public String getFeatureName() {
		return pro.getProperty("featureName");
	}
	
	public String getExecutionMode() {
		return pro.getProperty("ExecutionMode");
	}
	
	public String getURL() {
		return pro.getProperty("URL");
	}
	public String gettokenURL() {
		return pro.getProperty("token_url");
	}

	public void setURL(String URL) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("URL", URL);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	
	public void setExecutionMode(String ExecutionMode) throws IOException {

		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("ExecutionMode", ExecutionMode);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}
	}
	
	public void setSoapAuthorisationToken(String tokenValue) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("auth_token_soap", tokenValue);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public void setRestAuthorisationToken(String tokenValue) throws IOException {

		try {
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("auth_token_rest", tokenValue);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}
	}

	public void setExperianReference(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("experianReference", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}
	
	public void setTestNGpath(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("TestNGFilePath", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public void setspareflag(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("spareflag", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public void setPowerCurveId(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("powercurveId", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public void setEndPointUrl(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("endpointurl", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public void setFeatureId(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("featureId", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}
	
	public void setFeatureName(String value) throws IOException {
		try {
			fis.close();
			//Adding code to fetch the path dynamically.
			String filePathdir = System.getProperty("user.dir");
			filePathdir = filePathdir.replace("\\", "//");
			FileOutputStream out = new FileOutputStream( filePathdir + "//resource//Configuration//Config.properties");
			pro.setProperty("featureName", value);
			pro.store(out, null);
			out.close();
		} catch (Exception ex) {

		}

	}

	public String setDBConnectionString() {
		return pro.getProperty("dbconnectionstring");
	}

	public String getRestAuthorisationToken() {
		return pro.getProperty("auth_token_rest");
	}

	public String UserNameTextBoxID() {
		return pro.getProperty("UserNameTextBoxID");
	}

	public String PasswordTextBoxID() {
		return pro.getProperty("PasswordTextBoxID");
	}

	public String LoginClickButton() {
		return pro.getProperty("LoginClickButton");
	}

}
