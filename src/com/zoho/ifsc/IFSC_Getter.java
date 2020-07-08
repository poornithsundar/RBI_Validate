package com.zoho.ifsc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IFSC_Getter
{
	public static void insert_data(ArrayList<ArrayList<String>> a,ArrayList<ArrayList<String>> b,ArrayList<ArrayList<String>> c,ArrayList<ArrayList<String>> d,ArrayList<ArrayList<String>> e,ArrayList<ArrayList<String>> f) throws Exception
	{
		FileOutputStream xlsOutputStream = new FileOutputStream(new File("valid.xlsx"));
		FileOutputStream xlsOutputStream2 = new FileOutputStream(new File("invalid.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFWorkbook workbook2 = new XSSFWorkbook();
		XSSFSheet sheet1 = workbook.createSheet("NEFT");
		Row ro1 = sheet1.createRow(0);
		ro1.createCell(0).setCellValue("Bank Name");
		ro1.createCell(1).setCellValue("IFSC CODE");
		ro1.createCell(2).setCellValue("Branch");
		XSSFSheet sheet2 = workbook.createSheet("RTGS");
		ro1 = sheet2.createRow(0);
		ro1.createCell(0).setCellValue("Bank Name");
		ro1.createCell(1).setCellValue("IFSC CODE");
		ro1.createCell(2).setCellValue("Branch");
		XSSFSheet sheet3 = workbook.createSheet("NACH");
		ro1 = sheet3.createRow(0);
		ro1.createCell(0).setCellValue("Branch Code");
		ro1.createCell(1).setCellValue("Bank Name");
		ro1.createCell(2).setCellValue("IFSC CODE");
		XSSFSheet sheet4 = workbook2.createSheet("NEFT");
		ro1 = sheet4.createRow(0);
		ro1.createCell(0).setCellValue("Bank Name");
		ro1.createCell(1).setCellValue("IFSC CODE");
		ro1.createCell(2).setCellValue("Branch");
		XSSFSheet sheet5 = workbook2.createSheet("RTGS");
		ro1 = sheet5.createRow(0);
		ro1.createCell(0).setCellValue("Bank Name");
		ro1.createCell(1).setCellValue("IFSC CODE");
		ro1.createCell(2).setCellValue("Branch");
		XSSFSheet sheet6 = workbook2.createSheet("NACH");
		ro1 = sheet6.createRow(0);
		ro1.createCell(0).setCellValue("Branch Code");
		ro1.createCell(1).setCellValue("Bank Name");
		ro1.createCell(2).setCellValue("IFSC CODE");
		try {
			int i;
			for(i=0;i<a.size();i++)
			{
				Row r1 = sheet1.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(a.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(a.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(a.get(i).get(2));
			}
			for(i=0;i<b.size();i++)
			{
				Row r1 = sheet2.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(b.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(b.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(b.get(i).get(2));
			}
			for(i=0;i<c.size();i++)
			{
				Row r1 = sheet3.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(c.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(c.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(c.get(i).get(2));
			}
			workbook.write(xlsOutputStream);
			for(i=0;i<d.size();i++)
			{
				Row r1 = sheet4.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(d.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(d.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(d.get(i).get(2));
			}
			for(i=0;i<e.size();i++)
			{
				Row r1 = sheet5.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(e.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(e.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(e.get(i).get(2));
			}
			for(i=0;i<f.size();i++)
			{
				Row r1 = sheet6.createRow(i+1);
				Cell c3 = r1.createCell(0);
				c3.setCellValue(f.get(i).get(0));
				Cell c4 = r1.createCell(1);
				c4.setCellValue(f.get(i).get(1));
				Cell c5 = r1.createCell(2);
				c5.setCellValue(f.get(i).get(2));
			}
			workbook2.write(xlsOutputStream2);
			System.out.println("Excel File is created .....!");
			workbook.close();
			workbook2.close();
		} catch (Exception ee) {
			System.out.println("Exception while reading Excel " + ee);
		} 
	}
	public static void main(String[] args) throws Exception
	{		
		int dup_count = 0;
		ArrayList<ArrayList<String>> NEFT_valid = new ArrayList<>();
		ArrayList<ArrayList<String>> NEFT_invalid = new ArrayList<>();
		ArrayList<ArrayList<String>> RTGS_valid = new ArrayList<>();
		ArrayList<ArrayList<String>> RTGS_invalid = new ArrayList<>();
		ArrayList<ArrayList<String>> NACH_valid = new ArrayList<>();
		ArrayList<ArrayList<String>> NACH_invalid = new ArrayList<>();
		ArrayList<String> ifsc_check = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(new File("NEFT_Check.xlsx"));
	
			XSSFWorkbook wb=new XSSFWorkbook(is);
			// InputStream or File for XLSX file (required)
			int i = 0;
			while (i < wb.getNumberOfSheets())
			{
				XSSFSheet sheet = wb.getSheetAt(i);		
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
								System.out.println("Invalid value");
						}
						value = value.replaceAll("[^a-zA-Z0-9 ]","");
						temp.add(value.trim());
					}
					String ifsc = temp.get(1);
					data.add(temp.get(0));
					data.add(temp.get(1));
					data.add(temp.get(2));
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						NEFT_valid.add(data);
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.trim().length() == 11)
					{
						dup_count++;
					}
					else
					{
						NEFT_invalid.add(data);
					}
				}
				i++;
			}
			wb.close();
			System.out.println("\n\n\n ****************** NEFT COMPLETED *********************");
			System.out.println("NEFT Duplicate_Count = "+ dup_count);
			dup_count=0;
			is = new FileInputStream(new File("RTGS_Check.xlsx"));
			wb=new XSSFWorkbook(is);
			// InputStream or File for XLSX file (required)
			i = 1;
			while (i < wb.getNumberOfSheets())
			{
				XSSFSheet sheet = wb.getSheetAt(i);		
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
						value = value.replaceAll("[^a-zA-Z0-9 ]","");
						temp.add(value.trim());
					}
					String ifsc = temp.get(1);
					data.add(temp.get(0));
					data.add(temp.get(1));
					data.add(temp.get(2));
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						RTGS_valid.add(data);
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.trim().length() == 11)
					{
						dup_count++;
					}
					else
					{
						RTGS_invalid.add(data);
					}
				}
				i++;
			}
			System.out.println("\n\n\n ****************** RTGS COMPLETED *********************");
			System.out.println("RTGS Duplicate_Count = "+ dup_count);
			dup_count=0;
			TrustManager[] trustAllCerts = new TrustManager[]{
			        new X509TrustManager() {
			            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			                return null;
			            }

			            public void checkClientTrusted(
			                    java.security.cert.X509Certificate[] certs, String authType) {
			            }

			            public void checkServerTrusted(
			                    java.security.cert.X509Certificate[] certs, String authType) {
			            }
			        }
			};

			// Install the all-trusting trust manager
			try {
			    SSLContext sc = SSLContext.getInstance("SSL");
			    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
			    System.out.println("Error" + e);
			}

			// Now you can access URL(https) without having the certificate in the truststore
			try {

			    HostnameVerifier hv = new HostnameVerifier() {
			        public boolean verify(String urlHostName, SSLSession session) {
			            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
			                    + session.getPeerHost());
			            return true;
			        }
			    };

			    String datam = "param=myparam";
			    URL url = new URL("https://www.npci.org.in/national-automated-clearing-live-members-1");
			    URLConnection conn = url.openConnection();
			    HttpsURLConnection urlConn = (HttpsURLConnection) conn;
			    urlConn.setHostnameVerifier(hv);
			    conn.setDoOutput(true);
			    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			    wr.write(datam);
			    wr.flush();

			    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			    StringBuilder sb = new StringBuilder();
			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        sb.append(inputLine);
			    }
			    in.close();
			    String res = sb.toString();
			    Document doc = Jsoup.parse(res);
			    Element table = doc.select("table").get(0);
			    Iterator<Element> ite = table.select("td").iterator();
			    i=0;
			    while(ite.hasNext())
			    {
			    	ArrayList<String> temp = new ArrayList<>();
			    	ArrayList<String> data = new ArrayList<>();
			    	ite.next();
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        temp.add(ite.next().text());
			        ite.next();
			        String ifsc = temp.get(3);
			        data.add(temp.get(0));
					data.add(temp.get(1));
					data.add(temp.get(3));
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						NACH_valid.add(data);
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.trim().length() == 11)
					{
						dup_count++;
					}
					else
					{
						NACH_invalid.add(data);
					}
				}
			    System.out.println("\n\n\n ****************** NACH COMPLETED *********************");
			} catch (MalformedURLException e) {
			    System.out.println("Error in SLL Connetion" + e);
			}
			System.out.println("NACH Duplicate_Count = "+ dup_count);
			System.out.println("VALID\t - \tINVALID");
			System.out.println(NEFT_valid.size()+"\t - \t"+NEFT_invalid.size());
			System.out.println(RTGS_valid.size()+"\t - \t"+RTGS_invalid.size());
			System.out.println(NACH_valid.size()+"\t - \t"+NACH_invalid.size());
			insert_data(NEFT_valid,RTGS_valid,NACH_valid,NEFT_invalid,RTGS_invalid,NACH_invalid);
			wb.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}