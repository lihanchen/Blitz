package cs490.blitz;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by lihan on 2015/10/24.
 */
public abstract class Tools {
    public static String query(String queryRequest, int port) {
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, port);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
            osw.write(queryRequest);
            osw.flush();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            return responseReader.readLine();
        } catch (Exception e) {
            Log.e("Error", "In query", e);
            return null;
        }
    }
}
