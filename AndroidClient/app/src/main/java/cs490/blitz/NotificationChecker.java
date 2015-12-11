package cs490.blitz;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NotificationChecker extends Service {
    static int id = 0;
    public Set<String> read;
    String username;

    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        username = sp.getString("username", null);
        if (username == null)
            this.stopSelf();

        read = sp.getStringSet("readNotifications", new HashSet<String>());
        new Thread() {
            @Override
            public void run() {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "GetNotifications");
                queryRequest.put("username", username);
                while (true) {
                    String ret = Tools.query(JSON.toJSONString(queryRequest), 9072);
                    JSONArray notifications = JSON.parseArray(ret);
                    if (notifications != null) {
                        for (Object obj : notifications) {
                            JSONObject jsonObj = (JSONObject) obj;
                            if (!read.contains(jsonObj.getString("_id"))) {
                                read.add(jsonObj.getString("_id"));
                                try {
                                    PostsList.instance.myHandler.obtainMessage(0).sendToTarget();
                                } catch (Exception e) {
                                }
                                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(NotificationChecker.this);
                                nBuilder.setContentTitle("Blitz");
                                nBuilder.setSmallIcon(R.drawable.unread);
                                nBuilder.setVibrate(new long[]{300, 100});
                                nBuilder.setContentText(jsonObj.getString("msg"));
                                nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(jsonObj.getString("msg")));
                                nBuilder.setTicker(jsonObj.getString("msg"));
                                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(id++, nBuilder.build());
                            }
                        }
                    }
                    try {
                        Thread.sleep(2000000);//TODO change time
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
