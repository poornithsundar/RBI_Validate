package com.zoho.ifsc;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IFSC_Getter
{
	static ArrayList<String> NEFT_headers = new ArrayList<>();
	static ArrayList<String> RTGS_headers = new ArrayList<>();
	static ArrayList<String> NACH_headers = new ArrayList<>();
	static ArrayList<ArrayList<String>> NEFT_valid = new ArrayList<>();
	static ArrayList<ArrayList<String>> NEFT_invalid = new ArrayList<>();
	static ArrayList<ArrayList<String>> RTGS_valid = new ArrayList<>();
	static ArrayList<ArrayList<String>> RTGS_invalid = new ArrayList<>();
	static ArrayList<ArrayList<String>> NACH_valid = new ArrayList<>();
	static ArrayList<ArrayList<String>> NACH_invalid = new ArrayList<>();
	static ArrayList<String> ifsc_check = new ArrayList<>();
	static int dup_count = 0;
	private static String neft = "https://rbidocs.rbi.org.in/rdocs/content/docs/68774.xlsx";
	private static String rtgs = "https://rbidocs.rbi.org.in/rdocs/RTGS/DOCs/RTGEB0815.xlsx";
	private static String nach = "https://www.npci.org.in/national-automated-clearing-live-members-1";
	private static String neft_file_name = "NEFT_"+java.time.LocalDate.now()+".xlsx";
	private static String rtgs_file_name = "RTGS_"+java.time.LocalDate.now()+".xlsx";
	private static String valid_final_file_name = "IFSC_VALID_"+java.time.LocalDate.now()+".xlsx";
	private static String invalid_final_file_name = "IFSC_INVALID_"+java.time.LocalDate.now()+".xlsx";
	
	//download files method
	private static void downloadUsingStream(String urlStr, String file) throws Exception{
	    URL url = new URL(urlStr);
	    BufferedInputStream bis = new BufferedInputStream(url.openStream());
	    FileOutputStream fis = new FileOutputStream(file);
	    byte[] buffer = new byte[1024];
	    int count=0;
	    while((count = bis.read(buffer,0,1024)) != -1)
	    {
	        fis.write(buffer, 0, count);
	    }
	    fis.close();
	    bis.close();
	    System.out.println("**************"+file+" downloaded************");
	}
	
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
	public static void insert_data(ArrayList<String> neft_header, ArrayList<String> rtgs_header, ArrayList<String> nach_header, ArrayList<ArrayList<String>> a,ArrayList<ArrayList<String>> b,ArrayList<ArrayList<String>> c,ArrayList<ArrayList<String>> d,ArrayList<ArrayList<String>> e,ArrayList<ArrayList<String>> f) throws Exception
	{
		FileOutputStream xlsOutputStream = new FileOutputStream(new File(valid_final_file_name));
		FileOutputStream xlsOutputStream2 = new FileOutputStream(new File(invalid_final_file_name));
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFWorkbook workbook2 = new XSSFWorkbook();
		XSSFSheet sheet1 = create_sheet("NEFT",neft_header,workbook);
		XSSFSheet sheet2 = create_sheet("RTGS",rtgs_header,workbook);
		XSSFSheet sheet3 = create_sheet("NACH",nach_header,workbook);
		XSSFSheet sheet4 = create_sheet("NEFT",neft_header,workbook2);
		XSSFSheet sheet5 = create_sheet("RTGS",rtgs_header,workbook2);
		XSSFSheet sheet6 = create_sheet("NACH",nach_header,workbook2);
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
	
	//split records into valid and invalid
	public static ArrayList<ArrayList<ArrayList<String>>> insert_records(String file_name, int sheet_start)
	{
		ArrayList<String> header = new ArrayList<>();
		ArrayList<ArrayList<String>> valid = new ArrayList<>();
		ArrayList<ArrayList<String>> headers = new ArrayList<>();
		ArrayList<ArrayList<String>> invalid = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> output = new ArrayList<>();
		dup_count = 0;
		try {
			InputStream is = new FileInputStream(new File(file_name));
	
			XSSFWorkbook wb=new XSSFWorkbook(is);
			// InputStream or File for XLSX file (required)
			int i = sheet_start;
			while (i < wb.getNumberOfSheets())
			{
				int ifsc_index=0;
				int micr_index=0;
				XSSFSheet sheet = wb.getSheetAt(i);	
				for(int iter=0;iter<sheet.getRow(0).getPhysicalNumberOfCells();iter++)
				{
					String field = sheet.getRow(0).getCell(iter).getStringCellValue();
					header.add(field);
					if(field.contains("IFSC")==true || field.contains("Ifsc")==true)
					{
						ifsc_index = iter;
					}
					else if(field.contains("MICR")==true || field.contains("Micr")==true)
					{
						micr_index = iter;
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
						value = value.replaceAll("[^a-zA-Z0-9 ]","");
						temp.add(value.trim());
					}
					String ifsc = temp.get(ifsc_index).trim();
					for(int k=0;k<temp.size();k++)
					{
						if(k==micr_index)
						{
							String mic = (temp.get(micr_index).trim().substring(0,temp.get(micr_index).trim().length()-2));
							if(mic.length()!=9)
							{
								data.add(String.format("%1$-" + 9 + "s", mic).replace(' ', '0'));
							}
							else
							{
								data.add(mic);
							}
						}
						else
						{
							data.add(temp.get(k).trim());	
						}
					}
					if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
					{
						valid.add(data);
						ifsc_check.add(ifsc.trim());
					}
					else if (ifsc.trim().length() == 11)
					{
						dup_count++;
					}
					else
					{
						System.out.println("********** Row index = "+j+". IFSC Error="+data+" **********");
						invalid.add(data);
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
		headers.add(header);
		output.add(valid);
		output.add(invalid);
		output.add(headers);
		return output;
	}
	
	//get NACH data and store in valid and invalid
	public static void get_NACH() throws Exception
	{
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
		    URL url = new URL(nach);
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
		    Iterator<Element> iter2 = table.select("th").iterator();
		    int ifsc_index=0,i=0;
		    while(iter2.hasNext())
		    {
		    	String field = iter2.next().text();
		    	NACH_headers.add(field);
		    	if(field.contains("IFSC")==true || field.contains("Ifsc")==true)
				{
					ifsc_index = i;
				}
		    	i++;
		    }
		    
		    int index=1;
		    while(ite.hasNext())
		    {
		    	ArrayList<String> temp = new ArrayList<>();
		    	ArrayList<String> data = new ArrayList<>();
		    	temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        temp.add(ite.next().text());
		        String ifsc = temp.get(ifsc_index).trim();
		        data.addAll(temp);
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
					System.out.println("********** Row index = "+index+". IFSC Error="+data+" **********");
					NACH_invalid.add(data);
				}
				index++;
			}
		} catch (MalformedURLException e) {
		    System.out.println("Error in SLL Connetion" + e);
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{		
		//Download files using the urls
	    try {
	    	System.out.println("************** NEFT file is downloading ************");
	        downloadUsingStream(neft, neft_file_name);
	        System.out.println("************** RTGS file is downloading ************");
	        downloadUsingStream(rtgs, rtgs_file_name);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		try {
			System.out.println("****************** NEFT STARTED *********************");
			ArrayList<ArrayList<ArrayList<String>>> output = insert_records(neft_file_name,0);
			NEFT_valid.addAll(output.get(0));
			NEFT_invalid.addAll(output.get(1));
			NEFT_headers.addAll(output.get(2).get(0));
			System.out.println("NEFT Duplicate_Count = "+ dup_count);
			System.out.println("****************** NEFT COMPLETED *********************");
			dup_count=0;
			System.out.println("\n\n\n ****************** RTGS STARTED *********************");
			output = insert_records(rtgs_file_name,0);
			RTGS_valid.addAll(output.get(0));
			RTGS_invalid.addAll(output.get(1));
			RTGS_headers.addAll(output.get(2).get(0));
			System.out.println("RTGS Duplicate_Count = "+ dup_count);
			System.out.println("****************** RTGS COMPLETED *********************");
			dup_count=0;
			System.out.println("\n\n\n ****************** NACH STARTED *********************");
			get_NACH();
			System.out.println("NACH Duplicate_Count = "+ dup_count);
			System.out.println("****************** NACH COMPLETED *********************");
			System.out.println("\n\n\nVALID\t - \tINVALID");
			System.out.println(NEFT_valid.size()+"\t - \t"+NEFT_invalid.size());
			System.out.println(RTGS_valid.size()+"\t - \t"+RTGS_invalid.size());
			System.out.println(NACH_valid.size()+"\t - \t"+NACH_invalid.size());
			insert_data(NEFT_headers,RTGS_headers,NACH_headers,NEFT_valid,RTGS_valid,NACH_valid,NEFT_invalid,RTGS_invalid,NACH_invalid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}