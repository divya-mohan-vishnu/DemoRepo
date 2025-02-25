package com.framework.client;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TableExcelUtility {
	Workbook wb = null;
	Sheet sheet;

	public void setExcel(String path, String sheetname) {
		File src = new File(path);
		try {

			FileInputStream fis = new FileInputStream(src);
			wb = WorkbookFactory.create(fis);
//			System.out.println(wb);
			sheet = wb.getSheet(sheetname);

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public int getRowCount() {
		int rowCount = 0;
		try {
			rowCount = sheet.getLastRowNum() + 1;

		} catch (Exception e) {

		}
		return rowCount;
	}

	public String getdata(int rownum, int cellnum) {
		String data = "";
		try {
			DataFormatter formatter = new DataFormatter();
			Cell cell = ((org.apache.poi.ss.usermodel.Sheet) sheet).getRow(rownum).getCell(cellnum);
			if (cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.FORMULA) {
				try {
					cell.setCellType(CellType.NUMERIC);
					data = formatter.formatCellValue(
							((org.apache.poi.ss.usermodel.Sheet) sheet).getRow(rownum).getCell(cellnum));

				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
				// System.out.println(data);
				if (data.equals("")) {
					data = formatter.formatCellValue(
							((org.apache.poi.ss.usermodel.Sheet) sheet).getRow(rownum).getCell(cellnum));
				}
			}

			else {

				data = formatter
						.formatCellValue(((org.apache.poi.ss.usermodel.Sheet) sheet).getRow(rownum).getCell(cellnum));
				// System.out.println(data);
			}
			return data;
		} catch (Exception e) {
		}
		return data;
	}

	public void setFirstSheet(String path) {
		File src = new File(path);
		try {
			FileInputStream fis = new FileInputStream(src);
			wb = WorkbookFactory.create(fis);
			sheet = wb.getSheetAt(0);

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public int getcellCount() {
		int cellCount = 0;
		try {
			cellCount = ((org.apache.poi.ss.usermodel.Sheet) sheet).getRow(0).getPhysicalNumberOfCells();

		} catch (Exception e) {

		}
		return cellCount;

	}

	public void closeExcel(String path) {
		File src = new File(path);
		try {

			FileInputStream fis = new FileInputStream(src);
			
			fis.close();
			wb.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}
