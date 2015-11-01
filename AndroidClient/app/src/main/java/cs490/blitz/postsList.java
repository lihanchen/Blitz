package cs490.blitz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class postsList extends AppCompatActivity {
    static int mode = 0; //0=request 1=offer
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) findViewById(R.id.drawerFilter)).openDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.textRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 0;
                findViewById(R.id.textRequest).setBackgroundColor(0xff0003a3);
                findViewById(R.id.textOffer).setBackgroundColor(0x00000000);
                loadData();
            }
        });

        findViewById(R.id.textOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 1;
                findViewById(R.id.textRequest).setBackgroundColor(0x00000000);
                findViewById(R.id.textOffer).setBackgroundColor(0xff0003a3);
                loadData();
            }
        });

        loadData();
    }

    public void loadData() {
        new AsyncTask<Integer, Integer, JSONArray>() {
            protected JSONArray doInBackground(Integer... params) {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "Query");
                queryRequest.put("isRequest", params[0] == 0);
                queryRequest.put("TranscationCompleted", false);
                String ret = Tools.query(JSON.toJSONString(queryRequest), 9067);
                return JSON.parseArray(ret);
            }

            protected void onPostExecute(JSONArray jsonArray) {
                if (jsonArray == null) {
                    Log.e("Err", "Failed login");
                    return;
                }
                super.onPostExecute(jsonArray);

                ArrayList<HashMap<String, Object>> data = new ArrayList<>(jsonArray.size());
                for (Object obj : jsonArray) {
                    HashMap<String, Object> map = new HashMap<>(3);
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject.get("category").equals("FoodDiscover"))
                        map.put("img", R.drawable.fooddiscover);
                    else
                        map.put("img", null);
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
        }.execute(mode);
    }
}
