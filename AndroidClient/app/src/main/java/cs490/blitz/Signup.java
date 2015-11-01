package cs490.blitz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class Signup extends AppCompatActivity implements View.OnClickListener{
    Button bSignUp, bCancel;
    EditText etUserName, etPassword, etPassword2, etEMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        bSignUp = (Button)findViewById(R.id.buttonSignup);
        bCancel = (Button) findViewById(R.id.buttonCancel);

        bSignUp.setOnClickListener(this);
        bCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonSignup:
                signUp();
                break;
            case R.id.buttonCancel:
                cancel();
                break;

        }
    }


    private void signUp() {
        String username = ((EditText) findViewById(R.id.editUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.editPassword2)).getText().toString();
        String email = ((EditText) findViewById(R.id.editEmail)).getText().toString();
        if (!password.equals(password2)) {
            Tools.showToast(getApplicationContext(), "Password doesn't match");
        } else {
            new AsyncTask<String, Integer, JSONObject>() {
                protected JSONObject doInBackground(String... params) {
                    HashMap<String, String> loginCredential = new HashMap<>();
                    loginCredential.put("operation", "Signup");
                    loginCredential.put("username", params[0]);
                    loginCredential.put("password", params[1]);
                    loginCredential.put("email", params[2]);
                    String ret = Tools.query(JSON.toJSONString(loginCredential), 9066);
                    return JSON.parseObject(ret);
                }

                protected void onPostExecute(JSONObject jsonObject) {
                    if (jsonObject == null)
                        Log.e("Err", "Failed login");
                    else if (jsonObject.get("success").equals(true)) {
                        finish();
                        Tools.showToast(getApplicationContext(), "Successfully create account. Please go to your mailbox to activate your account");
                    } else {
                        Tools.showToast(getApplicationContext(), (String) jsonObject.get("msg"));
                    }
                    super.onPostExecute(jsonObject);
                }
            }.execute(username, password, email);
        }

    }

    private void cancel() {
        finish();
    }
}
