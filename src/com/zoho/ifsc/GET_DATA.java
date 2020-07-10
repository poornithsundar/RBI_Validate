package com.zoho.ifsc;

import com.zoho.ifsc.IFSC_Getter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GET_DATA {
	//split records into lists
	public static ArrayList<ArrayList<ArrayList<String>>> insert_records(String file_name, int sheet_start, ArrayList<String> headers)
	{
		ArrayList<String> header = new ArrayList<>();
		ArrayList<ArrayList<String>> valid = new ArrayList<>();
		ArrayList<ArrayList<String>> invalid = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> output = new ArrayList<>();
		IFSC_Getter.dup_count = 0;
		try {
			InputStream is = new FileInputStream(new File(file_name));
	
			XSSFWorkbook wb=new XSSFWorkbook(is);
			// InputStream or File for XLSX file (required)
			int i = sheet_start;
			XSSFSheet sheet1 = wb.getSheetAt(i);
			for(String head:headers)
			{
				int iter;
				for(iter=0;iter<sheet1.getRow(0).getPhysicalNumberOfCells();iter++)
				{
					String field = sheet1.getRow(0).getCell(iter).getStringCellValue();
					if(field.toLowerCase().contains(head.toLowerCase()))
					{
						header.add(iter+"");
						break;
					}
				}
				if(iter==sheet1.getRow(0).getPhysicalNumberOfCells())
				{
					header.add(null);
				}
			}
			while (i < wb.getNumberOfSheets())
			{
				int ifsc_index=0;
				XSSFSheet sheet = wb.getSheetAt(i);	
				for(int iter=0;iter<sheet.getRow(0).getPhysicalNumberOfCells();iter++)
				{
					String field = sheet.getRow(0).getCell(iter).getStringCellValue();
					if(field.contains("IFSC")==true || field.contains("Ifsc")==true)
					{
						ifsc_index = iter;
					}
				}
				for (int j=1;j<=sheet.getLastRowNum();j++)
				{
					Row r = sheet.getRow(j);
					ArrayList<String> temp = new ArrayList<>();
					ArrayList<String> data = new ArrayList<>();
					for (Cell cell : r)
					{
						String value = "";
						switch (cell.getCellType())
						{
							case STRING:
								value = cell.getRichStringCellValue().getString();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(cell))
								{
									value = cell.getDateCellValue().toString();
								}
								else
								{
									value = String.valueOf(cell.getNumericCellValue());
								}
								break;
							case BOOLEAN:
								value = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								break;
						}
						value = value.replaceAll("[^a-zA-Z0-9 -(),.]","");
						temp.add(value.trim());
					}
					String ifsc = temp.get(ifsc_index).trim();
					for(int k=0;k<temp.size();k++)
					{
						data.add(temp.get(k).trim());	
					}
					ArrayList<String> data2 = new ArrayList<>();
					for(String head:header)
					{
						if(head!=null)
						{
							data2.add(data.get(Integer.parseInt(head)));
						}
						else
						{
							data2.add("NA");
						}
					}
					if (ifsc.trim().length() == 11 && !(IFSC_Getter.ifsc_check.contains(ifsc.trim())))
					{
						valid.add(data2);
						IFSC_Getter.ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.trim().length() == 11)
					{
						IFSC_Getter.dup_count++;
					}
					else
					{
						System.out.println("********** Row index = "+j+". IFSC Error="+data2+" **********");
						invalid.add(data2);
					}
				}
				i++;
			}
			wb.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		output.add(valid);
		output.add(invalid);
		return output;
	}
}
