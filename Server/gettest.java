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

public class gettest {

	public static void main(String args[]) throws Exception {
		int port = 5005;
		String host = "127.0.0.1";
		final Socket client = new Socket(host, port);
		



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


		BufferedReader responseReader= new BufferedReader(new InputStreamReader(client.getInputStream()));
		while(true){
			String response=responseReader.readLine();
			if (response==null) System.exit(-1);
			System.out.println(response);
			System.out.flush();
		}
	}
}
