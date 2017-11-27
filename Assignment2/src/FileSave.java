
import java.io.BufferedWriter;
import java.io.FileWriter; 
 
public class FileSave {  
  
    public static void save(String filename, String filecontent) {  
        try {    
//            BufferedWriter writter = new BufferedWriter(new FileWriter(filename,true));
            BufferedWriter writter = new BufferedWriter(new FileWriter(filename,false));
            writter.write(filecontent);
            writter.flush();
            writter.close();
        } catch (Exception e) {  
//            e.printStackTrace();  
        }
    } 
    public static void cover(String filename, String filecontent) {  
        try {    
            BufferedWriter writter = new BufferedWriter(new FileWriter(filename,false));
            writter.write(filecontent);
            writter.flush();
            writter.close();
        } catch (Exception e) {  
//            e.printStackTrace();  
        }
    } 
    public static void clear(String filename) {  
        try {    
            BufferedWriter writter = new BufferedWriter(new FileWriter(filename,false));
            writter.write("");
            writter.flush();
            writter.close();
        } catch (Exception e) {  
//            e.printStackTrace();  
        }
    }
    
}  
