package cs490.blitz;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;

public abstract class Tools {
    public volatile static boolean exit = false;
    static Socket clientPicture = null;
    static OutputStreamWriter oswPicture = null;
    static InputStreamReader isrPicture = null;

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
        //Log.e("Query on " + port, queryRequest);
        final String host = "blitzproject.cs.purdue.edu";
        try {
            Socket client = new Socket(host, port);
            OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
            osw.write(queryRequest);
            osw.flush();
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            int ret = isr.read();
            StringBuffer response = new StringBuffer();
            while (ret != -1) {
                response.append((char) ret);
                ret = isr.read();
            }
            //Log.e("Result", response.toString());
            return response.toString();
        } catch (Exception e) {
            Log.e("Error", "In query", e);
            return null;
        }
    }

    public synchronized static String queryPicture(String queryRequest) {
        final String host = "blitzproject.cs.purdue.edu";
        try {
            if (clientPicture == null || clientPicture.isConnected() == false) {
                clientPicture = new Socket(host, 9071);
                oswPicture = new OutputStreamWriter(clientPicture.getOutputStream());
                isrPicture = new InputStreamReader(clientPicture.getInputStream());
            }

            oswPicture.write(queryRequest);
            oswPicture.flush();

            int ret = isrPicture.read();
            StringBuffer response = new StringBuffer();
            while (ret != -1 && ret != '}') {
                response.append((char) ret);
                ret = isrPicture.read();
            }
            response.append((char) ret);
            return response.toString();
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

    public synchronized static String getPicture(JSONArray picids){
        StringBuffer ret = new StringBuffer();
        try{
            //System.out.println(picids);
            //System.out.println("picture number: " + picids.size());
            for(int i = 0; i<picids.size();i++){
                JSONArray json = picids;
                String picid = json.getString(i);
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "getpic");
                queryRequest.put("id", picid);
                JSONObject returnobject = JSONObject.parseObject(Tools.queryPicture(JSON.toJSONString(queryRequest)));
                //System.out.println(returnobject);
                if (returnobject.getBoolean("success")) {
                    //start sending image data
                    ret.append(returnobject.getString("data"));
                }
                else {
                    System.out.println("picture return json failed");
                    return null;
                }


                
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return ret.toString();
    }

    private static String compressImage(Bitmap bitmapImage) {
        double factor = (double) 256 / (double) bitmapImage.getWidth();
        System.out.println("factor: "+factor+" width: "+bitmapImage.getWidth()+" height: "+bitmapImage.getHeight());
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, (int) (bitmapImage.getWidth() * factor), (int) (bitmapImage.getHeight() * factor), true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.out.println("after compress: width:"+scaled.getWidth()+" height: "+scaled.getHeight());
        scaled.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }

    public synchronized static String[] uploadPic(Bitmap bitmap) {
        String base64pic;
        base64pic = compressImage(bitmap);
        List<String> subStrings = stringSplit(base64pic, 1000);
        List<String> picIdList = new ArrayList<>();
        for (String s : subStrings) {
            try {
                final String host = "blitzproject.cs.purdue.edu";
                Socket client = new Socket(host, 9071);
                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());

                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "upload");
                System.out.println("base64length: " + s.length());
                queryRequest.put("data", s);
                osw.write(JSON.toJSONString(queryRequest));
                osw.flush();
                InputStreamReader isr = new InputStreamReader(client.getInputStream());
                int ret = isr.read();
                StringBuffer response = new StringBuffer();

                while (ret != -1) {
                    response.append((char) ret);
                    ret = isr.read();
                }
                JSONObject json = JSONObject.parseObject(response.toString());

                if (json.getBoolean("success")) {
                    picIdList.add(json.get("id").toString());
                }
            } catch (Exception e) {
                Log.e("Error", "In query", e);
            }

        }
        return picIdList.toArray(new String[picIdList.size()]);
    }


    private static List<String> stringSplit(String str, int sizeOfEachSubString) {
        List<String> strings = new ArrayList<>();
        int index = 0;
        while (index < str.length()) {
            strings.add(str.substring(index, Math.min(index + sizeOfEachSubString, str.length())));
            index += sizeOfEachSubString;
        }
        return strings;
    }

    public static String safeToString(Object obj){
        if (obj!=null) return obj.toString();
        return "N/A";
    }
}
