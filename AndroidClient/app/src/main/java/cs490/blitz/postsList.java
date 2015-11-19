package cs490.blitz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class postsList extends AppCompatActivity {
    static int mode; //0=request 1=offer
    static postsList instance;
    volatile boolean exitOnNextBack = false;
    JSONArray data;
    String username;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            int id = 0;
            switch (msg.what) {
                case 0://New Notification
                    Log.e("Received Notification", "Received Notification");
                    break;
            }
            super.handleMessage(msg);
        }
    };


    Spinner ReqOrOffer;
    String ReqOrOfferStr;
    ArrayAdapter<CharSequence> adapterOfRoO;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapterOfCateg;

    EditText bountyU, bountyL, searchUser, searchTitle;
    int intBountyU, intBountyL;
    String strSearchUser, strSearchTitle;
    Button apply;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        mode = 0;
        Tools.exit = false;
        setContentView(R.layout.posts_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(postsList.this, MakeAPost.class);
                startActivity(loginIntent);
            }
        });

        findViewById(R.id.textFilers).setOnClickListener(new View.OnClickListener() {
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
                loadData(mode, null, -1, -1, null, null);
            }
        });

        findViewById(R.id.textOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 1;
                findViewById(R.id.textRequest).setBackgroundColor(0x00000000);
                findViewById(R.id.textOffer).setBackgroundColor(0xff0003a3);
                loadData(mode, null, -1, -1, null, null);
            }
        });

        findViewById(R.id.imageProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProfileIntent = new Intent(postsList.this, Profile.class);
                ProfileIntent.putExtra("username", username);
                startActivity(ProfileIntent);
            }
        });

        ((ListView) findViewById(R.id.listPostList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ProfileIntent = new Intent(postsList.this, Postdetail.class);
                startActivity(ProfileIntent);
            }
        });

        Intent serviceIntent = new Intent(postsList.this, NotificationChecker.class);
        startService(serviceIntent);




        ReqOrOffer = (Spinner) findViewById(R.id.spReqOrOfferInFilter);
        adapterOfRoO = ArrayAdapter.createFromResource(this, R.array.Request_Offer, android.R.layout.simple_spinner_item);
        ReqOrOffer.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (positon > 1) mode = 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );


        categorySpinner = (Spinner) findViewById(R.id.spCategoryInFilter);
        adapterOfCateg = ArrayAdapter.createFromResource(this, R.array.category_list, android.R.layout.simple_spinner_item);
        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                selectedCategory = null;
                                break;
                            case 1:
                                selectedCategory = "FoodDiscover";
                                break;
                            case 2:
                                selectedCategory = "Carpool";
                                break;
                            case 3:
                                selectedCategory = "House Rental";
                                break;
                            case 4:
                                selectedCategory = "Other";
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCategory = null;
                    }
                }
        );


        bountyU = (EditText) findViewById(R.id.etBountyUpper);
        intBountyU = Integer.MAX_VALUE;
        final HashMap<String, Object> hpBountyU = new HashMap<>();
        bountyL = (EditText) findViewById(R.id.etBountyLower);
        intBountyL = Integer.MIN_VALUE;
        final HashMap<String, Object> hpBountyL = new HashMap<>();
        searchUser = (EditText) findViewById(R.id.etSearchUser);
        strSearchUser  = null;
        searchTitle = (EditText) findViewById(R.id.etSearchTitle);
        strSearchTitle = null;
        apply = (Button) findViewById(R.id.bApplyFilter);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    intBountyU = Integer.parseInt(bountyU.getText().toString());

                } catch (Exception e) {}
                hpBountyU.put("$gt", intBountyU);
                try {
                    intBountyL = Integer.parseInt(bountyL.getText().toString());
                } catch (Exception e) {}
                hpBountyL.put("$lt", intBountyL);
                strSearchUser = searchUser.getText().toString();
                strSearchTitle = searchTitle.getText().toString();

                loadData(mode, selectedCategory, hpBountyL, hpBountyU, strSearchUser, strSearchTitle);
            }
        });
    }

    public void loadData(int ReqorOffer, String category, HashMap<String, Object> hpBountyL, HashMap<String, Object> hpBountyU, String searchUser, String searchTitle ) {

        final HashMap<String, Object> queryRequest = new HashMap<>();
        queryRequest.put("operation", "Query");
        queryRequest.put("isRequest",  ReqorOffer == 0);
        queryRequest.put("TransactionCompleted", false);
        queryRequest.put("bounty", hpBountyL);
        queryRequest.put("bounty", hpBountyU);
        if (category != null || !category.equals(""))
            queryRequest.put("category", category);

        if (searchUser != null || !searchUser.equals(""))
            queryRequest.put("username", searchUser);

        if (searchTitle != null || !searchTitle.equals(""))
            queryRequest.put("title", searchTitle);

        new AsyncTask<HashMap<String, Object>, Integer, JSONArray>() {
            protected JSONArray doInBackground(HashMap<String, Object>... params) {
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
        }.execute(queryRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Tools.exit)
            finish();
        else {
            SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
            username = sp.getString("username", null);
            if (username == null) {
                Intent loginIntent = new Intent(postsList.this, Login.class);
                startActivity(loginIntent);
            } else {
                loadData(mode, null, -1, -1, null, null);
            }
        }
    }

    public void onBackPressed() {
        if (!exitOnNextBack) {
            exitOnNextBack = true;
            Tools.showToast(getApplicationContext(), "Press back again to exit app");
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                        exitOnNextBack = false;
                    } catch (Exception e) {
                        Log.e("Log", "Double back timer", e);
                    }
                }
            }.start();
        } else {
            Tools.exit = true;
            finish();
        }
    }
}
