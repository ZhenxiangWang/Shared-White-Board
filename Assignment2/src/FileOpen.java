
import java.io.BufferedReader;

import java.io.File;  
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.print.DocFlavor.STRING;  

public class FileOpen {  
  
    public static String read(String filepath) {  
        BufferedReader reader = null;
        String returnstring = new String();
        try {
        
        		FileInputStream inputStream = new FileInputStream(filepath);
          	
        		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
        		reader = new BufferedReader(inputStreamReader);
        		String tmpString = new String();
        		while((tmpString = reader.readLine())!=null) {
        			returnstring +=tmpString+"\n";
        		}
        		reader.close();
        }catch (IOException e) {
//			e.printStackTrace();
		}
        return returnstring;
    }  
  
}  
