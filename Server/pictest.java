import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.OutputStreamWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;


//import org.apache.commons.codec.binary.Base64;

public class pictest {

	public static void main(String args[]) throws Exception {
		int port = 9071;
		String host = "blitzproject.cs.purdue.edu";
		//host = "127.0.0.1";
		final Socket client = new Socket(host, port);
		


//insert picture to database
///*
		String filename = "./large.jpg";
		Path path = Paths.get(filename);
		byte[] bytes = Files.readAllBytes(path);
		try{
			InputStream bytestream = new ByteArrayInputStream(bytes);
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(bytestream));
			OutputStreamWriter osw1 = new OutputStreamWriter(client.getOutputStream());
			String temp;

final BufferedOutputStream outStream = new BufferedOutputStream(client.getOutputStream());
			String decoded = new String(bytes, "UTF-8");


			byte[] encodeByte = Base64.encodeBase64(bytes);
			String encodepic = new String(encodeByte);


			JSONObject json = new JSONObject();
			json.put("operation", "upload");

			System.out.println(json.toString());

			osw1.write(json.toString());
			osw1.flush();



			//osw1.write(bytes,0,bytes.length);
			//osw1.flush();
System.out.println(bytes.length);

			BufferedReader responseReader= new BufferedReader(new InputStreamReader(client.getInputStream()));
			while(true){
				String response=responseReader.readLine();
				//if (response==null) System.exit(-1);
				if(response != null) {
					System.out.println(response);
					System.out.flush();
					break;
				}
			}
			//start writing data:
			osw1.write(encodepic);
			osw1.flush();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}
