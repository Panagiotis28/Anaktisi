package lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class txtGenerator {
	private static String paperID;
	private static String title;
	private static String authors;
	private static String ab;
	private static String bodyText;
	
	
	public void generateTxt() {
		File path = new File("C:\\Users\\Panagiotis\\Desktop\\anak\\corpus");
	    File [] files = path.listFiles();
	    
	    try (PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Panagiotis\\Desktop\\anak\\euretirio.txt"))) {
	   
	    	for (int i = 0; i < files.length; i++){
		        jsonIterator(files[i].toString());
		        
		        out.println("https://api.semanticscholar.org/"+paperID);
		        out.print("\n");
		        out.println(title);
		        out.print("\n");
		        out.println(authors);
		        out.print("\n");
		        out.println(ab);
		        out.print("\n");
		        out.println(bodyText);
		        out.print("\n");
		        
		        title = "";
		        authors = "";
		        ab = "";
		        bodyText = "";
		        
		       
		        
		    }
	    	
	    }catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	    }
	}
	
	
    
	


	public static void jsonIterator(String filename) {
		JSONParser jsonParser = new JSONParser();
	      try {
	    	  
	    	  
	
	         JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(filename));
	        
	         paperID = "";
	         paperID = jsonObject.get("paper_id").toString();
	         
	         
	         title = "";
	         JSONObject metadata = (JSONObject) jsonObject.get("metadata");
	         title = metadata.get("title").toString();
	         
	         JSONArray jsonArrayAuthors = (JSONArray) metadata.get("authors");
	         Iterator<JSONObject> authorsIterator = jsonArrayAuthors.iterator();
	         
	    
	         authors = "";
	         while(authorsIterator.hasNext()) {
		            JSONObject test = authorsIterator.next();
		            authors += test.get("first").toString()+" ";
		            
		            if(!test.get("middle").toString().equals("[]")) {
		            	JSONArray mid = (JSONArray) test.get("middle");
		            	String f =  (String) mid.get(0);
		            	
		            	authors += f+" ";
		            	
		            }
		            
		            if(!test.get("last").toString().equals("")) {
		            	authors += test.get("last").toString()+" ";
			            
		            }
		           
		            
		     }
	         
	         JSONArray jsonArrayAbstract = (JSONArray) jsonObject.get("abstract");
	         
	         //Iterating the contents of the array
	         Iterator<JSONObject> abstractIterator = jsonArrayAbstract.iterator();
	         ab = "";
	         while(abstractIterator.hasNext()) {
	            ab += abstractIterator.next().get("text").toString();
	         }
	         
	         
	         JSONArray jsonArray = (JSONArray) jsonObject.get("body_text");
	         
	         Iterator<JSONObject> iterator = jsonArray.iterator();
	         
	         bodyText = "";
	         while(iterator.hasNext()) {
	        	 bodyText+=iterator.next().get("text").toString();
	         }
	         
	
	    
	      } catch (FileNotFoundException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	            e.printStackTrace();
	      } catch (ParseException e) {
	            e.printStackTrace();
	      }
	
	}
}
	
