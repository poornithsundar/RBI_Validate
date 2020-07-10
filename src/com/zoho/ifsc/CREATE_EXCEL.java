package com.zoho.ifsc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.zoho.ifsc.IFSC_Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CREATE_EXCEL {
		//add data to sheet
		public static void add_data(XSSFSheet sheet,ArrayList<ArrayList<String>> data)
		{
			for(int i=0;i<data.size();i++)
			{
				Row r1 = sheet.createRow(i+1);
				for(int j=0;j<data.get(i).size();j++)
				{
					Cell c3 = r1.createCell(j);
					c3.setCellValue(data.get(i).get(j));
				}
			}
		}
		
		//create excel headers
		public static XSSFSheet create_sheet(String sheetname, ArrayList<String> headers, XSSFWorkbook workbook)
		{
			XSSFSheet sheet1 = workbook.createSheet(sheetname);
			Row ro1 = sheet1.createRow(0);
			for(int iter=0;iter<headers.size();iter++)
			{
				ro1.createCell(iter).setCellValue(headers.get(iter));
			}
			return sheet1;
		}
		
		//store data and save file method
		public static void insert_data(ArrayList<String> header, ArrayList<ArrayList<String>> a,ArrayList<ArrayList<String>> b,ArrayList<ArrayList<String>> c,ArrayList<ArrayList<String>> d,ArrayList<ArrayList<String>> e,ArrayList<ArrayList<String>> f) throws Exception
		{
			FileOutputStream xlsOutputStream = new FileOutputStream(new File(IFSC_Getter.valid_final_file_name));
			FileOutputStream xlsOutputStream2 = new FileOutputStream(new File(IFSC_Getter.invalid_final_file_name));
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFWorkbook workbook2 = new XSSFWorkbook();
			XSSFSheet sheet1 = create_sheet("NEFT",header,workbook);
			XSSFSheet sheet2 = create_sheet("RTGS",header,workbook);
			XSSFSheet sheet3 = create_sheet("NACH",header,workbook);
			XSSFSheet sheet4 = create_sheet("NEFT",header,workbook2);
			XSSFSheet sheet5 = create_sheet("RTGS",header,workbook2);
			XSSFSheet sheet6 = create_sheet("NACH",header,workbook2);
			try {
				add_data(sheet1,a);
				add_data(sheet2,b);
				add_data(sheet3,c);
				workbook.write(xlsOutputStream);
				add_data(sheet4,d);
				add_data(sheet5,e);
				add_data(sheet6,f);
				workbook2.write(xlsOutputStream2);
				System.out.println("Excel File is created .....!");
				workbook.close();
				workbook2.close();
			} catch (Exception ee) {
				System.out.println("Exception while reading Excel " + ee);
			} 
		}
}
