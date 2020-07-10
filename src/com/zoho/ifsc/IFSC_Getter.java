package com.zoho.ifsc;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IFSC_Getter
{
	static ArrayList<String> headers = new ArrayList<>();
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
	static String neft = "https://rbidocs.rbi.org.in/rdocs/content/docs/68774.xlsx";
	static String rtgs = "https://rbidocs.rbi.org.in/rdocs/RTGS/DOCs/RTGEB0815.xlsx";
	static String nach = "https://www.npci.org.in/national-automated-clearing-live-members-1";
	static String neft_file_name = "NEFT_"+java.time.LocalDate.now()+".xlsx";
	static String rtgs_file_name = "RTGS_"+java.time.LocalDate.now()+".xlsx";
	static String valid_final_file_name = "IFSC_VALID_"+java.time.LocalDate.now()+".xlsx";
	static String invalid_final_file_name = "IFSC_INVALID_"+java.time.LocalDate.now()+".xlsx";
	
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
		    ArrayList<String> header = new ArrayList<>();
		    for(String head:headers)
			{
				int iter;
				for(iter=0;iter<NACH_headers.size();iter++)
				{
					String field = NACH_headers.get(iter);
					if(field.toLowerCase().contains(head.toLowerCase()))
					{
						header.add(iter+"");
						break;
					}
				}
				if(iter==NACH_headers.size())
				{
					header.add(null);
				}
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
				if (ifsc.trim().length() == 11 && !(ifsc_check.contains(ifsc.trim())))
				{
					NACH_valid.add(data2);
					ifsc_check.add(ifsc.trim());
				}
				else if (ifsc.trim().length() == 11)
				{
					dup_count++;
				}
				else
				{
					System.out.println("********** Row index = "+index+". IFSC Error="+data2+" **********");
					NACH_invalid.add(data2);
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
		int neft_duplicate_count=0,rtgs_duplicate_count=0;
		//default header list
		headers.add("Name");
		headers.add("Ifsc");
		headers.add("Micr");
		headers.add("Branch");
		headers.add("Address");
		headers.add("Contact");
		headers.add("City");
		headers.add("District");
		headers.add("State");
		try {
			System.out.println("****************** NEFT STARTED *********************");
			//NEFT file header list
			//Using different lists because files used different names for columns required (Eg: City column is named as 'CITY' in NEFT but as 'CENTRE' in RTGS)
			NEFT_headers.add("Bank");
			NEFT_headers.add("Ifsc");
			NEFT_headers.add("Micr");
			NEFT_headers.add("Branch");
			NEFT_headers.add("Address");
			NEFT_headers.add("Contact");
			NEFT_headers.add("City");
			NEFT_headers.add("District");
			NEFT_headers.add("State");
			ArrayList<ArrayList<ArrayList<String>>> output = GET_DATA.insert_records(neft_file_name,0,NEFT_headers);
			NEFT_valid.addAll(output.get(0));
			NEFT_invalid.addAll(output.get(1));
			neft_duplicate_count = dup_count;
			System.out.println("****************** NEFT COMPLETED *********************");
			dup_count=0;
			System.out.println("\n\n\n ****************** RTGS STARTED *********************");
			//RTGS file header list
			RTGS_headers.add("Name");
			RTGS_headers.add("Ifsc");
			RTGS_headers.add("Micr");
			RTGS_headers.add("Branch");
			RTGS_headers.add("Address");
			RTGS_headers.add("Contact");
			RTGS_headers.add("Centre");
			RTGS_headers.add("District");
			RTGS_headers.add("State");
			output = GET_DATA.insert_records(rtgs_file_name,0,RTGS_headers);
			RTGS_valid.addAll(output.get(0));
			RTGS_invalid.addAll(output.get(1));
			rtgs_duplicate_count = dup_count;
			System.out.println("****************** RTGS COMPLETED *********************");
			dup_count=0;
			System.out.println("\n\n\n ****************** NACH STARTED *********************");
			get_NACH();
			System.out.println("****************** NACH COMPLETED *********************");
			System.out.println("\n\n\nFILE\t - \tVALID\t - \tINVALID\t - \tDUPLI\t - \tTOTAL RECORDS");
			System.out.println("NEFT"+"\t - \t"+NEFT_valid.size()+"\t - \t"+NEFT_invalid.size()+"\t - \t"+neft_duplicate_count+"\t - \t"+(NEFT_valid.size()+NEFT_invalid.size()+neft_duplicate_count));
			System.out.println("RTGS"+"\t - \t"+RTGS_valid.size()+"\t - \t"+RTGS_invalid.size()+"\t - \t"+rtgs_duplicate_count+"\t - \t"+(RTGS_valid.size()+RTGS_invalid.size()+rtgs_duplicate_count));
			System.out.println("NACH"+"\t - \t"+NACH_valid.size()+"\t - \t"+NACH_invalid.size()+"\t - \t"+dup_count+"\t - \t"+(NACH_valid.size()+NACH_invalid.size()+dup_count));
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("TOTAL"+"\t - \t"+(NEFT_valid.size()+RTGS_valid.size()+NACH_valid.size())+"\t - \t"+(NEFT_invalid.size()+RTGS_invalid.size()+NACH_invalid.size())+"\t - \t"+(neft_duplicate_count+rtgs_duplicate_count+dup_count)+"\t - \t"+((NEFT_valid.size()+NEFT_invalid.size()+neft_duplicate_count)+(RTGS_valid.size()+RTGS_invalid.size()+rtgs_duplicate_count)+(NACH_valid.size()+NACH_invalid.size()+dup_count)));
			System.out.println("------------------------------------------------------------------------------");			
			CREATE_EXCEL.insert_data(headers,NEFT_valid,RTGS_valid,NACH_valid,NEFT_invalid,RTGS_invalid,NACH_invalid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}