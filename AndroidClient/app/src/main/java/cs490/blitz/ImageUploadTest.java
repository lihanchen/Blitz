package cs490.blitz;

/**
 * Created by Dingzhe on 12/8/2015.
 */
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;


public class ImageUploadTest extends Activity {
    Button imgsel,upload;
    ImageView img;
    String path;
    Bitmap testBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageupload);
        img = (ImageView)findViewById(R.id.img);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        imgsel = (Button)findViewById(R.id.selimg);
        upload =(Button)findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String,String,String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String ret = Tools.uploadPic(testBit);
                        return ret;
                    }

                    protected void onPostExecute(String ret) {
                        System.out.println("upload return: "+ret);
                    }
                }.execute("");

                /*
                File f = new File(path);

                Future uploading = Ion.with(ImageUploadTest.this)
                        .load("http://blitzproject.cs.purdue.edu:9074")
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {
                                    JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });
                */
            }

        });

        imgsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/jpeg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    path = getPathFromURI(data.getData());
                    System.out.println(path);
                    Bitmap bitmap;
                    Bitmap scaled = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        testBit = bitmap;
                        scaled = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                        String pictureString = compressImage(scaled);
                        System.out.println("Size After Compress: "+pictureString.length());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //img.setImageURI(data.getData());
                    img.setImageBitmap(scaled);
                    upload.setVisibility(View.VISIBLE);

                }
        }
    }
    private String getPathFromURI(Uri contentUri) {
        System.out.println(contentUri.toString());
        /*String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);*/
        return contentUri.getPath();
    }


    private String compressImage(Bitmap bitmapImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }

    public void searchpictest(View v){
        new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String... params) {
                String ret = Tools.getPic(params[0]);
                return ret;
            }

            protected void onPostExecute(String ret) {
                Tools.showPic(ret, img);
            }
        }.execute("5668ebbe1fe810bc39cab4af");
    }
}