package cs490.blitz;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class NotificationList extends AppCompatActivity {
    JSONArray data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_list);
        String source=getIntent().getStringExtra("source");
        setTitle(source+" List");
        loadData(source);
    }

    public void loadData(String source) {
        new AsyncTask<String, Integer, JSONArray>() {
            protected JSONArray doInBackground(String... params) {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "Query");
                if (params[0].equals("Posts")) {
                    SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
                    String username = sp.getString("username", null);
                    queryRequest.put("username", username);
                }else if (params[0].equals("Responses")) {
                    SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
                    String username = sp.getString("username", null);
                    queryRequest.put("response.username", username);
                }else {
                    Log.e("Error", "Can't parse list request");
                    finish();
                }
                String ret = Tools.query(JSON.toJSONString(queryRequest), 9067);
                return JSON.parseArray(ret);
            }

            protected void onPostExecute(JSONArray jsonArray) {
                super.onPostExecute(jsonArray);
                if (jsonArray == null) {
                    Log.e("Err", "Failed login");
                    return;
                }
                data = jsonArray;
                ArrayList<HashMap<String, Object>> data = new ArrayList<>(jsonArray.size());
                for (Object obj : jsonArray) {
                    HashMap<String, Object> map = new HashMap<>(3);
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject.get("category").equals("FoodDiscover"))
                        map.put("img", R.drawable.fooddiscover);
                    else if (jsonObject.get("category").equals("Carpool"))
                        map.put("img", R.drawable.carpool);
                    else if (jsonObject.get("category").equals("House Rental"))
                        map.put("img", R.drawable.house);
                    else
                        map.put("img", R.drawable.other);
                    map.put("title", jsonObject.get("title"));
                    map.put("time", Tools.timeProcess(jsonObject.get("postTime").toString()));
                    data.add(map);
                }

                ListView lv = (ListView) findViewById(R.id.listPostList);
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.list_item, new String[]{"img", "title", "time"},
                        new int[]{R.id.imageView, R.id.textTitle, R.id.textTime});
                lv.setAdapter(adapter);
            }
        }.execute(source);
    }

}
