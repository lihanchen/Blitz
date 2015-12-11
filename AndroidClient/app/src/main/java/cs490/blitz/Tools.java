package cs490.blitz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;

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
        Log.e("Query on " + port, queryRequest);
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, port);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
            osw.write(queryRequest);
            osw.flush();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String ret = responseReader.readLine();
            Log.e("Result", ret);
            return ret;
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

    public synchronized static String getPic(String picid) {
        final String host = "blitzproject.cs.purdue.edu";
        String ret="";
        try {
            Socket client = new Socket(host, 9071);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());

            HashMap<String, Object> queryRequest = new HashMap<>();
            queryRequest.put("operation", "getpic");
            queryRequest.put("id", picid);
            osw.write(JSON.toJSONString(queryRequest));
            osw.flush();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String response = responseReader.readLine();
                if (response != null) {
                    System.out.println(response);
                    JSONObject json = JSONObject.parseObject(response);
                    if (json.getBoolean("success")) {
                        //start sending image data
                        ret = json.get("data").toString();
                        break;
                    } else {
                        return null;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
        /*
        , ImageView i
        try {
            String imageUrl = "http://blitzproject.cs.purdue.edu:9073";
            imageUrl = imageUrl+"/"+picid;
            System.out.println(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            i.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    public synchronized static void showPic(String base64pic,ImageView i){
        byte[] decodedString = Base64.decode(base64pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        i.setImageBitmap(decodedByte);
    }



    private static String compressImage(Bitmap bitmapImage) {
        double factor = (double)100/(double)bitmapImage.getWidth();
        System.out.println("factor: "+factor+" width: "+bitmapImage.getWidth()+" height: "+bitmapImage.getHeight());
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, (int)(bitmapImage.getWidth()*factor), (int)(bitmapImage.getHeight()*factor), true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.out.println("after compress: width:"+scaled.getWidth()+" height: "+scaled.getHeight());
        scaled.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }

    public synchronized static String uploadPic(Bitmap bitmap) {
        String base64pic = "";
        base64pic = compressImage(bitmap);
        String picid;
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, 9071);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());

            HashMap<String, Object> queryRequest = new HashMap<>();
            queryRequest.put("operation", "upload");
            System.out.println("base64length: " + base64pic.length());
            queryRequest.put("data", base64pic);
            osw.write(JSON.toJSONString(queryRequest));
            osw.flush();
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            int ret = isr.read();
            String response = "";

            while (ret != -1) {
                response += (char)ret;
                ret = isr.read();
            }
            JSONObject json = JSONObject.parseObject(response);

            if (json.getBoolean("success")) {
                //start sending image data
                picid = json.get("id").toString();
                return picid;
            }
            else {
                return null;
            }
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
