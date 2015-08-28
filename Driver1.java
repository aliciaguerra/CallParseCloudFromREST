package com.audaexplore.parserest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import almonds.Parse;
import almonds.ParseException;
import almonds.ParseObject;
import almonds.ParseQuery;
import almonds.ParseResponse;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Driver {
	private static String PARSE_APPLICATION_ID = "tIyIZ4zyPCq3nx5KFALNFy4i7i2PqtzInLMKc69P3wHBXFZYn8rp11M4gz3wBiqhBzRMKRf8mE6JhCvttyjzDDP16bW065yAQwqWgAntF0aCNDtZcj4N2wmi";
	private static String PARSE_REST_API_KEY;
	private static String PARSE_MASTER_KEY;
	PrintWriter out = null;
	private static String filepathcsv = "C:\\mobile_daily_extract.csv";
	private static String filepath = "C:\\ClaimNumbers.txt";
	private static String parsefilepath = "C:\\ClaimNumbersFromParse.txt";

	
	public static void main(String[] args) {
		PARSE_APPLICATION_ID = args[0];
		PARSE_REST_API_KEY = args[1];
		PARSE_MASTER_KEY = args[2];
		Parse.initialize(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, PARSE_MASTER_KEY);

		try {	    
		    Driver driver = new Driver();
		    driver.lookupClaim();
		    driver.extractedclaimsintotext(filepathcsv);
		    driver.comparefiles();
		    driver.Claims24Hours();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void lookupClaim() throws ParseException {
		ParseQuery query = new ParseQuery("Claims");
		query.addDescendingOrder("createdAt");
		
		try {

			FileWriter writer=new FileWriter("C:\\ClaimNumbersFromParse.txt", true);
			
			query.setLimit(500);
			List<ParseObject> resultList = query.find();
			for(ParseObject singleClaim : resultList) {
				writer.write(singleClaim.getString("claimNumber"));
				writer.write("\r\n");
				
			}
			writer.close();
			
			System.out.println("Done getting claims from Parse");
			
		}
		
			catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void bufferedWrite(List<String> content, String fPath) {
        	 
       try {
		FileWriter writer = new FileWriter("C:\\ClaimNumbers.txt", true);  
		 for (String line : content) {
                writer.write(line, 0, line.length());
                
                writer.write("\r\n");
            }
		 writer.close();
        } 
       catch (IOException e) {

           e.printStackTrace();
        }
    }

			private void extractedclaimsintotext(String fpath) throws IOException {
			String csvfile = filepathcsv; 
			String line = null;
			int lineNumber = 0;
			String splitby = ",";
			final String filepath = "C:\\ClaimNumbers.txt";
			final String parsefilepath = "C:\\ClaimNumbersFromParse.txt";
			BufferedReader br;

		
			List<String> list = new ArrayList<String>();
			 
			try {
				br = new BufferedReader(new FileReader(csvfile));
				while ((line = br.readLine()) != null) {
						if ((line.length() != 0)){
							String a[] = line.split(splitby);
							Set<String> toRetain = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
							toRetain.addAll(list);
							Set<String> set = new LinkedHashSet<String>(list);
							set.retainAll(new LinkedHashSet<String>(toRetain));
							list = new ArrayList<String>(set);
							a[0] = a[0].replace("\"", "");
						
							list.add(a[0]);
												
						}					
				}	
				bufferedWrite(list, filepath);
				System.out.println(list.size() + " claims written to ClaimNumbers.txt.");
				System.out.println("Done isolating claim numbers from extract.");
				br.close();	
				
				
			}
			 catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		private void comparefiles() throws IOException {
			//final String filepath = "C:\\ClaimNumbers.txt";
			//String parsefilepath = "C:\\ClaimNumbersFromParse.txt";
			FileReader file1 = new FileReader(filepath);
			FileReader file2 = new FileReader(parsefilepath);
			List<String> list_extract = new ArrayList<String>();
			List<String> list_parse = new ArrayList<String>();
			BufferedReader buf1= new BufferedReader(file1);
			String str1 = buf1.readLine();
			BufferedReader buf2= new BufferedReader(file2);
			String str2= buf2.readLine(); 
			
			String z, y;

			while((z=buf1.readLine())!=null)
			list_extract.add(0,z);

			while((y=buf2.readLine())!=null)
				list_parse.add(0,y);
		
			FileWriter writer=new FileWriter("C:\\MissingClaims.txt", true);
			if (list_parse.containsAll(list_extract)){
					System.out.println("No missing claims");	
				}
				else{
					for (int j = 1; j < list_extract.size(); j++){
						String c = list_extract.get(j);
						if ((list_parse.contains(c)) == false) {
							System.out.println("missing claim " + c);
							//writer.write("missing claim" + c);
							//writer.write("\r\n");
						}
				}
				//writer.close();
				buf1.close();
			buf2.close();			
		}	
		}

int counter=1;

		private void Claims24Hours() throws ParseException {
			ParseQuery query2 = new ParseQuery("Claims");
			query2.addDescendingOrder("updatedAt");
			try{
			query2.setLimit(500);
			List<ParseObject> resultsList = query2.find();
			System.out.println("\n////////////////////////////////////////////////////////");
			System.out.println("The following claims have a customerStatus of 5\nand have an updatedAt time that's over 24 hours old:");
			   System.out.println("///////////////////////////////////////////////////////\n");
			for(ParseObject singleClaim : resultsList){
            //If the customerStatus is 5, we check to see if the updatedTime is within 24 Hours.					
        if(singleClaim.getInt("customerStatus") == 5) {
        	//Create a date formatter.
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH':'mm':'ss'.'SSS'Z'");
            //Create a date object for today's date.
            Date currentDate=new Date();
            //Create a string for the date from parse.
            String parseTime = singleClaim.getString("updatedAt");            
            //Initialize the date object for the updatedAt time on Parse.
            Date parseDate = null;
            try {
            //Here, we convert the parseTime string into a date object and format it.
                parseDate = formatter.parse(parseTime);
            } 
            catch (Exception e) {
                e.printStackTrace();
            }           
            //Get the time difference from the current date versus the date on Parse in milliseconds.
            long difference = currentDate.getTime() - parseDate.getTime();

			FileWriter writer = new FileWriter("C:\\Claim24Hours.txt", true);
            //If the time difference is more than 24 hours in milliseconds, we print out the claim number.
			if(difference>86400000)
            
			{					
			System.out.println(counter++ +".)" + singleClaim.getString("claimNumber"));
            writer.write(singleClaim.getString("claimNumber"));
            writer.write("\r\n");;
				}            
			writer.close();
			}}		

			System.out.println("\n////////////////////////////////////////////////////////");
			System.out.println("Done getting claims with customerStatus of 5 updated\nover 24 hours ago.");
			System.out.println("///////////////////////////////////////////////////////\n");
			}
			catch(Exception e){
			e.printStackTrace();
			}
				
		}}
		


	


	

