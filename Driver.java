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


public class Driver {
	private static String PARSE_APPLICATION_ID;
	private static String PARSE_REST_API_KEY;
	private static String PARSE_MASTER_KEY;
	PrintWriter out = null;
	private static String filepathcsv = "C:\\Users\\alicia.guerra\\My Documents\\mobile_daily_extract.csv";;
	private static String filepath = "C:\\Users\\alicia.guerra\\My Documents\\ClaimNumbers.txt";;
	private static String parsefilepath = "C:\\Users\\alicia.guerra\\My Documents\\ClaimNumbersFromParse.txt";
	File ClaimNumbersFile = new File(filepath);
	File ClaimNumbersFromParse = new File(parsefilepath);
	Writer fileWriter = null;
	BufferedWriter bw = null;
	
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
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void lookupClaim() throws ParseException {
		ParseQuery query = new ParseQuery("Claims");
		query.addDescendingOrder("createdAt");
		
		try {
			fileWriter = new FileWriter(ClaimNumbersFromParse);
			bw = new BufferedWriter(fileWriter);
			query.setLimit(500);
			List<ParseObject> resultList = query.find();
			for(ParseObject singleClaim : resultList) {
				bw.write(singleClaim.getString("claimNumber"));
				bw.newLine();
			}
			bw.close();
			fileWriter.close();
			System.out.println("Done getting claims from Parse");
			
		}
		
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		public void bufferedWrite(List<String> content, String fPath) {
			
			        Path fileP = Paths.get(filepath);		
			        Charset charset = Charset.forName("utf-8");
			        try (BufferedWriter writer = Files.newBufferedWriter(fileP, charset)) {	 
			            for (String line : content) {
			                writer.write(line, 0, line.length());
			                writer.newLine();
			            }		
			        } catch (IOException e) {
			
			            e.printStackTrace();
			        }
			    }

			private void extractedclaimsintotext(String fpath) throws IOException {
			String csvfile = filepathcsv; 
			String line = null;
			int lineNumber = 0;
			String splitby = ",";
			
			BufferedReader br;
			
			List<String> list = new ArrayList<String>();
			 
			try {
				br = new BufferedReader(new FileReader(csvfile));
				while ((line = br.readLine()) != null) {
					//System.out.println(br.readLine());
					//lineNumber++;
					//for(int i = 0; ;i++){
						//System.out.println(lineNumber);
						//line = br.readLine();
						if ((line.length() != 0)){
							String a[] = line.split(splitby);
							Set<String> toRetain = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
							toRetain.addAll(list);
							Set<String> set = new LinkedHashSet<String>(list);
							set.retainAll(new LinkedHashSet<String>(toRetain));
							list = new ArrayList<String>(set);
							//a[0].substring(1,12);
							a[0] = a[0].replace("\"", "");
						
							list.add(a[0]);
												
						}					
					//}
				}
				bufferedWrite(list, filepath);
				System.out.println(list.size());
				System.out.println("Done isolating claim numbers from extract");
				br.close();	
				
				
			}
			 catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		private void comparefiles() throws IOException {
			FileReader file1 = new FileReader(filepath);
			FileReader file2 = new FileReader(parsefilepath);
			List<String> list_extract = new ArrayList<String>();
			List<String> list_parse = new ArrayList<String>();
			BufferedReader buf1= new BufferedReader(file1);
			String str1 = buf1.readLine();
			BufferedReader buf2= new BufferedReader(file2);
			String str2= buf2.readLine(); 
			
			String z, y;
			//String s2="", s3[]="", s4="", y="", z="";
			
			while((z=buf1.readLine())!=null)
			list_extract.add(0,z);
			
			while((y=buf2.readLine())!=null)
				list_parse.add(0,y);
				
				if (list_parse.containsAll(list_extract)){
					System.out.println("No missing claims");		
				}
				else{
					for (int j = 0; j < list_extract.size(); j++){
						String c = list_extract.get(j);
						if ((list_parse.contains(c)) == false) {
							System.out.println("missing claim " + c);
						}
				}
				
				buf1.close();
			buf2.close();
			
		}	
		}
}
	
	

