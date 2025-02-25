package com.project.reusablemethods;

//reading value of a particular cell  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Reporter;

import com.framework.client.RawExcelUtility;

public class ExcelOperations

{
	String projectName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
			.getParameter("projectName");

	String FileName = Constants.DirectoryPath + Constants.ScreenlessTestDataFilePath;

	// method defined for reading a cell
	public String ReadCellData(String SheetName, String FieldName) throws IOException {
		
		String value = null; // variable for storing the cell value
		Workbook wb = null;
		String Query = null;
		// initialize Workbook null
		try {
			// reading data from a file in the form of bytes
			FileInputStream fis = new FileInputStream(FileName);
			// constructs an XSSFWorkbook object, by buffering the whole stream into the
			// memory
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Sheet sh = wb.getSheet(SheetName); // getting the XSSFSheet object at given index

		int starRow = sh.getFirstRowNum();
		int endRow = sh.getLastRowNum();
	//	System.out.println("starRow= " + starRow);
	//	System.out.println("endRow= " + endRow);
		for (int i = starRow + 1; i < endRow; i++) {

			Cell c = sh.getRow(i).getCell(0);
	//		System.out.println(c.getStringCellValue());
			if (c.getStringCellValue().equals(FieldName)) {

				Cell c2 = sh.getRow(i).getCell(1);
				Query = c2.getStringCellValue();
				break;

			}

		}
		wb.close();
		return Query;
	}

	public void WriteCellData(String SheetName, String FieldName, String value) throws IOException {

		Workbook wb = null;
		String Query = null;

//initialize Workbook null  
		try {
//reading data from a file in the form of bytes  
			FileInputStream fis = new FileInputStream(FileName);
//constructs an XSSFWorkbook object, by buffering the whole stream into the memory  
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Sheet sh = wb.getSheet(SheetName); // getting the XSSFSheet object at given index

		int starRow = sh.getFirstRowNum();
		int endRow = sh.getLastRowNum();

		for (int i = starRow + 1; i < endRow; i++) {

			Cell c = sh.getRow(i).getCell(0);
			if (c.getStringCellValue().equals(FieldName)) {

				Cell c2 = sh.getRow(i).getCell(1);
				c2.setCellValue(value);
				break;

			}

		}
		FileOutputStream fos = new FileOutputStream(FileName);
		wb.write(fos);
		wb.close();

	}

	public void WriteCustomCellData(String File, String SheetName, String FieldName, String value) throws IOException {

		Workbook wb = null;
		// initialize Workbook null
		try {
			// reading data from a file in the form of bytes
			FileInputStream fis = new FileInputStream(File);
			// constructs an XSSFWorkbook object, by buffering the whole stream into the
			// memory
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Sheet sh = wb.getSheet(SheetName); // getting the XSSFSheet object at given index

		int starRow = sh.getFirstRowNum();
		int noOfColumns = sh.getRow(0).getLastCellNum();
		int Rowcount = sh.getPhysicalNumberOfRows();
		for (int i = 0; i < noOfColumns; i++) {

			Cell c = sh.getRow(0).getCell(i);
			if (c.getStringCellValue().equals(FieldName)) {

				Cell c2 = sh.getRow(1).getCell(i);
				c2.setCellValue(value);
				break;

			}

		}
		wb.setForceFormulaRecalculation(true);
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		evaluator.clearAllCachedResultValues();
		evaluator.evaluateAll();

		FileOutputStream fos = new FileOutputStream(File);
		wb.write(fos);
		fos.close();
		wb.close();

	}

	public String ReadcustomCellData(String SheetName, String FieldName) throws IOException
//public List <String> ReadcustomCellData(String SheetName, String FieldName) throws IOException  
	{
		
		String value = null; // variable for storing the cell value
		Workbook wb = null;
		String Query = null;
		String newQuery = null;
//String newQuery = null;
//List<String> Query = new ArrayList<>();
//initialize Workbook null  
		try {
//reading data from a file in the form of bytes  
			FileInputStream fis = new FileInputStream(FileName);
//constructs an XSSFWorkbook object, by buffering the whole stream into the memory  
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Sheet sh = wb.getSheet(SheetName); // getting the XSSFSheet object at given index

		int starRow = sh.getFirstRowNum();
		int endRow = sh.getLastRowNum() - 1;
//		System.out.println("starRow= " + starRow);
//		System.out.println("endRow= " + endRow);

		for (int j = 0; j < endRow; j++) {
			// System.out.println("first loop j = "+j);

			// for (int i = starRow; i < endRow; i++) {
			for (int i = 0; i < endRow; i++) {
				// System.out.println("second loop j = "+j+" i = "+i);
				Cell c = sh.getRow(i).getCell(j);
				// System.out.println(c.getStringCellValue());

				if (c.getStringCellValue().equals(FieldName)) {

					Cell c2 = sh.getRow(i).getCell(j + 1);
					// Query = c2.getStringCellValue();
					Query = c2.getStringCellValue();
					// System.out.println(Query);
					Cell c3 = sh.getRow(i).getCell(j + 2);
					newQuery = (c3.getStringCellValue());
					// System.out.println(newQuery);
					Query = Query + "," + newQuery;
					// System.out.println(Query);
					j = endRow;
					break;
				}

			}
		}
		wb.close();
		return Query;
//return newQuery;
	}

	

}
