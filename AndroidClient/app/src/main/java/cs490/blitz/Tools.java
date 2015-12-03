package cs490.blitz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public abstract class Tools {
    public volatile static boolean exit = false;


    public synchronized static void postNotification(String postID, String userName, String msg) {
        final HashMap<String, Object> notification = new HashMap<>();
        notification.put("operation", "PostNotifications");
        notification.put("postID", postID);
        notification.put("username", userName);
        notification.put("msg", msg);

        final AsyncTask<HashMap<String, Object>, Integer, JSONObject> success = new AsyncTask<HashMap<String, Object>, Integer, JSONObject>() {
            @SafeVarargs
            protected final JSONObject doInBackground(HashMap<String, Object>... params) {

                String ret = Tools.query(JSON.toJSONString(notification), 9068);
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

    public synchronized static String getPic(String picid) {
        Log.d("enter getpic", "");
        String picdata;
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, 9071);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());

            HashMap<String, Object> queryRequest = new HashMap<>();
            queryRequest.put("operation", "getpic");
            queryRequest.put("id", picid);

            osw.write(JSON.toJSONString(queryRequest));
            osw.flush();
            //BufferedReader responseReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String ret = "";


/*
            BufferedInputStream inBuf = new BufferedInputStream(client.getInputStream());
            byte buf[] = new byte[1024 * 2];
            int len;
            int total = 0;
            while ((len = inBuf.read(buf)) > 0) {
                System.out.println(len);
                total += len;
            }
            System.out.println(total);
*/
            /*
            while(true){
                String v = responseReader.readLine();

                if (v == null){
                    //Log.d("read pic", "finished");
                    break;
                }
                System.out.println(v);
                System.out.println(v.length());
                ret += v;
            }*/
            /*
            InputStream is = client.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, 8190);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while(bis.read() != -1){
                baf.append((byte)current);
            }
            byte[] imagedata = baf.toByteArray();
            System.out.println(imagedata.length);
            ret = new String(imagedata, "UTF-8");
            */
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            String g = (String) ois.readObject();
            System.out.println(g.length());

            JSONObject json = JSON.parseObject(ret);
            System.out.print(ret);
            ret = json.getString("data");
            Log.d("picdata", ret);
            Log.d("picdata.length", "" + ret.length());
            System.out.print(ret);
            return ret;
        } catch (Exception e) {
            Log.e("Error", "In query", e);
            return null;
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

}
