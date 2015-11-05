package cs490.blitz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;

import java.util.HashMap;

public class ChangePassword extends Activity implements View.OnClickListener {
    Button bSignUp, bCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        bSignUp = (Button) findViewById(R.id.buttonSubmit);
        bCancel = (Button) findViewById(R.id.buttonCancel);

        bSignUp.setOnClickListener(this);
        bCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                changePassword();
                break;
            case R.id.buttonCancel:
                finish();
                break;

        }
    }


    private void changePassword() {
        String current = ((EditText) findViewById(R.id.editCurrent)).getText().toString();
        String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.editPassword2)).getText().toString();
        if (!password.equals(password2)) {
            Tools.showToast(getApplicationContext(), "Password doesn't match");
        } else {
            SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
            final String username = sp.getString("username", null);
            new AsyncTask<String, Integer, JSONObject>() {
                protected JSONObject doInBackground(String... params) {
                    HashMap<String, String> checkPassword = new HashMap<>();
                    checkPassword.put("operation", "Login");
                    checkPassword.put("username", username);
                    checkPassword.put("password", params[0]);
                    String ret = Tools.query(JSON.toJSONString(checkPassword), 9066);
                    JSONObject ret1 = JSON.parseObject(ret);
                    if (!ret1.getBoolean("success")) {
                        return null;
                    }
                    HashMap<String, Object> changePassword = new HashMap<>();
                    changePassword.put("operation", "ModifyProfile");
                    changePassword.put("username", username);
                    HashMap<String, Object> changePassword2 = new HashMap<>();
                    changePassword2.put("password", params[1]);
                    JSONObject[] change = new JSONObject[1];
                    change[0] = new JSONObject(changePassword2);
                    changePassword.put("changes", change);
                    Log.e("out", JSON.toJSONString(changePassword));
                    String ret2 = Tools.query(JSON.toJSONString(changePassword), 9066);
                    return JSON.parseObject(ret2);
                }

                protected void onPostExecute(JSONObject jsonObject) {
                    if (jsonObject == null)
                        Tools.showToast(getApplicationContext(), "Incorrect Current Password");
                    else if (jsonObject.get("success").equals(true)) {
                        Tools.showToast(getApplicationContext(), "Successfully Changing Password");
                        finish();
                    } else {
                        Tools.showToast(getApplicationContext(), (String) jsonObject.get("msg"));
                    }
                    super.onPostExecute(jsonObject);
                }
            }.execute(current, password);
        }

    }
}
