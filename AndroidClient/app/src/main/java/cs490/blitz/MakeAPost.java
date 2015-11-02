package cs490.blitz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;

/**
 * Created by arthurchan35 on 11/1/2015.
 */
public class MakeAPost extends AppCompatActivity implements View.OnClickListener{
    private static final int RESULT_IMAGE = 1;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapter;
    ImageView imageToUpload;
    Button bUploadImage, makeAPost;
    EditText postTitle, contact, bounty, quantity, postBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_a_post);

        selectedCategory = null;
        imageToUpload = (ImageView)findViewById(R.id.ivToImageUpload);
        bUploadImage = (Button)findViewById(R.id.bUploadImage);
        makeAPost = (Button)findViewById(R.id.bMakeAPost);
        postTitle = (EditText)findViewById(R.id.etPostTitle);
        contact = (EditText)findViewById(R.id.etContact);
        bounty = (EditText)findViewById(R.id.etBounty);
        quantity = (EditText)findViewById(R.id.etQuantity);
        postBody = (EditText)findViewById(R.id.etPostBody);
        categorySpinner = (Spinner)findViewById(R.id.spCategory);
        adapter = ArrayAdapter.createFromResource(this, R.array.category_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        bUploadImage.setOnClickListener(this);
        makeAPost.setOnClickListener(this);
        categorySpinner.setOnItemSelectedListner(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategory = (String)parent.getItemAtPosition(position);
                        if (selectedCategory.equals("ChooseACategory")) {
                            selectedCategory = "";
                            Tools.showToast(getApplicationContext(), "Please select a category");
                        }
                        Tools.showToast(getApplicationContext(), parent.getItemAtPosition(position) + " selected");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bUploadImage:
                loadImage();
                break;
            case R.id.bMakeAPost:
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
        if (title == null || title.equals("") || body == null || body.equals("")) {
            Tools.showToast(getApplicationContext(), "Please provide complete title and body");
            return;
        }
        String strContact = contact.getText().toString();
        if (strContact == null) strContact = "";
        String strBounty = bounty.getText().toString();
        if (strBounty == null) strBounty = "";
        String strQuantity = quantity.getText().toString();
        if (strQuantity == null) strQuantity = "";
        String photo = encodeImage();
        if (photo == null) photo = "";


        new AsyncTask<String, Integer, JSONObject>() {
            protected JSONObject doInBackground(String... params) {
                HashMap<String, String> post = new HashMap<>();
                post.put("operation", "CreatePost");
                post.put("username", params[0]);
                post.put("position", params[1]);
                post.put("description", params[2]);
                post.put("quantity", params[3]);
                post.put("title", params[4]);
                post.put("bounty", params[5]);
                post.put("contact", params[6]);
                post.put("TransactionCompleted", params[7]);
                post.put("photo", params[8]);
                post.put("response", params[9]);
                post.put("isRequest", params[10]);
                post.put("category", params[11]);
                String ret = Tools.query(JSON.toJSONString(post), 9068);
                return JSON.parseObject(ret);
            }

            protected void onPostExecute(JSONObject jsonObject) {

            }
        }.execute("username", "position", body, strQuantity, title, strBounty, strContact, "false", photo , "response", "true", selectedCategory);
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

    private String encodeImage () {
        Bitmap bitmapImage = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String result = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return result;
    }
}
