package cs490.blitz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MakeAPost extends FragmentActivity implements View.OnClickListener {
    private static final int RESULT_IMAGE = 1;
    private static final int RESULT_MATCHING = 2;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapter;
    Button bUploadImage;
    FloatingActionButton makeAPost;
    EditText postTitle, contact, bounty, quantity, postBody;
    EditText fromMap, toMap;
    Button searchbutton1,searchbutton2;
    volatile int uploadingFiles = 0;
    ArrayList<String[]> pictureIDs = new ArrayList<String[]>();
    private GoogleMap mMap;
    private GoogleMap mMap2;
    private ScrollView sv;
    LatLng lat1,lat2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_a_post);

        selectedCategory = null;
        fromMap = (EditText) findViewById(R.id.fromMAP);
        toMap = (EditText) findViewById(R.id.toMAP);
        searchbutton1 = (Button)findViewById(R.id.search1MAP);
        searchbutton2 = (Button)findViewById(R.id.search2MAP);
        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        makeAPost = (FloatingActionButton) findViewById(R.id.fab);
        postTitle = (EditText) findViewById(R.id.etPostTitle);
        contact = (EditText) findViewById(R.id.etContact);
        bounty = (EditText) findViewById(R.id.etBounty);
        quantity = (EditText) findViewById(R.id.etQuantity);
        postBody = (EditText) findViewById(R.id.etPostBody);
        categorySpinner = (Spinner) findViewById(R.id.spCategory);
        adapter = ArrayAdapter.createFromResource(this, R.array.category_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        bUploadImage.setOnClickListener(this);
        makeAPost.setOnClickListener(this);
        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                selectedCategory = "";
                                break;
                            case 1:
                                selectedCategory = "FoodDiscover";
                                toMap.setVisibility(View.GONE);
                                searchbutton2.setVisibility(View.GONE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2MAP)).getView().setVisibility(View.GONE);
                                fromMap.setHint("Location: ");
                                break;
                            case 2:
                                selectedCategory = "Carpool";
                                fromMap.setHint("From: ");
                                toMap.setVisibility(View.VISIBLE);
                                searchbutton2.setVisibility(View.VISIBLE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2MAP)).getView().setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                selectedCategory = "House Rental";
                                fromMap.setHint("Location: ");
                                toMap.setVisibility(View.GONE);
                                searchbutton2.setVisibility(View.GONE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2MAP)).getView().setVisibility(View.GONE);
                                break;
                            case 4:
                                selectedCategory = "Other";
                                fromMap.setHint("Location: ");
                                toMap.setVisibility(View.GONE);
                                searchbutton2.setVisibility(View.GONE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2MAP)).getView().setVisibility(View.GONE);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        setUpMap();
        searchbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(1);
            }
        });
        searchbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(2);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bUploadImage:
                loadImage();
                break;
            case R.id.fab:
                publishPost();
                break;

        }
    }

    private void loadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_IMAGE);
    }

    private void publishPost() {
        if (selectedCategory == null || selectedCategory.equals("")) {
            Tools.showToast(getApplicationContext(), "Please select a category");
            return;
        }

        String title = postTitle.getText().toString();
        String body = postBody.getText().toString();
        if (title.equals("") || body.equals("")) {
            Tools.showToast(getApplicationContext(), "Please provide complete title and body");
            return;
        }

        if ((selectedCategory.equals("Carpool") && lat1 == null) || (selectedCategory.equals("Carpool") && lat2 == null)){
            Tools.showToast(getApplicationContext(),"Please provide correct location");
            return;
        }
        if(!selectedCategory.equals("Carpool") && lat1 == null){
            Tools.showToast(getApplicationContext(),"Please provide correct location");
            return;
        }

        Intent matchingIntent = new Intent(MakeAPost.this, MatchingList.class);
        matchingIntent.putExtra("title", title);
        matchingIntent.putExtra("category", selectedCategory);
        startActivityForResult(matchingIntent, RESULT_MATCHING);
    }

    void create() {
        String title = postTitle.getText().toString();
        String body = postBody.getText().toString();
        String strContact = contact.getText().toString();
        int intBounty, intQuantity;
        try {
            intBounty = Integer.parseInt(bounty.getText().toString());
        } catch (Exception e) {
            intBounty = 0;
        }
        try {
            intQuantity = Integer.parseInt(quantity.getText().toString());
        } catch (Exception e) {
            intQuantity = 0;
        }
        ;
        final HashMap<String, Object> post = new HashMap<>();

        SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        String username = sp.getString("username", null);

        post.put("photo", pictureIDs.toArray());
        post.put("operation", "CreatePost");
        post.put("username", username);
        post.put("position", "position");
        post.put("description", body);
        post.put("quantity", intQuantity);
        post.put("title", title);
        post.put("bounty", intBounty);
        post.put("contact", strContact);
        post.put("TransactionCompleted", false);
        post.put("response", new JSONObject[0]);
        post.put("isRequest", PostsList.mode == 1);
        post.put("category", selectedCategory);
        //map related:
        if(selectedCategory.equals("Carpool")) {
            HashMap<String, Object> position = new HashMap<>();
            position.put("address",fromMap.getText());
            position.put("latitude",lat1.latitude);
            position.put("longitude",lat1.longitude);
            HashMap<String, Object> position2 = new HashMap<>();
            position2.put("address",toMap.getText());
            position2.put("latitude",lat2.latitude);
            position2.put("longitude",lat2.longitude);
            post.put("from", position);
            post.put("to", position2);
        }
        HashMap<String, Object> position = new HashMap<>();
        position.put("address",fromMap.getText());
        position.put("latitude",lat1.latitude);
        position.put("longitude",lat1.longitude);
        post.put("position",position);

        final AsyncTask<HashMap<String, Object>, Integer, JSONObject> success = new AsyncTask<HashMap<String, Object>, Integer, JSONObject>() {
            @SafeVarargs
            protected final JSONObject doInBackground(HashMap<String, Object>... params) {

                String ret = Tools.query(JSON.toJSONString(post), 9068);
                return JSON.parseObject(ret);
            }

            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject.getBoolean("success")) {
                    Tools.showToast(getApplicationContext(), "Successfully create a post");
                    finish();
                }
            }
        }.execute(post);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_IMAGE:
                if (resultCode != RESULT_OK || data == null) {
                    Tools.showToast(getApplicationContext(), "Error loading image 0");
                    return;
                }
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (Exception e) {
                    Tools.showToast(getApplicationContext(), "Error loading image 1");
                    return;
                }

                new AsyncTask<Bitmap, String, String[]>() {
                    @SafeVarargs
                    protected final String[] doInBackground(Bitmap... params) {
                        uploadingFiles++;
                        String ret[] = Tools.uploadPic(params[0]);
                        uploadingFiles--;
                        return ret;
                    }

                    protected void onPostExecute(String ret[]) {
                        if (ret == null) {
                            Tools.showToast(getApplicationContext(), "Error loading image 2");
                            return;
                        }
                        pictureIDs.add(ret);
                        ((TextView) findViewById(R.id.textPictureCount)).setText("" + pictureIDs.size() + "\nImages\nSelected");
                    }
                }.execute(bitmap);

                break;
            case RESULT_MATCHING:
                if (resultCode == RESULT_OK) {
                    finish();
                } else
                    create();
                break;
        }
    }

    private void setUpMap(){
        if(mMap == null){
            mMap = ((WorkaroundMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.mapMAP)).getMap();
        }

        if(mMap!= null){
            mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
            mMap.setMyLocationEnabled(true);

            sv = (ScrollView) findViewById(R.id.ScrollViewMAP);
            //disable sv when touching map
            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMAP)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
            });

        }

        if(mMap2 == null){
            mMap2 = ((WorkaroundMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map2MAP)).getMap();
        }

        if(mMap2!= null){
            mMap2.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
            mMap2.setMyLocationEnabled(true);

            sv = (ScrollView) findViewById(R.id.ScrollViewMAP);
            //disable sv when touching map
            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2MAP)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
            });

        }
    }

    public void onSearch(int i){
        EditText location;
        if(i==1){
            location = fromMap;
        }
        else{
            location = toMap;
        }
        String locaStr = location.getText().toString();
        List<Address> addressesList = null;

        if(locaStr != null && !locaStr.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressesList = geocoder.getFromLocationName(locaStr, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressesList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            if(i==1) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                lat1 = latLng;
            } else {
                mMap2.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                lat2 = latLng;
            }

            System.out.println(latLng.latitude);
            System.out.println(latLng.longitude);

        }
    }
}
