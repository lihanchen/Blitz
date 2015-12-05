package cs490.blitz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Dingzhe on 11/3/2015.
 */
public class Postdetail extends AppCompatActivity implements OnMapReadyCallback {
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    ScrollView sv;
    double latitude;
    double longitude;
    String currentusername;
    String postusername;
    ArrayList<HashMap<String, Object>> offerdata;
    String postid;
    private GoogleMap googleMap;

    @Override
    public void onMapReady(GoogleMap map) {


    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postdetail);
        postid = getIntent().getStringExtra("postid");
        currentusername = getIntent().getStringExtra("username");

        //map related
        try {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, mLocationListener);
            Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            System.out.println(latitude + "+" + longitude);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            if (googleMap == null) {
                googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().
                        findFragmentById(R.id.mapPD)).getMap();
            }
            sv = (ScrollView) findViewById(R.id.containerPD);
            //disable sv when touching map
            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPD)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
            });
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(latitude, longitude), 14));
            Marker TP = googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(latitude, longitude)).title("Current Location"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                postusername = postname;
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
                    map.put("comment", jsonObject.get("comment"));
                    data.add(map);
                }
                offerdata = data;

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


                //if this post is created by user then setup accept offer onclicklistener
                //if not, then setup make offer button
                int placeholderId = R.id.listofferPD; // placeholderId==12
                ViewGroup placeholder = (ViewGroup) findViewById(placeholderId);
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.response_item, new String[]{"img", "username", "bounty", "comment"},
                        new int[]{R.id.avatarRI, R.id.usernameRI, R.id.bountyRI, R.id.detailRI});
                LinearLayout layout = (LinearLayout) findViewById(R.id.listofferPD);
                final int adapterCount = adapter.getCount();
                for (int i = 0; i < adapterCount; i++) {
                    View item = adapter.getView(i, null, null);
                    item.setId(i);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int position = v.getId();
                            System.out.println(position);
                            if (postusername.equals(currentusername)) {
                                System.out.println("this is the same user");
                                AlertDialog.Builder builder = new AlertDialog.Builder(Postdetail.this);
                                builder.setMessage("Do you want to accept offer from " + offerdata.get(position).get("username")
                                        + " with bounty " + offerdata.get(position).get("bounty"))
                                        .setTitle("Accept Offer");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                        new AsyncTask<String, Integer, JSONObject>() {
                                            @Override
                                            protected JSONObject doInBackground(String... params) {
                                                Tools.postNotification(postid, (String) offerdata.get(position).get("username"), "Great! Your offer/request has been accepted!");
                                                HashMap<String, Object> queryRequest = new HashMap<>();
                                                queryRequest.put("operation", "AcceptOffer");
                                                queryRequest.put("postID", postid);
                                                queryRequest.put("username", offerdata.get(position).get("username"));
                                                String ret = Tools.query(JSON.toJSONString(queryRequest), 9069);
                                                return JSON.parseObject(ret);
                                            }

                                            @Override
                                            protected void onPostExecute(JSONObject jsonArray) {
                                                if (jsonArray != null)
                                                    System.out.println(jsonArray.toString());
                                            }
                                        }.execute();

                                        System.out.println("Ok is clicked");
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        System.out.println("Ok is clicked");
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                    placeholder.addView(item);
                }

                ImageView plussign = (ImageView) findViewById(R.id.plusPD);
                System.out.println("postuser: " + postusername);
                System.out.println("currentuser: " + currentusername);
                if (!postusername.equals(currentusername)) {
                    plussign.setImageResource(R.drawable.plussign);
                    //setup onclick listener to enable user to make an offer:
                    plussign.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Postdetail.this);
                            // Get the layout inflater
                            LayoutInflater inflater = Postdetail.this.getLayoutInflater();

                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the dialog layout
                            final View dialogView = inflater.inflate(R.layout.offerdialog, null);
                            builder.setView(dialogView)
                                    // Add action buttons
                                    .setPositiveButton("Make Offer!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //ok
                                            EditText comment = (EditText) dialogView.findViewById(R.id.dialogComments);
                                            EditText bounty = (EditText) dialogView.findViewById(R.id.dialogBounty);
                                            final String commentstr = comment.getText().toString();
                                            final double bountynum = Double.parseDouble(bounty.getText().toString());

                                            new AsyncTask<String, Integer, JSONObject>() {
                                                @Override
                                                protected JSONObject doInBackground(String... params) {
                                                    Tools.postNotification(postid, postusername, "Great! Someone responded to your post!");
                                                    HashMap<String, Object> queryRequest = new HashMap<>();
                                                    queryRequest.put("operation", "OfferPrice");
                                                    queryRequest.put("postID", postid);
                                                    queryRequest.put("username", currentusername);
                                                    queryRequest.put("comment", commentstr);
                                                    queryRequest.put("offeredPrice", bountynum);
                                                    System.out.println(JSON.toJSONString(queryRequest));
                                                    String ret = Tools.query(JSON.toJSONString(queryRequest), 9069);
                                                    return JSON.parseObject(ret);
                                                }

                                                @Override
                                                protected void onPostExecute(JSONObject jsonArray) {
                                                    if (jsonArray != null)
                                                        System.out.println(jsonArray.toString());
                                                }
                                            }.execute();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //cancel
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                }

                //set up delete post button:
                if(postusername.equals(currentusername)){
                    Button closebutton = (Button)findViewById(R.id.deletePD);
                    closebutton.setVisibility(View.VISIBLE);
                    closebutton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new AsyncTask<String, Integer, JSONObject>() {
                                @Override
                                protected JSONObject doInBackground(String... params) {
                                    HashMap<String, Object> queryRequest = new HashMap<>();
                                    queryRequest.put("operation", "DeletePost");
                                    queryRequest.put("postID", postid);
                                    System.out.println(JSON.toJSONString(queryRequest));
                                    String ret = Tools.query(JSON.toJSONString(queryRequest), 9068);
                                    return JSON.parseObject(ret);
                                }

                                @Override
                                protected void onPostExecute(JSONObject jsonArray) {
                                    if (jsonArray.getBoolean("success") == true){
                                        System.out.println("Delete success");
                                        Tools.showToast(getApplicationContext(),"Delete Successed!");
                                        finish();
                                    }
                                    else{
                                        System.out.println("Delete failed");
                                        Tools.showToast(getApplicationContext(),"Delete Failed!");
                                    }
                                }
                            }.execute();
                        }
                    });
                } else {
                    Button closebutton = (Button)findViewById(R.id.deletePD);
                    closebutton.setVisibility(View.GONE);
                }





            }
        }.execute(postid);

        /*
        new AsyncTask<String, Integer, String>() {
            protected String doInBackground(String... params) {
                String picdata = Tools.getPic(params[0]);
                return picdata;
            }

            protected void onPostExecute(String pic) {
                Log.d("finished getpic",pic);

                byte[] firstpic = Base64.decode(pic, Base64.DEFAULT);
                //String text = new String(data, "UTF-8");
                Bitmap bitmap = BitmapFactory.decodeByteArray(firstpic , 0, firstpic.length);
                ImageView defaultimage = (ImageView)findViewById(R.id.defaultimagePD);
                defaultimage.setImageBitmap(bitmap);
            }
        }.execute("563bd4ad499a593a0ac7b1fb");
           */
    }


}
