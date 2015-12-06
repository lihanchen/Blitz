package cs490.blitz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//TODO Matching
public class MatchingList extends AppCompatActivity {
    JSONArray data;
    int returnIndex = -1;
    String title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_list);
        title = getIntent().getStringExtra("title");
        String category = getIntent().getStringExtra("category");
        setTitle("Potential Existing Post");
        loadData(category);
    }

    public void loadData(String category) {
        new AsyncTask<String, Integer, JSONArray>() {
            protected JSONArray doInBackground(String... params) {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "Query");
                queryRequest.put("category", params[0]);
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
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject.getString("title").toLowerCase().contains(title.toLowerCase())) {
                        HashMap<String, Object> map = new HashMap<>(3);
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
                }

                HashMap<String, Object> map = new HashMap<>(1);
                returnIndex = data.size();
                map.put("title", "I still want to send a new Post");
                data.add(map);

                ListView lv = (ListView) findViewById(R.id.listPostList);
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.list_item, new String[]{"img", "title", "time"},
                        new int[]{R.id.imageView, R.id.textTitle, R.id.textTime});
                lv.setAdapter(adapter);
            }
        }.execute(category);

        ((ListView) findViewById(R.id.listPostList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == returnIndex) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                Intent ProfileIntent = new Intent(MatchingList.this, PostDetail.class);
                ProfileIntent.putExtra("postid", ((JSONObject) data.get(position)).getString("_id"));
                startActivity(ProfileIntent);
            }
        });
    }
}
