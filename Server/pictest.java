import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.OutputStreamWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;

public class pictest {

	public static void main(String args[]) throws Exception {
		int port = 5005;
		String host = "127.0.0.1";
		final Socket client = new Socket(host, port);
		


//insert picture to database
///*
		String filename = "./large.jpg";
		Path path = Paths.get(filename);
		byte[] bytes = Files.readAllBytes(path);
		System.out.println(bytes.length);
		try{
			InputStream bytestream = new ByteArrayInputStream(bytes);
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(bytestream));
			OutputStreamWriter osw1 = new OutputStreamWriter(client.getOutputStream());
			String temp;

final BufferedOutputStream outStream = new BufferedOutputStream(client.getOutputStream());
/*
			while(true){
				temp = bfReader.readLine();
				if (temp == null) break; 
				osw1.write(temp);

			}
*/
			String decoded = new String(bytes, "UTF-8");
			//osw1.write(bytes,0,bytes.length);
			//osw1.flush();
System.out.println(bytes.length);
outStream.write(bytes,0,bytes.length);
//outStream.close();
		} catch (Exception e){
			e.printStackTrace();
		}
//*/


/*
		new Thread(){
			public void run(){
				try {
					BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
					OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
					while(true){
						osw.write(screenReader.readLine());
						osw.flush();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
*/

		BufferedReader responseReader= new BufferedReader(new InputStreamReader(client.getInputStream()));
		while(true){
			String response=responseReader.readLine();
			if (response==null) System.exit(-1);
			System.out.println(response);
			System.out.flush();
		}
	}
}
