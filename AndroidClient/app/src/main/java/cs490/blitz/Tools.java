package cs490.blitz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public abstract class Tools {
    public volatile static boolean exit = false;


    public synchronized static void postNotification(String postID, String userName, String msg) {
        HashMap<String, Object> notification = new HashMap<>();
        notification.put("operation", "PostNotifications");
        notification.put("postID", postID);
        notification.put("username", userName);
        notification.put("msg", msg);

        new AsyncTask<HashMap<String, Object>, Integer, JSONObject>() {
            protected final JSONObject doInBackground(HashMap<String, Object>... params) {

                String ret = Tools.query(JSON.toJSONString(params[0]), 9072);
                return JSON.parseObject(ret);
            }

            protected void onPostExecute(JSONObject jsonObject) {
            }
        }.execute(notification);
    }

    public synchronized static String query(String queryRequest, int port) {
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

    public synchronized static void showToast(Context con, String msg) {
        Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
    }

    public synchronized static String timeProcess(String input) {
        int year = Integer.parseInt(input.substring(0, 4));
        int month = Integer.parseInt(input.substring(5, 7));
        int date = Integer.parseInt(input.substring(8, 10));
        int hour = Integer.parseInt(input.substring(11, 13));
        int minute = Integer.parseInt(input.substring(14, 16));
        int second = Integer.parseInt(input.substring(17, 19));
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        cal.set(year, month, date, hour, minute, second);
        SimpleDateFormat format = new SimpleDateFormat("kk:mma MMM dd, yyyy");
        return format.format(cal.getTime());
    }

    public synchronized static void getPic(String picid, ImageView i) {
        try {
            String imageUrl = "http://blitzproject.cs.purdue.edu:9073";
            imageUrl = imageUrl+"/"+picid;
            System.out.println(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            i.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized static String uploadPic(String base64pic) {
        String picid;
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, 9071);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());

            HashMap<String, Object> queryRequest = new HashMap<>();
            queryRequest.put("operation", "upload");
            osw.write(JSON.toJSONString(queryRequest));
            osw.flush();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String response = responseReader.readLine();
                if (response != null) {
                    JSONObject json = JSONObject.parseObject(response);
                    if (json.getBoolean("success")) {
                        //start sending image data
                        picid = json.get("id").toString();
                        break;
                    } else {
                        return null;
                    }
                }
            }
            osw.write(base64pic);
            osw.flush();
            return picid;
        } catch (Exception e) {
            Log.e("Error", "In query", e);
            return null;
        }
    }

    public static String safeToString(Object obj){
        if (obj!=null) return obj.toString();
        return "N/A";
    }
}
