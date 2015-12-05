package cs490.blitz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

public class Profile extends Activity {

    String username;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        username = getIntent().getStringExtra("username");
        ((TextView) findViewById(R.id.textUsername)).setText(username);

        String item[] = new String[5];
        int i = 0;
        item[i++] = "Notifications";
        item[i++] = "My Posts";
        item[i++] = "My Responses";
        item[i++] = "Change Password";
        item[i++] = "Log out";
        ((ListView) findViewById(R.id.listView)).setAdapter(new ArrayAdapter<>(this, R.layout.profile_item, R.id.textItem, item));
        ((ListView) findViewById(R.id.listView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent listIntent;
                switch (position) {
                    case 0:
                        listIntent = new Intent(Profile.this, NotificationList.class);
                        listIntent.putExtra("username", username);
                        startActivity(listIntent);
                        break;
                    case 1:
                        listIntent = new Intent(Profile.this, CustomizeList.class);
                        listIntent.putExtra("source", "Posts");
                        listIntent.putExtra("username", username);
                        startActivity(listIntent);
                        break;
                    case 2:
                        listIntent = new Intent(Profile.this, CustomizeList.class);
                        listIntent.putExtra("source", "Responses");
                        listIntent.putExtra("username", username);
                        startActivity(listIntent);
                        break;
                    case 3:
                        Intent loginIntent = new Intent(Profile.this, ChangePassword.class);
                        startActivity(loginIntent);
                        break;
                    case 4:
                        sp.edit().putString("username", null).apply();
                        finish();
                        break;
                }
            }
        });

        new AsyncTask<String, Integer, Float>() {
            protected Float doInBackground(String[] params) {
                HashMap<String, Object> getProfileRequest = new HashMap<>();
                getProfileRequest.put("operation", "GetProfile");
                getProfileRequest.put("username", params[0]);
                String ret = Tools.query(JSON.toJSONString(getProfileRequest), 9066);
                return JSON.parseObject(ret).getFloat("rating");
            }

            protected void onPostExecute(Float o) {
                ((RatingBar) findViewById(R.id.ratingBar)).setRating(o);
            }
        }.execute(username);

    }

}
