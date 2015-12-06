package cs490.blitz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;


public class NotificationList extends AppCompatActivity {
    JSONArray data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_list);
        String username = getIntent().getStringExtra("username");
        setTitle("Notifications");
        loadData(username);
    }

    public void loadData(String username) {
        new AsyncTask<String, Integer, JSONArray>() {
            protected JSONArray doInBackground(String... params) {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "GetNotifications");
                queryRequest.put("username", params[0]);
                String ret = Tools.query(JSON.toJSONString(queryRequest), 9072);
                return JSON.parseArray(ret);
            }

            protected void onPostExecute(JSONArray jsonArray) {
                super.onPostExecute(jsonArray);
                if (jsonArray == null) {
                    Log.e("Err", "Failed login");
                    return;
                }
                data = jsonArray;
                LinkedList<HashMap<String, Object>> data = new LinkedList<>();
                for (Object obj : jsonArray) {
                    HashMap<String, Object> map = new HashMap<>(3);
                    JSONObject jsonObject = (JSONObject) obj;
                    Boolean unread = Math.random() > 0.5;
                    map.put("msg", jsonObject.get("msg"));
                    if (unread) {
                        map.put("img", R.drawable.unread);
                        data.addFirst(map);
                    } else {
                        map.put("img", R.drawable.read);
                        data.addLast(map);
                    }
                }

                ListView lv = (ListView) findViewById(R.id.listPostList);
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.notification_item, new String[]{"img", "msg"},
                        new int[]{R.id.imageView, R.id.textMsg});
                lv.setAdapter(adapter);
            }
        }.execute(username);
    }

}
