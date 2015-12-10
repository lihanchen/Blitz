package cs490.blitz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * Created by arthurchan35 on 12/9/2015.
 */
public class Rating extends FragmentActivity implements View.OnClickListener {
    RatingBar rbRateForUser;
    float ratingNumber;
    Button bSubRating;
    String postUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating);
        postUserName = getIntent().getStringExtra("username");
        rbRateForUser = (RatingBar) findViewById(R.id.rbRateForUser);

        rbRateForUser.setOnRatingBarChangeListener(
            new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingNumber = rating;
                }
            }
        );

        bSubRating = (Button) findViewById(R.id.bSubRating);
        bSubRating.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e("button ", "clicked");
        switch (v.getId()) {
            case R.id.bSubRating:
                sendRatingToSever();
                break;
        }
    }

    private void sendRatingToSever() {
        final HashMap<String, Object> sendRating = new HashMap<>();
        sendRating.put("operation", "Rate");
        sendRating.put("username", /*postUserName*/"lhc2");
        sendRating.put("score", ratingNumber);
        Log.e("User is: ", postUserName);
        Log.e("Score: ", Float.toString(ratingNumber));
        new AsyncTask<HashMap<String, Object>, Integer, JSONObject>() {
            @SafeVarargs
            protected final JSONObject doInBackground(HashMap<String, Object>... params) {

                String ret = Tools.query(JSON.toJSONString(sendRating), 9066);
                return JSON.parseObject(ret);
            }

            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject.getBoolean("success")) {
                    Log.e("Rate: ", "Successfully");
                    Tools.showToast(getApplicationContext(), "Successfully rate");
                    finish();
                }
                else {
                    Log.e("Rate: ", "Unsuccessfully");
                    Tools.showToast(getApplicationContext(), "Error occur");
                    finish();
                }
            }
        }.execute(sendRating);
    }
}
