package cs490.blitz;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class ForgetPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);
//        setupUI(findViewById(R.id.forgetPWparent));

        //set onclick listener for reset
        Button buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.resetUsername)).getText().toString();
                new AsyncTask<String, Integer, JSONObject>() {
                    protected JSONObject doInBackground(String... params) {
                        HashMap<String, String> resetPassword = new HashMap<>();
                        resetPassword.put("operation", "ForgetPassword");
                        resetPassword.put("username", params[0]);
                        String ret = Tools.query(JSON.toJSONString(resetPassword), 9066);
                        return JSON.parseObject(ret);
                    }

                    protected void onPostExecute(JSONObject jsonObject) {
                        if (jsonObject == null) {
                            Log.e("Err", "C");
                            Tools.showToast(getApplicationContext(), "Error");
                        } else if (jsonObject.get("success").equals(true)) {
                            Tools.showToast(getApplicationContext(), "Please click on the link in " + jsonObject.getString("email") + " to reset your password.");
                        } else {
                            Log.e("Err", (String) jsonObject.get("msg"));
                            Tools.showToast(getApplicationContext(), (String) jsonObject.get("msg"));
                        }
                        super.onPostExecute(jsonObject);
                    }
                }.execute(username);
            }
        });
    }
}