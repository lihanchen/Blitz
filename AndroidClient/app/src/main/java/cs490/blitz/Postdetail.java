package cs490.blitz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Dingzhe on 11/3/2015.
 */
public class Postdetail extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postdetail);

        new AsyncTask<String, Integer, JSONObject>() {
            protected JSONObject doInBackground(String... params) {
                HashMap<String, Object> queryRequest = new HashMap<>();
                queryRequest.put("operation", "GetPostDetail");
                queryRequest.put("postID", params[0]);
                String ret = Tools.query(JSON.toJSONString(queryRequest), 9069);
                return JSON.parseObject(ret);
            }

            protected void onPostExecute(JSONObject jsonArray) {
                if (jsonArray == null) {
                    Log.e("Err", "Failed to collect the post");
                    return;
                }
                super.onPostExecute(jsonArray);
                Log.e("return json//", jsonArray.toString());
                if (jsonArray.getBoolean("success") != true) return;
                JSONObject json = JSON.parseObject(jsonArray.get("object").toString());

                TextView bounty = (TextView) findViewById(R.id.bountyDP);
                TextView quantity = (TextView) findViewById(R.id.remainseatDP);
                TextView description = (TextView) findViewById(R.id.desDetailPD);
                TextView topic = (TextView) findViewById(R.id.topicPD);
                TextView username = (TextView) findViewById(R.id.usernamePD);
                TextView posttime = (TextView) findViewById(R.id.posttimePD);

                bounty.append(": " + json.get("bounty").toString());
                quantity.append(": " + json.get("quantity").toString());
                description.setText(json.get("description").toString());
                topic.setText(json.get("title").toString());
                String postname = json.get("username").toString();
                username.setText(json.get("username").toString());
                String serverTime = json.get("postTime").toString();
                posttime.setText("");

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                DateFormat currentTZ = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                currentTZ.setTimeZone(TimeZone.getDefault());
                try {
                    Date d = df.parse(serverTime);
                    posttime.setText(currentTZ.format(d));
                } catch (Exception e) {
                    Log.e("Err", e.toString());
                }


                JSONArray response = JSON.parseArray(json.get("response").toString());
                ArrayList<HashMap<String, Object>> data = new ArrayList<>(response.size());
                for (Object obj : response) {
                    HashMap<String, Object> map = new HashMap<>(4);
                    JSONObject jsonObject = (JSONObject) obj;
                    map.put("img", R.drawable.defaultavatar);
                    map.put("username", jsonObject.get("username"));
                    String bounty1 = "Offering: ";
                    bounty1 += jsonObject.get("bounty").toString();
                    map.put("bounty", bounty1);
                    map.put("content", jsonObject.get("content"));
                    data.add(map);
                }

                SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
                String un = sp.getString("username", null);
                if (un == null) {
                    Log.e("username = null", "");
                    Intent loginIntent = new Intent(Postdetail.this, Login.class);
                    startActivity(loginIntent);
                } else {
                    Log.e("Login successful", un);
                    if (un.equals(postname)) {
                        Log.e("same user", "");
                    }
                }

                ListView lv = (ListView) findViewById(R.id.listofferPD);
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.response_item, new String[]{"img", "username", "bounty", "content"},
                        new int[]{R.id.avatarRI, R.id.usernameRI, R.id.bountyRI, R.id.detailRI});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("############", "id " + id + "position" + position);
                    }
                });


                ImageView avatar = (ImageView) findViewById(R.id.avatarPD);


            }
        }.execute("56355c27519882c405964950");

    }

}
