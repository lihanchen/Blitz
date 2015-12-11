package cs490.blitz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;


public class MakeAPost extends FragmentActivity implements View.OnClickListener {
    private static final int RESULT_IMAGE = 1;
    private static final int RESULT_MATCHING = 2;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapter;
    ImageView imageToUpload;
    Button bUploadImage;
    FloatingActionButton makeAPost;
    EditText postTitle, contact, bounty, quantity, postBody;
    EditText fromMap, toMap;
    Button searchbutton1,searchbutton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_a_post);

        selectedCategory = null;
        fromMap = (EditText) findViewById(R.id.fromMAP);
        toMap = (EditText) findViewById(R.id.toMAP);
        searchbutton1 = (Button)findViewById(R.id.search1MAP);
        searchbutton2 = (Button)findViewById(R.id.search2MAP);
        imageToUpload = (ImageView) findViewById(R.id.ivToImageUpload);
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
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMAP)).getView().setVisibility(View.GONE);
                                fromMap.setHint("Location: ");
                                break;
                            case 2:
                                selectedCategory = "Carpool";
                                fromMap.setHint("From: ");
                                toMap.setVisibility(View.VISIBLE);
                                searchbutton2.setVisibility(View.VISIBLE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMAP)).getView().setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                selectedCategory = "House Rental";
                                fromMap.setHint("Location: ");
                                toMap.setVisibility(View.GONE);
                                searchbutton2.setVisibility(View.GONE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMAP)).getView().setVisibility(View.GONE);
                                break;
                            case 4:
                                selectedCategory = "Other";
                                fromMap.setHint("Location: ");
                                toMap.setVisibility(View.GONE);
                                searchbutton2.setVisibility(View.GONE);
                                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMAP)).getView().setVisibility(View.GONE);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        setUpMap();
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
            case R.id.search1MAP:
                onSearch(1);
                break;
            case R.id.search2MAP:
                onSearch(2);
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
        final Bitmap bitmapImage = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        try {

            final AsyncTask<String, String, String> successUploadImage = new AsyncTask<String, String, String>() {
                @SafeVarargs
                protected final String doInBackground(String ... params) {
                    String ret = Tools.uploadPic(bitmapImage);
                    return ret;
                }

                protected void onPostExecute(String ret) {
                    post.put("photo", ret);
                }
            }.execute("");

            while (successUploadImage.getStatus() != AsyncTask.Status.FINISHED) {
                
            }
            //photo = /*encodeImage()*/Tools.uploadPic(((BitmapDrawable) imageToUpload.getDrawable()).getBitmap());

        } catch (NullPointerException e) {
        }
        //Log.e("photo is: ", photo);
        SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        String username = sp.getString("username", null);

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
                    Tools.showToast(getApplicationContext(), "Error loading image");
                    return;
                }
                Uri selectedImage = data.getData();
                imageToUpload.setImageURI(selectedImage);
                break;
            case RESULT_MATCHING:
                if (resultCode == RESULT_OK) {
                    finish();
                } else
                    create();
                break;
        }
    }

    private String encodeImage() {
        Bitmap bitmapImage = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }

    private GoogleMap mMap;
    private GoogleMap mMap2;
    private ScrollView sv;

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
            } else {
                mMap2.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }

            System.out.println(latLng.latitude);
            System.out.println(latLng.longitude);

        }
    }
}
