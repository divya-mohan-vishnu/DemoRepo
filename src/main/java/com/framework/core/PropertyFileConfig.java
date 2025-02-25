package com.framework.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class PropertyFileConfig  {
	
	
	public static Properties readPropertiesFile() throws IOException {
		FileInputStream fis = null;
		Properties prop = null;
		String fileName = "project.properties";
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch(FileNotFoundException fnfe) {
			
			fnfe.printStackTrace();
		} catch(IOException ioe) {
			
			ioe.printStackTrace();
		} finally {
			fis.close();
			
		}
		
		return prop;      
	}

}
