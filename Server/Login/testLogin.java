import java.net.Socket;
import java.io.OutputStreamWriter;

public class testLogin {  
   
   public static void main(String args[]) throws Exception {  
     String host = "127.0.0.1";
     int port = 5000;  
     Socket client = new Socket(host, port);  
     OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());  
     osw.write("{\"username\":\"lhc1\",\"password\":\"111\"}");  
     osw.close();   
     client.close();  
   }  
     
}  
