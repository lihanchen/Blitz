package cs490.blitz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;


public class MakeAPost extends Activity implements View.OnClickListener {
    private static final int RESULT_IMAGE = 1;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapter;
    ImageView imageToUpload;
    Button bUploadImage;
    FloatingActionButton makeAPost;
    EditText postTitle, contact, bounty, quantity, postBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_a_post);

        selectedCategory = null;
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
                    }
                }
        );
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

        String photo;
        try {
            photo = encodeImage();
        } catch (NullPointerException e) {
            photo = "";
        }

        SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        String username = sp.getString("username", null);


        final HashMap<String, Object> post = new HashMap<>();
        post.put("operation", "CreatePost");
        post.put("username", username);
        post.put("position", "position");
        post.put("description", body);
        post.put("quantity", intQuantity);
        post.put("title", title);
        post.put("bounty", intBounty);
        post.put("contact", strContact);
        post.put("TransactionCompleted", false);
        post.put("photo", photo);
        post.put("response", new JSONObject[0]);
        post.put("isRequest", postsList.mode == 1);
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

        if (requestCode != RESULT_IMAGE || resultCode != RESULT_OK || data == null) {
            Tools.showToast(getApplicationContext(), "Error loading image");
            return;
        }
        Uri selectedImage = data.getData();
        imageToUpload.setImageURI(selectedImage);

    }

    private String encodeImage() {
        Bitmap bitmapImage = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }
}
