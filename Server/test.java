import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.OutputStreamWriter;

public class test {

	public static void main(String args[]) throws Exception {
		int port = Integer.parseInt(args[0]);
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
